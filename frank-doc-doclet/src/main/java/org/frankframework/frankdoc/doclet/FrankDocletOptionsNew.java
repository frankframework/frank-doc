/*
Copyright 2021, 2022 WeAreFrank!

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

package org.frankframework.frankdoc.doclet;

import jdk.javadoc.doclet.Doclet;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Getter
@Log4j2
public class FrankDocletOptionsNew {
	private static final boolean OK = true;
	private static final boolean FAILED = false;
	public static final String STRING = "<string>";

	private String outputDirectory;
	private String xsdStrictPath = "xml/xsd/FrankConfig.xsd";
	private String xsdCompatibilityPath = "xml/xsd/FrankConfig-compatibility.xsd";
	private String jsonOutputPath = "js/frankdoc.json";
	private String elementSummaryPath = "txt/elementSummary.txt";
	private URL digesterRulesUrl;
	private String rootClass = "nl.nn.adapterframework.configuration.Configuration";
	private String frankFrameworkVersion;

	private final @Getter Set<Option> options = Set.of(
		new Option("-rootClass", true, "rootClass desc", STRING) {
			@Override
			public boolean process(String option, List<String> arguments) {
				rootClass = arguments.get(0);
				return OK;
			}
		},

		new Option("-outputDirectory", true, "outputDirectory", STRING) {
			@Override
			public boolean process(String option, List<String> arguments) {
				outputDirectory = arguments.get(0);
				return OK;
			}
		},

		new Option("-frankFrameworkVersion", true, "frankFrameworkVersion", STRING) {
			@Override
			public boolean process(String option, List<String> arguments) {
				frankFrameworkVersion = arguments.get(0);
				return OK;
			}
		},
		new Option("-digesterRulesPath", true, "digesterRulesPath", STRING) {
			@Override
			public boolean process(String option, List<String> arguments) {
				try {
					digesterRulesUrl = createDigesterRulesURL(arguments.get(0));
					return OK;
				} catch (FrankDocException e) {
					log.error("Error: {}", e.getMessage());
					return FAILED;
				}
			}
		});

	private URL createDigesterRulesURL(String value) throws FrankDocException {
		try {
			File f = new File(value);
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new FrankDocException(String.format("Invalid path to digester rules file: [%s]", value), e);
		}
	}

	/**
	 * A base class for declaring options.
	 * Subtypes for specific options should implement
	 * the {@link #process(String, List) process} method
	 * to handle instances of the option found on the
	 * command line.
	 */
	abstract static class Option implements Doclet.Option {
		private final String name;
		private final boolean hasArg;
		private final String description;
		private final String parameters;

		Option(String name, boolean hasArg, String description, String parameters) {
			this.name = name;
			this.hasArg = hasArg;
			this.description = description;
			this.parameters = parameters;
		}

		@Override
		public int getArgumentCount() {
			return hasArg ? 1 : 0;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public Kind getKind() {
			return Kind.STANDARD;
		}

		@Override
		public List<String> getNames() {
			return List.of(name);
		}

		@Override
		public String getParameters() {
			return hasArg ? parameters : "";
		}
	}

}
