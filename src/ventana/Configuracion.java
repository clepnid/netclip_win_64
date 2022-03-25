package ventana;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import http.OpcionesModulosHttp;

public class Configuracion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String nombre, carpeta, serial, rutaGuardadoHttp, idioma, licencia;
	private static final String RUTA = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "Configuracion.ser";
	private static final String RUTAHTTPGUARDADO = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "rutas.ser";
	public static final int LENGTHSERIAL = 15;
	public Boolean isAutomatic, recibirEnvios, inicializarRutas;
	public OpcionesModulosHttp.FileSizeMedida filesizemedida;
	public int filesizenumber = 0;

	public Configuracion() {
		this.nombre = "";
		this.carpeta = "";
		this.serial = "";
		this.idioma = "";
		this.rutaGuardadoHttp = "";
		this.isAutomatic = false;
		this.recibirEnvios = false;
		this.inicializarRutas = false;
		this.filesizemedida = OpcionesModulosHttp.FileSizeMedida.None;
		this.filesizenumber = 0;
		this.licencia = "";
	}

	public Configuracion(String nombre, String carpeta, Boolean isAutomatic, String serial) {
		this.nombre = nombre;
		this.carpeta = carpeta;
		this.isAutomatic = isAutomatic;
		this.serial = serial;
	}

	public Configuracion(String nombre, String carpeta, Boolean isAutomatic) {
		this.nombre = nombre;
		this.carpeta = carpeta;
		this.isAutomatic = isAutomatic;
	}

	public static void serializar(Configuracion configuracion) throws IOException {
		FileOutputStream archivo = new FileOutputStream(RUTA);
		ObjectOutputStream salida = new ObjectOutputStream(archivo);
		salida.writeObject(configuracion);
		salida.close();
		archivo.close();
	}

	public static String getRandomString(int i) {

		// bind the length
		byte[] bytearray;
		bytearray = new byte[256];
		String mystring;
		StringBuffer thebuffer;
		String theAlphaNumericS;

		new Random().nextBytes(bytearray);

		mystring = new String(bytearray, Charset.forName("UTF-8"));

		thebuffer = new StringBuffer();

		// remove all spacial char
		theAlphaNumericS = mystring.replaceAll("[^A-Z0-9]", "");

		// random selection
		for (int m = 0; m < theAlphaNumericS.length(); m++) {

			if (Character.isLetter(theAlphaNumericS.charAt(m)) && (i > 0)
					|| Character.isDigit(theAlphaNumericS.charAt(m)) && (i > 0)) {

				thebuffer.append(theAlphaNumericS.charAt(m));
				i--;
			}
		}

		// the resulting string
		return thebuffer.toString();
	}

	public static Boolean existeFicheroConfig() {
		return new File(RUTA).exists();
	}

	public static Boolean controlarExistencia() {
		File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
		if (!ficheroAux.exists()) {
			ficheroAux.mkdir();
		}
		if (!existeFicheroConfig()) {
			Configuracion configAux = new Configuracion();
			String string = System.getProperty("user.home");
			configAux.carpeta = string + File.separator + "Clepnid";
			string = getNombreRed();
			if (!(string == null)) {
				configAux.nombre = string;
			} else {
				configAux.nombre = "User";
			}
			configAux.rutaGuardadoHttp = RUTAHTTPGUARDADO;
			configAux.isAutomatic = false;
			configAux.recibirEnvios = false;
			configAux.inicializarRutas = false;
			configAux.idioma = "Ingles";
			configAux.filesizemedida = OpcionesModulosHttp.FileSizeMedida.None;
			configAux.filesizenumber = 0;
			configAux.serial = getRandomString(LENGTHSERIAL);
			configAux.licencia = "";
			try {
				serializar(configAux);
			} catch (IOException e) {
				return false;
			}

		}
		try {
			Configuracion configDeserializada = deserializar();
			if (configDeserializada.idioma==null) {
				configDeserializada.idioma = "Ingles";
			}
			if (configDeserializada.nombre==null) {
				String nombreRed = getNombreRed();
				if (!(nombreRed == null)) {
					configDeserializada.nombre = nombreRed;
				} else {
					configDeserializada.nombre = "User";
				}
			}
			
			if (configDeserializada.inicializarRutas==null) {
				configDeserializada.inicializarRutas = false;
			}
			if (configDeserializada.recibirEnvios==null) {
				configDeserializada.recibirEnvios = false;
			}
			if (configDeserializada.isAutomatic==null) {
				configDeserializada.isAutomatic = false;
			}
			if (configDeserializada.rutaGuardadoHttp==null) {
				configDeserializada.rutaGuardadoHttp = RUTAHTTPGUARDADO;
			}
			if (configDeserializada.filesizemedida==null) {
				configDeserializada.filesizemedida = OpcionesModulosHttp.FileSizeMedida.None;
			}
			if (configDeserializada.serial==null) {
				configDeserializada.serial = getRandomString(LENGTHSERIAL);
			}
			if (configDeserializada.licencia==null) {
				configDeserializada.licencia = "";
			}
			try {
				serializar(configDeserializada);
			} catch (IOException e) {
				return false;
			}
			
		} catch (ClassNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static String getNombreRed() {
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();

			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();

				List<InterfaceAddress> e2 = ni.getInterfaceAddresses();

				for (int i = 0; i < e2.size(); i++) {
					InetAddress ip = e2.get(i).getAddress();
					String ip_aux = ip.getHostAddress().toString().split("\\.")[0];
					if (ip instanceof Inet4Address
							&& (ip_aux.equals("192") || ip_aux.equals("172") || ip_aux.equals("10"))) {
						return ip.getHostName().toString();
					}
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static Configuracion deserializar() throws IOException, ClassNotFoundException {
		Configuracion config = new Configuracion();
		FileInputStream archivo = new FileInputStream(RUTA);
		ObjectInputStream entrada = new ObjectInputStream(archivo);
		config = (Configuracion) entrada.readObject();
		entrada.close();
		archivo.close();
		return config;
	}
}
