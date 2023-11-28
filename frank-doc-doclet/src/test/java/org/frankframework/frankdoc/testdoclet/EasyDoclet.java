package org.frankframework.frankdoc.testdoclet;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.internal.tool.AccessKind;
import jdk.javadoc.internal.tool.JavadocTool;
import jdk.javadoc.internal.tool.Messager;
import jdk.javadoc.internal.tool.ToolOption;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.tools.StandardLocation;
import java.io.File;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
@Getter
public class EasyDoclet {
	final private File sourceDirectory;
	final private String[] packageNames;
	final private DocletEnvironment docletEnvironment;

	private static final AccessKind ACCESS_KIND = AccessKind.PUBLIC;

	public EasyDoclet(File sourceDirectory, String[] packageNames) {
		this.sourceDirectory = sourceDirectory;
		this.packageNames = packageNames;

		String sourcePath = getSourceDirectory().getAbsolutePath();

		if (getSourceDirectory().exists()) {
			log.info("Using source path: [{}]", sourcePath);
//			compOpts.put("-sourcepath", getSourceDirectory().getAbsolutePath());
		} else {
			log.error("Ignoring non-existing source path, check your source directory argument. Used: [{}]", sourcePath);
		}

		try {
			docletEnvironment = createDocletEnv(sourcePath, List.of(packageNames));
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	private static DocletEnvironment createDocletEnv(String sourcePath, Collection<String> packageNames) throws Exception {

		// Create a context to hold settings for Javadoc.
		Context context = new Context();

		// Pre-register a messager for the context. Not needed for Java 17!
		Messager.preRegister(context, EasyDoclet.class.getName());

		// Set source path option for Javadoc.
		try (JavacFileManager fileManager = new JavacFileManager(context, true, UTF_8)) {

			fileManager.setLocation(StandardLocation.SOURCE_PATH, List.of(new File(sourcePath)));

//			JavadocLog.preRegister(context, "javadoc"); // Java 17

			// Create an instance of Javadoc.
			JavadocTool javadocTool = JavadocTool.make0(context);

			// Set up javadoc tool options. Not needed for Java 17!
//			ToolOption options = new ToolOption(context, log, null); // Java 17 ??
			Map<ToolOption, Object> options = new EnumMap<>(ToolOption.class);
			options.put(ToolOption.SHOW_PACKAGES, ACCESS_KIND);
			options.put(ToolOption.SHOW_TYPES, ACCESS_KIND);
			options.put(ToolOption.SHOW_MEMBERS, ACCESS_KIND);
			options.put(ToolOption.SHOW_MODULE_CONTENTS, ACCESS_KIND);
			options.put(ToolOption.SUBPACKAGES, packageNames);

			// Invoke Javadoc and ask it for a DocletEnvironment containing the specified packages.
			return javadocTool.getEnvironment(
				options, // options
				List.of(), // java names
				List.of()); // java files
		}
	}

}
