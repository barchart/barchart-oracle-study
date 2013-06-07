/*    */ package org.omg.CORBA.portable;
/*    */ 
/*    */ public class ApplicationException extends Exception
/*    */ {
/*    */   private String id;
/*    */   private InputStream ins;
/*    */ 
/*    */   public ApplicationException(String paramString, InputStream paramInputStream)
/*    */   {
/* 41 */     this.id = paramString;
/* 42 */     this.ins = paramInputStream;
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 51 */     return this.id;
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream()
/*    */   {
/* 59 */     return this.ins;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.portable.ApplicationException
 * JD-Core Version:    0.6.2
 */