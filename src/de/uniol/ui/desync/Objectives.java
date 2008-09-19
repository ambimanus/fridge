package de.uniol.ui.desync;

import java.text.NumberFormat;
import java.util.ArrayList;

import de.uniol.ui.desync.Configuration.DAMPING;
import de.uniol.ui.desync.Configuration.SIGNAL;
import de.uniol.ui.desync.Configuration.VARIATE;

public class Objectives {

	protected static NumberFormat nf = NumberFormat.getNumberInstance();
	static {
		nf.setMinimumFractionDigits(1);
	}
	
	public static ArrayList<Configuration> createObjectives_T() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;

		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.NONE;
		conf.title = "T=" + conf.variate_Tcurrent_default;
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.title = "T=uniform(" + conf.variate_Tcurrent_min + ","
				+ conf.variate_Tcurrent_max + ")";
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.NORMAL;
		conf.title = "T=normal(" + conf.variate_Tcurrent_default + ","
				+ conf.variate_Tcurrent_sdev + ")";
		objectives.add(conf);

		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_mc() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;
		
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NONE;
		conf.title = "mc=" + conf.variate_mc_default;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.UNIFORM;
		conf.title = "mc=uniform(" + conf.variate_mc_min + ","
				+ conf.variate_mc_max + ")";
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.title = "mc=normal(" + conf.variate_mc_default + ","
				+ conf.variate_mc_sdev + ")";
		objectives.add(conf);
		
		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_A() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;
		
		conf = new Configuration();
		conf.variate_A = Configuration.VARIATE.NONE;
		conf.title = "A=" + conf.variate_A_default;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_A = Configuration.VARIATE.UNIFORM;
		conf.title = "A=uniform(" + conf.variate_A_min + ","
				+ conf.variate_A_max + ")";
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.title = "A=normal(" + conf.variate_A_default + ","
				+ conf.variate_A_sdev + ")";
		objectives.add(conf);
		
		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_TO() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;

		conf = new Configuration();
		conf.variate_TO = Configuration.VARIATE.NONE;
		conf.title = "TO=" + conf.variate_TO_default;
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_TO = Configuration.VARIATE.UNIFORM;
		conf.title = "TO=uniform(" + conf.variate_TO_min + ","
				+ conf.variate_TO_max + ")";
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.title = "TO=normal(" + conf.variate_TO_default + ","
				+ conf.variate_TO_sdev + ")";
		objectives.add(conf);

		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_eta() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;

		conf = new Configuration();
		conf.variate_eta = Configuration.VARIATE.NONE;
		conf.title = "eta=" + conf.variate_eta_default;
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_eta = Configuration.VARIATE.UNIFORM;
		conf.title = "eta=uniform(" + conf.variate_eta_min + ","
				+ conf.variate_eta_max + ")";
		objectives.add(conf);

		conf = new Configuration();
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.title = "eta=normal(" + conf.variate_eta_default + ","
				+ conf.variate_eta_sdev + ")";
		objectives.add(conf);
		
		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_T_mc() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NONE;
		conf.variate_Tcurrent = Configuration.VARIATE.NONE;
		conf.title = "T=" + conf.variate_Tcurrent_default + ",mc="
				+ conf.variate_mc_default;
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NONE;
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.title = "T=uniform(" + conf.variate_Tcurrent_min + ","
				+ conf.variate_Tcurrent_max + "),mc=" + conf.variate_mc_default;
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NONE;
		conf.variate_Tcurrent = Configuration.VARIATE.NORMAL;
		conf.title = "T=normal(" + conf.variate_Tcurrent_default + ","
				+ conf.variate_Tcurrent_sdev + "),mc="
				+ conf.variate_mc_default;
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.UNIFORM;
		conf.variate_Tcurrent = Configuration.VARIATE.NONE;
		conf.title = "T=" + conf.variate_Tcurrent_default + ",mc=uniform("
				+ conf.variate_mc_min + "," + conf.variate_mc_max + ")";
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_Tcurrent = Configuration.VARIATE.NONE;
		conf.title = "T=" + conf.variate_Tcurrent_default + ",mc=normal("
				+ conf.variate_mc_default + "," + conf.variate_mc_sdev + ")";
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.UNIFORM;
		conf.variate_Tcurrent = Configuration.VARIATE.NORMAL;
		conf.title = "T=normal(" + conf.variate_Tcurrent_default + ","
				+ conf.variate_Tcurrent_sdev + "),mc=uniform("
				+ conf.variate_mc_min + "," + conf.variate_mc_max + ")";
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.title = "T=uniform(" + conf.variate_Tcurrent_min + ","
				+ conf.variate_Tcurrent_max + "),mc=normal("
				+ conf.variate_mc_default + "," + conf.variate_mc_sdev + ")";
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.UNIFORM;
		conf.variate_Tcurrent = VARIATE.UNIFORM;
		conf.title = "T=uniform(" + conf.variate_Tcurrent_min + ","
				+ conf.variate_Tcurrent_max + "),mc=uniform("
				+ conf.variate_mc_min + "," + conf.variate_mc_max + ")";
		objectives.add(conf);

		// Create config
		conf = new Configuration();
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_Tcurrent = VARIATE.NORMAL;
		conf.title = "T=normal(" + conf.variate_Tcurrent_default + ","
				+ conf.variate_Tcurrent_sdev + "),mc=normal("
				+ conf.variate_mc_default + "," + conf.variate_mc_sdev + ")";
		objectives.add(conf);

		return objectives;
	}
	
	public static ArrayList<Configuration> createObjectives_AllVariated_DifferentStrategies() {
		ArrayList<Configuration> objectives = new ArrayList<Configuration>();
		Configuration conf;

		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.NONE;
		conf.damping = DAMPING.NONE;
		conf.title = "signal=" + conf.strategy;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = false;
		conf.damping = DAMPING.STATEFUL_HALF;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = false;
		conf.damping = DAMPING.STATEFUL_FULL;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = false;
		conf.damping = DAMPING.RANDOM;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = true;
		conf.damping = DAMPING.STATEFUL_HALF;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = true;
		conf.damping = DAMPING.STATEFUL_FULL;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.DSC;
		conf.direct_doUnload = true;
		conf.damping = DAMPING.RANDOM;
		conf.title = "signal=" + conf.strategy + " ("
				+ (conf.direct_doUnload ? "unload" : "load") + "), damping="
				+ conf.damping;
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.TLR;
		conf.damping = DAMPING.STATEFUL_FULL;
		conf.title = "signal=" + conf.strategy + ", damping=STATEFUL";
		objectives.add(conf);
		
		conf = new Configuration();
		conf.variate_Tcurrent = Configuration.VARIATE.UNIFORM;
		conf.variate_mc = Configuration.VARIATE.NORMAL;
		conf.variate_A = Configuration.VARIATE.NORMAL;
		conf.variate_TO = Configuration.VARIATE.NORMAL;
		conf.variate_eta = Configuration.VARIATE.NORMAL;
		conf.strategy = SIGNAL.TLR;
		conf.damping = DAMPING.RANDOM;
		conf.title = "signal=" + conf.strategy + ", damping=RANDOM";
		objectives.add(conf);

		return objectives;
	}
}
