package de.uniol.ui.desync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * This composite represents a progress bar.
 * 
 * @author Chh
 */
public class ProgressComposite extends Composite {

	/** underlying progress bar widget */
	private ProgressBar bar;
	/** parent dislay */
	private static Display display;
	/** parent shell */
	private static Shell shell;
	/** title of the progress window */
	private String title = new String();
	
	/**
	 * Creates a shell with this composite as contents.
	 */
	public ProgressComposite() {
		this(createShell(), SWT.APPLICATION_MODAL);
	}
	
	/**
	 * Creates this composite in the given shell.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProgressComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * Initializes the progress bar.
	 */
	private void initialize() {
		setLayout(new FillLayout());
		
		bar = new ProgressBar(this, SWT.NONE);
		bar.setMinimum(0);
		bar.setMaximum(100);
	}
	
	/**
	 * Sets the new progress. This method is thread-safe.
	 * 
	 * @param p
	 */
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
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Creates a new shell for this composite.
	 * 
	 * @return
	 */
	public static Shell createShell() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setSize(600, 64);
		shell.setLayout(new FillLayout());
		shell.setText("Simulation progress");
		return shell;
	}
	
	/**
	 * Opens the parent shell and shows the contents. Blocks until the shell is
	 * closed.
	 */
	public void open() {
		if (shell == null) {
			shell = getShell();
		}
		shell.setText(title);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}