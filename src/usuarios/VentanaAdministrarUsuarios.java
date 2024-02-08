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

public class VentanaAdministrarUsuarios {
	private ArrayList<Usuario> listaUsuarios;
	public VentanaAdministrarUsuarios ventana;
	private final List list;
	private Button btnGuardar, btnEliminar, btnModificar;
	private Composite panelUsuarios, panelBotones;
	private PanelAnyadirUsuario panelAnyadirUsuario;
	private Shell shell;

	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public PanelAnyadirUsuario getPanelAnyadirUsuario() {
		return panelAnyadirUsuario;
	}

	public List getList() {
		return list;
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

	public VentanaAdministrarUsuarios(ventana.Ventana ventana) {

		shell = new Shell(ventana.shlSwt, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		this.listaUsuarios = new ArrayList<Usuario>();
		shell.setText("Administrar Usuarios");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;

		GridData databtnanyadir = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		panelAnyadirUsuario = new PanelAnyadirUsuario(shell, SWT.None, this);
		panelAnyadirUsuario.setLayoutData(databtnanyadir);

		panelUsuarios = new Composite(shell, SWT.None);
		panelUsuarios.setLayout(new GridLayout(1, false));
		panelUsuarios.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		list = new List(panelUsuarios, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		panelUsuarios.setLayoutData(dataTable);

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
		addItems(SistemaUsuarios.deserializar().getUsuarios());
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	public void guardar(SistemaUsuarios sistemaUsuarios) {
		SistemaUsuarios.serializar(sistemaUsuarios);
	}

	private static void addListenerBotones(VentanaAdministrarUsuarios panel) {
		panel.getBtnEliminar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(panel.getShell(), style);
					messageBox.setText("Ventana Emergente");
					messageBox.setMessage("¿Seguro quiere eliminar el usuario: "
							+ panel.getList().getItem(panel.getList().getSelectionIndex()) + "?");
					if (messageBox.open() == SWT.YES) {
						panel.eliminarUsuario(panel.getList().getSelectionIndex());
						event.doit = true;
					} else {
						event.doit = false;
					}

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Debe seleccionar un usuario");
				}
			}
		});
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					new VentanaModificarUsuario(panel.getShell(), panel, panel.getList().getSelectionIndex());

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Debe seleccionar un usuario");
				}
			}
		});
	}

	public boolean eliminarUsuario(int posicion) {
		if (posicion > this.getListaUsuarios().size() - 1 || posicion < 0) {
			return false;
		}
		SistemaUsuarios sis = SistemaUsuarios.deserializar();
		sis.eliminarUsuario(sis.getUsuario(this.getList().getItem(posicion)));
		this.getList().remove(posicion);
		this.getListaUsuarios().remove(posicion);
		return true;
	}

	public boolean eliminarUsuario(String nombreUsuario) {
		int posicion = this.getList().indexOf(nombreUsuario);
		return eliminarUsuario(posicion);
	}

	public void addItems(ArrayList<Usuario> items) {
		resetearComponente();
		for (Usuario elemento : items) {
			list.add(elemento.getUsuario());
			listaUsuarios.add(elemento);
		}
	}

	private void resetearComponente() {
		this.listaUsuarios.clear();
		this.list.removeAll();
	}

}