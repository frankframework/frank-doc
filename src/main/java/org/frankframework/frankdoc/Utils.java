/* 
Copyright 2020, 2021, 2022 WeAreFrank! 

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

package org.frankframework.frankdoc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.FrankType;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Utility methods for the Frank!Doc.
 * @author martijn
 *
 */
public final class Utils {
	private static final String JAVA_STRING = "java.lang.String";
	private static final String JAVA_INTEGER = "java.lang.Integer";
	private static final String JAVA_BOOLEAN = "java.lang.Boolean";
	private static final String JAVA_LONG = "java.lang.Long";
	private static final String JAVA_BYTE = "java.lang.Byte";
	private static final String JAVA_SHORT = "java.lang.Short";

	private static final String JAVADOC_TOFLATTEN_START = "{@";
	private static final String JAVADOC_TOFLATTEN_STOP = "}";

	private static Map<String, String> primitiveToBoxed = new HashMap<>();
	static {
		primitiveToBoxed.put("int", JAVA_INTEGER);
		primitiveToBoxed.put("boolean", JAVA_BOOLEAN);
		primitiveToBoxed.put("long", JAVA_LONG);
		primitiveToBoxed.put("byte", JAVA_BYTE);
		primitiveToBoxed.put("short", JAVA_SHORT);
	}

	private static final Set<String> JAVA_BOXED = new HashSet<String>(Arrays.asList(new String[] {
			JAVA_STRING, JAVA_INTEGER, JAVA_BOOLEAN, JAVA_LONG, JAVA_BYTE, JAVA_SHORT}));

	// All types that are accepted by method isGetterOrSetter() 
	public static final Set<String> ALLOWED_SETTER_TYPES = new HashSet<>();
	static {
		ALLOWED_SETTER_TYPES.addAll(primitiveToBoxed.keySet());
		ALLOWED_SETTER_TYPES.addAll(JAVA_BOXED);
	}

	private Utils() {
	}

	public static boolean isAttributeGetterOrSetter(FrankMethod method) {
		boolean isSetter = method.getReturnType().isPrimitive()
				&& method.getReturnType().getName().equals("void")
				&& (method.getParameterTypes().length == 1)
				&& (! method.isVarargs())
				&& (method.getParameterTypes()[0].isPrimitive()
						|| JAVA_BOXED.contains(method.getParameterTypes()[0].getName())
						|| method.getParameterTypes()[0].isEnum());
		boolean isGetter = (
					(method.getReturnType().isPrimitive()
							&& !method.getReturnType().getName().equals("void"))
					|| JAVA_BOXED.contains(method.getReturnType().getName())
					|| method.getReturnType().isEnum()
				) && (method.getParameterTypes().length == 0);
		return isSetter || isGetter;
	}

	public static boolean isConfigChildSetter(FrankMethod method) {
		return (method.getParameterTypes().length == 1)
				&& configChildSetter(method.getName(), method.getParameterTypes()[0])
				&& (method.getReturnType().isPrimitive())
				&& (method.getReturnType().getName().equals("void"));
	}

	private static boolean configChildSetter(String methodName, FrankType parameterType) {
		boolean objectConfigChild = (! parameterType.isPrimitive())
				&& (! JAVA_BOXED.contains(parameterType.getName()));
		// A ConfigChildSetterDescriptor for a TextConfigChild should not start with "set".
		// If that would be allowed then we would have confusing with attribute setters.
		boolean textConfigChild = (! methodName.startsWith("set")) && parameterType.getName().equals(JAVA_STRING);
		return objectConfigChild || textConfigChild;
	}
	
	public static String promoteIfPrimitive(String typeName) {
		if(primitiveToBoxed.containsKey(typeName)) {
			return primitiveToBoxed.get(typeName);
		} else {
			return typeName;
		}
	}

	public static String toUpperCamelCase(String arg) {
		return arg.substring(0,  1).toUpperCase() + arg.substring(1);
	}

	public static <T> void addToSortedListUnique(List<T> list, T item) {
		int index = Collections.binarySearch(list, item, null);
		if (index < 0) {
			list.add(binarySearchResultToInsertionPoint(index), item);
		}
	}

	public static <T> void addToSortedListNonUnique(List<T> list, T item) {
		int index = Utils.binarySearchResultToInsertionPoint(Collections.binarySearch(list, item, null));
		list.add(index, item);		
	}

	private static int binarySearchResultToInsertionPoint(int index) {
		// See https://stackoverflow.com/questions/16764007/insert-into-an-already-sorted-list/16764413
		// for more information.
		if (index < 0) {
			index = -index - 1;
		}
		return index;
	}

	public static InputSource asInputSource(URL url) throws IOException {
		InputSource inputSource = new InputSource(url.openStream());
		inputSource.setSystemId(url.toExternalForm());
		return inputSource;
	}

	public static void parseXml(InputSource inputSource, ContentHandler handler) throws IOException, SAXException {
		parseXml(inputSource, handler, null);
	}
	
	public static void parseXml(InputSource inputSource, ContentHandler handler, ErrorHandler errorHandler) throws IOException, SAXException {
		XMLReader xmlReader;
		try {
			xmlReader = getXMLReader(handler);
			if (errorHandler != null) {
				xmlReader.setErrorHandler(errorHandler);
			}
		} catch (ParserConfigurationException e) {
			throw new SAXException("Cannot configure parser",e);
		}
		xmlReader.parse(inputSource);
	}

	private static XMLReader getXMLReader(ContentHandler handler) throws ParserConfigurationException, SAXException {
		XMLReader xmlReader = getXMLReader(true);
		xmlReader.setContentHandler(handler);
		if (handler instanceof LexicalHandler) {
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		}
		if (handler instanceof ErrorHandler) {
			xmlReader.setErrorHandler((ErrorHandler)handler);
		}
		return xmlReader;
	}

	public static XMLReader getXMLReader(boolean namespaceAware) throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = getSAXParserFactory(namespaceAware);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		XMLReader xmlReader = factory.newSAXParser().getXMLReader();
		return xmlReader;
	}

	public static SAXParserFactory getSAXParserFactory(boolean namespaceAware) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);
		return factory;
	}

	public static String jsonPretty(String json) {
		StringWriter sw = new StringWriter();
		JsonReader jr = Json.createReader(new StringReader(json));
		JsonObject jobj = jr.readObject();

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);

		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		try (JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
			jsonWriter.writeObject(jobj);
		}

		return sw.toString().trim();
	}

	public static String flattenJavaDocLinksToLastWords(String text) throws FrankDocException {
		StringBuilder result = new StringBuilder();
		int currentIndex = 0;
		int nextToFlatten = text.indexOf(JAVADOC_TOFLATTEN_START, currentIndex);
		while(nextToFlatten >= 0) {
			result.append(text.substring(currentIndex, nextToFlatten));
			int toFlattenEnd = text.indexOf(JAVADOC_TOFLATTEN_STOP, nextToFlatten);
			if(toFlattenEnd < 0) {
				throw new FrankDocException(String.format("Unfinished JavaDoc link in text [%s] at index [%d]", text, nextToFlatten), null);
			}
			String bodyToFlatten = text.substring(nextToFlatten + JAVADOC_TOFLATTEN_START.length(), toFlattenEnd);
			result.append(getLinkReplacement(bodyToFlatten));
			currentIndex = toFlattenEnd + 1;
			if(currentIndex >= text.length()) {
				return result.toString();
			}
			nextToFlatten = text.indexOf(JAVADOC_TOFLATTEN_START, currentIndex);
		}
		result.append(text.substring(currentIndex));
		return result.toString();
	}

	private static String getLinkReplacement(String linkBody) {
		String[] words = linkBody.split("[ \\t]");
		// {@link} should be replaced by the empty string.
		if((words.length == 0) || (words.length == 1)) {
			return "";
		}
		return words[words.length - 1];
	}

	public static boolean equalsNullable(Object o1, Object o2) {
		if(o1 == null) {
			if(o2 == null) {
				return true;
			} else {
				return false;
			}
		} else {
			if(o2 == null) {
				return false;
			} else {
				return o1.equals(o2);
			}
		}
	}
}
