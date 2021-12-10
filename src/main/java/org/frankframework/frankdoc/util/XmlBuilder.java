/*
   Copyright 2013, 2018 Nationale-Nederlanden, 2020, 2021 WeAreFrank!

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
package org.frankframework.frankdoc.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Builds a XML-element with attributes and sub-elements. Attributes can be
 * added with the addAttribute method, the content can be set with the setValue
 * method. Subelements can be added with the addSubElement method. the toXML
 * function returns the node and subnodes as an indented xml string.
 * <p/>
 * Before February 2018 this class was deprecated. From then it uses the JDOM
 * standard solution.
 * 
 * @author Johan Verrips
 * @author Peter Leeuwenburgh
 **/
public class XmlBuilder {
	static Logger log = LogUtil.getLogger(XmlBuilder.class);
	
	private Element element;

	public XmlBuilder(String tagName) {
		element = new Element(tagName);
	}

	public XmlBuilder(String tagName, String prefix, String uri) {
		element = new Element(tagName, prefix, uri);
	}

	public void addAttribute(String name, String value) {
		if (value != null) {
			if (name.equalsIgnoreCase("xmlns")) {
				element.setNamespace(Namespace.getNamespace(value));
			} else if (StringUtils.startsWithIgnoreCase(name, "xmlns:")) {
				String prefix = name.substring(6);
				element.addNamespaceDeclaration(
						Namespace.getNamespace(prefix, value));
			} else {
				element.setAttribute(new Attribute(name, value));
			}
		}
	}

	public void addSubElement(XmlBuilder newElement) {
		addSubElement(newElement, true);
	}

	public void addSubElement(XmlBuilder newElement, boolean adoptNamespace) {
		if (newElement != null) {
			if (adoptNamespace
					&& StringUtils.isNotEmpty(element.getNamespaceURI())) {
				addNamespaceRecursive(newElement.element,
						element.getNamespace());
			}
			element.addContent(newElement.element);
		}
	}

	private void addNamespaceRecursive(Element element, Namespace namespace) {
		if (StringUtils.isEmpty(element.getNamespaceURI())) {
			element.setNamespace(namespace);
			List<Element> childList = element.getChildren();
			if (!childList.isEmpty()) {
				for (Element child : childList) {
					addNamespaceRecursive(child, namespace);
				}
			}
		}
	}

	public void setValue(String value) {
		if (value != null) {
			element.setText(value);
		}
	}

	public String toXML() {
		return toXML(false);
	}

	public String toXML(boolean xmlHeader) {
		Document document = new Document(element.detach());
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(
				Format.getPrettyFormat().setOmitDeclaration(!xmlHeader));
		return xmlOutputter.outputString(document);
	}
}
