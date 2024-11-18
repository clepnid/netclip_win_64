package historial;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import portapapeles.Ficheros;
import red.historial.ClienteComando;
import red.multicast.MulticastControl;
import ventana.Ventana;

public class PanelHistoriaFichero extends PanelHistoria {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	private StyledText lblNombre;
	private ByteBuffer buffer;

	public PanelHistoriaFichero(VentanaListaHistorial ventanaListaHistorial, Composite parent, ScrolledComposite c2, int style, Historial historial,
			MulticastControl controlMulticast) {
		super(ventanaListaHistorial, parent, c2, style, historial);
		lblFecha.setText(historial.getFecha());
		if (new File(historial.rutaFicheroCliente).isDirectory()) {
			lblImagen.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/carpeta.gif".replace("\\", "/")));
		} else {
			lblImagen.setImage(getCheckedImage(parent.getDisplay(),
					Ficheros.rutaImagen(Ficheros.tipoFichero(new File(historial.rutaFicheroCliente).getAbsolutePath()))));

		}
		lblNombre = new StyledText(panAtributos, SWT.NONE);
		lblNombre.setEditable(false);
		lblNombre.setEnabled(false);
		lblNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNombre.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		buffer = StandardCharsets.UTF_8.encode(historial.nombreFichero);
		String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
		lblNombre.setText(utf8EncodedString);

		if (historial.getHistorialFiltrado(controlMulticast.escaner).equals(Historial.Filtrado.Carpeta)) {
			// mostrar en carpeta
			Button btnMostrarCarpeta = new Button(panBoton, SWT.NONE);
			btnMostrarCarpeta.setToolTipText("Mostrar en carpeta");
			btnMostrarCarpeta.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1));
			btnMostrarCarpeta.setText(Ventana.idioma.getProperty("historial_btnMostrarCarpeta"));
			btnMostrarCarpeta.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					try {
						Desktop.getDesktop()
								.open(new File(historial.rutaFicheroCliente).getParentFile().getAbsoluteFile());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

		} else if (historial.getHistorialFiltrado(controlMulticast.escaner).equals(Historial.Filtrado.NoExiste)) {

			// No existe fichero en red.
			Label lblNoExiste = new Label(panAtributos, SWT.NONE);
			lblNoExiste.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblNoExiste.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
			lblNoExiste.setText(Ventana.idioma.getProperty("historial_No_se_encuentra_fichero")+".");

		} else {

			// volver a descargar
			Button btnVolverObtener = new Button(panBoton, SWT.NONE);
			btnVolverObtener.setToolTipText(Ventana.idioma.getProperty("historial_Volver_descargar_fichero"));
			btnVolverObtener.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1));
			btnVolverObtener.setText(Ventana.idioma.getProperty("historial_Volver_obtener"));
			// cuando le des al boton creara un nuevo historial en lista historial fichero y
			// convertira esta panel a mostrar carpeta
			btnVolverObtener.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
					String ruta = dialog.open();
					if (ruta != null) {
						red.historial.ClienteComando cliente = new ClienteComando(controlMulticast, historial);
						cliente.ruta = ruta;
						cliente.start();
						try {
							cliente.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						parent.getDisplay().asyncExec(new Runnable() {
							public void run() {

								btnVolverObtener.setVisible(false);
								ListaHistorial.deleteHistorial(historial);
								Button btnMostrarCarpeta = new Button(panBoton, SWT.NONE);
								btnMostrarCarpeta.setToolTipText(Ventana.idioma.getProperty("historial_btnMostrarCarpeta"));
								btnMostrarCarpeta.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1));
								btnMostrarCarpeta.setText(Ventana.idioma.getProperty("historial_btnMostrarCarpeta"));
								btnMostrarCarpeta.addListener(SWT.Selection, new Listener() {
									public void handleEvent(Event e) {
										try {
											Desktop.getDesktop().open(new File(historial.rutaFicheroCliente)
													.getParentFile().getAbsoluteFile());
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
								});
								btnMostrarCarpeta.pack();
								getParent().pack();

							}
						});
					}
				}
			});

		}

	}

	public String getText() {
		return lblNombre.getText();
	}

	public void setTextEstilo(StyleRange style) {
		lblNombre.setStyleRange(style);
	}

	public void removeStyleRange() {
		lblNombre.setStyleRange(null);
		lblFecha.setStyleRange(null);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
