package ventana;

import java.util.ArrayList;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import http.ConfiguracionJson;

public class TableComboDesplegable {
	public static TableCombo getDesplegable(Composite parent, String nombre, ArrayList<ConfiguracionJson> config,
			Ventana ventana) {
		TableCombo tc = new TableCombo(parent, SWT.BORDER | SWT.READ_ONLY | SWT.FULL_SELECTION);
		tc.setText(Ventana.idioma.getProperty("fichero_lista_otros"));
		tc.clearSelection();
		for (ConfiguracionJson configuracionJson : config) {

			ventana.display.asyncExec(new Runnable() {
				public void run() {
					ObjetoTablaQr ti = new ObjetoTablaQr(tc.getTable(), SWT.NONE, nombre.replace(" ", ""),
							configuracionJson, ventana);
					ti.setText(configuracionJson.getTextoBoton());
					// ti.setImage(image);
					ti.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							System.out.println(ti.getText());
						}
					});
				}

			});
		}

		tc.getTable().addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent arg0) {
				((ObjetoTablaQr) tc.getTable().getItem(tc.getTable().getSelectionIndex())).mostrarQr();
			}

		});
		tc.setCursor(ventana.display.getSystemCursor(SWT.CURSOR_HAND));
		return tc;
	}
}
