package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.MediaType;

import portapapeles.Ficheros;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventana.Configuracion;
import ventana.Ventana;

public class Http {
	private static int MAX_READ_SIZE = 1024;
	private ArrayList<String> urlsParciales = new ArrayList<String>();
	private ArrayList<String> urlsSistema = new ArrayList<String>();

	public enum Tipo {
		Carpeta, Archivo, Video
	}

	private ArrayList<Tipo> tipos = new ArrayList<Tipo>();
	private final static String RUTAVIDEODEFECTO = "no hay video";
	private final static String TEXTOPRIMERARUTA = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n"
			+ "	<meta charset=\"utf-8\">\r\n"
			+ "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
			+ "	<title>Clepnid</title>\r\n" + "</head>\r\n" + "<body>\r\n" + "<h1>CLEPNID</h1>\r\n" + "\r\n"
			+ "<p><a href=\"menu\">Entrar a la aplicaci�n</a></p>\r\n" + "</body>\r\n" + "</html>";
	private final static String TEXTODEFECTO = "no hay nada que copiar";
	private final static String TEXTOHTMLDEFECTO = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n"
			+ "<meta charset=&nbsp;utf-8&nbsp;>\n"
			+ "<meta name=&nbsp;viewport&nbsp; content=&nbsp;width=device-width, initial-scale=1&nbsp;>\n"
			+ "<title>Clepnid</title>\n" + "</head>\n" + "<body>\n" + "</body>\n" + "</html>";
	private final static int PUERTOHTTP = 3000;
	private String texto = null, rutaVideo = null;
	private static String textoHTML = null;

	public Http() {
		texto = TEXTODEFECTO;
		rutaVideo = RUTAVIDEODEFECTO;
		textoHTML = TEXTOHTMLDEFECTO;
		Spark.port(PUERTOHTTP);

		Spark.get("/favicon.ico", (req, res) -> getFavicon(req, res, ".src/imagenes/clipboard.ico"));
		Spark.get("/pagina.html", (req, res) -> renderHtml(req, res));
		Spark.get("/", (req, res) -> {
			return TEXTOPRIMERARUTA;
		});

		ClepnidJson.iniciar();
		getCarpetaEstatica("", "./src/html");
		Spark.after("/index.html", (request, response) -> {
			if (!texto.equals(TEXTODEFECTO)) {
				String body = response.body();
				response.body(body.replace(TEXTODEFECTO, texto));
			}
		});
		for (ConfiguracionJson config : ClepnidJson.config) {
			System.out.println(config);
		}
		Configuracion config = null;
		try {
			config = Configuracion.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// si la ruta del fichero de inicializacion no es correcta no se podrá realizar
		// esta acción
		// si no se ha cargado la configuracion del menu no se podrá realizar esta
		// acción
		if (config.inicializarRutas != null && config.rutaGuardadoHttp != null && !config.rutaGuardadoHttp.equals("")
				&& Clepnid_WebJson.config != null) {
			try {
				if (config.inicializarRutas) {
					GuardadoRutas guardado = GuardadoRutas.deserializar();
					guardado.cargar(this);
				}
			} catch (ClassNotFoundException e) {
				System.out.print("");
			} catch (IOException e) {
				System.out.print("");
			}
		}
		if (Clepnid_WebJson.config != null) {
			crearUrlIndice(Clepnid_WebJson.config);
		}

		Spark.unmap("/modulo_subir_ficheros/index.html");

		Spark.get("/modulo_subir_ficheros/index.html",
				(req, res) -> renderIndex(req, res, "./src/html/modulo_subir_ficheros/index.html"));

	}

	/**
	 * Metodo principal para controlar el envio segun el tipo de fichero.
	 * 
	 * @param zipOpStream {@link ZipOutputStream} buffer para enviar fichero.
	 * @param outFile     {@link File} fichero a controlar.
	 */

	public static void sendFileOutput(ZipOutputStream zipOpStream, File outFile) throws Exception {
		String relativePath = outFile.getAbsoluteFile().getParentFile().getAbsolutePath();
		outFile = outFile.getAbsoluteFile();
		if (outFile.isDirectory()) {
			sendFolder(zipOpStream, outFile, relativePath);
		} else {
			sendFile(zipOpStream, outFile, relativePath);
		}
	}

	public void modificarHTML(String textoHtml) {
		textoHTML = textoHtml;
	}

	/**
	 * Envia los ficheros contenidos en la carpeta.
	 * 
	 * @param zipOpStream  {@link ZipOutputStream} buffer para enviar fichero.
	 * @param folder       {@link File} carpeta.
	 * @param relativePath {@link File} ruta relativa.
	 */

	public static void sendFolder(ZipOutputStream zipOpStream, File folder, String relativePath) throws Exception {
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				sendFolder(zipOpStream, file, relativePath);
			} else {
				sendFile(zipOpStream, file, relativePath);
			}
		}
	}

	/**
	 * Envia fichero
	 * 
	 * @param zipOpStream  {@link ZipOutputStream} buffer para enviar fichero.
	 * @param file         {@link File} carpeta.
	 * @param relativePath {@link File} ruta relativa.
	 */

	public static void sendFile(ZipOutputStream zipOpStream, File file, String relativePath) throws Exception {
		String absolutePath = file.getAbsolutePath();
		String zipEntryFileName = absolutePath;
		if (absolutePath.startsWith(relativePath)) {
			zipEntryFileName = absolutePath.substring(relativePath.length());
			if (zipEntryFileName.startsWith(File.separator)) {
				zipEntryFileName = zipEntryFileName.substring(1);
			}
		} else {
			throw new Exception("Invalid Absolute Path");
		}

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte[] fileByte = new byte[MAX_READ_SIZE];
		int readBytes = 0;
		CRC32 crc = new CRC32();
		while (0 != (readBytes = bis.read(fileByte))) {
			if (-1 == readBytes) {
				break;
			}
			crc.update(fileByte, 0, readBytes);
		}
		bis.close();
		ZipEntry zipEntry = new ZipEntry(zipEntryFileName);
		zipEntry.setMethod(ZipEntry.STORED);
		zipEntry.setCompressedSize(file.length());
		zipEntry.setSize(file.length());
		zipEntry.setCrc(crc.getValue());
		zipOpStream.putNextEntry(zipEntry);
		bis = new BufferedInputStream(new FileInputStream(file));

		while (0 != (readBytes = bis.read(fileByte))) {
			if (-1 == readBytes) {
				break;
			}

			zipOpStream.write(fileByte, 0, readBytes);
		}
		bis.close();

	}

	public static int getPuertoHTTP() {
		return PUERTOHTTP;
	}

	public void dispose() {
		new Runnable() {
			public void run() {
				textoDefecto();
				htmlDefecto();
			}
		}.run();
	}

	public void vaciarUrls() {
		for (String urls : getUrlsParciales()) {
			Spark.unmap("/" + urls);
		}
		Clepnid_WebJson.config.vaciarWeb();
		setUrlsParciales(new ArrayList<String>());
		setUrlsSistema(new ArrayList<String>());
		setTipos(new ArrayList<Tipo>());
	}

	public void eliminarUrl(String ruta) {
		System.out.println(ruta);
		Spark.unmap("/" + ruta);
		ArrayList<String> urlsSistemaAux = getUrlsSistema();
		urlsSistemaAux.remove(urlsParciales.indexOf(ruta));
		setUrlsSistema(urlsSistemaAux);

		ArrayList<Tipo> tiposAux = getTipos();
		tiposAux.remove(urlsParciales.indexOf(ruta));
		setTipos(tiposAux);

		urlsParciales.remove(ruta);
		Clepnid_WebJson.config.eliminarRuta(ruta);

	}

	public void close() {
		new Runnable() {
			public void run() {
				Spark.stop();
			}
		}.run();
	}

	public static void main(String[] args) {
		Spark.port(PUERTOHTTP);
		Spark.externalStaticFileLocation("/video/webfonts");
		Spark.get("/hello", (req, res) -> getFile(req, res,
				"C:\\\\Users\\\\pavon\\\\Desktop\\\\UNI\\\\MATIII_Prueba1_Antonio_Jesus_Pavon_Correa_1.jpeg"));
		Spark.get("/index.html", (req, res) -> renderIndex(req, res));
		Spark.get("/script.js", (req, res) -> renderJavaScript(req, res));
		Spark.get("/", (req, res) -> {
			res.raw().sendRedirect("/yourpage");
			return 1;
		});
	}

	/* Convierte un string a algo que se puede insertar en una url */
	public static String encodeURIcomponent(String s) {
		StringBuilder o = new StringBuilder();
		for (char ch : s.toCharArray()) {
			if (isUnsafe(ch)) {
				o.append('%');
				o.append(toHex(ch / 16));
				o.append(toHex(ch % 16));
			} else
				o.append(ch);
		}
		return o.toString();
	}

	private static char toHex(int ch) {
		return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	}

	private static boolean isUnsafe(char ch) {
		if (ch > 128 || ch < 0)
			return true;
		return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
	}

	private static void getCarpetaEstatica(String rutaHttp, String rutaFichero) {
		HttpCarpetaEstatica.get(rutaHttp, new File(rutaFichero).getAbsoluteFile());
	}

	public void modificarUrlTexto(String texto) {
		this.texto = texto;
	}

	public void crearUrlCarpeta(String nombreCarpeta, String rutaCarpeta) {
		Spark.get("/" + nombreCarpeta, (req, res) -> getZip(req, res, rutaCarpeta));
		anyadirUrlParcial(nombreCarpeta, rutaCarpeta, Tipo.Carpeta);
	}

	public void crearUrlArchivo(String nombreArchivo, String rutaArchivo) {
		Spark.get("/" + nombreArchivo, (req, res) -> getFile(req, res, rutaArchivo));
		anyadirUrlParcial(nombreArchivo, rutaArchivo, Tipo.Archivo);
	}

	// si es un archivo de musica debera cargarse como stream
	public Boolean crearUrlMusicaStream(String nombreArchivo, String rutaArchivo) {
		if (Ficheros.tipoFichero(nombreArchivo).equals(Ventana.idioma.get("tipo_fichero_audio"))
				&& MusicPlayerComponent.getRutaModulo() != null) {
			Spark.get("/clepnid_stream" + MusicPlayerComponent.getRutaModulo() + "/" + nombreArchivo, (req, res) -> {
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
			});
			MusicPlayerJson.anyadirMusica(nombreArchivo);
			return true;
		}
		return false;
	}

	public void crearUrlVideo(String nombreArchivo, String rutaArchivo) {
		Spark.get("/" + nombreArchivo, (req, res) -> getVideoStream(req, res, rutaArchivo));
		anyadirUrlParcial(nombreArchivo, rutaArchivo, Tipo.Video);
	}

	public boolean estaEnUrl(String nombre) {
		boolean encontrado = false;
		for (String string : urlsParciales) {
			if (string.equals(nombre)) {
				encontrado = true;
			}
		}
		return encontrado;
	}

	private void anyadirUrlParcial(String nombre, String ruta, Tipo tipo) {
		if (!estaEnUrl(nombre)) {
			getUrlsParciales().add(nombre);
			getUrlsSistema().add(ruta);
			getTipos().add(tipo);
		}
	}

	public void crearUrlModulo(ConfiguracionJson configuracionJson, String nombreArchivo, String rutaArchivo) {
		String nombreArchivoAux = nombreArchivo;
		Boolean musica = crearUrlMusicaStream(nombreArchivo, rutaArchivo);
		String ruta = configuracionJson.getRutaHttp() + "/" + nombreArchivo;
		Spark.get(ruta, (req, res) -> renderIndex(req, res, configuracionJson.getHtml()));

		if (musica) {
			Spark.after(ruta, (request, response) -> {
				String body = response.body();
				response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), MusicPlayerJson.getJson(nombreArchivo)));
			});
		} else {
			Spark.after(ruta, (request, response) -> {
				String body = response.body();
				response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), "/" + nombreArchivoAux));
			});
		}

	}

	public void crearUrlIndice(ConfiguracionWebJson configuracionJson) {
		Spark.unmap(configuracionJson.getRutaHttp());
		Spark.get(configuracionJson.getRutaHttp(), (req, res) -> renderIndex(req, res, configuracionJson.getHtml()));
		for (WebJson web : configuracionJson.getWebs()) {
			Spark.get(web.getGoTo(), (req, res) -> renderIndex(req, res, configuracionJson.getHtml()));
			Spark.after(web.getGoTo(), (request, response) -> {
				String body = response.body();
				response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), web.getJson()));
			});
		}
		Spark.after(configuracionJson.getRutaHttp(), (request, response) -> {
			String body = response.body();
			String json = configuracionJson.getJson();
			response.body(body.replace(configuracionJson.getHtmlReemplazoBody(), json));
		});
	}

	public Object getVideoStream(Request request, Response response, String rutaFichero) {
		Path path = Paths.get(rutaFichero);

		File file = new File(rutaFichero);
		response.raw().setContentType("video/mp4");
		response.type("video/mp4");
		response.raw().setHeader("Content-Length", String.valueOf(file.length()));
		try {
			MultipartFileVideoSender.fromPath(path).with(request.raw()).with(response.raw()).serveResource();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.raw();
	}

	public void textoDefecto() {
		texto = TEXTODEFECTO;
	}

	public void htmlDefecto() {
		textoHTML = TEXTOHTMLDEFECTO;
	}

	public void textoVideoDefecto() {
		rutaVideo = RUTAVIDEODEFECTO;
	}

	public String getRutaVideo() {
		return this.rutaVideo;

	}

	private static String renderIndex(Request request, Response responce) throws IOException, URISyntaxException {
		Path path = Paths.get("./src/html/index.html");
		return new String(Files.readAllBytes(path), Charset.defaultCharset());
	}

	public static String renderIndex(Request request, Response responce, String ruta)
			throws IOException, URISyntaxException {
		Path path = Paths.get(ruta);
		return new String(Files.readAllBytes(path), Charset.defaultCharset());
	}

	private static String renderHtml(Request request, Response responce) throws IOException, URISyntaxException {
		return textoHTML;
	}

	private static String renderJavaScript(Request request, Response responce) throws IOException, URISyntaxException {
		Path path = Paths.get("./src/html/script.js");

		return new String(Files.readAllBytes(path), Charset.defaultCharset());
	}

	@SuppressWarnings("unused")
	private static String renderStyle(Request request, Response responce) throws IOException, URISyntaxException {
		Path path = Paths.get("./src/html/style.css");

		return new String(Files.readAllBytes(path), Charset.defaultCharset());
	}

	private static Object getZip(Request request, Response responce, String rutaCarpeta) {
		File file = new File(rutaCarpeta);
		responce.raw().setContentType("application/octet-stream");
		responce.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName() + ".zip");
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new BufferedOutputStream(responce.raw().getOutputStream()))) {
			sendFileOutput(zipOutputStream, file);
			zipOutputStream.flush();
			zipOutputStream.close();
		} catch (Exception e) {
			Spark.halt(405, "server error");
		}
		return responce.raw();
	}

	private static Object getFile(Request request, Response response, String rutaFichero) {
		Path path = Paths.get(rutaFichero);
		File file = new File(rutaFichero);

		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (Exception e1) {
			Spark.halt(405, "server error");
		}

		response.raw().setContentType("application/octet-stream");
		response.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName());
		try {
			response.raw().getOutputStream().write(data);
			response.raw().getOutputStream().flush();
			response.raw().getOutputStream().close();
		} catch (Exception e) {
			Spark.halt(405, "server error");
		}
		return response.raw();
	}

	private static Object getFavicon(Request request, Response response, String rutaFichero) {
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

	public ArrayList<String> getUrlsParciales() {
		return urlsParciales;
	}

	public void setUrlsParciales(ArrayList<String> urlsParciales) {
		this.urlsParciales = urlsParciales;
	}

	public ArrayList<String> getUrlsSistema() {
		return urlsSistema;
	}

	public void setUrlsSistema(ArrayList<String> urlsSistema) {
		this.urlsSistema = urlsSistema;
	}

	public ArrayList<Tipo> getTipos() {
		return tipos;
	}

	public void setTipos(ArrayList<Tipo> tipos) {
		this.tipos = tipos;
	}
}