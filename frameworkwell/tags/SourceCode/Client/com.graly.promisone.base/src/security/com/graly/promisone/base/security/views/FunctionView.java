package com.graly.promisone.base.security.views;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.base.entitymanager.editor.EntityEditorInput;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.base.ui.views.RefreshTreeView;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADEditor;
import com.graly.promisone.security.model.ADMenu;
import com.graly.promisone.security.model.ADOrg;

public class FunctionView extends RefreshTreeView {
	
	private static final Logger logger = Logger.getLogger(FunctionView.class);
	private String viewerTitle = Message.getString("common.function_list");
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(final Composite parent) {
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
						if(event.getElement() instanceof ADMenu){
							ADMenu menu = (ADMenu)event.getElement();
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
							ADMenu menu = (ADMenu)selection.getFirstElement();
							if (ADMenu.MENU_TYPE_FUNCTION.equals(menu.getMenuType())){
								open(menu);
							}
							if(ADMenu.MENU_TYPE_MENU.equals(menu.getMenuType())) {
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
			final List<ADMenu> list = securityManager.getUserMenu(Env.getOrgId(), Env.getUserId());

			viewer.setInput(list);
		} catch (Exception e) {
			logger.error("StepTreeView refrsh error:", e);
		}
	}
	
	public void open(ADMenu menu){
		try {
			ADManager manager = Framework.getService(ADManager.class);
			ADEditor editor = new ADEditor();
			editor.setObjectId(menu.getEditorId());
			editor = (ADEditor)manager.getEntity((ADBase)editor);
			if (ADMenu.ACTION_TYPE_EDITOR.equalsIgnoreCase(menu.getAction())){
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				for (IEditorReference editorRef : page.getEditorReferences()){
					if (editorRef.getId().equals(editor.getEditorId())){
						if (editorRef.getEditorInput() instanceof EntityEditorInput){
							EntityEditorInput input = (EntityEditorInput)editorRef.getEditorInput();
							if (input.getTableId() == Long.valueOf(editor.getPARAM1())){
								page.activate(editorRef.getEditor(false));
								return;
							}
						}
					}
				}
				page.openEditor(new EntityEditorInput(Long.valueOf(editor.getPARAM1())), editor.getEditorId());
			
			} else if (ADMenu.ACTION_TYPE_DIALOG.equalsIgnoreCase(menu.getAction())) {
				String className = editor.getEditorId();
				Class clazz = Class.forName(className);
				Class[] parameterTypes;
				Constructor constructor;
				
				String param1 = editor.getPARAM1();					
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
				
				Object obj = constructor.newInstance(parameters);
				if (obj instanceof Dialog) {
					Dialog dialog = (Dialog)obj;
					dialog.open();
				} else {
					logger.error("MenuTreeViewer : expect dialog but found " + obj.toString());
				}
			}
		} catch (Exception e) {
			logger.error("MenuTreeViewer : open", e);
		}
	}
}
