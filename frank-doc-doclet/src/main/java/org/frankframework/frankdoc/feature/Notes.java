/*
Copyright 2024 WeAreFrank!

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

package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.model.Note;
import org.frankframework.frankdoc.model.NoteType;
import org.frankframework.frankdoc.wrapper.FrankClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Notes {

	public static final String JAVADOC_INFO_NOTE_TAG = "@ff.info";
	public static final String JAVADOC_TIP_NOTE_TAG = "@ff.tip";
	public static final String JAVADOC_WARNING_NOTE_TAG = "@ff.warning";
	public static final String JAVADOC_DANGER_NOTE_TAG = "@ff.danger";

	private static final Notes INSTANCE = new Notes();

	private Notes() {}

	public static Notes getInstance() {
		return INSTANCE;
	}

	public List<Note> valueOf(FrankClass clazz) {
		ArrayList<Note> notes = new ArrayList<>(Stream.of(
				parseNoteJavadocTags(clazz, JAVADOC_INFO_NOTE_TAG, NoteType.INFO),
				parseNoteJavadocTags(clazz, JAVADOC_TIP_NOTE_TAG, NoteType.TIP),
				parseNoteJavadocTags(clazz, JAVADOC_WARNING_NOTE_TAG, NoteType.WARNING),
				parseNoteJavadocTags(clazz, JAVADOC_DANGER_NOTE_TAG, NoteType.DANGER)
			).flatMap(Collection::stream)
			.toList());

		String javaDoc = clazz.getJavaDoc();

		if (javaDoc != null && javaDoc.contains(Description.INHERIT_DOC_TAG)) {
			FrankClass superClazz = clazz.getSuperclass();
			if (superClazz != null) {
				notes.addAll(valueOf(superClazz));
			}
		}

		return notes;
	}

	private List<Note> parseNoteJavadocTags(FrankClass clazz, String tagName, NoteType type) {
		return clazz.getAllJavaDocTagsOf(tagName).stream()
			.map(value -> new Note(type, Utils.substituteJavadocTags(value, clazz)))
			.toList();
	}

}
