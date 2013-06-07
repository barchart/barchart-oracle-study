/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.sound.sampled.AudioPermission;
/*     */ import sun.misc.Service;
/*     */ 
/*     */ class JSSecurityManager
/*     */ {
/*     */   private static boolean hasSecurityManager()
/*     */   {
/*  66 */     return System.getSecurityManager() != null;
/*     */   }
/*     */ 
/*     */   static void checkRecordPermission()
/*     */     throws SecurityException
/*     */   {
/*  72 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  73 */     if (localSecurityManager != null)
/*  74 */       localSecurityManager.checkPermission(new AudioPermission("record"));
/*     */   }
/*     */ 
/*     */   static void loadLibrary(String paramString)
/*     */   {
/*     */     try
/*     */     {
/*  81 */       if (hasSecurityManager())
/*     */       {
/*  83 */         PrivilegedAction local1 = new PrivilegedAction() {
/*     */           public Object run() {
/*  85 */             System.loadLibrary(this.val$libName);
/*  86 */             return null;
/*     */           }
/*     */         };
/*  89 */         AccessController.doPrivileged(local1);
/*     */       }
/*     */       else {
/*  92 */         System.loadLibrary(paramString);
/*     */       }
/*     */     }
/*     */     catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
/*     */     {
/*  97 */       throw localUnsatisfiedLinkError;
/*     */     }
/*     */   }
/*     */ 
/*     */   static String getProperty(String paramString)
/*     */   {
/*     */     String str;
/* 104 */     if (hasSecurityManager()) {
/*     */       try
/*     */       {
/* 107 */         PrivilegedAction local2 = new PrivilegedAction() {
/*     */           public Object run() {
/*     */             try {
/* 110 */               return System.getProperty(this.val$propertyName); } catch (Throwable localThrowable) {
/*     */             }
/* 112 */             return null;
/*     */           }
/*     */         };
/* 116 */         str = (String)AccessController.doPrivileged(local2);
/*     */       }
/*     */       catch (Exception localException) {
/* 119 */         str = System.getProperty(paramString);
/*     */       }
/*     */     }
/*     */     else {
/* 123 */       str = System.getProperty(paramString);
/*     */     }
/* 125 */     return str;
/*     */   }
/*     */ 
/*     */   static void loadProperties(Properties paramProperties, final String paramString)
/*     */   {
/* 142 */     if (hasSecurityManager()) {
/*     */       try
/*     */       {
/* 145 */         PrivilegedAction local3 = new PrivilegedAction() {
/*     */           public Object run() {
/* 147 */             JSSecurityManager.loadPropertiesImpl(this.val$properties, paramString);
/* 148 */             return null;
/*     */           }
/*     */         };
/* 151 */         AccessController.doPrivileged(local3);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 156 */         loadPropertiesImpl(paramProperties, paramString);
/*     */       }
/*     */     }
/*     */     else
/* 160 */       loadPropertiesImpl(paramProperties, paramString);
/*     */   }
/*     */ 
/*     */   private static void loadPropertiesImpl(Properties paramProperties, String paramString)
/*     */   {
/* 168 */     String str = System.getProperty("java.home");
/*     */     try {
/* 170 */       if (str == null) {
/* 171 */         throw new Error("Can't find java.home ??");
/*     */       }
/* 173 */       File localFile = new File(str, "lib");
/* 174 */       localFile = new File(localFile, paramString);
/* 175 */       str = localFile.getCanonicalPath();
/* 176 */       FileInputStream localFileInputStream = new FileInputStream(str);
/* 177 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
/*     */       try {
/* 179 */         paramProperties.load(localBufferedInputStream);
/*     */       } finally {
/* 181 */         if (localFileInputStream != null)
/* 182 */           localFileInputStream.close();
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static ThreadGroup getTopmostThreadGroup()
/*     */   {
/*     */     ThreadGroup localThreadGroup;
/* 197 */     if (hasSecurityManager()) {
/*     */       try
/*     */       {
/* 200 */         PrivilegedAction local4 = new PrivilegedAction() {
/*     */           public Object run() {
/*     */             try {
/* 203 */               return JSSecurityManager.access$100(); } catch (Throwable localThrowable) {
/*     */             }
/* 205 */             return null;
/*     */           }
/*     */         };
/* 209 */         localThreadGroup = (ThreadGroup)AccessController.doPrivileged(local4);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 214 */         localThreadGroup = getTopmostThreadGroupImpl();
/*     */       }
/*     */     }
/*     */     else {
/* 218 */       localThreadGroup = getTopmostThreadGroupImpl();
/*     */     }
/* 220 */     return localThreadGroup;
/*     */   }
/*     */ 
/*     */   private static ThreadGroup getTopmostThreadGroupImpl()
/*     */   {
/* 226 */     ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/* 227 */     while ((localThreadGroup.getParent() != null) && (localThreadGroup.getParent().getParent() != null)) {
/* 228 */       localThreadGroup = localThreadGroup.getParent();
/*     */     }
/*     */ 
/* 231 */     return localThreadGroup;
/*     */   }
/*     */ 
/*     */   static Thread createThread(Runnable paramRunnable, final String paramString, final boolean paramBoolean1, final int paramInt, final boolean paramBoolean2)
/*     */   {
/* 241 */     Thread localThread = null;
/* 242 */     if (hasSecurityManager()) {
/* 243 */       PrivilegedAction local5 = new PrivilegedAction() {
/*     */         public Object run() {
/*     */           try {
/* 246 */             return JSSecurityManager.createThreadImpl(this.val$runnable, paramString, paramBoolean1, paramInt, paramBoolean2);
/*     */           }
/*     */           catch (Throwable localThrowable) {
/*     */           }
/* 250 */           return null;
/*     */         }
/*     */       };
/* 254 */       localThread = (Thread)AccessController.doPrivileged(local5);
/*     */     }
/*     */     else
/*     */     {
/* 258 */       localThread = createThreadImpl(paramRunnable, paramString, paramBoolean1, paramInt, paramBoolean2);
/*     */     }
/*     */ 
/* 261 */     return localThread;
/*     */   }
/*     */ 
/*     */   private static Thread createThreadImpl(Runnable paramRunnable, String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
/*     */   {
/* 269 */     ThreadGroup localThreadGroup = getTopmostThreadGroupImpl();
/* 270 */     Thread localThread = new Thread(localThreadGroup, paramRunnable);
/* 271 */     if (paramString != null) {
/* 272 */       localThread.setName(paramString);
/*     */     }
/* 274 */     localThread.setDaemon(paramBoolean1);
/* 275 */     if (paramInt >= 0) {
/* 276 */       localThread.setPriority(paramInt);
/*     */     }
/* 278 */     if (paramBoolean2) {
/* 279 */       localThread.start();
/*     */     }
/* 281 */     return localThread;
/*     */   }
/*     */ 
/*     */   static List getProviders(Class paramClass)
/*     */   {
/* 286 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 289 */     Iterator localIterator = Service.providers(paramClass);
/*     */ 
/* 293 */     PrivilegedAction local6 = new PrivilegedAction() {
/*     */       public Boolean run() {
/* 295 */         return Boolean.valueOf(this.val$ps.hasNext());
/*     */       }
/*     */     };
/* 299 */     while (((Boolean)AccessController.doPrivileged(local6)).booleanValue())
/*     */     {
/*     */       try
/*     */       {
/* 304 */         Object localObject = localIterator.next();
/* 305 */         if (paramClass.isInstance(localObject))
/*     */         {
/* 310 */           localArrayList.add(0, localObject);
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/*     */       }
/*     */     }
/* 317 */     return localArrayList;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.JSSecurityManager
 * JD-Core Version:    0.6.2
 */