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
import java.util.stream.Collectors;

import jakarta.json.JsonObject;

public class FrankDocSitemapFactory {
	private static final String DEFAULT_BASE_URL = "https://frankdoc.frankframework.org";
	private static final String HIGHER_PRIORITY = "0.8";
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
		Set<String> urls = new LinkedHashSet<>(STATIC_ROUTES);
		addJsonUrls(urls);
		String body = urls.stream()
			.map(url -> buildSitemapUrl(url, STATIC_ROUTES.contains(url)))
			.collect(Collectors.joining("\n  "));
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
					urls.add("/#/" + encodePathValue(elementName));
				}
			}
		}

		JsonObject credentialProviders = json.getJsonObject("credentialProviders");
		if (credentialProviders != null) {
			for (String name : new TreeSet<>(credentialProviders.keySet())) {
				urls.add("/#/credential-providers/" + encodePathValue(name));
			}
		}

		JsonObject servletAuthenticators = json.getJsonObject("servletAuthenticators");
		if (servletAuthenticators != null) {
			for (String name : new TreeSet<>(servletAuthenticators.keySet())) {
				urls.add("/#/servlet-authenticators/" + encodePathValue(name));
			}
		}
	}

	private String encodePathValue(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8)
			.replace("+", "%20");
	}

	private String buildSitemapUrl(String loc, boolean higherPriority) {
		return "<url>"
			+ "\n    <loc>" + withBase(loc) + "</loc>"
			+ "\n    <changefreq>" + CHANGE_FREQUENCY + "</changefreq>"
			+ (higherPriority ? "\n    <priority>" + HIGHER_PRIORITY + "</priority>" : "")
			+ "\n  </url>";
	}

	private String withBase(String route) {
		return baseUrl + escapeXml(route);
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
