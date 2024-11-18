package ventana;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

public class DragAndDrop {

	public static void establecer(Ventana ventana) {
		DropTarget dropTarget = new DropTarget(ventana.shlSwt, DND.DROP_COPY | DND.DROP_DEFAULT);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {
			FileTransfer fileTransfer = FileTransfer.getInstance();

			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = DND.DROP_COPY;
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = DND.DROP_COPY;
			}

			public void dragOver(DropTargetEvent event) {
				event.detail = DND.DROP_NONE;
				int index = 0;
				while (index < event.dataTypes.length) {
					if (fileTransfer.isSupportedType(event.dataTypes[index]))
						break;
					index++;
				}
				if (index < event.dataTypes.length) {
					event.currentDataType = event.dataTypes[index];
					event.detail = DND.DROP_COPY;
					return;
				}

			}

			public void drop(DropTargetEvent event) {
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					accion(files, ventana);
				}
			}
		});
	}

	private static void accion(String[] files, Ventana ventana) {
		ArrayList<File> lista = new ArrayList<File>();
		for (String string : files) {
			System.out.println(new File(string));
			lista.add(new File(string));
		}
		if (!lista.isEmpty()) {
			try {
				ventana.teclas.eventos.copiar();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ventana.teclas.eventos.ventanaServidorFichero(lista);
		}
	}
}