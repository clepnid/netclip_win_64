package torrentLibreria.ficherosTorrent;

public class ContenidoTorrent {
	private String nombre, nombreCompleto;
	private int numero;
	private long tamanyo;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Nombre: "+nombre+", NombreCompleto: "+nombreCompleto+", Numero: "+numero+", Tamaño: "+convertBytesToString(tamanyo);
	}
	
	public static String convertBytesToString(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            double kilobytes = bytes / 1024.0;
            return String.format("%.2f KB", kilobytes);
        } else if (bytes < 1024 * 1024 * 1024) {
            double megabytes = bytes / (1024.0 * 1024);
            return String.format("%.2f MB", megabytes);
        } else {
            double gigabytes = bytes / (1024.0 * 1024 * 1024);
            return String.format("%.2f GB", gigabytes);
        }
    }

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		ContenidoTorrent c;
		try {
			c = (ContenidoTorrent) obj;
			return c.getNombre().equals(this.nombre) && c.getNombreCompleto().equals(this.nombreCompleto);
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public long getTamanyo() {
		return tamanyo;
	}

	public void setTamanyo(long tamanyo) {
		this.tamanyo = tamanyo;
	}
}
