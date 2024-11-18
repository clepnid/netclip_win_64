package http;

import java.io.Serializable;
import java.util.ArrayList;

//esta clase se manda como contenido para los paneles visualizados en la ventana.
public class ConfiguracionJson implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String titulo, textoBoton, htmlReemplazoBody, html, rutaHttp, rutaImagen;
	private boolean grupo;
	private ArrayList<String> extensiones;
	private JsonModulosGrupos rutasJson;

	public JsonModulosGrupos getRutasJson() {
		return rutasJson;
	}

	public void setRutasJson(JsonModulosGrupos rutasJson) {
		this.rutasJson = rutasJson;
	}

	public ConfiguracionJson() {
		setTitulo("");
		setTextoBoton("");
		setHtmlReemplazoBody("");
		setHtml("");
		setRutaHttp("");
		setRutaImagen("");
		setRutasJson(new JsonModulosGrupos(""));
		setGrupo(false);
		this.extensiones = new ArrayList<String>();
	}

	public ConfiguracionJson(ConfiguracionJson jsonAux) {
		setTitulo(jsonAux.getTitulo());
		setTextoBoton(jsonAux.getTextoBoton());
		setHtmlReemplazoBody(jsonAux.getHtmlReemplazoBody());
		setHtml(jsonAux.getHtml());
		setRutaImagen(jsonAux.getRutaImagen());
		setGrupo(jsonAux.isGrupo());
		setRutaHttp(jsonAux.getRutaHttp());
		setRutasJson(jsonAux.getRutasJson());
		this.extensiones = new ArrayList<String>();
		for (String ext : jsonAux.extensiones) {
			extensiones.add(ext);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new ConfiguracionJson(this);
	}

	@Override
	public String toString() {
		String ext = "";
		for (String ex : extensiones) {
			ext = ext + ", " + ex;
		}
		String grupal = "";
		if (this.grupo)
			grupal = "Es un modulo colectivo";
		else
			grupal = "No es un modulo colectivo";
		return "Modulo:\n" + "    " + this.titulo + "\n" + "Extensiones:\n" + "    " + ext + "\n" + "Texto boton:\n"
				+ "    " + this.textoBoton + "\n" + grupal + "\n" + "Html:\n" + "    " + this.html + "\n";
	}

	public String getTextoBoton() {
		return textoBoton;
	}

	public void setTextoBoton(String textoBoton) {
		this.textoBoton = textoBoton;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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

	public boolean isGrupo() {
		return grupo;
	}

	public void setGrupo(boolean grupo) {
		this.grupo = grupo;
	}

	public ArrayList<String> getExtensiones() {
		return extensiones;
	}

	public void setExtension(String extension) {
		this.extensiones.add(extension);
	}

	public String getRutaHttp() {
		return rutaHttp;
	}

	public void setRutaHttp(String rutaHttp) {
		this.rutaHttp = rutaHttp;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
}
