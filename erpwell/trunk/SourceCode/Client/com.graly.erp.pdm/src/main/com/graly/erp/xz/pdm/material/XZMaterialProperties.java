package com.graly.erp.xz.pdm.material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.material.AffectedBomInfoDialog;
import com.graly.erp.pdm.material.CalculateVolumeProgress;
import com.graly.erp.pdm.material.CalculateVolumeProgressDialog;
import com.graly.erp.pdm.material.ImageForm;
import com.graly.erp.pdm.material.MaterialProperties;
import com.graly.erp.pdm.material.UsageInfoDialog;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.VPdmBom;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZMaterialProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(MaterialProperties.class);
	private final String TABLE_BOM = "BOM_Using_Material";//BOMʹ�õı�
	private final String TABLE_AFFECTEDBOMS = "Affected_BOMS";//�޸����ʱ������ЩBOM����Ӱ��ı�
	
	protected static String URL = "http://192.168.0.235:81/caxa.jsp?serial_number=";
	
	private ADTable vPdmBomTable;
	private UsageInfoDialog usageDialog;
	private String label;
	private ADManager adManager;
	private boolean isVolumeChanged = false;
	private ADBase oldAdObject = null;
	private MovementLine movementLine;
	protected ImageForm imageForm;
	private String IMAGEFORM_TITLE = "Photo";
	public XZMaterialProperties() {
		super();
	}

	public XZMaterialProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}

	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			Form itemForm = null;
			if (IMAGEFORM_TITLE.equals(tab.getName())) {
				imageForm = new ImageForm(getTabs(), SWT.NULL, tab, mmng);
				item.setControl(imageForm);
			}else {
				itemForm = new XZLotTypeForm(getTabs(), SWT.NONE, tab, mmng);
				getDetailForms().add(itemForm);
				item.setControl(itemForm);
			}
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		createLinkComposite(client,toolkit);
		createPriceComposite(client,toolkit);
	}
	
	//��������Ѹ����è�ȵ���վ�ļ۸�
	private void createLinkComposite(Composite client,FormToolkit toolkit){
		Composite priceComposite = toolkit.createComposite(client, SWT.NULL);
		priceComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = 5;
		gl.marginHeight = 0;
		priceComposite.setLayout(gl);
		
		String linkAddress =  "<A href=\"www.jd.com\">�������ٹ���</A>";
		Link link = new Link(priceComposite,SWT.NONE);
		link.setText(linkAddress);
		link.setSize(200,200);
		link.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
//						String url = "http://cart.jd.com/cart/addToCart.html?rcd=1&pid=1022282&rid=1396335101363";
						String url = material.getComments();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		String linkDetailAddress =  "<A href=\"www.jd.com\">�������ٹ�����Ʒ��ϸ��Ϣ</A>";
		Link linkDetail = new Link(priceComposite,SWT.NONE);
		linkDetail.setText(linkDetailAddress);
		linkDetail.setSize(200,200);
		linkDetail.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getComments();
						String[] urls = url.split("\\?");
						String[] params = urls[1].split("\\&");
						String[] productIds = params[0].split("\\=");
						String productId = productIds[1];
						url = "http://item.jd.com/"+productId+".html";
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		String linkAddress2 =  "<A href=\"www.tmall.com\">��è���ٹ���</A>";
		Link link2 = new Link(priceComposite,SWT.NONE);
		link2.setText(linkAddress2);
		link2.setSize(200,200);
		link2.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getBomId();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		String linkDetailAddress2 =  "<A href=\"www.jd.com\">��è���ٹ�����Ʒ��ϸ��Ϣ</A>";
		Link linkDetail2 = new Link(priceComposite,SWT.NONE);
		linkDetail2.setText(linkDetailAddress2);
		linkDetail2.setSize(200,200);
		linkDetail2.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getBomId();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		String linkAddress3 =  "<A href=\"www.yhd.com\">��Ѹ���ٹ���</A>";
		Link link3 = new Link(priceComposite,SWT.NONE);
		link3.setText(linkAddress3);
		link3.setSize(200,200);
		link3.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getChildMaterialId();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		String linkDetailAddress3 =  "<A href=\"http://item.yixun.com/item-401678.html\">��Ѹ���ٹ�����Ʒ��ϸ��Ϣ</A>";
		Link linkDetail3 = new Link(priceComposite,SWT.NONE);
		linkDetail3.setText(linkDetailAddress3);
		linkDetail3.setSize(200,200);
		linkDetail3.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getChildMaterialId();
						String[] urls = url.split("\\?");
						String[] params = urls[1].split("\\&");
						String[] productIds = params[0].split("\\=");
						String productId = productIds[1];
						url = "http://item.yixun.com/item-"+productId+".html";
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		String linkAddress4 =  "<A href=\"www.tmall.com\">ʷ̩�����ٹ���</A>";
		Link link4 = new Link(priceComposite,SWT.NONE);
		link4.setText(linkAddress4);
		link4.setSize(200,200);
		link4.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getReferenceDoc5();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		String linkDetailAddress4 =  "<A href=\"http://item.yixun.com/item-401678.html\">ʷ̩�����ٹ�����Ʒ��ϸ��Ϣ</A>";
		Link linkDetail4 = new Link(priceComposite,SWT.NONE);
		linkDetail4.setText(linkDetailAddress4);
		linkDetail4.setSize(200,200);
		linkDetail4.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				try {
					if(getAdObject().getObjectRrn() != null){
						Material material = (Material) getAdObject();
						String url = material.getReferenceDoc5();
						Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+url );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		Label label = toolkit.createLabel(priceComposite,"", SWT.NONE);
		label.setText("�������ٹ������ӵ�ַģ��:");
		Text text = toolkit.createText(priceComposite,"", SWT.NONE);
		text.setText("http://gate.jd.com/InitCart.aspx?pid=��Ʒ���&pcount=1&ptype=1");
		Label labelyx = toolkit.createLabel(priceComposite,"", SWT.NONE);
		labelyx.setText("��Ѹ���ٹ������ӵ�ַģ��:");
		Text textyx = toolkit.createText(priceComposite,"", SWT.NONE);
		textyx.setText("http://buy.yixun.com/cart.html?pid=��Ʒ���&pnum=1&chid=0");
		

		
	}
	
	
	//��������Ѹ����è�ȵ���վ�ļ۸�
	private void createPriceComposite(Composite client,FormToolkit toolkit){
		Composite priceComposite = toolkit.createComposite(client, SWT.NULL);
		priceComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gl = new GridLayout(6, false);
		gl.marginWidth = 5;
		gl.marginHeight = 0;
		priceComposite.setLayout(gl);
		
		ImageHyperlink jdLink = toolkit.createImageHyperlink(priceComposite, SWT.WRAP);
		jdLink.setText("�����۸�");
		jdLink.setImage(SWTResourceCache.getImage("jd"));
		jdLink.addHyperlinkListener(new IHyperlinkListener(){
			@Override
			public void linkActivated(HyperlinkEvent e) {
//				String url = "http://search.jd.com/Search?keyword=12344444444444444&enc=utf-8";
				Material material = (Material) getAdObject();
				StringBuffer url = new StringBuffer();
				url.append("http://search.jd.com/Search?keyword=");
				String keyValue= "";
				try {
					keyValue = java.net.URLEncoder.encode(material.getName(), "UTF-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				url.append(keyValue);
				url.append("&enc=utf-8");

				BrowserDialog jdDialog = new BrowserDialog(UI.getActiveShell(), url.toString());
				jdDialog.open();
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {}
			@Override
			public void linkExited(HyperlinkEvent e) {}
			
		});
		
		ImageHyperlink yxLink = toolkit.createImageHyperlink(priceComposite, SWT.WRAP);
		yxLink.setText("��Ѹ�۸�");
		yxLink.setImage(SWTResourceCache.getImage("yx"));
		yxLink.addHyperlinkListener(new IHyperlinkListener(){
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Material material = (Material) getAdObject();
				StringBuffer url = new StringBuffer();
				url.append("http://searchex.yixun.com/html?key=");
				String keyValue= "";
				try {
					keyValue = java.net.URLEncoder.encode(material.getName(), "UTF-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				url.append(keyValue);
				url.append("&acharset=utf-8");

				BrowserDialog yxDialog = new BrowserDialog(UI.getActiveShell(), url.toString());
				yxDialog.open();
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {}
			@Override
			public void linkExited(HyperlinkEvent e) {}
			
		});
		
		ImageHyperlink tbLink = toolkit.createImageHyperlink(priceComposite, SWT.WRAP);
		tbLink.setText("��è�۸�");
		tbLink.setImage(SWTResourceCache.getImage("tb"));
		tbLink.addHyperlinkListener(new IHyperlinkListener(){
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Material material = (Material) getAdObject();
				StringBuffer url = new StringBuffer();
				url.append("http://s.taobao.com/search?q=");
				String keyValue= "";
				try {
					keyValue = java.net.URLEncoder.encode(material.getName(), "UTF-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				url.append(keyValue);
//				url.append("&acharset=utf-8");

				BrowserDialog tbDialog = new BrowserDialog(UI.getActiveShell(), url.toString());
				tbDialog.open();
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {}
			@Override
			public void linkExited(HyperlinkEvent e) {}
			
		});
	}


    
	private IField getIField(String fieldId) {
		for (Form form : getDetailForms()) {
			IField f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
		setInitAuthority(true);
	}
	
 
       
 
	
	protected void queryAdapter() {
		if(getAdObject() != null && getAdObject().getObjectRrn() != null) {
			usageDialog = new UsageInfoDialog(UI.getActiveShell(), (Material) getAdObject(), getVPdmBomTable());
			usageDialog.open();
		}
	}
	
	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	@Override
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SAVE);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	@Override
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete =new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	@Override
	protected void deleteAdapter() {
		if(getAdObject().getObjectRrn() != null)
		{
		try{
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			INVManager invManager = Framework.getService(INVManager.class);			
			List childBOM = pdmManager.getAllChildBoms(getAdObject().getObjectRrn());
			List lotStorage = invManager.getLotStorage(getAdObject().getObjectRrn());
			if(!pdmManager.getMaterialRrn(getAdObject().getObjectRrn())){
				UI.showInfo("�������г�����¼������ɾ��");
				return ;
			};

			if(childBOM.size() > 0){
				UI.showInfo("��������BOM������ɾ��");
				return ;
			}
			
			if(lotStorage != null && lotStorage.size() > 0){
				UI.showInfo("�������п�棬����ɾ��");
				return ;
			}
			List<ADBase> objects = pdmManager.validateBeforeDeleteMaterial((Material) getAdObject());
//			if(materialRrn = null){
//				
//			}
			if(objects != null && objects.size() > 0){
				StringBuffer maSb = new StringBuffer();
				StringBuffer moSb = new StringBuffer();
				StringBuffer moLineSb = new StringBuffer();
				for(ADBase object : objects){
					if(object instanceof VPdmBom){
						maSb.append(" '" + ((VPdmBom)object).getMaterialParentId() + "' ");
					}else if(object instanceof ManufactureOrder){
						moSb.append(" '" + ((ManufactureOrder)object).getDocId() + "' ");
					}else if(object instanceof ManufactureOrderLine){
						moLineSb.append(" '" + ((ManufactureOrderLine)object).getMaterialId() + "' ");
					}
				}
				StringBuffer info = new StringBuffer();
				info.append( "����" + objects.size() + "������ʹ���˸�����,����\n" );
				info.append( " ����:" + maSb +"\n" );
				info.append( " ������:" + moSb + "\n" );
				info.append( " �ӹ�����:" + moLineSb );
				
				UI.showInfo(info.toString());
				return;
			}
			
			
			super.deleteAdapter();
		}
		catch (Exception e){
			ExceptionHandlerManager.asyncHandleException(e);
		}
		}else
		{
			UI.showInfo("��ѡ������");
		}
	}
	
            
//	protected void selectBomAdapter(SelectionEvent event) {
//		form.getMessageManager().removeAllMessages();
//		try {
//			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
//				if(adManager == null) 
//					adManager = Framework.getService(ADManager.class);
//				setAdObject(adManager.getEntity(getAdObject()));
//				
//				if(!hasActualBom()) {
//					BomSelectTreeDialog btd = new BomSelectTreeDialog(event.widget.getDisplay().getActiveShell(), form, (Material) getAdObject());
//					if(btd.open() == Window.CANCEL) {
//						Material material = (Material)adManager.getEntity(this.getAdObject());
//						setAdObject(material);
//						refresh();
//						getMasterParent().refreshUpdate(material);
//					}					
//				}
//				else {
//					UI.showError(String.format(Message.getString("pdm.has_bom_can_not_select"),
//							((Material)getAdObject()).getMaterialId()));
//				}
//			}
//		} catch(Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			logger.error("MaterialProperties : selectBomAdapter()", e);
//		}
//	}
	 
	@Override
	protected void saveAdapter() {
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			Material mat = (Material) getAdObject();
			List<Bom> parentBOMs = null;
			List<Material> materials = null;
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					ADBase oldBase = getAdObject();
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					
					//save material and synchronize the one in another area
					//1.save material this area
					Material material = pdmManager.saveMaterial(getTable().getObjectRrn(), (Material) getAdObject(), Env.getUserRrn());
					ADManager entityManager = Framework.getService(ADManager.class);
					setAdObject(entityManager.getEntity(material));
//					
					// ����Material Photo
					imageForm.setObject(getAdObject());
					imageForm.saveToObject();
					
					UI.showInfo(Message.getString("common.save_successed"));// ������ʾ��
					refresh();
					ADBase newBase = getAdObject();
					if (oldBase.getObjectRrn() == null) {
						getMasterParent().refreshAdd(newBase);
					} else {
						getMasterParent().refreshUpdate(newBase);
					}
//					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	public void refresh() {
		isVolumeChanged = false;
		for (Form detailForm : getDetailForms()) {
			detailForm.setObject(getAdObject());
			detailForm.loadFromObject();
		}
		// ˢ�� Material Photo
		imageForm.setObject(getAdObject());
		imageForm.loadFromObject();
		form.getMessageManager().removeAllMessages();
	}
	
	protected void setInitAuthority(boolean enabled) {
		itemNew.setEnabled(enabled);
		itemSave.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
	}

	@Override
	public void dispose() {
		if (imageForm != null && !imageForm.isDisposed()) {
			imageForm.dispose();
		}
		super.dispose();
	}

	private ADTable getVPdmBomTable() {
		try {
			if (vPdmBomTable != null) {
				return vPdmBomTable;
			} else {
				vPdmBomTable = getADTableByName(TABLE_BOM);
				return vPdmBomTable;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	private ADTable getADTableByName(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTablee = entityManager.getADTable(0L, tableName);
			return adTablee;
		} catch (Exception e) {
			logger.error(e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
        oldAdObject = getAdObject();
	}
	
	public void saveVolume(){
		try {
			if(isVolumeChanged){//ֻ������ı�ʱ�ű��棬���û��ʱ������
				boolean isCalculate = false;
				if(isVolumeChanged){
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					Material mat = (Material) getAdObject();
					List<Bom> parentBOMs = null;
					List<Material> materials = null;
					if(mat != null && mat.getObjectRrn() != null && mat.getIsVolumeBasis()){
						parentBOMs = pdmManager.getDeepParentBOMs(mat);
						materials = new ArrayList<Material>();
						for(Bom bom : parentBOMs){
							Material m = bom.getParentMaterial();
							if(!materials.contains(m)){
								materials.add(m);
							}
						}
						
						AffectedBomInfoDialog abid = new AffectedBomInfoDialog(UI.getActiveShell(), materials, getADTableByName(TABLE_AFFECTEDBOMS));
						if(abid.open() == Window.OK){
							isCalculate = true;
						}else{
							return;
						}
						
					}
					if (getAdObject() != null) {
						Material material = pdmManager.saveMaterial(getTable().getObjectRrn(), (Material) getAdObject(), Env.getUserRrn());
						ADManager entityManager = Framework.getService(ADManager.class);
						setAdObject(entityManager.getEntity(material));
						if(isCalculate){
							CalculateVolumeProgressDialog progressDialog = new CalculateVolumeProgressDialog(UI.getActiveShell());
							CalculateVolumeProgress progress = new CalculateVolumeProgress(materials);
							progressDialog.run(true, true, progress);
							if (progress.isFinished()) {
								UI.showInfo("ʹ�ø����ϵ�BOM����ѳɹ����£�");
							}
						}
						UI.showInfo(Message.getString("common.save_successed"));// ������ʾ��
						refresh();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public boolean isVolumeChanged() {
		return isVolumeChanged;
	}

	public void setVolumeChanged(boolean isVolumeChanged) {
		this.isVolumeChanged = isVolumeChanged;
	}
}