package de.uniol.ui.desync;

import java.util.ArrayList;

import simkit.random.BernoulliVariate;
import simkit.random.Congruential;
import simkit.random.RandomVariate;
import simkit.random.UniformVariate;
import simkit.stat.SimpleStatsTimeVarying;
import de.uniol.ui.desync.model.ControlCenter;
import de.uniol.ui.desync.model.Simulation;
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
import de.uniol.ui.desync.model.signals.AbstractSignalPerformer;
import de.uniol.ui.desync.model.signals.SignalPerformerDirect;
import de.uniol.ui.desync.model.signals.SignalPerformerTimed;
import de.uniol.ui.desync.util.MessagingEventList;

/**
 * This class is the main class of this simulation. It creates all entities and
 * prepares and runs the simulation.
 * 
 * @author Chh
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

	public Experiment(Configuration conf) {
		this.conf = conf;
		name = "Experiment" + instances++;
	}

	/**
	 * Runs an experiment on the specified {@link MessagingEventList} based on
	 * the given {@link Configuration}. The EventList will be resetted after
	 * the simulation.
	 * 
	 * @param conf
	 */
	public void run(MessagingEventList el) {
		name = "Experiment" + instances + " @run" + runs++;
		
		// Create fridges
		ArrayList<AbstractFridge> fridges = createFridges(el.getID());
		if (conf.strategy != Configuration.SIGNAL.NONE) {
			// Create ControlCenter and Strategy
			new ControlCenter(el.getID(), fridges, createStrategy(el.getID()));
		}

		// Prepare simulation
		simulation = new Simulation(el, fridges);
//simulation.setCollectMeanLoad(false);
//simulation.setCollectMeanTemperature(false);
// TODO

		// Simulate
		double start = System.currentTimeMillis();
		simulation.simulate(conf.SIMULATION_LENGTH * 60.0);
		while (el.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		simulationTime = (System.currentTimeMillis() - start) / 1000.0;

		// Create charts
		if (conf.showResults) {
			simulation.showResults();
		}
		
		// Cleanup
		fridges.clear();
	}

	/**
	 * Creates the fridges population using the runtime type defined in
	 * {@link mode}.
	 * 
	 * @param list
	 *            the underlying FEL id
	 * @return the population
	 */
	private ArrayList<AbstractFridge> createFridges(int list) {
		AbstractFridge.resetInstanceCounter();
		// Create distinct uniform random variates for m_c and t_current
		RandomVariate thermalMassVariate = new UniformVariate();
		Congruential cong = new Congruential();
		cong.setSeed(conf.variate_mc_seed);
		thermalMassVariate.setRandomNumber(cong);
		thermalMassVariate.setParameters(conf.MC_MIN, conf.MC_MAX);
		RandomVariate tCurrentVariate = new UniformVariate();
		cong = new Congruential();
		cong.setSeed(conf.variate_Tcurrent_seed);
		tCurrentVariate.setRandomNumber(cong);
		tCurrentVariate.setParameters(AbstractFridge.DEFAULT_t_min,
				AbstractFridge.DEFAULT_t_max);
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
					c = new DirectControllerIterative((IterativeFridge) f, list);
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
			if (conf.variate_mc) {
				// Equally distribute m_c between MC_MIN and MC_MAX
				f.generate_mC(thermalMassVariate);
			}
			if (conf.variate_Tcurrent) {
				// Equally distribute t_current between t_min and t_max
				f.generate_tCurrent(tCurrentVariate);
			}
			// Make (ACTIVE_AT_START_PROPABILITY*100)% of the fridges active
			f.setStartActive(activityAtStartVariate.generate() > 0);
			// Assign proper FEL
//			f.setEventListID(list);
//			c.setEventListID(list);
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
					conf.timed_tau_activ, conf.timed_tau_reduce);
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
	
	public void showLastResults() {
		if (simulation != null) {
			simulation.showResults();
		}
	}
}