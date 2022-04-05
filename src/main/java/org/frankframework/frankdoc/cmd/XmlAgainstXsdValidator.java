/* 
Copyright 2022 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package org.frankframework.frankdoc.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class XmlAgainstXsdValidator {
	public static void main(String[] argv) {
		try {
			if((argv.length <= 0) || (argv.length >= 3)) {
				printUsage();
				System.exit(2);
			} else {
				if(! checkXmlAgainstXsd(argv[0], argv[1])) {
					System.exit(1);
				}
			}
		}
		catch(Exception e) {
			System.out.println(String.format("Failed validating XML file [%s] against XSD [%s]: ", argv[0], argv[1]));
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void printUsage() {
		System.out.println("Usage: java -jar <name-of-jar-file-containing-this-class> <fileNameXml> <fileNameXsd>");
	}

	private static boolean checkXmlAgainstXsd(String fileNameXml, String fileNameXsd) throws ParserConfigurationException, SAXException, IOException {
		ValidatorHandler validatorHandler = getValidatorHandler(fileToUrl(new File(fileNameXsd)));
		CustomErrorHandler errorHandler = new CustomErrorHandler();
		validatorHandler.setErrorHandler(errorHandler);
		InputSource inputSource = fileToInputSource(new File(fileNameXml));
		XMLReader xmlReader = registerContentHandler(getXmlReader(), getValidatorHandler(fileToUrl(new File(fileNameXsd))));
		xmlReader.parse(inputSource);
		return ! errorHandler.hasErrors;
	}

	private static URL fileToUrl(File f) throws IOException {
		return f.toURI().toURL();
	}

	private static ValidatorHandler getValidatorHandler(URL schemaURL) throws SAXException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(schemaURL); 
		return schema.newValidatorHandler();
	}

	private static XMLReader registerContentHandler(XMLReader xmlReader, ContentHandler handler) throws SAXException {
		xmlReader.setContentHandler(handler);
		if (handler instanceof LexicalHandler) {
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		}
		if (handler instanceof ErrorHandler) {
			xmlReader.setErrorHandler((ErrorHandler)handler);
		}
		return xmlReader;
	}

	private static XMLReader getXmlReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		// In the original F!F code from which this was copied, an entity resolver is set.
		// The Frank!Doc XSDs do not reference entities, so this is omitted here.
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		XMLReader xmlReader = factory.newSAXParser().getXMLReader();
		return xmlReader;
	}

	private static InputSource fileToInputSource(File f) throws IOException {
		InputSource inputSource = new InputSource(new FileInputStream(f));
		inputSource.setSystemId(f.getPath());
		return inputSource;
	}

	private static class CustomErrorHandler implements ErrorHandler {
		boolean hasErrors = false;

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			System.out.println("Warning encountered:");
			exception.printStackTrace();
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			System.out.println("Error encountered:");
			exception.printStackTrace();
			hasErrors = true;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			System.out.println("Fatal error encountered:");
			exception.printStackTrace();
			hasErrors = true;
		}
	}
}
