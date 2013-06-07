/*    */ package com.sun.org.apache.xml.internal.security.signature;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ 
/*    */ public class XMLSignatureException extends XMLSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public XMLSignatureException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public XMLSignatureException(String paramString)
/*    */   {
/* 55 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public XMLSignatureException(String paramString, Object[] paramArrayOfObject)
/*    */   {
/* 65 */     super(paramString, paramArrayOfObject);
/*    */   }
/*    */ 
/*    */   public XMLSignatureException(String paramString, Exception paramException)
/*    */   {
/* 75 */     super(paramString, paramException);
/*    */   }
/*    */ 
/*    */   public XMLSignatureException(String paramString, Object[] paramArrayOfObject, Exception paramException)
/*    */   {
/* 87 */     super(paramString, paramArrayOfObject, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.XMLSignatureException
 * JD-Core Version:    0.6.2
 */