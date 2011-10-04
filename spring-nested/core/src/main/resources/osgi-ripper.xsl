<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:osgi="http://www.springframework.org/schema/osgi"
	xmlns:beans="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<!-- useful for bash
	$ find . -name \*osgi\*.xml -execdir xsltproc -o references-context.xml /home/path/to/osgi-ripper.xsl {} \; 
	-->
	<xsl:output indent="yes" method="xml"></xsl:output>

	<xsl:template match="/">
		<xsl:element name="beans">
			<xsl:attribute name="xmlns">http://www.springframework.org/schema/beans</xsl:attribute>
			<xsl:attribute name="xsi:schemaLocation">http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd</xsl:attribute>
			<xsl:apply-templates select="child::node()" />
		</xsl:element>
	</xsl:template>
	
		<xsl:template match="osgi:reference">
			<!--
				<bean name="early-import" factory-bean="root"
				factory-method="lookup" > <constructor-arg ><idref
				local="just-bean"/></constructor-arg> <constructor-arg
				value="com.griddynamics.spring.nested.NarrowDaddy"></constructor-arg>
				</bean>
			-->
			<xsl:element name="bean">
				<xsl:attribute name="name"><xsl:value-of select="@id" /></xsl:attribute>
				<xsl:attribute name="factory-bean">root</xsl:attribute>
				<xsl:attribute name="factory-method">lookup</xsl:attribute>
				<xsl:attribute name="lazy-init">true</xsl:attribute>
				<xsl:element name="constructor-arg">
					<xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
				</xsl:element>
				<xsl:element name="constructor-arg">
					<xsl:attribute name="value"><xsl:value-of
						select="@interface" /></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:template>

		<xsl:template match="osgi:service">
			<!--
				<bean name="export-declaration" factory-bean="root"
				factory-method="export" depends-on="early-import" >
				<constructor-arg> <bean
				class="com.griddynamics.spring.nested.ExportRef">
				<constructor-arg><idref local="just-bean"/></constructor-arg>
				<constructor-arg
				value="com.griddynamics.spring.nested.ExtendedChild"></constructor-arg>
				</bean></constructor-arg> </bean>
			-->
			<xsl:element name="bean">
				<xsl:attribute name="name"><xsl:value-of select="@ref" /><xsl:text>-export</xsl:text></xsl:attribute>
				<xsl:attribute name="factory-bean">root</xsl:attribute>
				<xsl:attribute name="factory-method">export</xsl:attribute>
				<xsl:attribute name="lazy-init">false</xsl:attribute>
				<xsl:element name="constructor-arg">
					<xsl:element name="bean">
						<xsl:attribute name="class">com.griddynamics.spring.nested.ExportRef</xsl:attribute>
						<xsl:attribute name="name"><xsl:value-of select="@ref" /><xsl:text>-export-ref</xsl:text></xsl:attribute>
						<xsl:element name="constructor-arg">
							<!-- <xsl:attribute name="type">java.lang.String</xsl:attribute> -->
							<xsl:element name="idref">
								<xsl:attribute name="bean"><xsl:value-of
									select="@ref" /></xsl:attribute>
							</xsl:element>
						</xsl:element>
						<xsl:element name="constructor-arg">
							<xsl:attribute name="value"><xsl:value-of
								select="@interface" /></xsl:attribute>
							<!--  <xsl:attribute name="type">java.lang.Class</xsl:attribute> -->
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:template>

	<xsl:template match="@*|text()">  
	</xsl:template>  
	
</xsl:stylesheet>
