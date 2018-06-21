package com.graly.erp.base.print;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.apache.log4j.Logger;

import com.graly.framework.core.exception.ClientException;

public class PrintUtil {
	private static final Logger logger = Logger.getLogger(PrintUtil.class);
	public enum PrintType {
		LOTID, RACKID, LLOTID
	};
	
	public static PrintService getDefaultPrintService() throws ClientException {
		try {
			PrintService defService = PrintServiceLookup.lookupDefaultPrintService();
			if (defService == null) {
				throw new ClientException("error.printer_is_notfound");
			}
			return defService;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException("error.printer_is_notfound");
		}
	}

	public static void print(PrintService printService, String s1, String s2) throws ClientException {
		print(printService, s1, s2, PrintType.LOTID);
	}
	
	public static void print(PrintService printService, String s1, String s2, PrintType printType) throws ClientException {
		try {
			if (printService == null) {
				printService = getDefaultPrintService();
			}
			if (printService == null) {
				throw new ClientException("error.printer_is_notfound");
			}
			DocPrintJob printJob = printService.createPrintJob();
			logger.info(s1);
			logger.info(s2);
			byte[] by = genearteZPLString(s1, s2, printType).getBytes();
	        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	        Doc doc = new SimpleDoc(by, flavor, null);
	        printJob.print(doc, null);
		} catch (ClientException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException("error.print_failed");
		}
	}

	public static void print(PrintService printService, String s) throws ClientException {
		print(printService, s, PrintType.LOTID);
	}
	
	public static void print(PrintService printService, String s, PrintType printType) throws ClientException {
		try {
			if (printService == null) {
				printService = getDefaultPrintService();
			}
			if (printService == null) {
				throw new ClientException("error.printer_is_notfound");
			}
			DocPrintJob printJob = printService.createPrintJob();
			byte[] by = genearteZPLString(s,printType).getBytes();
	        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	        Doc doc = new SimpleDoc(by, flavor, null);
	        printJob.print(doc, null);
		} catch (ClientException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException("error.print_failed");
		}
	}
	
	//单边打印
	public static String genearteZPLString(String s, PrintType printType) {
		String invocation = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer prints = new StringBuffer();
//		String prints = null;
//		if (s.charAt(0) >= '0' && s.charAt(0) <= '9') {
//			
//			if (s.length() <= 15) {
//				prints =  ">5" + s.substring(0,8) + ">6" + s.substring(8);
//			} else {
//				prints = ">5" + s.substring(0, 8) + ">6" + s.substring(8, 12) + ">5" + s.substring(12);
//			}	
//			
//		}
//		else {
//			if (s.length() <= 15) {
//				prints = s.substring(0,1) + ">5" + s.substring(1, 7) + ">6" + s.substring(7, 11) + ">5" + s.substring(11);
//			}else{
//				prints = s.substring(0,1) + ">5" + s.substring(1, 9) + ">6" + s.substring(9, 12) + ">5" + s.substring(12);
//			}
//		}
		if(PrintType.LOTID.equals(printType)){
			
//			int j = 0;
//			int k = 0;
//			for(int i=0; i<s.length(); i++){
//				char c = s.charAt(i);
//				if(c>='0' && c<='9'){
//					k=0;
//					if(j == 0){
//						char c2 = s.charAt(i+1);
//						if(c2>='0' && c2<='9'){
//							invocation = ">5";
//							prints.append(invocation);
//						}
//						j++;
//					}
//					prints.append(c);
//				}else{
//					j=0;
//					if(k == 0 && i > 0){
//						if(invocation != null && !">6".equalsIgnoreCase(invocation)){
//							invocation = ">6";
//							prints.append(invocation);
//						}
//						k++;
//					}
//					prints.append(c);
//				}
//			}
			
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^MD0^JUS^LRN^CI0^XZ^XA^MMT^LL0128^PW400^LS0^BY2,2.5,75^FT8,95^BCN,,Y,N^FD>:");
			prints = makeZplCode(s);
			System.out.println(prints);
			sb.append(prints);
			sb.append("^FS^PQ1,0,1,Y^XZ");
		}else if(PrintType.RACKID.equals(printType)){
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^JUS^LRN^CI0^XZ");
			sb.append("^XA");
			sb.append("^MMT");
			sb.append("^LL0128");
			sb.append("^PW400");
			sb.append("^LS0");
			sb.append("^BY3,3,72^FT60,90^BCN,,Y,N");
			sb.append("^FD>:");
			
			char c = s.charAt(0);
			prints.append(c);
			prints.append(">5");
			prints.append(s.substring(1));
			
			sb.append(prints);
			sb.append("^FS");
			sb.append("^PQ1,0,1,Y^XZ");
			
			System.out.println(prints);
		}else if(PrintType.LLOTID.equals(printType)){
			
//			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^JUS^LRN^CI0^XZ");
//			sb.append("^XA");
//			sb.append("^MMT");
//			sb.append("^LL0128");
//			sb.append("^PW400");
//			sb.append("^LS0");
//			sb.append("^BY2,3,67^FT440,86^BCN,,Y,N");
//			sb.append("^FD>:LL>;30010006");
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^JUS^LRN^CI0^XZ");
			sb.append("^XA");
			sb.append("^MMT");
			sb.append("^LL0128");
			sb.append("^PW816");
			sb.append("^LS0");
			sb.append("^BY2,3,67^FT432,94^BCN,,Y,N");
			sb.append("^FD>:LL");
			
			String subSb = s.substring(2);//删除开始的LL
			StringBuffer print1 = makeZplCode(subSb);
			
			sb.append(print1);
			sb.append("^FS");
			sb.append("^PQ1,0,1,Y^XZ");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	//双边打印
	private static String genearteZPLString(String s1, String s2, PrintType printType) {
		String invocation1 = null;
		String invocation2 = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer prints1 = new StringBuffer();
		StringBuffer prints2 = new StringBuffer();
		
		if(PrintType.LOTID.equals(printType)){
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^MD0^JUS^LRN^CI0^XZ^XA^MMT^LL0128^PW816^LS0^BY2,2.5,75^FT424,95^BCN,,Y,N^FD>:");
//			String prints1 = null;
//			String prints2 = null;
			
//			if (s1.charAt(0) >= '0' && s1.charAt(0) <= '9') {
//				
//				if (s1.length() <= 15) {
//					prints1 =  ">5" + s1.substring(0,8) + ">6" + s1.substring(8);
//				} else {
//					prints1 = ">5" + s1.substring(0, 8) + ">6" + s1.substring(8, 12) + ">5" + s1.substring(12);
//				}	
//				
//			}
//			else {
//				if(s1.length()<=15){
//					prints1 = s1.substring(0,1) + ">5" + s1.substring(1, 7) + ">6" + s1.substring(7, 11) + ">5" + s1.substring(11);
//				}else{
//					prints1 =  s1.substring(0,1) + ">5" + s1.substring(1, 9) + ">6" + s1.substring(9, 12) + ">5" + s1.substring(12);
//				}
//				
//			}
//			int j = 0;
//			int k = 0;
//			for(int i=0; i<s1.length(); i++){
//				char c = s1.charAt(i);
//				if(c>='0' && c<='9'){
//					k=0;
//					if(j == 0){
//						char c2 = s1.charAt(i+1);
//						if(c2>='0' && c2<='9'){
//							invocation1 = ">5";
//							prints1.append(invocation1);
//						}
//						j++;
//					}
//					prints1.append(c);
//				}else{
//					j=0;
//					if(k == 0 && i > 0){
//						if(invocation1 != null && !">6".equalsIgnoreCase(invocation1)){
//							invocation1 = ">6";
//							prints1.append(invocation1);
//						}
//						k++;
//					}
//					prints1.append(c);
//				}
//			}
			prints1 = makeZplCode(s1);
			System.out.println(prints1);
			sb.append(prints1);
			sb.append("^FS^BY2,2.5,75^FT8,95^BCN,,Y,N^FD>:");
			
//			if (s2.charAt(0) >= '0' && s2.charAt(0) <= '9') {
//				
//				if (s2.length() <= 15) {
//					prints2 =  ">5" + s2.substring(0,8) + ">6" + s2.substring(8);
//				} else {
//					prints2 = ">5" + s2.substring(0, 8) + ">6" + s2.substring(8, 12) + ">5" + s2.substring(12);
//				}	
//				
//			}
//			else {
//				if(s2.length()<=15){
//					prints2 = s2.substring(0,1) + ">5" + s2.substring(1, 7) + ">6" + s2.substring(7, 11) + ">5" + s2.substring(11);
//				}else{
//					prints2 =  s2.substring(0,1) + ">5" + s2.substring(1, 9) + ">6" + s2.substring(9, 12) + ">5" + s2.substring(12);
//				}
//			}
			prints2 = makeZplCode(s2);
			System.out.println(prints2);
			sb.append(prints2);
		}else if(PrintType.RACKID.equals(printType)){
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^MD0^JUS^LRN^CI0^XZ");
			sb.append("^XA");
			sb.append("^MMT");
			sb.append("^LL0128");
			sb.append("^PW816");
			sb.append("^LS0");
			sb.append("^BY3,3,72^FT476,90^BCN,,Y,N^FD>:");
			char c1 = s1.charAt(0);
			prints1.append(c1);
			prints1.append(">5");
			prints1.append(s1.substring(1));
			
			System.out.println(prints1);
			sb.append(prints1);
			
			sb.append("^FS^BY3,3,72^FT60,90^BCN,,Y,N^FD>:");
			char c2 = s2.charAt(0);
			prints2.append(c2);
			prints2.append(">5");
			prints2.append(s2.substring(1));
			
			System.out.println(prints2);
			sb.append(prints2);
			
		}else if(PrintType.LLOTID.equals(printType)){
			
//			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^JUS^LRN^CI0^XZ");
//			sb.append("^XA");
//			sb.append("^MMT");
//			sb.append("^LL0128");
//			sb.append("^PW400");
//			sb.append("^LS0");
//			sb.append("^BY2,3,67^FT440,86^BCN,,Y,N");
//			sb.append("^FD>:LL>;30010006");
			sb.append("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD30^JUS^LRN^CI0^XZ");
			sb.append("^XA");
			sb.append("^MMT");
			sb.append("^LL0128");
			sb.append("^PW816");
			sb.append("^LS0");
			sb.append("^BY2,3,67^FT432,94^BCN,,Y,N");
			sb.append("^FD>:LL");
			
			String subSb = s1.substring(2);//删除开始的LL
			prints1 = makeZplCode(subSb);
			


			System.out.println(prints1);
			
			sb.append("^FS^BY2,3,67^FT16,94^BCN,,Y,N");
			sb.append("^FD>:LL");
			
			subSb = s2.substring(2);//删除开始的LL
			prints2 = makeZplCode(subSb);
			System.out.println(prints2);
		}
		sb.append("^FS^PQ1,0,1,Y^XZ");
		System.out.println(sb.toString());
		return sb.toString();
	}

	private static StringBuffer makeZplCode(String s) {
		StringBuffer prints1 = new StringBuffer();
		String mark = ">:";
		//遍历s
		int i = 0;
		while(i < s.length()){
			Character c = s.charAt(i);
			if(Character.isDigit(c)){
				if((i+1)<s.length()){
					Character c2 = s.charAt(i+1);
					if(Character.isDigit(c2)){
						if(">5".equals(mark)){
							prints1.append(c).append(c2);
						}else{
							mark = ">5";
							prints1.append(mark).append(c).append(c2);
						}
						i++;
					}else{
						if(">:".equals(mark)||">6".equals(mark)){
							prints1.append(c);
						}else{
							mark = ">6";
							prints1.append(mark).append(c);
						}
					}
				}else{
					if(">:".equals(mark)||">6".equals(mark)){
						prints1.append(c);
					}else{
						mark = ">6";
						prints1.append(mark).append(c);
					}
				}
				
			}else{//除数字外的其他字符
				if(">:".equals(mark)||">6".equals(mark)){
					prints1.append(c);
				}else{
					mark = ">6";
					prints1.append(mark).append(c);
				}
			}
			i++;
		}
		return prints1;
	}
}
