/*     */ package sun.net.www.protocol.file;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ import sun.net.www.ParseUtil;
/*     */ 
/*     */ public class Handler extends URLStreamHandler
/*     */ {
/*     */   private String getHost(URL paramURL)
/*     */   {
/*  46 */     String str = paramURL.getHost();
/*  47 */     if (str == null)
/*  48 */       str = "";
/*  49 */     return str;
/*     */   }
/*     */ 
/*     */   protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2)
/*     */   {
/*  67 */     super.parseURL(paramURL, paramString.replace(File.separatorChar, '/'), paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public synchronized URLConnection openConnection(URL paramURL) throws IOException
/*     */   {
/*  72 */     return openConnection(paramURL, null);
/*     */   }
/*     */ 
/*     */   public synchronized URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException
/*     */   {
/*  77 */     String str = paramURL.getHost();
/*     */     Object localObject;
/*  78 */     if ((str == null) || (str.equals("")) || (str.equals("~")) || (str.equalsIgnoreCase("localhost")))
/*     */     {
/*  80 */       localObject = new File(ParseUtil.decode(paramURL.getPath()));
/*  81 */       return createFileURLConnection(paramURL, (File)localObject);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  91 */       URL localURL = new URL("ftp", str, paramURL.getFile() + (paramURL.getRef() == null ? "" : new StringBuilder().append("#").append(paramURL.getRef()).toString()));
/*     */ 
/*  93 */       if (paramProxy != null)
/*  94 */         localObject = localURL.openConnection(paramProxy);
/*     */       else
/*  96 */         localObject = localURL.openConnection();
/*     */     }
/*     */     catch (IOException localIOException) {
/*  99 */       localObject = null;
/*     */     }
/* 101 */     if (localObject == null) {
/* 102 */       throw new IOException("Unable to connect to: " + paramURL.toExternalForm());
/*     */     }
/*     */ 
/* 105 */     return localObject;
/*     */   }
/*     */ 
/*     */   protected URLConnection createFileURLConnection(URL paramURL, File paramFile)
/*     */   {
/* 112 */     return new FileURLConnection(paramURL, paramFile);
/*     */   }
/*     */ 
/*     */   protected boolean hostsEqual(URL paramURL1, URL paramURL2)
/*     */   {
/* 128 */     String str1 = paramURL1.getHost();
/* 129 */     String str2 = paramURL2.getHost();
/* 130 */     if (("localhost".equalsIgnoreCase(str1)) && ((str2 == null) || ("".equals(str2))))
/* 131 */       return true;
/* 132 */     if (("localhost".equalsIgnoreCase(str2)) && ((str1 == null) || ("".equals(str1))))
/* 133 */       return true;
/* 134 */     return super.hostsEqual(paramURL1, paramURL2);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.file.Handler
 * JD-Core Version:    0.6.2
 */