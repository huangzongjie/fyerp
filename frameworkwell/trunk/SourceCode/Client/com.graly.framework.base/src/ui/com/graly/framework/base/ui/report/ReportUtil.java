package com.graly.framework.base.ui.report;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ReportUtil {
	
	private static final Logger logger = Logger.getLogger(ReportUtil.class);
	
	private final static String APPCONTEXT_HOST = "localhost"; 
	private final static String APPCONTEXT_PORT = "8080"; 
	private final static String APPCONTEXT_WEBAPP = "ERPreport"; 
	
	public final static String SERVLET_NAME_KEY = "SERVLET_NAME_KEY"; 
	public final static String FORMAT_KEY = "FORMAT_KEY";
	public final static String ALLOW_PAGE_KEY = "ALLOW_PAGE_KEY";
	public final static String RESOURCE_FOLDER_KEY = "RESOURCE_FOLDER_KEY"; 
	public final static String MAX_ROWS_KEY = "MAX_ROWS_KEY"; 
	public final static String MAX_CUBE_ROW_LEVELS_KEY = "MAX_CUBE_ROW_LEVELS_KEY"; 
	public final static String MAX_CUBE_COLUMN_LEVELS_KEY = "MAX_CUBE_COLUMN_LEVELS_KEY"; 
	public final static String DOCUMENT_NAME_KEY = "DOCUMENT_NAME_KEY";
	public final static String CLOSE_WINDOW_KEY = "CLOSE_WINDOW_KEY";
	public final static String OUTPUT_DOCUMENT_KEY = "OUTPUT_DOCUMENT_KEY";
	public final static String SHOW_PARAMETER_PAGE_KEY = "SHOW_PARAMETER_PAGE";
	
	private static final String PARAM_REPORT = "__report";
	private static final String PARAM_REPORT_DOCUMENT = "__document";
	private static final String PARAM_FORMAT = "__format";
	private static final String PARAM_MAXROWS = "__maxrows";
	private static final String PARAM_MAXCUBE_ROWLEVELS = "__maxrowlevels"; //$NON-NLS-1$
	private static final String PARAM_MAXCUBE_COLUMNLEVELS = "__maxcolumnlevels"; //$NON-NLS-1$
	private static final String PARAM_RESOURCE_FOLDER = "__resourceFolder";
	private static final String PARAM_CLOSEWIN = "__closewin";
	private static final String PARAM_OUTPUT_DOCUMENT_NAME = "__outputDocName";
	private static final String PARAM_PARAMETER_PAGE = "__parameterpage";
	
	private static final String UTF_8 = "utf-8";
	private final static String HTML = "html";
	private final static String PDF = "pdf";
	private final static String DOC = "doc";
	
	public static final String VIEWER_PREVIEW = "preview";
	public static final String VIEWER_FRAMESET = "frameset";
	public static final String VIEWER_DOCUMENT = "document";
	
	public static String createURL(String report, Map<String, String> userParams) {
		return createURL(report, new HashMap<String, Object>(), userParams);
	}
	
	public static String createURL(String report, Map<String, Object> params, Map<String, String> userParams) {
		if (params == null || params.isEmpty()) {
			return createURL(null, report, null, null, null, null, null);
		}
		String servletName = (String) params.get( SERVLET_NAME_KEY );
		String format = (String) params.get( FORMAT_KEY );
		String resourceFolder = (String) params.get( RESOURCE_FOLDER_KEY );
		Boolean allowPage = (Boolean) params.get( ALLOW_PAGE_KEY );
		String outputDocName = (String) params.get( OUTPUT_DOCUMENT_KEY );
		String showParameter = (String) params.get( SHOW_PARAMETER_PAGE_KEY );
		
		if (isBlank(format)) {
			format = HTML;
		}
		
		if (!HTML.equalsIgnoreCase(format)) {
			servletName = VIEWER_PREVIEW;
		} else {
			if (isBlank(servletName)) {
				if (allowPage == null) {
					servletName = VIEWER_FRAMESET;
				} else {
					servletName = allowPage.booleanValue() ? VIEWER_FRAMESET : VIEWER_PREVIEW;
				}
			}
		}
		
		// max rows setting
		String maxrows = (String) params.get( MAX_ROWS_KEY );

		// max level member setting
		String maxrowlevels = (String) params.get( MAX_CUBE_ROW_LEVELS_KEY );
		String maxcolumnlevels = (String) params.get( MAX_CUBE_COLUMN_LEVELS_KEY );
		
		Map<String, String> urlParams = prepareCommonURLParams(format,
				resourceFolder, maxrows, maxrowlevels, maxcolumnlevels);

		// if document mode, append document parameter in URL
		String documentName = (String) params.get( DOCUMENT_NAME_KEY );
		if (documentName != null && VIEWER_DOCUMENT.equals(servletName)) {
			// current opened report isn't document
			if (!isReportDocument(report)) {
				try {
					String encodedDocumentName = URLEncoder.encode(documentName, UTF_8);
					urlParams.put(PARAM_REPORT_DOCUMENT, encodedDocumentName);

					String isCloseWin = (String) params.get(CLOSE_WINDOW_KEY);
					if (isCloseWin != null) {
						urlParams.put(PARAM_CLOSEWIN, isCloseWin);
					}
				} catch (UnsupportedEncodingException e) {
					logger.warn(e.getLocalizedMessage(), e);
				}
			}
		}
		
		if (!isBlank(outputDocName)) {
			try {
				String encodedOutputDocumentName = URLEncoder.encode(outputDocName, UTF_8);
				urlParams.put(PARAM_OUTPUT_DOCUMENT_NAME, encodedOutputDocumentName);
			} catch (UnsupportedEncodingException e) {
				logger.warn(e.getLocalizedMessage(), e);
			}
		}
		
		if ( showParameter != null ) {
			urlParams.put( PARAM_PARAMETER_PAGE, showParameter );
		}
		
		if (userParams != null && !userParams.isEmpty()) {
			urlParams.putAll(userParams);
		}
		return createURL(servletName, report, urlParams);
	}
	
	private static String createURL(String servletName,
			String report, String format, String resourceFolder,
			String maxrows, String maxrowlevels, String maxcolumnlevels) {
		return createURL(servletName, report,
				prepareCommonURLParams(format, resourceFolder, maxrows, maxrowlevels, maxcolumnlevels));
	}

	
	private static String createURL(String servletName, String report, Map<String, String> urlParams) {
		String encodedReportName = null;

		if (isBlank(servletName)) {
			servletName = VIEWER_FRAMESET;
		}
		
		try {
			encodedReportName = URLEncoder.encode(report, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.warn(e.getLocalizedMessage(), e);
		}

		String reportParam = PARAM_REPORT;
		if (isReportDocument(encodedReportName)) {
			reportParam = PARAM_REPORT_DOCUMENT;
		}
		reportParam += "=" + encodedReportName; 

		// So far, only report name is encoded as utf-8 format
		return getBaseURL() + servletName + "?" + reportParam + convertParams(urlParams);
	}
	
	
	private static String getBaseURL() {
		return "http://" + APPCONTEXT_HOST + ":" + APPCONTEXT_PORT + "/" + APPCONTEXT_WEBAPP + "/"; 
	}
	
	private static boolean isReportDocument(String reportName) {
		if (reportName == null)
			return false;

		Pattern p = Pattern.compile(".[a-z]{3}document$"); 
		Matcher m = p.matcher(reportName);
		if (m.find())
			return true;

		return false;
	}
	
	private static String convertParams(Map<String, String> params) {
		if (params != null && !params.isEmpty()) {
			StringBuffer sb = new StringBuffer();

			for (Entry<String, String> entry : params.entrySet()) {
				sb.append("&").append(entry.getKey()); 

				if (entry.getValue() != null) {
					sb.append("=").append(entry.getValue()); 
				}
			}

			return sb.toString();
		}

		return ""; 
	}
	
	private static Map<String, String> prepareCommonURLParams(String format,
			String resourceFolder, String maxrows, String maxrowlevels,
			String maxcolumnlevels) {

		// handle resource folder encoding
		String encodedResourceFolder = null;

		if (resourceFolder != null) {
			try {
				encodedResourceFolder = URLEncoder.encode(resourceFolder, UTF_8);
			} catch (UnsupportedEncodingException e) {
				logger.warn(e.getLocalizedMessage(), e);
			}
		}

		if (encodedResourceFolder == null) {
			encodedResourceFolder = ""; 
		}

		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		if (format != null) {
			params.put(PARAM_FORMAT, format);
		}

		if (!isBlank(maxrows)) {
			params.put(PARAM_MAXROWS, maxrows.trim());
		}
		if (!isBlank(maxrowlevels)) {
			params.put(PARAM_MAXCUBE_ROWLEVELS, maxrowlevels.trim());
		}
		if (!isBlank(maxcolumnlevels)) {
			params.put(PARAM_MAXCUBE_COLUMNLEVELS,	maxcolumnlevels.trim());
		}

		params.put(PARAM_RESOURCE_FOLDER, encodedResourceFolder);
		return params;
	}
	
	private static boolean isBlank(String value) {
		if (value == null)
			return true;
		value = value.trim();
		if (value.length() == 0)
			return true;
		return false;
	}
	
}
