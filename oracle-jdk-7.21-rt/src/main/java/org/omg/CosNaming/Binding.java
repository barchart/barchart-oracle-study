/*    */ package org.omg.CosNaming;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class Binding
/*    */   implements IDLEntity
/*    */ {
/* 13 */   public NameComponent[] binding_name = null;
/*    */ 
/* 16 */   public BindingType binding_type = null;
/*    */ 
/*    */   public Binding()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Binding(NameComponent[] paramArrayOfNameComponent, BindingType paramBindingType)
/*    */   {
/* 24 */     this.binding_name = paramArrayOfNameComponent;
/* 25 */     this.binding_type = paramBindingType;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CosNaming.Binding
 * JD-Core Version:    0.6.2
 */