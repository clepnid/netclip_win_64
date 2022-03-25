package http;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ventana.Configuracion;

public class OpcionesModulosHttp {
	private static Calendar c = Calendar.getInstance();
	public static int mes = Integer.valueOf(Integer.toString(c.get(Calendar.MONTH))) + 1,
			anyo = Integer.valueOf(Integer.toString(c.get(Calendar.YEAR)).substring(1)), tamañoCodigo = 40;
	public static String[] letters = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K" };

	public enum Tipo {
		Premium, noPremium
	}

	public enum FileSizeMedida {
		Kilobyte, Megabyte, Gigabyte, None
	}

	public static Tipo tipoPremium = setTipoLicencia();
	private static long fileSizeNumber;
	private static FileSizeMedida fileSizeMedida;

	public static boolean esTipoPremium() {
		return tipoPremium.equals(Tipo.Premium);
	}
	
	public static Tipo setTipoLicencia() {
		if (validar()) {
			return Tipo.Premium;
		}
		return Tipo.noPremium;
	}
	
	public static boolean validar() {
		String codigo="";
		try {
			codigo = Configuracion.deserializar().licencia;
		} catch (ClassNotFoundException | IOException e) {
			System.out.print("");
		}
		if (codigo != null && codigo.length()!=tamañoCodigo) {
			return false;
		}
		int contadorPar = 0, contadorImpar = 0;
		for (int i = 1; i < tamañoCodigo; i = i + 2) {
			contadorImpar += getNumeroLetra(codigo.charAt(i));
		}
		for (int i = 0; i < tamañoCodigo; i = i + 2) {
			contadorPar += getNumeroLetra(codigo.charAt(i));
		}
		return ((contadorPar + contadorImpar) == (mes+(anyo*12))) && getNumeroLetra(codigo.charAt(mes-1)) == mes;
	}
	
	public static boolean validar(String codigo) {
		if (codigo.length()!=tamañoCodigo) {
			return false;
		}
		int contadorPar = 0, contadorImpar = 0;
		for (int i = 1; i < tamañoCodigo; i = i + 2) {
			contadorImpar += getNumeroLetra(codigo.charAt(i));
		}
		for (int i = 0; i < tamañoCodigo; i = i + 2) {
			contadorPar += getNumeroLetra(codigo.charAt(i));
		}
		return ((contadorPar + contadorImpar) == (mes+(anyo*12))) && getNumeroLetra(codigo.charAt(mes-1)) == mes;
	}
	
	private static int getNumeroLetra(char caracter) {
		for (int i = 0; i < letters.length; i++) {
			if (letters[i].equals(String.valueOf(caracter))) {
				return i;
			}
		}
		return -1;
	}

	public static String getHtml() {
		if (tipoPremium.equals(Tipo.noPremium)) {
			return "<p style=\"color: #9932cc;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Premium version <a style=\"color: #9932cc;\" href=\"https://www.buymeacoffee.com/clepnid\" rel=\"nofollow\">Link</a></p>";
		} else {
			return "";
		}
	}

	public static String getHtml(String html) {
		if (tipoPremium.equals(Tipo.noPremium)) {
			return html.replace("<body>",
					"<body><p style=\"color: #9932cc;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Premium version <a style=\"color: #9932cc;\" href=\"https://www.buymeacoffee.com/clepnid\" rel=\"nofollow\">Link</a></p>");
		} else {
			return html;
		}
	}

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
		if (!esTipoPremium()) {
			if (fileSizeNumber <= 0) {
				return 2 * 1024 * 1024 * 1024;
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
				if (fileSizeNumber > 2) {
					return 2 * 1024 * 1024 * 1024;
				}
				return fileSizeNumber * 1024 * 1024 * 1024;
			case None:
				return 2 * 1024 * 1024 * 1024;
			default:
				return -1;
			}
		}

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
