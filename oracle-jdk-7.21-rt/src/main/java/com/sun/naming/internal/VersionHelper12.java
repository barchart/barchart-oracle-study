/*     */ package com.sun.naming.internal;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Properties;
/*     */ import javax.naming.NamingEnumeration;
/*     */ 
/*     */ final class VersionHelper12 extends VersionHelper
/*     */ {
/*  57 */   private boolean getSystemPropsFailed = false;
/*     */ 
/*     */   public Class loadClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/*  62 */     ClassLoader localClassLoader = getContextClassLoader();
/*  63 */     return Class.forName(paramString, true, localClassLoader);
/*     */   }
/*     */ 
/*     */   Class loadClass(String paramString, ClassLoader paramClassLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/*  71 */     return Class.forName(paramString, true, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public Class loadClass(String paramString1, String paramString2)
/*     */     throws ClassNotFoundException, MalformedURLException
/*     */   {
/*  82 */     ClassLoader localClassLoader = getContextClassLoader();
/*  83 */     URLClassLoader localURLClassLoader = URLClassLoader.newInstance(getUrlArray(paramString2), localClassLoader);
/*     */ 
/*  85 */     return Class.forName(paramString1, true, localURLClassLoader);
/*     */   }
/*     */ 
/*     */   String getJndiProperty(final int paramInt) {
/*  89 */     return (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*     */         try {
/*  93 */           return System.getProperty(VersionHelper.PROPS[paramInt]); } catch (SecurityException localSecurityException) {
/*     */         }
/*  95 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   String[] getJndiProperties()
/*     */   {
/* 103 */     if (this.getSystemPropsFailed) {
/* 104 */       return null;
/*     */     }
/* 106 */     Properties localProperties = (Properties)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*     */         try {
/* 110 */           return System.getProperties();
/*     */         } catch (SecurityException localSecurityException) {
/* 112 */           VersionHelper12.this.getSystemPropsFailed = true;
/* 113 */         }return null;
/*     */       }
/*     */     });
/* 118 */     if (localProperties == null) {
/* 119 */       return null;
/*     */     }
/* 121 */     String[] arrayOfString = new String[PROPS.length];
/* 122 */     for (int i = 0; i < PROPS.length; i++) {
/* 123 */       arrayOfString[i] = localProperties.getProperty(PROPS[i]);
/*     */     }
/* 125 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   InputStream getResourceAsStream(final Class paramClass, final String paramString) {
/* 129 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 132 */         return paramClass.getResourceAsStream(paramString);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   InputStream getJavaHomeLibStream(final String paramString)
/*     */   {
/* 139 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*     */         try {
/* 143 */           String str1 = System.getProperty("java.home");
/* 144 */           if (str1 == null) {
/* 145 */             return null;
/*     */           }
/* 147 */           String str2 = str1 + File.separator + "lib" + File.separator + paramString;
/*     */ 
/* 149 */           return new FileInputStream(str2); } catch (Exception localException) {
/*     */         }
/* 151 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   NamingEnumeration getResources(final ClassLoader paramClassLoader, final String paramString)
/*     */     throws IOException
/*     */   {
/*     */     Enumeration localEnumeration;
/*     */     try
/*     */     {
/* 163 */       localEnumeration = (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Object run() throws IOException {
/* 166 */           return paramClassLoader == null ? ClassLoader.getSystemResources(paramString) : paramClassLoader.getResources(paramString);
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/* 173 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/* 175 */     return new InputStreamEnumeration(localEnumeration);
/*     */   }
/*     */ 
/*     */   ClassLoader getContextClassLoader() {
/* 179 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 182 */         return Thread.currentThread().getContextClassLoader();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   class InputStreamEnumeration
/*     */     implements NamingEnumeration
/*     */   {
/*     */     private final Enumeration urls;
/* 200 */     private Object nextElement = null;
/*     */ 
/*     */     InputStreamEnumeration(Enumeration arg2)
/*     */     {
/*     */       Object localObject;
/* 203 */       this.urls = localObject;
/*     */     }
/*     */ 
/*     */     private Object getNextElement()
/*     */     {
/* 211 */       return AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run() {
/* 214 */           while (VersionHelper12.InputStreamEnumeration.this.urls.hasMoreElements())
/*     */             try {
/* 216 */               return ((URL)VersionHelper12.InputStreamEnumeration.this.urls.nextElement()).openStream();
/*     */             }
/*     */             catch (IOException localIOException)
/*     */             {
/*     */             }
/* 221 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public boolean hasMore()
/*     */     {
/* 228 */       if (this.nextElement != null) {
/* 229 */         return true;
/*     */       }
/* 231 */       this.nextElement = getNextElement();
/* 232 */       return this.nextElement != null;
/*     */     }
/*     */ 
/*     */     public boolean hasMoreElements() {
/* 236 */       return hasMore();
/*     */     }
/*     */ 
/*     */     public Object next() {
/* 240 */       if (hasMore()) {
/* 241 */         Object localObject = this.nextElement;
/* 242 */         this.nextElement = null;
/* 243 */         return localObject;
/*     */       }
/* 245 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     public Object nextElement()
/*     */     {
/* 250 */       return next();
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.naming.internal.VersionHelper12
 * JD-Core Version:    0.6.2
 */