/*     */ package com.sun.org.apache.xml.internal.security;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
/*     */ import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
/*     */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transform;
/*     */ import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.I18n;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class Init
/*     */ {
/*  56 */   static Logger log = Logger.getLogger(Init.class.getName());
/*     */ 
/*  60 */   private static boolean _alreadyInitialized = false;
/*     */   public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
/*     */ 
/*     */   public static final boolean isInitialized()
/*     */   {
/*  71 */     return _alreadyInitialized;
/*     */   }
/*     */ 
/*     */   public static synchronized void init()
/*     */   {
/*  80 */     if (_alreadyInitialized) {
/*  81 */       return;
/*     */     }
/*  83 */     long l1 = 0L;
/*  84 */     long l2 = 0L;
/*  85 */     long l3 = 0L;
/*  86 */     long l4 = 0L;
/*  87 */     long l5 = 0L;
/*  88 */     long l6 = 0L;
/*  89 */     long l7 = 0L;
/*  90 */     long l8 = 0L;
/*  91 */     long l9 = 0L;
/*  92 */     long l10 = 0L;
/*  93 */     long l11 = 0L;
/*  94 */     long l12 = 0L;
/*  95 */     _alreadyInitialized = true;
/*     */     try
/*     */     {
/*  98 */       long l13 = System.currentTimeMillis();
/*  99 */       long l14 = System.currentTimeMillis();
/*     */ 
/* 103 */       long l15 = System.currentTimeMillis();
/*     */ 
/* 106 */       long l16 = System.currentTimeMillis();
/* 107 */       DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 109 */       localDocumentBuilderFactory.setNamespaceAware(true);
/* 110 */       localDocumentBuilderFactory.setValidating(false);
/*     */ 
/* 112 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/*     */ 
/* 118 */       InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run()
/*     */         {
/* 123 */           return getClass().getResourceAsStream("resource/config.xml");
/*     */         }
/*     */       });
/* 129 */       Document localDocument = localDocumentBuilder.parse(localInputStream);
/* 130 */       long l17 = System.currentTimeMillis();
/* 131 */       long l18 = 0L;
/*     */ 
/* 134 */       l5 = System.currentTimeMillis();
/*     */       try {
/* 136 */         KeyInfo.init();
/*     */       } catch (Exception localException2) {
/* 138 */         localException2.printStackTrace();
/*     */ 
/* 140 */         throw localException2;
/*     */       }
/* 142 */       l11 = System.currentTimeMillis();
/*     */ 
/* 145 */       long l19 = 0L;
/* 146 */       long l20 = 0L;
/* 147 */       long l21 = 0L;
/* 148 */       long l22 = 0L;
/* 149 */       long l23 = 0L;
/* 150 */       Node localNode1 = localDocument.getFirstChild();
/* 151 */       while ((localNode1 != null) && 
/* 152 */         (!"Configuration".equals(localNode1.getLocalName()))) {
/* 151 */         localNode1 = localNode1.getNextSibling();
/*     */       }
/*     */ 
/* 156 */       for (Node localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling()) {
/* 157 */         if (localNode2.getNodeType() == 1)
/*     */         {
/* 160 */           String str1 = localNode2.getLocalName();
/*     */           Element[] arrayOfElement;
/*     */           int i;
/*     */           String str2;
/*     */           String str3;
/*     */           Object[] arrayOfObject;
/* 181 */           if (str1.equals("CanonicalizationMethods")) {
/* 182 */             l2 = System.currentTimeMillis();
/* 183 */             Canonicalizer.init();
/* 184 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod");
/*     */ 
/* 186 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 187 */               str2 = arrayOfElement[i].getAttributeNS(null, "URI");
/*     */ 
/* 189 */               str3 = arrayOfElement[i].getAttributeNS(null, "JAVACLASS");
/*     */               try
/*     */               {
/* 193 */                 Class.forName(str3);
/*     */ 
/* 204 */                 if (log.isLoggable(Level.FINE)) {
/* 205 */                   log.log(Level.FINE, "Canonicalizer.register(" + str2 + ", " + str3 + ")");
/*     */                 }
/* 207 */                 Canonicalizer.register(str2, str3);
/*     */               } catch (ClassNotFoundException localClassNotFoundException1) {
/* 209 */                 arrayOfObject = new Object[] { str2, str3 };
/*     */ 
/* 211 */                 log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
/*     */               }
/*     */             }
/*     */ 
/* 215 */             l3 = System.currentTimeMillis();
/*     */           }
/*     */ 
/* 218 */           if (str1.equals("TransformAlgorithms")) {
/* 219 */             l19 = System.currentTimeMillis();
/* 220 */             Transform.init();
/*     */ 
/* 222 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm");
/*     */ 
/* 224 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 225 */               str2 = arrayOfElement[i].getAttributeNS(null, "URI");
/*     */ 
/* 227 */               str3 = arrayOfElement[i].getAttributeNS(null, "JAVACLASS");
/*     */               try
/*     */               {
/* 231 */                 Class.forName(str3);
/* 232 */                 if (log.isLoggable(Level.FINE))
/* 233 */                   log.log(Level.FINE, "Transform.register(" + str2 + ", " + str3 + ")");
/* 234 */                 Transform.register(str2, str3);
/*     */               } catch (ClassNotFoundException localClassNotFoundException2) {
/* 236 */                 arrayOfObject = new Object[] { str2, str3 };
/*     */ 
/* 238 */                 log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
/*     */               }
/*     */               catch (NoClassDefFoundError localNoClassDefFoundError)
/*     */               {
/* 242 */                 log.log(Level.WARNING, "Not able to found dependecies for algorithm, I'm keep working.");
/*     */               }
/*     */             }
/* 245 */             l10 = System.currentTimeMillis();
/*     */           }
/*     */ 
/* 249 */           if ("JCEAlgorithmMappings".equals(str1)) {
/* 250 */             l20 = System.currentTimeMillis();
/* 251 */             JCEMapper.init((Element)localNode2);
/* 252 */             l4 = System.currentTimeMillis();
/*     */           }
/*     */ 
/* 257 */           if (str1.equals("SignatureAlgorithms")) {
/* 258 */             l21 = System.currentTimeMillis();
/* 259 */             SignatureAlgorithm.providerInit();
/*     */ 
/* 261 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm");
/*     */ 
/* 264 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 265 */               str2 = arrayOfElement[i].getAttributeNS(null, "URI");
/*     */ 
/* 267 */               str3 = arrayOfElement[i].getAttributeNS(null, "JAVACLASS");
/*     */               try
/*     */               {
/* 274 */                 Class.forName(str3);
/*     */ 
/* 285 */                 if (log.isLoggable(Level.FINE))
/* 286 */                   log.log(Level.FINE, "SignatureAlgorithm.register(" + str2 + ", " + str3 + ")");
/* 287 */                 SignatureAlgorithm.register(str2, str3);
/*     */               } catch (ClassNotFoundException localClassNotFoundException3) {
/* 289 */                 arrayOfObject = new Object[] { str2, str3 };
/*     */ 
/* 291 */                 log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 296 */             l9 = System.currentTimeMillis();
/*     */           }
/*     */ 
/* 301 */           if (str1.equals("ResourceResolvers")) {
/* 302 */             l8 = System.currentTimeMillis();
/* 303 */             ResourceResolver.init();
/*     */ 
/* 305 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
/*     */ 
/* 308 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 309 */               str2 = arrayOfElement[i].getAttributeNS(null, "JAVACLASS");
/*     */ 
/* 312 */               str3 = arrayOfElement[i].getAttributeNS(null, "DESCRIPTION");
/*     */ 
/* 316 */               if ((str3 != null) && (str3.length() > 0)) {
/* 317 */                 if (log.isLoggable(Level.FINE))
/* 318 */                   log.log(Level.FINE, "Register Resolver: " + str2 + ": " + str3);
/*     */               }
/* 320 */               else if (log.isLoggable(Level.FINE))
/* 321 */                 log.log(Level.FINE, "Register Resolver: " + str2 + ": For unknown purposes");
/*     */               try
/*     */               {
/* 324 */                 ResourceResolver.register(str2);
/*     */               } catch (Throwable localThrowable) {
/* 326 */                 log.log(Level.WARNING, "Cannot register:" + str2 + " perhaps some needed jars are not installed", localThrowable);
/*     */               }
/* 328 */               l22 = System.currentTimeMillis();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 339 */           if (str1.equals("KeyResolver")) {
/* 340 */             l12 = System.currentTimeMillis();
/* 341 */             KeyResolver.init();
/*     */ 
/* 343 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
/*     */ 
/* 345 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 346 */               str2 = arrayOfElement[i].getAttributeNS(null, "JAVACLASS");
/*     */ 
/* 349 */               str3 = arrayOfElement[i].getAttributeNS(null, "DESCRIPTION");
/*     */ 
/* 353 */               if ((str3 != null) && (str3.length() > 0)) {
/* 354 */                 if (log.isLoggable(Level.FINE))
/* 355 */                   log.log(Level.FINE, "Register Resolver: " + str2 + ": " + str3);
/*     */               }
/* 357 */               else if (log.isLoggable(Level.FINE)) {
/* 358 */                 log.log(Level.FINE, "Register Resolver: " + str2 + ": For unknown purposes");
/*     */               }
/*     */ 
/* 361 */               KeyResolver.register(str2);
/*     */             }
/* 363 */             l6 = System.currentTimeMillis();
/*     */           }
/*     */ 
/* 367 */           if (str1.equals("PrefixMappings")) {
/* 368 */             l7 = System.currentTimeMillis();
/* 369 */             if (log.isLoggable(Level.FINE)) {
/* 370 */               log.log(Level.FINE, "Now I try to bind prefixes:");
/*     */             }
/* 372 */             arrayOfElement = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping");
/*     */ 
/* 374 */             for (i = 0; i < arrayOfElement.length; i++) {
/* 375 */               str2 = arrayOfElement[i].getAttributeNS(null, "namespace");
/*     */ 
/* 377 */               str3 = arrayOfElement[i].getAttributeNS(null, "prefix");
/*     */ 
/* 379 */               if (log.isLoggable(Level.FINE))
/* 380 */                 log.log(Level.FINE, "Now I try to bind " + str3 + " to " + str2);
/* 381 */               ElementProxy.setDefaultPrefix(str2, str3);
/*     */             }
/*     */ 
/* 384 */             l23 = System.currentTimeMillis();
/*     */           }
/*     */         }
/*     */       }
/* 388 */       long l24 = System.currentTimeMillis();
/*     */ 
/* 391 */       if (log.isLoggable(Level.FINE)) {
/* 392 */         log.log(Level.FINE, "XX_init                             " + (int)(l24 - l13) + " ms");
/* 393 */         log.log(Level.FINE, "  XX_prng                           " + (int)(l15 - l14) + " ms");
/* 394 */         log.log(Level.FINE, "  XX_parsing                        " + (int)(l17 - l16) + " ms");
/* 395 */         log.log(Level.FINE, "  XX_configure_i18n                 " + (int)(l1 - l18) + " ms");
/* 396 */         log.log(Level.FINE, "  XX_configure_reg_c14n             " + (int)(l3 - l2) + " ms");
/* 397 */         log.log(Level.FINE, "  XX_configure_reg_jcemapper        " + (int)(l4 - l20) + " ms");
/* 398 */         log.log(Level.FINE, "  XX_configure_reg_keyInfo          " + (int)(l11 - l5) + " ms");
/* 399 */         log.log(Level.FINE, "  XX_configure_reg_keyResolver      " + (int)(l6 - l12) + " ms");
/* 400 */         log.log(Level.FINE, "  XX_configure_reg_prefixes         " + (int)(l23 - l7) + " ms");
/* 401 */         log.log(Level.FINE, "  XX_configure_reg_resourceresolver " + (int)(l22 - l8) + " ms");
/* 402 */         log.log(Level.FINE, "  XX_configure_reg_sigalgos         " + (int)(l9 - l21) + " ms");
/* 403 */         log.log(Level.FINE, "  XX_configure_reg_transforms       " + (int)(l10 - l19) + " ms");
/*     */       }
/*     */     } catch (Exception localException1) {
/* 406 */       log.log(Level.SEVERE, "Bad: ", localException1);
/* 407 */       localException1.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.Init
 * JD-Core Version:    0.6.2
 */