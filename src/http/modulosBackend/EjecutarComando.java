package http.modulosBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ventana.Ventana;

public class EjecutarComando extends Thread {
	public static boolean salir = false;
	public boolean conOutput;
	public boolean cambiarDirectorioTrabajo;
	public String rutaDirectorioTrabajo;
	private String comando;

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

	public EjecutarComando(String comando) {
		this.comando = comando;
		this.conOutput = false;
	}
	
	public EjecutarComando(String comando, String ruta) {
		this.comando = comando;
		this.conOutput = false;
		this.cambiarRutaDirectorioTrabajo(ruta);
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
				String[] comandosAux = this.comando.split("\\|");
				for (String comand : comandosAux) {
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

						if (cambiarDirectorioTrabajo) {
							proc = rt.exec(listaComandos.toArray(new String[0]), null, new File(rutaDirectorioTrabajo));
						} else {
							proc = rt.exec(listaComandos.toArray(new String[0]));
						}
						
						stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

						stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

						// read the output from the command
						while ((stdInput.readLine()) != null && !salir) {
							System.out.print("");
						}

						stdInput.close();

						// read any errors from the attempted command
						while ((stdError.readLine()) != null && !salir) {
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
				for (String comand : comandosAux) {
					if (!comand.equals("")) {
						Process proc;
						BufferedReader stdInput = null;
						BufferedReader stdError = null;
						try {
							if (cambiarDirectorioTrabajo) {
								proc = rt.exec(comand, null, new File(rutaDirectorioTrabajo));
							} else {
								proc = rt.exec(comand);
							}
							stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

							stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

							while ((stdInput.readLine()) != null && !salir) {
								System.out.print("");
							}

							stdInput.close();

							// read any errors from the attempted command
							while ((stdError.readLine()) != null && !salir) {
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
				for (String comand : comandosAux) {
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
						if (cambiarDirectorioTrabajo) {
							proc = rt.exec(listaComandos.toArray(new String[0]), null, new File(rutaDirectorioTrabajo));
						} else {
							proc = rt.exec(listaComandos.toArray(new String[0]));
						}
						stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

						stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

						// read the output from the command
						System.out.println("Here is the standard output of the command:\n");

						String s = null;
						while ((s = stdInput.readLine()) != null && !salir) {
							System.out.println(s);
						}

						stdInput.close();

						// read any errors from the attempted command
						System.out.println("Here is the standard error of the command (if any):\n");
						while ((s = stdError.readLine()) != null && !salir) {
							System.out.println("ERROR: " + s);

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
				for (String comand : comandosAux) {
					if (!comand.equals("")) {
						Process proc;
						BufferedReader stdInput = null;
						BufferedReader stdError = null;
						try {
							if (cambiarDirectorioTrabajo) {
								proc = rt.exec(comand, null, new File(rutaDirectorioTrabajo));
							} else {
								proc = rt.exec(comand);
							}
							stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

							stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

							// read the output from the command
							System.out.println("Here is the standard output of the command:\n");

							String s = null;
							while ((s = stdInput.readLine()) != null && !salir) {
								System.out.println(s);
							}

							stdInput.close();

							// read any errors from the attempted command
							System.out.println("Here is the standard error of the command (if any):\n");
							while ((s = stdError.readLine()) != null && !salir) {
								System.out.println("ERROR: " + s);

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
