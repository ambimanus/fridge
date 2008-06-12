package de.uniol.ui.desync.ui;

import java.awt.BasicStroke;
import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

public class StepChartDialog extends LineChartDialog {

	private double minRange = -10.0;
	private double maxRange = 80.0;
	
	public StepChartDialog(Shell parent, String title, String xTitle,
			String yTitle, String tooltipRangeUnits, String tooltipValueUnits,
			double minRange, double maxRange) {
		super(parent, title, xTitle, yTitle, tooltipRangeUnits,
				tooltipValueUnits);
		this.maxRange = maxRange;
		this.minRange = minRange;
	}

	protected JFreeChart createChart() {
		JFreeChart chart = ChartFactory.createXYStepChart(title, xTitle,
				yTitle, xy, PlotOrientation.VERTICAL, true, true, false);

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
	
	public void create() {
		Shell shell = getParent();
		ChartComposite cc = new ChartComposite(shell, SWT.NONE, createChart(),
				true);
		cc.setDisplayToolTips(true);
		cc.setHorizontalAxisTrace(false);
		cc.setVerticalAxisTrace(false);
	}

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