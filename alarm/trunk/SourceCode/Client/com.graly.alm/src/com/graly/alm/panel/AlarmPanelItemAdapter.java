package com.graly.alm.panel;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

import com.graly.alm.model.AlarmPanelMessage;
import com.graly.framework.base.application.Activator;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.views.ListItemAdapter;

public class AlarmPanelItemAdapter<T extends Object> extends ListItemAdapter<Object> {
	public static final String Severity = "severity";
	public static final String Error = "Error";
	public static final String Warning = "Warning";
	public static final String Info = "Info";
	
	public AlarmPanelItemAdapter() {}

	@Override
	public Color getForeground(Object element, String id) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		if(object instanceof AlarmPanelMessage) {
			Object severity = (String)PropertyUtil.getPropertyForString(object, Severity);
			if(Error.equals(severity)) {
				return Activator.getImageDescriptor("error");				
			} else if(Warning.equals(severity)) {
				return Activator.getImageDescriptor("warning");
			} else if(Info.equals(severity)) {
				return Activator.getImageDescriptor("info");
			}
		}
		return null;
	}

}
