/*      */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*      */ 
/*      */ import com.sun.istack.internal.NotNull;
/*      */ import com.sun.istack.internal.Nullable;
/*      */ import com.sun.istack.internal.SAXParseException2;
/*      */ import com.sun.xml.internal.bind.IDResolver;
/*      */ import com.sun.xml.internal.bind.api.AccessorException;
/*      */ import com.sun.xml.internal.bind.api.ClassResolver;
/*      */ import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
/*      */ import com.sun.xml.internal.bind.v2.ClassFactory;
/*      */ import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
/*      */ import com.sun.xml.internal.bind.v2.runtime.Coordinator;
/*      */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*      */ import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.Callable;
/*      */ import javax.xml.bind.JAXBElement;
/*      */ import javax.xml.bind.UnmarshalException;
/*      */ import javax.xml.bind.ValidationEvent;
/*      */ import javax.xml.bind.ValidationEventHandler;
/*      */ import javax.xml.bind.ValidationEventLocator;
/*      */ import javax.xml.bind.helpers.ValidationEventImpl;
/*      */ import javax.xml.namespace.NamespaceContext;
/*      */ import javax.xml.namespace.QName;
/*      */ import org.xml.sax.ErrorHandler;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.helpers.LocatorImpl;
/*      */ 
/*      */ public final class UnmarshallingContext extends Coordinator
/*      */   implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, XmlVisitor.TextPredictor
/*      */ {
/*      */   private final State root;
/*      */   private State current;
/*   97 */   private static final LocatorEx DUMMY_INSTANCE = new LocatorExWrapper(loc);
/*      */ 
/*      */   @NotNull
/*  100 */   private LocatorEx locator = DUMMY_INSTANCE;
/*      */   private Object result;
/*      */   private JaxBeanInfo expectedType;
/*      */   private IDResolver idResolver;
/*  129 */   private boolean isUnmarshalInProgress = true;
/*  130 */   private boolean aborted = false;
/*      */   public final UnmarshallerImpl parent;
/*      */   private final AssociationMap assoc;
/*      */   private boolean isInplaceMode;
/*      */   private InfosetScanner scanner;
/*      */   private Object currentElement;
/*      */   private NamespaceContext environmentNamespaceContext;
/*      */ 
/*      */   @Nullable
/*      */   public ClassResolver classResolver;
/*      */ 
/*      */   @Nullable
/*      */   public ClassLoader classLoader;
/*      */   private final Map<Class, Factory> factories;
/*      */   private Patcher[] patchers;
/*      */   private int patchersLen;
/*      */   private String[] nsBind;
/*      */   private int nsLen;
/*      */   private Scope[] scopes;
/*      */   private int scopeTop;
/* 1022 */   private static final Loader DEFAULT_ROOT_LOADER = new DefaultRootLoader(null);
/* 1023 */   private static final Loader EXPECTED_TYPE_ROOT_LOADER = new ExpectedTypeRootLoader(null);
/*      */ 
/*      */   public UnmarshallingContext(UnmarshallerImpl _parent, AssociationMap assoc)
/*      */   {
/*  408 */     this.factories = new HashMap();
/*      */ 
/*  716 */     this.patchers = null;
/*  717 */     this.patchersLen = 0;
/*      */ 
/*  801 */     this.nsBind = new String[16];
/*  802 */     this.nsLen = 0;
/*      */ 
/*  938 */     this.scopes = new Scope[16];
/*      */ 
/*  942 */     this.scopeTop = 0;
/*      */ 
/*  945 */     for (int i = 0; i < this.scopes.length; i++)
/*  946 */       this.scopes[i] = new Scope(this);
/*  322 */     this.parent = _parent;
/*  323 */     this.assoc = assoc;
/*  324 */     this.root = (this.current = new State(null, null));
/*  325 */     allocateMoreStates();
/*      */   }
/*      */ 
/*      */   public void reset(InfosetScanner scanner, boolean isInplaceMode, JaxBeanInfo expectedType, IDResolver idResolver) {
/*  329 */     this.scanner = scanner;
/*  330 */     this.isInplaceMode = isInplaceMode;
/*  331 */     this.expectedType = expectedType;
/*  332 */     this.idResolver = idResolver;
/*      */   }
/*      */ 
/*      */   public JAXBContextImpl getJAXBContext() {
/*  336 */     return this.parent.context;
/*      */   }
/*      */ 
/*      */   public State getCurrentState() {
/*  340 */     return this.current;
/*      */   }
/*      */ 
/*      */   public Loader selectRootLoader(State state, TagName tag)
/*      */     throws SAXException
/*      */   {
/*      */     try
/*      */     {
/*  352 */       Loader l = getJAXBContext().selectRootLoader(state, tag);
/*  353 */       if (l != null) return l;
/*      */ 
/*  355 */       if (this.classResolver != null) {
/*  356 */         Class clazz = this.classResolver.resolveElementName(tag.uri, tag.local);
/*  357 */         if (clazz != null) {
/*  358 */           JAXBContextImpl enhanced = getJAXBContext().createAugmented(clazz);
/*  359 */           JaxBeanInfo bi = enhanced.getBeanInfo(clazz);
/*  360 */           return bi.getLoader(enhanced, true);
/*      */         }
/*      */       }
/*      */     } catch (RuntimeException e) {
/*  364 */       throw e;
/*      */     } catch (Exception e) {
/*  366 */       handleError(e);
/*      */     }
/*      */ 
/*  369 */     return null;
/*      */   }
/*      */ 
/*      */   private void allocateMoreStates()
/*      */   {
/*  381 */     assert (this.current.next == null);
/*      */ 
/*  383 */     State s = this.current;
/*  384 */     for (int i = 0; i < 8; i++)
/*  385 */       s = new State(s, null);
/*      */   }
/*      */ 
/*      */   public void clearStates() {
/*  389 */     State last = this.current;
/*  390 */     while (last.next != null) last = last.next;
/*  391 */     while (last.prev != null) {
/*  392 */       last.loader = null;
/*  393 */       last.nil = false;
/*  394 */       last.receiver = null;
/*  395 */       last.intercepter = null;
/*  396 */       last.elementDefaultValue = null;
/*  397 */       last.target = null;
/*  398 */       last = last.prev;
/*  399 */       last.next.prev = null;
/*  400 */       last.next = null;
/*      */     }
/*  402 */     this.current = last;
/*      */   }
/*      */ 
/*      */   public void setFactories(Object factoryInstances)
/*      */   {
/*  411 */     this.factories.clear();
/*  412 */     if (factoryInstances == null) {
/*  413 */       return;
/*      */     }
/*  415 */     if ((factoryInstances instanceof Object[]))
/*  416 */       for (Object factory : (Object[])factoryInstances)
/*      */       {
/*  418 */         addFactory(factory);
/*      */       }
/*      */     else
/*  421 */       addFactory(factoryInstances);
/*      */   }
/*      */ 
/*      */   private void addFactory(Object factory)
/*      */   {
/*  426 */     for (Method m : factory.getClass().getMethods())
/*      */     {
/*  428 */       if (m.getName().startsWith("create"))
/*      */       {
/*  430 */         if (m.getParameterTypes().length <= 0)
/*      */         {
/*  433 */           Class type = m.getReturnType();
/*      */ 
/*  435 */           this.factories.put(type, new Factory(factory, m)); } 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*  440 */   public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException { if (locator != null)
/*  441 */       this.locator = locator;
/*  442 */     this.environmentNamespaceContext = nsContext;
/*      */ 
/*  444 */     this.result = null;
/*  445 */     this.current = this.root;
/*      */ 
/*  447 */     this.patchersLen = 0;
/*  448 */     this.aborted = false;
/*  449 */     this.isUnmarshalInProgress = true;
/*  450 */     this.nsLen = 0;
/*      */ 
/*  452 */     setThreadAffinity();
/*      */ 
/*  454 */     if (this.expectedType != null)
/*  455 */       this.root.loader = EXPECTED_TYPE_ROOT_LOADER;
/*      */     else {
/*  457 */       this.root.loader = DEFAULT_ROOT_LOADER;
/*      */     }
/*  459 */     this.idResolver.startDocument(this); }
/*      */ 
/*      */   public void startElement(TagName tagName) throws SAXException
/*      */   {
/*  463 */     pushCoordinator();
/*      */     try {
/*  465 */       _startElement(tagName);
/*      */     } finally {
/*  467 */       popCoordinator();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void _startElement(TagName tagName)
/*      */     throws SAXException
/*      */   {
/*  476 */     if (this.assoc != null) {
/*  477 */       this.currentElement = this.scanner.getCurrentElement();
/*      */     }
/*  479 */     Loader h = this.current.loader;
/*  480 */     this.current.push();
/*      */ 
/*  483 */     h.childElement(this.current, tagName);
/*  484 */     assert (this.current.loader != null);
/*      */ 
/*  486 */     this.current.loader.startElement(this.current, tagName);
/*      */   }
/*      */ 
/*      */   public void text(CharSequence pcdata) throws SAXException {
/*  490 */     State cur = this.current;
/*  491 */     pushCoordinator();
/*      */     try {
/*  493 */       if ((cur.elementDefaultValue != null) && 
/*  494 */         (pcdata.length() == 0))
/*      */       {
/*  496 */         pcdata = cur.elementDefaultValue;
/*      */       }
/*      */ 
/*  499 */       cur.loader.text(cur, pcdata);
/*      */     } finally {
/*  501 */       popCoordinator();
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void endElement(TagName tagName) throws SAXException {
/*  506 */     pushCoordinator();
/*      */     try {
/*  508 */       State child = this.current;
/*      */ 
/*  511 */       child.loader.leaveElement(child, tagName);
/*      */ 
/*  514 */       Object target = child.target;
/*  515 */       Receiver recv = child.receiver;
/*  516 */       Intercepter intercepter = child.intercepter;
/*  517 */       child.pop();
/*      */ 
/*  520 */       if (intercepter != null)
/*  521 */         target = intercepter.intercept(this.current, target);
/*  522 */       if (recv != null)
/*  523 */         recv.receive(this.current, target);
/*      */     } finally {
/*  525 */       popCoordinator();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endDocument() throws SAXException {
/*  530 */     runPatchers();
/*  531 */     this.idResolver.endDocument();
/*      */ 
/*  533 */     this.isUnmarshalInProgress = false;
/*  534 */     this.currentElement = null;
/*  535 */     this.locator = DUMMY_INSTANCE;
/*  536 */     this.environmentNamespaceContext = null;
/*      */ 
/*  539 */     assert (this.root == this.current);
/*      */ 
/*  541 */     resetThreadAffinity();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean expectText()
/*      */   {
/*  549 */     return this.current.loader.expectText;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public XmlVisitor.TextPredictor getPredictor()
/*      */   {
/*  557 */     return this;
/*      */   }
/*      */ 
/*      */   public UnmarshallingContext getContext() {
/*  561 */     return this;
/*      */   }
/*      */ 
/*      */   public Object getResult()
/*      */     throws UnmarshalException
/*      */   {
/*  568 */     if (this.isUnmarshalInProgress) {
/*  569 */       throw new IllegalStateException();
/*      */     }
/*  571 */     if (!this.aborted) return this.result;
/*      */ 
/*  574 */     throw new UnmarshalException((String)null);
/*      */   }
/*      */ 
/*      */   void clearResult() {
/*  578 */     if (this.isUnmarshalInProgress) {
/*  579 */       throw new IllegalStateException();
/*      */     }
/*  581 */     this.result = null;
/*      */   }
/*      */ 
/*      */   public Object createInstance(Class<?> clazz)
/*      */     throws SAXException
/*      */   {
/*  589 */     if (!this.factories.isEmpty()) {
/*  590 */       Factory factory = (Factory)this.factories.get(clazz);
/*  591 */       if (factory != null)
/*  592 */         return factory.createInstance();
/*      */     }
/*  594 */     return ClassFactory.create(clazz);
/*      */   }
/*      */ 
/*      */   public Object createInstance(JaxBeanInfo beanInfo)
/*      */     throws SAXException
/*      */   {
/*  602 */     if (!this.factories.isEmpty()) {
/*  603 */       Factory factory = (Factory)this.factories.get(beanInfo.jaxbType);
/*  604 */       if (factory != null)
/*  605 */         return factory.createInstance();
/*      */     }
/*      */     try {
/*  608 */       return beanInfo.createInstance(this);
/*      */     } catch (IllegalAccessException e) {
/*  610 */       Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e, false);
/*      */     } catch (InvocationTargetException e) {
/*  612 */       Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e, false);
/*      */     } catch (InstantiationException e) {
/*  614 */       Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e, false);
/*      */     }
/*  616 */     return null;
/*      */   }
/*      */ 
/*      */   public void handleEvent(ValidationEvent event, boolean canRecover)
/*      */     throws SAXException
/*      */   {
/*  638 */     ValidationEventHandler eventHandler = this.parent.getEventHandler();
/*      */ 
/*  640 */     boolean recover = eventHandler.handleEvent(event);
/*      */ 
/*  644 */     if (!recover) this.aborted = true;
/*      */ 
/*  646 */     if ((!canRecover) || (!recover))
/*  647 */       throw new SAXParseException2(event.getMessage(), this.locator, new UnmarshalException(event.getMessage(), event.getLinkedException()));
/*      */   }
/*      */ 
/*      */   public boolean handleEvent(ValidationEvent event)
/*      */   {
/*      */     try
/*      */     {
/*  656 */       boolean recover = this.parent.getEventHandler().handleEvent(event);
/*  657 */       if (!recover) this.aborted = true;
/*  658 */       return recover;
/*      */     }
/*      */     catch (RuntimeException re) {
/*      */     }
/*  662 */     return false;
/*      */   }
/*      */ 
/*      */   public void handleError(Exception e)
/*      */     throws SAXException
/*      */   {
/*  672 */     handleError(e, true);
/*      */   }
/*      */ 
/*      */   public void handleError(Exception e, boolean canRecover) throws SAXException {
/*  676 */     handleEvent(new ValidationEventImpl(1, e.getMessage(), this.locator.getLocation(), e), canRecover);
/*      */   }
/*      */ 
/*      */   public void handleError(String msg) {
/*  680 */     handleEvent(new ValidationEventImpl(1, msg, this.locator.getLocation()));
/*      */   }
/*      */ 
/*      */   protected ValidationEventLocator getLocation() {
/*  684 */     return this.locator.getLocation();
/*      */   }
/*      */ 
/*      */   public LocatorEx getLocator()
/*      */   {
/*  693 */     return this.locator;
/*      */   }
/*      */ 
/*      */   public void errorUnresolvedIDREF(Object bean, String idref, LocatorEx loc)
/*      */     throws SAXException
/*      */   {
/*  699 */     handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(new Object[] { idref }), loc.getLocation()), true);
/*      */   }
/*      */ 
/*      */   public void addPatcher(Patcher job)
/*      */   {
/*  729 */     if (this.patchers == null)
/*  730 */       this.patchers = new Patcher[32];
/*  731 */     if (this.patchers.length == this.patchersLen) {
/*  732 */       Patcher[] buf = new Patcher[this.patchersLen * 2];
/*  733 */       System.arraycopy(this.patchers, 0, buf, 0, this.patchersLen);
/*  734 */       this.patchers = buf;
/*      */     }
/*  736 */     this.patchers[(this.patchersLen++)] = job;
/*      */   }
/*      */ 
/*      */   private void runPatchers() throws SAXException
/*      */   {
/*  741 */     if (this.patchers != null)
/*  742 */       for (int i = 0; i < this.patchersLen; i++) {
/*  743 */         this.patchers[i].run();
/*  744 */         this.patchers[i] = null;
/*      */       }
/*      */   }
/*      */ 
/*      */   public String addToIdTable(String id)
/*      */     throws SAXException
/*      */   {
/*  776 */     Object o = this.current.target;
/*  777 */     if (o == null)
/*  778 */       o = this.current.prev.target;
/*  779 */     this.idResolver.bind(id, o);
/*  780 */     return id;
/*      */   }
/*      */ 
/*      */   public Callable getObjectFromId(String id, Class targetType)
/*      */     throws SAXException
/*      */   {
/*  793 */     return this.idResolver.resolve(id, targetType);
/*      */   }
/*      */ 
/*      */   public void startPrefixMapping(String prefix, String uri)
/*      */   {
/*  805 */     if (this.nsBind.length == this.nsLen)
/*      */     {
/*  807 */       String[] n = new String[this.nsLen * 2];
/*  808 */       System.arraycopy(this.nsBind, 0, n, 0, this.nsLen);
/*  809 */       this.nsBind = n;
/*      */     }
/*  811 */     this.nsBind[(this.nsLen++)] = prefix;
/*  812 */     this.nsBind[(this.nsLen++)] = uri;
/*      */   }
/*      */   public void endPrefixMapping(String prefix) {
/*  815 */     this.nsLen -= 2;
/*      */   }
/*      */   private String resolveNamespacePrefix(String prefix) {
/*  818 */     if (prefix.equals("xml")) {
/*  819 */       return "http://www.w3.org/XML/1998/namespace";
/*      */     }
/*  821 */     for (int i = this.nsLen - 2; i >= 0; i -= 2) {
/*  822 */       if (prefix.equals(this.nsBind[i])) {
/*  823 */         return this.nsBind[(i + 1)];
/*      */       }
/*      */     }
/*  826 */     if (this.environmentNamespaceContext != null)
/*      */     {
/*  828 */       return this.environmentNamespaceContext.getNamespaceURI(prefix.intern());
/*      */     }
/*      */ 
/*  832 */     if (prefix.equals("")) {
/*  833 */       return "";
/*      */     }
/*      */ 
/*  836 */     return null;
/*      */   }
/*      */ 
/*      */   public String[] getNewlyDeclaredPrefixes()
/*      */   {
/*  847 */     return getPrefixList(this.current.prev.numNsDecl);
/*      */   }
/*      */ 
/*      */   public String[] getAllDeclaredPrefixes()
/*      */   {
/*  858 */     return getPrefixList(0);
/*      */   }
/*      */ 
/*      */   private String[] getPrefixList(int startIndex) {
/*  862 */     int size = (this.current.numNsDecl - startIndex) / 2;
/*  863 */     String[] r = new String[size];
/*  864 */     for (int i = 0; i < r.length; i++)
/*  865 */       r[i] = this.nsBind[(startIndex + i * 2)];
/*  866 */     return r;
/*      */   }
/*      */ 
/*      */   public Iterator<String> getPrefixes(String uri)
/*      */   {
/*  875 */     return Collections.unmodifiableList(getAllPrefixesInList(uri)).iterator();
/*      */   }
/*      */ 
/*      */   private List<String> getAllPrefixesInList(String uri)
/*      */   {
/*  880 */     List a = new ArrayList();
/*      */ 
/*  882 */     if (uri == null)
/*  883 */       throw new IllegalArgumentException();
/*  884 */     if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
/*  885 */       a.add("xml");
/*  886 */       return a;
/*      */     }
/*  888 */     if (uri.equals("http://www.w3.org/2000/xmlns/")) {
/*  889 */       a.add("xmlns");
/*  890 */       return a;
/*      */     }
/*      */ 
/*  893 */     for (int i = this.nsLen - 2; i >= 0; i -= 2)
/*  894 */       if ((uri.equals(this.nsBind[(i + 1)])) && 
/*  895 */         (getNamespaceURI(this.nsBind[i]).equals(this.nsBind[(i + 1)])))
/*      */       {
/*  897 */         a.add(this.nsBind[i]);
/*      */       }
/*  899 */     return a;
/*      */   }
/*      */ 
/*      */   public String getPrefix(String uri) {
/*  903 */     if (uri == null)
/*  904 */       throw new IllegalArgumentException();
/*  905 */     if (uri.equals("http://www.w3.org/XML/1998/namespace"))
/*  906 */       return "xml";
/*  907 */     if (uri.equals("http://www.w3.org/2000/xmlns/")) {
/*  908 */       return "xmlns";
/*      */     }
/*  910 */     for (int i = this.nsLen - 2; i >= 0; i -= 2)
/*  911 */       if ((uri.equals(this.nsBind[(i + 1)])) && 
/*  912 */         (getNamespaceURI(this.nsBind[i]).equals(this.nsBind[(i + 1)])))
/*      */       {
/*  914 */         return this.nsBind[i];
/*      */       }
/*  916 */     if (this.environmentNamespaceContext != null) {
/*  917 */       return this.environmentNamespaceContext.getPrefix(uri);
/*      */     }
/*  919 */     return null;
/*      */   }
/*      */ 
/*      */   public String getNamespaceURI(String prefix) {
/*  923 */     if (prefix == null)
/*  924 */       throw new IllegalArgumentException();
/*  925 */     if (prefix.equals("xmlns")) {
/*  926 */       return "http://www.w3.org/2000/xmlns/";
/*      */     }
/*  928 */     return resolveNamespacePrefix(prefix);
/*      */   }
/*      */ 
/*      */   public void startScope(int frameSize)
/*      */   {
/*  965 */     this.scopeTop += frameSize;
/*      */ 
/*  968 */     if (this.scopeTop >= this.scopes.length) {
/*  969 */       Scope[] s = new Scope[Math.max(this.scopeTop + 1, this.scopes.length * 2)];
/*  970 */       System.arraycopy(this.scopes, 0, s, 0, this.scopes.length);
/*  971 */       for (int i = this.scopes.length; i < s.length; i++)
/*  972 */         s[i] = new Scope(this);
/*  973 */       this.scopes = s;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endScope(int frameSize)
/*      */     throws SAXException
/*      */   {
/*      */     try
/*      */     {
/*  989 */       for (; frameSize > 0; this.scopeTop -= 1) {
/*  990 */         this.scopes[this.scopeTop].finish();
/*      */ 
/*  989 */         frameSize--;
/*      */       }
/*      */     } catch (AccessorException e) {
/*  992 */       handleError(e);
/*      */ 
/*  996 */       for (; frameSize > 0; frameSize--)
/*  997 */         this.scopes[(this.scopeTop--)] = new Scope(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Scope getScope(int offset)
/*      */   {
/* 1011 */     return this.scopes[(this.scopeTop - offset)];
/*      */   }
/*      */ 
/*      */   public void recordInnerPeer(Object innerPeer)
/*      */   {
/* 1117 */     if (this.assoc != null)
/* 1118 */       this.assoc.addInner(this.currentElement, innerPeer);
/*      */   }
/*      */ 
/*      */   public Object getInnerPeer()
/*      */   {
/* 1129 */     if ((this.assoc != null) && (this.isInplaceMode)) {
/* 1130 */       return this.assoc.getInnerPeer(this.currentElement);
/*      */     }
/* 1132 */     return null;
/*      */   }
/*      */ 
/*      */   public void recordOuterPeer(Object outerPeer)
/*      */   {
/* 1143 */     if (this.assoc != null)
/* 1144 */       this.assoc.addOuter(this.currentElement, outerPeer);
/*      */   }
/*      */ 
/*      */   public Object getOuterPeer()
/*      */   {
/* 1155 */     if ((this.assoc != null) && (this.isInplaceMode)) {
/* 1156 */       return this.assoc.getOuterPeer(this.currentElement);
/*      */     }
/* 1158 */     return null;
/*      */   }
/*      */ 
/*      */   public String getXMIMEContentType()
/*      */   {
/* 1177 */     Object t = this.current.target;
/* 1178 */     if (t == null) return null;
/* 1179 */     return getJAXBContext().getXMIMEContentType(t);
/*      */   }
/*      */ 
/*      */   public static UnmarshallingContext getInstance()
/*      */   {
/* 1187 */     return (UnmarshallingContext)Coordinator._getInstance();
/*      */   }
/*      */ 
/*      */   public Collection<QName> getCurrentExpectedElements()
/*      */   {
/* 1197 */     pushCoordinator();
/*      */     try {
/* 1199 */       State s = getCurrentState();
/* 1200 */       Loader l = s.loader;
/* 1201 */       return l != null ? l.getExpectedChildElements() : null;
/*      */     } finally {
/* 1203 */       popCoordinator();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Collection<QName> getCurrentExpectedAttributes()
/*      */   {
/* 1214 */     pushCoordinator();
/*      */     try {
/* 1216 */       State s = getCurrentState();
/* 1217 */       Loader l = s.loader;
/* 1218 */       return l != null ? l.getExpectedAttributes() : null;
/*      */     } finally {
/* 1220 */       popCoordinator();
/*      */     }
/*      */   }
/*      */ 
/*      */   public StructureLoader getStructureLoader()
/*      */   {
/* 1230 */     if ((this.current.loader instanceof StructureLoader)) {
/* 1231 */       return (StructureLoader)this.current.loader;
/*      */     }
/* 1233 */     return null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   92 */     LocatorImpl loc = new LocatorImpl();
/*   93 */     loc.setPublicId(null);
/*   94 */     loc.setSystemId(null);
/*   95 */     loc.setLineNumber(-1);
/*   96 */     loc.setColumnNumber(-1);
/*      */   }
/*      */ 
/*      */   private static final class DefaultRootLoader extends Loader
/*      */     implements Receiver
/*      */   {
/*      */     public void childElement(UnmarshallingContext.State state, TagName ea)
/*      */       throws SAXException
/*      */     {
/* 1036 */       Loader loader = state.getContext().selectRootLoader(state, ea);
/* 1037 */       if (loader != null) {
/* 1038 */         state.loader = loader;
/* 1039 */         state.receiver = this;
/* 1040 */         return;
/*      */       }
/*      */ 
/* 1045 */       JaxBeanInfo beanInfo = XsiTypeLoader.parseXsiType(state, ea, null);
/* 1046 */       if (beanInfo == null)
/*      */       {
/* 1048 */         reportUnexpectedChildElement(ea, false);
/* 1049 */         return;
/*      */       }
/*      */ 
/* 1052 */       state.loader = beanInfo.getLoader(null, false);
/* 1053 */       state.prev.backup = new JAXBElement(ea.createQName(), Object.class, null);
/* 1054 */       state.receiver = this;
/*      */     }
/*      */ 
/*      */     public Collection<QName> getExpectedChildElements()
/*      */     {
/* 1059 */       return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames();
/*      */     }
/*      */ 
/*      */     public void receive(UnmarshallingContext.State state, Object o) {
/* 1063 */       if (state.backup != null) {
/* 1064 */         ((JAXBElement)state.backup).setValue(o);
/* 1065 */         o = state.backup;
/*      */       }
/* 1067 */       if (state.nil) {
/* 1068 */         ((JAXBElement)o).setNil(true);
/*      */       }
/* 1070 */       state.getContext().result = o;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ExpectedTypeRootLoader extends Loader
/*      */     implements Receiver
/*      */   {
/*      */     public void childElement(UnmarshallingContext.State state, TagName ea)
/*      */     {
/* 1085 */       UnmarshallingContext context = state.getContext();
/*      */ 
/* 1088 */       QName qn = new QName(ea.uri, ea.local);
/* 1089 */       state.prev.target = new JAXBElement(qn, context.expectedType.jaxbType, null, null);
/* 1090 */       state.receiver = this;
/*      */ 
/* 1095 */       state.loader = new XsiNilLoader(context.expectedType.getLoader(null, true));
/*      */     }
/*      */ 
/*      */     public void receive(UnmarshallingContext.State state, Object o) {
/* 1099 */       JAXBElement e = (JAXBElement)state.target;
/* 1100 */       e.setValue(o);
/* 1101 */       state.getContext().recordOuterPeer(e);
/* 1102 */       state.getContext().result = e;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Factory
/*      */   {
/*      */     private final Object factorInstance;
/*      */     private final Method method;
/*      */ 
/*      */     public Factory(Object factorInstance, Method method)
/*      */     {
/*  297 */       this.factorInstance = factorInstance;
/*  298 */       this.method = method;
/*      */     }
/*      */ 
/*      */     public Object createInstance() throws SAXException {
/*      */       try {
/*  303 */         return this.method.invoke(this.factorInstance, new Object[0]);
/*      */       } catch (IllegalAccessException e) {
/*  305 */         UnmarshallingContext.getInstance().handleError(e, false);
/*      */       } catch (InvocationTargetException e) {
/*  307 */         UnmarshallingContext.getInstance().handleError(e, false);
/*      */       }
/*  309 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final class State
/*      */   {
/*      */     public Loader loader;
/*      */     public Receiver receiver;
/*      */     public Intercepter intercepter;
/*      */     public Object target;
/*      */     public Object backup;
/*      */     private int numNsDecl;
/*      */     public String elementDefaultValue;
/*      */     public State prev;
/*      */     private State next;
/*  254 */     public boolean nil = false;
/*      */ 
/*      */     public UnmarshallingContext getContext()
/*      */     {
/*  260 */       return UnmarshallingContext.this;
/*      */     }
/*      */ 
/*      */     private State(State prev) {
/*  264 */       this.prev = prev;
/*  265 */       if (prev != null)
/*  266 */         prev.next = this;
/*      */     }
/*      */ 
/*      */     private void push() {
/*  270 */       if (this.next == null)
/*  271 */         UnmarshallingContext.this.allocateMoreStates();
/*  272 */       State n = this.next;
/*  273 */       n.numNsDecl = UnmarshallingContext.this.nsLen;
/*  274 */       UnmarshallingContext.this.current = n;
/*      */     }
/*      */ 
/*      */     private void pop() {
/*  278 */       assert (this.prev != null);
/*  279 */       this.loader = null;
/*  280 */       this.nil = false;
/*  281 */       this.receiver = null;
/*  282 */       this.intercepter = null;
/*  283 */       this.elementDefaultValue = null;
/*  284 */       this.target = null;
/*  285 */       UnmarshallingContext.this.current = this.prev;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext
 * JD-Core Version:    0.6.2
 */