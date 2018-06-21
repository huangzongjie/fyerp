package com.graly.erp.inv.outserialquery;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLineOutSerial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class OutserialQuerySection extends EntitySection {
	private static final Logger logger = Logger.getLogger(OutserialQuerySection.class);
	protected Text text;
	protected Lot lot = null;
	
	public OutserialQuerySection(ADTable table) {
		super(table);
	}

	@Override
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("inv.by_out_serial"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		text = toolkit.createText(top, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		GridData gText = new GridData();
		gText.widthHint = 200;
		text.setLayoutData(gText);
		text.setTextLimit(32);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text tOutSerialId = ((Text) event.widget);
				tOutSerialId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String outSerialId = tOutSerialId.getText();
					lot = searchLot(outSerialId);
					tOutSerialId.selectAll();
					if (lot == null) {
						tOutSerialId.setForeground(SWTResourceCache.getColor("Red"));
						initAdObject();
						break;
					} else {
						setAdObject(lot);
						refresh();
					}
					break;
				}
			}

			public Lot searchLot(String outSerialId) {
				try {
					MovementLineOutSerial mlo = null;
					INVManager invManager = Framework.getService(INVManager.class);
					List<MovementLineOutSerial> l = invManager.getMovementLineOutSerials(outSerialId);
					if(l.size() != 0){
						mlo = l.get(0);
					}
					Long lotRrn = null;
					if(mlo != null){
						lotRrn = mlo.getLotRrn();
						ADManager adManager = Framework.getService(ADManager.class);
						Lot lot = new Lot();
						lot.setObjectRrn(lotRrn);
						return (Lot) adManager.getEntity(lot);
					}else{
						return invManager.getLotByLotId(Env.getOrgRrn(), outSerialId);
					}
				} catch (Exception e) {
					logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
					ExceptionHandlerManager.asyncHandleException(e);
					return null;
				}
			}
		});

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Text tLotId = ((Text) e.widget);
				tLotId.setText(tLotId.getText());
				tLotId.selectAll();
			}
		});
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	public void initAdObject() {
		Lot lot = new Lot();
		lot.setOrgRrn(Env.getOrgRrn());
		setAdObject(lot);
		refresh();
	}
	
}
