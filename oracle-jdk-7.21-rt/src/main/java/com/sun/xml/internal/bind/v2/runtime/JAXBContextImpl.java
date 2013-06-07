/*      */ package com.sun.xml.internal.bind.v2.runtime;
/*      */ 
/*      */ import com.sun.istack.internal.NotNull;
/*      */ import com.sun.istack.internal.Pool;
/*      */ import com.sun.istack.internal.Pool.Impl;
/*      */ import com.sun.xml.internal.bind.api.AccessorException;
/*      */ import com.sun.xml.internal.bind.api.Bridge;
/*      */ import com.sun.xml.internal.bind.api.BridgeContext;
/*      */ import com.sun.xml.internal.bind.api.CompositeStructure;
/*      */ import com.sun.xml.internal.bind.api.ErrorListener;
/*      */ import com.sun.xml.internal.bind.api.JAXBRIContext;
/*      */ import com.sun.xml.internal.bind.api.RawAccessor;
/*      */ import com.sun.xml.internal.bind.api.TypeReference;
/*      */ import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
/*      */ import com.sun.xml.internal.bind.util.Which;
/*      */ import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
/*      */ import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
/*      */ import com.sun.xml.internal.bind.v2.model.core.Adapter;
/*      */ import com.sun.xml.internal.bind.v2.model.core.NonElement;
/*      */ import com.sun.xml.internal.bind.v2.model.core.Ref;
/*      */ import com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
/*      */ import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
/*      */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*      */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
/*      */ import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
/*      */ import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
/*      */ import com.sun.xml.internal.bind.v2.runtime.property.Property;
/*      */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*      */ import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
/*      */ import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
/*      */ import com.sun.xml.internal.bind.v2.util.EditDistance;
/*      */ import com.sun.xml.internal.bind.v2.util.QNameMap;
/*      */ import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
/*      */ import com.sun.xml.internal.txw2.output.ResultFactory;
/*      */ import java.io.IOException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import javax.xml.bind.Binder;
/*      */ import javax.xml.bind.JAXBElement;
/*      */ import javax.xml.bind.JAXBException;
/*      */ import javax.xml.bind.JAXBIntrospector;
/*      */ import javax.xml.bind.Marshaller;
/*      */ import javax.xml.bind.SchemaOutputResolver;
/*      */ import javax.xml.bind.Unmarshaller;
/*      */ import javax.xml.bind.Validator;
/*      */ import javax.xml.bind.annotation.XmlAttachmentRef;
/*      */ import javax.xml.bind.annotation.XmlList;
/*      */ import javax.xml.bind.annotation.XmlNs;
/*      */ import javax.xml.bind.annotation.XmlSchema;
/*      */ import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.FactoryConfigurationError;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.transform.Result;
/*      */ import javax.xml.transform.Transformer;
/*      */ import javax.xml.transform.TransformerConfigurationException;
/*      */ import javax.xml.transform.TransformerFactory;
/*      */ import javax.xml.transform.sax.SAXResult;
/*      */ import javax.xml.transform.sax.SAXTransformerFactory;
/*      */ import javax.xml.transform.sax.TransformerHandler;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Node;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXParseException;
/*      */ import org.xml.sax.helpers.DefaultHandler;
/*      */ 
/*      */ public final class JAXBContextImpl extends JAXBRIContext
/*      */ {
/*  131 */   private final Map<TypeReference, Bridge> bridges = new LinkedHashMap();
/*      */   private static volatile SAXTransformerFactory tf;
/*      */   private static DocumentBuilder db;
/*  147 */   private final QNameMap<JaxBeanInfo> rootMap = new QNameMap();
/*  148 */   private final HashMap<QName, JaxBeanInfo> typeMap = new HashMap();
/*      */ 
/*  153 */   private final Map<Class, JaxBeanInfo> beanInfoMap = new LinkedHashMap();
/*      */ 
/*  164 */   protected Map<RuntimeTypeInfo, JaxBeanInfo> beanInfos = new LinkedHashMap();
/*      */ 
/*  166 */   private final Map<Class, Map<QName, ElementBeanInfoImpl>> elements = new LinkedHashMap();
/*      */ 
/*  171 */   public final Pool<Marshaller> marshallerPool = new Pool.Impl() {
/*  173 */     @NotNull
/*      */     protected Marshaller create() { return JAXBContextImpl.this.createMarshaller(); }
/*      */ 
/*  171 */   };
/*      */ 
/*  177 */   public final Pool<Unmarshaller> unmarshallerPool = new Pool.Impl() {
/*  179 */     @NotNull
/*      */     protected Unmarshaller create() { return JAXBContextImpl.this.createUnmarshaller(); }
/*      */ 
/*  177 */   };
/*      */ 
/*  187 */   public NameBuilder nameBuilder = new NameBuilder();
/*      */   public final NameList nameList;
/*      */   private final String defaultNsUri;
/*      */   private final Class[] classes;
/*      */   protected final boolean c14nSupport;
/*      */   public final boolean xmlAccessorFactorySupport;
/*      */   public final boolean allNillable;
/*      */   public final boolean retainPropertyInfo;
/*      */   public final boolean supressAccessorWarnings;
/*      */   public final boolean improvedXsiTypeHandling;
/*      */   private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;
/*      */ 
/*      */   @NotNull
/*      */   private RuntimeAnnotationReader annotationReader;
/*      */   private boolean hasSwaRef;
/*      */ 
/*      */   @NotNull
/*      */   private final Map<Class, Class> subclassReplacements;
/*      */   public final boolean fastBoot;
/*  247 */   private Set<XmlNs> xmlNsSet = null;
/*      */   private Encoded[] utf8nameTable;
/* 1036 */   private static final Comparator<QName> QNAME_COMPARATOR = new Comparator() {
/*      */     public int compare(QName lhs, QName rhs) {
/* 1038 */       int r = lhs.getLocalPart().compareTo(rhs.getLocalPart());
/* 1039 */       if (r != 0) return r;
/*      */ 
/* 1041 */       return lhs.getNamespaceURI().compareTo(rhs.getNamespaceURI());
/*      */     }
/* 1036 */   };
/*      */ 
/*      */   public Set<XmlNs> getXmlNsSet()
/*      */   {
/*  255 */     return this.xmlNsSet; } 
/*  260 */   private JAXBContextImpl(JAXBContextBuilder builder) throws JAXBException { this.defaultNsUri = builder.defaultNsUri;
/*  261 */     this.retainPropertyInfo = builder.retainPropertyInfo;
/*  262 */     this.annotationReader = builder.annotationReader;
/*  263 */     this.subclassReplacements = builder.subclassReplacements;
/*  264 */     this.c14nSupport = builder.c14nSupport;
/*  265 */     this.classes = builder.classes;
/*  266 */     this.xmlAccessorFactorySupport = builder.xmlAccessorFactorySupport;
/*  267 */     this.allNillable = builder.allNillable;
/*  268 */     this.supressAccessorWarnings = builder.supressAccessorWarnings;
/*  269 */     this.improvedXsiTypeHandling = builder.improvedXsiTypeHandling;
/*      */ 
/*  271 */     Collection typeRefs = builder.typeRefs;
/*      */     boolean fastB;
/*      */     try { fastB = Boolean.getBoolean(JAXBContextImpl.class.getName() + ".fastBoot");
/*      */     } catch (SecurityException e) {
/*  277 */       fastB = false;
/*      */     }
/*  279 */     this.fastBoot = fastB;
/*      */ 
/*  281 */     System.arraycopy(this.classes, 0, this.classes, 0, this.classes.length);
/*      */ 
/*  283 */     RuntimeTypeInfoSet typeSet = getTypeInfoSet();
/*      */ 
/*  286 */     this.elements.put(null, new LinkedHashMap());
/*      */ 
/*  289 */     for (RuntimeBuiltinLeafInfo leaf : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos) {
/*  290 */       bi = new LeafBeanInfoImpl(this, leaf);
/*  291 */       this.beanInfoMap.put(leaf.getClazz(), bi);
/*  292 */       for (QName t : bi.getTypeNames())
/*  293 */         this.typeMap.put(t, bi);
/*      */     }
/*      */     LeafBeanInfoImpl bi;
/*  296 */     for (RuntimeEnumLeafInfo e : typeSet.enums().values()) {
/*  297 */       JaxBeanInfo bi = getOrCreate(e);
/*  298 */       for (QName qn : bi.getTypeNames())
/*  299 */         this.typeMap.put(qn, bi);
/*  300 */       if (e.isElement()) {
/*  301 */         this.rootMap.put(e.getElementName(), bi);
/*      */       }
/*      */     }
/*  304 */     for (RuntimeArrayInfo a : typeSet.arrays().values()) {
/*  305 */       ai = getOrCreate(a);
/*  306 */       for (QName qn : ai.getTypeNames())
/*  307 */         this.typeMap.put(qn, ai);
/*      */     }
/*      */     JaxBeanInfo ai;
/*  310 */     for (Map.Entry e : typeSet.beans().entrySet()) {
/*  311 */       bi = getOrCreate((RuntimeClassInfo)e.getValue());
/*      */ 
/*  313 */       XmlSchema xs = (XmlSchema)this.annotationReader.getPackageAnnotation(XmlSchema.class, e.getKey(), null);
/*  314 */       if ((xs != null) && 
/*  315 */         (xs.xmlns() != null) && (xs.xmlns().length > 0)) {
/*  316 */         if (this.xmlNsSet == null)
/*  317 */           this.xmlNsSet = new HashSet();
/*  318 */         this.xmlNsSet.addAll(Arrays.asList(xs.xmlns()));
/*      */       }
/*      */ 
/*  322 */       if (bi.isElement()) {
/*  323 */         this.rootMap.put(((RuntimeClassInfo)e.getValue()).getElementName(), bi);
/*      */       }
/*  325 */       for (QName qn : bi.getTypeNames())
/*  326 */         this.typeMap.put(qn, bi);
/*      */     }
/*      */     ClassBeanInfoImpl bi;
/*  330 */     for (RuntimeElementInfo n : typeSet.getAllElements()) {
/*  331 */       ElementBeanInfoImpl bi = getOrCreate(n);
/*  332 */       if (n.getScope() == null) {
/*  333 */         this.rootMap.put(n.getElementName(), bi);
/*      */       }
/*  335 */       RuntimeClassInfo scope = n.getScope();
/*  336 */       Class scopeClazz = scope == null ? null : (Class)scope.getClazz();
/*  337 */       Map m = (Map)this.elements.get(scopeClazz);
/*  338 */       if (m == null) {
/*  339 */         m = new LinkedHashMap();
/*  340 */         this.elements.put(scopeClazz, m);
/*      */       }
/*  342 */       m.put(n.getElementName(), bi);
/*      */     }
/*      */ 
/*  346 */     this.beanInfoMap.put(JAXBElement.class, new ElementBeanInfoImpl(this));
/*      */ 
/*  348 */     this.beanInfoMap.put(CompositeStructure.class, new CompositeStructureBeanInfo(this));
/*      */ 
/*  350 */     getOrCreate(typeSet.getAnyTypeInfo());
/*      */ 
/*  353 */     for (JaxBeanInfo bi : this.beanInfos.values()) {
/*  354 */       bi.link(this);
/*      */     }
/*      */ 
/*  357 */     for (Map.Entry e : RuntimeUtil.primitiveToBox.entrySet()) {
/*  358 */       this.beanInfoMap.put(e.getKey(), this.beanInfoMap.get(e.getValue()));
/*      */     }
/*      */ 
/*  361 */     ReflectionNavigator nav = typeSet.getNavigator();
/*      */ 
/*  363 */     for (TypeReference tr : typeRefs) {
/*  364 */       XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)tr.get(XmlJavaTypeAdapter.class);
/*  365 */       Adapter a = null;
/*  366 */       XmlList xl = (XmlList)tr.get(XmlList.class);
/*      */ 
/*  369 */       Class erasedType = nav.erasure(tr.type);
/*      */ 
/*  371 */       if (xjta != null) {
/*  372 */         a = new Adapter(xjta.value(), nav);
/*      */       }
/*  374 */       if (tr.get(XmlAttachmentRef.class) != null) {
/*  375 */         a = new Adapter(SwaRefAdapter.class, nav);
/*  376 */         this.hasSwaRef = true;
/*      */       }
/*      */ 
/*  379 */       if (a != null) {
/*  380 */         erasedType = nav.erasure((Type)a.defaultType);
/*      */       }
/*      */ 
/*  383 */       Name name = this.nameBuilder.createElementName(tr.tagName);
/*      */       InternalBridge bridge;
/*      */       InternalBridge bridge;
/*  386 */       if (xl == null)
/*  387 */         bridge = new BridgeImpl(this, name, getBeanInfo(erasedType, true), tr);
/*      */       else {
/*  389 */         bridge = new BridgeImpl(this, name, new ValueListBeanInfoImpl(this, erasedType), tr);
/*      */       }
/*  391 */       if (a != null) {
/*  392 */         bridge = new BridgeAdapter(bridge, (Class)a.adapterType);
/*      */       }
/*  394 */       this.bridges.put(tr, bridge);
/*      */     }
/*      */ 
/*  397 */     this.nameList = this.nameBuilder.conclude();
/*      */ 
/*  399 */     for (JaxBeanInfo bi : this.beanInfos.values()) {
/*  400 */       bi.wrapUp();
/*      */     }
/*      */ 
/*  403 */     this.nameBuilder = null;
/*  404 */     this.beanInfos = null;
/*      */   }
/*      */ 
/*      */   public boolean hasSwaRef()
/*      */   {
/*  411 */     return this.hasSwaRef;
/*      */   }
/*      */ 
/*      */   public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
/*      */     try {
/*  416 */       return getTypeInfoSet();
/*      */     }
/*      */     catch (IllegalAnnotationsException e) {
/*  419 */       throw new AssertionError(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public RuntimeTypeInfoSet getTypeInfoSet()
/*      */     throws IllegalAnnotationsException
/*      */   {
/*  429 */     if (this.typeInfoSetCache != null) {
/*  430 */       RuntimeTypeInfoSet r = (RuntimeTypeInfoSet)this.typeInfoSetCache.get();
/*  431 */       if (r != null) {
/*  432 */         return r;
/*      */       }
/*      */     }
/*  435 */     RuntimeModelBuilder builder = new RuntimeModelBuilder(this, this.annotationReader, this.subclassReplacements, this.defaultNsUri);
/*      */ 
/*  437 */     IllegalAnnotationsException.Builder errorHandler = new IllegalAnnotationsException.Builder();
/*  438 */     builder.setErrorHandler(errorHandler);
/*      */ 
/*  440 */     for (Class c : this.classes) {
/*  441 */       if (c != CompositeStructure.class)
/*      */       {
/*  445 */         builder.getTypeInfo(new Ref(c));
/*      */       }
/*      */     }
/*  448 */     this.hasSwaRef |= builder.hasSwaRef;
/*  449 */     RuntimeTypeInfoSet r = builder.link();
/*      */ 
/*  451 */     errorHandler.check();
/*  452 */     assert (r != null) : "if no error was reported, the link must be a success";
/*      */ 
/*  454 */     this.typeInfoSetCache = new WeakReference(r);
/*      */ 
/*  456 */     return r;
/*      */   }
/*      */ 
/*      */   public ElementBeanInfoImpl getElement(Class scope, QName name)
/*      */   {
/*  461 */     Map m = (Map)this.elements.get(scope);
/*  462 */     if (m != null) {
/*  463 */       ElementBeanInfoImpl bi = (ElementBeanInfoImpl)m.get(name);
/*  464 */       if (bi != null)
/*  465 */         return bi;
/*      */     }
/*  467 */     m = (Map)this.elements.get(null);
/*  468 */     return (ElementBeanInfoImpl)m.get(name);
/*      */   }
/*      */ 
/*      */   private ElementBeanInfoImpl getOrCreate(RuntimeElementInfo rei)
/*      */   {
/*  476 */     JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(rei);
/*  477 */     if (bi != null) return (ElementBeanInfoImpl)bi;
/*      */ 
/*  480 */     return new ElementBeanInfoImpl(this, rei);
/*      */   }
/*      */ 
/*      */   protected JaxBeanInfo getOrCreate(RuntimeEnumLeafInfo eli) {
/*  484 */     JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(eli);
/*  485 */     if (bi != null) return bi;
/*  486 */     bi = new LeafBeanInfoImpl(this, eli);
/*  487 */     this.beanInfoMap.put(bi.jaxbType, bi);
/*  488 */     return bi;
/*      */   }
/*      */ 
/*      */   protected ClassBeanInfoImpl getOrCreate(RuntimeClassInfo ci) {
/*  492 */     ClassBeanInfoImpl bi = (ClassBeanInfoImpl)this.beanInfos.get(ci);
/*  493 */     if (bi != null) return bi;
/*  494 */     bi = new ClassBeanInfoImpl(this, ci);
/*  495 */     this.beanInfoMap.put(bi.jaxbType, bi);
/*  496 */     return bi;
/*      */   }
/*      */ 
/*      */   protected JaxBeanInfo getOrCreate(RuntimeArrayInfo ai) {
/*  500 */     JaxBeanInfo abi = (JaxBeanInfo)this.beanInfos.get(ai);
/*  501 */     if (abi != null) return abi;
/*      */ 
/*  503 */     abi = new ArrayBeanInfoImpl(this, ai);
/*      */ 
/*  505 */     this.beanInfoMap.put(ai.getType(), abi);
/*  506 */     return abi;
/*      */   }
/*      */ 
/*      */   public JaxBeanInfo getOrCreate(RuntimeTypeInfo e) {
/*  510 */     if ((e instanceof RuntimeElementInfo))
/*  511 */       return getOrCreate((RuntimeElementInfo)e);
/*  512 */     if ((e instanceof RuntimeClassInfo))
/*  513 */       return getOrCreate((RuntimeClassInfo)e);
/*  514 */     if ((e instanceof RuntimeLeafInfo)) {
/*  515 */       JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(e);
/*  516 */       assert (bi != null);
/*  517 */       return bi;
/*      */     }
/*  519 */     if ((e instanceof RuntimeArrayInfo))
/*  520 */       return getOrCreate((RuntimeArrayInfo)e);
/*  521 */     if (e.getType() == Object.class)
/*      */     {
/*  523 */       JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(Object.class);
/*  524 */       if (bi == null) {
/*  525 */         bi = new AnyTypeBeanInfo(this, e);
/*  526 */         this.beanInfoMap.put(Object.class, bi);
/*      */       }
/*  528 */       return bi;
/*      */     }
/*      */ 
/*  531 */     throw new IllegalArgumentException();
/*      */   }
/*      */ 
/*      */   public final JaxBeanInfo getBeanInfo(Object o)
/*      */   {
/*  546 */     for (Class c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
/*  547 */       JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(c);
/*  548 */       if (bi != null) return bi;
/*      */     }
/*  550 */     if ((o instanceof org.w3c.dom.Element))
/*  551 */       return (JaxBeanInfo)this.beanInfoMap.get(Object.class);
/*  552 */     for (Class c : o.getClass().getInterfaces()) {
/*  553 */       JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(c);
/*  554 */       if (bi != null) return bi;
/*      */     }
/*  556 */     return null;
/*      */   }
/*      */ 
/*      */   public final JaxBeanInfo getBeanInfo(Object o, boolean fatal)
/*      */     throws JAXBException
/*      */   {
/*  568 */     JaxBeanInfo bi = getBeanInfo(o);
/*  569 */     if (bi != null) return bi;
/*  570 */     if (fatal) {
/*  571 */       if ((o instanceof Document))
/*  572 */         throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(new Object[] { o.getClass() }));
/*  573 */       throw new JAXBException(Messages.UNKNOWN_CLASS.format(new Object[] { o.getClass() }));
/*      */     }
/*  575 */     return null;
/*      */   }
/*      */ 
/*      */   public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz)
/*      */   {
/*  589 */     return (JaxBeanInfo)this.beanInfoMap.get(clazz);
/*      */   }
/*      */ 
/*      */   public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz, boolean fatal)
/*      */     throws JAXBException
/*      */   {
/*  601 */     JaxBeanInfo bi = getBeanInfo(clazz);
/*  602 */     if (bi != null) return bi;
/*  603 */     if (fatal)
/*  604 */       throw new JAXBException(clazz.getName() + " is not known to this context");
/*  605 */     return null;
/*      */   }
/*      */ 
/*      */   public final Loader selectRootLoader(UnmarshallingContext.State state, TagName tag)
/*      */   {
/*  616 */     JaxBeanInfo beanInfo = (JaxBeanInfo)this.rootMap.get(tag.uri, tag.local);
/*  617 */     if (beanInfo == null) {
/*  618 */       return null;
/*      */     }
/*  620 */     return beanInfo.getLoader(this, true);
/*      */   }
/*      */ 
/*      */   public JaxBeanInfo getGlobalType(QName name)
/*      */   {
/*  632 */     return (JaxBeanInfo)this.typeMap.get(name);
/*      */   }
/*      */ 
/*      */   public String getNearestTypeName(QName name)
/*      */   {
/*  643 */     String[] all = new String[this.typeMap.size()];
/*  644 */     int i = 0;
/*  645 */     for (QName qn : this.typeMap.keySet()) {
/*  646 */       if (qn.getLocalPart().equals(name.getLocalPart()))
/*  647 */         return qn.toString();
/*  648 */       all[(i++)] = qn.toString();
/*      */     }
/*      */ 
/*  651 */     String nearest = EditDistance.findNearest(name.toString(), all);
/*      */ 
/*  653 */     if (EditDistance.editDistance(nearest, name.toString()) > 10) {
/*  654 */       return null;
/*      */     }
/*  656 */     return nearest;
/*      */   }
/*      */ 
/*      */   public Set<QName> getValidRootNames()
/*      */   {
/*  664 */     Set r = new TreeSet(QNAME_COMPARATOR);
/*  665 */     for (QNameMap.Entry e : this.rootMap.entrySet()) {
/*  666 */       r.add(e.createQName());
/*      */     }
/*  668 */     return r;
/*      */   }
/*      */ 
/*      */   public synchronized Encoded[] getUTF8NameTable()
/*      */   {
/*  677 */     if (this.utf8nameTable == null) {
/*  678 */       Encoded[] x = new Encoded[this.nameList.localNames.length];
/*  679 */       for (int i = 0; i < x.length; i++) {
/*  680 */         Encoded e = new Encoded(this.nameList.localNames[i]);
/*  681 */         e.compact();
/*  682 */         x[i] = e;
/*      */       }
/*  684 */       this.utf8nameTable = x;
/*      */     }
/*  686 */     return this.utf8nameTable;
/*      */   }
/*      */ 
/*      */   public int getNumberOfLocalNames() {
/*  690 */     return this.nameList.localNames.length;
/*      */   }
/*      */ 
/*      */   public int getNumberOfElementNames() {
/*  694 */     return this.nameList.numberOfElementNames;
/*      */   }
/*      */ 
/*      */   public int getNumberOfAttributeNames() {
/*  698 */     return this.nameList.numberOfAttributeNames;
/*      */   }
/*      */ 
/*      */   static Transformer createTransformer()
/*      */   {
/*      */     try
/*      */     {
/*  706 */       if (tf == null) {
/*  707 */         synchronized (JAXBContextImpl.class) {
/*  708 */           if (tf == null) {
/*  709 */             tf = (SAXTransformerFactory)TransformerFactory.newInstance();
/*      */           }
/*      */         }
/*      */       }
/*  713 */       return tf.newTransformer();
/*      */     } catch (TransformerConfigurationException e) {
/*  715 */       throw new Error(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static TransformerHandler createTransformerHandler()
/*      */   {
/*      */     try
/*      */     {
/*  724 */       if (tf == null) {
/*  725 */         synchronized (JAXBContextImpl.class) {
/*  726 */           if (tf == null) {
/*  727 */             tf = (SAXTransformerFactory)TransformerFactory.newInstance();
/*      */           }
/*      */         }
/*      */       }
/*  731 */       return tf.newTransformerHandler();
/*      */     } catch (TransformerConfigurationException e) {
/*  733 */       throw new Error(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   static Document createDom()
/*      */   {
/*  741 */     synchronized (JAXBContextImpl.class) {
/*  742 */       if (db == null) {
/*      */         try {
/*  744 */           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/*  745 */           dbf.setNamespaceAware(true);
/*  746 */           db = dbf.newDocumentBuilder();
/*      */         }
/*      */         catch (ParserConfigurationException e) {
/*  749 */           throw new FactoryConfigurationError(e);
/*      */         }
/*      */       }
/*  752 */       return db.newDocument();
/*      */     }
/*      */   }
/*      */ 
/*      */   public MarshallerImpl createMarshaller() {
/*  757 */     return new MarshallerImpl(this, null);
/*      */   }
/*      */ 
/*      */   public UnmarshallerImpl createUnmarshaller() {
/*  761 */     return new UnmarshallerImpl(this, null);
/*      */   }
/*      */ 
/*      */   public Validator createValidator() {
/*  765 */     throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED_IN_2_0.format(new Object[0]));
/*      */   }
/*      */ 
/*      */   public JAXBIntrospector createJAXBIntrospector()
/*      */   {
/*  770 */     return new JAXBIntrospector() {
/*      */       public boolean isElement(Object object) {
/*  772 */         return getElementName(object) != null;
/*      */       }
/*      */ 
/*      */       public QName getElementName(Object jaxbElement) {
/*      */         try {
/*  777 */           return JAXBContextImpl.this.getElementName(jaxbElement); } catch (JAXBException e) {
/*      */         }
/*  779 */         return null;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private NonElement<Type, Class> getXmlType(RuntimeTypeInfoSet tis, TypeReference tr)
/*      */   {
/*  786 */     if (tr == null) {
/*  787 */       throw new IllegalArgumentException();
/*      */     }
/*  789 */     XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)tr.get(XmlJavaTypeAdapter.class);
/*  790 */     XmlList xl = (XmlList)tr.get(XmlList.class);
/*      */ 
/*  792 */     Ref ref = new Ref(this.annotationReader, tis.getNavigator(), tr.type, xjta, xl);
/*      */ 
/*  794 */     return tis.getTypeInfo(ref);
/*      */   }
/*      */ 
/*      */   public void generateEpisode(Result output)
/*      */   {
/*  799 */     if (output == null)
/*  800 */       throw new IllegalArgumentException();
/*  801 */     createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
/*      */   }
/*      */ 
/*      */   public void generateSchema(SchemaOutputResolver outputResolver)
/*      */     throws IOException
/*      */   {
/*  807 */     if (outputResolver == null) {
/*  808 */       throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format(new Object[0]));
/*      */     }
/*  810 */     final SAXParseException[] e = new SAXParseException[1];
/*  811 */     final SAXParseException[] w = new SAXParseException[1];
/*      */ 
/*  813 */     createSchemaGenerator().write(outputResolver, new ErrorListener() {
/*      */       public void error(SAXParseException exception) {
/*  815 */         e[0] = exception;
/*      */       }
/*      */ 
/*      */       public void fatalError(SAXParseException exception) {
/*  819 */         e[0] = exception;
/*      */       }
/*      */ 
/*      */       public void warning(SAXParseException exception) {
/*  823 */         w[0] = exception;
/*      */       }
/*      */ 
/*      */       public void info(SAXParseException exception)
/*      */       {
/*      */       }
/*      */     });
/*  829 */     if (e[0] != null) {
/*  830 */       IOException x = new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format(new Object[0]));
/*  831 */       x.initCause(e[0]);
/*  832 */       throw x;
/*      */     }
/*  834 */     if (w[0] != null) {
/*  835 */       IOException x = new IOException(Messages.ERROR_PROCESSING_SCHEMA.format(new Object[0]));
/*  836 */       x.initCause(w[0]);
/*  837 */       throw x;
/*      */     }
/*      */   }
/*      */ 
/*      */   private XmlSchemaGenerator<Type, Class, Field, Method> createSchemaGenerator() {
/*      */     RuntimeTypeInfoSet tis;
/*      */     try {
/*  844 */       tis = getTypeInfoSet();
/*      */     }
/*      */     catch (IllegalAnnotationsException e) {
/*  847 */       throw new AssertionError(e);
/*      */     }
/*      */ 
/*  850 */     XmlSchemaGenerator xsdgen = new XmlSchemaGenerator(tis.getNavigator(), tis);
/*      */ 
/*  856 */     Set rootTagNames = new HashSet();
/*  857 */     for (RuntimeElementInfo ei : tis.getAllElements()) {
/*  858 */       rootTagNames.add(ei.getElementName());
/*      */     }
/*  860 */     for (RuntimeClassInfo ci : tis.beans().values()) {
/*  861 */       if (ci.isElement()) {
/*  862 */         rootTagNames.add(ci.asElement().getElementName());
/*      */       }
/*      */     }
/*  865 */     for (TypeReference tr : this.bridges.keySet())
/*  866 */       if (!rootTagNames.contains(tr.tagName))
/*      */       {
/*  869 */         if ((tr.type == Void.TYPE) || (tr.type == Void.class)) {
/*  870 */           xsdgen.add(tr.tagName, false, null);
/*      */         }
/*  872 */         else if (tr.type != CompositeStructure.class)
/*      */         {
/*  875 */           NonElement typeInfo = getXmlType(tis, tr);
/*  876 */           xsdgen.add(tr.tagName, !Navigator.REFLECTION.isPrimitive(tr.type), typeInfo);
/*      */         }
/*      */       }
/*  879 */     return xsdgen;
/*      */   }
/*      */ 
/*      */   public QName getTypeName(TypeReference tr) {
/*      */     try {
/*  884 */       NonElement xt = getXmlType(getTypeInfoSet(), tr);
/*  885 */       if (xt == null) throw new IllegalArgumentException();
/*  886 */       return xt.getTypeName();
/*      */     }
/*      */     catch (IllegalAnnotationsException e) {
/*  889 */       throw new AssertionError(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public SchemaOutputResolver createTestResolver()
/*      */   {
/*  897 */     return new SchemaOutputResolver() {
/*      */       public Result createOutput(String namespaceUri, String suggestedFileName) {
/*  899 */         SAXResult r = new SAXResult(new DefaultHandler());
/*  900 */         r.setSystemId(suggestedFileName);
/*  901 */         return r;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public <T> Binder<T> createBinder(Class<T> domType)
/*      */   {
/*  908 */     if (domType == Node.class) {
/*  909 */       return createBinder();
/*      */     }
/*  911 */     return super.createBinder(domType);
/*      */   }
/*      */ 
/*      */   public Binder<Node> createBinder()
/*      */   {
/*  916 */     return new BinderImpl(this, new DOMScanner());
/*      */   }
/*      */ 
/*      */   public QName getElementName(Object o) throws JAXBException {
/*  920 */     JaxBeanInfo bi = getBeanInfo(o, true);
/*  921 */     if (!bi.isElement())
/*  922 */       return null;
/*  923 */     return new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
/*      */   }
/*      */ 
/*      */   public QName getElementName(Class o) throws JAXBException {
/*  927 */     JaxBeanInfo bi = getBeanInfo(o, true);
/*  928 */     if (!bi.isElement())
/*  929 */       return null;
/*  930 */     return new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
/*      */   }
/*      */ 
/*      */   public Bridge createBridge(TypeReference ref) {
/*  934 */     return (Bridge)this.bridges.get(ref);
/*      */   }
/*      */   @NotNull
/*      */   public BridgeContext createBridgeContext() {
/*  938 */     return new BridgeContextImpl(this);
/*      */   }
/*      */ 
/*      */   public RawAccessor getElementPropertyAccessor(Class wrapperBean, String nsUri, String localName) throws JAXBException {
/*  942 */     JaxBeanInfo bi = getBeanInfo(wrapperBean, true);
/*  943 */     if (!(bi instanceof ClassBeanInfoImpl)) {
/*  944 */       throw new JAXBException(wrapperBean + " is not a bean");
/*      */     }
/*  946 */     for (ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi; cb != null; cb = cb.superClazz) {
/*  947 */       for (Property p : cb.properties) {
/*  948 */         final Accessor acc = p.getElementPropertyAccessor(nsUri, localName);
/*  949 */         if (acc != null)
/*  950 */           return new RawAccessor()
/*      */           {
/*      */             public Object get(Object bean)
/*      */               throws AccessorException
/*      */             {
/*  957 */               return acc.getUnadapted(bean);
/*      */             }
/*      */ 
/*      */             public void set(Object bean, Object value) throws AccessorException {
/*  961 */               acc.setUnadapted(bean, value);
/*      */             }
/*      */           };
/*      */       }
/*      */     }
/*  966 */     throw new JAXBException(new QName(nsUri, localName) + " is not a valid property on " + wrapperBean);
/*      */   }
/*      */ 
/*      */   public List<String> getKnownNamespaceURIs() {
/*  970 */     return Arrays.asList(this.nameList.namespaceURIs);
/*      */   }
/*      */ 
/*      */   public String getBuildId() {
/*  974 */     Package pkg = getClass().getPackage();
/*  975 */     if (pkg == null) return null;
/*  976 */     return pkg.getImplementationVersion();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  981 */     StringBuilder buf = new StringBuilder(Which.which(getClass()) + " Build-Id: " + getBuildId());
/*  982 */     buf.append("\nClasses known to this context:\n");
/*      */ 
/*  984 */     Set names = new TreeSet();
/*      */ 
/*  986 */     for (Class key : this.beanInfoMap.keySet()) {
/*  987 */       names.add(key.getName());
/*      */     }
/*  989 */     for (String name : names) {
/*  990 */       buf.append("  ").append(name).append('\n');
/*      */     }
/*  992 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public String getXMIMEContentType(Object o)
/*      */   {
/* 1000 */     JaxBeanInfo bi = getBeanInfo(o);
/* 1001 */     if (!(bi instanceof ClassBeanInfoImpl)) {
/* 1002 */       return null;
/*      */     }
/* 1004 */     ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi;
/* 1005 */     for (Property p : cb.properties) {
/* 1006 */       if ((p instanceof AttributeProperty)) {
/* 1007 */         AttributeProperty ap = (AttributeProperty)p;
/* 1008 */         if (ap.attName.equals("http://www.w3.org/2005/05/xmlmime", "contentType"))
/*      */           try {
/* 1010 */             return (String)ap.xacc.print(o);
/*      */           } catch (AccessorException e) {
/* 1012 */             return null;
/*      */           } catch (SAXException e) {
/* 1014 */             return null;
/*      */           } catch (ClassCastException e) {
/* 1016 */             return null;
/*      */           }
/*      */       }
/*      */     }
/* 1020 */     return null;
/*      */   }
/*      */ 
/*      */   public JAXBContextImpl createAugmented(Class<?> clazz)
/*      */     throws JAXBException
/*      */   {
/* 1027 */     Class[] newList = new Class[this.classes.length + 1];
/* 1028 */     System.arraycopy(this.classes, 0, newList, 0, this.classes.length);
/* 1029 */     newList[this.classes.length] = clazz;
/*      */ 
/* 1031 */     JAXBContextBuilder builder = new JAXBContextBuilder(this);
/* 1032 */     builder.setClasses(newList);
/* 1033 */     return builder.build();
/*      */   }
/*      */ 
/*      */   public static class JAXBContextBuilder
/*      */   {
/* 1047 */     private boolean retainPropertyInfo = false;
/* 1048 */     private boolean supressAccessorWarnings = false;
/* 1049 */     private String defaultNsUri = "";
/*      */ 
/*      */     @NotNull
/* 1050 */     private RuntimeAnnotationReader annotationReader = new RuntimeInlineAnnotationReader();
/*      */ 
/*      */     @NotNull
/* 1051 */     private Map<Class, Class> subclassReplacements = Collections.emptyMap();
/* 1052 */     private boolean c14nSupport = false;
/*      */     private Class[] classes;
/*      */     private Collection<TypeReference> typeRefs;
/* 1055 */     private boolean xmlAccessorFactorySupport = false;
/*      */     private boolean allNillable;
/* 1057 */     private boolean improvedXsiTypeHandling = true;
/*      */ 
/*      */     public JAXBContextBuilder() {
/*      */     }
/*      */     public JAXBContextBuilder(JAXBContextImpl baseImpl) {
/* 1062 */       this.supressAccessorWarnings = baseImpl.supressAccessorWarnings;
/* 1063 */       this.retainPropertyInfo = baseImpl.retainPropertyInfo;
/* 1064 */       this.defaultNsUri = baseImpl.defaultNsUri;
/* 1065 */       this.annotationReader = baseImpl.annotationReader;
/* 1066 */       this.subclassReplacements = baseImpl.subclassReplacements;
/* 1067 */       this.c14nSupport = baseImpl.c14nSupport;
/* 1068 */       this.classes = baseImpl.classes;
/* 1069 */       this.typeRefs = baseImpl.bridges.keySet();
/* 1070 */       this.xmlAccessorFactorySupport = baseImpl.xmlAccessorFactorySupport;
/* 1071 */       this.allNillable = baseImpl.allNillable;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setRetainPropertyInfo(boolean val) {
/* 1075 */       this.retainPropertyInfo = val;
/* 1076 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setSupressAccessorWarnings(boolean val) {
/* 1080 */       this.supressAccessorWarnings = val;
/* 1081 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setC14NSupport(boolean val) {
/* 1085 */       this.c14nSupport = val;
/* 1086 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setXmlAccessorFactorySupport(boolean val) {
/* 1090 */       this.xmlAccessorFactorySupport = val;
/* 1091 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setDefaultNsUri(String val) {
/* 1095 */       this.defaultNsUri = val;
/* 1096 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setAllNillable(boolean val) {
/* 1100 */       this.allNillable = val;
/* 1101 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setClasses(Class[] val) {
/* 1105 */       this.classes = val;
/* 1106 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setAnnotationReader(RuntimeAnnotationReader val) {
/* 1110 */       this.annotationReader = val;
/* 1111 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setSubclassReplacements(Map<Class, Class> val) {
/* 1115 */       this.subclassReplacements = val;
/* 1116 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setTypeRefs(Collection<TypeReference> val) {
/* 1120 */       this.typeRefs = val;
/* 1121 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextBuilder setImprovedXsiTypeHandling(boolean val) {
/* 1125 */       this.improvedXsiTypeHandling = val;
/* 1126 */       return this;
/*      */     }
/*      */ 
/*      */     public JAXBContextImpl build()
/*      */       throws JAXBException
/*      */     {
/* 1132 */       if (this.defaultNsUri == null) {
/* 1133 */         this.defaultNsUri = "";
/*      */       }
/*      */ 
/* 1136 */       if (this.subclassReplacements == null) {
/* 1137 */         this.subclassReplacements = Collections.emptyMap();
/*      */       }
/*      */ 
/* 1140 */       if (this.annotationReader == null) {
/* 1141 */         this.annotationReader = new RuntimeInlineAnnotationReader();
/*      */       }
/*      */ 
/* 1144 */       if (this.typeRefs == null) {
/* 1145 */         this.typeRefs = Collections.emptyList();
/*      */       }
/*      */ 
/* 1148 */       return new JAXBContextImpl(this, null);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl
 * JD-Core Version:    0.6.2
 */