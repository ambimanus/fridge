/*
 * AdaptiveFridge Copyright (C) 2008 Christian Hinrichs
 * 
 * AdaptiveFridge is copyright under the GNU General Public License.
 * 
 * This file is part of AdaptiveFridge.
 * 
 * AdaptiveFridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AdaptiveFridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AdaptiveFridge.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uniol.ui.desync.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.SystemColor;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import simkit.EventList;
import simkit.SimEntity;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.ui.LineChartDialog;
import de.uniol.ui.desync.ui.ProgressComposite;
import de.uniol.ui.desync.ui.StepChartDialog;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.ProgressListener;
import de.uniol.ui.desync.util.collectors.AbstractCollector;
import de.uniol.ui.desync.util.collectors.TimeseriesCollector;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

/**
 * This class controls the actual simulation run. It gets the simulated entities
 * as input, provides them with statistical collectors if desired and is able to
 * present the collected data visually after the simulation finished.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */
public class Simulation {

	/** The underlying FEL */
	private MessagingEventList el;
	/** The simulated fridge entities */
	private ArrayList<? extends AbstractFridge> fridges;

	/* Statistics collectors */
	private ArrayList<TimeseriesCollector> temps = new ArrayList<TimeseriesCollector>();
	private ArrayList<TimeseriesCollector> loads = new ArrayList<TimeseriesCollector>();
	private TimeseriesMultiMeanCollector meanTemp;
	private TimeseriesMultiMeanCollector meanLoad;
	
	/** defines whether to collect mean temperature changes */
	private boolean collectMeanTemperature = true;
	/** defines whether to collect mean load changes */
	private boolean collectMeanLoad = true;
	/** defines whether to collect all temperature changes */
	private boolean collectAllTemperature = false;
	/** defines whether to collect all load changes */
	private boolean collectAllLoad = false;
	/** the color which will be used to highlight the mean curves */
	private Color highlightColor = SystemColor.BLACK;
	
	/**
	 * Creates a new simulation using the specified {@link EventList} and the
	 * given population of {@link SimEntity}s.
	 * 
	 * @param el
	 * @param fridges
	 */
	public Simulation(MessagingEventList el,
			ArrayList<? extends AbstractFridge> fridges) {
		this.el = el;
		this.fridges = fridges;
		if (fridges.size() > 1 && fridges.size() < 10) {
			collectAllLoad = true;
			collectAllTemperature = true;
		}
	}

	/**
	 * If desired, provide the entities with statistical collectors.
	 */
	private void initStatistics() {
		if (collectMeanTemperature) {
			meanTemp = new TimeseriesMultiMeanCollector(el, "mean temperature");
		}
		if (collectMeanLoad) {
			meanLoad = new TimeseriesMultiMeanCollector(el, "mean load");
		}
		for (AbstractFridge f : fridges) {
			if (collectMeanTemperature) {
				meanTemp.addEntity(f, AbstractFridge.PROP_TEMPERATURE);
			}
			if (collectAllTemperature) {
				TimeseriesCollector t = new TimeseriesCollector(
						"Temperature of " + f.getName(), f,
						IterativeFridge.PROP_TEMPERATURE);
				temps.add(t);
			}
			if (collectMeanLoad) {
				meanLoad.addEntity(f, AbstractFridge.PROP_LOAD);
			}
			if (collectAllLoad) {
				TimeseriesCollector l = new TimeseriesCollector("Load of "
						+ f.getName(), f, AbstractFridge.PROP_LOAD);
				loads.add(l);
			}
		}
	}

	/**
	 * Perform the simulation until the simTime reaches <code>end</code>. If
	 * showProgress is true, a Progressbar will be shown until the simulation
	 * finishes. Else, information about the progress will be printed to the
	 * console. In either case this method blocks until the simulation has
	 * finished.
	 * 
	 * @param end
	 */
	public void simulate(double end, boolean showProgress, String title) {
		initStatistics();
		el.stopAtTime(end);
		el.reset();
		Thread sim = null;
		if (showProgress) {
			final ProgressComposite pc = new ProgressComposite();
			pc.setTitle(title);
			el.addPropertyChangeListener(MessagingEventList.PROP_SIMTIME,
					new ProgressListener(end) {
						protected void progressChanged(int progress) {
							pc.setProgress(progress);
						}
					});
			sim = new Thread() {
				public void run() {
					el.startSimulation();
				}
			};
			sim.start();
			pc.open();
			while (sim.isAlive()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		} else {
			sim = new Thread() {
				public void run() {
					el.startSimulation();
				}
			};
			sim.start();
			while (sim.isAlive()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
		if (collectMeanLoad) {
			// Add load observations at finish time
			meanLoad.addObservation(el.getStopTime(), meanLoad
					.getObservation(meanLoad.getSize() - 1)[1]);
		}
	}

	/**
	 * Displays the simulation results as line/step charts.
	 * 
	 * @param block
	 */
	public void showResults(boolean block) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Simulation results");
		int numberOfCharts = 0;
		if (collectMeanTemperature || collectAllTemperature) {
			// Temperature chart
			LineChartDialog lcd = new LineChartDialog(shell,
					"Temperature progress", "Time (h)", "Temperature (�C)",
					"min", "�C", 3.0, 8.0);
			if (collectMeanTemperature) {
				lcd.addSeries(meanTemp);
			}
			if (collectAllTemperature) {
				lcd.addAllSeries(temps);
			}
			if (collectMeanTemperature && collectAllTemperature) {
				lcd.setSeriesStroke(0, new BasicStroke(2.0f));
			}
//lcd.setSeriesWidth(0, 2.0f);
// TODO
			if (collectMeanTemperature || temps.size() == 1) {
				lcd.setSeriesColor(0, highlightColor);
			}
			lcd.create();
			numberOfCharts++;
		}
		if (collectMeanLoad || collectAllLoad) {
			// Load chart
			StepChartDialog scd = new StepChartDialog(shell, "Load progress",
					"Time (h)", "Load (W)", "min", "W", 0.0, 70.0);
			if (collectMeanLoad) {
				// Add series
				scd.addSeries(meanLoad);
			}
			if (collectAllLoad) {
				// Add load observations at finish time
				for (AbstractCollector col : loads) {
					if (col.getSize() != 0) {
						col.addObservation(el.getStopTime(), col.getObservation(col
								.getSize() - 1)[1]);
					}
				}
				// Add series
				scd.addAllSeries(loads);
			}
			if (collectMeanLoad && collectAllLoad) {
				scd.setSeriesStroke(0, new BasicStroke(2.0f));
			}
//scd.setSeriesWidth(0, 2.0f);
// TODO
			if (collectMeanLoad || loads.size() == 1) {
				scd.setSeriesColor(0, highlightColor);
			}
			scd.create();
			numberOfCharts++;
		}
		
		// Open shell
		shell.setSize(900, numberOfCharts * 400);
		shell.open();
		if (block) {
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}

	/**
	 * @return whether temperature values are being collected
	 */
	public boolean isCollectMeanTemperature() {
		return collectMeanTemperature;
	}

	/**
	 * Sets whether temperature values are being collected
	 * @param collectTemperature
	 */
	public void setCollectMeanTemperature(boolean collectTemperature) {
		this.collectMeanTemperature = collectTemperature;
	}

	/**
	 * @return whether load values are being collected
	 */
	public boolean isCollectMeanLoad() {
		return collectMeanLoad;
	}

	/**
	 * Sets whether load values are being collected
	 * @param collectLoad
	 */
	public void setCollectMeanLoad(boolean collectLoad) {
		this.collectMeanLoad = collectLoad;
	}
	
	/**
	 * @return the collectAllTemperature
	 */
	public boolean isCollectAllTemperature() {
		return collectAllTemperature;
	}

	/**
	 * @param collectAllTemperature the collectAllTemperature to set
	 */
	public void setCollectAllTemperature(boolean collectAllTemperature) {
		this.collectAllTemperature = collectAllTemperature;
	}

	/**
	 * @return the collectAllLoad
	 */
	public boolean isCollectAllLoad() {
		return collectAllLoad;
	}

	/**
	 * @param collectAllLoad the collectAllLoad to set
	 */
	public void setCollectAllLoad(boolean collectAllLoad) {
		this.collectAllLoad = collectAllLoad;
	}

	/**
	 * @return the highlightColor
	 */
	public Color getHighlightColor() {
		return highlightColor;
	}

	/**
	 * @param highlightColor the highlightColor to set
	 */
	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	/**
	 * @return a time-weighted stats object if load was collected, null
	 *         otherwise
	 */
	public SimpleStatsTimeVarying getLoadStats() {
		if (collectMeanLoad) {
			return meanLoad.getTimeVaryingStats();
		}
		return null;
	}
	
	/**
	 * @return a time-weighted stats object if temperature was collected, null
	 *         otherwise
	 */
	public SimpleStatsTimeVarying getTemperatureStats() {
		if (collectMeanTemperature) {
			return meanTemp.getTimeVaryingStats();
		}
		return null;
	}

	/**
	 * @return the meanTemp
	 */
	public TimeseriesMultiMeanCollector getMeanTemp() {
		return meanTemp;
	}

	/**
	 * @return the meanLoad
	 */
	public TimeseriesMultiMeanCollector getMeanLoad() {
		return meanLoad;
	}

	/**
	 * @return the time weighted load result values if load was collected, null
	 *         otherwise
	 */
	public double[][] getLoadResults() {
		if (collectMeanLoad) {
			return meanLoad.getResults();
		}
		return null;
	}

	/**
	 * @return the time weighted temperature result values if temperature was
	 *         collected, null otherwise
	 */
	public double[][] getTemperatureResults() {
		if (collectMeanTemperature) {
			return meanTemp.getResults();
		}
		return null;
	}
	
	/**
	 * Clears the collected statistic values.
	 */
	public void clearStats() {
		for (TimeseriesCollector col : temps) {
			col.clear();
		}
		if (meanTemp != null) {
			meanTemp.clear();
		}
		for (TimeseriesCollector col : loads) {
			col.clear();
		}
		if (meanLoad != null) {
			meanLoad.clear();
		}
	}
}