package ventanaGestionModulo;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class PanelConfiguracionModuloSistema extends Composite {

	private Ventana ventana;
	private PanelSeleccionCarpeta seleccionarProyecto;
	private PanelIntroduccionTextos textoComando, titulo;
	private PanelIntroduccionTextoListaRutasHttp listaRutas;
	private Button btnCrear;

	public PanelConfiguracionModuloSistema(Composite parent, int style, Ventana ventana) {
		super(parent, style);
		this.setVentana(ventana);
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(1, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 400;
		data.verticalIndent = 10;
		GridData dataTexto1 = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dataTexto1.widthHint = 300;
		seleccionarProyecto = new PanelSeleccionCarpeta(this, 0, "Seleccionar Projecto:", 200, ventana);
		seleccionarProyecto.setLayoutData(dataTexto1);
		titulo = new PanelIntroduccionTextos(this, 0, "Titulo:", 200);
		titulo.setLayoutData(dataTexto1);
		textoComando = new PanelIntroduccionTextos(this, 0, "Introducir Comando Ejecucion:", 200);
		textoComando.setLayoutData(dataTexto1);

		listaRutas = new PanelIntroduccionTextoListaRutasHttp(this, style, ventana, "Introducir Rutas:");
		listaRutas.setLayoutData(data);

		btnCrear = new Button(this, SWT.CENTER);
		btnCrear.setText("Comenzar");
		btnCrear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (ComprobarInputs()) {

					String escritura = "[\n\t{\n\t\t" + "\"Web\": {\n" + "\t\t\t\"TypeModule\": \"Sistema\",\n"
							+ "\t\t\t\"Title\": \"" + titulo.getTextoIntroducido() + "\",\n" 
							+ "\t\t\t\"Comando\": \"" + textoComando.getTextoIntroducido() + "\",\n"
							+ "\t\t\t\"ListaRutas\": " + listaRutas.getRutasSeleccionadosJSON() + "\n"
							+ "\t\t}\n" + "\t}\n" + "]"
							+ "";
					// comprobamos si el fichero clepnid.json existe
					if (!seleccionarProyecto.getTextoIntroducido().equals("")) {
						String stringRutaHtml = seleccionarProyecto.getTextoIntroducido() + File.separatorChar+ "clepnid.json";
						File rutaHtml = new File(stringRutaHtml);
						if (rutaHtml.exists()) {
							if (MessageDialog.openQuestion(ventana.getShell(), "¿Sobreescribir?",
									"Desea sobreescribir el fichero " + stringRutaHtml)) {
								if (EscribirClepnidJSON.escribir(stringRutaHtml, escritura)) {
									MessageDialog.openInformation(ventana.getShell(), "Exito",
											"Se ha creado " + stringRutaHtml + " exitosamente");
									System.out.println("clepnid.json:\n" + escritura);
								} else {
									MessageDialog.openError(ventana.getShell(), "Error",
											"No se ha podido crear " + stringRutaHtml + " exitosamente");
								}
							}
						} else {
							// si no existe crearemos uno en la ruta padre de index.html
							if (EscribirClepnidJSON
									.escribir(seleccionarProyecto.getTextoIntroducido() + File.separatorChar+ "clepnid.json", escritura)) {
								MessageDialog.openInformation(ventana.getShell(), "Exito",
										"Se ha creado " + stringRutaHtml + " exitosamente");
								System.out.println("clepnid.json:\n" + escritura);
							} else {
								MessageDialog.openError(ventana.getShell(), "Error",
										"No se ha podido crear " + stringRutaHtml + " exitosamente");
							}
						}
					} else {
						MessageDialog.openError(ventana.getShell(), "Error",
								"Debe introducir una carpeta de proyecto.");
					}

				}
			}
		});

	}

	public boolean ComprobarInputs() {

		if (seleccionarProyecto.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha seleccionado el Proyecto");
			return false;
		}
		if (titulo.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha introducido el título");
			return false;
		}
		if (textoComando.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha introducido el texto boton");
			return false;
		}
		if (listaRutas.vacio()) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha introducido ninguna traduccion de url");
			return false;
		}

		return true;
	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	public PanelSeleccionCarpeta getSeleccionarProyecto() {
		return seleccionarProyecto;
	}

	public void setSeleccionarProyecto(PanelSeleccionCarpeta seleccionarProyecto) {
		this.seleccionarProyecto = seleccionarProyecto;
	}

	public PanelIntroduccionTextos getTextoComando() {
		return textoComando;
	}

	public void setTextoComando(PanelIntroduccionTextos textoComando) {
		this.textoComando = textoComando;
	}

	public PanelIntroduccionTextos getTitulo() {
		return titulo;
	}

	public void setTitulo(PanelIntroduccionTextos titulo) {
		this.titulo = titulo;
	}

	public PanelIntroduccionTextoListaRutasHttp getListaRutas() {
		return listaRutas;
	}

	public void setListaRutas(PanelIntroduccionTextoListaRutasHttp listaRutas) {
		this.listaRutas = listaRutas;
	}

	public Button getBtnCrear() {
		return btnCrear;
	}

	public void setBtnCrear(Button btnCrear) {
		this.btnCrear = btnCrear;
	}

}