package com.graly.erp.vdm.vendormaterial;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;

import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.runtime.Framework;

public class VendorMaterialTableManager extends EntityTableManager {
	private static final Logger logger = Logger.getLogger(VendorMaterialTableManager.class);
	private static final Object[] EMPTY = new Object[0];

	public VendorMaterialTableManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new VendorMaterialItemAdapter());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
	}

	public class VendorMaterialItemAdapter extends EntityItemAdapter {
		
		public VendorMaterialItemAdapter(){	
		}
		
		@Override
		public Color getBackground(Object element, String id) {
			return null;
		}

		@Override
		public Object[] getChildren(Object object) {
			if(object instanceof MainVendorOnlyInput) {
				MainVendorOnlyInput input = (MainVendorOnlyInput)object;
				if(input.isQueryMainVendorOnly()) {
					try {
						VDMManager vdmManager = Framework.getService(VDMManager.class);
						List<VendorMaterial> list = vdmManager.getOnlyMainVendorMaterialList(Env.getOrgRrn(), Env.getMaxResult());
						input.setDisplaCount(list.size());
						return list.toArray();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			} else if (object instanceof EntityItemInput){
				return super.getChildren(object);				
			} else {
				logger.error("Exception: Expect EntityItemInput or MainVendorOnlyInput, but found " + object.toString());
			}
	        return EMPTY;
		}
	}
}
