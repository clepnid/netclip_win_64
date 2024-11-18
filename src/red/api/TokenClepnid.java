package red.api;

import java.io.Serializable;

public class TokenClepnid implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
    private String token;
    private JsonData jsonData;  
    private String fecha;

    // Constructor
    public TokenClepnid(String id, String token, JsonData jsonData, String fecha) {
        this.id = id;
        this.token = token;
        this.jsonData = jsonData;
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public JsonData getJsonData() {
    	return this.jsonData;
    }

    public void setJsonData(JsonData jsonData) {
        this.jsonData = jsonData;  // Método para establecer jsonData
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "TokenClepnid [id=" + id + ", token=" + token + ", jsonData=" + jsonData.toString() + ", fecha=" + fecha + "]";
    }
}
