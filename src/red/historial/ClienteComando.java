package red.historial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import historial.Historial;
import historial.ListaHistorial;
import red.Serializar_funciones;
import red.multicast.MulticastControl;
import ventana.Ventana;

/**
 * Clase que extiende de {@link Thread}, realiza peticiones a {@link Servidor}
 * para obtener comprimido y crear ficheros en la ruta introducida.
 * 
 * @author: Pavon
 * @version: 17/05/2020
 * @since 1.0
 */

public class ClienteComando extends Thread {
	// Puerto del servidor
	public final static int PUERTO = 5015;
	public static boolean conectado = false;
	public static Socket sc;
	public boolean copiarFicheros = false;
	private DataInputStream din;
	private DataOutputStream dout;
	RandomAccessFile rw;
	Boolean seguir;
	public String ruta;
	DataOutputStream out = null;
	long tamanyo, contador, controladorTamanyo, anteriorTamanyo;
	public Historial historial;
	int porcentaje;
	MulticastControl multicastControl;
	public String ip_servidor;

	/**
	 * Inicia las variables
	 * 
	 * @param ventana             {@link Ventana} para manejar
	 *                            {@link ventana.BarraProgreso}
	 * @param controlBroadcasting {@link MulticastCambioServidor} del que se obtiene
	 *                            la direccion servidor.
	 */

	public ClienteComando(MulticastControl multicastControl, Historial historial) {
		tamanyo = (long) 0;
		this.porcentaje = 0;
		contador = (long) 0;
		controladorTamanyo = (long) 0;
		anteriorTamanyo = (long) 0;
		this.multicastControl = multicastControl;
		if (multicastControl.escaner.listaIps.listaSeriales.contains(historial.serial)) {
			ip_servidor = multicastControl.escaner.listaIps.getIpdeSerial(historial.serial);
		}
		this.historial = historial;
	}

	public static Boolean comprobar_existencia(String ip, String rutaFicheroServidor) {
		try {
			Socket sc = new Socket(ip, PUERTO);
			sc.setSoTimeout(1000);
			DataOutputStream out = new DataOutputStream(sc.getOutputStream());
			out.writeUTF(rutaFicheroServidor);
			DataInputStream din = new DataInputStream(sc.getInputStream());
			String respuesta = din.readUTF();
			if (sc != null) {
				sc.close();
			}
			if (out != null) {
				out.close();
			}
			if (din != null) {
				din.close();
			}
			if (respuesta.equals("si")) {
				return true;
			}

		} catch (IOException e) {
			;
		}

		return false;
	}

	private byte[] ReadStream() {
		byte[] data_buff = null;
		try {
			int b = 0;
			String buff_length = "";
			while ((b = din.read()) != 4) {
				buff_length += (char) b;
			}
			int data_length = Integer.parseInt(buff_length);
			data_buff = new byte[Integer.parseInt(buff_length)];
			int byte_read = 0;
			int byte_offset = 0;
			while (byte_offset < data_length) {
				byte_read = din.read(data_buff, byte_offset, data_length - byte_offset);
				byte_offset += byte_read;
			}
		} catch (IOException ex) {
			Logger.getLogger(ClienteComando.class.getName()).log(Level.SEVERE, null, ex);
		}
		return data_buff;
	}

	private byte[] CreateDataPacket(byte[] cmd, byte[] data) {
		byte[] packet = null;
		try {
			byte[] initialize = new byte[1];
			initialize[0] = 2;
			byte[] separator = new byte[1];
			separator[0] = 4;
			byte[] data_length = String.valueOf(data.length).getBytes("UTF8");
			packet = new byte[initialize.length + cmd.length + separator.length + data_length.length + data.length];

			System.arraycopy(initialize, 0, packet, 0, initialize.length);
			System.arraycopy(cmd, 0, packet, initialize.length, cmd.length);
			System.arraycopy(data_length, 0, packet, initialize.length + cmd.length, data_length.length);
			System.arraycopy(separator, 0, packet, initialize.length + cmd.length + data_length.length,
					separator.length);
			System.arraycopy(data, 0, packet, initialize.length + cmd.length + data_length.length + separator.length,
					data.length);

		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(ClienteComando.class.getName()).log(Level.SEVERE, null, ex);
		}
		return packet;
	}

	/**
	 * Lee comprimido pedido del {@link Servidor}
	 * 
	 * @param socketIs {@link InputStream} buffer del que se obtiene los datos del
	 *                 comprimido mandado por {@link Servidor}
	 * @throws IOException cuando se interrumpe el traspaso de archivos.
	 */

	public void readZip() throws IOException {
		out = new DataOutputStream(sc.getOutputStream());
		out.writeUTF("si");
		out.flush();
		out.writeUTF(historial.rutaFicheroServidor);
		porcentaje = 0;
		String serial_servidor = null;
		String ruta_fichero_servidor = null;
		String nombre_fichero = null;
		String ruta_fichero_cliente = null;
		while (seguir) {
			long current_file_pointer = 0;
			byte[] initilize = new byte[1];
			boolean loop_break = false;
			while (!loop_break) {
				try {
					din.read(initilize, 0, initilize.length);
					if (initilize[0] == 2) {
						byte[] cmd_buff = new byte[3];
						din.read(cmd_buff, 0, cmd_buff.length);
						byte[] recv_data = ReadStream();
						switch (Integer.parseInt(new String(cmd_buff))) {
						case 121:
							serial_servidor = new String(recv_data);
							break;
						case 122:
							ruta_fichero_servidor = new String(recv_data);
							break;
						case 123:
							tamanyo = Serializar_funciones.bytesToLong(recv_data);
							break;
						case 124:
							String fileName = new String(recv_data);
							ruta_fichero_cliente = ruta + fileName.replace("\\", "/");
							File outFile = new File(ruta_fichero_cliente);
							nombre_fichero = outFile.getName();
							File parentFolder = outFile.getParentFile();
							if (!parentFolder.exists()) {
								parentFolder.mkdirs();
							}
							if (!multicastControl.ventana.panBarraProgreso.nombre.contentEquals(outFile.getName())) {
								multicastControl.ventana.panBarraProgreso.setNombre(outFile.getName());
							}
							if (rw != null) {
								rw.close();
							}
							rw = new RandomAccessFile(ruta + fileName, "rw");
							dout.write(CreateDataPacket("125".getBytes("UTF8"),
									String.valueOf(current_file_pointer).getBytes("UTF8")));
							dout.flush();
							break;
						case 126:
							rw.seek(current_file_pointer);
							rw.write(recv_data);
							current_file_pointer = rw.getFilePointer();
							
							if (contador > rw.getFilePointer()) {
								controladorTamanyo += anteriorTamanyo;
							}
							if (anteriorTamanyo != rw.length()) {
								anteriorTamanyo = rw.length();
							}
							
							contador = rw.getFilePointer();
							
							porcentaje = (int) Math.round((((double) (contador+controladorTamanyo)) / ((double) tamanyo)) * 100);
							
							if (multicastControl.ventana.panBarraProgreso.porcentaje != porcentaje) {
								multicastControl.ventana.panBarraProgreso.setPorcentaje(porcentaje);
							}

							dout.write(CreateDataPacket("125".getBytes("UTF8"),
									String.valueOf(current_file_pointer).getBytes("UTF8")));
							dout.flush();
							break;
						case 127:
							if ("Close".equals(new String(recv_data))) {
								loop_break = true;
							}
							break;
						case 128:
							if ("Close".equals(new String(recv_data))) {
								loop_break = true;
								seguir = false;
							}
							break;
						case 129:
							if ("Close".equals(new String(recv_data))) {
								ListaHistorial.anyadirHistoria(new Historial(serial_servidor, nombre_fichero,
										ruta_fichero_servidor, ruta_fichero_cliente, Historial.fechaActual()));
							}
							break;
						case 130:
							String fichero_nombre = new String(recv_data);
							ruta_fichero_cliente = ruta + fichero_nombre.replace("\\", "/");
							nombre_fichero = new File(ruta_fichero_cliente).getName();
							ListaHistorial.anyadirHistoria(new Historial(serial_servidor, nombre_fichero,
									ruta_fichero_servidor, ruta_fichero_cliente, Historial.fechaActual()));
							break;
						}
					}
				} catch (java.net.SocketException e) {
					e.printStackTrace();
				} catch (IOException ex) {
					Logger.getLogger(ClienteComando.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		}
		sc.close();
		rw.close();
	}

	/**
	 * Hilo que conecta con {@link Servidor} y recibe/crea ficheros mostrando el
	 * progreso en {@link Ventana} con {@link ventana.BarraProgreso}.
	 */

	@Override
	public void run() {

		// Creo el socket para conectarme con el client
		boolean estaConectado = true;
		try {
			sc = new Socket(ip_servidor, PUERTO);
		} catch (Exception e) {
			estaConectado = false;
		}
		if (estaConectado) {
			conectado = true;
		}
		if (conectado) {
			copiarFicheros = true;
			try {
				din = new DataInputStream(sc.getInputStream());
				dout = new DataOutputStream(sc.getOutputStream());
				rw = null;
				seguir = true;
				multicastControl.ventana.panBarraProgreso.esconderPanelProgressBar(false);
				readZip();
				multicastControl.ventana.panBarraProgreso.esconderPanelProgressBar(true);
				conectado = false;
			} catch (java.net.SocketException e) {
				multicastControl.ventana.panBarraProgreso.esconderPanelProgressBar(true);
				conectado = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			if (out != null) {
				out.close();
			}
			if (din != null) {
				din.close();
			}
			if (dout != null) {
				dout.close();
			}
			if (sc != null) {
				sc.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(ClienteComando.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}