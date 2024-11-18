package usuarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class SistemaUsuarios implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ruta = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "Usuarios.ser";
	private ArrayList<Usuario> usuarios;
	private ArrayList<String> grupos;
	private ArrayList<String> hashUsuariosLogeados;

	public ArrayList<Usuario> getUsuarios() {
		return usuarios;
	}

	public ArrayList<String> getGrupos() {
		return grupos;
	}
	
	public ArrayList<String> getHashUsuariosLogeados() {
		return hashUsuariosLogeados;
	}

	public boolean seEncuentraIniciado(String hash) {
		if (this.hashUsuariosLogeados.isEmpty()) {
			return false;
		}
		return this.hashUsuariosLogeados.contains(hash);
	}
	
	public int getnumeroUsuariosLogeados() {
		return hashUsuariosLogeados.size();
	}
	
	public Usuario getUsuarioDesdeHash(String hash) {
		for (Usuario usuario : usuarios) {
			if (usuario.getHash().equals(hash)) {
				return usuario;
			}
		}
		return null;
	}
	
	public boolean cerrarSesionDesdeHash(String hash) {
		Usuario user = getUsuarioDesdeHash(hash);
		if (user != null) {
			user.setSesionActiva(false);
			return true;
		}
		return false;
	}
	
	public boolean abrirSesionDesdeHash(String hash) {
		Usuario user = getUsuarioDesdeHash(hash);
		if (user != null) {
			user.setSesionActiva(true);
			return true;
		}
		return false;
	}

	public boolean anyadirLog(String hash) {
		if (seEncuentraIniciado(hash)) {
			return false;
		}
		hashUsuariosLogeados.add(hash);
		serializar();
		return true;
	}
	
	public boolean eliminarLog(String hash) {
		if (seEncuentraIniciado(hash)) {
			hashUsuariosLogeados.remove(hashUsuariosLogeados.indexOf(hash));
			serializar();
			return true;
		}
		return false;
	}

	public boolean seEncuentraHash(String nombreUser, String hash) {
		System.out.println(nombreUser);
		System.out.println(hash);
		System.out.println(getPosicionUsuario(nombreUser));
		int pos = getPosicionUsuario(nombreUser);
		if (pos != -1) {
			return hash.equals(usuarios.get(getPosicionUsuario(nombreUser)).getHash());
		}
		return false;
		
	}

	public SistemaUsuarios() {
		this.usuarios = new ArrayList<Usuario>();
		this.grupos = new ArrayList<String>();
		this.hashUsuariosLogeados = new ArrayList<String>();
	}

	public boolean anyadirGrupo(String grupo) {
		if (getPosicion(grupo) == -1) {
			grupos.add(grupo);
			serializar();
			return true;
		}
		return false;
	}
	
	public boolean cambiarNombreGrupo(String n1, String n2){
		int pos = getPosicion(n1);
		if (pos != -1) {
			grupos.set(pos, n2);
			for (Usuario us : usuarios) {
				int posAux = us.getGrupos().indexOf(n1);
				if (posAux != -1) {
					us.getGrupos().set(posAux, n2);
				}
			}
			serializar();
			return true;
		}
		return false;
	}

	public boolean anyadirUsuario(Usuario usuario) {
		if (getPosicion(usuario) == -1) {
			usuarios.add(usuario);
			serializar();
			return true;
		}
		return false;
	}

	public boolean eliminarUsuario(Usuario usuario) {
		int posicion = getPosicion(usuario);
		if (posicion != -1) {
			usuarios.remove(posicion);
			serializar();
			return true;
		}
		return false;
	}

	public boolean eliminarGrupo(String grupo) {
		int posicion = getPosicion(grupo);
		if (posicion != -1) {
			grupos.remove(posicion);
			serializar();
			return true;
		}
		return false;
	}

	public boolean existeUsuario(Usuario usuario) {
		return getPosicion(usuario) != -1;
	}

	public boolean existeGrupo(String grupo) {
		return getPosicion(grupo) != -1;
	}

	public boolean usuarioTieneAcceso(Usuario usuario, ArrayList<String> gruposAcceso) {
		for (String grupoUsuario : usuarios.get(getPosicion(usuario)).getGrupos()) {
			for (String grupoAux : gruposAcceso) {
				if (grupoUsuario.equals(grupoAux)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hashEsCorrecto(String usuario, String hash) {
		return usuarios.get(getPosicionUsuario(usuario)).compararHash(hash);
	}

	public static Boolean existeFicheroConfig() {
		return new File(ruta).exists();
	}

	public static boolean serializar(SistemaUsuarios sis) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ruta);
			try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(sis);
				oos.close();
			}
			fos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	
	public synchronized boolean serializar() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ruta);
			try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(this);
				oos.close();
			}
			fos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public static Boolean controlarExistencia() {
		if (!existeFicheroConfig()) {
			SistemaUsuarios sistema = new SistemaUsuarios();
			File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
			if (!ficheroAux.exists()) {
				ficheroAux.mkdir();
			}
			sistema.anyadirGrupo("New");
			serializar(sistema);
		}
		return true;
	}

	public void anyadirGrupoAUsuario(Usuario usuario, String grupo) {
		if (existeGrupo(grupo)) {
			usuario.anyadirGrupo(grupo);
			serializar();
		}
	}

	public boolean anyadirGrupoAUsuario(String usuario, ArrayList<String> grupo) {
		Usuario usuarioAux = getUsuario(usuario);
		if (usuarioAux != null) {
			for (String string : grupo) {
				anyadirGrupoAUsuario(usuarioAux, string);
			}
		} else {
			return false;
		}
		serializar();
		return true;

	}

	public boolean cambiarNombreUsuario(String nombre, int posicion) {
		Usuario usuarioAux = usuarios.get(posicion);
		if (usuarioAux != null) {
			usuarioAux.setUsuario(nombre);
		} else {
			return false;
		}
		serializar();
		return true;
	}

	public static synchronized SistemaUsuarios deserializar() {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				SistemaUsuarios sistema = (SistemaUsuarios) ois.readObject();
				ois.close();
				fis.close();
				if (sistema.grupos.isEmpty()) {
					sistema.grupos.add("New");
				}
				return sistema;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	private int getPosicionUsuario(String usuario) {
		int posicion = -1;

		for (Usuario usuarioAux : usuarios) {
			posicion++;
			if (usuarioAux.getUsuario().equals(usuario)) {
				return posicion;
			}
		}

		return -1;

	}

	private int getPosicion(Usuario usuario) {
		return posicionUsuarioEnArrayList(usuario, this.usuarios);
	}

	private int getPosicion(String grupo) {
		return posicionCadenaEnArrayList(grupo, this.grupos);
	}

	public Usuario getUsuario(String nombre) {
		int posicion = getPosicionUsuario(nombre);
		if (posicion != -1) {
			return usuarios.get(posicion);
		} else {
			return null;
		}
	}

	public Usuario getUsuario(int posicion) {
		return usuarios.get(posicion);
	}

	private int posicionCadenaEnArrayList(String string, ArrayList<String> lista) {
		int posicion = -1;
		for (String string2 : lista) {
			posicion++;
			if (string2.equals(string)) {
				return posicion;
			}
		}
		return -1;
	}

	private int posicionUsuarioEnArrayList(Usuario usuario, ArrayList<Usuario> lista) {
		int posicion = -1;
		for (Usuario usuario2 : lista) {
			posicion++;
			if (usuario2.equals(usuario)) {
				return posicion;
			}
		}
		return -1;
	}

}
