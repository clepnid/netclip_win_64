package barraNavegacion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelIntroduccionTextos extends Composite {
	
	private Text texto;

	public PanelIntroduccionTextos(Composite parent, int style, String labelText, int width) {
		super(parent, style);
		this.setBackground(this.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		this.setLayout(new GridLayout(2, false));
		Label label = new Label(this, SWT.LEFT);
	    label.setText(labelText);
	    label.setBackground(this.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
	    GridData gd = new GridData ();
	    gd.widthHint = width;
	    label.setLayoutData (gd);
	    
	    GridData dataText = new GridData (SWT.FILL, SWT.BEGINNING, true, false);
	    texto = new Text(this, SWT.BORDER);
	    texto.setLayoutData(dataText);
		
	}
	
	//metodo importante que devuelve la lista de formatos añadidas a la tabla por el usuario.
	
	public String getTextoIntroducido() {
		return texto.getText();
	}
	
	public void setTexto(String text) {
		texto.setText(text);
	}
	
	public void setTextoEnable(Boolean bool) {
		texto.setEnabled(bool);
	}
}
