package com.graly.erp.inv.out.serialnum;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

/**
 * @author Administrator
 * 出库序列号对话框
 */
public class OutSerialNumberDialog extends LotDialog {
	public static final String ADTABLE_MOVEMENT_LINE_OUT_SERIAL = "INVMovementLineOutSerial";
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 450;
	protected MovementOut out;
	protected MovementLine line;

	public OutSerialNumberDialog(Shell shell) {
		super(shell);
	}
	
	public OutSerialNumberDialog(Shell shell, MovementOut out, MovementLine line) {
		this(shell);
		this.out = out;
		this.line = line;
	}

	@Override
	protected void createSection(Composite composite) {
		lotSection = new OutSerialNumberSection(table, this, out, line);
		lotSection.createContents(managedForm, composite);
	}
	
	protected void setTitleMessage() {
		String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label"));
		setTitle(editorTitle);
	}

	@Override
	public String getADTableName() {
		return ADTABLE_MOVEMENT_LINE_OUT_SERIAL;
	}

	@Override
	protected boolean isSureExit() {
		return true;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
}
