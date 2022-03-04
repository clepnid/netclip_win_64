package ventana;

public class HiloBarraProgreso extends Thread {
	public VentanaBarraProgreso barraProgreso;

	@Override
	public void run() {
		this.barraProgreso = new VentanaBarraProgreso();
		this.barraProgreso.open();
	}
}
