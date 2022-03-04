package portapapeles;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
	private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	private static Pattern pattern = Pattern.compile(HTML_PATTERN);

	public static boolean hasHTMLTags(Object text) {
		if (text.getClass().equals(String.class)) {
			Matcher matcher = pattern.matcher((CharSequence) text);
			return matcher.find();
		} else {
			return false;
		}

	}

	public static void main(String[] args) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = c.getContents(null);

		try {
			Object kdd = cc.getTransferData(DataFlavor.allHtmlFlavor);
			System.out.println(kdd.toString());
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (DataFlavor stddring : c.getAvailableDataFlavors()) {
			if (stddring.equals(DataFlavor.allHtmlFlavor)) {
				try {
					Object hola = c.getData(stddring);
					System.out.println(hasHTMLTags(hola));
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
