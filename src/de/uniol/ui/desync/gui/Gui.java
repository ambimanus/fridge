package de.uniol.ui.desync.gui;

import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import simkit.Schedule;
import simkit.random.LKSeeds;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.Configuration;
import de.uniol.ui.desync.model.Experiment;
import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ResultWriter;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

public class Gui {

	protected boolean allowSimulation = true;
	
	protected Shell shell;
	protected Table table;
	protected Button add;
	protected Button edit;
	protected Button remove;
	protected Button simulate;
	
	protected HashMap<TableItem, Configuration> objectives = new HashMap<TableItem, Configuration>();
	protected HashMap<Configuration, TableItem> items = new HashMap<Configuration, TableItem>();
	protected Configuration selectedConf = null;
	
	public static void main(String[] args) {
		Gui gui = new Gui();
		gui.open(true);
	}
	
	public Gui() {
		initShell();
		initControls();
	}

	protected void initShell() {
		Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("The adaptive fridge - a discrete-event system simulation (Chh, 2008)");
	}
	
	private void initControls() {
		Composite c = new Composite(shell, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		c.setLayout(gl);
		GridData gd;
		
		table = new Table(c, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				updateSelectedObjective();
			}
		});
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setAlignment(SWT.LEFT);
		column.setMoveable(false);
		column.setResizable(false);
		column.setText("Objectives");
		column.setWidth(200);
		gd = new GridData();
		gd.verticalSpan = 4;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		table.setLayoutData(gd);
		
		add = new Button(c, SWT.PUSH);
		add.setText("Add objective");
		add.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				ObjectiveDialog od = new ObjectiveDialog(shell,
						new Configuration());
				Configuration conf = od.open();
				addObjective(conf);
			}
		});
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.TOP;
		add.setLayoutData(gd);
		
		edit = new Button(c, SWT.PUSH);
		edit.setText("Edit objective");
		edit.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				if (selectedConf != null) {
					ObjectiveDialog od = new ObjectiveDialog(shell,
							selectedConf);
					Configuration conf = od.open();
					addObjective(conf);
				}
			}
		});
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.TOP;
		edit.setLayoutData(gd);
		
		remove = new Button(c, SWT.PUSH);
		remove.setText("Remove objective");
		remove.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				if (selectedConf != null) {
					removeObjective(selectedConf);
				}
			}
		});
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.TOP;
		remove.setLayoutData(gd);
		
		simulate = new Button(c, SWT.PUSH);
		simulate.setText("Start simulation");
		simulate.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				startSimulation();
			}
		});
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.BOTTOM;
		simulate.setLayoutData(gd);
		
		updateButtons();
	}
	
	private void updateSelectedObjective() {
		TableItem[] sel = table.getSelection();
		if (sel.length != 1) {
			selectedConf = null;
		} else {
			selectedConf = objectives.get(sel[0]);
		}
		updateButtons();
	}
	
	protected void updateButtons() {
		edit.setEnabled(selectedConf != null);
		remove.setEnabled(selectedConf != null);
		simulate.setEnabled(allowSimulation && items.size() > 0);
	}
	
	protected void addObjective(Configuration conf) {
		TableItem item = items.get(conf);
		if (item == null) {
			item = new TableItem(table, SWT.NONE);
			items.put(conf, item);
			objectives.put(item, conf);
		}
		item.setText(0, conf.title);
		updateSelectedObjective();
	}
	
	protected void removeObjective(Configuration conf) {
		TableItem item = items.get(conf);
		items.remove(conf);
		objectives.remove(item);
		if (item != null) {
			table.remove(table.indexOf(item));
		}
		updateSelectedObjective();
	}
	
	protected void startSimulation() {
		// Disable buttons
//		add.setEnabled(false);
//		edit.setEnabled(false);
//		remove.setEnabled(false);
		allowSimulation = false;
		simulate.setEnabled(false);
		
		/* Create results list */
		HashMap<String, double[][][]> results = new HashMap<String, double[][][]>();
		ArrayList<String> sortedKeys = new ArrayList<String>();
		long start = System.currentTimeMillis();
		
		Iterator<Configuration> it = new ArrayList<Configuration>(items
				.keySet()).iterator();
		int i = 0;
		while (it.hasNext()) {
			Configuration conf = it.next();
			sortedKeys.add(conf.title);
			results.put(conf.title, run(conf, i++));
		}

		/* Print status */
		long dur = System.currentTimeMillis() - start;
		long min = dur / 60000l;
		long sec = (dur % 60000l) / 1000l;
		MessageBox mb = new MessageBox(shell);
		mb.setText("Finish");
		mb.setMessage("All objectives finished in " + min + "m" + sec + "s");

		/* Show overall results */
		showResults(sortedKeys, results);
		
		// Enable buttons
//		add.setEnabled(true);
//		edit.setEnabled(true);
//		remove.setEnabled(true);
		allowSimulation = true;
		simulate.setEnabled(true);
		
		updateSelectedObjective();
	}
	
	public void open(boolean block) {
		shell.setSize(600, 400);
		shell.open();
		while(block && !shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}
	
	protected static double[][][] run(Configuration conf, int instance) {
		long start = System.currentTimeMillis();
		SimpleStatsTally sst = new SimpleStatsTally();
		HashMap<Configuration, TimeseriesMultiMeanCollector> temps = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		HashMap<Configuration, TimeseriesMultiMeanCollector> loads = new HashMap<Configuration, TimeseriesMultiMeanCollector>();
		// Perform experiment(s)
		for (int i = 1; i <= conf.repetitions; i++) {
			Configuration.distinct++;
			conf.showResults = false;
			if (i < 99) {
				conf.variate_Tcurrent_seed = getSeed(i);
				conf.variate_mc_seed = getSeed(i + 1);
				conf.variate_A_seed = getSeed(i + 2);
				conf.variate_TO_seed = getSeed(i + 3);
				conf.variate_eta_seed = getSeed(i + 4);
				conf.variate_qc_seed = getSeed(i + 5);
				conf.variate_qw_seed = getSeed(i + 6);
			} else {
				conf.variate_Tcurrent_seed = Math
						.round(Math.random() * 1000000000d);
				conf.variate_mc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_A_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_TO_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_eta_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_qc_seed = Math.round(Math.random() * 1000000000d);
				conf.variate_qw_seed = Math.round(Math.random() * 1000000000d);
			}

			// Get FEL
			int list = Schedule.addNewEventList(MessagingEventList.class);
			MessagingEventList el = (MessagingEventList) Schedule
					.getEventList(list);
			
			// Run
			Experiment exp = new Experiment(conf, instance, i - 1);
			exp.run(el, i == conf.repetitions);
			
			// Print current results
			System.out.println(exp.getName() + "(" + conf.SIMULATION_LENGTH
					+ "h) - " + exp.getSimulationTime() + "s");
			if (exp.getLoadStats() != null) {
				SimpleStatsTimeVarying sstv = exp.getLoadStats();
				System.out.println("\nLoad stats of current run:");
				System.out.println("\tn        = " + sstv.getCount());
				System.out.println("\tmin      = " + sstv.getMinObs());
				System.out.println("\tmax      = " + sstv.getMaxObs());
				System.out.println("\tmean     = " + sstv.getMean());
				System.out.println("\tvariance = " + sstv.getVariance());
				System.out.println("\tstd.dev  = " + sstv.getStandardDeviation());
				System.out.println();
				sst.newObservation(sstv.getMean());
			} else {
				System.out.println();
			}
			
			// Store current result values
			temps.put(conf, exp.getMeanTemp());
			loads.put(conf, exp.getMeanLoad());
			
//			double[][] res = exp.getMeanTemp().getResults();
//			System.out.println(Arrays.toString(res[0]).replaceAll(", ", "\n").replaceAll("\\.", ","));
//			System.out.println(Arrays.toString(res[1]).replaceAll(", ", "\n").replaceAll("\\.", ","));
			
			// Cleanup
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
			}
			el.reset();
			el.coldReset();
//			exp.clear();
//			SimEntityClean.coldReset();
			exp = null;
			System.gc();
		}
		
		// Print overall stats
		System.out.println("\nOverall load stats:");
		System.out.println("\tn        = " + sst.getCount());
		System.out.println("\tmin      = " + sst.getMinObs());
		System.out.println("\tmax      = " + sst.getMaxObs());
		System.out.println("\tmean     = " + sst.getMean());
		System.out.println("\tvariance = " + sst.getVariance());
		System.out.println("\tstd.dev  = " + sst.getStandardDeviation());

		// Calculate overall results
		double[][] tData = ResultWriter.convertToMilliseconds(ResultWriter
				.mean(ResultWriter.interpolate(temps, true)));
		double[][] lData = ResultWriter.convertToMilliseconds(ResultWriter
				.mean(ResultWriter.interpolate(loads, false)));
//		System.out.println(Arrays.toString(lData[0]).replaceAll(", ", "\n").replaceAll("\\.", ","));
//		System.out.println(Arrays.toString(lData[1]).replaceAll(", ", "\n").replaceAll("\\.", ","));
		
		// Print status
		long dur = System.currentTimeMillis() - start;
		long min = dur / 60000l;
		long sec = (dur % 60000l) / 1000l;
		System.out.println("\n" + conf.title + " finished in " + min + "m"
				+ sec + "s");
		
		// Return results
		return new double[][][] { tData, lData };
	}
	
	protected static long getSeed(int index) {
		if (index > 99) {
			index -= 99;
		}
		return LKSeeds.ZRNG[index];
	}
	
	protected static void showResults(ArrayList<String> keys,
			HashMap<String, double[][][]> results) {
		// Prepare window
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		// Prepare temperature chart
		LineChartDialog lcd = new LineChartDialog(shell,
				"Temperature progress", "Time (h)", "Temperature (°C)", "min",
				"°C", 3.0, 8.0);
		// Prepare load chart
		StepChartDialog scd = new StepChartDialog(shell, "Load progress",
				"Time (h)", "Load (W)", "min", "W", 0.0, 70.0);
		// Create series
		for (String key : keys) {
			double[][][] data = results.get(key);
			double[][] tData = data[0];
			double[][] lData = data[1];
			lcd.addSeries(key, tData);
			if (keys.size() == 1) {
				lcd.setSeriesColor(1, SystemColor.BLACK);
			}
			scd.addSeries(key, lData);
			if (keys.size() == 1) {
				scd.setSeriesColor(1, SystemColor.BLACK);
			}
		}
		// Finish charts
		lcd.create();
		scd.create();
		// Open shell
		shell.setSize(900, 800);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}