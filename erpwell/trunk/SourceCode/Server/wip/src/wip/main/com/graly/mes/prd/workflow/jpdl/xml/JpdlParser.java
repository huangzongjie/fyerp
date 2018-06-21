package com.graly.mes.prd.workflow.jpdl.xml;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.graly.mes.prd.workflow.graph.exe.Token;

public class JpdlParser implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Token.class);
	
	static final EntityResolver JPDL_ENTITY_RESOLVER = new JpdlEntityResolver();
	static SAXParserFactory saxParserFactory = createSaxParserFactory();

	public static Document parse(InputSource inputSource,
			ProblemListener problemListener) throws Exception {
		Document document = null;
		SAXReader saxReader = createSaxReader(problemListener);
		document = saxReader.read(inputSource);
		return document;
	}

	public static SAXReader createSaxReader(ProblemListener problemListener)
			throws Exception {
		XMLReader xmlReader = createXmlReader();
		SAXReader saxReader = new SAXReader(xmlReader);
		saxReader.setErrorHandler(new JpdlErrorHandler(problemListener));
		saxReader.setEntityResolver(JPDL_ENTITY_RESOLVER);
		return saxReader;
	}

	public static XMLReader createXmlReader() throws Exception {
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();

		try {
			saxParser.setProperty(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
		} catch (SAXException e) {
			logger
					.warn(
							"couldn't set xml parser property 'http://java.sun.com/xml/jaxp/properties/schemaLanguage' to 'http://www.w3.org/2001/XMLSchema'",
							e);
		}

		try {
			saxParser
					.setProperty(
							"http://apache.org/xml/properties/schema/external-schemaLocation",
							"http://jbpm.org/3/jpdl http://jbpm.org/jpdl-3.0.xsd "
									+ "urn:jbpm.org:jpdl-3.0 http://jbpm.org/jpdl-3.0.xsd "
									+ "urn:jbpm.org:jpdl-3.1 http://jbpm.org/jpdl-3.1.xsd "
									+ "urn:jbpm.org:jpdl-3.2 http://jbpm.org/jpdl-3.2.xsd");
		} catch (SAXException e) {
			logger
					.warn(
							"couldn't set xml parser property 'http://apache.org/xml/properties/schema/external-schemaLocation'",
							e);
		}

		try {
			xmlReader.setFeature(
					"http://apache.org/xml/features/validation/dynamic", true);
		} catch (SAXException e) {
			logger
					.warn(
							"couldn't set xml parser feature 'http://apache.org/xml/features/validation/dynamic'",
							e);
		}
		return xmlReader;
	}

	static class JpdlErrorHandler implements ErrorHandler, Serializable {
		private static final long serialVersionUID = 1L;
		ProblemListener problemListener = null;

		JpdlErrorHandler(ProblemListener problemListener) {
			this.problemListener = problemListener;
		}

		public void warning(SAXParseException pe) {
			addProblem(Problem.LEVEL_WARNING, "line " + pe.getLineNumber()
					+ ": " + pe.getMessage(), pe);
		}

		public void error(SAXParseException pe) {
			addProblem(Problem.LEVEL_ERROR, "line " + pe.getLineNumber() + ": "
					+ pe.getMessage(), pe);
		}

		public void fatalError(SAXParseException pe) {
			addProblem(Problem.LEVEL_FATAL, "line " + pe.getLineNumber() + ": "
					+ pe.getMessage(), pe);
		}

		void addProblem(int level, String description, Throwable exception) {
			problemListener.addProblem(new Problem(level, description,
					exception));
		}
	}

	static class JpdlEntityResolver implements EntityResolver, Serializable {
		private static final long serialVersionUID = 1L;

		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			InputSource inputSource = null;
			logger.debug("resolving schema reference publicId(" + publicId
					+ ") systemId(" + systemId + ")");

			if ("http://jbpm.org/jpdl-3.2.xsd".equals(systemId)) {
				logger
						.debug("providing input source to local 'jpdl-3.2.xsd' resource");
				inputSource = new InputSource(this.getClass()
						.getResourceAsStream("jpdl-3.2.xsd"));

			} else if ("http://jbpm.org/jpdl-3.1.xsd".equals(systemId)) {
				logger
						.debug("providing input source to local 'jpdl-3.1.xsd' resource");
				inputSource = new InputSource(this.getClass()
						.getResourceAsStream("jpdl-3.1.xsd"));

			} else if ("http://jbpm.org/jpdl-3.0.xsd".equals(systemId)) {
				logger
						.debug("providing input source to local 'jpdl-3.0.xsd' resource");
				inputSource = new InputSource(this.getClass()
						.getResourceAsStream("jpdl-3.0.xsd"));

			} else {
				logger.debug("original systemId as input source");
				inputSource = new InputSource(systemId);
			}
			return inputSource;
		}
	}

	private static SAXParserFactory createSaxParserFactory() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(true);
		return saxParserFactory;
	}

	
}
