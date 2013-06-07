/*     */ package com.sun.xml.internal.bind.v2.runtime.reflect;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.istack.internal.SAXException2;
/*     */ import com.sun.xml.internal.bind.WhiteSpaceProcessor;
/*     */ import com.sun.xml.internal.bind.api.AccessorException;
/*     */ import com.sun.xml.internal.bind.v2.model.core.ID;
/*     */ import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
/*     */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*     */ import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
/*     */ import com.sun.xml.internal.bind.v2.runtime.Name;
/*     */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public abstract class TransducedAccessor<BeanT>
/*     */ {
/*     */   public boolean useNamespace()
/*     */   {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   public void declareNamespace(BeanT o, XMLSerializer w)
/*     */     throws AccessorException, SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public abstract CharSequence print(@NotNull BeanT paramBeanT)
/*     */     throws AccessorException, SAXException;
/*     */ 
/*     */   public abstract void parse(BeanT paramBeanT, CharSequence paramCharSequence)
/*     */     throws AccessorException, SAXException;
/*     */ 
/*     */   public abstract boolean hasValue(BeanT paramBeanT)
/*     */     throws AccessorException;
/*     */ 
/*     */   public static <T> TransducedAccessor<T> get(JAXBContextImpl context, RuntimeNonElementRef ref)
/*     */   {
/* 142 */     Transducer xducer = RuntimeModelBuilder.createTransducer(ref);
/* 143 */     RuntimePropertyInfo prop = ref.getSource();
/*     */ 
/* 145 */     if (prop.isCollection()) {
/* 146 */       return new ListTransducedAccessorImpl(xducer, prop.getAccessor(), Lister.create(Navigator.REFLECTION.erasure(prop.getRawType()), prop.id(), prop.getAdapter()));
/*     */     }
/*     */ 
/* 151 */     if (prop.id() == ID.IDREF) {
/* 152 */       return new IDREFTransducedAccessorImpl(prop.getAccessor());
/*     */     }
/* 154 */     if ((xducer.isDefault()) && (context != null) && (!context.fastBoot)) {
/* 155 */       TransducedAccessor xa = OptimizedTransducedAccessorFactory.get(prop);
/* 156 */       if (xa != null) return xa;
/*     */     }
/*     */ 
/* 159 */     if (xducer.useNamespace()) {
/* 160 */       return new CompositeContextDependentTransducedAccessorImpl(context, xducer, prop.getAccessor());
/*     */     }
/* 162 */     return new CompositeTransducedAccessorImpl(context, xducer, prop.getAccessor());
/*     */   }
/*     */ 
/*     */   public abstract void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString)
/*     */     throws SAXException, AccessorException, IOException, XMLStreamException;
/*     */ 
/*     */   public abstract void writeText(XMLSerializer paramXMLSerializer, BeanT paramBeanT, String paramString)
/*     */     throws AccessorException, SAXException, IOException, XMLStreamException;
/*     */ 
/*     */   static class CompositeContextDependentTransducedAccessorImpl<BeanT, ValueT> extends TransducedAccessor.CompositeTransducedAccessorImpl<BeanT, ValueT>
/*     */   {
/*     */     public CompositeContextDependentTransducedAccessorImpl(JAXBContextImpl context, Transducer<ValueT> xducer, Accessor<BeanT, ValueT> acc)
/*     */     {
/* 184 */       super(xducer, acc);
/* 185 */       assert (xducer.useNamespace());
/*     */     }
/*     */ 
/*     */     public boolean useNamespace()
/*     */     {
/* 190 */       return true;
/*     */     }
/*     */ 
/*     */     public void declareNamespace(BeanT bean, XMLSerializer w) throws AccessorException
/*     */     {
/* 195 */       Object o = this.acc.get(bean);
/* 196 */       if (o != null)
/* 197 */         this.xducer.declareNamespace(o, w);
/*     */     }
/*     */ 
/*     */     public void writeLeafElement(XMLSerializer w, Name tagName, BeanT o, String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException
/*     */     {
/* 202 */       w.startElement(tagName, null);
/* 203 */       declareNamespace(o, w);
/* 204 */       w.endNamespaceDecls(null);
/* 205 */       w.endAttributes();
/* 206 */       this.xducer.writeText(w, this.acc.get(o), fieldName);
/* 207 */       w.endElement();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CompositeTransducedAccessorImpl<BeanT, ValueT> extends TransducedAccessor<BeanT>
/*     */   {
/*     */     protected final Transducer<ValueT> xducer;
/*     */     protected final Accessor<BeanT, ValueT> acc;
/*     */ 
/*     */     public CompositeTransducedAccessorImpl(JAXBContextImpl context, Transducer<ValueT> xducer, Accessor<BeanT, ValueT> acc)
/*     */     {
/* 221 */       this.xducer = xducer;
/* 222 */       this.acc = acc.optimize(context);
/*     */     }
/*     */ 
/*     */     public CharSequence print(BeanT bean) throws AccessorException {
/* 226 */       Object o = this.acc.get(bean);
/* 227 */       if (o == null) return null;
/* 228 */       return this.xducer.print(o);
/*     */     }
/*     */ 
/*     */     public void parse(BeanT bean, CharSequence lexical) throws AccessorException, SAXException {
/* 232 */       this.acc.set(bean, this.xducer.parse(lexical));
/*     */     }
/*     */ 
/*     */     public boolean hasValue(BeanT bean) throws AccessorException {
/* 236 */       return this.acc.getUnadapted(bean) != null;
/*     */     }
/*     */ 
/*     */     public void writeLeafElement(XMLSerializer w, Name tagName, BeanT o, String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException
/*     */     {
/* 241 */       this.xducer.writeLeafElement(w, tagName, this.acc.get(o), fieldName);
/*     */     }
/*     */ 
/*     */     public void writeText(XMLSerializer w, BeanT o, String fieldName) throws AccessorException, SAXException, IOException, XMLStreamException
/*     */     {
/* 246 */       this.xducer.writeText(w, this.acc.get(o), fieldName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class IDREFTransducedAccessorImpl<BeanT, TargetT> extends DefaultTransducedAccessor<BeanT>
/*     */   {
/*     */     private final Accessor<BeanT, TargetT> acc;
/*     */     private final Class<TargetT> targetType;
/*     */ 
/*     */     public IDREFTransducedAccessorImpl(Accessor<BeanT, TargetT> acc)
/*     */     {
/* 265 */       this.acc = acc;
/* 266 */       this.targetType = acc.getValueType();
/*     */     }
/*     */ 
/*     */     public String print(BeanT bean) throws AccessorException, SAXException {
/* 270 */       Object target = this.acc.get(bean);
/* 271 */       if (target == null) return null;
/*     */ 
/* 273 */       XMLSerializer w = XMLSerializer.getInstance();
/*     */       try {
/* 275 */         String id = w.grammar.getBeanInfo(target, true).getId(target, w);
/* 276 */         if (id == null)
/* 277 */           w.errorMissingId(target);
/* 278 */         return id;
/*     */       } catch (JAXBException e) {
/* 280 */         w.reportError(null, e);
/* 281 */       }return null;
/*     */     }
/*     */ 
/*     */     private void assign(BeanT bean, TargetT t, UnmarshallingContext context) throws AccessorException
/*     */     {
/* 286 */       if (!this.targetType.isInstance(t))
/* 287 */         context.handleError(Messages.UNASSIGNABLE_TYPE.format(new Object[] { this.targetType, t.getClass() }));
/*     */       else
/* 289 */         this.acc.set(bean, t);
/*     */     }
/*     */ 
/*     */     public void parse(final BeanT bean, CharSequence lexical) throws AccessorException, SAXException {
/* 293 */       final String idref = WhiteSpaceProcessor.trim(lexical).toString();
/* 294 */       final UnmarshallingContext context = UnmarshallingContext.getInstance();
/*     */ 
/* 296 */       final Callable callable = context.getObjectFromId(idref, this.acc.valueType);
/* 297 */       if (callable == null)
/*     */       {
/* 299 */         context.errorUnresolvedIDREF(bean, idref, context.getLocator());
/*     */         return;
/*     */       }
/*     */       Object t;
/*     */       try {
/* 305 */         t = callable.call();
/*     */       } catch (SAXException e) {
/* 307 */         throw e;
/*     */       } catch (RuntimeException e) {
/* 309 */         throw e;
/*     */       } catch (Exception e) {
/* 311 */         throw new SAXException2(e);
/*     */       }
/* 313 */       if (t != null) {
/* 314 */         assign(bean, t, context);
/*     */       }
/*     */       else {
/* 317 */         final LocatorEx loc = new LocatorEx.Snapshot(context.getLocator());
/* 318 */         context.addPatcher(new Patcher() {
/*     */           public void run() throws SAXException {
/*     */             try {
/* 321 */               Object t = callable.call();
/* 322 */               if (t == null)
/* 323 */                 context.errorUnresolvedIDREF(bean, idref, loc);
/*     */               else
/* 325 */                 TransducedAccessor.IDREFTransducedAccessorImpl.this.assign(bean, t, context);
/*     */             }
/*     */             catch (AccessorException e) {
/* 328 */               context.handleError(e);
/*     */             } catch (SAXException e) {
/* 330 */               throw e;
/*     */             } catch (RuntimeException e) {
/* 332 */               throw e;
/*     */             } catch (Exception e) {
/* 334 */               throw new SAXException2(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean hasValue(BeanT bean) throws AccessorException {
/* 342 */       return this.acc.get(bean) != null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor
 * JD-Core Version:    0.6.2
 */