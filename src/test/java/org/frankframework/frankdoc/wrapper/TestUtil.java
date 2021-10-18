package org.frankframework.frankdoc.wrapper;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.testdoclet.EasyDoclet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.javadoc.ClassDoc;

public final class TestUtil {
	public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
	public static final String DEFAULT_INPUT_STREAM_ENCODING=DEFAULT_CHARSET.displayName();

	private static final Properties BUILD_PROPERTIES = new TestUtil().loadBuildProperties();
	private static final File TEST_SOURCE_DIRECTORY = new File(BUILD_PROPERTIES.getProperty("testSourceDirectory"));

	static final String JAVADOC_GROUP_ANNOTATION = "nl.nn.adapterframework.doc.FrankDocGroup";
	static final String JAVADOC_DEFAULT_VALUE_TAG = "@ff.default";

	private TestUtil() {
	}

	static FrankMethod getDeclaredMethodOf(FrankClass clazz, String methodName) {
		FrankMethod[] methods = clazz.getDeclaredMethods();
		for(FrankMethod m: methods) {
			if(m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}

	public static FrankClassRepository getFrankClassRepositoryDoclet(String ...packages) {
		ClassDoc[] classes = getClassDocs(packages);
		return FrankClassRepository.getDocletInstance(classes, new HashSet<>(Arrays.asList(packages)), new HashSet<>(), new HashSet<>());
	}

	public static ClassDoc[] getClassDocs(String ...packages) {
		System.out.println("System property java.home: " + System.getProperty("java.home"));
		EasyDoclet doclet = new EasyDoclet(TEST_SOURCE_DIRECTORY, packages);
		return doclet.getRootDoc().classes();
	}

	private Properties loadBuildProperties() {
		try {
			Properties result = new Properties();
			InputStream is = getClass().getClassLoader().getResource("build.properties").openStream();
			result.load(is);
			return result;
		} catch(IOException e) {
			throw new RuntimeException("Cannot load build.properties", e);
		}
	}

	public static URL resourceAsURL(String path) throws IOException {
		if(! path.startsWith("/")) {
			path = "/" + path;
		}
		return TestUtil.class.getResource(path);
	}

	public static SAXSource inputSourceToSAXSource(InputSource is, boolean namespaceAware) throws SAXException {
		try {
			return new SAXSource(Utils.getXMLReader(namespaceAware), is);
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
	}

	public static void assertJsonEqual(String description, String jsonExp, String jsonAct) {
		assertEquals(description, Utils.jsonPretty(jsonExp), Utils.jsonPretty(jsonAct));
	}

	public static String getTestFile(String file) throws IOException {
		return getTestFile(file, "UTF-8");
	}

	public static String getTestFile(String file, String charset) throws IOException {
		URL url = getTestFileURL(file);
		if (url == null) {
			System.out.println("file [" + file + "] not found");
			return null;
		}
		return getTestFile(url, charset);
	}

	public static URL getTestFileURL(String file) throws IOException {
		return TestUtil.class.getResource(file);
	}

	public static String getTestFile(URL url, String charset) throws IOException {
		if (url == null) {
			return null;
		}
		return readLines(getCharsetDetectingInputStreamReader(url.openStream(), charset));
	}

	public static String readLines(Reader reader) throws IOException {
		BufferedReader buf = new BufferedReader(reader);
		StringBuilder string = new StringBuilder();
		String line = buf.readLine();
		while (line != null) {
			string.append(line);
			line = buf.readLine();
			if (line != null) {
				string.append("\n");
			}
		}
		return string.toString();
	}

	/**
	 * Return a Reader that reads the InputStream in the character set specified by the BOM. If no BOM is found, a default character set is used.
	 */
	public static Reader getCharsetDetectingInputStreamReader(InputStream inputStream, String defaultCharset) throws IOException {
		BOMInputStream bOMInputStream = new BOMInputStream(inputStream,ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE);
		ByteOrderMark bom = bOMInputStream.getBOM();
		String charsetName = bom == null ? defaultCharset : bom.getCharsetName();

		if(StringUtils.isEmpty(charsetName)) {
			charsetName = DEFAULT_INPUT_STREAM_ENCODING;
		}

		return new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);
	}

	static public void assertEqualsIgnoreCRLF(String expected, String actual) {
		assertEqualsIgnoreCRLF(null, expected, actual);
	}

	static public void assertEqualsIgnoreCRLF(String message, String expected, String actual) {
		assertEquals(message, expected.trim().replace("\r",""), actual.trim().replace("\r",""));
	}
}
