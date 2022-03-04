package ventana;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class VentanaEnviar extends Shell {

	private List list;
	private Button btnEnviar, btnCancelar;

	public VentanaEnviar(Shell parent, String titulo) {
		super(parent);
		setText(titulo);
		setSize(400, 400);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		list = new List(composite, SWT.FLAT | SWT.RIGHT);
		list.setToolTipText("");
		list.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		list.setBounds(0, 0, 108, 28);

		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 1, 1));

		btnEnviar = new Button(composite_1, SWT.PUSH);
		btnEnviar.setText("Send");

		btnCancelar = new Button(composite_1, SWT.PUSH);
		btnCancelar.setText("Cancel");
		open();
	}
}
