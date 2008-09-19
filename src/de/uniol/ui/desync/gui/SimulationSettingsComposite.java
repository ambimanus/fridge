package de.uniol.ui.desync.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import de.uniol.ui.desync.Configuration;
import de.uniol.ui.desync.Configuration.DAMPING;
import de.uniol.ui.desync.Configuration.MODEL;
import de.uniol.ui.desync.Configuration.SIGNAL;

public class SimulationSettingsComposite extends Composite {

	private Configuration conf;
	private Text title;
	private Spinner length;
	private Group dsc;
	private Spinner repetitions;
	private Spinner population;
	private Combo model;
	private Combo signal;
	private Group tlr;
	private Combo damping;
	private Button showProgress;
	private Button showResults;
	private Spinner tnotify;
	private Spinner spread;
	private Spinner tnotify2;
	private Spinner tauActiv;
	private Spinner tauReduce;

	public SimulationSettingsComposite(Composite parent, int style,
			Configuration conf) {
		super(parent, style);
		this.conf = conf;
		initialize();
	}

	private void initialize() {
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		setLayout(gl);
		GridData gd;
		
		// Title
		Label titleLabel = new Label(this, SWT.NONE);
		titleLabel.setText("Objective name");
		
		title = new Text(this, SWT.BORDER | SWT.SINGLE);
		title.setText(conf.title);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		title.setLayoutData(gd);
		title.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.title = title.getText();
			}
		});

		// First line
		Label lengthLabel = new Label(this, SWT.NONE);
		lengthLabel.setText("Simulation length (hours)");
		gd = new GridData();
		lengthLabel.setLayoutData(gd);

		length = new Spinner(this, SWT.BORDER);
		length.setDigits(1);
		length.setIncrement(10);
		length.setPageIncrement(100);
		length.setMinimum(0);
		length.setMaximum(Integer.MAX_VALUE);
		length.setSelection((int) (conf.SIMULATION_LENGTH * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		length.setLayoutData(gd);
		length.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.SIMULATION_LENGTH = (double) length.getSelection() / 10.0;
			}
		});

		dsc = new Group(this, SWT.NONE);
		dsc.setText("DSC params");
		gd = new GridData();
		gd.verticalSpan = 4;
		gd.horizontalIndent = 50;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		dsc.setLayoutData(gd);

		// Second line
		Label repetitionsLabel = new Label(this, SWT.NONE);
		repetitionsLabel.setText("Simulation repetitions");
		gd = new GridData();
		repetitionsLabel.setLayoutData(gd);

		repetitions = new Spinner(this, SWT.BORDER);
		repetitions.setIncrement(1);
		repetitions.setPageIncrement(10);
		repetitions.setMinimum(1);
		repetitions.setMaximum(Integer.MAX_VALUE);
		repetitions.setSelection(conf.repetitions);
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		repetitions.setLayoutData(gd);
		repetitions.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.repetitions = repetitions.getSelection();
			}
		});

		// Third line
		Label populationLabel = new Label(this, SWT.NONE);
		populationLabel.setText("Population size");
		gd = new GridData();
		populationLabel.setLayoutData(gd);

		population = new Spinner(this, SWT.BORDER);
		population.setIncrement(100);
		population.setPageIncrement(1000);
		population.setMinimum(0);
		population.setMaximum(Integer.MAX_VALUE);
		population.setSelection(conf.POPULATION_SIZE);
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		population.setLayoutData(gd);
		population.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.POPULATION_SIZE = population.getSelection();
			}
		});

		// Fourth line
		Label modelLabel = new Label(this, SWT.NONE);
		modelLabel.setText("Model type");
		gd = new GridData();
		modelLabel.setLayoutData(gd);

		model = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		model.add("Iterative");
		model.add("Linear");
		model.add("Compact-linear");
		switch (conf.model) {
		case ITERATIVE: {
			model.select(0);
			break;
		}
		case LINEAR: {
			model.select(1);
			break;
		}
		case COMPACT_LINEAR: {
			model.select(2);
			break;
		}
		}
		gd = new GridData();
		gd.widthHint = 85;
		gd.horizontalAlignment = SWT.RIGHT;
		model.setLayoutData(gd);
		model.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				switch(model.getSelectionIndex()) {
				case 0: {
					conf.model = MODEL.ITERATIVE;
					break;
				}
				case 1: {
					conf.model = MODEL.LINEAR;
					break;
				}
				case 2: {
					conf.model = MODEL.COMPACT_LINEAR;
					break;
				}
				}
			}
		});

		// Fifth line
		Label signalLabel = new Label(this, SWT.NONE);
		signalLabel.setText("Signal type");
		gd = new GridData();
		signalLabel.setLayoutData(gd);

		signal = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		signal.add("None");
		signal.add("DSC (load)");
		signal.add("DSC (unload)");
		signal.add("TLR");
		switch (conf.strategy) {
		case NONE: {
			signal.select(0);
			break;
		}
		case DSC: {
			if (conf.direct_doUnload) {
				signal.select(2);
			} else {
				signal.select(1);
			}
			break;
		}
		case TLR: {
			signal.select(3);
			break;
		}
		}
		gd = new GridData();
		gd.widthHint = 85;
		gd.horizontalAlignment = SWT.RIGHT;
		signal.setLayoutData(gd);
		signal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				switch(signal.getSelectionIndex()) {
				case 0: {
					conf.strategy = SIGNAL.NONE;
					break;
				}
				case 1: {
					conf.strategy = SIGNAL.DSC;
					conf.direct_doUnload = false;
					break;
				}
				case 2: {
					conf.strategy = SIGNAL.DSC;
					conf.direct_doUnload = true;
					break;
				}
				case 3: {
					conf.strategy = SIGNAL.TLR;
					break;
				}
				}
				setDscEnabled(conf.strategy == SIGNAL.DSC);
				setTlrEnabled(conf.strategy == SIGNAL.TLR);
			}
		});

		tlr = new Group(this, SWT.NONE);
		tlr.setText("TLR params");
		gd = new GridData();
		gd.verticalSpan = 4;
		gd.horizontalIndent = 50;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		tlr.setLayoutData(gd);

		// Sixth line
		Label dampingLabel = new Label(this, SWT.NONE);
		dampingLabel.setText("Damping type");
		gd = new GridData();
		dampingLabel.setLayoutData(gd);

		damping = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		damping.add("None");
		damping.add("Stateful (half)");
		damping.add("Stateful (full)");
		damping.add("Random");
		switch (conf.damping) {
		case NONE: {
			damping.select(0);
			break;
		}
		case STATEFUL_HALF: {
			damping.select(1);
			break;
		}
		case STATEFUL_FULL: {
			damping.select(2);
			break;
		}
		case RANDOM: {
			damping.select(3);
			break;
		}
		}
		gd = new GridData();
		gd.widthHint = 85;
		gd.horizontalAlignment = SWT.RIGHT;
		damping.setLayoutData(gd);
		damping.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				switch(damping.getSelectionIndex()) {
				case 0: {
					conf.damping = DAMPING.NONE;
					break;
				}
				case 1: {
					conf.damping = DAMPING.STATEFUL_HALF;
					break;
				}
				case 2: {
					conf.damping = DAMPING.STATEFUL_FULL;
					break;
				}
				case 3: {
					conf.damping = DAMPING.RANDOM;
					break;
				}
				}
			}
		});

		showProgress = new Button(this, SWT.CHECK | SWT.TRAIL
				| SWT.RIGHT_TO_LEFT);
		showProgress.setText("Show progress");
		showProgress.setSelection(conf.showProgress);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.heightHint = 20;
		showProgress.setLayoutData(gd);
		showProgress.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.showProgress = showProgress.getSelection();
			}
		});

		showResults = new Button(this, SWT.CHECK | SWT.TRAIL
				| SWT.RIGHT_TO_LEFT);
		showResults.setText("Show results");
		showResults.setSelection(conf.showResults);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.heightHint = 20;
		showResults.setLayoutData(gd);
		showResults.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.showResults = showResults.getSelection();
			}
		});

		// Group dsc
		GridLayout gl2 = new GridLayout();
		gl2.numColumns = 2;
		dsc.setLayout(gl2);

		Label tnotifyLabel = new Label(dsc, SWT.NONE);
		tnotifyLabel.setText("t_notify (minutes)");
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		tnotifyLabel.setLayoutData(gd);

		tnotify = new Spinner(dsc, SWT.BORDER);
		tnotify.setDigits(1);
		tnotify.setIncrement(10);
		tnotify.setPageIncrement(100);
		tnotify.setMinimum(0);
		tnotify.setMaximum(Integer.MAX_VALUE);
		tnotify.setSelection((int) (conf.direct_t_notify * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessVerticalSpace = true;
		tnotify.setLayoutData(gd);
		tnotify.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.direct_t_notify = (double) tnotify.getSelection() / 10.0;
			}
		});

		Label spreadLabel = new Label(dsc, SWT.NONE);
		spreadLabel.setText("spread (minutes)");
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		spreadLabel.setLayoutData(gd);

		spread = new Spinner(dsc, SWT.BORDER);
		spread.setDigits(1);
		spread.setIncrement(10);
		spread.setPageIncrement(100);
		spread.setMinimum(0);
		spread.setMaximum(Integer.MAX_VALUE);
		spread.setSelection((int) (conf.direct_spread * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessVerticalSpace = true;
		spread.setLayoutData(gd);
		spread.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.direct_spread = (double) spread.getSelection() / 10.0;
			}
		});

		// Group tlr
		GridLayout gl3 = new GridLayout();
		gl3.numColumns = 2;
		tlr.setLayout(gl3);

		Label tnotifyLabel2 = new Label(tlr, SWT.NONE);
		tnotifyLabel2.setText("t_notify (minutes)");
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		tnotifyLabel2.setLayoutData(gd);

		tnotify2 = new Spinner(tlr, SWT.BORDER);
		tnotify2.setDigits(1);
		tnotify2.setIncrement(10);
		tnotify2.setPageIncrement(100);
		tnotify2.setMinimum(0);
		tnotify2.setMaximum(Integer.MAX_VALUE);
		tnotify2.setSelection((int) (conf.timed_t_notify * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessVerticalSpace = true;
		tnotify2.setLayoutData(gd);
		tnotify2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.timed_t_notify = (double) tnotify2.getSelection() / 10.0;
			}
		});

		Label tauActivLabel = new Label(tlr, SWT.NONE);
		tauActivLabel.setText("tau_activ (minutes)");
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		tauActivLabel.setLayoutData(gd);

		tauActiv = new Spinner(tlr, SWT.BORDER);
		tauActiv.setDigits(1);
		tauActiv.setIncrement(10);
		tauActiv.setPageIncrement(100);
		tauActiv.setMinimum(0);
		tauActiv.setMaximum(Integer.MAX_VALUE);
		tauActiv.setSelection((int) (conf.timed_tau_activ * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessVerticalSpace = true;
		tauActiv.setLayoutData(gd);
		tauActiv.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.timed_tau_activ = (double) tauActiv.getSelection() / 10.0;
			}
		});

		Label tauReduceLabel = new Label(tlr, SWT.NONE);
		tauReduceLabel.setText("tau_reduce (minutes)");
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		tauReduceLabel.setLayoutData(gd);

		tauReduce = new Spinner(tlr, SWT.BORDER);
		tauReduce.setDigits(1);
		tauReduce.setIncrement(10);
		tauReduce.setPageIncrement(100);
		tauReduce.setMinimum(0);
		tauReduce.setMaximum(Integer.MAX_VALUE);
		tauReduce.setSelection((int) (conf.timed_tau_reduce * 10));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessVerticalSpace = true;
		tauReduce.setLayoutData(gd);
		tauReduce.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				conf.timed_tau_reduce = (double) tauReduce.getSelection() / 10.0;
			}
		});
		
		// Set groups enabled/disabled
		setDscEnabled(conf.strategy == SIGNAL.DSC);
		setTlrEnabled(conf.strategy == SIGNAL.TLR);
	}
	
	private void setDscEnabled(boolean enabled) {
		dsc.setEnabled(enabled);
		tnotify.setEnabled(enabled);
		spread.setEnabled(enabled);
	}
	
	private void setTlrEnabled(boolean enabled) {
		tlr.setEnabled(enabled);
		tnotify2.setEnabled(enabled);
		tauActiv.setEnabled(enabled);
		tauReduce.setEnabled(enabled);
	}
}