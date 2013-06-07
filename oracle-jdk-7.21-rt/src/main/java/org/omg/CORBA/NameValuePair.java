/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class NameValuePair
/*    */   implements IDLEntity
/*    */ {
/*    */   public String id;
/*    */   public Any value;
/*    */ 
/*    */   public NameValuePair()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NameValuePair(String paramString, Any paramAny)
/*    */   {
/* 60 */     this.id = paramString;
/* 61 */     this.value = paramAny;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.NameValuePair
 * JD-Core Version:    0.6.2
 */