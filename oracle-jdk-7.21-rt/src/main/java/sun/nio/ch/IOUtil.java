/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ class IOUtil
/*     */ {
/* 352 */   static final int IOV_MAX = iovMax();
/*     */ 
/*     */   static int write(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher, Object paramObject)
/*     */     throws IOException
/*     */   {
/*  50 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/*  51 */       return writeFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher, paramObject);
/*     */     }
/*     */ 
/*  54 */     int i = paramByteBuffer.position();
/*  55 */     int j = paramByteBuffer.limit();
/*  56 */     assert (i <= j);
/*  57 */     int k = i <= j ? j - i : 0;
/*  58 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(k);
/*     */     try {
/*  60 */       localByteBuffer.put(paramByteBuffer);
/*  61 */       localByteBuffer.flip();
/*     */ 
/*  63 */       paramByteBuffer.position(i);
/*     */ 
/*  65 */       int m = writeFromNativeBuffer(paramFileDescriptor, localByteBuffer, paramLong, paramNativeDispatcher, paramObject);
/*  66 */       if (m > 0)
/*     */       {
/*  68 */         paramByteBuffer.position(i + m);
/*     */       }
/*  70 */       return m;
/*     */     } finally {
/*  72 */       Util.offerFirstTemporaryDirectBuffer(localByteBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int writeFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher, Object paramObject)
/*     */     throws IOException
/*     */   {
/*  81 */     int i = paramByteBuffer.position();
/*  82 */     int j = paramByteBuffer.limit();
/*  83 */     assert (i <= j);
/*  84 */     int k = i <= j ? j - i : 0;
/*     */ 
/*  86 */     int m = 0;
/*  87 */     if (k == 0)
/*  88 */       return 0;
/*  89 */     if (paramLong != -1L) {
/*  90 */       m = paramNativeDispatcher.pwrite(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramLong, paramObject);
/*     */     }
/*     */     else
/*     */     {
/*  94 */       m = paramNativeDispatcher.write(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k);
/*     */     }
/*  96 */     if (m > 0)
/*  97 */       paramByteBuffer.position(i + m);
/*  98 */     return m;
/*     */   }
/*     */ 
/*     */   static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher)
/*     */     throws IOException
/*     */   {
/* 104 */     return write(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
/*     */   }
/*     */ 
/*     */   static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher)
/*     */     throws IOException
/*     */   {
/* 111 */     IOVecWrapper localIOVecWrapper = IOVecWrapper.get(paramInt2);
/*     */ 
/* 113 */     int i = 0;
/* 114 */     int j = 0;
/*     */     try
/*     */     {
/* 118 */       int k = paramInt1 + paramInt2;
/* 119 */       int m = paramInt1;
/*     */       int i1;
/* 120 */       while ((m < k) && (j < IOV_MAX)) {
/* 121 */         Object localObject1 = paramArrayOfByteBuffer[m];
/* 122 */         int n = ((ByteBuffer)localObject1).position();
/* 123 */         i1 = ((ByteBuffer)localObject1).limit();
/* 124 */         assert (n <= i1);
/* 125 */         int i2 = n <= i1 ? i1 - n : 0;
/* 126 */         if (i2 > 0) {
/* 127 */           localIOVecWrapper.setBuffer(j, (ByteBuffer)localObject1, n, i2);
/*     */ 
/* 130 */           if (!(localObject1 instanceof DirectBuffer)) {
/* 131 */             ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(i2);
/* 132 */             localByteBuffer2.put((ByteBuffer)localObject1);
/* 133 */             localByteBuffer2.flip();
/* 134 */             localIOVecWrapper.setShadow(j, localByteBuffer2);
/* 135 */             ((ByteBuffer)localObject1).position(n);
/* 136 */             localObject1 = localByteBuffer2;
/* 137 */             n = localByteBuffer2.position();
/*     */           }
/*     */ 
/* 140 */           localIOVecWrapper.putBase(j, ((DirectBuffer)localObject1).address() + n);
/* 141 */           localIOVecWrapper.putLen(j, i2);
/* 142 */           j++;
/*     */         }
/* 144 */         m++;
/*     */       }
/* 146 */       if (j == 0)
/*     */       {
/*     */         ByteBuffer localByteBuffer1;
/* 147 */         return 0L;
/*     */       }
/* 149 */       long l1 = paramNativeDispatcher.writev(paramFileDescriptor, localIOVecWrapper.address, j);
/*     */ 
/* 152 */       long l2 = l1;
/*     */       int i4;
/* 153 */       for (int i3 = 0; i3 < j; i3++) {
/* 154 */         if (l2 > 0L) {
/* 155 */           localByteBuffer3 = localIOVecWrapper.getBuffer(i3);
/* 156 */           i4 = localIOVecWrapper.getPosition(i3);
/* 157 */           int i5 = localIOVecWrapper.getRemaining(i3);
/* 158 */           int i6 = l2 > i5 ? i5 : (int)l2;
/* 159 */           localByteBuffer3.position(i4 + i6);
/* 160 */           l2 -= i6;
/*     */         }
/*     */ 
/* 163 */         ByteBuffer localByteBuffer3 = localIOVecWrapper.getShadow(i3);
/* 164 */         if (localByteBuffer3 != null)
/* 165 */           Util.offerLastTemporaryDirectBuffer(localByteBuffer3);
/* 166 */         localIOVecWrapper.clearRefs(i3);
/*     */       }
/*     */ 
/* 169 */       i = 1;
/*     */       ByteBuffer localByteBuffer4;
/* 170 */       return l1;
/*     */     }
/*     */     finally
/*     */     {
/* 175 */       if (i == 0)
/* 176 */         for (int i7 = 0; i7 < j; i7++) {
/* 177 */           ByteBuffer localByteBuffer5 = localIOVecWrapper.getShadow(i7);
/* 178 */           if (localByteBuffer5 != null)
/* 179 */             Util.offerLastTemporaryDirectBuffer(localByteBuffer5);
/* 180 */           localIOVecWrapper.clearRefs(i7);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   static int read(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 190 */     if (paramByteBuffer.isReadOnly())
/* 191 */       throw new IllegalArgumentException("Read-only buffer");
/* 192 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/* 193 */       return readIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher, paramObject);
/*     */     }
/*     */ 
/* 196 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
/*     */     try {
/* 198 */       int i = readIntoNativeBuffer(paramFileDescriptor, localByteBuffer, paramLong, paramNativeDispatcher, paramObject);
/* 199 */       localByteBuffer.flip();
/* 200 */       if (i > 0)
/* 201 */         paramByteBuffer.put(localByteBuffer);
/* 202 */       return i;
/*     */     } finally {
/* 204 */       Util.offerFirstTemporaryDirectBuffer(localByteBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int readIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 213 */     int i = paramByteBuffer.position();
/* 214 */     int j = paramByteBuffer.limit();
/* 215 */     assert (i <= j);
/* 216 */     int k = i <= j ? j - i : 0;
/*     */ 
/* 218 */     if (k == 0)
/* 219 */       return 0;
/* 220 */     int m = 0;
/* 221 */     if (paramLong != -1L) {
/* 222 */       m = paramNativeDispatcher.pread(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramLong, paramObject);
/*     */     }
/*     */     else {
/* 225 */       m = paramNativeDispatcher.read(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k);
/*     */     }
/* 227 */     if (m > 0)
/* 228 */       paramByteBuffer.position(i + m);
/* 229 */     return m;
/*     */   }
/*     */ 
/*     */   static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher)
/*     */     throws IOException
/*     */   {
/* 235 */     return read(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
/*     */   }
/*     */ 
/*     */   static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher)
/*     */     throws IOException
/*     */   {
/* 242 */     IOVecWrapper localIOVecWrapper = IOVecWrapper.get(paramInt2);
/*     */ 
/* 244 */     int i = 0;
/* 245 */     int j = 0;
/*     */     try
/*     */     {
/* 249 */       int k = paramInt1 + paramInt2;
/* 250 */       int m = paramInt1;
/*     */       int i1;
/* 251 */       while ((m < k) && (j < IOV_MAX)) {
/* 252 */         Object localObject1 = paramArrayOfByteBuffer[m];
/* 253 */         if (((ByteBuffer)localObject1).isReadOnly())
/* 254 */           throw new IllegalArgumentException("Read-only buffer");
/* 255 */         int n = ((ByteBuffer)localObject1).position();
/* 256 */         i1 = ((ByteBuffer)localObject1).limit();
/* 257 */         assert (n <= i1);
/* 258 */         int i2 = n <= i1 ? i1 - n : 0;
/*     */ 
/* 260 */         if (i2 > 0) {
/* 261 */           localIOVecWrapper.setBuffer(j, (ByteBuffer)localObject1, n, i2);
/*     */ 
/* 264 */           if (!(localObject1 instanceof DirectBuffer)) {
/* 265 */             ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(i2);
/* 266 */             localIOVecWrapper.setShadow(j, localByteBuffer2);
/* 267 */             localObject1 = localByteBuffer2;
/* 268 */             n = localByteBuffer2.position();
/*     */           }
/*     */ 
/* 271 */           localIOVecWrapper.putBase(j, ((DirectBuffer)localObject1).address() + n);
/* 272 */           localIOVecWrapper.putLen(j, i2);
/* 273 */           j++;
/*     */         }
/* 275 */         m++;
/*     */       }
/* 277 */       if (j == 0)
/*     */       {
/*     */         ByteBuffer localByteBuffer1;
/* 278 */         return 0L;
/*     */       }
/* 280 */       long l1 = paramNativeDispatcher.readv(paramFileDescriptor, localIOVecWrapper.address, j);
/*     */ 
/* 283 */       long l2 = l1;
/* 284 */       for (int i3 = 0; i3 < j; i3++) {
/* 285 */         ByteBuffer localByteBuffer3 = localIOVecWrapper.getShadow(i3);
/* 286 */         if (l2 > 0L) {
/* 287 */           ByteBuffer localByteBuffer4 = localIOVecWrapper.getBuffer(i3);
/* 288 */           int i5 = localIOVecWrapper.getRemaining(i3);
/* 289 */           int i6 = l2 > i5 ? i5 : (int)l2;
/* 290 */           if (localByteBuffer3 == null) {
/* 291 */             int i7 = localIOVecWrapper.getPosition(i3);
/* 292 */             localByteBuffer4.position(i7 + i6);
/*     */           } else {
/* 294 */             localByteBuffer3.limit(localByteBuffer3.position() + i6);
/* 295 */             localByteBuffer4.put(localByteBuffer3);
/*     */           }
/* 297 */           l2 -= i6;
/*     */         }
/* 299 */         if (localByteBuffer3 != null)
/* 300 */           Util.offerLastTemporaryDirectBuffer(localByteBuffer3);
/* 301 */         localIOVecWrapper.clearRefs(i3);
/*     */       }
/*     */ 
/* 304 */       i = 1;
/*     */       int i4;
/*     */       ByteBuffer localByteBuffer5;
/* 305 */       return l1;
/*     */     }
/*     */     finally
/*     */     {
/* 310 */       if (i == 0)
/* 311 */         for (int i8 = 0; i8 < j; i8++) {
/* 312 */           ByteBuffer localByteBuffer6 = localIOVecWrapper.getShadow(i8);
/* 313 */           if (localByteBuffer6 != null)
/* 314 */             Util.offerLastTemporaryDirectBuffer(localByteBuffer6);
/* 315 */           localIOVecWrapper.clearRefs(i8);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   static FileDescriptor newFD(int paramInt)
/*     */   {
/* 322 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 323 */     setfdVal(localFileDescriptor, paramInt);
/* 324 */     return localFileDescriptor;
/*     */   }
/*     */ 
/*     */   static native boolean randomBytes(byte[] paramArrayOfByte);
/*     */ 
/*     */   static native long makePipe(boolean paramBoolean);
/*     */ 
/*     */   static native boolean drain(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void configureBlocking(FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   static native int fdVal(FileDescriptor paramFileDescriptor);
/*     */ 
/*     */   static native void setfdVal(FileDescriptor paramFileDescriptor, int paramInt);
/*     */ 
/*     */   static native int iovMax();
/*     */ 
/*     */   static native void initIDs();
/*     */ 
/*     */   static
/*     */   {
/* 351 */     Util.load();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.IOUtil
 * JD-Core Version:    0.6.2
 */