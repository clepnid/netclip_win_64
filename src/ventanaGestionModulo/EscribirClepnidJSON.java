package ventanaGestionModulo;

import java.io.File;
import java.io.PrintStream;

public class EscribirClepnidJSON {
	public static boolean escribir(String rutaHtml, String escritura) {
		PrintStream stream = null;

		PrintStream console = System.out;
		try {
			stream = new PrintStream(new File(rutaHtml));
			System.setOut(stream);

			System.out.print(escritura);

		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != stream) {
					stream.close();
					System.setOut(console);
				}
			} catch (Exception e2) {
				System.setOut(console);
				return false;
			}

			System.setOut(console);
		}
		return true;
	}
	
}
