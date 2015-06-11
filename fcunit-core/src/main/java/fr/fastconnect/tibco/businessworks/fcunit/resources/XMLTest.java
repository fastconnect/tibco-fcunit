/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.fastconnect.tibco.businessworks.fcunit.resources;

import java.io.File;
import java.io.Serializable;

import org.w3c.dom.Node;

public class XMLTest extends BWResource implements Serializable {

	private static final long serialVersionUID = 6608201235091024313L;

	private final static String testedProcess = "/Repository:repository/tasklist/associatedResource";

	private void checkTest(File file) throws Exception {
		if (file == null || !file.getAbsolutePath().toLowerCase().endsWith(".tasklist")) {
			throw new Exception("An XML test must be a file with '.tasklist' extension");
		}
	}

	public XMLTest(File file) throws Exception {
		super(file);
		checkTest(file);
	}

	public XMLTest(String path) throws Exception {
		super(path);
		checkTest(new File(path));
	}

	public String getTestedProcessPath() {
		Node node = Resources.getNodeFromXPath(this.getFile(), testedProcess);
		String result = null;
		if (node != null) {
			result  = node.getNodeValue();
			result = node.getTextContent();
		}
		return result;
	}

	public String getInputXMLFile() {
		File result = new File(this.getFile().getParent(), "input.xml");
		return result.getAbsolutePath();
	}

	public String getExpectedXMLFile() {
		File result = new File(this.getFile().getParent(), "expected.xml");
		return result.getAbsolutePath();
	}

}
