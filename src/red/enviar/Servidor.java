package red.enviar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
	private ArrayList<String> ips;
	private ArrayList<File> ficheros;
	private Ventana ventana;
	private String ipServidor;
	public ServerSocket servidor = null;

	/**
	 * Inicia las variables
	 * 
	 * @param ventana          {@link Ventana}
	 * @param multicastControl {@link controlBroadcasting} para detener la ejecucion
	 *                         del hilo en caso de que el hilo BroadcastingIpControl
	 *                         finalice.
	 */

	public Servidor(ArrayList<String> ips) {
		this.ips = ips;
	}

	public Servidor() {

	}

	/**
	 * Inicia hilo que conecta con varios {@link Cliente} a la vez y conforme
	 * terminan se crean nuevos.
	 */

	@Override
	public void run() {
		ArrayList<OperacionServidor> operaciones = new ArrayList<OperacionServidor>();

		// puerto de nuestro servidor
		final int PUERTO = 5013;
		// Creamos el socket del servidor
		try {
			servidor = new ServerSocket(PUERTO);
			servidor.setSoTimeout(2000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final int PUERTOCLIENTE = 5014;
		for (String ip : ips) {
			try {
				Socket cliente = new Socket(ip, PUERTOCLIENTE);
				DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
				out.writeUTF(getIpServidor());
				out.close();
				cliente.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Servidor.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String ip : ips) {
			Boolean aceptado = true;
			Socket sc = null;
			try {
				sc = servidor.accept();
			} catch (IOException e) {
				aceptado = false;
			}
			if (aceptado) {
				if (ficheros != null) {
					operaciones.add(new OperacionServidor(sc, ip, ficheros));
				}
				if (operaciones.size() > 0) {
					operaciones.get(operaciones.size() - 1).start();

				}
			}
		}
		try {
			servidor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<File> getFicheros() {
		return ficheros;
	}

	public void setFicheros(ArrayList<File> ficheros) {
		this.ficheros = ficheros;
	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	public ArrayList<String> getIps() {
		return ips;
	}

	public void setIps(ArrayList<String> ipr) {
		this.ips = ipr;
	}

	public String getIpServidor() {
		return ipServidor;
	}

	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

}