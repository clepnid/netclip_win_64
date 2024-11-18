package http.modulosBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import http.HttpBackend;
import http.HttpBackendUsuarios;
import http.actividad.Actividad;
import http.actividad.ActividadApi;
import http.actividad.LocalDateTimeAdapter;
import spark.Spark;
import ventana.Configuracion;
import ventana.Ventana;

public class EjecutarComando extends Thread {

	public static boolean salir = false;
	private String nombre;
	private boolean mostrarActividad;
	private ArrayList<Actividad> listaActividad;
	public boolean conOutput;
	public boolean cambiarDirectorioTrabajo;
	public String rutaDirectorioTrabajo;
	private String comando;
	private int nActivityLog;
	private String valorActivityLog;

	public String getComando() {
		return comando;
	}

	public void cambiarRutaDirectorioTrabajo(String ruta) {
		this.cambiarDirectorioTrabajo = true;
		this.rutaDirectorioTrabajo = ruta;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public static void cerrar() {
		salir = true;
	}

	private void crearGetActividadConsola() {
		Spark.get("/actividades/Consola/" + this.nombre, (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/actividades/Consola/" + this.nombre);
			if (n == 0) {
				try {
					Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
							.create();
					if (valorActivityLog!=null) {
						ArrayList<Actividad> listaAux = new ArrayList<Actividad>();
						for (int i = 0; i < this.listaActividad.size(); i++) {
							if (i==nActivityLog) {
								Actividad act = this.listaActividad.get(i);
								Actividad actividadAu = ActividadApi.crearActividad(0, act.getNombre(),
										Configuracion.getNombreRed());
								String rutaA = "";
								if (cambiarDirectorioTrabajo) {
									rutaA = rutaDirectorioTrabajo + File.separator + valorActivityLog;
								} else {
									rutaA = valorActivityLog;
								}
								actividadAu.anyadirALista(0, readFileToString(rutaA));
								listaAux.add(actividadAu);
							}else {
								listaAux.add(this.listaActividad.get(i));
							}
						}
						return gson.toJson(listaAux);
					}
					return gson.toJson(this.listaActividad);
				} catch (Exception e) {
					// Manejar cualquier excepción que ocurra
					response.status(500); // Internal Server Error
					return "Error al obtener las actividades públicas: " + e.getMessage();
				}
			} else {
				if (n == 1) {
					return HttpBackend.renderIndex(request, response, "./src/html/403/index.html", "/403/index.html");
				} else {
					try {
						response.raw().sendRedirect("/login-layout");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			}
		});
	}

	public EjecutarComando(String nombre, String comando, boolean mostrarActividad) {
		this.listaActividad = new ArrayList<Actividad>();
		this.nombre = nombre;
		this.comando = comando;
		this.conOutput = false;
		this.mostrarActividad = mostrarActividad;
		crearGetActividadConsola();
	}

	public EjecutarComando(String nombre, String comando, String ruta, boolean mostrarActividad) {
		this.listaActividad = new ArrayList<Actividad>();
		this.nombre = nombre;
		this.comando = comando;
		this.conOutput = false;
		this.cambiarRutaDirectorioTrabajo(ruta);
		this.mostrarActividad = mostrarActividad;
		crearGetActividadConsola();
	}

	private void anyadirALista(int nPosicion, int nActividad, String item) {
		Actividad actividad;
		try {
			actividad = this.listaActividad.get(nPosicion);
			actividad.anyadirALista(nActividad, item);
		} catch (Exception e) {
			System.out.print("");
		}
	}

	private static String readFileToString(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = null;

		try {
			if (!new File(filePath).exists()) {
				return "";
			}
			reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("<br>"); // Agrega un salto de línea para mantener el formato del archivo
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					return "";
				}
			}
		}

		return content.toString();
	}

	@Override
	public void run() {
		if (conOutput) {
			ejecucionConOutput();
		} else {
			ejecucionSinOutput();
		}

	}

	public void ejecucionSinOutput() {
		Runtime rt = Runtime.getRuntime();
		try {
			@SuppressWarnings("unused")
			String line;
			if (Ventana.OS.equals("WINDOWS")) {
				nActivityLog = ComandLogIdentificator.getLogPosicionCommand(this.comando);
				valorActivityLog = ComandLogIdentificator.getLog(this.comando);
				String[] comandosAux = null;
				if (valorActivityLog != null) {
					comandosAux = ComandLogIdentificator.deleteLog(this.comando).split("\\|");
				} else {
					comandosAux = this.comando.split("\\|");
				}
				int nActividad = -1;
				for (String comand : comandosAux) {
					nActividad++;

					String[] arrayAux = comand.split(" ");
					ArrayList<String> listaComandos = new ArrayList<String>();
					for (String s : arrayAux) {
						if (!s.equals("")) {
							listaComandos.add(s);
						}
					}
					Process proc;
					BufferedReader stdInput = null;
					BufferedReader stdError = null;
					try {
						if (mostrarActividad) {
							Actividad actividad = ActividadApi.crearActividad(0, comand,
									Configuracion.getNombreRed());
							this.listaActividad.add(actividad);
						}
						if (cambiarDirectorioTrabajo) {
							proc = rt.exec(listaComandos.toArray(new String[0]), null, new File(rutaDirectorioTrabajo));
						} else {
							proc = rt.exec(listaComandos.toArray(new String[0]));
						}

						stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

						stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

						String s = null;
						while ((s = stdInput.readLine()) != null && !salir) {
							if (mostrarActividad) {
								anyadirALista(nActividad, 0, s);
							}
							System.out.print("");
						}

						stdInput.close();

						// read any errors from the attempted command
						while ((s = stdError.readLine()) != null && !salir) {
							if (mostrarActividad) {
								anyadirALista(nActividad, 1, s);
							}
							System.out.print("");
						}
						stdError.close();
						if (proc != null) {
							proc.destroy();
						}
					} catch (IOException e1) {
						try {
							if (stdInput != null) {
								stdInput.close();
							}
							if (stdInput != null) {
								stdError.close();
							}
						} catch (IOException e) {
							System.out.print("");
						}
					}
				}
			} else {
				String[] comandosAux = this.comando.split("\\|");
				int nActividad = -1;
				for (String comand : comandosAux) {
					if (!comand.equals("")) {
						nActividad++;
						Process proc;
						BufferedReader stdInput = null;
						BufferedReader stdError = null;
						try {
							if (mostrarActividad) {
								Actividad actividad = ActividadApi.crearActividad(0, comand,
										Configuracion.getNombreRed());
								this.listaActividad.add(actividad);
							}
							if (cambiarDirectorioTrabajo) {
								proc = rt.exec(comand, null, new File(rutaDirectorioTrabajo));
							} else {
								proc = rt.exec(comand);
							}
							stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

							stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

							String s = null;
							while ((s = stdInput.readLine()) != null && !salir) {
								if (mostrarActividad) {
									anyadirALista(nActividad, 0, s);
								}
								System.out.print(s);
							}

							stdInput.close();

							s = null;
							// read any errors from the attempted command
							while ((s = stdError.readLine()) != null && !salir) {
								if (mostrarActividad) {
									anyadirALista(nActividad, 1, s);
								}
								System.out.print(s);
							}
							stdError.close();
							if (proc != null) {
								proc.destroy();
							}
						} catch (IOException e1) {
							try {
								if (stdInput != null) {
									stdInput.close();
								}
								if (stdInput != null) {
									stdError.close();
								}
							} catch (IOException e) {
								System.out.print("");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ejecucionConOutput() {
		Runtime rt = Runtime.getRuntime();
		try {
			@SuppressWarnings("unused")
			String line;
			if (Ventana.OS.equals("WINDOWS")) {
				String[] comandosAux = this.comando.split("\\|");
				int nActividad = -1;
				for (String comand : comandosAux) {
					nActividad++;
					String[] arrayAux = comand.split(" ");
					ArrayList<String> listaComandos = new ArrayList<String>();
					for (String s : arrayAux) {
						if (!s.equals("")) {
							listaComandos.add(s);
						}
					}
					Process proc;
					BufferedReader stdInput = null;
					BufferedReader stdError = null;
					try {
						if (mostrarActividad) {
							Actividad actividad = ActividadApi.crearActividad(0, comand, Configuracion.getNombreRed());
							this.listaActividad.add(actividad);
						}
						if (cambiarDirectorioTrabajo) {
							proc = rt.exec(listaComandos.toArray(new String[0]), null, new File(rutaDirectorioTrabajo));
						} else {
							proc = rt.exec(listaComandos.toArray(new String[0]));
						}

						stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

						stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

						String s = null;
						while ((s = stdInput.readLine()) != null && !salir) {
							if (mostrarActividad) {
								anyadirALista(nActividad, 0, s);
							}
							System.out.println(s);
						}

						stdInput.close();

						// read any errors from the attempted command
						while ((s = stdError.readLine()) != null && !salir) {
							if (mostrarActividad) {
								anyadirALista(nActividad, 1, s);
							}
							System.out.println(s);
						}
						stdError.close();
						if (proc != null) {
							proc.destroy();
						}
					} catch (IOException e1) {
						try {
							if (stdInput != null) {
								stdInput.close();
							}
							if (stdInput != null) {
								stdError.close();
							}
						} catch (IOException e) {
							System.out.print("");
						}
					}
				}
			} else {
				String[] comandosAux = this.comando.split("\\|");
				int nActividad = -1;
				for (String comand : comandosAux) {
					if (!comand.equals("")) {
						nActividad++;
						Process proc;
						BufferedReader stdInput = null;
						BufferedReader stdError = null;
						try {
							if (mostrarActividad) {
								Actividad actividad = ActividadApi.crearActividad(0, comand,
										Configuracion.getNombreRed());
								this.listaActividad.add(actividad);
							}
							if (cambiarDirectorioTrabajo) {
								proc = rt.exec(comand, null, new File(rutaDirectorioTrabajo));
							} else {
								proc = rt.exec(comand);
							}
							stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

							stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

							String s = null;
							while ((s = stdInput.readLine()) != null && !salir) {
								if (mostrarActividad) {
									anyadirALista(nActividad, 0, s);
								}
								System.out.print(s);
							}

							stdInput.close();

							s = null;
							// read any errors from the attempted command
							while ((s = stdError.readLine()) != null && !salir) {
								if (mostrarActividad) {
									anyadirALista(nActividad, 1, s);
								}
								System.out.print(s);
							}
							stdError.close();
							if (proc != null) {
								proc.destroy();
							}
						} catch (IOException e1) {
							try {
								if (stdInput != null) {
									stdInput.close();
								}
								if (stdInput != null) {
									stdError.close();
								}
							} catch (IOException e) {
								System.out.print("");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
