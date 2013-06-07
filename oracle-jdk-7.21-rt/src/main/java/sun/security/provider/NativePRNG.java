/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProviderException;
/*     */ import java.security.SecureRandomSpi;
/*     */ 
/*     */ public final class NativePRNG extends SecureRandomSpi
/*     */ {
/*     */   private static final long serialVersionUID = -6599091113397072932L;
/*     */   private static final String NAME_RANDOM = "/dev/random";
/*     */   private static final String NAME_URANDOM = "/dev/urandom";
/*  71 */   private static final RandomIO INSTANCE = initIO();
/*     */ 
/*     */   private static RandomIO initIO() {
/*  74 */     return (RandomIO)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public NativePRNG.RandomIO run() {
/*  77 */         File localFile1 = new File("/dev/random");
/*  78 */         if (!localFile1.exists()) {
/*  79 */           return null;
/*     */         }
/*  81 */         File localFile2 = new File("/dev/urandom");
/*  82 */         if (!localFile2.exists())
/*  83 */           return null;
/*     */         try
/*     */         {
/*  86 */           return new NativePRNG.RandomIO(localFile1, localFile2, null); } catch (Exception localException) {
/*     */         }
/*  88 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static boolean isAvailable()
/*     */   {
/*  96 */     return INSTANCE != null;
/*     */   }
/*     */ 
/*     */   public NativePRNG()
/*     */   {
/* 102 */     if (INSTANCE == null)
/* 103 */       throw new AssertionError("NativePRNG not available");
/*     */   }
/*     */ 
/*     */   protected void engineSetSeed(byte[] paramArrayOfByte)
/*     */   {
/* 109 */     INSTANCE.implSetSeed(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   protected void engineNextBytes(byte[] paramArrayOfByte)
/*     */   {
/* 114 */     INSTANCE.implNextBytes(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   protected byte[] engineGenerateSeed(int paramInt)
/*     */   {
/* 119 */     return INSTANCE.implGenerateSeed(paramInt);
/*     */   }
/*     */ 
/*     */   private static class RandomIO
/*     */   {
/*     */     private static final long MAX_BUFFER_TIME = 100L;
/*     */     private static final int BUFFER_SIZE = 32;
/*     */     private final InputStream randomIn;
/*     */     private final InputStream urandomIn;
/*     */     private OutputStream randomOut;
/*     */     private boolean randomOutInitialized;
/*     */     private volatile SecureRandom mixRandom;
/*     */     private final byte[] urandomBuffer;
/*     */     private int buffered;
/*     */     private long lastRead;
/* 156 */     private final Object LOCK_GET_BYTES = new Object();
/*     */ 
/* 159 */     private final Object LOCK_GET_SEED = new Object();
/*     */ 
/* 162 */     private final Object LOCK_SET_SEED = new Object();
/*     */ 
/*     */     private RandomIO(File paramFile1, File paramFile2) throws IOException
/*     */     {
/* 166 */       this.randomIn = new FileInputStream(paramFile1);
/* 167 */       this.urandomIn = new FileInputStream(paramFile2);
/* 168 */       this.urandomBuffer = new byte[32];
/*     */     }
/*     */ 
/*     */     private SecureRandom getMixRandom()
/*     */     {
/* 174 */       SecureRandom localSecureRandom = this.mixRandom;
/* 175 */       if (localSecureRandom == null) {
/* 176 */         synchronized (this.LOCK_GET_BYTES) {
/* 177 */           localSecureRandom = this.mixRandom;
/* 178 */           if (localSecureRandom == null) {
/* 179 */             localSecureRandom = new SecureRandom();
/*     */             try {
/* 181 */               byte[] arrayOfByte = new byte[20];
/* 182 */               readFully(this.urandomIn, arrayOfByte);
/* 183 */               localSecureRandom.engineSetSeed(arrayOfByte);
/*     */             } catch (IOException localIOException) {
/* 185 */               throw new ProviderException("init failed", localIOException);
/*     */             }
/* 187 */             this.mixRandom = localSecureRandom;
/*     */           }
/*     */         }
/*     */       }
/* 191 */       return localSecureRandom;
/*     */     }
/*     */ 
/*     */     private static void readFully(InputStream paramInputStream, byte[] paramArrayOfByte)
/*     */       throws IOException
/*     */     {
/* 199 */       int i = paramArrayOfByte.length;
/* 200 */       int j = 0;
/* 201 */       while (i > 0) {
/* 202 */         int k = paramInputStream.read(paramArrayOfByte, j, i);
/* 203 */         if (k <= 0) {
/* 204 */           throw new EOFException("/dev/[u]random closed?");
/*     */         }
/* 206 */         j += k;
/* 207 */         i -= k;
/*     */       }
/* 209 */       if (i > 0)
/* 210 */         throw new IOException("Could not read from /dev/[u]random");
/*     */     }
/*     */ 
/*     */     private byte[] implGenerateSeed(int paramInt)
/*     */     {
/* 216 */       synchronized (this.LOCK_GET_SEED) {
/*     */         try {
/* 218 */           byte[] arrayOfByte = new byte[paramInt];
/* 219 */           readFully(this.randomIn, arrayOfByte);
/* 220 */           return arrayOfByte;
/*     */         } catch (IOException localIOException) {
/* 222 */           throw new ProviderException("generateSeed() failed", localIOException);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void implSetSeed(byte[] paramArrayOfByte)
/*     */     {
/* 231 */       synchronized (this.LOCK_SET_SEED) {
/* 232 */         if (!this.randomOutInitialized) {
/* 233 */           this.randomOutInitialized = true;
/* 234 */           this.randomOut = ((OutputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public OutputStream run() {
/*     */               try {
/* 238 */                 return new FileOutputStream("/dev/random", true); } catch (Exception localException) {
/*     */               }
/* 240 */               return null;
/*     */             }
/*     */           }));
/*     */         }
/*     */ 
/* 245 */         if (this.randomOut != null) {
/*     */           try {
/* 247 */             this.randomOut.write(paramArrayOfByte);
/*     */           } catch (IOException localIOException) {
/* 249 */             throw new ProviderException("setSeed() failed", localIOException);
/*     */           }
/*     */         }
/* 252 */         getMixRandom().engineSetSeed(paramArrayOfByte);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void ensureBufferValid()
/*     */       throws IOException
/*     */     {
/* 259 */       long l = System.currentTimeMillis();
/* 260 */       if ((this.buffered > 0) && (l - this.lastRead < 100L)) {
/* 261 */         return;
/*     */       }
/* 263 */       this.lastRead = l;
/* 264 */       readFully(this.urandomIn, this.urandomBuffer);
/* 265 */       this.buffered = this.urandomBuffer.length;
/*     */     }
/*     */ 
/*     */     private void implNextBytes(byte[] paramArrayOfByte)
/*     */     {
/* 272 */       synchronized (this.LOCK_GET_BYTES) {
/*     */         try {
/* 274 */           getMixRandom().engineNextBytes(paramArrayOfByte);
/* 275 */           int i = paramArrayOfByte.length;
/* 276 */           int j = 0;
/*     */ 
/* 283 */           for (; i > 0; 
/* 283 */             goto 41)
/*     */           {
/* 278 */             ensureBufferValid();
/* 279 */             int k = this.urandomBuffer.length - this.buffered;
/* 280 */             if ((i > 0) && (this.buffered > 0))
/*     */             {
/*     */               int tmp58_55 = (j++);
/*     */               byte[] tmp58_52 = paramArrayOfByte; tmp58_52[tmp58_55] = ((byte)(tmp58_52[tmp58_55] ^ this.urandomBuffer[(k++)]));
/* 282 */               i--;
/* 283 */               this.buffered -= 1;
/*     */             }
/*     */           }
/*     */         } catch (IOException localIOException) {
/* 287 */           throw new ProviderException("nextBytes() failed", localIOException);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.NativePRNG
 * JD-Core Version:    0.6.2
 */