package com.graly.mes.wip.exception;

import com.graly.framework.core.exception.ClientException;

public class LotExistException extends ClientException {
	
	public LotExistException() {
		super("error.lot_exist");
	}
}
