/*     */ package com.sun.beans.decoder;
/*     */ 
/*     */ import com.sun.beans.finder.ClassFinder;
/*     */ import java.beans.ExceptionListener;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ import sun.misc.JavaSecurityAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ public final class DocumentHandler extends DefaultHandler
/*     */ {
/*  64 */   private final AccessControlContext acc = AccessController.getContext();
/*  65 */   private final Map<String, Class<? extends ElementHandler>> handlers = new HashMap();
/*  66 */   private final Map<String, Object> environment = new HashMap();
/*  67 */   private final List<Object> objects = new ArrayList();
/*     */   private Reference<ClassLoader> loader;
/*     */   private ExceptionListener listener;
/*     */   private Object owner;
/*     */   private ElementHandler handler;
/*     */ 
/*     */   public DocumentHandler()
/*     */   {
/*  79 */     setElementHandler("java", JavaElementHandler.class);
/*  80 */     setElementHandler("null", NullElementHandler.class);
/*  81 */     setElementHandler("array", ArrayElementHandler.class);
/*  82 */     setElementHandler("class", ClassElementHandler.class);
/*  83 */     setElementHandler("string", StringElementHandler.class);
/*  84 */     setElementHandler("object", ObjectElementHandler.class);
/*     */ 
/*  86 */     setElementHandler("void", VoidElementHandler.class);
/*  87 */     setElementHandler("char", CharElementHandler.class);
/*  88 */     setElementHandler("byte", ByteElementHandler.class);
/*  89 */     setElementHandler("short", ShortElementHandler.class);
/*  90 */     setElementHandler("int", IntElementHandler.class);
/*  91 */     setElementHandler("long", LongElementHandler.class);
/*  92 */     setElementHandler("float", FloatElementHandler.class);
/*  93 */     setElementHandler("double", DoubleElementHandler.class);
/*  94 */     setElementHandler("boolean", BooleanElementHandler.class);
/*     */ 
/*  97 */     setElementHandler("new", NewElementHandler.class);
/*  98 */     setElementHandler("var", VarElementHandler.class);
/*  99 */     setElementHandler("true", TrueElementHandler.class);
/* 100 */     setElementHandler("false", FalseElementHandler.class);
/* 101 */     setElementHandler("field", FieldElementHandler.class);
/* 102 */     setElementHandler("method", MethodElementHandler.class);
/* 103 */     setElementHandler("property", PropertyElementHandler.class);
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader()
/*     */   {
/* 114 */     return this.loader != null ? (ClassLoader)this.loader.get() : null;
/*     */   }
/*     */ 
/*     */   public void setClassLoader(ClassLoader paramClassLoader)
/*     */   {
/* 127 */     this.loader = new WeakReference(paramClassLoader);
/*     */   }
/*     */ 
/*     */   public ExceptionListener getExceptionListener()
/*     */   {
/* 140 */     return this.listener;
/*     */   }
/*     */ 
/*     */   public void setExceptionListener(ExceptionListener paramExceptionListener)
/*     */   {
/* 151 */     this.listener = paramExceptionListener;
/*     */   }
/*     */ 
/*     */   public Object getOwner()
/*     */   {
/* 160 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public void setOwner(Object paramObject)
/*     */   {
/* 169 */     this.owner = paramObject;
/*     */   }
/*     */ 
/*     */   public Class<? extends ElementHandler> getElementHandler(String paramString)
/*     */   {
/* 179 */     Class localClass = (Class)this.handlers.get(paramString);
/* 180 */     if (localClass == null) {
/* 181 */       throw new IllegalArgumentException("Unsupported element: " + paramString);
/*     */     }
/* 183 */     return localClass;
/*     */   }
/*     */ 
/*     */   public void setElementHandler(String paramString, Class<? extends ElementHandler> paramClass)
/*     */   {
/* 193 */     this.handlers.put(paramString, paramClass);
/*     */   }
/*     */ 
/*     */   public boolean hasVariable(String paramString)
/*     */   {
/* 204 */     return this.environment.containsKey(paramString);
/*     */   }
/*     */ 
/*     */   public Object getVariable(String paramString)
/*     */   {
/* 214 */     if (!this.environment.containsKey(paramString)) {
/* 215 */       throw new IllegalArgumentException("Unbound variable: " + paramString);
/*     */     }
/* 217 */     return this.environment.get(paramString);
/*     */   }
/*     */ 
/*     */   public void setVariable(String paramString, Object paramObject)
/*     */   {
/* 227 */     this.environment.put(paramString, paramObject);
/*     */   }
/*     */ 
/*     */   public Object[] getObjects()
/*     */   {
/* 236 */     return this.objects.toArray();
/*     */   }
/*     */ 
/*     */   void addObject(Object paramObject)
/*     */   {
/* 245 */     this.objects.add(paramObject);
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */   {
/* 253 */     this.objects.clear();
/* 254 */     this.handler = null;
/*     */   }
/*     */ 
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
/*     */     throws SAXException
/*     */   {
/* 272 */     ElementHandler localElementHandler = this.handler;
/*     */     try {
/* 274 */       this.handler = ((ElementHandler)getElementHandler(paramString3).newInstance());
/* 275 */       this.handler.setOwner(this);
/* 276 */       this.handler.setParent(localElementHandler);
/*     */     }
/*     */     catch (Exception localException) {
/* 279 */       throw new SAXException(localException);
/*     */     }
/* 281 */     for (int i = 0; i < paramAttributes.getLength(); i++) {
/*     */       try {
/* 283 */         String str1 = paramAttributes.getQName(i);
/* 284 */         String str2 = paramAttributes.getValue(i);
/* 285 */         this.handler.addAttribute(str1, str2);
/*     */       }
/*     */       catch (RuntimeException localRuntimeException) {
/* 288 */         handleException(localRuntimeException);
/*     */       }
/*     */     }
/* 291 */     this.handler.startElement();
/*     */   }
/*     */ 
/*     */   public void endElement(String paramString1, String paramString2, String paramString3)
/*     */   {
/*     */     try
/*     */     {
/* 309 */       this.handler.endElement();
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 312 */       handleException(localRuntimeException);
/*     */     }
/*     */     finally {
/* 315 */       this.handler = this.handler.getParent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*     */   {
/* 328 */     if (this.handler != null)
/*     */       try {
/* 330 */         while (0 < paramInt2--)
/* 331 */           this.handler.addCharacter(paramArrayOfChar[(paramInt1++)]);
/*     */       }
/*     */       catch (RuntimeException localRuntimeException)
/*     */       {
/* 335 */         handleException(localRuntimeException);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void handleException(Exception paramException)
/*     */   {
/* 347 */     if (this.listener == null) {
/* 348 */       throw new IllegalStateException(paramException);
/*     */     }
/* 350 */     this.listener.exceptionThrown(paramException);
/*     */   }
/*     */ 
/*     */   public void parse(final InputSource paramInputSource)
/*     */   {
/* 359 */     if ((this.acc == null) && (null != System.getSecurityManager())) {
/* 360 */       throw new SecurityException("AccessControlContext is not set");
/*     */     }
/* 362 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/* 363 */     SharedSecrets.getJavaSecurityAccess().doIntersectionPrivilege(new PrivilegedAction() {
/*     */       public Void run() {
/*     */         try {
/* 366 */           SAXParserFactory.newInstance().newSAXParser().parse(paramInputSource, DocumentHandler.this);
/*     */         }
/*     */         catch (ParserConfigurationException localParserConfigurationException) {
/* 369 */           DocumentHandler.this.handleException(localParserConfigurationException);
/*     */         }
/*     */         catch (SAXException localSAXException) {
/* 372 */           Object localObject = localSAXException.getException();
/* 373 */           if (localObject == null) {
/* 374 */             localObject = localSAXException;
/*     */           }
/* 376 */           DocumentHandler.this.handleException((Exception)localObject);
/*     */         }
/*     */         catch (IOException localIOException) {
/* 379 */           DocumentHandler.this.handleException(localIOException);
/*     */         }
/* 381 */         return null;
/*     */       }
/*     */     }
/*     */     , localAccessControlContext, this.acc);
/*     */   }
/*     */ 
/*     */   public Class<?> findClass(String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 395 */       return ClassFinder.resolveClass(paramString, getClassLoader());
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 398 */       handleException(localClassNotFoundException);
/* 399 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.DocumentHandler
 * JD-Core Version:    0.6.2
 */