/*      */ package com.sun.xml.internal.ws.model;
/*      */ 
/*      */ import com.sun.istack.internal.NotNull;
/*      */ import com.sun.xml.internal.bind.api.CompositeStructure;
/*      */ import com.sun.xml.internal.bind.api.TypeReference;
/*      */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*      */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*      */ import com.sun.xml.internal.ws.api.BindingID;
/*      */ import com.sun.xml.internal.ws.api.SOAPVersion;
/*      */ import com.sun.xml.internal.ws.api.model.ExceptionType;
/*      */ import com.sun.xml.internal.ws.api.model.MEP;
/*      */ import com.sun.xml.internal.ws.api.model.Parameter;
/*      */ import com.sun.xml.internal.ws.api.model.ParameterBinding;
/*      */ import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
/*      */ import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLBoundOperationImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLInputImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLOperationImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
/*      */ import com.sun.xml.internal.ws.model.wsdl.WSDLPortTypeImpl;
/*      */ import com.sun.xml.internal.ws.resources.ModelerMessages;
/*      */ import com.sun.xml.internal.ws.resources.ServerMessages;
/*      */ import com.sun.xml.internal.ws.util.localization.Localizable;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Type;
/*      */ import java.rmi.RemoteException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Collection;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.logging.Logger;
/*      */ import javax.jws.Oneway;
/*      */ import javax.jws.WebMethod;
/*      */ import javax.jws.WebParam;
/*      */ import javax.jws.WebParam.Mode;
/*      */ import javax.jws.WebResult;
/*      */ import javax.jws.WebService;
/*      */ import javax.jws.soap.SOAPBinding;
/*      */ import javax.jws.soap.SOAPBinding.ParameterStyle;
/*      */ import javax.jws.soap.SOAPBinding.Style;
/*      */ import javax.xml.bind.annotation.XmlElement;
/*      */ import javax.xml.bind.annotation.XmlSeeAlso;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.ws.Action;
/*      */ import javax.xml.ws.AsyncHandler;
/*      */ import javax.xml.ws.FaultAction;
/*      */ import javax.xml.ws.Holder;
/*      */ import javax.xml.ws.RequestWrapper;
/*      */ import javax.xml.ws.Response;
/*      */ import javax.xml.ws.ResponseWrapper;
/*      */ import javax.xml.ws.WebFault;
/*      */ import javax.xml.ws.WebServiceFeature;
/*      */ 
/*      */ public class RuntimeModeler
/*      */ {
/*      */   private final WebServiceFeature[] features;
/*      */   private final BindingID bindingId;
/*      */   private final Class portClass;
/*      */   private AbstractSEIModelImpl model;
/*      */   private SOAPBindingImpl defaultBinding;
/*      */   private String packageName;
/*      */   private String targetNamespace;
/*   88 */   private boolean isWrapped = true;
/*      */   private ClassLoader classLoader;
/*      */   private final WSDLPortImpl binding;
/*      */   private QName serviceName;
/*      */   private QName portName;
/*      */   public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
/*      */   public static final String JAXWS_PACKAGE_PD = "jaxws.";
/*      */   public static final String RESPONSE = "Response";
/*      */   public static final String RETURN = "return";
/*      */   public static final String BEAN = "Bean";
/*      */   public static final String SERVICE = "Service";
/*      */   public static final String PORT = "Port";
/*  107 */   public static final Class HOLDER_CLASS = Holder.class;
/*  108 */   public static final Class<RemoteException> REMOTE_EXCEPTION_CLASS = RemoteException.class;
/*  109 */   public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
/*  110 */   public static final Class<Exception> EXCEPTION_CLASS = Exception.class;
/*      */ 
/*  177 */   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");
/*      */ 
/*      */   public RuntimeModeler(@NotNull Class portClass, @NotNull QName serviceName, @NotNull BindingID bindingId, @NotNull WebServiceFeature[] features)
/*      */   {
/*  113 */     this(portClass, serviceName, null, bindingId, features);
/*      */   }
/*      */ 
/*      */   public RuntimeModeler(@NotNull Class portClass, @NotNull QName serviceName, @NotNull WSDLPortImpl wsdlPort, @NotNull WebServiceFeature[] features)
/*      */   {
/*  125 */     this(portClass, serviceName, wsdlPort, wsdlPort.getBinding().getBindingId(), features);
/*      */   }
/*      */ 
/*      */   private RuntimeModeler(@NotNull Class portClass, @NotNull QName serviceName, WSDLPortImpl binding, BindingID bindingId, @NotNull WebServiceFeature[] features) {
/*  129 */     this.portClass = portClass;
/*  130 */     this.serviceName = serviceName;
/*  131 */     this.binding = binding;
/*  132 */     this.bindingId = bindingId;
/*  133 */     this.features = features;
/*      */   }
/*      */ 
/*      */   public void setClassLoader(ClassLoader classLoader)
/*      */   {
/*  141 */     this.classLoader = classLoader;
/*      */   }
/*      */ 
/*      */   public void setPortName(QName portName)
/*      */   {
/*  150 */     this.portName = portName;
/*      */   }
/*      */ 
/*      */   private static <T extends Annotation> T getPrivClassAnnotation(Class<?> clazz, final Class<T> T) {
/*  154 */     return (Annotation)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public T run() {
/*  156 */         return this.val$clazz.getAnnotation(T);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static <T extends Annotation> T getPrivMethodAnnotation(Method method, final Class<T> T) {
/*  162 */     return (Annotation)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public T run() {
/*  164 */         return this.val$method.getAnnotation(T);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static Annotation[][] getPrivParameterAnnotations(Method method) {
/*  170 */     return (Annotation[][])AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Annotation[][] run() {
/*  172 */         return this.val$method.getParameterAnnotations();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public AbstractSEIModelImpl buildRuntimeModel()
/*      */   {
/*  188 */     this.model = new SOAPSEIModel(this.features);
/*  189 */     Class clazz = this.portClass;
/*  190 */     WebService webService = (WebService)getPrivClassAnnotation(this.portClass, WebService.class);
/*  191 */     if (webService == null) {
/*  192 */       throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { this.portClass.getCanonicalName() });
/*      */     }
/*      */ 
/*  195 */     if (webService.endpointInterface().length() > 0) {
/*  196 */       clazz = getClass(webService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(webService.endpointInterface()));
/*  197 */       WebService seiService = (WebService)getPrivClassAnnotation(clazz, WebService.class);
/*  198 */       if (seiService == null) {
/*  199 */         throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() });
/*      */       }
/*      */ 
/*  204 */       SOAPBinding sbPortClass = (SOAPBinding)getPrivClassAnnotation(this.portClass, SOAPBinding.class);
/*  205 */       SOAPBinding sbSei = (SOAPBinding)getPrivClassAnnotation(clazz, SOAPBinding.class);
/*  206 */       if ((sbPortClass != null) && (
/*  207 */         (sbSei == null) || (sbSei.style() != sbPortClass.style()) || (sbSei.use() != sbPortClass.use()))) {
/*  208 */         logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", this.portClass.getName(), clazz.getName()));
/*      */       }
/*      */     }
/*      */ 
/*  212 */     if (this.serviceName == null)
/*  213 */       this.serviceName = getServiceName(this.portClass);
/*  214 */     this.model.setServiceQName(this.serviceName);
/*      */ 
/*  216 */     String portLocalName = this.portClass.getSimpleName() + "Port";
/*  217 */     if (webService.portName().length() > 0)
/*  218 */       portLocalName = webService.portName();
/*  219 */     else if (webService.name().length() > 0) {
/*  220 */       portLocalName = webService.name() + "Port";
/*      */     }
/*      */ 
/*  223 */     if (this.portName == null)
/*  224 */       this.portName = new QName(this.serviceName.getNamespaceURI(), portLocalName);
/*  225 */     if (!this.portName.getNamespaceURI().equals(this.serviceName.getNamespaceURI())) {
/*  226 */       throw new RuntimeModelerException("runtime.modeler.portname.servicename.namespace.mismatch", new Object[] { this.serviceName, this.portName });
/*      */     }
/*      */ 
/*  229 */     this.model.setPortName(this.portName);
/*      */ 
/*  231 */     processClass(clazz);
/*  232 */     if (this.model.getJavaMethods().size() == 0) {
/*  233 */       throw new RuntimeModelerException("runtime.modeler.no.operations", new Object[] { this.portClass.getName() });
/*      */     }
/*  235 */     this.model.postProcess();
/*      */ 
/*  240 */     if (this.binding != null) {
/*  241 */       this.model.freeze(this.binding);
/*      */     }
/*  243 */     return this.model;
/*      */   }
/*      */ 
/*      */   private Class getClass(String className, Localizable errorMessage)
/*      */   {
/*      */     try
/*      */     {
/*  255 */       if (this.classLoader == null) {
/*  256 */         return Thread.currentThread().getContextClassLoader().loadClass(className);
/*      */       }
/*  258 */       return this.classLoader.loadClass(className); } catch (ClassNotFoundException e) {
/*      */     }
/*  260 */     throw new RuntimeModelerException(errorMessage);
/*      */   }
/*      */ 
/*      */   private Class getRequestWrapperClass(String className, Method method, QName reqElemName)
/*      */   {
/*  265 */     ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
/*      */     try {
/*  267 */       return loader.loadClass(className);
/*      */     } catch (ClassNotFoundException e) {
/*  269 */       logger.fine("Dynamically creating request wrapper Class " + className);
/*  270 */     }return WrapperBeanGenerator.createRequestWrapperBean(className, method, reqElemName, loader);
/*      */   }
/*      */ 
/*      */   private Class getResponseWrapperClass(String className, Method method, QName resElemName)
/*      */   {
/*  275 */     ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
/*      */     try {
/*  277 */       return loader.loadClass(className);
/*      */     } catch (ClassNotFoundException e) {
/*  279 */       logger.fine("Dynamically creating response wrapper bean Class " + className);
/*  280 */     }return WrapperBeanGenerator.createResponseWrapperBean(className, method, resElemName, loader);
/*      */   }
/*      */ 
/*      */   private Class getExceptionBeanClass(String className, Class exception, String name, String namespace)
/*      */   {
/*  286 */     ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
/*      */     try {
/*  288 */       return loader.loadClass(className);
/*      */     } catch (ClassNotFoundException e) {
/*  290 */       logger.fine("Dynamically creating exception bean Class " + className);
/*  291 */     }return WrapperBeanGenerator.createExceptionBean(className, exception, this.targetNamespace, name, namespace, loader);
/*      */   }
/*      */ 
/*      */   void processClass(Class clazz)
/*      */   {
/*  296 */     WebService webService = (WebService)getPrivClassAnnotation(clazz, WebService.class);
/*  297 */     String portTypeLocalName = clazz.getSimpleName();
/*  298 */     if (webService.name().length() > 0) {
/*  299 */       portTypeLocalName = webService.name();
/*      */     }
/*      */ 
/*  302 */     this.targetNamespace = webService.targetNamespace();
/*  303 */     this.packageName = "";
/*  304 */     if (clazz.getPackage() != null)
/*  305 */       this.packageName = clazz.getPackage().getName();
/*  306 */     if (this.targetNamespace.length() == 0) {
/*  307 */       this.targetNamespace = getNamespace(this.packageName);
/*      */     }
/*  309 */     this.model.setTargetNamespace(this.targetNamespace);
/*  310 */     QName portTypeName = new QName(this.targetNamespace, portTypeLocalName);
/*  311 */     this.model.setPortTypeName(portTypeName);
/*  312 */     this.model.setWSDLLocation(webService.wsdlLocation());
/*      */ 
/*  314 */     SOAPBinding soapBinding = (SOAPBinding)getPrivClassAnnotation(clazz, SOAPBinding.class);
/*  315 */     if (soapBinding != null) {
/*  316 */       if ((soapBinding.style() == SOAPBinding.Style.RPC) && (soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE)) {
/*  317 */         throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { soapBinding, clazz });
/*      */       }
/*      */ 
/*  321 */       this.isWrapped = (soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED);
/*      */     }
/*  323 */     this.defaultBinding = createBinding(soapBinding);
/*      */ 
/*  343 */     for (Method method : clazz.getMethods()) {
/*  344 */       if ((clazz.isInterface()) || 
/*  345 */         (isWebMethodBySpec(method, clazz)))
/*      */       {
/*  351 */         processMethod(method);
/*      */       }
/*      */     }
/*  354 */     XmlSeeAlso xmlSeeAlso = (XmlSeeAlso)getPrivClassAnnotation(clazz, XmlSeeAlso.class);
/*  355 */     if (xmlSeeAlso != null)
/*  356 */       this.model.addAdditionalClasses(xmlSeeAlso.value());
/*      */   }
/*      */ 
/*      */   private boolean isWebMethodBySpec(Method method, Class clazz)
/*      */   {
/*  372 */     int modifiers = method.getModifiers();
/*  373 */     boolean staticFinal = (Modifier.isStatic(modifiers)) || (Modifier.isFinal(modifiers));
/*      */ 
/*  375 */     assert (Modifier.isPublic(modifiers));
/*  376 */     assert (!clazz.isInterface());
/*      */ 
/*  378 */     WebMethod webMethod = (WebMethod)getPrivMethodAnnotation(method, WebMethod.class);
/*  379 */     if (webMethod != null) {
/*  380 */       if (webMethod.exclude()) {
/*  381 */         return false;
/*      */       }
/*  383 */       if (staticFinal) {
/*  384 */         throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(method));
/*      */       }
/*  386 */       return true;
/*      */     }
/*      */ 
/*  389 */     if (staticFinal) {
/*  390 */       return false;
/*      */     }
/*      */ 
/*  393 */     Class declClass = method.getDeclaringClass();
/*  394 */     return getPrivClassAnnotation(declClass, WebService.class) != null;
/*      */   }
/*      */ 
/*      */   protected SOAPBindingImpl createBinding(SOAPBinding soapBinding)
/*      */   {
/*  403 */     SOAPBindingImpl rtSOAPBinding = new SOAPBindingImpl();
/*  404 */     SOAPBinding.Style style = soapBinding != null ? soapBinding.style() : SOAPBinding.Style.DOCUMENT;
/*  405 */     rtSOAPBinding.setStyle(style);
/*  406 */     assert (this.bindingId != null);
/*  407 */     SOAPVersion soapVersion = this.bindingId.getSOAPVersion();
/*  408 */     rtSOAPBinding.setSOAPVersion(soapVersion);
/*  409 */     return rtSOAPBinding;
/*      */   }
/*      */ 
/*      */   public static String getNamespace(@NotNull String packageName)
/*      */   {
/*  419 */     if (packageName.length() == 0) {
/*  420 */       return null;
/*      */     }
/*  422 */     StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
/*      */     String[] tokens;
/*      */     String[] tokens;
/*  424 */     if (tokenizer.countTokens() == 0) {
/*  425 */       tokens = new String[0];
/*      */     } else {
/*  427 */       tokens = new String[tokenizer.countTokens()];
/*  428 */       for (int i = tokenizer.countTokens() - 1; i >= 0; i--) {
/*  429 */         tokens[i] = tokenizer.nextToken();
/*      */       }
/*      */     }
/*  432 */     StringBuilder namespace = new StringBuilder("http://");
/*  433 */     for (int i = 0; i < tokens.length; i++) {
/*  434 */       if (i != 0)
/*  435 */         namespace.append('.');
/*  436 */       namespace.append(tokens[i]);
/*      */     }
/*  438 */     namespace.append('/');
/*  439 */     return namespace.toString();
/*      */   }
/*      */ 
/*      */   private boolean isServiceException(Class<?> exception)
/*      */   {
/*  448 */     return (EXCEPTION_CLASS.isAssignableFrom(exception)) && (!RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception)) && (!REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception));
/*      */   }
/*      */ 
/*      */   protected void processMethod(Method method)
/*      */   {
/*  457 */     int mods = method.getModifiers();
/*  458 */     if ((!Modifier.isPublic(mods)) || (Modifier.isStatic(mods))) {
/*  459 */       if (method.getAnnotation(WebMethod.class) != null)
/*      */       {
/*  462 */         if (Modifier.isStatic(mods)) {
/*  463 */           throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATIC(method));
/*      */         }
/*  465 */         throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_PUBLIC(method));
/*      */       }
/*  467 */       return;
/*      */     }
/*      */ 
/*  470 */     WebMethod webMethod = (WebMethod)getPrivMethodAnnotation(method, WebMethod.class);
/*  471 */     if ((webMethod != null) && (webMethod.exclude())) {
/*  472 */       return;
/*      */     }
/*  474 */     String methodName = method.getName();
/*  475 */     boolean isOneway = method.isAnnotationPresent(Oneway.class);
/*      */ 
/*  478 */     if (isOneway)
/*  479 */       for (Class exception : method.getExceptionTypes())
/*  480 */         if (isServiceException(exception))
/*  481 */           throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[] { this.portClass.getCanonicalName(), methodName, exception.getName() });
/*      */     JavaMethodImpl javaMethod;
/*      */     JavaMethodImpl javaMethod;
/*  489 */     if (method.getDeclaringClass() == this.portClass)
/*  490 */       javaMethod = new JavaMethodImpl(this.model, method, method);
/*      */     else {
/*      */       try {
/*  493 */         Method tmpMethod = this.portClass.getMethod(method.getName(), method.getParameterTypes());
/*      */ 
/*  495 */         javaMethod = new JavaMethodImpl(this.model, tmpMethod, method);
/*      */       } catch (NoSuchMethodException e) {
/*  497 */         throw new RuntimeModelerException("runtime.modeler.method.not.found", new Object[] { method.getName(), this.portClass.getName() });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  505 */     MEP mep = getMEP(method);
/*  506 */     javaMethod.setMEP(mep);
/*      */ 
/*  508 */     String action = null;
/*      */ 
/*  511 */     String operationName = method.getName();
/*  512 */     if (webMethod != null) {
/*  513 */       action = webMethod.action();
/*  514 */       operationName = webMethod.operationName().length() > 0 ? webMethod.operationName() : operationName;
/*      */     }
/*      */ 
/*  520 */     if (this.binding != null) {
/*  521 */       WSDLBoundOperationImpl bo = this.binding.getBinding().get(new QName(this.targetNamespace, operationName));
/*  522 */       if (bo != null) {
/*  523 */         WSDLInputImpl wsdlInput = bo.getOperation().getInput();
/*  524 */         String wsaAction = wsdlInput.getAction();
/*  525 */         if ((wsaAction != null) && (!wsdlInput.isDefaultAction()))
/*  526 */           action = wsaAction;
/*      */         else {
/*  528 */           action = bo.getSOAPAction();
/*      */         }
/*      */       }
/*      */     }
/*  532 */     javaMethod.setOperationName(operationName);
/*  533 */     SOAPBinding methodBinding = (SOAPBinding)method.getAnnotation(SOAPBinding.class);
/*      */ 
/*  535 */     if ((methodBinding != null) && (methodBinding.style() == SOAPBinding.Style.RPC)) {
/*  536 */       logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(methodBinding, method.getName(), method.getDeclaringClass().getName()));
/*  537 */     } else if ((methodBinding == null) && (!method.getDeclaringClass().equals(this.portClass))) {
/*  538 */       methodBinding = (SOAPBinding)method.getDeclaringClass().getAnnotation(SOAPBinding.class);
/*  539 */       if ((methodBinding != null) && (methodBinding.style() == SOAPBinding.Style.RPC) && (methodBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE)) {
/*  540 */         throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { methodBinding, method.getDeclaringClass() });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  545 */     if ((methodBinding != null) && (this.defaultBinding.getStyle() != methodBinding.style())) {
/*  546 */       throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", new Object[] { methodBinding.style(), method.getName(), this.defaultBinding.getStyle() });
/*      */     }
/*      */ 
/*  550 */     boolean methodIsWrapped = this.isWrapped;
/*  551 */     SOAPBinding.Style style = this.defaultBinding.getStyle();
/*  552 */     if (methodBinding != null) {
/*  553 */       SOAPBindingImpl mySOAPBinding = createBinding(methodBinding);
/*  554 */       style = mySOAPBinding.getStyle();
/*  555 */       if (action != null)
/*  556 */         mySOAPBinding.setSOAPAction(action);
/*  557 */       methodIsWrapped = methodBinding.parameterStyle().equals(SOAPBinding.ParameterStyle.WRAPPED);
/*      */ 
/*  559 */       javaMethod.setBinding(mySOAPBinding);
/*      */     } else {
/*  561 */       SOAPBindingImpl sb = new SOAPBindingImpl(this.defaultBinding);
/*  562 */       if (action != null)
/*  563 */         sb.setSOAPAction(action);
/*      */       else
/*  565 */         sb.setSOAPAction("");
/*  566 */       javaMethod.setBinding(sb);
/*      */     }
/*  568 */     if (!methodIsWrapped)
/*  569 */       processDocBareMethod(javaMethod, operationName, method);
/*  570 */     else if (style.equals(SOAPBinding.Style.DOCUMENT)) {
/*  571 */       processDocWrappedMethod(javaMethod, methodName, operationName, method);
/*      */     }
/*      */     else {
/*  574 */       processRpcMethod(javaMethod, methodName, operationName, method);
/*      */     }
/*  576 */     this.model.addJavaMethod(javaMethod);
/*      */   }
/*      */ 
/*      */   private MEP getMEP(Method m) {
/*  580 */     if (m.isAnnotationPresent(Oneway.class)) {
/*  581 */       return MEP.ONE_WAY;
/*      */     }
/*  583 */     if (Response.class.isAssignableFrom(m.getReturnType()))
/*  584 */       return MEP.ASYNC_POLL;
/*  585 */     if (Future.class.isAssignableFrom(m.getReturnType())) {
/*  586 */       return MEP.ASYNC_CALLBACK;
/*      */     }
/*  588 */     return MEP.REQUEST_RESPONSE;
/*      */   }
/*      */ 
/*      */   protected void processDocWrappedMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method)
/*      */   {
/*  600 */     boolean methodHasHeaderParams = false;
/*  601 */     boolean isOneway = method.isAnnotationPresent(Oneway.class);
/*  602 */     RequestWrapper reqWrapper = (RequestWrapper)method.getAnnotation(RequestWrapper.class);
/*  603 */     ResponseWrapper resWrapper = (ResponseWrapper)method.getAnnotation(ResponseWrapper.class);
/*  604 */     String beanPackage = this.packageName + ".jaxws.";
/*  605 */     if ((this.packageName == null) || ((this.packageName != null) && (this.packageName.length() == 0)))
/*  606 */       beanPackage = "jaxws.";
/*      */     String requestClassName;
/*      */     String requestClassName;
/*  608 */     if ((reqWrapper != null) && (reqWrapper.className().length() > 0))
/*  609 */       requestClassName = reqWrapper.className();
/*      */     else
/*  611 */       requestClassName = beanPackage + capitalize(method.getName());
/*      */     String responseClassName;
/*      */     String responseClassName;
/*  616 */     if ((resWrapper != null) && (resWrapper.className().length() > 0))
/*  617 */       responseClassName = resWrapper.className();
/*      */     else {
/*  619 */       responseClassName = beanPackage + capitalize(method.getName()) + "Response";
/*      */     }
/*      */ 
/*  622 */     String reqName = operationName;
/*  623 */     String reqNamespace = this.targetNamespace;
/*  624 */     String reqPartName = "parameters";
/*  625 */     if (reqWrapper != null) {
/*  626 */       if (reqWrapper.targetNamespace().length() > 0)
/*  627 */         reqNamespace = reqWrapper.targetNamespace();
/*  628 */       if (reqWrapper.localName().length() > 0)
/*  629 */         reqName = reqWrapper.localName();
/*      */       try {
/*  631 */         if (reqWrapper.partName().length() > 0)
/*  632 */           reqPartName = reqWrapper.partName();
/*      */       }
/*      */       catch (LinkageError e)
/*      */       {
/*      */       }
/*      */     }
/*  638 */     QName reqElementName = new QName(reqNamespace, reqName);
/*  639 */     Class requestClass = getRequestWrapperClass(requestClassName, method, reqElementName);
/*      */ 
/*  641 */     Class responseClass = null;
/*  642 */     String resName = operationName + "Response";
/*  643 */     String resNamespace = this.targetNamespace;
/*  644 */     QName resElementName = null;
/*  645 */     String resPartName = "parameters";
/*  646 */     if (!isOneway) {
/*  647 */       if (resWrapper != null) {
/*  648 */         if (resWrapper.targetNamespace().length() > 0)
/*  649 */           resNamespace = resWrapper.targetNamespace();
/*  650 */         if (resWrapper.localName().length() > 0)
/*  651 */           resName = resWrapper.localName();
/*      */         try {
/*  653 */           if (resWrapper.partName().length() > 0)
/*  654 */             resPartName = resWrapper.partName();
/*      */         }
/*      */         catch (LinkageError e)
/*      */         {
/*      */         }
/*      */       }
/*  660 */       resElementName = new QName(resNamespace, resName);
/*  661 */       responseClass = getResponseWrapperClass(responseClassName, method, resElementName);
/*      */     }
/*      */ 
/*  664 */     TypeReference typeRef = new TypeReference(reqElementName, requestClass, new Annotation[0]);
/*      */ 
/*  666 */     WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
/*      */ 
/*  668 */     requestWrapper.setPartName(reqPartName);
/*  669 */     requestWrapper.setBinding(ParameterBinding.BODY);
/*  670 */     javaMethod.addParameter(requestWrapper);
/*  671 */     WrapperParameter responseWrapper = null;
/*  672 */     if (!isOneway) {
/*  673 */       typeRef = new TypeReference(resElementName, responseClass, new Annotation[0]);
/*  674 */       responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
/*  675 */       javaMethod.addParameter(responseWrapper);
/*  676 */       responseWrapper.setBinding(ParameterBinding.BODY);
/*      */     }
/*      */ 
/*  682 */     WebResult webResult = (WebResult)method.getAnnotation(WebResult.class);
/*  683 */     XmlElement xmlElem = (XmlElement)method.getAnnotation(XmlElement.class);
/*  684 */     QName resultQName = getReturnQName(method, webResult, xmlElem);
/*  685 */     Class returnType = method.getReturnType();
/*  686 */     boolean isResultHeader = false;
/*  687 */     if (webResult != null) {
/*  688 */       isResultHeader = webResult.header();
/*  689 */       methodHasHeaderParams = (isResultHeader) || (methodHasHeaderParams);
/*  690 */       if ((isResultHeader) && (xmlElem != null)) {
/*  691 */         throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " as the return value is bound to header", new Object[0]);
/*      */       }
/*  693 */       if ((resultQName.getNamespaceURI().length() == 0) && (webResult.header()))
/*      */       {
/*  695 */         resultQName = new QName(this.targetNamespace, resultQName.getLocalPart());
/*      */       }
/*      */     }
/*      */ 
/*  699 */     if (javaMethod.isAsync()) {
/*  700 */       returnType = getAsyncReturnType(method, returnType);
/*  701 */       resultQName = new QName("return");
/*      */     }
/*      */ 
/*  704 */     if ((!isOneway) && (returnType != null) && (!returnType.getName().equals("void"))) {
/*  705 */       Annotation[] rann = method.getAnnotations();
/*  706 */       if (resultQName.getLocalPart() != null) {
/*  707 */         TypeReference rTypeReference = new TypeReference(resultQName, returnType, rann);
/*  708 */         ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
/*  709 */         if (isResultHeader) {
/*  710 */           returnParameter.setBinding(ParameterBinding.HEADER);
/*  711 */           javaMethod.addParameter(returnParameter);
/*      */         } else {
/*  713 */           returnParameter.setBinding(ParameterBinding.BODY);
/*  714 */           responseWrapper.addWrapperChild(returnParameter);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  720 */     Class[] parameterTypes = method.getParameterTypes();
/*  721 */     Type[] genericParameterTypes = method.getGenericParameterTypes();
/*  722 */     Annotation[][] pannotations = getPrivParameterAnnotations(method);
/*  723 */     int pos = 0;
/*  724 */     for (Class clazzType : parameterTypes) {
/*  725 */       String partName = null;
/*  726 */       String paramName = "arg" + pos;
/*      */ 
/*  728 */       boolean isHeader = false;
/*      */ 
/*  730 */       if ((!javaMethod.isAsync()) || (!AsyncHandler.class.isAssignableFrom(clazzType)))
/*      */       {
/*  734 */         boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
/*      */ 
/*  736 */         if ((isHolder) && 
/*  737 */           (clazzType == Holder.class)) {
/*  738 */           clazzType = Navigator.REFLECTION.erasure(((java.lang.reflect.ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
/*      */         }
/*      */ 
/*  741 */         WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
/*  742 */         WebParam webParam = null;
/*  743 */         xmlElem = null;
/*  744 */         for (Annotation annotation : pannotations[pos]) {
/*  745 */           if (annotation.annotationType() == WebParam.class)
/*  746 */             webParam = (WebParam)annotation;
/*  747 */           else if (annotation.annotationType() == XmlElement.class) {
/*  748 */             xmlElem = (XmlElement)annotation;
/*      */           }
/*      */         }
/*  751 */         QName paramQName = getParameterQName(method, webParam, xmlElem, paramName);
/*  752 */         if (webParam != null) {
/*  753 */           isHeader = webParam.header();
/*  754 */           methodHasHeaderParams = (isHeader) || (methodHasHeaderParams);
/*  755 */           if ((isHeader) && (xmlElem != null)) {
/*  756 */             throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " parameter that is bound to header", new Object[0]);
/*      */           }
/*  758 */           if (webParam.partName().length() > 0)
/*  759 */             partName = webParam.partName();
/*      */           else
/*  761 */             partName = paramQName.getLocalPart();
/*  762 */           if ((isHeader) && (paramQName.getNamespaceURI().equals(""))) {
/*  763 */             paramQName = new QName(this.targetNamespace, paramQName.getLocalPart());
/*      */           }
/*  765 */           paramMode = webParam.mode();
/*  766 */           if ((isHolder) && (paramMode == WebParam.Mode.IN))
/*  767 */             paramMode = WebParam.Mode.INOUT;
/*      */         }
/*  769 */         typeRef = new TypeReference(paramQName, clazzType, pannotations[pos]);
/*      */ 
/*  771 */         ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
/*      */ 
/*  773 */         if (isHeader) {
/*  774 */           param.setBinding(ParameterBinding.HEADER);
/*  775 */           javaMethod.addParameter(param);
/*  776 */           param.setPartName(partName);
/*      */         } else {
/*  778 */           param.setBinding(ParameterBinding.BODY);
/*  779 */           if (paramMode != WebParam.Mode.OUT) {
/*  780 */             requestWrapper.addWrapperChild(param);
/*      */           }
/*  782 */           if (paramMode != WebParam.Mode.IN) {
/*  783 */             if (isOneway) {
/*  784 */               throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), methodName });
/*      */             }
/*      */ 
/*  787 */             responseWrapper.addWrapperChild(param);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  795 */     if (methodHasHeaderParams) {
/*  796 */       resPartName = "result";
/*      */     }
/*  798 */     if (responseWrapper != null)
/*  799 */       responseWrapper.setPartName(resPartName);
/*  800 */     processExceptions(javaMethod, method);
/*      */   }
/*      */ 
/*      */   protected void processRpcMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method)
/*      */   {
/*  813 */     boolean isOneway = method.isAnnotationPresent(Oneway.class);
/*      */ 
/*  820 */     Map resRpcParams = new TreeMap();
/*  821 */     Map reqRpcParams = new TreeMap();
/*      */ 
/*  824 */     String reqNamespace = this.targetNamespace;
/*  825 */     String respNamespace = this.targetNamespace;
/*      */ 
/*  827 */     if ((this.binding != null) && (this.binding.getBinding().isRpcLit())) {
/*  828 */       QName opQName = new QName(this.binding.getBinding().getPortTypeName().getNamespaceURI(), operationName);
/*  829 */       WSDLBoundOperationImpl op = this.binding.getBinding().get(opQName);
/*  830 */       if (op != null)
/*      */       {
/*  832 */         if (op.getRequestNamespace() != null) {
/*  833 */           reqNamespace = op.getRequestNamespace();
/*      */         }
/*      */ 
/*  837 */         if (op.getResponseNamespace() != null) {
/*  838 */           respNamespace = op.getResponseNamespace();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  843 */     QName reqElementName = new QName(reqNamespace, operationName);
/*  844 */     QName resElementName = null;
/*  845 */     if (!isOneway) {
/*  846 */       resElementName = new QName(respNamespace, operationName + "Response");
/*      */     }
/*      */ 
/*  849 */     Class wrapperType = CompositeStructure.class;
/*  850 */     TypeReference typeRef = new TypeReference(reqElementName, wrapperType, new Annotation[0]);
/*  851 */     WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
/*  852 */     requestWrapper.setInBinding(ParameterBinding.BODY);
/*  853 */     javaMethod.addParameter(requestWrapper);
/*  854 */     WrapperParameter responseWrapper = null;
/*  855 */     if (!isOneway) {
/*  856 */       typeRef = new TypeReference(resElementName, wrapperType, new Annotation[0]);
/*  857 */       responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
/*  858 */       responseWrapper.setOutBinding(ParameterBinding.BODY);
/*  859 */       javaMethod.addParameter(responseWrapper);
/*      */     }
/*      */ 
/*  862 */     Class returnType = method.getReturnType();
/*  863 */     String resultName = "return";
/*  864 */     String resultTNS = this.targetNamespace;
/*  865 */     String resultPartName = resultName;
/*  866 */     boolean isResultHeader = false;
/*  867 */     WebResult webResult = (WebResult)method.getAnnotation(WebResult.class);
/*      */ 
/*  869 */     if (webResult != null) {
/*  870 */       isResultHeader = webResult.header();
/*  871 */       if (webResult.name().length() > 0)
/*  872 */         resultName = webResult.name();
/*  873 */       if (webResult.partName().length() > 0) {
/*  874 */         resultPartName = webResult.partName();
/*  875 */         if (!isResultHeader)
/*  876 */           resultName = resultPartName;
/*      */       } else {
/*  878 */         resultPartName = resultName;
/*  879 */       }if (webResult.targetNamespace().length() > 0)
/*  880 */         resultTNS = webResult.targetNamespace();
/*  881 */       isResultHeader = webResult.header();
/*      */     }
/*      */     QName resultQName;
/*      */     QName resultQName;
/*  884 */     if (isResultHeader)
/*  885 */       resultQName = new QName(resultTNS, resultName);
/*      */     else {
/*  887 */       resultQName = new QName(resultName);
/*      */     }
/*  889 */     if (javaMethod.isAsync()) {
/*  890 */       returnType = getAsyncReturnType(method, returnType);
/*      */     }
/*      */ 
/*  893 */     if ((!isOneway) && (returnType != null) && (returnType != Void.TYPE)) {
/*  894 */       Annotation[] rann = method.getAnnotations();
/*  895 */       TypeReference rTypeReference = new TypeReference(resultQName, returnType, rann);
/*  896 */       ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
/*  897 */       returnParameter.setPartName(resultPartName);
/*  898 */       if (isResultHeader) {
/*  899 */         returnParameter.setBinding(ParameterBinding.HEADER);
/*  900 */         javaMethod.addParameter(returnParameter);
/*      */       } else {
/*  902 */         ParameterBinding rb = getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
/*  903 */         returnParameter.setBinding(rb);
/*  904 */         if (rb.isBody()) {
/*  905 */           WSDLPart p = getPart(new QName(this.targetNamespace, operationName), resultPartName, WebParam.Mode.OUT);
/*  906 */           if (p == null)
/*  907 */             resRpcParams.put(Integer.valueOf(resRpcParams.size() + 10000), returnParameter);
/*      */           else
/*  909 */             resRpcParams.put(Integer.valueOf(p.getIndex()), returnParameter);
/*      */         } else {
/*  911 */           javaMethod.addParameter(returnParameter);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  917 */     Class[] parameterTypes = method.getParameterTypes();
/*  918 */     Type[] genericParameterTypes = method.getGenericParameterTypes();
/*  919 */     Annotation[][] pannotations = getPrivParameterAnnotations(method);
/*  920 */     int pos = 0;
/*  921 */     for (Class clazzType : parameterTypes) {
/*  922 */       String paramName = "";
/*  923 */       String paramNamespace = "";
/*  924 */       String partName = "";
/*  925 */       boolean isHeader = false;
/*      */ 
/*  927 */       if ((!javaMethod.isAsync()) || (!AsyncHandler.class.isAssignableFrom(clazzType)))
/*      */       {
/*  931 */         boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
/*      */ 
/*  933 */         if ((isHolder) && 
/*  934 */           (clazzType == Holder.class)) {
/*  935 */           clazzType = Navigator.REFLECTION.erasure(((java.lang.reflect.ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
/*      */         }
/*  937 */         WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
/*  938 */         for (Annotation annotation : pannotations[pos]) {
/*  939 */           if (annotation.annotationType() == WebParam.class) {
/*  940 */             WebParam webParam = (WebParam)annotation;
/*  941 */             paramName = webParam.name();
/*  942 */             partName = webParam.partName();
/*  943 */             isHeader = webParam.header();
/*  944 */             WebParam.Mode mode = webParam.mode();
/*  945 */             paramNamespace = webParam.targetNamespace();
/*  946 */             if ((isHolder) && (mode == WebParam.Mode.IN))
/*  947 */               mode = WebParam.Mode.INOUT;
/*  948 */             paramMode = mode;
/*  949 */             break;
/*      */           }
/*      */         }
/*      */ 
/*  953 */         if (paramName.length() == 0) {
/*  954 */           paramName = "arg" + pos;
/*      */         }
/*  956 */         if (partName.length() == 0)
/*  957 */           partName = paramName;
/*  958 */         else if (!isHeader) {
/*  959 */           paramName = partName;
/*      */         }
/*  961 */         if (partName.length() == 0)
/*  962 */           partName = paramName;
/*      */         QName paramQName;
/*      */         QName paramQName;
/*  966 */         if (!isHeader)
/*      */         {
/*  968 */           paramQName = new QName("", paramName);
/*      */         } else {
/*  970 */           if (paramNamespace.length() == 0)
/*  971 */             paramNamespace = this.targetNamespace;
/*  972 */           paramQName = new QName(paramNamespace, paramName);
/*      */         }
/*  974 */         typeRef = new TypeReference(paramQName, clazzType, pannotations[pos]);
/*      */ 
/*  977 */         ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
/*  978 */         param.setPartName(partName);
/*      */ 
/*  980 */         if (paramMode == WebParam.Mode.INOUT) {
/*  981 */           ParameterBinding pb = getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
/*  982 */           param.setInBinding(pb);
/*  983 */           pb = getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
/*  984 */           param.setOutBinding(pb);
/*      */         }
/*  986 */         else if (isHeader) {
/*  987 */           param.setBinding(ParameterBinding.HEADER);
/*      */         } else {
/*  989 */           ParameterBinding pb = getBinding(operationName, partName, false, paramMode);
/*  990 */           param.setBinding(pb);
/*      */         }
/*      */ 
/*  993 */         if (param.getInBinding().isBody()) {
/*  994 */           if (!param.isOUT()) {
/*  995 */             WSDLPart p = getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.IN);
/*  996 */             if (p == null)
/*  997 */               reqRpcParams.put(Integer.valueOf(reqRpcParams.size() + 10000), param);
/*      */             else {
/*  999 */               reqRpcParams.put(Integer.valueOf(p.getIndex()), param);
/*      */             }
/*      */           }
/* 1002 */           if (!param.isIN()) {
/* 1003 */             if (isOneway) {
/* 1004 */               throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), methodName });
/*      */             }
/*      */ 
/* 1007 */             WSDLPart p = getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.OUT);
/* 1008 */             if (p == null)
/* 1009 */               resRpcParams.put(Integer.valueOf(resRpcParams.size() + 10000), param);
/*      */             else
/* 1011 */               resRpcParams.put(Integer.valueOf(p.getIndex()), param);
/*      */           }
/*      */         } else {
/* 1014 */           javaMethod.addParameter(param);
/*      */         }
/*      */       }
/*      */     }
/* 1017 */     for (ParameterImpl p : reqRpcParams.values())
/* 1018 */       requestWrapper.addWrapperChild(p);
/* 1019 */     for (ParameterImpl p : resRpcParams.values())
/* 1020 */       responseWrapper.addWrapperChild(p);
/* 1021 */     processExceptions(javaMethod, method);
/*      */   }
/*      */ 
/*      */   protected void processExceptions(JavaMethodImpl javaMethod, Method method)
/*      */   {
/* 1031 */     Action actionAnn = (Action)method.getAnnotation(Action.class);
/* 1032 */     FaultAction[] faultActions = new FaultAction[0];
/* 1033 */     if (actionAnn != null)
/* 1034 */       faultActions = actionAnn.fault();
/* 1035 */     for (Class exception : method.getExceptionTypes())
/*      */     {
/* 1038 */       if (EXCEPTION_CLASS.isAssignableFrom(exception))
/*      */       {
/* 1040 */         if ((!RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception)) && (!REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception)))
/*      */         {
/* 1045 */           WebFault webFault = (WebFault)getPrivClassAnnotation(exception, WebFault.class);
/* 1046 */           Method faultInfoMethod = getWSDLExceptionFaultInfo(exception);
/* 1047 */           ExceptionType exceptionType = ExceptionType.WSDLException;
/* 1048 */           String namespace = this.targetNamespace;
/* 1049 */           String name = exception.getSimpleName();
/* 1050 */           String beanPackage = this.packageName + ".jaxws.";
/* 1051 */           if (this.packageName.length() == 0)
/* 1052 */             beanPackage = "jaxws.";
/* 1053 */           String className = beanPackage + name + "Bean";
/* 1054 */           String messageName = exception.getSimpleName();
/* 1055 */           if (webFault != null) {
/* 1056 */             if (webFault.faultBean().length() > 0)
/* 1057 */               className = webFault.faultBean();
/* 1058 */             if (webFault.name().length() > 0)
/* 1059 */               name = webFault.name();
/* 1060 */             if (webFault.targetNamespace().length() > 0)
/* 1061 */               namespace = webFault.targetNamespace();
/* 1062 */             if (webFault.messageName().length() > 0)
/* 1063 */               messageName = webFault.messageName();
/*      */           }
/*      */           Annotation[] anns;
/*      */           Class exceptionBean;
/*      */           Annotation[] anns;
/* 1065 */           if (faultInfoMethod == null) {
/* 1066 */             Class exceptionBean = getExceptionBeanClass(className, exception, name, namespace);
/* 1067 */             exceptionType = ExceptionType.UserDefined;
/* 1068 */             anns = exceptionBean.getAnnotations();
/*      */           } else {
/* 1070 */             exceptionBean = faultInfoMethod.getReturnType();
/* 1071 */             anns = faultInfoMethod.getAnnotations();
/*      */           }
/* 1073 */           QName faultName = new QName(namespace, name);
/* 1074 */           TypeReference typeRef = new TypeReference(faultName, exceptionBean, anns);
/* 1075 */           CheckedExceptionImpl checkedException = new CheckedExceptionImpl(javaMethod, exception, typeRef, exceptionType);
/*      */ 
/* 1077 */           checkedException.setMessageName(messageName);
/* 1078 */           for (FaultAction fa : faultActions) {
/* 1079 */             if ((fa.className().equals(exception)) && (!fa.value().equals(""))) {
/* 1080 */               checkedException.setFaultAction(fa.value());
/* 1081 */               break;
/*      */             }
/*      */           }
/* 1084 */           javaMethod.addException(checkedException);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Method getWSDLExceptionFaultInfo(Class exception)
/*      */   {
/* 1096 */     if (!exception.isAnnotationPresent(WebFault.class))
/* 1097 */       return null;
/*      */     try {
/* 1099 */       return exception.getMethod("getFaultInfo", new Class[0]); } catch (NoSuchMethodException e) {
/*      */     }
/* 1101 */     return null;
/*      */   }
/*      */ 
/*      */   protected void processDocBareMethod(JavaMethodImpl javaMethod, String operationName, Method method)
/*      */   {
/* 1114 */     String resultName = operationName + "Response";
/* 1115 */     String resultTNS = this.targetNamespace;
/* 1116 */     String resultPartName = null;
/* 1117 */     boolean isResultHeader = false;
/* 1118 */     WebResult webResult = (WebResult)method.getAnnotation(WebResult.class);
/* 1119 */     if (webResult != null) {
/* 1120 */       if (webResult.name().length() > 0)
/* 1121 */         resultName = webResult.name();
/* 1122 */       if (webResult.targetNamespace().length() > 0)
/* 1123 */         resultTNS = webResult.targetNamespace();
/* 1124 */       resultPartName = webResult.partName();
/* 1125 */       isResultHeader = webResult.header();
/*      */     }
/*      */ 
/* 1128 */     Class returnType = method.getReturnType();
/*      */ 
/* 1130 */     if (javaMethod.isAsync()) {
/* 1131 */       returnType = getAsyncReturnType(method, returnType);
/*      */     }
/*      */ 
/* 1134 */     if ((returnType != null) && (!returnType.getName().equals("void"))) {
/* 1135 */       Annotation[] rann = method.getAnnotations();
/* 1136 */       if (resultName != null) {
/* 1137 */         QName responseQName = new QName(resultTNS, resultName);
/* 1138 */         TypeReference rTypeReference = new TypeReference(responseQName, returnType, rann);
/* 1139 */         ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
/*      */ 
/* 1141 */         if ((resultPartName == null) || (resultPartName.length() == 0)) {
/* 1142 */           resultPartName = resultName;
/*      */         }
/* 1144 */         returnParameter.setPartName(resultPartName);
/* 1145 */         if (isResultHeader) {
/* 1146 */           returnParameter.setBinding(ParameterBinding.HEADER);
/*      */         } else {
/* 1148 */           ParameterBinding rb = getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
/* 1149 */           returnParameter.setBinding(rb);
/*      */         }
/* 1151 */         javaMethod.addParameter(returnParameter);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1156 */     Class[] parameterTypes = method.getParameterTypes();
/* 1157 */     Type[] genericParameterTypes = method.getGenericParameterTypes();
/* 1158 */     Annotation[][] pannotations = getPrivParameterAnnotations(method);
/* 1159 */     int pos = 0;
/* 1160 */     for (Class clazzType : parameterTypes) {
/* 1161 */       String paramName = operationName;
/* 1162 */       String partName = null;
/* 1163 */       String requestNamespace = this.targetNamespace;
/* 1164 */       boolean isHeader = false;
/*      */ 
/* 1167 */       if ((!javaMethod.isAsync()) || (!AsyncHandler.class.isAssignableFrom(clazzType)))
/*      */       {
/* 1171 */         boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
/*      */ 
/* 1173 */         if ((isHolder) && 
/* 1174 */           (clazzType == Holder.class)) {
/* 1175 */           clazzType = Navigator.REFLECTION.erasure(((java.lang.reflect.ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
/*      */         }
/*      */ 
/* 1178 */         WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
/* 1179 */         for (Annotation annotation : pannotations[pos]) {
/* 1180 */           if (annotation.annotationType() == WebParam.class) {
/* 1181 */             WebParam webParam = (WebParam)annotation;
/* 1182 */             paramMode = webParam.mode();
/* 1183 */             if ((isHolder) && (paramMode == WebParam.Mode.IN))
/* 1184 */               paramMode = WebParam.Mode.INOUT;
/* 1185 */             isHeader = webParam.header();
/* 1186 */             if (isHeader)
/* 1187 */               paramName = "arg" + pos;
/* 1188 */             if ((paramMode == WebParam.Mode.OUT) && (!isHeader))
/* 1189 */               paramName = operationName + "Response";
/* 1190 */             if (webParam.name().length() > 0)
/* 1191 */               paramName = webParam.name();
/* 1192 */             partName = webParam.partName();
/* 1193 */             if (webParam.targetNamespace().equals("")) break;
/* 1194 */             requestNamespace = webParam.targetNamespace(); break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1200 */         QName requestQName = new QName(requestNamespace, paramName);
/*      */ 
/* 1202 */         TypeReference typeRef = new TypeReference(requestQName, clazzType, pannotations[pos]);
/*      */ 
/* 1206 */         ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
/* 1207 */         if ((partName == null) || (partName.length() == 0)) {
/* 1208 */           partName = paramName;
/*      */         }
/* 1210 */         param.setPartName(partName);
/* 1211 */         if (paramMode == WebParam.Mode.INOUT) {
/* 1212 */           ParameterBinding pb = getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
/* 1213 */           param.setInBinding(pb);
/* 1214 */           pb = getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
/* 1215 */           param.setOutBinding(pb);
/*      */         }
/* 1217 */         else if (isHeader) {
/* 1218 */           param.setBinding(ParameterBinding.HEADER);
/*      */         } else {
/* 1220 */           ParameterBinding pb = getBinding(operationName, partName, false, paramMode);
/* 1221 */           param.setBinding(pb);
/*      */         }
/*      */ 
/* 1224 */         javaMethod.addParameter(param);
/*      */       }
/*      */     }
/* 1226 */     validateDocBare(javaMethod);
/* 1227 */     processExceptions(javaMethod, method);
/*      */   }
/*      */ 
/*      */   private void validateDocBare(JavaMethodImpl javaMethod)
/*      */   {
/* 1237 */     int numInBodyBindings = 0;
/* 1238 */     for (Parameter param : javaMethod.getRequestParameters()) {
/* 1239 */       if ((param.getBinding().equals(ParameterBinding.BODY)) && (param.isIN())) {
/* 1240 */         numInBodyBindings++;
/*      */       }
/* 1242 */       if (numInBodyBindings > 1) {
/* 1243 */         throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
/*      */       }
/*      */     }
/*      */ 
/* 1247 */     int numOutBodyBindings = 0;
/* 1248 */     for (Parameter param : javaMethod.getResponseParameters()) {
/* 1249 */       if ((param.getBinding().equals(ParameterBinding.BODY)) && (param.isOUT())) {
/* 1250 */         numOutBodyBindings++;
/*      */       }
/* 1252 */       if (numOutBodyBindings > 1)
/* 1253 */         throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private Class getAsyncReturnType(Method method, Class returnType)
/*      */   {
/* 1259 */     if (Response.class.isAssignableFrom(returnType)) {
/* 1260 */       Type ret = method.getGenericReturnType();
/* 1261 */       return Navigator.REFLECTION.erasure(((java.lang.reflect.ParameterizedType)ret).getActualTypeArguments()[0]);
/*      */     }
/* 1263 */     Type[] types = method.getGenericParameterTypes();
/* 1264 */     Class[] params = method.getParameterTypes();
/* 1265 */     int i = 0;
/* 1266 */     for (Class cls : params) {
/* 1267 */       if (AsyncHandler.class.isAssignableFrom(cls)) {
/* 1268 */         return Navigator.REFLECTION.erasure(((java.lang.reflect.ParameterizedType)types[i]).getActualTypeArguments()[0]);
/*      */       }
/* 1270 */       i++;
/*      */     }
/*      */ 
/* 1273 */     return returnType;
/*      */   }
/*      */ 
/*      */   public static String capitalize(String name)
/*      */   {
/* 1282 */     if ((name == null) || (name.length() == 0)) {
/* 1283 */       return name;
/*      */     }
/* 1285 */     char[] chars = name.toCharArray();
/* 1286 */     chars[0] = Character.toUpperCase(chars[0]);
/* 1287 */     return new String(chars);
/*      */   }
/*      */ 
/*      */   public static QName getServiceName(Class<?> implClass)
/*      */   {
/* 1299 */     if (implClass.isInterface()) {
/* 1300 */       throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", new Object[] { implClass.getCanonicalName() });
/*      */     }
/*      */ 
/* 1304 */     String name = implClass.getSimpleName() + "Service";
/* 1305 */     String packageName = "";
/* 1306 */     if (implClass.getPackage() != null) {
/* 1307 */       packageName = implClass.getPackage().getName();
/*      */     }
/* 1309 */     WebService webService = (WebService)implClass.getAnnotation(WebService.class);
/* 1310 */     if (webService == null) {
/* 1311 */       throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implClass.getCanonicalName() });
/*      */     }
/*      */ 
/* 1314 */     if (webService.serviceName().length() > 0) {
/* 1315 */       name = webService.serviceName();
/*      */     }
/* 1317 */     String targetNamespace = getNamespace(packageName);
/* 1318 */     if (webService.targetNamespace().length() > 0)
/* 1319 */       targetNamespace = webService.targetNamespace();
/* 1320 */     else if (targetNamespace == null) {
/* 1321 */       throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { implClass.getName() });
/*      */     }
/*      */ 
/* 1327 */     return new QName(targetNamespace, name);
/*      */   }
/*      */ 
/*      */   public static QName getPortName(Class<?> implClass, String targetNamespace)
/*      */   {
/* 1337 */     WebService webService = (WebService)implClass.getAnnotation(WebService.class);
/* 1338 */     if (webService == null)
/* 1339 */       throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implClass.getCanonicalName() });
/*      */     String name;
/*      */     String name;
/* 1343 */     if (webService.portName().length() > 0) {
/* 1344 */       name = webService.portName();
/*      */     }
/*      */     else
/*      */     {
/*      */       String name;
/* 1345 */       if (webService.name().length() > 0)
/* 1346 */         name = webService.name() + "Port";
/*      */       else {
/* 1348 */         name = implClass.getSimpleName() + "Port";
/*      */       }
/*      */     }
/* 1351 */     if (targetNamespace == null) {
/* 1352 */       if (webService.targetNamespace().length() > 0) {
/* 1353 */         targetNamespace = webService.targetNamespace();
/*      */       } else {
/* 1355 */         String packageName = null;
/* 1356 */         if (implClass.getPackage() != null) {
/* 1357 */           packageName = implClass.getPackage().getName();
/*      */         }
/* 1359 */         targetNamespace = getNamespace(packageName);
/* 1360 */         if (targetNamespace == null) {
/* 1361 */           throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { implClass.getName() });
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1368 */     return new QName(targetNamespace, name);
/*      */   }
/*      */ 
/*      */   public static QName getPortTypeName(Class<?> implOrSeiClass)
/*      */   {
/* 1377 */     assert (implOrSeiClass != null);
/* 1378 */     Class clazz = implOrSeiClass;
/* 1379 */     if (!implOrSeiClass.isAnnotationPresent(WebService.class)) {
/* 1380 */       throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implOrSeiClass.getCanonicalName() });
/*      */     }
/*      */ 
/* 1383 */     if (!implOrSeiClass.isInterface()) {
/* 1384 */       WebService webService = (WebService)implOrSeiClass.getAnnotation(WebService.class);
/* 1385 */       String epi = webService.endpointInterface();
/* 1386 */       if (epi.length() > 0) {
/*      */         try {
/* 1388 */           clazz = Thread.currentThread().getContextClassLoader().loadClass(epi);
/*      */         } catch (ClassNotFoundException e) {
/* 1390 */           throw new RuntimeModelerException("runtime.modeler.class.not.found", new Object[] { epi });
/*      */         }
/* 1392 */         if (!clazz.isAnnotationPresent(WebService.class)) {
/* 1393 */           throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() });
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1399 */     WebService webService = (WebService)clazz.getAnnotation(WebService.class);
/* 1400 */     String name = webService.name();
/* 1401 */     if (name.length() == 0) {
/* 1402 */       name = clazz.getSimpleName();
/*      */     }
/*      */ 
/* 1405 */     String tns = webService.targetNamespace();
/* 1406 */     if (tns.length() == 0)
/* 1407 */       tns = getNamespace(clazz.getPackage().getName());
/* 1408 */     if (tns == null) {
/* 1409 */       throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { clazz.getName() });
/*      */     }
/* 1411 */     return new QName(tns, name);
/*      */   }
/*      */ 
/*      */   private ParameterBinding getBinding(String operation, String part, boolean isHeader, WebParam.Mode mode) {
/* 1415 */     if (this.binding == null) {
/* 1416 */       if (isHeader) {
/* 1417 */         return ParameterBinding.HEADER;
/*      */       }
/* 1419 */       return ParameterBinding.BODY;
/*      */     }
/* 1421 */     QName opName = new QName(this.binding.getBinding().getPortType().getName().getNamespaceURI(), operation);
/* 1422 */     return this.binding.getBinding().getBinding(opName, part, mode);
/*      */   }
/*      */ 
/*      */   private WSDLPart getPart(QName opName, String partName, WebParam.Mode mode) {
/* 1426 */     if (this.binding != null) {
/* 1427 */       WSDLBoundOperationImpl bo = this.binding.getBinding().get(opName);
/* 1428 */       if (bo != null)
/* 1429 */         return bo.getPart(partName, mode);
/*      */     }
/* 1431 */     return null;
/*      */   }
/*      */ 
/*      */   private static QName getReturnQName(Method method, WebResult webResult, XmlElement xmlElem) {
/* 1435 */     String webResultName = null;
/* 1436 */     if ((webResult != null) && (webResult.name().length() > 0)) {
/* 1437 */       webResultName = webResult.name();
/*      */     }
/* 1439 */     String xmlElemName = null;
/* 1440 */     if ((xmlElem != null) && (!xmlElem.name().equals("##default"))) {
/* 1441 */       xmlElemName = xmlElem.name();
/*      */     }
/* 1443 */     if ((xmlElemName != null) && (webResultName != null) && (!xmlElemName.equals(webResultName))) {
/* 1444 */       throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebResult(name)=" + webResultName + " are different for method " + method, new Object[0]);
/*      */     }
/* 1446 */     String localPart = "return";
/* 1447 */     if (webResultName != null)
/* 1448 */       localPart = webResultName;
/* 1449 */     else if (xmlElemName != null) {
/* 1450 */       localPart = xmlElemName;
/*      */     }
/*      */ 
/* 1453 */     String webResultNS = null;
/* 1454 */     if ((webResult != null) && (webResult.targetNamespace().length() > 0)) {
/* 1455 */       webResultNS = webResult.targetNamespace();
/*      */     }
/* 1457 */     String xmlElemNS = null;
/* 1458 */     if ((xmlElem != null) && (!xmlElem.namespace().equals("##default"))) {
/* 1459 */       xmlElemNS = xmlElem.namespace();
/*      */     }
/* 1461 */     if ((xmlElemNS != null) && (webResultNS != null) && (!xmlElemNS.equals(webResultNS))) {
/* 1462 */       throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebResult(targetNamespace)=" + webResultNS + " are different for method " + method, new Object[0]);
/*      */     }
/* 1464 */     String ns = "";
/* 1465 */     if (webResultNS != null)
/* 1466 */       ns = webResultNS;
/* 1467 */     else if (xmlElemNS != null) {
/* 1468 */       ns = xmlElemNS;
/*      */     }
/*      */ 
/* 1471 */     return new QName(ns, localPart);
/*      */   }
/*      */ 
/*      */   private static QName getParameterQName(Method method, WebParam webParam, XmlElement xmlElem, String paramDefault) {
/* 1475 */     String webParamName = null;
/* 1476 */     if ((webParam != null) && (webParam.name().length() > 0)) {
/* 1477 */       webParamName = webParam.name();
/*      */     }
/* 1479 */     String xmlElemName = null;
/* 1480 */     if ((xmlElem != null) && (!xmlElem.name().equals("##default"))) {
/* 1481 */       xmlElemName = xmlElem.name();
/*      */     }
/* 1483 */     if ((xmlElemName != null) && (webParamName != null) && (!xmlElemName.equals(webParamName))) {
/* 1484 */       throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebParam(name)=" + webParamName + " are different for method " + method, new Object[0]);
/*      */     }
/* 1486 */     String localPart = paramDefault;
/* 1487 */     if (webParamName != null)
/* 1488 */       localPart = webParamName;
/* 1489 */     else if (xmlElemName != null) {
/* 1490 */       localPart = xmlElemName;
/*      */     }
/*      */ 
/* 1493 */     String webParamNS = null;
/* 1494 */     if ((webParam != null) && (webParam.targetNamespace().length() > 0)) {
/* 1495 */       webParamNS = webParam.targetNamespace();
/*      */     }
/* 1497 */     String xmlElemNS = null;
/* 1498 */     if ((xmlElem != null) && (!xmlElem.namespace().equals("##default"))) {
/* 1499 */       xmlElemNS = xmlElem.namespace();
/*      */     }
/* 1501 */     if ((xmlElemNS != null) && (webParamNS != null) && (!xmlElemNS.equals(webParamNS))) {
/* 1502 */       throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebParam(targetNamespace)=" + webParamNS + " are different for method " + method, new Object[0]);
/*      */     }
/* 1504 */     String ns = "";
/* 1505 */     if (webParamNS != null)
/* 1506 */       ns = webParamNS;
/* 1507 */     else if (xmlElemNS != null) {
/* 1508 */       ns = xmlElemNS;
/*      */     }
/*      */ 
/* 1511 */     return new QName(ns, localPart);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.model.RuntimeModeler
 * JD-Core Version:    0.6.2
 */