package red.api;

import java.io.Serializable;
import java.util.List;

public class JsonData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> ips; // Lista de IPs extraídas de jsonData

	public List<String> getIps() {
		return ips;
	}

	public void setIps(List<String> ips) {
		this.ips = ips;
	}
	// Sobreescribir el método toString() para representar el objeto en forma legible
    @Override
    public String toString() {
        return ""+ips;
    }
}


