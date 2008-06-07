package de.uniol.ui.desync.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.SystemColor;
import java.text.NumberFormat;
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

public class ChartDialog extends Dialog {

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

	private DefaultXYDataset xy;
	private String title;
	private String xTitle;
	private String yTitle;
	private int highlightedSeriesId = -1;

	public ChartDialog(Shell parent, String title, String xTitle, String yTitle) {
		super(parent, SWT.APPLICATION_MODAL);
		this.title = title;
		this.xTitle = xTitle;
		this.yTitle = yTitle;
		xy = new DefaultXYDataset();
	}

	public void addSeries(AbstractCollector col) {
		xy.addSeries(col.getName(), col.getResults());
	}
	
	public void addAllSeries(List<? extends AbstractCollector> list) {
		Iterator<? extends AbstractCollector> it = list.iterator();
		while(it.hasNext()) {
			addSeries(it.next());
		}
	}

	public int getHighlightedSeriesId() {
		return highlightedSeriesId;
	}

	public void setHighlightedSeriesId(int highlightedSeriesId) {
		this.highlightedSeriesId = highlightedSeriesId;
	}

	private JFreeChart createChart() {
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
					return nf.format(dataset.getXValue(series, item)) + "min, "
							+ nf2.format(dataset.getYValue(series, item))
							+ "°C";
				}
			});
			
			if (highlightedSeriesId != -1) {
				xyr.setSeriesPaint(highlightedSeriesId, SystemColor.BLACK);
				xyr.setSeriesStroke(highlightedSeriesId, new BasicStroke(2.0f));
			}
		}

		NumberAxis xaxis = new NumberAxis(xTitle);
		plot.setDomainAxis(xaxis);

		ValueAxis yaxis = plot.getRangeAxis();
		Range old = yaxis.getRange();
		yaxis.setRange(new Range(old.getLowerBound()
				- (old.getUpperBound() * 0.1), old.getUpperBound()
				+ (old.getUpperBound() * 0.1)));

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