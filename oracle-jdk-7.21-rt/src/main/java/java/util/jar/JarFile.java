/*     */ package java.util.jar;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.CodeSource;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import sun.misc.IOUtils;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ManifestEntryVerifier;
/*     */ 
/*     */ public class JarFile extends ZipFile
/*     */ {
/*     */   private SoftReference<Manifest> manRef;
/*     */   private JarEntry manEntry;
/*     */   private JarVerifier jv;
/*     */   private boolean jvInitialized;
/*     */   private boolean verify;
/*     */   private boolean computedHasClassPathAttribute;
/*     */   private boolean hasClassPathAttribute;
/*     */   public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
/*     */   private static int[] lastOcc;
/*     */   private static int[] optoSft;
/*     */   private static char[] src;
/*     */   private static String javaHome;
/*     */   private static String[] jarNames;
/*     */ 
/*     */   public JarFile(String paramString)
/*     */     throws IOException
/*     */   {
/*  90 */     this(new File(paramString), true, 1);
/*     */   }
/*     */ 
/*     */   public JarFile(String paramString, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 104 */     this(new File(paramString), paramBoolean, 1);
/*     */   }
/*     */ 
/*     */   public JarFile(File paramFile)
/*     */     throws IOException
/*     */   {
/* 117 */     this(paramFile, true, 1);
/*     */   }
/*     */ 
/*     */   public JarFile(File paramFile, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 132 */     this(paramFile, paramBoolean, 1);
/*     */   }
/*     */ 
/*     */   public JarFile(File paramFile, boolean paramBoolean, int paramInt)
/*     */     throws IOException
/*     */   {
/* 153 */     super(paramFile, paramInt);
/* 154 */     this.verify = paramBoolean;
/*     */   }
/*     */ 
/*     */   public Manifest getManifest()
/*     */     throws IOException
/*     */   {
/* 166 */     return getManifestFromReference();
/*     */   }
/*     */ 
/*     */   private Manifest getManifestFromReference() throws IOException {
/* 170 */     Manifest localManifest = this.manRef != null ? (Manifest)this.manRef.get() : null;
/*     */ 
/* 172 */     if (localManifest == null)
/*     */     {
/* 174 */       JarEntry localJarEntry = getManEntry();
/*     */ 
/* 177 */       if (localJarEntry != null) {
/* 178 */         if (this.verify) {
/* 179 */           byte[] arrayOfByte = getBytes(localJarEntry);
/* 180 */           localManifest = new Manifest(new ByteArrayInputStream(arrayOfByte));
/* 181 */           if (!this.jvInitialized)
/* 182 */             this.jv = new JarVerifier(arrayOfByte);
/*     */         }
/*     */         else {
/* 185 */           localManifest = new Manifest(super.getInputStream(localJarEntry));
/*     */         }
/* 187 */         this.manRef = new SoftReference(localManifest);
/*     */       }
/*     */     }
/* 190 */     return localManifest;
/*     */   }
/*     */ 
/*     */   private native String[] getMetaInfEntryNames();
/*     */ 
/*     */   public JarEntry getJarEntry(String paramString)
/*     */   {
/* 209 */     return (JarEntry)getEntry(paramString);
/*     */   }
/*     */ 
/*     */   public ZipEntry getEntry(String paramString)
/*     */   {
/* 226 */     ZipEntry localZipEntry = super.getEntry(paramString);
/* 227 */     if (localZipEntry != null) {
/* 228 */       return new JarFileEntry(localZipEntry);
/*     */     }
/* 230 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<JarEntry> entries()
/*     */   {
/* 237 */     final Enumeration localEnumeration = super.entries();
/* 238 */     return new Enumeration() {
/*     */       public boolean hasMoreElements() {
/* 240 */         return localEnumeration.hasMoreElements();
/*     */       }
/*     */       public JarFile.JarFileEntry nextElement() {
/* 243 */         ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 244 */         return new JarFile.JarFileEntry(JarFile.this, localZipEntry);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private void maybeInstantiateVerifier()
/*     */     throws IOException
/*     */   {
/* 292 */     if (this.jv != null) {
/* 293 */       return;
/*     */     }
/*     */ 
/* 296 */     if (this.verify) {
/* 297 */       String[] arrayOfString = getMetaInfEntryNames();
/* 298 */       if (arrayOfString != null) {
/* 299 */         for (int i = 0; i < arrayOfString.length; i++) {
/* 300 */           String str = arrayOfString[i].toUpperCase(Locale.ENGLISH);
/* 301 */           if ((str.endsWith(".DSA")) || (str.endsWith(".RSA")) || (str.endsWith(".EC")) || (str.endsWith(".SF")))
/*     */           {
/* 308 */             getManifest();
/* 309 */             return;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 315 */       this.verify = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializeVerifier()
/*     */   {
/* 325 */     ManifestEntryVerifier localManifestEntryVerifier = null;
/*     */     try
/*     */     {
/* 329 */       String[] arrayOfString = getMetaInfEntryNames();
/* 330 */       if (arrayOfString != null) {
/* 331 */         for (int i = 0; i < arrayOfString.length; i++) {
/* 332 */           JarEntry localJarEntry = getJarEntry(arrayOfString[i]);
/* 333 */           if (localJarEntry == null) {
/* 334 */             throw new JarException("corrupted jar file");
/*     */           }
/* 336 */           if (!localJarEntry.isDirectory()) {
/* 337 */             if (localManifestEntryVerifier == null) {
/* 338 */               localManifestEntryVerifier = new ManifestEntryVerifier(getManifestFromReference());
/*     */             }
/*     */ 
/* 341 */             byte[] arrayOfByte = getBytes(localJarEntry);
/* 342 */             if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
/* 343 */               this.jv.beginEntry(localJarEntry, localManifestEntryVerifier);
/* 344 */               this.jv.update(arrayOfByte.length, arrayOfByte, 0, arrayOfByte.length, localManifestEntryVerifier);
/* 345 */               this.jv.update(-1, null, 0, 0, localManifestEntryVerifier);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 353 */       this.jv = null;
/* 354 */       this.verify = false;
/* 355 */       if (JarVerifier.debug != null) {
/* 356 */         JarVerifier.debug.println("jarfile parsing error!");
/* 357 */         localIOException.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 364 */     if (this.jv != null)
/*     */     {
/* 366 */       this.jv.doneWithMeta();
/* 367 */       if (JarVerifier.debug != null) {
/* 368 */         JarVerifier.debug.println("done with meta!");
/*     */       }
/*     */ 
/* 371 */       if (this.jv.nothingToVerify()) {
/* 372 */         if (JarVerifier.debug != null) {
/* 373 */           JarVerifier.debug.println("nothing to verify!");
/*     */         }
/* 375 */         this.jv = null;
/* 376 */         this.verify = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] getBytes(ZipEntry paramZipEntry)
/*     */     throws IOException
/*     */   {
/* 386 */     InputStream localInputStream = super.getInputStream(paramZipEntry); Object localObject1 = null;
/*     */     try { return IOUtils.readFully(localInputStream, (int)paramZipEntry.getSize(), true); }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 386 */       localObject1 = localThrowable1; throw localThrowable1;
/*     */     } finally {
/* 388 */       if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localInputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getInputStream(ZipEntry paramZipEntry)
/*     */     throws IOException
/*     */   {
/* 407 */     maybeInstantiateVerifier();
/* 408 */     if (this.jv == null) {
/* 409 */       return super.getInputStream(paramZipEntry);
/*     */     }
/* 411 */     if (!this.jvInitialized) {
/* 412 */       initializeVerifier();
/* 413 */       this.jvInitialized = true;
/*     */ 
/* 417 */       if (this.jv == null) {
/* 418 */         return super.getInputStream(paramZipEntry);
/*     */       }
/*     */     }
/*     */ 
/* 422 */     return new JarVerifier.VerifierStream(getManifestFromReference(), (paramZipEntry instanceof JarFileEntry) ? (JarEntry)paramZipEntry : getJarEntry(paramZipEntry.getName()), super.getInputStream(paramZipEntry), this.jv);
/*     */   }
/*     */ 
/*     */   private JarEntry getManEntry()
/*     */   {
/* 454 */     if (this.manEntry == null)
/*     */     {
/* 456 */       this.manEntry = getJarEntry("META-INF/MANIFEST.MF");
/* 457 */       if (this.manEntry == null)
/*     */       {
/* 460 */         String[] arrayOfString = getMetaInfEntryNames();
/* 461 */         if (arrayOfString != null) {
/* 462 */           for (int i = 0; i < arrayOfString.length; i++) {
/* 463 */             if ("META-INF/MANIFEST.MF".equals(arrayOfString[i].toUpperCase(Locale.ENGLISH)))
/*     */             {
/* 465 */               this.manEntry = getJarEntry(arrayOfString[i]);
/* 466 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 472 */     return this.manEntry;
/*     */   }
/*     */ 
/*     */   boolean hasClassPathAttribute()
/*     */     throws IOException
/*     */   {
/* 480 */     if (this.computedHasClassPathAttribute) {
/* 481 */       return this.hasClassPathAttribute;
/*     */     }
/*     */ 
/* 484 */     this.hasClassPathAttribute = false;
/* 485 */     if (!isKnownToNotHaveClassPathAttribute()) {
/* 486 */       JarEntry localJarEntry = getManEntry();
/* 487 */       if (localJarEntry != null) {
/* 488 */         byte[] arrayOfByte = getBytes(localJarEntry);
/* 489 */         int i = arrayOfByte.length - src.length;
/* 490 */         int j = 0;
/*     */ 
/* 492 */         if (j <= i) {
/* 493 */           for (int k = 9; ; k--) { if (k < 0) break label150;
/* 494 */             int m = (char)arrayOfByte[(j + k)];
/* 495 */             m = (m - 65 | 90 - m) >= 0 ? (char)(m + 32) : m;
/* 496 */             if (m != src[k]) {
/* 497 */               j += Math.max(k + 1 - lastOcc[(m & 0x7F)], optoSft[k]);
/* 498 */               break;
/*     */             }
/*     */           }
/* 501 */           this.hasClassPathAttribute = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 506 */     label150: this.computedHasClassPathAttribute = true;
/* 507 */     return this.hasClassPathAttribute;
/*     */   }
/*     */ 
/*     */   private boolean isKnownToNotHaveClassPathAttribute()
/*     */   {
/* 517 */     if (javaHome == null) {
/* 518 */       javaHome = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
/*     */     }
/*     */ 
/* 521 */     if (jarNames == null) {
/* 522 */       localObject = new String[10];
/* 523 */       str = File.separator;
/* 524 */       int i = 0;
/* 525 */       localObject[(i++)] = (str + "rt.jar");
/* 526 */       localObject[(i++)] = (str + "sunrsasign.jar");
/* 527 */       localObject[(i++)] = (str + "jsse.jar");
/* 528 */       localObject[(i++)] = (str + "jce.jar");
/* 529 */       localObject[(i++)] = (str + "charsets.jar");
/* 530 */       localObject[(i++)] = (str + "dnsns.jar");
/* 531 */       localObject[(i++)] = (str + "ldapsec.jar");
/* 532 */       localObject[(i++)] = (str + "localedata.jar");
/* 533 */       localObject[(i++)] = (str + "sunjce_provider.jar");
/* 534 */       localObject[(i++)] = (str + "sunpkcs11.jar");
/* 535 */       jarNames = (String[])localObject;
/*     */     }
/*     */ 
/* 538 */     Object localObject = getName();
/* 539 */     String str = javaHome;
/* 540 */     if (((String)localObject).startsWith(str)) {
/* 541 */       String[] arrayOfString = jarNames;
/* 542 */       for (int j = 0; j < arrayOfString.length; j++) {
/* 543 */         if (((String)localObject).endsWith(arrayOfString[j])) {
/* 544 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 548 */     return false;
/*     */   }
/*     */ 
/*     */   private synchronized void ensureInitialization() {
/*     */     try {
/* 553 */       maybeInstantiateVerifier();
/*     */     } catch (IOException localIOException) {
/* 555 */       throw new RuntimeException(localIOException);
/*     */     }
/* 557 */     if ((this.jv != null) && (!this.jvInitialized)) {
/* 558 */       initializeVerifier();
/* 559 */       this.jvInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   JarEntry newEntry(ZipEntry paramZipEntry) {
/* 564 */     return new JarFileEntry(paramZipEntry);
/*     */   }
/*     */ 
/*     */   Enumeration<String> entryNames(CodeSource[] paramArrayOfCodeSource) {
/* 568 */     ensureInitialization();
/* 569 */     if (this.jv != null) {
/* 570 */       return this.jv.entryNames(this, paramArrayOfCodeSource);
/*     */     }
/*     */ 
/* 577 */     int i = 0;
/* 578 */     for (int j = 0; j < paramArrayOfCodeSource.length; j++) {
/* 579 */       if (paramArrayOfCodeSource[j].getCodeSigners() == null) {
/* 580 */         i = 1;
/* 581 */         break;
/*     */       }
/*     */     }
/* 584 */     if (i != 0) {
/* 585 */       return unsignedEntryNames();
/*     */     }
/* 587 */     return new Enumeration()
/*     */     {
/*     */       public boolean hasMoreElements() {
/* 590 */         return false;
/*     */       }
/*     */ 
/*     */       public String nextElement() {
/* 594 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   Enumeration<JarEntry> entries2()
/*     */   {
/* 606 */     ensureInitialization();
/* 607 */     if (this.jv != null) {
/* 608 */       return this.jv.entries2(this, super.entries());
/*     */     }
/*     */ 
/* 612 */     final Enumeration localEnumeration = super.entries();
/* 613 */     return new Enumeration()
/*     */     {
/*     */       ZipEntry entry;
/*     */ 
/*     */       public boolean hasMoreElements() {
/* 618 */         if (this.entry != null) {
/* 619 */           return true;
/*     */         }
/* 621 */         while (localEnumeration.hasMoreElements()) {
/* 622 */           ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 623 */           if (!JarVerifier.isSigningRelated(localZipEntry.getName()))
/*     */           {
/* 626 */             this.entry = localZipEntry;
/* 627 */             return true;
/*     */           }
/*     */         }
/* 629 */         return false;
/*     */       }
/*     */ 
/*     */       public JarFile.JarFileEntry nextElement() {
/* 633 */         if (hasMoreElements()) {
/* 634 */           ZipEntry localZipEntry = this.entry;
/* 635 */           this.entry = null;
/* 636 */           return new JarFile.JarFileEntry(JarFile.this, localZipEntry);
/*     */         }
/* 638 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   CodeSource[] getCodeSources(URL paramURL) {
/* 644 */     ensureInitialization();
/* 645 */     if (this.jv != null) {
/* 646 */       return this.jv.getCodeSources(this, paramURL);
/*     */     }
/*     */ 
/* 653 */     Enumeration localEnumeration = unsignedEntryNames();
/* 654 */     if (localEnumeration.hasMoreElements()) {
/* 655 */       return new CodeSource[] { JarVerifier.getUnsignedCS(paramURL) };
/*     */     }
/* 657 */     return null;
/*     */   }
/*     */ 
/*     */   private Enumeration<String> unsignedEntryNames()
/*     */   {
/* 662 */     final Enumeration localEnumeration = entries();
/* 663 */     return new Enumeration()
/*     */     {
/*     */       String name;
/*     */ 
/*     */       public boolean hasMoreElements()
/*     */       {
/* 672 */         if (this.name != null) {
/* 673 */           return true;
/*     */         }
/* 675 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 677 */           ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 678 */           String str = localZipEntry.getName();
/* 679 */           if ((!localZipEntry.isDirectory()) && (!JarVerifier.isSigningRelated(str)))
/*     */           {
/* 682 */             this.name = str;
/* 683 */             return true;
/*     */           }
/*     */         }
/* 685 */         return false;
/*     */       }
/*     */ 
/*     */       public String nextElement() {
/* 689 */         if (hasMoreElements()) {
/* 690 */           String str = this.name;
/* 691 */           this.name = null;
/* 692 */           return str;
/*     */         }
/* 694 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   CodeSource getCodeSource(URL paramURL, String paramString) {
/* 700 */     ensureInitialization();
/* 701 */     if (this.jv != null) {
/* 702 */       if (this.jv.eagerValidation) {
/* 703 */         CodeSource localCodeSource = null;
/* 704 */         JarEntry localJarEntry = getJarEntry(paramString);
/* 705 */         if (localJarEntry != null)
/* 706 */           localCodeSource = this.jv.getCodeSource(paramURL, this, localJarEntry);
/*     */         else {
/* 708 */           localCodeSource = this.jv.getCodeSource(paramURL, paramString);
/*     */         }
/* 710 */         return localCodeSource;
/*     */       }
/* 712 */       return this.jv.getCodeSource(paramURL, paramString);
/*     */     }
/*     */ 
/* 716 */     return JarVerifier.getUnsignedCS(paramURL);
/*     */   }
/*     */ 
/*     */   void setEagerValidation(boolean paramBoolean) {
/*     */     try {
/* 721 */       maybeInstantiateVerifier();
/*     */     } catch (IOException localIOException) {
/* 723 */       throw new RuntimeException(localIOException);
/*     */     }
/* 725 */     if (this.jv != null)
/* 726 */       this.jv.setEagerValidation(paramBoolean);
/*     */   }
/*     */ 
/*     */   List getManifestDigests()
/*     */   {
/* 731 */     ensureInitialization();
/* 732 */     if (this.jv != null) {
/* 733 */       return this.jv.getManifestDigests();
/*     */     }
/* 735 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  72 */     SharedSecrets.setJavaUtilJarAccess(new JavaUtilJarAccessImpl());
/*     */ 
/* 436 */     src = new char[] { 'c', 'l', 'a', 's', 's', '-', 'p', 'a', 't', 'h' };
/*     */ 
/* 438 */     lastOcc = new int[''];
/* 439 */     optoSft = new int[10];
/* 440 */     lastOcc[99] = 1;
/* 441 */     lastOcc[108] = 2;
/* 442 */     lastOcc[115] = 5;
/* 443 */     lastOcc[45] = 6;
/* 444 */     lastOcc[112] = 7;
/* 445 */     lastOcc[97] = 8;
/* 446 */     lastOcc[116] = 9;
/* 447 */     lastOcc[104] = 10;
/* 448 */     for (int i = 0; i < 9; i++)
/* 449 */       optoSft[i] = 10;
/* 450 */     optoSft[9] = 1;
/*     */   }
/*     */ 
/*     */   private class JarFileEntry extends JarEntry
/*     */   {
/*     */     JarFileEntry(ZipEntry arg2)
/*     */     {
/* 251 */       super();
/*     */     }
/*     */     public Attributes getAttributes() throws IOException {
/* 254 */       Manifest localManifest = JarFile.this.getManifest();
/* 255 */       if (localManifest != null) {
/* 256 */         return localManifest.getAttributes(getName());
/*     */       }
/* 258 */       return null;
/*     */     }
/*     */ 
/*     */     public Certificate[] getCertificates() {
/*     */       try {
/* 263 */         JarFile.this.maybeInstantiateVerifier();
/*     */       } catch (IOException localIOException) {
/* 265 */         throw new RuntimeException(localIOException);
/*     */       }
/* 267 */       if ((this.certs == null) && (JarFile.this.jv != null)) {
/* 268 */         this.certs = JarFile.this.jv.getCerts(JarFile.this, this);
/*     */       }
/* 270 */       return this.certs == null ? null : (Certificate[])this.certs.clone();
/*     */     }
/*     */     public CodeSigner[] getCodeSigners() {
/*     */       try {
/* 274 */         JarFile.this.maybeInstantiateVerifier();
/*     */       } catch (IOException localIOException) {
/* 276 */         throw new RuntimeException(localIOException);
/*     */       }
/* 278 */       if ((this.signers == null) && (JarFile.this.jv != null)) {
/* 279 */         this.signers = JarFile.this.jv.getCodeSigners(JarFile.this, this);
/*     */       }
/* 281 */       return this.signers == null ? null : (CodeSigner[])this.signers.clone();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.jar.JarFile
 * JD-Core Version:    0.6.2
 */