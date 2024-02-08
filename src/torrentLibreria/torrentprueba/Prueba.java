package torrentLibreria.torrentprueba;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.libtorrent4j.FileStorage;
import org.libtorrent4j.TorrentInfo;

import torrentLibreria.exceptions.TorrentInfoException;
import torrentLibreria.ficherosTorrent.ContenidoTorrent;
import torrentLibreria.ficherosTorrent.FicheroTorrent;
import torrentLibreria.torrentstream.StreamStatus;
import torrentLibreria.torrentstream.Torrent;
import torrentLibreria.torrentstream.TorrentInfoClone;
import torrentLibreria.torrentstream.TorrentOptions;
import torrentLibreria.torrentstream.TorrentStream;
import torrentLibreria.torrentstreamserver.TorrentServerListener;
import torrentLibreria.torrentstreamserver.TorrentStreamNotInitializedException;
import torrentLibreria.torrentstreamserver.TorrentStreamServer;

public class Prueba {

	public static String ruta = "magnet:?xt=urn:btih:74b3a343566d644f4a0a390c6c05e7de5b877eba&dn=%5BANi%5D%20Dr%20STONE%20S3%20-%20%20Dr.%20STONE%20%E6%96%B0%E7%9F%B3%E7%B4%80%20%E7%AC%AC%E4%B8%89%E5%AD%A3%20-%2003%20%5B1080P%5D%5BBaha%5D%5BWEB-DL%5D%5BAAC%20AVC%5D%5BCHT%5D%5BMP4%5D&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce";

	public static String ruta1 = "magnet:?xt=urn:btih:221da4283e24317f64d1f651084517eaeba6aaac&dn=%5BSlyFox%5D%20Summertime%20Rendering%20%28Summer%20Time%20Render%29%20-%2024%20%5BB4E39B51%5D.mkv&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce";

	public static String ruta2 = "magnet:?xt=urn:btih:d8b0d1e082efc921f2e7e14996c332e798b00f2f&dn=%E3%80%90%E5%96%B5%E8%90%8C%E5%A5%B6%E8%8C%B6%E5%B1%8B%E3%80%91%5B%E5%A4%8F%E6%97%A5%E9%87%8D%E7%8F%BE%20%2F%20Summer%20Time%20Rendering%5D%5B01-25%5D%5BBDRip%5D%5B1080p%5D%5B%E7%B9%81%E6%97%A5%E9%9B%99%E8%AA%9E%5D%5B%E6%8B%9B%E5%8B%9F%E7%BF%BB%E8%AD%AF%5D&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce";

	public static String ruta3 = "magnet:?xt=urn:btih:d8b0d1e082efc921f2e7e14996c332e798b00f2f&dn=%E3%80%90%E5%96%B5%E8%90%8C%E5%A5%B6%E8%8C%B6%E5%B1%8B%E3%80%91%5B%E5%A4%8F%E6%97%A5%E9%87%8D%E7%8F%BE%20%2F%20Summer%20Time%20Rendering%5D%5B01-25%5D%5BBDRip%5D%5B1080p%5D%5B%E7%B9%81%E6%97%A5%E9%9B%99%E8%AA%9E%5D%5B%E6%8B%9B%E5%8B%9F%E7%BF%BB%E8%AD%AF%5D&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce";

	public static String ruta4 = "magnet:?xt=urn:btih:d8b0d1e082efc921f2e7e1499sdf6c332e798b00f2f&dn=%E3%80%90%E5%96%B5%E8%90%8C%E5%A5%B6%E8%8C%B6%E5%B1%8B%E3%80%91%5B%E5%A4%8F%E6%97%A5%E9%87%8D%E7%8F%BE%20%2F%20Sdummer%20Time%20Rendering%5D%5B01-25%5D%5BBDRip%5D%5B1080p%5D%5B%E7%B9%81%E6%97%A5%E9%9B%99%E8%AA%9E%5D%5B%E6%8B%9B%E5%8B%9F%E7%BF%BB%E8%AD%AF%5D&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Fopen.stealth.si%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce";

	public static String rutaPadre = "C:/Users/pavon/eclipse-workspace/TorrentStream/descargas";
	public static ExecutorService executor;

	public static void main(String[] args) throws TorrentInfoException, URISyntaxException {

        executor = Executors.newSingleThreadExecutor();
		TorrentServerListener s = new TorrentServerListener() {
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
		TorrentOptions torrentOptions = new TorrentOptions.Builder().removeFilesAfterStop(true).build();
		TorrentStreamServer torrentStreamServer = TorrentStreamServer.getInstance();
		String ipAddress = "127.0.0.1";
		torrentStreamServer.setTorrentOptions(torrentOptions);
		torrentStreamServer.setServerHost(ipAddress);
		torrentStreamServer.setServerPort(8080);
		torrentStreamServer.addListener(s);
		FicheroTorrent.getInstance().setRutaPadre(rutaPadre);
		LogManager.getLogManager().reset();

		// el parametro numerico es el numero seleccionado del fichero a descargar
		//torrentStreamServer.startTorrentStream(5);
		//torrentStreamServer.startStream(ruta3);
		// el parametro numerico es el numero seleccionado del fichero a descargar
		//torrentStreamServer.startTorrentStream(0);
		//torrentStreamServer.startStream(ruta);

		System.out.println("entra");
		HashMap<String, ArrayList<ContenidoTorrent>> diccionario1 = getDiccionarioTorrent(torrentOptions, ruta);
		getContenido(diccionario1);
		System.out.println( getContenidoJson(diccionario1));
		HashMap<String, ArrayList<ContenidoTorrent>> diccionario2 = getDiccionarioTorrent(torrentOptions, ruta4);
		getContenido(diccionario2);
		System.out.println( getContenidoJson(diccionario2));
		HashMap<String, ArrayList<ContenidoTorrent>> diccionario3 = getDiccionarioTorrent(torrentOptions, ruta3);
		getContenido(diccionario3);
		System.out.println( getContenidoJson(diccionario3));
		
		System.out.println("terminado");

	}
	
	public static HashMap<String, ArrayList<ContenidoTorrent>> getDiccionarioTorrent(TorrentOptions opt, String url){
		Future<HashMap<String, ArrayList<ContenidoTorrent>>> future = executor.submit(new MyCallable(opt, url));
        try {
        	HashMap<String, ArrayList<ContenidoTorrent>> result = future.get(5, TimeUnit.SECONDS); // Establece el tiempo límite en 5 segundos
            return result;
        } catch (Exception e) {
            future.cancel(true);
            return null;
        }
	}
	
	static class MyCallable implements Callable<HashMap<String, ArrayList<ContenidoTorrent>>> {
        @Override
        public HashMap<String, ArrayList<ContenidoTorrent>> call() throws Exception {
            return getFileNames(opt, url);
        }
        private TorrentOptions opt;
        private  String url;
        MyCallable(TorrentOptions opt, String url){
        	this.opt = opt;
        	this.url = url;
        }
        
        private HashMap<String, ArrayList<ContenidoTorrent>> getFileNames(TorrentOptions opt, String url)
    			throws Exception {
    		TorrentStream to = new TorrentStream(opt, 0);
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
    		to.stopStream();
    		return diccionario;
    	}
    }

	/**
	 * Get the filenames of the files in the torrent
	 *
	 * @return {@link String[]}
	 * @throws URISyntaxException
	 * @throws TorrentInfoException
	 */
	
	

	public static void getContenido(HashMap<String, ArrayList<ContenidoTorrent>> diccionario) {
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
		ArrayList<String> sAux = getContenidoJsonCarpeta("/", diccionario);
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
	
	private static ArrayList<String> getContenidoJsonCarpeta(String s, HashMap<String, ArrayList<ContenidoTorrent>> diccionario) {
		ArrayList<String> contenidoS = new ArrayList<String>();
		if (diccionario.containsKey(s)) {
			for (ContenidoTorrent c : diccionario.get(s)) {
				String nombre = c.getNombre();
				if (diccionario.containsKey(nombre)) {
					contenidoS.add("{ \"nombre\": \""+nombre+"\", \"tamanyo\": \""+ContenidoTorrent.convertBytesToString(c.getTamanyo())+"\", \"isFile\": false"+"}");
					for (String string : getContenidoJsonCarpeta(nombre, diccionario)) {
						contenidoS.add(string);
					}
				}else {
					contenidoS.add("{ \"nombre\": \""+nombre+"\", \"tamanyo\": \""+ContenidoTorrent.convertBytesToString(c.getTamanyo())+"\", \"isFile\": true"+"}");
				}
			}
		}
		return contenidoS;
	}

}
