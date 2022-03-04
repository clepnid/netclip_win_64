package historial;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;

import red.historial.ClienteComando;
import red.multicast.MulticastRedScanner;

public class Historial extends CopyOnWriteArrayList<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fecha;
	public int indice;
	public String serial, contenido, nombreFichero, rutaFicheroServidor, rutaFicheroCliente, tipoContenido;
	public final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");
	public boolean esFichero;

	public enum Filtrado {
		Carpeta, Red, NoExiste, String
	}

	public Historial(String serial, String nombreFichero, String rutaFicheroServidor, String rutaFicheroCliente,
			String fecha) {
		this.indice = ListaHistorial.getSizeLista();
		this.serial = serial;
		this.contenido = null;
		this.nombreFichero = nombreFichero;
		this.rutaFicheroCliente = rutaFicheroCliente;
		this.rutaFicheroServidor = rutaFicheroServidor;
		this.setFecha(fecha);
		this.esFichero = true;
	}

	public Historial(String contenido, String fecha) {
		this.indice = ListaHistorial.getSizeLista();
		this.serial = null;
		this.contenido = contenido;
		this.nombreFichero = null;
		this.rutaFicheroCliente = null;
		this.rutaFicheroServidor = null;
		this.setFecha(fecha);
		this.esFichero = false;
	}

	public Filtrado getHistorialFiltrado(MulticastRedScanner escaner) {
		if (this.esFichero) {
			if (this.estaEnDisco()) {
				return Filtrado.Carpeta;
			} else {
				if (this.openEquipoOrigen(escaner)) {
					// volver a descargar
					// se tiene que controlar la existencia del archivo en nuestra carpeta y en la
					// carpeta del servidor

					if (ClienteComando.comprobar_existencia(this.listaIpEquipoOrigen(escaner),
							this.rutaFicheroServidor)) {
						return Filtrado.Red;
					} else {
						// equipo esta en la red pero no tiene el fichero
						return Filtrado.NoExiste;
					}
				} else {
					// no esta en la red
					return Filtrado.NoExiste;
				}
			}
		} else {
			return Filtrado.String;
		}
	}

	public Historial() {
		;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public void setFecha() {
		this.setFecha(dtf.format(LocalDateTime.now()));
	}

	public static String fechaActual() {
		return dtf.format(LocalDateTime.now());
	}

	public boolean openEquipoOrigen(MulticastRedScanner escaner) {
		return escaner.listaIps.listaSeriales.contains(this.serial);
	}

	public String listaIpEquipoOrigen(MulticastRedScanner escaner) {
		return escaner.listaIps.getIpdeSerial(this.serial);
	}

	public boolean estaEnDisco() {
		return new File(this.rutaFicheroCliente).exists();
	}

}
