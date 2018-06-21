package com.graly.mes.prd.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import com.graly.framework.base.application.Activator;

public class ProcedureItemAdapter extends AbstractFlowItemAdapter {
	
	@Override
	public Object[] getElements(Object object) {
		return getChildren(object);
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		if (object instanceof Process){
			return Activator.getImageDescriptor("procedure");
		} 
		return null;
	}

}
