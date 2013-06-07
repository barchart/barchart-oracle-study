/*     */ package java.util.jar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.CodeSource;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.zip.ZipEntry;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ManifestDigester;
/*     */ import sun.security.util.ManifestEntryVerifier;
/*     */ import sun.security.util.SignatureFileVerifier;
/*     */ 
/*     */ class JarVerifier
/*     */ {
/*  47 */   static final Debug debug = Debug.getInstance("jar");
/*     */   private Hashtable verifiedSigners;
/*     */   private Hashtable sigFileSigners;
/*     */   private Hashtable sigFileData;
/*     */   private ArrayList pendingBlocks;
/*     */   private ArrayList signerCache;
/*  68 */   private boolean parsingBlockOrSF = false;
/*     */ 
/*  71 */   private boolean parsingMeta = true;
/*     */ 
/*  74 */   private boolean anyToVerify = true;
/*     */   private ByteArrayOutputStream baos;
/*     */   private volatile ManifestDigester manDig;
/*  84 */   byte[] manifestRawBytes = null;
/*     */   boolean eagerValidation;
/*  90 */   private Object csdomain = new Object();
/*     */   private List manifestDigests;
/* 496 */   private Map urlToCodeSourceMap = new HashMap();
/* 497 */   private Map signerToCodeSource = new HashMap();
/*     */   private URL lastURL;
/*     */   private Map lastURLMap;
/* 538 */   private CodeSigner[] emptySigner = new CodeSigner[0];
/*     */   private Map signerMap;
/* 767 */   private Enumeration emptyEnumeration = new Enumeration()
/*     */   {
/*     */     public boolean hasMoreElements() {
/* 770 */       return false;
/*     */     }
/*     */ 
/*     */     public String nextElement() {
/* 774 */       throw new NoSuchElementException();
/*     */     }
/* 767 */   };
/*     */   private List jarCodeSigners;
/*     */ 
/*     */   public JarVerifier(byte[] paramArrayOfByte)
/*     */   {
/*  96 */     this.manifestRawBytes = paramArrayOfByte;
/*  97 */     this.sigFileSigners = new Hashtable();
/*  98 */     this.verifiedSigners = new Hashtable();
/*  99 */     this.sigFileData = new Hashtable(11);
/* 100 */     this.pendingBlocks = new ArrayList();
/* 101 */     this.baos = new ByteArrayOutputStream();
/* 102 */     this.manifestDigests = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void beginEntry(JarEntry paramJarEntry, ManifestEntryVerifier paramManifestEntryVerifier)
/*     */     throws IOException
/*     */   {
/* 113 */     if (paramJarEntry == null) {
/* 114 */       return;
/*     */     }
/* 116 */     if (debug != null) {
/* 117 */       debug.println("beginEntry " + paramJarEntry.getName());
/*     */     }
/*     */ 
/* 120 */     String str1 = paramJarEntry.getName();
/*     */ 
/* 132 */     if (this.parsingMeta) {
/* 133 */       String str2 = str1.toUpperCase(Locale.ENGLISH);
/* 134 */       if ((str2.startsWith("META-INF/")) || (str2.startsWith("/META-INF/")))
/*     */       {
/* 137 */         if (paramJarEntry.isDirectory()) {
/* 138 */           paramManifestEntryVerifier.setEntry(null, paramJarEntry);
/* 139 */           return;
/*     */         }
/*     */ 
/* 142 */         if (SignatureFileVerifier.isBlockOrSF(str2))
/*     */         {
/* 144 */           this.parsingBlockOrSF = true;
/* 145 */           this.baos.reset();
/* 146 */           paramManifestEntryVerifier.setEntry(null, paramJarEntry);
/*     */         }
/* 148 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 152 */     if (this.parsingMeta) {
/* 153 */       doneWithMeta();
/*     */     }
/*     */ 
/* 156 */     if (paramJarEntry.isDirectory()) {
/* 157 */       paramManifestEntryVerifier.setEntry(null, paramJarEntry);
/* 158 */       return;
/*     */     }
/*     */ 
/* 163 */     if (str1.startsWith("./")) {
/* 164 */       str1 = str1.substring(2);
/*     */     }
/*     */ 
/* 168 */     if (str1.startsWith("/")) {
/* 169 */       str1 = str1.substring(1);
/*     */     }
/*     */ 
/* 172 */     if (this.sigFileSigners.get(str1) != null) {
/* 173 */       paramManifestEntryVerifier.setEntry(str1, paramJarEntry);
/* 174 */       return;
/*     */     }
/*     */ 
/* 178 */     paramManifestEntryVerifier.setEntry(null, paramJarEntry);
/*     */   }
/*     */ 
/*     */   public void update(int paramInt, ManifestEntryVerifier paramManifestEntryVerifier)
/*     */     throws IOException
/*     */   {
/* 190 */     if (paramInt != -1) {
/* 191 */       if (this.parsingBlockOrSF)
/* 192 */         this.baos.write(paramInt);
/*     */       else
/* 194 */         paramManifestEntryVerifier.update((byte)paramInt);
/*     */     }
/*     */     else
/* 197 */       processEntry(paramManifestEntryVerifier);
/*     */   }
/*     */ 
/*     */   public void update(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, ManifestEntryVerifier paramManifestEntryVerifier)
/*     */     throws IOException
/*     */   {
/* 209 */     if (paramInt1 != -1) {
/* 210 */       if (this.parsingBlockOrSF)
/* 211 */         this.baos.write(paramArrayOfByte, paramInt2, paramInt1);
/*     */       else
/* 213 */         paramManifestEntryVerifier.update(paramArrayOfByte, paramInt2, paramInt1);
/*     */     }
/*     */     else
/* 216 */       processEntry(paramManifestEntryVerifier);
/*     */   }
/*     */ 
/*     */   private void processEntry(ManifestEntryVerifier paramManifestEntryVerifier)
/*     */     throws IOException
/*     */   {
/*     */     Object localObject1;
/* 226 */     if (!this.parsingBlockOrSF) {
/* 227 */       localObject1 = paramManifestEntryVerifier.getEntry();
/* 228 */       if ((localObject1 != null) && (((JarEntry)localObject1).signers == null)) {
/* 229 */         ((JarEntry)localObject1).signers = paramManifestEntryVerifier.verify(this.verifiedSigners, this.sigFileSigners);
/* 230 */         ((JarEntry)localObject1).certs = mapSignersToCertArray(((JarEntry)localObject1).signers);
/*     */       }
/*     */     }
/*     */     else {
/*     */       try {
/* 235 */         this.parsingBlockOrSF = false;
/*     */ 
/* 237 */         if (debug != null) {
/* 238 */           debug.println("processEntry: processing block");
/*     */         }
/*     */ 
/* 241 */         localObject1 = paramManifestEntryVerifier.getEntry().getName().toUpperCase(Locale.ENGLISH);
/*     */         Object localObject2;
/* 244 */         if (((String)localObject1).endsWith(".SF")) {
/* 245 */           str = ((String)localObject1).substring(0, ((String)localObject1).length() - 3);
/* 246 */           byte[] arrayOfByte = this.baos.toByteArray();
/*     */ 
/* 248 */           this.sigFileData.put(str, arrayOfByte);
/*     */ 
/* 251 */           localObject2 = this.pendingBlocks.iterator();
/* 252 */           while (((Iterator)localObject2).hasNext()) {
/* 253 */             SignatureFileVerifier localSignatureFileVerifier = (SignatureFileVerifier)((Iterator)localObject2).next();
/*     */ 
/* 255 */             if (localSignatureFileVerifier.needSignatureFile(str)) {
/* 256 */               if (debug != null) {
/* 257 */                 debug.println("processEntry: processing pending block");
/*     */               }
/*     */ 
/* 261 */               localSignatureFileVerifier.setSignatureFile(arrayOfByte);
/* 262 */               localSignatureFileVerifier.process(this.sigFileSigners, this.manifestDigests);
/*     */             }
/*     */           }
/* 265 */           return;
/*     */         }
/*     */ 
/* 270 */         String str = ((String)localObject1).substring(0, ((String)localObject1).lastIndexOf("."));
/*     */ 
/* 272 */         if (this.signerCache == null) {
/* 273 */           this.signerCache = new ArrayList();
/*     */         }
/* 275 */         if (this.manDig == null) {
/* 276 */           synchronized (this.manifestRawBytes) {
/* 277 */             if (this.manDig == null) {
/* 278 */               this.manDig = new ManifestDigester(this.manifestRawBytes);
/* 279 */               this.manifestRawBytes = null;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 284 */         ??? = new SignatureFileVerifier(this.signerCache, this.manDig, (String)localObject1, this.baos.toByteArray());
/*     */ 
/* 288 */         if (((SignatureFileVerifier)???).needSignatureFileBytes())
/*     */         {
/* 290 */           localObject2 = (byte[])this.sigFileData.get(str);
/*     */ 
/* 292 */           if (localObject2 == null)
/*     */           {
/* 296 */             if (debug != null) {
/* 297 */               debug.println("adding pending block");
/*     */             }
/* 299 */             this.pendingBlocks.add(???);
/* 300 */             return;
/*     */           }
/* 302 */           ((SignatureFileVerifier)???).setSignatureFile((byte[])localObject2);
/*     */         }
/*     */ 
/* 305 */         ((SignatureFileVerifier)???).process(this.sigFileSigners, this.manifestDigests);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 309 */         if (debug != null) debug.println("processEntry caught: " + localIOException); 
/*     */       }
/*     */       catch (SignatureException localSignatureException)
/*     */       {
/* 312 */         if (debug != null) debug.println("processEntry caught: " + localSignatureException); 
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */       {
/* 315 */         if (debug != null) debug.println("processEntry caught: " + localNoSuchAlgorithmException); 
/*     */       }
/*     */       catch (CertificateException localCertificateException)
/*     */       {
/* 318 */         if (debug != null) debug.println("processEntry caught: " + localCertificateException);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Certificate[] getCerts(String paramString)
/*     */   {
/* 331 */     return mapSignersToCertArray(getCodeSigners(paramString));
/*     */   }
/*     */ 
/*     */   public Certificate[] getCerts(JarFile paramJarFile, JarEntry paramJarEntry)
/*     */   {
/* 336 */     return mapSignersToCertArray(getCodeSigners(paramJarFile, paramJarEntry));
/*     */   }
/*     */ 
/*     */   public CodeSigner[] getCodeSigners(String paramString)
/*     */   {
/* 346 */     return (CodeSigner[])this.verifiedSigners.get(paramString);
/*     */   }
/*     */ 
/*     */   public CodeSigner[] getCodeSigners(JarFile paramJarFile, JarEntry paramJarEntry)
/*     */   {
/* 351 */     String str = paramJarEntry.getName();
/* 352 */     if ((this.eagerValidation) && (this.sigFileSigners.get(str) != null))
/*     */     {
/*     */       try
/*     */       {
/* 358 */         InputStream localInputStream = paramJarFile.getInputStream(paramJarEntry);
/* 359 */         byte[] arrayOfByte = new byte[1024];
/* 360 */         int i = arrayOfByte.length;
/* 361 */         while (i != -1) {
/* 362 */           i = localInputStream.read(arrayOfByte, 0, arrayOfByte.length);
/*     */         }
/* 364 */         localInputStream.close();
/*     */       } catch (IOException localIOException) {
/*     */       }
/*     */     }
/* 368 */     return getCodeSigners(str);
/*     */   }
/*     */ 
/*     */   private static Certificate[] mapSignersToCertArray(CodeSigner[] paramArrayOfCodeSigner)
/*     */   {
/* 378 */     if (paramArrayOfCodeSigner != null) {
/* 379 */       ArrayList localArrayList = new ArrayList();
/* 380 */       for (int i = 0; i < paramArrayOfCodeSigner.length; i++) {
/* 381 */         localArrayList.addAll(paramArrayOfCodeSigner[i].getSignerCertPath().getCertificates());
/*     */       }
/*     */ 
/* 386 */       return (Certificate[])localArrayList.toArray(new Certificate[localArrayList.size()]);
/*     */     }
/*     */ 
/* 390 */     return null;
/*     */   }
/*     */ 
/*     */   boolean nothingToVerify()
/*     */   {
/* 400 */     return !this.anyToVerify;
/*     */   }
/*     */ 
/*     */   void doneWithMeta()
/*     */   {
/* 411 */     this.parsingMeta = false;
/* 412 */     this.anyToVerify = (!this.sigFileSigners.isEmpty());
/* 413 */     this.baos = null;
/* 414 */     this.sigFileData = null;
/* 415 */     this.pendingBlocks = null;
/* 416 */     this.signerCache = null;
/* 417 */     this.manDig = null;
/*     */ 
/* 420 */     if (this.sigFileSigners.containsKey("META-INF/MANIFEST.MF"))
/* 421 */       this.verifiedSigners.put("META-INF/MANIFEST.MF", this.sigFileSigners.remove("META-INF/MANIFEST.MF"));
/*     */   }
/*     */ 
/*     */   private synchronized CodeSource mapSignersToCodeSource(URL paramURL, CodeSigner[] paramArrayOfCodeSigner)
/*     */   {
/*     */     Object localObject1;
/* 508 */     if (paramURL == this.lastURL) {
/* 509 */       localObject1 = this.lastURLMap;
/*     */     } else {
/* 511 */       localObject1 = (Map)this.urlToCodeSourceMap.get(paramURL);
/* 512 */       if (localObject1 == null) {
/* 513 */         localObject1 = new HashMap();
/* 514 */         this.urlToCodeSourceMap.put(paramURL, localObject1);
/*     */       }
/* 516 */       this.lastURLMap = ((Map)localObject1);
/* 517 */       this.lastURL = paramURL;
/*     */     }
/* 519 */     Object localObject2 = (CodeSource)((Map)localObject1).get(paramArrayOfCodeSigner);
/* 520 */     if (localObject2 == null) {
/* 521 */       localObject2 = new VerifierCodeSource(this.csdomain, paramURL, paramArrayOfCodeSigner);
/* 522 */       this.signerToCodeSource.put(paramArrayOfCodeSigner, localObject2);
/*     */     }
/* 524 */     return localObject2;
/*     */   }
/*     */ 
/*     */   private CodeSource[] mapSignersToCodeSources(URL paramURL, List paramList, boolean paramBoolean) {
/* 528 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 530 */     for (int i = 0; i < paramList.size(); i++) {
/* 531 */       localArrayList.add(mapSignersToCodeSource(paramURL, (CodeSigner[])paramList.get(i)));
/*     */     }
/* 533 */     if (paramBoolean) {
/* 534 */       localArrayList.add(mapSignersToCodeSource(paramURL, null));
/*     */     }
/* 536 */     return (CodeSource[])localArrayList.toArray(new CodeSource[localArrayList.size()]);
/*     */   }
/*     */ 
/*     */   private CodeSigner[] findMatchingSigners(CodeSource paramCodeSource)
/*     */   {
/* 544 */     if ((paramCodeSource instanceof VerifierCodeSource)) {
/* 545 */       localObject = (VerifierCodeSource)paramCodeSource;
/* 546 */       if (((VerifierCodeSource)localObject).isSameDomain(this.csdomain)) {
/* 547 */         return ((VerifierCodeSource)paramCodeSource).getPrivateSigners();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 555 */     Object localObject = mapSignersToCodeSources(paramCodeSource.getLocation(), getJarCodeSigners(), true);
/* 556 */     ArrayList localArrayList = new ArrayList();
/* 557 */     for (int i = 0; i < localObject.length; i++) {
/* 558 */       localArrayList.add(localObject[i]);
/*     */     }
/* 560 */     i = localArrayList.indexOf(paramCodeSource);
/* 561 */     if (i != -1)
/*     */     {
/* 563 */       CodeSigner[] arrayOfCodeSigner = ((VerifierCodeSource)localArrayList.get(i)).getPrivateSigners();
/* 564 */       if (arrayOfCodeSigner == null) {
/* 565 */         arrayOfCodeSigner = this.emptySigner;
/*     */       }
/* 567 */       return arrayOfCodeSigner;
/*     */     }
/* 569 */     return null;
/*     */   }
/*     */ 
/*     */   private synchronized Map signerMap()
/*     */   {
/* 647 */     if (this.signerMap == null)
/*     */     {
/* 653 */       this.signerMap = new HashMap(this.verifiedSigners.size() + this.sigFileSigners.size());
/* 654 */       this.signerMap.putAll(this.verifiedSigners);
/* 655 */       this.signerMap.putAll(this.sigFileSigners);
/*     */     }
/* 657 */     return this.signerMap;
/*     */   }
/*     */ 
/*     */   public synchronized Enumeration<String> entryNames(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource) {
/* 661 */     Map localMap = signerMap();
/* 662 */     final Iterator localIterator = localMap.entrySet().iterator();
/* 663 */     int i = 0;
/*     */ 
/* 669 */     ArrayList localArrayList1 = new ArrayList(paramArrayOfCodeSource.length);
/* 670 */     for (int j = 0; j < paramArrayOfCodeSource.length; j++) {
/* 671 */       localObject = findMatchingSigners(paramArrayOfCodeSource[j]);
/* 672 */       if (localObject != null) {
/* 673 */         if (localObject.length > 0)
/* 674 */           localArrayList1.add(localObject);
/*     */         else {
/* 676 */           i = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 681 */     final ArrayList localArrayList2 = localArrayList1;
/* 682 */     Object localObject = i != 0 ? unsignedEntryNames(paramJarFile) : this.emptyEnumeration;
/*     */ 
/* 684 */     return new Enumeration()
/*     */     {
/*     */       String name;
/*     */ 
/*     */       public boolean hasMoreElements() {
/* 689 */         if (this.name != null) {
/* 690 */           return true;
/*     */         }
/*     */ 
/* 693 */         while (localIterator.hasNext()) {
/* 694 */           Map.Entry localEntry = (Map.Entry)localIterator.next();
/* 695 */           if (localArrayList2.contains((CodeSigner[])localEntry.getValue())) {
/* 696 */             this.name = ((String)localEntry.getKey());
/* 697 */             return true;
/*     */           }
/*     */         }
/* 700 */         if (this.val$enum2.hasMoreElements()) {
/* 701 */           this.name = ((String)this.val$enum2.nextElement());
/* 702 */           return true;
/*     */         }
/* 704 */         return false;
/*     */       }
/*     */ 
/*     */       public String nextElement() {
/* 708 */         if (hasMoreElements()) {
/* 709 */           String str = this.name;
/* 710 */           this.name = null;
/* 711 */           return str;
/*     */         }
/* 713 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Enumeration<JarEntry> entries2(final JarFile paramJarFile, Enumeration paramEnumeration)
/*     */   {
/* 723 */     final HashMap localHashMap = new HashMap();
/* 724 */     localHashMap.putAll(signerMap());
/* 725 */     final Enumeration localEnumeration = paramEnumeration;
/* 726 */     return new Enumeration() {
/* 728 */       Enumeration signers = null;
/*     */       JarEntry entry;
/*     */ 
/*     */       public boolean hasMoreElements() {
/* 732 */         if (this.entry != null)
/* 733 */           return true;
/*     */         Object localObject;
/* 735 */         while (localEnumeration.hasMoreElements()) {
/* 736 */           localObject = (ZipEntry)localEnumeration.nextElement();
/* 737 */           if (!JarVerifier.isSigningRelated(((ZipEntry)localObject).getName()))
/*     */           {
/* 740 */             this.entry = paramJarFile.newEntry((ZipEntry)localObject);
/* 741 */             return true;
/*     */           }
/*     */         }
/* 743 */         if (this.signers == null) {
/* 744 */           this.signers = Collections.enumeration(localHashMap.keySet());
/*     */         }
/* 746 */         if (this.signers.hasMoreElements()) {
/* 747 */           localObject = (String)this.signers.nextElement();
/* 748 */           this.entry = paramJarFile.newEntry(new ZipEntry((String)localObject));
/* 749 */           return true;
/*     */         }
/*     */ 
/* 753 */         return false;
/*     */       }
/*     */ 
/*     */       public JarEntry nextElement() {
/* 757 */         if (hasMoreElements()) {
/* 758 */           JarEntry localJarEntry = this.entry;
/* 759 */           localHashMap.remove(localJarEntry.getName());
/* 760 */           this.entry = null;
/* 761 */           return localJarEntry;
/*     */         }
/* 763 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static boolean isSigningRelated(String paramString)
/*     */   {
/* 780 */     paramString = paramString.toUpperCase(Locale.ENGLISH);
/* 781 */     if (!paramString.startsWith("META-INF/")) {
/* 782 */       return false;
/*     */     }
/* 784 */     paramString = paramString.substring(9);
/* 785 */     if (paramString.indexOf('/') != -1) {
/* 786 */       return false;
/*     */     }
/* 788 */     if ((paramString.endsWith(".DSA")) || (paramString.endsWith(".RSA")) || (paramString.endsWith(".SF")) || (paramString.endsWith(".EC")) || (paramString.startsWith("SIG-")) || (paramString.equals("MANIFEST.MF")))
/*     */     {
/* 794 */       return true;
/*     */     }
/* 796 */     return false;
/*     */   }
/*     */ 
/*     */   private Enumeration<String> unsignedEntryNames(JarFile paramJarFile) {
/* 800 */     final Map localMap = signerMap();
/* 801 */     final Enumeration localEnumeration = paramJarFile.entries();
/* 802 */     return new Enumeration()
/*     */     {
/*     */       String name;
/*     */ 
/*     */       public boolean hasMoreElements()
/*     */       {
/* 811 */         if (this.name != null) {
/* 812 */           return true;
/*     */         }
/* 814 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 816 */           ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 817 */           String str = localZipEntry.getName();
/* 818 */           if ((!localZipEntry.isDirectory()) && (!JarVerifier.isSigningRelated(str)))
/*     */           {
/* 821 */             if (localMap.get(str) == null) {
/* 822 */               this.name = str;
/* 823 */               return true;
/*     */             }
/*     */           }
/*     */         }
/* 826 */         return false;
/*     */       }
/*     */ 
/*     */       public String nextElement() {
/* 830 */         if (hasMoreElements()) {
/* 831 */           String str = this.name;
/* 832 */           this.name = null;
/* 833 */           return str;
/*     */         }
/* 835 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private synchronized List getJarCodeSigners()
/*     */   {
/* 843 */     if (this.jarCodeSigners == null) {
/* 844 */       HashSet localHashSet = new HashSet();
/* 845 */       localHashSet.addAll(signerMap().values());
/* 846 */       this.jarCodeSigners = new ArrayList();
/* 847 */       this.jarCodeSigners.addAll(localHashSet);
/*     */     }
/* 849 */     return this.jarCodeSigners;
/*     */   }
/*     */ 
/*     */   public synchronized CodeSource[] getCodeSources(JarFile paramJarFile, URL paramURL) {
/* 853 */     boolean bool = unsignedEntryNames(paramJarFile).hasMoreElements();
/*     */ 
/* 855 */     return mapSignersToCodeSources(paramURL, getJarCodeSigners(), bool);
/*     */   }
/*     */ 
/*     */   public CodeSource getCodeSource(URL paramURL, String paramString)
/*     */   {
/* 861 */     CodeSigner[] arrayOfCodeSigner = (CodeSigner[])signerMap().get(paramString);
/* 862 */     return mapSignersToCodeSource(paramURL, arrayOfCodeSigner);
/*     */   }
/*     */ 
/*     */   public CodeSource getCodeSource(URL paramURL, JarFile paramJarFile, JarEntry paramJarEntry)
/*     */   {
/* 868 */     return mapSignersToCodeSource(paramURL, getCodeSigners(paramJarFile, paramJarEntry));
/*     */   }
/*     */ 
/*     */   public void setEagerValidation(boolean paramBoolean) {
/* 872 */     this.eagerValidation = paramBoolean;
/*     */   }
/*     */ 
/*     */   public synchronized List getManifestDigests() {
/* 876 */     return Collections.unmodifiableList(this.manifestDigests);
/*     */   }
/*     */ 
/*     */   static CodeSource getUnsignedCS(URL paramURL) {
/* 880 */     return new VerifierCodeSource(null, paramURL, (Certificate[])null);
/*     */   }
/*     */ 
/*     */   private static class VerifierCodeSource extends CodeSource
/*     */   {
/*     */     URL vlocation;
/*     */     CodeSigner[] vsigners;
/*     */     Certificate[] vcerts;
/*     */     Object csdomain;
/*     */ 
/*     */     VerifierCodeSource(Object paramObject, URL paramURL, CodeSigner[] paramArrayOfCodeSigner)
/*     */     {
/* 584 */       super(paramArrayOfCodeSigner);
/* 585 */       this.csdomain = paramObject;
/* 586 */       this.vlocation = paramURL;
/* 587 */       this.vsigners = paramArrayOfCodeSigner;
/*     */     }
/*     */ 
/*     */     VerifierCodeSource(Object paramObject, URL paramURL, Certificate[] paramArrayOfCertificate) {
/* 591 */       super(paramArrayOfCertificate);
/* 592 */       this.csdomain = paramObject;
/* 593 */       this.vlocation = paramURL;
/* 594 */       this.vcerts = paramArrayOfCertificate;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 604 */       if (paramObject == this) {
/* 605 */         return true;
/*     */       }
/* 607 */       if ((paramObject instanceof VerifierCodeSource)) {
/* 608 */         VerifierCodeSource localVerifierCodeSource = (VerifierCodeSource)paramObject;
/*     */ 
/* 615 */         if (isSameDomain(localVerifierCodeSource.csdomain)) {
/* 616 */           if ((localVerifierCodeSource.vsigners != this.vsigners) || (localVerifierCodeSource.vcerts != this.vcerts))
/*     */           {
/* 618 */             return false;
/*     */           }
/* 620 */           if (localVerifierCodeSource.vlocation != null)
/* 621 */             return localVerifierCodeSource.vlocation.equals(this.vlocation);
/* 622 */           if (this.vlocation != null) {
/* 623 */             return this.vlocation.equals(localVerifierCodeSource.vlocation);
/*     */           }
/* 625 */           return true;
/*     */         }
/*     */       }
/*     */ 
/* 629 */       return super.equals(paramObject);
/*     */     }
/*     */ 
/*     */     boolean isSameDomain(Object paramObject) {
/* 633 */       return this.csdomain == paramObject;
/*     */     }
/*     */ 
/*     */     private CodeSigner[] getPrivateSigners() {
/* 637 */       return this.vsigners;
/*     */     }
/*     */ 
/*     */     private Certificate[] getPrivateCertificates() {
/* 641 */       return this.vcerts;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class VerifierStream extends InputStream
/*     */   {
/*     */     private InputStream is;
/*     */     private JarVerifier jv;
/*     */     private ManifestEntryVerifier mev;
/*     */     private long numLeft;
/*     */ 
/*     */     VerifierStream(Manifest paramManifest, JarEntry paramJarEntry, InputStream paramInputStream, JarVerifier paramJarVerifier)
/*     */       throws IOException
/*     */     {
/* 438 */       this.is = paramInputStream;
/* 439 */       this.jv = paramJarVerifier;
/* 440 */       this.mev = new ManifestEntryVerifier(paramManifest);
/* 441 */       this.jv.beginEntry(paramJarEntry, this.mev);
/* 442 */       this.numLeft = paramJarEntry.getSize();
/* 443 */       if (this.numLeft == 0L)
/* 444 */         this.jv.update(-1, this.mev);
/*     */     }
/*     */ 
/*     */     public int read() throws IOException
/*     */     {
/* 449 */       if (this.numLeft > 0L) {
/* 450 */         int i = this.is.read();
/* 451 */         this.jv.update(i, this.mev);
/* 452 */         this.numLeft -= 1L;
/* 453 */         if (this.numLeft == 0L)
/* 454 */           this.jv.update(-1, this.mev);
/* 455 */         return i;
/*     */       }
/* 457 */       return -1;
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 462 */       if ((this.numLeft > 0L) && (this.numLeft < paramInt2)) {
/* 463 */         paramInt2 = (int)this.numLeft;
/*     */       }
/*     */ 
/* 466 */       if (this.numLeft > 0L) {
/* 467 */         int i = this.is.read(paramArrayOfByte, paramInt1, paramInt2);
/* 468 */         this.jv.update(i, paramArrayOfByte, paramInt1, paramInt2, this.mev);
/* 469 */         this.numLeft -= i;
/* 470 */         if (this.numLeft == 0L)
/* 471 */           this.jv.update(-1, paramArrayOfByte, paramInt1, paramInt2, this.mev);
/* 472 */         return i;
/*     */       }
/* 474 */       return -1;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 481 */       if (this.is != null)
/* 482 */         this.is.close();
/* 483 */       this.is = null;
/* 484 */       this.mev = null;
/* 485 */       this.jv = null;
/*     */     }
/*     */ 
/*     */     public int available() throws IOException {
/* 489 */       return this.is.available();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.jar.JarVerifier
 * JD-Core Version:    0.6.2
 */