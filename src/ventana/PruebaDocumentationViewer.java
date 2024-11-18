package ventana;

/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class PruebaDocumentationViewer {

static Browser browser;
static String[] urls;
static String[] titles;
static int index;

public static void main(String argv[]) {
	Display display = new Display();
	final Shell shell = new Shell(display);
	shell.setText("SWT Browser - Documentation Viewer");
	shell.setLayout(new GridLayout());
	Composite compTools = new Composite(shell, SWT.NONE);
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	compTools.setLayoutData(data);
	compTools.setLayout(new GridLayout(2, false));
	ToolBar tocBar = new ToolBar(compTools, SWT.NONE);
	ToolItem openItem = new ToolItem(tocBar, SWT.PUSH);
	openItem.setText("Browse");
	ToolBar navBar = new ToolBar(compTools, SWT.NONE);
	navBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
	final ToolItem back = new ToolItem(navBar, SWT.PUSH);
	back.setText("Back");
	back.setEnabled(false);
	final ToolItem forward = new ToolItem(navBar, SWT.PUSH);
	forward.setText("Forward");
	forward.setEnabled(false);
	
	Composite comp = new Composite(shell, SWT.NONE);
	data = new GridData(GridData.FILL_BOTH);
	comp.setLayoutData(data);
	comp.setLayout(new FillLayout());
	final SashForm form = new SashForm(comp, SWT.HORIZONTAL);
	form.setLayout(new FillLayout());

	final List list = new List(form, SWT.SINGLE);
	try {
		browser = new Browser(form, SWT.NONE);
	} catch (SWTError e) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setMessage("Closing application. The Browser could not be initialized.");
		messageBox.setText("Fatal error - application terminated");
		messageBox.open();
		System.exit(-1);
	}
	back.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			browser.back();
		}
	});
	forward.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			browser.forward();
		}
	});
	list.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event e) {
			int index = list.getSelectionIndex();
			browser.setUrl(urls[index]);
		}
	});
	final LocationListener locationListener = new LocationListener() {
		public void changed(LocationEvent event) {
			Browser browser = (Browser)event.widget;
			back.setEnabled(browser.isBackEnabled());
			forward.setEnabled(browser.isForwardEnabled());
		}
		public void changing(LocationEvent event) {
		}
	};
	new TitleListener() {
		public void changed(TitleEvent event) {
			titles[index] = event.title;
		}
	};
	new ProgressListener() {
		public void changed(ProgressEvent event) {
		}
		public void completed(ProgressEvent event) {
			Browser browser = (Browser)event.widget;
			index++;
			boolean tocCompleted = index >= titles.length;
			if (tocCompleted) {
				browser.dispose();
				browser = new Browser(form, SWT.NONE);
				PruebaDocumentationViewer.browser = browser;
				form.layout(true);
				browser.addLocationListener(locationListener);
				list.removeAll();
				for (int i = 0; i < titles.length; i++) list.add(titles[i]);
				list.select(0);
				browser.setUrl(urls[0]);
				shell.setText("SWT Browser - Documentation Viewer");
				return;
			}
			shell.setText("Building index "+index+"/"+urls.length);
			browser.setUrl(urls[index]);
		}
	};
	openItem.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event e) {
			browser.setUrl("https://www.hola.com/");
		}
	});
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}
}