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
import java.io.FileNotFoundException;

import fr.fastconnect.tibco.businessworks.fcunit.ProjectBaseDir;
import fr.fastconnect.tibco.businessworks.fcunit.TestCase;
import fr.fastconnect.tibco.businessworks.fcunit.TestSuite;

/**
 * Une classe d'abstraction pour tous les éléments des tests (TestSuite, TestCase, Test)
 * qui ont en commun le fait d'avoir un "path".
 * Le "path" correspond au chemin, relatif au répertoire racine du projet, du répertoire
 * (cas des {@link TestSuite} et {@link TestCase}) ou du fichier (cas d'un {@link TestProcess})
 * représentant la ressource.
 * 
 * @author Mathieu Debove
 *
 */
public class BWResource {

	private String path;

	public BWResource(File file) throws FileNotFoundException {
		if (file == null || !file.exists()) {
			throw new FileNotFoundException();
		}
		String basedir = ProjectBaseDir.getProjectBaseDir();
		
		String relative = "/" + new File(basedir).toURI().relativize(file.toURI()).getPath();

		this.path = relative;
	}

	public BWResource(String path) {
		this.path = path;
	}

	/**
	 * Deux objets {@link BWResource} sont égaux s'ils ont le même "path".
	 * 
	 * @param tr, l'objet de type TestResource dont l'égalité doit être testée
	 * @return
	 */
	public boolean equals(BWResource tr) {
		return this.path.equals(tr.path);
	}
	
	/**
	 * Le nom d'une {@link BWResource} est la partie du "path" située après le dernier '/'
	 * à laquelle on enlève le suffixe ".process" dans le cas d'un {@link TestProcess}.
	 * 
	 * @return le nom de la ressource
	 */
	public String getName() {
		String basename = this.path.substring(this.path.lastIndexOf('/') + 1);
		
		if (basename.endsWith(".process")) {
			basename = basename.substring(0, basename.lastIndexOf('.'));
		}
		if (basename.endsWith(".tasklist")) {
			basename = basename.substring(0, basename.lastIndexOf('.'));
		}
		
		return basename;
	}

	/**
	 * 
	 * @return le "path" complet de la ressource
	 */
	public String getPath() {
		return path;
	}

	protected File getFile() {
		return new File(ProjectBaseDir.getProjectBaseDir(), this.path);
	}
}
