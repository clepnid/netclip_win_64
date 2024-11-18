package comandos;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import portapapeles.Clip;
import portapapeles.Ficheros;
import red.multicast.MulticastControl;
import red.multicast.MulticastRedScannerScript;
import teclado.PulsarTeclas;

public class Enviar {
	protected Shell shell;
	protected ArrayList<File> ficheros;
	protected MulticastRedScannerScript escaner;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Thread.sleep(1900);
			Clip clip = new Clip();
			FlavorListener flavorListener = new FlavorListener() {

				@Override
				public void flavorsChanged(FlavorEvent e) {
					clip.copiadoDelSistema = true;
					clip.recogido = false;
					clip.proccessClipboard(clip.clipboard);
					clip.recogido = true;
					clip.copiadoDelSistema = false;
				}
			};
			clip.setFlavorListener(flavorListener);
			clip.clipboard = Clip.resetearClipboard(clip.clipboard, flavorListener);
			Transferable backupClip = (Transferable) Clip.getContenidoClipboard(clip.clipboard, flavorListener);
			String myString = "Clepnid";
			StringSelection stringSelection = new StringSelection(myString);
			clip.clipboard.setContents(stringSelection, null);
			try {
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			clip.tipoContenido = "";
			clip.contenidoRecogido = null;
			clip.pulsadoTeclas = true;
			PulsarTeclas.copiar();

			while (!clip.recogido && clip.copiadoDelSistema) {
				Thread.sleep(100);
			}
			clip.recogido = false;
			clip.pulsadoTeclas = false;
			if (clip.recogido == null) {
				clip.tipoContenido = "";
			} else {
				if (clip.tipoContenido.equals("ficheros")) {
					clip.setContenidoClipboard(backupClip);
					java.util.List<?> filesAux = ((java.util.List<?>) clip.contenidoRecogido);
					Ficheros ficherosAux = new Ficheros(filesAux);
					ArrayList<String> files = new ArrayList<String>();
					for (File fileAux : ficherosAux.ficheros) {
						files.add(fileAux.getAbsolutePath());
					}
					if (files.size() != 0) {
						MulticastRedScannerScript escaner = new MulticastRedScannerScript();
						escaner.start();
						Enviar window = new Enviar(files, escaner);
						window.open();

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Enviar(ArrayList<String> files, MulticastRedScannerScript escaner) {
		this.escaner = escaner;
		ficheros = new ArrayList<File>();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String string : files) {
			string.replace("/", "\\");
			ficheros.add(new File(string));
		}
	}

	/**
	 * Open the window.
	 * 
	 * @throws ClassNotFoundException
	 */
	public void open() throws ClassNotFoundException {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
				escaner.close();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(450, 300);
		shell.setText("Enviar a ...");

		shell.setMinimumSize(400, 0);
		shell.setLayout(new GridLayout(1, false));

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
		for (String ip : escaner.listaIps.listaNombres) {
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
		btnEnviar.setText("Enviar");
		btnEnviar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				ArrayList<String> listaIp = new ArrayList<String>();
				for (int i = 0; i < list.getSelection().length; i++) {
					if ((listaIp.size()) < list.getSelection().length) {
						for (String nombre : escaner.listaIps.listaNombres) {
							if ((listaIp.size()) < list.getSelection().length) {
								if (!nombre.equals("")) {
									if (nombre.equals(list.getSelection()[i])) {
										listaIp.add(escaner.listaIps.getIpdeNombre(nombre));
										break;
									} else {
										if (list.getSelection()[i].startsWith(nombre)) {
											if (!(list.getSelection()[i].length() <= nombre.length())) {
												listaIp.add(escaner.listaIps.getIpdeNombre(nombre));
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

				red.enviar.Servidor servidor = new red.enviar.Servidor(listaIp);
				servidor.setIpServidor(MulticastControl.getMyIps().get(0));
				servidor.setFicheros(ficheros);
				servidor.start();
			}
		});

		Button btnCancelar = new Button(composite_1, SWT.PUSH);
		btnCancelar.setText("Cancelar");

		btnCancelar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.close();
			}
		});

	}
}
