package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.erp.wip.model.ManufactureOrderLine;

/**
 * @author Denny
 * 记录拆分MoLine的历史
 */
@Entity
@DiscriminatorValue("DISASSEMBLE")
public class DisassembleMoLineHis extends MoLineHis {

	private static final long serialVersionUID = 1L;

	public DisassembleMoLineHis() {
		super();
	}
	
	public DisassembleMoLineHis(ManufactureOrderLine moLine){
		super(moLine);
		this.setTransType(MoLineHis.TRANS_DISASSEMBLE);
	}
}
