/*     */ package com.sun.corba.se.spi.servicecontext;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ import org.omg.CORBA_2_3.portable.OutputStream;
/*     */ 
/*     */ public abstract class ServiceContext
/*     */ {
/* 114 */   protected InputStream in = null;
/*     */ 
/*     */   protected ServiceContext()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void dprint(String paramString)
/*     */   {
/*  70 */     ORBUtility.dprint(this, paramString);
/*     */   }
/*     */ 
/*     */   protected ServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
/*     */     throws SystemException
/*     */   {
/*  82 */     this.in = paramInputStream;
/*     */   }
/*     */ 
/*     */   public abstract int getId();
/*     */ 
/*     */   public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
/*     */     throws SystemException
/*     */   {
/*  95 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream((ORB)paramOutputStream.orb(), paramGIOPVersion);
/*  96 */     localEncapsOutputStream.putEndian();
/*  97 */     writeData(localEncapsOutputStream);
/*  98 */     byte[] arrayOfByte = localEncapsOutputStream.toByteArray();
/*     */ 
/* 100 */     paramOutputStream.write_long(getId());
/* 101 */     paramOutputStream.write_long(arrayOfByte.length);
/* 102 */     paramOutputStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
/*     */   }
/*     */ 
/*     */   protected abstract void writeData(OutputStream paramOutputStream);
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     return "ServiceContext[ id=" + getId() + " ]";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.servicecontext.ServiceContext
 * JD-Core Version:    0.6.2
 */