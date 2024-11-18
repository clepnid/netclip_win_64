package portapapeles;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import ventana.Ventana;

/**
 * Clase para manejar ficheros {@link List} de {@link File}
 * 
 * @author: Pavon
 * @version: 10/05/2020
 * @since 1.0
 */

public class Ficheros {

	public List<File> ficheros;
	public List<Long> tamanyos;
	public long tamanyoTodos;

	/**
	 * Constructor para definir los ficheros a manejar en la clase.
	 * 
	 * @param rutas lista con las rutas a manejar.
	 */

	public Ficheros(List<?> rutas) {
		tamanyoTodos = (long) 0;
		tamanyos = new ArrayList<Long>();
		ficheros = new ArrayList<File>();
		for (Object object : rutas) {
			if (object.getClass().equals(File.class)) {
				File ficheroAux = (File) object;
				try {
					recorrerFicheros(ficheroAux);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ficheros.add(new File(ficheroAux.toString()));
			}

		}

	}

	public void recorrerFicheros(File outFile) throws Exception {
		String relativePath = outFile.getAbsoluteFile().getParentFile().getAbsolutePath();
		outFile = outFile.getAbsoluteFile();
		tamanyos.add((long) 0);
		if (outFile.isDirectory()) {
			recorrerCarpeta(outFile, relativePath, tamanyos.size() - 1);
		} else {
			anaydirTamanyoFichero(outFile, tamanyos.size() - 1);
		}
	}

	/**
	 * Envia los ficheros contenidos en la carpeta.
	 * 
	 * @param zipOpStream  {@link ZipOutputStream} buffer para enviar fichero.
	 * @param folder       {@link File} carpeta.
	 * @param relativePath {@link File} ruta relativa.
	 */

	public void recorrerCarpeta(File folder, String relativePath, int index) throws Exception {
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				recorrerCarpeta(file, relativePath, index);
			} else {
				anaydirTamanyoFichero(file, index);
			}
		}
	}

	public void anaydirTamanyoFichero(File file, int index) throws Exception {
		tamanyoTodos += file.length();
		tamanyos.set(index, (tamanyos.get(index) + file.length()));
	}

	public static String bitsRepresentacion(float bits, String tipo) {
		float bits_aux = (float) 0;
		String tipo_Aux = tipo;
		switch (tipo) {
		case "bits":
			bits_aux = bits / 8;
			tipo = "bytes";
			break;
		case "bytes":
			bits_aux = bits / 1024;
			tipo = "KB";
			break;
		case "KB":
			bits_aux = bits / 1024;
			tipo = "MB";
			break;
		case "MB":
			bits_aux = bits / 1024;
			tipo = "GB";
			break;

		default:
			break;
		}
		if (1 < bits_aux && !tipo.equals("GB")) {
			return bitsRepresentacion(bits_aux, tipo);
		} else {
			DecimalFormat formato = new DecimalFormat("#.##");
			return formato.format(bits) + " " + tipo_Aux;
		}
	}

	/**
	 * devuelve si es carpeta un fichero.
	 * 
	 * @param fichero objeto manejador de fichero.
	 * @return <code>true</code> si es carpeta, <code>false</code> si no es carpeta.
	 */

	public boolean esCarpeta(File fichero) {
		return fichero.isDirectory();
	}

	/**
	 * devuelve si es fichero.
	 * 
	 * @param fichero objeto manejador de fichero.
	 * @return <code>true</code> si es fichero, <code>false</code> si no es fichero.
	 */

	public boolean esFichero(File fichero) {
		return fichero.isFile();
	}

	/**
	 * devuelve extension de un fichero.
	 * 
	 * @param nombre {@link String} nombre del fichero a obtener extension.
	 * @return {@link String} extension de fichero.
	 */

	public static String getExtensionFichero(String nombre) {
		if (nombre.lastIndexOf(".") != -1 && nombre.lastIndexOf(".") != 0)
			return nombre.substring(nombre.lastIndexOf(".") + 1);
		else
			return "";
	}

	/**
	 * Método que reconoce cerca de 900 extensiones, devuelve un {@link String} con
	 * el tipo de fichero referente a la extension del mismo.
	 * 
	 * @param ruta ruta de un fichero para obtener extension.
	 * @return {@link String} con el tipo de extension de un fichero.
	 */
	public static Boolean esTipo(String ruta, String extension) {
		if (getExtensionFichero(ruta).toUpperCase().equals(extension)) {
			return true;
		} else {
			return false;
		}
	}

	public static String tipoFichero(String ruta) {
		String extension = null;
		if (!ruta.equals("carpeta")) {
			extension = getExtensionFichero(ruta).toUpperCase();
		} else {
			extension = ruta.toUpperCase();
		}

		String tipo = "";
		if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.CARPETA, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_carpeta");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.TEXTO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_fichero_texto");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.CODIGO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_codigo");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.EJECUTABLE, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_ejecutable");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.DATOSCIENTIFICOS, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_datos_cientificos");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.VIDEO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_tipo_fichero_video");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.MAQUINAVIRTUAL, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_maquina_virtual");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.DISCO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_disco");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.HOJACALCULO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_hoja_calculo");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.AUDIO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_audio");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.DOCUMENTO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_documento");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.SEGURIDAD, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_seguridad");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.VECTOR, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_vector");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.IMAGEN, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_fichero_imagen");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.BASEDATOS, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_base_datos");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.ENLACE, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_enlace");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.COMPRIMIDO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_comprimido");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.JUEGO, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_juego");
		} else if (ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.PRESENTACION, extension)) {
			tipo = Ventana.idioma.getProperty("tipo_fichero_presentacion");
		} else {
			tipo = Ventana.idioma.getProperty("tipo_fichero_desconocido");
		}

		return tipo;

	}

	/**
	 * devuelve la ruta de una imagen relaciondo con la extensión
	 * 
	 * @param tipo {@link String} que contiene la extension de un fichero.
	 * @return {@link String} con la ruta de una imagen relacionada con la extension
	 *         de un fichero.
	 */

	public static String rutaImagen(String tipo) {
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_carpeta"))) {
			return "./src/imagenes/carpeta.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_texto"))) {
			return "./src/imagenes/fichero_texto.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_codigo"))) {
			return "./src/imagenes/codigo.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_ejecutable"))) {
			return "./src/imagenes/ejecutable.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_datos_cientificos"))) {
			return "./src/imagenes/datos_cientificos.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_tipo_fichero_video"))) {
			return "./src/imagenes/video.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_maquina_virtual"))) {
			return "./src/imagenes/maquina_virtual.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_disco"))) {
			return "./src/imagenes/disco.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_hoja_calculo"))) {
			return "./src/imagenes/hoja_calculo.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_audio"))) {
			return "./src/imagenes/audio.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_documento"))) {
			return "./src/imagenes/documento.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_seguridad"))) {
			return "./src/imagenes/seguridad.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_vector"))) {
			return "./src/imagenes/vector.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_fichero_imagen"))) {
			return "./src/imagenes/fichero_imagen.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_base_datos"))) {
			return "./src/imagenes/base_datos.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_enlace"))) {
			return "./src/imagenes/enlace.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_comprimido"))) {
			return "./src/imagenes/comprimido.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_juego"))) {
			return "./src/imagenes/juego.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_presentacion"))) {
			return "./src/imagenes/presentacion.gif";
		}
		if (tipo.equals(Ventana.idioma.get("tipo_fichero_desconocido"))) {
			return "./src/imagenes/desconocido.gif";
		}

		return "";
	}

	/**
	 * devuelve un objeto {@link String} para mostrar con pantalla la informacion de
	 * esta clase {@link Ficheros}.
	 * 
	 * @return {@link String} con la información de la clase {@link Ficheros}.
	 */

	@Override
	public String toString() {
		String texto = "";
		for (File fichero : ficheros) {
			if (fichero.isFile()) {
				texto = texto + "fichero\n";
				texto = texto + "\tNombre: " + fichero.getName() + "\n";
				texto = texto + "\tExtension: " + getExtensionFichero(fichero.getPath()) + "\n";
				texto = texto + "\tTipo: " + tipoFichero(fichero.getPath()) + "\n";
				texto = texto + "\tEs Fichero: " + esFichero(fichero) + "\n";
				texto = texto + "\tEs Carpeta: " + esCarpeta(fichero) + "\n\n";
			}
		}
		return texto;
	}

	/**
	 * devuelve una lista {@link ArrayList} conteniendo objetos {@link String} con
	 * el contendio de esta clase {@link Ficheros} que construye una clase
	 * {@link Contenido} de tipo Ficheros, para mostrar en la clase
	 * {@link ventana.Ventana}.
	 * 
	 * @return {@link ArrayList} que contiene {@link String} para mostrar por la
	 *         clase {@link ventana.Ventana}.
	 */

	public ArrayList<String[]> ficherosToContenido() {
		ArrayList<String[]> listaFicherosToContenido = new ArrayList<String[]>();
		for (File fichero : ficheros) {
			// se diferencian los ficheros de los directorios
			if (fichero.isFile()) {
				String[] ficheroToContenido = { fichero.getName(),
						Ventana.idioma.getProperty("fichero_peso") + ":  "
								+ bitsRepresentacion(fichero.length(), "bytes"),
						tipoFichero(fichero.getAbsolutePath()), rutaImagen(tipoFichero(fichero.getPath())),
						fichero.getAbsolutePath() };
				listaFicherosToContenido.add(ficheroToContenido);
			} else if (fichero.isDirectory()) {
				String[] ficheroToContenido = { fichero.getName(), "Carpeta", "", rutaImagen(tipoFichero("carpeta")),
						fichero.getAbsolutePath() };
				listaFicherosToContenido.add(ficheroToContenido);
			}
		}
		return listaFicherosToContenido;
	}

}
