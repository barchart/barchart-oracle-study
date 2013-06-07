/*     */ package java.rmi.dgc;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
/*     */ import java.rmi.server.UID;
/*     */ import java.security.AccessController;
/*     */ import java.security.DigestOutputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public final class VMID
/*     */   implements Serializable
/*     */ {
/*  44 */   private static byte[] localAddr = computeAddressHash();
/*     */   private byte[] addr;
/*     */   private UID uid;
/*     */   private static final long serialVersionUID = -538642295484486218L;
/*     */ 
/*     */   public VMID()
/*     */   {
/*  68 */     this.addr = localAddr;
/*  69 */     this.uid = new UID();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static boolean isUnique()
/*     */   {
/*  80 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  87 */     return this.uid.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*  95 */     if ((paramObject instanceof VMID)) {
/*  96 */       VMID localVMID = (VMID)paramObject;
/*  97 */       if (!this.uid.equals(localVMID.uid))
/*  98 */         return false;
/*  99 */       if (((this.addr == null ? 1 : 0) ^ (localVMID.addr == null ? 1 : 0)) != 0)
/* 100 */         return false;
/* 101 */       if (this.addr != null) {
/* 102 */         if (this.addr.length != localVMID.addr.length)
/* 103 */           return false;
/* 104 */         for (int i = 0; i < this.addr.length; i++)
/* 105 */           if (this.addr[i] != localVMID.addr[i])
/* 106 */             return false;
/*     */       }
/* 108 */       return true;
/*     */     }
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     StringBuffer localStringBuffer = new StringBuffer();
/* 119 */     if (this.addr != null) {
/* 120 */       for (int i = 0; i < this.addr.length; i++) {
/* 121 */         int j = this.addr[i] & 0xFF;
/* 122 */         localStringBuffer.append((j < 16 ? "0" : "") + Integer.toString(j, 16));
/*     */       }
/*     */     }
/* 125 */     localStringBuffer.append(':');
/* 126 */     localStringBuffer.append(this.uid.toString());
/* 127 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static byte[] computeAddressHash()
/*     */   {
/* 139 */     byte[] arrayOfByte1 = (byte[])AccessController.doPrivileged(new PrivilegedAction() { public byte[] run() { // Byte code:
/*     */         //   0: invokestatic 37	java/net/InetAddress:getLocalHost	()Ljava/net/InetAddress;
/*     */         //   3: invokevirtual 36	java/net/InetAddress:getAddress	()[B
/*     */         //   6: areturn
/*     */         //   7: astore_1
/*     */         //   8: iconst_4
/*     */         //   9: newarray byte
/*     */         //   11: dup
/*     */         //   12: iconst_0
/*     */         //   13: iconst_0
/*     */         //   14: bastore
/*     */         //   15: dup
/*     */         //   16: iconst_1
/*     */         //   17: iconst_0
/*     */         //   18: bastore
/*     */         //   19: dup
/*     */         //   20: iconst_2
/*     */         //   21: iconst_0
/*     */         //   22: bastore
/*     */         //   23: dup
/*     */         //   24: iconst_3
/*     */         //   25: iconst_0
/*     */         //   26: bastore
/*     */         //   27: areturn
/*     */         //
/*     */         // Exception table:
/*     */         //   from	to	target	type
/*     */         //   0	6	7	java/lang/Exception }  } );
/*     */     byte[] arrayOfByte2;
/*     */     try { MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 158 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(64);
/* 159 */       DataOutputStream localDataOutputStream = new DataOutputStream(new DigestOutputStream(localByteArrayOutputStream, localMessageDigest));
/*     */ 
/* 161 */       localDataOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
/* 162 */       localDataOutputStream.flush();
/*     */ 
/* 164 */       byte[] arrayOfByte3 = localMessageDigest.digest();
/* 165 */       int i = Math.min(8, arrayOfByte3.length);
/* 166 */       arrayOfByte2 = new byte[i];
/* 167 */       System.arraycopy(arrayOfByte3, 0, arrayOfByte2, 0, i);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 171 */       arrayOfByte2 = new byte[0];
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 173 */       throw new InternalError(localNoSuchAlgorithmException.toString());
/*     */     }
/* 175 */     return arrayOfByte2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.dgc.VMID
 * JD-Core Version:    0.6.2
 */