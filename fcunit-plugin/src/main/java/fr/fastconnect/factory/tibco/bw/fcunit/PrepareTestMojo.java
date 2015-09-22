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
package fr.fastconnect.factory.tibco.bw.fcunit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.ZipScanner;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.FileSelector;

/**
 * <p>
 * This goal will prepare the target project for the Code Review.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo(name = "prepare-test", defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class PrepareTestMojo extends TestNoForkMojo {

	@Override
	public void execute() throws MojoExecutionException {
		if (skip()) {
			getLog().info(SKIPPING);
			return;
		}

//		super.execute(); // don't call or it will call the Service

		try {
			cleanStarters();
		} catch (ZipException e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		} catch (IOException e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		}
	}

	private void cleanStarters() throws ZipException, IOException {
		getLog().info("Cleaning the starters from " + testSrcDirectory);

		List<String> ignore = new ArrayList<String>();
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueEventSource</pd:type>");
		ignore.add("<pd:type>com.tibco.pe.core.OnStartupEventSource</pd:type>");
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueSendActivity</pd:type>");
		ignore.add("<pd:type>com.tibco.plugin.jms.JMSQueueRequestReplyActivity</pd:type>");

		for (String i : ignore) {
			removeFileContaining(i);
		}

		// look inside Projlibs too !
		if (testLibDirectory.exists()) {
			DirectoryScanner ds = new DirectoryScanner();
			String[] includes = {"**/*.projlib"};
			ds.setIncludes(includes);
			String[] excludes = {"**/fcunit*.projlib"};
			ds.setExcludes(excludes);
			ds.setBasedir(testLibDirectory);
			//ds.setCaseSensitive(true);
			ds.scan();

			String[] files = ds.getIncludedFiles();
			List<String> projlibsFiles = Arrays.asList(files);
			for (String projlibFile : projlibsFiles) {
				removeFileInZipContaining(ignore, new File(testLibDirectory + File.separator + projlibFile));
			}
		}
	}

	private boolean containsString(String string, InputStream fileInZip) throws IOException {
		boolean result = false;
		BufferedInputStream lzf = new BufferedInputStream(fileInZip);
		FindStringInInputStream ris = new FindStringInInputStream(lzf);

		result = ris.containsString(string);

		ris.close();
		lzf.close();

		return result;
	}

	class FindStringInInputStream extends FilterInputStream {
	    LinkedList<Integer> inQueue;
	    LinkedList<Integer> outQueue;
	    private byte[] search;
		private boolean found;

	    protected FindStringInInputStream(InputStream in) {
	        super(in);
	    }

	    private void init() throws IOException {
			inQueue = new LinkedList<Integer>();
		    outQueue = new LinkedList<Integer>();
	    }

	    private boolean isMatchFound() {
	        Iterator<Integer> inIter = inQueue.iterator();
	        for (int i = 0; i < search.length; i++)
	            if (!inIter.hasNext() || search[i] != inIter.next())
	                return false;
	        return true;
	    }

	    private void readAhead() throws IOException {
	        // Work up some look-ahead.
	        while (inQueue.size() < search.length) {
	            int next = super.in.read();
	            inQueue.offer(next);
	            if (next == -1)
	                break;
	        }
	    }

	    @Override
	    public int read() throws IOException {
			//this.in.read();
	        // Next byte already determined.
	        if (outQueue.isEmpty()) {

	            readAhead();

	            if (isMatchFound()) {
					found = true;
					return -1;
	            } else
	                outQueue.add(inQueue.remove());
	        }

	        return outQueue.remove();
	    }

	    public boolean containsString(String str) throws IOException {
			search = str.getBytes("UTF-8");
			found = false;
			init();

			while (this.read() != -1) {}

			return found;
	    }
	    // TODO: Override the other read methods.
	}

	private void removeFileContaining(String content) {
		DirectoryScanner ds = new DirectoryScanner();
		String[] includes = {"**/*.process"};
		ds.setIncludes(includes);
		ds.setBasedir(testSrcDirectory);
		//ds.setCaseSensitive(true);

		ContainsSelector contentToRemove = new ContainsSelector();
		contentToRemove.setText(content);

		FileSelector[] selectors = {contentToRemove};
		ds.setSelectors(selectors);
		ds.scan();

		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++) {
			String file = testSrcDirectory + File.separator + files[i];
			getLog().debug("Deleting file with starter : '" + file + "'");
			getLog().info("Deleting file with starter : '" + file + "'");
			FileUtils.deleteQuietly(new File(file));
		}

	}

	private void removeFileInZipContaining(List<String> contentFilter, File zipFile) throws ZipException, IOException {
		ZipScanner zs = new ZipScanner();
		zs.setSrc(zipFile);
		String[] includes = {"**/*.process"};
		zs.setIncludes(includes);
		//zs.setCaseSensitive(true);
		zs.init();
		zs.scan();

		File originalProjlib = zipFile; // to be overwritten
		File tmpProjlib = new File(zipFile.getAbsolutePath() + ".tmp"); // to read
		FileUtils.copyFile(originalProjlib, tmpProjlib);

		ZipFile listZipFile = new ZipFile(tmpProjlib);
		ZipInputStream readZipFile = new ZipInputStream(new FileInputStream(tmpProjlib));
		ZipOutputStream writeZipFile = new ZipOutputStream(new FileOutputStream(originalProjlib));

		ZipEntry zipEntry;
		boolean keep;
		while ((zipEntry = readZipFile.getNextEntry()) != null) {
			keep = true;
			for (String filter : contentFilter) {
				keep = keep && !containsString(filter, listZipFile.getInputStream(zipEntry));
			}
//			if (!containsString("<pd:type>com.tibco.pe.core.OnStartupEventSource</pd:type>", listZipFile.getInputStream(zipEntry))
//			 && !containsString("<pd:type>com.tibco.plugin.jms.JMSTopicEventSource</pd:type>", listZipFile.getInputStream(zipEntry))) {
			if (keep) {
				writeZipFile.putNextEntry(zipEntry);
		        int len = 0;
		        byte[] buf = new byte[1024];
		        while ((len = readZipFile.read(buf)) >= 0) {
					writeZipFile.write(buf, 0, len);
		        }
		        writeZipFile.closeEntry();
		        //getLog().info("written");
			} else {
				getLog().info("removed " + zipEntry.getName());
			}

		}

		writeZipFile.close();
		readZipFile.close();
		listZipFile.close();

		originalProjlib.setLastModified(originalProjlib.lastModified()-100000);
	}

}
