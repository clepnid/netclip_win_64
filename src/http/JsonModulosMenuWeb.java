package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import http.modulosBackend.JsonListaHerramientasBackend;
import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventana.Configuracion;
import ventana.Ventana;
import ventanaGestionarModulo.SistemaModulos;

// se usa para leer el json de menuArchivo y darle funcionabilidad al momento de cargarse.
public class JsonModulosMenuWeb {

	public static ConfiguracionWebJson config;
	private static String rutaJson;

	@SuppressWarnings("unchecked")
	public static void InstanciarWeb(String ruta) {

		rutaJson = ruta.replace("\\", "/");
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(rutaJson)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray webList = (JSONArray) obj;
			System.out.println(webList);

			webList.forEach(web -> parseControlWebObject((JSONObject) web));
			crearMenu();
			crearBuscador();
			SistemaModulos sis = SistemaModulos.getInstance();
			sis.inicializarModulo("downloadVideosUrl");
			sis.inicializarModulo("uploadFiles");
			if (JsonEntradaMenuTiposFicheroModulo.existe_modulo_subir_fichero()) {
				crearRecibirArchivos();
			}
			if (JsonEntradaMenuTiposFicheroModulo.existe_modulo_youtube_downloader()) {
				crearVideoDownloader();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void crearMenu() {
		Spark.get("/menu", (req, res) -> controlarUsuario(req, res, config.getHtml(), "/menu"));

		Spark.after("/menu", (request, response) -> {
			controlarUsuarioAfter(request, response);
		});
	}

	private static String controlarUsuario(Request request, Response responce, String html, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, ruta);
		if (n == 0) {
			return HttpBackend.renderIndex(request, responce, config.getHtml(), ruta);
		} else {
			if (n == 1) {
				responce.raw().sendRedirect("/login");
				return null;
			} else {
				responce.raw().sendRedirect("/login-layout/index.html");
				return null;
			}
		}
	}

	private static void controlarUsuarioAfter(Request request, Response responce)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, "*");
		System.out.println(n);
		if (n == 0) {
			String body = responce.body();
			responce.body(body.replace(config.getHtmlReemplazoBody(),
					"[\r\n" + "  {\r\n" + "    \"search\": \"\",\r\n" + "    \"content\": [\r\n" + "      {\r\n"
							+ "        \"title_content\": \"\",\r\n" + "        \"webs\": ["
							+ JsonEntradaMenuModulo.getArchivosJson() + "," + JsonEntradaMenuModulo.getTextJson() + ","
							+ JsonEntradaMenuModulo.getHtmlJson() + "," + JsonEntradaMenuModulo.getHerramientasJson() + "]}]}]"));
		}
	}

	private static void crearBuscador() {
		Spark.get("/searchFile/:name", (request, response) -> HttpBackend.renderIndex(request, response,
				config.getHtml(), "/searchFile/:name"));
		Spark.redirect.get("/searchFile/", config.getRutaHttp());
		Spark.after("/searchFile/:name", (request, response) -> {
			String body = response.body();
			response.body(body.replace(config.getHtmlReemplazoBody(),
					config.getJsonFiltrado(request, response, request.params(":name"))));
		});
	}

	private static void crearVideoDownloader() {
		SistemaModulos sis = SistemaModulos.getInstance();
		Spark.get("/downloadYoutubemp4", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/downloadYoutubemp4");
			if (n == 0 && sis.isValido("downloadVideosUrl")) {
				VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.YoutubeVideo,
						request.queryParams("url"));
				downloader.setFormat(request.queryParams("format"));
				downloader.setQuality(request.queryParams("quality"));
				downloader.setOutputName(request.queryParams("outputName"));
				downloader.start();
				return "Peticion enviada";
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");

		});

		Spark.get("/downloadYoutubemp3", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/downloadYoutubemp3");
			if (n == 0 && sis.isValido("downloadVideosUrl")) {
				VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.YoutubeAudio,
						request.queryParams("url"));
				downloader.setQuality(request.queryParams("quality"));
				downloader.setOutputName(request.queryParams("outputName"));
				downloader.start();
				return "Peticion enviada";
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");

		});

		Spark.get("/downloadOthermp4", (request, response) -> {

			int n = HttpBackendUsuarios.tienePermiso(request, response, "/downloadOthermp4");
			if (n == 0 && sis.isValido("downloadVideosUrl")) {
				VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.OtherVideo,
						request.queryParams("url"));
				downloader.setOutputName(request.queryParams("outputName"));
				downloader.start();
				return "Peticion enviada";
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");

		});

		Spark.get("/downloadOthermp3", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/downloadOthermp3");
			if (n == 0 && sis.isValido("downloadVideosUrl")) {
				VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.OtherAudio,
						request.queryParams("url"));
				downloader.setOutputName(request.queryParams("outputName"));
				downloader.start();
				return "Peticion enviada";
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");

		});

	}

	private static void crearRecibirArchivos() {
		Spark.post("/uploadFiles/:name", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/uploadFiles");
			SistemaModulos sis = SistemaModulos.getInstance();
			if (n == 0 && sis.isValido("uploadFiles")) {
				String nombreFichero = request.params(":name");
				String rutaFichero = Configuracion.deserializar().carpeta + File.separator + request.params(":name");
				System.out.println(rutaFichero);
				DiskFileItemFactory dff = new DiskFileItemFactory();
				String savePath = Configuracion.deserializar().carpeta;
				dff.setRepository(new File(savePath));
				ServletFileUpload sfu = new ServletFileUpload(dff);
				sfu.setSizeMax(OpcionesModulosHttp.getFileSize());
				sfu.setHeaderEncoding("utf-8");
				FileItemIterator fii = sfu.getItemIterator(request.raw());
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();
					if (!fis.isFormField()) {

						BufferedInputStream in = new BufferedInputStream(fis.openStream());
						FileOutputStream out = new FileOutputStream(new File(rutaFichero));
						BufferedOutputStream output = new BufferedOutputStream(out);
						Streams.copy(in, output, true);
						in.close();
						output.close();
						out.close();
					}
				}

				String extension, nombre;
				boolean yaIntroducido;
				if (Ficheros.tipoFichero(nombreFichero).equals("video")) {
					nombre = Http.encodeURIcomponent(nombreFichero);
					yaIntroducido = Ventana.getInstance().http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					Ventana.getInstance().http.crearUrlVideo(nombre, rutaFichero);
				} else {
					nombre = Http.encodeURIcomponent(nombreFichero);
					yaIntroducido = Ventana.getInstance().http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					Ventana.getInstance().http.crearUrlArchivo(nombre, rutaFichero);
				}
				if (!yaIntroducido && config != null) {
					System.out.println("hola");
					JsonEntradaMenuModulo webArchivo = new JsonEntradaMenuModulo();
					webArchivo.setArchivo();
					webArchivo.setRandomHexa();
					webArchivo.setTitulo(nombreFichero);
					webArchivo.setDescripcion("." + extension);
					webArchivo.setGoTo(config.getRutaHttp() + "/" + nombre);
					System.out.println("clepnid_webjson");
					webArchivo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagen(nombre));
					ArrayList<ConfiguracionJson> listaModulos = JsonModulosFicheros.obtenerConfiguraciones(extension);
					JsonEntradaMenuModulo modulo = new JsonEntradaMenuModulo();
					if (listaModulos != null) {
						for (ConfiguracionJson configuracionJson : listaModulos) {
							Ventana.getInstance().http.crearUrlModulo(configuracionJson, nombre, rutaFichero);
							// anyadir modulo en website
							modulo.setTitulo(configuracionJson.getTitulo());
							modulo.setRandomHexa();
							modulo.setDescripcion(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setGoTo(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setRutaImagen(configuracionJson.getRutaImagen());
							webArchivo.addModulo(modulo);
						}
					}

					// anyadir a descarga en website
					modulo.setTitulo("Descargar");
					modulo.setRandomHexa();
					modulo.setDescripcion(nombre);
					modulo.setGoTo("/" + nombre);
					modulo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagenDescarga());
					webArchivo.addModulo(modulo);
					config.addWeb(webArchivo);
				}

				if (config != null) {
					synchronized (config) {
						Ventana.getInstance().http.crearUrlIndice(config);
					}
				}

				return true;
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");

		});
	}

	private static void parseControlWebObject(JSONObject employee) {
		// InicializarEnOpcionVentana
		SistemaModulos.getInstance().inicializarModulo("Menu");

		// Obtener web de la lista de webs

		JSONObject webObject = (JSONObject) employee.get("Web");
		ConfiguracionWebJson configJson = new ConfiguracionWebJson();
		
		JsonListaHerramientasBackend listaHerramientas =  JsonListaHerramientasBackend.getInstance();
		String htmlReemplazoBody = (String) webObject.get("HtmlBodyReplace");
		configJson.setHtmlReemplazoBody(htmlReemplazoBody);
		
		listaHerramientas.setHtmlReemplazoBody(htmlReemplazoBody);
		

		String rutaHtml = rutaJson.replace("clepnid_web.json", ((String) webObject.get("Html")));
		rutaHtml = rutaHtml.replace("/.", "");
		listaHerramientas.setHtmlMenuIndex(rutaHtml);
		
		configJson.setHtml(rutaHtml);

		configJson.setRutaHttp((String) webObject.get("rutaHttp"));

		config = new ConfiguracionWebJson(configJson);
	}

}
