package http;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ventanaGestionarModulo.SistemaModulos;

//lee el fichero clepnid.json de los modulos y los añade en el arraylist config.
public class JsonModulosFicheros {
	
	public static ArrayList<ConfiguracionJson> config;
	private static String rutaJson;
	
	static public void iniciar() {
		config = new ArrayList<ConfiguracionJson>();
	}
	
	public static ArrayList<ConfiguracionJson> obtenerConfiguraciones(String extension){
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
		if (configAux.size()==0) 
			return null;
		return configAux;
	} 

	@SuppressWarnings("unchecked")
	public static void InstanciarWeb(String ruta) {

		rutaJson = ruta.replace("\\", "/");
        JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(rutaJson))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray webList = (JSONArray) obj;
            System.out.println(webList);
             
            webList.forEach( web -> parseControlWebObject( (JSONObject) web ) );
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	private static void parseControlWebObject(JSONObject employee) 
    {
        //Obtener web de la lista de webs
        JSONObject webObject = (JSONObject) employee.get("Web");
        ConfiguracionJson configJson = new ConfiguracionJson();
        
        //introducir titulo
        String t = (String) webObject.get("Title");
        configJson.setTitulo(t);   

        //InicializarEnOpcionVentana
        SistemaModulos.getInstance().inicializarModulo(t);
        
         
        @SuppressWarnings("unchecked")
		ArrayList<String> extensiones = (ArrayList<String>) webObject.get("Extensions");
        for (String ext : extensiones) {
			configJson.setExtension(ext);
		}
        
        configJson.setTextoBoton((String) webObject.get("BotonText"));

        configJson.setHtmlReemplazoBody((String) webObject.get("HtmlBodyReplace"));
        
        String rutaHtml = rutaJson.replace("clepnid.json", ((String) webObject.get("Html")));
        rutaHtml = rutaHtml.replace("/.", "");
        
        
        configJson.setHtml(rutaHtml);

        
        
        configJson.setRutaImagen((String) webObject.get("rutaImagen"));
        
        configJson.setRutaHttp((String) webObject.get("rutaHttp"));
        
        String esGrupal = ((String) webObject.get("Group"));
        if (esGrupal.equals("si")) {
			configJson.setGrupo(true);
			if (configJson.getTitulo().equals("Music Reproductor")) {
				configJson.setRutasJson(new JsonModulosGrupos(configJson.getRutaHttp()));
			}
		}else {
			configJson.setGrupo(false);
		}
        
        config.add(configJson);
    }
	
}
