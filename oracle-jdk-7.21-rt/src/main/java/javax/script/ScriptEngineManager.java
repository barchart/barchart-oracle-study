/*     */ package javax.script;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.misc.Service;
/*     */ import sun.misc.ServiceConfigurationError;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ public class ScriptEngineManager
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private HashSet<ScriptEngineFactory> engineSpis;
/*     */   private HashMap<String, ScriptEngineFactory> nameAssociations;
/*     */   private HashMap<String, ScriptEngineFactory> extensionAssociations;
/*     */   private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
/*     */   private Bindings globalScope;
/*     */ 
/*     */   public ScriptEngineManager()
/*     */   {
/*  66 */     ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*  67 */     if (canCallerAccessLoader(localClassLoader))
/*     */     {
/*  69 */       init(localClassLoader);
/*     */     }
/*     */     else
/*  72 */       init(null);
/*     */   }
/*     */ 
/*     */   public ScriptEngineManager(ClassLoader paramClassLoader)
/*     */   {
/*  87 */     init(paramClassLoader);
/*     */   }
/*     */ 
/*     */   private void init(final ClassLoader paramClassLoader) {
/*  91 */     this.globalScope = new SimpleBindings();
/*  92 */     this.engineSpis = new HashSet();
/*  93 */     this.nameAssociations = new HashMap();
/*  94 */     this.extensionAssociations = new HashMap();
/*  95 */     this.mimeTypeAssociations = new HashMap();
/*  96 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*  98 */         ScriptEngineManager.this.initEngines(paramClassLoader);
/*  99 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void initEngines(ClassLoader paramClassLoader) {
/* 105 */     Iterator localIterator = null;
/*     */     try {
/* 107 */       if (paramClassLoader != null)
/* 108 */         localIterator = Service.providers(ScriptEngineFactory.class, paramClassLoader);
/*     */       else
/* 110 */         localIterator = Service.installedProviders(ScriptEngineFactory.class);
/*     */     }
/*     */     catch (ServiceConfigurationError localServiceConfigurationError1) {
/* 113 */       System.err.println("Can't find ScriptEngineFactory providers: " + localServiceConfigurationError1.getMessage());
/*     */ 
/* 121 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 125 */       while (localIterator.hasNext()) {
/*     */         try {
/* 127 */           ScriptEngineFactory localScriptEngineFactory = (ScriptEngineFactory)localIterator.next();
/* 128 */           this.engineSpis.add(localScriptEngineFactory);
/*     */         } catch (ServiceConfigurationError localServiceConfigurationError2) {
/* 130 */           System.err.println("ScriptEngineManager providers.next(): " + localServiceConfigurationError2.getMessage());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (ServiceConfigurationError localServiceConfigurationError3)
/*     */     {
/* 140 */       System.err.println("ScriptEngineManager providers.hasNext(): " + localServiceConfigurationError3.getMessage());
/*     */ 
/* 148 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBindings(Bindings paramBindings)
/*     */   {
/* 162 */     if (paramBindings == null) {
/* 163 */       throw new IllegalArgumentException("Global scope cannot be null.");
/*     */     }
/*     */ 
/* 166 */     this.globalScope = paramBindings;
/*     */   }
/*     */ 
/*     */   public Bindings getBindings()
/*     */   {
/* 177 */     return this.globalScope;
/*     */   }
/*     */ 
/*     */   public void put(String paramString, Object paramObject)
/*     */   {
/* 188 */     this.globalScope.put(paramString, paramObject);
/*     */   }
/*     */ 
/*     */   public Object get(String paramString)
/*     */   {
/* 197 */     return this.globalScope.get(paramString);
/*     */   }
/*     */ 
/*     */   public ScriptEngine getEngineByName(String paramString)
/*     */   {
/* 217 */     if (paramString == null) throw new NullPointerException();
/*     */     Object localObject1;
/* 220 */     if (null != (localObject1 = this.nameAssociations.get(paramString))) {
/* 221 */       localObject2 = (ScriptEngineFactory)localObject1;
/*     */       try {
/* 223 */         ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
/* 224 */         localScriptEngine1.setBindings(getBindings(), 200);
/* 225 */         return localScriptEngine1;
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/* 231 */     for (Object localObject2 = this.engineSpis.iterator(); ((Iterator)localObject2).hasNext(); ) { localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
/* 232 */       List localList = null;
/*     */       try {
/* 234 */         localList = localScriptEngineFactory.getNames();
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/*     */       }
/* 239 */       if (localList != null)
/* 240 */         for (String str : localList)
/* 241 */           if (paramString.equals(str))
/*     */             try {
/* 243 */               ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
/* 244 */               localScriptEngine2.setBindings(getBindings(), 200);
/* 245 */               return localScriptEngine2;
/*     */             }
/*     */             catch (Exception localException3)
/*     */             {
/*     */             }
/*     */     }
/*     */     ScriptEngineFactory localScriptEngineFactory;
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */   public ScriptEngine getEngineByExtension(String paramString)
/*     */   {
/* 268 */     if (paramString == null) throw new NullPointerException();
/*     */     Object localObject1;
/* 271 */     if (null != (localObject1 = this.extensionAssociations.get(paramString))) {
/* 272 */       localObject2 = (ScriptEngineFactory)localObject1;
/*     */       try {
/* 274 */         ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
/* 275 */         localScriptEngine1.setBindings(getBindings(), 200);
/* 276 */         return localScriptEngine1;
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/* 282 */     for (Object localObject2 = this.engineSpis.iterator(); ((Iterator)localObject2).hasNext(); ) { localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
/* 283 */       List localList = null;
/*     */       try {
/* 285 */         localList = localScriptEngineFactory.getExtensions();
/*     */       }
/*     */       catch (Exception localException2) {
/*     */       }
/* 289 */       if (localList != null)
/* 290 */         for (String str : localList)
/* 291 */           if (paramString.equals(str))
/*     */             try {
/* 293 */               ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
/* 294 */               localScriptEngine2.setBindings(getBindings(), 200);
/* 295 */               return localScriptEngine2;
/*     */             }
/*     */             catch (Exception localException3)
/*     */             {
/*     */             }
/*     */     }
/*     */     ScriptEngineFactory localScriptEngineFactory;
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */   public ScriptEngine getEngineByMimeType(String paramString)
/*     */   {
/* 316 */     if (paramString == null) throw new NullPointerException();
/*     */     Object localObject1;
/* 319 */     if (null != (localObject1 = this.mimeTypeAssociations.get(paramString))) {
/* 320 */       localObject2 = (ScriptEngineFactory)localObject1;
/*     */       try {
/* 322 */         ScriptEngine localScriptEngine1 = ((ScriptEngineFactory)localObject2).getScriptEngine();
/* 323 */         localScriptEngine1.setBindings(getBindings(), 200);
/* 324 */         return localScriptEngine1;
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/* 330 */     for (Object localObject2 = this.engineSpis.iterator(); ((Iterator)localObject2).hasNext(); ) { localScriptEngineFactory = (ScriptEngineFactory)((Iterator)localObject2).next();
/* 331 */       List localList = null;
/*     */       try {
/* 333 */         localList = localScriptEngineFactory.getMimeTypes();
/*     */       }
/*     */       catch (Exception localException2) {
/*     */       }
/* 337 */       if (localList != null)
/* 338 */         for (String str : localList)
/* 339 */           if (paramString.equals(str))
/*     */             try {
/* 341 */               ScriptEngine localScriptEngine2 = localScriptEngineFactory.getScriptEngine();
/* 342 */               localScriptEngine2.setBindings(getBindings(), 200);
/* 343 */               return localScriptEngine2;
/*     */             }
/*     */             catch (Exception localException3)
/*     */             {
/*     */             }
/*     */     }
/*     */     ScriptEngineFactory localScriptEngineFactory;
/* 350 */     return null;
/*     */   }
/*     */ 
/*     */   public List<ScriptEngineFactory> getEngineFactories()
/*     */   {
/* 359 */     ArrayList localArrayList = new ArrayList(this.engineSpis.size());
/* 360 */     for (ScriptEngineFactory localScriptEngineFactory : this.engineSpis) {
/* 361 */       localArrayList.add(localScriptEngineFactory);
/*     */     }
/* 363 */     return Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public void registerEngineName(String paramString, ScriptEngineFactory paramScriptEngineFactory)
/*     */   {
/* 374 */     if ((paramString == null) || (paramScriptEngineFactory == null)) throw new NullPointerException();
/* 375 */     this.nameAssociations.put(paramString, paramScriptEngineFactory);
/*     */   }
/*     */ 
/*     */   public void registerEngineMimeType(String paramString, ScriptEngineFactory paramScriptEngineFactory)
/*     */   {
/* 389 */     if ((paramString == null) || (paramScriptEngineFactory == null)) throw new NullPointerException();
/* 390 */     this.mimeTypeAssociations.put(paramString, paramScriptEngineFactory);
/*     */   }
/*     */ 
/*     */   public void registerEngineExtension(String paramString, ScriptEngineFactory paramScriptEngineFactory)
/*     */   {
/* 403 */     if ((paramString == null) || (paramScriptEngineFactory == null)) throw new NullPointerException();
/* 404 */     this.extensionAssociations.put(paramString, paramScriptEngineFactory);
/*     */   }
/*     */ 
/*     */   private boolean canCallerAccessLoader(ClassLoader paramClassLoader)
/*     */   {
/* 423 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 424 */     if (localSecurityManager != null) {
/* 425 */       ClassLoader localClassLoader = getCallerClassLoader();
/* 426 */       if ((localClassLoader != null) && (
/* 427 */         (paramClassLoader != localClassLoader) || (!isAncestor(paramClassLoader, localClassLoader)))) {
/*     */         try {
/* 429 */           localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*     */         }
/*     */         catch (SecurityException localSecurityException) {
/* 432 */           return false;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 438 */     return true;
/*     */   }
/*     */ 
/*     */   private ClassLoader getCallerClassLoader()
/*     */   {
/* 444 */     Class localClass = Reflection.getCallerClass(3);
/* 445 */     if (localClass == null) {
/* 446 */       return null;
/*     */     }
/* 448 */     return localClass.getClassLoader();
/*     */   }
/*     */ 
/*     */   private boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/*     */     do {
/* 454 */       paramClassLoader2 = paramClassLoader2.getParent();
/* 455 */       if (paramClassLoader1 == paramClassLoader2) return true; 
/*     */     }
/* 456 */     while (paramClassLoader2 != null);
/* 457 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.script.ScriptEngineManager
 * JD-Core Version:    0.6.2
 */