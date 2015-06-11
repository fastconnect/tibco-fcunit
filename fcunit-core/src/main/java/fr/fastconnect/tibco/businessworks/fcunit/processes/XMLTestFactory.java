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
package fr.fastconnect.tibco.businessworks.fcunit.processes;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

import fr.fastconnect.tibco.businessworks.fcunit.resources.XMLTest;

public class XMLTestFactory implements AbstractBWResourceFactory<XMLTest>, Serializable {

	private static final long serialVersionUID = 3016084206913487246L;

	@Override
	public XMLTest createResource(String path) {
		try {
			return new XMLTest(path);
		} catch (Exception e) {
			System.err.println(path + " was not created");
		}
		return null;
	}

	@Override
	public XMLTest[] createResourceArray(int length) {
		return new XMLTest[length-1];
	}

	@Override
	public FileFilter getFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() &&
					   file.getParentFile() != null &&
					   file.getParentFile().getName().toLowerCase().endsWith("TestCase".toLowerCase()) &&
					   file.getParentFile().getParentFile() != null &&
					   file.getParentFile().getParentFile().getName().toLowerCase().endsWith("TestSuite".toLowerCase()) &&
					   file.getName().toLowerCase().endsWith(".tasklist");
			}
		};
	}

	@Override
	public FileFilter getTreeFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() ||
					   file.isDirectory();
			}
		};
	}

}
