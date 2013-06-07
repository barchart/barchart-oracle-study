/*     */ package sun.net.www.protocol.jar;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketPermission;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.Permission;
/*     */ import java.util.HashMap;
/*     */ import java.util.jar.JarFile;
/*     */ import sun.net.util.URLUtil;
/*     */ 
/*     */ class JarFileFactory
/*     */   implements URLJarFile.URLJarFileCloseController
/*     */ {
/*  46 */   private static HashMap<String, JarFile> fileCache = new HashMap();
/*     */ 
/*  49 */   private static HashMap<JarFile, URL> urlCache = new HashMap();
/*     */ 
/*     */   URLConnection getConnection(JarFile paramJarFile) throws IOException {
/*  52 */     URL localURL = (URL)urlCache.get(paramJarFile);
/*  53 */     if (localURL != null) {
/*  54 */       return localURL.openConnection();
/*     */     }
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */   public JarFile get(URL paramURL) throws IOException {
/*  60 */     return get(paramURL, true);
/*     */   }
/*     */ 
/*     */   JarFile get(URL paramURL, boolean paramBoolean) throws IOException
/*     */   {
/*  65 */     Object localObject1 = null;
/*  66 */     JarFile localJarFile = null;
/*     */ 
/*  68 */     if (paramBoolean) {
/*  69 */       synchronized (this) {
/*  70 */         localObject1 = getCachedJarFile(paramURL);
/*     */       }
/*  72 */       if (localObject1 == null) {
/*  73 */         localJarFile = URLJarFile.getJarFile(paramURL, this);
/*  74 */         synchronized (this) {
/*  75 */           localObject1 = getCachedJarFile(paramURL);
/*  76 */           if (localObject1 == null) {
/*  77 */             fileCache.put(URLUtil.urlNoFragString(paramURL), localJarFile);
/*  78 */             urlCache.put(localJarFile, paramURL);
/*  79 */             localObject1 = localJarFile;
/*     */           }
/*  81 */           else if (localJarFile != null) {
/*  82 */             localJarFile.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  88 */       localObject1 = URLJarFile.getJarFile(paramURL, this);
/*     */     }
/*  90 */     if (localObject1 == null) {
/*  91 */       throw new FileNotFoundException(paramURL.toString());
/*     */     }
/*  93 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public void close(JarFile paramJarFile)
/*     */   {
/* 102 */     URL localURL = (URL)urlCache.remove(paramJarFile);
/* 103 */     if (localURL != null)
/* 104 */       fileCache.remove(URLUtil.urlNoFragString(localURL));
/*     */   }
/*     */ 
/*     */   private JarFile getCachedJarFile(URL paramURL)
/*     */   {
/* 110 */     JarFile localJarFile = (JarFile)fileCache.get(URLUtil.urlNoFragString(paramURL));
/*     */ 
/* 113 */     if (localJarFile != null) {
/* 114 */       Permission localPermission = getPermission(localJarFile);
/* 115 */       if (localPermission != null) {
/* 116 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 117 */         if (localSecurityManager != null) {
/*     */           try {
/* 119 */             localSecurityManager.checkPermission(localPermission);
/*     */           }
/*     */           catch (SecurityException localSecurityException)
/*     */           {
/* 123 */             if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1))
/*     */             {
/* 125 */               localSecurityManager.checkRead(localPermission.getName());
/* 126 */             } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1))
/*     */             {
/* 129 */               localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */             }
/* 131 */             else throw localSecurityException;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 137 */     return localJarFile;
/*     */   }
/*     */ 
/*     */   private Permission getPermission(JarFile paramJarFile) {
/*     */     try {
/* 142 */       URLConnection localURLConnection = getConnection(paramJarFile);
/* 143 */       if (localURLConnection != null)
/* 144 */         return localURLConnection.getPermission();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.jar.JarFileFactory
 * JD-Core Version:    0.6.2
 */