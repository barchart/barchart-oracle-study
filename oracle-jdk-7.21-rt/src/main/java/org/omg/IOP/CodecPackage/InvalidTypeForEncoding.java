/*    */ package org.omg.IOP.CodecPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class InvalidTypeForEncoding extends UserException
/*    */ {
/*    */   public InvalidTypeForEncoding()
/*    */   {
/* 16 */     super(InvalidTypeForEncodingHelper.id());
/*    */   }
/*    */ 
/*    */   public InvalidTypeForEncoding(String paramString)
/*    */   {
/* 22 */     super(InvalidTypeForEncodingHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.IOP.CodecPackage.InvalidTypeForEncoding
 * JD-Core Version:    0.6.2
 */