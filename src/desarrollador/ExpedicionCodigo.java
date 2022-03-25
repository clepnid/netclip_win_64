package desarrollador;

public class ExpedicionCodigo {
	public static int mes = 3, anyo = 22, tamañoCodigo=40;
	public static String[] letters = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F","G", "H", "I", "J", "K" };

	public static void main(String[] args) {
		String codigo = "";
		while (true) {
			codigo = getCode();
			if (validar(codigo)) {
				System.out.println("\n");
				System.out.println(codigo);
				System.out.println("\n");
			}
		}
		
	}

//retorna un codigo de 20 digitos hexadecimal tal que la (suma de los pares) - (suma impares) = mesActual / anyoActual
	private static String getCode() {
		String color = "";
		for (int i = 0; i < tamañoCodigo; i++) {
			color += letters[(int) Math.round(Math.random() * 20)];
		}
		return color;
	}

	private static boolean validar(String codigo) {
		if (codigo.length()!=tamañoCodigo) {
			return false;
		}
		int contadorPar = 0, contadorImpar = 0;
		for (int i = 1; i < tamañoCodigo; i = i + 2) {
			contadorImpar += getNumeroLetra(codigo.charAt(i));
		}
		for (int i = 0; i < tamañoCodigo; i = i + 2) {
			contadorPar += getNumeroLetra(codigo.charAt(i));
		}
		return ((contadorPar + contadorImpar) == (mes+(anyo*12))) && getNumeroLetra(codigo.charAt(mes-1)) == mes;
	}

	private static int getNumeroLetra(char caracter) {
		for (int i = 0; i < letters.length; i++) {
			if (letters[i].equals(String.valueOf(caracter))) {
				return i;
			}
		}
		return -1;
	}

}
