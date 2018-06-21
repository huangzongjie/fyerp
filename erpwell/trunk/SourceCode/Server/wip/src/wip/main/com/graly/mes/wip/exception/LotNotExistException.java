package com.graly.mes.wip.exception;

import com.graly.framework.core.exception.ClientException;

public class LotNotExistException extends ClientException {
	
	public LotNotExistException() {
		super("error.lot_not_exist");
	}
}
