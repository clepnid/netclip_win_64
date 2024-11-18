package comandos;

import historial.ListaHistorial;
import red.compartirFicheros.ClienteComando;
import red.multicast.MulticastControl;

public class Pegar {
	public static void main(String[] args) {
		for (String ruta : args) {
			MulticastControl controlMulticast = new MulticastControl();
			controlMulticast.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!MulticastControl.ip_servidor.equals("")) {
				ListaHistorial.controlarExistencia();
				red.compartirFicheros.ClienteComando cliente = new ClienteComando(controlMulticast);
				cliente.ruta = ruta;
				cliente.start();
			}else {
				if (controlMulticast!=null) {
					controlMulticast.close();
				}
			}
		}
	}
}
