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
package de.uniol.ui.desync.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import de.uniol.ui.desync.model.Configuration;

/**
 * This class represents a dialog which allows editing a simulation objective.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class ObjectiveDialog extends Dialog {

	/** Underlying shell */
	protected Shell shell;
	/** Objective */
	protected Configuration conf;
	
	public ObjectiveDialog(Shell parent, Configuration conf) {
		super(parent);
		this.conf = conf;
		if (this.conf == null) {
			conf = new Configuration();
		}
		setText("Settings for objective <"
				+ conf.title
				+ (Configuration.instance == 0 ? ""
						: (" " + Configuration.instance)) + ">");
	}

	public Configuration open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
		shell.setText(getText());
		shell.setLayout(new FillLayout());
		
		initControls();
		
		shell.setSize(835, 570);
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return conf;
	}
	
	protected void initControls() {
		TabFolder tf = new TabFolder(shell, SWT.NONE);
		TabItem ti = new TabItem(tf, SWT.NONE);
		ti.setControl(new SimulationSettingsComposite(tf, SWT.NONE, conf));
		ti.setText("Simulation settings");
		ti = new TabItem(tf, SWT.NONE);
		ti.setControl(new PopulationSettingsComposite(tf, SWT.NONE, conf));
		ti.setText("Population settings");
	}
}