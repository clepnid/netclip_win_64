package http.actividad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import http.HttpBackend;
import http.HttpBackendUsuarios;
import spark.Spark;

public class ActividadApi implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String RUTAARCHIVOACTIVIDAD = System.getProperty("user.home") + File.separator + "Clepnid"
			+ File.separator + "actividad.ser";
	private static ActividadApi api;
	private ArrayList<Actividad> listaActividades;

	private ArrayList<Actividad> getLista() {
		return this.listaActividades;
	}

	private ActividadApi() {
		this.listaActividades = new ArrayList<Actividad>();
	}

	private static synchronized ActividadApi getApi() {
		return api;
	}

	private static synchronized void setApi(ActividadApi apiAux) {
		api = apiAux;
	}

	private static void crearGet() {
		Spark.get("/actividades", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/actividades");
			if (n == 0) {
				try {
					Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
							.create();
					return gson.toJson(getApi().getLista());
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

	private static void crearGetVaciar() {
		Spark.get("/actividades/vaciar", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/actividades/vaciar");
			if (n == 0) {
				setApi(new ActividadApi());
				try {
					serializar(getApi());
				} catch (IOException e) {
					return false;
				}
				return true;
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

	private static void crearPost() {
		Spark.post("/actividades", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/actividades");
			if (n == 0) {
				try {
					// Obtener el JSON de la solicitud
					String json = request.body();

					// Convertir el JSON a un objeto Java usando Gson
					Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
							.create();
					Actividad actividad = gson.fromJson(json, Actividad.class);

					// Realizar las acciones necesarias con la actividad recibida
					System.out.println("Nombre: " + actividad.getNombre());
					System.out.println("Usuario: " + actividad.getUsuario());

					ActividadApi.getInstance().addActividad(actividad);

					// Respuesta de éxito
					return "Actividad recibida correctamente.";
				} catch (Exception e) {
					// Manejar cualquier excepción que ocurra
					response.status(400); // Bad Request
					e.printStackTrace();
					return "Error al procesar la solicitud: " + e.getMessage();
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

	public void addActividad(long id, String text) {
		try {
			setApi(deserializar());
			boolean encontrado = false;
			int aux = -1;
			for (Actividad act : getApi().getLista()) {
				aux++;
				if (act.getId() == id) {
					encontrado = true;
					break;
				}
			}
			if (encontrado) {
				Actividad act = new Actividad();
				Actividad actAux = getApi().getLista().get(aux);
				act.setId(actAux.getId());
				act.setFechaHora(actAux.getFechaHora());
				act.setNombre(actAux.getNombre());
				act.setTipo(actAux.getTipo());
				act.setUsuario(actAux.getUsuario());
				act.setTipo(actAux.getTipo());
				ArrayList<String> lista = new ArrayList<String>();
				for (String string : actAux.getListaActividad()) {
					lista.add(string);
				}
				lista.add(text);
				act.setListaActividad(lista);
				getApi().getLista().remove(aux);
				getApi().getLista().add(act);
				serializar(getApi());
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addActividadExterna(Actividad actividad) {
		try {
			setApi(deserializar());
			getApi().getLista().add(actividad);
			serializar(getApi());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addActividad(Actividad actividad) {
		try {
			LocalDateTime fechaHoraActual = LocalDateTime.now();
			actividad.setFechaHora(fechaHoraActual);

			setApi(deserializar());
			boolean encontrado = false;
			int aux = -1;
			for (Actividad act : getApi().getLista()) {
				aux++;
				if (act.getId() == actividad.getId() && act.getId() != 0) {
					encontrado = true;
					break;
				}
			}
			if (encontrado) {
				getApi().getLista().remove(aux);
			}
			getApi().getLista().add(actividad);
			serializar(getApi());

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ActividadApi getInstance() {
		if (getApi() == null) {
			controlarExistencia();
			anyadirActividadInicioModulos();
			anyadirActividadInicioBarraNavegacion();
			crearPost();
			crearGet();
			crearGetVaciar();
		}
		return getApi();
	}

	private static void anyadirActividadInicioModulos() {
		Actividad actividad = crearActividad(1, "Introducción módulos del sistema", "Inicio Aplicación Escritorio");
		ActividadApi.getInstance().addActividad(actividad);
	}

	private static void anyadirActividadInicioBarraNavegacion() {
		Actividad actividad = crearActividad(2, "Introducción entrada barra navegación",
				"Inicio Aplicación Escritorio");
		ActividadApi.getInstance().addActividad(actividad);
	}

	public static Actividad crearActividad(long id, String nombre, String usuario) {
		Actividad actividad = new Actividad();
		LocalDateTime fechaHoraActual = LocalDateTime.now();
		actividad.setFechaHora(fechaHoraActual);
		actividad.setId(id);
		actividad.setNombre(nombre);
		actividad.setTipo("public");
		actividad.setUsuario(usuario);
		return actividad;
	}

	private static void controlarExistencia() {
		File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
		if (!ficheroAux.exists()) {
			ficheroAux.mkdir();
		}
		if (!existeFicheroActividadApi()) {
			setApi(new ActividadApi());
			try {
				serializar(getApi());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			setApi(deserializar());
		} catch (ClassNotFoundException e) {
			System.out.print("");
		} catch (IOException e) {
			System.out.print("");
		}
	}

	private static Boolean existeFicheroActividadApi() {
		return new File(RUTAARCHIVOACTIVIDAD).exists();
	}

	private synchronized static ActividadApi deserializar() throws IOException, ClassNotFoundException {
		ActividadApi api = new ActividadApi();
		FileInputStream archivo = new FileInputStream(RUTAARCHIVOACTIVIDAD);
		ObjectInputStream entrada = new ObjectInputStream(archivo);
		try {
			api = (ActividadApi) entrada.readObject();
		} catch (Exception e) {
			
		}
		entrada.close();
		archivo.close();
		return api;
	}

	private synchronized static void serializar(ActividadApi api) throws IOException {
		FileOutputStream archivo = new FileOutputStream(RUTAARCHIVOACTIVIDAD);
		ObjectOutputStream salida = new ObjectOutputStream(archivo);
		salida.writeObject(api);
		salida.close();
		archivo.close();
	}
}
