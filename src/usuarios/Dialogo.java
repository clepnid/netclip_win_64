package usuarios;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Dialogo {
	public Dialogo ventana;
	public static int ERROR = SWT.ICON_ERROR;
	public static int INFORMACION = SWT.ICON_INFORMATION;
	public static int ADVERTENCIA = SWT.ICON_WARNING;
	public static int PREGUNTA = SWT.ICON_QUESTION;

	
	public Dialogo(Shell padre,int tipoDialogo, String titulo, String mensaje) {

		Shell shell = new Shell(padre, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
		shell.setText(titulo);
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		data.widthHint = 300;
		Composite panelMensaje = new Composite(shell, SWT.None);
		panelMensaje.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		panelMensaje.setLayout(new GridLayout(1, false));
		panelMensaje.setLayoutData(data);
		
		Label lblIcon = new Label(panelMensaje, SWT.None);
		lblIcon.setImage(shell.getDisplay().getSystemImage(tipoDialogo));
		lblIcon.setText(mensaje);
		lblIcon.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		Button btn = new Button(panelMensaje, SWT.None);
		btn.setText("Aceptar");
		btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
			}
		});
		
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}


}