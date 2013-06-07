/*     */ package com.sun.org.apache.xerces.internal.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.PropertyResourceBundle;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public final class SecuritySupport
/*     */ {
/*  45 */   private static final SecuritySupport securitySupport = new SecuritySupport();
/*     */ 
/*     */   public static SecuritySupport getInstance()
/*     */   {
/*  51 */     return securitySupport;
/*     */   }
/*     */ 
/*     */   static ClassLoader getContextClassLoader() {
/*  55 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  58 */         ClassLoader cl = null;
/*     */         try {
/*  60 */           cl = Thread.currentThread().getContextClassLoader(); } catch (SecurityException ex) {
/*     */         }
/*  62 */         return cl;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static ClassLoader getSystemClassLoader() {
/*  68 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  71 */         ClassLoader cl = null;
/*     */         try {
/*  73 */           cl = ClassLoader.getSystemClassLoader(); } catch (SecurityException ex) {
/*     */         }
/*  75 */         return cl;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static ClassLoader getParentClassLoader(ClassLoader cl) {
/*  81 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  84 */         ClassLoader parent = null;
/*     */         try {
/*  86 */           parent = this.val$cl.getParent();
/*     */         }
/*     */         catch (SecurityException ex)
/*     */         {
/*     */         }
/*  91 */         return parent == this.val$cl ? null : parent;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String propName) {
/*  97 */     return (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 100 */         return System.getProperty(this.val$propName);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static FileInputStream getFileInputStream(File file) throws FileNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 109 */       return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Object run() throws FileNotFoundException {
/* 112 */           return new FileInputStream(this.val$file);
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException e) {
/* 116 */       throw ((FileNotFoundException)e.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(String name)
/*     */   {
/* 124 */     if (System.getSecurityManager() != null) {
/* 125 */       return getResourceAsStream(null, name);
/*     */     }
/* 127 */     return getResourceAsStream(ObjectFactory.findClassLoader(), name);
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(ClassLoader cl, final String name)
/*     */   {
/* 134 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*     */         InputStream ris;
/*     */         InputStream ris;
/* 138 */         if (this.val$cl == null)
/* 139 */           ris = Object.class.getResourceAsStream("/" + name);
/*     */         else {
/* 141 */           ris = this.val$cl.getResourceAsStream(name);
/*     */         }
/* 143 */         return ris;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static ResourceBundle getResourceBundle(String bundle)
/*     */   {
/* 154 */     return getResourceBundle(bundle, Locale.getDefault());
/*     */   }
/*     */ 
/*     */   public static ResourceBundle getResourceBundle(String bundle, final Locale locale)
/*     */   {
/* 164 */     return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public ResourceBundle run() {
/*     */         try {
/* 167 */           return PropertyResourceBundle.getBundle(this.val$bundle, locale);
/*     */         } catch (MissingResourceException e) {
/*     */           try {
/* 170 */             return PropertyResourceBundle.getBundle(this.val$bundle, new Locale("en", "US")); } catch (MissingResourceException e2) {  }
/*     */         }
/* 172 */         throw new MissingResourceException("Could not load any resource bundle by " + this.val$bundle, this.val$bundle, "");
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static boolean getFileExists(File f)
/*     */   {
/* 181 */     return ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 184 */         return this.val$f.exists() ? Boolean.TRUE : Boolean.FALSE;
/*     */       }
/*     */     })).booleanValue();
/*     */   }
/*     */ 
/*     */   static long getLastModified(File f)
/*     */   {
/* 190 */     return ((Long)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 193 */         return new Long(this.val$f.lastModified());
/*     */       }
/*     */     })).longValue();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.utils.SecuritySupport
 * JD-Core Version:    0.6.2
 */