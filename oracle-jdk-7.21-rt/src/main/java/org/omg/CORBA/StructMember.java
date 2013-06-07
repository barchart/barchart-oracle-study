/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class StructMember
/*    */   implements IDLEntity
/*    */ {
/*    */   public String name;
/*    */   public TypeCode type;
/*    */   public IDLType type_def;
/*    */ 
/*    */   public StructMember()
/*    */   {
/*    */   }
/*    */ 
/*    */   public StructMember(String paramString, TypeCode paramTypeCode, IDLType paramIDLType)
/*    */   {
/* 85 */     this.name = paramString;
/* 86 */     this.type = paramTypeCode;
/* 87 */     this.type_def = paramIDLType;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.StructMember
 * JD-Core Version:    0.6.2
 */