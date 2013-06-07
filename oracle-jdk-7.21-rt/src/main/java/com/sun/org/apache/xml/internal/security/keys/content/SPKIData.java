/*    */ package com.sun.org.apache.xml.internal.security.keys.content;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class SPKIData extends SignatureElementProxy
/*    */   implements KeyInfoContent
/*    */ {
/*    */   public SPKIData(Element paramElement, String paramString)
/*    */     throws XMLSecurityException
/*    */   {
/* 44 */     super(paramElement, paramString);
/*    */   }
/*    */ 
/*    */   public String getBaseLocalName()
/*    */   {
/* 49 */     return "SPKIData";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.content.SPKIData
 * JD-Core Version:    0.6.2
 */