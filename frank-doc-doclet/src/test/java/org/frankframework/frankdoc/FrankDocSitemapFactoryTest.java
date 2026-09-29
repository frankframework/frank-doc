/*
Copyright 2026 WeAreFrank!

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jakarta.json.Json;
import jakarta.json.JsonObject;

class FrankDocSitemapFactoryTest {

	@Test
	void testSitemapWithEmptyJson() throws Exception {
		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(null);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		validateUrlCount(xml, 6); // Only static routes
	}

	@Test
	void testSitemapWithElements() throws Exception {
		JsonObject elements = Json.createObjectBuilder()
			.add("org.frankframework.pipes.SomePipe", Json.createObjectBuilder().build())
			.add("org.frankframework.senders.HttpSender", Json.createObjectBuilder().build())
			.build();

		JsonObject json = Json.createObjectBuilder()
			.add("elements", elements)
			.build();

		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(json);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		// 6 static routes + 2 element routes
		validateUrlCount(xml, 8);
		assertTrue(xml.contains("org.frankframework.pipes.SomePipe"));
		assertTrue(xml.contains("org.frankframework.senders.HttpSender"));
	}

	@Test
	void testSitemapWithCredentialProviders() throws Exception {
		JsonObject credentialProviders = Json.createObjectBuilder()
			.add("BasicAuthCredentialProvider", Json.createObjectBuilder().build())
			.build();

		JsonObject json = Json.createObjectBuilder()
			.add("credentialProviders", credentialProviders)
			.build();

		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(json);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		// 6 static routes + 1 credential provider route
		validateUrlCount(xml, 7);
		assertTrue(xml.contains("credential-providers"));
		assertTrue(xml.contains("BasicAuthCredentialProvider"));
	}

	@Test
	void testSitemapWithServletAuthenticators() throws Exception {
		JsonObject servletAuthenticators = Json.createObjectBuilder()
			.add("MyAuthenticator", Json.createObjectBuilder().build())
			.build();

		JsonObject json = Json.createObjectBuilder()
			.add("servletAuthenticators", servletAuthenticators)
			.build();

		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(json);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		// 6 static routes + 1 servlet authenticator route
		validateUrlCount(xml, 7);
		assertTrue(xml.contains("servlet-authenticators"));
		assertTrue(xml.contains("MyAuthenticator"));
	}

	@Test
	void testStaticRoutesPriority() {
		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(null);
		String xml = factory.getXml();

		// Static routes should have priority
		assertTrue(xml.contains("<priority>0.8</priority>"));
	}

	@Test
	void testUrlEncodingAndXmlEscaping() throws Exception {
		JsonObject elements = Json.createObjectBuilder()
			.add("org.frankframework.test.Special&Chars", Json.createObjectBuilder().build())
			.add("org.frankframework.test.Spaces In Name", Json.createObjectBuilder().build())
			.build();

		JsonObject json = Json.createObjectBuilder()
			.add("elements", elements)
			.build();

		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(json);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		// Should have URL-encoded space as %20
		assertTrue(xml.contains("%20"));
		// Should have XML-escaped ampersand
		assertTrue(xml.contains("&amp;"));
	}

	@Test
	void testCustomBaseUrl() throws Exception {
		String customUrl = "https://example.com/docs";
		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(null, customUrl);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		assertTrue(xml.contains(customUrl));
	}

	@Test
	void testDefaultBaseUrl() throws Exception {
		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(null, null);
		String xml = factory.getXml();

		validateSitemapStructure(xml);
		assertTrue(xml.contains("https://frankdoc.frankframework.org"));
	}

	@Test
	void testChangeFrequency() {
		FrankDocSitemapFactory factory = new FrankDocSitemapFactory(null);
		String xml = factory.getXml();

		// All URLs should have changefreq
		assertTrue(xml.contains("<changefreq>weekly</changefreq>"));
	}

	private void validateSitemapStructure(String xml) throws Exception {
		assertNotNull(xml);
		assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertTrue(xml.contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"));
		assertTrue(xml.contains("</urlset>"));

		// Parse and validate XML well-formedness
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		assertNotNull(doc);
		assertEquals("urlset", doc.getDocumentElement().getTagName());
	}

	private void validateUrlCount(String xml, int expectedCount) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList urlNodes = (NodeList) xPath.evaluate("//url", doc, XPathConstants.NODESET);

		assertEquals(expectedCount, urlNodes.getLength());

		// Verify each URL has required elements
		for (int i = 0; i < urlNodes.getLength(); i++) {
			NodeList locNodes = (NodeList) xPath.evaluate("loc", urlNodes.item(i), XPathConstants.NODESET);
			assertEquals(1, locNodes.getLength(), "Each URL must have exactly one loc element");

			NodeList freqNodes = (NodeList) xPath.evaluate("changefreq", urlNodes.item(i), XPathConstants.NODESET);
			assertEquals(1, freqNodes.getLength(), "Each URL must have exactly one changefreq element");
		}
	}
}



