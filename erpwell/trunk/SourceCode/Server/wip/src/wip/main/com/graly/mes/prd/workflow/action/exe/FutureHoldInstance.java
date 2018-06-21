package com.graly.mes.prd.workflow.action.exe;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.log4j.Logger;

import com.graly.framework.core.exception.ClientException;
import com.graly.mes.prd.workflow.action.def.FutureHold;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.node.EndState;
import com.graly.mes.prd.workflow.graph.node.StartState;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotHold;
import com.graly.mes.wip.model.LotStateMachine;


@Entity
@DiscriminatorValue("H")
public class FutureHoldInstance extends InstanceAction {
	
	static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(FutureHoldInstance.class);
	
	@OneToOne
	@JoinColumn(name = "ACTION_RRN", referencedColumnName = "OBJECT_RRN")
	private FutureHold futureHold;

	public void setFutureHold(FutureHold futureHold) {
		this.futureHold = futureHold;
	}

	public FutureHold getFutureHold() {
		return futureHold;
	}
	
	public void execute(ExecutionContext executionContext) {
		try {
			Lot lot = executionContext.getLot();
			LotHold lotHold = new LotHold();
			lotHold.setHoldCode(futureHold.getHoldCode());
			lotHold.setHoldReason(futureHold.getHoldReason());
			lotHold.setHoldPwd(futureHold.getHoldPwd());
			String trans = LotStateMachine.TRANS_QUEUEHOLD;
			if (executionContext.getToken().getNode() instanceof StartState) {
				trans = LotStateMachine.TRANS_QUEUEHOLD;
			} else if (executionContext.getToken().getNode() instanceof EndState){
				trans = LotStateMachine.TRANS_TRACKOUTHOLD;
			}
			executionContext.getLotManager().holdLot(lot, lotHold, trans, futureHold.getUpdatedBy());
		} catch (ClientException e) {
			logger.error(e);
		}
	}
}
