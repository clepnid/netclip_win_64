package ventanaGestionModulo;

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

public class PanelSeleccionExtensiones extends Composite {

	public Ventana ventana;
	private Button btnFormato, btnTiposArchivo;
	private ArrayList<String> itemsFormatos;
	private ArrayList<String> itemsConjuntoFormatos;
	private Combo combo;
	private Table table;
	private AutoCompleteField autocomplete;
	boolean tipoFichero;

	public Button getBtnFormato() {
		return btnFormato;
	}

	public Button getBtnTiposArchivo() {
		return btnTiposArchivo;
	}

	public ArrayList<String> getItemsConjuntoFormatos() {
		return itemsConjuntoFormatos;
	}

	public ArrayList<String> getItemsFormatos() {
		return itemsFormatos;
	}

	private boolean getTipoFichero() {
		return tipoFichero;
	}

	private void setTipoFichero(boolean tipoFichero) {
		this.tipoFichero = tipoFichero;
	}

	public Table getTable() {
		return table;
	}

	public Combo getCombo() {
		return combo;
	}

	public AutoCompleteField getAutocomplete() {
		return autocomplete;
	}

	/**
	 * Constructor del panel con la barra de progreso.
	 * 
	 * @param parent  {@link Composite} panel padre para
	 *                {@link PanelSeleccionExtensiones}.
	 * @param style   numero de referencia de la apariencia que va a tener el
	 *                componente.
	 * @param ventana {@link Ventana} para mostrar por pantalla el componente.
	 */

	public PanelSeleccionExtensiones(Composite parent, int style, Ventana ventana) {
		super(parent, style);
		this.ventana = ventana;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		this.setLayout(new GridLayout(1, false));
		btnFormato = new Button(this, SWT.CHECK);
		btnFormato.setText("Formatos");
		btnFormato.setSelection(true);
		btnFormato.setLayoutData(data);
		btnFormato.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		tipoFichero = true;

		btnTiposArchivo = new Button(this, SWT.CHECK);
		btnTiposArchivo.setText("Tipos Archivos");
		btnTiposArchivo.setLayoutData(data);
		btnTiposArchivo.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		itemsFormatos = new ArrayList<String>();
		for (String geek : ArraysStringFormatos.listaTodosFormatos())
			itemsFormatos.add(geek);
		
		itemsConjuntoFormatos = new ArrayList<String>();
		for (String geek : ArraysStringFormatos.CONJUNTOFORMATOS)
			itemsConjuntoFormatos.add(geek);
		combo = new Combo(this, SWT.DROP_DOWN | SWT.RESIZE);
		combo.setItems(itemsFormatos.toArray(new String[0]));
		combo.setLayoutData(data);
		autocomplete = new AutoCompleteField(combo, new ComboContentAdapter(), itemsFormatos.toArray(new String[0]));

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		table.setLayoutData(dataTable);

		addListenerTable(this);
		addListenerCombo(this);
		addListenerBtnFormato(this);
		addListenerBtnArchivo(this);

	}

	// metodo importante que devuelve la lista de formatos añadidas a la tabla por
	// el usuario.

	public String[] getFormatosSeleccionados() {
		ArrayList<String> listAux = new ArrayList<String>();

		for (TableItem tableItem : this.table.getItems()) {
			if (tableItem.getChecked()) {
				listAux.add("\"" + tableItem.getText() + "\"");
			}
		}

		return listAux.toArray(new String[0]);
	}

	private static void addListenerBtnFormato(PanelSeleccionExtensiones panel) {
		panel.getBtnFormato().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				if (btn.getSelection()) {
					if (panel.getBtnTiposArchivo().getSelection()) {
						panel.getBtnTiposArchivo().setSelection(false);
					}
				} else {
					if (!panel.getBtnTiposArchivo().getSelection()) {
						panel.getBtnTiposArchivo().setSelection(true);
					}
				}
				if (panel.getTipoFichero()) {
					panel.setTipoFichero(false);
					panel.getAutocomplete().setProposals(panel.getItemsConjuntoFormatos().toArray(new String[0]));
					panel.getCombo().setItems(panel.getItemsConjuntoFormatos().toArray(new String[0]));
				} else {
					panel.setTipoFichero(true);
					panel.getAutocomplete().setProposals(panel.getItemsFormatos().toArray(new String[0]));
					panel.getCombo().setItems(panel.getItemsFormatos().toArray(new String[0]));
				}
			}
		});
	}

	private static void addListenerBtnArchivo(PanelSeleccionExtensiones panel) {
		panel.getBtnTiposArchivo().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				if (btn.getSelection()) {
					if (panel.getBtnFormato().getSelection()) {
						panel.getBtnFormato().setSelection(false);
					}
				} else {
					if (!panel.getBtnFormato().getSelection()) {
						panel.getBtnFormato().setSelection(true);
					}
				}
				if (panel.getTipoFichero()) {
					panel.setTipoFichero(false);
					panel.getAutocomplete().setProposals(panel.getItemsConjuntoFormatos().toArray(new String[0]));
					panel.getCombo().setItems(panel.getItemsConjuntoFormatos().toArray(new String[0]));
				} else {
					panel.setTipoFichero(true);
					panel.getAutocomplete().setProposals(panel.getItemsFormatos().toArray(new String[0]));
					panel.getCombo().setItems(panel.getItemsFormatos().toArray(new String[0]));
				}
			}
		});
	}

	private static void addListenerTable(PanelSeleccionExtensiones panel) {
		panel.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				System.out.println(event.item + " " + string);
			}
		});
	}

	private static void addListenerCombo(PanelSeleccionExtensiones panel) {
		panel.getCombo().addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				String elementoAux = e.widget.toString();
				String elemento = "";
				boolean encontrado = false;
				for (int i = 0; i < elementoAux.length(); i++) {
					if (encontrado && elementoAux.charAt(i) != '}') {
						elemento = elemento + elementoAux.charAt(i);
					}
					if (!encontrado && elementoAux.charAt(i) == '{') {
						encontrado = true;
					}
					if (elementoAux.charAt(i) == '}') {
						break;
					}
				}

				if (elemento != "") {
					boolean noEncontrado = true;
					for (TableItem tableItem : panel.getTable().getItems()) {
						if (tableItem.getText().toUpperCase().equals(elemento.toUpperCase())) {
							noEncontrado = false;
						}
					}
					if (noEncontrado) {

						if (panel.getTipoFichero()) {
							TableItem item = new TableItem(panel.getTable(), SWT.NONE);
							item.setText(elemento);
							item.setChecked(true);
							panel.getCombo().setText("");
							if (panel.getItemsFormatos().remove(elemento)) {
								panel.getAutocomplete().setProposals(panel.getItemsFormatos().toArray(new String[0]));
								panel.getCombo().setItems(panel.getItemsFormatos().toArray(new String[0]));
							}

						} else {
							int indice = -1;
							for (int i = 0; i < panel.getCombo().getItems().length; i++) {
								if (panel.getCombo().getItems()[i].toUpperCase().equals(elemento)) {
									indice = i;
									break;
								}
							}
							if (indice != -1) {
								for (int i = 0; i < ArraysStringFormatos.FORMATOS[indice].length; i++) {
									boolean noAnyadir = false;
									for (TableItem tableItem : panel.getTable().getItems()) {
										if (tableItem.getText().equals(ArraysStringFormatos.FORMATOS[indice][i])) {
											noAnyadir = true;
										}
									}
									if (!noAnyadir) {
										TableItem item = new TableItem(panel.getTable(), SWT.NONE);
										item.setText(ArraysStringFormatos.FORMATOS[indice][i]);
										item.setChecked(true);
										panel.getItemsFormatos().remove(ArraysStringFormatos.FORMATOS[indice][i]);
									}

								}

								if (panel.getItemsConjuntoFormatos().remove(elemento)) {

									panel.getAutocomplete()
											.setProposals(panel.getItemsConjuntoFormatos().toArray(new String[0]));
									panel.getCombo().setItems(panel.getItemsConjuntoFormatos().toArray(new String[0]));
								}
								panel.getCombo().setText("");
							}
						}
					}else {
						panel.getCombo().setText("");
					}
				}
			}
		});
	}
	
	public void addItems(ArrayList<String> items) {
		resetearComponente();
		for (String elemento : items) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(elemento);
			item.setChecked(true);
			itemsFormatos.remove(elemento);
		}
		autocomplete.setProposals(itemsFormatos.toArray(new String[0]));
		combo.setItems(itemsFormatos.toArray(new String[0]));
	}
	
	private void resetearComponente() {
		itemsFormatos = new ArrayList<String>();
		for (String geek : ArraysStringFormatos.listaTodosFormatos())
			itemsFormatos.add(geek);
		
		itemsConjuntoFormatos = new ArrayList<String>();
		for (String geek : ArraysStringFormatos.CONJUNTOFORMATOS)
			itemsConjuntoFormatos.add(geek);
		autocomplete.setProposals(itemsFormatos.toArray(new String[0]));
		combo.setItems(itemsFormatos.toArray(new String[0]));
		combo.setText("");
		this.table.removeAll();
	}

}
