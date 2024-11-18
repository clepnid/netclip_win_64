package red.api;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Controlador {

	private static final String API_URL_VER = "https://clepnid-api.vercel.app/api/ver";
	private static final String API_URL_CREAR = "https://clepnid-api.vercel.app/api/create";
	private static final String API_URL_UPDATE = "https://clepnid-api.vercel.app/api/update";
	private static final Gson gson = new Gson();

	// Clase para representar el cuerpo de la solicitud
	static class ApiRequest {
		int opcion_post;
		String token;

		public ApiRequest(int opcion_post, String token) {
			this.opcion_post = opcion_post;
			this.token = token;
		}
	}

	// Clase para manejar la respuesta de la API
	static class ApiResponse {
		String message;
		TokenClepnid data;

		@Override
		public String toString() {
			return "Message: " + message + ", Data: " + data;
		}
	}

	// Método genérico para enviar solicitudes POST
	private static String sendPost(String url, JsonObject requestBody) throws IOException, ParseException {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON));

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		}
	}

	// Función para hacer una solicitud POST a la API usando HttpClient
	public static TokenClepnid sendPostRequestVer(int opcionPost, String token) throws IOException {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("opcion_post", opcionPost);
		requestBody.addProperty("token", token);

		String responseString;
		try {
			responseString = sendPost(API_URL_VER, requestBody);

			JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();
			String jsonDataString = jsonResponse.get("json_data").getAsString();
			JsonObject jsonDataObject = JsonParser.parseString(jsonDataString).getAsJsonObject();
			JsonData jsonData = gson.fromJson(jsonDataObject, JsonData.class);

			return new TokenClepnid(jsonResponse.get("id").getAsString(), jsonResponse.get("token").getAsString(),
					jsonData, jsonResponse.get("fecha").getAsString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	// Función para actualizar datos
	public static ApiResponse sendPostRequestUpdate(int opcionPost, String id, String token, JsonData jsonData, Date fecha) throws IOException {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("opcion_post", opcionPost);
		requestBody.addProperty("id", id);
		requestBody.addProperty("token", token);
		requestBody.addProperty("json_data", JsonDataToJson(jsonData));
		requestBody.addProperty("fecha", formatDateToYearMonthDay(fecha));

		String responseString;
		try {
			responseString = sendPost(API_URL_UPDATE, requestBody);
			return gson.fromJson(responseString, ApiResponse.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	// Función para crear un nuevo recurso
	public static ApiResponse sendPostRequestCrear(int opcionPost, String token, JsonData jsonData, Date fecha)
			throws IOException {
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("opcion_post", opcionPost);
		requestBody.addProperty("token", token);
		requestBody.addProperty("json_data", JsonDataToJson(jsonData));
		requestBody.addProperty("fecha", formatDateToYearMonthDay(fecha));

		String responseString;
		try {
			responseString = sendPost(API_URL_CREAR, requestBody);

			return gson.fromJson(responseString, ApiResponse.class);
		} catch (Exception e) {
			return null;
		}
	}

	private static String ListOfJsonDataToJson(List<String> lista) {
		// Crear el objeto JsonObject
		JsonObject jsonObject = new JsonObject();
		if (lista == null || lista.size() == 0) {
			String[] aux = new String[0];
			jsonObject.add("ips", new Gson().toJsonTree(aux));
		} else {
			jsonObject.add("ips", new Gson().toJsonTree(lista));
		}

		// Convertir el JsonObject a un string JSON
		String jsonString = new Gson().toJson(jsonObject);

		return jsonString;
	}

	private static String JsonDataToJson(JsonData jsonData) {
		return ListOfJsonDataToJson(jsonData.getIps());
	}

	private static String formatDateToYearMonthDay(Date date) {
		// Validar que el objeto Date no sea nulo
		if (date == null) {
			throw new IllegalArgumentException("La fecha no puede ser nula");
		}

		// Crear el formateador con el patrón deseado
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		// Formatear la fecha y devolverla como String
		return formatter.format(date);
	}
	
	public static boolean isUpdated(List<String> lista1, List<String> lista2) {
        // Validar que las listas no sean nulas
        if (lista1 == null || lista2 == null) {
            return true;
        }

        // Si las listas tienen tamaños diferentes, retornamos true
        if (lista1.size() != lista2.size()) {
            return true;
        }

        // Crear copias de las listas para no modificar las originales
        List<String> copy1 = lista1;
        List<String> copy2 = lista2;

        // Ordenar ambas listas
        Collections.sort(copy1);
        Collections.sort(copy2);

        // Comparar las listas ordenadas
        return !copy1.equals(copy2);
    }

	public static void main(String[] args) {
		String token = "12345689";
		ArrayList<String> lista = new ArrayList<String>();
		lista.add("192.168.2.2");
		lista.add("192.168.3.3");
		JsonData j = new JsonData();
		j.setIps(lista);

		try {
			TokenClepnid response = sendPostRequestVer(1, token);

			if (response == null) {
				System.out.println("Crear: " + token);
				sendPostRequestCrear(1, token, j, new Date());
			} else {
				System.out.println(response);
				if (isUpdated(lista, response.getJsonData().getIps())) {
					System.out.println("UPdate");
					sendPostRequestUpdate(1, response.getId(), token, j, new Date());
				}
			}
		} catch (IOException e) {
			
		}
	}
}
