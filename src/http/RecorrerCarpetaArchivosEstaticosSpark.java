package http;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;

import http.modulosBackend.JsonListaHerramientasBackend;
import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventana.Ventana;

public class RecorrerCarpetaArchivosEstaticosSpark {
	private static String padre = null;

	static void get(Http http, String rutaHttp, File file) {
		padre = rutaHttp;

		String relativePath = file.getAbsoluteFile().getParentFile().getAbsolutePath();
		file = file.getAbsoluteFile();
		System.out.println(relativePath);
		System.out.println(file);
		if (file.isDirectory()) {
			getFolder(http, file, file.getAbsolutePath());
		} else {
			getFile(http, file, file.getAbsolutePath());
		}
	}

	static void getFolder(Http http, File folder, String relativePath) {
		String absolutePath = folder.getAbsolutePath();
		String EntryFileName = absolutePath;
		if (absolutePath.startsWith(relativePath)) {
			EntryFileName = absolutePath.substring(relativePath.length());
			if (EntryFileName.startsWith(File.separator)) {
				EntryFileName = EntryFileName.substring(1);
			}
		} else {
			System.out.println("fallo");
		}
		System.out.println("/" + padre + "/" + EntryFileName.replace("\\", "/"));
		Spark.get("/" + padre + "/" + EntryFileName.replace("\\", "/"), (req, res) -> {
			return "";
		});
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				getFolder(http, file, relativePath);
			} else {
				getFile(http, file, relativePath);
			}
		}

	}

	static void getFile(Http http, File file, String relativePath) {
		String absolutePath = file.getAbsolutePath();
		String EntryFileName = absolutePath;
		if (absolutePath.startsWith(relativePath)) {
			EntryFileName = absolutePath.substring(relativePath.length());
			if (EntryFileName.startsWith(File.separator)) {
				EntryFileName = EntryFileName.substring(1);
			}
		} else {
			System.out.println("fallo");
		}
		System.out.println("/" + padre + "/" + EntryFileName.replace("\\", "/"));
		if (!leerConfiguracionWeb(http, file)) {
			String rutaAux1 = "/" + padre + "/" + EntryFileName.replace("\\", "/");
			Spark.get(rutaAux1,
					(req, res) -> devolverArchivoEstatico(req, res, absolutePath, rutaAux1));
			String tipoFile = Ficheros.tipoFichero(file.getPath());

			// crear ruta para hacer streaming de audio
			if (tipoFile.equals(Ventana.idioma.get("tipo_fichero_audio"))) {
				//<?> se crea una ruta en el padre, se podria usar este padre para crear los modulos de carpetas
				//en verdad este padre es relativo a la carpeta html, asi que nosotros para el modulo de carpeta tendriamos
				//que crear nuestro padre que sera la carpeta compartida de musica
				String rutaAux2 = "/" + padre + "/clepnid_stream/" + EntryFileName.replace("\\", "/");
				Spark.get(rutaAux2, (req, res) -> {
					int n1 = HttpBackendUsuarios.tienePermiso(req, res, rutaAux2);
					if (n1 != 0) {
						if (n1 == 1) {
							return HttpBackend.renderIndex(req, res, "./src/html/403/index.html", "/403/index.html");
						} else {
							res.raw().sendRedirect("/login-layout");
							return null;
						}
					}
					File fichero = new File(absolutePath);
					res.raw().setContentType("audio/mpeg");
					res.type("audio/mpeg");
					res.raw().setHeader("Content-Length", String.valueOf(fichero.length()));
					try {
						MediaType n = new MediaType("audio", "audio");
						return MultipartFileMusicSender.writePartialContent(req.raw(), res.raw(), fichero, n);
					} catch (IOException e) {
						return res.raw();
					}
				});
			}
		}
	}

	/**
	 * Retorna true si es un archivo json de configuracion
	 * 
	 * @param ventana
	 * @param file
	 * @return
	 */
	static boolean leerConfiguracionWeb(Http http, File file) {
		String nombreFicheroConf = file.getName();
		if (nombreFicheroConf.equals("clepnid.json")) {
			JsonModulosFicheros.InstanciarWeb(file.getAbsolutePath());
			return true;
		}
		if (nombreFicheroConf.equals("clepnid_web.json")) {
			JsonModulosMenuWeb.InstanciarWeb(file.getAbsolutePath());
			return true;
		}
		if (nombreFicheroConf.equals("clepnid_backend.json")) {
			http.getJsonModulosBackend().InstanciarWeb(file.getAbsolutePath());
			return true;
		}
		if (nombreFicheroConf.equals("clepnid_backend_icon.json")) {
			JsonListaHerramientasBackend.getInstance().InstanciarWeb(file.getAbsolutePath());
			return true;
		}
		return false;
	}

	static Object devolverArchivoEstatico(Request req, Response res, String ruta, String url) throws IOException, URISyntaxException {
		int n1 = HttpBackendUsuarios.tienePermiso(req, res, url);
		if (n1 != 0 && (!url.contains("login-layout"))) {
			if (n1 == 1) {
				return HttpBackend.renderIndex(req, res, "./src/html/403/index.html", "/403/index.html");
			} else {
				res.raw().sendRedirect("/login-layout");
				return null;
			}
		}
		File file = new File(ruta);
		String tipoFile = Ficheros.tipoFichero(file.getPath());
		if (Ficheros.esTipo(ruta, "JS")) {
			res.raw().setContentType("text/javascript");
			Path path = Paths.get(ruta);
			try {
				return new String(Files.readAllBytes(path), Charset.defaultCharset());
			} catch (IOException e) {
				return null;
			}
		} else {
			if (Ficheros.esTipo(ruta, "CSS") || Ficheros.esTipo(ruta, "SCSS")) {
				res.raw().setContentType("text/css");
				Path path = Paths.get(ruta);
				try {
					return new String(Files.readAllBytes(path), Charset.defaultCharset());
				} catch (IOException e) {
					return null;
				}
			} else {
				if (tipoFile.equals(Ventana.idioma.get("tipo_fichero_documento"))
						|| tipoFile.equals(Ventana.idioma.get("tipo_fichero_codigo"))) {
					Path path = Paths.get(ruta);
					try {
						return new String(Files.readAllBytes(path), Charset.defaultCharset());
					} catch (IOException e) {
						return null;
					}
				} else {
					if (tipoFile.equals(Ventana.idioma.get("tipo_fichero_tipo_fichero_video"))) {
						Path path = Paths.get(ruta);

						file = new File(ruta);
						res.raw().setContentType("video/mp4");
						res.type("video/mp4");
						res.raw().setContentLength(HttpBackend.getContentLength(res.raw()));
						res.raw().setHeader("Content-Length", String.valueOf(file.length()));
						try {
							MultipartFileVideoSender.fromPath(path).with(req.raw()).with(res.raw()).serveResource();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return res.raw();
					} else {
						Path path = Paths.get(ruta);
						file = new File(ruta);

						byte[] data = null;
						try {
							data = Files.readAllBytes(path);
						} catch (Exception e1) {
							Spark.halt(405, "server error");
						}
						res.raw().setContentType("application/octet-stream");
						res.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName());
						try {
							res.raw().getOutputStream().write(data);
							res.raw().getOutputStream().flush();
							res.raw().getOutputStream().close();
						} catch (Exception e) {
							Spark.halt(405, "server error");
						}
						return res.raw();

					}
				}
			}
		}
	}
}
