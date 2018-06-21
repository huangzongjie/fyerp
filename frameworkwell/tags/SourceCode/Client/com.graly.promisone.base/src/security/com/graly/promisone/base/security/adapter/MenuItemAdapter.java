package com.graly.promisone.base.security.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.graly.promisone.base.application.Activator;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.base.ui.views.AbstractItemAdapter;
import com.graly.promisone.security.model.ADMenu;

public class MenuItemAdapter extends AbstractItemAdapter {
	
	private static final Logger logger = Logger.getLogger(MenuItemAdapter.class);
	List<ADMenu> list;
	
	public MenuItemAdapter(){
	}
	
	@Override
	public Object[] getChildren(Object object) {
		ADMenu parent = (ADMenu)object;
		List<ADMenu> childrenList = new ArrayList<ADMenu>();
		for(ADMenu menu : list){
			if (menu.getParentId() == null){
				continue;
			}
			if(menu.getParentId().equals(parent.getObjectId())){
				childrenList.add(menu);
			}
		}
		return childrenList.toArray();
	}
	
	@Override
	public Object[] getElements(Object object) {
		list = (List<ADMenu>)object;
		List<ADMenu> root = new ArrayList<ADMenu>();
		for(ADMenu menu : list){
			if(menu.getLevel() == 1){
				root.add(menu);
			}
		}
		return root.toArray();
	}
	
	@Override
	public Object getParent(Object element) {
		ADMenu menu = (ADMenu)element;
		if (menu.getParentId() == null) {
			return null;
		} else {
			for(ADMenu parent : list){
				if(menu.getParentId().equals(parent.getObjectId()))
					return parent;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		ADMenu menu = (ADMenu)element;
		for(ADMenu child : list){
			if (child.getParentId() == null){
				continue;
			} else if(child.getParentId().equals(menu.getObjectId()))
				return true;
		}
		return false;
	}
	
	@Override
	public String getText(Object element) {
		ADMenu menu = (ADMenu)element;
		String lable = (String)I18nUtil.getI18nMessage(menu, "label");
		
		return lable;
	}
	
	@Override
	public String getText(Object object, String id) {
		ADMenu menu = (ADMenu)object;
		return I18nUtil.getI18nMessage(menu, "label");
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		ADMenu adMenu = (ADMenu)object;
		if(ADMenu.MENU_TYPE_MENU.equals(adMenu.getMenuType())) {
			return Activator.getImageDescriptor("open-folder");
		} else if(ADMenu.MENU_TYPE_FUNCTION.equals(adMenu.getMenuType())) {
			return Activator.getImageDescriptor("function");
		} else if(ADMenu.MENU_TYPE_FEATURE.equals(adMenu.getMenuType())) {
			return Activator.getImageDescriptor("feature");
		}
		return null;
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		ADMenu parent = (ADMenu)element;
		for(ADMenu child : list) {
			if(child.getParentId() == null) {
				continue;
			}
			if(child.getParentId().equals(parent.getObjectId())) {
				try {
					return SWTResourceCache.getColor("Folder");
				} catch(Exception ex) {
					logger.error("Exception MenuItemAdapter, not found color Folder", ex);
				}
			}
		}
		try {
			return SWTResourceCache.getColor("Function");
		} catch(Exception ex) {
			logger.error("Exception MenuItemAdapter, not found color Function", ex);
		}
		return null;
	}
	
	public Color getColor(Object object) {
		return new Color(Display.getCurrent(), 0, 67, 255);
	}
	
	@Override
	public Font getFont(Object object, String id) {
		try {
			return SWTResourceCache.getFont("Verdana");
		} catch(Exception ex) {
			logger.error("Exception MenuItemAdapter, not found font Verdana", ex);
		}
		return null;
	}
}