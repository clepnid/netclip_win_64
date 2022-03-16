package ventana;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import http.ConfiguracionJson;

public class ObjetoTablaQr extends TableItem{
	private Ventana ventana;
	private String nombre;
	private ConfiguracionJson config;
	public ObjetoTablaQr(Table arg0, int arg1 ,String nombre, ConfiguracionJson config, Ventana ventana) {
		super(arg0, arg1);
		this.ventana = ventana;
		this.config = config;
		this.nombre = nombre;
	}
	@Override
	protected void checkSubclass() {
		// TODO Auto-generated method stub
//		super.checkSubclass();
	}
	
	public void mostrarQr() {
		ventana.crearVentanaModuloQR(ventana.shlSwt, config.getRutaHttp(), nombre);
	}
	

}
