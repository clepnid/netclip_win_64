package http;

import java.io.File;
import java.io.IOException;

import ventana.Configuracion;

//clase que permite la opcion de limitar por peso los ficheros que se pueden mostrar a traves de la web.
public class OpcionesModulosHttp {

	public enum FileSizeMedida {
		Kilobyte, Megabyte, Gigabyte, None
	}

	private static long fileSizeNumber;
	private static FileSizeMedida fileSizeMedida;

	
	

	private static void comprobarFileSize() {
		Configuracion config = null;
		config = null;
		try {
			config = Configuracion.deserializar();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileSizeNumber = config.filesizenumber;
		fileSizeMedida = config.filesizemedida;
	}

	public static boolean comprobarSize(File fichero) {
		return getFileSizePositivo() > fichero.length();
	}

	public static boolean esCorrecto(File fichero) {
		return comprobarSize(fichero);
	}

	/*
	 * retorna dependiendo si la cuenta es premium o no el limite positivo de los ficheros de
	 * subida a la web 
	 */
	
	public static long getFileSizePositivo() {
		long fileSize = getFileSize();
		if (fileSize<-1) {
			return fileSize*(-1);
		}
		return fileSize;
	}
	

	/*
	 * retorna dependiendo si la cuenta es premium o no el limite de los ficheros de
	 * subida a la web
	 */

	public static long getFileSize() {
		comprobarFileSize();

		if (fileSizeNumber <= 0) {
			return Long.MAX_VALUE;
		}
		switch (fileSizeMedida) {
		case Kilobyte:
			if (fileSizeNumber > 1024) {
				fileSizeMedida = FileSizeMedida.Megabyte;
				return getFileSize();
			}
			return fileSizeNumber * 1024;
		case Megabyte:
			if (fileSizeNumber > 1024) {
				fileSizeMedida = FileSizeMedida.Gigabyte;
				return getFileSize();
			}
			return fileSizeNumber * 1024 * 1024;
		case Gigabyte:
			if (fileSizeNumber > 1024) {
				return Long.MAX_VALUE;
			}
			return fileSizeNumber * 1024 * 1024 * 1024;
		case None:
			return Long.MAX_VALUE;
		default:
			return -1;
		}
	}

	public static FileSizeMedida getFileSizeMedida() {
		return fileSizeMedida;
	}

	public static void setFileSizeMedida(FileSizeMedida fileSizeMedida) {
		OpcionesModulosHttp.fileSizeMedida = fileSizeMedida;
	}

}
