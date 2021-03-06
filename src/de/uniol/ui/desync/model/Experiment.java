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

import java.util.ArrayList;

import simkit.random.BernoulliVariate;
import simkit.random.Congruential;
import simkit.random.NormalVariate;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.controller.AbstractController;
import de.uniol.ui.desync.model.controller.BaseControllerCompactLinear;
import de.uniol.ui.desync.model.controller.BaseControllerIterative;
import de.uniol.ui.desync.model.controller.BaseControllerLinear;
import de.uniol.ui.desync.model.controller.DirectControllerCompactLinear;
import de.uniol.ui.desync.model.controller.DirectControllerIterative;
import de.uniol.ui.desync.model.controller.DirectControllerLinear;
import de.uniol.ui.desync.model.controller.TimedControllerCompactLinear;
import de.uniol.ui.desync.model.controller.TimedControllerIterative;
import de.uniol.ui.desync.model.controller.TimedControllerLinear;
import de.uniol.ui.desync.model.controller.extended.RandomizedDirectCompactLinear;
import de.uniol.ui.desync.model.controller.extended.RandomizedTimedCompactLinear;
import de.uniol.ui.desync.model.controller.extended.StatefulDirectCompactLinearFullWidth;
import de.uniol.ui.desync.model.controller.extended.StatefulDirectCompactLinearHalfWidth;
import de.uniol.ui.desync.model.controller.extended.StatefulTimedCompactLinear;
import de.uniol.ui.desync.model.fridges.AbstractFridge;
import de.uniol.ui.desync.model.fridges.IterativeFridge;
import de.uniol.ui.desync.model.fridges.LinearFridge;
import de.uniol.ui.desync.model.lamps.Lamp;
import de.uniol.ui.desync.model.signals.AbstractSignalPerformer;
import de.uniol.ui.desync.model.signals.SignalPerformerDirect;
import de.uniol.ui.desync.model.signals.SignalPerformerTimed;
import de.uniol.ui.desync.util.MessagingEventList;
import de.uniol.ui.desync.util.collectors.TimeseriesMultiMeanCollector;

/**
 * This class creates all entities and prepares and runs a simulation.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 */
public class Experiment {

	/** Static instance counter */
	private static int instances = 0;
	/** Static run counter */
	private int runs = 0;
	/** Name of this experiment */
	private String name;
	/** Configuration for this run */
	private Configuration conf;
	/** Performed simulation */
	private Simulation simulation;
	/** Time needed for the simulation */
	private double simulationTime = Double.NaN;

	public Experiment(Configuration conf, int instance, int run) {
		this.conf = conf;
		instances++;
//		name = "Experiment" + instance + "@run" + run;
		name = conf.title + "___run" + run;
	}

	/**
	 * Runs an experiment on the specified {@link MessagingEventList} based on
	 * the given {@link Configuration}. The EventList will be resetted after
	 * the simulation.
	 * 
	 * @param el
	 * @param blockResults
	 */
	public void run(MessagingEventList el, boolean blockResults) {
		runs++;
		
		// Create fridges
		ArrayList<AbstractFridge> fridges = createFridges(el.getID());
		if (conf.strategy != Configuration.SIGNAL.NONE) {
			// Create ControlCenter and Strategy
			new ControlCenter(el.getID(), fridges, createStrategy(el.getID()));
		}
		// Create lamps
		if (conf.includeLamps) {
			createLamps(el.getID(), fridges);
		}

		// Prepare simulation
		simulation = new Simulation(el, fridges);
//simulation.setCollectMeanLoad(false);
//simulation.setCollectMeanTemperature(false);
// TODO

		// Simulate
		double start = System.currentTimeMillis();
		simulation.simulate(conf.SIMULATION_LENGTH * 60.0, conf.showProgress,
				name);
		simulationTime = (System.currentTimeMillis() - start) / 1000.0;

		// Create charts
		if (conf.showResults) {
			simulation.showResults(blockResults);
		}
		
		// Cleanup
		fridges.clear();
	}

	/**
	 * Creates the lamp SimEntities for the specified fridges and with the given
	 * FEL id.
	 * 
	 * @param id
	 * @param fridges
	 * @return
	 */
	private ArrayList<Lamp> createLamps(int id,
			ArrayList<AbstractFridge> fridges) {
		ArrayList<Lamp> lamps = new ArrayList<Lamp>();
		UniformVariate uv = new UniformVariate();
		uv.setMinimum(5.0);
		uv.setMaximum(25.0);
		for (AbstractFridge fridge : fridges) {
			lamps.add(new Lamp(id, fridge));
		}
		return lamps;
	}

	/**
	 * Creates the fridges population using the runtime type defined in
	 * {@link Configuration#model}.
	 * 
	 * @param list
	 *            the underlying FEL id
	 * @return the population
	 */
	private ArrayList<AbstractFridge> createFridges(int list) {
		AbstractFridge.resetInstanceCounter();
		// Create distinct random variates
		RandomVariate tVariate = null;
		switch (conf.variate_Tcurrent) {
		case UNIFORM: {
			tVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_Tcurrent_seed);
			tVariate.setRandomNumber(cong);
			tVariate.setParameters(conf.variate_Tcurrent_min,
					conf.variate_Tcurrent_max);
			break;
		}
		case NORMAL: {
			tVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_Tcurrent_seed);
			tVariate.setRandomNumber(cong);
			tVariate.setParameters(conf.variate_Tcurrent_default,
					conf.variate_Tcurrent_sdev);
			break;
		}
		default: {
			tVariate = null;
			break;
		}
		}
		RandomVariate mcVariate = null;
		switch (conf.variate_mc) {
		case UNIFORM: {
			mcVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_mc_seed);
			mcVariate.setRandomNumber(cong);
			mcVariate.setParameters(conf.variate_mc_min, conf.variate_mc_max);
			break;
		}
		case NORMAL: {
			mcVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_mc_seed);
			mcVariate.setRandomNumber(cong);
			mcVariate.setParameters(conf.variate_mc_default,
					conf.variate_mc_sdev);
			break;
		}
		default: {
			mcVariate = null;
			break;
		}
		}
		RandomVariate aVariate = null;
		switch (conf.variate_A) {
		case UNIFORM: {
			aVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_A_seed);
			aVariate.setRandomNumber(cong);
			aVariate.setParameters(conf.variate_A_min, conf.variate_A_max);
			break;
		}
		case NORMAL: {
			aVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_A_seed);
			aVariate.setRandomNumber(cong);
			aVariate.setParameters(conf.variate_A_default,
					conf.variate_A_sdev);
			break;
		}
		default: {
			aVariate = null;
			break;
		}
		}

		RandomVariate toVariate = null;
		switch (conf.variate_TO) {
		case UNIFORM: {
			toVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_TO_seed);
			toVariate.setRandomNumber(cong);
			toVariate.setParameters(conf.variate_TO_min, conf.variate_TO_max);
			break;
		}
		case NORMAL: {
			toVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_TO_seed);
			toVariate.setRandomNumber(cong);
			toVariate.setParameters(conf.variate_TO_default,
					conf.variate_TO_sdev);
			break;
		}
		default: {
			toVariate = null;
			break;
		}
		}
		RandomVariate etaVariate = null;
		switch (conf.variate_eta) {
		case UNIFORM: {
			etaVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_eta_seed);
			etaVariate.setRandomNumber(cong);
			etaVariate.setParameters(conf.variate_eta_min, conf.variate_eta_max);
			break;
		}
		case NORMAL: {
			etaVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_eta_seed);
			etaVariate.setRandomNumber(cong);
			etaVariate.setParameters(conf.variate_eta_default,
					conf.variate_eta_sdev);
			break;
		}
		default: {
			etaVariate = null;
			break;
		}
		}
		RandomVariate qcVariate = null;
		switch (conf.variate_qc) {
		case UNIFORM: {
			qcVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_qc_seed);
			qcVariate.setRandomNumber(cong);
			qcVariate.setParameters(conf.variate_qc_min, conf.variate_qc_max);
			break;
		}
		case NORMAL: {
			qcVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_qc_seed);
			qcVariate.setRandomNumber(cong);
			qcVariate.setParameters(conf.variate_qc_default,
					conf.variate_qc_sdev);
			break;
		}
		default: {
			qcVariate = null;
			break;
		}
		}
		RandomVariate qwVariate = null;
		switch (conf.variate_qw) {
		case UNIFORM: {
			qwVariate = new UniformVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_qw_seed);
			qwVariate.setRandomNumber(cong);
			qwVariate.setParameters(conf.variate_qw_min, conf.variate_qw_max);
			break;
		}
		case NORMAL: {
			qwVariate = new NormalVariate();
			Congruential cong = new Congruential();
			cong.setSeed(conf.variate_qw_seed);
			qwVariate.setRandomNumber(cong);
			qwVariate.setParameters(conf.variate_qw_default,
					conf.variate_qw_sdev);
			break;
		}
		default: {
			qwVariate = null;
			break;
		}
		}
		// Create [0|1] variate for starting states
		RandomVariate activityAtStartVariate = new BernoulliVariate();
		activityAtStartVariate.setParameters(conf.ACTIVE_AT_START_PROPABILITY);
		// Create fridges
		ArrayList<AbstractFridge> fridges = new ArrayList<AbstractFridge>(
				conf.POPULATION_SIZE);
		for (int i = 0; i < conf.POPULATION_SIZE; i++) {
			AbstractFridge f = null;
			AbstractController c = null;
			switch (conf.model) {
			case ITERATIVE: {
				f = new IterativeFridge(list);
				switch (conf.strategy) {
				case NONE: {
					c = new BaseControllerIterative((IterativeFridge) f, list);
					break;
				}
				case DSC: {
					switch(conf.damping) {
					case NONE: {
						c = new DirectControllerIterative((IterativeFridge) f, list);
						break;
					}
					case RANDOM: {
						c = new DirectControllerIterative((IterativeFridge) f, list);
						System.err.println("Damping not implement for iterative model!");
						// TODO
						break;
					}
					case STATEFUL_HALF: {
						c = new DirectControllerIterative((IterativeFridge) f, list);
						System.err.println("Damping not implement for iterative model!");
						// TODO
						break;
					}
					case STATEFUL_FULL: {
						c = new DirectControllerIterative((IterativeFridge) f, list);
						System.err.println("Damping not implement for iterative model!");
						// TODO
						break;
					}
					}
					break;
				}
				case TLR: {
					c = new TimedControllerIterative((IterativeFridge) f, list);
					break;
				}
				}
				break;
			}
			case LINEAR: {
				f = new LinearFridge(list);
				switch (conf.strategy) {
				case NONE: {
					c = new BaseControllerLinear((LinearFridge) f, list);
					break;
				}
				case DSC: {
					c = new DirectControllerLinear((LinearFridge) f, list);
					break;
				}
				case TLR: {
					c = new TimedControllerLinear((LinearFridge) f, list);
					break;
				}
				}
				break;
			}
			case COMPACT_LINEAR: {
				f = new LinearFridge(list);
				switch (conf.strategy) {
				case NONE: {
					c = new BaseControllerCompactLinear((LinearFridge) f, list);
					break;
				}
				case DSC: {
					switch(conf.damping) {
					case NONE: {
						c = new DirectControllerCompactLinear((LinearFridge) f, list);
						break;
					}
					case RANDOM: {
						c = new RandomizedDirectCompactLinear((LinearFridge) f, list);
						break;
					}
					case STATEFUL_HALF: {
						c = new StatefulDirectCompactLinearHalfWidth(
								(LinearFridge) f, list);
						break;
					}
					case STATEFUL_FULL: {
						c = new StatefulDirectCompactLinearFullWidth(
								(LinearFridge) f, list);
						break;
					}
					}
//conf.strategy = Configuration.SIGNAL.NONE;
// TODO
					break;
				}
				case TLR: {
					switch(conf.damping) {
					case NONE: {
						c = new TimedControllerCompactLinear((LinearFridge) f, list);
						break;
					}
					case RANDOM: {
						c = new RandomizedTimedCompactLinear((LinearFridge) f, list);
						break;
					}
					case STATEFUL_HALF:
					case STATEFUL_FULL: {
						c = new StatefulTimedCompactLinear((LinearFridge) f, list);
						break;
					}
					}
//conf.strategy = Configuration.SIGNAL.NONE;
// TODO
					break;
				}
				}
				break;
			}
			}
			if (conf.variate_Tcurrent != Configuration.VARIATE.NONE) {
				f.generate_tCurrent(tVariate);
			} else {
				f.setT_current(conf.variate_Tcurrent_default);
			}
			if (conf.variate_mc != Configuration.VARIATE.NONE) {
				f.generate_mC(mcVariate);
			} else {
				f.setM_c(conf.variate_mc_default);
			}
			if (conf.variate_A != Configuration.VARIATE.NONE) {
				f.generate_a(aVariate);
			} else {
				f.setA(conf.variate_A_default);
			}
			if (conf.variate_TO != Configuration.VARIATE.NONE) {
				f.generate_tSurround(toVariate);
			} else {
				f.setT_surround(conf.variate_TO_default);
			}
			if (conf.variate_eta != Configuration.VARIATE.NONE) {
				f.generate_eta(etaVariate);
			} else {
				f.setEta(conf.variate_eta_default);
			}
			if (conf.variate_qc != Configuration.VARIATE.NONE) {
				f.generate_qCooling(qcVariate);
			}
			if (conf.variate_qw != Configuration.VARIATE.NONE) {
				f.generate_qWarming(qwVariate);
			}
			// Make (ACTIVE_AT_START_PROPABILITY*100)% of the fridges active
			f.setStartActive(activityAtStartVariate.generate() > 0);
			// Assign controller
			f.setController(c);
			// Store created fridge
			fridges.add(f);
		}
//conf.strategy = Configuration.SIGNAL.TLR;
//conf.strategy = Configuration.SIGNAL.DSC;
// TODO
		return fridges;
	}

	/**
	 * @param eventListID
	 * @return an {@link AbstractSignalPerformer} based on settings in
	 *         configuration
	 */
	private AbstractSignalPerformer createStrategy(int eventListID) {
		switch (conf.strategy) {
		case NONE: {
			return null;
		}
		case DSC: {
			return new SignalPerformerDirect(eventListID,
					conf.direct_t_notify, conf.direct_spread,
					conf.direct_doUnload);
		}
		case TLR: {
			return new SignalPerformerTimed(eventListID, conf.timed_t_notify,
					conf.timed_tau_preload, conf.timed_tau_reduce);
		}
		}
		return null;
	}

	/**
	 * @return a time-weighted stats object if load was collected, null
	 *         otherwise
	 */
	public SimpleStatsTimeVarying getLoadStats() {
		if (simulation != null) {
			return simulation.getLoadStats();
		}
		return null;
	}
	
	/**
	 * @return a time-weighted stats object if temperature was collected, null
	 *         otherwise
	 */
	public SimpleStatsTimeVarying getTemperatureStats() {
		if (simulation != null) {
			return simulation.getTemperatureStats();
		}
		return null;
	}

	/**
	 * @return the meanTemp
	 */
	public TimeseriesMultiMeanCollector getMeanTemp() {
		if (simulation != null) {
			return simulation.getMeanTemp();
		}
		return null;
	}

	/**
	 * @return the meanLoad
	 */
	public TimeseriesMultiMeanCollector getMeanLoad() {
		if (simulation != null) {
			return simulation.getMeanLoad();
		}
		return null;
	}

	/**
	 * @return the time weighted load result values if load was collected, null
	 *         otherwise
	 */
	public double[][] getLoadResults() {
		if (simulation != null) {
			return simulation.getLoadResults();
		}
		return null;
	}

	/**
	 * @return the time weighted temperature result values if temperature was
	 *         collected, null otherwise
	 */
	public double[][] getTemperatureResults() {
		if (simulation != null) {
			return simulation.getTemperatureResults();
		}
		return null;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the time needed for the simulation
	 */
	public double getSimulationTime() {
		return simulationTime;
	}
	
	/**
	 * Clears the collected statistic values.
	 */
	public void clear() {
		if (simulation != null) {
			simulation.clearStats();
		}
	}

	/**
	 * Visually presents the results of the last simulation run
	 * 
	 * @param block
	 *            whether this method should block until the window is closed
	 */
	public void showLastResults(boolean block) {
		if (simulation != null) {
			simulation.showResults(block);
		}
	}
}