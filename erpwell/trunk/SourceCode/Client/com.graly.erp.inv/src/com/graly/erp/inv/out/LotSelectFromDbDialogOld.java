package com.graly.erp.inv.out;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.mes.wip.model.Lot;
/**
 * 
 * @author Administrator
 * ѡ�����ζԻ�������������ʱ��������һ�����ⵥ�У�Ȼ������ݿ���ѡ�з��������Ŀ��Գ��������
 */
public class LotSelectFromDbDialogOld extends OutLineLotDialog {
	protected List<Lot> selectedLots;

	public LotSelectFromDbDialogOld(Shell shell, Object parent, Object child, List<Lot> selectedLots) {
		super(shell, parent, child, false);
		this.selectedLots = selectedLots;
	}

	@Override
	protected void createSection(Composite composite) {
		lotSection = new LotSelectFromDbSectionOld(movementOut, outLine, table, selectedLots, this);
		lotSection.createContents(managedForm, composite);
	}
}
