package torrentLibreria.ficherosTorrent;

import java.io.File;
import java.util.ArrayList;

public class FicheroTorrent {
	// private instance, so that it can be
	// accessed by only by getInstance() method
	private static FicheroTorrent instance;
	private ArrayList<String> listaFicherosEnSistema;
	private ArrayList<String> listaFicherosEnUrl;
	private String rutaPadre;
	
	public void close() {
		instance = null;
		listaFicherosEnSistema = null;
		listaFicherosEnUrl = null;
		rutaPadre = null;
	}
	
	public File getFichero(String nombre) { 
		int n = -1;
		for (String string : listaFicherosEnUrl) {
			n++;
			System.out.println("FicheroTorrent: COMPARAR: "+nombre+" ----- "+string);
			if (nombre.equals("/"+string)) {
				File f = new File(this.listaFicherosEnSistema.get(n));
				System.out.println("FicheroTorrent: EXISTE: "+f.exists());
				return f;
			}
		}
		return null;
	}

	private FicheroTorrent()
	  {
	    this.setRutaPadre("");
	    this.setListaFicherosEnSistema(new ArrayList<String>());
	    this.setListaFicherosEnUrl(new ArrayList<String>());
	  }

	// method to return instance of class
	public static FicheroTorrent getInstance() {
		if (instance == null) {
			// if instance is null, initialize
			instance = new FicheroTorrent();
		}
		return instance;
	}

	public String getRutaPadre() {
		return rutaPadre;
	}

	public void setRutaPadre(String rutaPadre) {
		if (!rutaPadre.equals(this.rutaPadre)) {
			this.rutaPadre = rutaPadre;
		}
	}

	public ArrayList<String> getListaFicherosEnSistema() {
		return listaFicherosEnSistema;
	}
	
	public boolean anydirRuta(String s) {
		String sAux = this.rutaPadre+"/"+s;
		if (this.listaFicherosEnSistema.contains(sAux)) {
			return false;
		}
		this.listaFicherosEnSistema.add(sAux);
		this.listaFicherosEnUrl.add(encodeURIcomponent(s));
		System.out.println("FicheroTorrent: METIDO EN URL: "+s+" ------- "+encodeURIcomponent(s));
		return true;
	}

	private void setListaFicherosEnSistema(ArrayList<String> listaNombres) {
		this.listaFicherosEnSistema = listaNombres;
	}

	/* Convierte un string a algo que se puede insertar en una url */
	public static String encodeURIcomponent(String s) {
		Double d = new Double(0.0);
		char[] caracteres = s.toCharArray();
		for (int i = 1; i < caracteres.length; i=i+2) {
			d = d + ((int)caracteres[i])*((int)caracteres[i-1]);
		}
		return String.valueOf(d)+".mp4";
	}

	public ArrayList<String> getListaFicherosEnUrl() {
		return listaFicherosEnUrl;
	}

	private void setListaFicherosEnUrl(ArrayList<String> listaFicherosEnUrl) {
		this.listaFicherosEnUrl = listaFicherosEnUrl;
	}
}
