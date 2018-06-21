package com.graly.erp.inv;

public class TestLongParse {

	public static void main(String[] args) {
		Object obj = "";
		boolean is = false;
		try {
			Long.parseLong(obj.toString());
		} catch(Exception e) {
			is = true;
		}
		System.out.print(is);
	}

}
