package historial;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import portapapeles.Clip;
import red.multicast.MulticastControl;
import ventana.Ventana;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class VentanaListaHistorial {

	public ArrayList<PanelHistoria> listaPaneles;
	private GridData grid;
	private Color yellow;
	public Composite panCuerpo;
	public ScrolledComposite scrolledComposite;
	private Display display;
	private MulticastControl controlMulticast;
	private Clip clip;
	private Text textBuscador;
	private VentanaListaHistorial ventanaListaHistorial;

	protected Shell shell;

	public VentanaListaHistorial(MulticastControl controlMulticast, Clip clip) {
		this.controlMulticast = controlMulticast;
		this.clip = clip;
	}

	public VentanaListaHistorial() {
		this.controlMulticast = null;
		this.clip = null;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		if (controlMulticast != null) {
			display = controlMulticast.ventana.display;
		} else {
			display = new Display();
		}
		createContents();
		anyadirHistorial();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void anyadirHistorialFichero(Historial historial, MulticastControl controlMulticast) {
		ventanaListaHistorial = this;
		display.asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed()) {
					listaPaneles.add(new PanelHistoriaFichero(ventanaListaHistorial, panCuerpo, scrolledComposite, SWT.BORDER, historial,
							controlMulticast));
					scrolledComposite.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					panCuerpo.layout();
				}
			}
		});
	}

	public void anyadirHistorialString(Historial historial, Clip clip) {
		ventanaListaHistorial = this;
		display.asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed()) {
					listaPaneles.add(new PanelHistoriaString(ventanaListaHistorial, panCuerpo, scrolledComposite, SWT.BORDER, historial, clip));
					scrolledComposite.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					panCuerpo.layout();
				}
			}
		});
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		if (controlMulticast != null) {
			shell = new Shell(controlMulticast.ventana.shlSwt, SWT.RESIZE | SWT.CLOSE);
		} else {
			shell = new Shell();
		}
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(550, 600);
		shell.setMinimumSize(550, 300);
		shell.setText(Ventana.idioma.getProperty("btnHistorial"));
		shell.setLayout(new GridLayout(1, false));

		Composite panelBuscador = new Composite(shell, SWT.NONE);
		panelBuscador.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panelBuscador.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		panelBuscador.setLayout(new GridLayout(2, false));

		Composite btnBuscador = new Composite(panelBuscador, SWT.NONE);
		btnBuscador.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnBuscador.setLayout(new GridLayout(2, false));
		btnBuscador.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		Label btnBuscadorImg = new Label(btnBuscador, SWT.NONE);
		btnBuscadorImg.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnBuscadorImg.setImage(PanelHistoria.getCheckedImage(display, "./src/imagenes/buscar.gif"));

		Label btnBuscadorTxt = new Label(btnBuscador, SWT.NONE);
		btnBuscadorTxt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnBuscadorTxt.setText(Ventana.idioma.getProperty("historial_buscar"));

		textBuscador = new Text(panelBuscador, SWT.BORDER);
		textBuscador.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBuscador.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 400;
		gd_text.heightHint = 30;
		textBuscador.setLayoutData(gd_text);
		textBuscador.setBounds(0, 0, 78, 26);

		textBuscador.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				buscarHistorialGrafico(textBuscador.getText());
			}
		});

		scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		panCuerpo = new Composite(scrolledComposite, SWT.PUSH);
		panCuerpo.setToolTipText("Contenido disponibles");
		panCuerpo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panCuerpo.setLayout(new GridLayout(1, false));
		grid = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		panCuerpo.setLayoutData(grid);
		scrolledComposite.setContent(panCuerpo);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setContent(panCuerpo);
		listaPaneles = new ArrayList<PanelHistoria>();

		Composite panelBajo = new Composite(shell, SWT.NONE);
		panelBajo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panelBajo.setLayout(new GridLayout(1, false));
		panelBajo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		Composite panelOrdenar = new Composite(panelBajo, SWT.NONE);
		panelOrdenar.setSize(158, 38);
		panelOrdenar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panelOrdenar.setLayout(new GridLayout(2, false));

		Label lblOrdenar = new Label(panelOrdenar, SWT.NONE);
		lblOrdenar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOrdenar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOrdenar.setText(Ventana.idioma.getProperty("historial_ordenar")+":");

		Combo radio = new Combo(panelOrdenar, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd_radio = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_radio.widthHint = 250;
		radio.setLayoutData(gd_radio);
		radio.add(Ventana.idioma.getProperty("historial_fecha3"));
		radio.add(Ventana.idioma.getProperty("historial_fecha4"));
		radio.add(Ventana.idioma.getProperty("historial_fecha1"));
		radio.add(Ventana.idioma.getProperty("historial_fecha2"));
		radio.setText(Ventana.idioma.getProperty("historial_fecha1"));

		radio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				ordenar(radio.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				ordenar(radio.getText());

			}
		});
		yellow = display.getSystemColor(SWT.COLOR_YELLOW);

		scrolledComposite.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		panCuerpo.layout();
	}

	protected void anyadirHistorial() {

		Thread thread = new Thread() {
			public void run() {
				for (Historial historial : ListaHistorial.ordenarFechaAscendente()) {
					if (historial.esFichero) {
						anyadirHistorialFichero(historial, controlMulticast);
					} else {
						anyadirHistorialString(historial, clip);
					}
				}
			}
		};

		thread.start();
	}

	protected void vaciarHistorialGrafico() {
		for (PanelHistoria panelHistoria : listaPaneles) {
			panelHistoria.setVisible(false);
			panelHistoria.gd_panObjeto.exclude = true;
		}
		panCuerpo.layout();
		scrolledComposite.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		listaPaneles = new ArrayList<PanelHistoria>();
	}

	protected void subrayarContenido(String palabra, int indice, PanelHistoria panelHistoria) {
		StyleRange range = new StyleRange(indice, palabra.length(), null, yellow);
		panelHistoria.setTextEstilo(range);
	}

	protected void subrayarFecha(String palabra, int indice, PanelHistoria panelHistoria) {
		StyleRange range = new StyleRange(indice, palabra.length(), null, yellow);
		panelHistoria.setFechaEstilo(range);
	}

	protected void buscarHistorialGrafico(String palabra) {
		int indice = 0;
		for (PanelHistoria panelHistoria : listaPaneles) {
			Historial hist = panelHistoria.getHistorial();
			if (hist.esFichero) {
				indice = hist.nombreFichero.indexOf(palabra);
				if (indice != -1) {
					if (!panelHistoria.getVisible()) {
						panelHistoria.setVisible(true);
						panelHistoria.gd_panObjeto.exclude = false;
					}
					panelHistoria.removeStyleRange();
					subrayarContenido(palabra, indice, panelHistoria);
				} else {
					indice = hist.getFecha().indexOf(palabra);
					if (indice != -1) {
						if (!panelHistoria.getVisible()) {
							panelHistoria.setVisible(true);
							panelHistoria.gd_panObjeto.exclude = false;
						}
						panelHistoria.removeStyleRange();
						subrayarFecha(palabra, indice, panelHistoria);
					} else if (panelHistoria.getVisible()) {
						panelHistoria.setVisible(false);
						panelHistoria.gd_panObjeto.exclude = true;
					}
				}
			} else {
				indice = hist.contenido.indexOf(palabra);
				if (indice != -1) {
					if (!panelHistoria.getVisible()) {
						panelHistoria.setVisible(true);
						panelHistoria.gd_panObjeto.exclude = false;
					}
					panelHistoria.removeStyleRange();
					subrayarContenido(palabra, indice, panelHistoria);
				} else {
					indice = hist.getFecha().indexOf(palabra);
					if (indice != -1) {
						if (!panelHistoria.getVisible()) {
							panelHistoria.setVisible(true);
							panelHistoria.gd_panObjeto.exclude = false;
						}
						panelHistoria.removeStyleRange();
						subrayarFecha(palabra, indice, panelHistoria);
					} else if (panelHistoria.getVisible()) {
						panelHistoria.setVisible(false);
						panelHistoria.gd_panObjeto.exclude = true;
					}
				}
			}
		}
		panCuerpo.layout();
		scrolledComposite.setMinSize(panCuerpo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	protected void ordenar(String string) {
		if (string.equals(Ventana.idioma.getProperty("historial_fecha3"))) {
			ordenarNombreAscendente();
		} else if (string.equals(Ventana.idioma.getProperty("historial_fecha4"))) {
			ordenarNombreDescendente();
		} else if (string.equals(Ventana.idioma.getProperty("historial_fecha1"))) {
			ordenarFechaAscendente();
		} else if (string.equals(Ventana.idioma.getProperty("historial_fecha2"))) {
			ordenarFechaDescendente();
		}
	}

	protected void ordenarNombreDescendente() {
		vaciarHistorialGrafico();
		Thread thread = new Thread() {
			public void run() {
				for (Historial historial : ListaHistorial.ordenarNombreDescendente()) {
					if (historial.esFichero) {
						anyadirHistorialFichero(historial, controlMulticast);
					} else {
						anyadirHistorialString(historial, clip);
					}
				}
			}
		};

		thread.start();
	}

	protected void ordenarNombreAscendente() {
		vaciarHistorialGrafico();
		Thread thread = new Thread() {
			public void run() {
				for (Historial historial : ListaHistorial.ordenarNombreAscendente()) {
					if (historial.esFichero) {
						anyadirHistorialFichero(historial, controlMulticast);
					} else {
						anyadirHistorialString(historial, clip);
					}
				}
			}
		};

		thread.start();
	}

	protected void ordenarFechaAscendente() {
		vaciarHistorialGrafico();
		Thread thread = new Thread() {
			public void run() {
				for (Historial historial : ListaHistorial.ordenarFechaAscendente()) {
					if (historial.esFichero) {
						anyadirHistorialFichero(historial, controlMulticast);
					} else {
						anyadirHistorialString(historial, clip);
					}
				}
			}
		};

		thread.start();
	}

	protected void ordenarFechaDescendente() {
		vaciarHistorialGrafico();
		Thread thread = new Thread() {
			public void run() {
				for (Historial historial : ListaHistorial.ordenarFechaDescendente()) {
					if (historial.esFichero) {
						anyadirHistorialFichero(historial, controlMulticast);
					} else {
						anyadirHistorialString(historial, clip);
					}
				}
			}
		};

		thread.start();
	}

}
