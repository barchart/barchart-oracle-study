/*     */ package java.lang;
/*     */ 
/*     */ import java.text.BreakIterator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import sun.text.Normalizer;
/*     */ 
/*     */ final class ConditionalSpecialCasing
/*     */ {
/*     */   static final int FINAL_CASED = 1;
/*     */   static final int AFTER_SOFT_DOTTED = 2;
/*     */   static final int MORE_ABOVE = 3;
/*     */   static final int AFTER_I = 4;
/*     */   static final int NOT_BEFORE_DOT = 5;
/*     */   static final int COMBINING_CLASS_ABOVE = 230;
/*  60 */   static Entry[] entry = { new Entry(931, new char[] { 'ς' }, new char[] { 'Σ' }, null, 1), new Entry(775, new char[] { '̇' }, new char[0], "lt", 2), new Entry(73, new char[] { 'i', '̇' }, new char[] { 'I' }, "lt", 3), new Entry(74, new char[] { 'j', '̇' }, new char[] { 'J' }, "lt", 3), new Entry(302, new char[] { 'į', '̇' }, new char[] { 'Į' }, "lt", 3), new Entry(204, new char[] { 'i', '̇', '̀' }, new char[] { 'Ì' }, "lt", 0), new Entry(205, new char[] { 'i', '̇', '́' }, new char[] { 'Í' }, "lt", 0), new Entry(296, new char[] { 'i', '̇', '̃' }, new char[] { 'Ĩ' }, "lt", 0), new Entry(304, new char[] { 'i', '̇' }, new char[] { 'İ' }, "lt", 0), new Entry(775, new char[0], new char[] { '̇' }, "tr", 4), new Entry(775, new char[0], new char[] { '̇' }, "az", 4), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "tr", 5), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "az", 5), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "tr", 0), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "az", 0), new Entry(304, new char[] { 'i', '̇' }, new char[] { 'İ' }, "en", 0) };
/*     */ 
/*  95 */   static Hashtable entryTable = new Hashtable();
/*     */ 
/*     */   static int toLowerCaseEx(String paramString, int paramInt, Locale paramLocale)
/*     */   {
/* 111 */     char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, true);
/*     */ 
/* 113 */     if (arrayOfChar != null) {
/* 114 */       if (arrayOfChar.length == 1) {
/* 115 */         return arrayOfChar[0];
/*     */       }
/* 117 */       return -1;
/*     */     }
/*     */ 
/* 121 */     return Character.toLowerCase(paramString.codePointAt(paramInt));
/*     */   }
/*     */ 
/*     */   static int toUpperCaseEx(String paramString, int paramInt, Locale paramLocale)
/*     */   {
/* 126 */     char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
/*     */ 
/* 128 */     if (arrayOfChar != null) {
/* 129 */       if (arrayOfChar.length == 1) {
/* 130 */         return arrayOfChar[0];
/*     */       }
/* 132 */       return -1;
/*     */     }
/*     */ 
/* 136 */     return Character.toUpperCaseEx(paramString.codePointAt(paramInt));
/*     */   }
/*     */ 
/*     */   static char[] toLowerCaseCharArray(String paramString, int paramInt, Locale paramLocale)
/*     */   {
/* 141 */     return lookUpTable(paramString, paramInt, paramLocale, true);
/*     */   }
/*     */ 
/*     */   static char[] toUpperCaseCharArray(String paramString, int paramInt, Locale paramLocale) {
/* 145 */     char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
/* 146 */     if (arrayOfChar != null) {
/* 147 */       return arrayOfChar;
/*     */     }
/* 149 */     return Character.toUpperCaseCharArray(paramString.codePointAt(paramInt));
/*     */   }
/*     */ 
/*     */   private static char[] lookUpTable(String paramString, int paramInt, Locale paramLocale, boolean paramBoolean)
/*     */   {
/* 154 */     HashSet localHashSet = (HashSet)entryTable.get(new Integer(paramString.codePointAt(paramInt)));
/*     */ 
/* 156 */     if (localHashSet != null) {
/* 157 */       Iterator localIterator = localHashSet.iterator();
/* 158 */       String str1 = paramLocale.getLanguage();
/* 159 */       while (localIterator.hasNext()) {
/* 160 */         Entry localEntry = (Entry)localIterator.next();
/* 161 */         String str2 = localEntry.getLanguage();
/* 162 */         if (((str2 == null) || (str2.equals(str1))) && (isConditionMet(paramString, paramInt, paramLocale, localEntry.getCondition())))
/*     */         {
/* 164 */           return paramBoolean ? localEntry.getLowerCase() : localEntry.getUpperCase();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 169 */     return null;
/*     */   }
/*     */ 
/*     */   private static boolean isConditionMet(String paramString, int paramInt1, Locale paramLocale, int paramInt2) {
/* 173 */     switch (paramInt2) {
/*     */     case 1:
/* 175 */       return isFinalCased(paramString, paramInt1, paramLocale);
/*     */     case 2:
/* 178 */       return isAfterSoftDotted(paramString, paramInt1);
/*     */     case 3:
/* 181 */       return isMoreAbove(paramString, paramInt1);
/*     */     case 4:
/* 184 */       return isAfterI(paramString, paramInt1);
/*     */     case 5:
/* 187 */       return !isBeforeDot(paramString, paramInt1);
/*     */     }
/*     */ 
/* 190 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean isFinalCased(String paramString, int paramInt, Locale paramLocale)
/*     */   {
/* 205 */     BreakIterator localBreakIterator = BreakIterator.getWordInstance(paramLocale);
/* 206 */     localBreakIterator.setText(paramString);
/*     */     int i;
/* 210 */     for (int j = paramInt; (j >= 0) && (!localBreakIterator.isBoundary(j)); 
/* 211 */       j -= Character.charCount(i))
/*     */     {
/* 213 */       i = paramString.codePointBefore(j);
/* 214 */       if (isCased(i))
/*     */       {
/* 216 */         int k = paramString.length();
/*     */ 
/* 218 */         j = paramInt + Character.charCount(paramString.codePointAt(paramInt));
/*     */ 
/* 220 */         for (; (j < k) && (!localBreakIterator.isBoundary(j)); 
/* 220 */           j += Character.charCount(i))
/*     */         {
/* 222 */           i = paramString.codePointAt(j);
/* 223 */           if (isCased(i)) {
/* 224 */             return false;
/*     */           }
/*     */         }
/*     */ 
/* 228 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 232 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAfterI(String paramString, int paramInt)
/*     */   {
/*     */     int i;
/* 249 */     for (int k = paramInt; k > 0; k -= Character.charCount(i))
/*     */     {
/* 251 */       i = paramString.codePointBefore(k);
/*     */ 
/* 253 */       if (i == 73) {
/* 254 */         return true;
/*     */       }
/* 256 */       int j = Normalizer.getCombiningClass(i);
/* 257 */       if ((j == 0) || (j == 230)) {
/* 258 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 263 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAfterSoftDotted(String paramString, int paramInt)
/*     */   {
/*     */     int i;
/* 281 */     for (int k = paramInt; k > 0; k -= Character.charCount(i))
/*     */     {
/* 283 */       i = paramString.codePointBefore(k);
/*     */ 
/* 285 */       if (isSoftDotted(i)) {
/* 286 */         return true;
/*     */       }
/* 288 */       int j = Normalizer.getCombiningClass(i);
/* 289 */       if ((j == 0) || (j == 230)) {
/* 290 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 295 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isMoreAbove(String paramString, int paramInt)
/*     */   {
/* 310 */     int k = paramString.length();
/*     */ 
/* 313 */     int m = paramInt + Character.charCount(paramString.codePointAt(paramInt));
/*     */     int i;
/* 314 */     for (; m < k; m += Character.charCount(i))
/*     */     {
/* 316 */       i = paramString.codePointAt(m);
/* 317 */       int j = Normalizer.getCombiningClass(i);
/*     */ 
/* 319 */       if (j == 230)
/* 320 */         return true;
/* 321 */       if (j == 0) {
/* 322 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 326 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isBeforeDot(String paramString, int paramInt)
/*     */   {
/* 343 */     int k = paramString.length();
/*     */ 
/* 346 */     int m = paramInt + Character.charCount(paramString.codePointAt(paramInt));
/*     */     int i;
/* 347 */     for (; m < k; m += Character.charCount(i))
/*     */     {
/* 349 */       i = paramString.codePointAt(m);
/*     */ 
/* 351 */       if (i == 775) {
/* 352 */         return true;
/*     */       }
/* 354 */       int j = Normalizer.getCombiningClass(i);
/* 355 */       if ((j == 0) || (j == 230)) {
/* 356 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 361 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isCased(int paramInt)
/*     */   {
/* 375 */     int i = Character.getType(paramInt);
/* 376 */     if ((i == 2) || (i == 1) || (i == 3))
/*     */     {
/* 379 */       return true;
/*     */     }
/*     */ 
/* 383 */     if ((paramInt >= 688) && (paramInt <= 696))
/*     */     {
/* 385 */       return true;
/* 386 */     }if ((paramInt >= 704) && (paramInt <= 705))
/*     */     {
/* 388 */       return true;
/* 389 */     }if ((paramInt >= 736) && (paramInt <= 740))
/*     */     {
/* 391 */       return true;
/* 392 */     }if (paramInt == 837)
/*     */     {
/* 394 */       return true;
/* 395 */     }if (paramInt == 890)
/*     */     {
/* 397 */       return true;
/* 398 */     }if ((paramInt >= 7468) && (paramInt <= 7521))
/*     */     {
/* 400 */       return true;
/* 401 */     }if ((paramInt >= 8544) && (paramInt <= 8575))
/*     */     {
/* 404 */       return true;
/* 405 */     }if ((paramInt >= 9398) && (paramInt <= 9449))
/*     */     {
/* 408 */       return true;
/*     */     }
/* 410 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isSoftDotted(int paramInt)
/*     */   {
/* 416 */     switch (paramInt) {
/*     */     case 105:
/*     */     case 106:
/*     */     case 303:
/*     */     case 616:
/*     */     case 1110:
/*     */     case 1112:
/*     */     case 7522:
/*     */     case 7725:
/*     */     case 7883:
/*     */     case 8305:
/* 427 */       return true;
/*     */     }
/* 429 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  98 */     for (int i = 0; i < entry.length; i++) {
/*  99 */       Entry localEntry = entry[i];
/* 100 */       Integer localInteger = new Integer(localEntry.getCodePoint());
/* 101 */       HashSet localHashSet = (HashSet)entryTable.get(localInteger);
/* 102 */       if (localHashSet == null) {
/* 103 */         localHashSet = new HashSet();
/*     */       }
/* 105 */       localHashSet.add(localEntry);
/* 106 */       entryTable.put(localInteger, localHashSet);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Entry
/*     */   {
/*     */     int ch;
/*     */     char[] lower;
/*     */     char[] upper;
/*     */     String lang;
/*     */     int condition;
/*     */ 
/*     */     Entry(int paramInt1, char[] paramArrayOfChar1, char[] paramArrayOfChar2, String paramString, int paramInt2)
/*     */     {
/* 444 */       this.ch = paramInt1;
/* 445 */       this.lower = paramArrayOfChar1;
/* 446 */       this.upper = paramArrayOfChar2;
/* 447 */       this.lang = paramString;
/* 448 */       this.condition = paramInt2;
/*     */     }
/*     */ 
/*     */     int getCodePoint() {
/* 452 */       return this.ch;
/*     */     }
/*     */ 
/*     */     char[] getLowerCase() {
/* 456 */       return this.lower;
/*     */     }
/*     */ 
/*     */     char[] getUpperCase() {
/* 460 */       return this.upper;
/*     */     }
/*     */ 
/*     */     String getLanguage() {
/* 464 */       return this.lang;
/*     */     }
/*     */ 
/*     */     int getCondition() {
/* 468 */       return this.condition;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ConditionalSpecialCasing
 * JD-Core Version:    0.6.2
 */