<?xml version="1.0" encoding="UTF-8"?>
<!--

    (C) Copyright 2011-2015 FastConnect SAS
    (http://www.fastconnect.fr/) and others.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet exclude-result-prefixes="ns_0 xsl"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns="http://fastconnect.fr/fcunit.xsd"
xmlns:ns_0 = "www.tibco.com/plugin/java/xmlSchema/fr.fastconnect.tibco.businessworks.fcunit.TestSuite"
  version="2.0">
	<xsl:output omit-xml-declaration="no"/>
	<xsl:template match="/ns_0:TestSuite">
		<test-suites>
			<xsl:apply-templates/>
		</test-suites>
	</xsl:template>

	<xsl:template match="ns_0:Name|ns_0:Path">
	</xsl:template>

	<xsl:template match="ns_0:Suites">
		<test-suite name="{ns_0:Name}" path="{ns_0:Path}">
			<xsl:apply-templates select="ns_0:Suites"/>
			<xsl:apply-templates select="ns_0:Cases"/>
		</test-suite>
	</xsl:template>

	<xsl:template match="ns_0:Cases">
		<test-case name="{ns_0:Name}" path="{ns_0:Path}">
			<xsl:apply-templates select="ns_0:Tests"/>
			<xsl:apply-templates select="ns_0:TestsXML"/>
		</test-case>
	</xsl:template>

	<xsl:template match="ns_0:Tests">
		<test name="{ns_0:Name}" path="{ns_0:Path}">
		</test>
	</xsl:template>

	<xsl:template match="ns_0:TestsXML">
		<test-xml name="{ns_0:Name}" path="{ns_0:Path}">
			<input-xml-filename><xsl:value-of select="ns_0:InputXMLFile" /></input-xml-filename>
			<expected-xml-filename><xsl:value-of select="ns_0:ExpectedXMLFile" /></expected-xml-filename>
			<tested-process-path><xsl:value-of select="ns_0:TestedProcessPath" /></tested-process-path>
			<ignored-xpaths></ignored-xpaths>
			<accept-more-fields-in-actual>false</accept-more-fields-in-actual>
		</test-xml>
	</xsl:template>

</xsl:stylesheet>