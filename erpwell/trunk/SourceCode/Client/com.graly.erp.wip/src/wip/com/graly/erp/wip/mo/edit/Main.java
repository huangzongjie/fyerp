package com.graly.erp.wip.mo.edit;

import java.util.Calendar;

public class Main {

	public static void main(String args[]) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2009);
		calendar.set(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		System.out.println(calendar.get(Calendar.DATE));
	}
}
