package http.actividad;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Actividad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nombre, usuario, tipo;
	private ArrayList<String> listaActividad;
	private LocalDateTime fechaHora;

	Actividad() {
		this.setListaActividad(new ArrayList<String>());
	}

	public void anyadirALista(String item) {
		this.listaActividad.add(item);
	}

	public void anyadirALista(int n, String item) {
		String string;
		try {
			string = this.listaActividad.get(n);
			this.listaActividad.set(n, string + item);
		} catch (Exception e) {
			anyadirALista(item);
		}
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ArrayList<String> getListaActividad() {
		return listaActividad;
	}

	public void setListaActividad(ArrayList<String> listaActividad) {
		this.listaActividad = listaActividad;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
