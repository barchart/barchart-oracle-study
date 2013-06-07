/*     */ package com.sun.corba.se.impl.encoding;
/*     */ 
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.portable.InputStream;
/*     */ 
/*     */ public class EncapsOutputStream extends CDROutputStream
/*     */ {
/*     */   static final boolean usePooledByteBuffers = false;
/*     */ 
/*     */   public EncapsOutputStream(ORB paramORB)
/*     */   {
/*  77 */     this(paramORB, GIOPVersion.V1_2);
/*     */   }
/*     */ 
/*     */   public EncapsOutputStream(ORB paramORB, GIOPVersion paramGIOPVersion)
/*     */   {
/*  85 */     this(paramORB, paramGIOPVersion, false);
/*     */   }
/*     */ 
/*     */   public EncapsOutputStream(ORB paramORB, boolean paramBoolean)
/*     */   {
/*  91 */     this(paramORB, GIOPVersion.V1_2, paramBoolean);
/*     */   }
/*     */ 
/*     */   public EncapsOutputStream(ORB paramORB, GIOPVersion paramGIOPVersion, boolean paramBoolean)
/*     */   {
/*  98 */     super(paramORB, paramGIOPVersion, (byte)0, paramBoolean, BufferManagerFactory.newBufferManagerWrite(0, (byte)0, paramORB), (byte)1, false);
/*     */   }
/*     */ 
/*     */   public InputStream create_input_stream()
/*     */   {
/* 108 */     freeInternalCaches();
/*     */ 
/* 110 */     return new EncapsInputStream(orb(), getByteBuffer(), getSize(), isLittleEndian(), getGIOPVersion());
/*     */   }
/*     */ 
/*     */   protected CodeSetConversion.CTBConverter createCharCTBConverter()
/*     */   {
/* 118 */     return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
/* 122 */     if (getGIOPVersion().equals(GIOPVersion.V1_0)) {
/* 123 */       throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */ 
/* 127 */     if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
/* 128 */       return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, isLittleEndian(), false);
/*     */     }
/*     */ 
/* 137 */     boolean bool = ((ORB)orb()).getORBData().useByteOrderMarkersInEncapsulations();
/*     */ 
/* 139 */     return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, bool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.encoding.EncapsOutputStream
 * JD-Core Version:    0.6.2
 */