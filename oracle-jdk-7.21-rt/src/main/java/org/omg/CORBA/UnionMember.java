/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class UnionMember
/*    */   implements IDLEntity
/*    */ {
/*    */   public String name;
/*    */   public Any label;
/*    */   public TypeCode type;
/*    */   public IDLType type_def;
/*    */ 
/*    */   public UnionMember()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnionMember(String paramString, Any paramAny, TypeCode paramTypeCode, IDLType paramIDLType)
/*    */   {
/* 90 */     this.name = paramString;
/* 91 */     this.label = paramAny;
/* 92 */     this.type = paramTypeCode;
/* 93 */     this.type_def = paramIDLType;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.UnionMember
 * JD-Core Version:    0.6.2
 */