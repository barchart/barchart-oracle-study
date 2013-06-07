/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandomSpi;
/*     */ 
/*     */ public final class SecureRandom extends SecureRandomSpi
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3581829991155417889L;
/*     */   private static final int DIGEST_SIZE = 20;
/*     */   private transient MessageDigest digest;
/*     */   private byte[] state;
/*     */   private byte[] remainder;
/*     */   private int remCount;
/*     */ 
/*     */   public SecureRandom()
/*     */   {
/*  78 */     init(null);
/*     */   }
/*     */ 
/*     */   private SecureRandom(byte[] paramArrayOfByte)
/*     */   {
/*  88 */     init(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   private void init(byte[] paramArrayOfByte)
/*     */   {
/*     */     try
/*     */     {
/*  97 */       this.digest = MessageDigest.getInstance("SHA");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  99 */       throw new InternalError("internal error: SHA-1 not available.");
/*     */     }
/*     */ 
/* 102 */     if (paramArrayOfByte != null)
/* 103 */       engineSetSeed(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public byte[] engineGenerateSeed(int paramInt)
/*     */   {
/* 124 */     byte[] arrayOfByte = new byte[paramInt];
/* 125 */     SeedGenerator.generateSeed(arrayOfByte);
/* 126 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public synchronized void engineSetSeed(byte[] paramArrayOfByte)
/*     */   {
/* 137 */     if (this.state != null) {
/* 138 */       this.digest.update(this.state);
/* 139 */       for (int i = 0; i < this.state.length; i++)
/* 140 */         this.state[i] = 0;
/*     */     }
/* 142 */     this.state = this.digest.digest(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   private static void updateState(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
/* 146 */     int i = 1;
/* 147 */     int j = 0;
/* 148 */     int k = 0;
/* 149 */     int m = 0;
/*     */ 
/* 152 */     for (int n = 0; n < paramArrayOfByte1.length; n++)
/*     */     {
/* 154 */       j = paramArrayOfByte1[n] + paramArrayOfByte2[n] + i;
/*     */ 
/* 156 */       k = (byte)j;
/*     */ 
/* 158 */       m |= (paramArrayOfByte1[n] != k ? 1 : 0);
/* 159 */       paramArrayOfByte1[n] = k;
/*     */ 
/* 161 */       i = j >> 8;
/*     */     }
/*     */ 
/* 165 */     if (m == 0)
/*     */     {
/*     */       int tmp79_78 = 0; paramArrayOfByte1[tmp79_78] = ((byte)(paramArrayOfByte1[tmp79_78] + 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void engineNextBytes(byte[] paramArrayOfByte)
/*     */   {
/* 197 */     int i = 0;
/*     */ 
/* 199 */     byte[] arrayOfByte1 = this.remainder;
/*     */ 
/* 201 */     if (this.state == null) {
/* 202 */       byte[] arrayOfByte2 = new byte[20];
/* 203 */       SeederHolder.seeder.engineNextBytes(arrayOfByte2);
/* 204 */       this.state = this.digest.digest(arrayOfByte2);
/*     */     }
/*     */ 
/* 208 */     int k = this.remCount;
/*     */     int j;
/*     */     int m;
/* 209 */     if (k > 0)
/*     */     {
/* 211 */       j = paramArrayOfByte.length - i < 20 - k ? paramArrayOfByte.length - i : 20 - k;
/*     */ 
/* 214 */       for (m = 0; m < j; m++) {
/* 215 */         paramArrayOfByte[m] = arrayOfByte1[k];
/* 216 */         arrayOfByte1[(k++)] = 0;
/*     */       }
/* 218 */       this.remCount += j;
/* 219 */       i += j;
/*     */     }
/*     */ 
/* 223 */     while (i < paramArrayOfByte.length)
/*     */     {
/* 225 */       this.digest.update(this.state);
/* 226 */       arrayOfByte1 = this.digest.digest();
/* 227 */       updateState(this.state, arrayOfByte1);
/*     */ 
/* 230 */       j = paramArrayOfByte.length - i > 20 ? 20 : paramArrayOfByte.length - i;
/*     */ 
/* 233 */       for (m = 0; m < j; m++) {
/* 234 */         paramArrayOfByte[(i++)] = arrayOfByte1[m];
/* 235 */         arrayOfByte1[m] = 0;
/*     */       }
/* 237 */       this.remCount += j;
/*     */     }
/*     */ 
/* 241 */     this.remainder = arrayOfByte1;
/* 242 */     this.remCount %= 20;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 258 */     paramObjectInputStream.defaultReadObject();
/*     */     try
/*     */     {
/* 261 */       this.digest = MessageDigest.getInstance("SHA");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 263 */       throw new InternalError("internal error: SHA-1 not available.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SeederHolder
/*     */   {
/* 184 */     private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy(), null);
/*     */ 
/* 185 */     static { byte[] arrayOfByte = new byte[20];
/* 186 */       SeedGenerator.generateSeed(arrayOfByte);
/* 187 */       seeder.engineSetSeed(arrayOfByte);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.SecureRandom
 * JD-Core Version:    0.6.2
 */