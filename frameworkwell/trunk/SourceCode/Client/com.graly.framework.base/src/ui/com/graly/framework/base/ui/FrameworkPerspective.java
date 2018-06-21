package com.graly.framework.base.ui;

import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.graly.framework.base.ui.PanelExtensionPoint;

public abstract class FrameworkPerspective implements IPerspectiveFactory {
	
	public static String FOLDER_LEFT = "left";
	public static String FOLDER_RIGHT = "right";
	public static String FOLDER_TOP = "top";
	public static String FOLDER_BOTTOM = "bottom";
	
	protected static IPageLayout layout = null;
	protected IFolderLayout left = null;
	protected IFolderLayout rigth = null;
	protected IFolderLayout top = null;
	protected IFolderLayout bottom = null;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		setLayout(layout);
	}
	
	protected IFolderLayout createLeftFolder(String refId) {
		return layout.createFolder(FOLDER_LEFT, IPageLayout.LEFT, 0.25f, refId);
	}
	
	protected IFolderLayout createRightFolder(String refId) {
		return layout.createFolder(FOLDER_RIGHT, IPageLayout.RIGHT, 0.8f, refId);
	}
	
	protected IFolderLayout createTopFolder(String refId) {
		return layout.createFolder(FOLDER_TOP, IPageLayout.TOP, 0.8f, refId);
	}
	
	protected IFolderLayout createBottomFolder(String refId) {
		return layout.createFolder(FOLDER_BOTTOM, IPageLayout.BOTTOM, 0.2f, refId);
	}
	
	public void addLeftView(String viewId, boolean closeable) {
		if (left == null) {
			left = createLeftFolder(layout.getEditorArea());
		}
		left.addView(viewId);
		layout.getViewLayout(viewId).setCloseable(closeable);
	}
	
	public void addRightView(String viewId, boolean closeable) {
		if (rigth == null) {
			rigth = createRightFolder(layout.getEditorArea());
		}
		rigth.addView(viewId);
		layout.getViewLayout(viewId).setCloseable(closeable);
	}
	
	public void addTopView(String viewId, boolean closeable) {
		if (top == null) {
			top = createTopFolder(layout.getEditorArea());
		}
		top.addView(viewId);
		layout.getViewLayout(viewId).setCloseable(closeable);
	}
	
	public void addBottomView(String viewId, boolean closeable) {
		if (bottom == null) {
			bottom = createBottomFolder(layout.getEditorArea());
		}
		bottom.addPlaceholder(viewId + ":*");   
		bottom.addView(viewId);
		layout.getViewLayout(viewId).setCloseable(closeable);
	}
	
	protected void registerPanel() {
		Collection<IConfigurationElement> panels = PanelExtensionPoint.getPanels();
		for (IConfigurationElement panel : panels) {
			String perspective = panel.getAttribute(PanelExtensionPoint.A_PERSPECTIVE);
			String viewId = panel.getAttribute(PanelExtensionPoint.A_ID);
			if (perspective == null || perspective.trim().length() == 0 
				|| perspective.equals(getPerspectiveId())) {
				addBottomView(viewId, Boolean.valueOf(panel.getAttribute(PanelExtensionPoint.A_CLOSEABLE)));
			}
		}
	}
	
	public abstract String getPerspectiveId();
	
	public static IPageLayout getLayout() {
		return layout;
	}

	public static void setLayout(IPageLayout layout) {
		FrameworkPerspective.layout = layout;
	}

}
