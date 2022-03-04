package historial;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;

public class PanelHistoria extends Composite {

	public Composite composite, panOpciones, panImagen, panAtributos, panBoton;
	public Label lblImagen, lblCros;
	private Composite composite_1;
	public StyledText lblFecha;
	private Historial historial;
	private ScrolledComposite c2;
	public GridData gd_panObjeto;
	private VentanaListaHistorial ventanaListaHistorial;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PanelHistoria(VentanaListaHistorial ventanaListaHistorial, Composite parent, ScrolledComposite c2, int style, Historial historial) {
		super(parent, style);
		this.setVentanaListaHistorial(ventanaListaHistorial);
		this.c2 = c2;
		this.setHistorial(historial);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new GridLayout(1, false));

		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		lblFecha = new StyledText(composite_1, SWT.NONE);
		lblFecha.setSize(70, 20);
		lblFecha.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblFecha.setText("New Label");

		lblCros = new Label(composite_1, SWT.NONE);
		lblCros.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCros.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblCros.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/cros.gif"));

		// Eventos al posar raton encima del boton cerrar
		lblCros.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				lblCros.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/cros_up.gif"));
				
			}
		});

		// Eventos al posar raton fuera del boton cerrar
		lblCros.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				lblCros.setImage(getCheckedImage(parent.getDisplay(), "./src/imagenes/cros.gif"));
				
			}
		});
		
		lblCros.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent arg0) {
				ListaHistorial.deleteHistorial(historial);
				ocultar();
				ventanaListaHistorial.listaPaneles.remove(historial.indice);
				for (int i = historial.indice; i < ventanaListaHistorial.listaPaneles.size(); i++) {
					ventanaListaHistorial.listaPaneles.get(i).historial.indice--;
				}
			}
		});

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(2, false));
		gd_panObjeto = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_panObjeto.heightHint = 190;
		gd_panObjeto.widthHint = 450;
		setLayoutData(gd_panObjeto);

		panImagen = new Composite(composite, SWT.NONE);
		panImagen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panImagen.setLayout(new GridLayout(1, false));
		GridData gd_panImagen = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_panImagen.heightHint = 182;
		gd_panImagen.widthHint = 140;
		panImagen.setLayoutData(gd_panImagen);

		lblImagen = new Label(panImagen, SWT.NONE);
		lblImagen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblImagen.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		// lblImagen.setImage(getCheckedImage(display, ruta.replace("/", "\\")));

		panOpciones = new Composite(composite, SWT.NONE);
		panOpciones.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panOpciones.setLayout(new GridLayout(1, false));
		GridData gd_panOpciones = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_panOpciones.heightHint = 182;
		gd_panOpciones.widthHint = 160;
		panOpciones.setLayoutData(gd_panOpciones);

		panAtributos = new Composite(panOpciones, SWT.NONE);
		panAtributos.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panAtributos.setLayout(new GridLayout(1, false));
		GridData gd_panAtributos = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_panAtributos.heightHint = 80;
		gd_panAtributos.widthHint = 140;
		panAtributos.setLayoutData(gd_panAtributos);

		// Label lblNombre = new Label(panAtributos, SWT.NONE);
		// lblNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		// lblNombre.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
		// 1));
		// lblNombre.setText("dd");

		// Label lblPeso = new Label(panAtributos, SWT.NONE);
		// lblPeso.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		// lblPeso.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		// lblPeso.setText("ff");

		// Label lblFormato = new Label(panAtributos, SWT.NONE);
		// lblFormato.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		// lblFormato.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
		// 1));
		// lblFormato.setText("ff");

		panBoton = new Composite(panOpciones, SWT.NONE);
		panBoton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		panBoton.setLayout(new GridLayout(1, false));
		panBoton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		GridData gd_panBotones = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_panBotones.heightHint = 90;
		gd_panBotones.widthHint = 140;
		panBoton.setLayoutData(gd_panBotones);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public String getText() {
		return null;
	}

	public void setTextEstilo(StyleRange range) {
		// TODO Auto-generated method stub
		
	}
	
	public void setFechaEstilo(StyleRange style) {
		lblFecha.setStyleRange(style);
	}
	
	public void removeStyleRange() {
		lblFecha.setStyleRange(null);
	}

	static Image getCheckedImage(Display display, String ruta) {
		Image image = new Image(display, ruta);
		GC gc = new GC(image);
		gc.drawImage(image, 0, 0);
		gc.dispose();
		return image;
	}
	
	public void ocultar() {
		gd_panObjeto.exclude=true;
		this.setVisible(false);
		c2.setMinSize(this.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.getParent().layout();
	}

	public Historial getHistorial() {
		return historial;
	}

	public void setHistorial(Historial historial) {
		this.historial = historial;
	}

	public VentanaListaHistorial getVentanaListaHistorial() {
		return ventanaListaHistorial;
	}

	public void setVentanaListaHistorial(VentanaListaHistorial ventanaListaHistorial) {
		this.ventanaListaHistorial = ventanaListaHistorial;
	}

}
