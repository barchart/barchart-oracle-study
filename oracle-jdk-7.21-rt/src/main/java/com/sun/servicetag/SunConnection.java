/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.util.Locale;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ 
/*     */ class SunConnection
/*     */ {
/*  54 */   private static String JDK_REGISTRATION_URL = "https://hs-ws1.oracle.com/";
/*  55 */   private static String SANDBOX_TESTING_URL = "https://hs-ws1-tst.oracle.com/";
/*  56 */   private static String REGISTRATION_WEB_PATH = "RegistrationWeb/register";
/*     */ 
/*  59 */   private static String SVCTAG_REGISTER_TESTING = "servicetag.register.testing";
/*  60 */   private static String SVCTAG_REGISTRATION_URL = "servicetag.registration.url";
/*  61 */   private static String SVCTAG_CONNECTION_TIMEOUT = "servicetag.connection.timeout";
/*     */ 
/*     */   static URL getRegistrationURL(String paramString1, Locale paramLocale, String paramString2)
/*     */   {
/*  82 */     String str1 = System.getProperty(SVCTAG_REGISTRATION_URL);
/*  83 */     if (str1 == null) {
/*  84 */       if (System.getProperty(SVCTAG_REGISTER_TESTING) != null)
/*  85 */         str1 = SANDBOX_TESTING_URL;
/*     */       else {
/*  87 */         str1 = JDK_REGISTRATION_URL;
/*     */       }
/*     */     }
/*  90 */     str1 = str1 + REGISTRATION_WEB_PATH;
/*     */ 
/*  93 */     str1 = str1.trim();
/*  94 */     if (str1.length() == 0) {
/*  95 */       throw new InternalError("Empty registration url set");
/*     */     }
/*     */ 
/*  99 */     String str2 = rewriteURL(str1, paramString1, paramLocale, paramString2);
/*     */     try {
/* 101 */       return new URL(str2);
/*     */     }
/*     */     catch (MalformedURLException localMalformedURLException) {
/* 104 */       InternalError localInternalError = new InternalError(localMalformedURLException.getMessage());
/*     */ 
/* 106 */       localInternalError.initCause(localMalformedURLException);
/* 107 */       throw localInternalError;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String rewriteURL(String paramString1, String paramString2, Locale paramLocale, String paramString3) {
/* 112 */     StringBuilder localStringBuilder = new StringBuilder(paramString1.trim());
/* 113 */     int i = localStringBuilder.length();
/* 114 */     if (localStringBuilder.charAt(i - 1) != '/') {
/* 115 */       localStringBuilder.append('/');
/*     */     }
/* 117 */     localStringBuilder.append(paramString2);
/* 118 */     localStringBuilder.append("?");
/* 119 */     localStringBuilder.append("product=jdk");
/* 120 */     localStringBuilder.append("&");
/* 121 */     localStringBuilder.append("locale=").append(paramLocale.toString());
/* 122 */     localStringBuilder.append("&");
/* 123 */     localStringBuilder.append("version=").append(paramString3);
/* 124 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   public static void register(RegistrationData paramRegistrationData, Locale paramLocale, String paramString)
/*     */     throws IOException
/*     */   {
/* 143 */     URL localURL = getRegistrationURL(paramRegistrationData.getRegistrationURN(), paramLocale, paramString);
/*     */ 
/* 148 */     boolean bool = postRegistrationData(localURL, paramRegistrationData);
/* 149 */     if (bool)
/*     */     {
/* 152 */       openBrowser(localURL);
/*     */     }
/*     */     else
/* 155 */       openOfflineRegisterPage();
/*     */   }
/*     */ 
/*     */   private static void openBrowser(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 164 */     if (!BrowserSupport.isSupported()) {
/* 165 */       if (Util.isVerbose()) {
/* 166 */         System.out.println("Browser is not supported");
/*     */       }
/* 168 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 172 */       BrowserSupport.browse(paramURL.toURI());
/*     */     } catch (URISyntaxException localURISyntaxException) {
/* 174 */       InternalError localInternalError = new InternalError("Error in registering: " + localURISyntaxException.getMessage());
/* 175 */       localInternalError.initCause(localURISyntaxException);
/* 176 */       throw localInternalError;
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 178 */       if (Util.isVerbose())
/* 179 */         localIllegalArgumentException.printStackTrace();
/*     */     }
/*     */     catch (UnsupportedOperationException localUnsupportedOperationException)
/*     */     {
/* 183 */       if (Util.isVerbose())
/* 184 */         localUnsupportedOperationException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean postRegistrationData(URL paramURL, RegistrationData paramRegistrationData)
/*     */   {
/*     */     try
/*     */     {
/* 198 */       HttpsURLConnection localHttpsURLConnection = (HttpsURLConnection)paramURL.openConnection();
/* 199 */       localHttpsURLConnection.setDoInput(true);
/* 200 */       localHttpsURLConnection.setDoOutput(true);
/* 201 */       localHttpsURLConnection.setUseCaches(false);
/* 202 */       localHttpsURLConnection.setAllowUserInteraction(false);
/*     */ 
/* 205 */       localObject1 = System.getProperty(SVCTAG_CONNECTION_TIMEOUT, "10");
/* 206 */       localHttpsURLConnection.setConnectTimeout(Util.getIntValue((String)localObject1) * 1000);
/*     */ 
/* 208 */       if (Util.isVerbose()) {
/* 209 */         System.out.println("Connecting to post registration data at " + paramURL);
/*     */       }
/*     */ 
/* 212 */       localHttpsURLConnection.setRequestMethod("POST");
/* 213 */       localHttpsURLConnection.setRequestProperty("Content-Type", "text/xml;charset=\"utf-8\"");
/* 214 */       localHttpsURLConnection.connect();
/*     */ 
/* 216 */       OutputStream localOutputStream = null;
/*     */       try {
/* 218 */         localOutputStream = localHttpsURLConnection.getOutputStream();
/* 219 */         paramRegistrationData.storeToXML(localOutputStream);
/* 220 */         localOutputStream.flush();
/*     */       } finally {
/* 222 */         if (localOutputStream != null) {
/* 223 */           localOutputStream.close();
/*     */         }
/*     */       }
/*     */ 
/* 227 */       int i = localHttpsURLConnection.getResponseCode();
/* 228 */       if (Util.isVerbose()) {
/* 229 */         System.out.println("POST return status = " + i);
/* 230 */         printReturnData(localHttpsURLConnection, i);
/*     */       }
/* 232 */       return i == 200;
/*     */     }
/*     */     catch (MalformedURLException localMalformedURLException) {
/* 235 */       Object localObject1 = new InternalError("Error in registering: " + localMalformedURLException.getMessage());
/* 236 */       ((InternalError)localObject1).initCause(localMalformedURLException);
/* 237 */       throw ((Throwable)localObject1);
/*     */     }
/*     */     catch (Exception localException) {
/* 240 */       if (Util.isVerbose())
/* 241 */         localException.printStackTrace();
/*     */     }
/* 243 */     return false;
/*     */   }
/*     */ 
/*     */   private static void openOfflineRegisterPage()
/*     */     throws IOException
/*     */   {
/* 253 */     if (!BrowserSupport.isSupported()) {
/* 254 */       if (Util.isVerbose()) {
/* 255 */         System.out.println("Browser is not supported");
/*     */       }
/* 257 */       return;
/*     */     }
/*     */ 
/* 260 */     File localFile = Installer.getRegistrationHtmlPage();
/*     */     try {
/* 262 */       BrowserSupport.browse(localFile.toURI());
/*     */     }
/*     */     catch (FileNotFoundException localFileNotFoundException) {
/* 265 */       InternalError localInternalError = new InternalError("Error in launching " + localFile + ": " + localFileNotFoundException.getMessage());
/*     */ 
/* 267 */       localInternalError.initCause(localFileNotFoundException);
/* 268 */       throw localInternalError;
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 270 */       if (Util.isVerbose())
/* 271 */         localIllegalArgumentException.printStackTrace();
/*     */     }
/*     */     catch (UnsupportedOperationException localUnsupportedOperationException)
/*     */     {
/* 275 */       if (Util.isVerbose())
/* 276 */         localUnsupportedOperationException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void printReturnData(HttpURLConnection paramHttpURLConnection, int paramInt)
/*     */     throws IOException
/*     */   {
/* 283 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 285 */       if (paramInt < 400) {
/* 286 */         localBufferedReader = new BufferedReader(new InputStreamReader(paramHttpURLConnection.getInputStream()));
/*     */       }
/*     */       else {
/* 289 */         localBufferedReader = new BufferedReader(new InputStreamReader(paramHttpURLConnection.getErrorStream()));
/*     */       }
/*     */ 
/* 292 */       StringBuilder localStringBuilder = new StringBuilder();
/*     */       String str;
/* 294 */       while ((str = localBufferedReader.readLine()) != null) {
/* 295 */         localStringBuilder.append(str).append("\n");
/*     */       }
/* 297 */       System.out.println("Response is : ");
/* 298 */       System.out.println(localStringBuilder.toString());
/*     */     } finally {
/* 300 */       if (localBufferedReader != null)
/* 301 */         localBufferedReader.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.SunConnection
 * JD-Core Version:    0.6.2
 */