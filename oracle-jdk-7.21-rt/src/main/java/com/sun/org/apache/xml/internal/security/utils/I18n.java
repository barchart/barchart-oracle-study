/*     */ package com.sun.org.apache.xml.internal.security.utils;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.Init;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class I18n
/*     */ {
/*     */   public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
/*     */   private static String defaultLanguageCode;
/*     */   private static String defaultCountryCode;
/*  47 */   private static ResourceBundle resourceBundle = ResourceBundle.getBundle("com/sun/org/apache/xml/internal/security/resource/xmlsecurity", Locale.US);
/*     */ 
/*  52 */   private static boolean alreadyInitialized = false;
/*     */ 
/*  55 */   private static String _languageCode = null;
/*     */ 
/*  58 */   private static String _countryCode = null;
/*     */ 
/*     */   public static String translate(String paramString, Object[] paramArrayOfObject)
/*     */   {
/*  82 */     return getExceptionMessage(paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public static String translate(String paramString)
/*     */   {
/*  95 */     return getExceptionMessage(paramString);
/*     */   }
/*     */ 
/*     */   public static String getExceptionMessage(String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 108 */       return resourceBundle.getString(paramString);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/* 112 */       if (Init.isInitialized()) {
/* 113 */         return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"";
/*     */       }
/*     */     }
/*     */ 
/* 117 */     return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
/*     */   }
/*     */ 
/*     */   public static String getExceptionMessage(String paramString, Exception paramException)
/*     */   {
/*     */     try
/*     */     {
/* 132 */       Object[] arrayOfObject = { paramException.getMessage() };
/* 133 */       return MessageFormat.format(resourceBundle.getString(paramString), arrayOfObject);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/* 138 */       if (Init.isInitialized()) {
/* 139 */         return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\". Original Exception was a " + paramException.getClass().getName() + " and message " + paramException.getMessage();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 146 */     return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
/*     */   }
/*     */ 
/*     */   public static String getExceptionMessage(String paramString, Object[] paramArrayOfObject)
/*     */   {
/*     */     try
/*     */     {
/* 160 */       return MessageFormat.format(resourceBundle.getString(paramString), paramArrayOfObject);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/* 165 */       if (Init.isInitialized()) {
/* 166 */         return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"";
/*     */       }
/*     */     }
/*     */ 
/* 170 */     return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.I18n
 * JD-Core Version:    0.6.2
 */