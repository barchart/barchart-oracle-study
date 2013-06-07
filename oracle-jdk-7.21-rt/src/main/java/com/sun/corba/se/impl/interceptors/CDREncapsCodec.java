/*     */ package com.sun.corba.se.impl.interceptors;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.AnyImpl;
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import org.omg.CORBA.Any;
/*     */ import org.omg.CORBA.LocalObject;
/*     */ import org.omg.CORBA.TypeCode;
/*     */ import org.omg.IOP.Codec;
/*     */ import org.omg.IOP.CodecPackage.FormatMismatch;
/*     */ import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
/*     */ import org.omg.IOP.CodecPackage.TypeMismatch;
/*     */ 
/*     */ public final class CDREncapsCodec extends LocalObject
/*     */   implements Codec
/*     */ {
/*     */   private org.omg.CORBA.ORB orb;
/*     */   ORBUtilSystemException wrapper;
/*     */   private GIOPVersion giopVersion;
/*     */ 
/*     */   public CDREncapsCodec(org.omg.CORBA.ORB paramORB, int paramInt1, int paramInt2)
/*     */   {
/*  78 */     this.orb = paramORB;
/*  79 */     this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.protocol");
/*     */ 
/*  82 */     this.giopVersion = GIOPVersion.getInstance((byte)paramInt1, (byte)paramInt2);
/*     */   }
/*     */ 
/*     */   public byte[] encode(Any paramAny)
/*     */     throws InvalidTypeForEncoding
/*     */   {
/*  91 */     if (paramAny == null)
/*  92 */       throw this.wrapper.nullParam();
/*  93 */     return encodeImpl(paramAny, true);
/*     */   }
/*     */ 
/*     */   public Any decode(byte[] paramArrayOfByte)
/*     */     throws FormatMismatch
/*     */   {
/* 103 */     if (paramArrayOfByte == null)
/* 104 */       throw this.wrapper.nullParam();
/* 105 */     return decodeImpl(paramArrayOfByte, null);
/*     */   }
/*     */ 
/*     */   public byte[] encode_value(Any paramAny)
/*     */     throws InvalidTypeForEncoding
/*     */   {
/* 115 */     if (paramAny == null)
/* 116 */       throw this.wrapper.nullParam();
/* 117 */     return encodeImpl(paramAny, false);
/*     */   }
/*     */ 
/*     */   public Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode)
/*     */     throws FormatMismatch, TypeMismatch
/*     */   {
/* 128 */     if (paramArrayOfByte == null)
/* 129 */       throw this.wrapper.nullParam();
/* 130 */     if (paramTypeCode == null)
/* 131 */       throw this.wrapper.nullParam();
/* 132 */     return decodeImpl(paramArrayOfByte, paramTypeCode);
/*     */   }
/*     */ 
/*     */   private byte[] encodeImpl(Any paramAny, boolean paramBoolean)
/*     */     throws InvalidTypeForEncoding
/*     */   {
/* 144 */     if (paramAny == null) {
/* 145 */       throw this.wrapper.nullParam();
/*     */     }
/*     */ 
/* 158 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream((com.sun.corba.se.spi.orb.ORB)this.orb, this.giopVersion);
/*     */ 
/* 162 */     localEncapsOutputStream.putEndian();
/*     */ 
/* 165 */     if (paramBoolean) {
/* 166 */       localEncapsOutputStream.write_TypeCode(paramAny.type());
/*     */     }
/*     */ 
/* 170 */     paramAny.write_value(localEncapsOutputStream);
/*     */ 
/* 172 */     return localEncapsOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   private Any decodeImpl(byte[] paramArrayOfByte, TypeCode paramTypeCode)
/*     */     throws FormatMismatch
/*     */   {
/* 184 */     if (paramArrayOfByte == null) {
/* 185 */       throw this.wrapper.nullParam();
/*     */     }
/* 187 */     AnyImpl localAnyImpl = null;
/*     */     try
/*     */     {
/* 195 */       EncapsInputStream localEncapsInputStream = new EncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length, this.giopVersion);
/*     */ 
/* 198 */       localEncapsInputStream.consumeEndian();
/*     */ 
/* 201 */       if (paramTypeCode == null) {
/* 202 */         paramTypeCode = localEncapsInputStream.read_TypeCode();
/*     */       }
/*     */ 
/* 206 */       localAnyImpl = new AnyImpl((com.sun.corba.se.spi.orb.ORB)this.orb);
/* 207 */       localAnyImpl.read_value(localEncapsInputStream, paramTypeCode);
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/* 211 */       throw new FormatMismatch();
/*     */     }
/*     */ 
/* 214 */     return localAnyImpl;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.interceptors.CDREncapsCodec
 * JD-Core Version:    0.6.2
 */