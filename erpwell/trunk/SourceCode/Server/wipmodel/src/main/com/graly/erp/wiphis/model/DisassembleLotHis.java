package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

/**
 * @author Denny
 * 记录拆分批次的历史
 */
@Entity
@DiscriminatorValue("DISASSEMBLE")
public class DisassembleLotHis extends LotHis {
	private static final long serialVersionUID = 1L;

	public DisassembleLotHis() {
		super();
	}
	
	public DisassembleLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_DISASSEMBLE);
	}
}
