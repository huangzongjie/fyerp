package com.graly.erp.chart.builder;

import org.eclipse.birt.chart.model.Chart;

import com.graly.erp.chart.model.data.util.DataSetFactory;
public abstract class AbstractChartBuilder {

    /**
     * Font name for all titles, labels, and values.
     */
    protected final static String FONT_NAME = "MS Sans Serif";

    /**
     * Chart instance.
     */
    protected Chart chart = null;

    /**
     * Chart title.
     */
    protected String title = null;
    
    protected DataSetFactory df = null;

    /**
     * Constructs one chart builder and associate it to one data set.
     * 
     * @param dataSet
     *            data set
     */
    public AbstractChartBuilder(DataSetFactory df) {
        this.df = df;
    }

    /**
     * Builds one chart.
     */
    public void build() {
        createChart();
        buildPlot();
        buildLegend();
        buildTitle();
        buildXAxis();
        buildYAxis();
        buildXSeries();
        buildYSeries();
    }

    /**
     * Creates chart instance.
     */
    protected abstract void createChart();

    /**
     * Builds plot.
     */
    protected void buildPlot() {

    }

    /**
     * Builds X axis.
     */
    protected void buildXAxis() {

    }

    /**
     * Builds Y axis.
     */
    protected void buildYAxis() {

    }

    /**
     * Builds X series.
     */
    protected void buildXSeries() {

    }

    /**
     * Builds Y series.
     */
    protected void buildYSeries() {
    	
    }

    /**
     * Builds legend.
     * 
     */
    protected void buildLegend() {

    }

    /**
     * Builds the chart title.
     */
    protected void buildTitle() {
        chart.getTitle().getLabel().getCaption().setValue(title);
        chart.getTitle().getLabel().getCaption().getFont().setSize(14);
        chart.getTitle().getLabel().getCaption().getFont().setName(FONT_NAME);
    }

    /**
     * Returns the chart instance.
     * 
     * @return the chart instance
     */
    public Chart getChart() {
        return chart;
    }

}
