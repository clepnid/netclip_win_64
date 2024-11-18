package http.modulosBackend;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComandLogIdentificator {
    public static String deleteLog(String commandOpen) {
        // Definir el patrón para buscar la parte [LOG=archivo_salida.txt] en el comando de apertura
        Pattern pattern = Pattern.compile("\\[LOG=([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(commandOpen);
        
        // Si se encuentra la parte [LOG=archivo_salida.txt], reemplazarla con una cadena vacía
        if (matcher.find()) {
            String logValue = matcher.group(1);
            return commandOpen.replace("[LOG=" + logValue + "]", "");
        }
        
        // Si no se encuentra la parte [LOG=archivo_salida.txt], devolver el comando de apertura original
        return commandOpen;
    }
    public static String getLog(String commandOpen) {
        // Definir el patrón para buscar la parte [LOG=archivo_salida.txt] en el comando de apertura
        Pattern pattern = Pattern.compile("\\[LOG=([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(commandOpen);
        
        // Si se encuentra la parte [LOG=archivo_salida.txt], reemplazarla con una cadena vacía
        if (matcher.find()) {
            String logValue = matcher.group(1);
            return  logValue;
        }
        
        // Si no se encuentra la parte [LOG=archivo_salida.txt], devolver el comando de apertura original
        return null;
    }
    
    public static int getLogPosicionCommand(String commandOpen) {
        int n= -1;
        String[] comandosAux = commandOpen.split("\\|");
        for (String string : comandosAux) {
			n++;
			if (getLog(string)!=null) {
				return n;
			}
		}
        // Si no se encuentra la parte [LOG=archivo_salida.txt], devolver el comando de apertura original
        return n;
    }

    
}