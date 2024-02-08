package red.compartirFicheros;

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
import ventana.HiloBarraProgreso;
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
	public final int PUERTO = 5001;
	public static boolean conectado = false;
	public static Socket sc;
	public boolean copiarFicheros = false;
	private DataInputStream din;
	private DataOutputStream dout;
	RandomAccessFile rw;
	Boolean seguir;
	public String ruta;
	public HiloBarraProgreso hiloBarraProgreso;
	DataOutputStream out = null;
	long tamanyo, contador, controladorTamanyo, anteriorTamanyo;
	MulticastControl multicastControl;
	public String one;

	/**
	 * Inicia las variables
	 * 
	 * @param ventana             {@link Ventana} para manejar
	 *                            {@link ventana.BarraProgreso}
	 * @param controlBroadcasting {@link MulticastCambioServidor} del que se obtiene
	 *                            la direccion servidor.
	 */

	public ClienteComando(MulticastControl controlBroadcasting) {
		tamanyo = (long) 0;
		contador = (long) 0;
		controladorTamanyo = (long) 0;
		anteriorTamanyo = (long) 0;
		this.multicastControl = controlBroadcasting;
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
		out.writeUTF("no");
		int porcentaje = 0;
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
							while (!(hiloBarraProgreso.barraProgreso.nombre != null)) {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (!hiloBarraProgreso.barraProgreso.nombre.equals(outFile.getName())) {
								hiloBarraProgreso.barraProgreso.setNombre(outFile.getName());
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

							if (hiloBarraProgreso.barraProgreso.porcentaje != porcentaje) {
								hiloBarraProgreso.barraProgreso.setPorcentaje(porcentaje);
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
			sc = new Socket(MulticastControl.ip_servidor, PUERTO);
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
				hiloBarraProgreso = new HiloBarraProgreso();
				hiloBarraProgreso.start();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				readZip();
				hiloBarraProgreso.barraProgreso.close();
				conectado = false;
			} catch (java.net.SocketException e) {
				hiloBarraProgreso.barraProgreso.close();
				conectado = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			if (hiloBarraProgreso!=null) {
				if (hiloBarraProgreso.barraProgreso!=null) {
					hiloBarraProgreso.barraProgreso.close();
				}
			}
			if (multicastControl.seguir) {
				multicastControl.close();
			}
			if (out != null) {
				out.close();
			}
			if (sc != null) {
				sc.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(ClienteComando.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}