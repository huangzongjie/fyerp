package com.graly.framework.base.security.views;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.ContainerPlaceholder;
import org.eclipse.ui.internal.ILayoutContainer;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.ViewStack;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.ui.DialogExtensionPoint;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.views.RefreshView;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.client.SecurityManager;
import com.graly.framework.security.model.ADAuthority;
import com.graly.framework.security.model.ADEditor;

public class FunctionView extends RefreshView {
	
	private static final Logger logger = Logger.getLogger(FunctionView.class);
	private String viewerTitle = Message.getString("common.function_list");
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		try {
			this.setPartName(viewerTitle);
			parent.setLayout(new GridLayout(1, false));
			
			MenuTreeManager treeManager = new MenuTreeManager();
			viewer = (TreeViewer)treeManager.createViewer(parent, new FormToolkit(parent.getDisplay()));
			viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
			viewer.addTreeListener(new ITreeViewerListener(){
				@Override
				public void treeCollapsed(TreeExpansionEvent event) {					
				}
				@Override
				public void treeExpanded(TreeExpansionEvent event) {//为了解决点[+]展开树时会触发selectionChanged事件的问题
						if(event.getElement() instanceof ADAuthority){
							ADAuthority menu = (ADAuthority)event.getElement();
							ISelection sel = new StructuredSelection(menu);
							viewer.setSelection(sel);
						}
				}
				
			});
			viewer.addSelectionChangedListener(new ISelectionChangedListener(){
				//监听菜单项选择事件
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					if(event.getSelection().isEmpty()) {
						return;
					}
					if(event.getSelection() instanceof TreeSelection) {
						TreeSelection selection = (TreeSelection)event.getSelection();
						if (selection.getFirstElement() != null){
							ADAuthority authority = (ADAuthority)selection.getFirstElement();
							if (ADAuthority.AUTHORITY_TYPE_FUNCTION.equals(authority.getAuthorityType())){
								open(authority);
							}
							if(ADAuthority.AUTHORITY_TYPE_MENU.equals(authority.getAuthorityType())) {
								//open(menu);
							}
						}
					}
				}				
			});
			
			refresh();
		} catch (Exception e) {
			logger.error("FunctionView createPartControl error:", e);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void refresh() {
		try {
			SecurityManager securityManager = Framework.getService(SecurityManager.class);
			final List<ADAuthority> authoritySet = securityManager.getUserMenuTree(Env.getOrgRrn(), Env.getUserRrn());

			viewer.setInput(authoritySet);
		} catch (Exception e) {
			logger.error("StepTreeView refrsh error:", e);
		}
	}
	
	public void open(ADAuthority menu){
		try {
			ADManager manager = Framework.getService(ADManager.class);
			ADEditor editor = new ADEditor();
			editor.setObjectRrn(menu.getEditorRrn());
			editor = (ADEditor)manager.getEntity((ADBase)editor);
			
			if (ADAuthority.ACTION_TYPE_EDITOR.equalsIgnoreCase(menu.getAction())){
				openEditor(editor, menu);
			} else if (ADAuthority.ACTION_TYPE_DIALOG.equalsIgnoreCase(menu.getAction())) {
				String param1 = editor.getPARAM1();	
				String param5 = editor.getPARAM5();
				Object parent = null;
				if (param5 != null) {
					ADEditor fEditor = new ADEditor();
					fEditor.setObjectRrn(Long.parseLong(param5));
					fEditor = (ADEditor)manager.getEntity((ADBase)fEditor);
					parent = openEditor(fEditor, menu);
				}
				
				String className = editor.getEditorId();
				Object obj = DialogExtensionPoint.getDialog(className);
				if (obj != null){
					ExtendDialog eDialog = (ExtendDialog)obj;
					eDialog.setTableId(param1);
					eDialog.setParent(parent);
					eDialog.open();
				} else {
					Class clazz = Class.forName(className);
					Class[] parameterTypes;
					Constructor constructor;
					
					Object[] parameters;
					if(param1 != null){
						parameters = new Object[]{UI.getActiveShell(), param1};
						parameterTypes = new Class[]{Shell.class,String.class}; 
						constructor = clazz.getConstructor(parameterTypes);
					} else {
						parameters = new Object[]{UI.getActiveShell()};
						parameterTypes = new Class[]{Shell.class}; 
						constructor = clazz.getConstructor(parameterTypes);
					}
					obj = constructor.newInstance(parameters);
					if (obj instanceof Dialog) {
						Dialog dialog = (Dialog)obj;
						dialog.open();
					} else {
						logger.error("MenuTreeViewer : expect dialog but found " + obj.toString());
					}
				}
			} else if("V".equalsIgnoreCase(menu.getAction())){
				openView(editor);
			}
		} catch (Exception e) {
			logger.error("MenuTreeViewer : open", e);
		}
	}

	private IEditorPart openEditor(ADEditor editor, ADAuthority menu) throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference editorRef : page.getEditorReferences()){
			if (editorRef.getId().equals(editor.getEditorId())){
				if (editorRef.getEditorInput() instanceof EntityEditorInput){
					EntityEditorInput input = (EntityEditorInput)editorRef.getEditorInput();
					if (input.getTableId() == Long.valueOf(editor.getPARAM1())){
						IEditorPart part = editorRef.getEditor(false);
						page.activate(part);
						return part;
					}
				}
			}
		}
		return page.openEditor(new EntityEditorInput(Long.valueOf(editor.getPARAM1()), menu.getName()), editor.getEditorId());
	}
	

	@SuppressWarnings("restriction")
	private void openView(ADEditor editor){
		WorkbenchWindow wbw = (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Perspective persp = ((WorkbenchPage)wbw.getActivePage()).getActivePerspective();
		//FastViewManager fvMgr = persp.getFastViewManager();
		
		LayoutPart part = persp.getPresentation().findPart(editor.getEditorId(), null);
		ILayoutContainer container = part.getContainer();
		if (container instanceof ContainerPlaceholder) {
			ViewStack stack = (ViewStack) ((ContainerPlaceholder)container).getRealContainer();
			stack.setMinimized(false);
		}
	}
}
