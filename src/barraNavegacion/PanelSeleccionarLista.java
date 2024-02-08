package barraNavegacion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Clase que extiende de {@link Composite}.
 * 
 * @author: Pavon
 * @version: 10/04/2020
 * @since 1.0
 */

public class PanelSeleccionarLista extends Composite {
	
	private Combo combo;
	
	public PanelSeleccionarLista(Composite parent, int style, String labelText, String[] lista, int width) {
		super(parent, style);
		this.setBackground(this.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		this.setLayout(new GridLayout(2, false));
		Label label = new Label(this, SWT.LEFT);
	    label.setText(labelText);
	    label.setBackground(this.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
	    GridData gd = new GridData ();
	    gd.widthHint = width;
	    label.setLayoutData (gd);
	    
	    GridData comboText = new GridData (SWT.FILL, SWT.BEGINNING, true, false);
	    combo = new Combo(this, SWT.BORDER);
		combo.setItems(lista);
		combo.setLayoutData(comboText);
		
	}
	
	public String getSelectItem() {
		if (this.combo.getSelectionIndex()!=-1) {
			return this.combo.getItem(this.combo.getSelectionIndex());
		}
		return null;
	}
	
	public void setSelectItem(String s) {
		for (int i = 0; i < this.combo.getItemCount(); i++) {
			if (this.combo.getItem(i).equals(s)) {
				this.combo.select(i);
				System.out.println("grupo: "+ this.combo.getItem(i));
				break;
			}
		}
	}
	
}
