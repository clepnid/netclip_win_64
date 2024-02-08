package http;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import http.modulosBackend.JsonModulosBackend;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;
import ventana.Configuracion;

public class Http {

	public class RequestLimitFilter implements Filter {
		private class ResetVariableTask extends TimerTask {
			private int comportamiento; // si comportamiento != 2 restara 1 al contador, si comportamiento es 2 pondra a
										// 0 contador
			private RequestLimitFilter l;
			private String ip;

			public ResetVariableTask(RequestLimitFilter l, String ip, int comportamiento) {
				this.comportamiento = comportamiento;
				this.ip = ip;
				this.l = l;
			}

			public void run() {
				if (comportamiento == 2) {
					l.getIpRequestCount().put(ip, 0);
				} else {
					int cont = l.getIpRequestCount().get(ip);
					if (cont > 10) {
						l.getIpRequestCount().put(ip, cont - 10);
					}
				}
				int index = l.getIpRequestTimer().indexOf(ip);
				if (index != -1) {
					l.getIpRequestTimer().remove(index);
				}
			}
		}

		private int MAX_REQUEST_SIZE;
		private int MAX_REQUEST_COUNT;
		private Map<String, Integer> ipRequestCount;
		private ArrayList<String> ipRequestTimer;
		Timer timer;

		public RequestLimitFilter() {
			// TODO Auto-generated constructor stub
			this.setIpRequestCount(new HashMap<>());
			this.setIpRequestTimer(new ArrayList<String>());
			MAX_REQUEST_COUNT = 100;// 1000 VECES
			MAX_REQUEST_SIZE = 1024 * 1024;// 1 MB
			timer = new Timer();
		}

		public RequestLimitFilter(int requestCount, int requestSize) {
			// TODO Auto-generated constructor stub
			this.setIpRequestCount(new HashMap<>());
			this.setIpRequestTimer(new ArrayList<String>());
			MAX_REQUEST_COUNT = requestCount;
			MAX_REQUEST_SIZE = requestSize;
			timer = new Timer();
		}

		@Override
		public void handle(Request request, Response response) throws Exception {
			String ipAddress = request.ip();
			if (isRequestLengthValid(request)) {
				Spark.halt(413, "Solicitud demasiado grande");
				return;
			}
			if (isRequestCountValid(ipAddress)) { // limita a un máximo de solicitudes por IP
				crearIpRequestCountTimer(ipAddress, 2, 30 * 60 * 1000); // crea un timer con el que se pondra a 0 el
																		// contador de dicha ip en media hora
				Spark.halt(429, "Demasiadas peticiones de esta IP");
				return;
			}

		}

		public int getIpRequestCount(String ipAddress) {
			if (!getIpRequestCount().containsKey(ipAddress)) {
				getIpRequestCount().put(ipAddress, 0);
			}
			int count = getIpRequestCount().get(ipAddress);
			crearIpRequestCountTimer(ipAddress, 1, 100); // restar 10 al contador
			count++;
			getIpRequestCount().put(ipAddress, count);
			System.out.println("CONTADOR: " + count);
			return count;
		}

		public void crearIpRequestCountTimer(String ipAddress, int modo, int milisegundos) {
			if (!getIpRequestTimer().contains(ipAddress)) {
				// Si no está, agregarlo al final de la lista
				getIpRequestTimer().add(ipAddress);
				timer.schedule(new ResetVariableTask(this, ipAddress, modo), milisegundos);
			}
		}

		public boolean isRequestLengthValid(Request request) {
			return request.contentLength() > MAX_REQUEST_SIZE;
		}

		public boolean isRequestCountValid(String ipAddress) {
			return getIpRequestCount(ipAddress) >= MAX_REQUEST_COUNT;
		}

		public synchronized Map<String, Integer> getIpRequestCount() {
			return ipRequestCount;
		}

		public void setIpRequestCount(Map<String, Integer> ipRequestCount) {
			this.ipRequestCount = ipRequestCount;
		}

		public synchronized ArrayList<String> getIpRequestTimer() {
			return ipRequestTimer;
		}

		public synchronized void setIpRequestTimer(ArrayList<String> ipRequestTimer) {
			this.ipRequestTimer = ipRequestTimer;
		}

	}

	private static Http INSTANCE;
	public static int MAX_READ_SIZE = 1024;
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
			+ "<p><a href=\"menu\">Entrar a la aplicación</a></p>\r\n" + "</body>\r\n" + "</html>";
	private final static String TEXTODEFECTO = "no hay nada que copiar";
	private final static String TEXTOHTMLDEFECTO = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n"
			+ "<meta charset=&nbsp;utf-8&nbsp;>\n"
			+ "<meta name=&nbsp;viewport&nbsp; content=&nbsp;width=device-width, initial-scale=1&nbsp;>\n"
			+ "<title>Clepnid</title>\n" + "</head>\n" + "<body>\n" + "</body>\n" + "</html>";
	private final static int PUERTOHTTP = 3000;
	private String texto = null, rutaVideo = null;
	private static String textoHTML = null;
	private JsonModulosBackend jsonModulosBackend;

	public JsonModulosBackend getJsonModulosBackend() {
		return jsonModulosBackend;
	}

	public static Http getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Http();
		}

		return INSTANCE;
	}

	private Http() {
		texto = TEXTODEFECTO;
		rutaVideo = RUTAVIDEODEFECTO;
		textoHTML = TEXTOHTMLDEFECTO;
		Spark.port(PUERTOHTTP);

		Spark.get("/pagina.html", (req, res) -> renderHtml(req, res));
		Spark.get("/", (req, res) -> {
			return TEXTOPRIMERARUTA;
		});

		this.jsonModulosBackend = new JsonModulosBackend();

		JsonModulosFicheros.iniciar();
		getCarpetaEstatica(this, "", "./src/html");
		Spark.after("/index.html", (request, response) -> {
			if (!texto.equals(TEXTODEFECTO)) {
				String body = response.body();
				response.body(body.replace(TEXTODEFECTO, texto));
			}
		});
		for (ConfiguracionJson config : JsonModulosFicheros.config) {
			System.out.println(config);
		}
		Configuracion config = null;
		try {
			config = Configuracion.deserializar();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// si la ruta del fichero de inicializacion no es correcta no se podrÃ¡ realizar
		// esta acciÃ³n
		// si no se ha cargado la configuracion del menu no se podrÃ¡ realizar esta
		// acciÃ³n
		if (config.inicializarRutas != null && config.rutaGuardadoHttp != null && !config.rutaGuardadoHttp.equals("")
				&& JsonModulosMenuWeb.config != null) {
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
		if (JsonModulosMenuWeb.config != null) {
			crearUrlIndice(JsonModulosMenuWeb.config);
		}

		Spark.unmap("/modulo_subir_ficheros/index.html");

		Spark.get("/modulo_subir_ficheros/index.html", (req, res) -> HttpBackend.renderIndex(req, res,
				"./src/html/modulo_subir_ficheros/index.html", "/modulo_subir_ficheros/index.html"));

		HttpBackendUsuarios.crearControlUsuarios();

		Spark.notFound((req, res) -> HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html"));
		Spark.internalServerError(
				(req, res) -> HttpBackend.renderIndex(req, res, "./src/html/500/index.html", "/500/index.html"));

		// HttpBackend.initCorsffmpeg();
		HttpBackend.enrutarDinamicamente("/servidorRtmp/ffmpegMonitorRecord/files");

		try {
			HttpBackendControladorTeclasRaton.getInstance();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// limitar antes las peticiones de Spark en la clase implementada en este mismo
		Spark.before(new RequestLimitFilter());

		HttpBackend.crearRespuestaNavbar();

		// Crear pagina de herramientas /menuTools en el menu principal
		HttpBackend.crearMenuTools();

		// Crear torrent paginas
		HttpTorrent httpTorrent = HttpTorrent.getInstance();
		httpTorrent.inicializarRutaDownloadTorrent();
		httpTorrent.inicializarRutaGetJsonTorrent();

		Spark.after("/login-layout", (req, res) -> redirectLoginLayout(res));
		

	}
	
	private static boolean redirectLoginLayout(Response res) {
		System.out.println("redirect");
		res.redirect("/login-layout/index.html");
		return true;
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
		JsonModulosMenuWeb.config.vaciarWeb();
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
		JsonModulosMenuWeb.config.eliminarRuta(ruta);

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
		// Spark.get("/hello", (req, res) -> HttpBackend.getFile(req,
		// res,"C:\\\\Users\\\\pavon\\\\Desktop\\\\UNI\\\\MATIII_Prueba1_Antonio_Jesus_Pavon_Correa_1.jpeg"));
		// Spark.get("/index.html", (req, res) -> HttpBackend.renderIndex(req, res));
		Spark.get("/script.js", (req, res) -> renderJavaScript(req, res));
		Spark.get("/", (req, res) -> {
			res.raw().sendRedirect("/yourpage");
			return 1;
		});
	}

	public void modificarHTML(String textoHtml) {
		textoHTML = textoHtml;
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

	private static void getCarpetaEstatica(Http http, String rutaHttp, String rutaFichero) {
		RecorrerCarpetaArchivosEstaticosSpark.get(http, rutaHttp, new File(rutaFichero).getAbsoluteFile());
	}

	public void modificarUrlTexto(String texto) {
		this.texto = texto;
	}

	public synchronized void crearUrlCarpeta(String nombreCarpeta, String rutaCarpeta) {
		Spark.get("/" + nombreCarpeta, (req, res) -> HttpBackend.getZip(req, res, rutaCarpeta, "/" + nombreCarpeta));
		anyadirUrlParcial(nombreCarpeta, rutaCarpeta, Tipo.Carpeta);
	}

	public synchronized void crearUrlArchivo(String nombreArchivo, String rutaArchivo) {
		Spark.get("/" + nombreArchivo, (req, res) -> HttpBackend.getFile(req, res, rutaArchivo, "/" + nombreArchivo));
		anyadirUrlParcial(nombreArchivo, rutaArchivo, Tipo.Archivo);
	}

	public synchronized void crearUrlVideo(String nombreArchivo, String rutaArchivo) {
		Spark.get("/" + nombreArchivo,
				(req, res) -> HttpBackend.getVideoStream(req, res, rutaArchivo, "/" + nombreArchivo));
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

		// llamar a metodo clase para crear una entrada en los modulos de grupo
		// tendra que introducir como parametros el nombreArchivo y rutaArchivo
		// este metodo iterara en lista de modulos si son de grupos y si el archivo
		// tiene extension de los de modulo de grupo creará una cacharro
		// retornará true si tiene modulo de grupo
		Boolean grupo = HttpBackend.crearUrlGrupo(configuracionJson, nombreArchivo, rutaArchivo);
		String ruta = configuracionJson.getRutaHttp() + "/" + nombreArchivo;
		Spark.get(ruta, (req, res) -> HttpBackend.renderIndex(req, res, configuracionJson.getHtml(), ruta));

		if (grupo) {
			System.out.println("holaaaaaaa   "
					+ configuracionJson.getRutasJson().getJson(nombreArchivo, new File(ruta).getName()));
			// esto sirve para introducirle en la pagina del modulo una lista json que
			// empieza con el fichero
			Spark.after(ruta,
					(request, response) -> HttpBackend.reemplazarBodyModuloJson(request, response, configuracionJson,
							configuracionJson.getRutasJson().getJson(nombreArchivo, new File(ruta).getName()), ruta));
		} else {
			// reemplaza en el modulo de la pagina el body para meterle la ruta del fichero
			Spark.after(ruta, (request, response) -> HttpBackend.reemplazarBodyModuloFichero(request, response,
					configuracionJson, nombreArchivoAux, ruta));

		}

	}

	public synchronized void crearUrlIndice(ConfiguracionWebJson configuracionJson) {
		synchronized (Spark.class) {
			if (!Spark.unmap(configuracionJson.getRutaHttp())) {
				return;
			}
			Spark.get(configuracionJson.getRutaHttp(), (req, res) -> HttpBackend.renderIndex(req, res,
					configuracionJson.getHtml(), configuracionJson.getRutaHttp()));
			for (JsonEntradaMenuModulo web : configuracionJson.getWebs()) {
				Spark.get(web.getGoTo(),
						(req, res) -> HttpBackend.renderIndex(req, res, configuracionJson.getHtml(), web.getGoTo()));
				Spark.after(web.getGoTo(), (request, response) -> HttpBackend.estilarMenuConWebJson(request, response,
						configuracionJson, web, web.getGoTo()));
			}
			Spark.after(configuracionJson.getRutaHttp(), (request, response) -> {
				String body = response.body();
				String json = configuracionJson.getJson(request, response);
				try {
					body = body.replace(configuracionJson.getHtmlReemplazoBody(), json);
					response.body(body);
				} catch (Exception e) {
					response.redirect("/login/clear");
				}
			});
		}
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