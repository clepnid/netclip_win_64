package compartirQR;


import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
 
public class IpPublica {
 
	private String publicIP = null;
 
	public static void main(String args[]){
		new IpPublica();
	}
 
    public IpPublica() {
    	try {
                URL tempURL = new URL("http://www.whatismyip.org/");
                HttpURLConnection tempConn = (HttpURLConnection)tempURL.openConnection();
                InputStream tempInStream = tempConn.getInputStream();
                InputStreamReader tempIsr = new InputStreamReader(tempInStream);
                BufferedReader tempBr = new BufferedReader(tempIsr);        
 
                publicIP = tempBr.readLine();
 
                tempBr.close();
                tempInStream.close();
 
        } catch (Exception ex) {
                publicIP = "<No es posible resolver la direccion IP>";   
          }
 
         System.out.println("Mi IP Publica es " +publicIP);
    }
}