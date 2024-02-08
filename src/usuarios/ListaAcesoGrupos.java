package usuarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import http.JerarquiaRutas;
import http.JerarquiaRutas.HijoPadre;
import spark.Spark;
import spark.routematch.RouteMatch;

public class ListaAcesoGrupos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class RutaGrupo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String ruta;
		private ArrayList<String> grupos;

		public RutaGrupo() {
			grupos = new ArrayList<String>();
		}

		public ArrayList<String> getGrupos() {
			return grupos;
		}

		public void setGrupos(ArrayList<String> grupos) {
			this.grupos.clear();
			for (String string : grupos) {
				this.grupos.add(string);
			}
		}

		public String getRuta() {
			return ruta;
		}

		public boolean contieneGrupo(String grupo) {
			for (String string : this.grupos) {
				if (grupo.equals(string)) {
					return true;
				}

			}
			return false;

		}

		public boolean anyadirGrupo(String grupo) {
			if (!this.grupos.contains(grupo)) {
				this.grupos.add(grupo);
				return true;
			}
			return false;
		}

		RutaGrupo(String ruta) {
			this.grupos = new ArrayList<String>();
			this.ruta = ruta;
		}

		RutaGrupo(String ruta, ArrayList<String> grupos) {
			this.grupos = grupos;
			this.ruta = ruta;
		}

		public boolean tieneAcceso(Usuario usuario) {
			if (this.grupos.contains("*")) {
				return true;
			}
			for (String grupoAux : usuario.getGrupos()) {
				if (this.grupos.contains(grupoAux)) {
					return true;
				}
			}
			return false;
		}

	}

	private static final String ruta = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "ListaAcesoGrupos.ser";
	public static final String grupoDefault = "*";
	ArrayList<ListaAcesoGrupos.RutaGrupo> lista;

	public ArrayList<ListaAcesoGrupos.RutaGrupo> getLista() {
		return lista;
	}

	public static void eliminar(ArrayList<String> listaRutas) {
		ListaAcesoGrupos lag = ListaAcesoGrupos.deserializar();
		ArrayList<Integer> listaIndices = new ArrayList<Integer>();
		for (int i = 0; i < lag.getLista().size(); i++) {
			for (String rutaAux : listaRutas) {
				if (rutaAux.equals(lag.getLista().get(i).getRuta())) {
					listaIndices.add(i);
				}
			}
		}
		int nRemovidos = 0;
		for (Integer integer : listaIndices) {
			lag.getLista().remove(integer - nRemovidos);
			nRemovidos++;
		}
		if (!listaIndices.isEmpty()) {
			ListaAcesoGrupos.serializar(lag);
		}
	}

	public static void eliminar(String ruta, String grupo) {
		ListaAcesoGrupos lag = ListaAcesoGrupos.deserializar();
		for (RutaGrupo it : lag.getLista()) {
			if (it.getRuta().contains(ruta)) {
				if (it.getGrupos().contains(grupo)) {
					it.getGrupos().remove(grupo);
				}
				break;
			}
		}
		ListaAcesoGrupos.serializar(lag);
	}

	public static ArrayList<String> getGruposRutas(String ruta) {
		ListaAcesoGrupos lag = ListaAcesoGrupos.deserializar();
		ArrayList<String> arrayList = new ArrayList<String>();
		for (RutaGrupo rg : lag.getLista()) {
			if (rg.getRuta().equals(ruta)) {
				for (String gru : rg.getGrupos()) {
					arrayList.add(gru);
				}
			}
		}
		return arrayList;
	}

	public static void anyadirRestriccion(String ruta, String grupo) {
		ListaAcesoGrupos lag = ListaAcesoGrupos.deserializar();
		ListaAcesoGrupos.RutaGrupo rutaGrupo = new ListaAcesoGrupos.RutaGrupo(ruta);
		boolean encontrado = false;
		for (int i = 0; i < lag.getLista().size(); i++) {
			ListaAcesoGrupos.RutaGrupo rutaGrupoAux = lag.getLista().get(i);
			if (rutaGrupo.getRuta().equals(rutaGrupoAux.getRuta())) {
				if (rutaGrupo.contieneGrupo(grupo)) {
					encontrado = true;
				}
				break;
			}
		}
		if (!encontrado) {
			// anyadir
			rutaGrupo.getGrupos().add(grupo);
			lag.lista.add(rutaGrupo);
			lag.serializar();
		}
	}

	/**
	 * devuelve -1 si no se encuentra devuelve -2 si se encuentra la ruta pero hay
	 * que modificarla devuelve posicion si se encuentra la ruta con el grupo
	 * 
	 * @param listAux
	 * @return
	 */
	public int seEncuentra(ListaAcesoGrupos.RutaGrupo rutaGrupo) {
		for (int i = 0; i < this.getLista().size(); i++) {
			ListaAcesoGrupos.RutaGrupo rutaGrupoAux = this.getLista().get(i);
			if ((rutaGrupo.getRuta().equals(rutaGrupoAux.getRuta()))) {
				for (int j = 0; j < rutaGrupo.getGrupos().size(); j++) {
					if (rutaGrupoAux.getGrupos().contains(rutaGrupo.getGrupos().get(i))) {
						return i;
					}
				}
				return -2;
			}
		}
		return -1;
	}

	public static Boolean existeFicheroConfig() {
		return new File(ruta).exists();
	}

	public static synchronized boolean serializar(ListaAcesoGrupos sis) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ruta);
			try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(sis);
				oos.close();
			}
			fos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public synchronized boolean serializar() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ruta);
			try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(this);
				oos.close();
			}
			fos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	ListaAcesoGrupos() {
		this.lista = new ArrayList<ListaAcesoGrupos.RutaGrupo>();
	}

	public static Boolean controlarExistencia() {
		if (!existeFicheroConfig()) {
			ListaAcesoGrupos sistema = new ListaAcesoGrupos();
			File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
			if (!ficheroAux.exists()) {
				ficheroAux.mkdir();
			}
			serializar(sistema);
		}
		return true;
	}

	public static ListaAcesoGrupos deserializar() {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				ListaAcesoGrupos sistema = (ListaAcesoGrupos) ois.readObject();
				ois.close();
				fis.close();
				// if (sistema.grupos.isEmpty()) {
				// sistema.grupos.add("New");
				// }
				return sistema;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static void imprimir() {
		JerarquiaRutas je = new JerarquiaRutas();
		System.out.println("-----------------");
		System.out.println("-----------------");
		System.out.println("-----------------");
		for (RouteMatch route : Spark.routes()) {
			System.out.println(route.getMatchUri());
			je.anyadirRuta(route.getMatchUri(), "/");
		}
		System.out.println(je);
		System.out.println("-----------------");
		System.out.println("-----------------");
		System.out.println("-----------------");
		for (int i = 0; i < je.getLista().size(); i++) {
			System.out.println(ListaAcesoGrupos.getItemRuta(je, i));
		}
		System.out.println("-----------------");
		System.out.println("-----------------");
		System.out.println("-----------------");
	}

	public static String getItemRuta(JerarquiaRutas je, int posicion) {
		if (posicion < 0 && posicion > je.getLista().size()) {
			return null;
		}
		JerarquiaRutas.HijoPadre hijoPadre = je.getLista().get(posicion);
		return getItemRuta(je, hijoPadre, "");
	}

	private static String getItemRuta(JerarquiaRutas je, JerarquiaRutas.HijoPadre hijoPadre, String rutaAux) {
		String ruta = "/" + hijoPadre.getHijo() + rutaAux;
		if (hijoPadre.getPadre() == null) {
			return ruta;
		}
		for (JerarquiaRutas.HijoPadre hp : je.getLista()) {
			if (hp.esPadre(hijoPadre)) {
				return getItemRuta(je, hp, ruta);
			}
		}
		return null;
	}

	public static String getItemRuta(JerarquiaRutas je, HijoPadre hijoPadre) {
		try {
			JerarquiaRutas.HijoPadre hp = je.getLista().get(je.getLista().indexOf(hijoPadre));
			return getItemRuta(je, hp, "");
		} catch (Exception e) {
			return null;
		}
	}

	public static void anyadirRestriccion(ArrayList<String> listaRutas, String grupo) {
		// TODO Auto-generated method stub
		ListaAcesoGrupos lag = ListaAcesoGrupos.deserializar();
		boolean serializar = false;
		for (String string : listaRutas) {
			ListaAcesoGrupos.RutaGrupo rg = getRutaGrupo(lag, string);
			if (rg == null) {
				ArrayList<String> gruposAux = new ArrayList<String>();
				gruposAux.add(grupo);
				ListaAcesoGrupos.RutaGrupo rgAux = new RutaGrupo(string, gruposAux);
				lag.getLista().add(rgAux);
				serializar = true;
			} else {
				boolean introducido = rg.anyadirGrupo(grupo);
				if (introducido) {
					serializar = true;
				}
			}
		}
		if (serializar) {
			lag.serializar();
		}
	}

	private static ListaAcesoGrupos.RutaGrupo getRutaGrupo(ListaAcesoGrupos lag, String ruta) {
		for (RutaGrupo it : lag.getLista()) {
			if (it.getRuta().equals(ruta)) {
				return it;
			}
		}
		return null;
	}

}
