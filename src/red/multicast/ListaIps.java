package red.multicast;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import ventana.Configuracion;

public class ListaIps extends CopyOnWriteArrayList<String> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numeroIpsPropias;
	public CopyOnWriteArrayList<String> listaNombres, listaSeriales;

	public ListaIps() {
		add("1");
		listaNombres = new CopyOnWriteArrayList<String>();
		listaSeriales = new CopyOnWriteArrayList<String>();
		listaNombres.add("");
		listaSeriales.add("");
		numeroIpsPropias = 0;
		CopyOnWriteArrayList<String> listaMiIp = MulticastControl.getMyIps();
		Configuracion config = null;
		try {
			config = Configuracion.deserializar();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String ips : listaMiIp) {
			add(ips);
			listaNombres.add(config.nombre);
			listaSeriales.add(config.serial);
			numeroIpsPropias++;
		}
	}

	public void cerrar() {
		set(0, "0");
	}

	public String getIpdeNombre(String nombre) {
		int indice = listaNombres.indexOf(nombre);
		if (indice != -1) {
			return get(indice);
		}
		return null;
	}

	public String getIpdeSerial(String serial) {
		int indice = listaSeriales.indexOf(serial);
		if (indice != -1) {
			return get(indice);
		}
		return null;
	}

	public String getNombre(String ip) {
		int indice = indexOf(ip);
		if (indice != -1) {
			return getListaIpsPropias().get(indice);
		}
		return null;
	}

	public String getSerial(String ip) {
		int indice = indexOf(ip);
		if (indice != -1) {
			return getListaIpsPropias().get(indice);
		}
		return null;
	}

	public ListaIps getListaIpsPropias() {
		return new ListaIps();
	}

	public int getNumeroIpsPropias() {
		return numeroIpsPropias;
	}

	public void setNumeroIpsPropias(int numeroIpsPropias) {
		this.numeroIpsPropias = numeroIpsPropias;
	}

	@Override
	public String remove(int index) {
		listaNombres.remove(index);
		listaSeriales.remove(index);
		return super.remove(index);
	}
}
