package http;

import java.util.ArrayList;

import portapapeles.Ficheros;
import ventana.Ventana;

//se usa para dar formato y funcionabilidad a cada modulo por entrada de fichero
public class WebJson implements Cloneable{
	private String hexa, titulo, goTo, descripcion, rutaImagen;
	private ArrayList<WebJson> modulos;
	private boolean archivo;
	
	public WebJson() {
		archivo = false;
		setTitulo("");
		setDescripcion("");
		setHexa("");
		setGoTo("");
		setRutaImagen("");
	}
	
	public void setArchivo() {
		archivo = true;
		modulos = new ArrayList<WebJson>();
	}
	
	public boolean getEsArchivo() {
		return archivo;
	}

	public WebJson(WebJson jsonAux) {
		setTitulo(jsonAux.getTitulo());
		setDescripcion(jsonAux.getDescripcion());
		setHexa(jsonAux.getHexa());
		setGoTo(jsonAux.getGoTo());
		setRutaImagen(jsonAux.getRutaImagen());
		if (jsonAux.getEsArchivo()) {
			setArchivo();
			for (WebJson webJson : jsonAux.getModulos()) {
				addModulo(webJson);
			}
		}
	}
	

	public String getJson() {
		String json = "[\r\n"
				+ "  {\r\n"
				+ "    \"search\": \"\",\r\n"
				+ "    \"content\": [\r\n"
				+ "      {\r\n"
				+ "        \"title_content\": \"\",\r\n"
				+ "        \"webs\": [";
		for (WebJson web : modulos) {
			json = json + "{\"hexa\": \""+web.getHexa()+"\",";
			json = json + "\"title\": \""+web.getTitulo()+"\",";
			json = json + "\"goTo\": \""+web.getGoTo()+"\",";
			json = json + "\"description\": \""+web.getDescripcion()+"\",";
			json = json + "\"image\": \""+web.getRutaImagen()+"\"},";
		}
		json = json.substring(0, json.length()-1);
		json = json + "]}]}]";
		return json;
	}


	public static  String getTextJson() {
		String json = "";
		json = json + "{\"hexa\": \""+generateRandomColor()+"\",";
		json = json + "\"title\": \""+"Texto"+"\",";
		json = json + "\"goTo\": \"/index.html"+"\",";
		json = json + "\"description\": \"Texto compartido"+""+"\",";
		json = json + "\"image\": \""+getRutaHttpImagen("hola.txt")+"\"}";
		return json;
	}

	public static  String getHtmlJson() {
		String json = "";
		json = json + "{\"hexa\": \""+generateRandomColor()+"\",";
		json = json + "\"title\": \""+"Html"+"\",";
		json = json + "\"goTo\": \"/pagina.html"+"\",";
		json = json + "\"description\": \"Html compartido"+""+"\",";
		json = json + "\"image\": \""+getRutaHttpImagen("hola.cpp")+"\"}";
		return json;
	}

	public static  String getArchivosJson() {
		String json = "";
		json = json + "{\"hexa\": \""+generateRandomColor()+"\",";
		json = json + "\"title\": \""+"Ficheros"+"\",";
		json = json + "\"goTo\": \""+Clepnid_WebJson.config.getRutaHttp()+"\",";
		json = json + "\"description\": \"Ficheros compartido"+""+"\",";
		json = json + "\"image\": \""+getRutaHttpImagen("hola.zip")+"\"}";
		return json;
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new WebJson(this);
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getHexa() {
		return hexa;
	}

	public void setHexa(String hexa) {
		this.hexa = hexa;
	}
	
	public void setRandomHexa() {
		this.hexa = generateRandomColor();
	}
	
	public static String generateRandomColor(){
		String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String color = "#";
		for (int i = 0; i < 6; i++ ) {
		    color += letters[(int) Math.round(Math.random() * 15)];
		}
		return color;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public String getGoTo() {
		return goTo;
	}

	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}

	public ArrayList<WebJson> getModulos() {
		return modulos;
	}
	
	public static String getRutaHttpImagenDescarga() {
		return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/descargar.jpg";
	}
	
	public static String getRutaHttpImagen(String ruta) {
		String tipo = Ficheros.tipoFichero(ruta);
		System.out.println(tipo+"--------------------------------------------");
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_texto"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/texto.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_codigo"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/codigo.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_ejecutable"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/config.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_datos_cientificos"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/cientificos.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_tipo_fichero_video"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/video.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_maquina_virtual"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/maquina_virtual.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_disco"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/disco.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_audio"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/musica.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_hoja_calculo"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/excel.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_documento"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/pdf.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_seguridad"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/seguridad.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_vector"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/vector.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_imagen"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/imagen.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_base_datos"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/base-datos.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_enlace"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/enlace.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_comprimido"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/zip.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_juego"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/juegos.jpg";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_presentacion"))) {
			return Clepnid_WebJson.config.getRutaHttp()+"/imagenes/grafico.jpg";
		}
		return "";
	}

	public void addModulo(WebJson modulo) {
		try {
			this.modulos.add((WebJson) modulo.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
