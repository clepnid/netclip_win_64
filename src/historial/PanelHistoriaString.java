package historial;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import portapapeles.Clip;
import portapapeles.DatoSeleccion;
import portapapeles.Html;
import ventana.Ventana;

public class PanelHistoriaString extends PanelHistoria {

	private StyledText lblContenido;
	private ByteBuffer buffer;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PanelHistoriaString(VentanaListaHistorial ventanaListaHistorial, Composite parent, ScrolledComposite c2, int style, Historial historial, Clip clip) {
		super(ventanaListaHistorial, parent, c2, style, historial);
		lblFecha.setText(historial.getFecha());
		if (Html.hasHTMLTags(historial.contenido)) {
			lblImagen.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/html.gif"));
		} else {
			lblImagen.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/texto.gif"));
		}
		
		ScrolledComposite scroll = new ScrolledComposite(panAtributos, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Composite panTexto = new Composite(scroll, SWT.PUSH);
		panTexto.setToolTipText(Ventana.idioma.getProperty("contenido_disponible"));
		panTexto.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panTexto.setLayout(new GridLayout(1, false));
		GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		panTexto.setLayoutData(grid);
		lblContenido = new StyledText(panTexto, SWT.NONE);
		lblContenido.setEditable(false);
		lblContenido.setEnabled(true);
		lblContenido.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblContenido.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		buffer = StandardCharsets.UTF_8.encode(historial.contenido);
		String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
		lblContenido.setText(utf8EncodedString);
		scroll.setContent(panAtributos);
		scroll.setExpandVertical(true);
		scroll.setExpandHorizontal(true);
		scroll.setContent(panTexto);
		scroll.setMinSize(panTexto.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		panTexto.layout();
		Button btnCopiar = new Button(panBoton, SWT.NONE);
		btnCopiar.setToolTipText(Ventana.idioma.getProperty("historial_btnCopiar"));
		btnCopiar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, true, 1, 1));
		btnCopiar.setText(Ventana.idioma.getProperty("historial_btnCopiar"));
		btnCopiar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				DatoSeleccion seleccion = new DatoSeleccion(lblContenido.getText());
				clip.setContenidoClipboard(seleccion);
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public String getText() {
		return lblContenido.getText();
	}

	public void setTextEstilo(StyleRange style) {
		lblContenido.setStyleRange(style);
	}

	public void removeStyleRange() {
		lblContenido.setStyleRange(null);
		lblFecha.setStyleRange(null);
	}

}
