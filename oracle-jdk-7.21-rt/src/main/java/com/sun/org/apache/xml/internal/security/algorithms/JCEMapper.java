/*     */ package com.sun.org.apache.xml.internal.security.algorithms;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class JCEMapper
/*     */ {
/*  43 */   static Logger log = Logger.getLogger(JCEMapper.class.getName());
/*     */   private static Map uriToJCEName;
/*     */   private static Map algorithmsMap;
/*  52 */   private static String providerName = null;
/*     */ 
/*     */   public static void init(Element paramElement)
/*     */     throws Exception
/*     */   {
/*  61 */     loadAlgorithms((Element)paramElement.getElementsByTagName("Algorithms").item(0));
/*     */   }
/*     */ 
/*     */   static void loadAlgorithms(Element paramElement) {
/*  65 */     Element[] arrayOfElement = XMLUtils.selectNodes(paramElement.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm");
/*  66 */     uriToJCEName = new HashMap(arrayOfElement.length * 2);
/*  67 */     algorithmsMap = new HashMap(arrayOfElement.length * 2);
/*  68 */     for (int i = 0; i < arrayOfElement.length; i++) {
/*  69 */       Element localElement = arrayOfElement[i];
/*  70 */       String str1 = localElement.getAttribute("URI");
/*  71 */       String str2 = localElement.getAttribute("JCEName");
/*  72 */       uriToJCEName.put(str1, str2);
/*  73 */       algorithmsMap.put(str1, new Algorithm(localElement));
/*     */     }
/*     */   }
/*     */ 
/*     */   static Algorithm getAlgorithmMapping(String paramString)
/*     */   {
/*  79 */     return (Algorithm)algorithmsMap.get(paramString);
/*     */   }
/*     */ 
/*     */   public static String translateURItoJCEID(String paramString)
/*     */   {
/*  90 */     if (log.isLoggable(Level.FINE)) {
/*  91 */       log.log(Level.FINE, "Request for URI " + paramString);
/*     */     }
/*  93 */     String str = (String)uriToJCEName.get(paramString);
/*  94 */     return str;
/*     */   }
/*     */ 
/*     */   public static String getAlgorithmClassFromURI(String paramString)
/*     */   {
/* 106 */     if (log.isLoggable(Level.FINE)) {
/* 107 */       log.log(Level.FINE, "Request for URI " + paramString);
/*     */     }
/* 109 */     return ((Algorithm)algorithmsMap.get(paramString)).algorithmClass;
/*     */   }
/*     */ 
/*     */   public static int getKeyLengthFromURI(String paramString)
/*     */   {
/* 119 */     return Integer.parseInt(((Algorithm)algorithmsMap.get(paramString)).keyLength);
/*     */   }
/*     */ 
/*     */   public static String getJCEKeyAlgorithmFromURI(String paramString)
/*     */   {
/* 131 */     return ((Algorithm)algorithmsMap.get(paramString)).requiredKey;
/*     */   }
/*     */ 
/*     */   public static String getProviderId()
/*     */   {
/* 140 */     return providerName;
/*     */   }
/*     */ 
/*     */   public static void setProviderId(String paramString)
/*     */   {
/* 148 */     providerName = paramString;
/*     */   }
/*     */ 
/*     */   public static class Algorithm
/*     */   {
/*     */     String algorithmClass;
/*     */     String keyLength;
/*     */     String requiredKey;
/*     */ 
/*     */     public Algorithm(Element paramElement)
/*     */     {
/* 163 */       this.algorithmClass = paramElement.getAttribute("AlgorithmClass");
/* 164 */       this.keyLength = paramElement.getAttribute("KeyLength");
/* 165 */       this.requiredKey = paramElement.getAttribute("RequiredKey");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.algorithms.JCEMapper
 * JD-Core Version:    0.6.2
 */