package compartirQR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import portapapeles.Ficheros;
import ventana.Ventana;

public class MenuJsonTipos {

	Map<String, ArrayList<WebJson>> map;

	public MenuJsonTipos() {
		map = new HashMap<String, ArrayList<WebJson>>();
		prepararDiccionario();
	}
	
	public void vaciar() {
		map = new HashMap<String, ArrayList<WebJson>>();
		prepararDiccionario();
	}
	
	public void remove(WebJson json) {
		for (Map.Entry<String, ArrayList<WebJson>> entry : map.entrySet()) {
			final String key = entry.getKey();

			if (key.equals(title_content(json.getGoTo()))) {
				final ArrayList<WebJson> lista = entry.getValue();
				lista.remove(json);
			}

		}
	}

	public void setJsonTipo(WebJson json) {
		//si es carpeta tendra como tipo por extension desconocido al no tener extension por eso se deberá controlar.
		if (json.getDescripcion().equals(".zip")) {
			for (Map.Entry<String, ArrayList<WebJson>> entry : map.entrySet()) {
				final String key = entry.getKey();
	
				if (key.equals("Ficheros comprimidos")) {
					final ArrayList<WebJson> lista = entry.getValue();
					if (lista.size() == 0) {
						lista.add(json);
					} else {
						boolean estaEnLista = false;
						for (WebJson webJsonAux : lista) {
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
		}else {
			// si es un archivo en vez de carpeta
			for (Map.Entry<String, ArrayList<WebJson>> entry : map.entrySet()) {
				final String key = entry.getKey();
	
				if (key.equals(title_content(json.getGoTo()))) {
					final ArrayList<WebJson> lista = entry.getValue();
					if (lista.size() == 0) {
						lista.add(json);
					} else {
						boolean estaEnLista = false;
						for (WebJson webJsonAux : lista) {
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

	public String getJsonTipo() {
		String jsonAux = "";
		//subir archivos
		jsonAux = jsonAux + ",";
		jsonAux = jsonAux + "{\r\n" + "        \"title_content\": \"\",\r\n" + "        \"webs\": [";
		jsonAux = jsonAux + "{\"hexa\": \"" + "#000000" + "\",";
		jsonAux = jsonAux + "\"title\": \"" + "Subir archivos" + "\",";
		jsonAux = jsonAux + "\"goTo\": \"" + "modulo_subir_ficheros/index.html" + "\",";
		jsonAux = jsonAux + "\"description\": \"" + "subir archivo" + "\",";
		//no hay imagen para publicar
		jsonAux = jsonAux + "\"image\": \"" + "modulo_subir_ficheros/muestra.jpg" + "\"}";
		jsonAux = jsonAux + "]}";
		for (Map.Entry<String, ArrayList<WebJson>> entry : map.entrySet()) {
			final String key = entry.getKey();
			final ArrayList<WebJson> lista = entry.getValue();
			if (lista.size() != 0) {
				jsonAux = jsonAux + ",";
				jsonAux = jsonAux + "{\r\n" + "        \"title_content\": \"" + key + "\",\r\n" + "        \"webs\": [";
				for (WebJson web : lista) {
					jsonAux = jsonAux + "{\"hexa\": \"" + web.getHexa() + "\",";
					jsonAux = jsonAux + "\"title\": \"" + web.getTitulo() + "\",";
					jsonAux = jsonAux + "\"goTo\": \"" + web.getGoTo() + "\",";
					String descripcion = web.getDescripcion();
					jsonAux = jsonAux + "\"description\": \"" + descripcion + "\",";
					// si es carpeta tendra como tipo por extension desconocido al no tener
					// extension por eso se deberá controlar.
					if (descripcion.equals(".zip")) {
						jsonAux = jsonAux + "\"image\": \"" + Clepnid_WebJson.config.getRutaHttp()
								+ "/imagenes/zip.jpg\"},";
					} else {
						jsonAux = jsonAux + "\"image\": \"" + web.getRutaImagen() + "\"},";
					}
				}
				jsonAux = jsonAux.substring(0, jsonAux.length() - 1);

				jsonAux = jsonAux + "]}";
			}
		}
		return jsonAux;
	}

	private void prepararDiccionario() {
		map.put("Texto", new ArrayList<WebJson>());
		map.put("Código", new ArrayList<WebJson>());
		map.put("Ejecutables", new ArrayList<WebJson>());
		map.put("Ficheros de datos científicos", new ArrayList<WebJson>());
		map.put("Videos", new ArrayList<WebJson>());
		map.put("Maquinas virtuales", new ArrayList<WebJson>());
		map.put("Discos", new ArrayList<WebJson>());
		map.put("Audios", new ArrayList<WebJson>());
		map.put("Hojas de cálculo", new ArrayList<WebJson>());
		map.put("Documentos", new ArrayList<WebJson>());
		map.put("Ficheros de seguridad", new ArrayList<WebJson>());
		map.put("Vectores", new ArrayList<WebJson>());
		map.put("Imágenes", new ArrayList<WebJson>());
		map.put("Base de datos", new ArrayList<WebJson>());
		map.put("Enlaces", new ArrayList<WebJson>());
		map.put("Ficheros comprimidos", new ArrayList<WebJson>());
		map.put("Juegos", new ArrayList<WebJson>());
		map.put("Presentaciones", new ArrayList<WebJson>());
		map.put("Ficheros desconocidos", new ArrayList<WebJson>());
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
