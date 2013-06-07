/*    */ package org.omg.DynamicAny.DynAnyPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class InvalidValue extends UserException
/*    */ {
/*    */   public InvalidValue()
/*    */   {
/* 16 */     super(InvalidValueHelper.id());
/*    */   }
/*    */ 
/*    */   public InvalidValue(String paramString)
/*    */   {
/* 22 */     super(InvalidValueHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.DynamicAny.DynAnyPackage.InvalidValue
 * JD-Core Version:    0.6.2
 */