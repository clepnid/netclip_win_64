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

public class Servidor2 extends Thread {

	private ArrayList<String> ips;
	private Boolean enviarTodos;
	private ArrayList<File> fichero;
	private Ventana ventana;
	private String ipServidor;
	public ServerSocket servidor = null;

	/**
	 * Inicia las variables
	 * 
	 * @param ventana          {@link Ventana}
	 * @param multicastControl {@link ControlMulticast} para detener la ejecucion
	 *                         del hilo en caso de que el hilo BroadcastingIpControl
	 *                         finalice.
	 */

	public Servidor2(ArrayList<String> ips, Boolean enviarTodos) {
		this.ips = ips;
		this.enviarTodos = enviarTodos;
	}

	public Servidor2() {

	}

	/**
	 * Inicia hilo que conecta con varios {@link Cliente} a la vez y conforme
	 * terminan se crean nuevos.
	 */

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
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
			Thread.sleep(100);
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
				if (enviarTodos) {
					;
					//operaciones.add(new OperacionServidor(sc, ip, ventana));
				} else {
					operaciones.add(new OperacionServidor(sc, ip, fichero));
				}
				operaciones.get(operaciones.size() - 1).start();
			}
		}
		try {
			servidor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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