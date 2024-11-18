package portapapeles;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import http.JsonModulosFicheros;
import http.ConfiguracionJson;
import ventana.Ventana;

/**
 * Clase que contiene los datos para mostrar por {@link Ventana}, implementa la
 * clase {@link Serializable}, se envia haciendo uso de la clase {@link Socket}
 * por la clase {@link red.compartirFicheros.Servidor} y se recibe por la clase
 * {@link red.compartirFicheros.Cliente}
 * 
 * @author: Pavon
 * @version: 10/05/2020
 * @since 1.0
 */

public class Contenido implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Tipo {
		Texto, Ficheros, Html
	}

	public Tipo tipo;
	public String texto = null;
	public ArrayList<String[]> listaFicheros = null;
	public int id = 0;
	private ArrayList<ArrayList<ConfiguracionJson>> listaModulos;

	/**
	 * Constructor para definir las variables:
	 * <ul>
	 * <li>tipo: categoriza el contenido de esta clase</li>
	 * <li>id: int que hace uso del metodo random() de {@link Math}</li>
	 * </ul>
	 */

	Contenido(Tipo tipo) {
		this.tipo = tipo;
		this.id = (int) ((99999999 * Math.random() + 1));
	}
	
	/**
	 * Constructor para definir las variables:
	 * <ul>
	 * <li>tipo: categoriza el contenido de esta clase</li>
	 * <li>texto: {@link String} texto contenido</li>
	 * <li>id: int que hace uso del metodo random() de {@link Math}</li>
	 * </ul>
	 * 
	 * @param texto {@link String} a contener.
	 */

	public Contenido(String texto) {
		if (Html.hasHTMLTags(texto)) {
			this.tipo = Tipo.Html;
		} else {
			this.tipo = Tipo.Texto;
		}
		this.texto = texto;
		this.id = (int) ((99999999 * Math.random() + 1));
	}

	/**
	 * Constructor para definir las variables:
	 * <ul>
	 * <li>tipo: categoriza el contenido de esta clase</li>
	 * <li>listaFicheros: {@link ArrayList} de arrays de {@link String}
	 * ficheros</li>
	 * <li>id: int que hace uso del metodo random() de {@link Math}</li>
	 * </ul>
	 * 
	 * @param ficheros {@link Ficheros} a contener.
	 */

	public Contenido(Ficheros ficheros) {
		this.tipo = Tipo.Ficheros;
		this.listaFicheros = ficheros.ficherosToContenido();
		this.id = (int) ((99999999 * Math.random() + 1));
		this.listaModulos = new ArrayList<ArrayList<ConfiguracionJson>>();
		for (String[] strings : listaFicheros) {
			this.listaModulos.add(JsonModulosFicheros.obtenerConfiguraciones(Ficheros.getExtensionFichero(strings[0])));
		}
	}

	/**
	 * Compara el id propio y el de la clase pasada por parametros
	 * 
	 * @param contenido {@link Contenido} a compararar.
	 * @return <code>true</code> tienen el mismo id. <code>false</code> no tienen el
	 *         mismo id.
	 */

	public boolean equals(Contenido contenido) {
		if (contenido == null) {
			return false;
		}
		if (this.id == contenido.id) {
			return true;
		}
		return false;
	}

	public ArrayList<ArrayList<ConfiguracionJson>> getListaModulos() {
		return listaModulos;
	}

	public void setListaModulos(ArrayList<ArrayList<ConfiguracionJson>> listaModulos) {
		this.listaModulos = listaModulos;
	}

}
