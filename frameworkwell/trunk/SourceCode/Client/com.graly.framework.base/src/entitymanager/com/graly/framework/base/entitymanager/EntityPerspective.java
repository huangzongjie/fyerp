package com.graly.framework.base.entitymanager;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

import com.graly.framework.base.ui.FrameworkPerspective;

public class EntityPerspective extends FrameworkPerspective {
	
	private static final String FUNCTION_VIEW_ID = "FunctionView";

	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		super.createInitialLayout(layout);
		layout.setEditorAreaVisible(true);

		addLeftView(FUNCTION_VIEW_ID, false);
		
		registerPanel();
	}


	protected IFolderLayout createBottomFolder(String refId) {
		return layout.createFolder(FOLDER_BOTTOM, IPageLayout.BOTTOM, 0.8f, refId);
	}
	
	public String getPerspectiveId() {
		return "Function";
	}
}
