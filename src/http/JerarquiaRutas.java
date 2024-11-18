package http;

import java.io.Serializable;
import java.util.ArrayList;

import spark.Spark;
import spark.routematch.RouteMatch;

public class JerarquiaRutas implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class HijoPadre implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String hijo, padre;
		private int nJerarquia;

		public int getnJerarquia() {
			return nJerarquia;
		}

		public void setnJerarquia(int nJerarquia) {
			this.nJerarquia = nJerarquia;
		}

		public String getHijo() {
			return hijo;
		}

		public String getPadre() {
			return padre;
		}

		public boolean noTienePadre() {
			return padre == null;
		}

		HijoPadre(String hijo, String padre) {
			this.nJerarquia = 0;
			this.hijo = hijo;
			this.padre = padre;
		}

		HijoPadre(String hijo, String padre, int n) {
			this.nJerarquia = n;
			this.hijo = hijo;
			this.padre = padre;
		}

		@Override
		public boolean equals(Object obj) {
			try {
				HijoPadre h = (HijoPadre) obj;
				if (h.getPadre() == null && this.getPadre() == null) {
					return h.getHijo().equals(this.hijo);
				}
				if ((h.getPadre() == null && this.getPadre() != null)
						|| (h.getPadre() != null && this.getPadre() == null)) {
					return false;
				}
				return h.getHijo().equals(this.hijo) && h.getPadre().equals(this.padre)
						&& nJerarquia == h.getnJerarquia();
			} catch (Exception e) {
				return false;
			}
		}

		public boolean esHijo(HijoPadre hp) {
			if (this.getPadre() == null) {
				return false;
			}
			return hp.getHijo().equals(this.getPadre());
		}

		public boolean esPadre(HijoPadre hp) {
			if (hp.getPadre() == null) {
				return false;
			}
			return hp.getPadre().equals(this.getHijo());
		}
	}

	private int numeroRutasIniciales;

	private ArrayList<JerarquiaRutas.HijoPadre> lista;

	public ArrayList<JerarquiaRutas.HijoPadre> getLista() {
		return lista;
	}

	public JerarquiaRutas() {
		lista = new ArrayList<JerarquiaRutas.HijoPadre>();
		numeroRutasIniciales = 0;
	}

	public void setNumeroRutasIniciales() {
		int n = 0;
		for (HijoPadre hijoPadre : lista) {
			if (hijoPadre.getPadre()==null) {
				n++;
			}
		}
		this.numeroRutasIniciales = n;
	}
	
	public int getNumeroRutasIniciales() {
		return this.numeroRutasIniciales;
	}

	public void anyadirRuta(String ruta, String spliter) {
		String[] splitRuta = ruta.split(spliter);
		boolean leidoPrimerAcceso = false;
		for (int i = 0; i < splitRuta.length; i++) {
			if (splitRuta[i] != "") {
				if (!leidoPrimerAcceso) {
					HijoPadre h = new HijoPadre(splitRuta[i], null);
					if (!lista.contains(h)) {
						lista.add(h);
					}
					leidoPrimerAcceso = true;
				} else {
					HijoPadre h = new HijoPadre(splitRuta[i], splitRuta[i - 1]);
					if (!lista.contains(h)) {
						lista.add(h);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return leerJerarquia();
	}
	
	/**
	 * metodo para recogerLista y pintarla.
	 * @return
	 */
	
	public static JerarquiaRutas prepararListaJerarquia(){
		JerarquiaRutas je = getSparkRutas();
		return je.recorrerJerarquia();
	}
	
	public static JerarquiaRutas getSparkRutas() {
		JerarquiaRutas je = new JerarquiaRutas();
		for (RouteMatch route : Spark.routes()) {
			je.anyadirRuta(route.getMatchUri(), "/");
		}
		je.setNumeroRutasIniciales();
		return je;
	}

	private JerarquiaRutas recorrerJerarquia() {
		for (HijoPadre hijoPadre : this.lista) {
			if (hijoPadre.noTienePadre()) {
				hijoPadre.setnJerarquia(0);
			}else {
				recorrerJerarquia(hijoPadre, 0);
			}
		}
		return this;
	}

	private void recorrerJerarquia(HijoPadre hp, int nivel) {
		int n = nivel;
		n++;
		hp.setnJerarquia(n);
		for (HijoPadre hijoPadre : this.lista) {
			if (hijoPadre.getPadre() != null) {
				if (hijoPadre.getPadre().equals(hp.getHijo())) {
					recorrerJerarquia(hp, n);
				}
			}
		}
	}

	private String leerJerarquia() {
		String texto = "";
		for (HijoPadre hijoPadre : this.lista) {
			if (hijoPadre.noTienePadre()) {
				texto += leerJerarquia(hijoPadre.getHijo(), 0);
			}
		}
		return texto;
	}

	private String leerJerarquia(String nombre, int nivel) {
		int n = nivel;
		n++;
		String texto = "";
		for (int i = 0; i < nivel; i++) {
			texto += "\t";
		}
		texto += nombre + "\n";
		for (HijoPadre hijoPadre : this.lista) {
			if (hijoPadre.getPadre() != null) {
				if (hijoPadre.getPadre().equals(nombre)) {
					texto += leerJerarquia(hijoPadre.getHijo(), n);
				}
			}
		}
		return texto;
	}

}
