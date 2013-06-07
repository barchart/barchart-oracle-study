/*     */ package sun.security.jgss;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ public class GSSNameImpl
/*     */   implements GSSName
/*     */ {
/* 104 */   static final Oid oldHostbasedServiceName = localOid;
/*     */ 
/* 107 */   private GSSManagerImpl gssManager = null;
/*     */ 
/* 117 */   private String appNameStr = null;
/* 118 */   private byte[] appNameBytes = null;
/* 119 */   private Oid appNameType = null;
/*     */ 
/* 126 */   private String printableName = null;
/* 127 */   private Oid printableNameType = null;
/*     */ 
/* 129 */   private HashMap<Oid, GSSNameSpi> elements = null;
/* 130 */   private GSSNameSpi mechElement = null;
/*     */ 
/*     */   static GSSNameImpl wrapElement(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi) throws GSSException
/*     */   {
/* 134 */     return paramGSSNameSpi == null ? null : new GSSNameImpl(paramGSSManagerImpl, paramGSSNameSpi);
/*     */   }
/*     */ 
/*     */   GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi)
/*     */   {
/* 139 */     this.gssManager = paramGSSManagerImpl;
/* 140 */     this.appNameStr = (this.printableName = paramGSSNameSpi.toString());
/* 141 */     this.appNameType = (this.printableNameType = paramGSSNameSpi.getStringNameType());
/* 142 */     this.mechElement = paramGSSNameSpi;
/* 143 */     this.elements = new HashMap(1);
/* 144 */     this.elements.put(paramGSSNameSpi.getMechanism(), this.mechElement);
/*     */   }
/*     */ 
/*     */   GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid)
/*     */     throws GSSException
/*     */   {
/* 151 */     this(paramGSSManagerImpl, paramObject, paramOid, null);
/*     */   }
/*     */ 
/*     */   GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException
/*     */   {
/* 160 */     if (oldHostbasedServiceName.equals(paramOid1)) {
/* 161 */       paramOid1 = GSSName.NT_HOSTBASED_SERVICE;
/*     */     }
/* 163 */     if (paramObject == null) {
/* 164 */       throw new GSSExceptionImpl(3, "Cannot import null name");
/*     */     }
/* 166 */     if (paramOid2 == null) paramOid2 = ProviderList.DEFAULT_MECH_OID;
/* 167 */     if (NT_EXPORT_NAME.equals(paramOid1))
/* 168 */       importName(paramGSSManagerImpl, paramObject);
/*     */     else
/* 170 */       init(paramGSSManagerImpl, paramObject, paramOid1, paramOid2);
/*     */   }
/*     */ 
/*     */   private void init(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException
/*     */   {
/* 179 */     this.gssManager = paramGSSManagerImpl;
/* 180 */     this.elements = new HashMap(paramGSSManagerImpl.getMechs().length);
/*     */ 
/* 183 */     if ((paramObject instanceof String)) {
/* 184 */       this.appNameStr = ((String)paramObject);
/*     */ 
/* 191 */       if (paramOid1 != null) {
/* 192 */         this.printableName = this.appNameStr;
/* 193 */         this.printableNameType = paramOid1;
/*     */       }
/*     */     } else {
/* 196 */       this.appNameBytes = ((byte[])paramObject);
/*     */     }
/*     */ 
/* 199 */     this.appNameType = paramOid1;
/*     */ 
/* 201 */     this.mechElement = getElement(paramOid2);
/*     */ 
/* 207 */     if (this.printableName == null) {
/* 208 */       this.printableName = this.mechElement.toString();
/* 209 */       this.printableNameType = this.mechElement.getStringNameType();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void importName(GSSManagerImpl paramGSSManagerImpl, Object paramObject)
/*     */     throws GSSException
/*     */   {
/* 226 */     int i = 0;
/* 227 */     byte[] arrayOfByte1 = null;
/*     */ 
/* 229 */     if ((paramObject instanceof String))
/*     */       try {
/* 231 */         arrayOfByte1 = ((String)paramObject).getBytes("UTF-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */       }
/*     */     else {
/* 236 */       arrayOfByte1 = (byte[])paramObject;
/*     */     }
/* 238 */     if ((arrayOfByte1[(i++)] != 4) || (arrayOfByte1[(i++)] != 1))
/*     */     {
/* 240 */       throw new GSSExceptionImpl(3, "Exported name token id is corrupted!");
/*     */     }
/*     */ 
/* 243 */     int j = (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
/*     */ 
/* 245 */     ObjectIdentifier localObjectIdentifier = null;
/*     */     try {
/* 247 */       DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1, i, j);
/*     */ 
/* 249 */       localObjectIdentifier = new ObjectIdentifier(localDerInputStream);
/*     */     } catch (IOException localIOException) {
/* 251 */       throw new GSSExceptionImpl(3, "Exported name Object identifier is corrupted!");
/*     */     }
/*     */ 
/* 254 */     Oid localOid = new Oid(localObjectIdentifier.toString());
/* 255 */     i += j;
/* 256 */     int k = (0xFF & arrayOfByte1[(i++)]) << 24 | (0xFF & arrayOfByte1[(i++)]) << 16 | (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
/*     */ 
/* 260 */     byte[] arrayOfByte2 = new byte[k];
/* 261 */     System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, k);
/*     */ 
/* 263 */     init(paramGSSManagerImpl, arrayOfByte2, NT_EXPORT_NAME, localOid);
/*     */   }
/*     */ 
/*     */   public GSSName canonicalize(Oid paramOid) throws GSSException {
/* 267 */     if (paramOid == null) paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */ 
/* 269 */     return wrapElement(this.gssManager, getElement(paramOid));
/*     */   }
/*     */ 
/*     */   public boolean equals(GSSName paramGSSName)
/*     */     throws GSSException
/*     */   {
/* 279 */     if ((isAnonymous()) || (paramGSSName.isAnonymous())) {
/* 280 */       return false;
/*     */     }
/* 282 */     if (paramGSSName == this) {
/* 283 */       return true;
/*     */     }
/* 285 */     if (!(paramGSSName instanceof GSSNameImpl)) {
/* 286 */       return equals(this.gssManager.createName(paramGSSName.toString(), paramGSSName.getStringNameType()));
/*     */     }
/*     */ 
/* 294 */     GSSNameImpl localGSSNameImpl = (GSSNameImpl)paramGSSName;
/*     */ 
/* 296 */     GSSNameSpi localGSSNameSpi1 = this.mechElement;
/* 297 */     GSSNameSpi localGSSNameSpi2 = localGSSNameImpl.mechElement;
/*     */ 
/* 303 */     if ((localGSSNameSpi1 == null) && (localGSSNameSpi2 != null))
/* 304 */       localGSSNameSpi1 = getElement(localGSSNameSpi2.getMechanism());
/* 305 */     else if ((localGSSNameSpi1 != null) && (localGSSNameSpi2 == null)) {
/* 306 */       localGSSNameSpi2 = localGSSNameImpl.getElement(localGSSNameSpi1.getMechanism());
/*     */     }
/*     */ 
/* 309 */     if ((localGSSNameSpi1 != null) && (localGSSNameSpi2 != null)) {
/* 310 */       return localGSSNameSpi1.equals(localGSSNameSpi2);
/*     */     }
/*     */ 
/* 313 */     if ((this.appNameType != null) && (localGSSNameImpl.appNameType != null))
/*     */     {
/* 315 */       if (!this.appNameType.equals(localGSSNameImpl.appNameType)) {
/* 316 */         return false;
/*     */       }
/* 318 */       byte[] arrayOfByte1 = null;
/* 319 */       byte[] arrayOfByte2 = null;
/*     */       try {
/* 321 */         arrayOfByte1 = this.appNameStr != null ? this.appNameStr.getBytes("UTF-8") : this.appNameBytes;
/*     */ 
/* 325 */         arrayOfByte2 = localGSSNameImpl.appNameStr != null ? localGSSNameImpl.appNameStr.getBytes("UTF-8") : localGSSNameImpl.appNameBytes;
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */       {
/*     */       }
/*     */ 
/* 333 */       return Arrays.equals(arrayOfByte1, arrayOfByte2);
/*     */     }
/*     */ 
/* 336 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 356 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*     */     try
/*     */     {
/* 365 */       if ((paramObject instanceof GSSName))
/* 366 */         return equals((GSSName)paramObject);
/*     */     }
/*     */     catch (GSSException localGSSException)
/*     */     {
/*     */     }
/* 371 */     return false;
/*     */   }
/*     */ 
/*     */   public byte[] export()
/*     */     throws GSSException
/*     */   {
/* 400 */     if (this.mechElement == null)
/*     */     {
/* 402 */       this.mechElement = getElement(ProviderList.DEFAULT_MECH_OID);
/*     */     }
/*     */ 
/* 405 */     byte[] arrayOfByte1 = this.mechElement.export();
/* 406 */     byte[] arrayOfByte2 = null;
/* 407 */     ObjectIdentifier localObjectIdentifier = null;
/*     */     try
/*     */     {
/* 410 */       localObjectIdentifier = new ObjectIdentifier(this.mechElement.getMechanism().toString());
/*     */     }
/*     */     catch (IOException localIOException1) {
/* 413 */       throw new GSSExceptionImpl(11, "Invalid OID String ");
/*     */     }
/*     */ 
/* 416 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     try {
/* 418 */       localDerOutputStream.putOID(localObjectIdentifier);
/*     */     } catch (IOException localIOException2) {
/* 420 */       throw new GSSExceptionImpl(11, "Could not ASN.1 Encode " + localObjectIdentifier.toString());
/*     */     }
/*     */ 
/* 424 */     arrayOfByte2 = localDerOutputStream.toByteArray();
/*     */ 
/* 426 */     byte[] arrayOfByte3 = new byte[4 + arrayOfByte2.length + 4 + arrayOfByte1.length];
/*     */ 
/* 429 */     int i = 0;
/* 430 */     arrayOfByte3[(i++)] = 4;
/* 431 */     arrayOfByte3[(i++)] = 1;
/* 432 */     arrayOfByte3[(i++)] = ((byte)(arrayOfByte2.length >>> 8));
/* 433 */     arrayOfByte3[(i++)] = ((byte)arrayOfByte2.length);
/* 434 */     System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
/* 435 */     i += arrayOfByte2.length;
/* 436 */     arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 24));
/* 437 */     arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 16));
/* 438 */     arrayOfByte3[(i++)] = ((byte)(arrayOfByte1.length >>> 8));
/* 439 */     arrayOfByte3[(i++)] = ((byte)arrayOfByte1.length);
/* 440 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i, arrayOfByte1.length);
/* 441 */     return arrayOfByte3;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 445 */     return this.printableName;
/*     */   }
/*     */ 
/*     */   public Oid getStringNameType() throws GSSException
/*     */   {
/* 450 */     return this.printableNameType;
/*     */   }
/*     */ 
/*     */   public boolean isAnonymous() {
/* 454 */     if (this.printableNameType == null) {
/* 455 */       return false;
/*     */     }
/* 457 */     return GSSName.NT_ANONYMOUS.equals(this.printableNameType);
/*     */   }
/*     */ 
/*     */   public boolean isMN()
/*     */   {
/* 462 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized GSSNameSpi getElement(Oid paramOid)
/*     */     throws GSSException
/*     */   {
/* 468 */     GSSNameSpi localGSSNameSpi = (GSSNameSpi)this.elements.get(paramOid);
/*     */ 
/* 470 */     if (localGSSNameSpi == null) {
/* 471 */       if (this.appNameStr != null) {
/* 472 */         localGSSNameSpi = this.gssManager.getNameElement(this.appNameStr, this.appNameType, paramOid);
/*     */       }
/*     */       else {
/* 475 */         localGSSNameSpi = this.gssManager.getNameElement(this.appNameBytes, this.appNameType, paramOid);
/*     */       }
/*     */ 
/* 478 */       this.elements.put(paramOid, localGSSNameSpi);
/*     */     }
/* 480 */     return localGSSNameSpi;
/*     */   }
/*     */ 
/*     */   Set<GSSNameSpi> getElements() {
/* 484 */     return new HashSet(this.elements.values());
/*     */   }
/*     */ 
/*     */   private static String getNameTypeStr(Oid paramOid)
/*     */   {
/* 489 */     if (paramOid == null) {
/* 490 */       return "(NT is null)";
/*     */     }
/* 492 */     if (paramOid.equals(NT_USER_NAME))
/* 493 */       return "NT_USER_NAME";
/* 494 */     if (paramOid.equals(NT_HOSTBASED_SERVICE))
/* 495 */       return "NT_HOSTBASED_SERVICE";
/* 496 */     if (paramOid.equals(NT_EXPORT_NAME))
/* 497 */       return "NT_EXPORT_NAME";
/* 498 */     if (paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL)) {
/* 499 */       return "NT_GSS_KRB5_PRINCIPAL";
/*     */     }
/* 501 */     return "Unknown";
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  98 */     Oid localOid = null;
/*     */     try {
/* 100 */       localOid = new Oid("1.3.6.1.5.6.2");
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.jgss.GSSNameImpl
 * JD-Core Version:    0.6.2
 */