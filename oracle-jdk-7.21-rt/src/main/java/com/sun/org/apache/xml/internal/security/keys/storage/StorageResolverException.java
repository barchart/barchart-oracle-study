/*    */ package com.sun.org.apache.xml.internal.security.keys.storage;
/*    */ 
/*    */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*    */ 
/*    */ public class StorageResolverException extends XMLSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public StorageResolverException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public StorageResolverException(String paramString)
/*    */   {
/* 53 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public StorageResolverException(String paramString, Object[] paramArrayOfObject)
/*    */   {
/* 63 */     super(paramString, paramArrayOfObject);
/*    */   }
/*    */ 
/*    */   public StorageResolverException(String paramString, Exception paramException)
/*    */   {
/* 73 */     super(paramString, paramException);
/*    */   }
/*    */ 
/*    */   public StorageResolverException(String paramString, Object[] paramArrayOfObject, Exception paramException)
/*    */   {
/* 85 */     super(paramString, paramArrayOfObject, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException
 * JD-Core Version:    0.6.2
 */