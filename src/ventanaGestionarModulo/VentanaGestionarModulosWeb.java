package ventanaGestionarModulo;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import ventana.Ventana;

public class VentanaGestionarModulosWeb {
	private Shell shell;
	private Table table;
	private Composite panelBotones;
	private Button btnGuardar, btnCancelar;

	public Table getTable() {
		return table;
	}

	public VentanaGestionarModulosWeb() {
		Ventana ventana = Ventana.getInstance();
		shell = new Shell(ventana.shlSwt, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Administrar Modulos Web");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// se expande horizontal y vertical.

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(dataTable);

		panelBotones = new Composite(shell, SWT.None);

		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.widthHint = 300;
		
		panelBotones.setLayoutData(data);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnGuardar = new Button(panelBotones, SWT.None);
		btnGuardar.setText("Guardar");
		btnGuardar.setLayoutData(databotones);
		btnCancelar = new Button(panelBotones, SWT.None);
		btnCancelar.setText("Cancelar");
		btnCancelar.setLayoutData(databotones);
		
		
		addListenerBotones(this);
		
		addItems(SistemaModulos.getInstance().getListaNombreModulosWeb());

		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	public ArrayList<String> getModulosSeleccionados() {
		ArrayList<String> listAux = new ArrayList<String>();

		for (TableItem tableItem : this.table.getItems()) {
			if (tableItem.getChecked()) {
				listAux.add(tableItem.getText());
			}

		}

		return listAux;
	}
	

	public void addItems(ArrayList<String> grupos) {
		resetearComponente();
		SistemaModulos sis = SistemaModulos.getInstance();
		ArrayList<String> listaNombres = sis.getListaNombreModulosWeb();
		for (String string : listaNombres) {
			if (sis.isInicializado(string)) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(string);
				item.setChecked(sis.isHabilitado(string));
			}
		}
		
	}
	
	private void addListenerBotones(VentanaGestionarModulosWeb v) {
		btnGuardar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(v.getShell(), style);
				messageBox.setText("Ventana Emergente");
				messageBox.setMessage("¿Seguro quiere guardar los cambios?");
				if (messageBox.open() == SWT.YES) {
					SistemaModulos sis = SistemaModulos.getInstance();
					for (TableItem it : v.getTable().getItems()) {
						sis.setHabilitado(it.getText(), it.getChecked());
					}
					SistemaModulos.serializar(sis);
					v.getShell().dispose();
				} else {
					event.doit = false;
				}
			}
		});
		btnCancelar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});
	}

	protected Shell getShell() {
		// TODO Auto-generated method stub
		return shell;
	}

	private void resetearComponente() {
		this.table.removeAll();
	}


}
