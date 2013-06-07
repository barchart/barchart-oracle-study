/*     */ package com.sun.xml.internal.ws.developer;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
/*     */ import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.Marshaller;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import javax.xml.bind.annotation.XmlAnyAttribute;
/*     */ import javax.xml.bind.annotation.XmlAnyElement;
/*     */ import javax.xml.bind.annotation.XmlAttribute;
/*     */ import javax.xml.bind.annotation.XmlElement;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ import javax.xml.bind.annotation.XmlType;
/*     */ import javax.xml.bind.annotation.XmlValue;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.ws.EndpointReference;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ @XmlRootElement(name="EndpointReference", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */ @XmlType(name="EndpointReferenceType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */ public final class MemberSubmissionEndpointReference extends EndpointReference
/*     */   implements MemberSubmissionAddressingConstants
/*     */ {
/*  67 */   private static final JAXBContext msjc = getMSJaxbContext();
/*     */ 
/*     */   @XmlElement(name="Address", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */   public Address addr;
/*     */ 
/*     */   @XmlElement(name="ReferenceProperties", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */   public Elements referenceProperties;
/*     */ 
/*     */   @XmlElement(name="ReferenceParameters", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */   public Elements referenceParameters;
/*     */ 
/*     */   @XmlElement(name="PortType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */   public AttributedQName portTypeName;
/*     */ 
/*     */   @XmlElement(name="ServiceName", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
/*     */   public ServiceNameType serviceName;
/*     */ 
/*     */   @XmlAnyAttribute
/*     */   public Map<QName, String> attributes;
/*     */ 
/*     */   @XmlAnyElement
/*     */   public List<Element> elements;
/*     */   protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
/*     */ 
/*     */   public MemberSubmissionEndpointReference() {  } 
/*  84 */   public MemberSubmissionEndpointReference(@NotNull Source source) { if (source == null)
/*  85 */       throw new WebServiceException("Source parameter can not be null on constructor");
/*     */     try
/*     */     {
/*  88 */       Unmarshaller unmarshaller = msjc.createUnmarshaller();
/*  89 */       MemberSubmissionEndpointReference epr = (MemberSubmissionEndpointReference)unmarshaller.unmarshal(source, MemberSubmissionEndpointReference.class).getValue();
/*     */ 
/*  91 */       this.addr = epr.addr;
/*  92 */       this.referenceProperties = epr.referenceProperties;
/*  93 */       this.referenceParameters = epr.referenceParameters;
/*  94 */       this.portTypeName = epr.portTypeName;
/*  95 */       this.serviceName = epr.serviceName;
/*  96 */       this.attributes = epr.attributes;
/*  97 */       this.elements = epr.elements;
/*     */     } catch (JAXBException e) {
/*  99 */       throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", e);
/*     */     } catch (ClassCastException e) {
/* 101 */       throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", e);
/*     */     } }
/*     */ 
/*     */   public void writeTo(Result result)
/*     */   {
/*     */     try {
/* 107 */       Marshaller marshaller = msjc.createMarshaller();
/*     */ 
/* 109 */       marshaller.marshal(this, result);
/*     */     } catch (JAXBException e) {
/* 111 */       throw new WebServiceException("Error marshalling W3CEndpointReference. ", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Source toWSDLSource()
/*     */   {
/* 121 */     Element wsdlElement = null;
/*     */ 
/* 123 */     for (Element elem : this.elements) {
/* 124 */       if ((elem.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) && (elem.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())))
/*     */       {
/* 126 */         wsdlElement = elem;
/*     */       }
/*     */     }
/*     */ 
/* 130 */     return new DOMSource(wsdlElement);
/*     */   }
/*     */ 
/*     */   private static JAXBContext getMSJaxbContext()
/*     */   {
/*     */     try {
/* 136 */       return JAXBContext.newInstance(new Class[] { MemberSubmissionEndpointReference.class });
/*     */     } catch (JAXBException e) {
/* 138 */       throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Address
/*     */   {
/*     */ 
/*     */     @XmlValue
/*     */     public String uri;
/*     */ 
/*     */     @XmlAnyAttribute
/*     */     public Map<QName, String> attributes;
/*     */   }
/*     */ 
/*     */   public static class AttributedQName
/*     */   {
/*     */ 
/*     */     @XmlValue
/*     */     public QName name;
/*     */ 
/*     */     @XmlAnyAttribute
/*     */     public Map<QName, String> attributes;
/*     */   }
/*     */ 
/*     */   public static class Elements
/*     */   {
/*     */ 
/*     */     @XmlAnyElement
/*     */     public List<Element> elements;
/*     */   }
/*     */ 
/*     */   public static class ServiceNameType extends MemberSubmissionEndpointReference.AttributedQName
/*     */   {
/*     */ 
/*     */     @XmlAttribute(name="PortName")
/*     */     public String portName;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference
 * JD-Core Version:    0.6.2
 */