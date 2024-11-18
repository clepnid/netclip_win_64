package ventanaGestionModulo;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelIntroduccionFicheroIndexHtml extends Composite {

	private Ventana ventana;
	private Text texto;
	private PanelConfiguracionModuloFichero panelConfiguracionModuloFichero;
	private PanelConfiguracionModuloIcono panelConfiguracionModuloIcono;
	private PanelConfiguracionModuloVista panelConfiguracionModuloVista;
	private PanelSeleccionCarpeta seleccionPoyecto;
	private String rutaPadre;

	public PanelIntroduccionFicheroIndexHtml(PanelConfiguracionModuloFichero panel, int style, String labelText,
			int width, Ventana ventana) {
		super(panel, style);
		this.ventana = ventana;
		this.panelConfiguracionModuloFichero = panel;
		this.seleccionPoyecto = panel.getSeleccionarProyecto();
		inicializar(labelText, width);
	}

	public PanelIntroduccionFicheroIndexHtml(PanelConfiguracionModuloIcono panel, int style, String labelText,
			int width, Ventana ventana) {
		super(panel, style);
		this.ventana = ventana;
		this.panelConfiguracionModuloIcono = panel;
		this.seleccionPoyecto = panel.getSeleccionarProyecto();
		inicializar(labelText, width);
	}

	public PanelIntroduccionFicheroIndexHtml(PanelConfiguracionModuloVista panel, int style, String labelText,
			int width, Ventana ventana) {
		super(panel, style);
		this.ventana = ventana;
		this.panelConfiguracionModuloVista = panel;
		this.seleccionPoyecto = panel.getSeleccionarProyecto();
		inicializar(labelText, width);
	}

	public void inicializar(String labelText, int width) {
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(3, false));
		Label label = new Label(this, SWT.LEFT);
		label.setText(labelText);
		label.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData();
		gd.widthHint = width;
		label.setLayoutData(gd);

		GridData dataText = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		texto = new Text(this, SWT.BORDER | SWT.LEFT);
		texto.setLayoutData(dataText);
		texto.setEditable(false);

		GridData dataBtn = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		Button btn = new Button(this, SWT.CENTER);
		btn.setText("Seleccionar");
		btn.setLayoutData(dataBtn);
		btn.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (comprobarCarpetaProyecto()) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String[] extensiones = { "*.html", "*.*" };
					dialog.setFilterPath(seleccionPoyecto.getTextoIntroducido());
					dialog.setFilterExtensions(extensiones);
					final String file = dialog.open();
					if (file != null) {
						File fichero = new File(file);
						if (comprobarIndexHtml(fichero)) {
							rellenarCamposPanel(fichero.getName());
						} else {
							MessageDialog.openError(ventana.getShell(), "Error",
									"Debe seleccionar un fichero html dentro de la carpeta proyecto");
						}
					}
				} else {
					MessageDialog.openError(ventana.getShell(), "Error",
							"Debe introducir primero la carpeta de proyecto");
				}

			}
		});
	}

	public boolean comprobarIndexHtml(File fichero) {
		return fichero.getAbsolutePath().contains(this.seleccionPoyecto.getTextoIntroducido());
	}

	public boolean comprobarCarpetaProyecto() {
		if (panelConfiguracionModuloFichero != null) {
			if (!panelConfiguracionModuloFichero.getSeleccionarProyecto().getTextoIntroducido().equals("")) {
				return true;
			}
		} else {
			if (panelConfiguracionModuloIcono != null) {
				if (!panelConfiguracionModuloIcono.getSeleccionarProyecto().getTextoIntroducido().equals("")) {
					return true;
				}
			} else {
				if (panelConfiguracionModuloVista != null) {
					if (!panelConfiguracionModuloVista.getSeleccionarProyecto().getTextoIntroducido().equals("")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void rellenarCamposPanel(String nombreIndex) {
		if (panelConfiguracionModuloFichero != null) {
			File fichero = rellenarCamposPanel(panelConfiguracionModuloFichero,
					new File(panelConfiguracionModuloFichero.getSeleccionarProyecto().getTextoIntroducido()),
					nombreIndex);
			rellenarTextoIndexHtml(panelConfiguracionModuloFichero, fichero);

		} else {
			if (panelConfiguracionModuloIcono != null) {
				File fichero = rellenarCamposPanel(panelConfiguracionModuloIcono,
						new File(panelConfiguracionModuloIcono.getSeleccionarProyecto().getTextoIntroducido()),
						nombreIndex);
				rellenarTextoIndexHtml(panelConfiguracionModuloIcono, fichero);
			} else {
				if (panelConfiguracionModuloVista != null) {
					File fichero = rellenarCamposPanel(panelConfiguracionModuloVista,
							new File(panelConfiguracionModuloVista.getSeleccionarProyecto().getTextoIntroducido()),
							nombreIndex);
					rellenarTextoIndexHtml(panelConfiguracionModuloVista, fichero);
				}
			}
		}
	}

	public void rellenarTextoIndexHtml(PanelConfiguracionModuloFichero panel, File fichero) {
		String rutaProyecto = new File(panel.getSeleccionarProyecto().getTextoIntroducido()).getParentFile().getAbsolutePath();
		String rutaIndice = fichero.getAbsolutePath();
		this.rutaPadre = fichero.getParentFile().getAbsolutePath();
		String rutaAux = getStringSobrante(rutaProyecto, rutaIndice);
		String rutaHttp = getStringNoContenido(rutaAux, fichero.getName()).replace("\\", "/");
		if (rutaHttp.endsWith("/")) {
			rutaHttp =rutaHttp.substring(0, rutaHttp.length()-1);
		}
		panel.getRutaHttp().setTexto(rutaHttp);
		if (rutaHttp.split("/").length >2) {
			texto.setText("."+fichero.getName());
		}else {
			texto.setText(fichero.getName());
		}

	}

	public void rellenarTextoIndexHtml(PanelConfiguracionModuloIcono panel, File fichero) {
		String rutaProyecto = new File(panel.getSeleccionarProyecto().getTextoIntroducido()).getParentFile().getAbsolutePath();
		String rutaIndice = fichero.getAbsolutePath();
		this.rutaPadre = fichero.getParentFile().getAbsolutePath();
		String rutaAux = getStringSobrante(rutaProyecto, rutaIndice);
		String rutaHttp = getStringNoContenido(rutaAux, fichero.getName()).replace("\\", "/");
		if (rutaHttp.endsWith("/")) {
			rutaHttp =rutaHttp.substring(0, rutaHttp.length()-1);
		}
		panel.getRutaHttp().setTexto(rutaHttp);
		if (rutaHttp.split("/").length >2) {
			texto.setText("."+fichero.getName());
		}else {
			texto.setText(fichero.getName());
		}
	}

	public void rellenarTextoIndexHtml(PanelConfiguracionModuloVista panel, File fichero) {
		String rutaProyecto = new File(panel.getSeleccionarProyecto().getTextoIntroducido()).getParentFile().getAbsolutePath();
		String rutaIndice = fichero.getAbsolutePath();
		this.rutaPadre = fichero.getParentFile().getAbsolutePath();
		String rutaAux = getStringSobrante(rutaProyecto, rutaIndice);
		String rutaHttp = getStringNoContenido(rutaAux, fichero.getName()).replace("\\", "/");
		if (rutaHttp.endsWith("/")) {
			rutaHttp =rutaHttp.substring(0, rutaHttp.length()-1);
		}
		panel.getRutaHttp().setTexto(rutaHttp);
		if (rutaHttp.split("/").length >2) {
			texto.setText("."+fichero.getName());
		}else {
			texto.setText(fichero.getName());
		}
	}

	public String getStringSobrante(String stringCorto, String stringLargo) {
		String rutaAux = "";
		for (int i = stringCorto.length(); i < stringLargo.length(); i++) {
			rutaAux = rutaAux + stringLargo.charAt(i);
		}
		return rutaAux;
	}

	public String getStringNoContenido(String parrafo, String palabra) {
		String rutaAux = "";
		int contador = 0;
		for (int i = 0; i < parrafo.length(); i++) {
			if (parrafo.charAt(i) == palabra.charAt(contador)) {
				contador++;
				if (contador == palabra.length()) {
					break;
				}
			} else {
				rutaAux = rutaAux + parrafo.charAt(i);
			}
		}
		return rutaAux;
	}

	public File rellenarCamposPanel(PanelConfiguracionModuloFichero panel, File file, String nombreIndex) {
		File[] ficheros = file.listFiles();

		if (ficheros != null) {
			for (int x = 0; x < ficheros.length; x++) {
				if (ficheros[x].getName().equals(nombreIndex)) {
					return ficheros[x];
				}
				if (ficheros[x].isDirectory()) {
					File ficheroAux = rellenarCamposPanel(panel, ficheros[x], nombreIndex);
					if (ficheroAux != null) {
						return ficheroAux;
					}
				}
			}
		}
		return null;
	}

	public File rellenarCamposPanel(PanelConfiguracionModuloIcono panel, File file, String nombreIndex) {
		File[] ficheros = file.listFiles();

		if (ficheros != null) {
			for (int x = 0; x < ficheros.length; x++) {
				if (ficheros[x].getName().equals(nombreIndex)) {
					return ficheros[x];
				}
				if (ficheros[x].isDirectory()) {
					File ficheroAux = rellenarCamposPanel(panel, ficheros[x], nombreIndex);
					if (ficheroAux != null) {
						return ficheroAux;
					}
				}
			}
		}
		return null;
	}

	public File rellenarCamposPanel(PanelConfiguracionModuloVista panel, File file, String nombreIndex) {
		File[] ficheros = file.listFiles();

		if (ficheros != null) {
			for (int x = 0; x < ficheros.length; x++) {
				if (ficheros[x].getName().equals(nombreIndex)) {
					return ficheros[x];
				}
				if (ficheros[x].isDirectory()) {
					File ficheroAux = rellenarCamposPanel(panel, ficheros[x], nombreIndex);
					if (ficheroAux != null) {
						return ficheroAux;
					}
				}
			}
		}
		return null;
	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	// metodo importante que devuelve la lista de formatos añadidas a la tabla por
	// el usuario.

	public String getTextoIntroducido() {
		return texto.getText();
	}

	public String getRutaPadre() {
		return rutaPadre;
	}
	
	public void setTexto(String text) {
		this.texto.setText(text);
	}

}
