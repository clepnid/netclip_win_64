package ventana;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

import compartirQR.CerrarTunelWindows;
import compartirQR.ClepnidJson;
import compartirQR.Clepnid_WebJson;
import compartirQR.ConfiguracionJson;
import compartirQR.CrearQR;
import compartirQR.CrearTunel;
import compartirQR.Http;
import compartirQR.WebJson;
import historial.ListaHistorial;
import historial.VentanaListaHistorial;
import idioma.Idioma;
import portapapeles.Contenido;
import portapapeles.Ficheros;
import red.compartirContenido.Cliente;
import red.multicast.MulticastControl;
import teclado.GlobalKeys;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Ventana de la aplicacion para mostrar por pantalla el contenido recogido en
 * el portapapeles del sistema {@link portapapeles.Clip} usando
 * {@link GlobalKeys} del propio equipo o de otro equipo conectado en la misma
 * red {@link Cliente} / {@link Servidor}.
 * 
 * @author: Pavon
 * @version: 20/05/2020
 * @since 1.0
 */

public class Ventana {

	public static Idioma idioma;
	public Display display;
	public Shell shlSwt;
	protected ProgressBar BarraProgreso;
	protected Label lblPorcentajeBarraProgreso;
	public static Http http;
	public BarraProgreso panBarraProgreso;
	public PanelTexto bloque1;
	public PanelFichero bloque3;
	public Composite panCuerpo;
	public Listener listener;
	public ScrolledComposite c2;
	public ArrayList<PanelContenido> listaContenido = new ArrayList<PanelContenido>();
	public MulticastControl multicastControl;
	public Ficheros ficheros;
	private MenuBar menuBar;
	public Contenido contenido = null;
	public red.enviar.Cliente clienteEnviar;
	public GlobalKeys teclas;
	public Label lblBotonServidor;
	public Label lblBotonConfiguracion, lblBotonHistorial;
	public Image lblBotonServidorImage;
	public ListaHistorial listaHistorial;
	private Composite composite;
	private String rutaQr;
	public Label lblHayServidor;
	private boolean reiniciar = false;
	public Configuracion config;
	public Boolean editable, abiertoConfiguracion, recibirArchivo, ventanaRecibirCerrada, ventanaHistorialCerrada;

	/**
	 * Crea y añade un objeto {@link PanelContenido} mostrando el tipo de contenido
	 * disponible para recoger por {@link GlobalKeys}.
	 * 
	 * @param contenido {@link Contenido} recogido por {@link Cliente}.
	 */

	public boolean mostrarContenidoPorPantalla(Contenido contenido) {
		if (contenido != null) {
			if (this.contenido == null || !(contenido == null)) {
				switch (contenido.tipo) {
				case Html:
					vaciarLista();
					anyadirPanelHtml(panCuerpo, this);
					this.contenido = contenido;
					return true;
				case Texto:
					vaciarLista();
					anyadirPanelTexto(panCuerpo, this, contenido.texto);
					this.contenido = contenido;
					return true;
				case Ficheros:
					vaciarLista();
					for (int i = 0; i < contenido.listaFicheros.size(); i++) {
						anyadirPanelFichero(panCuerpo, this, contenido.listaFicheros.get(i)[0],
								contenido.listaFicheros.get(i)[1], contenido.listaFicheros.get(i)[2],
								contenido.listaFicheros.get(i)[3], i, contenido.getListaModulos().get(i));
					}
					this.contenido = contenido;
					return true;
				default:
					break;
				}
			}

		}
		return false;
	}

	private void httpAnyadirArchivos(Contenido contenido) {
		String extension, nombre;
		boolean yaIntroducido;
		for (int i = 0; i < contenido.listaFicheros.size(); i++) {
			extension = "";
			nombre = "";
			if (contenido.listaFicheros.get(i)[1].equals("Carpeta")) {
				extension = "zip";
				nombre = Http.encodeURIcomponent(contenido.listaFicheros.get(i)[0]);
				yaIntroducido = http.estaEnUrl(nombre);
				http.crearUrlCarpeta(nombre, contenido.listaFicheros.get(i)[4]);
			} else {
				if (contenido.listaFicheros.get(i)[2].equals("video")) {
					nombre = Http.encodeURIcomponent(contenido.listaFicheros.get(i)[0]);
					yaIntroducido = http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					http.crearUrlVideo(nombre, contenido.listaFicheros.get(i)[4]);
				} else {
					nombre = Http.encodeURIcomponent(contenido.listaFicheros.get(i)[0]);
					yaIntroducido = http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					http.crearUrlArchivo(nombre, contenido.listaFicheros.get(i)[4]);
				}
			}
			if (!yaIntroducido && Clepnid_WebJson.config != null) {
				System.out.println("hola");
				WebJson webArchivo = new WebJson();
				webArchivo.setArchivo();
				webArchivo.setRandomHexa();
				webArchivo.setTitulo(contenido.listaFicheros.get(i)[0]);
				webArchivo.setDescripcion("." + extension);
				webArchivo.setGoTo(Clepnid_WebJson.config.getRutaHttp() + "/" + nombre);
				webArchivo.setRutaImagen(WebJson.getRutaHttpImagen(nombre));
				ArrayList<ConfiguracionJson> listaModulos = ClepnidJson.obtenerConfiguraciones(extension);
				WebJson modulo = new WebJson();
				if (listaModulos != null) {
					for (ConfiguracionJson configuracionJson : listaModulos) {
						http.crearUrlModulo(configuracionJson, nombre);
						// anyadir modulo en website
						modulo.setTitulo(configuracionJson.getTitulo());
						modulo.setRandomHexa();
						modulo.setDescripcion(configuracionJson.getRutaHttp() + "/" + nombre);
						modulo.setGoTo(configuracionJson.getRutaHttp() + "/" + nombre);
						modulo.setRutaImagen(configuracionJson.getRutaImagen());
						webArchivo.addModulo(modulo);
					}
				}

				// anyadir a descarga en website
				modulo.setTitulo("Descargar");
				modulo.setRandomHexa();
				modulo.setDescripcion(nombre);
				modulo.setGoTo("/" + nombre);
				modulo.setRutaImagen(WebJson.getRutaHttpImagenDescarga());
				webArchivo.addModulo(modulo);
				Clepnid_WebJson.config.addWeb(webArchivo);
			}
		}
		if (Clepnid_WebJson.config != null) {
			http.crearUrlIndice(Clepnid_WebJson.config);
		}
	}

	private void httpEditarTexto(Contenido contenido) {
		http.modificarUrlTexto(contenido.texto);
	}

	private void httpEditarHtml(Contenido contenido) {
		http.modificarHTML(contenido.texto);
	}

	public void httpCrearRutas() {
		if (this.contenido != null) {
			switch (this.contenido.tipo) {
			case Html:
				httpEditarHtml(this.contenido);
				break;
			case Texto:
				httpEditarTexto(this.contenido);
				break;
			case Ficheros:
				httpAnyadirArchivos(this.contenido);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Constructor de la Clase a la que hay que añadir objetos:
	 * {@link MulticastCambioServidor}, {@link GlobalKeys}.
	 */

	public Ventana() {
		idioma = new Idioma();
	}

	/**
	 * Abre la ventana.
	 */
	public void open() {
		display = new Display();
		createContents();
		shlSwt.open();
		shlSwt.layout();
		center(shlSwt);
		config = new Configuracion();
		listaHistorial = new ListaHistorial();
		abiertoConfiguracion = false;
		ventanaHistorialCerrada = true;

		// Evento que abre una ventana modal para validar o cancelar el cierre de
		// ventana.
		shlSwt.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(shlSwt, style);
				messageBox.setText(idioma.getProperty("nombre_aplicacion"));
				messageBox.setMessage(idioma.getProperty("quieresCerrar"));
				if (messageBox.open() == SWT.YES) {
					// llamamos el metodo cerrar de la aplicacion para cerrar los demas hilos.
					cerrar();
					event.doit = true;
				} else {
					event.doit = false;
				}
			}
		});
		// Inicializa icono oculto de la barra de tareas.
		final Tray tray = display.getSystemTray();
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);

		// Cuando el programa inicia, la ventana aparece, entonces el icono oculto se
		// oculta.
		trayItem.setVisible(false);
		trayItem.setToolTipText(shlSwt.getText());
		trayItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleDisplay(shlSwt, tray);
			}
		});

		final Menu trayMenu = new Menu(shlSwt, SWT.POP_UP);
		MenuItem showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		showMenuItem.setText(idioma.getProperty("tray_mostrar"));

		// Muestra la ventana y oculta el icono de la barra del sistema windows.
		showMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				toggleDisplay(shlSwt, tray);
			}
		});

		trayMenu.setDefaultItem(showMenuItem);

		new MenuItem(trayMenu, SWT.SEPARATOR);

		// Menu cerrar en el icono oculto para cerrar la aplicacion.
		MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		exitMenuItem.setText(idioma.getProperty("tray_cerrar"));

		exitMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				toggleDisplay(shlSwt, tray);
				cerrar();
			}
		});

		// evento cuando el boton derecho pulsa en el icono oculto de la barra de tareas
		// y mostrará una lista.
		trayItem.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				trayMenu.setVisible(true);
			}
		});

		trayItem.setImage(shlSwt.getImage());
		shlSwt.addShellListener(new ShellAdapter() {

			// Cuando minimiza la ventana de la aplicacion, esta se oculta y aparece el
			// icono oculto dentro de la barra de tareas.
			public void shellIconified(ShellEvent e) {
				toggleDisplay(shlSwt, tray);
			}

		});
		lblBotonServidorImage = SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on.gif");
		multicastControl.compartirLista(this);
		while (!shlSwt.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (!display.isDisposed()) {
			display.dispose();
			shlSwt.dispose();
		}
		if (reiniciar) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Ventana.main(null);
		}
	}
	
	public static void main(String[] args) {
		Configuracion.controlarExistencia();
		ListaHistorial.controlarExistencia();
		Ventana ventana = new Ventana();
		MulticastControl controlMulticast = new MulticastControl(ventana);
		controlMulticast.start();
		GlobalKeys teclas = new GlobalKeys(ventana);
		http = new Http();
		ventana.teclas = teclas;
		ventana.multicastControl = controlMulticast;
		try {
			ventana.clienteEnviar = new red.enviar.Cliente(ventana, ventana.multicastControl);
			ventana.clienteEnviar.start();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ventana.open();
	}

	/**
	 * Cierra la {@link Shell} de {@link Ventana}, la conexion de esta con el
	 * sistema operativo {@link Display} y las Clases.
	 * {@link MulticastCambioServidor}, {@link GlobalKeys}
	 */

	public void cerrar() {
		teclas.cerrar();
		http.close();
		multicastControl.close();
		clienteEnviar.close();
		shlSwt.dispose();
		CerrarTunelWindows cerrarTunel = new CerrarTunelWindows();
		cerrarTunel.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Ventana se muestra centrada
	 * 
	 * @param shell ventana para centrar
	 */

	private static void center(Shell shell) {
		Monitor monitor = shell.getMonitor();
		Rectangle bounds = monitor.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	/**
	 * Cuando la ventana es visible, la ventana es ocultada y el icono oculto es
	 * eliminado. Cuando la ventana es ocultada, el icono oculto aparece en la barra
	 * de tareas.
	 * 
	 * @param shell ventana
	 * @param tray  controlador del icono oculto en la barra de tareas.
	 */

	private static void toggleDisplay(Shell shell, Tray tray) {
		try {
			shell.setVisible(!shell.isVisible());
			tray.getItem(0).setVisible(!shell.isVisible());
			if (shell.getVisible()) {
				shell.setMinimized(false);
				shell.setActive();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crea los componentes dentro de {@link Ventana}.
	 */

	protected void createContents() {
		shlSwt = new Shell(display);
		shlSwt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlSwt.setImage(getImageValida(display, "./src/imagenes/clipboard.gif"));
		shlSwt.setSize(600, 400);
		shlSwt.setMinimumSize(400, 0);
		shlSwt.setText(idioma.getProperty("nombre_aplicacion"));
		shlSwt.setLayout(new GridLayout(1, false));

		c2 = new ScrolledComposite(shlSwt, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		ventanaRecibirCerrada = false;
		panCuerpo = new Composite(c2, SWT.PUSH);
		panCuerpo.setToolTipText(idioma.getProperty("contenido_disponible"));
		panCuerpo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panCuerpo.setLayout(new GridLayout(1, false));
		panCuerpo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		panBarraProgreso = new BarraProgreso(this, panCuerpo, SWT.NONE, display, shlSwt);
		GridData gd_panBarraProgreso = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_panBarraProgreso.heightHint = 79;
		panBarraProgreso.setLayoutData(gd_panBarraProgreso);
		panBarraProgreso.esconderPanelProgressBar(true);
		c2.setContent(panCuerpo);
		c2.setExpandHorizontal(true);
		c2.setExpandVertical(true);

		composite = new Composite(shlSwt, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(5, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 32;
		composite.setLayoutData(gd_composite);

		Composite compositeConfig = new Composite(composite, SWT.NONE);
		compositeConfig.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		compositeConfig.setLayout(layout);
		GridData gd_compositeConfig = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_compositeConfig.heightHint = 32;
		gd_compositeConfig.widthHint = 120;
		gd_compositeConfig.verticalIndent = 0;
		gd_compositeConfig.verticalSpan = 0;
		compositeConfig.setLayoutData(gd_compositeConfig);

		Label lblConfigurar = new Label(compositeConfig, SWT.NONE);
		lblConfigurar.setToolTipText(idioma.getProperty("btnContextConfigurar"));
		lblConfigurar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblConfigurar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblConfigurar.setText(idioma.getProperty("btnConfigurar"));

		lblBotonConfiguracion = new Label(compositeConfig, SWT.NONE);
		lblBotonConfiguracion.setToolTipText(idioma.getProperty("btnContextConfigurar"));
		lblBotonConfiguracion.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblBotonConfiguracion.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf.gif"));
		lblBotonConfiguracion.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Composite compositeHistorial = new Composite(composite, SWT.NONE);
		compositeHistorial.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout layout_historial = new GridLayout(2, false);
		layout_historial.marginHeight = 0;
		layout_historial.marginWidth = 0;
		compositeHistorial.setLayout(layout_historial);
		GridData gd_compositeHistorial = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_compositeHistorial.heightHint = 32;
		gd_compositeHistorial.widthHint = 120;
		gd_compositeHistorial.verticalIndent = 0;
		gd_compositeHistorial.verticalSpan = 0;
		compositeHistorial.setLayoutData(gd_compositeHistorial);

		Label lblHistorial = new Label(compositeHistorial, SWT.NONE);
		lblHistorial.setToolTipText(idioma.getProperty("btnContextHistorial"));
		lblHistorial.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblHistorial.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblHistorial.setText(idioma.getProperty("btnHistorial"));

		lblBotonHistorial = new Label(compositeHistorial, SWT.NONE);
		lblBotonHistorial.setToolTipText(idioma.getProperty("btnContextHistorial"));
		lblBotonHistorial.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblBotonHistorial.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial.gif"));
		lblBotonHistorial.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label lblServidor = new Label(composite, SWT.NONE);
		lblServidor.setToolTipText(idioma.getProperty("btnContextServidor"));
		lblServidor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblServidor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblServidor.setText(idioma.getProperty("btnServidor"));

		lblBotonServidor = new Label(composite, SWT.NONE);
		lblBotonServidor.setToolTipText(idioma.getProperty("btnContextEstadoApagado"));
		lblBotonServidor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_off.gif"));
		lblBotonServidor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		lblHayServidor = new Label(composite, SWT.NONE);
		lblHayServidor.setToolTipText(idioma.getProperty("btnContextEstado"));
		lblHayServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_gris.gif"));
		lblHayServidor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblHayServidor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		// Eventos al pulsar el botón Historial
		lblBotonHistorial.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent arg0) {
				if (lblBotonHistorial.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial_down.gif"))) {
					display.asyncExec(new Runnable() {
						public void run() {
							lblBotonHistorial
									.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial.gif"));
							if (ventanaHistorialCerrada) {
								ventanaHistorialCerrada = false;
								abrirVentanaHistorial();
							}
						}
					});
				}
			}

			public void mouseDown(MouseEvent arg0) {
				if (lblBotonHistorial.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial_up.gif"))) {
					lblBotonHistorial
							.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial_down.gif"));
				}
			}

		});

		// Eventos al posar raton encima del boton Historial
		lblBotonHistorial.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonHistorial.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial.gif"))) {
					lblBotonHistorial
							.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial_up.gif"));
				}
			}
		});

		// Eventos al posar raton fuera del boton Historial
		lblBotonHistorial.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonHistorial.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial_up.gif"))) {
					lblBotonHistorial.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/historial.gif"));
				}
			}
		});

		// Eventos al pulsar el botón configuracion
		lblBotonConfiguracion.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent arg0) {
				if (lblBotonConfiguracion.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf_down.gif"))) {
					display.asyncExec(new Runnable() {
						public void run() {
							lblBotonConfiguracion
									.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf.gif"));
							if (!abiertoConfiguracion) {
								crearVentanaConfiguracion(shlSwt).layout();
							}
						}
					});
				}
			}

			public void mouseDown(MouseEvent arg0) {
				if (lblBotonConfiguracion.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf_up.gif"))) {
					lblBotonConfiguracion
							.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf_down.gif"));
				}
			}

		});

		// Eventos al posar raton encima del boton configuracion
		lblBotonConfiguracion.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonConfiguracion.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf.gif"))) {
					lblBotonConfiguracion.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf_up.gif"));
				}
			}
		});

		// Eventos al posar raton fuera del boton configuracion
		lblBotonConfiguracion.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonConfiguracion.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf_up.gif"))) {
					lblBotonConfiguracion.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/conf.gif"));
				}
			}
		});

		// Eventos al pulsar el botón servidor
		lblBotonServidor.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent arg0) {
				pararServidor();
			}

			public void mouseDown(MouseEvent arg0) {
				if (lblBotonServidor.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_up.gif"))) {
					lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_down.gif"));
				}
			}

		});

		// Eventos al posar raton encima del boton servidor
		lblBotonServidor.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonServidorImage.equals(lblBotonServidor.getImage())) {
					lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_up.gif"));
					lblBotonServidor.setToolTipText(idioma.getProperty("btnContextEstadoEncendido"));
				}
			}
		});

		// Eventos al posar raton fuera del boton servidor
		lblBotonServidor.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				if (lblBotonServidor.getImage()
						.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_up.gif"))) {
					lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on.gif"));
				}
			}
		});

		menuBar = new MenuBar(this);

	}

	/**
	 * Vacia la lista de {@link PanelContenido}.
	 */

	public void vaciarLista() {
		display.asyncExec(new Runnable() {
			public void run() {
				http.dispose();
				for (PanelContenido panelContenido : listaContenido) {
					panelContenido.gd_panObjeto.exclude = true;
					panelContenido.setVisible(false);
				}
				listaContenido = new ArrayList<PanelContenido>();
				panCuerpo.layout();
				c2.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
	}

	public void eliminarArchivoDeLista(String nombre) {
		if (contenido.tipo.equals(Contenido.Tipo.Ficheros)) {
			display.asyncExec(new Runnable() {
				public void run() {
					String nombreSinEspacios;
					for (PanelContenido panelContenido : listaContenido) {
						nombreSinEspacios = panelContenido.getNombre().replace(" ", "");
						System.out.println(nombre + " = " + nombreSinEspacios);
						if (nombreSinEspacios.equals(nombre)) {
							panelContenido.gd_panObjeto.exclude = true;
							panelContenido.setVisible(false);
							listaContenido.remove(panelContenido);
							break;
						}
					}
					panCuerpo.layout();
					c2.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					eliminarArchivoDeContenido(nombre);
				}
			});

		}
	}

	private void eliminarArchivoDeContenido(String nombre) {
		String nombreSinEspacios;
		for (String[] fichero : this.contenido.listaFicheros) {
			nombreSinEspacios = fichero[0].replace(" ", "");
			System.out.println(nombre + " = " + nombreSinEspacios);
			if (nombreSinEspacios.equals(nombre)) {
				this.contenido.listaFicheros.remove(fichero);

				break;
			}
		}
		if (this.contenido.listaFicheros.size() == 0) {
			lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_down.gif"));
			pararServidor();
		}

	}

	/**
	 * Cambia el botón representando el estado del servidor
	 * {@link MulticastCambioServidor}.
	 * 
	 * @param verde <code>true</code> definir imagen de botón en verde,
	 *              <code>false</code> definir imagen de boton en gris.
	 */

	public void cambiarbtnHayServidor(boolean verde) {

		if (verde) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (!lblHayServidor.getImage()
							.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_verde.gif"))) {
						lblHayServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_verde.gif"));
					}
					if (!lblBotonServidor.getImage()
							.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_off.gif"))) {
						lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_off.gif"));
					}
				}
			});
		} else {

		}
	}

	/**
	 * Añade un componente {@link PanelTexto} a la lista contenido de esta Clase.
	 * 
	 * @param parent  {@link Composite} panel padre para {@link PanelTexto}.
	 * @param ventana {@link Ventana} para mostrar por pantalla el componente.
	 * @param texto   {@link String} a mostrar por {@link Ventana}.
	 */

	public void anyadirPanelTexto(Composite parent, Ventana ventana, String texto) {
		display.asyncExec(new Runnable() {
			public void run() {
				listaContenido.add(new PanelTexto(parent, ventana, SWT.NONE, texto));
				c2.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				panCuerpo.layout();
			}
		});
	}

	public static void mensajeTray(String mensaje, MessageType tipo) {
		SystemTray tray = SystemTray.getSystemTray();
		java.awt.Image image = Toolkit.getDefaultToolkit().getImage("./src/imagenes/clipboard.gif");
		TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.print("");
		}
		trayIcon.displayMessage("Clepnid", mensaje, tipo);
		tray.remove(trayIcon);
	}

	/**
	 * Añade un componente {@link PanelHtml} a la lista contenido de esta Clase.
	 * 
	 * @param parent  {@link Composite} panel padre para {@link PanelHtml}.
	 * @param ventana {@link Ventana} para mostrar por pantalla el componente.
	 */

	public void anyadirPanelHtml(Composite parent, Ventana ventana) {
		display.asyncExec(new Runnable() {
			public void run() {
				listaContenido.add(new PanelHtml(parent, ventana, SWT.NONE));
				c2.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				panCuerpo.layout();
			}
		});
	}

	/**
	 * Añade un componente {@link PanelFichero} a la lista contenido de esta Clase.
	 * 
	 * @param parent  {@link Composite} panel padre para {@link PanelTexto}.
	 * @param ventana {@link Ventana} para mostrar por pantalla el componente.
	 * @param nombre  {@link String} con el nombre del fichero
	 * @param peso    {@link String} con el peso del fichero
	 * @param formato {@link String} con el formato del fichero
	 * @param ruta    {@link String} con la ruta del fichero
	 * @param numero  numero posición de la lista contenido.
	 */

	public void anyadirPanelFichero(Composite parent, Ventana ventana, String nombre, String peso, String formato,
			String ruta, int numero, ArrayList<ConfiguracionJson> modulos) {
		display.asyncExec(new Runnable() {
			public void run() {
				listaContenido
						.add(new PanelFichero(parent, ventana, SWT.NONE, nombre, peso, formato, ruta, numero, modulos));
				c2.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				panCuerpo.layout();
			}

		});
	}

	/**
	 * Procesa una imagen.
	 * 
	 * @param display {@link Display} controlador entre la ventana y el sistema
	 *                operativo.
	 * @param ruta    {@link String} ruta de la imagen a validar.
	 * @return {@link Image}
	 */

	public Image getImageValida(Display display, String ruta) {
		Image image = new Image(display, ruta);
		GC gc = new GC(image);
		gc.drawImage(image, 0, 0);
		gc.dispose();
		return image;
	}

	public void ventanaRecibirArchivo(Shell parent) {
		display.asyncExec(new Runnable() {
			public void run() {

				Shell hijo = new Shell(parent, SWT.RESIZE | SWT.CLOSE);
				editable = false;
				hijo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				hijo.setSize(450, 300);
				hijo.setText(idioma.getProperty("recibir_titulo"));
				hijo.setLayout(new GridLayout(1, false));
				hijo.addListener(SWT.Close, new Listener() {
					public void handleEvent(Event event) {
						recibirArchivo = false;
						ventanaRecibirCerrada = true;
					}
				});
				Composite composite = new Composite(hijo, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

				Label lblNewLabel = new Label(composite, SWT.NONE);
				lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
				lblNewLabel.setText(idioma.getProperty("recibir_pregunta"));

				Composite composite_1 = new Composite(composite, SWT.NONE);
				composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
				composite_1.setLayout(new GridLayout(2, false));

				Button btnAceptar = new Button(composite_1, SWT.NONE);
				btnAceptar.setBounds(0, 0, 90, 30);
				btnAceptar.setText(idioma.getProperty("recibir_btnAceptar"));
				btnAceptar.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						hijo.close();
						recibirArchivo = true;
						ventanaRecibirCerrada = true;
					}
				});

				Button btnCancelar = new Button(composite_1, SWT.NONE);
				btnCancelar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				btnCancelar.setText(idioma.getProperty("recibir_btnCancelar"));
				btnCancelar.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						hijo.close();
						recibirArchivo = false;
						ventanaRecibirCerrada = true;
					}
				});
				hijo.pack();
				Monitor primary = parent.getDisplay().getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = parent.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				hijo.setLocation(x, y);
				hijo.open();
			}

		});
	}

	public void reiniciar() {
		reiniciar = true;
		cerrar();
	}

	public Shell crearVentanaConfiguracion(Shell parent) {
		Shell hijo = new Shell(parent, SWT.RESIZE | SWT.CLOSE);
		editable = false;
		hijo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		hijo.setSize(450, 300);
		hijo.setText(idioma.getProperty("configuracion_titulo"));
		hijo.setLayout(new GridLayout(1, false));
		hijo.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				abiertoConfiguracion = false;
			}
		});

		Composite composite_principal = new Composite(hijo, SWT.NONE);
		composite_principal.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_principal.setLayout(new GridLayout(2, false));
		composite_principal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblNombre = new Label(composite_principal, SWT.NONE);
		lblNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNombre.setBounds(0, 0, 70, 20);
		lblNombre.setText(idioma.getProperty("configuracion_nombre") + ":");

		Text text_Nombre = new Text(composite_principal, SWT.BORDER);
		text_Nombre.setEnabled(false);
		text_Nombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_Nombre.setEditable(false);
		GridData gd_text_Nombre = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_Nombre.widthHint = 111;
		text_Nombre.setLayoutData(gd_text_Nombre);
		text_Nombre.setBounds(0, 0, 78, 26);

		Label lblCarpeta = new Label(composite_principal, SWT.NONE);
		lblCarpeta.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCarpeta.setBounds(0, 0, 70, 20);
		lblCarpeta.setText(idioma.getProperty("configuracion_carpeta") + ":");

		Text text_Carpeta = new Text(composite_principal, SWT.BORDER);
		text_Carpeta.setEnabled(false);
		text_Carpeta.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_Carpeta.setEditable(false);
		text_Carpeta.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblRecibirAutomaticamente = new Label(composite_principal, SWT.NONE);
		lblRecibirAutomaticamente.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRecibirAutomaticamente.setText(idioma.getProperty("configuracion_recibir_envios_automaticamente") + ":");

		Button btnRecibirAutomaticamenteCheckButton = new Button(composite_principal, SWT.CHECK);
		btnRecibirAutomaticamenteCheckButton.setEnabled(false);
		btnRecibirAutomaticamenteCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label lblRecibirEnvios = new Label(composite_principal, SWT.NONE);
		lblRecibirEnvios.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRecibirEnvios.setText(idioma.getProperty("configuracion_recibir_envios") + ":");

		Button btnRecibirEnviosCheckButton = new Button(composite_principal, SWT.CHECK);
		btnRecibirEnviosCheckButton.setEnabled(false);
		btnRecibirEnviosCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label lblRutasInicio = new Label(composite_principal, SWT.NONE);
		lblRutasInicio.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRutasInicio.setText(idioma.getProperty("configuracion_cargar_rutas_inicio") + ":");

		Button btnRutasInicioCheckButton = new Button(composite_principal, SWT.CHECK);
		btnRutasInicioCheckButton.setEnabled(false);
		btnRutasInicioCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Label lblFicheroRutasInicio = new Label(composite_principal, SWT.NONE);
		lblFicheroRutasInicio.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFicheroRutasInicio.setText(idioma.getProperty("configuracion_fichero_rutas_inicio") + ":");

		Text text_FicheroRutasInicio = new Text(composite_principal, SWT.BORDER);
		text_FicheroRutasInicio.setEnabled(false);
		text_FicheroRutasInicio.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_FicheroRutasInicio.setEditable(false);
		text_FicheroRutasInicio.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblIdioma = new Label(composite_principal, SWT.NONE);
		lblIdioma.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblIdioma.setText(idioma.getProperty("configuracion_btnIdioma") + ":");

		Combo idiomas = new Combo(composite_principal, SWT.READ_ONLY);
		idiomas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		idiomas.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		String idioma_items[] = { "Ingles", "Espanyol" };
		idiomas.setItems(idioma_items);
		idiomas.setEnabled(false);
		idiomas.setTouchEnabled(false);

		Composite composite_btn = new Composite(hijo, SWT.NONE);
		composite_btn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_btn.setLayout(new GridLayout(2, false));
		composite_btn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));

		Button btnModificar = new Button(composite_btn, SWT.NONE);
		btnModificar.setText(idioma.getProperty("configuracion_btnModificar"));
		btnModificar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		try {
			config = Configuracion.deserializar();
			if (config.carpeta != null) {
				text_Carpeta.setText(config.carpeta);
			} else {
				text_Carpeta.setText("");
			}
			if (config.nombre != null) {
				text_Nombre.setText(config.nombre);
			} else {
				text_Nombre.setText("");
			}
			if (config.rutaGuardadoHttp != null) {
				text_FicheroRutasInicio.setText(config.rutaGuardadoHttp);
			} else {
				text_FicheroRutasInicio.setText("");
			}
			if (config.isAutomatic != null && config.isAutomatic) {
				btnRecibirAutomaticamenteCheckButton.setSelection(true);
			} else {
				btnRecibirAutomaticamenteCheckButton.setSelection(false);
			}
			if (config.recibirEnvios != null && config.recibirEnvios) {
				btnRecibirEnviosCheckButton.setSelection(true);
			} else {
				btnRecibirEnviosCheckButton.setSelection(false);
			}
			if (config.inicializarRutas != null && config.inicializarRutas) {
				btnRutasInicioCheckButton.setSelection(true);
			} else {
				btnRutasInicioCheckButton.setSelection(false);
			}
			if (config.idioma != null) {
				idiomas.setText(config.idioma);
			}
		} catch (ClassNotFoundException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		btnModificar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (editable) {
					boolean abrirVentanaReiniciar = false;
					Configuracion configAux = null;
					try {
						configAux = Configuracion.deserializar();
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					if (!text_Nombre.getText().equals(configAux.nombre)) {
						abrirVentanaReiniciar = true;
					}
					if (!text_Carpeta.getText().equals(configAux.carpeta)) {
						abrirVentanaReiniciar = true;
					}
					if (!btnRecibirAutomaticamenteCheckButton.getSelection()==configAux.isAutomatic) {
						abrirVentanaReiniciar = true;
					}
					if (!text_FicheroRutasInicio.getText().equals(configAux.rutaGuardadoHttp)) {
						abrirVentanaReiniciar = true;
					}
					if (!idioma_items[idiomas.getSelectionIndex()].equals(configAux.idioma)) {
						abrirVentanaReiniciar = true;
					}
					if (!btnRutasInicioCheckButton.getSelection()==config.inicializarRutas) {
						abrirVentanaReiniciar = true;
					}
					if (!btnRecibirEnviosCheckButton.getSelection()==config.recibirEnvios) {
						abrirVentanaReiniciar = true;
					}
					config = new Configuracion(text_Nombre.getText(), text_Carpeta.getText(),
							btnRecibirAutomaticamenteCheckButton.getSelection());
					if (config.serial == null) {
						config.serial = Configuracion.getRandomString(Configuracion.LENGTHSERIAL);
					}
					config.rutaGuardadoHttp = text_FicheroRutasInicio.getText();
					config.inicializarRutas = btnRutasInicioCheckButton.getSelection();
					config.recibirEnvios = btnRecibirEnviosCheckButton.getSelection();
					config.idioma = idioma_items[idiomas.getSelectionIndex()];
					try {
						Configuracion.serializar(config);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						Idioma.introducirIdioma(idioma_items[idiomas.getSelectionIndex()]);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					text_Nombre.setEnabled(false);
					text_Nombre.setEditable(false);
					text_Carpeta.setEnabled(false);
					text_Carpeta.setEditable(false);
					text_FicheroRutasInicio.setEnabled(false);
					text_FicheroRutasInicio.setEditable(false);
					btnRecibirAutomaticamenteCheckButton.setEnabled(false);
					btnRecibirEnviosCheckButton.setEnabled(false);
					btnRutasInicioCheckButton.setEnabled(false);
					idiomas.setEnabled(false);
					idiomas.setTouchEnabled(false);
					editable = false;
					btnModificar.setText(idioma.getProperty("configuracion_btnModificar"));
					if (abrirVentanaReiniciar) {
						int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
						MessageBox messageBox = new MessageBox(shlSwt, style);
						messageBox.setText(idioma.getProperty("nombre_aplicacion"));
						messageBox.setMessage(idioma.getProperty("quieresReiniciar"));
						if (messageBox.open() == SWT.YES) {
							// llamamos el metodo cerrar de la aplicacion para cerrar los demas hilos.
							reiniciar();
						} 
					}

				} else {
					text_Nombre.setEnabled(true);
					text_Nombre.setEditable(true);
					text_Carpeta.setEnabled(true);
					text_Carpeta.setEditable(true);

					// si no se ha cargado la configuracion del menu no se podrá realizar esta
					// accion
					if (Clepnid_WebJson.config != null) {
						text_FicheroRutasInicio.setEnabled(true);
						text_FicheroRutasInicio.setEditable(true);
						btnRutasInicioCheckButton.setEnabled(true);
					}
					btnRecibirAutomaticamenteCheckButton.setEnabled(true);
					btnRecibirEnviosCheckButton.setEnabled(true);
					idiomas.setEnabled(true);
					idiomas.setTouchEnabled(true);
					editable = true;
					btnModificar.setText(idioma.getProperty("configuracion_btnAplicar"));
				}

			}
		});

		Button btnCerrar = new Button(composite_btn, SWT.NONE);
		btnCerrar.setText(idioma.getProperty("configuracion_btnCerrar"));
		btnCerrar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		btnCerrar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				hijo.close();
				abiertoConfiguracion = false;
			}
		});
		hijo.pack();
		hijo.layout(true);
		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		hijo.setLocation(x, y);
		hijo.open();
		abiertoConfiguracion = true;
		return hijo;
	}

	public void abrirVentanaHistorial() {
		VentanaListaHistorial ventanaListaHistorial;
		ventanaListaHistorial = new VentanaListaHistorial(multicastControl, teclas.eventos.getClip());
		ventanaListaHistorial.open();
		ventanaHistorialCerrada = true;
	}

	public Shell crearVentanaQR(Shell parent, String ruta) {

		Shell hijo = new Shell(parent, SWT.RESIZE | SWT.CLOSE);
		editable = false;
		hijo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		hijo.setSize(450, 300);
		hijo.setText(idioma.getProperty("qr_titulo"));
		hijo.setLayout(new GridLayout(1, false));
		hijo.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				abiertoConfiguracion = false;
			}
		});

		Composite composite_principal = new Composite(hijo, SWT.NONE);
		composite_principal.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_principal.setLayout(new GridLayout(1, false));

		Label label = new Label(composite_principal, SWT.NONE);
		label.setText("Can't find icon");
		ImageData image = null;

		rutaQr = "";
		if (ruta.equals("")) {
			rutaQr = "http://" + multicastControl.ip_servidor + ":" + Http.getPuertoHTTP() + "/index.html";
		} else {
			if (ruta.equals("HTML")) {
				rutaQr = "http://" + multicastControl.ip_servidor + ":" + Http.getPuertoHTTP() + "/pagina.html";
			} else {
				if (ruta.equals("VIDEO")) {
					rutaQr = http.getRutaVideo();
				} else {
					if (ruta.startsWith("http://")) {
						rutaQr = ruta;
					} else {
						rutaQr = "http://" + multicastControl.ip_servidor + ":" + Http.getPuertoHTTP() + "/" + ruta;
					}
				}
			}
		}
		image = CrearQR.crearImagenQr(rutaQr);
		if (image != null) {
			Image imagen = new Image(null, image);
			label.setImage(imagen);
		}

		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		// Create a Link
		Link linkRutaQr = new Link(composite_principal, SWT.NONE);
		linkRutaQr.setText("<a href=\"" + rutaQr + "\">" + idioma.getProperty("qr_enlace") + "</a>");
		linkRutaQr.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		linkRutaQr.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		// Event handling when users click on links.
		linkRutaQr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(getRutaQr());
			}
		});

		label.pack();
		hijo.pack();
		hijo.layout(true);
		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		hijo.setLocation(x, y);
		hijo.open();
		return hijo;
	}

	public String getRutaQr() {
		return rutaQr;
	}

	public Shell crearVentanaModuloQR(Shell parent, String ruta, String nombre) {
		Shell hijo = new Shell(parent, SWT.RESIZE | SWT.CLOSE);
		editable = false;
		hijo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		hijo.setSize(450, 300);
		hijo.setText(idioma.getProperty("qr_titulo"));
		hijo.setLayout(new GridLayout(1, false));
		hijo.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				abiertoConfiguracion = false;
			}
		});

		Composite composite_principal = new Composite(hijo, SWT.NONE);
		composite_principal.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_principal.setLayout(new GridLayout(1, false));

		Label label = new Label(composite_principal, SWT.NONE);
		label.setText("Can't find icon");
		ImageData image = null;
		rutaQr = "http://" + multicastControl.ip_servidor + ":" + Http.getPuertoHTTP() + ruta + "/" + nombre;

		image = CrearQR.crearImagenQr(rutaQr);
		if (image != null) {
			Image imagen = new Image(null, image);
			label.setImage(imagen);
		}
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		// Create a Link
		Link linkRutaQr = new Link(composite_principal, SWT.NONE);
		linkRutaQr.setText("<a href=\"" + rutaQr + "\">" + idioma.getProperty("qr_enlace") + "</a>");
		linkRutaQr.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		linkRutaQr.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		// Event handling when users click on links.
		linkRutaQr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(getRutaQr());
			}
		});

		label.pack();
		hijo.pack();
		hijo.layout(true);
		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		hijo.setLocation(x, y);
		hijo.open();
		return hijo;
	}

	public void pararServidor() {
		if (lblBotonServidor.getImage()
				.equals(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_down.gif"))) {
			display.asyncExec(new Runnable() {
				public void run() {
					lblBotonServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_off.gif"));
					lblBotonServidor.setToolTipText(idioma.getProperty("btnContextEstadoApagado"));
					lblHayServidor.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_gris.gif"));
				}
			});
			vaciarLista();
			contenido = null;
			multicastControl.cliente.pararServidor();
			http.vaciarUrls();
			http.textoDefecto();
			CrearTunel.salir = true;
		}
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public void setMenuBar(MenuBar menuBar) {
		this.menuBar = menuBar;
	}

}
