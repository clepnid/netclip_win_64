package historial;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import ventana.Ventana;

public class PaneBuscador extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PaneBuscador(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Composite panelBajo = new Composite(this, SWT.NONE);
		panelBajo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panelBajo.setLayout(new GridLayout(1, false));
		panelBajo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		Composite panelOrdenar = new Composite(panelBajo, SWT.NONE);
		panelOrdenar.setSize(158, 38);
		panelOrdenar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panelOrdenar.setLayout(new GridLayout(2, false));

		Label lblOrdenar = new Label(panelOrdenar, SWT.NONE);
		lblOrdenar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOrdenar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOrdenar.setText(Ventana.idioma.getProperty("historial_ordenar")+":");

		Combo radio = new Combo(panelOrdenar, SWT.ABORT);
		GridData gd_radio = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_radio.widthHint = 164;
		radio.setLayoutData(gd_radio);
		radio.add(Ventana.idioma.getProperty("historial_fecha2"));
		radio.add(Ventana.idioma.getProperty("historial_fecha1"));
		radio.add(Ventana.idioma.getProperty("historial_fecha4"));
		radio.setText(Ventana.idioma.getProperty("historial_fecha4"));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
