package teclado;

import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import historial.Historial;
import historial.ListaHistorial;
import portapapeles.Clip;
import portapapeles.Contenido;
import portapapeles.DatoSeleccion;
import portapapeles.Ficheros;
import portapapeles.Contenido.Tipo;
import red.compartirFicheros.Cliente;
import red.compartirFicheros.Servidor;
import ventana.Ventana;

/**
 * Clase que implementa un controlador de teclas del sistema.
 * {@link NativeKeyListener}
 * 
 * @author: Pavon
 * @version: 10/05/2020
 * @since 1.0
 */

public class EventoTeclasGlobal implements NativeKeyListener {
	
	private static EventoTeclasGlobal INSTANCE;
	private Set<Integer> pressed = new HashSet<Integer>();
	private static boolean copiar = false, pegar = false;
	private Clip clip;
	Transferable backupClip;
	public String rutaEscritorio = "";
	private FlavorListener flavorListener;
	private String stringAnterior;
	private Ficheros ficherosAnterior;
	private Ventana ventana;
	public static boolean clienteFicherosFuncionando = false;

	private EventoTeclasGlobal() {
		clip = new Clip();
		flavorListener = new FlavorListener() {

			@Override
			public void flavorsChanged(FlavorEvent e) {
				if (clip.pulsadoTeclas) {
					clip.copiadoDelSistema = true;
					clip.contenidoRecogido = false;
					synchronized (clip.contenidoRecogido) {
						clip.proccessClipboard(clip.clipboard);
					}
					clip.recogido = true;
					clip.copiadoDelSistema = false;
				}
			}
		};
		clip.setFlavorListener(flavorListener);
		stringAnterior = null;
		ficherosAnterior = null;
		this.ventana = Ventana.getInstance();
	}
	
	public static EventoTeclasGlobal getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new EventoTeclasGlobal();
        }
        
        return INSTANCE;
    }

	/**
	 * Define el evento de pulsar una tecla del sistema, controlando si la
	 * combinación de estas es la indicada (Ctrl + Shift + 1) o (Ctrl + Shift + 2)
	 * para realizar las acciones de copiar y pegar.
	 * 
	 * @param arg0 {@link NativeKeyEvent} evento al pulsar una tecla.
	 */

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		pressed.add(arg0.getKeyCode());
		if (pressed.size() > 2) {
			if (pressed.contains(NativeKeyEvent.VC_CONTROL) && pressed.contains(NativeKeyEvent.VC_SHIFT)
					&& pressed.contains(NativeKeyEvent.VC_1)) {
				copiar = true;
			} else if (pressed.contains(NativeKeyEvent.VC_CONTROL) && pressed.contains(NativeKeyEvent.VC_SHIFT)
					&& pressed.contains(NativeKeyEvent.VC_2)) {
				pegar = true;
			}
		}
	}

	/**
	 * Define el evento de dejar de pulsar una tecla del sistema, controlando si la
	 * combinación de estas es la indicada (Ctrl + Shift + 1) o (Ctrl + Shift + 2)
	 * para realizar las acciones de copiar y pegar.
	 * 
	 * @param arg0 {@link NativeKeyEvent} evento al dejar de pulsar una tecla.
	 */

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		pressed.remove(arg0.getKeyCode());
		if (copiar && (!pressed.contains(NativeKeyEvent.VC_CONTROL) && !pressed.contains(NativeKeyEvent.VC_SHIFT)
				&& !pressed.contains(NativeKeyEvent.VC_1))) {
			copiar = false;
			try {
				copiar();
			} catch (InterruptedException e) {
				clip.tipoContenido = "";
			}
		}
		if (pegar && (!pressed.contains(NativeKeyEvent.VC_CONTROL) && !pressed.contains(NativeKeyEvent.VC_SHIFT)
				&& !pressed.contains(NativeKeyEvent.VC_2))) {
			pegar = false;
			pegarContenido();
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Pulsa las teclas Ctrl + c para copiar {@link PulsarTeclas}, recoge los datos
	 * de texto, imagen o rutas de ficheros del portapapeles del sistema
	 * {@link Clip}, los muestra por {@link Ventana} y restaura el portapapeles al
	 * estado de antes de pulsar Ctrl + c. Con este metodo nos convertimos en
	 * servidor {@link MulticastCambioServidor}.
	 * 
	 * @throws InterruptedException cuando se interrumpe el hilo para copiar
	 */

	public void copiar() throws InterruptedException {
		clip.clipboard = Clip.resetearClipboard(clip.clipboard, flavorListener);
		try {
			backupClip = (Transferable) Clip.getContenidoClipboard(clip.clipboard, flavorListener);
		} catch (IOException e) {
			backupClip = null;
		}
		String myString = "Clepnid";
		StringSelection stringSelection = new StringSelection(myString);
		clip.setContenidoClipboard(stringSelection);
		while (!clip.introducido(myString)) {
			Thread.sleep(100);
		}
		clip.tipoContenido = "";
		clip.contenidoRecogido = null;
		clip.pulsadoTeclas = true;
		PulsarTeclas.copiar();
		clip.recogido = false;
		while (!clip.recogido || clip.copiadoDelSistema) {
			Thread.sleep(100);
			if ((!clip.copiadoDelSistema && clip.introducido(myString))
					|| !clip.copiadoDelSistema && !clip.introducido(myString)) {
				// no se ha seleccionado nada por lo tanto no se puede recoger del portapapeles
				break;
			}
		}
		clip.pulsadoTeclas = false;
		clip.recogido = false;
		if (ventana.contenido == null) {
			if (clip.contenidoRecogido != null) {
				if (clip.tipoContenido.equals("html") || clip.tipoContenido.equals("texto")) {
					String string = null;
					synchronized (clip.contenidoRecogido) {
						if (!clip.contenidoRecogido.getClass().equals(Boolean.class)) {
							string = (String) clip.contenidoRecogido;
						}
					}
					if (string != null) {
						if (!string.contentEquals(myString)) {
							ventanaServidorString(string);
						}
					}
					clip.setContenidoClipboard(backupClip);
				} else if (clip.tipoContenido.equals("ficheros")) {
					List<?> rutas_ficheros = null;
					synchronized (clip.contenidoRecogido) {
						rutas_ficheros = (List<?>) clip.contenidoRecogido;
					}
					ventanaServidorFichero(rutas_ficheros);
					clip.setContenidoClipboard(backupClip);
				}
			}
		} else {
			if (clip.contenidoRecogido != null) {
				if (clip.tipoContenido.equals("html") || clip.tipoContenido.equals("texto")) {
					if (stringAnterior != null) {
						String string = null;
						synchronized (clip.contenidoRecogido) {
							if (!clip.contenidoRecogido.getClass().equals(Boolean.class)) {
								string = (String) clip.contenidoRecogido;
							}
						}
						if (string != null) {
							if (!string.contentEquals(myString)) {
								if (!string.equals(stringAnterior)) {
									ventanaServidorString(string);
								}
							}
						}
						clip.setContenidoClipboard(backupClip);
					} else {
						String string = null;
						synchronized (clip.contenidoRecogido) {
							if (!clip.contenidoRecogido.getClass().equals(Boolean.class)) {
								string = (String) clip.contenidoRecogido;
							}
						}
						if (string != null) {
							if (!string.contentEquals(myString)) {
								ventanaServidorString(string);
							}
						}
						clip.setContenidoClipboard(backupClip);
					}
				} else if (clip.tipoContenido.equals("ficheros")) {
					if (ficherosAnterior != null) {
						List<?> rutas_ficheros = null;
						synchronized (clip.contenidoRecogido) {
							rutas_ficheros = (List<?>) clip.contenidoRecogido;
						}
						Ficheros ficheros = new Ficheros(rutas_ficheros);
						Boolean esIgual = true;
						for (File ruta : ficheros.ficheros) {
							if (!ficherosAnterior.ficheros.contains(ruta)) {
								esIgual = false;
							}
						}
						if (!esIgual) {
							ventanaServidorFichero(rutas_ficheros);
						}
						clip.setContenidoClipboard(backupClip);
					} else {
						List<?> rutas_ficheros = null;
						synchronized (clip.contenidoRecogido) {
							rutas_ficheros = (List<?>) clip.contenidoRecogido;
						}
						ventanaServidorFichero(rutas_ficheros);
						clip.setContenidoClipboard(backupClip);
					}
				}
			}
		}
	}

	public void ventanaServidorString(String string) {
		if (string != null) {
			if (ventana.mostrarContenidoPorPantalla(new Contenido(string))) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						ventana.lblBotonServidor.setImage(ventana.lblBotonServidorImage);
						ventana.lblHayServidor
								.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_verde.gif"));
					}
				});
				stringAnterior = string;
				ficherosAnterior = null;
				ventana.multicastControl.cliente.cambioServidor();
			}
		}
	}

	public void ventanaServidorFichero(List<?> rutas_ficheros) {
		if (rutas_ficheros != null) {
			Ficheros ficheros = new Ficheros(rutas_ficheros);
			ventana.ficheros = new Ficheros(rutas_ficheros);
			if (ventana.mostrarContenidoPorPantalla(new Contenido(ficheros))) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						ventana.lblBotonServidor.setImage(ventana.lblBotonServidorImage);
						ventana.lblHayServidor
								.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_verde.gif"));
					}
				});
				ficherosAnterior = ficheros;
				stringAnterior = null;
				ventana.multicastControl.cliente.cambioServidor();
			}
		}
	}
	
	public void pegarTexto(String texto) {
		ventana.display.asyncExec(new Runnable() {
			public void run() {
				long ventanaActiva = getVentanaActiva();
				DatoSeleccion seleccion = new DatoSeleccion(texto);
				clip.setContenidoClipboard(seleccion);
				setVentanaActiva(ventanaActiva);
				PulsarTeclas.pegar();
				//ListaHistorial.anyadirHistoria(new Historial(texto, Historial.fechaActual()));
				clip.setContenidoClipboard(backupClip);
			}
		});
	}

	/**
	 * vuelca los datos de texto, imagen o ficheros recogidos por {@link Servidor}
	 * mostrados por {@link Ventana} en el clipboard del sistema {@link Clip}, pulsa
	 * las teclas Ctrl + v para pegar {@link PulsarTeclas} si los datos son de texto
	 * o imagen, o abre una ventana de clase {@link DirectoryDialog} si son ficheros
	 * y restaura el portapapeles al estado de antes de pulsar Ctrl + v.
	 */

	public void pegarContenido() {
		if (ventana.contenido != null) {
			Transferable backupClip = (Transferable) clip.getContenidoClipboard();
			if (ventana.contenido.tipo != Tipo.Ficheros) {
				if (ventana.contenido.tipo == Tipo.Texto || ventana.contenido.tipo == Tipo.Html) {
					ventana.display.asyncExec(new Runnable() {
						public void run() {
							long ventanaActiva = getVentanaActiva();
							String texto = ventana.contenido.texto;
							DatoSeleccion seleccion = new DatoSeleccion(texto);
							clip.setContenidoClipboard(seleccion);
							setVentanaActiva(ventanaActiva);
							PulsarTeclas.pegar();
							ListaHistorial.anyadirHistoria(new Historial(texto, Historial.fechaActual()));
							clip.setContenidoClipboard(backupClip);
						}
					});
				}
			} else {
				if (!clienteFicherosFuncionando) {
					ventana.display.asyncExec(new Runnable() {
						public void run() {
							clienteFicherosFuncionando = true;
							DirectoryDialog dialog = new DirectoryDialog(ventana.shlSwt);
							String ruta = dialog.open();
							if (ruta != null) {
								red.compartirFicheros.Cliente cliente = new Cliente(ventana, ventana.multicastControl);
								cliente.ruta = ruta;
								cliente.start();
							}else {
								clienteFicherosFuncionando = false;
							}
						}
					});

				}else {
					Ventana.mensajeTray("Espere a que se reciban los ficheros compartidos", MessageType.INFO);
				}
			}
		}
	}

	/**
	 * vuelca en el portapapeles del sistema {@link Clip} los datos de texto/imagen
	 * dependiendo del {@link Contenido} mostrado por {@link Ventana}
	 */

	public void copiarContenidoBtn() {
		if (ventana.contenido != null) {
			if (ventana.contenido.tipo != Tipo.Ficheros) {
				if (ventana.contenido.tipo == Tipo.Texto || ventana.contenido.tipo == Tipo.Html) {
					ventana.display.asyncExec(new Runnable() {
						public void run() {
							String texto = ventana.contenido.texto;
							DatoSeleccion seleccion = new DatoSeleccion(texto);
							clip.setContenidoClipboard(seleccion);
							ListaHistorial.anyadirHistoria(new Historial(texto, Historial.fechaActual()));
						}
					});
				}
			}
		}
	}

	/**
	 * {@link Cliente} realiza una peticion al servidor {@link Servidor} y lo recibe
	 * en la ruta introducida en {@link DirectoryDialog}
	 * 
	 * @param numero entero con la posición del contenido a pedir.
	 */

	public void copiarContenidoFicheroBtn(int numero) {
		if (ventana.contenido != null && !clienteFicherosFuncionando) {

			if (ventana.contenido.tipo == Tipo.Ficheros) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						clienteFicherosFuncionando = true;
						DirectoryDialog dialog = new DirectoryDialog(ventana.shlSwt);
						String ruta = dialog.open();
						if (ruta != null) {
							red.compartirFicheros.Cliente cliente = new Cliente(ventana, ventana.multicastControl,
									numero);
							cliente.ruta = ruta;
							cliente.start();
						}else {
							clienteFicherosFuncionando = false;
						}
					}
				});
			}
		}
		if (clienteFicherosFuncionando) {
			Ventana.mensajeTray("Espere a que se reciban los ficheros compartidos", MessageType.INFO);
		}
	}

	/**
	 * @return identificador de la ventana activa del sistema Windows.
	 */

	public long getVentanaActiva() {
		return OS.GetForegroundWindow();
	}

	public Clip getClip() {
		return clip;
	}

	/**
	 * Proporciona el foco del sistema Windows
	 * 
	 * @param ventana Es el identificador de la ventana a conceder el foco del
	 *                sistema Windows.
	 */

	public void setVentanaActiva(long ventana) {

		long ventanaActual = getVentanaActiva();
		int ventanaNumeroHilo = OS.GetWindowThreadProcessId((int) ventana, null);
		int otraVentanaNumeroHilo = OS.GetWindowThreadProcessId((int) ventanaActual, null);
		OS.AttachThreadInput(otraVentanaNumeroHilo, ventanaNumeroHilo, true);
		OS.SetForegroundWindow((int) ventana);
		OS.BringWindowToTop((int) ventana);
		OS.UpdateWindow((int) ventana);
		OS.SetActiveWindow((int) ventana);
		if (OS.IsIconic((int) ventana)) {
			OS.ShowWindow((int) ventana, OS.SW_SHOWMAXIMIZED);
		}

	}

}
