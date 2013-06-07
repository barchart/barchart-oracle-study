/*     */ package com.sun.corba.se.impl.encoding;
/*     */ 
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.org.omg.SendingContext.CodeBase;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ 
/*     */ public class EncapsInputStream extends CDRInputStream
/*     */ {
/*     */   private ORBUtilSystemException wrapper;
/*     */   private CodeBase codeBase;
/*     */ 
/*     */   public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
/*     */   {
/*  67 */     super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
/*     */ 
/*  74 */     this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
/*     */ 
/*  77 */     performORBVersionSpecificInit();
/*     */   }
/*     */ 
/*     */   public EncapsInputStream(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
/*     */   {
/*  83 */     super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
/*     */ 
/*  90 */     performORBVersionSpecificInit();
/*     */   }
/*     */ 
/*     */   public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt)
/*     */   {
/*  99 */     this(paramORB, paramArrayOfByte, paramInt, GIOPVersion.V1_2);
/*     */   }
/*     */ 
/*     */   public EncapsInputStream(EncapsInputStream paramEncapsInputStream)
/*     */   {
/* 105 */     super(paramEncapsInputStream);
/*     */ 
/* 107 */     this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramEncapsInputStream.orb(), "rpc.encoding");
/*     */ 
/* 110 */     performORBVersionSpecificInit();
/*     */   }
/*     */ 
/*     */   public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion)
/*     */   {
/* 121 */     this(paramORB, paramArrayOfByte, paramInt, false, paramGIOPVersion);
/*     */   }
/*     */ 
/*     */   public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion, CodeBase paramCodeBase)
/*     */   {
/* 135 */     super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, false, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
/*     */ 
/* 145 */     this.codeBase = paramCodeBase;
/*     */ 
/* 147 */     performORBVersionSpecificInit();
/*     */   }
/*     */ 
/*     */   public CDRInputStream dup() {
/* 151 */     return new EncapsInputStream(this);
/*     */   }
/*     */ 
/*     */   protected CodeSetConversion.BTCConverter createCharBTCConverter() {
/* 155 */     return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   protected CodeSetConversion.BTCConverter createWCharBTCConverter()
/*     */   {
/* 160 */     if (getGIOPVersion().equals(GIOPVersion.V1_0)) {
/* 161 */       throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */ 
/* 165 */     if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
/* 166 */       return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, isLittleEndian());
/*     */     }
/*     */ 
/* 176 */     return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, false);
/*     */   }
/*     */ 
/*     */   public CodeBase getCodeBase()
/*     */   {
/* 181 */     return this.codeBase;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.encoding.EncapsInputStream
 * JD-Core Version:    0.6.2
 */