package http;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import spark.Spark;
import ventana.Ventana;
import ventanaGestionarModulo.SistemaModulos;

public class HttpBackendControladorTeclasRaton {

	private static HttpBackendControladorTeclasRaton INSTANCE;
	ArrayList<Integer> listaTeclas;
	Robot robot;
	int screenWidth, screenHeight, contador;

	public static HttpBackendControladorTeclasRaton getInstance() throws AWTException {
		if (INSTANCE == null) {
			INSTANCE = new HttpBackendControladorTeclasRaton();
		}

		return INSTANCE;
	}

	private HttpBackendControladorTeclasRaton() throws AWTException {
		this.robot = new Robot();
		this.contador = 0;
		listaTeclas = new ArrayList<Integer>();
		this.screenWidth = 0;
		this.screenHeight = 0;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (GraphicsDevice device : gs) {
			this.screenWidth += device.getDisplayMode().getWidth();
			this.screenHeight += device.getDisplayMode().getHeight();
		}
		// CREAR ENTRADAS HTTP
		crearControladorTecladoDown();
		crearControladorTecladoUp();
		crearControladorMouseMove();
		crearControladorMouseDown();
		crearControladorMouseUp();
		crearControladorPegarTexto();
		crearControladorContextMenu();
	}

	private void crearControladorTecladoDown() {
		Spark.get("/controlClepnid/tecladoDown", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/tecladoDown");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				String parametro = req.queryParams("caracter");
				if (parametro != null) {
					this.contador++;
					System.out.println(this.contador);
					presionarTecla(parametro);
					return true;
				}
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorMouseUp() {
		Spark.get("/controlClepnid/mouseUp", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/mouseUp");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				this.contador++;
				System.out.println(this.contador);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorContextMenu() {
		Spark.get("/controlClepnid/contextMenu", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/contextMenu");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {

				this.contador++;
				System.out.println(this.contador);
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorPegarTexto() {
		// Manejar una solicitud POST en la URL "/controlClepnid/pegarTexto"
		Spark.post("/controlClepnid/pegarTexto", (request, response) -> {
			int n = HttpBackendUsuarios.tienePermiso(request, response, "/controlClepnid/pegarTexto");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				String jsonString = request.body(); // Obtener el cuerpo de la solicitud
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(jsonString);
				String texto = (String) json.get("texto");

				// Imprimir el texto por pantalla
				this.contador++;
				System.out.println(this.contador);
				Ventana.getInstance().teclas.eventos.pegarTexto(texto);

				return true;
			}
			return HttpBackend.renderIndex(request, response, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorMouseDown() {
		Spark.get("/controlClepnid/mouseDown", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/mouseDown");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				this.contador++;
				System.out.println(this.contador);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorTecladoUp() {
		Spark.get("/controlClepnid/tecladoUp", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/tecladoUp");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				String parametro = req.queryParams("caracter");
				if (parametro != null) {
					System.out.println("Tecleado soltada: " + parametro);
					dejarPresionarTecla(parametro);
					return true;
				}
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private void crearControladorMouseMove() {
		Spark.get("/controlClepnid/mouseMove", (req, res) -> {
			int n = HttpBackendUsuarios.tienePermiso(req, res, "/controlClepnid/mouseMove");
			if (n == 0 && SistemaModulos.getInstance().isValido("PC Control")) {
				this.contador++;
				System.out.println(this.contador);
				robot.mouseMove(
						Double.valueOf((Double.valueOf(req.queryParams("x")) * this.screenWidth) / 100).intValue(),
						Double.valueOf((Double.valueOf(req.queryParams("y")) * this.screenHeight) / 100).intValue());
				return true;
			}
			return HttpBackend.renderIndex(req, res, "./src/html/404/index.html", "/404/index.html");
		});
	}

	private synchronized void presionarTecla(String parametro) {
		int keyCode = getKeyCode(parametro);
		if (keyCode != 0 && !listaTeclas.contains(keyCode)) {
			// presionar keycode con robot
			listaTeclas.add(keyCode);
			try {
				robot.keyPress(keyCode);
				this.contador++;
				System.out.println(this.contador);
			} catch (Exception e) {
				System.out.print("");
			}
		}
	}

	private synchronized void dejarPresionarTecla(String parametro) {
		int keyCode = getKeyCode(parametro);
		if (keyCode != 0) {
			int index = listaTeclas.indexOf(keyCode);
			if (index != -1) {
				listaTeclas.remove(index);
				try {
					robot.keyRelease(keyCode);
					this.contador++;
					System.out.println(this.contador);
				} catch (Exception e) {
					System.out.print("");
				}
			}
		}
	}

	private synchronized int getKeyCode(String parametro) {
		String parametroMayus = parametro.toUpperCase();
		if (parametroMayus.length() == 1) {
			return KeyEvent.getExtendedKeyCodeForChar(parametro.charAt(0));
		} else {
			if (parametro.replace(" ", "").equals("")) {
				return KeyEvent.VK_SPACE;
			}
		}
		switch (parametroMayus) {
		case "F1":
			return KeyEvent.VK_F1;
		case "F2":
			return KeyEvent.VK_F2;
		case "F3":
			return KeyEvent.VK_F3;
		case "F4":
			return KeyEvent.VK_F4;
		case "F5":
			return KeyEvent.VK_F5;
		case "F6":
			return KeyEvent.VK_F6;
		case "F7":
			return KeyEvent.VK_F7;
		case "F8":
			return KeyEvent.VK_F8;
		case "F9":
			return KeyEvent.VK_F9;
		case "F10":
			return KeyEvent.VK_F10;
		case "F11":
			return KeyEvent.VK_F11;
		case "F12":
			return KeyEvent.VK_F12;
		case "F13":
			return KeyEvent.VK_F13;
		case "F14":
			return KeyEvent.VK_F14;
		case "F15":
			return KeyEvent.VK_F15;
		case "F16":
			return KeyEvent.VK_F16;
		case "F17":
			return KeyEvent.VK_F17;
		case "F18":
			return KeyEvent.VK_F18;
		case "F19":
			return KeyEvent.VK_F19;
		case "F20":
			return KeyEvent.VK_F20;
		case "F21":
			return KeyEvent.VK_F21;
		case "F22":
			return KeyEvent.VK_F22;
		case "F23":
			return KeyEvent.VK_F23;
		case "F24":
			return KeyEvent.VK_F24;
		case "CONTROL":
			return KeyEvent.VK_CONTROL;
		case "SHIFT":
			return KeyEvent.VK_SHIFT;
		case "ALTGRAPH":
			return KeyEvent.VK_ALT_GRAPH;
		case "ALT":
			return KeyEvent.VK_ALT;
		case "META":
			return KeyEvent.VK_META;
		case "COMMAND":
			return KeyEvent.VK_META;
		case "ARROWUP":
			return KeyEvent.VK_UP;
		case "ARROWDOWN":
			return KeyEvent.VK_DOWN;
		case "ARROWLEFT":
			return KeyEvent.VK_LEFT;
		case "ARROWRIGHT":
			return KeyEvent.VK_RIGHT;
		case "PAGEUP":
			return KeyEvent.VK_PAGE_UP;
		case "PAGEDOWN":
			return KeyEvent.VK_PAGE_DOWN;
		case "HOME":
			return KeyEvent.VK_HOME;
		case "END":
			return KeyEvent.VK_END;
		case "ENTER":
			return KeyEvent.VK_ENTER;
		case "ESCAPE":
			return KeyEvent.VK_ESCAPE;
		case "BACKSPACE":
			return KeyEvent.VK_BACK_SPACE;
		case "TAB":
			return KeyEvent.VK_TAB;
		case "DELETE":
			return KeyEvent.VK_DELETE;
		case ".":
			return KeyEvent.VK_PERIOD;
		case ",":
			return KeyEvent.VK_COMMA;
		case ";":
			return KeyEvent.VK_SEMICOLON;
		case "'":
			return KeyEvent.VK_QUOTE;
		case "`":
			return KeyEvent.VK_BACK_QUOTE;
		case "DEAD":
			return KeyEvent.VK_BACK_QUOTE;
		case "-":
			return KeyEvent.VK_MINUS;
		case "=":
			return KeyEvent.VK_EQUALS;
		case "PLUS":
			return KeyEvent.VK_PLUS;
		case "[":
			return KeyEvent.VK_OPEN_BRACKET;
		case "]":
			return KeyEvent.VK_CLOSE_BRACKET;
		case "\\":
			return KeyEvent.VK_BACK_SLASH;
		case "/":
			return KeyEvent.VK_SLASH;
		case "#":
			return KeyEvent.VK_NUMBER_SIGN;
		case "SPACEBAR":
			return KeyEvent.VK_SPACE;
		case " ":
			return KeyEvent.VK_SPACE;
		case "INSERT":
			return KeyEvent.VK_INSERT;
		case "CAPSLOCK":
			return KeyEvent.VK_CAPS_LOCK;
		case "NUMLOCK":
			return KeyEvent.VK_NUM_LOCK;
		case "SCROLLLOCK":
			return KeyEvent.VK_SCROLL_LOCK;
		case "PRINTSCREEN":
			return KeyEvent.VK_PRINTSCREEN;
		case "PAUSE":
			return KeyEvent.VK_PAUSE;
		case "CONTEXTMENU":
			return KeyEvent.VK_CONTEXT_MENU;

		default:
			return 0;

		}
	}

}
