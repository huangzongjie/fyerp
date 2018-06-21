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
package com.graly.erp.wip.querychart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.ActionType;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.DataPoint;
import org.eclipse.birt.chart.model.attribute.DataPointComponentType;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.RiserType;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.TriggerCondition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointComponentImpl;
import org.eclipse.birt.chart.model.attribute.impl.GradientImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaDateFormatSpecifierImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaNumberFormatSpecifierImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.TooltipValueImpl;
import org.eclipse.birt.chart.model.component.Grid;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.DateTimeDataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.impl.ActionImpl;
import org.eclipse.birt.chart.model.data.impl.DateTimeDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TriggerImpl;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;

import com.graly.erp.chart.builder.AbstractChartWithAxisBuilder;
import com.graly.erp.chart.model.data.util.DataSetFactory;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;

/**
 * Builds stacked chart.
 * 
 * @author Qi Liang
 */
public class StackedChartBuilder extends AbstractChartWithAxisBuilder {
	private static final Logger logger = Logger.getLogger(StackedChartBuilder.class);
	private Map<String, String> errMessages = new HashMap<String, String>();

	/**
	 * Constructor.
	 * 
	 * @param dataSet
	 *            data for chart
	 */
	public StackedChartBuilder(DataSetFactory df, String chartTitle,
			String xTitle, String yTitle) {
		super(df);
		title = chartTitle;
		this.xTitle = xTitle;
		this.yTitle = yTitle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildPlot()
	 */
	protected void buildPlot() {
		((ChartWithAxes) chart).setUnitSpacing(25);
		chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());
		Plot p = chart.getPlot();
		p.getClientArea().setBackground(
				GradientImpl.create(ColorDefinitionImpl.create(255, 235, 255),
						ColorDefinitionImpl.create(255, 235, 255), -35, true));

		p.getClientArea().getInsets().set(8, 8, 8, 8);
		p.getOutline().setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildXAxis()
	 */
	protected void buildXAxis() {
		// X-Axis
		xAxis = ((ChartWithAxes) chart).getPrimaryBaseAxes()[0];
		xAxis.setCategoryAxis(true);
		xAxis.setType(AxisType.DATE_TIME_LITERAL);
		xAxis.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
		xAxis.getTitle().getCaption().setValue(xTitle);
		xAxis.setTitlePosition(Position.BELOW_LITERAL);
		xAxis.setLabelPosition(Position.BELOW_LITERAL);

		xAxis.setFormatSpecifier(JavaDateFormatSpecifierImpl
				.create("yyyy-MM-dd"));

		xAxis.getLabel().setBackground(
				ColorDefinitionImpl.create(255, 255, 255));
		xAxis.getLabel().getCaption().getFont().setRotation(0);//x轴label的角度

		xAxis.getTitle().setVisible(true);
		xAxis.getOrigin().setType(IntersectionType.MIN_LITERAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildYAxis()
	 */
	protected void buildYAxis() {
		yAxis = ((ChartWithAxes) chart).getPrimaryOrthogonalAxis(xAxis);

		LineAttributes la1 = LineAttributesImpl.create(ColorDefinitionImpl.create(196, 196, 196), LineStyle.SOLID_LITERAL, 1);
		yAxis.getMajorGrid().setLineAttributes(la1);
		
//		LineAttributes la2 = LineAttributesImpl.create(ColorDefinitionImpl.create(196, 196, 196), LineStyle.SOLID_LITERAL, 1);
//		yAxis.getMinorGrid().setLineAttributes(la2);
//		yAxis.getMinorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
		
		yAxis.setLabelPosition(Position.LEFT_LITERAL);
		yAxis.setTitlePosition(Position.LEFT_LITERAL);
		yAxis.getTitle().getCaption().setValue(yTitle);
		yAxis.getTitle().setVisible(true);

		yAxis.setType(AxisType.LINEAR_LITERAL);
		yAxis.getLabel().getCaption().getFont().setRotation(0);//y轴label的角度

//		        yAxis.getScale().setStep(5);//y轴刻度的步长
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildXSeries()
	 */
	protected void buildXSeries() {

		DateTimeDataSet dsDateValues = DateTimeDataSetImpl.create(df
				.getXCatalogs());

		// X-Series
		Series seBase = SeriesImpl.create();
		seBase.setDataSet(dsDateValues);

		SeriesDefinition sdX = SeriesDefinitionImpl.create();
		//        sdX.getSeriesPalette( ).shift( -1 );
		sdX.getSeriesPalette().update(1);
		xAxis.getSeriesDefinitions().add(sdX);
		xAxis.getLabel().getCaption().getFont().setRotation(-90);//X轴Label的角度
		sdX.getSeries().add(seBase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildYSeries()
	 */
	protected void buildYSeries() {
		// Add all series to one series definition
		SeriesDefinition sdY1 = SeriesDefinitionImpl.create();
		sdY1.getSeriesPalette().update(0);//此方法使每个series具有不同的颜色
		yAxis.getSeriesDefinitions().add(sdY1);
		Object obj = df.getYSeries();
		if (obj instanceof List) {
			List series = (List) obj;
			for (Object o : series) {
				if (o instanceof Map) {
					Map m = (Map) o;
					Iterator it = m.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						List vals = (LinkedList) m.get(key);
						List l = new LinkedList();
						String materialName = null;
						for (Object val : vals) {
							assert val == null;
							if (val instanceof DailyMoMaterial) {
								DailyMoMaterial dmm = (DailyMoMaterial) val;
								if(materialName == null){
									if(dmm.getMaterial() != null){
										materialName = dmm.getMaterial().getName();
									}
								}
								if(dmm.getCurrDayPower().intValue() == 0){
									try {
										ADManager entityManager = Framework.getService(ADManager.class);
										WorkCenter wc = new WorkCenter();
										wc.setObjectRrn(dmm.getWorkcenterRrn());
										wc = (WorkCenter)entityManager.getEntity(wc);
										String messageKey = wc.getObjectRrn() + "_" + I18nUtil.formatDate(dmm.getCurrDate());
										String message = wc.getName() + " " + I18nUtil.formatDate(dmm.getCurrDate()) + " 当天生产力为 0";
										if(!errMessages.containsKey(messageKey)){
											errMessages.put(messageKey, message);
											UI.showError(message);
										}
										l.add(0);
									} catch (Exception e) {
										logger.error(e);
										ExceptionHandlerManager.asyncHandleException(e);
									} finally {
									}
								}else{
									l.add(dmm.getTotalTime().divide(dmm.getCurrDayPower(), 4,
												BigDecimal.ROUND_HALF_UP)
												.doubleValue());
								}
							}
						}
						double[] rslts = new double[l.size()];
						for (int i = 0; i < l.size(); i++) {
							Object d = l.get(i);
							if (d instanceof Double) {
								Double dd = (Double)d;
								if(dd.doubleValue() == 0){
									rslts[i] = Double.NaN;
								}else{
									rslts[i] = dd.doubleValue();
								}
							}
						}
						NumberDataSet orthoValuesDataSet = NumberDataSetImpl
								.create(rslts);

						//	    				NumberDataSet orthoValuesDataSet = NumberDataSetImpl.create( new double[]{
						//	    						125.99, 352.95, -201.95, 299.95, -95.95, 65.95, 58.95
						//	    				} );
						BarSeries bs = createSeries(materialName, orthoValuesDataSet);
						sdY1.getSeries().add(bs);
//						sdY1.setGrouping(SeriesGroupingImpl.create( ));
					}

				}
			}
		}
	}

	private BarSeries createSeries(String identifier,
			NumberDataSet dsNumericValues) {
		BarSeries ss = (BarSeries) BarSeriesImpl.create();
		ss.setSeriesIdentifier(identifier);
		ss.setRiserOutline(null);
		ss.setRiser(RiserType.TUBE_LITERAL);//圆柱形
		ss.setLabelPosition(Position.INSIDE_LITERAL);
		ss.getLabel().getCaption().setColor(ColorDefinitionImpl.RED());
		//		ss.getLabel( ).setBackground( ColorDefinitionImpl.CYAN( ) );
//		ss.getLabel().setVisible(true);
		ss.setDataSet(dsNumericValues);
		ss.setStacked(true);

//		DataPoint dp = ss.getDataPoint();
//		dp.getComponents().clear();
//				dp.setPrefix( "(" ); //$NON-NLS-1$
//				dp.setSuffix( ")" ); //$NON-NLS-1$
//				dp.getComponents( )
//						.add( DataPointComponentImpl.create( DataPointComponentType.BASE_VALUE_LITERAL,
//								JavaNumberFormatSpecifierImpl.create( "0.00" ) ) ); //$NON-NLS-1$
//		dp.getComponents().add(
//				DataPointComponentImpl.create(
//						DataPointComponentType.ORTHOGONAL_VALUE_LITERAL,
//						JavaNumberFormatSpecifierImpl.create("0.00"))); 
//
				ss.getTriggers( )
						.add( TriggerImpl.create( TriggerCondition.ONMOUSEOVER_LITERAL,
								ActionImpl.create( ActionType.SHOW_TOOLTIP_LITERAL,
										TooltipValueImpl.create( 500, String.valueOf(ss.getSeriesIdentifier()) ) ) ) );//500是延迟时间
//		ss.getTriggers().add(
//				TriggerImpl.create(TriggerCondition.MOUSE_CLICK_LITERAL,
//						ActionImpl.create(ActionType.CALL_BACK_LITERAL,
//								CallBackValueImpl.create(String.valueOf(ss
//										.getSeriesIdentifier())))));
		return ss;
	}

}
