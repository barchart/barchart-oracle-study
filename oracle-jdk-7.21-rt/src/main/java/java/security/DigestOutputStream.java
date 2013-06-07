/*     */ package java.security;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class DigestOutputStream extends FilterOutputStream
/*     */ {
/*  57 */   private boolean on = true;
/*     */   protected MessageDigest digest;
/*     */ 
/*     */   public DigestOutputStream(OutputStream paramOutputStream, MessageDigest paramMessageDigest)
/*     */   {
/*  73 */     super(paramOutputStream);
/*  74 */     setMessageDigest(paramMessageDigest);
/*     */   }
/*     */ 
/*     */   public MessageDigest getMessageDigest()
/*     */   {
/*  84 */     return this.digest;
/*     */   }
/*     */ 
/*     */   public void setMessageDigest(MessageDigest paramMessageDigest)
/*     */   {
/*  94 */     this.digest = paramMessageDigest;
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/* 115 */     if (this.on) {
/* 116 */       this.digest.update((byte)paramInt);
/*     */     }
/* 118 */     this.out.write(paramInt);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 145 */     if (this.on) {
/* 146 */       this.digest.update(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/* 148 */     this.out.write(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void on(boolean paramBoolean)
/*     */   {
/* 161 */     this.on = paramBoolean;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 169 */     return "[Digest Output Stream] " + this.digest.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.DigestOutputStream
 * JD-Core Version:    0.6.2
 */