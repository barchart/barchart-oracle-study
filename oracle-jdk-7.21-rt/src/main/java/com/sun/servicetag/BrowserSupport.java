/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URI;
/*     */ 
/*     */ class BrowserSupport
/*     */ {
/*  45 */   private static boolean isBrowseSupported = false;
/*  46 */   private static Method browseMethod = null;
/*  47 */   private static Object desktop = null;
/*  48 */   private static volatile Boolean result = Boolean.valueOf(false);
/*     */ 
/*     */   private static void initX()
/*     */   {
/*  52 */     if (desktop != null) {
/*  53 */       return;
/*     */     }
/*  55 */     boolean bool = false;
/*  56 */     Method localMethod1 = null;
/*  57 */     Object localObject1 = null;
/*     */     try
/*     */     {
/*  60 */       Class localClass1 = Class.forName("java.awt.Desktop", true, null);
/*  61 */       localObject2 = localClass1.getMethod("getDesktop", new Class[0]);
/*  62 */       localMethod1 = localClass1.getMethod("browse", new Class[] { URI.class });
/*     */ 
/*  64 */       Class localClass2 = Class.forName("java.awt.Desktop$Action", true, null);
/*  65 */       Method localMethod2 = localClass1.getMethod("isDesktopSupported", new Class[0]);
/*  66 */       Method localMethod3 = localClass1.getMethod("isSupported", new Class[] { localClass2 });
/*  67 */       Field localField = localClass2.getField("BROWSE");
/*     */ 
/*  72 */       Thread local1 = new Thread()
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try {
/*  77 */             BrowserSupport.access$002((Boolean)this.val$isDesktopSupportedMethod.invoke(null, new Object[0]));
/*     */           }
/*     */           catch (IllegalAccessException localIllegalAccessException) {
/*  80 */             InternalError localInternalError = new InternalError("Desktop.getDesktop() method not found");
/*     */ 
/*  82 */             localInternalError.initCause(localIllegalAccessException);
/*     */           }
/*     */           catch (InvocationTargetException localInvocationTargetException) {
/*  85 */             if (Util.isVerbose())
/*  86 */               localInvocationTargetException.printStackTrace();
/*     */           }
/*     */         }
/*     */       };
/*  92 */       local1.setDaemon(true);
/*  93 */       local1.start();
/*     */       try {
/*  95 */         local1.join(5000L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {
/*     */       }
/*  99 */       if (result.booleanValue()) {
/* 100 */         localObject1 = ((Method)localObject2).invoke(null, new Object[0]);
/* 101 */         result = (Boolean)localMethod3.invoke(localObject1, new Object[] { localField.get(null) });
/* 102 */         bool = result.booleanValue();
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 106 */       if (Util.isVerbose())
/* 107 */         localClassNotFoundException.printStackTrace();
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException)
/*     */     {
/* 111 */       if (Util.isVerbose())
/* 112 */         localNoSuchMethodException.printStackTrace();
/*     */     }
/*     */     catch (NoSuchFieldException localNoSuchFieldException)
/*     */     {
/* 116 */       if (Util.isVerbose())
/* 117 */         localNoSuchFieldException.printStackTrace();
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException)
/*     */     {
/* 121 */       Object localObject2 = new InternalError("Desktop.getDesktop() method not found");
/*     */ 
/* 123 */       ((InternalError)localObject2).initCause(localIllegalAccessException);
/* 124 */       throw ((Throwable)localObject2);
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException) {
/* 127 */       if (Util.isVerbose()) {
/* 128 */         localInvocationTargetException.printStackTrace();
/*     */       }
/*     */     }
/* 131 */     isBrowseSupported = bool;
/* 132 */     browseMethod = localMethod1;
/* 133 */     desktop = localObject1;
/*     */   }
/*     */ 
/*     */   static boolean isSupported() {
/* 137 */     initX();
/* 138 */     return isBrowseSupported;
/*     */   }
/*     */ 
/*     */   static void browse(URI paramURI)
/*     */     throws IOException
/*     */   {
/* 163 */     if (paramURI == null) {
/* 164 */       throw new NullPointerException("null uri");
/*     */     }
/* 166 */     if (!isSupported()) {
/* 167 */       throw new UnsupportedOperationException("Browse operation is not supported");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 172 */       if (Util.isVerbose()) {
/* 173 */         System.out.println("desktop: " + desktop + ":browsing..." + paramURI);
/*     */       }
/* 175 */       browseMethod.invoke(desktop, new Object[] { paramURI });
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 178 */       localObject = new InternalError("Desktop.getDesktop() method not found");
/*     */ 
/* 180 */       ((InternalError)localObject).initCause(localIllegalAccessException);
/* 181 */       throw ((Throwable)localObject);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 183 */       Object localObject = localInvocationTargetException.getCause();
/* 184 */       if (localObject != null) {
/* 185 */         if ((localObject instanceof UnsupportedOperationException))
/* 186 */           throw ((UnsupportedOperationException)localObject);
/* 187 */         if ((localObject instanceof IllegalArgumentException))
/* 188 */           throw ((IllegalArgumentException)localObject);
/* 189 */         if ((localObject instanceof IOException))
/* 190 */           throw ((IOException)localObject);
/* 191 */         if ((localObject instanceof SecurityException))
/* 192 */           throw ((SecurityException)localObject);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.BrowserSupport
 * JD-Core Version:    0.6.2
 */