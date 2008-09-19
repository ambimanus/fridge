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

import simkit.random.LKSeeds;
import de.uniol.ui.desync.Configuration;
import de.uniol.ui.desync.Configuration.VARIATE;

public class PopulationSettingsComposite extends Composite {

	private Configuration conf;
	private Spinner active;
	private boolean enableModifyListeners = true;
	
	// Tstart
	private Button tcurrent_fixed;
	private Spinner tcurrent_fix;
	private Button tcurrent_uniform;
	private Combo tcurrent_uniform_seed;
	private Spinner tcurrent_min;
	private Spinner tcurrent_max;
	private Button tcurrent_normal;
	private Combo tcurrent_normal_seed;
	private Spinner tcurrent_mean;
	private Spinner tcurrent_sdev;
	// mc
	private Button mc_fixed;
	private Spinner mc_fix;
	private Button mc_uniform;
	private Combo mc_uniform_seed;
	private Spinner mc_min;
	private Spinner mc_max;
	private Button mc_normal;
	private Combo mc_normal_seed;
	private Spinner mc_mean;
	private Spinner mc_sdev;
	// A
	private Button A_fixed;
	private Spinner A_fix;
	private Button A_uniform;
	private Combo A_uniform_seed;
	private Spinner A_min;
	private Spinner A_max;
	private Button A_normal;
	private Combo A_normal_seed;
	private Spinner A_mean;
	private Spinner A_sdev;
	// TO
	private Button TO_fixed;
	private Spinner TO_fix;
	private Button TO_uniform;
	private Combo TO_uniform_seed;
	private Spinner TO_min;
	private Spinner TO_max;
	private Button TO_normal;
	private Combo TO_normal_seed;
	private Spinner TO_mean;
	private Spinner TO_sdev;
	// eta
	private Button eta_fixed;
	private Spinner eta_fix;
	private Button eta_uniform;
	private Combo eta_uniform_seed;
	private Spinner eta_min;
	private Spinner eta_max;
	private Button eta_normal;
	private Combo eta_normal_seed;
	private Spinner eta_mean;
	private Spinner eta_sdev;
	
	public PopulationSettingsComposite(Composite parent, int style,
			Configuration conf) {
		super(parent, style);
		this.conf = conf;
		initialize();
	}

	private void initialize() {
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		gl.horizontalSpacing = 15;
		gl.verticalSpacing = 15;
		setLayout(gl);
		GridData gd;
		
		// Activity setting
		Composite activeComp = new Composite(this, SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 3;
		activeComp.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = SWT.FILL;
		activeComp.setLayoutData(gd);
		
		Label activeLabel = new Label(activeComp, SWT.NONE);
		activeLabel
				.setText("Percentage of devices in state 'cooling' at simulation start:");
		gd = new GridData();
		activeLabel.setLayoutData(gd);
		
		active = new Spinner(activeComp, SWT.BORDER);
		active.setIncrement(1);
		active.setPageIncrement(10);
		active.setMinimum(0);
		active.setMaximum(100);
		active.setSelection((int) (conf.ACTIVE_AT_START_PROPABILITY * 100));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		active.setLayoutData(gd);
		active.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.ACTIVE_AT_START_PROPABILITY = (double) active
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label percent = new Label(activeComp, SWT.NONE);
		percent.setText("%");
		gd = new GridData();
		percent.setLayoutData(gd);
		
		// T_start
		createTStartGroup();
		createMcGroup();
		createAGroup();
		createTOGroup();
		createEtaGroup();
	}
	
	private void createTStartGroup() {
		Group t = new Group(this, SWT.NONE);
		t.setText("Starting temperature T_start");
		GridData gd = new GridData();
		t.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		t.setLayout(gl);
		
		tcurrent_fixed = new Button(t, SWT.RADIO);
		tcurrent_fixed.setText("Fixed");
		tcurrent_fixed.setSelection(conf.variate_Tcurrent == VARIATE.NONE);
		gd = new GridData();
		gd.horizontalSpan = 4;
		tcurrent_fixed.setLayoutData(gd);
		tcurrent_fixed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_Tcurrent = VARIATE.NONE;
			}
		});
		
		Label fixLabel = new Label(t, SWT.NONE);
		fixLabel.setText("Value");
		gd = new GridData();
		gd.horizontalIndent = 20;
		fixLabel.setLayoutData(gd);
		
		tcurrent_fix = new Spinner(t, SWT.BORDER);
		tcurrent_fix.setDigits(2);
		tcurrent_fix.setIncrement(10);
		tcurrent_fix.setPageIncrement(10);
		tcurrent_fix.setMinimum(Integer.MIN_VALUE);
		tcurrent_fix.setMaximum(Integer.MAX_VALUE);
		tcurrent_fix.setSelection((int) (conf.variate_Tcurrent_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		tcurrent_fix.setLayoutData(gd);
		tcurrent_fix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_Tcurrent_default = (double) tcurrent_fix
							.getSelection() / 100.0;
					tcurrent_mean.setSelection(tcurrent_fix.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		new Label(t, SWT.NONE);
		
		new Label(t, SWT.NONE);
		
		tcurrent_uniform = new Button(t, SWT.RADIO);
		tcurrent_uniform.setText("Uniform distribution");
		tcurrent_uniform.setSelection(conf.variate_Tcurrent == VARIATE.UNIFORM);
		gd = new GridData();
		gd.horizontalSpan = 4;
		tcurrent_uniform.setLayoutData(gd);
		tcurrent_uniform.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_Tcurrent = VARIATE.UNIFORM;
			}
		});
		
		Label tcurrent_uniform_seedLabel = new Label(t, SWT.NONE);
		tcurrent_uniform_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		tcurrent_uniform_seedLabel.setLayoutData(gd);
		
		tcurrent_uniform_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		int index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_Tcurrent_seed) {
				index = i;
			}
			tcurrent_uniform_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		tcurrent_uniform_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		tcurrent_uniform_seed.setLayoutData(gd);
		tcurrent_uniform_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				conf.variate_Tcurrent_seed = LKSeeds.ZRNG[tcurrent_uniform_seed
						.getSelectionIndex() + 1];
				tcurrent_normal_seed.select(tcurrent_uniform_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label minLabel = new Label(t, SWT.NONE);
		minLabel.setText("Min");
		gd = new GridData();
		gd.horizontalIndent = 20;
		minLabel.setLayoutData(gd);
		
		tcurrent_min = new Spinner(t, SWT.BORDER);
		tcurrent_min.setDigits(2);
		tcurrent_min.setIncrement(10);
		tcurrent_min.setPageIncrement(10);
		tcurrent_min.setMinimum(Integer.MIN_VALUE);
		tcurrent_min.setMaximum(Integer.MAX_VALUE);
		tcurrent_min.setSelection((int) (conf.variate_Tcurrent_min * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		tcurrent_min.setLayoutData(gd);
		tcurrent_min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_Tcurrent_min = (double) tcurrent_min
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label maxLabel = new Label(t, SWT.NONE);
		maxLabel.setText("Max");
		gd = new GridData();
		gd.horizontalIndent = 10;
		maxLabel.setLayoutData(gd);
		
		tcurrent_max = new Spinner(t, SWT.BORDER);
		tcurrent_max.setDigits(2);
		tcurrent_max.setIncrement(10);
		tcurrent_max.setPageIncrement(10);
		tcurrent_max.setMinimum(Integer.MIN_VALUE);
		tcurrent_max.setMaximum(Integer.MAX_VALUE);
		tcurrent_max.setSelection((int) (conf.variate_Tcurrent_max * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		tcurrent_max.setLayoutData(gd);
		tcurrent_max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_Tcurrent_max = (double) tcurrent_max
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		tcurrent_normal = new Button(t, SWT.RADIO);
		tcurrent_normal.setText("Normal distribution");
		tcurrent_normal.setSelection(conf.variate_Tcurrent == VARIATE.NORMAL);
		gd = new GridData();
		gd.horizontalSpan = 4;
		tcurrent_normal.setLayoutData(gd);
		tcurrent_normal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_Tcurrent = VARIATE.NORMAL;
			}
		});
		
		Label tcurrent_normal_seedLabel = new Label(t, SWT.NONE);
		tcurrent_normal_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		tcurrent_normal_seedLabel.setLayoutData(gd);
		
		tcurrent_normal_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_Tcurrent_seed) {
				index = i;
			}
			tcurrent_normal_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		tcurrent_normal_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		tcurrent_normal_seed.setLayoutData(gd);
		tcurrent_normal_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_Tcurrent_seed = LKSeeds.ZRNG[tcurrent_normal_seed
						.getSelectionIndex() + 1];
				tcurrent_uniform_seed.select(tcurrent_normal_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label meanLabel = new Label(t, SWT.NONE);
		meanLabel.setText("Mean");
		gd = new GridData();
		gd.horizontalIndent = 20;
		meanLabel.setLayoutData(gd);
		
		tcurrent_mean = new Spinner(t, SWT.BORDER);
		tcurrent_mean.setDigits(2);
		tcurrent_mean.setIncrement(10);
		tcurrent_mean.setPageIncrement(10);
		tcurrent_mean.setMinimum(Integer.MIN_VALUE);
		tcurrent_mean.setMaximum(Integer.MAX_VALUE);
		tcurrent_mean.setSelection((int) (conf.variate_Tcurrent_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		tcurrent_mean.setLayoutData(gd);
		tcurrent_mean.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_Tcurrent_default = (double) tcurrent_mean
							.getSelection() / 10.0;
					tcurrent_fix.setSelection(tcurrent_mean.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		Label sdevLabel = new Label(t, SWT.NONE);
		sdevLabel.setText("Std.dev.");
		gd = new GridData();
		gd.horizontalIndent = 10;
		sdevLabel.setLayoutData(gd);
		
		tcurrent_sdev = new Spinner(t, SWT.BORDER);
		tcurrent_sdev.setDigits(2);
		tcurrent_sdev.setIncrement(10);
		tcurrent_sdev.setPageIncrement(10);
		tcurrent_sdev.setMinimum(Integer.MIN_VALUE);
		tcurrent_sdev.setMaximum(Integer.MAX_VALUE);
		tcurrent_sdev.setSelection((int) (conf.variate_Tcurrent_sdev * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		tcurrent_sdev.setLayoutData(gd);
		tcurrent_sdev.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_Tcurrent_sdev = (double) tcurrent_sdev
							.getSelection() / 10.0;
					enableModifyListeners = true;
				}
			}
		});
	}
	
	private void createMcGroup() {
		Group t = new Group(this, SWT.NONE);
		t.setText("Thermal mass m_c");
		GridData gd = new GridData();
		t.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		t.setLayout(gl);
		
		mc_fixed = new Button(t, SWT.RADIO);
		mc_fixed.setText("Fixed");
		mc_fixed.setSelection(conf.variate_mc == VARIATE.NONE);
		gd = new GridData();
		gd.horizontalSpan = 4;
		mc_fixed.setLayoutData(gd);
		mc_fixed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NONE;
			}
		});
		
		Label fixLabel = new Label(t, SWT.NONE);
		fixLabel.setText("Value");
		gd = new GridData();
		gd.horizontalIndent = 20;
		fixLabel.setLayoutData(gd);
		
		mc_fix = new Spinner(t, SWT.BORDER);
		mc_fix.setDigits(2);
		mc_fix.setIncrement(10);
		mc_fix.setPageIncrement(10);
		mc_fix.setMinimum(Integer.MIN_VALUE);
		mc_fix.setMaximum(Integer.MAX_VALUE);
		mc_fix.setSelection((int) (conf.variate_mc_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		mc_fix.setLayoutData(gd);
		mc_fix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_mc_default = (double) mc_fix
							.getSelection() / 100.0;
					mc_mean.setSelection(mc_fix.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		new Label(t, SWT.NONE);
		
		new Label(t, SWT.NONE);
		
		mc_uniform = new Button(t, SWT.RADIO);
		mc_uniform.setText("Uniform distribution");
		mc_uniform.setSelection(conf.variate_mc == VARIATE.UNIFORM);
		gd = new GridData();
		gd.horizontalSpan = 4;
		mc_uniform.setLayoutData(gd);
		mc_uniform.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.UNIFORM;
			}
		});
		
		Label mc_uniform_seedLabel = new Label(t, SWT.NONE);
		mc_uniform_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		mc_uniform_seedLabel.setLayoutData(gd);
		
		mc_uniform_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		int index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_mc_seed) {
				index = i;
			}
			mc_uniform_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		mc_uniform_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		mc_uniform_seed.setLayoutData(gd);
		mc_uniform_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc_seed = LKSeeds.ZRNG[mc_uniform_seed
						.getSelectionIndex() + 1];
				mc_normal_seed.select(mc_uniform_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label minLabel = new Label(t, SWT.NONE);
		minLabel.setText("Min");
		gd = new GridData();
		gd.horizontalIndent = 20;
		minLabel.setLayoutData(gd);
		
		mc_min = new Spinner(t, SWT.BORDER);
		mc_min.setDigits(2);
		mc_min.setIncrement(10);
		mc_min.setPageIncrement(10);
		mc_min.setMinimum(Integer.MIN_VALUE);
		mc_min.setMaximum(Integer.MAX_VALUE);
		mc_min.setSelection((int) (conf.variate_mc_min * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		mc_min.setLayoutData(gd);
		mc_min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_mc_min = (double) mc_min
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label maxLabel = new Label(t, SWT.NONE);
		maxLabel.setText("Max");
		gd = new GridData();
		gd.horizontalIndent = 10;
		maxLabel.setLayoutData(gd);
		
		mc_max = new Spinner(t, SWT.BORDER);
		mc_max.setDigits(2);
		mc_max.setIncrement(10);
		mc_max.setPageIncrement(10);
		mc_max.setMinimum(Integer.MIN_VALUE);
		mc_max.setMaximum(Integer.MAX_VALUE);
		mc_max.setSelection((int) (conf.variate_mc_max * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		mc_max.setLayoutData(gd);
		mc_max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_mc_max = (double) mc_max
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		mc_normal = new Button(t, SWT.RADIO);
		mc_normal.setText("Normal distribution");
		mc_normal.setSelection(conf.variate_mc == VARIATE.NORMAL);
		gd = new GridData();
		gd.horizontalSpan = 4;
		mc_normal.setLayoutData(gd);
		mc_normal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NORMAL;
			}
		});
		
		Label mc_normal_seedLabel = new Label(t, SWT.NONE);
		mc_normal_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		mc_normal_seedLabel.setLayoutData(gd);
		
		mc_normal_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_mc_seed) {
				index = i;
			}
			mc_normal_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		mc_normal_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		mc_normal_seed.setLayoutData(gd);
		mc_normal_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc_seed = LKSeeds.ZRNG[mc_normal_seed
						.getSelectionIndex() + 1];
				mc_uniform_seed.select(mc_normal_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label meanLabel = new Label(t, SWT.NONE);
		meanLabel.setText("Mean");
		gd = new GridData();
		gd.horizontalIndent = 20;
		meanLabel.setLayoutData(gd);
		
		mc_mean = new Spinner(t, SWT.BORDER);
		mc_mean.setDigits(2);
		mc_mean.setIncrement(10);
		mc_mean.setPageIncrement(10);
		mc_mean.setMinimum(Integer.MIN_VALUE);
		mc_mean.setMaximum(Integer.MAX_VALUE);
		mc_mean.setSelection((int) (conf.variate_mc_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		mc_mean.setLayoutData(gd);
		mc_mean.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_mc_default = (double) mc_mean
							.getSelection() / 100.0;
					mc_fix.setSelection(mc_mean.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		Label sdevLabel = new Label(t, SWT.NONE);
		sdevLabel.setText("Std.dev.");
		gd = new GridData();
		gd.horizontalIndent = 10;
		sdevLabel.setLayoutData(gd);
		
		mc_sdev = new Spinner(t, SWT.BORDER);
		mc_sdev.setDigits(2);
		mc_sdev.setIncrement(10);
		mc_sdev.setPageIncrement(10);
		mc_sdev.setMinimum(Integer.MIN_VALUE);
		mc_sdev.setMaximum(Integer.MAX_VALUE);
		mc_sdev.setSelection((int) (conf.variate_mc_sdev * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		mc_sdev.setLayoutData(gd);
		mc_sdev.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_mc_sdev = (double) mc_sdev
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
	}
	
	private void createAGroup() {
		Group t = new Group(this, SWT.NONE);
		t.setText("Insulation A");
		GridData gd = new GridData();
		t.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		t.setLayout(gl);
		
		A_fixed = new Button(t, SWT.RADIO);
		A_fixed.setText("Fixed");
		A_fixed.setSelection(conf.variate_mc == VARIATE.NONE);
		gd = new GridData();
		gd.horizontalSpan = 4;
		A_fixed.setLayoutData(gd);
		A_fixed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NONE;
			}
		});
		
		Label fixLabel = new Label(t, SWT.NONE);
		fixLabel.setText("Value");
		gd = new GridData();
		gd.horizontalIndent = 20;
		fixLabel.setLayoutData(gd);
		
		A_fix = new Spinner(t, SWT.BORDER);
		A_fix.setDigits(2);
		A_fix.setIncrement(10);
		A_fix.setPageIncrement(10);
		A_fix.setMinimum(Integer.MIN_VALUE);
		A_fix.setMaximum(Integer.MAX_VALUE);
		A_fix.setSelection((int) (conf.variate_A_default * 10.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		A_fix.setLayoutData(gd);
		A_fix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_A_default = (double) A_fix
							.getSelection() / 100.0;
					A_mean.setSelection(A_fix.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		new Label(t, SWT.NONE);
		
		new Label(t, SWT.NONE);
		
		A_uniform = new Button(t, SWT.RADIO);
		A_uniform.setText("Uniform distribution");
		A_uniform.setSelection(conf.variate_mc == VARIATE.UNIFORM);
		gd = new GridData();
		gd.horizontalSpan = 4;
		A_uniform.setLayoutData(gd);
		A_uniform.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.UNIFORM;
			}
		});
		
		Label A_uniform_seedLabel = new Label(t, SWT.NONE);
		A_uniform_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		A_uniform_seedLabel.setLayoutData(gd);
		
		A_uniform_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		int index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_A_seed) {
				index = i;
			}
			A_uniform_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		A_uniform_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		A_uniform_seed.setLayoutData(gd);
		A_uniform_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				conf.variate_A_seed = LKSeeds.ZRNG[A_uniform_seed
						.getSelectionIndex() + 1];
				A_normal_seed.select(A_uniform_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label minLabel = new Label(t, SWT.NONE);
		minLabel.setText("Min");
		gd = new GridData();
		gd.horizontalIndent = 20;
		minLabel.setLayoutData(gd);
		
		A_min = new Spinner(t, SWT.BORDER);
		A_min.setDigits(2);
		A_min.setIncrement(10);
		A_min.setPageIncrement(10);
		A_min.setMinimum(Integer.MIN_VALUE);
		A_min.setMaximum(Integer.MAX_VALUE);
		A_min.setSelection((int) (conf.variate_A_min * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		A_min.setLayoutData(gd);
		A_min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_A_min = (double) A_min
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label maxLabel = new Label(t, SWT.NONE);
		maxLabel.setText("Max");
		gd = new GridData();
		gd.horizontalIndent = 10;
		maxLabel.setLayoutData(gd);
		
		A_max = new Spinner(t, SWT.BORDER);
		A_max.setDigits(2);
		A_max.setIncrement(10);
		A_max.setPageIncrement(10);
		A_max.setMinimum(Integer.MIN_VALUE);
		A_max.setMaximum(Integer.MAX_VALUE);
		A_max.setSelection((int) (conf.variate_A_max * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		A_max.setLayoutData(gd);
		A_max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_A_max = (double) A_max
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		A_normal = new Button(t, SWT.RADIO);
		A_normal.setText("Normal distribution");
		A_normal.setSelection(conf.variate_mc == VARIATE.NORMAL);
		gd = new GridData();
		gd.horizontalSpan = 4;
		A_normal.setLayoutData(gd);
		A_normal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NORMAL;
			}
		});
		
		Label A_normal_seedLabel = new Label(t, SWT.NONE);
		A_normal_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		A_normal_seedLabel.setLayoutData(gd);
		
		A_normal_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_A_seed) {
				index = i;
			}
			A_normal_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		A_normal_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		A_normal_seed.setLayoutData(gd);
		A_normal_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_A_seed = LKSeeds.ZRNG[A_normal_seed
						.getSelectionIndex() + 1];
				A_uniform_seed.select(A_normal_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label meanLabel = new Label(t, SWT.NONE);
		meanLabel.setText("Mean");
		gd = new GridData();
		gd.horizontalIndent = 20;
		meanLabel.setLayoutData(gd);
		
		A_mean = new Spinner(t, SWT.BORDER);
		A_mean.setDigits(2);
		A_mean.setIncrement(10);
		A_mean.setPageIncrement(10);
		A_mean.setMinimum(Integer.MIN_VALUE);
		A_mean.setMaximum(Integer.MAX_VALUE);
		A_mean.setSelection((int) (conf.variate_A_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		A_mean.setLayoutData(gd);
		A_mean.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_A_default = (double) A_mean
							.getSelection() / 100.0;
					A_fix.setSelection(A_mean.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		Label sdevLabel = new Label(t, SWT.NONE);
		sdevLabel.setText("Std.dev.");
		gd = new GridData();
		gd.horizontalIndent = 10;
		sdevLabel.setLayoutData(gd);
		
		A_sdev = new Spinner(t, SWT.BORDER);
		A_sdev.setDigits(2);
		A_sdev.setIncrement(10);
		A_sdev.setPageIncrement(10);
		A_sdev.setMinimum(Integer.MIN_VALUE);
		A_sdev.setMaximum(Integer.MAX_VALUE);
		A_sdev.setSelection((int) (conf.variate_A_sdev * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		A_sdev.setLayoutData(gd);
		A_sdev.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_A_sdev = (double) A_sdev
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
	}
	
	private void createTOGroup() {
		Group t = new Group(this, SWT.NONE);
		t.setText("Surrounding temperature T^O");
		GridData gd = new GridData();
		t.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		t.setLayout(gl);
		
		TO_fixed = new Button(t, SWT.RADIO);
		TO_fixed.setText("Fixed");
		TO_fixed.setSelection(conf.variate_mc == VARIATE.NONE);
		gd = new GridData();
		gd.horizontalSpan = 4;
		TO_fixed.setLayoutData(gd);
		TO_fixed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NONE;
			}
		});
		
		Label fixLabel = new Label(t, SWT.NONE);
		fixLabel.setText("Value");
		gd = new GridData();
		gd.horizontalIndent = 20;
		fixLabel.setLayoutData(gd);
		
		TO_fix = new Spinner(t, SWT.BORDER);
		TO_fix.setDigits(2);
		TO_fix.setIncrement(10);
		TO_fix.setPageIncrement(10);
		TO_fix.setMinimum(Integer.MIN_VALUE);
		TO_fix.setMaximum(Integer.MAX_VALUE);
		TO_fix.setSelection((int) (conf.variate_TO_default * 10.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		TO_fix.setLayoutData(gd);
		TO_fix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_TO_default = (double) TO_fix
							.getSelection() / 100.0;
					TO_mean.setSelection(TO_fix.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		new Label(t, SWT.NONE);
		
		new Label(t, SWT.NONE);
		
		TO_uniform = new Button(t, SWT.RADIO);
		TO_uniform.setText("Uniform distribution");
		TO_uniform.setSelection(conf.variate_mc == VARIATE.UNIFORM);
		gd = new GridData();
		gd.horizontalSpan = 4;
		TO_uniform.setLayoutData(gd);
		TO_uniform.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.UNIFORM;
			}
		});
		
		Label TO_uniform_seedLabel = new Label(t, SWT.NONE);
		TO_uniform_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		TO_uniform_seedLabel.setLayoutData(gd);
		
		TO_uniform_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		int index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_TO_seed) {
				index = i;
			}
			TO_uniform_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		TO_uniform_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		TO_uniform_seed.setLayoutData(gd);
		TO_uniform_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				conf.variate_TO_seed = LKSeeds.ZRNG[TO_uniform_seed
						.getSelectionIndex() + 1];
				TO_normal_seed.select(TO_uniform_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label minLabel = new Label(t, SWT.NONE);
		minLabel.setText("Min");
		gd = new GridData();
		gd.horizontalIndent = 20;
		minLabel.setLayoutData(gd);
		
		TO_min = new Spinner(t, SWT.BORDER);
		TO_min.setDigits(2);
		TO_min.setIncrement(10);
		TO_min.setPageIncrement(10);
		TO_min.setMinimum(Integer.MIN_VALUE);
		TO_min.setMaximum(Integer.MAX_VALUE);
		TO_min.setSelection((int) (conf.variate_TO_min * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		TO_min.setLayoutData(gd);
		TO_min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_TO_min = (double) TO_min
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label maxLabel = new Label(t, SWT.NONE);
		maxLabel.setText("Max");
		gd = new GridData();
		gd.horizontalIndent = 10;
		maxLabel.setLayoutData(gd);
		
		TO_max = new Spinner(t, SWT.BORDER);
		TO_max.setDigits(2);
		TO_max.setIncrement(10);
		TO_max.setPageIncrement(10);
		TO_max.setMinimum(Integer.MIN_VALUE);
		TO_max.setMaximum(Integer.MAX_VALUE);
		TO_max.setSelection((int) (conf.variate_TO_max * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		TO_max.setLayoutData(gd);
		TO_max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_TO_max = (double) TO_max
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		TO_normal = new Button(t, SWT.RADIO);
		TO_normal.setText("Normal distribution");
		TO_normal.setSelection(conf.variate_mc == VARIATE.NORMAL);
		gd = new GridData();
		gd.horizontalSpan = 4;
		TO_normal.setLayoutData(gd);
		TO_normal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NORMAL;
			}
		});
		
		Label TO_normal_seedLabel = new Label(t, SWT.NONE);
		TO_normal_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		TO_normal_seedLabel.setLayoutData(gd);
		
		TO_normal_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_TO_seed) {
				index = i;
			}
			TO_normal_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		TO_normal_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		TO_normal_seed.setLayoutData(gd);
		TO_normal_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_TO_seed = LKSeeds.ZRNG[TO_normal_seed
						.getSelectionIndex() + 1];
				TO_uniform_seed.select(TO_normal_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label meanLabel = new Label(t, SWT.NONE);
		meanLabel.setText("Mean");
		gd = new GridData();
		gd.horizontalIndent = 20;
		meanLabel.setLayoutData(gd);
		
		TO_mean = new Spinner(t, SWT.BORDER);
		TO_mean.setDigits(2);
		TO_mean.setIncrement(10);
		TO_mean.setPageIncrement(10);
		TO_mean.setMinimum(Integer.MIN_VALUE);
		TO_mean.setMaximum(Integer.MAX_VALUE);
		TO_mean.setSelection((int) (conf.variate_TO_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		TO_mean.setLayoutData(gd);
		TO_mean.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_TO_default = (double) TO_mean
							.getSelection() / 100.0;
					TO_fix.setSelection(TO_mean.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		Label sdevLabel = new Label(t, SWT.NONE);
		sdevLabel.setText("Std.dev.");
		gd = new GridData();
		gd.horizontalIndent = 10;
		sdevLabel.setLayoutData(gd);
		
		TO_sdev = new Spinner(t, SWT.BORDER);
		TO_sdev.setDigits(2);
		TO_sdev.setIncrement(10);
		TO_sdev.setPageIncrement(10);
		TO_sdev.setMinimum(Integer.MIN_VALUE);
		TO_sdev.setMaximum(Integer.MAX_VALUE);
		TO_sdev.setSelection((int) (conf.variate_TO_sdev * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		TO_sdev.setLayoutData(gd);
		TO_sdev.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_TO_sdev = (double) TO_sdev
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
	}
	
	private void createEtaGroup() {
		Group t = new Group(this, SWT.NONE);
		t.setText("Efficiency eta");
		GridData gd = new GridData();
		t.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		t.setLayout(gl);
		
		eta_fixed = new Button(t, SWT.RADIO);
		eta_fixed.setText("Fixed");
		eta_fixed.setSelection(conf.variate_mc == VARIATE.NONE);
		gd = new GridData();
		gd.horizontalSpan = 4;
		eta_fixed.setLayoutData(gd);
		eta_fixed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NONE;
			}
		});
		
		Label fixLabel = new Label(t, SWT.NONE);
		fixLabel.setText("Value");
		gd = new GridData();
		gd.horizontalIndent = 20;
		fixLabel.setLayoutData(gd);
		
		eta_fix = new Spinner(t, SWT.BORDER);
		eta_fix.setDigits(2);
		eta_fix.setIncrement(10);
		eta_fix.setPageIncrement(10);
		eta_fix.setMinimum(Integer.MIN_VALUE);
		eta_fix.setMaximum(Integer.MAX_VALUE);
		eta_fix.setSelection((int) (conf.variate_eta_default * 10.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		eta_fix.setLayoutData(gd);
		eta_fix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_eta_default = (double) eta_fix
							.getSelection() / 100.0;
					eta_mean.setSelection(eta_fix.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		new Label(t, SWT.NONE);
		
		new Label(t, SWT.NONE);
		
		eta_uniform = new Button(t, SWT.RADIO);
		eta_uniform.setText("Uniform distribution");
		eta_uniform.setSelection(conf.variate_mc == VARIATE.UNIFORM);
		gd = new GridData();
		gd.horizontalSpan = 4;
		eta_uniform.setLayoutData(gd);
		eta_uniform.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.UNIFORM;
			}
		});
		
		Label eta_uniform_seedLabel = new Label(t, SWT.NONE);
		eta_uniform_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		eta_uniform_seedLabel.setLayoutData(gd);
		
		eta_uniform_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		int index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_eta_seed) {
				index = i;
			}
			eta_uniform_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		eta_uniform_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		eta_uniform_seed.setLayoutData(gd);
		eta_uniform_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				conf.variate_eta_seed = LKSeeds.ZRNG[eta_uniform_seed
						.getSelectionIndex() + 1];
				eta_normal_seed.select(eta_uniform_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label minLabel = new Label(t, SWT.NONE);
		minLabel.setText("Min");
		gd = new GridData();
		gd.horizontalIndent = 20;
		minLabel.setLayoutData(gd);
		
		eta_min = new Spinner(t, SWT.BORDER);
		eta_min.setDigits(2);
		eta_min.setIncrement(10);
		eta_min.setPageIncrement(10);
		eta_min.setMinimum(Integer.MIN_VALUE);
		eta_min.setMaximum(Integer.MAX_VALUE);
		eta_min.setSelection((int) (conf.variate_eta_min * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		eta_min.setLayoutData(gd);
		eta_min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_eta_min = (double) eta_min
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		Label maxLabel = new Label(t, SWT.NONE);
		maxLabel.setText("Max");
		gd = new GridData();
		gd.horizontalIndent = 10;
		maxLabel.setLayoutData(gd);
		
		eta_max = new Spinner(t, SWT.BORDER);
		eta_max.setDigits(2);
		eta_max.setIncrement(10);
		eta_max.setPageIncrement(10);
		eta_max.setMinimum(Integer.MIN_VALUE);
		eta_max.setMaximum(Integer.MAX_VALUE);
		eta_max.setSelection((int) (conf.variate_eta_max * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		eta_max.setLayoutData(gd);
		eta_max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_eta_max = (double) eta_max
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
		
		eta_normal = new Button(t, SWT.RADIO);
		eta_normal.setText("Normal distribution");
		eta_normal.setSelection(conf.variate_mc == VARIATE.NORMAL);
		gd = new GridData();
		gd.horizontalSpan = 4;
		eta_normal.setLayoutData(gd);
		eta_normal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_mc = VARIATE.NORMAL;
			}
		});
		
		Label eta_normal_seedLabel = new Label(t, SWT.NONE);
		eta_normal_seedLabel.setText("Seed");
		gd = new GridData();
		gd.horizontalIndent = 20;
		eta_normal_seedLabel.setLayoutData(gd);
		
		eta_normal_seed = new Combo(t, SWT.DROP_DOWN | SWT.READ_ONLY);
		index = -1;
		for (int i = 1; i < LKSeeds.ZRNG.length; i++) {
			if (LKSeeds.ZRNG[i] == conf.variate_eta_seed) {
				index = i;
			}
			eta_normal_seed.add("#" + i + " (" + LKSeeds.ZRNG[i] + ")");
		}
		eta_normal_seed.select(index - 1);
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		eta_normal_seed.setLayoutData(gd);
		eta_normal_seed.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				conf.variate_eta_seed = LKSeeds.ZRNG[eta_normal_seed
						.getSelectionIndex() + 1];
				eta_uniform_seed.select(eta_normal_seed
						.getSelectionIndex());
			}
		});
		
		new Label(t, SWT.NONE);
		
		Label meanLabel = new Label(t, SWT.NONE);
		meanLabel.setText("Mean");
		gd = new GridData();
		gd.horizontalIndent = 20;
		meanLabel.setLayoutData(gd);
		
		eta_mean = new Spinner(t, SWT.BORDER);
		eta_mean.setDigits(2);
		eta_mean.setIncrement(10);
		eta_mean.setPageIncrement(10);
		eta_mean.setMinimum(Integer.MIN_VALUE);
		eta_mean.setMaximum(Integer.MAX_VALUE);
		eta_mean.setSelection((int) (conf.variate_eta_default * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.RIGHT;
		eta_mean.setLayoutData(gd);
		eta_mean.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_eta_default = (double) eta_mean
							.getSelection() / 100.0;
					eta_fix.setSelection(eta_mean.getSelection());
					enableModifyListeners = true;
				}
			}
		});
		
		Label sdevLabel = new Label(t, SWT.NONE);
		sdevLabel.setText("Std.dev.");
		gd = new GridData();
		gd.horizontalIndent = 10;
		sdevLabel.setLayoutData(gd);
		
		eta_sdev = new Spinner(t, SWT.BORDER);
		eta_sdev.setDigits(2);
		eta_sdev.setIncrement(10);
		eta_sdev.setPageIncrement(10);
		eta_sdev.setMinimum(Integer.MIN_VALUE);
		eta_sdev.setMaximum(Integer.MAX_VALUE);
		eta_sdev.setSelection((int) (conf.variate_eta_sdev * 100.0));
		gd = new GridData();
		gd.widthHint = 35;
		gd.horizontalAlignment = SWT.LEFT;
		eta_sdev.setLayoutData(gd);
		eta_sdev.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (enableModifyListeners) {
					enableModifyListeners = false;
					conf.variate_eta_sdev = (double) eta_sdev
							.getSelection() / 100.0;
					enableModifyListeners = true;
				}
			}
		});
	}
}