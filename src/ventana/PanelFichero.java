package ventana;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import http.JsonModulosFicheros;
import http.ConfiguracionJson;
import http.Http;
import portapapeles.Ficheros;
import red.multicast.MulticastControl;

/**
 * Clase que extiende de {@link Composite} para mostrar los datos de un array de
 * ficheros por {@link Ventana}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelFichero extends PanelContenido {

	private Composite panImagen;
	private Composite panBoton;
	private Button btnGuardar, btnEnviar, btnQR;
	private Composite panAtributos;
	private TableCombo desplegable;
	private Label lblImagen;
	private Label lblNombre;
	public Display display;
	public Shell shell;
	public Composite parent;
	public int numero;
	private Boolean abierto;
	public Boolean mostrarBotonEnviar;

	/**
	 * Constructor que define el panel referente a un fichero a mostrar por
	 * {@link Ventana}
	 * 
	 * @param parent      {@link Composite} panel padre para {@link PanelContenido}.
	 * @param ventana     {@link Ventana} para mostrar por pantalla el componente.
	 * @param style       numero de referencia de la apariencia que va a tener el
	 *                    componente.
	 * @param nombre      {@link String} nombre del fichero a mostrar.
	 * @param formato     {@link String} formato del fichero a mostrar.
	 * @param pesoFichero {@link String} con el peso del fichero a mostrar.
	 * @param ruta        {@link String} ruta del fichero a mostrar.
	 * @param numero      numeración del componente a mostrar por pantalla.
	 */

	public PanelFichero(Composite parent, int style, String nombre, String pesoFichero, String formato,
			String ruta, int numero, ArrayList<ConfiguracionJson> modulos) {
		super(parent, style);
		this.setNombre(nombre);
		this.parent = parent;
		mostrarBotonEnviar = false;
		abierto = false;
		this.numero = numero;
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(3, false));
		gd_panObjeto = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_panObjeto.heightHint = 146;
		setLayoutData(gd_panObjeto);

		panImagen = new Composite(this, SWT.NONE);
		panImagen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panImagen.setLayout(new GridLayout(1, false));
		GridData gd_panImagen = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_panImagen.heightHint = 182;
		gd_panImagen.widthHint = 133;
		panImagen.setLayoutData(gd_panImagen);

		lblImagen = new Label(panImagen, SWT.NONE);
		lblImagen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblImagen.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblImagen.setImage(getCheckedImage(display, ruta.replace("\\", "/")));

		panAtributos = new Composite(this, SWT.NONE);
		panAtributos.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panAtributos.setLayout(new GridLayout(1, false));
		panAtributos.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblNombre = new Label(panAtributos, SWT.NONE);
		lblNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNombre.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblNombre.setText(Ventana.idioma.getProperty("fichero_nombre") + ": " + nombre);

		Label lblPeso = new Label(panAtributos, SWT.NONE);
		lblPeso.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPeso.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblPeso.setText(pesoFichero);

		Label lblFormato = new Label(panAtributos, SWT.NONE);
		lblFormato.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFormato.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		if (formato.equals("")) {
			lblFormato.setText(formato);
		} else {
			lblFormato.setText(Ventana.idioma.getProperty("fichero_tipo_fichero") + ":  " + formato);
		}

		panBoton = new Composite(this, SWT.NONE);
		panBoton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panBoton.setLayout(new GridLayout(1, false));
		panBoton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

		btnGuardar = new Button(panBoton, SWT.NONE);
		btnGuardar.setToolTipText(Ventana.idioma.getProperty("fichero_btnContextGuardar"));
		btnGuardar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnGuardar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		btnGuardar.setText(Ventana.idioma.getProperty("fichero_btn_guardar"));

		btnGuardar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					Ventana.getInstance().teclas.eventos.copiarContenidoFicheroBtn(numero);
					break;
				}
			}
		});

		btnQR = new Button(panBoton, SWT.NONE);
		btnQR.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnQR.setToolTipText(Ventana.idioma.getProperty("btnContextMostrar_qr"));
		btnQR.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		btnQR.setText(Ventana.idioma.getProperty("mostrar_qr"));

		btnQR.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					Ventana ventana = Ventana.getInstance();
					ventana.crearVentanaQR(ventana.shlSwt, Http.encodeURIcomponent(nombre));
					break;
				}
			}
		});

		Ventana ventana = Ventana.getInstance();
		if (ventana.multicastControl.soyServidor()) {
			anyadirBtnEnviar();
			String extension = Ficheros.getExtensionFichero(nombre);
			ArrayList<ConfiguracionJson> listaModulos = JsonModulosFicheros.obtenerConfiguraciones(extension);

			if (listaModulos != null) {
				desplegable = TableComboDesplegable.getDesplegable(panBoton, Http.encodeURIcomponent(nombre), listaModulos, ventana);
				desplegable.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				desplegable.setToolTipText(Ventana.idioma.getProperty("fichero_lista_otrasOpciones"));
				desplegable.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
			}
		} else {
			if (modulos != null) {
				// añadir al desplegable la informacion pasada por red
				desplegable = TableComboDesplegable.getDesplegable(panBoton, Http.encodeURIcomponent(nombre), modulos, ventana);
				desplegable.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				desplegable.setToolTipText(Ventana.idioma.getProperty("fichero_lista_otrasOpciones"));
				desplegable.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));

			}
		}

	}

	public void anyadirBtnEnviar() {
		mostrarBotonEnviar = true;
		btnEnviar = new Button(panBoton, SWT.NONE);
		btnEnviar.setToolTipText(Ventana.idioma.getProperty("fichero_btnContextEnviar"));
		btnEnviar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnEnviar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1));
		btnEnviar.setText(Ventana.idioma.getProperty("fichero_btn_enviar"));

		btnEnviar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (!abierto) {
					Shell shell = new Shell(parent.getShell());
					shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					shell.setSize(600, 500);
					shell.setMinimumSize(400, 0);
					shell.setText(Ventana.idioma.getProperty("fichero_btn_enviar"));
					shell.setLayout(new GridLayout(1, false));

					shell.addListener(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							abierto = false;
						}
					});
					Composite composite = new Composite(shell, SWT.NONE);
					composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite.setLayout(new GridLayout(1, false));
					composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					List list = new List(composite, SWT.FLAT | SWT.MULTI | SWT.V_SCROLL);
					list.setToolTipText("");
					list.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					list.setBounds(0, 0, 108, 28);

					int numero_Aux = 0;
					Ventana ventana = Ventana.getInstance();
					for (String ip : ventana.multicastControl.escaner.listaIps.listaNombres) {
						if (!ip.equals("")) {
							numero_Aux = 0;
							for (String ip_aux : list.getItems()) {
								if (ip_aux.split("\\(").length != 0 && ip.equals(ip_aux.split("\\(")[0])) {
									numero_Aux++;
								}
							}
							if (numero_Aux != 0) {
								list.add(ip + "(" + numero_Aux + ")");
							} else {
								list.add(ip);
							}
						}
					}
					Composite composite_1 = new Composite(shell, SWT.NONE);
					composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite_1.setLayout(new GridLayout(2, false));
					composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 1, 1));

					Button btnEnviar = new Button(composite_1, SWT.PUSH);
					btnEnviar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					btnEnviar.setText(Ventana.idioma.getProperty("fichero_enviar_btnEnviar"));

					int numeroPanel = numero;

					btnEnviar.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							ArrayList<String> listaIp = new ArrayList<String>();
							for (int i = 0; i < list.getSelection().length; i++) {
								if ((listaIp.size()) < list.getSelection().length) {
									for (String nombre : ventana.multicastControl.escaner.listaIps.listaNombres) {
										if ((listaIp.size()) < list.getSelection().length) {
											if (!nombre.equals("")) {
												if (nombre.equals(list.getSelection()[i])) {
													listaIp.add(ventana.multicastControl.escaner.listaIps
															.getIpdeNombre(nombre));
													break;
												} else {
													if (list.getSelection()[i].startsWith(nombre)) {
														if (!(list.getSelection()[i].length() <= nombre.length())) {
															listaIp.add(ventana.multicastControl.escaner.listaIps
																	.getIpdeNombre(nombre));
														}
													}
												}
											}

										} else {
											break;
										}
									}
								} else {
									break;
								}
							}
							shell.close();
							abierto = false;

							red.enviar.Servidor servidor = new red.enviar.Servidor(listaIp);
							servidor.setIpServidor(MulticastControl.getMyIps().get(0));
							ArrayList<File> ficheroAux = new ArrayList<File>();
							ficheroAux.add(ventana.ficheros.ficheros.get(numeroPanel));
							servidor.setFicheros(ficheroAux);
							servidor.start();
						}
					});

					Button btnCancelar = new Button(composite_1, SWT.PUSH);
					btnCancelar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					btnCancelar.setText(Ventana.idioma.getProperty("recibir_btnCancelar"));

					btnCancelar.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							shell.close();
							abierto = false;
						}
					});
					shell.pack();
					Monitor primary = Display.getCurrent().getPrimaryMonitor();
					Rectangle bounds = primary.getBounds();
					Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
					int x = bounds.x + (bounds.width - rect.width) / 2;
					int y = bounds.y + (bounds.height - rect.height) / 2;
					shell.setLocation(x, y);
					shell.open();
					abierto = true;
				}
			}
		});
	}

}
