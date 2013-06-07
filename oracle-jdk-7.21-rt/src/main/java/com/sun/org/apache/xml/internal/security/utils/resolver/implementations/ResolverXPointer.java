/*     */ package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class ResolverXPointer extends ResourceResolverSpi
/*     */ {
/*  52 */   static Logger log = Logger.getLogger(ResolverXPointer.class.getName());
/*     */   private static final String XP = "#xpointer(id(";
/* 137 */   private static final int XP_LENGTH = "#xpointer(id(".length();
/*     */ 
/*     */   public boolean engineIsThreadSafe()
/*     */   {
/*  57 */     return true;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput engineResolve(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException
/*     */   {
/*  65 */     Object localObject1 = null;
/*  66 */     Document localDocument = paramAttr.getOwnerElement().getOwnerDocument();
/*     */ 
/*  68 */     String str = paramAttr.getNodeValue();
/*  69 */     if (isXPointerSlash(str)) {
/*  70 */       localObject1 = localDocument;
/*     */     }
/*  72 */     else if (isXPointerId(str)) {
/*  73 */       localObject2 = getXPointerId(str);
/*  74 */       localObject1 = IdResolver.getElementById(localDocument, (String)localObject2);
/*     */ 
/*  78 */       if (localObject1 == null) {
/*  79 */         Object[] arrayOfObject = { localObject2 };
/*     */ 
/*  81 */         throw new ResourceResolverException("signature.Verification.MissingID", arrayOfObject, paramAttr, paramString);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  92 */     Object localObject2 = new XMLSignatureInput((Node)localObject1);
/*     */ 
/*  94 */     ((XMLSignatureInput)localObject2).setMIMEType("text/xml");
/*  95 */     if ((paramString != null) && (paramString.length() > 0))
/*  96 */       ((XMLSignatureInput)localObject2).setSourceURI(paramString.concat(paramAttr.getNodeValue()));
/*     */     else {
/*  98 */       ((XMLSignatureInput)localObject2).setSourceURI(paramAttr.getNodeValue());
/*     */     }
/*     */ 
/* 101 */     return localObject2;
/*     */   }
/*     */ 
/*     */   public boolean engineCanResolve(Attr paramAttr, String paramString)
/*     */   {
/* 109 */     if (paramAttr == null) {
/* 110 */       return false;
/*     */     }
/* 112 */     String str = paramAttr.getNodeValue();
/* 113 */     if ((isXPointerSlash(str)) || (isXPointerId(str))) {
/* 114 */       return true;
/*     */     }
/*     */ 
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isXPointerSlash(String paramString)
/*     */   {
/* 128 */     if (paramString.equals("#xpointer(/)")) {
/* 129 */       return true;
/*     */     }
/*     */ 
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isXPointerId(String paramString)
/*     */   {
/* 148 */     if ((paramString.startsWith("#xpointer(id(")) && (paramString.endsWith("))")))
/*     */     {
/* 150 */       String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
/*     */ 
/* 155 */       int i = str.length() - 1;
/* 156 */       if (((str.charAt(0) == '"') && (str.charAt(i) == '"')) || ((str.charAt(0) == '\'') && (str.charAt(i) == '\'')))
/*     */       {
/* 160 */         if (log.isLoggable(Level.FINE)) {
/* 161 */           log.log(Level.FINE, "Id=" + str.substring(1, i));
/*     */         }
/*     */ 
/* 164 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 168 */     return false;
/*     */   }
/*     */ 
/*     */   private static String getXPointerId(String paramString)
/*     */   {
/* 180 */     if ((paramString.startsWith("#xpointer(id(")) && (paramString.endsWith("))")))
/*     */     {
/* 182 */       String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
/*     */ 
/* 184 */       int i = str.length() - 1;
/* 185 */       if (((str.charAt(0) == '"') && (str.charAt(i) == '"')) || ((str.charAt(0) == '\'') && (str.charAt(i) == '\'')))
/*     */       {
/* 189 */         return str.substring(1, i);
/*     */       }
/*     */     }
/*     */ 
/* 193 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer
 * JD-Core Version:    0.6.2
 */