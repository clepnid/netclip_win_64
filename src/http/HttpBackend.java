package http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.web.util.OnCommittedResponseWrapper;

import barraNavegacion.BarraNavegacion;
import http.modulosBackend.JsonListaHerramientasBackend;
import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import spark.Spark;
import usuarios.SistemaUsuarios;

public class HttpBackend {

	public static void estilarMenuConWebJson(Request request, Response response, ConfiguracionWebJson configuracionJson,
			JsonEntradaMenuModulo web, String ruta) {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			String body = response.body();
			response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), web.getJson(request, response)));
		}
	}

	public static void reemplazarBodyModuloFichero(Request request, Response response,
			ConfiguracionJson configuracionJson, String nombre, String ruta) {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			System.out.println("reeeeeeeeeeeemplazado   " + configuracionJson.getHtmlReemplazoBody());
			String body = response.body();
			response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), "/" + nombre));
		}
	}

	public static void reemplazarBodyModuloJson(Request request, Response response, ConfiguracionJson configuracionJson,
			String nombre, String ruta) {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			System.out.println("reeeeeeeeeeeemplazado   " + configuracionJson.getHtmlReemplazoBody());
			String body = response.body();
			response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), nombre));
		}
	}

	/**
	 * Devuelve los bytes del objeto poco a poco de una ruta (Descargar).
	 * 
	 * @param request
	 * @param response
	 * @param rutaFichero
	 * @return
	 */
	private static Object getFile(Request request, Response response, String rutaFichero) {
		int n = HttpBackendUsuarios.tienePermiso(request, response, rutaFichero);
		if (n == 0) {
			InputStream is = null;
			try {
				is = new FileInputStream(rutaFichero);
				response.raw().setHeader("Content-Disposition",
						"attachment; filename=\"" + new File(rutaFichero).getName() + "\"");
				int read = 0;
				byte[] bytes = new byte[1024];
				OutputStream os = response.raw().getOutputStream();

				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return response.raw();
	}

	public static Object getFile(Request request, Response response, String rutaFichero, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			return getFile(request, response, rutaFichero);
		} else {
			if (n == 1) {
				return renderIndex(request, response, "./src/html/403/index.html", "/403/index.html");
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
	}

	/**
	 * Devuelve un buffer del video para que se pueda desplazar la reproduccion en
	 * el frontend haciendo uso de {@link MultipartFileVideoSender}
	 * 
	 * @param request
	 * @param response
	 * @param rutaFichero
	 * @return
	 */
	private static Object getVideoStream(Request request, Response response, String rutaFichero) {
		int n = HttpBackendUsuarios.tienePermiso(request, response, rutaFichero);
		if (n == 0) {
			Path path = Paths.get(rutaFichero);

			File file = new File(rutaFichero);
			response.raw().setContentType("video/mp4");
			response.type("video/mp4");
			response.raw().setContentLength(getContentLength(response.raw()));
			response.raw().setHeader("Content-Length", String.valueOf(file.length()));
			try {
				MultipartFileVideoSender.fromPath(path).with(request.raw()).with(response.raw()).serveResource();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response.raw();
	}

	public static void initCorsffmpeg() {
		Spark.options("/*", (request, response) -> {
			response.header("Cross-Origin-Opener-Policy", "same-origin");
			response.header("Cross-Origin-Embedder-Policy", "require-corp");
			response.header("Cross-Origin-Resource-Policy", "same-site");
			return "OK";
		});

		Spark.before((request, response) -> {
			response.header("Cross-Origin-Opener-Policy", "same-origin");
			response.header("Cross-Origin-Embedder-Policy", "require-corp");
			response.header("Cross-Origin-Resource-Policy", "same-site");

		});
	}

	public static void enrutarDinamicamente(String ruta) {
		Spark.get(ruta + "/:name", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response,
					"./src/html" + ruta + "/" + request.params(":name"));
			if (n == 0) {
				String rutaFicheroPedido = "./src/html" + ruta + "/" + request.params(":name");
				if (new File(rutaFicheroPedido).exists()) {
					return HttpBackend.getFile(request, response, rutaFicheroPedido,
							ruta + "/" + request.params(":name"));
				}
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");
		});
	}

	public static Object getVideoStream(Request request, Response response, String rutaFichero, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			return getVideoStream(request, response, rutaFichero);
		} else {
			if (n == 1) {
				return renderIndex(request, response, "./src/html/403/index.html", "/403/index.html");
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
	}

	public static int getContentLength(HttpServletResponse response) {
		try {
			Field innerResponseField = ServletResponseWrapper.class.getDeclaredField("response");
			innerResponseField.setAccessible(true);
			innerResponseField.get(response);
			Field contentWrittenField = OnCommittedResponseWrapper.class.getDeclaredField("contentWritten");
			contentWrittenField.setAccessible(true);
			return (int) contentWrittenField.get(response);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * si es un archivo de musica debera cargarse como stream haciendo uso de
	 * {@link MultipartFileMusicSender}
	 * 
	 * @param nombreArchivo
	 * @param rutaArchivo
	 * @return
	 */
	public static void crearUrlMusicaStream(ConfiguracionJson configuracionJson, String nombreArchivo,
			String rutaArchivo) {

		Spark.get("/clepnid_stream" + configuracionJson.getRutasJson().getRutaModuloStream() + "/" + nombreArchivo,
				(req, res) -> {
					int num = HttpBackendUsuarios.tienePermiso(req, res, "/clepnid_stream"
							+ configuracionJson.getRutasJson().getRutaModuloStream() + "/" + nombreArchivo);
					if (num == 0) {
						File fichero = new File(rutaArchivo);
						res.raw().setContentType("audio/mpeg");
						res.type("audio/mpeg");
						res.raw().setHeader("Content-Length", String.valueOf(fichero.length()));
						try {
							MediaType n = new MediaType("audio", "audio");
							return MultipartFileMusicSender.writePartialContent(req.raw(), res.raw(), fichero, n);
						} catch (IOException e) {
							return res.raw();
						}
					} else {
						if (num == 1) {
							return renderIndex(req, res, "./src/html/403/index.html", "/403/index.html");
						} else {
							res.raw().sendRedirect("/login");
							return null;
						}
					}

				});
		configuracionJson.getRutasJson().anyadirItemClepnidStream(nombreArchivo, new File(rutaArchivo).getName());

	}

	public static void crearRespuestaNavbar() {
		Spark.get("/getNavBarlista.json", (req, res) -> {
			return BarraNavegacion.leerFichero()
					.getJsonNavbarGrupo(HttpBackendUsuarios.getGruposUsuario(req, res, SistemaUsuarios.deserializar()));
		});
	}

	public static void crearMenuTools() {
		Spark.get("/menuTools", (req, res) -> {
			return HttpBackend.renderIndex(req, res, JsonListaHerramientasBackend.getInstance().getHtmlMenuIndex(),
					"/menuTools");
		});
		Spark.after("/menuTools", (req, res) -> {
			JsonListaHerramientasBackend.getInstance().getPage(req, res);
		});
	}

	public static Boolean crearUrlGrupo(ConfiguracionJson configuracionJson, String nombreArchivo, String rutaArchivo) {
		if (!configuracionJson.isGrupo()) {
			return false;
		}
		String tipoFichero = Ficheros.tipoFichero(nombreArchivo);
		String extensionFichero = Ficheros.getExtensionFichero(nombreArchivo);
		if (configuracionJson.getExtensiones().contains(extensionFichero)) {
			if (tipoFichero.equals("audio")) {
				crearUrlMusicaStream(configuracionJson, nombreArchivo, rutaArchivo);
				return true;
			} else {
				configuracionJson.getRutasJson().anyadirItem(nombreArchivo, new File(rutaArchivo).getName());
				return true;
			}
		}
		return false;

	}

	/**
	 * se usa para enviar un archivo html
	 * 
	 * @param request
	 * @param responce
	 * @param ruta
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static String renderIndex(Request request, Response responce, String fichero)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, fichero);
		if (n == 0) {
			Path path = Paths.get(fichero);
			return new String(Files.readAllBytes(path), Charset.defaultCharset());
		} else {
			if (n == 1) {
				return renderIndex(request, responce, "./src/html/403/index.html", "/403/index.html");
			} else {
				responce.raw().sendRedirect("/login-layout");
				return null;
			}
		}
	}

	public static String renderIndex(Request request, Response responce, String fichero, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, ruta);
		if (n == 0) {
			return renderIndex(request, responce, fichero);
		} else {
			if (n == 1) {
				return renderIndex(request, responce, "./src/html/403/index.html", "/403/index.html");
			} else {
				responce.raw().sendRedirect("/login-layout");
				return null;
			}
		}
	}

	/**
	 * Se usa para cuando se comparte una carpeta descargar como zip
	 * 
	 * @param request
	 * @param responce
	 * @param rutaCarpeta
	 * @return
	 */
	private static Object getZip(Request request, Response responce, String rutaCarpeta) {
		File file = new File(rutaCarpeta);
		responce.raw().setContentType("application/octet-stream");
		responce.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName() + ".zip");
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new BufferedOutputStream(responce.raw().getOutputStream()))) {
			HttpBackendZip.sendFileOutput(zipOutputStream, file);
			zipOutputStream.flush();
			zipOutputStream.close();
		} catch (Exception e) {
			Spark.halt(405, "server error");
		}
		return responce.raw();
	}

	public static Object getZip(Request request, Response responce, String rutaCarpeta, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, responce, ruta);
		if (n == 0) {
			return getZip(request, responce, rutaCarpeta);
		} else {
			if (n == 1) {
				return renderIndex(request, responce, "./src/html/403/index.html", "/403/index.html");
			} else {
				try {
					responce.raw().sendRedirect("/login-layout");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}
	}

	public static Object getFavicon(Request request, Response response, String rutaFichero) {
		Path path = Paths.get(rutaFichero);

		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (Exception e1) {
			Spark.halt(405, "server error");
		}

		response.raw().setContentType("image/x-icon");
		try {
			response.raw().getOutputStream().write(data);
			response.raw().getOutputStream().flush();
			response.raw().getOutputStream().close();
		} catch (Exception e) {
			Spark.halt(405, "server error");
		}
		return response.raw();
	}

	public static Object getFavicon(Request request, Response response, String rutaFichero, String ruta)
			throws IOException, URISyntaxException {
		int n = HttpBackendUsuarios.tienePermiso(request, response, ruta);
		if (n == 0) {
			return getFavicon(request, response, rutaFichero);
		} else {
			if (n == 1) {
				return renderIndex(request, response, "./src/html/403/index.html", "/403/index.html");
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
	}
}
