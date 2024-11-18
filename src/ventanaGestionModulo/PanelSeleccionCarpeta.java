package ventanaGestionModulo;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelSeleccionCarpeta extends Composite {

	private Ventana ventana;
	private Text texto;
	//private final String PATH = System.getProperty("user.home");
	private final String PATH = "C:\\Program Files (x86)\\Clepnid\\src\\html";

	public PanelSeleccionCarpeta(Composite parent, int style, String labelText, int width, Ventana ventana) {
		super(parent, style);
		this.ventana = ventana;
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(3, false));
		Label label = new Label(this, SWT.LEFT);
		label.setText(labelText);
		label.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData();
		gd.widthHint = width;
		label.setLayoutData(gd);

		GridData dataText = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		texto = new Text(this, SWT.BORDER | SWT.LEFT);
		texto.setLayoutData(dataText);
		texto.setEditable(false);

		GridData dataBtn = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		Button btn = new Button(this, SWT.CENTER);
		btn.setText("Seleccionar");
		btn.setLayoutData(dataBtn);
		btn.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setFilterPath(PATH);
				final String file = dialog.open();
				if (file != null) {
					String content = new File(file).getAbsolutePath();
					texto.setText(content);
				}
			}
		});

	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	// metodo importante que devuelve la lista de formatos añadidas a la tabla por
	// el usuario.

	public String getTextoIntroducido() {
		return texto.getText();
	}
	
	public void setTexto(String text) {
		this.texto.setText(text);
	}
}
