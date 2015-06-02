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
package fr.fastconnect.tibco.businessworks.fcunit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.fastconnect.tibco.businessworks.fcunit.xml.FCDiff;

public class FCDiffTest {

	private String getExpected() throws IOException {
		return FileUtils.readFileToString(new File("src/test/resources/expected.xml"));

	}

	private String getActual() throws IOException {
		return FileUtils.readFileToString(new File("src/test/resources/actual.xml"));
	}

	public void testPathsAreEqual() throws Exception {
		FCDiff diff = new FCDiff(getExpected(), getActual());

		assertTrue(diff.pathsAreEqual("//FlowData[1]/text()[2]", "/ESBMsg[1]/FlowData[1]/text()[2]"));

		assertFalse(diff.pathsAreEqual("n'importe", "quoi"));
	}

	@Test
	public void formatDocument_test() throws SAXException, IOException {
		String actual = "<?xml version = \"1.0\" encoding = \"UTF-8\"?><dimensions>    <pyramide>        <aire_base>9</aire_base>        <volume>15</volume>    </pyramide></dimensions>";
		String expected = "<?xml version = \"1.0\" encoding = \"UTF-8\"?><dimensions>	<pyramide>		<aire_base>9</aire_base>		<volume>15</volume>	</pyramide></dimensions>";
		FCDiff diff = new FCDiff(expected, actual);
		Document doc = diff.getDocumentFromString(actual);
		Document docExpected = diff.getDocumentFromString(expected);

		String format = diff.formatDocumentForTesting(doc);
		String formatExpected = diff.formatDocumentForTesting(docExpected);
		Assert.assertTrue(format.equals(formatExpected));
	}

	@Test
	public void testIsPathIgnored() throws Exception {
		FCDiff diff = new FCDiff(getExpected(), getActual());

		ArrayList<String> ignoredXPaths = new ArrayList<String>();
		ignoredXPaths.add("/ESBMsg[1]/FlowData[1]/Pivot[1]/commande[1]/entete[1]/idCommandeBO[1]/text()[1]");

		diff.setIgnoredXPaths(ignoredXPaths);
		assertTrue(diff.isPathIgnored("/ESBMsg[1]/FlowData[1]/Pivot[1]/commande[1]/entete[1]/idCommandeBO[1]/text()[1]"));
	}

}
