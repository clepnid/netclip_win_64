package red.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastServer extends Thread {
	public static MulticastSocket socket;
	public Boolean seguir;
	public MulticastControl multicast;

	public MulticastServer(MulticastControl multicastCambioServidor) {
		this.multicast = multicastCambioServidor;
		seguir = true;
	}

	@Override
	public void run() {
		try {
			socket = new MulticastSocket();

			byte[] b = "Clepnid".getBytes();
			DatagramPacket dgram;

			dgram = new DatagramPacket(b, b.length, InetAddress.getByName("224.0.0.1"), 4000);

			while (multicast.seguir && multicast.soyServidor()) {
				socket.send(dgram);
				MulticastServer.sleep(1000);
			}
			socket.close();

		} catch (SocketException ex) {
			socket.close();
		} catch (UnknownHostException ex) {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		seguir = false;
	}

}