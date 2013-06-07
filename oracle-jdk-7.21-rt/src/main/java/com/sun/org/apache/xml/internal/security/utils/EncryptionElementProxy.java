/*    */ package com.sun.org.apache.xml.internal.security.utils;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public abstract class EncryptionElementProxy extends ElementProxy
/*    */ {
/*    */   public EncryptionElementProxy(Document paramDocument)
/*    */   {
/* 44 */     super(paramDocument);
/*    */   }
/*    */ 
/*    */   public EncryptionElementProxy(Element paramElement, String paramString)
/*    */     throws XMLSecurityException
/*    */   {
/* 56 */     super(paramElement, paramString);
/*    */   }
/*    */ 
/*    */   public final String getBaseNamespace()
/*    */   {
/* 61 */     return "http://www.w3.org/2001/04/xmlenc#";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.EncryptionElementProxy
 * JD-Core Version:    0.6.2
 */