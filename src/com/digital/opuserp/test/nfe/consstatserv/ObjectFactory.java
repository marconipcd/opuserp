//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.14 at 05:24:08 PM BRST 
//


package com.digital.opuserp.test.nfe.consstatserv;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.com.javac.v200.consstatserv package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ConsStatServ_QNAME = new QName("http://www.portalfiscal.inf.br/nfe", "consStatServ");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.com.javac.v200.consstatserv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TRetConsStatServ }
     * 
     */
    public TRetConsStatServ createTRetConsStatServ() {
        return new TRetConsStatServ();
    }

    /**
     * Create an instance of {@link TConsStatServ }
     * 
     */
    public TConsStatServ createTConsStatServ() {
        return new TConsStatServ();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TConsStatServ }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.portalfiscal.inf.br/nfe", name = "consStatServ")
    public JAXBElement<TConsStatServ> createConsStatServ(TConsStatServ value) {
        return new JAXBElement<TConsStatServ>(_ConsStatServ_QNAME, TConsStatServ.class, null, value);
    }

}
