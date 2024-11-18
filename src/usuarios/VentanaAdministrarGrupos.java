package usuarios;

import java.util.ArrayList;
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

public class VentanaAdministrarGrupos {
	public VentanaAdministrarGrupos ventana;
	final List list;
	private Button btnGuardar, btnAnyadir, btnEliminar, btnModificar;
	private Text campoTexto;
	private Composite panelTexto, panelGrupo, panelBotones;
	private Shell shell;

	public List getList() {
		return list;
	}

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnAnyadir() {
		return btnAnyadir;
	}

	public Button getBtnEliminar() {
		return btnEliminar;
	}

	public Button getBtnGuardar() {
		return btnGuardar;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}

	public Shell getShell() {
		return shell;
	}

	public VentanaAdministrarGrupos(ventana.Ventana ventana) {

		shell = new Shell(ventana.shlSwt, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Administrar Grupos");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		panelTexto = new Composite(shell, SWT.None);
		panelTexto.setLayout(new GridLayout(2, false));
		panelTexto.setLayoutData(data);
		panelTexto.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		panelGrupo = new Composite(shell, SWT.None);
		panelGrupo.setLayout(new GridLayout(1, false));
		panelGrupo.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		campoTexto = new Text(panelTexto, SWT.BORDER | SWT.RESIZE);
		campoTexto.setLayoutData(data);

		GridData databtnanyadir = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnAnyadir = new Button(panelTexto, SWT.None);
		btnAnyadir.setText("Añadir");
		btnAnyadir.setLayoutData(databtnanyadir);

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		list = new List(panelGrupo, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		panelGrupo.setLayoutData(dataTable);

		list.setLayoutData(dataTable);

		panelBotones = new Composite(shell, SWT.None);

		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		panelBotones.setLayoutData(data);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnEliminar = new Button(panelBotones, SWT.None);
		btnEliminar.setText("Eliminar");
		btnEliminar.setLayoutData(databotones);
		btnModificar = new Button(panelBotones, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databotones);

		addListenerBotones(this);
		addListenerAnyadir(this);
		addItems(SistemaUsuarios.deserializar().getGrupos());
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	private static void addListenerBotones(VentanaAdministrarGrupos panel) {
		panel.getBtnEliminar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(panel.getShell(), style);
					messageBox.setText("Ventana Emergente");
					messageBox.setMessage("¿Seguro quiere eliminar el grupo: "
							+ panel.getList().getItem(panel.getList().getSelectionIndex()) + "?");
					if (messageBox.open() == SWT.YES) {
						SistemaUsuarios.deserializar()
								.eliminarGrupo(panel.getList().getItem(panel.getList().getSelectionIndex()));
						panel.getList().remove(panel.getList().getSelectionIndex());
						event.doit = true;
					} else {
						event.doit = false;
					}

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Debe seleccionar un grupo");
				}
			}
		});
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					new VentanaModificarGrupos(panel.getShell(), panel.getList(), panel.getList().getSelectionIndex());

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Debe seleccionar un grupo");
				}
			}
		});
	}

	private static void addListenerAnyadir(VentanaAdministrarGrupos panel) {
		panel.getCampoTexto().addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				boolean encontrado = false;
				for (String item : panel.getList().getItems()) {
					if (item.equals(panel.getCampoTexto().getText())) {
						encontrado = true;
					}
				}
				if (!encontrado) {
					String nombreUsuario = panel.getCampoTexto().getText();
					panel.getList().add(nombreUsuario);
					SistemaUsuarios.deserializar().anyadirGrupo(nombreUsuario);
					panel.getCampoTexto().setText("");
				}
			}
		});

		panel.getBtnAnyadir().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				boolean encontrado = false;
				for (String item : panel.getList().getItems()) {
					if (item.equals(panel.getCampoTexto().getText())) {
						encontrado = true;
					}
				}
				if (!encontrado) {
					String nombreUsuario = panel.getCampoTexto().getText();
					if (nombreUsuario.equals("Oculto")) {
						panel.getCampoTexto().setText("");
					} else {
						panel.getList().add(nombreUsuario);
						SistemaUsuarios.deserializar().anyadirGrupo(nombreUsuario);
						panel.getCampoTexto().setText("");
					}
				} else {
					panel.getCampoTexto().setText("");
				}
			}
		});

	}

	public void addItems(ArrayList<String> items) {
		resetearComponente();
		for (String elemento : items) {
			list.add(elemento);
		}
	}

	private void resetearComponente() {
		campoTexto.setText("");
		this.list.removeAll();
	}

}