package red.multicast;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;

public class VentanaMulticast {

	protected Shell shell;
	public MulticastControl cambioServidor;
	public Label lblNoHay;
	public Display display;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VentanaMulticast window = new VentanaMulticast();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				cambioServidor.close();
			}
		});
		cambioServidor = MulticastControl.getInstance();
		cambioServidor.start();

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setBounds(0, 0, 64, 64);

		lblNoHay = new Label(composite_1, SWT.NONE);
		lblNoHay.setAlignment(SWT.CENTER);
		lblNoHay.setText("No hay Servidor");
		lblNoHay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(2, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		composite_2.setBounds(0, 0, 64, 64);

		Button btnSerservidor = new Button(composite_2, SWT.NONE);
		btnSerservidor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnSerservidor.setText("SerServidor");

		btnSerservidor.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				cambioServidor.cliente.cambioServidor();
			}
		});

		Button btnDejarserservidor = new Button(composite_2, SWT.NONE);
		btnDejarserservidor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnDejarserservidor.setText("dejarSerServidor");

		btnDejarserservidor.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				cambioServidor.cliente.pararServidor();
			}
		});

	}

}
