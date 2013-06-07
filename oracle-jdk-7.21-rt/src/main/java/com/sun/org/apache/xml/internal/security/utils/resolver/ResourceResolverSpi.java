/*     */ package com.sun.org.apache.xml.internal.security.utils.resolver;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Attr;
/*     */ 
/*     */ public abstract class ResourceResolverSpi
/*     */ {
/*  39 */   static Logger log = Logger.getLogger(ResourceResolverSpi.class.getName());
/*     */ 
/*  44 */   protected Map _properties = null;
/*     */ 
/*     */   public abstract XMLSignatureInput engineResolve(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException;
/*     */ 
/*     */   public void engineSetProperty(String paramString1, String paramString2)
/*     */   {
/*  65 */     if (this._properties == null) {
/*  66 */       this._properties = new HashMap();
/*     */     }
/*  68 */     this._properties.put(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public String engineGetProperty(String paramString)
/*     */   {
/*  78 */     if (this._properties == null) {
/*  79 */       return null;
/*     */     }
/*  81 */     return (String)this._properties.get(paramString);
/*     */   }
/*     */ 
/*     */   public void engineAddProperies(Map paramMap)
/*     */   {
/*  89 */     if (paramMap != null) {
/*  90 */       if (this._properties == null) {
/*  91 */         this._properties = new HashMap();
/*     */       }
/*  93 */       this._properties.putAll(paramMap);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean engineIsThreadSafe()
/*     */   {
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract boolean engineCanResolve(Attr paramAttr, String paramString);
/*     */ 
/*     */   public String[] engineGetPropertyKeys()
/*     */   {
/* 121 */     return new String[0];
/*     */   }
/*     */ 
/*     */   public boolean understandsProperty(String paramString)
/*     */   {
/* 132 */     String[] arrayOfString = engineGetPropertyKeys();
/*     */ 
/* 134 */     if (arrayOfString != null) {
/* 135 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 136 */         if (arrayOfString[i].equals(paramString)) {
/* 137 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 142 */     return false;
/*     */   }
/*     */ 
/*     */   public static String fixURI(String paramString)
/*     */   {
/* 156 */     paramString = paramString.replace(File.separatorChar, '/');
/*     */     int i;
/*     */     int j;
/* 158 */     if (paramString.length() >= 4)
/*     */     {
/* 161 */       i = Character.toUpperCase(paramString.charAt(0));
/* 162 */       j = paramString.charAt(1);
/* 163 */       int k = paramString.charAt(2);
/* 164 */       int m = paramString.charAt(3);
/* 165 */       int n = (65 <= i) && (i <= 90) && (j == 58) && (k == 47) && (m != 47) ? 1 : 0;
/*     */ 
/* 169 */       if ((n != 0) && 
/* 170 */         (log.isLoggable(Level.FINE))) {
/* 171 */         log.log(Level.FINE, "Found DOS filename: " + paramString);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     if (paramString.length() >= 2) {
/* 177 */       i = paramString.charAt(1);
/*     */ 
/* 179 */       if (i == 58) {
/* 180 */         j = Character.toUpperCase(paramString.charAt(0));
/*     */ 
/* 182 */         if ((65 <= j) && (j <= 90)) {
/* 183 */           paramString = "/" + paramString;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 189 */     return paramString;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi
 * JD-Core Version:    0.6.2
 */