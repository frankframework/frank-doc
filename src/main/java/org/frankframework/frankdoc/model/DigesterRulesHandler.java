/*
   Copyright 2020, 2021 WeAreFrank!

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
package org.frankframework.frankdoc.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses file digester-rules.xml.
 * 
 * Copied from package nl.nn.adapterframework.configuration.digester.
 * 
 */
public abstract class DigesterRulesHandler extends DefaultHandler {
	protected final Logger log = LogUtil.getLogger(DigesterRulesHandler.class);

	/**
	 * Parse all digester rules as {@link DigesterRule}
	 */
	@Override
	public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if("rule".equals(qName)) {
			DigesterRule rule = new DigesterRule();
			for (int i = 0; i < attributes.getLength(); ++i) {
				String method = attributes.getQName(i);
				String value = attributes.getValue(i);
				try {
					invokeSetter(rule, method, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.warn("unable to set method ["+method+"] with value ["+value+"]");
					e.printStackTrace();
				}
			}

			handle(rule);
		}
	}

	private void invokeSetter(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
		Class<?> clazz = bean.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if(pd.getName().equals(name)) {
					Object[] args = { value };
					pd.getWriteMethod().invoke(bean, args);
				}
			}
		} catch (IntrospectionException e) {
			throw new InvocationTargetException(e);
		}
	}

	protected abstract void handle(DigesterRule rule) throws SAXException;
}
