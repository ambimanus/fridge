package de.uniol.ui.desync.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import de.uniol.ui.desync.Configuration;

public class ObjectiveDialog extends Dialog {

	protected Shell shell;
	protected Configuration conf;
	
	public ObjectiveDialog(Shell parent, Configuration conf) {
		super(parent);
		this.conf = conf;
		setText("Settings for objective <" + conf.title + ">");
	}

	public Configuration open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());
		
		initControls();
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return conf;
	}
	
	protected void initControls() {
		TabFolder tf = new TabFolder(shell, SWT.NONE);
		TabItem ti = new TabItem(tf, SWT.NONE);
		ti.setControl(new SimulationSettingsComposite(tf, SWT.NONE));
	}
}