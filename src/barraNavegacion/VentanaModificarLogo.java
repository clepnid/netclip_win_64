package barraNavegacion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import usuarios.Dialogo;
import ventana.Ventana;

public class VentanaModificarLogo {
	public VentanaModificarLogo ventana;
	private Button btnModificar, btnCancelar;
	private List list;
	private Text campoTexto;
	private Shell shell;
	private int pos;
	private PanelIntroduccionTextos panelTitulo;

	public PanelIntroduccionTextos getPanelTitulo() {
		return panelTitulo;
	}

	public List getList() {
		return list;
	}

	public Button getBtnCancelar() {
		return btnCancelar;
	}

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}

	public Shell getShell() {
		return shell;
	}

	public int getPos() {
		return pos;
	}

	public VentanaModificarLogo(Ventana ventana) {

		shell = new Shell(ventana.shlSwt, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Modificar Ruta Logo");
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		GridData dataPanelAnyadir = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		shell.setLayout(new GridLayout(1, false));
		shell.setLayoutData(dataTable);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);

		Composite panelAnyadir = new Composite(shell, SWT.None);
		panelAnyadir.setLayout(new GridLayout(1, false));
		panelAnyadir.setLayoutData(dataPanelAnyadir);
		panelAnyadir.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		panelTitulo = new PanelIntroduccionTextos(panelAnyadir, SWT.None, "Ruta Logo:", 100);
		panelTitulo.setLayoutData(dataPanelAnyadir);
		
		panelTitulo.setTexto(BarraNavegacion.leerFichero().getRutaImagenLogo());

		Composite panelBotones = new Composite(shell, SWT.None);
		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setLayoutData(databotones);
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnModificar = new Button(panelBotones, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databotones);
		btnModificar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnCancelar = new Button(panelBotones, SWT.None);
		btnCancelar.setText("Cancelar");
		btnCancelar.setLayoutData(databotones);
		btnCancelar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		addListenerBotones(this);

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.pack();
		shell.setSize(600, 250);
		shell.open();
	}

	private static void addListenerBotones(VentanaModificarLogo panel) {
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(panel.getShell(), style);
				messageBox.setText("Ventana Emergente");
				messageBox.setMessage("¿Seguro quiere modificar Ruta Logo?");
				if (messageBox.open() == SWT.YES) {
					String nombre = panel.getPanelTitulo().getTextoIntroducido();
					if (nombre.equals("")) {
						new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca un Nombre");
					} else {
						BarraNavegacion.ModificiarRutaLogo(nombre);
						panel.getShell().close();
					}
					event.doit = true;
				} else {
					event.doit = false;
				}
			}
		});
		panel.getBtnCancelar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				panel.getShell().close();
			}
		});
	}
}