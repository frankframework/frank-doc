package org.frankframework.frankdoc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Tests that FrankConfig-strict.xsd and FrankConfig-compatibility.xsd can validate a valid XML.
 * @author martijn
 *
 */
public class ValidateTest {
	private static final Logger log = LogUtil.getLogger(ValidateTest.class);

	static final String JAXP_SCHEMA_LANGUAGE =
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	static final String W3C_XML_SCHEMA =
			"http://www.w3.org/2001/XMLSchema";

	static final String JAXP_SCHEMA_SOURCE =
			"http://java.sun.com/xml/jaxp/properties/schemaSource";

	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.validate.";
	private static final String DIGESTER_RULES = "/doc/general-test-digester-rules.xml";

	private static final String TEST_XML = Arrays.asList(
			"<Start xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
			"    xsi:noNamespaceSchemaLocation=\"./validationTest.xsd\">",
			"  <MyA active=\"true\">",
			"    <B myAttribute=\"true\" />",
			"  </MyA>",
			"</Start>").stream().collect(Collectors.joining("\n"));

	@Test
	public void test() throws Exception {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankDocModel model = FrankDocModel.populate(TestUtil.getTestFileURL(DIGESTER_RULES), PACKAGE + "Start", classRepository);
		DocWriterNew writer = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF);
		writer.init(PACKAGE + "Start", XsdVersion.STRICT);
		String schemaString = writer.getSchema();
		File schemaOutputFile = new File("validationTest.xsd");
		log.info("Writing schema to file [{}]", schemaOutputFile.getAbsolutePath());
		Writer schemaWriter = new FileWriter(schemaOutputFile);
		schemaWriter.write(schemaString);
		schemaWriter.flush();
		schemaWriter.close();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		DocumentBuilder db = dbf.newDocumentBuilder();
		ErrorHandler errorHandler = new ErrorHandler();
		db.setErrorHandler(errorHandler);
		db.parse(new InputSource(new StringReader(TEST_XML)));
		assertFalse(errorHandler.getErrorString(), errorHandler.isError());
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		spf.setNamespaceAware(true);
		SAXParser parser = spf.newSAXParser();
		parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		InputSource testInput = new InputSource(new StringReader(TEST_XML));
		errorHandler = new ErrorHandler();
		parser.parse(testInput, errorHandler);
		assertFalse(errorHandler.getErrorString(), errorHandler.isError());
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(schemaOutputFile);
		} catch(Exception e) {
			log.error("Could not create schema", e);
			fail(e.toString());
		}
		Validator validator = schema.newValidator();
		validator.setFeature("http://apache.org/xml/features/validation/schema-full-checking" , true);
		errorHandler = new ErrorHandler();
		validator.setErrorHandler(errorHandler);
		validator.validate(new SAXSource(new InputSource(new StringReader(TEST_XML))));
		assertFalse(errorHandler.getErrorString(), errorHandler.isError());
	}
}
