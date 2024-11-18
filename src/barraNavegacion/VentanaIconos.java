package barraNavegacion;


import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import barraNavegacion.BarraNavegacion.Item;
import usuarios.Dialogo;
import usuarios.SistemaUsuarios;
import ventana.Ventana;

public class VentanaIconos {
	public VentanaIconos ventana;
	private Button btnModificar, btnEliminar, btnAnyadir;
	private List list;
	private Text campoTexto;
	private Shell shell;
	private int pos;
	private PanelIntroduccionTextos panelTitulo, panelImagen, panelUrl;
	private PanelSeleccionarLista panelSeleccionarGrupo;

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

	public Button getBtnAnyadir() {
		return btnAnyadir;
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

	public Button getBtnEliminar() {
		return btnEliminar;
	}

	public VentanaIconos(Ventana ventana) {

		shell = new Shell(ventana.shlSwt, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Gestionar Iconos Menu");
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

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
		setPanelSeleccionarGrupo(new PanelSeleccionarLista(panelAnyadir, SWT.None, "Grupo:", getGruposExistentes(), 100));
		panelTitulo.setLayoutData(dataPanelAnyadir);
		panelImagen.setLayoutData(dataPanelAnyadir);
		panelUrl.setLayoutData(dataPanelAnyadir);
		getPanelSeleccionarGrupo().setLayoutData(dataPanelAnyadir);
		btnAnyadir = new Button(panelAnyadir, SWT.None);
		btnAnyadir.setText("Añadir");
		btnAnyadir.setLayoutData(databotones);
		btnAnyadir.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Composite panelLabelEntrada = new Composite(shell, SWT.None);
		panelLabelEntrada.setLayout(new GridLayout(1, false));
		panelLabelEntrada.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		panelLabelEntrada.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		Label label = new Label(panelLabelEntrada, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		label.setText("Entradas:");
		label.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Composite panelTabla = new Composite(shell, SWT.None);
		panelTabla.setLayout(new GridLayout(1, false));
		panelTabla.setLayoutData(dataTable);
		panelTabla.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		list = new List(panelTabla, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(dataTable);
		Composite panelBotones = new Composite(shell, SWT.None);
		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setLayoutData(databotones);
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnEliminar = new Button(panelBotones, SWT.None);
		btnEliminar.setText("Eliminar");
		btnEliminar.setLayoutData(databotones);
		btnEliminar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		btnModificar = new Button(panelBotones, SWT.None);
		btnModificar.setText("Modificar");
		btnModificar.setLayoutData(databotones);
		btnModificar.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		resetearComponente();
		addListenerBotones(this);
		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.pack();
		shell.setSize(600, 400);
		shell.open();
	}
	
	public static String[] getGruposExistentes(){
		ArrayList<String> a = SistemaUsuarios.deserializar().getGrupos();
		String[] stockArr = new String[a.size()];
		stockArr = a.toArray(stockArr);

		return stockArr;
	}

	private static void addListenerBotones(VentanaIconos panel) {
		panel.getBtnEliminar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(panel.getShell(), style);
					messageBox.setText("Ventana Emergente");
					messageBox.setMessage("¿Seguro quiere eliminar el item: "
							+ panel.getList().getItem(panel.getList().getSelectionIndex()) + "?");
					if (messageBox.open() == SWT.YES) {
						panel.eliminarItems(panel.getList().getSelectionIndex());
						event.doit = true;
					} else {
						event.doit = false;
					}

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Debe seleccionar un usuario");
				}
			}
		});
		panel.getBtnModificar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (panel.getList().getSelectionIndex() != -1) {
					new VentanaModificarIcono(panel, panel.getList().getSelectionIndex());

				} else {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Debe seleccionar un item");
				}
			}
		});
		panel.getBtnAnyadir().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String nombre = panel.getPanelTitulo().getTextoIntroducido();
				String url = panel.getPanelUrl().getTextoIntroducido();
				String urlImagen = panel.getPanelImagen().getTextoIntroducido();
				String grupo = panel.getPanelSeleccionarGrupo().getSelectItem();
				System.out.println("grupo: "+ grupo);
				if (nombre.equals("")) {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca un Nombre");
				} else if (url.equals("")) {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente", "Introduzca una Url");
				} else if (urlImagen.equals("")) {
					new Dialogo(panel.getShell(), Dialogo.INFORMACION, "Ventana Emergente",
							"Introduzca una Ruta Imagen");
				} else {
					BarraNavegacion.anyadirItem(new Item(nombre, url, urlImagen, grupo));
					panel.resetearComponente();
				}
			}
		});
	}

	public void addItems(Item item) {
		BarraNavegacion.anyadirItem(item);
		resetearComponente();
	}

	public void eliminarItems(int posicion) {
		BarraNavegacion.eliminarItem(posicion);
		resetearComponente();
	}

	public void resetearComponente() {
		this.list.removeAll();
		BarraNavegacion bn = BarraNavegacion.leerFichero();
		System.out.println(bn.getRutaImagenLogo());
		for (Item item : bn.getLista()) {
			System.out.println(item.getNombre());
			getList().add(item.getNombre());
		}
	}

	PanelSeleccionarLista getPanelSeleccionarGrupo() {
		return panelSeleccionarGrupo;
	}

	void setPanelSeleccionarGrupo(PanelSeleccionarLista panelSeleccionarGrupo) {
		this.panelSeleccionarGrupo = panelSeleccionarGrupo;
	}

}