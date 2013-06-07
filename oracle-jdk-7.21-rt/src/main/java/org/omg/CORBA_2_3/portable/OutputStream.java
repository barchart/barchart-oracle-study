/*    */ package org.omg.CORBA_2_3.portable;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.omg.CORBA.NO_IMPLEMENT;
/*    */ import org.omg.CORBA.portable.BoxedValueHelper;
/*    */ 
/*    */ public abstract class OutputStream extends org.omg.CORBA.portable.OutputStream
/*    */ {
/*    */   public void write_value(Serializable paramSerializable)
/*    */   {
/* 51 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ 
/*    */   public void write_value(Serializable paramSerializable, Class paramClass)
/*    */   {
/* 60 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ 
/*    */   public void write_value(Serializable paramSerializable, String paramString)
/*    */   {
/* 70 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ 
/*    */   public void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper)
/*    */   {
/* 80 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ 
/*    */   public void write_abstract_interface(Object paramObject)
/*    */   {
/* 88 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA_2_3.portable.OutputStream
 * JD-Core Version:    0.6.2
 */