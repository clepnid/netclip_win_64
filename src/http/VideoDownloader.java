package http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import portapapeles.Ficheros;
import ventana.Configuracion;
import ventana.Ventana;

public class VideoDownloader extends Thread {
	
	public enum Tipo {
		YoutubeAudio, YoutubeVideo, OtherAudio, OtherVideo
	}
	
	private String url, format, quality, outputName, fileToUp;
	
	private Tipo tipo;

	public VideoDownloader(Tipo tipo, String url) {
		this.tipo = tipo;
		this.url = url;
	}

	public void run() {
		// TODO Auto-generated method stub
		ProcessBuilder processBuilder = new ProcessBuilder();
		String[] commands = getComandos();
		Process proc;
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		try {
			processBuilder.directory(new File(System.getProperty("user.dir")+File.separator+"src"+File.separator+"html"+File.separator+"modulo_youtube_downloader"));
			processBuilder.command(commands);
			proc = processBuilder.start();
			stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
				
			}
			stdInput.close();
			stdError.close();
			System.out.println("salir");
			if (fileToUp!=null) {
				System.out.println("youtube ------------------"+fileToUp);
				String extension, nombre;
				boolean yaIntroducido;
				if (Ficheros.tipoFichero(fileToUp).equals("video")) {
					nombre = Http.encodeURIcomponent(fileToUp);
					yaIntroducido = Ventana.http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					Ventana.http.crearUrlVideo(nombre,
							Configuracion.deserializar().carpeta + File.separator + fileToUp);
				} else {
					nombre = Http.encodeURIcomponent(fileToUp);
					yaIntroducido = Ventana.http.estaEnUrl(nombre);
					extension = Ficheros.getExtensionFichero(nombre);
					Ventana.http.crearUrlArchivo(nombre,
							Configuracion.deserializar().carpeta + File.separator + fileToUp);
				}
				if (!yaIntroducido && Clepnid_WebJson.config != null) {
					System.out.println("hola");
					WebJson webArchivo = new WebJson();
					webArchivo.setArchivo();
					webArchivo.setRandomHexa();
					webArchivo.setTitulo(fileToUp);
					webArchivo.setDescripcion("." + extension);
					webArchivo.setGoTo(Clepnid_WebJson.config.getRutaHttp() + "/" + nombre);
					webArchivo.setRutaImagen(WebJson.getRutaHttpImagen(nombre));
					ArrayList<ConfiguracionJson> listaModulos = ClepnidJson.obtenerConfiguraciones(extension);
					WebJson modulo = new WebJson();
					if (listaModulos != null) {
						for (ConfiguracionJson configuracionJson : listaModulos) {
							Ventana.http.crearUrlModulo(configuracionJson, nombre, Configuracion.deserializar().carpeta + File.separator + fileToUp);
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
					modulo.setRutaImagen(WebJson.getRutaHttpImagenDescarga());
					webArchivo.addModulo(modulo);
					Clepnid_WebJson.config.addWeb(webArchivo);
				}

				if (Clepnid_WebJson.config != null) {
					Ventana.http.crearUrlIndice(Clepnid_WebJson.config);
				}
			}
			
		} catch (IOException e1) {
			try {
				if (stdInput != null) {
					stdInput.close();
				}
				if (stdInput != null) {
					stdError.close();
				}
			} catch (IOException e) {
				System.out.print("");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setQuality(String quality) {
		this.quality = quality;
	}
	
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	private String[] getComandos() {
		String carpeta;
		try {
			carpeta = Configuracion.deserializar().carpeta;
			carpeta.replace(" ", "%20");
		} catch (ClassNotFoundException | IOException e) {
			carpeta = "";
		}
		switch (this.tipo) {
		case YoutubeAudio:
			String[] commands = {"./src/html/modulo_youtube_downloader/yt-dlp.exe","-o",carpeta+File.separator+this.outputName.replace(" ", "%20")+".%(ext)s","--extract-audio","--audio-format","mp3",this.url};
			fileToUp = this.outputName+".mp3";
			return commands;
			
		case YoutubeVideo:
			String[] commands1 = {"./src/html/modulo_youtube_downloader/ffmpeg-win.exe", "--url", this.url, "-f", this.format, "-q", this.quality, "-o", carpeta+File.separator+this.outputName};
			fileToUp = this.outputName+"."+this.format;
			return commands1;

		case OtherAudio:
			String[] commands2 = {"./src/html/modulo_youtube_downloader/yt-dlp.exe","-o",carpeta+File.separator+this.outputName.replace(" ", "%20")+".%(ext)s","--extract-audio","--audio-format","mp3",this.url};
			fileToUp = this.outputName+".mp3";
			return commands2;

		case OtherVideo:
			String[] commands3 = {"./src/html/modulo_youtube_downloader/yt-dlp.exe","-o",carpeta+File.separator+this.outputName.replace(" ", "%20"),this.url};
			fileToUp = this.outputName;
			return commands3;

		default:
			return null;
		}
	}
}
