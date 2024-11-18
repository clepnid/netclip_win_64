package red.multicast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import red.Serializar_funciones;
import java.net.SocketTimeoutException;

public class MulticastRedScannerScript extends Thread {
	private final int BYTE_LENGTH = 1024, PUERTO_SERVIDOR = 4020;
	private final String IP_BROADCAST = "224.0.0.1";
	public MulticastSocket socket, socketCambioServidor;
	public ListaIps listaIps;
	public byte[] b;
	public Boolean seguir;
	public VentanaMulticast ventana;

	public MulticastRedScannerScript() {
		listaIps = new ListaIps();
		seguir = true;
		b = new byte[BYTE_LENGTH];
	}

	public void close() {
		seguir = false;
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

				ListaIps listaIpsAux = (ListaIps) Serializar_funciones.convertirAObjeto(dgram.getData());

				if (listaIpsAux.get(0).equals("0")) {
					for (int i = 1; i < listaIpsAux.size(); i++) {
						System.out.println("eliminar recorrer: " + listaIpsAux.get(i));
						int indice = listaIps.indexOf(listaIpsAux.get(i));
						if (!(indice == -1)) {
							System.out.println("eliminar: " + listaIpsAux.get(i));
							listaIps.remove(indice);
						}
						System.out.println("es igual?: " + MulticastControl.ip_servidor);
						if (ventana.cambioServidor.equals(listaIpsAux.get(i))) {
							System.out.println("eliminado");
							MulticastControl.ip_servidor = "";
						}
					}

				} else {
					for (int i = 1; i < listaIpsAux.size(); i++) {
						if (!listaIps.contains(listaIpsAux.get(i))) {
							System.out.println("anyadir: " + listaIpsAux.get(i));
							listaIps.add(listaIpsAux.get(i));
							listaIps.listaNombres.add(listaIpsAux.listaNombres.get(i));
							listaIps.listaSeriales.add(listaIpsAux.listaSeriales.get(i));
						}
					}
				}

			} 
		}
		this.socket.close();

	}

	public byte[] intToByteString(String string) {
		byte[] b = null;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		try {
			dataOut.writeChars(string);
			dataOut.close();
			b = byteOut.toByteArray();
			byteOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	public String byteArrayToString(byte[] bytes) {
		String string = "";
		ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		DataInputStream dataIn = new DataInputStream(byteIn);
		try {
			string = dataIn.readUTF();
			dataIn.close();
			byteIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return string;
	}
}
