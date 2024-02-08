package historial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListaHistorial extends CopyOnWriteArrayList<Historial> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ruta = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "Historial.ser";

	public ListaHistorial() {

	}

	public static Boolean existeFicheroConfig() {
		return new File(ruta).exists();
	}

	public static int getSizeLista() {
		try {
			return deserializar().size();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static Boolean controlarExistencia() {
		if (!existeFicheroConfig()) {
			ListaHistorial historialAux = new ListaHistorial();
			File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
			if (!ficheroAux.exists()) {
				ficheroAux.mkdir();
			}
			try {
				serializar(historialAux);
			} catch (IOException e) {
				return false;
			}

		}
		return true;
	}

	public static synchronized ListaHistorial deserializar() throws IOException, ClassNotFoundException {
		ListaHistorial historial = new ListaHistorial();
		FileInputStream archivo = new FileInputStream(ruta);
		ObjectInputStream entrada = new ObjectInputStream(archivo);
		historial = (ListaHistorial) entrada.readObject();
		entrada.close();
		archivo.close();
		return historial;
	}

	public static void anyadirHistoria(Historial historial) {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		historial.indice = listaHistorial.size();
		listaHistorial.add(historial);
		try {
			ListaHistorial.serializar(listaHistorial);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean deleteHistorial(Historial historial) {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listaHistorial.remove(historial.indice);
		for (int i = historial.indice; i < listaHistorial.size(); i++) {
			listaHistorial.get(i).indice--;
		}
		try {
			ListaHistorial.serializar(listaHistorial);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public static ListaHistorial ordenarFechaAscendente() {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(listaHistorial, new Comparator<Historial>() {
			@Override
			public int compare(Historial hist1, Historial hist2) {
				String[] fecha1 = hist1.getFecha().split(" ");
				String[] fechaAux1 = fecha1[0].split("/");
				String[] horaAux1 = fecha1[1].split(":");
				String[] fecha2 = hist2.getFecha().split(" ");
				String[] fechaAux2 = fecha2[0].split("/");
				String[] horaAux2 = fecha2[1].split(":");

				if (Integer.valueOf(fechaAux1[0]) > Integer.valueOf(fechaAux2[0])) {
					return -1;
				} else if (Integer.valueOf(fechaAux1[0]) < Integer.valueOf(fechaAux2[0])) {
					return 1;
				} else {
					if (Integer.valueOf(fechaAux1[1]) > Integer.valueOf(fechaAux2[1])) {
						return -1;
					} else if (Integer.valueOf(fechaAux1[1]) < Integer.valueOf(fechaAux2[1])) {
						return 1;
					} else {
						if (Integer.valueOf(fechaAux1[2]) > Integer.valueOf(fechaAux2[2])) {
							return -1;
						} else if (Integer.valueOf(fechaAux1[2]) < Integer.valueOf(fechaAux2[2])) {
							return 1;
						} else {
							if (Integer.valueOf(horaAux1[0]) > Integer.valueOf(horaAux2[0])) {
								return -1;
							} else if (Integer.valueOf(horaAux1[0]) < Integer.valueOf(horaAux2[0])) {
								return 1;
							} else {
								if (Integer.valueOf(horaAux1[1]) > Integer.valueOf(horaAux2[1])) {
									return -1;
								} else if (Integer.valueOf(horaAux1[1]) < Integer.valueOf(horaAux2[1])) {
									return 1;
								} else {
									return 0;
								}
							}
						}
					}
				}
			}
		});
		for (int i = 0; i < listaHistorial.size(); i++) {
			listaHistorial.get(i).indice = i;
		}
		return listaHistorial;
	}

	public static ListaHistorial ordenarFechaDescendente() {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(listaHistorial, new Comparator<Historial>() {
			@Override
			public int compare(Historial hist1, Historial hist2) {
				String[] fecha1 = hist1.getFecha().split(" ");
				String[] fechaAux1 = fecha1[0].split("/");
				String[] horaAux1 = fecha1[1].split(":");
				String[] fecha2 = hist2.getFecha().split(" ");
				String[] fechaAux2 = fecha2[0].split("/");
				String[] horaAux2 = fecha2[1].split(":");

				if (Integer.valueOf(fechaAux1[0]) > Integer.valueOf(fechaAux2[0])) {
					return 1;
				} else if (Integer.valueOf(fechaAux1[0]) < Integer.valueOf(fechaAux2[0])) {
					return -1;
				} else {
					if (Integer.valueOf(fechaAux1[1]) > Integer.valueOf(fechaAux2[1])) {
						return 1;
					} else if (Integer.valueOf(fechaAux1[1]) < Integer.valueOf(fechaAux2[1])) {
						return -1;
					} else {
						if (Integer.valueOf(fechaAux1[2]) > Integer.valueOf(fechaAux2[2])) {
							return 1;
						} else if (Integer.valueOf(fechaAux1[2]) < Integer.valueOf(fechaAux2[2])) {
							return -1;
						} else {
							if (Integer.valueOf(horaAux1[0]) > Integer.valueOf(horaAux2[0])) {
								return 1;
							} else if (Integer.valueOf(horaAux1[0]) < Integer.valueOf(horaAux2[0])) {
								return -1;
							} else {
								if (Integer.valueOf(horaAux1[1]) > Integer.valueOf(horaAux2[1])) {
									return 1;
								} else if (Integer.valueOf(horaAux1[1]) < Integer.valueOf(horaAux2[1])) {
									return -1;
								} else {
									return 0;
								}
							}
						}
					}
				}
			}
		});
		for (int i = 0; i < listaHistorial.size(); i++) {
			listaHistorial.get(i).indice = i;
		}

		return listaHistorial;
	}

	public static ListaHistorial ordenarNombreAscendente() {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(listaHistorial, new Comparator<Historial>() {
			@Override
			public int compare(Historial hist1, Historial hist2) {
				if (hist1.esFichero && hist2.esFichero) {
					return hist1.nombreFichero.compareToIgnoreCase(hist2.nombreFichero);
				}else if (hist1.esFichero && !hist2.esFichero) {
					return hist1.nombreFichero.compareToIgnoreCase(hist2.contenido);
				}else if (!hist1.esFichero && hist2.esFichero) {
					return hist1.contenido.compareToIgnoreCase(hist2.nombreFichero);
				}else {
					return hist1.contenido.compareToIgnoreCase(hist2.contenido);
				}
			}
		});
		for (int i = 0; i < listaHistorial.size(); i++) {
			listaHistorial.get(i).indice = i;
		}

		return listaHistorial;
	}

	public static ListaHistorial ordenarNombreDescendente() {
		ListaHistorial listaHistorial = null;
		try {
			listaHistorial = ListaHistorial.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(listaHistorial, new Comparator<Historial>() {
			@Override
			public int compare(Historial hist1, Historial hist2) {
				if (hist1.esFichero && hist2.esFichero) {
					return hist2.nombreFichero.compareToIgnoreCase(hist1.nombreFichero);
				}else if (hist1.esFichero && !hist2.esFichero) {
					return hist2.contenido.compareToIgnoreCase(hist1.nombreFichero);
				}else if (!hist1.esFichero && hist2.esFichero) {
					return hist2.nombreFichero.compareToIgnoreCase(hist1.contenido);
				}else {
					return hist2.contenido.compareToIgnoreCase(hist1.contenido);
				}
			}
		});
		for (int i = 0; i < listaHistorial.size(); i++) {
			listaHistorial.get(i).indice = i;
		}

		return listaHistorial;
	}

	public static synchronized void serializar(ListaHistorial historial) throws IOException {
		FileOutputStream archivo = new FileOutputStream(ruta);
		ObjectOutputStream salida = new ObjectOutputStream(archivo);
		salida.writeObject(historial);
		salida.close();
		archivo.close();
	}

}
