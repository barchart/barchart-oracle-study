/*     */ package org.omg.CORBA;
/*     */ 
/*     */ import org.omg.CORBA.portable.InputStream;
/*     */ import org.omg.CORBA.portable.OutputStream;
/*     */ import org.omg.CORBA.portable.Streamable;
/*     */ 
/*     */ public final class ByteHolder
/*     */   implements Streamable
/*     */ {
/*     */   public byte value;
/*     */ 
/*     */   public ByteHolder()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ByteHolder(byte paramByte)
/*     */   {
/*  76 */     this.value = paramByte;
/*     */   }
/*     */ 
/*     */   public void _read(InputStream paramInputStream)
/*     */   {
/*  87 */     this.value = paramInputStream.read_octet();
/*     */   }
/*     */ 
/*     */   public void _write(OutputStream paramOutputStream)
/*     */   {
/*  97 */     paramOutputStream.write_octet(this.value);
/*     */   }
/*     */ 
/*     */   public TypeCode _type()
/*     */   {
/* 108 */     return ORB.init().get_primitive_tc(TCKind.tk_octet);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.ByteHolder
 * JD-Core Version:    0.6.2
 */