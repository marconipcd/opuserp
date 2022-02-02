//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.14 at 05:24:31 PM BRST 
//


package com.digital.opuserp.test.nfe.retenvinfe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Tipo da NF-e processada
 * 
 * <p>Java class for TNfeProc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TNfeProc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NFe" type="{http://www.portalfiscal.inf.br/nfe}TNFe"/>
 *         &lt;element name="protNFe" type="{http://www.portalfiscal.inf.br/nfe}TProtNFe"/>
 *       &lt;/sequence>
 *       &lt;attribute name="versao" use="required" type="{http://www.portalfiscal.inf.br/nfe}TVerNFe" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TNfeProc", propOrder = {
    "nFe",
    "protNFe"
})
public class TNfeProc {

    @XmlElement(name = "NFe", required = true)
    protected TNFe nFe;
    @XmlElement(required = true)
    protected TProtNFe protNFe;
    @XmlAttribute(required = true)
    protected String versao;

    /**
     * Gets the value of the nFe property.
     * 
     * @return
     *     possible object is
     *     {@link TNFe }
     *     
     */
    public TNFe getNFe() {
        return nFe;
    }

    /**
     * Sets the value of the nFe property.
     * 
     * @param value
     *     allowed object is
     *     {@link TNFe }
     *     
     */
    public void setNFe(TNFe value) {
        this.nFe = value;
    }

    /**
     * Gets the value of the protNFe property.
     * 
     * @return
     *     possible object is
     *     {@link TProtNFe }
     *     
     */
    public TProtNFe getProtNFe() {
        return protNFe;
    }

    /**
     * Sets the value of the protNFe property.
     * 
     * @param value
     *     allowed object is
     *     {@link TProtNFe }
     *     
     */
    public void setProtNFe(TProtNFe value) {
        this.protNFe = value;
    }

    /**
     * Gets the value of the versao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersao() {
        return versao;
    }

    /**
     * Sets the value of the versao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersao(String value) {
        this.versao = value;
    }

}
