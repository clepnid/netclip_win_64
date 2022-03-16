package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
