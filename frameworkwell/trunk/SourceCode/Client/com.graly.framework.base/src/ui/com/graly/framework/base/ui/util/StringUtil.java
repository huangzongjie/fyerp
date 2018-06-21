package com.graly.framework.base.ui.util;

import static java.util.Locale.ENGLISH;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtil {
	public static final String SPLIT = ";";
	
	/*
	 * 获得以分号分开的属性列表,如" sex =:age AND address=:address; name=:sex and age=:age"
	 * 将会得到age, address, sex和age字符串列表
	 */
	public static List<String> parseClauseParam(String clauseString) {
		List<String> arrtibutes = new ArrayList<String>();
		String[] strs = clauseString.split(SPLIT);
		for(String clause : strs) {
			arrtibutes.addAll(getModelAttribute(clause.trim()));
		}
		return arrtibutes;
	}
	
	private static List<String> getModelAttribute(String clauseString) {
		List<String> paramList = new ArrayList<String>();
		if(clauseString == null || "".endsWith(clauseString.trim())) {
			return paramList;
		}
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
	
	/*
	 * 组建以分号分开的某一子句的where语句, 例如clauseString: " sex =:age AND address=:address ;name=:xyz and age=:age",
	 * paramMap: {xyz=Jim, age=23}, 
	 * 则将会返回 name='Jim' and age='23'
	 */
	public static String parseClause(String clauseString, Map<String, String> paramMap){
		String[] strs = clauseString.split(SPLIT);
		String clase = "";
		for(String str : strs) {
			boolean flag = true;
			for(String param : getModelAttribute(str.trim())) {
				if(!paramMap.keySet().contains(param)) {
					flag = false;
					break;
				}
			}
			if(flag) {
				clase = parseSingleClause(str.trim(), paramMap);				
			}
		}
		return clase;
	}
	
	private static String parseSingleClause(String clauseString, Map<String, String> paramMap) {
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
	
	public static String pareseWhereClause(String whereClause){
		if(whereClause != null && !"".equals(whereClause.trim())){
			//判断whereClause中是否包含$xxx的字符
			if(whereClause.contains("$")){
				char[] chars = whereClause.toCharArray();
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<chars.length; i++){
					char c = chars[i];
					if(c == '$'){
						try {
							StringBuffer sb2 = new StringBuffer();
							for(int j = i+1; j<chars.length && chars[j] != ' ' && chars[j] != ')';j++){
								sb2.append(chars[j]);
								i=j;
							}
							String name = sb2.toString();
							String methodName = "get"+name.substring(0, 1).toUpperCase() + name.substring(1);
							Method mtd = Env.class.getMethod(methodName);
							Object o  = mtd.invoke(Env.class.newInstance());
							sb.append("'"+String.valueOf(o)+"'");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						sb.append(chars[i]);
					}
				}
				return sb.toString();
			}else{
				return whereClause;
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
//		List<String> aa = parseClauseParam("where name=:gooden and age=:188");
//		String clause = "name=:xyz and age=:age; sex =:sex AND address=:address ";
//		for(String attri : parseClauseParam(clause)) {
//			System.out.println(attri);
//		}
		
		String clause = " sex =:age AND address=:address ;name=:xyz and age=:age";
		Map<String, String> paraMap = new LinkedHashMap<String, String>();
		paraMap.put("xyz", "Jim");
		paraMap.put("age", "23");
		String s = "xxxxxx =  $userRrn";
		pareseWhereClause(s);
		String re = parseClause(clause, paraMap);
		System.out.println(re);
		System.out.println(parseClause(clause, paraMap));
		
	}
}
