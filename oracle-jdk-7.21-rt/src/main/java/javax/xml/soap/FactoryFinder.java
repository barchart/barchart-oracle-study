/*     */ package javax.xml.soap;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Properties;
/*     */ 
/*     */ class FactoryFinder
/*     */ {
/*     */   private static Object newInstance(String className, ClassLoader classLoader)
/*     */     throws SOAPException
/*     */   {
/*     */     try
/*     */     {
/*  46 */       Class spiClass = safeLoadClass(className, classLoader);
/*  47 */       return spiClass.newInstance();
/*     */     } catch (ClassNotFoundException x) {
/*  49 */       throw new SOAPException("Provider " + className + " not found", x);
/*     */     } catch (Exception x) {
/*  51 */       throw new SOAPException("Provider " + className + " could not be instantiated: " + x, x);
/*     */     }
/*     */   }
/*     */ 
/*     */   static Object find(String factoryId)
/*     */     throws SOAPException
/*     */   {
/*  71 */     return find(factoryId, null, false);
/*     */   }
/*     */ 
/*     */   static Object find(String factoryId, String fallbackClassName)
/*     */     throws SOAPException
/*     */   {
/*  97 */     return find(factoryId, fallbackClassName, true);
/*     */   }
/*     */ 
/*     */   static Object find(String factoryId, String defaultClassName, boolean tryFallback)
/*     */     throws SOAPException
/*     */   {
/*     */     ClassLoader classLoader;
/*     */     try
/*     */     {
/* 128 */       classLoader = Thread.currentThread().getContextClassLoader();
/*     */     } catch (Exception x) {
/* 130 */       throw new SOAPException(x.toString(), x);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 135 */       String systemProp = System.getProperty(factoryId);
/*     */ 
/* 137 */       if (systemProp != null)
/* 138 */         return newInstance(systemProp, classLoader);
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/*     */     }
/*     */     try
/*     */     {
/* 145 */       String javah = System.getProperty("java.home");
/* 146 */       String configFile = javah + File.separator + "lib" + File.separator + "jaxm.properties";
/*     */ 
/* 148 */       File f = new File(configFile);
/* 149 */       if (f.exists()) {
/* 150 */         Properties props = new Properties();
/* 151 */         props.load(new FileInputStream(f));
/* 152 */         String factoryClassName = props.getProperty(factoryId);
/* 153 */         return newInstance(factoryClassName, classLoader);
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/* 158 */     String serviceId = "META-INF/services/" + factoryId;
/*     */     try
/*     */     {
/* 161 */       InputStream is = null;
/* 162 */       if (classLoader == null)
/* 163 */         is = ClassLoader.getSystemResourceAsStream(serviceId);
/*     */       else {
/* 165 */         is = classLoader.getResourceAsStream(serviceId);
/*     */       }
/*     */ 
/* 168 */       if (is != null) {
/* 169 */         BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */ 
/* 172 */         String factoryClassName = rd.readLine();
/* 173 */         rd.close();
/*     */ 
/* 175 */         if ((factoryClassName != null) && (!"".equals(factoryClassName)))
/*     */         {
/* 177 */           return newInstance(factoryClassName, classLoader);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/* 184 */     if (!tryFallback) {
/* 185 */       return null;
/*     */     }
/*     */ 
/* 189 */     if (defaultClassName == null) {
/* 190 */       throw new SOAPException("Provider for " + factoryId + " cannot be found", null);
/*     */     }
/*     */ 
/* 193 */     return newInstance(defaultClassName, classLoader);
/*     */   }
/*     */ 
/*     */   private static Class safeLoadClass(String className, ClassLoader classLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 207 */       SecurityManager s = System.getSecurityManager();
/* 208 */       if (s != null) {
/* 209 */         int i = className.lastIndexOf('.');
/* 210 */         if (i != -1) {
/* 211 */           s.checkPackageAccess(className.substring(0, i));
/*     */         }
/*     */       }
/*     */ 
/* 215 */       if (classLoader == null) {
/* 216 */         return Class.forName(className);
/*     */       }
/* 218 */       return classLoader.loadClass(className);
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/* 222 */       if (isDefaultImplementation(className)) {
/* 223 */         return Class.forName(className);
/*     */       }
/* 225 */       throw se;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isDefaultImplementation(String className) {
/* 230 */     return ("com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl".equals(className)) || ("com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl".equals(className)) || ("com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory".equals(className)) || ("com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl".equals(className));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.soap.FactoryFinder
 * JD-Core Version:    0.6.2
 */