package red.compartirContenido;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.wb.swt.SWTResourceManager;

import portapapeles.Contenido;
import red.Serializar_funciones;
import red.multicast.MulticastControl;
import ventana.Ventana;

/**
 * Clase que extiende de {@link Thread}, hace uso de metodos
 * {@link MulticastCambioServidor} para saber cual es la direccion a conectar
 * utilizando {@link Socket}, obtener {@link Contenido} del {@link Servidor} y
 * mostrarlo por {@link Ventana}.
 * 
 * @author: Pavon
 * @version: 17/05/2020
 * @since 1.0
 */

public class Cliente extends Thread {

	public static boolean conectado = false;
	public static Socket sc;
	public Boolean botonDesconectado;
	public Ventana ventana;
	MulticastControl multicastControl;

	/**
	 * Constructor para tener acceso a {@link Ventana} y
	 * {@link MulticastCambioServidor}.
	 * 
	 * @param ventana             {@link Ventana} para mostrar {@link Contenido}
	 *                            obtenido del {@link Servidor}
	 * @param controlBroadcasting {@link MulticastCambioServidor} obtener direccion
	 *                            y conectar con {@link Servidor} por medio de
	 *                            {@link Socket}
	 */

	public Cliente(Ventana ventana, MulticastControl controlBroadcasting) {
		this.ventana = ventana;
		this.multicastControl = controlBroadcasting;
		botonDesconectado = true;
	}

	/**
	 * Inicia hilo para conectar con {@link Servidor}, obtener {@link Contenido} del
	 * {@link Servidor} y mostrarlo por {@link Ventana}.
	 */

	@Override
	public void run() {
		// Puerto del servidor
		final int PUERTO = 5000;
		while (multicastControl.seguir) {
			if (!multicastControl.hayServidor() && !multicastControl.soyServidor() && ventana != null) {
				// cambia el indicador inferior derecho a gris ya que no hay servidor
				if (!botonDesconectado) {
					ventana.display.asyncExec(new Runnable() {
						public void run() {
							ventana.lblBotonServidor
									.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_off.gif"));
							ventana.lblBotonServidor.setToolTipText("Apagado");
							ventana.lblHayServidor
									.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_gris.gif"));
						}
					});
					ventana.vaciarLista();
					ventana.contenido = null;
					botonDesconectado = true;
				}
			}
			// si hay servidor en la red
			if (multicastControl.hayServidor() && !multicastControl.soyServidor()) {
				// Creo el socket para conectarme con el cliente
				boolean estaConectado = true;
				estaConectado = true;
				try {
					conectado = false;
					sc = new Socket();
					InetSocketAddress direccion = new InetSocketAddress(MulticastControl.ip_servidor, PUERTO);
					sc.setSoTimeout(1000);
					sc.connect(direccion);
				} catch (Exception e) {
					estaConectado = false;
				}
				if (estaConectado) {
					conectado = true;
				}
			}
			if (conectado) {
				try {

					// envio mensaje a la lista de servidores

					// Recibo el mensaje del servidor
					DataInputStream dIn = new DataInputStream(sc.getInputStream());

					int length = dIn.readInt(); // read length of incoming message
					if (length > 0) {
						byte[] message = new byte[length];
						dIn.readFully(message, 0, message.length); // read the message
						Contenido contenido = (Contenido) Serializar_funciones.convertirAObjeto(message);
						if (ventana.contenido != null) {
							if (!ventana.contenido.equals(contenido)) {
								ventana.mostrarContenidoPorPantalla(contenido);
								botonDesconectado = false;
								ventana.cambiarbtnHayServidor(true);
							}
						} else {
							ventana.mostrarContenidoPorPantalla(contenido);
							ventana.cambiarbtnHayServidor(true);
							botonDesconectado = false;
						}
					}
					dIn.close();

				} catch (IOException ex) {
					;
				}

			}
			try {
				Cliente.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if (sc != null) {
				sc.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}