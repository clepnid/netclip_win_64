package http.modulosBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import http.HttpBackend;
import http.HttpBackendUsuarios;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventanaGestionarModulo.SistemaModulos;

//lee el fichero clepnid.json de los modulos y los añade en el arraylist config.
public class JsonModulosBackend {
	ArrayList<EjecutarComando> listaEjecuciones;
	private String titulo;

	public JsonModulosBackend() {
		listaEjecuciones = new ArrayList<EjecutarComando>();
	}

	public void cerrar() {
		for (EjecutarComando ejecutarComando : listaEjecuciones) {
			ejecutarComando.start();
		}
	}

	@SuppressWarnings("unchecked")
	public void InstanciarWeb(String ruta) {

		String rutaJson = ruta.replace("\\", "/");
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(rutaJson)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray webList = (JSONArray) obj;
			System.out.println(webList);

			webList.forEach(web -> parseControlWebObject((JSONObject) web, rutaJson));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void parseControlWebObject(JSONObject employee, String ruta) {
		// Obtener web de la lista de webs
		JSONObject webObject = (JSONObject) employee.get("Web");
		// introducir titulo
		@SuppressWarnings("unused")
		String titulo = (String) webObject.get("Title");
		System.out.println("TITULO: " + titulo);
		this.titulo = titulo;

		// InicializarEnOpcionesVentana
		SistemaModulos.getInstance().inicializarModulo(titulo);

		String comando = (String) webObject.get("ComandOpen");
		String cambiarDirectorio = (String) webObject.get("ChangePath");
		String rutaPrompt = (String) webObject.get("PromptPath");
		String comandoCerrar = (String) webObject.get("ComandClose");
		if (comando != null) {
			if(cambiarDirectorio != null) {
				if (cambiarDirectorio.equals("yes")) {
					if (rutaPrompt != null) {
						new EjecutarComando(comando, rutaPrompt).start();
					}else {
						new EjecutarComando(comando, new File(ruta).getParent()).start();
					}
				}else {
					new EjecutarComando(comando).start();
				}
			}else {
				new EjecutarComando(comando).start();
			}
		}
		if (comandoCerrar != null) {
			listaEjecuciones.add(new EjecutarComando(comandoCerrar));
		}

		JSONArray listaRutas = (JSONArray) webObject.get("ListaRutas");
		if (listaRutas != null) {
			for (int i = 0; i < listaRutas.size(); i++) {
				JSONObject obj = (JSONObject) listaRutas.get(i);
				String rutaEjecutable = (String) obj.get("RutaEjecutable");
				String rutaClepnid = (String) obj.get("RutaClepnid");
				String tipo = (String) obj.get("Tipo");
				redireccionarRutaBackend(rutaEjecutable, rutaClepnid, tipo);
			}
		}
	}

	private void redireccionarRutaBackend(String rutaEjecutable, String rutaClepnid, String tipo) {
		if (tipo.equals("get")) {
			getRuta(rutaEjecutable, rutaClepnid);
		} else {
			postRuta(rutaEjecutable, rutaClepnid);
		}
	}

	private void getRuta(String rutaEjecutable, String rutaClepnid) {
		Spark.get(rutaClepnid, (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, rutaClepnid);
			if (n == 0 && SistemaModulos.getInstance().isValido(this.titulo)) {
				return JsonModulosBackend.getDataFromUrl(request, response, "http://" + rutaEjecutable);
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

	private static String obtenerTextoDeInputStream(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}
		return stringBuilder.toString();
	}

	private static String postDataFromUrl(Request request, Response response, String url) {
		try {
			response.header("Access-Control-Allow-Origin", "*");
			// Obtener los datos del formulario en formato JSON
			String jsonData = extractJsonFromForm(request);

			// Construir la conexión HTTP
			URL urlObject = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			// Configurar el contenido del cuerpo (body) de la solicitud POST
			byte[] postDataBytes = jsonData.getBytes(StandardCharsets.UTF_8);

			// Establecer las cabeceras de la solicitud
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

			// Obtener el flujo de salida para escribir los datos del cuerpo
			try (OutputStream os = connection.getOutputStream()) {
				os.write(postDataBytes);
			}

			// Obtener la respuesta y redirigir
			int responseCode = connection.getResponseCode();
			String responseText = obtenerTextoDeInputStream(connection.getInputStream());
			System.out.println("holaa: "+responseText);
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				return responseText;
			} else {
				// Manejar el caso en que la solicitud no sea exitosa
				return responseText;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}

	private static String extractJsonFromForm(Request request) {
		// Crear un objeto para almacenar los datos del formulario
		FormData formData = new FormData();

		if (request.queryParams().size() == 0) {
			return request.body();
		}

		// Iterar sobre los parámetros del formulario y agregarlos al objeto
		for (String paramName : request.queryParams()) {
			String paramValue = request.queryParams(paramName);
			formData.addField(paramName, paramValue);
		}

		// Convertir el objeto Java a JSON
		Gson gson = new Gson();
		return gson.toJson(formData);
	}

	private static class FormData {
		// Utiliza un mapa para almacenar campos y valores del formulario
		private Map<String, String> fields = new HashMap<>();

		public void addField(String fieldName, String fieldValue) {
			fields.put(fieldName, fieldValue);
		}
	}

	private void postRuta(String rutaEjecutable, String rutaClepnid) {
		Spark.post(rutaClepnid, (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, rutaClepnid);
			if (n == 0 && SistemaModulos.getInstance().isValido(this.titulo)) {
				return JsonModulosBackend.postDataFromUrl(request, response, "http://" + rutaEjecutable);
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

	public static boolean getDataFromUrl(Request request, Response response, String ruta) {
		InputStream is = null;
		try {
			URL url = new URL(ruta);
			is = url.openStream();
			int read = 0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.raw().getOutputStream();
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			os.flush();
			os.close();
		} catch (Exception e) {
			return false;
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
