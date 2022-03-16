package http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;

import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventana.Ventana;

public class HttpCarpetaEstatica {
	private static String padre = null;

	static void get(String rutaHttp, File file) {
		padre = rutaHttp;

		String relativePath = file.getAbsoluteFile().getParentFile().getAbsolutePath();
		file = file.getAbsoluteFile();
		System.out.println(relativePath);
		System.out.println(file);
		if (file.isDirectory()) {
			getFolder(file, file.getAbsolutePath());
		} else {
			getFile(file, file.getAbsolutePath());
		}
	}

	static void getFolder(File folder, String relativePath) {
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
				getFolder(file, relativePath);
			} else {
				getFile(file, relativePath);
			}
		}

	}

	static void getFile(File file, String relativePath) {
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
		if (!leerConfiguracionWeb(file)) {
			Spark.get("/" + padre + "/" + EntryFileName.replace("\\", "/"),
					(req, res) -> devolverArchivoEstatico(req, res, absolutePath));
			String tipoFile = Ficheros.tipoFichero(file.getPath());
			
			//crear ruta para hacer streaming de audio
			if (tipoFile.equals(Ventana.idioma.get("tipo_fichero_audio"))) {
				Spark.get("/" + padre + "/clepnid_stream/" + EntryFileName.replace("\\", "/"), (req, res)->{
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
	
	static boolean leerConfiguracionWeb(File file) {
		if (file.getName().equals("clepnid.json")) {
			ClepnidJson.InstanciarWeb(file.getAbsolutePath());
			System.out.println("--------ddsfd");
			return true;
		}else {
			if (file.getName().equals("clepnid_web.json")) {
				Clepnid_WebJson.InstanciarWeb(file.getAbsolutePath());
				return true;
			}else {
				return false;
			}
		}
	}
	

	static Object devolverArchivoEstatico(Request req, Response res, String ruta) {
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
			if (Ficheros.esTipo(ruta, "CSS")) {
				res.raw().setContentType("text/css");
				Path path = Paths.get(ruta);
				try {
					return new String(Files.readAllBytes(path), Charset.defaultCharset());
				} catch (IOException e) {
					return null;
				}
			} else {
				if (tipoFile.equals(Ventana.idioma.get("tipo_fichero_documento")) || tipoFile.equals(Ventana.idioma.get("tipo_fichero_codigo"))) {
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
						res.raw().setHeader("Content-Length", String.valueOf(file.length()));
						try {
							MultipartFileVideoSender.fromPath(path).with(res.raw()).with(res.raw()).serveResource();
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
