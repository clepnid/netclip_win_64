package ventana;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import barraNavegacion.VentanaIconos;
import barraNavegacion.VentanaModificarLogo;
import http.JsonModulosMenuWeb;
import http.CrearTunel;
import http.GuardadoRutas;
import http.Http;
import red.multicast.MulticastControl;
import usuarios.VentanaAdministrarAccesoGrupos;
import usuarios.VentanaAdministrarGrupos;
import usuarios.VentanaAdministrarUsuarios;
import ventanaGestionarModulo.VentanaGestionarModulosWeb;

public class MenuBar {
	private Ventana ventana;
	private Boolean ventanaEliminarRutaAbierta, ventanaAyudaAbierta, ventanaObtenerLinkAbierta,
			ventanaGestionModuloAbierta;
	private ventanaGestionModulo.Ventana ventanaGestionModulo;

	public MenuBar(Ventana ventana) {
		this.setVentana(ventana);
		ventanaEliminarRutaAbierta = false;
		ventanaGestionModuloAbierta = false;
		ventanaAyudaAbierta = false;
		ventanaObtenerLinkAbierta = false;
		ventana.shlSwt.setLayout(new GridLayout());

		// Create the bar menu
		Menu menuBar = new Menu(ventana.shlSwt, SWT.BAR);

		// Create the File item's dropdown menu
		Menu fileMenu = new Menu(menuBar);
		// Create all the items in the bar menu
		MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
		fileItem.setText(Ventana.idioma.getProperty("toolbar_web"));
		fileItem.setMenu(fileMenu);

		// si no se ha cargado la configuracion del menu no se podrá realizar esta
		// accion
		if (JsonModulosMenuWeb.config == null) {
			fileItem.setEnabled(false);
		}

		MenuItem newItem = new MenuItem(fileMenu, SWT.CASCADE);
		newItem.setText(Ventana.idioma.getProperty("toolbar_web_menu"));

		Menu newMenu = new Menu(fileMenu);
		newItem.setMenu(newMenu);

		MenuItem shortcutItem = new MenuItem(newMenu, SWT.NONE);
		shortcutItem.setText(Ventana.idioma.getProperty("toolbar_web_menu_ir_a_web"));
		shortcutItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Program.launch("http://localhost:" + Http.getPuertoHTTP() + "/menu");
			}

		});

		MenuItem iconItem = new MenuItem(newMenu, SWT.NONE);
		iconItem.setText(Ventana.idioma.getProperty("toolbar_web_menu_mostrar_qr"));
		iconItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ventana.crearVentanaQR(ventana.shlSwt,
						"http://" + MulticastControl.getMyIps().get(0) + ":" + Http.getPuertoHTTP() + "/menu");
			}

		});

		// Create the File item's dropdown menu

		MenuItem newItemServer = new MenuItem(fileMenu, SWT.CASCADE);
		newItemServer.setText(Ventana.idioma.getProperty("toolbar_web_menu_servidor"));

		Menu newMenuServer = new Menu(fileMenu);
		newItemServer.setMenu(newMenuServer);

		MenuItem shortcutItemServer = new MenuItem(newMenuServer, SWT.NONE);
		shortcutItemServer.setText(Ventana.idioma.getProperty("toolbar_web_menu_ir_a_web"));
		shortcutItemServer.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Program.launch("http://" + MulticastControl.ip_servidor + ":" + Http.getPuertoHTTP() + "/menu");
			}

		});

		MenuItem iconItemServer = new MenuItem(newMenuServer, SWT.NONE);
		iconItemServer.setText(Ventana.idioma.getProperty("toolbar_web_menu_mostrar_qr"));
		iconItemServer.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ventana.crearVentanaQR(ventana.shlSwt,
						"http://" + MulticastControl.ip_servidor + ":" + Http.getPuertoHTTP() + "/menu");
			}

		});

		// añade separador
		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem makeWebLink = new MenuItem(fileMenu, SWT.NONE);
		makeWebLink.setText(Ventana.idioma.getProperty("toolbar_web_crear_link"));
		makeWebLink.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (!ventanaObtenerLinkAbierta) {
					Shell shell = new Shell(ventana.shlSwt, SWT.SHELL_TRIM);
					shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					shell.setSize(600, 500);
					shell.setMinimumSize(400, 0);
					shell.setText(Ventana.idioma.getProperty("toolbar_web_crear_link"));
					shell.setLayout(new GridLayout(1, false));

					shell.addListener(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							ventanaObtenerLinkAbierta = false;
						}
					});
					Composite composite = new Composite(shell, SWT.NONE);
					composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite.setLayout(new GridLayout(1, false));
					composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					Composite compositeNombre = new Composite(composite, SWT.NONE);
					compositeNombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					compositeNombre.setLayout(new GridLayout(2, false));
					compositeNombre.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					Label label = new Label(compositeNombre, SWT.NONE);
					label.setText(Ventana.idioma.getProperty("toolbar_web_nombre_subdominio") + " ");
					label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

					Text text_nombre = new Text(compositeNombre, SWT.BORDER);
					text_nombre.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					text_nombre.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

					Composite compositeOutput = new Composite(composite, SWT.NONE);
					compositeOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					compositeOutput.setLayout(new GridLayout(1, false));
					compositeOutput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					Text text_output = new Text(compositeOutput, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
					text_output.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					text_output.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

					Composite compositeBotones = new Composite(composite, SWT.NONE);
					compositeBotones.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					compositeBotones.setLayout(new GridLayout(2, false));
					compositeBotones.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));

					Button btnCrear = new Button(compositeBotones, SWT.PUSH);
					btnCrear.setText(Ventana.idioma.getProperty("toolbar_web_nombre_subdominio_crear"));

					btnCrear.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							ventana.display.asyncExec(new Runnable() {
								public void run() {
									CrearTunel tunel = new CrearTunel(text_nombre.getText(), text_output,
											ventana.display);
									tunel.start();
								}
							});
						}
					});
					btnCrear.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

					Button btnCerrar = new Button(compositeBotones, SWT.PUSH);
					btnCerrar.setText(Ventana.idioma.getProperty("toolbar_web_cerrar"));

					btnCerrar.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							shell.close();
							ventanaObtenerLinkAbierta = false;
						}
					});
					btnCerrar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

					shell.pack();
					Monitor primary = Display.getCurrent().getPrimaryMonitor();
					Rectangle bounds = primary.getBounds();
					Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
					int x = bounds.x + (bounds.width - rect.width) / 2;
					int y = bounds.y + (bounds.height - rect.height) / 2;
					shell.setLocation(x, y);
					shell.open();
					ventanaObtenerLinkAbierta = true;
				}

			}

		});
		// añade separador
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem menuIconoMenu = new MenuItem(fileMenu, SWT.NONE);
		menuIconoMenu.setText("Gestionar Iconos MenuBar");
		menuIconoMenu.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new VentanaIconos(ventana);
			}
		});
		
		MenuItem menuIconoLogo = new MenuItem(fileMenu, SWT.NONE);
		menuIconoLogo.setText("Modificar Logo MenuBar");
		menuIconoLogo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new VentanaModificarLogo(ventana);
			}
		});
		
		MenuItem menuGestionarModulosWeb = new MenuItem(fileMenu, SWT.NONE);
		menuGestionarModulosWeb.setText("Gestionar Modulos Web");
		menuGestionarModulosWeb.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new VentanaGestionarModulosWeb();
			}
		});

		// añade separador
		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem loadItemIni = new MenuItem(fileMenu, SWT.NONE);
		loadItemIni.setText(Ventana.idioma.getProperty("toolbar_web_cargar_configuracion_inicio"));
		loadItemIni.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						GuardadoRutas guardado = null;
						try {
							guardado = GuardadoRutas.deserializar();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (guardado != null) {
							guardado.cargar(Ventana.getInstance().http);
						}
					}
				});
			}
		});
		MenuItem saveItemIni = new MenuItem(fileMenu, SWT.NONE);
		saveItemIni.setText(Ventana.idioma.getProperty("toolbar_web_guardar_configuracion_inicio"));
		saveItemIni.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						GuardadoRutas guardado = new GuardadoRutas(Ventana.getInstance().http);
						try {
							GuardadoRutas.serializar(guardado);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});

		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem loadItem = new MenuItem(fileMenu, SWT.NONE);
		loadItem.setText(Ventana.idioma.getProperty("toolbar_web_cargar_configuracion"));
		loadItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						FileDialog dialog = new FileDialog(ventana.shlSwt);
						String ruta = dialog.open();
						if (ruta != null) {
							GuardadoRutas guardado = null;
							try {
								guardado = GuardadoRutas.deserializar(ruta);
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (guardado != null) {
								guardado.cargar(Ventana.getInstance().http);
							}
						}
					}
				});
			}
		});
		MenuItem addItem = new MenuItem(fileMenu, SWT.NONE);
		addItem.setText(Ventana.idioma.getProperty("toolbar_web_acoplar_configuracion"));
		addItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						FileDialog dialog = new FileDialog(ventana.shlSwt);
						String ruta = dialog.open();
						if (ruta != null) {
							GuardadoRutas guardado = null;
							try {
								guardado = GuardadoRutas.deserializar(ruta);
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (guardado != null) {
								guardado.acoplar(Ventana.getInstance().http);
							}
						}
					}
				});
			}
		});
		MenuItem saveItem = new MenuItem(fileMenu, SWT.NONE);
		saveItem.setText(Ventana.idioma.getProperty("toolbar_web_guardar_configuracion"));

		saveItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ventana.display.asyncExec(new Runnable() {
					public void run() {
						DirectoryDialog dialog = new DirectoryDialog(ventana.shlSwt);
						String ruta = dialog.open();
						if (ruta != null) {
							GuardadoRutas guardado = new GuardadoRutas(Ventana.getInstance().http);
							try {
								GuardadoRutas.serializar(guardado, ruta);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		});

		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem refreshItem = new MenuItem(fileMenu, SWT.NONE);
		refreshItem.setText(Ventana.idioma.getProperty("toolbar_web_editar_rutas_archivo"));
		refreshItem.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (!ventanaEliminarRutaAbierta) {
					Shell shell = new Shell(ventana.shlSwt);
					shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					shell.setSize(600, 500);
					shell.setMinimumSize(400, 0);
					shell.setText(Ventana.idioma.getProperty("toolbar_web_eliminar_ruta"));
					shell.setLayout(new GridLayout(1, false));

					shell.addListener(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							ventanaEliminarRutaAbierta = false;
						}
					});
					Composite composite = new Composite(shell, SWT.NONE);
					composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite.setLayout(new GridLayout(1, false));
					composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					List list = new List(composite, SWT.FLAT | SWT.MULTI | SWT.V_SCROLL);
					list.setToolTipText("");
					list.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					list.setBounds(0, 0, 108, 28);
					for (String url : Ventana.getInstance().http.getUrlsParciales()) {
						list.add(url);
					}
					Composite composite_1 = new Composite(shell, SWT.NONE);
					composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite_1.setLayout(new GridLayout(2, false));
					composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 1, 1));

					Button btnEliminar = new Button(composite_1, SWT.PUSH);
					btnEliminar.setText(Ventana.idioma.getProperty("toolbar_web_eliminar"));

					btnEliminar.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							String[] nombres = list.getSelection();
							for (int i = 0; i < list.getSelection().length; i++) {
								Ventana.getInstance().http.eliminarUrl(nombres[i]);
							}
							list.remove(list.getSelectionIndices());
							if (ventana.multicastControl.soyServidor()) {
								for (String nombre : nombres) {
									ventana.eliminarArchivoDeLista(nombre);
								}
							}
						}
					});

					Button btnCerrar = new Button(composite_1, SWT.PUSH);
					btnCerrar.setText(Ventana.idioma.getProperty("toolbar_web_cerrar"));

					btnCerrar.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {
							shell.close();
							ventanaEliminarRutaAbierta = false;
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
					ventanaEliminarRutaAbierta = true;
				}

			}

		});
		MenuItem deleteItem = new MenuItem(fileMenu, SWT.NONE);
		deleteItem.setText(Ventana.idioma.getProperty("toolbar_web_eliminar_rutas_archivo"));
		deleteItem.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Ventana.getInstance().http.vaciarUrls();
				ventana.lblBotonServidor
						.setImage(SWTResourceManager.getImage(Ventana.class, "/imagenes/btn_on_down.gif"));
				ventana.pararServidor();
			}

		});

		MenuItem mostrarVentanaGestionModulo = new MenuItem(menuBar, SWT.NONE);
		mostrarVentanaGestionModulo.setText("Gestionar M�dulo");
		mostrarVentanaGestionModulo.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!ventanaGestionModuloAbierta) {
					setVentanaGestionModulo(new ventanaGestionModulo.Ventana(ventana));
					ventanaGestionModuloAbierta = true;
				}
			}

		});

		
		// Create the File item's dropdown menu
		Menu userMenu = new Menu(menuBar);
		// Create all the items in the bar menu
		MenuItem userItem = new MenuItem(menuBar, SWT.CASCADE);
		userItem.setText("Usuario");
		userItem.setMenu(userMenu);
		
		MenuItem adminAcceso = new MenuItem(userMenu, SWT.NONE);
		adminAcceso.setText("Administrar Acceso Grupos");
		adminAcceso.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				new VentanaAdministrarAccesoGrupos(ventana.shlSwt);
			}

		});
		
		MenuItem adminGrupo = new MenuItem(userMenu, SWT.NONE);
		adminGrupo.setText("Administrar Grupos");
		adminGrupo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				new VentanaAdministrarGrupos(ventana);
			}

		});
		
		MenuItem adminUsuarios = new MenuItem(userMenu, SWT.NONE);
		adminUsuarios.setText("Administrar Usuarios");
		adminUsuarios.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				new VentanaAdministrarUsuarios(ventana);
			}

		});
		
		MenuItem menuAyuda = new MenuItem(menuBar, SWT.NONE);
		menuAyuda.setText(Ventana.idioma.getProperty("toolbar_ayuda"));
		menuAyuda.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!ventanaAyudaAbierta) {
					Shell shell = new Shell(ventana.shlSwt);
					shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					shell.setSize(600, 500);
					shell.setMinimumSize(400, 0);
					shell.setText(Ventana.idioma.getProperty("toolbar_ayuda"));
					shell.setLayout(new GridLayout(1, false));

					shell.addListener(SWT.Close, new Listener() {
						public void handleEvent(Event event) {
							ventanaAyudaAbierta = false;
						}
					});
					Composite composite = new Composite(shell, SWT.NONE);
					composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					composite.setLayout(new GridLayout(1, false));
					composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

					Label label = new Label(composite, SWT.NONE);
					label.setText("Compartir y Obtener Datos");
					label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label1 = new Label(composite, SWT.NONE);
					label1.setText("Para compartir ficheros, texto o texto con estilos,");
					label1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label2 = new Label(composite, SWT.NONE);
					label2.setText("una vez seleccionados tendremos 2 opciones:");
					label2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label3 = new Label(composite, SWT.NONE);
					label3.setText("1-. Pulsar la siguiente combinacion de teclas: \'Ctrl + Shift + 1\'.");
					label3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label4 = new Label(composite, SWT.NONE);
					label4.setText("2-. Click Derecho y seleccionar \'Copiar mediante Red\'.");
					label4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label5 = new Label(composite, SWT.NONE);
					label5.setText("Para obtener los datos compartidos tendremos que");
					label5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label6 = new Label(composite, SWT.NONE);
					label6.setText("pulsar la siguiente combinacion de teclas: \'Ctrl + Shift + 2\',");
					label6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label7 = new Label(composite, SWT.NONE);
					label7.setText("si hay compartido archivos: se nos abrirá una ventana");
					label7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label8 = new Label(composite, SWT.NONE);
					label8.setText("para seleccionar la ruta donde guardarlos y si es texto o ");
					label8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					Label label9 = new Label(composite, SWT.NONE);
					label9.setText("texto con estilos se pegará donde seleccionemos anteriormente.");
					label9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

					shell.pack();
					Monitor primary = Display.getCurrent().getPrimaryMonitor();
					Rectangle bounds = primary.getBounds();
					Rectangle rect = Display.getCurrent().getActiveShell().getBounds();
					int x = bounds.x + (bounds.width - rect.width) / 2;
					int y = bounds.y + (bounds.height - rect.height) / 2;
					shell.setLocation(x, y);
					shell.open();
					ventanaAyudaAbierta = true;
				}
			}

		});
		
		

		ventana.shlSwt.setMenuBar(menuBar);
	}

	public Ventana getVentana() {
		return ventana;
	}

	public void setVentana(Ventana ventana) {
		this.ventana = ventana;
	}

	public boolean isventanaGestionModuloAbierta() {
		return ventanaGestionModuloAbierta;
	}

	public void setVentanaGestionModuloAbierta(boolean bool) {
		this.ventanaGestionModuloAbierta = bool;
	}

	public ventanaGestionModulo.Ventana getVentanaGestionModulo() {
		return ventanaGestionModulo;
	}

	public void setVentanaGestionModulo(ventanaGestionModulo.Ventana ventanaGestionModulo) {
		this.ventanaGestionModulo = ventanaGestionModulo;
	}

}