package red.multicast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import red.compartirContenido.Cliente;
import red.compartirContenido.Servidor;
import ventana.Ventana;

public class MulticastControl extends Thread {
	private static MulticastControl INSTANCE;
	private final int BYTE_LENGTH = 10, PUERTO_CAMBIO_SERVIDOR = 4010;
	private final String IP_BROADCAST = "224.0.0.1";
	public MulticastSocket socket;
	public static String ip_servidor;
	public byte[] b;
	public Boolean soyServidor;
	public MulticastServer servidor;
	public MulticastClient cliente;
	public MulticastRedScanner escaner;
	public Ventana ventana;
	public Boolean seguir;
	private Cliente clienteCompartirContenido;
	private Servidor servidorCompartirContenido;
	private red.compartirFicheros.Servidor servidorCompartirFicheros;
	private red.historial.Servidor servidorHistorial;

	public MulticastControl() {
		b = new byte[BYTE_LENGTH];
		ip_servidor = "";
		seguir = true;
		soyServidor = false;
	}
	
	public static MulticastControl getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MulticastControl();
            INSTANCE.ventana = Ventana.getInstance();
        }
        
        return INSTANCE;
    }

	public void close() {
		seguir = false;
		cliente.close();
		escaner.close();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		DatagramPacket dgram = new DatagramPacket(b, b.length);
		try {
			this.socket = new MulticastSocket(PUERTO_CAMBIO_SERVIDOR);
			this.socket.setSoTimeout(1000);
			this.socket.joinGroup(InetAddress.getByName(IP_BROADCAST));

		} catch (SocketException ex) {
			socket.close();
		} catch (UnknownHostException ex) {
			socket.close();
		} catch (IOException ex) {
			socket.close();
		}
		this.servidor = new MulticastServer(this);
		this.cliente = new MulticastClient(this);
		this.cliente.start();
		this.escaner = new MulticastRedScanner(this);
		this.escaner.start();

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
				if (byteArrayToInt(dgram.getData()) != 0) {
					if (byteArrayToInt(dgram.getData()) == 2) {
						ip_servidor = "";
						soyServidor = false;
					} else {
						ip_servidor = dgram.getAddress().getHostAddress();
						if (soyServidor()) {
							soyServidor = true;
							if (!servidor.isAlive()) {
								this.servidor = new MulticastServer(this);
								servidor.start();
							}
							ventana.httpCrearRutas();
						} else {
							soyServidor = false;
						}
					}
				} else {
					if (ip_servidor.equals(dgram.getAddress().getHostAddress())) {
						ip_servidor = "";
					}
				}

			}

			escaner.mandarListaPropiaEscaner();

		}
		this.socket.close();

	}

	public Boolean soyServidor() {
		return getMyIps().contains(ip_servidor);
	}

	public Boolean hayServidor() {
		if (ip_servidor.equals("")) {
			return false;
		}
		return true;
	}

	public static CopyOnWriteArrayList<String> getMyIps() {
		Enumeration<NetworkInterface> e;
		CopyOnWriteArrayList<String> myIps = new CopyOnWriteArrayList<String>();
		try {
			e = NetworkInterface.getNetworkInterfaces();

			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();

				List<InterfaceAddress> e2 = ni.getInterfaceAddresses();

				for (int i = 0; i < e2.size(); i++) {
					InetAddress ip = e2.get(i).getAddress();
					String ip_aux = ip.getHostAddress().toString().split("\\.")[0];
					if (ip instanceof Inet4Address
							&& (ip_aux.equals("192") || ip_aux.equals("192") || ip_aux.equals("10"))) {
						myIps.add(ip.getHostAddress().toString());
					}
				}
			}
			return myIps;
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public void compartirLista(Ventana ventana) {
		this.ventana = ventana;
		clienteCompartirContenido = new red.compartirContenido.Cliente(ventana, this);
		clienteCompartirContenido.start();
		servidorCompartirContenido = new red.compartirContenido.Servidor(ventana, this);
		servidorCompartirContenido.start();
		servidorCompartirFicheros = new red.compartirFicheros.Servidor(ventana, this);
		servidorCompartirFicheros.start();
		servidorHistorial = new red.historial.Servidor(this);
		servidorHistorial.start();
	}

	public int byteArrayToInt(byte[] bytes) {
		int numero = 0;
		ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		DataInputStream dataIn = new DataInputStream(byteIn);
		try {
			numero = dataIn.readInt();
			dataIn.close();
			byteIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numero;
	}
}
