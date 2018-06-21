package com.graly.framework.base.ui.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;

import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;


public class ListItemAdapter<T extends Object> extends AbstractItemAdapter {
	private List<T> elements = new ArrayList<T>();
	
	public ListItemAdapter() {}
	
	public ListItemAdapter(List<T> initialElements) {
        elements = initialElements==null ? new ArrayList<T>() : initialElements;
    }
	
	public void addElement(T obj) {
        elements.add(obj);
    }
	
	public void removeElement(T obj) {
        elements.remove(obj);
    }
	
	public void addElements(List<T> elements) {
        this.elements.addAll(elements);
    }
	
	public void removeElements(List<T> elements) {
        this.elements.removeAll(elements);
    }
	
	@Override
	public Object[] getElements(Object inputElement) {
		elements = (List<T>)inputElement;
		return elements.toArray();
    }
	
	public void setElements(List<T> elements) {
        this.elements = elements==null ? new ArrayList<T>() : elements;
    }
    
    public int getNumberOfElements() {
        return this.elements.size();
    }
    
    @Override
	public String getText(Object element) {
    	if(element instanceof String) {
    		return (String)element;
    	}
		return "";
	}
    
    @Override
	public String getText(Object object, String id) {
    	if(object instanceof String) {
    		return (String)object;
    	}
		if (object != null && id != null){
			try{
				Object property = PropertyUtil.getPropertyForString(object, id);
				return (String)property;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return "";
	}
    
    public void dispose() {}
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

}
