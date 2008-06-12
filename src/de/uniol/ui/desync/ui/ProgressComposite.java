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
	
	public ProgressComposite() {
		this(createShell(), SWT.APPLICATION_MODAL);
	}
	
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
			getDisplay().syncExec(new Runnable() {
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
	
	public static Shell createShell() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setSize(600, 64);
		shell.setLayout(new FillLayout());
		shell.setText("Simulation progress");
		return shell;
	}
	
	public void open(boolean block) {
		if (shell == null) {
			createShell();
		}
		shell.open();
		if (block) {
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}
}