<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:beans="http://www.springframework.org/schema/beans">
	
	<xsl:output indent="yes" method="text"></xsl:output>
		

		<xsl:template match="//beans:bean[@factory-method='lookup']" >
import:	<xsl:value-of select="beans:constructor-arg[1]/@value"/> of: <xsl:value-of select="beans:constructor-arg[2]/@value"/>
		</xsl:template>
		
		<xsl:template match="//beans:bean[@factory-method='export']" >
export: <xsl:value-of select="beans:constructor-arg/beans:bean/beans:constructor-arg/beans:idref/@bean"/> of: <xsl:value-of select="beans:constructor-arg/beans:bean/beans:constructor-arg/@value"/>
		</xsl:template>
	
	<xsl:template match="@*|text()">  
	</xsl:template>  
</xsl:stylesheet>