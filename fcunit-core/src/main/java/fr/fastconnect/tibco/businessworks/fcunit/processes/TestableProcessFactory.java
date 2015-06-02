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

import fr.fastconnect.tibco.businessworks.fcunit.resources.BWProcess;
import fr.fastconnect.tibco.businessworks.fcunit.resources.Resources;

public class TestableProcessFactory implements AbstractBWResourceFactory<BWProcess>, Serializable {

	private static final long serialVersionUID = 4809068280351869676L;

	/**
	 * 
	 * @return true si le process dont le chemin est "path" commence par un starter (JMS, RV...), false sinon
	 */
	private boolean processHasAStarter(File file) {
		String expression = "/pd:ProcessDefinition/pd:starter";
		return Resources.getNodeFromXPath(file, expression) != null;
	}

	@Override
	public BWProcess createResource(String path) {
		return new BWProcess(path);
	}

	@Override
	public BWProcess[] createResourceArray(int length) {
		return new BWProcess[length-1];
	}

	public class TestableProcessFilter implements FileFilter {
		public boolean accept(File file) {
			return file.isFile() &&
				   file.getName().toLowerCase().endsWith(".process") &&
				   !processHasAStarter(file);
		}
	}

	@Override
	public FileFilter getFilter() {
		return new TestableProcessFilter();
	}

	@Override
	public FileFilter getTreeFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() ||
					   file.isDirectory() &&
					   !file.getName().toLowerCase().endsWith("TestSuite".toLowerCase());
			}
		};
	}

}
