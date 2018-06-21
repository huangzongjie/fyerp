package com.graly.framework.base.entitymanager.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.util.Env;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

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
	            List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), input.getTable().getObjectRrn(), 
	            		Env.getMaxResult(), input.getWhereClause(), input.getOrderByClause());
	            return list.toArray();
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        }
		} else if(object instanceof List){
			return ((List)object).toArray();
		}else {
			logger.error("Expect EntityItemInput or List, but found " + object.toString());
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
				if(property instanceof Date){
					return I18nUtil.formatDate((Date) property);
				}
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
