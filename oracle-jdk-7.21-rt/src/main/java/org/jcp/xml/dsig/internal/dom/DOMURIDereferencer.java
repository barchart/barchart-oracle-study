/*    */ package org.jcp.xml.dsig.internal.dom;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.Init;
/*    */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*    */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*    */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*    */ import javax.xml.crypto.Data;
/*    */ import javax.xml.crypto.URIDereferencer;
/*    */ import javax.xml.crypto.URIReference;
/*    */ import javax.xml.crypto.URIReferenceException;
/*    */ import javax.xml.crypto.XMLCryptoContext;
/*    */ import javax.xml.crypto.dom.DOMCryptoContext;
/*    */ import javax.xml.crypto.dom.DOMURIReference;
/*    */ import org.w3c.dom.Attr;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class DOMURIDereferencer
/*    */   implements URIDereferencer
/*    */ {
/* 49 */   static final URIDereferencer INSTANCE = new DOMURIDereferencer();
/*    */ 
/*    */   private DOMURIDereferencer()
/*    */   {
/* 54 */     Init.init();
/*    */   }
/*    */ 
/*    */   public Data dereference(URIReference paramURIReference, XMLCryptoContext paramXMLCryptoContext)
/*    */     throws URIReferenceException
/*    */   {
/* 60 */     if (paramURIReference == null) {
/* 61 */       throw new NullPointerException("uriRef cannot be null");
/*    */     }
/* 63 */     if (paramXMLCryptoContext == null) {
/* 64 */       throw new NullPointerException("context cannot be null");
/*    */     }
/*    */ 
/* 67 */     DOMURIReference localDOMURIReference = (DOMURIReference)paramURIReference;
/* 68 */     Attr localAttr = (Attr)localDOMURIReference.getHere();
/* 69 */     String str1 = paramURIReference.getURI();
/* 70 */     DOMCryptoContext localDOMCryptoContext = (DOMCryptoContext)paramXMLCryptoContext;
/*    */     String str2;
/*    */     Object localObject;
/* 73 */     if ((str1 != null) && (str1.length() != 0) && (str1.charAt(0) == '#')) {
/* 74 */       str2 = str1.substring(1);
/*    */ 
/* 76 */       if (str2.startsWith("xpointer(id(")) {
/* 77 */         int i = str2.indexOf('\'');
/* 78 */         int j = str2.indexOf('\'', i + 1);
/* 79 */         str2 = str2.substring(i + 1, j);
/*    */       }
/*    */ 
/* 85 */       localObject = localDOMCryptoContext.getElementById(str2);
/* 86 */       if (localObject != null) {
/* 87 */         IdResolver.registerElementById((Element)localObject, str2);
/*    */       }
/*    */     }
/*    */     try
/*    */     {
/* 92 */       str2 = paramXMLCryptoContext.getBaseURI();
/* 93 */       localObject = ResourceResolver.getInstance(localAttr, str2);
/*    */ 
/* 95 */       XMLSignatureInput localXMLSignatureInput = ((ResourceResolver)localObject).resolve(localAttr, str2);
/* 96 */       if (localXMLSignatureInput.isOctetStream()) {
/* 97 */         return new ApacheOctetStreamData(localXMLSignatureInput);
/*    */       }
/* 99 */       return new ApacheNodeSetData(localXMLSignatureInput);
/*    */     }
/*    */     catch (Exception localException) {
/* 102 */       throw new URIReferenceException(localException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMURIDereferencer
 * JD-Core Version:    0.6.2
 */