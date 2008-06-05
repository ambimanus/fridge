package de.uniol.ui.desync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressComposite extends Composite {

	private ProgressBar bar;
	private static Display display;
	private static Shell shell;
	
	public ProgressComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		setLayout(new FillLayout());
		
		bar = new ProgressBar(this, SWT.SMOOTH);
		bar.setMinimum(0);
		bar.setMaximum(100);
	}
	
	public void setProgress(int p) {
		final int pr = p;
		if (!isDisposed()) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (pr < 100) {
						if (!bar.isDisposed()) {
							bar.setSelection(pr);
						}
					} else {
						shell.close();
					}
				}
			});
		}
	}
	
	public static ProgressComposite prepareOpening() {
		display = Display.getDefault();
		if (shell == null) {
			shell = new Shell(display);
		}
		shell.setSize(600, 64);
		shell.setLayout(new FillLayout());
		shell.setText("Simulation progress");
		return new ProgressComposite(shell, SWT.NONE);
	}
	
	public void open() {
		if (shell == null) {
			prepareOpening();
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}