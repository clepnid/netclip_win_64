package red.compartirContenido;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import red.multicast.MulticastControl;
import ventana.Ventana;

/**
 * Clase que extiende de {@link Thread}, crea hilos {@link OperacionServidor}
 * para conectar con varios {@link Cliente} y conforme terminan se crean nuevos.
 * 
 * @author: Pavon
 * @version: 17/05/2020
 * @since 1.0
 */

public class Servidor extends Thread {

	Ventana ventana;
	MulticastControl multicastControl;
	public ServerSocket servidor = null;

	/**
	 * Inicia las variables
	 * 
	 * @param ventana          {@link Ventana}
	 * @param multicastControl {@link controlBroadcasting} para detener la ejecucion
	 *                         del hilo en caso de que el hilo BroadcastingIpControl
	 *                         finalice.
	 */

	public Servidor(Ventana ventana, MulticastControl multicastControl) {
		this.ventana = ventana;
		this.multicastControl = multicastControl;
	}

	/**
	 * crea hilos {@link OperacionServidor} para conectar con varios {@link Cliente}
	 * a la vez y conforme terminan se crean nuevos.
	 */

	@Override
	public void run() {

		boolean reponer = false;
		int cont = 1, num = 3;
		ArrayList<OperacionServidor> operaciones = new ArrayList<OperacionServidor>();

		// puerto de nuestro servidor
		final int PUERTO = 5000;
		// Creamos el socket del servidor
		try {
			servidor = new ServerSocket(PUERTO);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (cont <= num && multicastControl.seguir) {
			// cuando el equipo es servidor
			if (multicastControl.soyServidor && !(ventana.contenido == null)) {
				operaciones.add(new OperacionServidor(servidor, this));
				operaciones.get(operaciones.size() - 1).start();
				cont++;
			}
			while (multicastControl.seguir && multicastControl.soyServidor && !(ventana.contenido == null)) {
				if (reponer) {
					operaciones.add(new OperacionServidor(servidor, this));
					operaciones.get(operaciones.size() - 1).start();
					reponer = false;
				}
				for (int i = 0; i < operaciones.size(); i++) {
					if (!operaciones.get(i).conectado) {
						reponer = true;
						operaciones.remove(i);
					}
				}
				try {
					Servidor.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cont = 1;
			try {
				Servidor.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Servidor.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			if (servidor != null) {
				if (!servidor.isClosed()) {
					servidor.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}