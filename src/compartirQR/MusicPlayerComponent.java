package compartirQR;

public class MusicPlayerComponent {
	private static String rutaModulo;
	private String rutaArchivo, rutaStream;

	public MusicPlayerComponent() {
		setRutaArchivo("");
		setRutaStream("");
	}
	
	public MusicPlayerComponent(String nombre) {
		setRutaArchivo(nombre);
		setRutaStream("/clepnid_stream"+rutaModulo+"/"+nombre);
	}

	public String getRutaArchivo() {
		return rutaArchivo;
	}

	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	public String getRutaStream() {
		return rutaStream;
	}

	public void setRutaStream(String rutaStream) {
		this.rutaStream = rutaStream;
	}

	public static String getRutaModulo() {
		return rutaModulo;
	}

	public static void setRutaModulo(String rutaModulo) {
		MusicPlayerComponent.rutaModulo = rutaModulo;
	}
	
	public boolean equals(MusicPlayerComponent componentAux) {
		if (!componentAux.rutaArchivo.equals(this.rutaArchivo)) {
			return false;
		}
		if (!componentAux.rutaStream.equals(this.rutaStream)) {
			return false;
		}
		return true;
	}

}
