/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.EmptyStackException;
/*     */ import java.util.Stack;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.util.KerberosString;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ public class Realm
/*     */   implements Cloneable
/*     */ {
/*     */   private String realm;
/*  52 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */ 
/*     */   private Realm() {
/*     */   }
/*     */ 
/*     */   public Realm(String paramString) throws RealmException {
/*  58 */     this.realm = parseRealm(paramString);
/*     */   }
/*     */ 
/*     */   public Object clone() {
/*  62 */     Realm localRealm = new Realm();
/*  63 */     if (this.realm != null) {
/*  64 */       localRealm.realm = new String(this.realm);
/*     */     }
/*  66 */     return localRealm;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/*  70 */     if (this == paramObject) {
/*  71 */       return true;
/*     */     }
/*     */ 
/*  74 */     if (!(paramObject instanceof Realm)) {
/*  75 */       return false;
/*     */     }
/*     */ 
/*  78 */     Realm localRealm = (Realm)paramObject;
/*  79 */     if ((this.realm != null) && (localRealm.realm != null)) {
/*  80 */       return this.realm.equals(localRealm.realm);
/*     */     }
/*  82 */     return (this.realm == null) && (localRealm.realm == null);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  87 */     int i = 17;
/*     */ 
/*  89 */     if (this.realm != null) {
/*  90 */       i = 37 * i + this.realm.hashCode();
/*     */     }
/*     */ 
/*  93 */     return i;
/*     */   }
/*     */ 
/*     */   public Realm(DerValue paramDerValue)
/*     */     throws Asn1Exception, RealmException, IOException
/*     */   {
/* 105 */     if (paramDerValue == null) {
/* 106 */       throw new IllegalArgumentException("encoding can not be null");
/*     */     }
/* 108 */     this.realm = new KerberosString(paramDerValue).toString();
/* 109 */     if ((this.realm == null) || (this.realm.length() == 0))
/* 110 */       throw new RealmException(601);
/* 111 */     if (!isValidRealmString(this.realm))
/* 112 */       throw new RealmException(600);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 116 */     return this.realm;
/*     */   }
/*     */ 
/*     */   public static String parseRealmAtSeparator(String paramString) throws RealmException
/*     */   {
/* 121 */     if (paramString == null) {
/* 122 */       throw new IllegalArgumentException("null input name is not allowed");
/*     */     }
/*     */ 
/* 125 */     String str1 = new String(paramString);
/* 126 */     String str2 = null;
/* 127 */     int i = 0;
/* 128 */     while (i < str1.length()) {
/* 129 */       if ((str1.charAt(i) == '@') && (
/* 130 */         (i == 0) || (str1.charAt(i - 1) != '\\'))) {
/* 131 */         if (i + 1 >= str1.length()) break;
/* 132 */         str2 = str1.substring(i + 1, str1.length()); break;
/*     */       }
/*     */ 
/* 136 */       i++;
/*     */     }
/* 138 */     if (str2 != null) {
/* 139 */       if (str2.length() == 0)
/* 140 */         throw new RealmException(601);
/* 141 */       if (!isValidRealmString(str2))
/* 142 */         throw new RealmException(600);
/*     */     }
/* 144 */     return str2;
/*     */   }
/*     */ 
/*     */   public static String parseRealmComponent(String paramString) {
/* 148 */     if (paramString == null) {
/* 149 */       throw new IllegalArgumentException("null input name is not allowed");
/*     */     }
/*     */ 
/* 152 */     String str1 = new String(paramString);
/* 153 */     String str2 = null;
/* 154 */     int i = 0;
/* 155 */     while (i < str1.length()) {
/* 156 */       if ((str1.charAt(i) == '.') && (
/* 157 */         (i == 0) || (str1.charAt(i - 1) != '\\'))) {
/* 158 */         if (i + 1 >= str1.length()) break;
/* 159 */         str2 = str1.substring(i + 1, str1.length()); break;
/*     */       }
/*     */ 
/* 163 */       i++;
/*     */     }
/* 165 */     return str2;
/*     */   }
/*     */ 
/*     */   protected static String parseRealm(String paramString) throws RealmException {
/* 169 */     String str = parseRealmAtSeparator(paramString);
/* 170 */     if (str == null)
/* 171 */       str = paramString;
/* 172 */     if ((str == null) || (str.length() == 0))
/* 173 */       throw new RealmException(601);
/* 174 */     if (!isValidRealmString(str))
/* 175 */       throw new RealmException(600);
/* 176 */     return str;
/*     */   }
/*     */ 
/*     */   protected static boolean isValidRealmString(String paramString)
/*     */   {
/* 182 */     if (paramString == null)
/* 183 */       return false;
/* 184 */     if (paramString.length() == 0)
/* 185 */       return false;
/* 186 */     for (int i = 0; i < paramString.length(); i++) {
/* 187 */       if ((paramString.charAt(i) == '/') || (paramString.charAt(i) == ':') || (paramString.charAt(i) == 0))
/*     */       {
/* 190 */         return false;
/*     */       }
/*     */     }
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 204 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 205 */     localDerOutputStream.putDerValue(new KerberosString(this.realm).toDerValue());
/* 206 */     return localDerOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   public static Realm parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 223 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
/* 224 */       return null;
/*     */     }
/* 226 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 227 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 228 */       throw new Asn1Exception(906);
/*     */     }
/* 230 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 231 */     return new Realm(localDerValue2);
/*     */   }
/*     */ 
/*     */   private static String[] doInitialParse(String paramString1, String paramString2)
/*     */     throws KrbException
/*     */   {
/* 240 */     if ((paramString1 == null) || (paramString2 == null)) {
/* 241 */       throw new KrbException(400);
/*     */     }
/* 243 */     if (DEBUG) {
/* 244 */       System.out.println(">>> Realm doInitialParse: cRealm=[" + paramString1 + "], sRealm=[" + paramString2 + "]");
/*     */     }
/*     */ 
/* 247 */     if (paramString1.equals(paramString2)) {
/* 248 */       String[] arrayOfString = null;
/* 249 */       arrayOfString = new String[1];
/* 250 */       arrayOfString[0] = new String(paramString1);
/*     */ 
/* 252 */       if (DEBUG) {
/* 253 */         System.out.println(">>> Realm doInitialParse: " + arrayOfString[0]);
/*     */       }
/*     */ 
/* 256 */       return arrayOfString;
/*     */     }
/* 258 */     return null;
/*     */   }
/*     */ 
/*     */   public static String[] getRealmsList(String paramString1, String paramString2)
/*     */     throws KrbException
/*     */   {
/* 285 */     String[] arrayOfString = doInitialParse(paramString1, paramString2);
/* 286 */     if ((arrayOfString != null) && (arrayOfString.length != 0)) {
/* 287 */       return arrayOfString;
/*     */     }
/*     */ 
/* 292 */     arrayOfString = parseCapaths(paramString1, paramString2);
/* 293 */     if ((arrayOfString != null) && (arrayOfString.length != 0)) {
/* 294 */       return arrayOfString;
/*     */     }
/*     */ 
/* 299 */     arrayOfString = parseHierarchy(paramString1, paramString2);
/* 300 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private static String[] parseCapaths(String paramString1, String paramString2)
/*     */     throws KrbException
/*     */   {
/* 339 */     String[] arrayOfString1 = null;
/*     */ 
/* 341 */     Config localConfig = null;
/*     */     try {
/* 343 */       localConfig = Config.getInstance();
/*     */     } catch (Exception localException) {
/* 345 */       if (DEBUG) {
/* 346 */         System.out.println("Configuration information can not be obtained " + localException.getMessage());
/*     */       }
/*     */ 
/* 349 */       return null;
/*     */     }
/*     */ 
/* 352 */     String str1 = localConfig.getDefault(paramString2, paramString1);
/*     */ 
/* 354 */     if (str1 == null) {
/* 355 */       if (DEBUG) {
/* 356 */         System.out.println(">>> Realm parseCapaths: no cfg entry");
/*     */       }
/* 358 */       return null;
/*     */     }
/*     */ 
/* 361 */     String str2 = null; Object localObject = null;
/* 362 */     Stack localStack = new Stack();
/*     */ 
/* 369 */     Vector localVector = new Vector(8, 8);
/* 370 */     localVector.add(paramString2);
/*     */ 
/* 372 */     int i = 0;
/* 373 */     str2 = paramString2;
/*     */     while (true)
/*     */     {
/* 376 */       if (DEBUG) {
/* 377 */         i++;
/* 378 */         System.out.println(">>> Realm parseCapaths: loop " + i + ": target=" + str2);
/*     */       }
/*     */ 
/* 382 */       if ((str1 != null) && (!str1.equals(".")) && (!str1.equals(paramString1)))
/*     */       {
/* 385 */         if (DEBUG) {
/* 386 */           System.out.println(">>> Realm parseCapaths: loop " + i + ": intermediaries=[" + str1 + "]");
/*     */         }
/*     */ 
/* 399 */         localStack.push(null);
/* 400 */         String[] arrayOfString2 = str1.split("\\s+");
/* 401 */         for (int k = arrayOfString2.length - 1; k >= 0; k--)
/*     */         {
/* 403 */           localObject = arrayOfString2[k];
/* 404 */           if (localObject.equals(".")) {
/*     */             break label564;
/*     */           }
/* 407 */           if (!localVector.contains(localObject)) {
/* 408 */             localStack.push(localObject);
/* 409 */             if (DEBUG) {
/* 410 */               System.out.println(">>> Realm parseCapaths: loop " + i + ": pushed realm on to stack: " + localObject);
/*     */             }
/*     */ 
/*     */           }
/* 415 */           else if (DEBUG) {
/* 416 */             System.out.println(">>> Realm parseCapaths: loop " + i + ": ignoring realm: [" + localObject + "]");
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 423 */         if (!DEBUG) break;
/* 424 */         System.out.println(">>> Realm parseCapaths: loop " + i + ": no intermediaries"); break;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 436 */         while ((str2 = (String)localStack.pop()) == null) {
/* 437 */           localVector.removeElementAt(localVector.size() - 1);
/* 438 */           if (DEBUG)
/* 439 */             System.out.println(">>> Realm parseCapaths: backtrack, remove tail");
/*     */         }
/*     */       }
/*     */       catch (EmptyStackException localEmptyStackException) {
/* 443 */         str2 = null;
/*     */       }
/*     */ 
/* 446 */       if (str2 == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 453 */       localVector.add(str2);
/*     */ 
/* 455 */       if (DEBUG) {
/* 456 */         System.out.println(">>> Realm parseCapaths: loop " + i + ": added intermediary to list: " + str2);
/*     */       }
/*     */ 
/* 461 */       str1 = localConfig.getDefault(str2, paramString1);
/*     */     }
/*     */ 
/* 465 */     label564: if (localVector.isEmpty()) {
/* 466 */       return null;
/*     */     }
/*     */ 
/* 470 */     arrayOfString1 = new String[localVector.size()];
/* 471 */     arrayOfString1[0] = paramString1;
/* 472 */     for (int j = 1; j < localVector.size(); j++) {
/* 473 */       arrayOfString1[j] = ((String)localVector.elementAt(localVector.size() - j));
/*     */     }
/*     */ 
/* 476 */     if ((DEBUG) && (arrayOfString1 != null)) {
/* 477 */       for (j = 0; j < arrayOfString1.length; j++) {
/* 478 */         System.out.println(">>> Realm parseCapaths [" + j + "]=" + arrayOfString1[j]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 483 */     return arrayOfString1;
/*     */   }
/*     */ 
/*     */   private static String[] parseHierarchy(String paramString1, String paramString2)
/*     */     throws KrbException
/*     */   {
/* 498 */     String[] arrayOfString1 = null;
/*     */ 
/* 502 */     String[] arrayOfString2 = null;
/* 503 */     String[] arrayOfString3 = null;
/*     */ 
/* 505 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, ".");
/*     */ 
/* 511 */     int i = localStringTokenizer.countTokens();
/* 512 */     arrayOfString2 = new String[i];
/*     */ 
/* 514 */     for (i = 0; localStringTokenizer.hasMoreTokens(); i++) {
/* 515 */       arrayOfString2[i] = localStringTokenizer.nextToken();
/*     */     }
/*     */ 
/* 518 */     if (DEBUG) {
/* 519 */       System.out.println(">>> Realm parseHierarchy: cRealm has " + i + " components:");
/*     */ 
/* 521 */       j = 0;
/* 522 */       while (j < i) {
/* 523 */         System.out.println(">>> Realm parseHierarchy: cComponents[" + j + "]=" + arrayOfString2[(j++)]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 530 */     localStringTokenizer = new StringTokenizer(paramString2, ".");
/*     */ 
/* 533 */     int j = localStringTokenizer.countTokens();
/* 534 */     arrayOfString3 = new String[j];
/*     */ 
/* 536 */     for (j = 0; localStringTokenizer.hasMoreTokens(); j++) {
/* 537 */       arrayOfString3[j] = localStringTokenizer.nextToken();
/*     */     }
/*     */ 
/* 540 */     if (DEBUG) {
/* 541 */       System.out.println(">>> Realm parseHierarchy: sRealm has " + j + " components:");
/*     */ 
/* 543 */       k = 0;
/* 544 */       while (k < j) {
/* 545 */         System.out.println(">>> Realm parseHierarchy: sComponents[" + k + "]=" + arrayOfString3[(k++)]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 552 */     int k = 0;
/*     */ 
/* 557 */     j--; for (i--; (j >= 0) && (i >= 0) && (arrayOfString3[j].equals(arrayOfString2[i])); 
/* 559 */       i--) {
/* 560 */       k++;
/*     */ 
/* 559 */       j--;
/*     */     }
/*     */ 
/* 563 */     int m = -1;
/* 564 */     int n = -1;
/*     */ 
/* 566 */     int i1 = 0;
/*     */ 
/* 568 */     if (k > 0) {
/* 569 */       n = j + 1;
/* 570 */       m = i + 1;
/*     */ 
/* 573 */       i1 += n;
/* 574 */       i1 += m;
/*     */     } else {
/* 576 */       i1++;
/*     */     }
/*     */ 
/* 579 */     if (DEBUG) {
/* 580 */       if (k > 0) {
/* 581 */         System.out.println(">>> Realm parseHierarchy: " + k + " common component" + (k > 1 ? "s" : " "));
/*     */ 
/* 585 */         System.out.println(">>> Realm parseHierarchy: common part in cRealm (starts at index " + m + ")");
/*     */ 
/* 589 */         System.out.println(">>> Realm parseHierarchy: common part in sRealm (starts at index " + n + ")");
/*     */ 
/* 593 */         str1 = substring(paramString1, m);
/* 594 */         System.out.println(">>> Realm parseHierarchy: common part in cRealm=" + str1);
/*     */ 
/* 597 */         str1 = substring(paramString2, n);
/* 598 */         System.out.println(">>> Realm parseHierarchy: common part in sRealm=" + str1);
/*     */       }
/*     */       else
/*     */       {
/* 602 */         System.out.println(">>> Realm parseHierarchy: no common part");
/*     */       }
/*     */     }
/* 605 */     if (DEBUG) {
/* 606 */       System.out.println(">>> Realm parseHierarchy: total links=" + i1);
/*     */     }
/*     */ 
/* 609 */     arrayOfString1 = new String[i1];
/*     */ 
/* 611 */     arrayOfString1[0] = new String(paramString1);
/*     */ 
/* 613 */     if (DEBUG) {
/* 614 */       System.out.println(">>> Realm parseHierarchy A: retList[0]=" + arrayOfString1[0]);
/*     */     }
/*     */ 
/* 623 */     String str1 = null; String str2 = null;
/*     */ 
/* 625 */     int i2 = 1; for (i = 0; (i2 < i1) && (i < m); i++) {
/* 626 */       str2 = substring(paramString1, i + 1);
/*     */ 
/* 628 */       arrayOfString1[(i2++)] = new String(str2);
/*     */ 
/* 630 */       if (DEBUG) {
/* 631 */         System.out.println(">>> Realm parseHierarchy B: retList[" + (i2 - 1) + "]=" + arrayOfString1[(i2 - 1)]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 637 */     for (j = n; (i2 < i1) && (j - 1 > 0); j--) {
/* 638 */       str2 = substring(paramString2, j - 1);
/*     */ 
/* 640 */       arrayOfString1[(i2++)] = new String(str2);
/* 641 */       if (DEBUG) {
/* 642 */         System.out.println(">>> Realm parseHierarchy D: retList[" + (i2 - 1) + "]=" + arrayOfString1[(i2 - 1)]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 647 */     return arrayOfString1;
/*     */   }
/*     */ 
/*     */   private static String substring(String paramString, int paramInt)
/*     */   {
/* 652 */     int i = 0; int j = 0; int k = paramString.length();
/*     */ 
/* 654 */     while ((i < k) && (j != paramInt)) {
/* 655 */       if (paramString.charAt(i++) == '.')
/*     */       {
/* 657 */         j++;
/*     */       }
/*     */     }
/* 660 */     return paramString.substring(i);
/*     */   }
/*     */ 
/*     */   static int getRandIndex(int paramInt) {
/* 664 */     return (int)(Math.random() * 16384.0D) % paramInt;
/*     */   }
/*     */ 
/*     */   static void printNames(String[] paramArrayOfString) {
/* 668 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/* 669 */       return;
/*     */     }
/* 671 */     int i = paramArrayOfString.length;
/* 672 */     int j = 0;
/* 673 */     System.out.println("List length = " + i);
/* 674 */     while (j < paramArrayOfString.length) {
/* 675 */       System.out.println("[" + j + "]=" + paramArrayOfString[j]);
/* 676 */       j++;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.Realm
 * JD-Core Version:    0.6.2
 */