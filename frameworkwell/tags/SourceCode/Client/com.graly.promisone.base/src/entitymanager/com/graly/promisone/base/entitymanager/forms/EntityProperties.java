package com.graly.promisone.base.entitymanager.forms;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;

public class EntityProperties extends EntitySection implements IDetailsPage {

	protected EntityBlock masterParent;
		
	public EntityProperties() {
		super();
    }
	
	public EntityProperties(EntityBlock masterParent, ADTable table) {
    	super(table);
    	this.masterParent = masterParent;
    }
	
	@Override
	public void createContents(Composite parent) {
       super.createContents(form, parent);
	}

	@Override
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		this.disposeContent();
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		Object object = ss.getFirstElement();
		try {
			setAdObject((ADBase)object);
			if (object != null && ((ADBase)object).getObjectId() != null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				setAdObject(entityManager.getEntity((ADBase)object));
			} else {
				setAdObject(createAdObject());
			}
			refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	return;
        }
	}
	
	@Override
	protected void saveAdapter() {
		super.saveAdapter();
		getMasterParent().refresh();
	}
	
	@Override
	protected void deleteAdapter(){
		super.deleteAdapter();
		getMasterParent().refresh();
	}
	
	public void setMasterParent(EntityBlock masterParent) {
		this.masterParent = masterParent;
	}

	public EntityBlock getMasterParent() {
		return masterParent;
	}
	
}
