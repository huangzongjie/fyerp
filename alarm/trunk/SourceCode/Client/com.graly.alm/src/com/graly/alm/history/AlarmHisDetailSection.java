package com.graly.alm.history;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class AlarmHisDetailSection extends EntitySection{

	public AlarmHisDetailSection(ADTable adTable, ADBase adObject) {
		this.table = adTable;
		this.adObject = adObject;
	}

	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent);
		refresh();
	}
	
	public void createToolBar(Section section) {
		
	}
}
