package com.graly.framework.base.ui.custom;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.graly.framework.base.ui.action.AbstractContributionItem;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class TextContribution extends AbstractContributionItem {
	
	private Logger logger = Logger.getLogger(TextContribution.class);
	private Composite wrapper;
	private Label label;
	private Text text;
	private int mCols;
	private Image image;
	private FontMetrics fm;
	
	public TextContribution(String name, boolean fillToolBar, boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super(TextContribution.class.getName(), name, fillToolBar, fillCoolBar, fillMenuBar, fillComposite);
		init();
	}

	public TextContribution(String name) {
		super(TextContribution.class.getName(), name);
		init();
	}
	
	private void init() {
	}
	
	@Override
	protected Control createControl(Composite parent) {
		wrapper = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		wrapper.setLayout(layout);
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 200;
		wrapper.setLayout(layout);
		wrapper.setLayoutData(layoutData);
		label = new Label(wrapper, SWT.ICON);
		label.setImage(getImage());
		label.setLayoutData(new GridData());
		
		text = new Text(wrapper, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fm = getFontMetric(text);
		gd.heightHint = Dialog.convertHeightInCharsToPixels(fm, 1);
		gd.widthHint = getColumns();
		text.setLayoutData(gd);
	
		return wrapper;
	}
	
	public Image getImage() {
		if (image == null) {
			SWTResourceCache.getImage("time");
		}
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public int getColumns() {
		if (mCols <= 0) {
			mCols = 16;
		}
		return Dialog.convertWidthInCharsToPixels(fm, mCols);
	}
	
	public void setColumns(int mCols) {
		this.mCols = mCols;
	}
	
    public static FontMetrics getFontMetric(Control ctrl) {
        FontMetrics fm;
        GC gc = new GC(ctrl);
        fm = gc.getFontMetrics();
        gc.dispose();
        return fm;
    }
    
    public void addTextKeyListener(KeyListener listener) {
    	text.addKeyListener(listener);
    }
    
    public void setText(String text) {
    	this.text.setText(text);
    }
    
    public String getText() {
    	return this.text.getText();
    }
    
	public void setFocus() {
		text.setFocus();
	}	
}
