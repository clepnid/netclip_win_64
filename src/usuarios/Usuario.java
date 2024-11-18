package usuarios;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String usuario, contrasenya, hash, email;
	private ArrayList<String> grupos;
	private boolean sesionActiva;

	public Usuario() {
		grupos = new ArrayList<String>();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Usuario(String usuario, String contrasenya, String hash) {
		grupos = new ArrayList<String>();
		this.usuario = usuario;
		this.contrasenya = contrasenya;
		this.hash = hash;
		this.grupos.add("New");
	}

	public Usuario(String usuario, String contrasenya, String hash, ArrayList<String> grupos) {
		grupos = new ArrayList<String>();
		this.usuario = usuario;
		this.contrasenya = contrasenya;
		this.hash = hash;
		if (grupos.isEmpty()) {
			this.grupos.add("New");
		} else {
			for (String string : grupos) {
				this.grupos.add(string);
			}
		}
	}

	public Usuario(String usuario) {
		grupos = new ArrayList<String>();
		this.usuario = usuario;
		this.contrasenya = "1234";
		this.hash = DigestUtils
				.sha256Hex(this.usuario + "vqXTWb9CzO8sW41EQpmhL0GXM8kw7aZFAK2bv7meXtDCQdyyf1" + this.contrasenya);
		System.out.println(DigestUtils
				.sha256Hex(this.usuario + "vqXTWb9CzO8sW41EQpmhL0GXM8kw7aZFAK2bv7meXtDCQdyyf1" + this.contrasenya));
		this.grupos.add("New");
	}

	public Usuario(String usuario, ArrayList<String> grupos) {
		this.grupos = new ArrayList<String>();
		this.usuario = usuario;
		this.contrasenya = "1234";
		this.hash = DigestUtils
				.sha256Hex(this.usuario + "vqXTWb9CzO8sW41EQpmhL0GXM8kw7aZFAK2bv7meXtDCQdyyf1" + this.contrasenya);
		System.out.println(DigestUtils
				.sha256Hex(this.usuario + "vqXTWb9CzO8sW41EQpmhL0GXM8kw7aZFAK2bv7meXtDCQdyyf1" + this.contrasenya));
		if (grupos.isEmpty()) {
			this.grupos.add("New");
		} else {
			for (String string : grupos) {
				this.grupos.add(string);
			}
		}
	}

	public String getContrasenya() {
		return contrasenya;
	}

	public ArrayList<String> getGrupos() {
		return grupos;
	}

	public String getHash() {
		return hash;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void anyadirGrupo(String grupo) {
		if (-1 == grupoExistente(grupo)) {
			this.grupos.add(grupo);
		}
	}

	public void eliminarGrupo(String grupo) {
		int indiceGrupo = grupoExistente(grupo);
		if (-1 != indiceGrupo) {
			this.grupos.remove(indiceGrupo);
		}
	}

	public int grupoExistente(String grupo) {
		int numero = -1;
		for (String g : grupos) {
			numero++;
			if (g.equals(grupo)) {
				return numero;
			}
		}
		return -1;
	}

	public boolean tieneGrupo(ArrayList<String> grupo) {
		for (String string : grupo) {
			if (grupoExistente(string) != -1) {
				return true;
			}
		}
		return false;
	}

	public boolean compararHash(String string) {
		return string.equals(hash);
	}

	@Override
	public String toString() {
		String string = "Usuario: " + this.usuario + ", contrasenya: " + this.contrasenya + ", hash: " + this.hash
				+ ", grupos: ";
		int numero = 0;
		for (String stringAux : grupos) {
			if (numero == 0) {
				string += stringAux;
			} else {
				string += ", " + stringAux;
			}
			numero++;
		}
		return string;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Usuario usuarioAux = (Usuario) obj;
			if (!usuarioAux.getUsuario().equals(this.getUsuario())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean equals(Usuario usuarioAux) {
		if (!usuarioAux.getUsuario().equals(this.getUsuario())) {
			return false;
		}
		return true;
	}

	public boolean isSesionActiva() {
		return sesionActiva;
	}

	public void setSesionActiva(boolean sesionActiva) {
		this.sesionActiva = sesionActiva;
	}

}
