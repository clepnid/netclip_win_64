package idioma;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Idioma extends Properties {

	private static final long serialVersionUID = 1L;
	private static String ruta = System.getProperty("user.home") + File.separator + "Clepnid"+ File.separator + "Idioma.ini";
	private static String rutaCarpeta = System.getProperty("user.home") + File.separator + "Clepnid";
	
	private void controlarExistencia() {
		File file = new File(crearCarpeta());
		if (!file.exists()) {
			try {
				introducirIdioma("Ingles");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static String crearCarpeta() {
		File ficheroAux = new File(rutaCarpeta);
		if (!ficheroAux.exists()) {
			ficheroAux.mkdir();
		}
		return ficheroAux.getAbsolutePath()+File.separator+"Idioma.ini";
	}
	
	public static void introducirIdioma(String texto) throws IOException {
		String filePath = crearCarpeta();
		new File(filePath).delete();
        FileOutputStream f = new FileOutputStream(filePath, true);
        String lineToAppend = "Idioma = "+texto;    
        byte[] byteArr = lineToAppend.getBytes();
        f.write(byteArr, 0, byteArr.length);
        f.close();
	}
	
	public Idioma() {

		// Modificar si quieres añadir mas idiomas
		// Cambia el nombre de los ficheros o añade los necesarios
		String idioma = null;

		try {
			controlarExistencia();
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream(ruta);
			properties.load(in);

			idioma = properties.getProperty("Idioma");
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (idioma) {
		case "Espanyol":
			getProperties("MessagesBundle_es.properties");
			break;
		case "Ingles":
			getProperties("MessagesBundle_en.properties");
			break;
		case "Ruso":
			getProperties("MessagesBundle_ru.properties");
			break;
		case "Portugues":
			getProperties("MessagesBundle_pt.properties");
			break;
		case "Japones":
			getProperties("MessagesBundle_ja.properties");
			break;
		case "Italiano":
			getProperties("MessagesBundle_it.properties");
			break;
		case "Frances":
			getProperties("MessagesBundle_fr.properties");
			break;
		case "Aleman":
			getProperties("MessagesBundle_de.properties");
			break;
		case "Chino":
			getProperties("MessagesBundle_ch.properties");
			break;
		default:
			getProperties("MessagesBundle_en.properties");
		}

	}

	private void getProperties(String idioma) {
		try {
			this.load(getClass().getResourceAsStream(idioma));
		} catch (IOException ex) {

		}
	}
}
