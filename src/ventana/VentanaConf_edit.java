package ventana;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class VentanaConf_edit {

	protected Shell shell;
	protected Display display;
	private Text text_Nombre;
	private Text text_Carpeta;
	private Boolean editable;
	/////////////////////////////////// debe estar en ventana y pasar por parametro
	/////////////////////////////////// !!!!!!!!!!!!!!!!!!!!!!! importante
	private Configuracion config;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VentanaConf_edit window = new VentanaConf_edit();
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
		editable = false;
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));

		Composite composite_principal = new Composite(shell, SWT.NONE);
		composite_principal.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_principal.setLayout(new GridLayout(2, false));
		composite_principal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblNombre = new Label(composite_principal, SWT.NONE);
		lblNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNombre.setBounds(0, 0, 70, 20);
		lblNombre.setText("Nombre:");

		text_Nombre = new Text(composite_principal, SWT.BORDER);
		text_Nombre.setEnabled(false);
		text_Nombre.setEditable(false);
		GridData gd_text_Nombre = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_Nombre.widthHint = 111;
		text_Nombre.setLayoutData(gd_text_Nombre);
		text_Nombre.setBounds(0, 0, 78, 26);

		Label lblCarpeta = new Label(composite_principal, SWT.NONE);
		lblCarpeta.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCarpeta.setBounds(0, 0, 70, 20);
		lblCarpeta.setText("Carpeta:");

		text_Carpeta = new Text(composite_principal, SWT.BORDER);
		text_Carpeta.setEnabled(false);
		text_Carpeta.setEditable(false);
		text_Carpeta.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblRecibirAutomticamente = new Label(composite_principal, SWT.NONE);
		lblRecibirAutomticamente.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRecibirAutomticamente.setText("Recibir Automáticamente:");

		Button btnCheckButton = new Button(composite_principal, SWT.CHECK);
		btnCheckButton.setEnabled(false);
		btnCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Composite composite_btn = new Composite(shell, SWT.NONE);
		composite_btn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_btn.setLayout(new GridLayout(2, false));
		composite_btn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));

		Button btnModificar = new Button(composite_btn, SWT.NONE);
		btnModificar.setText("Modificar");
		btnModificar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		try {
			config = Configuracion.deserializar();
			text_Carpeta.setText(config.carpeta);
			text_Nombre.setText(config.nombre);
			if (config.isAutomatic) {
				btnCheckButton.setSelection(true);
			} else {
				btnCheckButton.setSelection(false);
			}
		} catch (ClassNotFoundException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		btnModificar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (editable) {
					config = new Configuracion(text_Nombre.getText(), text_Carpeta.getText(),
							btnCheckButton.getSelection());
					try {
						Configuracion.serializar(config);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					text_Nombre.setEnabled(false);
					text_Nombre.setEditable(false);
					text_Carpeta.setEnabled(false);
					text_Carpeta.setEditable(false);
					btnCheckButton.setEnabled(false);
					editable = false;

				} else {
					text_Nombre.setEnabled(true);
					text_Nombre.setEditable(true);
					text_Carpeta.setEnabled(true);
					text_Carpeta.setEditable(true);
					btnCheckButton.setEnabled(true);
					editable = true;
				}

			}
		});

		Button btnCerrar = new Button(composite_btn, SWT.NONE);
		btnCerrar.setText("Cerrar");
		btnCerrar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		btnCerrar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.close();
			}
		});

	}
}
