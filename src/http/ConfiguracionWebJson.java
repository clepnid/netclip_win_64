package http;

import java.util.ArrayList;

public class ConfiguracionWebJson implements Cloneable {
	private String htmlReemplazoBody, html, rutaHttp;
	private ArrayList<WebJson> archivos;
	private MenuJsonTipos tiposArchivos = new MenuJsonTipos();;

	public ConfiguracionWebJson() {
		setHtmlReemplazoBody("");
		setHtml("");
		setRutaHttp("");
		this.archivos = new ArrayList<WebJson>();
	}

	public ConfiguracionWebJson(ConfiguracionWebJson jsonAux) {
		setHtmlReemplazoBody(jsonAux.getHtmlReemplazoBody());
		setHtml(jsonAux.getHtml());
		setRutaHttp(jsonAux.getRutaHttp());
		this.archivos = new ArrayList<WebJson>();
		for (WebJson ext : jsonAux.getWebs()) {
			this.archivos.add(ext);
		}
	}

	public String getJson() {
		String json = "[\r\n"
				+ "  {\r\n"
				+ "    \"search\": \"s\",\r\n"
				+ "    \"content\": [\r\n"
				+ "      {\r\n"
				+ "        \"title_content\": \"\",\r\n"
				+ "        \"webs\": [";
		//todos los archivos
		for (WebJson web : archivos) {
			json = json + "{\"hexa\": \"" + web.getHexa() + "\",";
			json = json + "\"title\": \"" + web.getTitulo() + "\",";
			json = json + "\"goTo\": \"" + web.getGoTo() + "\",";
			String descripcion= web.getDescripcion();
			json = json + "\"description\": \""+descripcion+"\",";
			//si es carpeta tendra como tipo por extension desconocido al no tener extension por eso se deberá controlar.
			if (descripcion.equals(".zip")) {
				json = json + "\"image\": \""+Clepnid_WebJson.config.getRutaHttp()+"/imagenes/zip.jpg\"},";
			}else {
				json = json + "\"image\": \""+web.getRutaImagen()+"\"},";
			}
			tiposArchivos.setJsonTipo(web);
		}
		if (archivos.size()>0) {
			//quitar la coma
			json = json.substring(0, json.length() - 1);
		}
		
		json = json + "]}";
		//tipos devuelve subir archivo y las listas con los diferentes tipos de archivos
		json = json + tiposArchivos.getJsonTipo();
		
		json = json + "]}]";
		return json;
	}

	public String getJsonFiltrado(String palabra) {
		String json = "[\r\n"
				+ "  {\r\n"
				+ "    \"search\": \"s\",\r\n"
				+ "    \"content\": [\r\n"
				+ "      {\r\n"
				+ "        \"title_content\": \"\",\r\n"
				+ "        \"webs\": [";
		for (WebJson web : archivos) {
			if (web.getTitulo().contains(palabra) || web.getDescripcion().contains(palabra)) {
				json = json + "{\"hexa\": \"" + web.getHexa() + "\",";
				json = json + "\"title\": \"" + web.getTitulo() + "\",";
				json = json + "\"goTo\": \"" + web.getGoTo() + "\",";
				json = json + "\"description\": \"" + web.getDescripcion() + "\",";
				json = json + "\"image\": \"" + web.getRutaImagen() + "\"},";
			}
		}
		json = json.substring(0, json.length() - 1);
		json = json + "]}]}]";
		return json;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new ConfiguracionWebJson(this);
	}

	@Override
	public String toString() {
		return "Modulo:\n" + "Html:\n" + "    " + this.html + "\n";
	}

	public String getHtmlReemplazoBody() {
		return htmlReemplazoBody;
	}

	public void setHtmlReemplazoBody(String htmlReemplazoBody) {
		this.htmlReemplazoBody = htmlReemplazoBody;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public ArrayList<WebJson> getWebs() {
		return archivos;
	}

	public void addWeb(WebJson web) {
		this.archivos.add(web);
	}

	public void vaciarWeb() {
		this.archivos = new ArrayList<WebJson>();
		this.tiposArchivos.vaciar();
	}

	public void eliminarRuta(String ruta) {
		System.out.println(getRutaHttp() + "/" + ruta);
		for (WebJson webJson : archivos) {
			if (webJson.getGoTo().equals(getRutaHttp() + "/" + ruta)) {
				archivos.remove(webJson);
				this.tiposArchivos.remove(webJson);
				return;
			}
		}
	}

	public String getRutaHttp() {
		return rutaHttp;
	}

	public WebJson getWeb(String nombre) {
		for (WebJson webJson : archivos) {
			if (webJson.getTitulo().equals(nombre)) {
				return webJson;
			}
		}
		return null;
	}

	public void setRutaHttp(String rutaHttp) {
		this.rutaHttp = rutaHttp;
	}
}
