package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import http.Http.Tipo;
import portapapeles.Ficheros;
import ventana.Configuracion;

public class GuardadoRutas implements Serializable {

	/**
	 *	Se utiliza para guardar los ficheros que tiene compartido el servidor
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> nombres;
	private ArrayList<String> rutasSistema;
	private ArrayList<Tipo> tipos;
	private static String ruta;

	public ArrayList<String> getRutasHttp() {
		return nombres;
	}

	public void setRutasHttp(ArrayList<String> rutasHttp) {
		this.nombres = rutasHttp;
	}

	public ArrayList<String> getRutasSistema() {
		return rutasSistema;
	}

	public void setRutasSistema(ArrayList<String> rutasSistema) {
		this.rutasSistema = rutasSistema;
	}

	public ArrayList<Tipo> getTipos() {
		return tipos;
	}

	public void setTipos(ArrayList<Tipo> tipos) {
		this.tipos = tipos;
	}

	public GuardadoRutas(Http http) {
		this.tipos = http.getTipos();
		this.rutasSistema = http.getUrlsSistema();
		this.nombres = http.getUrlsParciales();
		try {
			ruta = Configuracion.deserializar().rutaGuardadoHttp;
		} catch (ClassNotFoundException e) {
			ruta = "";
		} catch (IOException e) {
			ruta = "";
		}
	}

	public GuardadoRutas() {
		this.tipos = new ArrayList<Http.Tipo>();
		this.rutasSistema = new ArrayList<String>();
		this.nombres = new ArrayList<String>();
		try {
			ruta = Configuracion.deserializar().rutaGuardadoHttp;
		} catch (ClassNotFoundException e) {
			ruta = "";
		} catch (IOException e) {
			ruta = "";
		}
	}

	public void acoplar(Http http) {
		String extension, nombre, rutaSistema;
		boolean yaIntroducido = false;
		for (int i = 0; i < nombres.size(); i++) {
			extension = "";
			nombre = nombres.get(i);
			rutaSistema = rutasSistema.get(i);
			if (new File(rutaSistema).exists()) {
				switch (tipos.get(i)) {
				case Archivo:
					extension = Ficheros.getExtensionFichero(nombre);
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlArchivo(nombre, rutaSistema);
					break;
				case Carpeta:
					extension = "zip";
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlCarpeta(nombre, rutaSistema);
					break;
				case Video:
					extension = Ficheros.getExtensionFichero(nombre);
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlVideo(nombre, rutaSistema);
					break;

				default:
					break;
				}

				if (!yaIntroducido) {
					JsonEntradaMenuModulo webArchivo = new JsonEntradaMenuModulo();
					webArchivo.setArchivo();
					webArchivo.setRandomHexa();
					webArchivo.setTitulo(nombre);
					webArchivo.setDescripcion("." + extension);
					webArchivo.setGoTo(JsonModulosMenuWeb.config.getRutaHttp() + "/" + nombre);
					System.out.println("GuardadoRutas");
					webArchivo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagen(nombre));
					ArrayList<ConfiguracionJson> listaModulos = JsonModulosFicheros.obtenerConfiguraciones(extension);
					JsonEntradaMenuModulo modulo = new JsonEntradaMenuModulo();
					if (listaModulos != null) {
						for (ConfiguracionJson configuracionJson : listaModulos) {
							http.crearUrlModulo(configuracionJson, nombre, rutaSistema);
							// anyadir modulo en website
							modulo.setTitulo(configuracionJson.getTitulo());
							modulo.setRandomHexa();
							modulo.setDescripcion(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setGoTo(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setRutaImagen(configuracionJson.getRutaImagen());
							webArchivo.addModulo(modulo);
						}
					}

					// anyadir a descarga en website
					modulo.setTitulo("Descargar");
					modulo.setRandomHexa();
					modulo.setDescripcion(nombre);
					modulo.setGoTo("/" + nombre);
					modulo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagenDescarga());
					webArchivo.addModulo(modulo);
					JsonModulosMenuWeb.config.addWeb(webArchivo);
				}
			}
		}
		http.crearUrlIndice(JsonModulosMenuWeb.config);
	}

	public void cargar(Http http) {
		http.vaciarUrls();
		String extension, nombre, rutaSistema;
		boolean yaIntroducido = false;
		for (int i = 0; i < nombres.size(); i++) {
			extension = "";
			nombre = nombres.get(i);
			rutaSistema = rutasSistema.get(i);
			if (new File(rutaSistema).exists()) {
				switch (tipos.get(i)) {
				case Archivo:
					extension = Ficheros.getExtensionFichero(nombre);
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlArchivo(nombre, rutaSistema);
					break;
				case Carpeta:
					extension = "zip";
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlCarpeta(nombre, rutaSistema);
					break;
				case Video:
					extension = Ficheros.getExtensionFichero(nombre);
					yaIntroducido = http.estaEnUrl(nombre);
					http.crearUrlVideo(nombre, rutaSistema);
					break;

				default:
					break;
				}

				if (!yaIntroducido) {
					JsonEntradaMenuModulo webArchivo = new JsonEntradaMenuModulo();
					webArchivo.setArchivo();
					webArchivo.setRandomHexa();
					webArchivo.setTitulo(nombre);
					webArchivo.setDescripcion("." + extension);
					webArchivo.setGoTo(JsonModulosMenuWeb.config.getRutaHttp() + "/" + nombre);
					System.out.println("GuardadoRutas");
					webArchivo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagen(nombre));
					ArrayList<ConfiguracionJson> listaModulos = JsonModulosFicheros.obtenerConfiguraciones(extension);
					JsonEntradaMenuModulo modulo = new JsonEntradaMenuModulo();
					if (listaModulos != null) {
						for (ConfiguracionJson configuracionJson : listaModulos) {
							http.crearUrlModulo(configuracionJson, nombre, rutaSistema);
							// anyadir modulo en website
							modulo.setTitulo(configuracionJson.getTitulo());
							modulo.setRandomHexa();
							modulo.setDescripcion(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setGoTo(configuracionJson.getRutaHttp() + "/" + nombre);
							modulo.setRutaImagen(configuracionJson.getRutaImagen());
							webArchivo.addModulo(modulo);
						}
					}

					// anyadir a descarga en website
					modulo.setTitulo("Descargar");
					modulo.setRandomHexa();
					modulo.setDescripcion(nombre);
					modulo.setGoTo("/" + nombre);
					modulo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagenDescarga());
					webArchivo.addModulo(modulo);
					JsonModulosMenuWeb.config.addWeb(webArchivo);
				}

			}
		}
		http.crearUrlIndice(JsonModulosMenuWeb.config);
	}

	public static void serializar(GuardadoRutas guardado) throws IOException {
		FileOutputStream archivo = new FileOutputStream(getRuta());
		ObjectOutputStream salida = new ObjectOutputStream(archivo);
		salida.writeObject(guardado);
		salida.close();
		archivo.close();
	}

	public static void serializar(GuardadoRutas guardado, String rutaSerializado) throws IOException {
		String rutaSerializadoAux = rutaSerializado + "/rutas.ser";
		FileOutputStream archivo = new FileOutputStream(rutaSerializadoAux);
		ObjectOutputStream salida = new ObjectOutputStream(archivo);
		salida.writeObject(guardado);
		salida.close();
		archivo.close();
	}

	public static GuardadoRutas deserializar() throws IOException, ClassNotFoundException {
		if (getRuta()==null) {
			new GuardadoRutas();
		}
		if (!getRuta().equals("")) {
			GuardadoRutas guardado = new GuardadoRutas();
			FileInputStream archivo = new FileInputStream(getRuta());
			ObjectInputStream entrada = new ObjectInputStream(archivo);
			guardado = (GuardadoRutas) entrada.readObject();
			entrada.close();
			archivo.close();
			return guardado;
		}
		return null;
	}

	public static GuardadoRutas deserializar(String rutaSerializado) throws IOException, ClassNotFoundException {
		GuardadoRutas guardado = new GuardadoRutas();
		FileInputStream archivo = new FileInputStream(rutaSerializado);
		ObjectInputStream entrada = new ObjectInputStream(archivo);
		guardado = (GuardadoRutas) entrada.readObject();
		entrada.close();
		archivo.close();
		return guardado;
	}

	public static String getRuta() {
		return ruta;
	}

}
