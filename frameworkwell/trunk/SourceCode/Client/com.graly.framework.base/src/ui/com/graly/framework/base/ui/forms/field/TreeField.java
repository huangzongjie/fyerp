package com.graly.framework.base.ui.forms.field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.security.views.MenuTreeManager;
import com.graly.framework.security.model.ADAuthority;

public class TreeField extends AbstractField {
	
	protected TreeViewer viewer;
	protected Tree tree;
	protected MenuTreeManager manager;
	protected List<ADAuthority> list;
	private List<Object> authorities = new ArrayList<Object>();
	
	public TreeField(String id, String label, MenuTreeManager manager, List<ADAuthority> list) {
		super(id);
		setLabel(label);
		this.manager = manager;
		this.list = list;
	}

	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
		String labelStr = getLabel();
        if (labelStr != null) {
        	mControls = new Control[2];
        	Label label = toolkit.createLabel(composite, labelStr);
            mControls[0] = label;
            i = 1;
        } else {
        	mControls = new Control[1];
        }
       
        viewer = (TreeViewer)manager.createViewer(new Tree(composite, 
        		SWT.SINGLE|SWT.CHECK | SWT.BORDER | SWT.H_SCROLL),	toolkit);
        viewer.setInput(list);
        tree = viewer.getTree();
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.heightHint = tree.getItemHeight () * 20;
        gd.widthHint = 60;
        tree.setLayoutData(gd);
        
        refresh();
        
        mControls[i] = tree;
		viewer.expandAll();
		viewer.getTree().addSelectionListener(selectionListener);
	}

	private SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeItem item = (TreeItem) e.item;
			tree.setSelection(item);
			authorities = (value == null ? new ArrayList<Object>() : (List<Object>)value);
			checkMenu(item,authorities);
			
			setValue(authorities);
		}
	};
	
	private void checkMenu(TreeItem item,List<Object> authorities){
		checkMenu(item,authorities,false,false);
	}
	
	private void checkMenu(TreeItem item, List<Object> authorities,boolean isParent,boolean isChild){
		if(!isChild){
			if(item.getItemCount() != 0)
				isParent = true;			
		}
		
		if(item.getParentItem() != null){
			isChild = true;
		} else {
			isChild = false;
		}
		
		if (isParent) { // �Ǹ��ڵ�
			TreeItem[] children = item.getItems();
			Object o = item.getData();
			if (item.getChecked()) { // ����Ǹ��ڵ㣬��ôѡ�иø��ڵ�ʱ�������е��ӽڵ�ҲҪ��ѡ��
				if(!authorities.contains(o))
					authorities.add(o);// ��ѡ�еĽڵ���뵽authorities��
				for (int i = 0; i < children.length; i++) {
					children[i].setChecked(true);
					checkMenu(children[i],authorities);
				}
			} else {// ȡ��ѡ��ʱ���������ӽڵ�Ҳͬʱ��ȡ��ѡ��
				if(authorities.contains(o))
					authorities.remove(o);//���ѡ�еĽڵ�֮ǰ��authorities�У����Ƴ�
				for (int i = 0; i < children.length; i++) {
					children[i].setChecked(false);
					checkMenu(children[i],authorities);
				}
			}
		} 
		if (isChild) {// ���Ǹ��ڵ�
			TreeItem[] brotheres = item.getParentItem().getItems();// ����������е��ֵܽڵ�
			Object o = item.getData();
			if (item.getChecked()) {
				if(!authorities.contains(o))
					authorities.add(o);// ��ѡ�еĽڵ���뵽authorities��
			} else {
				if(authorities.contains(o))//���֮ǰauthorities��û�а����ýڵ������
					authorities.remove(o);
			}
			boolean hasBrotherChecked = false;
			for (TreeItem brother : brotheres) {// �ж������ֵܽڵ�����û�б�ѡ�е�
				if (brother.getChecked()) {
					hasBrotherChecked = true;
					break;
				}				
			}
			
			if(hasBrotherChecked){
				item.getParentItem().setChecked(true);
				Object po = item.getParentItem().getData();
				if(!authorities.contains(po))
					authorities.add(po);
				checkMenu(item.getParentItem(),authorities,false,true);
			} else {
				item.getParentItem().setChecked(false);
				Object po = item.getParentItem().getData();
				if(authorities.contains(po))
					authorities.remove(po);
				checkMenu(item.getParentItem(),authorities,false,true);
			}			
		}
	}
	
	@Override
	public void refresh() {
		if (tree.getItemCount() > 1){
			tree.setSelection(tree.getItem(0));
		}
		authorities = (value == null ? new ArrayList<Object>() : (List<Object>)value);
        if (authorities != null) {
        	List<TreeItem> allTreeItems = new ArrayList<TreeItem>();
        	TreeItem[] roots = tree.getItems();
        	for(TreeItem node : roots){
        		copyChildrenList(node, allTreeItems);
        	}
        	for(TreeItem treeItem : allTreeItems){
        		treeItem.setChecked(false);
        	}
        	for(Object menuItem : authorities){
				for(TreeItem treeItem : allTreeItems){     		
					if(treeItem.getData().equals(menuItem)){
						treeItem.setChecked(true);
						break;
					}
				}
        	}
        }
	}
	
	@Override
	public String getFieldType() {
		return "tree";
	}
	
	private void copyChildrenList(TreeItem item,List<TreeItem> children){
		if (item.getItemCount()!=0){
			children.add(item);
			for(TreeItem node : item.getItems()){
				copyChildrenList(node,children);
			}
		} else {
			children.add(item);
		}
	}
}
