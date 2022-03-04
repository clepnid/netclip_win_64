package ventana;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class VentanaBarraProgreso {

	public Display display;
	protected Shell shell;
	public String nombre;
	public ProgressBar barraProgreso;
	public Label lblPorcentajeBarraProgreso;
	public GridData gd_BarraProgreso;
	public GridData gd_panProgreso;
	public Label lblCopiando;
	public int porcentaje;
	public TaskItem taskItem;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VentanaBarraProgreso window = new VentanaBarraProgreso();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		display.asyncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		nombre = "";
		porcentaje = 0;
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
		shell.setSize(450, 300);
		shell.setText("Clepnid");
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setLayout(new GridLayout(1, false));

		this.gd_panProgreso = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_panProgreso.heightHint = 71;
		shell.setLayoutData(gd_panProgreso);

		this.lblCopiando = new Label(shell, SWT.NONE);
		lblCopiando.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCopiando.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblCopiando.setText("Copying ...");

		barraProgreso = new ProgressBar(shell, SWT.NONE);
		barraProgreso.setMinimum(0);
		barraProgreso.setMaximum(100);
		barraProgreso.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gd_BarraProgreso = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_BarraProgreso.heightHint = 12;
		barraProgreso.setLayoutData(gd_BarraProgreso);

		lblPorcentajeBarraProgreso = new Label(shell, SWT.NONE);
		lblPorcentajeBarraProgreso.setAlignment(SWT.RIGHT);
		lblPorcentajeBarraProgreso.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblPorcentajeBarraProgreso.setText("0%");
		lblPorcentajeBarraProgreso.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		nombre = "";
		porcentaje = 0;
	}

	/**
	 * Obtiene el objeto {@link TaskItem} manejador del icono de la barra de
	 * progreso.
	 * 
	 * @return {@link TaskItem} manejador del icono de la barra de progreso.
	 */

	public TaskItem getTaskBarItem() {
		TaskBar bar = display.getSystemTaskBar();
		if (bar == null) {
			return null;
		}
		TaskItem item = bar.getItem(shell);
		if (item == null) {
			item = bar.getItem(null);
		}
		return item;
	}

	/**
	 * Define el numero con el porcentaje que se va a mostrar en el
	 * {@link Composite}.
	 * 
	 * @param numero a mostrar por pantalla referente al porcentaje de la copia.
	 */

	public void setPorcentaje(int numero) {
		this.porcentaje = numero;
		display.asyncExec(new Runnable() {
			public void run() {
				barraProgreso.setSelection(numero);
				lblPorcentajeBarraProgreso.setText(String.valueOf(numero) + "%");
				taskItem = getTaskBarItem();
				if (taskItem != null) {
					taskItem.setProgress(numero);
				}
			}
		});
	}

	/**
	 * Define el nombre que se va a mostrar en el {@link Composite}.
	 * 
	 * @param nombre {@link String} a mostrar por pantalla referente al nombre de la
	 *               copia.
	 */

	public void setNombre(String nombre) {
		this.nombre = nombre;
		display.asyncExec(new Runnable() {
			public void run() {
				lblCopiando.setText("Copying " + nombre + "...");
			}
		});
	}

}
