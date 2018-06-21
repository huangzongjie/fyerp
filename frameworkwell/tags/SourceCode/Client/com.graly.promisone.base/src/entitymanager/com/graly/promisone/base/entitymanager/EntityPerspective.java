package com.graly.promisone.base.entitymanager;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class EntityPerspective implements IPerspectiveFactory {
	
	private static final String VIEW_ID = "FunctionView";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		String editorArea = layout.getEditorArea();
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
		left.addView(VIEW_ID);
		layout.getViewLayout(VIEW_ID).setCloseable(false);
	}

}
