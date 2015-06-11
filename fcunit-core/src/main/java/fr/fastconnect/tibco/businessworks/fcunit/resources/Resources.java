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
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.fastconnect.tibco.businessworks.fcunit.ProjectBaseDir;
import fr.fastconnect.tibco.businessworks.fcunit.TestSuite;
import fr.fastconnect.tibco.businessworks.fcunit.processes.AbstractBWResourceFactory;
import fr.fastconnect.tibco.businessworks.fcunit.processes.TestProcessFactory;
import fr.fastconnect.tibco.businessworks.fcunit.processes.TestableProcessFactory;
import fr.fastconnect.tibco.businessworks.fcunit.processes.TestedProcessFactory;
import fr.fastconnect.tibco.businessworks.fcunit.processes.XMLTestFactory;

public class Resources<T extends BWResource> implements Serializable {

	private static final long serialVersionUID = 8599266875650814943L;

	private static File projectBaseDir;
	private AbstractBWResourceFactory<T> resourceFactory;
	private FileFilter resourceFilter; // the resource to keep in the end
	private FileFilter resourceTreeFilter; // which children to traverse to find resources

	public Resources(AbstractBWResourceFactory<T> resourceFactory) {
		this.resourceFactory = resourceFactory;
		this.resourceFilter = this.resourceFactory.getFilter();
		this.resourceTreeFilter = this.resourceFactory.getTreeFilter();
		projectBaseDir = new File(ProjectBaseDir.getProjectBaseDir());
	}

	/**
	 * 
	 * @return le noeud dont le chemin est xPathExpression
	 */
	public static Node getNodeFromXPath(File file, String xPathExpression) {
		if (file != null && !file.exists() || file.isDirectory()) {
			return null;
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		dbFactory.setValidating(false);
	    
		DocumentBuilder dBuilder;
		Document document;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(file);
		} catch (ParserConfigurationException e2) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		NamespaceContext namespaces = new NamespaceContext() {
			
			public Iterator<?> getPrefixes(String namespaceURI) {
				return null;
			}
			
			public String getPrefix(String namespaceURI) {
				if ("http://xmlns.tibco.com/bw/process/2003".equals(namespaceURI)) {
					return "pd";
				} else if ("http://www.tibco.com/xmlns/repo/types/2002".equals(namespaceURI)) {
					return "Repository";
				} else {
					return null;
				}
			}
			
			public String getNamespaceURI(String prefix) {
				if ("pd".equals(prefix)) {
					return "http://xmlns.tibco.com/bw/process/2003";
				} else if ("Repository".equals(prefix)) {
					return "http://www.tibco.com/xmlns/repo/types/2002";
				} else {
					return null;
				}
			}
		};

		xpath.setNamespaceContext(namespaces);
		String expression = xPathExpression;
		try {
			Node widgetNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
			return widgetNode;
		} catch (XPathExpressionException e) {
			e.printStackTrace(); // FIXME : remove printStackTrace()
		}

		return null;
	}

	private HashSet<String> getResources(File file) {
		HashSet<String> result = new HashSet<String>();

		if (file == null) {
			return result;
		}

		// recursion on sub-folders
		if (file.isDirectory()) {
			File[] children = file.listFiles(resourceTreeFilter);
			for (File f : children) {
				result.addAll(getResources(f));
			}
		}
		// add the file or not based on the filter
		if (resourceFilter.accept(file)) {
			result.add(
				file.getAbsolutePath()
				.substring(projectBaseDir.getAbsolutePath().length())
				.replaceAll("\\\\", "/")
			);
		}
		return result;
	}

	public T[] getResources() {
		HashSet<String> resources = getResources(projectBaseDir);

		List<T> result = new ArrayList<T>();
		for (String resource : resources) {
			result.add(resourceFactory.createResource(resource));
		}

		return result.toArray(resourceFactory.createResourceArray(1));
	}

	public static BWProcess[] getTestableProcesses() {
		return new Resources<BWProcess>(new TestableProcessFactory()).getResources();
	}

	public static BWProcess[] getTestProcesses() {
		return new Resources<BWProcess>(new TestProcessFactory()).getResources();
	}

	public static XMLTest[] getXMLTests() {
		return new Resources<XMLTest>(new XMLTestFactory()).getResources();
	}

	public static BWResource[] getAllTests() {
//		return mergeResources(getTestProcesses(), getXMLTests());
		return mergeResources(getXMLTests(), getTestProcesses());
	}

	public static BWProcess[] getTestedProcesses() {
		return new Resources<BWProcess>(new TestedProcessFactory()).getResources();
	}

	public static int getTestableProcessesCount() {
		return getTestableProcesses().length;
	}
	
	public static int getTestProcessesCount() {
		return getTestProcesses().length;
	}

	public static int getXMLTestsCount() {
		return getXMLTests().length;
	}

	public static int getAllTestsCount() {
		return getAllTests().length;
	}

	public static int getTestedProcessesCount() {
		return getTestedProcesses().length;
	}

	public static BWResource[] mergeResources(BWResource[] r1, BWResource[] r2) {
		Collection<BWResource> collection = new ArrayList<BWResource>();
		collection.addAll(Arrays.asList(r1));
		collection.addAll(Arrays.asList(r2));

		return collection.toArray(new BWResource[] {});
	}

	public static void main(String... args) {
		ProjectBaseDir.projectBaseDir = "C:/tibco_dev/tec/fwk/trunk/bw/Utils/TEC-FILE/TEC-FILE";

		BWResource[] result;
		
		System.out.println("Testable processes");
		result = getTestableProcesses();
		for (BWResource resource : result) {
			System.out.println(resource.getPath());
		}

		System.out.println("Test processes");
		result = getTestProcesses();
		for (BWResource resource : result) {
			System.out.println(resource.getPath());
		}

		System.out.println("XML tests");
		result = getXMLTests();
		for (BWResource resource : result) {
			System.out.println(resource.getPath());
			System.out.println(((XMLTest) resource).getTestedProcessPath());
		}

		System.out.println("All Tests");
		result = getAllTests();
		for (BWResource resource : result) {
			System.out.println(resource.getPath());
		}
		
		TestSuite testSuite = TestSuite.createTreeFromList(result);
		testSuite.toString();
		
		System.out.println("Tested processes");
		result = getTestedProcesses();
		for (BWResource resource : result) {
			System.out.println(resource.getPath());
		}
	}

}
