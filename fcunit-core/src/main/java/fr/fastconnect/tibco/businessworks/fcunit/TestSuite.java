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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fastconnect.tibco.businessworks.fcunit.resources.BWResource;

/**
 * 
 * @author Mathieu Debove
 *
 */
public class TestSuite extends BWResource implements Serializable {

	private static final long serialVersionUID = 2914984144832167792L;

	private Set<TestSuite> suites = new HashSet<TestSuite>();
	private Set<TestCase> cases = new HashSet<TestCase>();
    
    public TestSuite(String path) {
		super(path);
    }

	public TestCase getCase(String path) {
		for (TestCase c : cases) {
			if (c.getPath().equals(path)) {
				return c;
			}
		}
		TestCase newCase = new TestCase(path);
		this.cases.add(newCase);
		return newCase;
	}

	public TestSuite getSuite(String path) {
		for (TestSuite s : suites) {
			if (s.getPath().equals(path)) {
				return s;
			}
		}
		TestSuite newSuite = new TestSuite(path);
		this.suites.add(newSuite);
		return newSuite;
	}

	public TestSuite[] getSuites() {
    	return suites.toArray(new TestSuite[0]);
    }

	public TestCase[] getCases() {
    	return cases.toArray(new TestCase[0]);
    }

	public static TestSuite createTreeFromList(BWResource[] result) {
		TestSuite current = new TestSuite("/");
		TestSuite root = current;

		Pattern p = Pattern.compile("(^\\S*TestSuite|[^\\/]*TestSuite|[^\\/]*TestCase|[^\\/]*\\.process)[^\\/]*");
	    for (int i = 0; i < result.length; i++) {
	    	current = root;
			BWResource bwResource = result[i];

			Matcher m = p.matcher(bwResource.getPath());

			String path = "";
			TestCase currentCase = null;
			while (m.find()) {
				String resource = m.group(1);

				if (resource.startsWith("/")) {
					path += resource;				
				} else {
					path += "/" + resource;
				}
				if (Helpers.matchSuite(resource)) {
					current = current.getSuite(path);
				} else if (Helpers.matchCase(resource)) {
					currentCase = current.getCase(path);
				} else if (resource.toLowerCase().endsWith(".process")) {
					if (currentCase != null) {
						currentCase.addProcess(path.substring(1));
					}
				}
				System.out.println(m.group(1));
			}

	    }

		return root;
	}
    
}