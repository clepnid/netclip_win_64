package portapapeles;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * Contiene metodos para manejar el portapapeles del sistema {@link Clipboard}
 * 
 * @author: Pavon
 * @version: 22/04/2020
 * @since 1.0
 * @see <a href =
 *      "https://www.developer.com/java/data/how-to-code-java-clipboard-functionality.html"
 *      > Funcionalidad del portapapeles de Java. </a>
 */

public class Clip implements ClipboardOwner {
	static DataFlavor dataFlavorStringJava;
	static DataFlavor dataFlavorHtmlJava;
	static DataFlavor dataFlavorFileJava;
	public Clipboard clipboard;
	public String tipoContenido;
	private FlavorListener flavorListener;
	public Boolean pulsadoTeclas, recogido, copiadoDelSistema;
	public Object contenidoRecogido;

	/**
	 * Constructor, define las variables:
	 * 
	 * <ul>
	 * <li>clipboard: {@link Clipboard} controlador del portapapeles del
	 * sistema</li>
	 * <li>tipoContenido: {@link String} <code>"texto"</code>,
	 * <code>"imagen"</code>, <code>"ficheros"</code></li>
	 * <li>dataFlavorStringJava: {@link DataFlavor} mimetype texto</li>
	 * <li>dataFlavorBitmapJava: {@link DataFlavor} mimetype imagen</li>
	 * <li>dataFlavorFileJava: {@link DataFlavor} mimetype lista ficheros</li>
	 * </ul>
	 */

	public Clip() {
		recogido = false;
		copiadoDelSistema = false;
		pulsadoTeclas = false;
		clipboard = null;
		tipoContenido = "";
		try {
			dataFlavorStringJava = new DataFlavor("application/x-java-serialized-object; class=java.lang.String");
			dataFlavorHtmlJava = DataFlavor.allHtmlFlavor;
			dataFlavorFileJava = new DataFlavor("application/x-java-file-list; class=java.util.List");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void resetearClipboard() {
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.removeFlavorListener(this.flavorListener);
		clipboard.addFlavorListener(this.flavorListener);
	}

	public Clipboard resetearClipboard(Clipboard c) {
		c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.removeFlavorListener(this.flavorListener);
		c.addFlavorListener(this.flavorListener);
		return c;
	}

	public static Clipboard resetearClipboard(Clipboard c, FlavorListener flavorListenerAux) {
		c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.removeFlavorListener(flavorListenerAux);
		c.addFlavorListener(flavorListenerAux);
		return c;
	}

	public void proccessClipboard(Clipboard c) {
		if (pulsadoTeclas) {
			String s = contenidoTipoPortapapeles();
			if (s != null) {
				if (s.equals("html")) {
					Boolean copiado = false;
					while (!copiado) {
						c = resetearClipboard(c);
						try {
							Object objeto = getContenidoClipboard();
							if (objeto != null) {
								Transferable transferible = (Transferable) objeto;
								s = (String) ((transferible).getTransferData(dataFlavorHtmlJava));
							}
							copiado = true;
						} catch (UnsupportedFlavorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (java.lang.IllegalStateException e1) {
							copiado = false;
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!copiado) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							if (s != null) {
								contenidoRecogido = s;
								tipoContenido = "html";
							} else {
								contenidoRecogido = s;
								tipoContenido = "";
							}
						}

					}
				} else if (s.equals("texto")) {
					Boolean copiado = false;
					while (!copiado) {
						c = resetearClipboard(c);
						try {
							Object objeto = getContenidoClipboard();
							if (objeto != null) {
								Transferable transferible = (Transferable) objeto;
								s = (String) (transferible).getTransferData(dataFlavorStringJava);
							}
							copiado = true;
						} catch (UnsupportedFlavorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (java.lang.IllegalStateException e1) {
							copiado = false;
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!copiado) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							if (s != null) {
								contenidoRecogido = s;
								tipoContenido = "texto";
							} else {
								contenidoRecogido = s;
								tipoContenido = "";
							}
						}

					}

				} else if (s.equals("ficheros")) {
					Boolean copiado = false;
					List<?> lista = null;
					while (!copiado) {
						c = resetearClipboard(c);
						try {
							Object objeto = getContenidoClipboard();
							if (objeto != null) {
								Transferable transferible = (Transferable) objeto;
								lista = (List<?>) transferible.getTransferData(dataFlavorFileJava);
							}
							copiado = true;
						} catch (UnsupportedFlavorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (java.lang.IllegalStateException e1) {
							copiado = false;
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!copiado) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							if (lista != null) {
								contenidoRecogido = lista;
								tipoContenido = "ficheros";
							} else {
								contenidoRecogido = lista;
								tipoContenido = "";
							}
						}
					}
				}
			}
		}

	}

	public Object proccessClipboardBackup(Clipboard c) {
		String s = contenidoTipoPortapapeles();
		if (s != null) {
			if (s.equals("html")) {
				Boolean copiado = false;
				while (!copiado) {
					c = resetearClipboard(c);
					try {
						Object objeto = getContenidoClipboard();
						if (objeto != null) {
							Transferable transferible = (Transferable) objeto;
							s = (String) (transferible).getTransferData(dataFlavorHtmlJava);
						}
						copiado = true;
					} catch (UnsupportedFlavorException e1) {
						// TODO Auto-generated catch block
						copiado = true;
					} catch (java.lang.IllegalStateException e1) {
						copiado = false;
					} catch (IOException e) {
						s = null;
						copiado = true;
					}
					if (!copiado) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						if (s != null) {
							contenidoRecogido = s;
						} else {
							tipoContenido = "";
						}
					}

				}
			} else if (s.equals("texto")) {
				Boolean copiado = false;
				String texto = null;
				while (!copiado) {
					c = resetearClipboard(c);
					try {
						Object objeto = getContenidoClipboard();
						if (objeto != null) {
							Transferable transferible = (Transferable) objeto;
							texto = (String) (transferible).getTransferData(dataFlavorStringJava);
						}
						copiado = true;
					} catch (UnsupportedFlavorException e1) {
						// TODO Auto-generated catch block
						copiado = true;
						e1.printStackTrace();
					} catch (java.lang.IllegalStateException e1) {
						copiado = false;
					} catch (IOException e) {
						texto = null;
						copiado = true;
					}
					if (!copiado) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						if (texto != null) {
							contenidoRecogido = texto;
						} else {
							tipoContenido = "";
						}
					}

				}
			} else if (s.equals("ficheros")) {
				Boolean copiado = false;
				List<?> lista = null;
				while (!copiado) {
					c = resetearClipboard(c);
					try {
						Object objeto = getContenidoClipboard();
						if (objeto != null) {
							Transferable transferible = (Transferable) objeto;
							lista = (List<?>) transferible.getTransferData(dataFlavorFileJava);
						}
						copiado = true;
					} catch (UnsupportedFlavorException e1) {
						// TODO Auto-generated catch block
						copiado = true;
						e1.printStackTrace();
					} catch (java.lang.IllegalStateException e1) {
						copiado = true;
					} catch (IOException e) {
						lista = null;
						copiado = true;
					}
					if (!copiado) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						if (lista != null) {
							contenidoRecogido = lista;
						} else {
							tipoContenido = "";
						}
					}

				}
			}
		}

		return null;
	}

	public Boolean introducido(String string) {
		String stringAux = null;
		Object objeto = getContenidoClipboard();
		if (objeto != null) {
			Transferable transferible = (Transferable) objeto;
			try {
				stringAux = (String) (transferible).getTransferData(dataFlavorStringJava);
			} catch (UnsupportedFlavorException | IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
		} else {
			return false;
		}
		if (stringAux.equals(string)) {
			return true;
		}
		return false;
	}

	/**
	 * Introduce un objeto {@link Transferable} dentro del clipboard del sistema.
	 * 
	 * @param objeto El objeto a introducir.
	 */

	public void setContenidoClipboard(Transferable objeto) {
		if (objeto != null) {
			try {
				clipboard.setContents(objeto, null);
			} catch (Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
	}

	/**
	 * Introduce un objeto {@link DatoSeleccion} dentro del clipboard del sistema.
	 * 
	 * @param objeto El objeto a introducir.
	 */

	public void setContenidoClipboard(DatoSeleccion objeto) {
		if (objeto != null) {
			clipboard.setContents(objeto, objeto);

		}
	}

	/**
	 * Obtiene los datos de todos los objetos contenidos en el portapapeles del
	 * sistema.
	 * 
	 * @return Contenido del clipboard sin filtrar, es decir se devolvera un objeto
	 *         con toda la información que contiene el portapapeles del sistema.
	 */

	public Transferable getContenidoClipboard() {
		Boolean obtenido = false;
		Transferable objeto = null;
		while (!obtenido) {
			try {
				objeto = clipboard.getContents(null);
				obtenido = true;
			} catch (java.lang.IllegalStateException e) {
				resetearClipboard();
			}
		}
		return objeto;
	}

	public static Transferable getContenidoClipboard(Clipboard c, FlavorListener flavorListener)
			throws java.io.IOException {
		Boolean obtenido = false;
		Transferable objeto = null;
		while (!obtenido) {
			try {
				objeto = c.getContents(null);
				obtenido = true;
			} catch (java.lang.IllegalStateException e) {
				c = resetearClipboard(c, flavorListener);
			}
		}
		return objeto;
	}

	private String contenidoTipoPortapapeles() {
		Transferable t = (Transferable) getContenidoClipboard();
		if (t != null) {
			if (t.isDataFlavorSupported(dataFlavorFileJava)) {
				return "ficheros";
			} else if (t.isDataFlavorSupported(dataFlavorHtmlJava)) {
				return "html";
			} else if (t.isDataFlavorSupported(dataFlavorStringJava)) {
				return "texto";
			}
		}
		return null;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

	public FlavorListener getFlavorListener() {
		return flavorListener;
	}

	public void setFlavorListener(FlavorListener flavorListener) {
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.addFlavorListener(flavorListener);
	}
}
