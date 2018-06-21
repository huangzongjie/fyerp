package com.graly.mes.prd.designer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.graly.mes.prd.designer.common.Constants;

public abstract class AbstractGraphicalViewer extends ScrollingGraphicalViewer {
	
	private AbstractGraphForm form;
	public AbstractGraphicalViewer(AbstractGraphForm form) {
		this.form = form;
		setKeyHandler(new GraphicalViewerKeyHandler(this));
		setRootEditPart(new ScalableFreeformRootEditPart());
		prepareGrid();
	}
	
	protected abstract void initEditPartFactory();
	
	private void prepareGrid() {
		getLayerManager().getLayer(LayerConstants.GRID_LAYER).setForegroundColor(Constants.veryLightBlue);
	}
	
	public void initControl(Composite parent) {
		super.createControl(parent);
		getControl().setBackground(ColorConstants.white);
		form.getEditDomain().addViewer(this);
		initEditPartFactory();
		setContents(form.getRootContainer());
		initKeyAction();
	}
	
	public void initKeyAction(){
		KeyHandler keyHandler = this.getKeyHandler();
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0), new DeleteAction(this));
	}
}
