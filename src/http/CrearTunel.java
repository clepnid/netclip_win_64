package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class CrearTunel extends Thread {
	public static boolean salir = false;
	private String standarOutput, errorOutput, nameDns, descargarNode;
	private Text textoSalida;
	private Display display;

	public CrearTunel(String nombre, Text textoSalida, Display display) {
		this.errorOutput = "";
		this.standarOutput = "";
		if (nombre!="") {
			this.nameDns = nombre;
		}
		this.textoSalida = textoSalida;
		this.display = display;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Runtime rt = Runtime.getRuntime();
		/*linux
		 * String commands = getComandos();*/
		String[] commands = getComandos();
		Process proc;
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		try {
			proc = rt.exec(commands);
			stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			display.asyncExec(new Runnable() {
				public void run() {
					textoSalida.setText("Creando...");
				}
			});
			
			String s = null;
			while ((s = stdInput.readLine()) != null && !salir) {
				standarOutput += s;
				System.out.println(standarOutput);
				display.asyncExec(new Runnable() {
					public void run() {
						textoSalida.setText(standarOutput);
					}
				});
			}

			if (salir) {
				stdInput.close();
				stdError.close();
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null && !salir) {
				errorOutput += s;
				descargarNode = "Es necesario instalar node: https://nodejs.org/es/download/";
				System.out.println(errorOutput);
				display.asyncExec(new Runnable() {
					public void run() {
						textoSalida.setText(descargarNode+"\n"+errorOutput);
					}
				});
				
			}
			if (salir) {
				stdInput.close();
				stdError.close();
			}
			while (!salir) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stdInput.close();
			stdError.close();
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
		}

	}

	private String[] getComandos() {
		if (nameDns != null) {
			String[] commands = {"./src/localtunnel/lt-win.exe", "--port", String.valueOf(Http.getPuertoHTTP()), "--subdomain", this.nameDns };
			return commands;
		} else {
			String[] commands = {"./src/localtunnel/lt-win.exe", "--port", String.valueOf(Http.getPuertoHTTP()) };
			return commands;
		}
	}
	
	/*linux
	 * private String getComandos() {
		if (nameDns != null && !nameDns.equals("")) {
			return "./src/localtunnel/lt-linux --port "+ String.valueOf(Http.getPuertoHTTP()) + " --subdomain "+ this.nameDns ;
			
		} else {
			return "./src/localtunnel/lt-linux --port "+ String.valueOf(Http.getPuertoHTTP());
		}
	}*/
	
	public String getStandarOutput() {
		return standarOutput;
	}

	public void setStandarOutput(String standarOutput) {
		this.standarOutput = standarOutput;
	}

	public String getErroOutput() {
		return errorOutput;
	}

	public void setErroOutput(String erroOutput) {
		this.errorOutput = erroOutput;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				Runtime rt = Runtime.getRuntime();
				/*linux
				 * String commands = getComandos();*/
				ArrayList<String> lista = new ArrayList<String>();
				
				String[] commands = "./src/html/servidorRtmp/ffmpegMonitorRecord/ffmpeg -y -loglevel warning -rtbufsize 150M  -f dshow -probesize 42M -i video=screen-capture-recorder:audio=virtual-audio-capturer -rtbufsize 1024M -threads 4 -r 30 -vcodec libx264 -preset ultrafast -tune zerolatency -crf 18 -b:v 1500k -bufsize 2500k -pix_fmt yuv420p -async 1 -vsync 1 -x264opts keyint=15 -f flv rtmp://127.0.0.1/live/first".split(" ");
				//String[] commands = "java -jar ./src/html/servidorRtmp/servidorRtmp.jar".split(" ");
				for (String string : commands) {
					if (!string.equals("")) {
						lista.add(string);
					}
				}
				Process proc;
				BufferedReader stdInput = null;
				BufferedReader stdError = null;
				try {
					proc = rt.exec(lista.toArray(new String[0]));
					stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

					stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

					// read the output from the command
					System.out.println("Here is the standard output of the command:\n");
					
					String s = null;
					while ((s = stdInput.readLine()) != null && !salir) {
						System.out.println(s);
					}

					if (salir) {
						stdInput.close();
						stdError.close();
					}

					// read any errors from the attempted command
					System.out.println("Here is the standard error of the command (if any):\n");
					while ((s = stdError.readLine()) != null && !salir) {
						System.out.println("ERROR: "+s);
						
					}
					if (salir) {
						stdInput.close();
						stdError.close();
					}
					while (!salir) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					stdInput.close();
					stdError.close();
					if (proc != null) {
						proc.destroy();
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
				}
	}
}
