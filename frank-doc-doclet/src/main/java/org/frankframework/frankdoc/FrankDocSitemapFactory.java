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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.json.JsonObject;

public class FrankDocSitemapFactory {
	private static final String DEFAULT_BASE_URL = "https://frankdoc.frankframework.org";
	private static final String PRIORITY = "1.0";
	private static final String CHANGE_FREQUENCY = "weekly";
	private static final List<String> STATIC_ROUTES = List.of(
		"/",
		"/#/search",
		"/#/components",
		"/#/properties",
		"/#/credential-providers",
		"/#/servlet-authenticators"
	);

	private final JsonObject json;
	private final String baseUrl;

	public FrankDocSitemapFactory(JsonObject json) {
		this(json, DEFAULT_BASE_URL);
	}

	public FrankDocSitemapFactory(JsonObject json, String baseUrl) {
		this.json = json;
		this.baseUrl = baseUrl == null || baseUrl.isBlank() ? DEFAULT_BASE_URL : baseUrl;
	}

	public String getXml() {
		Set<String> urls = new LinkedHashSet<>();
		STATIC_ROUTES.stream().map(this::withBase).forEach(urls::add);
		addJsonUrls(urls);
		String body = urls.stream()
			.map(this::buildSitemapUrl)
			.collect(java.util.stream.Collectors.joining("\n  "));
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
			+ "  " + body + "\n"
			+ "</urlset>\n";
	}

	private void addJsonUrls(Set<String> urls) {
		if (json == null)  return;

		JsonObject elements = json.getJsonObject("elements");
		if (elements != null) {
			for (String elementName : new TreeSet<>(elements.keySet())) {
				if (elementName.contains(".")) {
					urls.add(withBase("/#/" + encodePathValue(elementName)));
				}
			}
		}

		JsonObject credentialProviders = json.getJsonObject("credentialProviders");
		if (credentialProviders != null) {
			for (String name : new TreeSet<>(credentialProviders.keySet())) {
				urls.add(withBase("/#/credential-providers/" + encodePathValue(name)));
			}
		}

		JsonObject servletAuthenticators = json.getJsonObject("servletAuthenticators");
		if (servletAuthenticators != null) {
			for (String name : new TreeSet<>(servletAuthenticators.keySet())) {
				urls.add(withBase("/#/servlet-authenticators/" + encodePathValue(name)));
			}
		}
	}

	private String encodePathValue(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8)
			.replace("+", "%20");
	}

	private String buildSitemapUrl(String loc) {
		return "<url>\n"
			+ "  <loc>" + escapeXml(loc) + "</loc>\n"
			+ "  <changefreq>" + CHANGE_FREQUENCY + "</changefreq>\n"
			+ "  <priority>" + PRIORITY + "</priority>\n"
			+ "</url>";
	}

	private String withBase(String route) {
		return baseUrl + route;
	}

	private String escapeXml(String value) {
		return value
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&apos;");
	}
}
