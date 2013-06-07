/*     */ package javax.rmi;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Properties;
/*     */ 
/*     */ class GetORBPropertiesFileAction
/*     */   implements PrivilegedAction
/*     */ {
/* 238 */   private boolean debug = false;
/*     */ 
/*     */   private String getSystemProperty(final String paramString)
/*     */   {
/* 246 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 249 */         return System.getProperty(paramString);
/*     */       }
/*     */     });
/* 254 */     return str;
/*     */   }
/*     */ 
/*     */   private void getPropertiesFromFile(Properties paramProperties, String paramString)
/*     */   {
/*     */     try {
/* 260 */       File localFile = new File(paramString);
/* 261 */       if (!localFile.exists()) {
/* 262 */         return;
/*     */       }
/* 264 */       FileInputStream localFileInputStream = new FileInputStream(localFile);
/*     */       try
/*     */       {
/* 267 */         paramProperties.load(localFileInputStream);
/*     */       } finally {
/* 269 */         localFileInputStream.close();
/*     */       }
/*     */     } catch (Exception localException) {
/* 272 */       if (this.debug)
/* 273 */         System.out.println("ORB properties file " + paramString + " not found: " + localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object run()
/*     */   {
/* 280 */     Properties localProperties1 = new Properties();
/*     */ 
/* 282 */     String str1 = getSystemProperty("java.home");
/* 283 */     String str2 = str1 + File.separator + "lib" + File.separator + "orb.properties";
/*     */ 
/* 286 */     getPropertiesFromFile(localProperties1, str2);
/*     */ 
/* 288 */     Properties localProperties2 = new Properties(localProperties1);
/*     */ 
/* 290 */     String str3 = getSystemProperty("user.home");
/* 291 */     str2 = str3 + File.separator + "orb.properties";
/*     */ 
/* 293 */     getPropertiesFromFile(localProperties2, str2);
/* 294 */     return localProperties2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.rmi.GetORBPropertiesFileAction
 * JD-Core Version:    0.6.2
 */