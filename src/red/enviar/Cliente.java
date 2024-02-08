package red.enviar;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import historial.Historial;
import historial.ListaHistorial;
import red.Serializar_funciones;
import red.multicast.MulticastControl;
import teclado.EventoTeclasGlobal;
import ventana.Configuracion;
import ventana.Ventana;

/**
 * Clase que extiende de {@link Thread}, realiza peticiones a {@link Servidor}
 * para obtener comprimido y crear ficheros en la ruta introducida.
 * 
 * @author: Pavon
 * @version: 17/05/2020
 * @since 1.0
 */

public class Cliente extends Thread {
	// Puerto del servidor 
	private static Cliente INSTANCE;
	public final int PUERTO = 5013;
	public final int PUERTOCLIENTE = 5014;
	public static boolean conectado = false;
	public static Socket sc;
	public boolean copiarFicheros = false;
	public String ruta;
	public ServerSocket servidor;
	public String host = "";
	DataOutputStream out = null;
	public String obtener;
	public int numero = 0;
	private DataInputStream din;
	private DataOutputStream dout;
	RandomAccessFile rw;
	Boolean seguir;
	double tamanyo, contador;
	long controladorTamanyo, anteriorTamanyo;
	public String one = "no";

	/**
	 * Inicia las variables
	 * 
	 * @param ventana             {@link Ventana} para manejar
	 *                            {@link ventana.BarraProgreso}
	 * @param controlBroadcasting {@link BroadcastingIpControl} del que se obtiene
	 *                            la direccion servidor.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	private Cliente() throws ClassNotFoundException, IOException {
		if (Configuracion.deserializar().isAutomatic) {
			obtener = "si";
		} else {
			obtener = "no";
		}
		ruta = "";
		tamanyo = (double) 0;
		contador = (double) 0;
		controladorTamanyo = (long) 0;
		anteriorTamanyo = (long) 0;
	}
	
	public static Cliente getInstance() throws ClassNotFoundException, IOException {
        if(INSTANCE == null) {
            INSTANCE = new Cliente();
        }
        
        return INSTANCE;
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
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
		return packet;
	}

	public double byteArrayTodouble(byte[] bytes) {
		double numero = (double) 0;
		ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		DataInputStream dataIn = new DataInputStream(byteIn);
		try {
			numero = dataIn.readDouble();
			dataIn.close();
			byteIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numero;
	}

	/**
	 * Lee comprimido pedido del {@link Servidor}
	 * 
	 * @param socketIs {@link InputStream} buffer del que se obtiene los datos del
	 *                 comprimido mandado por {@link Servidor}
	 * @throws IOException            cuando se interrumpe el traspaso de archivos.
	 * @throws ClassNotFoundException
	 */

	public void readZip() throws IOException, ClassNotFoundException {
		if (Configuracion.deserializar().recibirEnvios) {
			Ventana ventana = Ventana.getInstance();
			out = new DataOutputStream(sc.getOutputStream());

			if (obtener.equals("no")) {
				ventana.ventanaRecibirArchivo(ventana.shlSwt);
				while (!ventana.ventanaRecibirCerrada) {
					try {
						Cliente.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ventana.ventanaRecibirCerrada = false;
				if (ventana.recibirArchivo) {
					obtener = "si";
				}
			}
			out.writeUTF(obtener);

			if (obtener.equals("si")) {
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
									if (!ventana.panBarraProgreso.nombre.contentEquals(outFile.getName())) {
										ventana.panBarraProgreso.setNombre(outFile.getName());
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

									if (ventana.panBarraProgreso.porcentaje != porcentaje) {
										ventana.panBarraProgreso.setPorcentaje(porcentaje);
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
							Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
						}

					}
				}
				ventana.panBarraProgreso.setPorcentajeCero();
				sc.close();
				rw.close();
			}
		}
	}

	public void close() {
		if (servidor != null) {
			try {
				servidor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print("");
			}
		}
	}

	/**
	 * Hilo que conecta con {@link Servidor} y recibe/crea ficheros mostrando el
	 * progreso en {@link Ventana} con {@link ventana.BarraProgreso}.
	 */

	@Override
	public void run() {
		Ventana ventana = Ventana.getInstance();
		MulticastControl multicastControl = ventana.multicastControl;
		try {
			Cliente.sleep(1200);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (multicastControl.seguir) {
			String ipServidor = "";
			try {
				servidor = new ServerSocket(PUERTOCLIENTE);
				Socket sk = servidor.accept();
				DataInputStream in = new DataInputStream(sk.getInputStream());
				ipServidor = in.readUTF();
				sk.close();
				servidor.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.print("");
			}
			// Creo el socket para conectarme con el client
			boolean estaConectado = true;
			if (!ipServidor.equals("")) {
				estaConectado = true;
				try {
					sc = new Socket(ipServidor, PUERTO);
				} catch (Exception e) {
					estaConectado = false;
				}
				if (estaConectado) {
					host = ipServidor;
					conectado = true;
				}
			}
			if (conectado) {
				copiarFicheros = true;
				try {
					ruta = Configuracion.deserializar().carpeta;
					din = new DataInputStream(sc.getInputStream());
					dout = new DataOutputStream(sc.getOutputStream());
					rw = null;
					seguir = true;
					ventana.panBarraProgreso.esconderPanelProgressBar(false);
					readZip();
					ventana.panBarraProgreso.esconderPanelProgressBar(true);
					conectado = false;
					EventoTeclasGlobal.clienteFicherosFuncionando = false;
				} catch (java.net.SocketException e) {
					ventana.panBarraProgreso.esconderPanelProgressBar(true);
					conectado = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ventana.panBarraProgreso.esconderPanelProgressBar(true);
					conectado = false;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ventana.panBarraProgreso.esconderPanelProgressBar(true);
					conectado = false;
				}
			}

			try {
				if (ventana != null) {
					if (ventana.panBarraProgreso != null) {
						if (ventana.panBarraProgreso.estaVisible()) {
							ventana.panBarraProgreso.esconderPanelProgressBar(true);
						}
					}
				}

				if (out != null) {
					out.close();
				}
				if (sc != null) {
					sc.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}