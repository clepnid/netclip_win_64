package red.compartirFicheros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

import red.Serializar_funciones;
import ventana.Configuracion;
import ventana.Ventana;

/**
 * Clase que extiende de {@link Thread}, manda comprimido de ficheros a
 * {@link Cliente}
 * 
 * @author: Pavon
 * @version: 17/05/2020
 * @since 1.0
 */

class OperacionServidor extends Thread {

	private RandomAccessFile rw;
	boolean conectado = false;
	private Socket sc;
	private DataInputStream din = null;
	private DataOutputStream dout = null;

	Ventana ventana;

	/**
	 * Inicia las variables
	 * 
	 * @param sc      {@link Socket} conexion con {@link Cliente}
	 * @param ventana {@link Ventana}
	 */

	public OperacionServidor(Socket sc, Ventana ventana) {
		this.sc = sc;
		this.rw = null;
		conectado = true;
		this.ventana = ventana;
	}

	/**
	 * Metodo principal para controlar el envio segun el tipo de fichero.
	 * 
	 * @param zipOpStream {@link ZipOutputStream} buffer para enviar fichero.
	 * @param outFile     {@link File} fichero a controlar.
	 */

	public void sendFileOutput(File outFile) throws Exception {
		String relativePath = outFile.getAbsoluteFile().getParentFile().getAbsolutePath();
		if (outFile.isDirectory()) {
			sendFolder(outFile, relativePath);
		} else {
			sendFile(outFile, relativePath);
			dout.write(CreateDataPacket("129".getBytes("UTF8"), "Close".getBytes("UTF8")));
			dout.flush();
		}
	}

	/**
	 * Envia los ficheros contenidos en la carpeta.
	 * 
	 * @param zipOpStream  {@link ZipOutputStream} buffer para enviar fichero.
	 * @param folder       {@link File} carpeta.
	 * @param relativePath {@link File} ruta relativa.
	 */

	public void sendFolder(File folder, String relativePath) throws Exception {
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				sendFolder(file, relativePath);
			} else {
				sendFile(file, relativePath);
			}
		}
	}

	/**
	 * Envia fichero
	 * 
	 * @param zipOpStream  {@link ZipOutputStream} buffer para enviar fichero.
	 * @param file         {@link File} carpeta.
	 * @param relativePath {@link File} ruta relativa.
	 */

	public void sendFile(File file, String relativePath) throws Exception {
		relativePath = relativePath.replace("\\", "/");
		String absolutePath = file.getAbsolutePath().replace("\\", "/");
		String zipEntryFileName = absolutePath;
		if (absolutePath.startsWith(relativePath)) {
			zipEntryFileName = absolutePath.substring(relativePath.length());
		} else {
			throw new Exception("Invalid Absolute Path");
		}
		dout.write(CreateDataPacket("124".getBytes("UTF8"), zipEntryFileName.getBytes("UTF8")));
		dout.flush();
		rw = new RandomAccessFile(file, "r");
		long current_file_pointer = 0;
		boolean loop_break = false;
		while (!loop_break) {
			if (din.read() == 2) {
				byte[] cmd_buff = new byte[3];
				din.read(cmd_buff, 0, cmd_buff.length);
				byte[] recv_buff = ReadStream(din);
				switch (Integer.parseInt(new String(cmd_buff))) {
				case 125:
					current_file_pointer = Long.valueOf(new String(recv_buff));
					int buff_len = (int) (rw.length() - current_file_pointer < 20000
							? rw.length() - current_file_pointer
							: 20000);
					byte[] temp_buff = new byte[buff_len];
					if (current_file_pointer != rw.length()) {
						rw.seek(current_file_pointer);
						rw.read(temp_buff, 0, temp_buff.length);
						dout.write(CreateDataPacket("126".getBytes("UTF8"), temp_buff));
						dout.flush();
					} else {
						dout.write(CreateDataPacket("127".getBytes("UTF8"), "Close".getBytes("UTF8")));
						dout.flush();
						loop_break = true;
					}
					break;
				}
			}
		}

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
			Logger.getLogger(OperacionServidor.class.getName()).log(Level.SEVERE, null, ex);
		}
		return packet;
	}

	private byte[] ReadStream(DataInputStream din) {
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
			Logger.getLogger(OperacionServidor.class.getName()).log(Level.SEVERE, null, ex);
		}
		return data_buff;
	}

	/**
	 * Inicia el hilo, envía fichero pedidos por {@link Cliente}
	 */

	public void run() {
		try {
			// empieza a leer
			din = new DataInputStream(sc.getInputStream());
			dout = new DataOutputStream(sc.getOutputStream());
			if (din.readUTF().equals("si")) {
				// leer un único archivo elegido por el cliente
				int numero = din.readInt();
				File file = ventana.ficheros.ficheros.get(numero);
				if (file.exists()) {
					dout.write(CreateDataPacket("123".getBytes("UTF8"),
							Serializar_funciones.longToBytes(ventana.ficheros.tamanyos.get(numero))));
					dout.flush();
					dout.write(CreateDataPacket("121".getBytes("UTF8"),
							Configuracion.deserializar().serial.getBytes("UTF8")));
					dout.flush();
					dout.write(CreateDataPacket("122".getBytes("UTF8"), file.getAbsolutePath().getBytes("UTF8")));
					dout.flush();
					sendFileOutput(ventana.ficheros.ficheros.get(numero));
					if (file.isDirectory()) {
						String relativePath = file.getAbsoluteFile().getParentFile().getAbsolutePath().replace("\\",
								"/");
						String absolutePath = file.getAbsolutePath().replace("\\", "/");
						String zipEntryFileName = absolutePath;
						if (absolutePath.startsWith(relativePath)) {
							zipEntryFileName = absolutePath.substring(relativePath.length());
						} else {
							throw new Exception("Invalid Absolute Path");
						}
						dout.write(CreateDataPacket("130".getBytes("UTF8"), zipEntryFileName.getBytes("UTF8")));
						dout.flush();
					}
				}
				close();
			} else {
				// leer todos los archivos
				long tamanyo = ventana.ficheros.tamanyoTodos;
				dout.write(CreateDataPacket("123".getBytes("UTF8"), Serializar_funciones.longToBytes(tamanyo)));
				dout.flush();
				dout.write(
						CreateDataPacket("121".getBytes("UTF8"), Configuracion.deserializar().serial.getBytes("UTF8")));
				dout.flush();
				for (File readFile : ventana.ficheros.ficheros) {
					if (readFile.exists()) {
						dout.write(
								CreateDataPacket("122".getBytes("UTF8"), readFile.getAbsolutePath().getBytes("UTF8")));
						dout.flush();
						sendFileOutput(readFile);
					}
					if (readFile.isDirectory()) {
						String relativePath = readFile.getAbsoluteFile().getParentFile().getAbsolutePath().replace("\\",
								"/");
						String absolutePath = readFile.getAbsolutePath().replace("\\", "/");
						String zipEntryFileName = absolutePath;
						if (absolutePath.startsWith(relativePath)) {
							zipEntryFileName = absolutePath.substring(relativePath.length());
						} else {
							throw new Exception("Invalid Absolute Path");
						}
						dout.write(CreateDataPacket("130".getBytes("UTF8"), zipEntryFileName.getBytes("UTF8")));
						dout.flush();
					}
				}
				close();
			}

		} catch (Exception e) {
			close();
		}
	}

	public void close() {
		try {
			dout.write(CreateDataPacket("128".getBytes("UTF8"), "Close".getBytes("UTF8")));
			dout.flush();
			sc.close();
		} catch (IOException e) {
			System.out.print("");
		}
		conectado = false;
		closeComponents();
	}

	private void closeComponents() {
		if (!(rw == null)) {
			try {
				rw.close();
			} catch (IOException e) {
				System.out.print("");
			}
		}
		if (!(dout == null)) {
			try {
				dout.close();
			} catch (IOException e) {
				System.out.print("");
			}
		}
		if (!(din == null)) {
			try {
				din.close();
			} catch (IOException e) {
				System.out.print("");
			}
		}
	}
}