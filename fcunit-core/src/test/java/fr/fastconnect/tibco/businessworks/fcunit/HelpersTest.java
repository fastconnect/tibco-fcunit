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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class HelpersTest {

	@Test
	public void testMatchCase() {
		assertTrue(Helpers.matchCase("MonTestCase"));
		assertTrue(Helpers.matchCase("TestCase"));
		assertFalse(Helpers.matchCase("MontestCase")); // t, not T
		assertFalse(Helpers.matchCase("MonTestcase")); // c, not C
		assertFalse(Helpers.matchCase("MonTEstCase")); // E, not e
		assertFalse(Helpers.matchCase("UnTestCaseOuPas")); // Not end of string
		assertFalse(Helpers.matchCase("UneTestSuite")); // Suite, not Case
	}

	@Test
	public void testMatchSuite() {
		assertTrue(Helpers.matchSuite("UneTestSuite"));
		assertTrue(Helpers.matchSuite("TestSuite"));
		assertFalse(Helpers.matchSuite("MatestSuite")); // t, not T
		assertFalse(Helpers.matchSuite("MaTestsuite")); // c, not C
		assertFalse(Helpers.matchSuite("MaTEstSuite")); // E, not e
		assertFalse(Helpers.matchSuite("UneTestSuiteOuPas")); // Not end of string
		assertFalse(Helpers.matchSuite("UnTestCase")); // Case, not Suite
	}

}
