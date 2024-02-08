package ventanaGestionModulo;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
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

public class PanelIntroduccionTextoListaRutasHttp extends Composite {

	public Ventana ventana;
	private ArrayList<String> items1, items2, itemsAutocomplete1, itemsAutocomplete2;
	private Combo combo1, combo2;
	private Table table;
	private AutoCompleteField autocomplete1, autocomplete2;
	private Button buttonAnyadir;

	public ArrayList<String> getItems1() {
		return items1;
	}

	public Button getButtonAnyadir() {
		return buttonAnyadir;
	}

	public ArrayList<String> getItemsAutocomplete1() {
		return itemsAutocomplete1;
	}

	public ArrayList<String> getItemsAutocomplete2() {
		return itemsAutocomplete2;
	}

	public ArrayList<String> getItems2() {
		return items2;
	}

	public Combo getCombo2() {
		return combo2;
	}

	public AutoCompleteField getAutocomplete2() {
		return autocomplete2;
	}

	public Table getTable() {
		return table;
	}

	public Combo getCombo1() {
		return combo1;
	}

	public AutoCompleteField getAutocomplete1() {
		return autocomplete1;
	}

	public PanelIntroduccionTextoListaRutasHttp(Composite parent, int style, Ventana ventana, String nombre) {
		super(parent, style);
		this.ventana = ventana;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(1, false));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		data.heightHint = 40;

		Label labelListaPuertos = new Label(this, 0);
		labelListaPuertos.setText(nombre);
		labelListaPuertos.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		labelListaPuertos.setLayoutData(data);

		Composite panelTraductor = new Composite(this, 0);
		panelTraductor.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		panelTraductor.setLayout(new GridLayout(4, false));
		panelTraductor.setLayoutData(data);

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData dataCombo = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

		items1 = new ArrayList<String>();
		itemsAutocomplete1 = new ArrayList<String>();

		combo1 = new Combo(panelTraductor, SWT.DROP_DOWN | SWT.RESIZE);
		combo1.setItems(itemsAutocomplete1.toArray(new String[0]));
		combo1.setLayoutData(dataCombo);
		combo1.setText("Ruta Ejecutable");
		autocomplete1 = new AutoCompleteField(combo1, new ComboContentAdapter(), items1.toArray(new String[0]));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData dataLabelTraductor = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		dataLabelTraductor.widthHint = 40;

		Label labelTraductor = new Label(panelTraductor, SWT.CENTER);
		labelTraductor.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		labelTraductor.setText("-->");
		labelTraductor.setLayoutData(dataLabelTraductor);

		items2 = new ArrayList<String>();
		itemsAutocomplete2 = new ArrayList<String>();

		combo2 = new Combo(panelTraductor, SWT.DROP_DOWN | SWT.RESIZE);
		combo2.setItems(itemsAutocomplete2.toArray(new String[0]));
		combo2.setLayoutData(dataCombo);
		combo2.setText("Ruta Clepnid");
		autocomplete2 = new AutoCompleteField(combo2, new ComboContentAdapter(), items2.toArray(new String[0]));

		GridData dataButtonTraductor = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		dataLabelTraductor.widthHint = 40;

		buttonAnyadir = new Button(panelTraductor, SWT.NONE);
		buttonAnyadir.setText("Introducir");
		buttonAnyadir.setLayoutData(dataButtonTraductor);

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		table.setLayoutData(dataTable);

		addListenerTable();
		addListenerComboAddAutocomplete();
		addListenerComboAddFocus();
		addListenerButton();

	}

	// metodo importante que devuelve la lista de formatos añadidas a la tabla por
	// el usuario.

	public ArrayList<String[]> getRutasSeleccionados() {
		ArrayList<String[]> listAux = new ArrayList<String[]>();

		for (int i = 0; i < this.table.getItems().length; i++) {
			if (this.table.getItems()[i].getChecked()) {
				String[] stringAux = { "\"" + items1.get(i) + "\"", "\"" + items2.get(i) + "\"" };
				listAux.add(stringAux);
			}

		}

		return listAux;
	}

	public String getRutasSeleccionadosJSON() {
		ArrayList<String> listaAux = new ArrayList<String>();
		String stringAux = "";
		for (int i = 0; i < this.table.getItems().length; i++) {
			if (this.table.getItems()[i].getChecked()) {
				stringAux = "\t\t\t\t{\n\t\t\t\t\t\"RutaEjecutable\": \"" + items1.get(i) + "\"";
				stringAux = stringAux + "\n\t\t\t\t\t\"RutaClepnid\": \"" + items2.get(i) + "\"";
				stringAux = stringAux + "\n\t\t\t\t}";
				listaAux.add(stringAux);

			}

		}

		return Arrays.toString(listaAux.toArray());
	}

	public boolean vacio() {
		for (int i = 0; i < this.table.getItems().length; i++) {
			if (this.table.getItems()[i].getChecked()) {
				return false;
			}

		}
		return true;
	}

	private void addListenerTable() {
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				System.out.println(event.item + " " + string);
			}
		});
	}

	private void addListenerComboAddFocus() {
		combo1.addListener(SWT.FocusIn, new Listener() {
			public void handleEvent(Event e) {
				if (combo1.getText().equals("Ruta Ejecutable")) {
					combo1.setText("");
				}
			}
		});
		combo1.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event e) {
				if (combo1.getText().equals("")) {
					combo1.setText("Ruta Ejecutable");
				}
			}
		});
		combo2.addListener(SWT.FocusIn, new Listener() {
			public void handleEvent(Event e) {
				if (combo2.getText().equals("Ruta Clepnid")) {
					combo2.setText("");
				}
			}
		});
		combo2.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event e) {
				if (combo2.getText().equals("")) {
					combo2.setText("Ruta Clepnid");
				}
			}
		});
	}

	private void addListenerButton() {
		buttonAnyadir.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				accionIntroducir();
			}
		});
	}

	private void addListenerComboAddAutocomplete() {
		combo1.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				accionIntroducir();
			}
		});
		combo2.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				accionIntroducir();
			}
		});
	}

	public void accionIntroducir() {
		if (combo2.getText().equals("") | combo2.getText().equals("Ruta Clepnid")) {
			combo2.setFocus();
		} else {
			if (combo1.getText().equals("") | combo1.getText().equals("Ruta Ejecutable")) {
				combo1.setFocus();
			} else {
				if (comprobarCombo1() && comprobarCombo2()) {
					String elemento1 = combo1.getText();
					String elemento2 = combo2.getText();
					if (!elemento2.startsWith("/")) {
						elemento2 = "/" + elemento2;
					}
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(elemento1 + " --> " + elemento2);
					item.setChecked(true);
					combo1.setText("Ruta Ejecutable");
					addItem1(elemento1);
					addItemAutocomplete1(elemento1);
					addItemAutocomplete1(elemento1.split("/")[0]);
					autocomplete1.setProposals(itemsAutocomplete1.toArray(new String[0]));
					combo1.setItems(itemsAutocomplete1.toArray(new String[0]));
					combo2.setText("Ruta Clepnid");
					addItem2(elemento2);
					addItemAutocomplete2(elemento2);
					addItemAutocomplete2(elemento2.split("/")[0]);
					autocomplete2.setProposals(itemsAutocomplete2.toArray(new String[0]));
					combo2.setItems(itemsAutocomplete2.toArray(new String[0]));
					buttonAnyadir.setFocus();
				}
			}
		}

	}

	public boolean comprobarCombo1() {
		String elemento = combo1.getText();
		if (!elemento.equals("")) {
			boolean encontrado = false;
			for (String item : items2) {
				if (item.toUpperCase().equals(elemento.toUpperCase())) {
					encontrado = true;
					break;
				}
			}
			if (!encontrado) {
				String[] elementoSplit = elemento.split(":");
				if (elementoSplit.length < 2) {
					MessageDialog.openError(ventana.getShell(), "Error Ruta Ejecutable",
							"La url introducida no es valida ya que no se ha introducido el puerto, introduzca una url como: localhost:3030/element");
					return false;
				} else {

					String[] elementoSplit2 = elementoSplit[1].split("/");
					boolean rutaIncorrecta = false;
					for (int i = 1; i < elementoSplit2.length; i++) {
						if (!rutaCorrecta(elementoSplit2[i])) {
							rutaIncorrecta = true;
						}
					}
					if (rutaIncorrecta) {
						MessageDialog.openError(ventana.getShell(), "Error Ruta Ejecutable",
								"La url: " + elemento + " contiene caracteres no aptos para rutas http");
						return false;
					}
					try {
						Integer.valueOf(elementoSplit2[0]);
						return true;

					} catch (java.lang.NumberFormatException e2) {
						try {
							Integer.valueOf(elementoSplit[1]);
							return true;
						} catch (java.lang.NumberFormatException e3) {
							MessageDialog.openError(ventana.getShell(), "Error Ruta Ejecutable",
									"El puerto introducido: " + elementoSplit2[0]
											+ " no es un puerto válido, introduzca una url como: localhost:3030/element");
							return false;
						}
					}

				}
			} else {
				MessageDialog.openInformation(ventana.getShell(), "Confirm Ruta Ejecutable",
						"Ya se ha introducido esta ruta");
			}
		}
		return false;

	}

	public boolean comprobarCombo2() {
		String elemento = combo2.getText();
		if (!elemento.equals("")) {
			boolean encontrado = false;
			for (String item : items2) {
				if (item.toUpperCase().equals(elemento.toUpperCase())) {
					encontrado = true;
					break;
				}
			}
			if (!encontrado) {
				String[] rutas = elemento.split("/");
				if (rutas[0].equals("")) {
					for (int i = 1; i < rutas.length; i++) {
						if (!rutaCorrecta(rutas[i])) {
							MessageDialog.openError(ventana.getShell(), "Error Ruta Clepnid",
									"La url: " + elemento + " contiene caracteres no aptos para rutas http");
							return false;
						}
					}
				} else {
					for (int i = 0; i < rutas.length; i++) {
						if (!rutaCorrecta(rutas[i])) {
							MessageDialog.openError(ventana.getShell(), "Error Ruta Clepnid",
									"La url: " + elemento + " contiene caracteres no aptos para rutas http");
							return false;
						}

					}
				}
				return true;

			} else {
				MessageDialog.openInformation(ventana.getShell(), "Confirm Ruta Clepnid",
						"Ya se ha introducido esta ruta");
				return false;
			}
		}
		return false;

	}

	public void addItemAutocomplete1(String item) {
		boolean seEncuentra = false;
		for (String itemAux : itemsAutocomplete1) {
			if (item.equals(itemAux)) {
				seEncuentra = true;
				break;
			}
		}
		if (!seEncuentra) {
			itemsAutocomplete1.add(item);
		}
	}

	public void addItemAutocomplete2(String item) {
		boolean seEncuentra = false;
		for (String itemAux : itemsAutocomplete2) {
			if (item.equals(itemAux)) {
				seEncuentra = true;
				break;
			}
		}
		if (!seEncuentra) {
			itemsAutocomplete2.add(item);
		}
	}

	public void addItem2(String item) {
		boolean seEncuentra = false;
		for (String itemAux : items2) {
			if (item.equals(itemAux)) {
				seEncuentra = true;
				break;
			}
		}
		if (!seEncuentra) {
			items2.add(item);
		}
	}

	public static boolean rutaCorrecta(String ruta) {
		return ruta.equals(http.Http.encodeURIcomponent(ruta));
	}

	public void addItem1(String item) {
		boolean seEncuentra = false;
		for (String itemAux : items1) {
			if (item.equals(itemAux)) {
				seEncuentra = true;
				break;
			}
		}
		if (!seEncuentra) {
			items1.add(item);
		}
	}

	public void setFormatos(ArrayList<String[]> array) {
		refrescar();
		for (int i = 0; i < array.size(); i++) {

			String elemento1 = array.get(i)[0];
			String elemento2 = array.get(i)[1];
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(elemento1 + " --> " + elemento2);
			item.setChecked(true);
			addItem1(elemento1);
			addItemAutocomplete1(elemento1);
			addItemAutocomplete1(elemento1.split("/")[0]);
			autocomplete1.setProposals(itemsAutocomplete1.toArray(new String[0]));
			combo1.setItems(itemsAutocomplete1.toArray(new String[0]));
			addItem2(elemento2);
			addItemAutocomplete2(elemento2);
			addItemAutocomplete2(elemento2.split("/")[0]);
			autocomplete2.setProposals(itemsAutocomplete2.toArray(new String[0]));
			combo2.setItems(itemsAutocomplete2.toArray(new String[0]));

		}
		combo1.setText("Ruta Ejecutable");
		combo2.setText("Ruta Clepnid");
	}

	public void refrescar() {
		items1 = new ArrayList<String>();
		items2 = new ArrayList<String>();
		itemsAutocomplete1 = new ArrayList<String>();
		itemsAutocomplete2 = new ArrayList<String>();
		combo1.setItems(itemsAutocomplete2.toArray(new String[0]));
		combo1.setText("Ruta Ejecutable");
		combo2.setItems(itemsAutocomplete2.toArray(new String[0]));
		combo2.setText("Ruta Clepnid");
		buttonAnyadir.setFocus();

		this.table.removeAll();
	}

}
