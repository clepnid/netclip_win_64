package ventanaGestionModulo;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
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

public class PanelSeleccionCarpetaProyecto extends Composite {

	private Ventana ventana;
	private Text texto;
	private final String PATH = "C:\\Program Files (x86)\\Clepnid\\src\\html";
	private String rutaPadreIndex;

	public PanelSeleccionCarpetaProyecto(Composite parent, int style, String labelText, int width, Ventana ventana) {
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
					File configuracion = existeFicheroClepnidJSON(new File(file));
					if (configuracion!=null) {
						new JsonRead(configuracion, ventana, content);
						rutaPadreIndex = configuracion.getParentFile().getAbsolutePath();
					}else {
						MessageDialog.openError(ventana.getShell(), "Error",
								"La carpeta seleccionada no contiene el fichero de configuración clepnid.json");
					}
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
	
	public File existeFicheroClepnidJSON(File directorio) {
        File[] ficheros = directorio.listFiles();
        
        if(ficheros != null){
            for (int x = 0; x < ficheros.length; x++) {
                if (ficheros[x].getName().equals("clepnid.json")) {
					return ficheros[x];
				}
                if (ficheros[x].isDirectory()) {
                	File ficheroAux = existeFicheroClepnidJSON(ficheros[x]);
                	if (ficheroAux != null) {
						return ficheroAux;
					}
                }
            }
        }
        return null;
    }
	
	public String getRutaPadreIndex() {
		return rutaPadreIndex;
	}

}
