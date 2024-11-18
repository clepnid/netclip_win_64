package ventanaCodigoApp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;

public class VentanaCodigoApp {

    private Shell shell;
    private StyledText styledText;

    public VentanaCodigoApp(String codigo) {
        Display display = Display.getCurrent();
        shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
        shell.setText("Código App");
        shell.setLayout(new GridLayout(1, false));

        // Establecer fondo blanco para la ventana
        shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        // Crear el widget StyledText para mostrar el código sin barras de desplazamiento
        styledText = new StyledText(shell, SWT.BORDER);
        styledText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true)); // Centrado
        styledText.setText(codigo); // Insertar el código

        // Establecer fondo blanco para el StyledText
        styledText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        // Establecer la fuente más grande
        FontData fontData = new FontData("Arial", 48, SWT.BOLD); // Cambiar el tamaño y tipo de fuente
        Font font = new Font(display, fontData);
        styledText.setFont(font);

        // Asegurarse de que el widget pueda recibir el foco
        styledText.setFocus(); // Forzar que sea focusable y esté enfocado

        styledText.setEditable(false); // Solo lectura

        // Crear un botón para cambiar el texto
        Button cambiarButton = new Button(shell, SWT.PUSH);
        cambiarButton.setText("Cambiar");
        cambiarButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        
        // Acción para el botón de cambiar
        cambiarButton.addListener(SWT.Selection, event -> {
            String nuevoCodigo = "0987654321"; // Código nuevo de ejemplo
            styledText.setText(nuevoCodigo); // Cambiar el código en el StyledText
        });

        // Establecer el tamaño de la ventana
        shell.setSize(500, 200);

        // Centrar la ventana en la pantalla
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);

        shell.open();
    }

    public void open() {
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
