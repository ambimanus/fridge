package de.uniol.ui.desync.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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
		if (this.conf == null) {
			conf = new Configuration();
		}
		setText("Settings for objective <"
				+ conf.title
				+ (Configuration.instance == 0 ? ""
						: (" " + Configuration.instance)) + ">");
	}

	public Configuration open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
		shell.setText(getText());
		shell.setLayout(new FillLayout());
		
		initControls();
		
		shell.setSize(835, 570);
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
		ti.setControl(new SimulationSettingsComposite(tf, SWT.NONE, conf));
		ti.setText("Simulation settings");
		ti = new TabItem(tf, SWT.NONE);
		ti.setControl(new PopulationSettingsComposite(tf, SWT.NONE, conf));
		ti.setText("Population settings");
	}
}