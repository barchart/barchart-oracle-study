/*     */ package sun.security.timestamp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ public class TimestampToken
/*     */ {
/*     */   private int version;
/*     */   private ObjectIdentifier policy;
/*     */   private BigInteger serialNumber;
/*     */   private AlgorithmId hashAlgorithm;
/*     */   private byte[] hashedMessage;
/*     */   private Date genTime;
/*     */   private BigInteger nonce;
/*     */ 
/*     */   public TimestampToken(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  90 */     if (paramArrayOfByte == null) {
/*  91 */       throw new IOException("No timestamp token info");
/*     */     }
/*  93 */     parse(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public Date getDate()
/*     */   {
/* 102 */     return this.genTime;
/*     */   }
/*     */ 
/*     */   public AlgorithmId getHashAlgorithm() {
/* 106 */     return this.hashAlgorithm;
/*     */   }
/*     */ 
/*     */   public byte[] getHashedMessage()
/*     */   {
/* 111 */     return this.hashedMessage;
/*     */   }
/*     */ 
/*     */   public BigInteger getNonce() {
/* 115 */     return this.nonce;
/*     */   }
/*     */ 
/*     */   private void parse(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 128 */     DerValue localDerValue1 = new DerValue(paramArrayOfByte);
/* 129 */     if (localDerValue1.tag != 48) {
/* 130 */       throw new IOException("Bad encoding for timestamp token info");
/*     */     }
/*     */ 
/* 133 */     this.version = localDerValue1.data.getInteger();
/*     */ 
/* 136 */     this.policy = localDerValue1.data.getOID();
/*     */ 
/* 139 */     DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 140 */     this.hashAlgorithm = AlgorithmId.parse(localDerValue2.data.getDerValue());
/* 141 */     this.hashedMessage = localDerValue2.data.getOctetString();
/*     */ 
/* 144 */     this.serialNumber = localDerValue1.data.getBigInteger();
/*     */ 
/* 147 */     this.genTime = localDerValue1.data.getGeneralizedTime();
/*     */ 
/* 150 */     while (localDerValue1.data.available() > 0) {
/* 151 */       DerValue localDerValue3 = localDerValue1.data.getDerValue();
/* 152 */       if (localDerValue3.tag == 2) {
/* 153 */         this.nonce = localDerValue3.getBigInteger();
/* 154 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.timestamp.TimestampToken
 * JD-Core Version:    0.6.2
 */