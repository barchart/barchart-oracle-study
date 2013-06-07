/*    */ package org.omg.DynamicAny;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class NameDynAnyPair
/*    */   implements IDLEntity
/*    */ {
/* 17 */   public String id = null;
/*    */ 
/* 22 */   public DynAny value = null;
/*    */ 
/*    */   public NameDynAnyPair()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NameDynAnyPair(String paramString, DynAny paramDynAny)
/*    */   {
/* 30 */     this.id = paramString;
/* 31 */     this.value = paramDynAny;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.DynamicAny.NameDynAnyPair
 * JD-Core Version:    0.6.2
 */