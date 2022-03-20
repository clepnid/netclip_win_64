package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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

import portapapeles.Ficheros;
import spark.Spark;
import ventana.Configuracion;
import ventana.Ventana;

// se usa para leer el json de menuArchivo y darle funcionabilidad al momento de cargarse.
public class Clepnid_WebJson {

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
			if (MenuJsonTipos.existe_modulo_subir_fichero()) {
				crearRecibirArchivos();
			}
			if (MenuJsonTipos.existe_modulo_youtube_downloader()) {
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
		Spark.get("/menu", (req, res) -> Http.renderIndex(req, res, config.getHtml()));

		Spark.after("/menu", (request, response) -> {
			String body = response.body();
			response.body(body.replace(config.getHtmlReemplazoBody(),
					"[\r\n" + "  {\r\n" + "    \"search\": \"\",\r\n" + "    \"content\": [\r\n" + "      {\r\n"
							+ "        \"title_content\": \"\",\r\n" + "        \"webs\": [" + WebJson.getArchivosJson()
							+ "," + WebJson.getTextJson() + "," + WebJson.getHtmlJson() + "]}]}]"));
		});
	}

	private static void crearBuscador() {
		Spark.get("/searchFile/:name", (request, response) -> Http.renderIndex(request, response, config.getHtml()));
		Spark.redirect.get("/searchFile/", config.getRutaHttp());
		Spark.after("/searchFile/:name", (request, response) -> {
			String body = response.body();
			response.body(body.replace(config.getHtmlReemplazoBody(), config.getJsonFiltrado(request.params(":name"))));
		});
	}

	private static void crearVideoDownloader() {
		Spark.get("/downloadYoutubemp4", (request, response) -> {
			VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.YoutubeVideo,
					request.queryParams("url"));
			downloader.setFormat(request.queryParams("format"));
			downloader.setQuality(request.queryParams("quality"));
			downloader.setOutputName(request.queryParams("outputName"));
			downloader.start();
			return "Peticion enviada";
		});

		Spark.get("/downloadYoutubemp3", (request, response) -> {
			VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.YoutubeAudio,
					request.queryParams("url"));
			downloader.setQuality(request.queryParams("quality"));
			downloader.setOutputName(request.queryParams("outputName"));
			downloader.start();
			return "Peticion enviada";
		});

		Spark.get("/downloadOthermp4", (request, response) -> {
			VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.OtherVideo,
					request.queryParams("url"));
			downloader.setOutputName(request.queryParams("outputName"));
			downloader.start();
			return "Peticion enviada";
		});

		Spark.get("/downloadOthermp3", (request, response) -> {
			VideoDownloader downloader = new VideoDownloader(VideoDownloader.Tipo.OtherAudio,
					request.queryParams("url"));
			downloader.setOutputName(request.queryParams("outputName"));
			downloader.start();
			return "Peticion enviada";
		});

	}

	private static void crearRecibirArchivos() {
		Spark.post("/uploadFiles/:name", (request, response) -> {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request.raw());
			if (!isMultipart) {
				//
			}
			String nombreFichero = request.params(":name");
			String rutaFichero = Configuracion.deserializar().carpeta + File.separator + request.params(":name");
			System.out.println(rutaFichero);
			DiskFileItemFactory dff = new DiskFileItemFactory();
			String savePath = Configuracion.deserializar().carpeta;
			dff.setRepository(new File(savePath));
			ServletFileUpload sfu = new ServletFileUpload(dff);
			sfu.setSizeMax(6 * 1024 * 1024 * 1024);
			sfu.setHeaderEncoding("utf-8");
			FileItemIterator fii = sfu.getItemIterator(request.raw());
			while (fii.hasNext()) {
				FileItemStream fis = fii.next();
				if (!fis.isFormField()) {

					BufferedInputStream in = new BufferedInputStream(fis.openStream());
					FileOutputStream out = new FileOutputStream(new File(rutaFichero));
					BufferedOutputStream output = new BufferedOutputStream(out);
					Streams.copy(in, output, true);

				}
			}

			String extension, nombre;
			boolean yaIntroducido;
			if (Ficheros.tipoFichero(nombreFichero).equals("video")) {
				nombre = Http.encodeURIcomponent(nombreFichero);
				yaIntroducido = Ventana.http.estaEnUrl(nombre);
				extension = Ficheros.getExtensionFichero(nombre);
				Ventana.http.crearUrlVideo(nombre, rutaFichero);
			} else {
				nombre = Http.encodeURIcomponent(nombreFichero);
				yaIntroducido = Ventana.http.estaEnUrl(nombre);
				extension = Ficheros.getExtensionFichero(nombre);
				Ventana.http.crearUrlArchivo(nombre, rutaFichero);
			}
			if (!yaIntroducido && Clepnid_WebJson.config != null) {
				System.out.println("hola");
				WebJson webArchivo = new WebJson();
				webArchivo.setArchivo();
				webArchivo.setRandomHexa();
				webArchivo.setTitulo(nombreFichero);
				webArchivo.setDescripcion("." + extension);
				webArchivo.setGoTo(Clepnid_WebJson.config.getRutaHttp() + "/" + nombre);
				webArchivo.setRutaImagen(WebJson.getRutaHttpImagen(nombre));
				ArrayList<ConfiguracionJson> listaModulos = ClepnidJson.obtenerConfiguraciones(extension);
				WebJson modulo = new WebJson();
				if (listaModulos != null) {
					for (ConfiguracionJson configuracionJson : listaModulos) {
						Ventana.http.crearUrlModulo(configuracionJson, nombre, rutaFichero);
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
				modulo.setRutaImagen(WebJson.getRutaHttpImagenDescarga());
				webArchivo.addModulo(modulo);
				Clepnid_WebJson.config.addWeb(webArchivo);
			}

			if (Clepnid_WebJson.config != null) {
				Ventana.http.crearUrlIndice(Clepnid_WebJson.config);
			}

			return "File uploaded and saved.";
		});
	}

	private static void parseControlWebObject(JSONObject employee) {
		// Obtener web de la lista de webs
		JSONObject webObject = (JSONObject) employee.get("Web");
		ConfiguracionWebJson configJson = new ConfiguracionWebJson();

		configJson.setHtmlReemplazoBody((String) webObject.get("HtmlBodyReplace"));

		String rutaHtml = rutaJson.replace("clepnid_web.json", ((String) webObject.get("Html")));
		rutaHtml = rutaHtml.replace("/.", "");

		configJson.setHtml(rutaHtml);

		configJson.setRutaHttp((String) webObject.get("rutaHttp"));

		config = new ConfiguracionWebJson(configJson);
	}

}
