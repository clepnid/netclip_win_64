package http.modulosBackend;

public class ObjetoBackend {
	private String titulo;
	private String comandoCerrar;
	private String comandoAbrir;

	public ObjetoBackend(String titulo, String comandoAbrir, String comandoCerrar) {
		this.titulo = titulo;
		this.comandoAbrir = comandoAbrir;
		this.comandoCerrar = comandoCerrar;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getComandoCerrar() {
		return comandoCerrar;
	}

	public void setComandoCerrar(String comandoCerrar) {
		this.comandoCerrar = comandoCerrar;
	}

	public String getComandoAbrir() {
		return comandoAbrir;
	}

	public void setComandoAbrir(String comandoAbrir) {
		this.comandoAbrir = comandoAbrir;
	}

	public void ejecutarAbrir() {
		if (this.comandoAbrir != null) {
			new EjecutarComando(this.comandoAbrir).start();
		}
	}

	public void ejecutarCerrar() {
		if (this.comandoCerrar != null) {
			new EjecutarComando(this.comandoCerrar).start();
		}
	}
}
