package de.uniol.ui.desync.ui;

import java.awt.Color;
import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

public class TemperatureChart extends ChartComposite {

	protected static NumberFormat nf = NumberFormat.getNumberInstance();
	static {
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
	}
	protected static NumberFormat nf2 = NumberFormat.getNumberInstance();
	static {
		nf2.setMaximumFractionDigits(3);
		nf2.setMinimumFractionDigits(3);
	}
	
	public TemperatureChart(Composite parent, double[][] results) {
		super(parent, SWT.NONE, createChart(results), true);
	}
	
	protected static JFreeChart createChart(double[][] results) {
		DefaultXYDataset xy = new DefaultXYDataset();
		xy.addSeries("mean values over all fridges", results);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Temperature progress", "Time (min)", "Temperature (°C)", xy,
				true, true, false);

	        chart.setBackgroundPaint(Color.white);

	        XYPlot plot = (XYPlot) chart.getPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
	        
	        XYItemRenderer r = plot.getRenderer();
	        if (r instanceof XYLineAndShapeRenderer) {
	            XYLineAndShapeRenderer xyr = (XYLineAndShapeRenderer) r;
	            xyr.setBaseToolTipGenerator(new XYToolTipGenerator() {
	            	public String generateToolTip(XYDataset dataset,
							int series, int item) {
						return nf.format(dataset.getXValue(series, item)) + "min, "
							+ nf2.format(dataset.getYValue(series, item)) + "°C";
					}
	            });
	        }
	        
	        NumberAxis axis = new NumberAxis("Time (min)");
	        plot.setDomainAxis(axis);
	        
	        return chart;
	}
	
	public static void openChart(double[][] results) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setSize(600, 300);
		shell.setLayout(new FillLayout());
		shell.setText("Simulation results");
		ChartComposite cc = new TemperatureChart(shell, results);
		cc.setDisplayToolTips(true);
		cc.setHorizontalAxisTrace(false);
		cc.setVerticalAxisTrace(false);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}