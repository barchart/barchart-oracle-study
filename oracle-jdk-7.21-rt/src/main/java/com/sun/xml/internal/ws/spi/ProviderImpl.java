/*     */ package com.sun.xml.internal.ws.spi;
/*     */ 
/*     */ import com.sun.xml.internal.ws.api.BindingID;
/*     */ import com.sun.xml.internal.ws.api.WSService;
/*     */ import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
/*     */ import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
/*     */ import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.Metadata;
/*     */ import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
/*     */ import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
/*     */ import com.sun.xml.internal.ws.api.server.BoundEndpoint;
/*     */ import com.sun.xml.internal.ws.api.server.Container;
/*     */ import com.sun.xml.internal.ws.api.server.ContainerResolver;
/*     */ import com.sun.xml.internal.ws.api.server.Module;
/*     */ import com.sun.xml.internal.ws.api.server.WSEndpoint;
/*     */ import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
/*     */ import com.sun.xml.internal.ws.client.WSServiceDelegate;
/*     */ import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
/*     */ import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
/*     */ import com.sun.xml.internal.ws.resources.ProviderApiMessages;
/*     */ import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
/*     */ import com.sun.xml.internal.ws.util.ServiceFinder;
/*     */ import com.sun.xml.internal.ws.util.xml.XmlUtil;
/*     */ import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import javax.xml.ws.Endpoint;
/*     */ import javax.xml.ws.EndpointReference;
/*     */ import javax.xml.ws.Service;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.spi.Invoker;
/*     */ import javax.xml.ws.spi.Provider;
/*     */ import javax.xml.ws.spi.ServiceDelegate;
/*     */ import javax.xml.ws.wsaddressing.W3CEndpointReference;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.EntityResolver;
/*     */ 
/*     */ public class ProviderImpl extends Provider
/*     */ {
/*  80 */   private static final JAXBContext eprjc = getEPRJaxbContext();
/*     */ 
/*  85 */   public static final ProviderImpl INSTANCE = new ProviderImpl();
/*     */ 
/*     */   public Endpoint createEndpoint(String bindingId, Object implementor)
/*     */   {
/*  89 */     return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
/*     */   }
/*     */ 
/*     */   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass)
/*     */   {
/*  96 */     return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass);
/*     */   }
/*     */ 
/*     */   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass, WebServiceFeature[] features)
/*     */   {
/* 101 */     if (features.length > 0) {
/* 102 */       throw new WebServiceException("Doesn't support any Service specific features");
/*     */     }
/* 104 */     return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass);
/*     */   }
/*     */ 
/*     */   public Endpoint createAndPublishEndpoint(String address, Object implementor)
/*     */   {
/* 110 */     Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
/*     */ 
/* 113 */     endpoint.publish(address);
/* 114 */     return endpoint;
/*     */   }
/*     */ 
/*     */   public Endpoint createEndpoint(String bindingId, Object implementor, WebServiceFeature[] features) {
/* 118 */     return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, features);
/*     */   }
/*     */ 
/*     */   public Endpoint createAndPublishEndpoint(String address, Object implementor, WebServiceFeature[] features)
/*     */   {
/* 124 */     Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, features);
/*     */ 
/* 126 */     endpoint.publish(address);
/* 127 */     return endpoint;
/*     */   }
/*     */ 
/*     */   public Endpoint createEndpoint(String bindingId, Class implementorClass, Invoker invoker, WebServiceFeature[] features) {
/* 131 */     return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementorClass), implementorClass, invoker, features);
/*     */   }
/*     */ 
/*     */   public EndpointReference readEndpointReference(final Source eprInfoset)
/*     */   {
/* 140 */     return (EndpointReference)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public EndpointReference run() {
/*     */         try {
/* 143 */           Unmarshaller unmarshaller = ProviderImpl.eprjc.createUnmarshaller();
/* 144 */           return (EndpointReference)unmarshaller.unmarshal(eprInfoset);
/*     */         } catch (JAXBException e) {
/* 146 */           throw new WebServiceException("Error creating Marshaller or marshalling.", e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public <T> T getPort(EndpointReference endpointReference, Class<T> clazz, WebServiceFeature[] webServiceFeatures)
/*     */   {
/* 158 */     if (endpointReference == null)
/* 159 */       throw new WebServiceException(ProviderApiMessages.NULL_EPR());
/* 160 */     WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
/* 161 */     WSEndpointReference.Metadata metadata = wsepr.getMetaData();
/*     */     WSService service;
/* 163 */     if (metadata.getWsdlSource() != null)
/* 164 */       service = new WSServiceDelegate(metadata.getWsdlSource(), metadata.getServiceName(), Service.class);
/*     */     else
/* 166 */       throw new WebServiceException("WSDL metadata is missing in EPR");
/*     */     WSService service;
/* 167 */     return service.getPort(wsepr, clazz, webServiceFeatures);
/*     */   }
/*     */ 
/*     */   public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters) {
/* 171 */     return createW3CEndpointReference(address, null, serviceName, portName, metadata, wsdlDocumentLocation, referenceParameters, null, null);
/*     */   }
/*     */ 
/*     */   public W3CEndpointReference createW3CEndpointReference(String address, QName interfaceName, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters, List<Element> elements, Map<QName, String> attributes)
/*     */   {
/* 177 */     Container container = ContainerResolver.getInstance().getContainer();
/* 178 */     if (address == null) {
/* 179 */       if ((serviceName == null) || (portName == null)) {
/* 180 */         throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS_SERVICE_ENDPOINT());
/*     */       }
/*     */ 
/* 183 */       Module module = (Module)container.getSPI(Module.class);
/* 184 */       if (module != null) {
/* 185 */         List beList = module.getBoundEndpoints();
/* 186 */         for (BoundEndpoint be : beList) {
/* 187 */           WSEndpoint wse = be.getEndpoint();
/* 188 */           if ((wse.getServiceName().equals(serviceName)) && (wse.getPortName().equals(portName))) {
/*     */             try {
/* 190 */               address = be.getAddress().toString();
/*     */             }
/*     */             catch (WebServiceException e)
/*     */             {
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 200 */       if (address == null) {
/* 201 */         throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS());
/*     */       }
/*     */     }
/* 204 */     if ((serviceName == null) && (portName != null)) {
/* 205 */       throw new IllegalStateException(ProviderApiMessages.NULL_SERVICE());
/*     */     }
/*     */ 
/* 208 */     String wsdlTargetNamespace = null;
/* 209 */     if (wsdlDocumentLocation != null) {
/*     */       try {
/* 211 */         EntityResolver er = XmlUtil.createDefaultCatalogResolver();
/*     */ 
/* 213 */         URL wsdlLoc = new URL(wsdlDocumentLocation);
/* 214 */         WSDLModelImpl wsdlDoc = RuntimeWSDLParser.parse(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), er, true, container, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
/*     */ 
/* 216 */         if (serviceName != null) {
/* 217 */           WSDLService wsdlService = wsdlDoc.getService(serviceName);
/* 218 */           if (wsdlService == null) {
/* 219 */             throw new IllegalStateException(ProviderApiMessages.NOTFOUND_SERVICE_IN_WSDL(serviceName, wsdlDocumentLocation));
/*     */           }
/* 221 */           if (portName != null) {
/* 222 */             WSDLPort wsdlPort = wsdlService.get(portName);
/* 223 */             if (wsdlPort == null) {
/* 224 */               throw new IllegalStateException(ProviderApiMessages.NOTFOUND_PORT_IN_WSDL(portName, serviceName, wsdlDocumentLocation));
/*     */             }
/*     */           }
/* 227 */           wsdlTargetNamespace = serviceName.getNamespaceURI();
/*     */         } else {
/* 229 */           QName firstService = wsdlDoc.getFirstServiceName();
/* 230 */           wsdlTargetNamespace = firstService.getNamespaceURI();
/*     */         }
/*     */       } catch (Exception e) {
/* 233 */         throw new IllegalStateException(ProviderApiMessages.ERROR_WSDL(wsdlDocumentLocation), e);
/*     */       }
/*     */     }
/* 236 */     return (W3CEndpointReference)new WSEndpointReference(AddressingVersion.fromSpecClass(W3CEndpointReference.class), address, serviceName, portName, interfaceName, metadata, wsdlDocumentLocation, wsdlTargetNamespace, referenceParameters, elements, attributes).toSpec(W3CEndpointReference.class);
/*     */   }
/*     */ 
/*     */   private static JAXBContext getEPRJaxbContext()
/*     */   {
/* 246 */     return (JAXBContext)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public JAXBContext run() {
/*     */         try {
/* 249 */           return JAXBContext.newInstance(new Class[] { MemberSubmissionEndpointReference.class, W3CEndpointReference.class });
/*     */         } catch (JAXBException e) {
/* 251 */           throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.spi.ProviderImpl
 * JD-Core Version:    0.6.2
 */