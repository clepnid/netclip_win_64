package ventanaGestionModulo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class PanelConfiguracionModuloVista extends Composite {

	private Ventana ventana;
	private PanelSeleccionCarpeta seleccionarProyecto;
	private PanelIntroduccionFicheroImagen seleccionarImagen;
	private PanelIntroduccionFicheroIndexHtml seleccionarIndex;
	private PanelIntroduccionTextos titulo, textoBoton, rutaHttp;
	private Button btnCrear;

	public PanelConfiguracionModuloVista(Composite parent, int style, Ventana ventana) {
		super(parent, style);
		this.setVentana(ventana);
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(1, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 400;
		data.verticalIndent = 10;
		GridData dataTexto1 = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dataTexto1.widthHint = 300;
		seleccionarProyecto = new PanelSeleccionCarpeta(this, 0, "Seleccionar Proyecto:", 200, ventana);
		seleccionarProyecto.setLayoutData(dataTexto1);
		titulo = new PanelIntroduccionTextos(this, 0, "Titulo:", 200);
		titulo.setLayoutData(dataTexto1);
		textoBoton = new PanelIntroduccionTextos(this, 0, "Texto Boton Ventana: ", 200);
		textoBoton.setLayoutData(dataTexto1);
		rutaHttp = new PanelIntroduccionTextos(this, 0, "Ruta Http:", 200);
		rutaHttp.setLayoutData(dataTexto1);
		rutaHttp.setTextoEnable(false);
		seleccionarIndex = new PanelIntroduccionFicheroIndexHtml(this, 0, "Seleccionar Index.html:", 200, ventana);
		seleccionarIndex.setLayoutData(dataTexto1);
		seleccionarImagen = new PanelIntroduccionFicheroImagen(this, 0, "Seleccionar imagen:", 200, ventana);
		seleccionarImagen.setLayoutData(dataTexto1);

		btnCrear = new Button(this, SWT.CENTER);
		btnCrear.setText("Comenzar");
		btnCrear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (ComprobarInputs()) {

					String rutaHttpAux = rutaHttp.getTextoIntroducido();
					if (!rutaHttpAux.startsWith("/")) {
						rutaHttpAux = "/" + rutaHttpAux;
					}
					String rutaImagen = seleccionarImagen.getTextoIntroducido();
					if (!rutaImagen.startsWith("/")) {
						rutaImagen = "/" + rutaImagen;
					}
					String escritura = "[\n\t{\n\t\t" + "\"Web\": {\n" 
							+ "\t\t\t\"TypeModule\": \"Vista\",\n"
							+ "\t\t\t\"Title\": \"" + titulo.getTextoIntroducido() + "\",\n"
							+ "\t\t\t\"BotonText\": \"" + textoBoton.getTextoIntroducido() + "\",\n"
							+ "\t\t\t\"Html\": \"" + seleccionarIndex.getTextoIntroducido() + "\",\n"
							+ "\t\t\t\"rutaHttp\": \"" + rutaHttpAux + "\",\n" + "\t\t\t\"rutaImagen\": \"" + rutaImagen
							+ "\"\n" + "\t\t}\n" + "\t}\n" + "]" + "";
					// comprobamos si el fichero clepnid.json existe
					if (seleccionarIndex.getRutaPadre() != null) {
						String stringRutaHtml = seleccionarIndex.getRutaPadre() + File.separatorChar+ "clepnid.json";
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
							if (EscribirClepnidJSON.escribir(seleccionarIndex.getRutaPadre() + File.separatorChar+ "clepnid.json",
									escritura)) {
								MessageDialog.openInformation(ventana.getShell(), "Exito",
										"Se ha creado " + stringRutaHtml + " exitosamente");
								System.out.println("clepnid.json:\n" + escritura);
							} else {
								MessageDialog.openError(ventana.getShell(), "Error",
										"No se ha podido crear " + stringRutaHtml + " exitosamente");
							}
						}
					} else {
						String stringRutaHtml = seleccionarProyecto.getTextoIntroducido() + File.separatorChar
								+ "clepnid.json";
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
							if (EscribirClepnidJSON.escribir(
									seleccionarIndex.getRutaPadre() + File.separatorChar + "clepnid.json", escritura)) {
								MessageDialog.openInformation(ventana.getShell(), "Exito",
										"Se ha creado " + stringRutaHtml + " exitosamente");
								System.out.println("clepnid.json:\n" + escritura);
							} else {
								MessageDialog.openError(ventana.getShell(), "Error",
										"No se ha podido crear " + stringRutaHtml + " exitosamente");
							}
						}
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
		if (textoBoton.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha introducido el texto boton");
			return false;
		}

		if (rutaHttp.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha introducido la ruta http");
			return false;
		}
		if (seleccionarImagen.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha seleccionado la imagen");
			return false;
		}
		if (seleccionarIndex.getTextoIntroducido().equals("")) {
			MessageDialog.openError(ventana.getShell(), "Error", "No se ha seleccionado el Index.html");
			return false;
		}

		int comprobarFicheros = isSubDirectory(seleccionarProyecto.getTextoIntroducido().replace("\\", "/"),
				seleccionarIndex.getTextoIntroducido().replace("\\", "/"));

		if (comprobarFicheros == 1) {
			MessageDialog.openError(ventana.getShell(), "Error",
					"El fichero index.html no se encuentra dentro de la carpeta Proyecto");
			return false;
		}

		if (!rutaCorrecta(rutaHttp.getTextoIntroducido())) {
			MessageDialog.openError(ventana.getShell(), "Error", "La ruta http introducida no es correcta");
			return false;
		}
		int comprobarImagen = isSubDirectory(seleccionarProyecto.getTextoIntroducido().replace("\\", "/"),
				seleccionarImagen.getTextoIntroducido().replace("\\", "/"));
		if (comprobarImagen == 1 || comprobarImagen == -1) {
			MessageDialog.openError(ventana.getShell(), "Error", "La imagen seleccionada no existe");
			return false;
		}
		String imageName = seleccionarImagen.getTextoIntroducido();
		String[] splitImageName = imageName.split("\\.");
		if (splitImageName.length < 2) {
			MessageDialog.openError(ventana.getShell(), "Error",
					Arrays.toString(splitImageName) + "La imagen seleccionada no tiene un formato válido");
			return false;
		}
		if (!ArraysStringFormatos.seEncuentraEnListaFormatos(ArraysStringFormatos.IMAGEN,
				splitImageName[splitImageName.length - 1].toUpperCase())) {
			MessageDialog.openError(ventana.getShell(), "Error", "La imagen seleccionada no tiene un formato válido");
			return false;
		}

		if (comprobarFicheros == -1) {
			return false;
		}

		return true;
	}

	public static boolean rutaCorrecta(String ruta) {
		if (ruta.startsWith("/")) {
			String rutaAux = ruta.substring(1);
			String[] rutaSplit = rutaAux.split("/");
			if (rutaSplit.length < 2) {
				return rutaAux.equals(http.Http.encodeURIcomponent(rutaAux));
			} else {
				for (String string : rutaSplit) {
					if (!string.equals(http.Http.encodeURIcomponent(string))) {
						return false;
					}
				}
				return true;
			}
		} else {
			String[] rutaSplit = ruta.split("/");
			if (rutaSplit.length < 2) {
				return ruta.equals(http.Http.encodeURIcomponent(ruta));
			} else {
				for (String string : rutaSplit) {
					if (!string.equals(http.Http.encodeURIcomponent(string))) {
						return false;
					}
				}
				return true;
			}
		}
	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	// retorna 1 si no se ha encontrado, 0 si se ha encontrado y -1 si no existe
	// alguno de los ficheros

	public int isSubDirectory(String baseString, String childString) {
		File base;
		try {
			base = new File(baseString);
			base = base.getCanonicalFile();
		} catch (IOException e) {
			MessageDialog.openError(ventana.getShell(), "Error", "La carpeta Proyecto introducida no existe");
			return -1;
		}

		if (childString.startsWith(".")) {
			String childPathStringAux = childString.substring(1);

			String[] childStringAuxsplit = rutaHttp.getTextoIntroducido().split("/");
			String childStringPosible = "";
			if (rutaHttp.getTextoIntroducido().startsWith("/")) {
				for (int i = 2; i < childStringAuxsplit.length; i++) {
					childStringPosible += childStringPosible + File.separatorChar + childStringAuxsplit[i];
				}
			} else {
				for (int i = 1; i < childStringAuxsplit.length; i++) {
					childStringPosible += childStringPosible + File.separatorChar + childStringAuxsplit[i];
				}
			}

			childStringPosible = childStringPosible + childPathStringAux;
			System.out.println((baseString + childStringPosible).replace("/", String.valueOf(File.separatorChar)));
			if (new File((baseString + childStringPosible).replace("/", String.valueOf(File.separatorChar))).exists()) {
				return 0;
			}
			return 1;

		} else {
			if (new File(childString).exists()) {
				return 0;
			}
			if (childString.startsWith(baseString)) {
				return 0;
			}
			System.out.println((baseString + String.valueOf(File.separatorChar) + childString).replace("/",
					String.valueOf(File.separatorChar)));
			if (new File((baseString + String.valueOf(File.separatorChar) + childString).replace("/",
					String.valueOf(File.separatorChar))).exists()) {
				return 0;
			}
			System.out.println((baseString + childString).replace("/", String.valueOf(File.separatorChar)));

			if (new File((baseString + childString).replace("/", String.valueOf(File.separatorChar))).exists()) {
				return 0;
			}
			String childStringAux = childString.replace("/", String.valueOf(File.separatorChar));
			String[] childStringAuxsplit = childStringAux.split("/");
			String childStringPosible = "";
			for (int i = 1; i < childStringAuxsplit.length; i++) {
				childStringPosible += childStringPosible + File.separatorChar + childStringAuxsplit[i];
			}
			if (new File((baseString + childStringPosible).replace("/", String.valueOf(File.separatorChar))).exists()) {
				return 0;
			}
			return 1;

		}
	}

	public PanelSeleccionCarpeta getSeleccionarProyecto() {
		return seleccionarProyecto;
	}

	public void setSeleccionarProyecto(PanelSeleccionCarpeta seleccionarProyecto) {
		this.seleccionarProyecto = seleccionarProyecto;
	}

	public PanelIntroduccionFicheroIndexHtml getSeleccionarIndex() {
		return seleccionarIndex;
	}

	public void setSeleccionarIndex(PanelIntroduccionFicheroIndexHtml seleccionarIndex) {
		this.seleccionarIndex = seleccionarIndex;
	}

	public PanelIntroduccionFicheroImagen getSeleccionarImagen() {
		return seleccionarImagen;
	}

	public void setSeleccionarImagen(PanelIntroduccionFicheroImagen seleccionarImagen) {
		this.seleccionarImagen = seleccionarImagen;
	}

	public PanelIntroduccionTextos getTitulo() {
		return titulo;
	}

	public void setTitulo(PanelIntroduccionTextos titulo) {
		this.titulo = titulo;
	}

	public PanelIntroduccionTextos getTextoBoton() {
		return textoBoton;
	}

	public void setTextoBoton(PanelIntroduccionTextos textoBoton) {
		this.textoBoton = textoBoton;
	}

	public PanelIntroduccionTextos getRutaHttp() {
		return rutaHttp;
	}

	public void setRutaHttp(PanelIntroduccionTextos rutaHttp) {
		this.rutaHttp = rutaHttp;
	}

	public Button getBtnCrear() {
		return btnCrear;
	}

	public void setBtnCrear(Button btnCrear) {
		this.btnCrear = btnCrear;
	}

}