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
/*     */ public class ResolverFragment extends ResourceResolverSpi
/*     */ {
/*  45 */   static Logger log = Logger.getLogger(ResolverFragment.class.getName());
/*     */ 
/*     */   public boolean engineIsThreadSafe()
/*     */   {
/*  49 */     return true;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput engineResolve(Attr paramAttr, String paramString)
/*     */     throws ResourceResolverException
/*     */   {
/*  65 */     String str = paramAttr.getNodeValue();
/*  66 */     Document localDocument = paramAttr.getOwnerElement().getOwnerDocument();
/*     */ 
/*  69 */     Object localObject1 = null;
/*  70 */     if (str.equals(""))
/*     */     {
/*  77 */       log.log(Level.FINE, "ResolverFragment with empty URI (means complete document)");
/*  78 */       localObject1 = localDocument;
/*     */     }
/*     */     else
/*     */     {
/*  89 */       localObject2 = str.substring(1);
/*     */ 
/*  92 */       localObject1 = IdResolver.getElementById(localDocument, (String)localObject2);
/*  93 */       if (localObject1 == null) {
/*  94 */         Object[] arrayOfObject = { localObject2 };
/*  95 */         throw new ResourceResolverException("signature.Verification.MissingID", arrayOfObject, paramAttr, paramString);
/*     */       }
/*     */ 
/*  98 */       if (log.isLoggable(Level.FINE)) {
/*  99 */         log.log(Level.FINE, "Try to catch an Element with ID " + (String)localObject2 + " and Element was " + localObject1);
/*     */       }
/*     */     }
/* 102 */     Object localObject2 = new XMLSignatureInput((Node)localObject1);
/* 103 */     ((XMLSignatureInput)localObject2).setExcludeComments(true);
/*     */ 
/* 106 */     ((XMLSignatureInput)localObject2).setMIMEType("text/xml");
/* 107 */     ((XMLSignatureInput)localObject2).setSourceURI(paramString != null ? paramString.concat(paramAttr.getNodeValue()) : paramAttr.getNodeValue());
/*     */ 
/* 109 */     return localObject2;
/*     */   }
/*     */ 
/*     */   public boolean engineCanResolve(Attr paramAttr, String paramString)
/*     */   {
/* 121 */     if (paramAttr == null) {
/* 122 */       log.log(Level.FINE, "Quick fail for null uri");
/* 123 */       return false;
/*     */     }
/*     */ 
/* 126 */     String str = paramAttr.getNodeValue();
/*     */ 
/* 128 */     if ((str.equals("")) || ((str.charAt(0) == '#') && ((str.charAt(1) != 'x') || (!str.startsWith("#xpointer(")))))
/*     */     {
/* 134 */       if (log.isLoggable(Level.FINE))
/* 135 */         log.log(Level.FINE, "State I can resolve reference: \"" + str + "\"");
/* 136 */       return true;
/*     */     }
/* 138 */     if (log.isLoggable(Level.FINE))
/* 139 */       log.log(Level.FINE, "Do not seem to be able to resolve reference: \"" + str + "\"");
/* 140 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment
 * JD-Core Version:    0.6.2
 */