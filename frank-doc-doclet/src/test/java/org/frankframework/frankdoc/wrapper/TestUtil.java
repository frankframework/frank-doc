package org.frankframework.frankdoc.wrapper;

import com.sun.source.util.DocTrees;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import jdk.javadoc.internal.tool.Start;
import lombok.extern.log4j.Log4j2;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.testdoclet.EasyDoclet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.lang.model.element.Element;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Log4j2
public final class TestUtil {
	private static final Properties BUILD_PROPERTIES = new TestUtil().loadBuildProperties();
	private static final File TEST_SOURCE_DIRECTORY = new File(BUILD_PROPERTIES.getProperty("testSourceDirectory"));

	public static final String DEPRECATED = "java.lang.Deprecated";

	static final String JAVADOC_GROUP_ANNOTATION = "org.frankframework.doc.FrankDocGroup";
	static final String JAVADOC_DEFAULT_VALUE_TAG = "@ff.default";

	private TestUtil() {
	}

	static FrankMethod getDeclaredMethodOf(FrankClass clazz, String methodName) {
		FrankMethod[] methods = clazz.getDeclaredMethods();
		for (FrankMethod m : methods) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}

	public static FrankClassRepository getFrankClassRepositoryDoclet(String... packages) {
		Set<? extends Element> classes = getIncludedElements(packages);
		return new FrankClassRepository(getDocTrees(), classes, new HashSet<>(Arrays.asList(packages)), new HashSet<>(), new HashSet<>());
	}

	public static Set<? extends Element> getIncludedElements(String... packages) {
		initEasyDoclet(packages);
		return EasyDoclet.getIncludedElements();
	}

	static void initEasyDoclet(String... packages) {
		// Cache the previous run if the packages are the same. Saves 50% of the time.
		if (Arrays.equals(packages, EasyDoclet.getPackages())) {
			return;
		}
		if (packages == null || packages.length == 0) {
			throw new IllegalArgumentException("At least one package must be specified");
		}
		EasyDoclet.setPackages(packages);
		log.debug("System property java.home: {}", System.getProperty("java.home"));
		for (String pack : packages) {
			log.debug("Package: {}", pack);
		}

		Start start = new Start(new Context());
		Iterable<String> options = Arrays.asList("-sourcepath", TEST_SOURCE_DIRECTORY.getAbsolutePath(), "-doclet", EasyDoclet.class.getName(), "-subpackages", String.join(":", packages));
		ArrayList<JavaFileObject> fileObjects = new ArrayList<>();
		try (JavaFileManager fileManager = new JavacFileManager(new Context(), true, StandardCharsets.UTF_8)) {
			for (String pack : packages) {
				log.debug("Listing source path for package {}", pack);
				Iterable<JavaFileObject> packageFileObjects = fileManager.list(StandardLocation.SOURCE_PATH, pack, Set.of(JavaFileObject.Kind.SOURCE), true);
				packageFileObjects.iterator().forEachRemaining(fileObjects::add);
			}
			start.begin(EasyDoclet.class, options, fileObjects);
		} catch (IOException e) {
			log.error("Cannot create file manager OR cannot process Doclet generation", e);
			throw new RuntimeException("Cannot create file manager OR cannot process Doclet generation", e);
		}
	}

	static DocTrees getDocTrees() {
		return EasyDoclet.getDocTrees();
	}

	private Properties loadBuildProperties() {
		try {
			Properties result = new Properties();
			InputStream is = getClass().getClassLoader().getResource("build.properties").openStream();
			result.load(is);
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Cannot load build.properties", e);
		}
	}

	public static URL resourceAsURL(String path) throws IOException {
		if (!path.startsWith("/")) {
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
		return getTestFile(file, StandardCharsets.UTF_8);
	}

	public static String getTestFile(String file, Charset charset) throws IOException {
		URL url = getTestFileURL(file);
		if (url == null) {
			System.out.println("file [" + file + "] not found");
			return null;
		}
		return getTestFile(url, charset);
	}

	public static URL getTestFileURL(String file) {
		return TestUtil.class.getResource(file);
	}

	public static String getTestFile(URL url, Charset charset) throws IOException {
		if (url == null) {
			return null;
		}
		InputStreamReader isr = new InputStreamReader(new BufferedInputStream(url.openStream()), charset);
		return readLines(isr);
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

	static public void assertEqualsIgnoreCRLF(String expected, String actual) {
		assertEqualsIgnoreCRLF(null, expected, actual);
	}

	static public void assertEqualsIgnoreCRLF(String message, String expected, String actual) {
		assertEquals(message, expected.trim().replace("\r", ""), actual.trim().replace("\r", ""));
	}
}
