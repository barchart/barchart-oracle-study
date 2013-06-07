/*    */ package org.omg.IOP.CodecFactoryPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class UnknownEncoding extends UserException
/*    */ {
/*    */   public UnknownEncoding()
/*    */   {
/* 16 */     super(UnknownEncodingHelper.id());
/*    */   }
/*    */ 
/*    */   public UnknownEncoding(String paramString)
/*    */   {
/* 22 */     super(UnknownEncodingHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.IOP.CodecFactoryPackage.UnknownEncoding
 * JD-Core Version:    0.6.2
 */