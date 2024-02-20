package org.frankframework.frankdoc.testdoclet;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Log4j2
public class EasyDoclet implements Doclet {
	@Getter
	private static DocTrees docTrees;
	@Getter
	private static Set<? extends Element> includedElements;
	@Getter @Setter
	private static String[] packages;

	@Override
	public void init(Locale locale, Reporter reporter) {
		log.debug("EasyDoclet.init start");
	}

	@Override
	public String getName() {
		return "EasyDoclet";
	}

	@Override
	public Set<? extends Option> getSupportedOptions() {
		return new HashSet<>();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_11;
	}

	@Override
	public boolean run(DocletEnvironment environment) {
		log.debug("EasyDoclet.run start");
		docTrees = environment.getDocTrees();
		includedElements = environment.getIncludedElements();
		log.debug("EasyDoclet.run: includedElements size = {}", includedElements.size());
		return true;
	}
}
