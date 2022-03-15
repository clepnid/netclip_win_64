package compartirQR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import portapapeles.Ficheros;
import spark.Spark;
import spark.utils.IOUtils;
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
			crearRecibirArchivos();

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

	private static void crearRecibirArchivos() {
		Spark.post("/uploadFiles/:name", (request, response) -> {
			request.attribute("org.eclipse.jetty.multipartConfig",
					new MultipartConfigElement(Configuracion.deserializar().carpeta));
			System.out.println("el fichero a recibir pesa: " + request.raw().getContentLength() + " bytes");
			Part filePart = request.raw().getPart("file");
			System.out.println(filePart.getName());
			if (!new File(Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName()).exists()) {
				try (InputStream inputStream = filePart.getInputStream()) {
					System.out.println(
							Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName());
					OutputStream outputStream = new FileOutputStream(
							Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName());
					IOUtils.copy(inputStream, outputStream);
					outputStream.close();
				}
			}
			String extension, nombre;
			boolean yaIntroducido;
			if (Ficheros.tipoFichero(filePart.getSubmittedFileName()).equals("video")) {
				nombre = Http.encodeURIcomponent(filePart.getSubmittedFileName());
				yaIntroducido = Ventana.http.estaEnUrl(nombre);
				extension = Ficheros.getExtensionFichero(nombre);
				Ventana.http.crearUrlVideo(nombre,
						Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName());
			} else {
				nombre = Http.encodeURIcomponent(filePart.getSubmittedFileName());
				yaIntroducido = Ventana.http.estaEnUrl(nombre);
				extension = Ficheros.getExtensionFichero(nombre);
				Ventana.http.crearUrlArchivo(nombre,
						Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName());
			}
			if (!yaIntroducido && Clepnid_WebJson.config != null) {
				System.out.println("hola");
				WebJson webArchivo = new WebJson();
				webArchivo.setArchivo();
				webArchivo.setRandomHexa();
				webArchivo.setTitulo(filePart.getSubmittedFileName());
				webArchivo.setDescripcion("." + extension);
				webArchivo.setGoTo(Clepnid_WebJson.config.getRutaHttp() + "/" + nombre);
				webArchivo.setRutaImagen(WebJson.getRutaHttpImagen(nombre));
				ArrayList<ConfiguracionJson> listaModulos = ClepnidJson.obtenerConfiguraciones(extension);
				WebJson modulo = new WebJson();
				if (listaModulos != null) {
					for (ConfiguracionJson configuracionJson : listaModulos) {
						Ventana.http.crearUrlModulo(configuracionJson, nombre, Configuracion.deserializar().carpeta + File.separator + filePart.getSubmittedFileName());
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
