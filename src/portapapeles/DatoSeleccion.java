package portapapeles;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Esta clase implementa {@link Transferable} y {@link ClipboardOwner} para
 * poder introducir datos al {@link Clipboard} del sistema.
 * 
 * @author: Pavon
 * @version: 22/04/2020
 * @since 1.0
 */

public class DatoSeleccion implements Transferable, ClipboardOwner {
	private Object objeto;
	private DataFlavor dataFlavor;

	/**
	 * Define dataFlavor para introducir texto, imagen o ficheros dentro del
	 * clipboard del sistema {@link Clipboard}.
	 * 
	 * @param objeto para introducir en el portapapeles con una variable
	 *               {@link DataFlavor} que indica el tipo de objeto que es.
	 */

	public DatoSeleccion(Object objeto) {

		this.objeto = objeto;

		try {
			if (objeto.getClass().equals(String.class)) {
				if (Html.hasHTMLTags(objeto)) {
					this.dataFlavor = DataFlavor.allHtmlFlavor;
				} else {
					this.dataFlavor = new DataFlavor("application/x-java-serialized-object; class=java.lang.String");
				}
			} else if (objeto.getClass().toString().equals("class java.util.Arrays$ArrayList")) {
				this.dataFlavor = new DataFlavor("application/x-java-file-list; class=java.util.List");
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { this.dataFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return this.dataFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!this.dataFlavor.equals(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return objeto;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}
}