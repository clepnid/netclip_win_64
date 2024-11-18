package http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import ventana.Ventana;
import ventanaGestionarModulo.SistemaModulos;

//controla los elementos json para el /menu
public class JsonEntradaMenuTiposFicheroModulo {

	Map<String, ArrayList<JsonEntradaMenuModulo>> map;

	public JsonEntradaMenuTiposFicheroModulo() {
		map = new HashMap<String, ArrayList<JsonEntradaMenuModulo>>();
		prepararDiccionario();
	}

	public void vaciar() {
		map = new HashMap<String, ArrayList<JsonEntradaMenuModulo>>();
		prepararDiccionario();
	}

	public void remove(JsonEntradaMenuModulo json) {
		for (Map.Entry<String, ArrayList<JsonEntradaMenuModulo>> entry : map.entrySet()) {
			final String key = entry.getKey();

			if (key.equals(title_content(json.getGoTo()))) {
				final ArrayList<JsonEntradaMenuModulo> lista = entry.getValue();
				lista.remove(json);
			}

		}
	}

	public void setJsonTipo(JsonEntradaMenuModulo json) {
		// si es carpeta tendra como tipo por extension desconocido al no tener
		// extension por eso se deberá controlar.
		if (json.getDescripcion().equals(".zip")) {
			for (Map.Entry<String, ArrayList<JsonEntradaMenuModulo>> entry : map.entrySet()) {
				final String key = entry.getKey();

				if (key.equals("Ficheros comprimidos")) {
					final ArrayList<JsonEntradaMenuModulo> lista = entry.getValue();
					if (lista.size() == 0) {
						lista.add(json);
					} else {
						boolean estaEnLista = false;
						for (JsonEntradaMenuModulo webJsonAux : lista) {
							if (webJsonAux.getGoTo().equals(json.getGoTo())) {
								estaEnLista = true;
							}
						}
						if (!estaEnLista) {
							lista.add(json);
						}
					}
				}

			}
		} else {
			// si es un archivo en vez de carpeta
			for (Map.Entry<String, ArrayList<JsonEntradaMenuModulo>> entry : map.entrySet()) {
				final String key = entry.getKey();

				if (key.equals(title_content(json.getGoTo()))) {
					final ArrayList<JsonEntradaMenuModulo> lista = entry.getValue();
					if (lista.size() == 0) {
						lista.add(json);
					} else {
						boolean estaEnLista = false;
						for (JsonEntradaMenuModulo webJsonAux : lista) {
							if (webJsonAux.getGoTo().equals(json.getGoTo())) {
								estaEnLista = true;
							}
						}
						if (!estaEnLista) {
							lista.add(json);
						}
					}
				}

			}
		}
	}
	
	private boolean hayAccesoDownloadVideosUrl(Request req, Response res) {
		SistemaModulos sis = SistemaModulos.getInstance();
		int n1 = HttpBackendUsuarios.tienePermiso(req, res, "/downloadYoutubemp4");
		int n2 = HttpBackendUsuarios.tienePermiso(req, res, "/downloadYoutubemp3");
		int n3 = HttpBackendUsuarios.tienePermiso(req, res, "/downloadOthermp4");
		int n4 = HttpBackendUsuarios.tienePermiso(req, res, "/downloadOthermp3");
		int n5 = HttpBackendUsuarios.tienePermiso(req, res, "/modulo_youtube_downloader");
		
		boolean hayPermisosDeAlgunBackend = ((n1 == 0) || (n2 == 0) || (n3 == 0) || (n4 == 0)) && n5 == 0;
		return existe_modulo_youtube_downloader() && sis.isValido("downloadVideosUrl") && hayPermisosDeAlgunBackend;
	}
	
	private boolean hayAccesoSubirFichero(Request req, Response res) {
		SistemaModulos sis = SistemaModulos.getInstance();
		int n = HttpBackendUsuarios.tienePermiso(req, res, "/uploadFiles");
		return existe_modulo_subir_fichero() && sis.isValido("uploadFiles") && n == 0;
	}

	public String getJsonTipo(Request req, Response res) {
		String jsonAux = "";
		// subir archivos
		jsonAux = jsonAux + ",";
		if (existe_modulo_subir_fichero() || existe_modulo_youtube_downloader()) {
			jsonAux = jsonAux + "{\r\n" + "        \"title_content\": \"\",\r\n";
		}
		if (hayAccesoSubirFichero(req, res) || hayAccesoDownloadVideosUrl(req, res)) {
			jsonAux = jsonAux + "        \"webs\": [";
		}else {
			jsonAux = jsonAux + "        \"webs\": []},";
		}
		if (hayAccesoSubirFichero(req, res)) {
			jsonAux = jsonAux + getJsonModuloSubirFicheros();
			if (hayAccesoDownloadVideosUrl(req, res)) {
				jsonAux = jsonAux + ",";
			}
		}
		if (hayAccesoDownloadVideosUrl(req, res)) {
			jsonAux = jsonAux + getJsonModuloYoutubeDownloader();
		}
		if (hayAccesoSubirFichero(req, res) || hayAccesoDownloadVideosUrl(req, res)) {
			jsonAux = jsonAux + "]}";
			jsonAux = jsonAux + ",";
		}
		for (Map.Entry<String, ArrayList<JsonEntradaMenuModulo>> entry : map.entrySet()) {
			final String key = entry.getKey();
			final ArrayList<JsonEntradaMenuModulo> lista = entry.getValue();
			if (lista.size() != 0 && numeroListaConAcceso(req, res, lista) != 0) {
				jsonAux = jsonAux + "{\r\n" + "        \"title_content\": \"" + key + "\",\r\n" + "        \"webs\": [";
				for (JsonEntradaMenuModulo web : lista) {
					if (HttpBackendUsuarios.tienePermiso(req, res, web.getGoTo()) == 0) {
						jsonAux = jsonAux + "{\"hexa\": \"" + web.getHexa() + "\",";
						jsonAux = jsonAux + "\"title\": \"" + web.getTitulo() + "\",";
						jsonAux = jsonAux + "\"goTo\": \"" + web.getGoTo() + "\",";
						String descripcion = web.getDescripcion();
						jsonAux = jsonAux + "\"description\": \"" + descripcion + "\",";
						// si es carpeta tendra como tipo por extension desconocido al no tener
						// extension por eso se deberá controlar.
						if (descripcion.equals(".zip")) {
							jsonAux = jsonAux + "\"image\": \"" + JsonModulosMenuWeb.config.getRutaHttp()
									+ "/imagenes/zip.jpg\"},";
						} else {
							jsonAux = jsonAux + "\"image\": \"" + web.getRutaImagen() + "\"},";
						}
					}
				}
				jsonAux = jsonAux.substring(0, jsonAux.length() - 1);

				jsonAux = jsonAux + "]}";
				jsonAux = jsonAux + ",";
			}
		}
		jsonAux = jsonAux.substring(0, jsonAux.length() - 1);
		return jsonAux;
	}

	private int numeroListaConAcceso(Request req, Response res, ArrayList<JsonEntradaMenuModulo> lista) {
		int numero = 0;
		for (JsonEntradaMenuModulo webJson : lista) {
			if (HttpBackendUsuarios.tienePermiso(req, res, webJson.getGoTo()) == 0) {
				numero++;
			}
		}
		return numero;
	}

	private String getJsonModuloSubirFicheros() {
		return getJsonHerramientasEstaticas("Subir archivos", "modulo_subir_ficheros", "subir archivo");
	}

	private String getJsonModuloYoutubeDownloader() {
		return getJsonHerramientasEstaticas("Subir Desde Url", "modulo_youtube_downloader", "subir archivo");
	}

	private String getJsonHerramientasEstaticas(String titulo, String nombreCarpeta, String descripcion) {
		String jsonAux = "";
		jsonAux = jsonAux + "{\"hexa\": \"" + JsonEntradaMenuModulo.generateRandomColor() + "\",";
		jsonAux = jsonAux + "\"title\": \"" + titulo + "\",";
		jsonAux = jsonAux + "\"goTo\": \"" + nombreCarpeta + "/index.html" + "\",";
		jsonAux = jsonAux + "\"description\": \"" + descripcion + "\",";
		jsonAux = jsonAux + "\"image\": \"" + nombreCarpeta + "/muestra.jpg" + "\"}";
		return jsonAux;
	}

	public static boolean existe_modulo_subir_fichero() {
		return existe_ruta("./src/html/modulo_subir_ficheros");
	}

	public static boolean existe_modulo_youtube_downloader() {
		return existe_ruta("./src/html/modulo_youtube_downloader");
	}

	private static boolean existe_ruta(String ruta) {
		File file = new File(ruta);
		return file.exists();
	}

	private void prepararDiccionario() {
		map.put("Texto", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Código", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Ejecutables", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Ficheros de datos científicos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Videos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Maquinas virtuales", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Discos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Audios", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Hojas de cálculo", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Documentos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Ficheros de seguridad", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Vectores", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Imágenes", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Base de datos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Enlaces", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Ficheros comprimidos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Juegos", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Presentaciones", new ArrayList<JsonEntradaMenuModulo>());
		map.put("Ficheros desconocidos", new ArrayList<JsonEntradaMenuModulo>());
	}

	public static String title_content(String goTo) {
		String tipo = Ficheros.tipoFichero(goTo);
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_texto"))) {
			return "Texto";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_codigo"))) {
			return "Código";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_ejecutable"))) {
			return "Ejecutables";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_datos_cientificos"))) {
			return "Ficheros de datos científicos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_tipo_fichero_video"))) {
			return "Videos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_maquina_virtual"))) {
			return "Maquinas virtuales";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_disco"))) {
			return "Discos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_audio"))) {
			return "Audios";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_hoja_calculo"))) {
			return "Hojas de cálculo";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_documento"))) {
			return "Documentos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_seguridad"))) {
			return "Ficheros de seguridad";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_vector"))) {
			return "Vectores";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_imagen"))) {
			return "Imágenes";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_base_datos"))) {
			return "Base de datos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_enlace"))) {
			return "Enlaces";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_comprimido"))) {
			return "Ficheros comprimidos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_juego"))) {
			return "Juegos";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_presentacion"))) {
			return "Presentaciones";
		}
		return "Ficheros desconocidos";
	}
}
