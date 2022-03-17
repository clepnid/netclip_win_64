package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Cierra el proceso node si ha sido creado con anterioridad para crear el proceso lt (localtunnel)
 */

public class CerrarTunelWindows extends Thread {

	public CerrarTunelWindows() {
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
		String[] commands = { "taskkill ", "/f", "/im", "lt-win.exe"};
		return commands;
	}
	/*linux
	 * private String getComandos() {
		return "killall -9 lt-linux";
		
	}*/
	
}
