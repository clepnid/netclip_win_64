package http;

public class itemRutasComponent {
	private String rutaArchivo, rutaStream, nombre;

	public itemRutasComponent() {
		setRutaArchivo("");
		setRutaStream("");
	}

	public itemRutasComponent(String rutaArchivo, String rutaModuloStream, String nombre) {
		setRutaArchivo(rutaArchivo);
		setRutaStream(rutaModuloStream);
		setNombre(nombre);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public boolean equals(itemRutasComponent componentAux) {
		if (!componentAux.rutaArchivo.equals(this.rutaArchivo)) {
			return false;
		}
		if (!componentAux.rutaStream.equals(this.rutaStream)) {
			return false;
		}
		if (!componentAux.nombre.equals(this.nombre)) {
			return false;
		}
		return true;
	}

}
