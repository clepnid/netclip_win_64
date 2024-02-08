package http;

import static spark.Spark.get;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import spark.Request;
import spark.Response;
import spark.Session;
import usuarios.ListaAcesoGrupos;
import usuarios.SistemaUsuarios;
import usuarios.Usuario;

/**
 * En esta clase se crea login registro y tiene metodos para saber si un usuario
 * puede ver una pagina.
 * 
 * @author pavon
 *
 */

public class HttpBackendUsuarios {
	public static final String RANDOM_STRING = "vqXTWb9CzO8sW41EQpmhL0GXM8kw7aZFAK2bv7meXtDCQdyyf1";
	private static final String SESSION_NAME = "usernameClepnid";
	private Http http;

	public Http getHttp() {
		return http;
	}

	public static void crearControlUsuarios() {

		get("/login", (request, response) -> {
			if (getNombreUsuario(request, response, SistemaUsuarios.deserializar()) != null) {
				response.redirect("/menu");
			} else {
				response.redirect("/login-layout/index.html");
			}
			return null;
		});

		get("/login/register", (request, response) -> {
			SistemaUsuarios sistemaUsuarios = SistemaUsuarios.deserializar();
			String username = request.queryParams("user");
			String email = request.queryParams("email");
			String password = request.queryParams("pass");
			String hash = hashPassword(username, password);

			try {
				if (!checkPassword(username, hash, sistemaUsuarios)) {
					Usuario us = new Usuario(username, password, hash);
					us.setEmail(email);
					sistemaUsuarios.anyadirUsuario(us);
					sistemaUsuarios.anyadirLog(hash);
					anyadirCookies(request, response, hash);
					boolean o = sistemaUsuarios.abrirSesionDesdeHash(hash);
					System.out.println("---------- Abrir sesion: " + o);
					JSONParser parser = new JSONParser();
					String s = "[{\"message\":\"exito\"}]";
					Object obj = parser.parse(s);
					JSONArray array = (JSONArray) obj;
					response.type("application/json");
					return array;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.type("application/json");
			JSONParser parser = new JSONParser();
			String s = "[{\"message\":\"exito\"}]";
			Object obj = parser.parse(s);
			JSONArray array = (JSONArray) obj;
			response.type("application/json");
			return array;
		});

		get("/login/entry", (request, response) -> {
			SistemaUsuarios sistemaUsuarios = SistemaUsuarios.deserializar();
			String username = request.queryParams("user");
			String password = request.queryParams("pass");
			// username = "pavon1";
			// password = "1234";
			String hash = hashPassword(username, password);

			try {
				if (checkPassword(username, hash, sistemaUsuarios)) {
					System.out.println("----------usuario: " + username + "  , esta iniciado?: "
							+ sistemaUsuarios.getUsuario(username).isSesionActiva());
					sistemaUsuarios.anyadirLog(hash);
					boolean o = sistemaUsuarios.abrirSesionDesdeHash(hash);
					System.out.println("---------- Abrir sesion: " + o + " del usuario: "
							+ sistemaUsuarios.getUsuarioDesdeHash(hash).getUsuario());
					anyadirCookies(request, response, hash);
					JSONParser parser = new JSONParser();
					String s = "[{\"message\":\"exito\"}]";
					Object obj = parser.parse(s);
					JSONArray array = (JSONArray) obj;
					response.type("application/json");
					return array;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONParser parser = new JSONParser();
			String s = "[{\"message\":\"exito\"}]";
			Object obj = parser.parse(s);
			JSONArray array = (JSONArray) obj;
			response.type("application/json");
			return array;
		});

		get("/login/clear", (request, response) -> {
			SistemaUsuarios sistemaUsuarios = SistemaUsuarios.deserializar();

			sistemaUsuarios.cerrarSesionDesdeHash(getHashCookies(request, response, sistemaUsuarios));
			eliminarCookies(request, response, sistemaUsuarios);
			response.redirect("/menu");
			return null;
		});
	}

	public static void eliminarCookies(Request request, Response response, SistemaUsuarios sistemaUsuarios) {
		sistemaUsuarios.eliminarLog(getHashCookies(request, response, sistemaUsuarios));
		response.removeCookie("/", SESSION_NAME);
		request.session().removeAttribute(SESSION_NAME);
	}

	public static void anyadirCookies(Request request, Response response, String hash) {
		Session session = request.session(true);
		session.attribute(SESSION_NAME, hash);
		response.cookie("/", SESSION_NAME, hash, 31557600, false, true);// one year
	}

	public static String getNombreUsuario(Request req, Response res, SistemaUsuarios sis) {
		String hash = req.session().attribute(SESSION_NAME);
		if (hash == null) {
			hash = req.cookie(SESSION_NAME);
		}
		if (hash != null) {
			Usuario u = sis.getUsuarioDesdeHash(hash);
			if (u != null) {
				return u.getUsuario();
			}
		}
		return null;
	}

	public static ArrayList<String> getGruposUsuario(Request req, Response res, SistemaUsuarios sis) {
		String hash = req.session().attribute(SESSION_NAME);
		if (hash == null) {
			hash = req.cookie(SESSION_NAME);
		}
		if (hash != null) {
			Usuario u = sis.getUsuarioDesdeHash(hash);
			if (u != null) {
				return u.getGrupos();
			}
		}
		return null;
	}

	public static String getNombreUsuario(Request req, Response res) {
		String hash = req.session().attribute(SESSION_NAME);
		if (hash == null) {
			hash = req.cookie(SESSION_NAME);
		}
		if (hash != null) {
			SistemaUsuarios sis = SistemaUsuarios.deserializar();
			return sis.getUsuarioDesdeHash(hash).getUsuario();
		}
		return null;
	}

	static private String hashPassword(String username, String password) throws NoSuchAlgorithmException {
		String toHash = username + RANDOM_STRING + password;
		return DigestUtils.sha256Hex(toHash);
	}

	private static boolean checkPassword(String nombre, String hash, SistemaUsuarios sistemaUsuarios) {
		return sistemaUsuarios.seEncuentraHash(nombre, hash);
	}

	public static boolean checkSession(Request req, Response res, SistemaUsuarios sistemaUsuarios) {
		try {
			if (req.cookies().containsKey(SESSION_NAME)) {
				return sistemaUsuarios.seEncuentraIniciado(req.cookie(SESSION_NAME));
			}
			if (req.session().attribute(SESSION_NAME) != null) {
				return sistemaUsuarios.seEncuentraIniciado(req.session().attribute(SESSION_NAME));
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	public static String getHashCookies(Request req, Response res, SistemaUsuarios sis) {
		String hash = req.session().attribute(SESSION_NAME);
		if (hash == null) {
			if (sis.getUsuarioDesdeHash(hash) != null) {
				hash = req.cookie(SESSION_NAME);
			}
		}
		return hash;
	}

	/**
	 * Metodo principal retorna 0 si en las coockies del navegador se encuentra un
	 * usuario y este contiene contiene al grupo en cuestion. * en grupo para que
	 * cualquiera tenga permiso. retorna 1 si no se tiene permisos retorna 2 si no
	 * se ha iniciado sesion
	 * 
	 * @param req
	 * @param res
	 * @param grupo
	 * @return
	 */

	public static int tienePermiso(Request req, Response res, String ruta) {
		SistemaUsuarios sis = SistemaUsuarios.deserializar();
		String hash = getHashCookies(req, res, sis);
		if (hash != null) {
			if (HttpBackendUsuarios.getNombreUsuario(req, res, sis) == null) {
				res.redirect("/login/clear");
			}
			ArrayList<String> gruposAcceso = ListaAcesoGrupos.getGruposRutas(ruta);
			if (gruposAcceso == null) {
				return 0;
			}
			if (gruposAcceso.isEmpty()) {
				return 0;
			}
			if (sis.getUsuarioDesdeHash(hash) != null) {
				if (sis.getUsuarioDesdeHash(hash).tieneGrupo(gruposAcceso)) {
					return 0;
				}
			}
			return 1;
		}
		return 2;
	}
}
