package com.graly.erp.inv.in.mo;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.Wizard;

import com.graly.erp.inv.model.MovementLineLot;
import com.graly.framework.base.ui.WizardPageExtensionPoint;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class WzMoInWizard extends FlowWizard {
	private static final Logger logger = Logger.getLogger(WzMoInWizard.class);
	protected WzMoInContext context;

	public WzMoInWizard(WzMoInContext context) {
		super(context.getCategory());
		this.context = context;
	}

	public void addPages() {
		Map<String, IConfigurationElement> pageMap = WizardPageExtensionPoint.getPageCategoryRegistry().get(category);
		if (pageMap != null) {
			for (Map.Entry<String, IConfigurationElement> entry : pageMap.entrySet()) {
				try{
					String name = entry.getKey();
					IConfigurationElement configElement = entry.getValue();
					String className = configElement.getAttribute(WizardPageExtensionPoint.A_CLASS);
					String defaultDirect = configElement.getAttribute(WizardPageExtensionPoint.A_DIRECT);
					Class clazz = Class.forName(className);
					Class[] parameterTypes = {String.class, Wizard.class, String.class}; 
					Constructor constructor= clazz.getConstructor(parameterTypes);
					Object[] parameters = {name, this, defaultDirect};
					FlowWizardPage page = (FlowWizardPage)constructor.newInstance(parameters);
					if ("true".equalsIgnoreCase(configElement.getAttribute(WizardPageExtensionPoint.A_ISSTART))) {
						startPage = page;
					}
					addPage(page);
				} catch (Exception e) {
					logger.error("TrackOutWizard : addPages ", e);
				}
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		try {
//			if(context.isMaterialType()) {
//				return true;
//			}
			
			List<Lot> lots = context.getSelectedLots();
			context.setInLineLots(this.parseLineLot(lots));
			return true;
		} catch(Exception e) {
			logger.error("WzMoInWizard : performFinish() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return false;
		}
	}
	
	private List<MovementLineLot> parseLineLot(List<Lot> lots) {
		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
		if(lots != null) {
			MovementLineLot inLineLot = null;
			for(Lot lot : lots) {
				Date now = Env.getSysDate();
				inLineLot = new MovementLineLot();
				inLineLot.setOrgRrn(Env.getOrgRrn());
				inLineLot.setIsActive(true);
				inLineLot.setCreated(now);
				inLineLot.setCreatedBy(Env.getUserRrn());
				inLineLot.setUpdated(now);
				inLineLot.setUpdatedBy(Env.getUserRrn());
				
				inLineLot.setLotRrn(lot.getObjectRrn());
				inLineLot.setLotId(lot.getLotId());
				inLineLot.setMaterialRrn(lot.getMaterialRrn());
				inLineLot.setMaterialId(lot.getMaterialId());
				inLineLot.setMaterialName(lot.getMaterialName());
				inLineLot.setQtyMovement(lot.getQtyCurrent());
				
				lineLots.add(inLineLot);
			}
		}
		return lineLots;
	}
	
	public void setContext(WzMoInContext context) {
		this.context = context;
	}

	public WzMoInContext getContext() {
		return context;
	}
}
