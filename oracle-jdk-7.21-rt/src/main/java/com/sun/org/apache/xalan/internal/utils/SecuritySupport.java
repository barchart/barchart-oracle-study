/*     */ package com.sun.org.apache.xalan.internal.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ListResourceBundle;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public final class SecuritySupport
/*     */ {
/*  48 */   private static final SecuritySupport securitySupport = new SecuritySupport();
/*     */ 
/*     */   public static SecuritySupport getInstance()
/*     */   {
/*  54 */     return securitySupport;
/*     */   }
/*     */ 
/*     */   static ClassLoader getContextClassLoader() {
/*  58 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*  60 */         ClassLoader cl = null;
/*     */         try {
/*  62 */           cl = Thread.currentThread().getContextClassLoader();
/*     */         } catch (SecurityException ex) {
/*     */         }
/*  65 */         return cl;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static ClassLoader getSystemClassLoader() {
/*  71 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*  73 */         ClassLoader cl = null;
/*     */         try {
/*  75 */           cl = ClassLoader.getSystemClassLoader();
/*     */         } catch (SecurityException ex) {
/*     */         }
/*  78 */         return cl;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static ClassLoader getParentClassLoader(ClassLoader cl) {
/*  84 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*  86 */         ClassLoader parent = null;
/*     */         try {
/*  88 */           parent = this.val$cl.getParent();
/*     */         }
/*     */         catch (SecurityException ex)
/*     */         {
/*     */         }
/*     */ 
/*  94 */         return parent == this.val$cl ? null : parent;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String propName) {
/* 100 */     return (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 102 */         return System.getProperty(this.val$propName);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String propName, final String def) {
/* 108 */     return (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 110 */         return System.getProperty(this.val$propName, def);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static FileInputStream getFileInputStream(File file) throws FileNotFoundException
/*     */   {
/*     */     try {
/* 118 */       return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Object run() throws FileNotFoundException {
/* 120 */           return new FileInputStream(this.val$file);
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException e) {
/* 124 */       throw ((FileNotFoundException)e.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(String name)
/*     */   {
/* 133 */     if (System.getSecurityManager() != null) {
/* 134 */       return getResourceAsStream(null, name);
/*     */     }
/* 136 */     return getResourceAsStream(ObjectFactory.findClassLoader(), name);
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(ClassLoader cl, final String name)
/*     */   {
/* 142 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*     */         InputStream ris;
/*     */         InputStream ris;
/* 145 */         if (this.val$cl == null)
/* 146 */           ris = Object.class.getResourceAsStream("/" + name);
/*     */         else {
/* 148 */           ris = this.val$cl.getResourceAsStream(name);
/*     */         }
/* 150 */         return ris;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static ListResourceBundle getResourceBundle(String bundle)
/*     */   {
/* 161 */     return getResourceBundle(bundle, Locale.getDefault());
/*     */   }
/*     */ 
/*     */   public static ListResourceBundle getResourceBundle(String bundle, final Locale locale)
/*     */   {
/* 171 */     return (ListResourceBundle)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public ListResourceBundle run() {
/*     */         try {
/* 174 */           return (ListResourceBundle)ResourceBundle.getBundle(this.val$bundle, locale);
/*     */         } catch (MissingResourceException e) {
/*     */           try {
/* 177 */             return (ListResourceBundle)ResourceBundle.getBundle(this.val$bundle, new Locale("en", "US")); } catch (MissingResourceException e2) {  }
/*     */         }
/* 179 */         throw new MissingResourceException("Could not load any resource bundle by " + this.val$bundle, this.val$bundle, "");
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static boolean getFileExists(File f)
/*     */   {
/* 188 */     return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 190 */         return this.val$f.exists() ? Boolean.TRUE : Boolean.FALSE;
/*     */       }
/*     */     })).booleanValue();
/*     */   }
/*     */ 
/*     */   static long getLastModified(File f)
/*     */   {
/* 196 */     return ((Long)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 198 */         return new Long(this.val$f.lastModified());
/*     */       }
/*     */     })).longValue();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.utils.SecuritySupport
 * JD-Core Version:    0.6.2
 */