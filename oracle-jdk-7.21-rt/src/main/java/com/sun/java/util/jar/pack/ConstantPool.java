/*      */ package com.sun.java.util.jar.pack;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.util.AbstractList;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ abstract class ConstantPool
/*      */ {
/*      */   protected static final Entry[] noRefs;
/*      */   protected static final ClassEntry[] noClassRefs;
/*      */   static final byte[] TAGS_IN_ORDER;
/*      */   static final byte[] TAG_ORDER;
/*      */ 
/*      */   static int verbose()
/*      */   {
/*   47 */     return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
/*      */   }
/*      */ 
/*      */   public static synchronized Utf8Entry getUtf8Entry(String paramString)
/*      */   {
/*   55 */     Map localMap = Utils.getUtf8Entries();
/*   56 */     Utf8Entry localUtf8Entry = (Utf8Entry)localMap.get(paramString);
/*   57 */     if (localUtf8Entry == null) {
/*   58 */       localUtf8Entry = new Utf8Entry(paramString);
/*   59 */       localMap.put(localUtf8Entry.stringValue(), localUtf8Entry);
/*      */     }
/*   61 */     return localUtf8Entry;
/*      */   }
/*      */ 
/*      */   public static synchronized ClassEntry getClassEntry(String paramString) {
/*   65 */     Map localMap = Utils.getClassEntries();
/*   66 */     ClassEntry localClassEntry = (ClassEntry)localMap.get(paramString);
/*   67 */     if (localClassEntry == null) {
/*   68 */       localClassEntry = new ClassEntry(getUtf8Entry(paramString));
/*   69 */       assert (paramString.equals(localClassEntry.stringValue()));
/*   70 */       localMap.put(localClassEntry.stringValue(), localClassEntry);
/*      */     }
/*   72 */     return localClassEntry;
/*      */   }
/*      */ 
/*      */   public static synchronized LiteralEntry getLiteralEntry(Comparable paramComparable) {
/*   76 */     Map localMap = Utils.getLiteralEntries();
/*   77 */     Object localObject = (LiteralEntry)localMap.get(paramComparable);
/*   78 */     if (localObject == null) {
/*   79 */       if ((paramComparable instanceof String))
/*   80 */         localObject = new StringEntry(getUtf8Entry((String)paramComparable));
/*      */       else
/*   82 */         localObject = new NumberEntry((Number)paramComparable);
/*   83 */       localMap.put(paramComparable, localObject);
/*      */     }
/*   85 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static synchronized StringEntry getStringEntry(String paramString) {
/*   89 */     return (StringEntry)getLiteralEntry(paramString);
/*      */   }
/*      */ 
/*      */   public static synchronized SignatureEntry getSignatureEntry(String paramString)
/*      */   {
/*   94 */     Map localMap = Utils.getSignatureEntries();
/*   95 */     SignatureEntry localSignatureEntry = (SignatureEntry)localMap.get(paramString);
/*   96 */     if (localSignatureEntry == null) {
/*   97 */       localSignatureEntry = new SignatureEntry(paramString);
/*   98 */       assert (localSignatureEntry.stringValue().equals(paramString));
/*   99 */       localMap.put(paramString, localSignatureEntry);
/*      */     }
/*  101 */     return localSignatureEntry;
/*      */   }
/*      */ 
/*      */   public static SignatureEntry getSignatureEntry(Utf8Entry paramUtf8Entry, ClassEntry[] paramArrayOfClassEntry) {
/*  105 */     return getSignatureEntry(SignatureEntry.stringValueOf(paramUtf8Entry, paramArrayOfClassEntry));
/*      */   }
/*      */ 
/*      */   public static synchronized DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry, SignatureEntry paramSignatureEntry)
/*      */   {
/*  110 */     Map localMap = Utils.getDescriptorEntries();
/*  111 */     String str = DescriptorEntry.stringValueOf(paramUtf8Entry, paramSignatureEntry);
/*  112 */     DescriptorEntry localDescriptorEntry = (DescriptorEntry)localMap.get(str);
/*  113 */     if (localDescriptorEntry == null) {
/*  114 */       localDescriptorEntry = new DescriptorEntry(paramUtf8Entry, paramSignatureEntry);
/*      */ 
/*  116 */       assert (localDescriptorEntry.stringValue().equals(str)) : (localDescriptorEntry.stringValue() + " != " + str);
/*  117 */       localMap.put(str, localDescriptorEntry);
/*      */     }
/*  119 */     return localDescriptorEntry;
/*      */   }
/*      */ 
/*      */   public static DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry1, Utf8Entry paramUtf8Entry2) {
/*  123 */     return getDescriptorEntry(paramUtf8Entry1, getSignatureEntry(paramUtf8Entry2.stringValue()));
/*      */   }
/*      */ 
/*      */   public static synchronized MemberEntry getMemberEntry(byte paramByte, ClassEntry paramClassEntry, DescriptorEntry paramDescriptorEntry)
/*      */   {
/*  128 */     Map localMap = Utils.getMemberEntries();
/*  129 */     String str = MemberEntry.stringValueOf(paramByte, paramClassEntry, paramDescriptorEntry);
/*  130 */     MemberEntry localMemberEntry = (MemberEntry)localMap.get(str);
/*  131 */     if (localMemberEntry == null) {
/*  132 */       localMemberEntry = new MemberEntry(paramByte, paramClassEntry, paramDescriptorEntry);
/*      */ 
/*  134 */       assert (localMemberEntry.stringValue().equals(str)) : (localMemberEntry.stringValue() + " != " + str);
/*  135 */       localMap.put(str, localMemberEntry);
/*      */     }
/*  137 */     return localMemberEntry;
/*      */   }
/*      */ 
/*      */   static boolean isMemberTag(byte paramByte)
/*      */   {
/*  237 */     switch (paramByte) {
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*  241 */       return true;
/*      */     }
/*  243 */     return false;
/*      */   }
/*      */ 
/*      */   static byte numberTagOf(Number paramNumber) {
/*  247 */     if ((paramNumber instanceof Integer)) return 3;
/*  248 */     if ((paramNumber instanceof Float)) return 4;
/*  249 */     if ((paramNumber instanceof Long)) return 5;
/*  250 */     if ((paramNumber instanceof Double)) return 6;
/*  251 */     throw new RuntimeException("bad literal value " + paramNumber);
/*      */   }
/*      */ 
/*      */   static int compareSignatures(String paramString1, String paramString2)
/*      */   {
/*  610 */     return compareSignatures(paramString1, paramString2, null, null);
/*      */   }
/*      */ 
/*      */   static int compareSignatures(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
/*      */   {
/*  615 */     int i = paramString1.charAt(0);
/*  616 */     int j = paramString2.charAt(0);
/*      */ 
/*  618 */     if ((i != 40) && (j == 40)) return -1;
/*  619 */     if ((j != 40) && (i == 40)) return 1;
/*  620 */     if (paramArrayOfString1 == null) paramArrayOfString1 = structureSignature(paramString1);
/*  621 */     if (paramArrayOfString2 == null) paramArrayOfString2 = structureSignature(paramString2);
/*      */ 
/*  629 */     if (paramArrayOfString1.length != paramArrayOfString2.length) return paramArrayOfString1.length - paramArrayOfString2.length;
/*  630 */     int k = paramArrayOfString1.length;
/*  631 */     int m = k;
/*      */     while (true) { m--; if (m < 0) break;
/*  632 */       int n = paramArrayOfString1[m].compareTo(paramArrayOfString2[m]);
/*  633 */       if (n != 0) return n;
/*      */     }
/*  635 */     assert (paramString1.equals(paramString2));
/*  636 */     return 0;
/*      */   }
/*      */ 
/*      */   static int countClassParts(Utf8Entry paramUtf8Entry) {
/*  640 */     int i = 0;
/*  641 */     String str = paramUtf8Entry.stringValue();
/*  642 */     for (int j = 0; j < str.length(); j++) {
/*  643 */       if (str.charAt(j) == 'L') i++;
/*      */     }
/*  645 */     return i;
/*      */   }
/*      */ 
/*      */   static String flattenSignature(String[] paramArrayOfString) {
/*  649 */     String str1 = paramArrayOfString[0];
/*  650 */     if (paramArrayOfString.length == 1) return str1;
/*  651 */     int i = str1.length();
/*  652 */     for (int j = 1; j < paramArrayOfString.length; j++) {
/*  653 */       i += paramArrayOfString[j].length();
/*      */     }
/*  655 */     char[] arrayOfChar = new char[i];
/*  656 */     int k = 0;
/*  657 */     int m = 1;
/*  658 */     for (int n = 0; n < str1.length(); n++) {
/*  659 */       int i1 = str1.charAt(n);
/*  660 */       arrayOfChar[(k++)] = i1;
/*  661 */       if (i1 == 76) {
/*  662 */         String str2 = paramArrayOfString[(m++)];
/*  663 */         str2.getChars(0, str2.length(), arrayOfChar, k);
/*  664 */         k += str2.length();
/*      */       }
/*      */     }
/*      */ 
/*  668 */     assert (k == i);
/*  669 */     assert (m == paramArrayOfString.length);
/*  670 */     return new String(arrayOfChar);
/*      */   }
/*      */ 
/*      */   private static int skipClassNameChars(String paramString, int paramInt) {
/*  674 */     int i = paramString.length();
/*  675 */     for (; paramInt < i; paramInt++) {
/*  676 */       int j = paramString.charAt(paramInt);
/*  677 */       if ((j <= 32) || (
/*  678 */         (j >= 59) && (j <= 64))) break;
/*      */     }
/*  680 */     return paramInt;
/*      */   }
/*      */ 
/*      */   static String[] structureSignature(String paramString) {
/*  684 */     paramString = paramString.intern();
/*      */ 
/*  686 */     int i = 0;
/*  687 */     int j = 1;
/*  688 */     for (int k = 0; k < paramString.length(); k++) {
/*  689 */       int m = paramString.charAt(k);
/*  690 */       i++;
/*  691 */       if (m == 76) {
/*  692 */         j++;
/*  693 */         n = skipClassNameChars(paramString, k + 1);
/*  694 */         k = n - 1;
/*  695 */         i1 = paramString.indexOf('<', k + 1);
/*  696 */         if ((i1 > 0) && (i1 < n))
/*  697 */           k = i1 - 1;
/*      */       }
/*      */     }
/*  700 */     char[] arrayOfChar = new char[i];
/*  701 */     if (j == 1) {
/*  702 */       arrayOfString = new String[] { paramString };
/*  703 */       return arrayOfString;
/*      */     }
/*  705 */     String[] arrayOfString = new String[j];
/*  706 */     int n = 0;
/*  707 */     int i1 = 1;
/*  708 */     for (int i2 = 0; i2 < paramString.length(); i2++) {
/*  709 */       int i3 = paramString.charAt(i2);
/*  710 */       arrayOfChar[(n++)] = i3;
/*  711 */       if (i3 == 76) {
/*  712 */         int i4 = skipClassNameChars(paramString, i2 + 1);
/*  713 */         arrayOfString[(i1++)] = paramString.substring(i2 + 1, i4);
/*  714 */         i2 = i4;
/*  715 */         i2--;
/*      */       }
/*      */     }
/*  718 */     assert (n == i);
/*  719 */     assert (i1 == arrayOfString.length);
/*  720 */     arrayOfString[0] = new String(arrayOfChar);
/*      */ 
/*  722 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   public static Index makeIndex(String paramString, Entry[] paramArrayOfEntry)
/*      */   {
/*  896 */     return new Index(paramString, paramArrayOfEntry);
/*      */   }
/*      */ 
/*      */   public static Index makeIndex(String paramString, Collection<Entry> paramCollection)
/*      */   {
/*  901 */     return new Index(paramString, paramCollection);
/*      */   }
/*      */ 
/*      */   public static void sort(Index paramIndex)
/*      */   {
/*  908 */     paramIndex.clearIndex();
/*  909 */     Arrays.sort(paramIndex.cpMap);
/*  910 */     if (verbose() > 2)
/*  911 */       System.out.println("sorted " + paramIndex.dumpString());
/*      */   }
/*      */ 
/*      */   public static Index[] partition(Index paramIndex, int[] paramArrayOfInt)
/*      */   {
/*  922 */     ArrayList localArrayList = new ArrayList();
/*  923 */     Entry[] arrayOfEntry = paramIndex.cpMap;
/*  924 */     assert (paramArrayOfInt.length == arrayOfEntry.length);
/*      */     Object localObject;
/*  925 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/*  926 */       j = paramArrayOfInt[i];
/*  927 */       if (j >= 0) {
/*  928 */         while (j >= localArrayList.size()) {
/*  929 */           localArrayList.add(null);
/*      */         }
/*  931 */         localObject = (List)localArrayList.get(j);
/*  932 */         if (localObject == null) {
/*  933 */           localArrayList.set(j, localObject = new ArrayList());
/*      */         }
/*  935 */         ((List)localObject).add(arrayOfEntry[i]);
/*      */       }
/*      */     }
/*  937 */     Index[] arrayOfIndex = new Index[localArrayList.size()];
/*  938 */     for (int j = 0; j < arrayOfIndex.length; j++) {
/*  939 */       localObject = (List)localArrayList.get(j);
/*  940 */       if (localObject != null) {
/*  941 */         arrayOfIndex[j] = new Index(paramIndex.debugName + "/part#" + j, (Collection)localObject);
/*  942 */         assert (arrayOfIndex[j].indexOf((Entry)((List)localObject).get(0)) == 0);
/*      */       }
/*      */     }
/*  944 */     return arrayOfIndex;
/*      */   }
/*      */ 
/*      */   public static Index[] partitionByTag(Index paramIndex)
/*      */   {
/*  949 */     Entry[] arrayOfEntry = paramIndex.cpMap;
/*  950 */     int[] arrayOfInt = new int[arrayOfEntry.length];
/*  951 */     for (int i = 0; i < arrayOfInt.length; i++) {
/*  952 */       Entry localEntry = arrayOfEntry[i];
/*  953 */       arrayOfInt[i] = (localEntry == null ? -1 : localEntry.tag);
/*      */     }
/*  955 */     Object localObject = partition(paramIndex, arrayOfInt);
/*  956 */     for (int j = 0; j < localObject.length; j++) {
/*  957 */       if (localObject[j] != null)
/*  958 */         localObject[j].debugName = tagName(j);
/*      */     }
/*  960 */     if (localObject.length < 14) {
/*  961 */       Index[] arrayOfIndex = new Index[14];
/*  962 */       System.arraycopy(localObject, 0, arrayOfIndex, 0, localObject.length);
/*  963 */       localObject = arrayOfIndex;
/*      */     }
/*  965 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static void completeReferencesIn(Set<Entry> paramSet, boolean paramBoolean)
/*      */   {
/* 1140 */     paramSet.remove(null);
/* 1141 */     ListIterator localListIterator = new ArrayList(paramSet).listIterator(paramSet.size());
/*      */ 
/* 1143 */     while (localListIterator.hasPrevious()) {
/* 1144 */       Object localObject1 = (Entry)localListIterator.previous();
/* 1145 */       localListIterator.remove();
/* 1146 */       assert (localObject1 != null);
/*      */       Object localObject2;
/* 1147 */       if ((paramBoolean) && (((Entry)localObject1).tag == 13)) {
/* 1148 */         SignatureEntry localSignatureEntry = (SignatureEntry)localObject1;
/* 1149 */         localObject2 = localSignatureEntry.asUtf8Entry();
/*      */ 
/* 1151 */         paramSet.remove(localSignatureEntry);
/* 1152 */         paramSet.add(localObject2);
/* 1153 */         localObject1 = localObject2;
/*      */       }
/*      */ 
/* 1156 */       for (int i = 0; ; i++) {
/* 1157 */         localObject2 = ((Entry)localObject1).getRef(i);
/* 1158 */         if (localObject2 == null)
/*      */           break;
/* 1160 */         if (paramSet.add(localObject2))
/* 1161 */           localListIterator.add(localObject2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static double percent(int paramInt1, int paramInt2) {
/* 1167 */     return (int)(10000.0D * paramInt1 / paramInt2 + 0.5D) / 100.0D;
/*      */   }
/*      */ 
/*      */   public static String tagName(int paramInt) {
/* 1171 */     switch (paramInt) { case 1:
/* 1172 */       return "Utf8";
/*      */     case 3:
/* 1173 */       return "Integer";
/*      */     case 4:
/* 1174 */       return "Float";
/*      */     case 5:
/* 1175 */       return "Long";
/*      */     case 6:
/* 1176 */       return "Double";
/*      */     case 7:
/* 1177 */       return "Class";
/*      */     case 8:
/* 1178 */       return "String";
/*      */     case 9:
/* 1179 */       return "Fieldref";
/*      */     case 10:
/* 1180 */       return "Methodref";
/*      */     case 11:
/* 1181 */       return "InterfaceMethodref";
/*      */     case 12:
/* 1182 */       return "NameandType";
/*      */     case 19:
/* 1185 */       return "*All";
/*      */     case 0:
/* 1186 */       return "*None";
/*      */     case 13:
/* 1187 */       return "*Signature";
/*      */     case 2:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/* 1189 */     case 18: } return "tag#" + paramInt;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  726 */     noRefs = new Entry[0];
/*  727 */     noClassRefs = new ClassEntry[0];
/*      */ 
/* 1193 */     TAGS_IN_ORDER = new byte[] { 1, 3, 4, 5, 6, 8, 7, 13, 12, 9, 10, 11 };
/*      */ 
/* 1209 */     TAG_ORDER = new byte[14];
/* 1210 */     for (int i = 0; i < TAGS_IN_ORDER.length; i++)
/* 1211 */       TAG_ORDER[TAGS_IN_ORDER[i]] = ((byte)(i + 1));
/*      */   }
/*      */ 
/*      */   public static class ClassEntry extends ConstantPool.Entry
/*      */   {
/*      */     final ConstantPool.Utf8Entry ref;
/*      */ 
/*      */     public ConstantPool.Entry getRef(int paramInt)
/*      */     {
/*  333 */       return paramInt == 0 ? this.ref : null;
/*      */     }
/*      */     protected int computeValueHash() {
/*  336 */       return this.ref.hashCode() + this.tag;
/*      */     }
/*      */     ClassEntry(ConstantPool.Entry paramEntry) {
/*  339 */       super();
/*  340 */       this.ref = ((ConstantPool.Utf8Entry)paramEntry);
/*  341 */       hashCode();
/*      */     }
/*      */     public boolean equals(Object paramObject) {
/*  344 */       return (paramObject != null) && (paramObject.getClass() == ClassEntry.class) && (((ClassEntry)paramObject).ref.eq(this.ref));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  348 */       int i = superCompareTo(paramObject);
/*  349 */       if (i == 0) {
/*  350 */         i = this.ref.compareTo(((ClassEntry)paramObject).ref);
/*      */       }
/*  352 */       return i;
/*      */     }
/*      */     public String stringValue() {
/*  355 */       return this.ref.stringValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class DescriptorEntry extends ConstantPool.Entry {
/*      */     final ConstantPool.Utf8Entry nameRef;
/*      */     final ConstantPool.SignatureEntry typeRef;
/*      */ 
/*  364 */     public ConstantPool.Entry getRef(int paramInt) { if (paramInt == 0) return this.nameRef;
/*  365 */       if (paramInt == 1) return this.typeRef;
/*  366 */       return null; }
/*      */ 
/*      */     DescriptorEntry(ConstantPool.Entry paramEntry1, ConstantPool.Entry paramEntry2) {
/*  369 */       super();
/*  370 */       if ((paramEntry2 instanceof ConstantPool.Utf8Entry)) {
/*  371 */         paramEntry2 = ConstantPool.getSignatureEntry(paramEntry2.stringValue());
/*      */       }
/*  373 */       this.nameRef = ((ConstantPool.Utf8Entry)paramEntry1);
/*  374 */       this.typeRef = ((ConstantPool.SignatureEntry)paramEntry2);
/*  375 */       hashCode();
/*      */     }
/*      */     protected int computeValueHash() {
/*  378 */       int i = this.typeRef.hashCode();
/*  379 */       return this.nameRef.hashCode() + (i << 8) ^ i;
/*      */     }
/*      */     public boolean equals(Object paramObject) {
/*  382 */       if ((paramObject == null) || (paramObject.getClass() != DescriptorEntry.class)) {
/*  383 */         return false;
/*      */       }
/*  385 */       DescriptorEntry localDescriptorEntry = (DescriptorEntry)paramObject;
/*  386 */       return (this.nameRef.eq(localDescriptorEntry.nameRef)) && (this.typeRef.eq(localDescriptorEntry.typeRef));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  390 */       int i = superCompareTo(paramObject);
/*  391 */       if (i == 0) {
/*  392 */         DescriptorEntry localDescriptorEntry = (DescriptorEntry)paramObject;
/*      */ 
/*  394 */         i = this.typeRef.compareTo(localDescriptorEntry.typeRef);
/*  395 */         if (i == 0)
/*  396 */           i = this.nameRef.compareTo(localDescriptorEntry.nameRef);
/*      */       }
/*  398 */       return i;
/*      */     }
/*      */     public String stringValue() {
/*  401 */       return stringValueOf(this.nameRef, this.typeRef);
/*      */     }
/*      */ 
/*      */     static String stringValueOf(ConstantPool.Entry paramEntry1, ConstantPool.Entry paramEntry2) {
/*  405 */       return paramEntry2.stringValue() + "," + paramEntry1.stringValue();
/*      */     }
/*      */ 
/*      */     public String prettyString() {
/*  409 */       return this.nameRef.stringValue() + this.typeRef.prettyString();
/*      */     }
/*      */ 
/*      */     public boolean isMethod() {
/*  413 */       return this.typeRef.isMethod();
/*      */     }
/*      */ 
/*      */     public byte getLiteralTag() {
/*  417 */       return this.typeRef.getLiteralTag();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class Entry
/*      */     implements Comparable
/*      */   {
/*      */     protected final byte tag;
/*      */     protected int valueHash;
/*      */ 
/*      */     protected Entry(byte paramByte)
/*      */     {
/*  148 */       this.tag = paramByte;
/*      */     }
/*      */ 
/*      */     public final byte getTag() {
/*  152 */       return this.tag;
/*      */     }
/*      */ 
/*      */     public Entry getRef(int paramInt) {
/*  156 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean eq(Entry paramEntry) {
/*  160 */       assert (paramEntry != null);
/*  161 */       return (this == paramEntry) || (equals(paramEntry));
/*      */     }
/*      */ 
/*      */     public abstract boolean equals(Object paramObject);
/*      */ 
/*      */     public final int hashCode() {
/*  167 */       if (this.valueHash == 0) {
/*  168 */         this.valueHash = computeValueHash();
/*  169 */         if (this.valueHash == 0) this.valueHash = 1;
/*      */       }
/*  171 */       return this.valueHash;
/*      */     }
/*      */     protected abstract int computeValueHash();
/*      */ 
/*      */     public abstract int compareTo(Object paramObject);
/*      */ 
/*      */     protected int superCompareTo(Object paramObject) {
/*  178 */       Entry localEntry = (Entry)paramObject;
/*      */ 
/*  180 */       if (this.tag != localEntry.tag) {
/*  181 */         return ConstantPool.TAG_ORDER[this.tag] - ConstantPool.TAG_ORDER[localEntry.tag];
/*      */       }
/*      */ 
/*  184 */       return 0;
/*      */     }
/*      */ 
/*      */     public final boolean isDoubleWord() {
/*  188 */       return (this.tag == 6) || (this.tag == 5);
/*      */     }
/*      */ 
/*      */     public final boolean tagMatches(int paramInt) {
/*  192 */       return this.tag == paramInt;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  196 */       String str = stringValue();
/*  197 */       if (ConstantPool.verbose() > 4) {
/*  198 */         if (this.valueHash != 0)
/*  199 */           str = str + " hash=" + this.valueHash;
/*  200 */         str = str + " id=" + System.identityHashCode(this);
/*      */       }
/*  202 */       return ConstantPool.tagName(this.tag) + "=" + str;
/*      */     }
/*      */ 
/*      */     public abstract String stringValue();
/*      */   }
/*      */ 
/*      */   public static final class Index extends AbstractList
/*      */   {
/*      */     protected String debugName;
/*      */     protected ConstantPool.Entry[] cpMap;
/*      */     protected boolean flattenSigs;
/*      */     protected ConstantPool.Entry[] indexKey;
/*      */     protected int[] indexValue;
/*      */ 
/*      */     protected ConstantPool.Entry[] getMap()
/*      */     {
/*  736 */       return this.cpMap;
/*      */     }
/*      */     protected Index(String paramString) {
/*  739 */       this.debugName = paramString;
/*      */     }
/*      */     protected Index(String paramString, ConstantPool.Entry[] paramArrayOfEntry) {
/*  742 */       this(paramString);
/*  743 */       setMap(paramArrayOfEntry);
/*      */     }
/*      */     protected void setMap(ConstantPool.Entry[] paramArrayOfEntry) {
/*  746 */       clearIndex();
/*  747 */       this.cpMap = paramArrayOfEntry;
/*      */     }
/*      */     protected Index(String paramString, Collection<ConstantPool.Entry> paramCollection) {
/*  750 */       this(paramString);
/*  751 */       setMap(paramCollection);
/*      */     }
/*      */     protected void setMap(Collection<ConstantPool.Entry> paramCollection) {
/*  754 */       this.cpMap = new ConstantPool.Entry[paramCollection.size()];
/*  755 */       paramCollection.toArray(this.cpMap);
/*  756 */       setMap(this.cpMap);
/*      */     }
/*      */     public int size() {
/*  759 */       return this.cpMap.length;
/*      */     }
/*      */     public Object get(int paramInt) {
/*  762 */       return this.cpMap[paramInt];
/*      */     }
/*      */ 
/*      */     public ConstantPool.Entry getEntry(int paramInt) {
/*  766 */       return this.cpMap[paramInt];
/*      */     }
/*      */ 
/*      */     private int findIndexOf(ConstantPool.Entry paramEntry)
/*      */     {
/*  777 */       if (this.indexKey == null) {
/*  778 */         initializeIndex();
/*      */       }
/*  780 */       int i = findIndexLocation(paramEntry);
/*  781 */       if (this.indexKey[i] != paramEntry) {
/*  782 */         if ((this.flattenSigs) && (paramEntry.tag == 13)) {
/*  783 */           ConstantPool.SignatureEntry localSignatureEntry = (ConstantPool.SignatureEntry)paramEntry;
/*  784 */           return findIndexOf(localSignatureEntry.asUtf8Entry());
/*      */         }
/*  786 */         return -1;
/*      */       }
/*  788 */       int j = this.indexValue[i];
/*  789 */       assert (paramEntry.equals(this.cpMap[j]));
/*  790 */       return j;
/*      */     }
/*      */     public boolean contains(ConstantPool.Entry paramEntry) {
/*  793 */       return findIndexOf(paramEntry) >= 0;
/*      */     }
/*      */ 
/*      */     public int indexOf(ConstantPool.Entry paramEntry) {
/*  797 */       int i = findIndexOf(paramEntry);
/*  798 */       if ((i < 0) && (ConstantPool.verbose() > 0)) {
/*  799 */         System.out.println("not found: " + paramEntry);
/*  800 */         System.out.println("       in: " + dumpString());
/*  801 */         Thread.dumpStack();
/*      */       }
/*  803 */       assert (i >= 0);
/*  804 */       return i;
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/*  807 */       return findIndexOf((ConstantPool.Entry)paramObject) >= 0;
/*      */     }
/*      */     public int indexOf(Object paramObject) {
/*  810 */       return findIndexOf((ConstantPool.Entry)paramObject);
/*      */     }
/*      */     public int lastIndexOf(Object paramObject) {
/*  813 */       return indexOf(paramObject);
/*      */     }
/*      */ 
/*      */     public boolean assertIsSorted() {
/*  817 */       for (int i = 1; i < this.cpMap.length; i++) {
/*  818 */         if (this.cpMap[(i - 1)].compareTo(this.cpMap[i]) > 0) {
/*  819 */           System.out.println("Not sorted at " + (i - 1) + "/" + i + ": " + dumpString());
/*  820 */           return false;
/*      */         }
/*      */       }
/*  823 */       return true;
/*      */     }
/*      */ 
/*      */     protected void clearIndex()
/*      */     {
/*  830 */       this.indexKey = null;
/*  831 */       this.indexValue = null;
/*      */     }
/*      */     private int findIndexLocation(ConstantPool.Entry paramEntry) {
/*  834 */       int i = this.indexKey.length;
/*  835 */       int j = paramEntry.hashCode();
/*  836 */       int k = j & i - 1;
/*  837 */       int m = (j >>> 8 | 0x1) & i - 1;
/*      */       while (true) {
/*  839 */         ConstantPool.Entry localEntry = this.indexKey[k];
/*  840 */         if ((localEntry == paramEntry) || (localEntry == null))
/*  841 */           return k;
/*  842 */         k += m;
/*  843 */         if (k >= i) k -= i; 
/*      */       }
/*      */     }
/*      */ 
/*  847 */     private void initializeIndex() { if (ConstantPool.verbose() > 2)
/*  848 */         System.out.println("initialize Index " + this.debugName + " [" + size() + "]");
/*  849 */       int i = (int)((this.cpMap.length + 10) * 1.5D);
/*  850 */       int j = 1;
/*  851 */       while (j < i) {
/*  852 */         j <<= 1;
/*      */       }
/*  854 */       this.indexKey = new ConstantPool.Entry[j];
/*  855 */       this.indexValue = new int[j];
/*  856 */       for (int k = 0; k < this.cpMap.length; k++) {
/*  857 */         ConstantPool.Entry localEntry = this.cpMap[k];
/*  858 */         if (localEntry != null) {
/*  859 */           int m = findIndexLocation(localEntry);
/*  860 */           assert (this.indexKey[m] == null);
/*  861 */           this.indexKey[m] = localEntry;
/*  862 */           this.indexValue[m] = k;
/*      */         }
/*      */       } } 
/*      */     public Object[] toArray(Object[] paramArrayOfObject) {
/*  866 */       int i = size();
/*  867 */       if (paramArrayOfObject.length < i) return super.toArray(paramArrayOfObject);
/*  868 */       System.arraycopy(this.cpMap, 0, paramArrayOfObject, 0, i);
/*  869 */       if (paramArrayOfObject.length > i) paramArrayOfObject[i] = null;
/*  870 */       return paramArrayOfObject;
/*      */     }
/*      */     public Object[] toArray() {
/*  873 */       return toArray(new ConstantPool.Entry[size()]);
/*      */     }
/*      */     public Object clone() {
/*  876 */       return new Index(this.debugName, (ConstantPool.Entry[])this.cpMap.clone());
/*      */     }
/*      */     public String toString() {
/*  879 */       return "Index " + this.debugName + " [" + size() + "]";
/*      */     }
/*      */     public String dumpString() {
/*  882 */       String str = toString();
/*  883 */       str = str + " {\n";
/*  884 */       for (int i = 0; i < this.cpMap.length; i++) {
/*  885 */         str = str + "    " + i + ": " + this.cpMap[i] + "\n";
/*      */       }
/*  887 */       str = str + "}";
/*  888 */       return str;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class IndexGroup
/*      */   {
/*      */     private ConstantPool.Index indexUntyped;
/*  972 */     private ConstantPool.Index[] indexByTag = new ConstantPool.Index[14];
/*      */     private int[] untypedFirstIndexByTag;
/*      */     private int totalSize;
/*      */     private ConstantPool.Index[][] indexByTagAndClass;
/*      */ 
/*      */     public ConstantPool.Index getUntypedIndex()
/*      */     {
/*  979 */       if (this.indexUntyped == null) {
/*  980 */         untypedIndexOf(null);
/*  981 */         ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[this.totalSize];
/*  982 */         for (int i = 0; i < this.indexByTag.length; i++) {
/*  983 */           ConstantPool.Index localIndex = this.indexByTag[i];
/*  984 */           if (localIndex != null) {
/*  985 */             int j = localIndex.cpMap.length;
/*  986 */             if (j != 0) {
/*  987 */               int k = this.untypedFirstIndexByTag[i];
/*  988 */               assert (arrayOfEntry[k] == null);
/*  989 */               assert (arrayOfEntry[(k + j - 1)] == null);
/*  990 */               System.arraycopy(localIndex.cpMap, 0, arrayOfEntry, k, j);
/*      */             }
/*      */           }
/*      */         }
/*  992 */         this.indexUntyped = new ConstantPool.Index("untyped", arrayOfEntry);
/*      */       }
/*  994 */       return this.indexUntyped;
/*      */     }
/*      */ 
/*      */     public int untypedIndexOf(ConstantPool.Entry paramEntry) {
/*  998 */       if (this.untypedFirstIndexByTag == null) {
/*  999 */         this.untypedFirstIndexByTag = new int[14];
/* 1000 */         i = 0;
/* 1001 */         for (int j = 0; j < ConstantPool.TAGS_IN_ORDER.length; j++) {
/* 1002 */           k = ConstantPool.TAGS_IN_ORDER[j];
/* 1003 */           ConstantPool.Index localIndex2 = this.indexByTag[k];
/* 1004 */           if (localIndex2 != null) {
/* 1005 */             int m = localIndex2.cpMap.length;
/* 1006 */             this.untypedFirstIndexByTag[k] = i;
/* 1007 */             i += m;
/*      */           }
/*      */         }
/* 1009 */         this.totalSize = i;
/*      */       }
/* 1011 */       if (paramEntry == null) return -1;
/* 1012 */       int i = paramEntry.tag;
/* 1013 */       ConstantPool.Index localIndex1 = this.indexByTag[i];
/* 1014 */       if (localIndex1 == null) return -1;
/* 1015 */       int k = localIndex1.findIndexOf(paramEntry);
/* 1016 */       if (k >= 0)
/* 1017 */         k += this.untypedFirstIndexByTag[i];
/* 1018 */       return k;
/*      */     }
/*      */ 
/*      */     public void initIndexByTag(byte paramByte, ConstantPool.Index paramIndex) {
/* 1022 */       assert (this.indexByTag[paramByte] == null);
/* 1023 */       ConstantPool.Entry[] arrayOfEntry = paramIndex.cpMap;
/* 1024 */       for (int i = 0; i < arrayOfEntry.length; i++)
/*      */       {
/* 1026 */         assert (arrayOfEntry[i].tag == paramByte);
/*      */       }
/* 1028 */       if (paramByte == 1)
/*      */       {
/* 1030 */         assert ((arrayOfEntry.length == 0) || (arrayOfEntry[0].stringValue().equals("")));
/*      */       }
/* 1032 */       this.indexByTag[paramByte] = paramIndex;
/*      */ 
/* 1034 */       this.untypedFirstIndexByTag = null;
/* 1035 */       this.indexUntyped = null;
/* 1036 */       if (this.indexByTagAndClass != null)
/* 1037 */         this.indexByTagAndClass[paramByte] = null;
/*      */     }
/*      */ 
/*      */     public ConstantPool.Index getIndexByTag(byte paramByte)
/*      */     {
/* 1042 */       if (paramByte == 19) {
/* 1043 */         return getUntypedIndex();
/*      */       }
/* 1045 */       ConstantPool.Index localIndex = this.indexByTag[paramByte];
/* 1046 */       if (localIndex == null)
/*      */       {
/* 1048 */         localIndex = new ConstantPool.Index(ConstantPool.tagName(paramByte), new ConstantPool.Entry[0]);
/* 1049 */         this.indexByTag[paramByte] = localIndex;
/*      */       }
/* 1051 */       return localIndex;
/*      */     }
/*      */ 
/*      */     public ConstantPool.Index getMemberIndex(byte paramByte, ConstantPool.ClassEntry paramClassEntry)
/*      */     {
/* 1056 */       if (paramClassEntry == null)
/* 1057 */         throw new RuntimeException("missing class reference for " + ConstantPool.tagName(paramByte));
/* 1058 */       if (this.indexByTagAndClass == null)
/* 1059 */         this.indexByTagAndClass = new ConstantPool.Index[14][];
/* 1060 */       ConstantPool.Index localIndex1 = getIndexByTag((byte)7);
/* 1061 */       ConstantPool.Index[] arrayOfIndex = this.indexByTagAndClass[paramByte];
/* 1062 */       if (arrayOfIndex == null)
/*      */       {
/* 1065 */         ConstantPool.Index localIndex2 = getIndexByTag(paramByte);
/* 1066 */         int[] arrayOfInt = new int[localIndex2.size()];
/* 1067 */         for (int j = 0; j < arrayOfInt.length; j++) {
/* 1068 */           ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)localIndex2.get(j);
/* 1069 */           int k = localIndex1.indexOf(localMemberEntry.classRef);
/* 1070 */           arrayOfInt[j] = k;
/*      */         }
/* 1072 */         arrayOfIndex = ConstantPool.partition(localIndex2, arrayOfInt);
/* 1073 */         for (j = 0; j < arrayOfIndex.length; j++) {
/* 1074 */           assert ((arrayOfIndex[j] == null) || (arrayOfIndex[j].assertIsSorted()));
/*      */         }
/*      */ 
/* 1077 */         this.indexByTagAndClass[paramByte] = arrayOfIndex;
/*      */       }
/* 1079 */       int i = localIndex1.indexOf(paramClassEntry);
/* 1080 */       return arrayOfIndex[i];
/*      */     }
/*      */ 
/*      */     public int getOverloadingIndex(ConstantPool.MemberEntry paramMemberEntry)
/*      */     {
/* 1086 */       ConstantPool.Index localIndex = getMemberIndex(paramMemberEntry.tag, paramMemberEntry.classRef);
/* 1087 */       ConstantPool.Utf8Entry localUtf8Entry = paramMemberEntry.descRef.nameRef;
/* 1088 */       int i = 0;
/* 1089 */       for (int j = 0; j < localIndex.cpMap.length; j++) {
/* 1090 */         ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)localIndex.cpMap[j];
/* 1091 */         if (localMemberEntry.equals(paramMemberEntry))
/* 1092 */           return i;
/* 1093 */         if (localMemberEntry.descRef.nameRef.equals(localUtf8Entry))
/*      */         {
/* 1095 */           i++;
/*      */         }
/*      */       }
/* 1097 */       throw new RuntimeException("should not reach here");
/*      */     }
/*      */ 
/*      */     public ConstantPool.MemberEntry getOverloadingForIndex(byte paramByte, ConstantPool.ClassEntry paramClassEntry, String paramString, int paramInt)
/*      */     {
/* 1102 */       assert (paramString.equals(paramString.intern()));
/* 1103 */       ConstantPool.Index localIndex = getMemberIndex(paramByte, paramClassEntry);
/* 1104 */       int i = 0;
/* 1105 */       for (int j = 0; j < localIndex.cpMap.length; j++) {
/* 1106 */         ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)localIndex.cpMap[j];
/* 1107 */         if (localMemberEntry.descRef.nameRef.stringValue().equals(paramString)) {
/* 1108 */           if (i == paramInt) return localMemberEntry;
/* 1109 */           i++;
/*      */         }
/*      */       }
/* 1112 */       throw new RuntimeException("should not reach here");
/*      */     }
/*      */ 
/*      */     public boolean haveNumbers() {
/* 1116 */       for (byte b = 3; b <= 6; b = (byte)(b + 1)) {
/* 1117 */         switch (b) {
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/* 1122 */           break;
/*      */         default:
/* 1124 */           if (!$assertionsDisabled) throw new AssertionError(); break;
/*      */         }
/* 1126 */         if (getIndexByTag(b).size() > 0) return true;
/*      */       }
/* 1128 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class LiteralEntry extends ConstantPool.Entry
/*      */   {
/*      */     protected LiteralEntry(byte paramByte)
/*      */     {
/*  257 */       super();
/*      */     }
/*      */ 
/*      */     public abstract Comparable literalValue();
/*      */   }
/*      */ 
/*      */   public static class MemberEntry extends ConstantPool.Entry
/*      */   {
/*      */     final ConstantPool.ClassEntry classRef;
/*      */     final ConstantPool.DescriptorEntry descRef;
/*      */ 
/*      */     public ConstantPool.Entry getRef(int paramInt)
/*      */     {
/*  426 */       if (paramInt == 0) return this.classRef;
/*  427 */       if (paramInt == 1) return this.descRef;
/*  428 */       return null;
/*      */     }
/*      */     protected int computeValueHash() {
/*  431 */       int i = this.descRef.hashCode();
/*  432 */       return this.classRef.hashCode() + (i << 8) ^ i;
/*      */     }
/*      */ 
/*      */     MemberEntry(byte paramByte, ConstantPool.ClassEntry paramClassEntry, ConstantPool.DescriptorEntry paramDescriptorEntry) {
/*  436 */       super();
/*  437 */       assert (ConstantPool.isMemberTag(paramByte));
/*  438 */       this.classRef = paramClassEntry;
/*  439 */       this.descRef = paramDescriptorEntry;
/*  440 */       hashCode();
/*      */     }
/*      */     public boolean equals(Object paramObject) {
/*  443 */       if ((paramObject == null) || (paramObject.getClass() != MemberEntry.class)) {
/*  444 */         return false;
/*      */       }
/*  446 */       MemberEntry localMemberEntry = (MemberEntry)paramObject;
/*  447 */       return (this.classRef.eq(localMemberEntry.classRef)) && (this.descRef.eq(localMemberEntry.descRef));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  451 */       int i = superCompareTo(paramObject);
/*  452 */       if (i == 0) {
/*  453 */         MemberEntry localMemberEntry = (MemberEntry)paramObject;
/*      */ 
/*  455 */         i = this.classRef.compareTo(localMemberEntry.classRef);
/*  456 */         if (i == 0)
/*  457 */           i = this.descRef.compareTo(localMemberEntry.descRef);
/*      */       }
/*  459 */       return i;
/*      */     }
/*      */     public String stringValue() {
/*  462 */       return stringValueOf(this.tag, this.classRef, this.descRef);
/*      */     }
/*      */ 
/*      */     static String stringValueOf(byte paramByte, ConstantPool.ClassEntry paramClassEntry, ConstantPool.DescriptorEntry paramDescriptorEntry) {
/*  466 */       assert (ConstantPool.isMemberTag(paramByte));
/*      */       String str;
/*  468 */       switch (paramByte) { case 9:
/*  469 */         str = "Field:"; break;
/*      */       case 10:
/*  470 */         str = "Method:"; break;
/*      */       case 11:
/*  471 */         str = "IMethod:"; break;
/*      */       default:
/*  472 */         str = paramByte + "???";
/*      */       }
/*  474 */       return str + paramClassEntry.stringValue() + "," + paramDescriptorEntry.stringValue();
/*      */     }
/*      */ 
/*      */     public boolean isMethod() {
/*  478 */       return this.descRef.isMethod();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class NumberEntry extends ConstantPool.LiteralEntry
/*      */   {
/*      */     final Number value;
/*      */ 
/*      */     NumberEntry(Number paramNumber)
/*      */     {
/*  267 */       super();
/*  268 */       this.value = paramNumber;
/*  269 */       hashCode();
/*      */     }
/*      */     protected int computeValueHash() {
/*  272 */       return this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  276 */       return (paramObject != null) && (paramObject.getClass() == NumberEntry.class) && (((NumberEntry)paramObject).value.equals(this.value));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject)
/*      */     {
/*  281 */       int i = superCompareTo(paramObject);
/*  282 */       if (i == 0) {
/*  283 */         i = ((Comparable)this.value).compareTo(((NumberEntry)paramObject).value);
/*      */       }
/*  285 */       return i;
/*      */     }
/*      */     public Number numberValue() {
/*  288 */       return this.value;
/*      */     }
/*      */     public Comparable literalValue() {
/*  291 */       return (Comparable)this.value;
/*      */     }
/*      */     public String stringValue() {
/*  294 */       return this.value.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class SignatureEntry extends ConstantPool.Entry
/*      */   {
/*      */     final ConstantPool.Utf8Entry formRef;
/*      */     final ConstantPool.ClassEntry[] classRefs;
/*      */     String value;
/*      */     ConstantPool.Utf8Entry asUtf8Entry;
/*      */ 
/*      */     public ConstantPool.Entry getRef(int paramInt)
/*      */     {
/*  489 */       if (paramInt == 0) return this.formRef;
/*  490 */       return paramInt - 1 < this.classRefs.length ? this.classRefs[(paramInt - 1)] : null;
/*      */     }
/*      */     SignatureEntry(String paramString) {
/*  493 */       super();
/*  494 */       paramString = paramString.intern();
/*  495 */       this.value = paramString;
/*  496 */       String[] arrayOfString = ConstantPool.structureSignature(paramString);
/*  497 */       this.formRef = ConstantPool.getUtf8Entry(arrayOfString[0]);
/*  498 */       this.classRefs = new ConstantPool.ClassEntry[arrayOfString.length - 1];
/*  499 */       for (int i = 1; i < arrayOfString.length; i++) {
/*  500 */         this.classRefs[(i - 1)] = ConstantPool.getClassEntry(arrayOfString[i]);
/*      */       }
/*  502 */       hashCode();
/*      */     }
/*      */     protected int computeValueHash() {
/*  505 */       stringValue();
/*  506 */       return this.value.hashCode() + this.tag;
/*      */     }
/*      */ 
/*      */     public ConstantPool.Utf8Entry asUtf8Entry() {
/*  510 */       if (this.asUtf8Entry == null) {
/*  511 */         this.asUtf8Entry = ConstantPool.getUtf8Entry(stringValue());
/*      */       }
/*  513 */       return this.asUtf8Entry;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  517 */       return (paramObject != null) && (paramObject.getClass() == SignatureEntry.class) && (((SignatureEntry)paramObject).value.equals(this.value));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  521 */       int i = superCompareTo(paramObject);
/*  522 */       if (i == 0) {
/*  523 */         SignatureEntry localSignatureEntry = (SignatureEntry)paramObject;
/*  524 */         i = ConstantPool.compareSignatures(this.value, localSignatureEntry.value);
/*      */       }
/*  526 */       return i;
/*      */     }
/*      */     public String stringValue() {
/*  529 */       if (this.value == null) {
/*  530 */         this.value = stringValueOf(this.formRef, this.classRefs);
/*      */       }
/*  532 */       return this.value;
/*      */     }
/*      */ 
/*      */     static String stringValueOf(ConstantPool.Utf8Entry paramUtf8Entry, ConstantPool.ClassEntry[] paramArrayOfClassEntry) {
/*  536 */       String[] arrayOfString = new String[1 + paramArrayOfClassEntry.length];
/*  537 */       arrayOfString[0] = paramUtf8Entry.stringValue();
/*  538 */       for (int i = 1; i < arrayOfString.length; i++) {
/*  539 */         arrayOfString[i] = paramArrayOfClassEntry[(i - 1)].stringValue();
/*      */       }
/*  541 */       return ConstantPool.flattenSignature(arrayOfString).intern();
/*      */     }
/*      */ 
/*      */     public int computeSize(boolean paramBoolean) {
/*  545 */       String str = this.formRef.stringValue();
/*  546 */       int i = 0;
/*  547 */       int j = 1;
/*  548 */       if (isMethod()) {
/*  549 */         i = 1;
/*  550 */         j = str.indexOf(')');
/*      */       }
/*  552 */       int k = 0;
/*  553 */       label154: for (int m = i; m < j; m++) {
/*  554 */         switch (str.charAt(m)) {
/*      */         case 'D':
/*      */         case 'J':
/*  557 */           if (paramBoolean)
/*  558 */             k++; break;
/*      */         case '[':
/*      */         case ';':
/*      */         default:
/*  563 */           while (str.charAt(m) == '[') {
/*  564 */             m++; continue;
/*      */ 
/*  568 */             break label154;
/*      */ 
/*  570 */             assert (0 <= "BSCIJFDZLV([".indexOf(str.charAt(m)));
/*      */           }
/*      */         }
/*  573 */         k++;
/*      */       }
/*  575 */       return k;
/*      */     }
/*      */     public boolean isMethod() {
/*  578 */       return this.formRef.stringValue().charAt(0) == '(';
/*      */     }
/*      */     public byte getLiteralTag() {
/*  581 */       switch (this.formRef.stringValue().charAt(0)) { case 'L':
/*  582 */         return 8;
/*      */       case 'I':
/*  583 */         return 3;
/*      */       case 'J':
/*  584 */         return 5;
/*      */       case 'F':
/*  585 */         return 4;
/*      */       case 'D':
/*  586 */         return 6;
/*      */       case 'B':
/*      */       case 'C':
/*      */       case 'S':
/*      */       case 'Z':
/*  588 */         return 3;
/*      */       case 'E':
/*      */       case 'G':
/*      */       case 'H':
/*      */       case 'K':
/*      */       case 'M':
/*      */       case 'N':
/*      */       case 'O':
/*      */       case 'P':
/*      */       case 'Q':
/*      */       case 'R':
/*      */       case 'T':
/*      */       case 'U':
/*      */       case 'V':
/*      */       case 'W':
/*      */       case 'X':
/*  590 */       case 'Y': } if (!$assertionsDisabled) throw new AssertionError();
/*  591 */       return 0;
/*      */     }
/*      */ 
/*      */     public String prettyString()
/*      */     {
/*      */       String str;
/*  595 */       if (isMethod()) {
/*  596 */         str = this.formRef.stringValue();
/*  597 */         str = str.substring(0, 1 + str.indexOf(')'));
/*      */       } else {
/*  599 */         str = "/" + this.formRef.stringValue();
/*      */       }
/*      */       int i;
/*  602 */       while ((i = str.indexOf(';')) >= 0) {
/*  603 */         str = str.substring(0, i) + str.substring(i + 1);
/*      */       }
/*  605 */       return str;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class StringEntry extends ConstantPool.LiteralEntry
/*      */   {
/*      */     final ConstantPool.Utf8Entry ref;
/*      */ 
/*      */     public ConstantPool.Entry getRef(int paramInt)
/*      */     {
/*  301 */       return paramInt == 0 ? this.ref : null;
/*      */     }
/*      */     StringEntry(ConstantPool.Entry paramEntry) {
/*  304 */       super();
/*  305 */       this.ref = ((ConstantPool.Utf8Entry)paramEntry);
/*  306 */       hashCode();
/*      */     }
/*      */     protected int computeValueHash() {
/*  309 */       return this.ref.hashCode() + this.tag;
/*      */     }
/*      */     public boolean equals(Object paramObject) {
/*  312 */       return (paramObject != null) && (paramObject.getClass() == StringEntry.class) && (((StringEntry)paramObject).ref.eq(this.ref));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  316 */       int i = superCompareTo(paramObject);
/*  317 */       if (i == 0) {
/*  318 */         i = this.ref.compareTo(((StringEntry)paramObject).ref);
/*      */       }
/*  320 */       return i;
/*      */     }
/*      */     public Comparable literalValue() {
/*  323 */       return this.ref.stringValue();
/*      */     }
/*      */     public String stringValue() {
/*  326 */       return this.ref.stringValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Utf8Entry extends ConstantPool.Entry
/*      */   {
/*      */     final String value;
/*      */ 
/*      */     Utf8Entry(String paramString)
/*      */     {
/*  212 */       super();
/*  213 */       this.value = paramString.intern();
/*  214 */       hashCode();
/*      */     }
/*      */     protected int computeValueHash() {
/*  217 */       return this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  221 */       return (paramObject != null) && (paramObject.getClass() == Utf8Entry.class) && (((Utf8Entry)paramObject).value.equals(this.value));
/*      */     }
/*      */ 
/*      */     public int compareTo(Object paramObject) {
/*  225 */       int i = superCompareTo(paramObject);
/*  226 */       if (i == 0) {
/*  227 */         i = this.value.compareTo(((Utf8Entry)paramObject).value);
/*      */       }
/*  229 */       return i;
/*      */     }
/*      */     public String stringValue() {
/*  232 */       return this.value;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.util.jar.pack.ConstantPool
 * JD-Core Version:    0.6.2
 */