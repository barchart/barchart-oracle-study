/*     */ package javax.security.auth.kerberos;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Objects;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ 
/*     */ public final class KeyTab
/*     */ {
/*     */   private final File file;
/*     */ 
/*     */   private KeyTab(File paramFile)
/*     */   {
/*  84 */     this.file = paramFile;
/*     */   }
/*     */ 
/*     */   public static KeyTab getInstance(File paramFile)
/*     */   {
/*  97 */     if (paramFile == null) {
/*  98 */       throw new NullPointerException("file must be non null");
/*     */     }
/* 100 */     return new KeyTab(paramFile);
/*     */   }
/*     */ 
/*     */   public static KeyTab getInstance()
/*     */   {
/* 112 */     return new KeyTab(null);
/*     */   }
/*     */ 
/*     */   private sun.security.krb5.internal.ktab.KeyTab takeSnapshot()
/*     */   {
/* 117 */     return sun.security.krb5.internal.ktab.KeyTab.getInstance(this.file);
/*     */   }
/*     */ 
/*     */   public KerberosKey[] getKeys(KerberosPrincipal paramKerberosPrincipal)
/*     */   {
/*     */     try
/*     */     {
/* 160 */       EncryptionKey[] arrayOfEncryptionKey = takeSnapshot().readServiceKeys(new PrincipalName(paramKerberosPrincipal.getName()));
/*     */ 
/* 162 */       KerberosKey[] arrayOfKerberosKey = new KerberosKey[arrayOfEncryptionKey.length];
/* 163 */       for (int i = 0; i < arrayOfKerberosKey.length; i++) {
/* 164 */         Integer localInteger = arrayOfEncryptionKey[i].getKeyVersionNumber();
/* 165 */         arrayOfKerberosKey[i] = new KerberosKey(paramKerberosPrincipal, arrayOfEncryptionKey[i].getBytes(), arrayOfEncryptionKey[i].getEType(), localInteger == null ? 0 : localInteger.intValue());
/*     */ 
/* 170 */         arrayOfEncryptionKey[i].destroy();
/*     */       }
/* 172 */       return arrayOfKerberosKey; } catch (RealmException localRealmException) {
/*     */     }
/* 174 */     return new KerberosKey[0];
/*     */   }
/*     */ 
/*     */   EncryptionKey[] getEncryptionKeys(PrincipalName paramPrincipalName)
/*     */   {
/* 179 */     return takeSnapshot().readServiceKeys(paramPrincipalName);
/*     */   }
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 194 */     return !takeSnapshot().isMissing();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 198 */     return this.file == null ? "Default keytab" : this.file.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 207 */     return Objects.hash(new Object[] { this.file });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 220 */     if (paramObject == this) {
/* 221 */       return true;
/*     */     }
/* 223 */     if (!(paramObject instanceof KeyTab)) {
/* 224 */       return false;
/*     */     }
/*     */ 
/* 227 */     KeyTab localKeyTab = (KeyTab)paramObject;
/* 228 */     return Objects.equals(localKeyTab.file, this.file);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  79 */     SharedSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.kerberos.KeyTab
 * JD-Core Version:    0.6.2
 */