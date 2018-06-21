package com.graly.erp.wip.mo.molinedelay;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * �������ڡ���������ԭ�����ڸ���ԭ���޷���ʱ���ܣ�����������һ����ERP�������ܵ��뷨���㿴һ���Ƿ��е�ͨ��
*1���ڽ������ڳ�����������3�죨������3�죩����Ʒ��
*2���ڹ��������ϵ���գ������������������á����ڵ�ȷ��ʱ������������ڳ���������3�����ϣ���������һ����ҳ�桰ѡ�񶩵�����ԭ�򡱣��������޷������������������á�ҳ�棬����ã���������������������ϡ�ҳ�棻����3�����ڣ���ֱ��������������������ϡ�ҳ�档
*3���ڡ�����������������һ��С����ܡ�����������ܡ���������������ϱ�š��������ơ��������ƻ����ڡ�ʵ��������ڡ���������������ԭ��
*4������ԭ��1���ɹ����ϣ�2��ҵ����ģ�3�����ܲ��㣻4����������
**/
public class MoLineDelayQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.mo.molinedelay.MoLineDelayQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MoLineDelayQueryEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}
}
