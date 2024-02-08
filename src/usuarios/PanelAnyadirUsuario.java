package usuarios;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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

public class PanelAnyadirUsuario extends Composite {

	public VentanaAdministrarUsuarios ventana;
	private Table table;
	private Text campoTexto;
	private Button btnAnyadir;

	public VentanaAdministrarUsuarios getVentana() {
		return ventana;
	}

	public Table getTable() {
		return table;
	}

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnAnyadir() {
		return btnAnyadir;
	}

	public PanelAnyadirUsuario(Composite parent, int style, VentanaAdministrarUsuarios ventana) {
		super(parent, style);
		this.ventana = ventana;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		this.setLayout(new GridLayout(3, false));

		// se expande horizontal y vertical.

		campoTexto = new Text(this, SWT.BORDER | SWT.RESIZE);
		campoTexto.setLayoutData(data);

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(dataTable);

		GridData databtnanyadir = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnAnyadir = new Button(this, SWT.None);
		btnAnyadir.setText("Añadir");
		btnAnyadir.setLayoutData(databtnanyadir);

		addItems(SistemaUsuarios.deserializar());

		addListenerTable(this);
		addListenerAnyadir(this);

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

	private static void addListenerTable(PanelAnyadirUsuario panel) {
		panel.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				System.out.println(event.item + " " + string);
			}
		});
	}

	private static void addListenerAnyadir(PanelAnyadirUsuario panel) {
		panel.getBtnAnyadir().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ArrayList<String> listaGruposNuevoUsuario = panel.getFormatosSeleccionados();
				String nombreNuevoUsuario = panel.getCampoTexto().getText();
				if (!nombreNuevoUsuario.equals("")) {
					Usuario nuevoUsuario = null;
					if (listaGruposNuevoUsuario.isEmpty()) {
						nuevoUsuario = new Usuario(nombreNuevoUsuario);
					} else {
						nuevoUsuario = new Usuario(nombreNuevoUsuario, listaGruposNuevoUsuario);
					}
					if (nuevoUsuario != null) {
						SistemaUsuarios sis = SistemaUsuarios.deserializar();
						if (sis.anyadirUsuario(nuevoUsuario)) {
							new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
									"Se ha introducido con exito usuario: " + nombreNuevoUsuario);
							panel.getVentana().addItems(sis.getUsuarios());
						} else {
							new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
									"Usuario ya se encuentra en el sistema");
						}
					}

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Introduzca un nombre de usuario");
				}
			}
		});
	}

	public void addItems(SistemaUsuarios sistemaUsuarios) {
		resetearComponente();
		for (String elemento : sistemaUsuarios.getGrupos()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(elemento);
			item.setChecked(false);
		}
	}

	private void resetearComponente() {
		this.table.removeAll();
	}

}
