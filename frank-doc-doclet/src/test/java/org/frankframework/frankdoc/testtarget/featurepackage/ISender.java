package org.frankframework.frankdoc.testtarget.featurepackage;

/*
   Copyright 2013 Nationale-Nederlanden, 2020, 2022 WeAreFrank!

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
import org.frankframework.doc.FrankDocGroup;
import org.frankframework.doc.FrankDocGroupValue;

/**
 * The <code>ISender</code> is responsible for sending a message to
 * some destination.
 */
@FrankDocGroup(FrankDocGroupValue.SENDER)
public interface ISender {

	public void configure();

	public void open();

	public void close();

	default boolean isSynchronous() {
		return true;
	}

	public String sendMessage(String message);

	default boolean consumesSessionVariable(String sessionKey) {
		return false;
	}
}

