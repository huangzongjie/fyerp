package com.graly.promisone.base.ui.action;

import java.lang.reflect.Field;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.jface.action.ContributionItem;

public abstract class AbstractContributionItem extends ContributionItem {
	
	protected boolean fillToolBar = true;
	protected boolean fillCoolBar = true;
	protected boolean fillMenuBar = true;
	protected boolean fillComposite = true;
	
	private boolean toolBarFilled = false;
	private boolean coolBarFilled = false;
	private boolean compositeFilled = false;
	
	private String name = "";
	
	public AbstractContributionItem() {
		super();
	}
	
	public AbstractContributionItem(String id, String name) {
		super();
		setId(id);
		this.name = name;
	}
	
	public AbstractContributionItem(String id, String name, boolean fillToolBar, 
			boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super();
		setId(id);
		this.name = name;		
		this.fillCoolBar = fillCoolBar;
		this.fillToolBar = fillToolBar;
		this.fillMenuBar = fillMenuBar;
		this.fillComposite = fillComposite;
	}
	
	protected abstract Control createControl(Composite parent);
	
	public String getName() {
		return name;
	}			
	public void setName(String name) {
		this.name = name;
	}
	
	protected void setSize() 
	{
		if (fillToolBar && toolBarFilled) 
			getToolItem().setWidth(computeWidth(getControl()));
		
		if (fillCoolBar && coolBarFilled)
			getCoolItem().setSize(computeWidth(getControl()), computeHeight(getControl()));
		
		if (fillComposite && compositeFilled)
			getControl().setSize(computeWidth(getControl()), computeHeight(getControl()));
	}
	  
	@Override
	public void fill(Composite parent) 
	{
		if (fillComposite) {
			control = createControl(parent);
			compositeFilled = true;
			setSize();
		}		
	}

	protected Control control = null;
	public Control getControl() {
		return control;
	}
	
	@Override
	public void fill(CoolBar parent, int index) 
	{
		if (fillCoolBar) {
			coolItem = new CoolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
			coolItem.setControl(control);
			coolBarFilled = true;
			setSize();
		}
	}

	protected CoolItem coolItem = null;
	public CoolItem getCoolItem() {
		return coolItem;
	}
	
	@Override
	public void fill(Menu menu, int index) 
	{
		if (fillMenuBar) {
			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(name);			
		}
	}

	private MenuItem menuItem = null;
	public MenuItem getMenuItem() {
		return menuItem;
	}
	
	@Override
	public void fill(ToolBar parent, int index) 
	{
		if (fillToolBar) {
			toolItem = new ToolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
	  	toolItem.setControl(control);
	  	toolBarFilled = true;
	  	setSize();
		}
	}
	
	protected ToolItem toolItem = null;
	public ToolItem getToolItem() {
		return toolItem;
	}
	
	public void setId(String id) {
		try {
			Field field = ContributionItem.class.getDeclaredField("id"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(this, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	protected int computeWidth(Control control) {
		int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
		return width;
	} 
	  
	protected int computeHeight(Control control) {
		int height = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
		return height;
	}   
	
	@Override
	public void dispose() {
		super.dispose();
		if (control != null)
			control.dispose();
		if (coolItem != null)
			coolItem.dispose();
		if (toolItem != null)
			toolItem.dispose();
		if (menuItem != null)
			menuItem.dispose();
	}
}
