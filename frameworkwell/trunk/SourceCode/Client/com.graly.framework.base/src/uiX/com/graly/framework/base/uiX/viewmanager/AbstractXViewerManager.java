package com.graly.framework.base.uiX.viewmanager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractXViewerManager {
	protected StructuredViewer viewer;
	protected IXViewerAdapter adapter;
	
	public AbstractXViewerManager(IXViewerAdapter adapter) {
		super();
		this.adapter = adapter;
	}

	public StructuredViewer getViewer(Composite parent){
		viewer = createViewer(parent);
		adapterViewer();
		return viewer;
	}
	
	public StructuredViewer getViewer(Composite parent, IXViewerAdapter adapter){
		setAdapter(adapter);
		viewer = createViewer(parent);
		adapterViewer();
		return viewer;
	}
	
	protected void adapterViewer(){
		Assert.isTrue(viewer != null);
		Assert.isTrue(adapter != null);
		
		if(viewer == null) return;
		if(adapter == null) return;
		viewer.setContentProvider(adapter);
		viewer.setLabelProvider(adapter);
	}
	
	protected abstract StructuredViewer createViewer(Composite parent);

	public void setAdapter(IXViewerAdapter adapter) {
		this.adapter = adapter;
	}
	
	public IXViewerAdapter getAdapter() {
		return adapter;
	}
}
