package de.uniol.ui.desync.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

import de.uniol.ui.desync.util.collectors.AbstractCollector;

/**
 * This class shows a line chart. That is a chart which connects all given data
 * points with straight lines.
 * 
 * @author Chh
 */
public class LineChartDialog extends Dialog {
	
	/** NumberFormat used in tooltips for x-values */
	protected static NumberFormat nf = NumberFormat.getNumberInstance();
	static {
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
	}
	
	/** NumberFormat used in tooltips for y-values */
	protected static NumberFormat nf2 = NumberFormat.getNumberInstance();
	static {
		nf2.setMaximumFractionDigits(3);
		nf2.setMinimumFractionDigits(3);
	}
	
	/** Underlying dataset */
	protected DefaultXYDataset xy;
	/** title of this chart */
	protected String title;
	/** title of the x axis */
	protected String xTitle;
	/** title of the y axis */
	protected String yTitle;
	/** unit string for x values */
	protected String tooltipRangeUnits;
	/** unit string for y values */
	protected String tooltipValueUnits;
	/** lower border of y axis range */
	protected double minRange;
	/** upper border of y axis range */
	protected double maxRange;
	
	/** Defines width values for individual series */
	protected HashMap<Integer, Float> seriesWidths = new HashMap<Integer, Float>();
	/** Defines color values for individual series */
	protected HashMap<Integer, Color> seriesColors = new HashMap<Integer, Color>();

	/**
	 * Creates a new line chart with the given values.
	 * 
	 * @param parent
	 * @param title
	 * @param xTitle
	 * @param yTitle
	 * @param tooltipRangeUnits
	 * @param tooltipValueUnits
	 * @param minRange
	 * @param maxRange
	 */
	public LineChartDialog(Shell parent, String title, String xTitle,
			String yTitle, String tooltipRangeUnits, String tooltipValueUnits,
			double minRange, double maxRange) {
		super(parent, SWT.APPLICATION_MODAL);
		this.title = title;
		this.xTitle = xTitle;
		this.yTitle = yTitle;
		this.tooltipRangeUnits = tooltipRangeUnits;
		this.tooltipValueUnits = tooltipValueUnits;
		this.maxRange = maxRange;
		this.minRange = minRange;
		xy = new DefaultXYDataset();
	}

	/**
	 * Adds a series represented in the given Collector.
	 * 
	 * @param col
	 */
	public void addSeries(AbstractCollector col) {
		xy.addSeries(col.getName(), col.getResults());
	}
	
	/**
	 * Adds all series from the given list of Collectors.
	 * 
	 * @param list
	 */
	public void addAllSeries(List<? extends AbstractCollector> list) {
		Iterator<? extends AbstractCollector> it = list.iterator();
		while(it.hasNext()) {
			addSeries(it.next());
		}
	}

	/**
	 * Defines the width of the specified series.
	 * 
	 * @param series
	 * @param width
	 */
	public void setSeriesWidth(int series, float width) {
		seriesWidths.put(series, width);
	}
	
	/**
	 * Defines the color of the specified series.
	 * 
	 * @param series
	 * @param c
	 */
	public void setSeriesColor(int series, Color c) {
		seriesColors.put(series, c);
	}

	/**
	 * @return the resulting chart object
	 */
	protected JFreeChart createChart() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xTitle,
				yTitle, xy, true, true, false);

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
				public String generateToolTip(XYDataset dataset, int series,
						int item) {
					return nf.format(dataset.getXValue(series, item))
							+ tooltipRangeUnits + ", "
							+ nf2.format(dataset.getYValue(series, item))
							+ tooltipValueUnits;
				}
			});
			
			for (int i : seriesWidths.keySet()) {
				xyr.setSeriesStroke(i, new BasicStroke(seriesWidths.get(i)));
			}
			for (int i : seriesColors.keySet()) {
				xyr.setSeriesPaint(i, seriesColors.get(i));
			}
		}

		NumberAxis xaxis = new NumberAxis(xTitle);
		plot.setDomainAxis(xaxis);

		ValueAxis yaxis = plot.getRangeAxis();
		yaxis.setRange(new Range(minRange, maxRange));

		return chart;
	}
	
	/**
	 * Creates the chart object in the current parent shell.
	 */
	public void create() {
		Shell shell = getParent();
		ChartComposite cc = new ChartComposite(shell, SWT.NONE, createChart(),
				true);
		cc.setDisplayToolTips(true);
		cc.setHorizontalAxisTrace(false);
		cc.setVerticalAxisTrace(false);
	}

	/**
	 * Creates the chart object in the current parent shell and shows this
	 * shell.
	 * 
	 * @param blocking
	 *            defines whether this methods blocks until the shell is closed
	 */
	public void open(boolean blocking) {
		create();
		Shell shell = getParent();
		shell.open();
		Display display = shell.getDisplay();
		while (blocking && !shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}