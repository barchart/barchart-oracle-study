/*     */ package com.sun.xml.internal.bind.v2.runtime;
/*     */ 
/*     */ import com.sun.xml.internal.bind.api.AccessorException;
/*     */ import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
/*     */ import com.sun.xml.internal.bind.v2.runtime.property.Property;
/*     */ import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
/*     */ import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
/*     */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
/*     */ import com.sun.xml.internal.bind.v2.util.QNameMap;
/*     */ import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Type;
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.JAXBElement.GlobalScope;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement>
/*     */ {
/*     */   private Loader loader;
/*     */   private final Property property;
/*     */   private final QName tagName;
/*     */   public final Class expectedType;
/*     */   private final Class scope;
/*     */   private final Constructor<? extends JAXBElement> constructor;
/*     */ 
/*     */   ElementBeanInfoImpl(JAXBContextImpl grammar, RuntimeElementInfo rei)
/*     */   {
/*  79 */     super(grammar, rei, rei.getType(), true, false, true);
/*     */ 
/*  81 */     this.property = PropertyFactory.create(grammar, rei.getProperty());
/*     */ 
/*  83 */     this.tagName = rei.getElementName();
/*  84 */     this.expectedType = Navigator.REFLECTION.erasure((Type)rei.getContentInMemoryType());
/*  85 */     this.scope = (rei.getScope() == null ? JAXBElement.GlobalScope.class : (Class)rei.getScope().getClazz());
/*     */ 
/*  87 */     Class type = Navigator.REFLECTION.erasure(rei.getType());
/*  88 */     if (type == JAXBElement.class)
/*  89 */       this.constructor = null;
/*     */     else
/*     */       try {
/*  92 */         this.constructor = type.getConstructor(new Class[] { this.expectedType });
/*     */       } catch (NoSuchMethodException e) {
/*  94 */         NoSuchMethodError x = new NoSuchMethodError("Failed to find the constructor for " + type + " with " + this.expectedType);
/*  95 */         x.initCause(e);
/*  96 */         throw x;
/*     */       }
/*     */   }
/*     */ 
/*     */   protected ElementBeanInfoImpl(final JAXBContextImpl grammar)
/*     */   {
/* 110 */     super(grammar, null, JAXBElement.class, true, false, true);
/* 111 */     this.tagName = null;
/* 112 */     this.expectedType = null;
/* 113 */     this.scope = null;
/* 114 */     this.constructor = null;
/*     */ 
/* 116 */     this.property = new Property() {
/*     */       public void reset(JAXBElement o) {
/* 118 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public void serializeBody(JAXBElement e, XMLSerializer target, Object outerPeer) throws SAXException, IOException, XMLStreamException {
/* 122 */         Class scope = e.getScope();
/* 123 */         if (e.isGlobalScope()) scope = null;
/* 124 */         QName n = e.getName();
/* 125 */         ElementBeanInfoImpl bi = grammar.getElement(scope, n);
/* 126 */         if (bi == null)
/*     */         {
/*     */           JaxBeanInfo tbi;
/*     */           try {
/* 130 */             tbi = grammar.getBeanInfo(e.getDeclaredType(), true);
/*     */           }
/*     */           catch (JAXBException x) {
/* 133 */             target.reportError(null, x);
/* 134 */             return;
/*     */           }
/* 136 */           Object value = e.getValue();
/* 137 */           target.startElement(n.getNamespaceURI(), n.getLocalPart(), n.getPrefix(), null);
/* 138 */           if (value == null)
/* 139 */             target.writeXsiNilTrue();
/*     */           else {
/* 141 */             target.childAsXsiType(value, "value", tbi, false);
/*     */           }
/* 143 */           target.endElement();
/*     */         } else {
/*     */           try {
/* 146 */             bi.property.serializeBody(e, target, e);
/*     */           } catch (AccessorException x) {
/* 148 */             target.reportError(null, x);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void serializeURIs(JAXBElement o, XMLSerializer target) {
/*     */       }
/*     */ 
/*     */       public boolean hasSerializeURIAction() {
/* 157 */         return false;
/*     */       }
/*     */ 
/*     */       public String getIdValue(JAXBElement o) {
/* 161 */         return null;
/*     */       }
/*     */ 
/*     */       public PropertyKind getKind() {
/* 165 */         return PropertyKind.ELEMENT;
/*     */       }
/*     */ 
/*     */       public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
/*     */       }
/*     */ 
/*     */       public Accessor getElementPropertyAccessor(String nsUri, String localName) {
/* 172 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public void wrapUp() {
/*     */       }
/*     */ 
/*     */       public RuntimePropertyInfo getInfo() {
/* 179 */         return ElementBeanInfoImpl.this.property.getInfo();
/*     */       }
/*     */ 
/*     */       public boolean isHiddenByOverride() {
/* 183 */         return false;
/*     */       }
/*     */ 
/*     */       public void setHiddenByOverride(boolean hidden) {
/* 187 */         throw new UnsupportedOperationException("Not supported on jaxbelements.");
/*     */       }
/*     */ 
/*     */       public String getFieldName() {
/* 191 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String getElementNamespaceURI(JAXBElement e)
/*     */   {
/* 264 */     return e.getName().getNamespaceURI();
/*     */   }
/*     */ 
/*     */   public String getElementLocalName(JAXBElement e) {
/* 268 */     return e.getName().getLocalPart();
/*     */   }
/*     */ 
/*     */   public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
/* 272 */     if (this.loader == null)
/*     */     {
/* 274 */       UnmarshallerChain c = new UnmarshallerChain(context);
/* 275 */       QNameMap result = new QNameMap();
/* 276 */       this.property.buildChildElementUnmarshallers(c, result);
/* 277 */       if (result.size() == 1)
/*     */       {
/* 279 */         this.loader = new IntercepterLoader(((ChildLoader)result.getOne().getValue()).loader);
/*     */       }
/*     */       else
/* 282 */         this.loader = Discarder.INSTANCE;
/*     */     }
/* 284 */     return this.loader;
/*     */   }
/*     */ 
/*     */   public final JAXBElement createInstance(UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException {
/* 288 */     return createInstanceFromValue(null);
/*     */   }
/*     */ 
/*     */   public final JAXBElement createInstanceFromValue(Object o) throws IllegalAccessException, InvocationTargetException, InstantiationException {
/* 292 */     if (this.constructor == null) {
/* 293 */       return new JAXBElement(this.tagName, this.expectedType, this.scope, o);
/*     */     }
/* 295 */     return (JAXBElement)this.constructor.newInstance(new Object[] { o });
/*     */   }
/*     */ 
/*     */   public boolean reset(JAXBElement e, UnmarshallingContext context) {
/* 299 */     e.setValue(null);
/* 300 */     return true;
/*     */   }
/*     */ 
/*     */   public String getId(JAXBElement e, XMLSerializer target)
/*     */   {
/* 309 */     Object o = e.getValue();
/* 310 */     if ((o instanceof String)) {
/* 311 */       return (String)o;
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public void serializeBody(JAXBElement element, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
/*     */     try {
/* 318 */       this.property.serializeBody(element, target, null);
/*     */     } catch (AccessorException x) {
/* 320 */       target.reportError(null, x);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void serializeRoot(JAXBElement e, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
/* 325 */     serializeBody(e, target);
/*     */   }
/*     */ 
/*     */   public void serializeAttributes(JAXBElement e, XMLSerializer target)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void serializeURIs(JAXBElement e, XMLSerializer target)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final Transducer<JAXBElement> getTransducer() {
/* 337 */     return null;
/*     */   }
/*     */ 
/*     */   public void wrapUp()
/*     */   {
/* 342 */     super.wrapUp();
/* 343 */     this.property.wrapUp();
/*     */   }
/*     */ 
/*     */   public void link(JAXBContextImpl grammar)
/*     */   {
/* 348 */     super.link(grammar);
/* 349 */     getLoader(grammar, true);
/*     */   }
/*     */ 
/*     */   private final class IntercepterLoader extends Loader
/*     */     implements Intercepter
/*     */   {
/*     */     private final Loader core;
/*     */ 
/*     */     public IntercepterLoader(Loader core)
/*     */     {
/* 207 */       this.core = core;
/*     */     }
/*     */ 
/*     */     public final void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException
/*     */     {
/* 212 */       state.loader = this.core;
/* 213 */       state.intercepter = this;
/*     */ 
/* 218 */       UnmarshallingContext context = state.getContext();
/*     */ 
/* 221 */       Object child = context.getOuterPeer();
/*     */ 
/* 223 */       if ((child != null) && (ElementBeanInfoImpl.this.jaxbType != child.getClass())) {
/* 224 */         child = null;
/*     */       }
/* 226 */       if (child != null) {
/* 227 */         ElementBeanInfoImpl.this.reset((JAXBElement)child, context);
/*     */       }
/* 229 */       if (child == null) {
/* 230 */         child = context.createInstance(ElementBeanInfoImpl.this);
/*     */       }
/* 232 */       fireBeforeUnmarshal(ElementBeanInfoImpl.this, child, state);
/*     */ 
/* 234 */       context.recordOuterPeer(child);
/* 235 */       UnmarshallingContext.State p = state.prev;
/* 236 */       p.backup = p.target;
/* 237 */       p.target = child;
/*     */ 
/* 239 */       this.core.startElement(state, ea);
/*     */     }
/*     */ 
/*     */     public Object intercept(UnmarshallingContext.State state, Object o) throws SAXException {
/* 243 */       JAXBElement e = (JAXBElement)state.target;
/* 244 */       state.target = state.backup;
/* 245 */       state.backup = null;
/*     */ 
/* 247 */       if (state.nil) {
/* 248 */         e.setNil(true);
/* 249 */         state.nil = false;
/*     */       }
/*     */ 
/* 252 */       if (o != null)
/*     */       {
/* 255 */         e.setValue(o);
/*     */       }
/* 257 */       fireAfterUnmarshal(ElementBeanInfoImpl.this, e, state);
/*     */ 
/* 259 */       return e;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.ElementBeanInfoImpl
 * JD-Core Version:    0.6.2
 */