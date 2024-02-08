package red;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

/**
 * Contiene funciones para serializar y viceversa objetos.
 * 
 * @author: Pavon
 * @version: 09/05/2020
 * @since 1.0
 */

public class Serializar_funciones {

	/**
	 * Convertir objeto a bytes[], el objeto a serializar tiene que tener
	 * implementado Serializable.
	 * 
	 * @param unObjetoSerializable {@link Object} objeto a serializar
	 * @return Array de bytes.
	 */
	public static byte[] longToBytes(long l) {
		byte[] result = new byte[Long.BYTES];
		for (int i = Long.BYTES - 1; i >= 0; i--) {
			result[i] = (byte) (l & 0xFF);
			l >>= Byte.SIZE;
		}
		return result;
	}

	public static long bytesToLong(final byte[] b) {
		long result = 0;
		for (int i = 0; i < Long.BYTES; i++) {
			result <<= Byte.SIZE;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

	public static byte[] convertirAByteArray(Object unObjetoSerializable) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(unObjetoSerializable);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = bs.toByteArray();
		try {
			bs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * Convertir byte[] al objeto original, el objeto a deserializar tiene que tener
	 * implementado Serializable.
	 * 
	 * @param bytes array de bytes a convertir en el objeto original.
	 * @return Objeto deserializado.
	 */

	public static Object convertirAObjeto(byte[] bytes) {
		Object unObjetoSerializable = new Object();
		ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(bs);
			unObjetoSerializable = (Object) is.readObject();
			is.close();
			bs.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return unObjetoSerializable;
	}

	/**
	 * Convertir una imagen a byte[]
	 * 
	 * @param imagen {@link BufferedImage} imagen a serializar
	 * @return byte[].
	 */

	public static byte[] imageToByte(BufferedImage imagen) {
		Raster hola = imagen.getData();
		hola.getDataBuffer();
		byte[] imageInByte = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// de bufferedImage pasa a buffer de bytes
			ImageIO.write(imagen, "jpg", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageInByte;
	}

	/**
	 * Convertir byte[] a imagen original.
	 * 
	 * @param imagen {@link BufferedImage} imagen a serializar
	 * @return {@link BufferedImage} original.
	 */

	public static BufferedImage ImageFromBytes(byte[] imagen) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imagen);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Devuelve si hay conectividad con el ordenador
	 * 
	 * @param host {@link String} ip a realizar peticion
	 * @param port {@link int} numero de puerto
	 * @param timeout {@link int} tiempo en milisegundos
	 * @return {@link boolean} devuelve true si hay conectividad con el ordenador.
	 */
	
	public static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; 
	    }
	}

}
