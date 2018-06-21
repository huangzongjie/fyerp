package com.graly.erp.xz.inv.in;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZInLineProperties extends ChildEntityProperties {
	private String PREFIX = " movementRrn = '";
	
	public XZInLineProperties() {
		super();
    }
	
	public XZInLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}
	
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		//init Locator
		XZInLineEntryBlock block = (XZInLineEntryBlock)getMasterParent();
		IField warehouse = block.getDetailForms().get(0).getFields().get("warehouseRrn");
		if(warehouse.getValue() != null){
			reputLocator(warehouse.getValue());
		}
	}

	private IField getIField(String fieldId) {
		IField f = null;
		for(Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if(f != null) {
				return f;
			}
		}
		return f;
	}
	
	//����warehouseRrn����locator
	public void reputLocator(Object object){
		IField locatorField = getIField("locatorRrn");
		if(locatorField instanceof RefTableField) {
			RefTableField tf = (RefTableField)locatorField;
			try{
				ADManager entityManager = Framework.getService(ADManager.class);
				String where = " orgRrn = " + Env.getOrgRrn() + " AND warehouseRrn = '" + object.toString() + "' ";
				List<Locator> list = entityManager.getEntityList(Env.getOrgRrn(), Locator.class, Env.getMaxResult(), where, "");
				list = list.size() == 0 ? new ArrayList<Locator>() : list;
				tf.setInput(list);
				tf.refresh();
			}catch(Exception e){
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}

	/* ɾ�������ݲ�ʵ��,��Ϊ��1)ɾ��ʱ������Ͳ�������ȷ��, 2)����һ�㲻ɾ�� */
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemDelete(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	public void refresh() {
		super.refresh();
		XZInLineEntryBlock block = (XZInLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}
	
	protected void saveAdapter(){
		XZInLineEntryBlock block = (XZInLineEntryBlock)getMasterParent();
			
		if(block.selectMovementLine == null){
			return;
		}
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					MovementLine movementLine = (MovementLine) getAdObject();

					/*
					 * ���汸ע--
					 */		
						boolean flag = true;
						Form commentForm = block.getDetailForms().get(1);
						if (!commentForm.saveToObject()) {
							flag = false;
						}
						
						if (flag) {
							
							PropertyUtil.copyProperties(block.getParentObject(), commentForm.getObject(), commentForm.getFields());
							
							MovementIn movementIn = (MovementIn) block.getParentObject();
							
							ADManager adManager = Framework.getService(ADManager.class);
							movementIn = (MovementIn) adManager.saveEntity(movementIn, Env.getUserRrn());
							block.setParentObject(adManager.getEntity(movementIn));
							this.getMasterParent().refresh();
						}
						/*
						 * --���汸ע
						 */
						
					MovementIn movementIn = (MovementIn) block.getParentObject();
					movementLine.setMovementLots(getLineLotList(movementIn, movementLine));
					List<MovementLine> list = new ArrayList<MovementLine>();
					list.add(movementLine);

					INVManager invManager = Framework.getService(INVManager.class);
					movementIn = invManager.saveMovementInLine(movementIn, list, MovementIn.InType.PIN, Env.getUserRrn());
					// ˢ�¸��Ӷ��󣬲����¸������whereClause
					ADManager adManager = Framework.getService(ADManager.class);
					block.setParentObject(adManager.getEntity(movementIn));

					List<MovementLine> lines = movementIn.getMovementLines();
					this.setAdObject(adManager.getEntity(lines.get(0))); //adManager.getEntity(line)
					getMasterParent().setWhereClause(" movementRrn = '" + movementIn.getObjectRrn() + "' ");
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected List<MovementLineLot> getLineLotList(MovementIn in, MovementLine line) throws Exception{
		List<MovementLineLot> lineLots = null;
		if(in.getObjectRrn() == null || line.getObjectRrn() == null)
			return lineLots;
		String whereClause = " movementRrn = " + in.getObjectRrn()
							+ " AND movementLineRrn = " + line.getObjectRrn() + " ";
    	ADManager manager = Framework.getService(ADManager.class);
    	lineLots = manager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
        		Integer.MAX_VALUE, whereClause, null);
		return lineLots;
	}
	
	/*protected void saveAdapter() {
		try {
			if (getAdObject() != null) {
				InLineEntryBlock section = (InLineEntryBlock)getMasterParent();
				if(!section.saveParent()) {
					return;   // �жϱ��游�����Ƿ�ɹ������򷵻�
				}
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					MovementLine line = (MovementLine)getAdObject();
					MovementIn in = (MovementIn)section.getParentObject();
					List<MovementLine> list = new ArrayList<MovementLine>();
					list.add(line);

					// ����saveMovementInLine()����, ������������Ͳ�����ȷ��
					INVManager invManager = Framework.getService(INVManager.class);
					in = invManager.saveMovementInLine(in, list, MovementIn.InType.PIN, Env.getUserRrn());
					// ˢ�¸����󣬲����¸������whereClause
					ADManager adManager = Framework.getService(ADManager.class);
					// ��Ϊ��properties��ֻ���޸ı����û���½�����,���Կ���ֱ�ӵ���getEntity()����
					section.setParentObject(adManager.getEntity(in));
					this.setAdObject(adManager.getEntity(line));  //adManager.getEntity(line)
					getMasterParent().setWhereClause(PREFIX + in.getObjectRrn() + "' ");
					// ��ʾ����ɹ�
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}*/
	
	protected void deleteAdapter() {
		if(!Movement.STATUS_DRAFTED.equals( ((MovementLine)getAdObject()).getLineStatus()) ) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.deleteMovementInLine((MovementLine)getAdObject(), MovementIn.InType.PIN, Env.getUserRrn());
					// ˢ�¸�����
					XZInLineEntryBlock block = (XZInLineEntryBlock)getMasterParent();
					MovementIn mi = (MovementIn)block.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					block.setParentObject(adManager.getEntity(mi));
					setAdObject(createAdObject());
					refresh();
					this.getMasterParent().refresh();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	@Override
	public ADBase createAdObject() throws Exception {
		MovementLine inLine = null;
		try {
			XZInLineEntryBlock block = (XZInLineEntryBlock)this.getMasterParent();
			if(block.isEnableByParentObject()) {
				INVManager invManager = Framework.getService(INVManager.class);			
				inLine = invManager.newMovementLine((MovementIn)block.getParentObject());
			} else {
				inLine = new MovementLine();
			}
			inLine.setOrgRrn(Env.getOrgRrn());
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return inLine;
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		MovementLine outLine = (MovementLine)adObject;
		if(outLine != null) {
			setStatusChanged(outLine.getLineStatus());
		} else {
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		if(PurchaseOrder.STATUS_DRAFTED.equals(status)) {
			itemSave.setEnabled(true);
//			itemDelete.setEnabled(true);
		} else if(PurchaseOrder.STATUS_APPROVED.equals(status)) {
			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
		} else {
			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
		}
	}

}