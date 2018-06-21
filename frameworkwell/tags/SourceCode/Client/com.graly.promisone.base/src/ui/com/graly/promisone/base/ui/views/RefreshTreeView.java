package com.graly.promisone.base.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.prd.model.Step;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.wip.client.LotManager;
import com.graly.promisone.wip.model.Lot;

public abstract class RefreshTreeView extends ViewPart {
	
	public abstract void refresh();
	
}
