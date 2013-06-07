/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.InputStream;
/*    */ import org.omg.CORBA.portable.OutputStream;
/*    */ import org.omg.CORBA.portable.Streamable;
/*    */ 
/*    */ public final class UShortSeqHolder
/*    */   implements Streamable
/*    */ {
/* 40 */   public short[] value = null;
/*    */ 
/*    */   public UShortSeqHolder()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UShortSeqHolder(short[] paramArrayOfShort)
/*    */   {
/* 48 */     this.value = paramArrayOfShort;
/*    */   }
/*    */ 
/*    */   public void _read(InputStream paramInputStream)
/*    */   {
/* 53 */     this.value = UShortSeqHelper.read(paramInputStream);
/*    */   }
/*    */ 
/*    */   public void _write(OutputStream paramOutputStream)
/*    */   {
/* 58 */     UShortSeqHelper.write(paramOutputStream, this.value);
/*    */   }
/*    */ 
/*    */   public TypeCode _type()
/*    */   {
/* 63 */     return UShortSeqHelper.type();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.UShortSeqHolder
 * JD-Core Version:    0.6.2
 */