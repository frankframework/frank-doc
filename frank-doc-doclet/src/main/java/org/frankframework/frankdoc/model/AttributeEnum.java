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

package org.frankframework.frankdoc.model;

import java.util.List;

import lombok.Getter;

public class AttributeEnum {
	private @Getter String fullName;
	private String simpleName;
	private final @Getter List<EnumValue> values;
	private int seq;

	AttributeEnum(String fullName, String simpleName, List<EnumValue> values, int seq) {
		this.fullName = fullName;
		this.values = values;
		this.simpleName = simpleName;
		this.seq = seq;
	}

	public String getUniqueName(String groupWord) {
		if(seq == 1) {
			return String.format("%s%s", simpleName, groupWord);
		} else {
			return String.format("%s%s_%d", simpleName, groupWord, seq);
		}
	}
}
