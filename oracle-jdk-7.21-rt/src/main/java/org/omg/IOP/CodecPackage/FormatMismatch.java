/*    */ package org.omg.IOP.CodecPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class FormatMismatch extends UserException
/*    */ {
/*    */   public FormatMismatch()
/*    */   {
/* 16 */     super(FormatMismatchHelper.id());
/*    */   }
/*    */ 
/*    */   public FormatMismatch(String paramString)
/*    */   {
/* 22 */     super(FormatMismatchHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.IOP.CodecPackage.FormatMismatch
 * JD-Core Version:    0.6.2
 */