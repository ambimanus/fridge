package de.uniol.ui.desync.ui;

import java.awt.BasicStroke;
import java.awt.Color;

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
import org.jfree.ui.RectangleInsets;

/**
 * This class shows a step chart. That is a chart which connects data points
 * only with horizontal and vertical lines.
 * 
 * @author Chh
 */
public class StepChartDialog extends LineChartDialog {

	/**
	 * Creates a new step chart with the given values.
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
	public StepChartDialog(Shell parent, String title, String xTitle,
			String yTitle, String tooltipRangeUnits, String tooltipValueUnits,
			double minRange, double maxRange) {
		super(parent, title, xTitle, yTitle, tooltipRangeUnits,
				tooltipValueUnits, minRange, maxRange);
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
}