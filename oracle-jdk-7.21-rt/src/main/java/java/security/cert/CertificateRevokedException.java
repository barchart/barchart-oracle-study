/*     */ package java.security.cert;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.InvalidityDateExtension;
/*     */ 
/*     */ public class CertificateRevokedException extends CertificateException
/*     */ {
/*     */   private static final long serialVersionUID = 7839996631571608627L;
/*     */   private Date revocationDate;
/*     */   private final CRLReason reason;
/*     */   private final X500Principal authority;
/*     */   private transient Map<String, Extension> extensions;
/*     */ 
/*     */   public CertificateRevokedException(Date paramDate, CRLReason paramCRLReason, X500Principal paramX500Principal, Map<String, Extension> paramMap)
/*     */   {
/*  91 */     if ((paramDate == null) || (paramCRLReason == null) || (paramX500Principal == null) || (paramMap == null))
/*     */     {
/*  93 */       throw new NullPointerException();
/*     */     }
/*  95 */     this.revocationDate = new Date(paramDate.getTime());
/*  96 */     this.reason = paramCRLReason;
/*  97 */     this.authority = paramX500Principal;
/*  98 */     this.extensions = new HashMap(paramMap);
/*     */   }
/*     */ 
/*     */   public Date getRevocationDate()
/*     */   {
/* 109 */     return (Date)this.revocationDate.clone();
/*     */   }
/*     */ 
/*     */   public CRLReason getRevocationReason()
/*     */   {
/* 118 */     return this.reason;
/*     */   }
/*     */ 
/*     */   public X500Principal getAuthorityName()
/*     */   {
/* 129 */     return this.authority;
/*     */   }
/*     */ 
/*     */   public Date getInvalidityDate()
/*     */   {
/* 146 */     Extension localExtension = (Extension)getExtensions().get("2.5.29.24");
/* 147 */     if (localExtension == null)
/* 148 */       return null;
/*     */     try
/*     */     {
/* 151 */       Date localDate = (Date)InvalidityDateExtension.toImpl(localExtension).get("DATE");
/*     */ 
/* 153 */       return new Date(localDate.getTime()); } catch (IOException localIOException) {
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<String, Extension> getExtensions()
/*     */   {
/* 170 */     return Collections.unmodifiableMap(this.extensions);
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 175 */     return "Certificate has been revoked, reason: " + this.reason + ", revocation date: " + this.revocationDate + ", authority: " + this.authority + ", extensions: " + this.extensions;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 192 */     paramObjectOutputStream.defaultWriteObject();
/*     */ 
/* 195 */     paramObjectOutputStream.writeInt(this.extensions.size());
/*     */ 
/* 202 */     for (Map.Entry localEntry : this.extensions.entrySet()) {
/* 203 */       Extension localExtension = (Extension)localEntry.getValue();
/* 204 */       paramObjectOutputStream.writeObject(localExtension.getId());
/* 205 */       paramObjectOutputStream.writeBoolean(localExtension.isCritical());
/* 206 */       byte[] arrayOfByte = localExtension.getValue();
/* 207 */       paramObjectOutputStream.writeInt(arrayOfByte.length);
/* 208 */       paramObjectOutputStream.write(arrayOfByte);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 219 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 222 */     this.revocationDate = new Date(this.revocationDate.getTime());
/*     */ 
/* 226 */     int i = paramObjectInputStream.readInt();
/* 227 */     if (i == 0)
/* 228 */       this.extensions = Collections.emptyMap();
/*     */     else {
/* 230 */       this.extensions = new HashMap(i);
/*     */     }
/*     */ 
/* 234 */     for (int j = 0; j < i; j++) {
/* 235 */       String str = (String)paramObjectInputStream.readObject();
/* 236 */       boolean bool = paramObjectInputStream.readBoolean();
/* 237 */       int k = paramObjectInputStream.readInt();
/* 238 */       byte[] arrayOfByte = new byte[k];
/* 239 */       paramObjectInputStream.readFully(arrayOfByte);
/* 240 */       sun.security.x509.Extension localExtension = sun.security.x509.Extension.newExtension(new ObjectIdentifier(str), bool, arrayOfByte);
/*     */ 
/* 242 */       this.extensions.put(str, localExtension);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CertificateRevokedException
 * JD-Core Version:    0.6.2
 */