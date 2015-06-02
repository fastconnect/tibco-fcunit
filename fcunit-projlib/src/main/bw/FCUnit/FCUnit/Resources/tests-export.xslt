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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:fcun="http://fastconnect.fr/fcunit.xsd" xmlns:ns0="http://fastconnect.fr/fcunit.xsd" exclude-result-prefixes="ns0">
	
	<xsl:output method="xml" indent="yes" />
	
	<xsl:param name="ExportDirectory" /> 
	
	<!-- ce template s'applique à tous les éléments du namespace par défaut (simple recopie) -->
	<xsl:template match="node()|text()">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- ce template s'applique à tous les éléments dont le namespace est "http://fastconnect.fr/fcunit.xsd"
	et leur donne le préfixe "fcun" (les éléments du fichier source ne sont pas forcément préfixés "ns0") -->
	
	<xsl:template match="ns0:*">
		<xsl:element name="fcun:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="/fcun:test-suites-results">
		<xsl:variable name="result_filename" select="concat($ExportDirectory, '/results.xml')" />
		<!--<xsl:result-document href="{$result_filename}">-->
		<fcun:test-suite-results>
		<xsl:for-each select="/fcun:test-suites-results/fcun:test-suite-result">
			<xsl:variable name="test_index" select="position()"/>

			<xsl:variable name="filename" select="concat($ExportDirectory, '/', 'test', $test_index, '.xml')" />
			
			<!--
			<xsl:result-document href="{$filename}">
				<fcun:test-suite-results>
			-->
					<fcun:test-suite-result>
						<xsl:attribute name="name">
							<xsl:value-of select="./@name"/>
						</xsl:attribute>
						<xsl:attribute name="path">
							<xsl:value-of select="./@path"/>
						</xsl:attribute>
						<xsl:attribute name="datetime">
							<xsl:value-of select="./@datetime"/>
						</xsl:attribute>
					<xsl:apply-templates select="./*"/>
					</fcun:test-suite-result>
			<!--
				</fcun:test-suite-results>
			</xsl:result-document>
			-->
			
		</xsl:for-each>
		</fcun:test-suite-results>
		<!--</xsl:result-document>-->

	</xsl:template>

	<xsl:template match="fcun:test-suite-result/ns0:*">
		<xsl:element name="fcun:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="node() | text()"/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
