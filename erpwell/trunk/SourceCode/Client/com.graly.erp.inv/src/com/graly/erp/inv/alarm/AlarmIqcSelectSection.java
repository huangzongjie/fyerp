package com.graly.erp.inv.alarm;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;

import com.graly.erp.inv.in.createfrom.iqc.EntityListTableManager;
import com.graly.erp.inv.model.Iqc;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmIqcSelectSection {
	private static final Logger logger = Logger.getLogger(AlarmIqcSelectSection.class);
	private static String IqcWhereClause = " isIn != 'Y' AND docStatus = '" + Iqc.STATUS_APPROVED + "' ";
	private AlarmIqcSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private EntityListTableManager tableManager;
	private TableViewer viewer;
	private String alarmWhereClause;

	public AlarmIqcSelectSection(ADTable table, AlarmIqcSelectPage parentPage,String alarmWhereClause) {
		this.adTable = table;
		this.parentPage = parentPage;
		this.alarmWhereClause = alarmWhereClause;
	}
	
	public void createContents(ManagedForm form, Composite parent) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.createSectionContent(parent);
	}

	protected void createSectionContent(Composite client) {
		try {
			tableManager = new EntityListTableManager(adTable);
			viewer = (TableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addSelectionChangedListener(getSelectionChangedListener());
			
			EntityItemInput input = new EntityItemInput(adTable, IqcWhereClause+alarmWhereClause, null);
			viewer.setInput(input);
			tableManager.updateView(viewer);
		} catch(Exception e) {
			logger.error("IqcSelectSection : createAdObject() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	private ISelectionChangedListener getSelectionChangedListener() {
		return new ISelectionChangedListener() {
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					parentPage.setSelectionIqc((Iqc)ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    };
	}

	public TableViewer getViewer() {
		return viewer;
	}

}
