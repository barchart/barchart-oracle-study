/*     */ package com.sun.org.apache.xml.internal.security.utils.resolver;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Attr;
/*     */ 
/*     */ public class ResourceResolver
/*     */ {
/*  52 */   static Logger log = Logger.getLogger(ResourceResolver.class.getName());
/*     */ 
/*  56 */   static boolean _alreadyInitialized = false;
/*     */ 
/*  59 */   static List _resolverVector = null;
/*     */ 
/*  61 */   static boolean allThreadSafeInList = true;
/*     */ 
/*  64 */   protected ResourceResolverSpi _resolverSpi = null;
/*     */ 
/*     */   private ResourceResolver(String paramString)
/*     */     throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*     */   {
/*  77 */     this._resolverSpi = ((ResourceResolverSpi)Class.forName(paramString).newInstance());
/*     */   }
/*     */ 
/*     */   public ResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
/*     */   {
/*  87 */     this._resolverSpi = paramResourceResolverSpi;
/*     */   }
/*     */ 
/*     */   public static final ResourceResolver getInstance(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException
/*     */   {
/* 102 */     int i = _resolverVector.size();
/* 103 */     for (int j = 0; j < i; j++) {
/* 104 */       ResourceResolver localResourceResolver1 = (ResourceResolver)_resolverVector.get(j);
/*     */ 
/* 106 */       ResourceResolver localResourceResolver2 = null;
/*     */       try {
/* 108 */         localResourceResolver2 = (allThreadSafeInList) || (localResourceResolver1._resolverSpi.engineIsThreadSafe()) ? localResourceResolver1 : new ResourceResolver((ResourceResolverSpi)localResourceResolver1._resolverSpi.getClass().newInstance());
/*     */       }
/*     */       catch (InstantiationException localInstantiationException) {
/* 111 */         throw new ResourceResolverException("", localInstantiationException, paramAttr, paramString);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 113 */         throw new ResourceResolverException("", localIllegalAccessException, paramAttr, paramString);
/*     */       }
/*     */ 
/* 116 */       if (log.isLoggable(Level.FINE)) {
/* 117 */         log.log(Level.FINE, "check resolvability by class " + localResourceResolver1._resolverSpi.getClass().getName());
/*     */       }
/* 119 */       if ((localResourceResolver1 != null) && (localResourceResolver2.canResolve(paramAttr, paramString))) {
/* 120 */         if (j != 0)
/*     */         {
/* 123 */           List localList = (List)((ArrayList)_resolverVector).clone();
/* 124 */           localList.remove(j);
/* 125 */           localList.add(0, localResourceResolver1);
/* 126 */           _resolverVector = localList;
/*     */         }
/*     */ 
/* 131 */         return localResourceResolver2;
/*     */       }
/*     */     }
/*     */ 
/* 135 */     Object[] arrayOfObject = { paramAttr != null ? paramAttr.getNodeValue() : "null", paramString };
/*     */ 
/* 139 */     throw new ResourceResolverException("utils.resolver.noClass", arrayOfObject, paramAttr, paramString);
/*     */   }
/*     */ 
/*     */   public static final ResourceResolver getInstance(Attr paramAttr, String paramString, List paramList)
/*     */     throws ResourceResolverException
/*     */   {
/* 155 */     if (log.isLoggable(Level.FINE))
/*     */     {
/* 157 */       log.log(Level.FINE, "I was asked to create a ResourceResolver and got " + (paramList == null ? 0 : paramList.size()));
/* 158 */       log.log(Level.FINE, " extra resolvers to my existing " + _resolverVector.size() + " system-wide resolvers");
/*     */     }
/*     */ 
/* 162 */     int i = 0;
/* 163 */     if ((paramList != null) && ((i = paramList.size()) > 0)) {
/* 164 */       for (int j = 0; j < i; j++) {
/* 165 */         ResourceResolver localResourceResolver = (ResourceResolver)paramList.get(j);
/*     */ 
/* 168 */         if (localResourceResolver != null) {
/* 169 */           String str = localResourceResolver._resolverSpi.getClass().getName();
/* 170 */           if (log.isLoggable(Level.FINE)) {
/* 171 */             log.log(Level.FINE, "check resolvability by class " + str);
/*     */           }
/* 173 */           if (localResourceResolver.canResolve(paramAttr, paramString)) {
/* 174 */             return localResourceResolver;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 180 */     return getInstance(paramAttr, paramString);
/*     */   }
/*     */ 
/*     */   public static void init()
/*     */   {
/* 188 */     if (!_alreadyInitialized) {
/* 189 */       _resolverVector = new ArrayList(10);
/* 190 */       _alreadyInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void register(String paramString)
/*     */   {
/* 202 */     register(paramString, false);
/*     */   }
/*     */ 
/*     */   public static void registerAtStart(String paramString)
/*     */   {
/* 213 */     register(paramString, true);
/*     */   }
/*     */ 
/*     */   private static void register(String paramString, boolean paramBoolean) {
/*     */     try {
/* 218 */       ResourceResolver localResourceResolver = new ResourceResolver(paramString);
/* 219 */       if (paramBoolean) {
/* 220 */         _resolverVector.add(0, localResourceResolver);
/* 221 */         log.log(Level.FINE, "registered resolver");
/*     */       } else {
/* 223 */         _resolverVector.add(localResourceResolver);
/*     */       }
/* 225 */       if (!localResourceResolver._resolverSpi.engineIsThreadSafe())
/* 226 */         allThreadSafeInList = false;
/*     */     }
/*     */     catch (Exception localException) {
/* 229 */       log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
/*     */     } catch (NoClassDefFoundError localNoClassDefFoundError) {
/* 231 */       log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static XMLSignatureInput resolveStatic(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException
/*     */   {
/* 247 */     ResourceResolver localResourceResolver = getInstance(paramAttr, paramString);
/*     */ 
/* 249 */     return localResourceResolver.resolve(paramAttr, paramString);
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput resolve(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException
/*     */   {
/* 263 */     return this._resolverSpi.engineResolve(paramAttr, paramString);
/*     */   }
/*     */ 
/*     */   public void setProperty(String paramString1, String paramString2)
/*     */   {
/* 273 */     this._resolverSpi.engineSetProperty(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public String getProperty(String paramString)
/*     */   {
/* 283 */     return this._resolverSpi.engineGetProperty(paramString);
/*     */   }
/*     */ 
/*     */   public void addProperties(Map paramMap)
/*     */   {
/* 292 */     this._resolverSpi.engineAddProperies(paramMap);
/*     */   }
/*     */ 
/*     */   public String[] getPropertyKeys()
/*     */   {
/* 301 */     return this._resolverSpi.engineGetPropertyKeys();
/*     */   }
/*     */ 
/*     */   public boolean understandsProperty(String paramString)
/*     */   {
/* 311 */     return this._resolverSpi.understandsProperty(paramString);
/*     */   }
/*     */ 
/*     */   private boolean canResolve(Attr paramAttr, String paramString)
/*     */   {
/* 322 */     return this._resolverSpi.engineCanResolve(paramAttr, paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver
 * JD-Core Version:    0.6.2
 */