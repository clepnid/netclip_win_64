package http;

import java.util.ArrayList;

import spark.Request;
import spark.Response;

public class ConfiguracionWebJson implements Cloneable {
	private String htmlReemplazoBody, html, rutaHttp;
	private ArrayList<JsonEntradaMenuModulo> archivos;
	private JsonEntradaMenuTiposFicheroModulo tiposArchivos = new JsonEntradaMenuTiposFicheroModulo();

	public ConfiguracionWebJson() {
		setHtmlReemplazoBody("");
		setHtml("");
		setRutaHttp("");
		this.archivos = new ArrayList<JsonEntradaMenuModulo>();
	}

	public ConfiguracionWebJson(ConfiguracionWebJson jsonAux) {
		setHtmlReemplazoBody(jsonAux.getHtmlReemplazoBody());
		setHtml(jsonAux.getHtml());
		setRutaHttp(jsonAux.getRutaHttp());
		this.archivos = new ArrayList<JsonEntradaMenuModulo>();
		for (JsonEntradaMenuModulo ext : jsonAux.getWebs()) {
			this.archivos.add(ext);
		}
	}

	public synchronized String getJson(Request request, Response response) {
	    StringBuilder json = new StringBuilder("[\n  {\n    \"search\": \"s\",\n    \"content\": [\n      {\n        \"title_content\": \"\",\n        \"webs\": [");

	    boolean hasContent = false;

	    for (JsonEntradaMenuModulo web : archivos) {
	        int permiso = HttpBackendUsuarios.tienePermiso(request, response, web.getGoTo());
	        if (permiso == 0) {
	            hasContent = true;

	            json.append("{")
	                .append("\"hexa\": \"").append(web.getHexa()).append("\",")
	                .append("\"title\": \"").append(web.getTitulo()).append("\",")
	                .append("\"goTo\": \"").append(web.getGoTo()).append("\",")
	                .append("\"description\": \"").append(web.getDescripcion()).append("\",");

	            String descripcion = web.getDescripcion();
	            if (".zip".equals(descripcion)) {
	                json.append("\"image\": \"")
	                    .append(JsonModulosMenuWeb.config.getRutaHttp())
	                    .append("/imagenes/zip.jpg\"},");
	            } else {
	                json.append("\"image\": \"").append(web.getRutaImagen()).append("\"},");
	            }

	            tiposArchivos.setJsonTipo(web);
	        }
	    }

	    if (hasContent) {
	        // Quitar la �ltima coma
	        json.setLength(json.length() - 1);
	    }

	    json.append("]}");
	    json.append(tiposArchivos.getJsonTipo(request, response));
	    json.append("]}]");

	    return json.toString();
	}


	public synchronized String getJsonFiltrado(Request request, Response response, String palabra) {
		String json = "[\r\n" + "  {\r\n" + "    \"search\": \"s\",\r\n" + "    \"content\": [\r\n" + "      {\r\n"
				+ "        \"title_content\": \"\",\r\n" + "        \"webs\": [";
		boolean introducidoAlgo = false;
		palabra = palabra.toUpperCase();
		for (JsonEntradaMenuModulo web : archivos) {
			if (web.getTitulo().toUpperCase().contains(palabra) || web.getDescripcion().toUpperCase().contains(palabra)) {
				int n = HttpBackendUsuarios.tienePermiso(request, response, web.getGoTo());
				if (n == 0) {
					introducidoAlgo = true;
					json = json + "{\"hexa\": \"" + web.getHexa() + "\",";
					json = json + "\"title\": \"" + web.getTitulo() + "\",";
					json = json + "\"goTo\": \"" + web.getGoTo() + "\",";
					json = json + "\"description\": \"" + web.getDescripcion() + "\",";
					json = json + "\"image\": \"" + web.getRutaImagen() + "\"},";
				}
			}
		}
		if (introducidoAlgo) {
			json = json.substring(0, json.length() - 1);
		}
		json = json + "]}]}]";
		return json;
	}

	@Override
	protected synchronized Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new ConfiguracionWebJson(this);
	}

	@Override
	public synchronized String toString() {
		return "Modulo:\n" + "Html:\n" + "    " + this.html + "\n";
	}

	public synchronized String getHtmlReemplazoBody() {
		return htmlReemplazoBody;
	}

	public synchronized void setHtmlReemplazoBody(String htmlReemplazoBody) {
		this.htmlReemplazoBody = htmlReemplazoBody;
	}

	public synchronized String getHtml() {
		return html;
	}

	public synchronized void setHtml(String html) {
		this.html = html;
	}

	public synchronized ArrayList<JsonEntradaMenuModulo> getWebs() {
		return archivos;
	}

	public synchronized void addWeb(JsonEntradaMenuModulo web) {
		this.archivos.add(web);
	}

	public void vaciarWeb() {
		this.archivos = new ArrayList<JsonEntradaMenuModulo>();
		this.tiposArchivos.vaciar();
	}

	public synchronized void eliminarRuta(String ruta) {
		System.out.println(getRutaHttp() + "/" + ruta);
		for (JsonEntradaMenuModulo webJson : archivos) {
			if (webJson.getGoTo().equals(getRutaHttp() + "/" + ruta)) {
				archivos.remove(webJson);
				this.tiposArchivos.remove(webJson);
				return;
			}
		}
	}

	public synchronized String getRutaHttp() {
		return rutaHttp;
	}

	public synchronized JsonEntradaMenuModulo getWeb(String nombre) {
		for (JsonEntradaMenuModulo webJson : archivos) {
			if (webJson.getTitulo().equals(nombre)) {
				return webJson;
			}
		}
		return null;
	}

	public synchronized void setRutaHttp(String rutaHttp) {
		this.rutaHttp = rutaHttp;
	}

}
