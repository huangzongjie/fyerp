package com.graly.mes.prd.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.node.EndState;
import com.graly.mes.prd.workflow.graph.node.StartState;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.runtime.Framework;

public abstract class AbstractFlowItemAdapter extends AbstractItemAdapter {
	
	private static final Logger logger = Logger.getLogger(AbstractFlowItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];

	@Override
	public Object[] getChildren(Object object) {
		if (object instanceof ProcessDefinition){
			ProcessDefinition pf = (ProcessDefinition)object;
			try {
				if(pf != null){
					PrdManager prdManager = Framework.getService(PrdManager.class);
					List<Node> nodes;
					if(pf.getObjectRrn() != null)
						nodes = prdManager.getProcessDefinitionChildern(pf);
					else
						nodes = pf.getChildren();
					List<Node> list = new ArrayList<Node>();
					for (Node node : nodes) {
						if (node instanceof StartState) {
							continue;
						} else if (node instanceof EndState) {
							continue;
						} else {
							list.add(node);
						}
					}
					return list.toArray();
				}
	            
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        }
		} else {
			logger.error("Expect ProcessDefinition, but found " + object.toString());
		}
        return EMPTY;
	}

	@Override
	public Object getParent(Object object) {
		return null;
	}

	@Override
	public boolean hasChildren(Object object) {
		Object[] children = getChildren(object);
		if (children != null && children.length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ProcessDefinition){
			ProcessDefinition pf = (ProcessDefinition)element;
			String description = (pf.getDescription() == null || "".equals(pf.getDescription().trim())
					 ? "" : " <" + pf.getDescription() + ">");
			return pf.getName() + description;
		}
		return "";
	}
	
	@Override
	public String getText(Object object, String id) {
		return getText(object);
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		return null;
	}
	
	public Color getColor(Object object) {
		return new Color(Display.getCurrent(), 0, 67, 255);
	}
	
	@Override
	public Font getFont(Object object, String id) {
		try {
			return SWTResourceCache.getFont("Verdana");
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
