package barraNavegacion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import barraNavegacion.BarraNavegacion.Item;
import usuarios.Dialogo;

public class VentanaModificarIcono {
	public VentanaModificarIcono ventana;
	private Button btnModificar, btnCancelar;
	private List list;
	private Text campoTexto;
	private Shell shell;
	private int pos;
	private PanelIntroduccionTextos panelTitulo, panelImagen, panelUrl;
	private PanelSeleccionarLista panelSeleccionarGrupo;
	private VentanaIconos ventanaIconos;
	
	public VentanaIconos getVentanaIconos() {
		return ventanaIconos;
	}
	

	public PanelIntroduccionTextos getPanelImagen() {
		return panelImagen;
	}

	public PanelIntroduccionTextos getPanelTitulo() {
		return panelTitulo;
	}

	public PanelIntroduccionTextos getPanelUrl() {
		return panelUrl;
	}

	public List getList() {
		return list;
	}

	public Button getBtnCancelar() {
		return btnCancelar;
	}

	public Text getCampoTexto() {
		return campoTexto;
	}

	public Button getBtnModificar() {
		return btnModificar;
	}

	public Shell getShell() {
		return shell;
	}

	public int getPos() {
		return pos;
	}

	public VentanaModificarIcono(VentanaIconos ventana, int posItem) {

		shell = new Shell(ventana.getShell(), SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Gestionar Iconos Menu");
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		this.ventanaIconos = ventana;

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		GridData dataPanelAnyadir = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		shell.setLayout(new GridLayout(1, false));
		shell.setLayoutData(dataTable);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);

		Composite panelAnyadir = new Composite(shell, SWT.None);
		panelAnyadir.setLayout(new GridLayout(1, false));
		panelAnyadir.setLayoutData(dataPanelAnyadir);
		panelAnyadir.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		panelTitulo = new PanelIntroduccionTextos(panelAnyadir, SWT.None, "Titulo:", 100);
		panelImagen = new PanelIntroduccionTextos(panelAnyadir, SWT.None, "Imagen:", 100);
		panelUrl = new PanelIntroduccionTextos(panelAnyadir, SWT.None, "Url:", 100);
		setPanelSeleccionarGrupo(new PanelSeleccionarLista(panelAnyadir, SWT.None, "Grupo:", VentanaIconos.getGruposExistentes(), 100));
		panelTitulo.setLayoutData(dataPanelAnyadir);
		panelImagen.setLayoutData(dataPanelAnyadir);
		panelUrl.setLayoutData(dataPanelAnyadir);
		getPanelSeleccionarGrupo().setLayoutData(dataPanelAnyadir);

		Item item = BarraNavegacion.getItem(posItem);
		panelTitulo.setTexto(item.getNombre());
		panelImagen.setTexto(item.getRutaImagen());
		panelUrl.setTexto(item.getRuta());
		System.out.println("grupo11: "+item.getGrupo());
		getPanelSeleccionarGrupo().setSelectItem(item.getGrupo());

		Composite panelBotones = new Composite(shell, SWT.None);
		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setLayoutData(databotones);
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnModificar = new Button(panelBotones, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databotones);
		btnModificar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnCancelar = new Button(panelBotones, SWT.None);
		btnCancelar.setText("Cancelar");
		btnCancelar.setLayoutData(databotones);
		btnCancelar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		addListenerBotones(this, posItem);

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.pack();
		shell.setSize(600, 250);
		shell.open();
	}

	private static void addListenerBotones(VentanaModificarIcono panel, int posicion) {
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(panel.getShell(), style);
				messageBox.setText("Ventana Emergente");
				messageBox.setMessage("¿Seguro quiere modificar item?");
				if (messageBox.open() == SWT.YES) {
					String nombre= panel.getPanelTitulo().getTextoIntroducido();
					String url= panel.getPanelUrl().getTextoIntroducido();
					String urlImagen= panel.getPanelImagen().getTextoIntroducido();
					String grupo = panel.getPanelSeleccionarGrupo().getSelectItem();
					System.out.println("grupo: "+grupo);
					if (nombre.equals("")) {
						new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca un Nombre");
					}else if (url.equals("")) {
						new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca una Url");
					}else if (urlImagen.equals("")) {
						new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca una Ruta Imagen");
					}else {
						BarraNavegacion.modificarItem(posicion, new Item(nombre, url, urlImagen, grupo));
						panel.getVentanaIconos().resetearComponente();
						panel.getShell().close();
					}
					event.doit = true;
				} else {
					event.doit = false;
				}
			}
		});
		panel.getBtnCancelar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				panel.getShell().close();
			}
		});
	}


	PanelSeleccionarLista getPanelSeleccionarGrupo() {
		return panelSeleccionarGrupo;
	}


	void setPanelSeleccionarGrupo(PanelSeleccionarLista panelSeleccionarGrupo) {
		this.panelSeleccionarGrupo = panelSeleccionarGrupo;
	}
}