package ventanaGestionModulo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Ventana {
	boolean moduloFicheroSeleccionado = true;
	boolean moduloSistemaSeleccionado = false;
	boolean moduloVistaSeleccionado = false;
	boolean moduloIconoSeleccionado = false;
	final Composite content;
	final StackLayout layoutStack;
	private PanelConfiguracionModuloFichero panelModuloFichero;
	private PanelConfiguracionModuloSistema panelModuloSistema;
	private PanelConfiguracionModuloVista panelModuloVista;
	private PanelConfiguracionModuloIcono panelModuloIcono;
	private PanelSeleccionCarpetaProyecto panelFicheroModulo;
	private Button buttonModuloFichero, buttonModuloSistema, buttonModuloVista, buttonModuloIcono;
	
	private Shell shell;

	public Ventana(ventana.Ventana ventana) {

		Shell shell = new Shell(ventana.shlSwt);
		shell.setText("Ventana Gestion Modulos");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				ventana.getMenuBar().setVentanaGestionModuloAbierta(false);
			}
		});
		GridData dataCompositeIntroduccionFichero = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

		panelFicheroModulo = new PanelSeleccionCarpetaProyecto(shell, 0, "Abrir Fichero: ", 200, this);
		panelFicheroModulo.setLayoutData(dataCompositeIntroduccionFichero);

		GridData dataCompositeCambio = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dataCompositeCambio.widthHint = 600;
		dataCompositeCambio.heightHint = 50;
		dataCompositeCambio.verticalIndent = 10;

		GridData dataBotonCambio = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

		GridData dataContenido = new GridData(SWT.FILL, SWT.FILL, true, true);

		final Composite compositeCambio = new Composite(shell, SWT.NONE);
		compositeCambio.setLayout(new GridLayout(4, false));

		compositeCambio.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		compositeCambio.setLayoutData(dataCompositeCambio);

		buttonModuloFichero = new Button(compositeCambio, SWT.RADIO);
		buttonModuloFichero.setText("Modulo Fichero");
		buttonModuloFichero.setLayoutData(dataBotonCambio);
		buttonModuloFichero.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		buttonModuloSistema = new Button(compositeCambio, SWT.RADIO);
		buttonModuloSistema.setText("Modulo Sistema");
		buttonModuloSistema.setLayoutData(dataBotonCambio);
		buttonModuloSistema.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		buttonModuloVista = new Button(compositeCambio, SWT.RADIO);
		buttonModuloVista.setText("Modulo Vista");
		buttonModuloVista.setLayoutData(dataBotonCambio);
		buttonModuloVista.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		buttonModuloIcono = new Button(compositeCambio, SWT.RADIO);
		buttonModuloIcono.setText("Modulo Icono");
		buttonModuloIcono.setLayoutData(dataBotonCambio);
		buttonModuloIcono.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		layoutStack = new StackLayout();

		content = new Composite(shell, SWT.NONE);
		content.setLayoutData(dataContenido);
		content.setLayout(layoutStack);
		content.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		panelModuloFichero = new PanelConfiguracionModuloFichero(content, 0, this);
		panelModuloSistema = new PanelConfiguracionModuloSistema(content, 0, this);
		panelModuloVista = new PanelConfiguracionModuloVista(content, 0, this);
		panelModuloIcono = new PanelConfiguracionModuloIcono(content, 0, this);

		layoutStack.topControl = panelModuloFichero;
		buttonModuloFichero.setSelection(true);

		buttonModuloFichero.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cambiarVistaModeloFichero();

			}
		});
		buttonModuloSistema.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cambiarVistaModeloSistema();

			}
		});
		buttonModuloVista.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cambiarVistaModeloVista();

			}
		});
		buttonModuloIcono.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cambiarVistaModeloIcono();

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
	}

	public void cambiarVistaModeloVista() {
		if (!moduloVistaSeleccionado) {
			layoutStack.topControl = panelModuloVista;
			content.layout();
			moduloFicheroSeleccionado = false;
			moduloSistemaSeleccionado = false;
			moduloVistaSeleccionado = true;
			moduloIconoSeleccionado = false;
			buttonModuloFichero.setSelection(false);
			buttonModuloSistema.setSelection(false);
			buttonModuloVista.setSelection(true);
			buttonModuloIcono.setSelection(false);
		}
	}

	public void cambiarVistaModeloSistema() {
		if (!moduloSistemaSeleccionado) {
			layoutStack.topControl = panelModuloSistema;
			content.layout();
			moduloFicheroSeleccionado = false;
			moduloSistemaSeleccionado = true;
			moduloVistaSeleccionado = false;
			moduloIconoSeleccionado = false;
			buttonModuloFichero.setSelection(false);
			buttonModuloSistema.setSelection(true);
			buttonModuloVista.setSelection(false);
			buttonModuloIcono.setSelection(false);
		}
	}

	public void cambiarVistaModeloFichero() {
		if (!moduloFicheroSeleccionado) {
			layoutStack.topControl = panelModuloFichero;
			content.layout();
			moduloFicheroSeleccionado = true;
			moduloSistemaSeleccionado = false;
			moduloVistaSeleccionado = false;
			moduloIconoSeleccionado = false;
			buttonModuloFichero.setSelection(true);
			buttonModuloSistema.setSelection(false);
			buttonModuloVista.setSelection(false);
			buttonModuloIcono.setSelection(false);
		}
	}

	public void cambiarVistaModeloIcono() {
		if (!moduloIconoSeleccionado) {
			layoutStack.topControl = panelModuloIcono;
			content.layout();
			moduloFicheroSeleccionado = false;
			moduloSistemaSeleccionado = false;
			moduloVistaSeleccionado = false;
			moduloIconoSeleccionado = true;
			buttonModuloFichero.setSelection(false);
			buttonModuloSistema.setSelection(false);
			buttonModuloVista.setSelection(false);
			buttonModuloIcono.setSelection(true);
		}
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public PanelConfiguracionModuloFichero getPanelModuloFichero() {
		return panelModuloFichero;
	}

	public PanelConfiguracionModuloSistema getPanelModuloSistema() {
		return panelModuloSistema;
	}

	public PanelConfiguracionModuloVista getPanelModuloVista() {
		return panelModuloVista;
	}

	public PanelConfiguracionModuloIcono getPanelModuloIcono() {
		return panelModuloIcono;
	}

	public Button getButtonModuloFichero() {
		return buttonModuloFichero;
	}

	public Button getButtonModuloSistema() {
		return buttonModuloSistema;
	}

	public Button getButtonModuloVista() {
		return buttonModuloVista;
	}

	public Button getButtonModuloIcono() {
		return buttonModuloIcono;
	}

	public PanelSeleccionCarpetaProyecto getPanelFicheroModulo() {
		return panelFicheroModulo;
	}

	public void setPanelFicheroModulo(PanelSeleccionCarpetaProyecto panelFicheroModulo) {
		this.panelFicheroModulo = panelFicheroModulo;
	}

}