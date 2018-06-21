/****************************************************************
 * Licensed Material - Property of IBM
 *
 * ****-*** 
 *
 * (c) Copyright IBM Corp. 2006.  All rights reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 *
 ****************************************************************
 */
package com.graly.erp.chart.builder;

import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;

import com.graly.erp.chart.model.data.util.DataSetFactory;

/**
 * Builds the chart with axis.
 * 
 * @author Qi Liang
 */
public abstract class AbstractChartWithAxisBuilder extends AbstractChartBuilder {

    /**
     * Title of X axis.
     */
    protected String xTitle = null;

    /**
     * Title of Y axis.
     */
    protected String yTitle = null;

    /**
     * X axis.
     */
    protected Axis xAxis = null;

    /**
     * Y axis.
     */
    protected Axis yAxis = null;

    /**
     * Constructor.
     * 
     * @param dataSet
     *            data for chart
     */
    public AbstractChartWithAxisBuilder(DataSetFactory df) {
        super(df);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#createChart()
     */
    protected void createChart() {
        chart = ChartWithAxesImpl.create();
    }
}
