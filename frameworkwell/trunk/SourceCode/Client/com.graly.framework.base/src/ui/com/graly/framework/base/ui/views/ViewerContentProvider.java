package com.graly.framework.base.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ViewerContentProvider extends LabelProvider implements
		IStructuredContentProvider, ITreeContentProvider, ITableLabelProvider,
		ITableFontProvider, ITableColorProvider {
	
	public final static Object[] EMPTY_ARRAY = new Object[0];
	protected ItemAdapterFactory adapterFactory;
	protected StructuredViewerManager manager;
	
	 public ViewerContentProvider(){//增加一个无参的构造器
		 
	 }
	
	 public ViewerContentProvider(StructuredViewerManager manager) {
		this.manager = manager;
		adapterFactory = manager.adapterFactory;
	}
	 
	@Override
	public Object[] getElements(Object inputElement) {
		ItemAdapter adapter = adapterFactory.getAdapter(inputElement.getClass());
        assert adapter != null;
        if (adapter == null) {
            return EMPTY_ARRAY;
        }
        return adapter.getElements(inputElement);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
	
	@Override
    public String getText(Object object) {
        ItemAdapter adapter = adapterFactory.getAdapter(object.getClass());
        assert adapter != null;
        if (adapter == null) {
            return object.toString();
        }
        String text = adapter.getText(object);
        return text != null ? text : "";
    }
	
	public String getText(Object object, String id){
		ItemAdapter adapter = adapterFactory.getAdapter(object.getClass());
        assert adapter != null;
        if (adapter == null) {
            return "";
        }
        String text = adapter.getText(object, id);
        return text != null ? text : "";
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		ItemAdapter adapter = adapterFactory.getAdapter(parentElement.getClass());
        assert adapter != null;
        if (adapter == null) {
            return EMPTY_ARRAY;
        }
        return adapter.getChildren(parentElement);
	}
	
	@Override
	public Object getParent(Object element) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return null;
        }
        return adapter.getParent(element);
	}

	@Override
	public boolean hasChildren(Object element) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return false;
        }
        return adapter.hasChildren(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return null;
        }
        String properties[] = manager.getColumns();
        ImageDescriptor descriptor = null;
        if (properties != null && columnIndex < properties.length) {
            descriptor = adapter.getImageDescriptor(element, manager.getColumns()[columnIndex]);
        }

        if (descriptor == null && columnIndex == 0) {
            descriptor = adapter.getImageDescriptor(element);
        }
        if (descriptor == null) {
            return null;
        }

//        return getImage(descriptor, element);
        return descriptor.createImage();
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return columnIndex == 0 ? element.toString() : "";
        }
        String properties[] = manager.getColumns();
        String text = null;
        if (properties != null && columnIndex < properties.length) {
            text = adapter.getText(element, manager.getColumns()[columnIndex]);
        }

        if (text == null && columnIndex == 0) {
            text = adapter.getText(element);
        }
        return text != null ? text : "";
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return null;
        }
        String properties[] = manager.getColumns();
        Font font = null;
        if (properties != null && columnIndex < properties.length) {
            font = adapter.getFont(element, manager.getColumns()[columnIndex]);
        }

        if (font == null && columnIndex == 0) {
            font = adapter.getFont(element);
        }
        return font;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return null;
        }
        String properties[] = manager.getColumns();
        Color color = null;
        if (properties != null && columnIndex < properties.length) {
            color = adapter.getBackground(element,
                    manager.getColumns()[columnIndex]);
        }
        return color;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		ItemAdapter adapter = adapterFactory.getAdapter(element.getClass());
        assert adapter != null;
        if (adapter == null) {
            return null;
        }
        String properties[] = manager.getColumns();
        Color color = null;
        if (properties != null && columnIndex < properties.length) {
            color = adapter.getForeground(element,
                    manager.getColumns()[columnIndex]);
        }
        return color;
	}

}
