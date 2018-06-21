package com.graly.promisone.base.entitymanager.adapter;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.views.AbstractItemAdapter;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.base.ui.util.Env;

public class EntityItemAdapter extends AbstractItemAdapter {
	
	private static final Logger logger = Logger.getLogger(EntityItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];
	
	public EntityItemAdapter(){	
	}
	
	@Override
	public Color getBackground(Object element, String id) {
		return null;
	}

	@Override
	public Object[] getChildren(Object object) {
		if (object instanceof EntityItemInput){
			EntityItemInput input = (EntityItemInput) object;
			try {
	        	ADManager manager = Framework.getService(ADManager.class);
	            List<ADBase> list = manager.getEntityList(Env.getOrgId(), input.getTable().getObjectId(), 
	            		Env.getMaxResult(), input.getWhereClause(), input.getOrderByClause());
	            return list.toArray();
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        }
		} else {
			logger.error("Exception EntityItemInput, but found " + object.toString());
		}
        return EMPTY;
	}
	
	@Override
	public Object[] getElements(Object object) {
		return getChildren(object);
	}
	
	@Override
	public Color getColor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont(Object object, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(Object element, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object object, String id) {
		if (object != null && id != null){
			try{
				Object property = PropertyUtil.getPropertyForString(object, id);
				return (String)property;
			} catch (Exception e){
				logger.error(e.getMessage(), e);
			}
		}
		return "";
	}

	@Override
	public boolean hasChildren(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isContainer(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

}
