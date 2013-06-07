/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ class Platform
/*     */ {
/*     */   private static final String libNameMain = "jsound";
/*     */   private static final String libNameALSA = "jsoundalsa";
/*     */   private static final String libNameDSound = "jsoundds";
/*     */   public static final int LIB_MAIN = 1;
/*     */   public static final int LIB_ALSA = 2;
/*     */   public static final int LIB_DSOUND = 4;
/*  54 */   private static int loadedLibs = 0;
/*     */   public static final int FEATURE_MIDIIO = 1;
/*     */   public static final int FEATURE_PORTS = 2;
/*     */   public static final int FEATURE_DIRECT_AUDIO = 3;
/*     */   private static boolean signed8;
/*     */   private static boolean bigEndian;
/*     */   private static String javahome;
/*     */   private static String classpath;
/*     */ 
/*     */   static void initialize()
/*     */   {
/*     */   }
/*     */ 
/*     */   static boolean isBigEndian()
/*     */   {
/* 118 */     return bigEndian;
/*     */   }
/*     */ 
/*     */   static boolean isSigned8()
/*     */   {
/* 127 */     return signed8;
/*     */   }
/*     */ 
/*     */   static String getJavahome()
/*     */   {
/* 137 */     return javahome;
/*     */   }
/*     */ 
/*     */   static String getClasspath()
/*     */   {
/* 146 */     return classpath;
/*     */   }
/*     */ 
/*     */   private static void loadLibraries()
/*     */   {
/*     */     try
/*     */     {
/* 160 */       JSSecurityManager.loadLibrary("jsound");
/*     */ 
/* 162 */       loadedLibs |= 1;
/*     */     }
/*     */     catch (SecurityException localSecurityException) {
/* 165 */       throw localSecurityException;
/*     */     }
/*     */ 
/* 170 */     String str1 = nGetExtraLibraries();
/*     */ 
/* 172 */     StringTokenizer localStringTokenizer = new StringTokenizer(str1);
/* 173 */     while (localStringTokenizer.hasMoreTokens()) {
/* 174 */       String str2 = localStringTokenizer.nextToken();
/*     */       try {
/* 176 */         JSSecurityManager.loadLibrary(str2);
/* 177 */         if (str2.equals("jsoundalsa")) {
/* 178 */           loadedLibs |= 2;
/*     */         }
/* 180 */         else if (str2.equals("jsoundds"))
/* 181 */           loadedLibs |= 4;
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isMidiIOEnabled()
/*     */   {
/* 194 */     return isFeatureLibLoaded(1);
/*     */   }
/*     */ 
/*     */   static boolean isPortsEnabled() {
/* 198 */     return isFeatureLibLoaded(2);
/*     */   }
/*     */ 
/*     */   static boolean isDirectAudioEnabled() {
/* 202 */     return isFeatureLibLoaded(3);
/*     */   }
/*     */ 
/*     */   private static boolean isFeatureLibLoaded(int paramInt)
/*     */   {
/* 207 */     int i = nGetLibraryForFeature(paramInt);
/* 208 */     boolean bool = (i != 0) && ((loadedLibs & i) == i);
/*     */ 
/* 210 */     return bool;
/*     */   }
/*     */ 
/*     */   private static native boolean nIsBigEndian();
/*     */ 
/*     */   private static native boolean nIsSigned8();
/*     */ 
/*     */   private static native String nGetExtraLibraries();
/*     */ 
/*     */   private static native int nGetLibraryForFeature(int paramInt);
/*     */ 
/*     */   private static void readProperties()
/*     */   {
/* 225 */     bigEndian = nIsBigEndian();
/* 226 */     signed8 = nIsSigned8();
/* 227 */     javahome = JSSecurityManager.getProperty("java.home");
/* 228 */     classpath = JSSecurityManager.getProperty("java.class.path");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  89 */     loadLibraries();
/*  90 */     readProperties();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.Platform
 * JD-Core Version:    0.6.2
 */