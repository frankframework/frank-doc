/*
Copyright 2021 WeAreFrank!

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

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.extern.log4j.Log4j2;
import org.frankframework.frankdoc.util.ErrorDetectingAppender;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import java.util.Locale;
import java.util.Set;

@Log4j2
public class DocletBuilder implements jdk.javadoc.doclet.Doclet {
	private final FrankDocletOptions options = new FrankDocletOptions();

	@Override
	public boolean run(DocletEnvironment docEnv) {
		printOptions();

		Set<? extends Element> elements = docEnv.getIncludedElements();
		DocTrees docTrees = docEnv.getDocTrees();

		boolean result = true;
		try {
			new Doclet(docTrees, elements, options).run();
		} catch (RuntimeException e) {
			e.printStackTrace();
			result = false;
		} catch (FrankDocException e) {
			log.error("FrankDocException occurred while running Frank!Doc Doclet", e);
			result = false;
		}
		if (ErrorDetectingAppender.HAVE_ERRORS) {
			log.error("There were log statements with level ERROR or FATAL. Failing");
			result = false;
		}
		return result;
	}

	private void printOptions() {
		log.info("Options used in the Frank!Doc doclet:");
		log.info(" rootClass: [{}]", options.getRootClass());
		log.info(" outputDirectory: [{}]", options.getOutputDirectory());
		log.info(" frankFrameworkVersion: [{}]", options.getFrankFrameworkVersion());
		log.info(" xsdStrictPath: [{}]", options.getXsdStrictPath());
		log.info(" xsdCompatibilityPath: [{}]", options.getXsdCompatibilityPath());
		log.info(" jsonOutputPath: [{}]", options.getJsonOutputFilePath());
		log.info(" elementSummaryPath: [{}]", options.getElementSummaryPath());
		log.info(" digesterRulesUrl: [{}]", options.getDigesterRulesUrl());
	}

	public static SourceVersion SourceVersion() {
		log.trace("Method SourceVersion() called");
		return SourceVersion.RELEASE_17;
	}

	@Override
	public void init(Locale locale, Reporter reporter) {
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public Set<? extends Option> getSupportedOptions() {
		return options.getOptions();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}


}
