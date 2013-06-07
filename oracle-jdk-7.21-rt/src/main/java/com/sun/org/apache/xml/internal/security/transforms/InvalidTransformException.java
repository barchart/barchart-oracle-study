/*    */ package com.sun.org.apache.xml.internal.security.transforms;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ 
/*    */ public class InvalidTransformException extends XMLSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public InvalidTransformException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public InvalidTransformException(String paramString)
/*    */   {
/* 53 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public InvalidTransformException(String paramString, Object[] paramArrayOfObject)
/*    */   {
/* 63 */     super(paramString, paramArrayOfObject);
/*    */   }
/*    */ 
/*    */   public InvalidTransformException(String paramString, Exception paramException)
/*    */   {
/* 73 */     super(paramString, paramException);
/*    */   }
/*    */ 
/*    */   public InvalidTransformException(String paramString, Object[] paramArrayOfObject, Exception paramException)
/*    */   {
/* 85 */     super(paramString, paramArrayOfObject, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException
 * JD-Core Version:    0.6.2
 */