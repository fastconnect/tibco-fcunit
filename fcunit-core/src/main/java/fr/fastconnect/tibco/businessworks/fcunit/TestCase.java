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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.fastconnect.tibco.businessworks.fcunit.resources.BWProcess;
import fr.fastconnect.tibco.businessworks.fcunit.resources.BWResource;
import fr.fastconnect.tibco.businessworks.fcunit.resources.XMLTest;

/**
 * Une classe qui modélise un TestCase, donc une liste de {@link TestProcess} contenus dans le TestCase.
 * 
 * @author Mathieu Debove
 *
 */
public class TestCase extends BWResource implements Serializable {

	private static final long serialVersionUID = -5129492488357372228L;

	private List<BWProcess> tests = new ArrayList<BWProcess>();

	private List<XMLTest> testsXML = new ArrayList<XMLTest>();

	/**
	 * Création via {@link BWResource}
	 * 
	 * @param path, le chemin du TestCase
	 */
	public TestCase(String path) {
		super(path);
	}

	/**
	 * Cette méthode crée le test corresondant au process passé en paramètre
	 * et l'ajoute à la liste des tests du {@link TestCase}.
	 * 
	 * @param process
	 */
	public void addTestProcess(String process) {
		this.tests.add(new BWProcess(process));
	}

	public void addTestXML(String path) {
		try {
			this.testsXML.add(new XMLTest(path));
		} catch (Exception e) {
			System.err.println(path + " was not added.");
		}
	}

	public BWProcess[] getTests() {
		return ((BWProcess[]) this.tests.toArray(new BWProcess[0]));
	}

	public XMLTest[] getTestsXML() {
		return ((XMLTest[]) this.testsXML.toArray(new XMLTest[0]));
	}

}
