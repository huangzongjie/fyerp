package com.graly.framework.base.entitymanager.views;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;


public class TableQueryDialog extends InClosableTitleAreaDialog {

	protected Table table;
	Text queryText;
	
	public TableQueryDialog(Shell parent) {
        super(parent);
        this.setShellStyle(getShellStyle() | SWT.MODELESS);
        this.setBlockOnOpen(false);
    }
	
	public TableQueryDialog(Shell parent, Table table){
		this(parent);
		this.table = table;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.find_Title"));
        setMessage(Message.getString("common.find_Message"));
		Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite center = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 10;
		layout.marginWidth = 5;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;//相邻两个控件的水平间距
		center.setLayout(layout);
		center.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.createLabel(center, Message.getString("common.find_by"));
		queryText = toolkit.createText(center, "");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		queryText.setLayoutData(gd);
		return center;
	}
	
	public void setVisible (boolean visible) {
		Shell shell = this.getShell();
		shell.setVisible(visible);
	}
	
	@Override
	protected void okPressed() {
		String queryString = queryText.getText();
		TableItem item = null;
		for (int i = table.getSelectionIndex() + 1; i < table.getItemCount(); i++) {
			item = table.getItem(i);
			for (int j = 0; j < table.getColumnCount(); j++) {
				String text = item.getText(j);
				if (text != null) {
					if (text.indexOf(queryString) >= 0) {
						table.setSelection(item);
						setReturnCode(OK);
						return;
					}
				}
			}
		}
		UI.showInfo(Message.getString("common.nostring_found"));
	}
	
	@Override
	protected void cancelPressed() {
		this.setVisible(false);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.NEXT_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}
}
