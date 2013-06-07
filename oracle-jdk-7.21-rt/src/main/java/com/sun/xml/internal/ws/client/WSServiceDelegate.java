/*     */ package com.sun.xml.internal.ws.client;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.ws.Closeable;
/*     */ import com.sun.xml.internal.ws.api.BindingID;
/*     */ import com.sun.xml.internal.ws.api.EndpointAddress;
/*     */ import com.sun.xml.internal.ws.api.WSService;
/*     */ import com.sun.xml.internal.ws.api.WSService.InitParams;
/*     */ import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
/*     */ import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.Metadata;
/*     */ import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
/*     */ import com.sun.xml.internal.ws.api.client.ServiceInterceptorFactory;
/*     */ import com.sun.xml.internal.ws.api.pipe.Stubs;
/*     */ import com.sun.xml.internal.ws.api.server.Container;
/*     */ import com.sun.xml.internal.ws.api.server.ContainerResolver;
/*     */ import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
/*     */ import com.sun.xml.internal.ws.binding.BindingImpl;
/*     */ import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
/*     */ import com.sun.xml.internal.ws.client.sei.SEIStub;
/*     */ import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
/*     */ import com.sun.xml.internal.ws.developer.WSBindingProvider;
/*     */ import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
/*     */ import com.sun.xml.internal.ws.model.RuntimeModeler;
/*     */ import com.sun.xml.internal.ws.model.SOAPSEIModel;
/*     */ import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
/*     */ import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
/*     */ import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
/*     */ import com.sun.xml.internal.ws.model.wsdl.WSDLServiceImpl;
/*     */ import com.sun.xml.internal.ws.resources.ClientMessages;
/*     */ import com.sun.xml.internal.ws.resources.DispatchMessages;
/*     */ import com.sun.xml.internal.ws.resources.ProviderApiMessages;
/*     */ import com.sun.xml.internal.ws.util.JAXWSUtils;
/*     */ import com.sun.xml.internal.ws.util.ServiceConfigurationError;
/*     */ import com.sun.xml.internal.ws.util.ServiceFinder;
/*     */ import com.sun.xml.internal.ws.util.xml.XmlUtil;
/*     */ import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import javax.jws.HandlerChain;
/*     */ import javax.jws.WebService;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import javax.xml.ws.Dispatch;
/*     */ import javax.xml.ws.EndpointReference;
/*     */ import javax.xml.ws.Service;
/*     */ import javax.xml.ws.Service.Mode;
/*     */ import javax.xml.ws.WebServiceClient;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.handler.HandlerResolver;
/*     */ import javax.xml.ws.soap.AddressingFeature;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class WSServiceDelegate extends WSService
/*     */ {
/* 124 */   private final Map<QName, PortInfo> ports = new HashMap();
/*     */ 
/*     */   @NotNull
/* 131 */   private HandlerConfigurator handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(null);
/*     */   private final Class<? extends Service> serviceClass;
/*     */ 
/*     */   @NotNull
/*     */   private final QName serviceName;
/* 144 */   private final Map<QName, SEIPortInfo> seiContext = new HashMap();
/*     */   private volatile Executor executor;
/*     */ 
/*     */   @Nullable
/*     */   private WSDLServiceImpl wsdlService;
/*     */   private final Container container;
/*     */ 
/*     */   @NotNull
/*     */   final ServiceInterceptor serviceInterceptor;
/* 692 */   private static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
/*     */ 
/*     */   Map<QName, PortInfo> getQNameToPortInfoMap()
/*     */   {
/* 126 */     return this.ports;
/*     */   }
/*     */ 
/*     */   public WSServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass)
/*     */   {
/* 168 */     this(wsdlDocumentLocation == null ? null : new StreamSource(wsdlDocumentLocation.toExternalForm()), serviceName, serviceClass);
/*     */   }
/*     */ 
/*     */   public WSServiceDelegate(@Nullable Source wsdl, @NotNull QName serviceName, @NotNull final Class<? extends Service> serviceClass)
/*     */   {
/* 179 */     if (serviceName == null) {
/* 180 */       throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL(serviceName));
/*     */     }
/* 182 */     WSService.InitParams initParams = (WSService.InitParams)INIT_PARAMS.get();
/* 183 */     INIT_PARAMS.set(null);
/* 184 */     if (initParams == null) initParams = EMPTY_PARAMS;
/*     */ 
/* 186 */     this.serviceName = serviceName;
/* 187 */     this.serviceClass = serviceClass;
/* 188 */     Container tContainer = initParams.getContainer() != null ? initParams.getContainer() : ContainerResolver.getInstance().getContainer();
/* 189 */     if (tContainer == Container.NONE) {
/* 190 */       tContainer = new ClientContainer();
/*     */     }
/* 192 */     this.container = tContainer;
/*     */ 
/* 195 */     ServiceInterceptor interceptor = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
/* 196 */     ServiceInterceptor si = (ServiceInterceptor)this.container.getSPI(ServiceInterceptor.class);
/* 197 */     if (si != null) {
/* 198 */       interceptor = ServiceInterceptor.aggregate(new ServiceInterceptor[] { interceptor, si });
/*     */     }
/* 200 */     this.serviceInterceptor = interceptor;
/*     */ 
/* 204 */     if ((wsdl == null) && 
/* 205 */       (serviceClass != Service.class)) {
/* 206 */       WebServiceClient wsClient = (WebServiceClient)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public WebServiceClient run() {
/* 208 */           return (WebServiceClient)serviceClass.getAnnotation(WebServiceClient.class);
/*     */         }
/*     */       });
/* 211 */       String wsdlLocation = wsClient.wsdlLocation();
/* 212 */       wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
/* 213 */       wsdl = new StreamSource(wsdlLocation);
/*     */     }
/*     */ 
/* 216 */     WSDLServiceImpl service = null;
/* 217 */     if (wsdl != null) {
/*     */       try {
/* 219 */         URL url = wsdl.getSystemId() == null ? null : new URL(wsdl.getSystemId());
/* 220 */         WSDLModelImpl model = parseWSDL(url, wsdl);
/* 221 */         service = model.getService(this.serviceName);
/* 222 */         if (service == null) {
/* 223 */           throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(model.getServices().keySet())));
/*     */         }
/*     */ 
/* 227 */         for (WSDLPortImpl port : service.getPorts())
/* 228 */           this.ports.put(port.getName(), new PortInfo(this, port));
/*     */       } catch (MalformedURLException e) {
/* 230 */         throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
/*     */       }
/*     */     }
/* 233 */     this.wsdlService = service;
/*     */ 
/* 235 */     if (serviceClass != Service.class)
/*     */     {
/* 237 */       HandlerChain handlerChain = (HandlerChain)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public HandlerChain run() {
/* 240 */           return (HandlerChain)serviceClass.getAnnotation(HandlerChain.class);
/*     */         }
/*     */       });
/* 243 */       if (handlerChain != null)
/* 244 */         this.handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private WSDLModelImpl parseWSDL(URL wsdlDocumentLocation, Source wsdlSource)
/*     */   {
/*     */     try
/*     */     {
/* 257 */       return RuntimeWSDLParser.parse(wsdlDocumentLocation, wsdlSource, XmlUtil.createDefaultCatalogResolver(), true, getContainer(), (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
/*     */     }
/*     */     catch (IOException e) {
/* 260 */       throw new WebServiceException(e);
/*     */     } catch (XMLStreamException e) {
/* 262 */       throw new WebServiceException(e);
/*     */     } catch (SAXException e) {
/* 264 */       throw new WebServiceException(e);
/*     */     } catch (ServiceConfigurationError e) {
/* 266 */       throw new WebServiceException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Executor getExecutor()
/*     */   {
/* 274 */     return this.executor;
/*     */   }
/*     */ 
/*     */   public void setExecutor(Executor executor) {
/* 278 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */   public HandlerResolver getHandlerResolver() {
/* 282 */     return this.handlerConfigurator.getResolver();
/*     */   }
/*     */ 
/*     */   final HandlerConfigurator getHandlerConfigurator() {
/* 286 */     return this.handlerConfigurator;
/*     */   }
/*     */ 
/*     */   public void setHandlerResolver(HandlerResolver resolver) {
/* 290 */     this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(resolver);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(QName portName, Class<T> portInterface) throws WebServiceException {
/* 294 */     return getPort(portName, portInterface, EMPTY_FEATURES);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(QName portName, Class<T> portInterface, WebServiceFeature[] features) {
/* 298 */     if ((portName == null) || (portInterface == null))
/* 299 */       throw new IllegalArgumentException();
/* 300 */     WSDLServiceImpl tWsdlService = this.wsdlService;
/* 301 */     if (tWsdlService == null)
/*     */     {
/* 304 */       tWsdlService = getWSDLModelfromSEI(portInterface);
/*     */ 
/* 306 */       if (tWsdlService == null) {
/* 307 */         throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
/*     */       }
/*     */     }
/*     */ 
/* 311 */     WSDLPortImpl portModel = getPortModel(tWsdlService, portName);
/* 312 */     return getPort(portModel.getEPR(), portName, portInterface, features);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(EndpointReference epr, Class<T> portInterface, WebServiceFeature[] features) {
/* 316 */     return getPort(WSEndpointReference.create(epr), portInterface, features);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(WSEndpointReference wsepr, Class<T> portInterface, WebServiceFeature[] features)
/*     */   {
/* 321 */     QName portTypeName = RuntimeModeler.getPortTypeName(portInterface);
/*     */ 
/* 323 */     QName portName = getPortNameFromEPR(wsepr, portTypeName);
/* 324 */     return getPort(wsepr, portName, portInterface, features);
/*     */   }
/*     */ 
/*     */   private <T> T getPort(WSEndpointReference wsepr, QName portName, Class<T> portInterface, WebServiceFeature[] features)
/*     */   {
/* 329 */     SEIPortInfo spi = addSEI(portName, portInterface, features);
/* 330 */     return createEndpointIFBaseProxy(wsepr, portName, portInterface, features, spi);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(Class<T> portInterface, WebServiceFeature[] features) {
/* 334 */     QName portTypeName = RuntimeModeler.getPortTypeName(portInterface);
/* 335 */     WSDLServiceImpl wsdlService = this.wsdlService;
/* 336 */     if (wsdlService == null)
/*     */     {
/* 339 */       wsdlService = getWSDLModelfromSEI(portInterface);
/*     */ 
/* 341 */       if (wsdlService == null) {
/* 342 */         throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
/*     */       }
/*     */     }
/*     */ 
/* 346 */     WSDLPortImpl port = wsdlService.getMatchingPort(portTypeName);
/* 347 */     if (port == null)
/* 348 */       throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
/* 349 */     QName portName = port.getName();
/* 350 */     return getPort(portName, portInterface, features);
/*     */   }
/*     */ 
/*     */   public <T> T getPort(Class<T> portInterface) throws WebServiceException {
/* 354 */     return getPort(portInterface, EMPTY_FEATURES);
/*     */   }
/*     */ 
/*     */   public void addPort(QName portName, String bindingId, String endpointAddress) throws WebServiceException {
/* 358 */     if (!this.ports.containsKey(portName)) {
/* 359 */       BindingID bid = bindingId == null ? BindingID.SOAP11_HTTP : BindingID.parse(bindingId);
/* 360 */       this.ports.put(portName, new PortInfo(this, endpointAddress == null ? null : EndpointAddress.create(endpointAddress), portName, bid));
/*     */     }
/*     */     else
/*     */     {
/* 364 */       throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(portName.toString()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode) throws WebServiceException {
/* 369 */     return createDispatch(portName, aClass, mode, EMPTY_FEATURES);
/*     */   }
/*     */ 
/*     */   public <T> Dispatch<T> createDispatch(QName portName, WSEndpointReference wsepr, Class<T> aClass, Service.Mode mode, WebServiceFeature[] features)
/*     */   {
/* 374 */     PortInfo port = safeGetPort(portName);
/* 375 */     BindingImpl binding = port.createBinding(features, null);
/* 376 */     binding.setMode(mode);
/* 377 */     Dispatch dispatch = Stubs.createDispatch(port, this, binding, aClass, mode, wsepr);
/* 378 */     this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
/* 379 */     return dispatch;
/*     */   }
/*     */ 
/*     */   public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode, WebServiceFeature[] features) {
/* 383 */     WebServiceFeatureList featureList = new WebServiceFeatureList(features);
/* 384 */     WSEndpointReference wsepr = null;
/* 385 */     if ((featureList.isEnabled(AddressingFeature.class)) && (this.wsdlService != null) && (this.wsdlService.get(portName) != null)) {
/* 386 */       wsepr = this.wsdlService.get(portName).getEPR();
/*     */     }
/* 388 */     return createDispatch(portName, wsepr, aClass, mode, features);
/*     */   }
/*     */ 
/*     */   public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Service.Mode mode, WebServiceFeature[] features) {
/* 392 */     WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
/* 393 */     QName portName = addPortEpr(wsepr);
/* 394 */     return createDispatch(portName, wsepr, type, mode, features);
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public PortInfo safeGetPort(QName portName)
/*     */   {
/* 403 */     PortInfo port = (PortInfo)this.ports.get(portName);
/* 404 */     if (port == null) {
/* 405 */       throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, buildNameList(this.ports.keySet())));
/*     */     }
/* 407 */     return port;
/*     */   }
/*     */ 
/*     */   private StringBuilder buildNameList(Collection<QName> names) {
/* 411 */     StringBuilder sb = new StringBuilder();
/* 412 */     for (QName qn : names) {
/* 413 */       if (sb.length() > 0) sb.append(',');
/* 414 */       sb.append(qn);
/*     */     }
/* 416 */     return sb;
/*     */   }
/*     */ 
/*     */   public EndpointAddress getEndpointAddress(QName qName) {
/* 420 */     return ((PortInfo)this.ports.get(qName)).targetEndpoint;
/*     */   }
/*     */ 
/*     */   public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode) throws WebServiceException {
/* 424 */     return createDispatch(portName, jaxbContext, mode, EMPTY_FEATURES);
/*     */   }
/*     */ 
/*     */   public Dispatch<Object> createDispatch(QName portName, WSEndpointReference wsepr, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature[] features)
/*     */   {
/* 429 */     PortInfo port = safeGetPort(portName);
/* 430 */     BindingImpl binding = port.createBinding(features, null);
/* 431 */     binding.setMode(mode);
/* 432 */     Dispatch dispatch = Stubs.createJAXBDispatch(port, binding, jaxbContext, mode, wsepr);
/*     */ 
/* 434 */     this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
/* 435 */     return dispatch;
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public Container getContainer() {
/* 440 */     return this.container;
/*     */   }
/*     */ 
/*     */   public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature[] webServiceFeatures) {
/* 444 */     WebServiceFeatureList featureList = new WebServiceFeatureList(webServiceFeatures);
/* 445 */     WSEndpointReference wsepr = null;
/* 446 */     if ((featureList.isEnabled(AddressingFeature.class)) && (this.wsdlService != null) && (this.wsdlService.get(portName) != null)) {
/* 447 */       wsepr = this.wsdlService.get(portName).getEPR();
/*     */     }
/* 449 */     return createDispatch(portName, wsepr, jaxbContext, mode, webServiceFeatures);
/*     */   }
/*     */ 
/*     */   public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Service.Mode mode, WebServiceFeature[] features) {
/* 453 */     WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
/* 454 */     QName portName = addPortEpr(wsepr);
/* 455 */     return createDispatch(portName, wsepr, context, mode, features);
/*     */   }
/*     */ 
/*     */   private QName addPortEpr(WSEndpointReference wsepr) {
/* 459 */     if (wsepr == null)
/* 460 */       throw new WebServiceException(ProviderApiMessages.NULL_EPR());
/* 461 */     QName eprPortName = getPortNameFromEPR(wsepr, null);
/*     */ 
/* 465 */     PortInfo portInfo = new PortInfo(this, wsepr.getAddress() == null ? null : EndpointAddress.create(wsepr.getAddress()), eprPortName, getPortModel(this.wsdlService, eprPortName).getBinding().getBindingId());
/*     */ 
/* 467 */     if (!this.ports.containsKey(eprPortName)) {
/* 468 */       this.ports.put(eprPortName, portInfo);
/*     */     }
/*     */ 
/* 471 */     return eprPortName;
/*     */   }
/*     */ 
/*     */   private QName getPortNameFromEPR(@NotNull WSEndpointReference wsepr, @Nullable QName portTypeName)
/*     */   {
/* 488 */     WSEndpointReference.Metadata metadata = wsepr.getMetaData();
/* 489 */     QName eprServiceName = metadata.getServiceName();
/* 490 */     QName eprPortName = metadata.getPortName();
/* 491 */     if ((eprServiceName != null) && (!eprServiceName.equals(this.serviceName))) {
/* 492 */       throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match");
/*     */     }
/*     */ 
/* 495 */     if (this.wsdlService == null) {
/* 496 */       Source eprWsdlSource = metadata.getWsdlSource();
/* 497 */       if (eprWsdlSource == null)
/* 498 */         throw new WebServiceException(ProviderApiMessages.NULL_WSDL());
/*     */       try
/*     */       {
/* 501 */         WSDLModelImpl eprWsdlMdl = parseWSDL(new URL(wsepr.getAddress()), eprWsdlSource);
/* 502 */         this.wsdlService = eprWsdlMdl.getService(this.serviceName);
/* 503 */         if (this.wsdlService == null)
/* 504 */           throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(eprWsdlMdl.getServices().keySet())));
/*     */       }
/*     */       catch (MalformedURLException e) {
/* 507 */         throw new WebServiceException(ClientMessages.INVALID_ADDRESS(wsepr.getAddress()));
/*     */       }
/*     */     }
/* 510 */     QName portName = eprPortName;
/*     */ 
/* 512 */     if ((portName == null) && (portTypeName != null))
/*     */     {
/* 514 */       WSDLPortImpl port = this.wsdlService.getMatchingPort(portTypeName);
/* 515 */       if (port == null)
/* 516 */         throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
/* 517 */       portName = port.getName();
/*     */     }
/* 519 */     if (portName == null)
/* 520 */       throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME());
/* 521 */     if (this.wsdlService.get(portName) == null) {
/* 522 */       throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(portName, buildWsdlPortNames()));
/*     */     }
/* 524 */     return portName;
/*     */   }
/*     */ 
/*     */   private WSDLServiceImpl getWSDLModelfromSEI(final Class sei)
/*     */   {
/* 529 */     WebService ws = (WebService)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public WebService run() {
/* 531 */         return (WebService)sei.getAnnotation(WebService.class);
/*     */       }
/*     */     });
/* 534 */     if ((ws == null) || (ws.wsdlLocation().equals("")))
/* 535 */       return null;
/* 536 */     String wsdlLocation = ws.wsdlLocation();
/* 537 */     wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
/* 538 */     Source wsdl = new StreamSource(wsdlLocation);
/* 539 */     WSDLServiceImpl service = null;
/*     */     try
/*     */     {
/* 542 */       URL url = wsdl.getSystemId() == null ? null : new URL(wsdl.getSystemId());
/* 543 */       WSDLModelImpl model = parseWSDL(url, wsdl);
/* 544 */       service = model.getService(this.serviceName);
/* 545 */       if (service == null)
/* 546 */         throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, buildNameList(model.getServices().keySet())));
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 550 */       throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
/*     */     }
/* 552 */     return service;
/*     */   }
/*     */ 
/*     */   public QName getServiceName() {
/* 556 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   protected Class getServiceClass() {
/* 560 */     return this.serviceClass;
/*     */   }
/*     */ 
/*     */   public Iterator<QName> getPorts()
/*     */     throws WebServiceException
/*     */   {
/* 566 */     return this.ports.keySet().iterator();
/*     */   }
/*     */ 
/*     */   public URL getWSDLDocumentLocation() {
/* 570 */     if (this.wsdlService == null) return null; try
/*     */     {
/* 572 */       return new URL(this.wsdlService.getParent().getLocation().getSystemId());
/*     */     } catch (MalformedURLException e) {
/* 574 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> T createEndpointIFBaseProxy(@Nullable WSEndpointReference epr, QName portName, Class<T> portInterface, WebServiceFeature[] webServiceFeatures, SEIPortInfo eif)
/*     */   {
/* 581 */     if (this.wsdlService == null) {
/* 582 */       throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(this.serviceName));
/*     */     }
/* 584 */     if (this.wsdlService.get(portName) == null) {
/* 585 */       throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, buildWsdlPortNames()));
/*     */     }
/*     */ 
/* 589 */     BindingImpl binding = eif.createBinding(webServiceFeatures, portInterface);
/* 590 */     SEIStub pis = new SEIStub(eif, binding, eif.model, epr);
/*     */ 
/* 592 */     Object proxy = createProxy(portInterface, pis);
/*     */ 
/* 594 */     if (this.serviceInterceptor != null) {
/* 595 */       this.serviceInterceptor.postCreateProxy((WSBindingProvider)proxy, portInterface);
/*     */     }
/* 597 */     return proxy;
/*     */   }
/*     */ 
/*     */   private <T> T createProxy(final Class<T> portInterface, final SEIStub pis)
/*     */   {
/* 603 */     RuntimePermission perm = new RuntimePermission("accessClassInPackage.com.sun.xml.internal.*");
/* 604 */     PermissionCollection perms = perm.newPermissionCollection();
/* 605 */     perms.add(perm);
/*     */ 
/* 607 */     return AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public T run()
/*     */       {
/* 611 */         Object proxy = Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[] { portInterface, WSBindingProvider.class, Closeable.class }, pis);
/*     */ 
/* 613 */         return portInterface.cast(proxy);
/*     */       }
/*     */     }
/*     */     , new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, perms) }));
/*     */   }
/*     */ 
/*     */   private StringBuilder buildWsdlPortNames()
/*     */   {
/* 627 */     Set wsdlPortNames = new HashSet();
/* 628 */     for (WSDLPortImpl port : this.wsdlService.getPorts())
/* 629 */       wsdlPortNames.add(port.getName());
/* 630 */     return buildNameList(wsdlPortNames);
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   private WSDLPortImpl getPortModel(WSDLServiceImpl wsdlService, QName portName)
/*     */   {
/* 639 */     WSDLPortImpl port = wsdlService.get(portName);
/* 640 */     if (port == null) {
/* 641 */       throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, buildWsdlPortNames()));
/*     */     }
/* 643 */     return port;
/*     */   }
/*     */ 
/*     */   private SEIPortInfo addSEI(QName portName, Class portInterface, WebServiceFeature[] features)
/*     */     throws WebServiceException
/*     */   {
/* 652 */     boolean ownModel = useOwnSEIModel(features);
/* 653 */     if (ownModel)
/*     */     {
/* 655 */       return createSEIPortInfo(portName, portInterface, features);
/*     */     }
/*     */ 
/* 658 */     SEIPortInfo spi = (SEIPortInfo)this.seiContext.get(portName);
/* 659 */     if (spi == null) {
/* 660 */       spi = createSEIPortInfo(portName, portInterface, features);
/* 661 */       this.seiContext.put(spi.portName, spi);
/* 662 */       this.ports.put(spi.portName, spi);
/*     */     }
/* 664 */     return spi;
/*     */   }
/*     */ 
/*     */   private SEIPortInfo createSEIPortInfo(QName portName, Class portInterface, WebServiceFeature[] features) {
/* 668 */     WSDLPortImpl wsdlPort = getPortModel(this.wsdlService, portName);
/* 669 */     RuntimeModeler modeler = new RuntimeModeler(portInterface, this.serviceName, wsdlPort, features);
/* 670 */     modeler.setClassLoader(portInterface.getClassLoader());
/* 671 */     modeler.setPortName(portName);
/* 672 */     AbstractSEIModelImpl model = modeler.buildRuntimeModel();
/* 673 */     return new SEIPortInfo(this, portInterface, (SOAPSEIModel)model, wsdlPort);
/*     */   }
/*     */ 
/*     */   private boolean useOwnSEIModel(WebServiceFeature[] features) {
/* 677 */     return WebServiceFeatureList.getFeature(features, UsesJAXBContextFeature.class) != null;
/*     */   }
/*     */ 
/*     */   public WSDLServiceImpl getWsdlService() {
/* 681 */     return this.wsdlService;
/*     */   }
/*     */   class DaemonThreadFactory implements ThreadFactory {
/*     */     DaemonThreadFactory() {
/*     */     }
/* 686 */     public Thread newThread(Runnable r) { Thread daemonThread = new Thread(r);
/* 687 */       daemonThread.setDaemon(Boolean.TRUE.booleanValue());
/* 688 */       return daemonThread;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.client.WSServiceDelegate
 * JD-Core Version:    0.6.2
 */