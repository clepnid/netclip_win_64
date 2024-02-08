package barraNavegacion;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BarraNavegacion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Item implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String nombre, ruta, rutaImagen, grupo;

		public String getNombre() {
			return nombre;
		}

		public String getGrupo() {
			return grupo;
		}

		public String getRuta() {
			return ruta;
		}

		public String getRutaImagen() {
			return rutaImagen;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public void setRuta(String ruta) {
			this.ruta = ruta;
		}

		public void setRutaImagen(String rutaImagen) {
			this.rutaImagen = rutaImagen;
		}

		public void setGrupo(String grupo) {
			this.grupo = grupo;
		}

		public Item(String nombre, String ruta, String rutaImagen, String grupo) {
			this.nombre = nombre;
			this.ruta = ruta;
			this.rutaImagen = rutaImagen;
			this.grupo = grupo;
		}

		@Override
		public String toString() {
			return "{\n" + "\t\"title\": \"" + this.nombre + "\",\r\n" + "\t\"group\": \"" + this.grupo + "\",\r\n"
					+ "\t\"image\": \"" + this.rutaImagen + "\",\r\n" + "\t\"goTo\": \"" + this.ruta + "\"\r\n" + "}";
		}

		@Override
		public boolean equals(Object obj) {
			try {
				Item item = (Item) obj;
				return this.nombre.equals(item.nombre) && this.ruta.equals(item.ruta)
						&& this.rutaImagen.equals(item.rutaImagen);
			} catch (Exception e) {
				return false;
			}
		}

	}

	private final static String RUTALISTA = "./src/html/navBar/lista.json";
	private final static String HOMEICONTITULO = "Menú Principal";
	private final static String HOMEICONURL = "/menu";
	private final static String HOMEICONRUTA = "/navBar/home_icon.png";
	private final static String CERRARSESIONCONTITULO = "Cerrar Sesión";
	private final static String CERRARSESIONICONURL = "/login/clear";
	private final static String CERRARSESIONICONRUTA = "/navBar/user.png";
	private final static String LOGODEFAULTRUTA = "/navBar/clipboard.gif";

	public static boolean EXISTE_FICHERO = new File(RUTALISTA).exists();

	private ArrayList<BarraNavegacion.Item> lista;
	private String rutaImagenLogo;

	public ArrayList<BarraNavegacion.Item> getLista() {
		return lista;
	}

	public static void setRutaImagenLogoEstatica(String ruta) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		bn.setRutaImagenLogo(ruta);
		BarraNavegacion.escribirFichero(bn);
	}

	public static void anyadirItem(Item i) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		bn.anyadir(i);
		BarraNavegacion.escribirFichero(bn);
	}

	public static void eliminarItem(Item i) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		bn.eliminar(i);
		BarraNavegacion.escribirFichero(bn);
	}

	public static void modificarItem(int posicion, Item item) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		bn.getLista().get(posicion).setNombre(item.getNombre());
		bn.getLista().get(posicion).setRuta(item.getRuta());
		bn.getLista().get(posicion).setRutaImagen(item.getRutaImagen());
		bn.getLista().get(posicion).setGrupo(item.getGrupo());
		BarraNavegacion.escribirFichero(bn);
	}

	public static void eliminarItem(int num) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		Item it = bn.get(num);
		if (it != null) {
			bn.eliminar(it);
			BarraNavegacion.escribirFichero(bn);
		}
	}

	public static void ModificiarRutaLogo(String rutaLogo) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		bn.setRutaImagenLogo(rutaLogo);
		BarraNavegacion.escribirFichero(bn);
	}

	public static Item getItem(int num) {
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		Item it = bn.get(num);
		return it;
	}

	public BarraNavegacion() {
		this.lista = new ArrayList<BarraNavegacion.Item>();
	}

	public void anyadir(BarraNavegacion.Item item) {
		if (!lista.contains(item)) {
			lista.add(item);
		}
	}

	public void eliminar(BarraNavegacion.Item item) {
		if (lista.contains(item)) {
			lista.remove(item);
		}
	}

	public Item get(int num) {
		if (num >= 0 && num < lista.size()) {
			return lista.get(num);
		}
		return null;
	}

	public String getRutaImagenLogo() {
		return rutaImagenLogo;
	}

	public void setRutaImagenLogo(String rutaImagenLogo) {
		this.rutaImagenLogo = rutaImagenLogo;
	}

	@Override
	public String toString() {
		String s = "[{\"logoImagen\":\"" + this.rutaImagenLogo + "\",";
		s += "\n\"content\": [\n";
		for (Item item : lista) {
			s += item.toString() + ",";
		}
		if (!lista.isEmpty()) {
			s = s.substring(0, s.length() - 1);
		}
		s += "]}]";

		return s;
	}

	private ArrayList<Item> getItemsDeGrupo(ArrayList<String> g) {
		if (g != null) {
			ArrayList<Item> i = new ArrayList<BarraNavegacion.Item>();
			for (Item item : lista) {
				for (String grupo : g) {
					if (item.getGrupo().equals(grupo) || item.getGrupo().replace(" ", "").equals("")
							|| item.getGrupo().isEmpty()) {
						i.add(item);
						break;
					}
				}
			}
			return i;
		}
		return null;
	}

	// metodo usado para /getNavBarlista.json

	public String getJsonNavbarGrupo(ArrayList<String> g) {
		String s = "[{\"logoImagen\":\"" + this.rutaImagenLogo + "\",";
		s += "\n\"content\": [\n";
		ArrayList<Item> i = getItemsDeGrupo(g);
		if (i!=null) {
			for (Item item : i) {
				s += item.toString() + ",";
			}
			if (!i.isEmpty()) {
				s = s.substring(0, s.length() - 1);
			}
		}
		s += "]}]";

		return s;
	}

	public static synchronized void escribirFichero(BarraNavegacion nav) {
		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter(BarraNavegacion.RUTALISTA);
			pw = new PrintWriter(fichero);
			pw.println(nav);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != fichero)
					fichero.close();
				if (null != pw)
					pw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static synchronized BarraNavegacion leerFichero() {
		BarraNavegacion nav = new BarraNavegacion();
		FileReader fr = null;
		if (EXISTE_FICHERO) {
			try {
				fr = new FileReader(BarraNavegacion.RUTALISTA);
				Object ob = new JSONParser().parse(fr);

				JSONArray webList = (JSONArray) ob;

				for (Object object : webList) {
					JSONObject objeto = (JSONObject) object;
					nav.setRutaImagenLogo((String) objeto.get("logoImagen"));
					for (Object object2 : (JSONArray) objeto.get("content")) {
						JSONObject objeto2 = (JSONObject) object2;
						String ruta = (String) objeto2.get("goTo");
						String rutaImagen = (String) objeto2.get("image");
						String nombre = (String) objeto2.get("title");
						String grupo = (String) objeto2.get("group");
						nav.anyadir(new Item(nombre, ruta, rutaImagen, grupo));
					}
				}
				if (fr != null) {
					fr.close();
				}

			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			nav.setRutaImagenLogo(LOGODEFAULTRUTA);
			nav.anyadir(new Item(HOMEICONTITULO, HOMEICONURL, HOMEICONRUTA, "New"));
			nav.anyadir(new Item(CERRARSESIONCONTITULO, CERRARSESIONICONURL, CERRARSESIONICONRUTA, "New"));
		}

		return nav;
	}

}
