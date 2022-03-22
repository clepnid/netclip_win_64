package red.multicast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MulticastClient extends Thread {
	private final int BYTE_LENGTH = 10, PUERTO_SERVIDOR = 4000, PUERTO_CAMBIO_SERVIDOR = 4010;
	private final String IP_BROADCAST = "224.0.0.1";
	public MulticastSocket socket, socketCambioServidor;
	public byte[] b;
	public MulticastControl multicast;
	public Boolean seguir;

	public MulticastClient(MulticastControl multicast) {
		this.multicast = multicast;
		b = new byte[BYTE_LENGTH];
		seguir = true;
	}

	@SuppressWarnings("deprecation")
	public void close() {
		try {
			this.socketCambioServidor = new MulticastSocket(PUERTO_CAMBIO_SERVIDOR);
			this.socketCambioServidor.joinGroup(InetAddress.getByName(IP_BROADCAST));
			byte[] b = intToByteArray(0);
			DatagramPacket dgram;

			dgram = new DatagramPacket(b, b.length, InetAddress.getByName(IP_BROADCAST), PUERTO_CAMBIO_SERVIDOR);
			socket.send(dgram);
			socketCambioServidor.close();
		} catch (IOException e) {
			socketCambioServidor.close();
		}
		seguir = false;
	}

	@SuppressWarnings("deprecation")
	public void cambioServidor() {
		try {
			this.socketCambioServidor = new MulticastSocket(PUERTO_CAMBIO_SERVIDOR);
			this.socketCambioServidor.joinGroup(InetAddress.getByName(IP_BROADCAST));
			byte[] b = intToByteArray(1);
			DatagramPacket dgram;
			dgram = new DatagramPacket(b, b.length, InetAddress.getByName(IP_BROADCAST), PUERTO_CAMBIO_SERVIDOR);
			socket.send(dgram);
			socketCambioServidor.close();
		} catch (IOException e) {
			socketCambioServidor.close();
		}
	}

	@SuppressWarnings("deprecation")
	public void pararServidor() {
		try {
			this.socketCambioServidor = new MulticastSocket(PUERTO_CAMBIO_SERVIDOR);
			this.socketCambioServidor.joinGroup(InetAddress.getByName(IP_BROADCAST));
			byte[] b = intToByteArray(2);
			DatagramPacket dgram;
			dgram = new DatagramPacket(b, b.length, InetAddress.getByName(IP_BROADCAST), PUERTO_CAMBIO_SERVIDOR);
			socket.send(dgram);
			socketCambioServidor.close();

		} catch (IOException e) {
			socketCambioServidor.close();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		DatagramPacket dgram = new DatagramPacket(b, b.length);
		try {
			this.socket = new MulticastSocket(PUERTO_SERVIDOR);
			this.socket.setSoTimeout(1000);
			this.socket.joinGroup(InetAddress.getByName(IP_BROADCAST));
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (seguir) {
			Boolean tiempoExpirado = false;
			try {
				this.socket.receive(dgram); // Se bloquea hasta que llegue un datagrama
			} catch (SocketTimeoutException ex) {
				tiempoExpirado = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!tiempoExpirado) {
				if (!multicast.soyServidor()) {
					if (!MulticastControl.ip_servidor.equals(dgram.getAddress().getHostAddress())) {
						MulticastControl.ip_servidor = dgram.getAddress().getHostAddress();
						System.out.println("4. recibido servidor");
						if (multicast.soyServidor()) {
							multicast.soyServidor = true;
							if (!multicast.servidor.isAlive()) {
								multicast.servidor = new MulticastServer(multicast);
								multicast.servidor.start();
							}
						} else {
							multicast.soyServidor = false;
						}
						if (multicast.hayServidor()) {
							System.out.println("hay servidor");
						}else {
							System.out.println("NO hay servidor");
						}
						if (multicast.soyServidor()) {
							System.out.println("soy servidor");
						}else {
							System.out.println("NO soy servidor");
						}
					}
				}
			}
		}
		this.socket.close();

	}

	public byte[] intToByteArray(int numero) {
		byte[] b = null;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		try {
			dataOut.writeInt(numero);
			dataOut.close();
			b = byteOut.toByteArray();
			byteOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

}
