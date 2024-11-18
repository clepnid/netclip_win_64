package http;

import java.util.ArrayList;

public class JsonModulosGrupos {
	private boolean hayItems;
	private String rutaModuloStream;
	private ArrayList<itemRutasComponent> lista;

	public String getRutaModuloStream() {
		return rutaModuloStream;
	}

	public void setRutaModuloStream(String rutaModuloStream) {
		this.rutaModuloStream = rutaModuloStream;
	}

	public JsonModulosGrupos(String rutaModuloStream) {
		this.rutaModuloStream = rutaModuloStream;
		lista = new ArrayList<itemRutasComponent>();
		hayItems = false;
	}

	public String getJson(String nombreArchivo, String name) {
		System.out.println(name + "---------------------");
		itemRutasComponent componentAux = new itemRutasComponent("/" + nombreArchivo,
				"/clepnid_stream" + this.rutaModuloStream + "/" + nombreArchivo, name);
		int index = -1;
		for (int i = 0; i < lista.size(); i++) {
			if (componentAux.equals(lista.get(i))) {
				index = i;
				break;
			}
		}
		ArrayList<String> sAux = new ArrayList<String>();
		if (index == -1) {
			String json = "{\r\n" + "        \"index\": 0,\r\n" + "        \"content\": [";
			for (itemRutasComponent musicPlayerComponent : lista) {
				String sAux1 = "";
				sAux1 += "{\"path\": \"" + musicPlayerComponent.getRutaArchivo() + "\",";
				sAux1 += "\"name\": \"" + musicPlayerComponent.getNombre() + "\",";
				sAux1 += "\"stream\": \"" + musicPlayerComponent.getRutaStream() + "\"}";
				sAux.add(sAux1);
			}
			for (int i = 0; i < sAux.size(); i++) {
				if (i==sAux.size()-1) {
					json+=sAux.get(i);
				}else {
					json+=sAux.get(i)+",";
				}
			}
			
			json += "]}";
			return json;

		} else {
			String json = "{\r\n" + "        \"index\": " + index + ",\r\n" + "        \"content\": [";
			for (itemRutasComponent musicPlayerComponent : lista) {
				String sAux1 = "";
				sAux1 += "{\"path\": \"" + musicPlayerComponent.getRutaArchivo() + "\",";
				sAux1 += "\"name\": \"" + musicPlayerComponent.getNombre() + "\",";
				sAux1 += "\"stream\": \"" + musicPlayerComponent.getRutaStream() + "\"}";
				sAux.add(sAux1);
			}
			for (int i = 0; i < sAux.size(); i++) {
				if (i==sAux.size()-1) {
					json+=sAux.get(i);
				}else {
					json+=sAux.get(i)+",";
				}
			}
			json += "]}";
			return json;
		}
	}

	public boolean hayItems() {
		return hayItems;
	}

	public void setHayItems(boolean hayItems) {
		this.hayItems = hayItems;
	}

	public ArrayList<itemRutasComponent> getLista() {
		return lista;
	}

	public void setLista(ArrayList<itemRutasComponent> lista) {
		this.lista = lista;
	}

	public void anyadirItem(String nombreArchivo) {
		// TODO Auto-generated method stub
		if (hayItems) {
			lista.add(new itemRutasComponent("/" + nombreArchivo, this.rutaModuloStream + "/" + nombreArchivo,""));
		} else {
			hayItems = true;
			lista.add(new itemRutasComponent("/" + nombreArchivo, this.rutaModuloStream + "/" + nombreArchivo,""));
		}
	}

	public void anyadirItemClepnidStream(String nombreArchivo, String name) {
		// TODO Auto-generated method stub
		if (hayItems) {
			lista.add(new itemRutasComponent("/" + nombreArchivo,
					"/clepnid_stream" + this.rutaModuloStream + "/" + nombreArchivo, name));
		} else {
			hayItems = true;
			lista.add(new itemRutasComponent("/" + nombreArchivo,
					"/clepnid_stream" + this.rutaModuloStream + "/" + nombreArchivo, name));
		}
	}

	public void anyadirItem(String nombreArchivo, String name) {
		// TODO Auto-generated method stub
		if (hayItems) {
			lista.add(new itemRutasComponent("/" + nombreArchivo,
					"/clepnid_stream" + this.rutaModuloStream + "/" + nombreArchivo, name));
		} else {
			hayItems = true;
			lista.add(new itemRutasComponent("/" + nombreArchivo,
					"/clepnid_stream" + this.rutaModuloStream + "/" + nombreArchivo, name));
		}

	}


}
