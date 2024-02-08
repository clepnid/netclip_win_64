package usuarios;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelModificarUsuario extends Composite {

	public VentanaModificarUsuario ventana;
	private Table table;
	private Text campoTexto;
	private Button btnModificar;

	public VentanaModificarUsuario getVentana() {
		return ventana;
	}

	public Table getTable() {
		return table;
	}

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}


	public PanelModificarUsuario(Composite parent, int style, VentanaModificarUsuario ventana) {
		super(parent, style);
		this.ventana = ventana;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		this.setLayout(new GridLayout(2, false));

		// se expande horizontal y vertical.

		campoTexto = new Text(this, SWT.BORDER | SWT.RESIZE);
		campoTexto.setLayoutData(data);
		campoTexto.setText(getVentana().getListaUsuarios().get(getVentana().getPos()).getUsuario());

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(dataTable);

		GridData databtnmodificar = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnModificar = new Button(this, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databtnmodificar);

		addItems(SistemaUsuarios.deserializar());
		marcarGrupos(this);

		addListenerTable(this);
		addListenerModificar(this);

	}

	// metodo importante que devuelve la lista de formatos añadidas a la tabla por
	// el usuario.

	public ArrayList<String> getFormatosSeleccionados() {
		ArrayList<String> listAux = new ArrayList<String>();

		for (TableItem tableItem : this.table.getItems()) {
			if (tableItem.getChecked()) {
				listAux.add(tableItem.getText());
			}
		}

		return listAux;
	}

	private static void addListenerTable(PanelModificarUsuario panel) {
		panel.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				System.out.println(event.item + " " + string);
			}
		});
	}

	private static void addListenerModificar(PanelModificarUsuario panel) {
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String nombreNuevoUsuario = panel.getCampoTexto().getText();
				if (!nombreNuevoUsuario.equals("")) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(panel.getShell(), style);
					messageBox.setText("Ventana Emergente");
					messageBox.setMessage("¿Seguro quiere modificar el usuario?");
					if (messageBox.open() == SWT.YES) {
						modificar(panel);
						panel.getShell().close();
						event.doit = true;
					} else {
						event.doit = false;
					}

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Introduzca un nombre de usuario");
				}
			}
		});
	}

	private static void modificar(PanelModificarUsuario panel) {
		ArrayList<String> listaGruposNuevoUsuario = panel.getFormatosSeleccionados();
		String nombreNuevoUsuario = panel.getCampoTexto().getText();
		SistemaUsuarios sis = SistemaUsuarios.deserializar();
		sis.cambiarNombreUsuario(nombreNuevoUsuario, panel.getVentana().getPos());
		sis.getUsuario(nombreNuevoUsuario).getGrupos().clear();
		if (!listaGruposNuevoUsuario.isEmpty()) {
			sis.anyadirGrupoAUsuario(nombreNuevoUsuario, listaGruposNuevoUsuario);
		} else {
			listaGruposNuevoUsuario.add("New");
			sis.anyadirGrupoAUsuario(nombreNuevoUsuario, listaGruposNuevoUsuario);
		}
		panel.getVentana().getVentanaAdministrarUsuarios().addItems(sis.getUsuarios());
	}

	public void addItems(SistemaUsuarios sistemaUsuarios) {
		resetearComponente();
		for (String elemento : sistemaUsuarios.getGrupos()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(elemento);
			item.setChecked(false);
		}
	}

	private void marcarGrupos(PanelModificarUsuario panel) {
		Usuario us = SistemaUsuarios.deserializar().getUsuario(panel.getVentana().getPos());
		for (TableItem iterable_element : panel.getTable().getItems()) {
			if (us.grupoExistente(iterable_element.getText()) != -1) {
				iterable_element.setChecked(true);
			}
		}
	}

	private void resetearComponente() {
		this.table.removeAll();
	}

}
