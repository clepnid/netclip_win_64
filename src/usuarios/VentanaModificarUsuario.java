package usuarios;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class VentanaModificarUsuario {
	public VentanaModificarUsuario ventana;
	private Button btnCancelar, btnModificar;
	private Text campoTexto;
	private ArrayList<Usuario> listaUsuarios;
	private Shell shell;
	private List lista;
	private int pos;
	private VentanaAdministrarUsuarios ventanaAdministrarUsuarios;
	
	public VentanaAdministrarUsuarios getVentanaAdministrarUsuarios() {
		return ventanaAdministrarUsuarios;
	}
	
	public Text getCampoTexto() {
		return campoTexto;
	}

	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
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

	public VentanaModificarUsuario(Shell padre, VentanaAdministrarUsuarios ventanaAdministrarUsuarios, int posicion) {
		
		this.ventanaAdministrarUsuarios = ventanaAdministrarUsuarios;
		this.lista = ventanaAdministrarUsuarios.getList();
		this.pos = posicion;
		this.listaUsuarios = ventanaAdministrarUsuarios.getListaUsuarios();

		shell = new Shell(padre, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Modificar Usuarios");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		PanelModificarUsuario mod = new PanelModificarUsuario(shell, posicion, this);

		mod.setLayout(new GridLayout(1, false));

		mod.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		shell.setLayoutData(dataTable);
		
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	
}