package ventanaGestionarModulo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SistemaModulos implements Serializable {
	
	private class ObjetoBooleano implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean habilitado, inicializado;
		
		public ObjetoBooleano() {
			this.setHabilitado(true);
			this.setInicializado(false);
		}

		public boolean isHabilitado() {
			return habilitado;
		}

		public void setHabilitado(boolean habilitado) {
			this.habilitado = habilitado;
		}

		public boolean isInicializado() {
			return inicializado;
		}

		public void setInicializado(boolean inicializado) {
			this.inicializado = inicializado;
		}
		
		public boolean isValido() {
			return inicializado && habilitado;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "\nEsta Habilitado: "+isHabilitado()+"\nEsta Inicializado: "+isInicializado();
		}
		
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SistemaModulos INSTANCE;
	private static final String ruta = System.getProperty("user.home") + File.separator + "Clepnid" + File.separator
			+ "ModulosHabilitados.ser";
	private Map<String, ObjetoBooleano> diccionario;
    
	public SistemaModulos(SistemaModulos s) {
		diccionario = new HashMap<>();
		for (Entry<String, ObjetoBooleano> entry : s.getDiccionario().entrySet()) {
            String clave = entry.getKey();
            ObjetoBooleano valorAsociado = entry.getValue();
            diccionario.put(clave, valorAsociado);
        }
	}
	
	public SistemaModulos() {
		diccionario = new HashMap<>();
	}
	
	public static Boolean existeFicheroConfig() {
		return new File(ruta).exists();
	}
	
	public static SistemaModulos getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SistemaModulos(SistemaModulos.deserializar());
        }
        
        return INSTANCE;
    }

	public static boolean serializar(SistemaModulos sis) {
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
			SistemaModulos sistema = new SistemaModulos();
			File ficheroAux = new File(System.getProperty("user.home") + File.separator + "Clepnid");
			if (!ficheroAux.exists()) {
				ficheroAux.mkdir();
			}
			serializar(sistema);
		}
		return true;
	}
	
	//usado para introducir en el sistema los modulos web de la carpeta estatica
	
	public void inicializarModulo(String nombreModulo) {
		ObjetoBooleano valor;
		if ( diccionario.containsKey(nombreModulo)) {
			valor = diccionario.get(nombreModulo);
		}else {
			valor = new ObjetoBooleano();
		}
		valor.setInicializado(true);
		diccionario.put(nombreModulo, valor);
	}
	
	public ArrayList<String> getListaNombreModulosWeb(){
		ArrayList<String> a = new ArrayList<String>();
		for (Entry<String, ObjetoBooleano> entry : diccionario.entrySet()) {
			a.add(entry.getKey());
		}
		return a;
	}
	
	public boolean isHabilitado(String nombreModuloWeb) {
		if (diccionario.containsKey(nombreModuloWeb)) {
			return diccionario.get(nombreModuloWeb).isHabilitado();
		}
		return false;
	}
	
	public boolean isInicializado(String nombreModuloWeb) {
		if (diccionario.containsKey(nombreModuloWeb)) {
			return diccionario.get(nombreModuloWeb).isInicializado();
		}
		return false;
	}
	
	public boolean isValido(String nombreModuloWeb) {
		if (diccionario.containsKey(nombreModuloWeb)) {
			return diccionario.get(nombreModuloWeb).isValido();
		}
		return false;
	}
	
	public void setHabilitado(String nombreModuloWeb, boolean b) {
		if (diccionario.containsKey(nombreModuloWeb)) {
			ObjetoBooleano o = diccionario.get(nombreModuloWeb);
			o.setHabilitado(b);
			diccionario.put(nombreModuloWeb, o);
		}
	}
	
	public void setInicializado(String nombreModuloWeb, boolean b) {
		if (diccionario.containsKey(nombreModuloWeb)) {
			ObjetoBooleano o = diccionario.get(nombreModuloWeb);
			o.setInicializado(b);
			diccionario.put(nombreModuloWeb, o);
		}
	}
	
	@Override
	public String toString() {
		String s = "WEB HABILITACION DE MODULOS: \n";
		for (Entry<String, ObjetoBooleano> entry : getDiccionario().entrySet()) {
            String clave = entry.getKey();
            ObjetoBooleano valorAsociado = entry.getValue();
            s+=clave+": "+valorAsociado+"\n";
        }
		return s;
	}


	public static synchronized SistemaModulos deserializar() {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			try (ObjectInputStream ois = new ObjectInputStream(fis)) {
				SistemaModulos sistema = (SistemaModulos) ois.readObject();
				ois.close();
				fis.close();
				sistema.setInicializadoFalse();
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
	
	public void setInicializadoFalse() {
		for (Entry<String, ObjetoBooleano> entry : diccionario.entrySet()) {
            String clave = entry.getKey();
            ObjetoBooleano valorAsociado = entry.getValue();
            valorAsociado.setInicializado(false);
            diccionario.put(clave, valorAsociado);
        }
	}

	public Map<String, ObjetoBooleano> getDiccionario() {
		return diccionario;
	}

	public void setDiccionario(Map<String, ObjetoBooleano> diccionario) {
		this.diccionario = diccionario;
	}

}
