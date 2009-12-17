//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.16 at 04:50:24 PM CET 
//


package org.deegree.commons.configuration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatabaseType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DatabaseType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="POSTGIS"/>
 *     &lt;enumeration value="ORACLE"/>
 *     &lt;enumeration value="MYSQL"/>
 *     &lt;enumeration value="UNDEFINED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DatabaseType")
@XmlEnum
public enum DatabaseType {

    POSTGIS,
    ORACLE,
    MYSQL,
    UNDEFINED;

    public String value() {
        return name();
    }

    public static DatabaseType fromValue(String v) {
        return valueOf(v);
    }

}
