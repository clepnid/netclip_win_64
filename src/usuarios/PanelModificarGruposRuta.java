package usuarios;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelModificarGruposRuta extends Composite {

	public VentanaAdministrarAccesoGrupos ventana;
	private Table table;

	public VentanaAdministrarAccesoGrupos getVentana() {
		return ventana;
	}

	public Table getTable() {
		return table;
	}

	public PanelModificarGruposRuta(Composite parent, int style,
			VentanaAdministrarAccesoGrupos ventanaAdministrarAccesoGrupos) {
		super(parent, style);
		this.ventana = ventanaAdministrarAccesoGrupos;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		data.widthHint = 300;
		this.setLayout(new GridLayout(2, false));

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(dataTable);

		addListenerTable(this);

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

	private static void addListenerTable(PanelModificarGruposRuta panel) {
		panel.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				if (string.equals("Checked")) {
					TableItem ti = (TableItem) event.item;
					controlarSeleccion(panel, ti.getText());
				}
			}
		});
	}

	private static void controlarSeleccion(PanelModificarGruposRuta panel, String nomSelec) {
		ArrayList<String> gruposSeleccionados = panel.getFormatosSeleccionados();
		if (gruposSeleccionados.isEmpty()) {
			System.out.println("se queda vacio");
			// eliminar restricciones
			ArrayList<String> listaRutas = panel.getVentana().getListaTree().getRutasSeleccionadas();
			if (listaRutas != null) {
				if (!listaRutas.isEmpty()) {
					ListaAcesoGrupos.eliminar(listaRutas);
				}
			}
			// quitar check de los demas que no sean todos
			TableItem[] listaGrupos = panel.getTable().getItems();
			listaGrupos[0].setChecked(true);
			for (int i = 1; i < listaGrupos.length; i++) {
				listaGrupos[i].setChecked(false);
			}
		} else {
			if (gruposSeleccionados.contains("Todos") && "Todos".equals(nomSelec)) {
				System.out.println("se sekeccuona todos");
				// eliminar restricciones
				ArrayList<String> listaRutas = panel.getVentana().getListaTree().getRutasSeleccionadas();
				if (listaRutas != null) {
					if (!listaRutas.isEmpty()) {
						ListaAcesoGrupos.eliminar(listaRutas);
					}
				}
				// quitar check de los demas que no sean todos
				TableItem[] listaGrupos = panel.getTable().getItems();
				listaGrupos[0].setChecked(true);
				for (int i = 1; i < listaGrupos.length; i++) {
					listaGrupos[i].setChecked(false);
				}

			} else if (gruposSeleccionados.contains("Oculto") && "Oculto".equals(nomSelec)) {
				System.out.println("se queda oculto");
				// quitar todas las restricciones añadir Oculto a restricciones
				ArrayList<String> listaRutas = panel.getVentana().getListaTree().getRutasSeleccionadas();
				if (listaRutas != null) {
					if (!listaRutas.isEmpty()) {
						ListaAcesoGrupos.eliminar(listaRutas);
						ListaAcesoGrupos.anyadirRestriccion(listaRutas, "Oculto");
						
					}
				}
				// quitar check de todos los demas poner a Oculto
				TableItem[] listaGrupos = panel.getTable().getItems();
				listaGrupos[0].setChecked(false);
				listaGrupos[1].setChecked(true);
				for (int i = 2; i < listaGrupos.length; i++) {
					listaGrupos[i].setChecked(false);
				}

			} else {
				System.out.println("otro grupo se añade");
				// quitar check todos y Oculto y poner check en el elemento seleccionado
				TableItem[] listaGrupos = panel.getTable().getItems();
				listaGrupos[0].setChecked(false);
				listaGrupos[1].setChecked(false);
				ArrayList<String> listaRutas = panel.getVentana().getListaTree().getRutasSeleccionadas();
				if (listaRutas != null) {
					if (!listaRutas.isEmpty()) {
						ListaAcesoGrupos.eliminar(listaRutas);
						for (int i = 2; i < listaGrupos.length; i++) {
							if (listaGrupos[i].getChecked()) {
									ListaAcesoGrupos.anyadirRestriccion(listaRutas, listaGrupos[i].getText());
								
							}
						}

					}
				}
			}
		}
	}

	public void addItems(ArrayList<String> grupos) {
		resetearComponente();
		// añadir el componente todos que sera el primer item de lista
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText("Todos");
		// añadir el componente todos que sera el primer item de lista
		TableItem item2 = new TableItem(table, SWT.NONE);
		item2.setText("Oculto");
		ArrayList<String> gruposSistema = SistemaUsuarios.deserializar().getGrupos();
		for (String string : gruposSistema) {
			TableItem item3 = new TableItem(table, SWT.NONE);
			item3.setText(string);
		}
		String ruta = this.getVentana().getListaTree().getRutasSeleccionadas().get(0);
		ArrayList<String> rgS = ListaAcesoGrupos.getGruposRutas(ruta);

		if (rgS == null) {
			getTable().getItem(0).setChecked(true);
		} else {
			if (rgS.isEmpty()) {
				getTable().getItem(0).setChecked(true);
			} else {
				TableItem[] listaItem = getTable().getItems();
				for (int i = 1; i < listaItem.length; i++) {
					if (rgS.contains(listaItem[i].getText())) {
						listaItem[i].setChecked(true);
					} else {
						listaItem[i].setChecked(false);
					}
				}
			}
		}
	}

	private void resetearComponente() {
		this.table.removeAll();
	}

}
