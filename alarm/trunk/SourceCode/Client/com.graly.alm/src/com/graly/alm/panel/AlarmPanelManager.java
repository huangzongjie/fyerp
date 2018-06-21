package com.graly.alm.panel;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;


public class AlarmPanelManager extends TableListManager {
	private static final Logger logger = Logger.getLogger(AlarmPanelManager.class);
	
	private String[] columns;

	public AlarmPanelManager(ADTable adTable) {
		super(adTable);
		super.setStyle(SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	}
	
	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
		try {
			factory.registerAdapter(Object.class, new AlarmPanelItemAdapter<ADBase>());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return factory;
	}
}
