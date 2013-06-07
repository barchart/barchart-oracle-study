/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.Console;
/*     */ import java.io.FileDescriptor;
/*     */ import java.net.HttpCookie;
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.zip.Adler32;
/*     */ import javax.security.auth.kerberos.KeyTab;
/*     */ 
/*     */ public class SharedSecrets
/*     */ {
/*  47 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   private static JavaUtilJarAccess javaUtilJarAccess;
/*     */   private static JavaLangAccess javaLangAccess;
/*     */   private static JavaIOAccess javaIOAccess;
/*     */   private static JavaNetAccess javaNetAccess;
/*     */   private static JavaNetHttpCookieAccess javaNetHttpCookieAccess;
/*     */   private static JavaNioAccess javaNioAccess;
/*     */   private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
/*     */   private static JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
/*     */   private static JavaSecurityAccess javaSecurityAccess;
/*     */   private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;
/*     */   private static JavaUtilZipAccess javaUtilZipAccess;
/*     */   private static JavaAWTAccess javaAWTAccess;
/*     */ 
/*     */   public static JavaUtilJarAccess javaUtilJarAccess()
/*     */   {
/*  62 */     if (javaUtilJarAccess == null)
/*     */     {
/*  65 */       unsafe.ensureClassInitialized(JarFile.class);
/*     */     }
/*  67 */     return javaUtilJarAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaUtilJarAccess(JavaUtilJarAccess paramJavaUtilJarAccess) {
/*  71 */     javaUtilJarAccess = paramJavaUtilJarAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaLangAccess(JavaLangAccess paramJavaLangAccess) {
/*  75 */     javaLangAccess = paramJavaLangAccess;
/*     */   }
/*     */ 
/*     */   public static JavaLangAccess getJavaLangAccess() {
/*  79 */     return javaLangAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaNetAccess(JavaNetAccess paramJavaNetAccess) {
/*  83 */     javaNetAccess = paramJavaNetAccess;
/*     */   }
/*     */ 
/*     */   public static JavaNetAccess getJavaNetAccess() {
/*  87 */     return javaNetAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaNetHttpCookieAccess(JavaNetHttpCookieAccess paramJavaNetHttpCookieAccess) {
/*  91 */     javaNetHttpCookieAccess = paramJavaNetHttpCookieAccess;
/*     */   }
/*     */ 
/*     */   public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
/*  95 */     if (javaNetHttpCookieAccess == null)
/*  96 */       unsafe.ensureClassInitialized(HttpCookie.class);
/*  97 */     return javaNetHttpCookieAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaNioAccess(JavaNioAccess paramJavaNioAccess) {
/* 101 */     javaNioAccess = paramJavaNioAccess;
/*     */   }
/*     */ 
/*     */   public static JavaNioAccess getJavaNioAccess() {
/* 105 */     if (javaNioAccess == null)
/*     */     {
/* 109 */       unsafe.ensureClassInitialized(ByteOrder.class);
/*     */     }
/* 111 */     return javaNioAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaIOAccess(JavaIOAccess paramJavaIOAccess) {
/* 115 */     javaIOAccess = paramJavaIOAccess;
/*     */   }
/*     */ 
/*     */   public static JavaIOAccess getJavaIOAccess() {
/* 119 */     if (javaIOAccess == null) {
/* 120 */       unsafe.ensureClassInitialized(Console.class);
/*     */     }
/* 122 */     return javaIOAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaIOFileDescriptorAccess(JavaIOFileDescriptorAccess paramJavaIOFileDescriptorAccess) {
/* 126 */     javaIOFileDescriptorAccess = paramJavaIOFileDescriptorAccess;
/*     */   }
/*     */ 
/*     */   public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
/* 130 */     if (javaIOFileDescriptorAccess == null) {
/* 131 */       unsafe.ensureClassInitialized(FileDescriptor.class);
/*     */     }
/* 133 */     return javaIOFileDescriptorAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaSecurityProtectionDomainAccess(JavaSecurityProtectionDomainAccess paramJavaSecurityProtectionDomainAccess)
/*     */   {
/* 138 */     javaSecurityProtectionDomainAccess = paramJavaSecurityProtectionDomainAccess;
/*     */   }
/*     */ 
/*     */   public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess()
/*     */   {
/* 143 */     if (javaSecurityProtectionDomainAccess == null)
/* 144 */       unsafe.ensureClassInitialized(ProtectionDomain.class);
/* 145 */     return javaSecurityProtectionDomainAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaSecurityAccess(JavaSecurityAccess paramJavaSecurityAccess) {
/* 149 */     javaSecurityAccess = paramJavaSecurityAccess;
/*     */   }
/*     */ 
/*     */   public static JavaSecurityAccess getJavaSecurityAccess() {
/* 153 */     if (javaSecurityAccess == null) {
/* 154 */       unsafe.ensureClassInitialized(AccessController.class);
/*     */     }
/* 156 */     return javaSecurityAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaxSecurityAuthKerberosAccess(JavaxSecurityAuthKerberosAccess paramJavaxSecurityAuthKerberosAccess)
/*     */   {
/* 161 */     javaxSecurityAuthKerberosAccess = paramJavaxSecurityAuthKerberosAccess;
/*     */   }
/*     */ 
/*     */   public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess()
/*     */   {
/* 166 */     if (javaxSecurityAuthKerberosAccess == null)
/* 167 */       unsafe.ensureClassInitialized(KeyTab.class);
/* 168 */     return javaxSecurityAuthKerberosAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaUtilZipAccess(JavaUtilZipAccess paramJavaUtilZipAccess) {
/* 172 */     javaUtilZipAccess = paramJavaUtilZipAccess;
/*     */   }
/*     */ 
/*     */   public static JavaUtilZipAccess getJavaUtilZipAccess() {
/* 176 */     if (javaUtilZipAccess == null) {
/* 177 */       unsafe.ensureClassInitialized(Adler32.class);
/*     */     }
/* 179 */     return javaUtilZipAccess;
/*     */   }
/*     */ 
/*     */   public static void setJavaAWTAccess(JavaAWTAccess paramJavaAWTAccess) {
/* 183 */     javaAWTAccess = paramJavaAWTAccess;
/*     */   }
/*     */ 
/*     */   public static JavaAWTAccess getJavaAWTAccess()
/*     */   {
/* 189 */     return javaAWTAccess;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.SharedSecrets
 * JD-Core Version:    0.6.2
 */