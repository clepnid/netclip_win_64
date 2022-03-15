package compartirQR;

import java.util.ArrayList;

public class MusicPlayerJson {
	static private boolean hayMusica = false;
	static private ArrayList<MusicPlayerComponent> lista = new ArrayList<MusicPlayerComponent>();

	public static void anyadirMusica(String nombre) {
		if (hayMusica) {
			lista.add(new MusicPlayerComponent(nombre));
		} else {
			hayMusica = true;
			lista.add(new MusicPlayerComponent(nombre));
		}
	}

	public static String getJson() {
		String json = "{\r\n" + "        index: 0,\r\n" + "        music: [";
		for (MusicPlayerComponent musicPlayerComponent : lista) {
			json += "{path: \"/" + musicPlayerComponent.getRutaArchivo() + "\",";
			json += "stream: \"" + musicPlayerComponent.getRutaStream() + "\"},";
		}
		json += "]}";
		return json;
	}

	public static String getJson(String nombre) {
		System.out.println(nombre+"---------------------");
		MusicPlayerComponent componentAux = new MusicPlayerComponent(nombre);
		int index = -1;
		for (int i = 0; i < lista.size(); i++) {
			if (componentAux.equals(lista.get(i))) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			String json = "{\r\n" + "        index: 0,\r\n" + "        music: [";
			for (MusicPlayerComponent musicPlayerComponent : lista) {
				json += "{path: \"/" + musicPlayerComponent.getRutaArchivo() + "\",";
				json += "stream: \"" + musicPlayerComponent.getRutaStream() + "\"},";
			}
			json += "]}";
			return json;

		} else {
			String json = "{\r\n" + "        index: " + index + ",\r\n" + "        music: [";
			for (MusicPlayerComponent musicPlayerComponent : lista) {
				json += "{path: \"/" + musicPlayerComponent.getRutaArchivo() + "\",";
				json += "stream: \"" + musicPlayerComponent.getRutaStream() + "\"},";
			}
			json += "]}";
			return json;
		}
	}

	public static boolean isHayMusica() {
		return hayMusica;
	}

	public static void setHayMusica(boolean hayMusica) {
		MusicPlayerJson.hayMusica = hayMusica;
	}

	public static ArrayList<MusicPlayerComponent> getLista() {
		return lista;
	}

	public static void setLista(ArrayList<MusicPlayerComponent> lista) {
		MusicPlayerJson.lista = lista;
	}

}
