/*     */ package sun.security.smartcardio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ class PlatformPCSC
/*     */ {
/*  45 */   static final Debug debug = Debug.getInstance("pcsc");
/*     */ 
/*  60 */   static final Throwable initException = (Throwable)AccessController.doPrivileged(new PrivilegedAction() {
/*     */     public Throwable run() {
/*     */       try {
/*  63 */         System.loadLibrary("j2pcsc");
/*  64 */         String str = PlatformPCSC.access$000();
/*  65 */         if (PlatformPCSC.debug != null) {
/*  66 */           PlatformPCSC.debug.println("Using PC/SC library: " + str);
/*     */         }
/*  68 */         PlatformPCSC.initialize(str);
/*  69 */         return null;
/*     */       } catch (Throwable localThrowable) {
/*  71 */         return localThrowable; }  }  } );
/*     */   private static final String PROP_NAME = "sun.security.smartcardio.library";
/*     */   private static final String LIB1 = "/usr/$LIBISA/libpcsclite.so";
/*     */   private static final String LIB2 = "/usr/local/$LIBISA/libpcsclite.so";
/*     */   private static final String PCSC_FRAMEWORK = "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC";
/*     */   static final int SCARD_PROTOCOL_T0 = 1;
/*     */   static final int SCARD_PROTOCOL_T1 = 2;
/*     */   static final int SCARD_PROTOCOL_RAW = 4;
/*     */   static final int SCARD_UNKNOWN = 1;
/*     */   static final int SCARD_ABSENT = 2;
/*     */   static final int SCARD_PRESENT = 4;
/*     */   static final int SCARD_SWALLOWED = 8;
/*     */   static final int SCARD_POWERED = 16;
/*     */   static final int SCARD_NEGOTIABLE = 32;
/*     */   static final int SCARD_SPECIFIC = 64;
/*     */ 
/*  79 */   private static String expand(String paramString) { int i = paramString.indexOf("$LIBISA");
/*  80 */     if (i == -1) {
/*  81 */       return paramString;
/*     */     }
/*  83 */     String str1 = paramString.substring(0, i);
/*  84 */     String str2 = paramString.substring(i + 7);
/*     */     String str3;
/*  86 */     if ("64".equals(System.getProperty("sun.arch.data.model"))) {
/*  87 */       if ("SunOS".equals(System.getProperty("os.name"))) {
/*  88 */         str3 = "lib/64";
/*     */       }
/*     */       else {
/*  91 */         str3 = "lib64";
/*     */       }
/*     */     }
/*     */     else {
/*  95 */       str3 = "lib";
/*     */     }
/*  97 */     String str4 = str1 + str3 + str2;
/*  98 */     return str4; }
/*     */ 
/*     */   private static String getLibraryName()
/*     */     throws IOException
/*     */   {
/* 103 */     String str = expand(System.getProperty("sun.security.smartcardio.library", "").trim());
/* 104 */     if (str.length() != 0) {
/* 105 */       return str;
/*     */     }
/* 107 */     str = expand("/usr/$LIBISA/libpcsclite.so");
/* 108 */     if (new File(str).isFile())
/*     */     {
/* 110 */       return str;
/*     */     }
/* 112 */     str = expand("/usr/local/$LIBISA/libpcsclite.so");
/* 113 */     if (new File(str).isFile())
/*     */     {
/* 115 */       return str;
/*     */     }
/* 117 */     str = "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC";
/* 118 */     if (new File(str).isFile())
/*     */     {
/* 120 */       return str;
/*     */     }
/* 122 */     throw new IOException("No PC/SC library found on this system");
/*     */   }
/*     */ 
/*     */   private static native void initialize(String paramString);
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.smartcardio.PlatformPCSC
 * JD-Core Version:    0.6.2
 */