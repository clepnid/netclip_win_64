package http;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import http.actividad.ActividadApi;
import ventanaGestionarModulo.SistemaModulos;

//lee el fichero clepnid.json de los modulos y los añade en el arraylist config.
public class JsonModulosFicheros {

	public static ArrayList<ConfiguracionJson> config;
	private static String rutaJson;

	static public void iniciar() {
		config = new ArrayList<ConfiguracionJson>();
	}

	public static ArrayList<ConfiguracionJson> obtenerConfiguraciones(String extension) {
		ArrayList<ConfiguracionJson> configAux = new ArrayList<ConfiguracionJson>();
		boolean correcto;
		for (ConfiguracionJson configuracionJson : config) {
			correcto = false;
			for (String ext : configuracionJson.getExtensiones()) {
				if (extension.toUpperCase().equals(ext.toUpperCase())) {
					correcto = true;
					break;
				}
			}
			if (correcto) {
				try {
					configAux.add((ConfiguracionJson) configuracionJson.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (configAux.size() == 0)
			return null;
		return configAux;
	}

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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void parseControlWebObject(JSONObject employee) {
		// Obtener web de la lista de webs
		JSONObject webObject = (JSONObject) employee.get("Web");
		ConfiguracionJson configJson = new ConfiguracionJson();

		// introducir titulo
		String tit = (String) webObject.get("Title");
		tit = tit.substring(0, 1).toUpperCase() + tit.substring(1);
		configJson.setTitulo(tit);

		// InicializarEnOpcionVentana
		SistemaModulos.getInstance().inicializarModulo(tit);

		@SuppressWarnings("unchecked")
		ArrayList<String> extensiones = (ArrayList<String>) webObject.get("Extensions");
		for (String ext : extensiones) {
			configJson.setExtension(ext);
		}
		

		String extAux = "";
		for (int i = 0; i < extensiones.size(); i++) {
			if (i != extensiones.size()-1) {
				extAux += extensiones.get(i) + ", ";
			}else {
				extAux += extensiones.get(i);
			}
			
		}		

		configJson.setTextoBoton((String) webObject.get("BotonText"));

		configJson.setHtmlReemplazoBody((String) webObject.get("HtmlBodyReplace"));

		String rutaHtml = rutaJson.replace("clepnid.json", ((String) webObject.get("Html")));
		rutaHtml = rutaHtml.replace("/.", "");
		configJson.setHtml(rutaHtml);
		configJson.setRutaImagen((String) webObject.get("rutaImagen"));
		String rutaHttp = (String) webObject.get("rutaHttp");
		configJson.setRutaHttp(rutaHttp);

		String esGrupal = ((String) webObject.get("Group"));
		if (esGrupal.equals("si")) {
			configJson.setGrupo(true);
			if (configJson.getTitulo().equals("Music Reproductor")) {
				configJson.setRutasJson(new JsonModulosGrupos(configJson.getRutaHttp()));
			}
		} else {
			configJson.setGrupo(false);
		}
		
		String actividad = "";
		actividad = tit;
		actividad +=", el link estará compuesto por el nombre del archivo subido al sistema y finalizando la ruta con: "+rutaHttp;
		actividad += ", para extenciones: "+extAux+".";
		ActividadApi.getInstance().addActividad(1, actividad);
		config.add(configJson);
	}

}
