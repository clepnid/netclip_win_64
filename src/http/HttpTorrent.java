package http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import org.libtorrent4j.FileStorage;

import portapapeles.Ficheros;
import spark.Spark;
import torrentLibreria.ficherosTorrent.ContenidoTorrent;
import torrentLibreria.ficherosTorrent.FicheroTorrent;
import torrentLibreria.torrentstream.StreamStatus;
import torrentLibreria.torrentstream.Torrent;
import torrentLibreria.torrentstream.TorrentInfoClone;
import torrentLibreria.torrentstream.TorrentOptions;
import torrentLibreria.torrentstream.TorrentStream;
import torrentLibreria.torrentstreamserver.TorrentServerListener;
import torrentLibreria.torrentstreamserver.TorrentStreamServer;
import ventana.Ventana;
import ventanaGestionarModulo.SistemaModulos;

public class HttpTorrent {
	private ArrayList<TorrentStream> listaTorrents;
	private static HttpTorrent INSTANCE;
	private TorrentServerListener s;
	private TorrentOptions torrentOptions;
	private TorrentStreamServer torrentStreamServer;
	public static String rutaPadre = "C:/Users/pavon/eclipse-workspace/TorrentStream/descargas";
	public static int tiempoRespuestaSegundos = 10;
    private static ExecutorService executor;
	
    public void close() {
    	executor.shutdown();
    	for (TorrentStream torrentStream : listaTorrents) {
			torrentStream.stopStream();
		}
    	borrarCarpeta(new File(rutaPadre));
    }
    
    private HttpTorrent() {     
    	listaTorrents = new ArrayList<TorrentStream>();
        executor = Executors.newSingleThreadExecutor();
    	s = new TorrentServerListener() {
			private int progresoActual = 0;

			@Override
			public void onStreamStopped() {
				System.out.println("parado");

			}

			@Override
			public void onStreamStarted(Torrent torrent) {
				System.out.println("empezando");

			}

			@Override
			public void onStreamReady(Torrent torrent) {
				System.out.println("ready");

			}

			@Override
			public void onStreamProgress(Torrent torrent, StreamStatus status) {
				if (status.bufferProgress <= 100 && progresoActual < 100 && progresoActual != status.bufferProgress) {
					progresoActual = status.bufferProgress;
					System.out.println("Progress: " + status.bufferProgress);
				}

			}

			@Override
			public void onStreamPrepared(Torrent torrent) {
				System.out.println("preparado");

			}

			@Override
			public void onStreamError(Torrent torrent, Exception e) {
				System.out.println("error");

			}

			@Override
			public void onServerReady(String url) {
				System.out.println("url: " + url);

			}
		};
		torrentOptions = new TorrentOptions.Builder().removeFilesAfterStop(true).build();
		torrentStreamServer = TorrentStreamServer.getInstance();
		String ipAddress = "127.0.0.1";
		torrentStreamServer.setTorrentOptions(torrentOptions);
		torrentStreamServer.setServerHost(ipAddress);
		torrentStreamServer.setServerPort(8080);
		torrentStreamServer.addListener(s);
		FicheroTorrent.getInstance().setRutaPadre(rutaPadre);
		LogManager.getLogManager().reset();
		SistemaModulos.getInstance().inicializarModulo("torrent");
    }
    
    public static HttpTorrent getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HttpTorrent();
        }
        
        return INSTANCE;
    }
    
    public void descargarTorrent(String rutaTorrent, int index) {
    	torrentStreamServer.startTorrentStream(index);
    	torrentStreamServer.startStream(rutaTorrent);
    }
    
    public String getInformacionTorrent(String torrent) {
    	return getContenidoJson(getDiccionarioTorrent(torrentOptions, torrent, listaTorrents));
    }
    
    public String getContenidoTorrent(String torrent, int index) {
    	return getContenidoJson(getDiccionarioTorrent(torrentOptions, torrent, listaTorrents));
    }
    
    private ContenidoTorrent getContenidoTorrent(HashMap<String, ArrayList<ContenidoTorrent>> diccionario, int index) {
    	if (diccionario == null) {
			return null;
		}
		for (Map.Entry<String, ArrayList<ContenidoTorrent>> entry : diccionario.entrySet()) {
			ArrayList<ContenidoTorrent> valores = entry.getValue();
			if (valores != null) {
				for (ContenidoTorrent valor : valores) {
					if (valor.getNumero() == index && !diccionario.containsKey(valor.getNombre())) {
						return valor;
					}
				}
			} 
		}
		return null;
    }

    public static HashMap<String, ArrayList<ContenidoTorrent>> getDiccionarioTorrent(TorrentOptions opt, String url, ArrayList<TorrentStream> listaTorrents){
		Future<HashMap<String, ArrayList<ContenidoTorrent>>> future = executor.submit(new MyCallable(opt, url, listaTorrents));
        try {
            return future.get(tiempoRespuestaSegundos, TimeUnit.SECONDS); // Establece el tiempo límite en 10 segundos
        } catch (Exception e) {
            future.cancel(true);
            return null;
        }
	}
	
	static class MyCallable implements Callable<HashMap<String, ArrayList<ContenidoTorrent>>> {
        @Override
        public HashMap<String, ArrayList<ContenidoTorrent>> call() throws Exception {
            return getFileNames(opt, url, listaTorrents);
        }
        private ArrayList<TorrentStream> listaTorrents;
        private TorrentOptions opt;
        private  String url;
        MyCallable(TorrentOptions opt, String url, ArrayList<TorrentStream> listaTorrents){
        	this.opt = opt;
        	this.url = url;
        	this.listaTorrents = listaTorrents;
        }
        
        private HashMap<String, ArrayList<ContenidoTorrent>> getFileNames(TorrentOptions opt, String url, ArrayList<TorrentStream> listaTorrents)
    			throws Exception {
    		TorrentStream to = new TorrentStream(opt, 0);
    		listaTorrents.add(to);
    		TorrentInfoClone t = to.getContenidoTorrent(url);
    		while (!t.getIntroducido()) {
    			try {
    				Thread.sleep(200);
    			} catch (InterruptedException e) {
    				return null;
    			}
    		}
    		if (t.getFallo() || t== null || t.getTorrentInfo() == null) {
    			return null;
    		}
    		FileStorage fileStorage = t.getTorrentInfo().files();
    		HashMap<String, ArrayList<ContenidoTorrent>> diccionario = new HashMap<>();
    		for (int i = 0; i < fileStorage.numFiles(); i++) {
    			String rS = fileStorage.filePath(i).replace("\\", "/");
    			String[] sRuta = rS.split("/");
    			for (int j = 0; j < sRuta.length; j++) {
    				if (j == 0) {
    					if (diccionario.containsKey("/")) {
    						ArrayList<ContenidoTorrent> a = diccionario.get("/");
    						ContenidoTorrent c = new ContenidoTorrent();
    						if (j == sRuta.length-1) {
    							c.setNombreCompleto(rS);
    							c.setTamanyo(fileStorage.fileSize(i));
    						}else {
    							String sMenosFinal = "";
    							for (int k = 0; k < sRuta.length-1; k++) {
    								sMenosFinal= sRuta[k]+"/";
    							}
    							c.setNombreCompleto(sMenosFinal);
    						}
    						c.setNombre(sRuta[j]);
    						c.setNumero(i);
    						if (!a.contains(c)) {
    							a.add(c);
    							diccionario.put("/", a);
    						}
    					}else {
    						ArrayList<ContenidoTorrent> a = new ArrayList<ContenidoTorrent>();
    						ContenidoTorrent c = new ContenidoTorrent();
    						if (j == sRuta.length-1) {
    							c.setNombreCompleto(rS);
    							c.setTamanyo(fileStorage.fileSize(i));
    						}else {
    							String sMenosFinal = "";
    							for (int k = 0; k < sRuta.length-1; k++) {
    								sMenosFinal= sRuta[k]+"/";
    							}
    							c.setNombreCompleto(sMenosFinal);
    						}
    						c.setNombre(sRuta[j]);
    						c.setNumero(i);
    						if (!a.contains(c)) {
    							a.add(c);
    							diccionario.put("/", a);
    						}
    					}
    					
    				} else {
    					if (diccionario.containsKey(sRuta[j - 1])) {
    						ArrayList<ContenidoTorrent> a = diccionario.get(sRuta[j - 1]);
    						ContenidoTorrent c = new ContenidoTorrent();
    						if (j == sRuta.length-1) {
    							c.setNombreCompleto(rS);
    							c.setTamanyo(fileStorage.fileSize(i));
    						}else {
    							String sMenosFinal = "";
    							for (int k = 0; k < sRuta.length-1; k++) {
    								sMenosFinal= sRuta[k]+"/";
    							}
    							c.setNombreCompleto(sMenosFinal);
    						}
    						c.setNombre(sRuta[j]);
    						c.setNumero(i);
    						if (!a.contains(c)) {
    							a.add(c);
    							diccionario.put(sRuta[j - 1], a);
    						}
    					} else {
    						ArrayList<ContenidoTorrent> a = new ArrayList<ContenidoTorrent>();
    						ContenidoTorrent c = new ContenidoTorrent();
    						if (j == sRuta.length-1) {
    							c.setNombreCompleto(rS);
    							c.setTamanyo(fileStorage.fileSize(i));
    						}else {
    							String sMenosFinal = "";
    							for (int k = 0; k < sRuta.length-1; k++) {
    								sMenosFinal= sRuta[k]+"/";
    							}
    							c.setNombreCompleto(sMenosFinal);
    						}
    						c.setNombre(sRuta[j]);
    						c.setNumero(i);
    						if (!a.contains(c)) {
    							a.add(c);
    							diccionario.put(sRuta[j - 1], a);
    						}
    					}
    				}

    			}
    		}
    		return diccionario;
    	}
    }

	@SuppressWarnings("unused")
	private static void getContenido(HashMap<String, ArrayList<ContenidoTorrent>> diccionario) {
		if (diccionario == null) {
			return;
		}
		for (Map.Entry<String, ArrayList<ContenidoTorrent>> entry : diccionario.entrySet()) {
			String clave = entry.getKey();
			ArrayList<ContenidoTorrent> valores = entry.getValue();
			if (valores == null) {
				System.out.println(clave + " es un fichero");
			} else {
				System.out.println(clave + " contiene los siguientes elementos:");
				for (ContenidoTorrent valor : valores) {
					System.out.println("\t" + valor);
				}
			}
			System.out.println();
		}
	}
	

	private static String getContenidoJson(HashMap<String, ArrayList<ContenidoTorrent>> diccionario) {
		if (diccionario == null || !diccionario.containsKey("/")) {
			return "{\"lista\": []}";
		}
		String s = "{\"lista\": [";
		ArrayList<String> sAux = getContenidoJsonCarpeta("/", diccionario, new ArrayList<ContenidoTorrent>());
		for (int i = 0; i < sAux.size(); i++) {
			if (i== sAux.size()-1) {
				s+=sAux.get(i);
			}else {
				s+=sAux.get(i)+",";
			}
			
		}
		s +="]}";
		return s;
	}
	
	private static void borrarCarpeta(File carpeta) {
        if (carpeta.exists()) {
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        borrarCarpeta(archivo);
                    } else {
                        archivo.delete();
                    }
                }
            }
            carpeta.delete();
            System.out.println("Carpeta borrada con éxito.");
        } else {
            System.out.println("La carpeta no existe.");
        }
    }
	
	private static ArrayList<String> getContenidoJsonCarpeta(String s, HashMap<String, ArrayList<ContenidoTorrent>> diccionario, ArrayList<ContenidoTorrent> arrayAux) {
		ArrayList<String> contenidoS = new ArrayList<String>();
		if (diccionario.containsKey(s)) {
			for (ContenidoTorrent c : diccionario.get(s)) {
				String nombre = c.getNombre();
				if (diccionario.containsKey(nombre) ) {
					if (!arrayAux.contains(c)) {
						contenidoS.add("{ \"nombre\": \""+nombre+"\", \"tamanyo\": \""+ContenidoTorrent.convertBytesToString(c.getTamanyo())+"\", \"isFile\": false"+"}");
						for (String string : getContenidoJsonCarpeta(nombre, diccionario, arrayAux)) {
							contenidoS.add(string);
						}
						arrayAux.add(c);
					}
				}else {
					contenidoS.add("{ \"nombre\": \""+nombre+"\", \"tamanyo\": \""+ContenidoTorrent.convertBytesToString(c.getTamanyo())+"\", \"isFile\": true"+", \"indice\": "+c.getNumero()+"}");
				}
			}
		}
		return contenidoS;
	}
	
	public void inicializarRutaGetJsonTorrent() {
		Spark.get("/torrentJson/:ruta", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/torrentJson");
			if (n == 0 && SistemaModulos.getInstance().isValido("torrent")) {
				String rutaTorrent = request.params(":ruta");
				System.out.println(rutaTorrent);
				return getInformacionTorrent(rutaTorrent);
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");
		});
	}
	
	public void inicializarRutaDownloadTorrent() {
		Spark.get("/torrentDownload", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/torrentJson");
			if (n == 0 && SistemaModulos.getInstance().isValido("torrent")) {
				String urlTorrent = request.queryParams("url");
				String indiceS = request.queryParams("index");
				try {
					int num = Integer.valueOf(indiceS);
					descargarTorrent(urlTorrent, num);
					introducirFicheroEnWeb(urlTorrent, num, this.listaTorrents);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");
		});
		
	}
	
	private boolean introducirFicheroEnWeb(String rutaTorrent, int index, ArrayList<TorrentStream> listaTorrents) {
		ContenidoTorrent c = getContenidoTorrent(getDiccionarioTorrent(torrentOptions, rutaTorrent, listaTorrents), index);
		String extension, nombre;
		String rutaFichero = HttpTorrent.rutaPadre.replace("\\", "/")+"/"+c.getNombreCompleto();
		String nombreFichero = c.getNombre();
		boolean yaIntroducido;
		if (Ficheros.tipoFichero(nombreFichero).equals("video")) {
			nombre = Http.encodeURIcomponent(nombreFichero);
			yaIntroducido = Ventana.getInstance().http.estaEnUrl(nombre);
			extension = Ficheros.getExtensionFichero(nombre);
			Ventana.getInstance().http.crearUrlVideo(nombre, rutaFichero);
		} else {
			nombre = Http.encodeURIcomponent(nombreFichero);
			yaIntroducido = Ventana.getInstance().http.estaEnUrl(nombre);
			extension = Ficheros.getExtensionFichero(nombre);
			Ventana.getInstance().http.crearUrlArchivo(nombre, rutaFichero);
		}
		if (!yaIntroducido && JsonModulosMenuWeb.config != null) {
			System.out.println("hola");
			JsonEntradaMenuModulo webArchivo = new JsonEntradaMenuModulo();
			webArchivo.setArchivo();
			webArchivo.setRandomHexa();
			webArchivo.setTitulo(nombreFichero);
			webArchivo.setDescripcion("." + extension);
			webArchivo.setGoTo(JsonModulosMenuWeb.config.getRutaHttp() + "/" + nombre);
			System.out.println("clepnid_webjson");
			webArchivo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagen(nombre));
			ArrayList<ConfiguracionJson> listaModulos = JsonModulosFicheros.obtenerConfiguraciones(extension);
			JsonEntradaMenuModulo modulo = new JsonEntradaMenuModulo();
			if (listaModulos != null) {
				for (ConfiguracionJson configuracionJson : listaModulos) {
					Ventana.getInstance().http.crearUrlModulo(configuracionJson, nombre, rutaFichero);
					// anyadir modulo en website
					modulo.setTitulo(configuracionJson.getTitulo());
					modulo.setRandomHexa();
					modulo.setDescripcion(configuracionJson.getRutaHttp() + "/" + nombre);
					modulo.setGoTo(configuracionJson.getRutaHttp() + "/" + nombre);
					modulo.setRutaImagen(configuracionJson.getRutaImagen());
					webArchivo.addModulo(modulo);
				}
			}

			// anyadir a descarga en website
			modulo.setTitulo("Descargar");
			modulo.setRandomHexa();
			modulo.setDescripcion(nombre);
			modulo.setGoTo("/" + nombre);
			modulo.setRutaImagen(JsonEntradaMenuModulo.getRutaHttpImagenDescarga());
			webArchivo.addModulo(modulo);
			JsonModulosMenuWeb.config.addWeb(webArchivo);
		}

		if (JsonModulosMenuWeb.config != null) {
			synchronized (JsonModulosMenuWeb.config) {
				Ventana.getInstance().http.crearUrlIndice(JsonModulosMenuWeb.config);
			}
		}
		return true;
	}
	
	
	
}
