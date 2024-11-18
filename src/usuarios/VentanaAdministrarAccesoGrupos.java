package usuarios;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import http.JerarquiaRutas;
import http.JerarquiaRutas.HijoPadre;

import org.eclipse.swt.widgets.Shell;

public class VentanaAdministrarAccesoGrupos {
	public static class ListaTree {
		private Tree tree;
		private JerarquiaRutas jerarquiaRutas;
		private ArrayList<TreeItem> registroTreeItem;
		private ArrayList<String> rutasSeleccionadas;
		private ArrayList<JerarquiaRutas.HijoPadre> registroHijoPadre;
		private VentanaAdministrarAccesoGrupos ventanaAdministrarAccesoGrupos;

		public VentanaAdministrarAccesoGrupos getVentanaAdministrarAccesoGrupos() {
			return ventanaAdministrarAccesoGrupos;
		}

		public ArrayList<String> getRutasSeleccionadas() {
			return rutasSeleccionadas;
		}

		public ArrayList<JerarquiaRutas.HijoPadre> getRegistroHijoPadre() {
			return registroHijoPadre;
		}

		public ArrayList<TreeItem> getRegistroTreeItem() {
			return registroTreeItem;
		}

		public void anyadirRegistro(TreeItem treeItem, JerarquiaRutas.HijoPadre hijoPadre) {
			this.registroHijoPadre.add(hijoPadre);
			this.registroTreeItem.add(treeItem);
		}

		public String getRuta(TreeItem treeItem) {
			String texto = ListaAcesoGrupos.getItemRuta(this.jerarquiaRutas,
					registroHijoPadre.get(registroTreeItem.indexOf(treeItem)));
			return texto;
		}

		public ArrayList<String> getGruposRutaSeleccionada(TreeItem treeItem) {

			return ListaAcesoGrupos.getGruposRutas(getRuta(treeItem));
		}

		public Tree getTree() {
			return tree;
		}

		public JerarquiaRutas getJerarquiaRutas() {
			return jerarquiaRutas;
		}

		ListaTree(VentanaAdministrarAccesoGrupos ve, JerarquiaRutas je) {
			this.registroHijoPadre = new ArrayList<JerarquiaRutas.HijoPadre>();
			this.registroTreeItem = new ArrayList<TreeItem>();
			this.rutasSeleccionadas = new ArrayList<String>();
			this.ventanaAdministrarAccesoGrupos = ve;
			this.jerarquiaRutas = je;
			int numero = je.getNumeroRutasIniciales();
			this.tree = ve.getTree();
			for (int i = 0; i < numero; i++) {
				new TreeItem(tree, 0);
			}
			for (HijoPadre hp : je.getLista()) {
				anyadirElementos(hp);
			}
			anyadirEventoSeleccion(this.ventanaAdministrarAccesoGrupos);
		}

		private void anyadirEventoSeleccion(VentanaAdministrarAccesoGrupos v) {
			tree.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					rutasSeleccionadas.clear();
					TreeItem[] treeItemSeleccionado = tree.getSelection();
					for (int i = 0; i < treeItemSeleccionado.length; i++) {
						ArrayList<String> g = getGruposRutaSeleccionada(treeItemSeleccionado[i]);
						seleccionarRutas(treeItemSeleccionado[i]);
						v.getPanelModificarGruposRuta().addItems(g);
					}
				}
			});
		}

		private void seleccionarRutas(TreeItem item) {
			rutasSeleccionadas.add(getRuta(item));
			TreeItem[] listaItems = item.getItems();
			if (listaItems.length == 0) {
				seleccionarRutasContenidasModulo(tree.getItems(), getRuta(item));
			} else {
				for (TreeItem itemAux : item.getItems()) {
					seleccionarRutas(itemAux);
				}
			}
		}

		private void seleccionarRutasContenidasModulo(TreeItem[] items, String rutaItemSeleccionado) {
			for (TreeItem item : items) {
				String rutaItem = getRuta(item);

				if (rutaItem.contains(rutaItemSeleccionado) && !rutasSeleccionadas.contains(rutaItem)) {
					rutasSeleccionadas.add(rutaItem);
					break;
				}
				TreeItem[] itemsAux = item.getItems();
				if (itemsAux.length != 0) {
					seleccionarRutasContenidasModulo(itemsAux, rutaItemSeleccionado);
				}
			}
		}

		private void anyadirElementos(HijoPadre hp) {
			if (hp.getPadre() == null) {
				for (int i = 0; i < tree.getItems().length; i++) {
					if ((tree.getItem(i).getText() == null) || (tree.getItem(i).getText().equals(""))) {

						boolean seEncuentra = false;
						for (TreeItem item : tree.getItems()) {
							if (item.getText().equals(hp.getHijo())) {
								seEncuentra = true;
							}
						}
						if (!seEncuentra) {
							tree.getItem(i).setText(hp.getHijo());
							anyadirRegistro(tree.getItem(i), hp);
						}
					}
				}
			} else {
				anyadirElementosHijos(hp, 0);
			}
		}

		private void anyadirElementosHijos(HijoPadre hp, int nivel) {
			int n = nivel;
			n++;
			ArrayList<TreeItem> listaNivel = new ArrayList<TreeItem>();

			for (int i = 0; i < n; i++) {
				if (i == 0) {
					for (TreeItem item : tree.getItems()) {
						listaNivel.add(item);
					}
				} else {
					ArrayList<TreeItem> listaNivelAux = new ArrayList<TreeItem>();
					for (TreeItem treeItem : listaNivel) {
						for (TreeItem item : treeItem.getItems()) {
							listaNivelAux.add(item);
						}
					}
					listaNivel.clear();
					for (TreeItem item : listaNivelAux) {
						listaNivel.add(item);
					}
				}
			}
			hp.setnJerarquia(n);
			for (TreeItem item : listaNivel) {
				if (hp.getPadre() != null) {
					if (item.getText().equals(hp.getPadre())) {
						TreeItem itemAux = new TreeItem(item, 0);
						itemAux.setText(hp.getHijo());
						anyadirRegistro(itemAux, hp);
					}
				}
			}
		}

	}

	public VentanaAdministrarAccesoGrupos ventana;
	private Button btnCancelar, btnEliminar;
	private Composite panelLista, panelBotones;
	private Shell shell;
	VentanaAdministrarAccesoGrupos.ListaTree listaTree;
	private Tree tree;
	private PanelModificarGruposRuta panelModificarGruposRuta;
	private int pos;
	
	public Button getBtnEliminar() {
		return btnEliminar;
	}

	public PanelModificarGruposRuta getPanelModificarGruposRuta() {
		return panelModificarGruposRuta;
	}

	public VentanaAdministrarAccesoGrupos.ListaTree getListaTree() {
		return listaTree;
	}

	public void setListaTree(VentanaAdministrarAccesoGrupos.ListaTree listaTree) {
		this.listaTree = listaTree;
	}

	public Tree getTree() {
		return tree;
	}

	public Shell getShell() {
		return shell;
	}

	public int getPos() {
		return pos;
	}

	public Button getBtnCancelar() {
		return btnCancelar;
	}

	public VentanaAdministrarAccesoGrupos(Shell padre) {
		this.pos = -1;

		shell = new Shell(padre, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		shell.setText("Administrar Acceso A Grupos");

		shell.setLayout(new GridLayout(1, false));

		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// este layout tiene que horizontal con el fill y solo se expande los huecos en
		// horizontal en vertical no se menea false
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		data.widthHint = 300;
		GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		data1.widthHint = 300;
		GridData dataGeneral = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataGeneral.widthHint = 600;

		Composite panelGeneral = new Composite(shell, SWT.None);
		panelGeneral.setLayout(new GridLayout(2, false));
		panelGeneral.setLayoutData(dataGeneral);
		panelGeneral.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		panelLista = new Composite(panelGeneral, SWT.None);
		panelLista.setLayout(new GridLayout(1, false));
		panelLista.setLayoutData(data1);
		panelLista.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		tree = new Tree(panelLista, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setLayoutData(data1);

		// se expande horizontal y vertical.

		panelModificarGruposRuta = new PanelModificarGruposRuta(panelGeneral, SWT.None, this);
		panelModificarGruposRuta.setLayoutData(data1);

		GridData dataBtn = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		panelBotones = new Composite(shell, SWT.None);
		panelBotones.setLayout(new GridLayout(2, false));
		panelBotones.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		panelBotones.setLayoutData(dataBtn);
		GridData databotones = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		btnCancelar = new Button(panelBotones, SWT.None);
		btnCancelar.setText("Cancelar");
		btnCancelar.setLayoutData(databotones);
		btnEliminar = new Button(panelBotones, SWT.None);
		btnEliminar.setText("Eliminar Todas las Restricciones");
		btnEliminar.setLayoutData(databotones);

		prepararArbol(this);
		addListenerBotones(this);
		shell.pack();

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
	}

	private static void addListenerBotones(VentanaAdministrarAccesoGrupos panel) {
		panel.getBtnCancelar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				panel.getShell().close();
			}
		});
		panel.getBtnEliminar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(panel.getShell(), style);
				messageBox.setText("Ventana Emergente");
				messageBox.setMessage("¿Seguro quiere eliminar todas las restricciones?");
				if (messageBox.open() == SWT.YES) {
					ListaAcesoGrupos lag= ListaAcesoGrupos.deserializar();
					lag.lista.clear();
					ListaAcesoGrupos.serializar(lag);
					event.doit = true;
				} else {
					event.doit = false;
				}
			}
		});
	}

	private static void prepararArbol(VentanaAdministrarAccesoGrupos v) {
		v.setListaTree(new VentanaAdministrarAccesoGrupos.ListaTree(v, JerarquiaRutas.getSparkRutas()));
	}

}