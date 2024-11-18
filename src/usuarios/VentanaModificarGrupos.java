package usuarios;

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

public class VentanaModificarGrupos {
	public VentanaModificarGrupos ventana;
	private Button btnCancelar, btnModificar;
	private Text campoTexto;
	private Composite panelTexto, panelBotones;
	private Shell shell;
	private List lista;
	private int pos;

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}

	public Shell getShell() {
		return shell;
	}

	public List getLista() {
		return lista;
	}

	public int getPos() {
		return pos;
	}

	public Button getBtnCancelar() {
		return btnCancelar;
	}

	public VentanaModificarGrupos(Shell padre, List lista, int posicion) {

		this.lista = lista;
		this.pos = posicion;

		shell = new Shell(padre, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Administrar Grupos");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		panelTexto = new Composite(shell, SWT.None);
		panelTexto.setLayout(new GridLayout(1, false));
		panelTexto.setLayoutData(data);
		panelTexto.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		campoTexto = new Text(panelTexto, SWT.BORDER | SWT.RESIZE);
		campoTexto.setLayoutData(data);
		campoTexto.setText(lista.getItem(posicion));

		// se expande horizontal y vertical.

		panelBotones = new Composite(shell, SWT.None);
		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		panelBotones.setLayoutData(data);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnModificar = new Button(panelBotones, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databotones);
		btnCancelar = new Button(panelBotones, SWT.None);
		btnCancelar.setText("Cancelar");
		btnCancelar.setLayoutData(databotones);

		addListenerBotones(this);
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	private static void addListenerBotones(VentanaModificarGrupos panel) {
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(panel.getShell(), style);
				messageBox.setText("Ventana Emergente");
				String nombreOriginal = panel.getLista().getItem(panel.getPos());
				String nombreNuevo = panel.getCampoTexto().getText();
				messageBox.setMessage("¿Seguro quiere modificar el grupo: " + nombreOriginal
						+ " por " + nombreNuevo + "?");
				if (messageBox.open() == SWT.YES) {
					panel.getLista().setItem(panel.getPos(), nombreNuevo);
					SistemaUsuarios.deserializar().cambiarNombreGrupo(nombreOriginal, nombreNuevo);
					panel.getShell().close();
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