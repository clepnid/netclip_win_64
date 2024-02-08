package ventanaGestionModulo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonRead {

	private Ventana ventana;
	private File fichero;
	private String rutaProyecto;

	@SuppressWarnings("unchecked")
	public JsonRead(File fichero, Ventana ventana, String rutaProyecto) {
		this.rutaProyecto = rutaProyecto;
		this.ventana = ventana;
		this.fichero = fichero;
		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(fichero)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray employeeList = (JSONArray) obj;
			System.out.println(employeeList);

			// Iterate over employee array
			employeeList.forEach(emp -> parseEmployeeObject((JSONObject) emp));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void parseEmployeeObject(JSONObject web) {
		if (web.get("Web") != null) {
			JSONObject webObject = (JSONObject) web.get("Web");
			if (webObject.get("TypeModule") != null) {
				String tipoModulo = (String) webObject.get("TypeModule");
				if (tipoModulo.equals("Fichero")) {
					accionModuloFichero(webObject);
				} else {
					if (tipoModulo.equals("Sistema")) {
						accionModuloSistema(webObject);
					} else {
						if (tipoModulo.equals("Vista")) {
							System.out.println("Vista");
							accionModuloVista(webObject);
						} else {
							if (tipoModulo.equals("Icono")) {
								accionModuloIcono(webObject);
							}
						}
					}
				}
			} else {
				accionModuloFichero(webObject);
			}
		}
	}

	public void accionModuloFichero(JSONObject webObject) {
		PanelConfiguracionModuloFichero panel = ventana.getPanelModuloFichero();

		panel.getSeleccionarProyecto().setTexto(rutaProyecto);

		panel.getTitulo().setTexto((String) webObject.get("Title"));

		panel.getPanelFormato().addItems(JSONArraytoArrayString((JSONArray) webObject.get("Extensions")));

		panel.getTextoBoton().setTexto((String) webObject.get("BotonText"));

		panel.getReemplazarTexto().setTexto((String) webObject.get("HtmlBodyReplace"));

		if (((String) webObject.get("Group")).equals("si")) {
			panel.getBtnGrupo().setSelection(true);
			panel.setVariosArchivos(true);
		} else {
			panel.getBtnGrupo().setSelection(false);
			panel.setVariosArchivos(false);
		}

		panel.getSeleccionarIndex().setTexto((String) webObject.get("Html"));
		panel.getRutaHttp().setTexto((String) webObject.get("rutaHttp"));
		panel.getSeleccionarImagen().setTexto((String) webObject.get("rutaImagen"));

		ventana.cambiarVistaModeloFichero();
	}

	public void accionModuloVista(JSONObject webObject) {
		PanelConfiguracionModuloVista panel = ventana.getPanelModuloVista();

		panel.getSeleccionarProyecto().setTexto(rutaProyecto);

		panel.getTitulo().setTexto((String) webObject.get("Title"));

		panel.getTextoBoton().setTexto((String) webObject.get("BotonText"));

		panel.getSeleccionarIndex().setTexto((String) webObject.get("Html"));
		panel.getRutaHttp().setTexto((String) webObject.get("rutaHttp"));
		panel.getSeleccionarImagen().setTexto((String) webObject.get("rutaImagen"));

		ventana.cambiarVistaModeloVista();
	}

	public void accionModuloIcono(JSONObject webObject) {
		PanelConfiguracionModuloIcono panel = ventana.getPanelModuloIcono();
		panel.getSeleccionarProyecto().setTexto(rutaProyecto);

		panel.getTitulo().setTexto((String) webObject.get("Title"));

		panel.getTextoBoton().setTexto((String) webObject.get("BotonText"));

		panel.getRutaHttp().setTexto((String) webObject.get("rutaHttp"));
		panel.getSeleccionarImagen().setTexto((String) webObject.get("rutaImagen"));

		ventana.cambiarVistaModeloIcono();
	}

	public void accionModuloSistema(JSONObject webObject) {
		PanelConfiguracionModuloSistema panel = ventana.getPanelModuloSistema();
		panel.getSeleccionarProyecto().setTexto(rutaProyecto);
		panel.getTitulo().setTexto((String) webObject.get("Title"));
		panel.getTextoComando().setTexto((String) webObject.get("Comando"));
		panel.getListaRutas().setFormatos(JSONArraytoRutas((JSONArray) webObject.get("ListaRutas")));
		panel.getListaRutas().ventana.cambiarVistaModeloSistema();
	}

	private ArrayList<String> JSONArraytoArrayString(JSONArray jsonArray) {
		ArrayList<String> arrayString = new ArrayList<String>();
		for (Object object : jsonArray) {
			arrayString.add((String) object);
		}

		return arrayString;
	}

	private ArrayList<String[]> JSONArraytoRutas(JSONArray jsonArray) {
		ArrayList<String[]> arrayString = new ArrayList<String[]>();
		for (Object object : jsonArray) {
			String[] arrayAux = { (String) ((JSONObject) object).get("RutaEjecutable"),
					(String) ((JSONObject) object).get("RutaClepnid") };
			arrayString.add(arrayAux);
		}

		return arrayString;
	}

	public File getFichero() {
		return fichero;
	}

	public void setFichero(File fichero) {
		this.fichero = fichero;
	}
}
