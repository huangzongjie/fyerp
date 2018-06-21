package com.graly.erp.wip.workcenter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;

public class GanttChartSection {
	private static final Logger logger = Logger.getLogger(GanttChartSection.class);
	private final int oneRowHeight = 24;
	private final int spacer = 2;
	private GanttChartDialog parentDialog;
	private GanttChart chart;
	private GanttComposite ganttComposite;
	
	public GanttChartSection(GanttChartDialog parentDialog) {
		this.parentDialog = parentDialog;
	}
	
	public void createContents(Composite parent, ManagedForm form) {
		Composite comp = form.getToolkit().createComposite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		// 创建Gantt Chart
		chart = new GanttChart(comp, SWT.BORDER);	
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		ganttComposite = chart.getGanttComposite();
		ganttComposite.setDrawHorizontalLinesOverride(true);
		ganttComposite.setDrawVerticalLinesOverride(true);
		ganttComposite.setFixedRowHeightOverride(oneRowHeight-spacer);
		ganttComposite.setEventSpacerOverride(spacer);		

		createGanttContent(chart);
	}
	
	protected void createGanttContent(GanttChart chart) {
		GanttEvent ge;
//		Color color = SWTResourceCache.getColor("Function");
		Material material = null;
		for(DocumentationLine line : getMoLines()) {
			GregorianCalendar sc = new GregorianCalendar();
			GregorianCalendar ec = new GregorianCalendar();
			if (line.getDateStart() != null) {
				sc.setTime(line.getDateStart());				
			}
			if (line.getDateEnd() != null) {
				ec.setTime(line.getDateEnd());
			}
			ge = new GanttEvent(chart, null, sc, ec, 0);
			ge.setMoveable(false);
			ge.setResizable(false);
			material = line.getMaterial();
			// 设置甘特图名称
			if(material != null) {
				String name = line.getMaterial().getName() != null ?
				material.getMaterialId() + "(" + material.getName() + ")" : material.getMaterialId();
				ge.setName(name);
				material = null;
			}
			// 设置甘特图完成百分比
			ge.setPercentComplete(getGEPercentComplete(line));
		}
	}
	
	private int getGEPercentComplete(DocumentationLine line) {
		BigDecimal manufactureQty = BigDecimal.ONE;
		BigDecimal completedQty = BigDecimal.ZERO;
		if(line instanceof ManufactureOrderLine) {
			ManufactureOrderLine ml = (ManufactureOrderLine)line;
			if(ml.getQty() != null && ml.getQty().compareTo(BigDecimal.ZERO) != 0) {
				manufactureQty = ml.getQty();
			}
			if(ml.getQtyReceive() != null) {
				completedQty = ml.getQtyReceive();
			}
		}
		return completedQty.divide(manufactureQty, 2, RoundingMode.UP).multiply(new BigDecimal("100")).intValue();
	}
	
	protected List<ManufactureOrderLine> getMoLines() {
		List<ManufactureOrderLine> list = new ArrayList<ManufactureOrderLine>();
		try {
			WorkCenter workCenter = this.parentDialog.getWorkCenter();
			if(workCenter != null && workCenter.getObjectRrn() != 0) {
				WipManager wipManager = Framework.getService(WipManager.class);
				list = wipManager.getMoLineByWorkCenter(Env.getOrgRrn(),workCenter.getObjectRrn(), null);
				return list;
			}
		} catch(Exception e) {
			logger.error("GanttChartSection : getMoLines()", e);
		}
		return list;
	}

}
