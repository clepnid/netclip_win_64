package http.modulosBackend;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import http.HttpBackendUsuarios;
import spark.Request;
import spark.Response;
import ventanaGestionarModulo.SistemaModulos;

public class JsonListaHerramientasBackend {
	
	private class IconoHerramienta {
		
		private String titulo, go, descripcion, imagen;
		
		public String getJson() {
			String json = "";
			json = json + "{\"hexa\": \"" + generateRandomColor() + "\",";
			json = json + "\"title\": \"" + getTitulo() + "\",";
			json = json + "\"goTo\": \"" + getGo() + "\",";
			json = json + "\"description\": \""+ getDescripcion() + "" + "\",";
			json = json + "\"image\": \"" + getImagen()  + "\"}";
			return json;
		}
		
		private String generateRandomColor() {
			String[] letters = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
			String color = "#";
			for (int i = 0; i < 6; i++) {
				color += letters[(int) Math.round(Math.random() * 15)];
			}
			return color;
		}

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		public String getGo() {
			return go;
		}

		public void setGo(String go) {
			this.go = go;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public String getImagen() {
			return imagen;
		}

		public void setImagen(String imagen) {
			this.imagen = imagen;
		}
	}
	
	private static JsonListaHerramientasBackend INSTANCE;
	private ArrayList<IconoHerramienta> iconos;
	private String htmlReemplazoBody, htmlMenuIndex;
	
    
    private JsonListaHerramientasBackend() {    
    	setIconos(new ArrayList<JsonListaHerramientasBackend.IconoHerramienta>());
    }
    
    public static JsonListaHerramientasBackend getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new JsonListaHerramientasBackend();
        }
        
        return INSTANCE;
    }
    
    public void getPage(Request request, Response responce)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, "*");
		System.out.println(n);
		if (n == 0) {
			String body = responce.body();
			responce.body(body.replace(htmlReemplazoBody,
					"[\r\n" + "  {\r\n" + "    \"search\": \"\",\r\n" + "    \"content\": [\r\n" + "      {\r\n"
							+ "        \"title_content\": \"\",\r\n" + "        \"webs\": ["
							+ getListaIconosJson(request, responce) + "]}]}]"));
		}
	}
    
    private String getListaIconosJson(Request request, Response responce) {
    	ArrayList<String> iconosDisponibles = new ArrayList<String>();
    	SistemaModulos sis = SistemaModulos.getInstance();
    	
    	for (IconoHerramienta i : this.iconos) {
    		String ruta = i.getGo();
    		int n = HttpBackendUsuarios.tienePermiso(request, responce, ruta);
    		System.out.println(ruta+": permisos: "+ n);
    		System.out.println(ruta+": esValido: "+ sis.isValido(i.getTitulo()));
    		System.out.println(i.getJson());
			if (n==0 && sis.isValido(i.getTitulo())) {
				iconosDisponibles.add(i.getJson());
			}
		}
    	
    	String s = "";
    	for (int i = 0; i < iconosDisponibles.size(); i++) {
			if (i == iconosDisponibles.size()-1) {
				s += iconosDisponibles.get(i);
			}else {
				s += iconosDisponibles.get(i)+",";
			}
		}
    	System.out.println(s);
    	return s;
    }

	public ArrayList<IconoHerramienta> getIconos() {
		return iconos;
	}

	public void setIconos(ArrayList<IconoHerramienta> iconos) {
		this.iconos = iconos;
	}

	public void InstanciarWeb(String ruta) {

		String rutaJson = ruta.replace("\\", "/");
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
	
	private void parseControlWebObject(JSONObject employee) {
		IconoHerramienta icono = new IconoHerramienta();
		// Obtener web de la lista de webs
		JSONObject webObject = (JSONObject) employee.get("Web");
		// introducir titulo
		@SuppressWarnings("unused")
		String titulo = (String) webObject.get("Title");	
		icono.setTitulo(titulo);
		@SuppressWarnings("unused")
		String go = (String) webObject.get("GoTo");	
		icono.setGo(go);
		@SuppressWarnings("unused")
		String descripcion = (String) webObject.get("Description");	
		icono.setDescripcion(descripcion);
		@SuppressWarnings("unused")
		String imagen = (String) webObject.get("Image");	
		icono.setImagen(imagen);
		this.iconos.add(icono);

	}

	public String getHtmlReemplazoBody() {
		return htmlReemplazoBody;
	}

	public void setHtmlReemplazoBody(String htmlReemplazoBody) {
		this.htmlReemplazoBody = htmlReemplazoBody;
	}

	public String getHtmlMenuIndex() {
		return htmlMenuIndex;
	}

	public void setHtmlMenuIndex(String htmlMenuIndex) {
		this.htmlMenuIndex = htmlMenuIndex;
	}
}
