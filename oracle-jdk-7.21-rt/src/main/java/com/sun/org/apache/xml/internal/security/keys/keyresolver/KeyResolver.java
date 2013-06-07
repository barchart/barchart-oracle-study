/*     */ package com.sun.org.apache.xml.internal.security.keys.keyresolver;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.crypto.SecretKey;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class KeyResolver
/*     */ {
/*  48 */   static Logger log = Logger.getLogger(KeyResolver.class.getName());
/*     */ 
/*  52 */   static boolean _alreadyInitialized = false;
/*     */ 
/*  55 */   static List _resolverVector = null;
/*     */ 
/*  58 */   protected KeyResolverSpi _resolverSpi = null;
/*     */ 
/*  61 */   protected StorageResolver _storage = null;
/*     */ 
/*     */   private KeyResolver(String paramString)
/*     */     throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*     */   {
/*  74 */     this._resolverSpi = ((KeyResolverSpi)Class.forName(paramString).newInstance());
/*     */ 
/*  76 */     this._resolverSpi.setGlobalResolver(true);
/*     */   }
/*     */ 
/*     */   public static int length()
/*     */   {
/*  85 */     return _resolverVector.size();
/*     */   }
/*     */ 
/*     */   public static void hit(Iterator paramIterator) {
/*  89 */     ResolverIterator localResolverIterator = (ResolverIterator)paramIterator;
/*  90 */     int i = localResolverIterator.i;
/*  91 */     if ((i != 1) && (localResolverIterator.res == _resolverVector)) {
/*  92 */       List localList = (List)((ArrayList)_resolverVector).clone();
/*  93 */       Object localObject = localList.remove(i - 1);
/*  94 */       localList.add(0, localObject);
/*  95 */       _resolverVector = localList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final X509Certificate getX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 116 */     List localList = _resolverVector;
/* 117 */     for (int i = 0; i < localList.size(); i++) {
/* 118 */       KeyResolver localKeyResolver = (KeyResolver)localList.get(i);
/*     */ 
/* 121 */       if (localKeyResolver == null) {
/* 122 */         localObject = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
/*     */ 
/* 128 */         throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject);
/*     */       }
/* 130 */       if (log.isLoggable(Level.FINE)) {
/* 131 */         log.log(Level.FINE, "check resolvability by class " + localKeyResolver.getClass());
/*     */       }
/* 133 */       Object localObject = localKeyResolver.resolveX509Certificate(paramElement, paramString, paramStorageResolver);
/* 134 */       if (localObject != null) {
/* 135 */         return localObject;
/*     */       }
/*     */     }
/*     */ 
/* 139 */     Object[] arrayOfObject = { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
/*     */ 
/* 144 */     throw new KeyResolverException("utils.resolver.noClass", arrayOfObject);
/*     */   }
/*     */ 
/*     */   public static final PublicKey getPublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 160 */     List localList = _resolverVector;
/* 161 */     for (int i = 0; i < localList.size(); i++) {
/* 162 */       KeyResolver localKeyResolver = (KeyResolver)localList.get(i);
/*     */ 
/* 165 */       if (localKeyResolver == null) {
/* 166 */         localObject1 = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
/*     */ 
/* 172 */         throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject1);
/*     */       }
/* 174 */       if (log.isLoggable(Level.FINE)) {
/* 175 */         log.log(Level.FINE, "check resolvability by class " + localKeyResolver.getClass());
/*     */       }
/* 177 */       Object localObject1 = localKeyResolver.resolvePublicKey(paramElement, paramString, paramStorageResolver);
/* 178 */       if (localObject1 != null) {
/* 179 */         if ((i != 0) && (localList == _resolverVector))
/*     */         {
/* 181 */           localList = (List)((ArrayList)_resolverVector).clone();
/* 182 */           Object localObject2 = localList.remove(i);
/* 183 */           localList.add(0, localObject2);
/* 184 */           _resolverVector = localList;
/*     */         }
/* 186 */         return localObject1;
/*     */       }
/*     */     }
/*     */ 
/* 190 */     Object[] arrayOfObject = { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
/*     */ 
/* 195 */     throw new KeyResolverException("utils.resolver.noClass", arrayOfObject);
/*     */   }
/*     */ 
/*     */   public static void init()
/*     */   {
/* 203 */     if (!_alreadyInitialized) {
/* 204 */       _resolverVector = new ArrayList(10);
/* 205 */       _alreadyInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void register(String paramString)
/*     */     throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*     */   {
/* 222 */     _resolverVector.add(new KeyResolver(paramString));
/*     */   }
/*     */ 
/*     */   public static void registerAtStart(String paramString)
/*     */   {
/* 234 */     _resolverVector.add(0, paramString);
/*     */   }
/*     */ 
/*     */   public PublicKey resolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 250 */     return this._resolverSpi.engineLookupAndResolvePublicKey(paramElement, paramString, paramStorageResolver);
/*     */   }
/*     */ 
/*     */   public X509Certificate resolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 266 */     return this._resolverSpi.engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
/*     */   }
/*     */ 
/*     */   public SecretKey resolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 280 */     return this._resolverSpi.engineLookupAndResolveSecretKey(paramElement, paramString, paramStorageResolver);
/*     */   }
/*     */ 
/*     */   public void setProperty(String paramString1, String paramString2)
/*     */   {
/* 291 */     this._resolverSpi.engineSetProperty(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public String getProperty(String paramString)
/*     */   {
/* 301 */     return this._resolverSpi.engineGetProperty(paramString);
/*     */   }
/*     */ 
/*     */   public boolean understandsProperty(String paramString)
/*     */   {
/* 312 */     return this._resolverSpi.understandsProperty(paramString);
/*     */   }
/*     */ 
/*     */   public String resolverClassName()
/*     */   {
/* 322 */     return this._resolverSpi.getClass().getName();
/*     */   }
/*     */ 
/*     */   public static Iterator iterator()
/*     */   {
/* 355 */     return new ResolverIterator(_resolverVector);
/*     */   }
/*     */ 
/*     */   static class ResolverIterator
/*     */     implements Iterator
/*     */   {
/*     */     List res;
/*     */     Iterator it;
/*     */     int i;
/*     */ 
/*     */     public ResolverIterator(List paramList)
/*     */     {
/* 330 */       this.res = paramList;
/* 331 */       this.it = this.res.iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 335 */       return this.it.hasNext();
/*     */     }
/*     */ 
/*     */     public Object next() {
/* 339 */       this.i += 1;
/* 340 */       KeyResolver localKeyResolver = (KeyResolver)this.it.next();
/* 341 */       if (localKeyResolver == null) {
/* 342 */         throw new RuntimeException("utils.resolver.noClass");
/*     */       }
/*     */ 
/* 345 */       return localKeyResolver._resolverSpi;
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver
 * JD-Core Version:    0.6.2
 */