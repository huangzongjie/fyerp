package com.graly.promisone.base.ui.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class StringUtil {
	
	public static List<String> parseClauseParam(String clauseString){
		List<String> paramList = new ArrayList<String>();
		char[] chars = clauseString.toCharArray();
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i < chars.length; i++) {
			if(chars[i] == ':') {
				for(int j = i+1; j < chars.length && chars[j] != ',' && chars[j] != ' ' && chars[j] != ')'; j++) {
					sb.append(chars[j]);
				}
				if (!paramList.contains(sb.toString())){
					paramList.add(sb.toString());
				}
				sb = new StringBuffer("");
			} else {
				continue;
			}
		}
		return paramList;
	}
	
	public static String parseClause(String clauseString, Map<String, String> paramMap){
		StringBuffer sb = new StringBuffer(clauseString+" ");
		char[] chs = sb.toString().toCharArray();
		int start=0, index=0;
		
		for(int i = 0; i < chs.length; i++) {
			StringBuffer temp = new StringBuffer("");
			if(chs[i] == ':') {
				start = i;
				for(int j = i+1; j < chs.length; j++) {
					if(chs[j] == ',' || chs[j] == ' ' || chs[j] == ')') {
						index = j - i;						
						break;
					}
					temp.append(chs[j]);
				}
				int offest = sb.indexOf(":");
				
				sb.delete(offest, offest + index);
				String insert = paramMap.get(temp.toString());
				insert = "'" + insert + "'";
				
				sb.insert(offest, insert);
			} else {
				continue;
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		List<String> aa = parseClauseParam("where nam=:gooden and age=:188");
		String clause = "nanme=:xyz and age=:age";
		
		Map<String, String> paraMap = new LinkedHashMap<String, String>();
		paraMap.put("xyz", "Jimwmmmmmmmmmmmmmmm");
		paraMap.put("age", "23");
		
		String re = parseClause(clause, paraMap);
		System.out.println("re= "+re);
	}
}