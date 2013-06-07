/*    */ package com.sun.org.apache.xml.internal.security.keys;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ 
/*    */ public class ContentHandlerAlreadyRegisteredException extends XMLSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ContentHandlerAlreadyRegisteredException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ContentHandlerAlreadyRegisteredException(String paramString)
/*    */   {
/* 54 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ContentHandlerAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject)
/*    */   {
/* 65 */     super(paramString, paramArrayOfObject);
/*    */   }
/*    */ 
/*    */   public ContentHandlerAlreadyRegisteredException(String paramString, Exception paramException)
/*    */   {
/* 76 */     super(paramString, paramException);
/*    */   }
/*    */ 
/*    */   public ContentHandlerAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject, Exception paramException)
/*    */   {
/* 88 */     super(paramString, paramArrayOfObject, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.ContentHandlerAlreadyRegisteredException
 * JD-Core Version:    0.6.2
 */