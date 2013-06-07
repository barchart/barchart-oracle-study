/*     */ package com.sun.corba.se.impl.ior;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.logging.IORSystemException;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import java.io.IOException;
/*     */ import org.omg.CORBA.MARSHAL;
/*     */ import org.omg.CORBA.OctetSeqHolder;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ 
/*     */ public class ObjectKeyFactoryImpl
/*     */   implements ObjectKeyFactory
/*     */ {
/*     */   public static final int MAGIC_BASE = -1347695874;
/*     */   public static final int JAVAMAGIC_OLD = -1347695874;
/*     */   public static final int JAVAMAGIC_NEW = -1347695873;
/*     */   public static final int JAVAMAGIC_NEWER = -1347695872;
/*     */   public static final int MAX_MAGIC = -1347695872;
/*     */   public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;
/*     */   private final ORB orb;
/*     */   private IORSystemException wrapper;
/* 128 */   private Handler fullKey = new Handler()
/*     */   {
/*     */     public ObjectKeyTemplate handle(int paramAnonymousInt1, int paramAnonymousInt2, InputStream paramAnonymousInputStream, OctetSeqHolder paramAnonymousOctetSeqHolder) {
/* 131 */       Object localObject = null;
/*     */ 
/* 133 */       if ((paramAnonymousInt2 >= 32) && (paramAnonymousInt2 <= 63))
/*     */       {
/* 135 */         if (paramAnonymousInt1 >= -1347695872)
/* 136 */           localObject = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
/*     */         else
/* 138 */           localObject = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
/* 139 */       } else if ((paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < 32)) {
/* 140 */         if (paramAnonymousInt1 >= -1347695872)
/* 141 */           localObject = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
/*     */         else {
/* 143 */           localObject = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
/*     */         }
/*     */       }
/* 146 */       return localObject;
/*     */     }
/* 128 */   };
/*     */ 
/* 152 */   private Handler oktempOnly = new Handler()
/*     */   {
/*     */     public ObjectKeyTemplate handle(int paramAnonymousInt1, int paramAnonymousInt2, InputStream paramAnonymousInputStream, OctetSeqHolder paramAnonymousOctetSeqHolder) {
/* 155 */       Object localObject = null;
/*     */ 
/* 157 */       if ((paramAnonymousInt2 >= 32) && (paramAnonymousInt2 <= 63))
/*     */       {
/* 159 */         if (paramAnonymousInt1 >= -1347695872)
/* 160 */           localObject = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
/*     */         else
/* 162 */           localObject = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
/* 163 */       } else if ((paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < 32)) {
/* 164 */         if (paramAnonymousInt1 >= -1347695872)
/* 165 */           localObject = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
/*     */         else {
/* 167 */           localObject = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
/*     */         }
/*     */       }
/* 170 */       return localObject;
/*     */     }
/* 152 */   };
/*     */ 
/*     */   public ObjectKeyFactoryImpl(ORB paramORB)
/*     */   {
/*  94 */     this.orb = paramORB;
/*  95 */     this.wrapper = IORSystemException.get(paramORB, "oa.ior");
/*     */   }
/*     */ 
/*     */   private boolean validMagic(int paramInt)
/*     */   {
/* 179 */     return (paramInt >= -1347695874) && (paramInt <= -1347695872);
/*     */   }
/*     */ 
/*     */   private ObjectKeyTemplate create(InputStream paramInputStream, Handler paramHandler, OctetSeqHolder paramOctetSeqHolder)
/*     */   {
/* 188 */     ObjectKeyTemplate localObjectKeyTemplate = null;
/*     */     try
/*     */     {
/* 191 */       paramInputStream.mark(0);
/* 192 */       int i = paramInputStream.read_long();
/*     */ 
/* 194 */       if (validMagic(i)) {
/* 195 */         int j = paramInputStream.read_long();
/* 196 */         localObjectKeyTemplate = paramHandler.handle(i, j, paramInputStream, paramOctetSeqHolder);
/*     */       }
/*     */     }
/*     */     catch (MARSHAL localMARSHAL)
/*     */     {
/*     */     }
/*     */ 
/* 203 */     if (localObjectKeyTemplate == null)
/*     */     {
/*     */       try
/*     */       {
/* 208 */         paramInputStream.reset();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */     }
/* 214 */     return localObjectKeyTemplate;
/*     */   }
/*     */ 
/*     */   public ObjectKey create(byte[] paramArrayOfByte)
/*     */   {
/* 219 */     OctetSeqHolder localOctetSeqHolder = new OctetSeqHolder();
/* 220 */     EncapsInputStream localEncapsInputStream = new EncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length);
/*     */ 
/* 222 */     Object localObject = create(localEncapsInputStream, this.fullKey, localOctetSeqHolder);
/* 223 */     if (localObject == null) {
/* 224 */       localObject = new WireObjectKeyTemplate(localEncapsInputStream, localOctetSeqHolder);
/*     */     }
/* 226 */     ObjectIdImpl localObjectIdImpl = new ObjectIdImpl(localOctetSeqHolder.value);
/* 227 */     return new ObjectKeyImpl((ObjectKeyTemplate)localObject, localObjectIdImpl);
/*     */   }
/*     */ 
/*     */   public ObjectKeyTemplate createTemplate(InputStream paramInputStream)
/*     */   {
/* 232 */     Object localObject = create(paramInputStream, this.oktempOnly, null);
/* 233 */     if (localObject == null) {
/* 234 */       localObject = new WireObjectKeyTemplate(this.orb);
/*     */     }
/* 236 */     return localObject;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.ior.ObjectKeyFactoryImpl
 * JD-Core Version:    0.6.2
 */