/*     */ package sun.security.krb5.internal.ktab;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ 
/*     */ public class KeyTab
/*     */   implements KeyTabConstants
/*     */ {
/*  65 */   private static final boolean DEBUG = Krb5.DEBUG;
/*  66 */   private static String defaultTabName = null;
/*     */ 
/*  70 */   private static Map<String, KeyTab> map = new HashMap();
/*     */ 
/*  73 */   private boolean isMissing = false;
/*     */ 
/*  76 */   private boolean isValid = true;
/*     */   private final String tabName;
/*     */   private long lastModified;
/*     */   private int kt_vno;
/*  82 */   private Vector<KeyTabEntry> entries = new Vector();
/*     */ 
/*     */   private KeyTab(String paramString)
/*     */   {
/*  92 */     this.tabName = paramString;
/*     */     try {
/*  94 */       this.lastModified = new File(this.tabName).lastModified();
/*  95 */       KeyTabInputStream localKeyTabInputStream = new KeyTabInputStream(new FileInputStream(paramString)); Object localObject1 = null;
/*     */       try {
/*  97 */         load(localKeyTabInputStream);
/*     */       }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/*  95 */         localObject1 = localThrowable2; throw localThrowable2;
/*     */       }
/*     */       finally {
/*  98 */         if (localKeyTabInputStream != null) if (localObject1 != null) try { localKeyTabInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localKeyTabInputStream.close();  
/*     */       }
/*     */     } catch (FileNotFoundException localFileNotFoundException) { this.entries.clear();
/* 101 */       this.isMissing = true;
/*     */     } catch (Exception localException) {
/* 103 */       this.entries.clear();
/* 104 */       this.isValid = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized KeyTab getInstance0(String paramString)
/*     */   {
/* 119 */     long l = new File(paramString).lastModified();
/* 120 */     KeyTab localKeyTab1 = (KeyTab)map.get(paramString);
/* 121 */     if ((localKeyTab1 != null) && (localKeyTab1.isValid()) && (localKeyTab1.lastModified == l)) {
/* 122 */       return localKeyTab1;
/*     */     }
/* 124 */     KeyTab localKeyTab2 = new KeyTab(paramString);
/* 125 */     if (localKeyTab2.isValid()) {
/* 126 */       map.put(paramString, localKeyTab2);
/* 127 */       return localKeyTab2;
/* 128 */     }if (localKeyTab1 != null) {
/* 129 */       return localKeyTab1;
/*     */     }
/* 131 */     return localKeyTab2;
/*     */   }
/*     */ 
/*     */   public static KeyTab getInstance(String paramString)
/*     */   {
/* 141 */     if (paramString == null) {
/* 142 */       return getInstance();
/*     */     }
/* 144 */     return getInstance0(parse(paramString));
/*     */   }
/*     */ 
/*     */   public static KeyTab getInstance(File paramFile)
/*     */   {
/* 154 */     if (paramFile == null) {
/* 155 */       return getInstance();
/*     */     }
/* 157 */     return getInstance0(paramFile.getPath());
/*     */   }
/*     */ 
/*     */   public static KeyTab getInstance()
/*     */   {
/* 166 */     return getInstance(getDefaultTabName());
/*     */   }
/*     */ 
/*     */   public boolean isMissing() {
/* 170 */     return this.isMissing;
/*     */   }
/*     */ 
/*     */   public boolean isValid() {
/* 174 */     return this.isValid;
/*     */   }
/*     */ 
/*     */   private static String getDefaultTabName()
/*     */   {
/* 184 */     if (defaultTabName != null) {
/* 185 */       return defaultTabName;
/*     */     }
/* 187 */     String str1 = null;
/*     */     try {
/* 189 */       String str2 = Config.getInstance().getDefault("default_keytab_name", "libdefaults");
/*     */ 
/* 191 */       if (str2 != null) {
/* 192 */         StringTokenizer localStringTokenizer = new StringTokenizer(str2, " ");
/* 193 */         while (localStringTokenizer.hasMoreTokens()) {
/* 194 */           str1 = parse(localStringTokenizer.nextToken());
/* 195 */           if (new File(str1).exists())
/* 196 */             break;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (KrbException localKrbException) {
/* 201 */       str1 = null;
/*     */     }
/*     */ 
/* 204 */     if (str1 == null) {
/* 205 */       String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
/*     */ 
/* 209 */       if (str3 == null) {
/* 210 */         str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
/*     */       }
/*     */ 
/* 215 */       str1 = str3 + File.separator + "krb5.keytab";
/*     */     }
/* 217 */     defaultTabName = str1;
/* 218 */     return str1;
/*     */   }
/*     */ 
/*     */   private static String parse(String paramString)
/*     */   {
/*     */     String str;
/* 229 */     if ((paramString.length() >= 5) && (paramString.substring(0, 5).equalsIgnoreCase("FILE:")))
/*     */     {
/* 231 */       str = paramString.substring(5);
/* 232 */     } else if ((paramString.length() >= 9) && (paramString.substring(0, 9).equalsIgnoreCase("ANY:FILE:")))
/*     */     {
/* 235 */       str = paramString.substring(9);
/* 236 */     } else if ((paramString.length() >= 7) && (paramString.substring(0, 7).equalsIgnoreCase("SRVTAB:")))
/*     */     {
/* 239 */       str = paramString.substring(7);
/*     */     }
/* 241 */     else str = paramString;
/* 242 */     return str;
/*     */   }
/*     */ 
/*     */   private void load(KeyTabInputStream paramKeyTabInputStream)
/*     */     throws IOException, RealmException
/*     */   {
/* 248 */     this.entries.clear();
/* 249 */     this.kt_vno = paramKeyTabInputStream.readVersion();
/* 250 */     if (this.kt_vno == 1281) {
/* 251 */       paramKeyTabInputStream.setNativeByteOrder();
/*     */     }
/* 253 */     int i = 0;
/*     */ 
/* 255 */     while (paramKeyTabInputStream.available() > 0) {
/* 256 */       i = paramKeyTabInputStream.readEntryLength();
/* 257 */       KeyTabEntry localKeyTabEntry = paramKeyTabInputStream.readEntry(i, this.kt_vno);
/* 258 */       if (DEBUG) {
/* 259 */         System.out.println(">>> KeyTab: load() entry length: " + i + "; type: " + (localKeyTabEntry != null ? localKeyTabEntry.keyType : 0));
/*     */       }
/*     */ 
/* 263 */       if (localKeyTabEntry != null)
/* 264 */         this.entries.addElement(localKeyTabEntry);
/*     */     }
/*     */   }
/*     */ 
/*     */   public EncryptionKey[] readServiceKeys(PrincipalName paramPrincipalName)
/*     */   {
/* 278 */     int i = this.entries.size();
/* 279 */     ArrayList localArrayList = new ArrayList(i);
/* 280 */     for (int j = i - 1; j >= 0; j--) {
/* 281 */       KeyTabEntry localKeyTabEntry = (KeyTabEntry)this.entries.elementAt(j);
/* 282 */       if (localKeyTabEntry.service.match(paramPrincipalName)) {
/* 283 */         if (EType.isSupported(localKeyTabEntry.keyType)) {
/* 284 */           EncryptionKey localEncryptionKey = new EncryptionKey(localKeyTabEntry.keyblock, localKeyTabEntry.keyType, new Integer(localKeyTabEntry.keyVersion));
/*     */ 
/* 287 */           localArrayList.add(localEncryptionKey);
/* 288 */           if (DEBUG) {
/* 289 */             System.out.println("Added key: " + localKeyTabEntry.keyType + "version: " + localKeyTabEntry.keyVersion);
/*     */           }
/*     */         }
/* 292 */         else if (DEBUG) {
/* 293 */           System.out.println("Found unsupported keytype (" + localKeyTabEntry.keyType + ") for " + paramPrincipalName);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 298 */     i = localArrayList.size();
/* 299 */     EncryptionKey[] arrayOfEncryptionKey = (EncryptionKey[])localArrayList.toArray(new EncryptionKey[i]);
/*     */ 
/* 302 */     if (DEBUG) {
/* 303 */       System.out.println("Ordering keys wrt default_tkt_enctypes list");
/*     */     }
/*     */ 
/* 306 */     final int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
/*     */ 
/* 311 */     Arrays.sort(arrayOfEncryptionKey, new Comparator()
/*     */     {
/*     */       public int compare(EncryptionKey paramAnonymousEncryptionKey1, EncryptionKey paramAnonymousEncryptionKey2) {
/* 314 */         if (arrayOfInt != null) {
/* 315 */           int i = paramAnonymousEncryptionKey1.getEType();
/* 316 */           int j = paramAnonymousEncryptionKey2.getEType();
/* 317 */           if (i != j) {
/* 318 */             for (int k = 0; k < arrayOfInt.length; k++) {
/* 319 */               if (arrayOfInt[k] == i)
/* 320 */                 return -1;
/* 321 */               if (arrayOfInt[k] == j) {
/* 322 */                 return 1;
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 330 */         return paramAnonymousEncryptionKey2.getKeyVersionNumber().intValue() - paramAnonymousEncryptionKey1.getKeyVersionNumber().intValue();
/*     */       }
/*     */     });
/* 335 */     return arrayOfEncryptionKey;
/*     */   }
/*     */ 
/*     */   public boolean findServiceEntry(PrincipalName paramPrincipalName)
/*     */   {
/* 349 */     for (int i = 0; i < this.entries.size(); i++) {
/* 350 */       KeyTabEntry localKeyTabEntry = (KeyTabEntry)this.entries.elementAt(i);
/* 351 */       if (localKeyTabEntry.service.match(paramPrincipalName)) {
/* 352 */         if (EType.isSupported(localKeyTabEntry.keyType))
/* 353 */           return true;
/* 354 */         if (DEBUG) {
/* 355 */           System.out.println("Found unsupported keytype (" + localKeyTabEntry.keyType + ") for " + paramPrincipalName);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */   public String tabName() {
/* 364 */     return this.tabName;
/*     */   }
/*     */ 
/*     */   public void addEntry(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, boolean paramBoolean)
/*     */     throws KrbException
/*     */   {
/* 381 */     EncryptionKey[] arrayOfEncryptionKey = EncryptionKey.acquireSecretKeys(paramArrayOfChar, paramPrincipalName.getSalt());
/*     */ 
/* 387 */     int i = 0;
/* 388 */     for (int j = this.entries.size() - 1; j >= 0; j--) {
/* 389 */       KeyTabEntry localKeyTabEntry1 = (KeyTabEntry)this.entries.get(j);
/* 390 */       if (localKeyTabEntry1.service.match(paramPrincipalName)) {
/* 391 */         if (localKeyTabEntry1.keyVersion > i) {
/* 392 */           i = localKeyTabEntry1.keyVersion;
/*     */         }
/* 394 */         if ((!paramBoolean) || (localKeyTabEntry1.keyVersion == paramInt)) {
/* 395 */           this.entries.removeElementAt(j);
/*     */         }
/*     */       }
/*     */     }
/* 399 */     if (paramInt == -1) {
/* 400 */       paramInt = i + 1;
/*     */     }
/*     */ 
/* 403 */     for (j = 0; (arrayOfEncryptionKey != null) && (j < arrayOfEncryptionKey.length); j++) {
/* 404 */       int k = arrayOfEncryptionKey[j].getEType();
/* 405 */       byte[] arrayOfByte = arrayOfEncryptionKey[j].getBytes();
/*     */ 
/* 407 */       KeyTabEntry localKeyTabEntry2 = new KeyTabEntry(paramPrincipalName, paramPrincipalName.getRealm(), new KerberosTime(System.currentTimeMillis()), paramInt, k, arrayOfByte);
/*     */ 
/* 411 */       this.entries.addElement(localKeyTabEntry2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public KeyTabEntry[] getEntries()
/*     */   {
/* 420 */     KeyTabEntry[] arrayOfKeyTabEntry = new KeyTabEntry[this.entries.size()];
/* 421 */     for (int i = 0; i < arrayOfKeyTabEntry.length; i++) {
/* 422 */       arrayOfKeyTabEntry[i] = ((KeyTabEntry)this.entries.elementAt(i));
/*     */     }
/* 424 */     return arrayOfKeyTabEntry;
/*     */   }
/*     */ 
/*     */   public static synchronized KeyTab create()
/*     */     throws IOException, RealmException
/*     */   {
/* 432 */     String str = getDefaultTabName();
/* 433 */     return create(str);
/*     */   }
/*     */ 
/*     */   public static synchronized KeyTab create(String paramString)
/*     */     throws IOException, RealmException
/*     */   {
/* 442 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramString)); Object localObject1 = null;
/*     */     try {
/* 444 */       localKeyTabOutputStream.writeVersion(1282);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 442 */       localObject1 = localThrowable2; throw localThrowable2;
/*     */     }
/*     */     finally {
/* 445 */       if (localKeyTabOutputStream != null) if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localKeyTabOutputStream.close(); 
/*     */     }
/* 446 */     return new KeyTab(paramString);
/*     */   }
/*     */ 
/*     */   public synchronized void save()
/*     */     throws IOException
/*     */   {
/* 453 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(this.tabName)); Object localObject1 = null;
/*     */     try {
/* 455 */       localKeyTabOutputStream.writeVersion(this.kt_vno);
/* 456 */       for (int i = 0; i < this.entries.size(); i++)
/* 457 */         localKeyTabOutputStream.writeEntry((KeyTabEntry)this.entries.elementAt(i));
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 453 */       localObject1 = localThrowable2; throw localThrowable2;
/*     */     }
/*     */     finally
/*     */     {
/* 459 */       if (localKeyTabOutputStream != null) if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localKeyTabOutputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int deleteEntries(PrincipalName paramPrincipalName, int paramInt1, int paramInt2)
/*     */   {
/* 470 */     int i = 0;
/*     */ 
/* 473 */     HashMap localHashMap = new HashMap();
/*     */     KeyTabEntry localKeyTabEntry;
/*     */     int k;
/* 475 */     for (int j = this.entries.size() - 1; j >= 0; j--) {
/* 476 */       localKeyTabEntry = (KeyTabEntry)this.entries.get(j);
/* 477 */       if ((paramPrincipalName.match(localKeyTabEntry.getService())) && (
/* 478 */         (paramInt1 == -1) || (localKeyTabEntry.keyType == paramInt1))) {
/* 479 */         if (paramInt2 == -2)
/*     */         {
/* 482 */           if (localHashMap.containsKey(Integer.valueOf(localKeyTabEntry.keyType))) {
/* 483 */             k = ((Integer)localHashMap.get(Integer.valueOf(localKeyTabEntry.keyType))).intValue();
/* 484 */             if (localKeyTabEntry.keyVersion > k)
/* 485 */               localHashMap.put(Integer.valueOf(localKeyTabEntry.keyType), Integer.valueOf(localKeyTabEntry.keyVersion));
/*     */           }
/*     */           else {
/* 488 */             localHashMap.put(Integer.valueOf(localKeyTabEntry.keyType), Integer.valueOf(localKeyTabEntry.keyVersion));
/*     */           }
/* 490 */         } else if ((paramInt2 == -1) || (localKeyTabEntry.keyVersion == paramInt2)) {
/* 491 */           this.entries.removeElementAt(j);
/* 492 */           i++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 499 */     if (paramInt2 == -2) {
/* 500 */       for (j = this.entries.size() - 1; j >= 0; j--) {
/* 501 */         localKeyTabEntry = (KeyTabEntry)this.entries.get(j);
/* 502 */         if ((paramPrincipalName.match(localKeyTabEntry.getService())) && (
/* 503 */           (paramInt1 == -1) || (localKeyTabEntry.keyType == paramInt1))) {
/* 504 */           k = ((Integer)localHashMap.get(Integer.valueOf(localKeyTabEntry.keyType))).intValue();
/* 505 */           if (localKeyTabEntry.keyVersion != k) {
/* 506 */             this.entries.removeElementAt(j);
/* 507 */             i++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 513 */     return i;
/*     */   }
/*     */ 
/*     */   public synchronized void createVersion(File paramFile)
/*     */     throws IOException
/*     */   {
/* 522 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramFile)); Object localObject1 = null;
/*     */     try {
/* 524 */       localKeyTabOutputStream.write16(1282);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 522 */       localObject1 = localThrowable2; throw localThrowable2;
/*     */     }
/*     */     finally {
/* 525 */       if (localKeyTabOutputStream != null) if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localKeyTabOutputStream.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.ktab.KeyTab
 * JD-Core Version:    0.6.2
 */