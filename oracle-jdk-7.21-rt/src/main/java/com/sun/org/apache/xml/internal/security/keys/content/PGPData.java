/*    */ package com.sun.org.apache.xml.internal.security.keys.content;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class PGPData extends SignatureElementProxy
/*    */   implements KeyInfoContent
/*    */ {
/*    */   public PGPData(Element paramElement, String paramString)
/*    */     throws XMLSecurityException
/*    */   {
/* 43 */     super(paramElement, paramString);
/*    */   }
/*    */ 
/*    */   public String getBaseLocalName()
/*    */   {
/* 48 */     return "PGPData";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.content.PGPData
 * JD-Core Version:    0.6.2
 */