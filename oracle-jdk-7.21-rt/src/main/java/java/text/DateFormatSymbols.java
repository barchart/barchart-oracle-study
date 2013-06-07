/*     */ package java.text;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.Locale.Category;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import sun.util.LocaleServiceProviderPool;
/*     */ import sun.util.LocaleServiceProviderPool.LocalizedObjectGetter;
/*     */ import sun.util.TimeZoneNameUtility;
/*     */ import sun.util.resources.LocaleData;
/*     */ 
/*     */ public class DateFormatSymbols
/*     */   implements Serializable, Cloneable
/*     */ {
/* 149 */   String[] eras = null;
/*     */ 
/* 157 */   String[] months = null;
/*     */ 
/* 166 */   String[] shortMonths = null;
/*     */ 
/* 175 */   String[] weekdays = null;
/*     */ 
/* 184 */   String[] shortWeekdays = null;
/*     */ 
/* 192 */   String[] ampms = null;
/*     */ 
/* 219 */   String[][] zoneStrings = (String[][])null;
/*     */ 
/* 224 */   transient boolean isZoneStringsSet = false;
/*     */   static final String patternChars = "GyMdkHmsSEDFwWahKzZYuX";
/*     */   static final int PATTERN_ERA = 0;
/*     */   static final int PATTERN_YEAR = 1;
/*     */   static final int PATTERN_MONTH = 2;
/*     */   static final int PATTERN_DAY_OF_MONTH = 3;
/*     */   static final int PATTERN_HOUR_OF_DAY1 = 4;
/*     */   static final int PATTERN_HOUR_OF_DAY0 = 5;
/*     */   static final int PATTERN_MINUTE = 6;
/*     */   static final int PATTERN_SECOND = 7;
/*     */   static final int PATTERN_MILLISECOND = 8;
/*     */   static final int PATTERN_DAY_OF_WEEK = 9;
/*     */   static final int PATTERN_DAY_OF_YEAR = 10;
/*     */   static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
/*     */   static final int PATTERN_WEEK_OF_YEAR = 12;
/*     */   static final int PATTERN_WEEK_OF_MONTH = 13;
/*     */   static final int PATTERN_AM_PM = 14;
/*     */   static final int PATTERN_HOUR1 = 15;
/*     */   static final int PATTERN_HOUR0 = 16;
/*     */   static final int PATTERN_ZONE_NAME = 17;
/*     */   static final int PATTERN_ZONE_VALUE = 18;
/*     */   static final int PATTERN_WEEK_YEAR = 19;
/*     */   static final int PATTERN_ISO_DAY_OF_WEEK = 20;
/*     */   static final int PATTERN_ISO_ZONE = 21;
/* 265 */   String localPatternChars = null;
/*     */ 
/* 273 */   Locale locale = null;
/*     */   static final long serialVersionUID = -5987973545549424702L;
/*     */   static final int millisPerHour = 3600000;
/* 647 */   private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap(3);
/*     */ 
/*     */   public DateFormatSymbols()
/*     */   {
/* 122 */     initializeData(Locale.getDefault(Locale.Category.FORMAT));
/*     */   }
/*     */ 
/*     */   public DateFormatSymbols(Locale paramLocale)
/*     */   {
/* 141 */     initializeData(paramLocale);
/*     */   }
/*     */ 
/*     */   public static Locale[] getAvailableLocales()
/*     */   {
/* 293 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
/*     */ 
/* 295 */     return localLocaleServiceProviderPool.getAvailableLocales();
/*     */   }
/*     */ 
/*     */   public static final DateFormatSymbols getInstance()
/*     */   {
/* 309 */     return getInstance(Locale.getDefault(Locale.Category.FORMAT));
/*     */   }
/*     */ 
/*     */   public static final DateFormatSymbols getInstance(Locale paramLocale)
/*     */   {
/* 325 */     DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
/* 326 */     if (localDateFormatSymbols != null) {
/* 327 */       return localDateFormatSymbols;
/*     */     }
/* 329 */     return (DateFormatSymbols)getCachedInstance(paramLocale).clone();
/*     */   }
/*     */ 
/*     */   static final DateFormatSymbols getInstanceRef(Locale paramLocale)
/*     */   {
/* 339 */     DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
/* 340 */     if (localDateFormatSymbols != null) {
/* 341 */       return localDateFormatSymbols;
/*     */     }
/* 343 */     return getCachedInstance(paramLocale);
/*     */   }
/*     */ 
/*     */   private static DateFormatSymbols getProviderInstance(Locale paramLocale) {
/* 347 */     DateFormatSymbols localDateFormatSymbols = null;
/*     */ 
/* 351 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
/*     */ 
/* 353 */     if (localLocaleServiceProviderPool.hasProviders()) {
/* 354 */       localDateFormatSymbols = (DateFormatSymbols)localLocaleServiceProviderPool.getLocalizedObject(DateFormatSymbolsGetter.INSTANCE, paramLocale, new Object[0]);
/*     */     }
/*     */ 
/* 357 */     return localDateFormatSymbols;
/*     */   }
/*     */ 
/*     */   private static DateFormatSymbols getCachedInstance(Locale paramLocale)
/*     */   {
/* 366 */     SoftReference localSoftReference1 = (SoftReference)cachedInstances.get(paramLocale);
/* 367 */     Object localObject = null;
/* 368 */     if ((localSoftReference1 == null) || ((localObject = (DateFormatSymbols)localSoftReference1.get()) == null)) {
/* 369 */       localObject = new DateFormatSymbols(paramLocale);
/* 370 */       localSoftReference1 = new SoftReference(localObject);
/* 371 */       SoftReference localSoftReference2 = (SoftReference)cachedInstances.putIfAbsent(paramLocale, localSoftReference1);
/* 372 */       if (localSoftReference2 != null) {
/* 373 */         DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)localSoftReference2.get();
/* 374 */         if (localDateFormatSymbols != null) {
/* 375 */           localObject = localDateFormatSymbols;
/*     */         }
/*     */         else {
/* 378 */           cachedInstances.put(paramLocale, localSoftReference1);
/*     */         }
/*     */       }
/*     */     }
/* 382 */     return localObject;
/*     */   }
/*     */ 
/*     */   public String[] getEras()
/*     */   {
/* 390 */     return (String[])Arrays.copyOf(this.eras, this.eras.length);
/*     */   }
/*     */ 
/*     */   public void setEras(String[] paramArrayOfString)
/*     */   {
/* 398 */     this.eras = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[] getMonths()
/*     */   {
/* 406 */     return (String[])Arrays.copyOf(this.months, this.months.length);
/*     */   }
/*     */ 
/*     */   public void setMonths(String[] paramArrayOfString)
/*     */   {
/* 414 */     this.months = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[] getShortMonths()
/*     */   {
/* 422 */     return (String[])Arrays.copyOf(this.shortMonths, this.shortMonths.length);
/*     */   }
/*     */ 
/*     */   public void setShortMonths(String[] paramArrayOfString)
/*     */   {
/* 430 */     this.shortMonths = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[] getWeekdays()
/*     */   {
/* 439 */     return (String[])Arrays.copyOf(this.weekdays, this.weekdays.length);
/*     */   }
/*     */ 
/*     */   public void setWeekdays(String[] paramArrayOfString)
/*     */   {
/* 449 */     this.weekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[] getShortWeekdays()
/*     */   {
/* 458 */     return (String[])Arrays.copyOf(this.shortWeekdays, this.shortWeekdays.length);
/*     */   }
/*     */ 
/*     */   public void setShortWeekdays(String[] paramArrayOfString)
/*     */   {
/* 468 */     this.shortWeekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[] getAmPmStrings()
/*     */   {
/* 476 */     return (String[])Arrays.copyOf(this.ampms, this.ampms.length);
/*     */   }
/*     */ 
/*     */   public void setAmPmStrings(String[] paramArrayOfString)
/*     */   {
/* 484 */     this.ampms = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
/*     */   }
/*     */ 
/*     */   public String[][] getZoneStrings()
/*     */   {
/* 526 */     return getZoneStringsImpl(true);
/*     */   }
/*     */ 
/*     */   public void setZoneStrings(String[][] paramArrayOfString)
/*     */   {
/* 559 */     String[][] arrayOfString; = new String[paramArrayOfString.length][];
/* 560 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 561 */       int j = paramArrayOfString[i].length;
/* 562 */       if (j < 5) {
/* 563 */         throw new IllegalArgumentException();
/*     */       }
/* 565 */       arrayOfString;[i] = ((String[])Arrays.copyOf(paramArrayOfString[i], j));
/*     */     }
/* 567 */     this.zoneStrings = arrayOfString;;
/* 568 */     this.isZoneStringsSet = true;
/*     */   }
/*     */ 
/*     */   public String getLocalPatternChars()
/*     */   {
/* 576 */     return this.localPatternChars;
/*     */   }
/*     */ 
/*     */   public void setLocalPatternChars(String paramString)
/*     */   {
/* 586 */     this.localPatternChars = paramString.toString();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 596 */       DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)super.clone();
/* 597 */       copyMembers(this, localDateFormatSymbols);
/* 598 */       return localDateFormatSymbols; } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 600 */     throw new InternalError();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 609 */     int i = 0;
/* 610 */     String[][] arrayOfString = getZoneStringsWrapper();
/* 611 */     for (int j = 0; j < arrayOfString[0].length; j++)
/* 612 */       i ^= arrayOfString[0][j].hashCode();
/* 613 */     return i;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 621 */     if (this == paramObject) return true;
/* 622 */     if ((paramObject == null) || (getClass() != paramObject.getClass())) return false;
/* 623 */     DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)paramObject;
/* 624 */     return (Arrays.equals(this.eras, localDateFormatSymbols.eras)) && (Arrays.equals(this.months, localDateFormatSymbols.months)) && (Arrays.equals(this.shortMonths, localDateFormatSymbols.shortMonths)) && (Arrays.equals(this.weekdays, localDateFormatSymbols.weekdays)) && (Arrays.equals(this.shortWeekdays, localDateFormatSymbols.shortWeekdays)) && (Arrays.equals(this.ampms, localDateFormatSymbols.ampms)) && (Arrays.deepEquals(getZoneStringsWrapper(), localDateFormatSymbols.getZoneStringsWrapper())) && (((this.localPatternChars != null) && (this.localPatternChars.equals(localDateFormatSymbols.localPatternChars))) || ((this.localPatternChars == null) && (localDateFormatSymbols.localPatternChars == null)));
/*     */   }
/*     */ 
/*     */   private void initializeData(Locale paramLocale)
/*     */   {
/* 651 */     this.locale = paramLocale;
/*     */ 
/* 654 */     SoftReference localSoftReference = (SoftReference)cachedInstances.get(this.locale);
/*     */     DateFormatSymbols localDateFormatSymbols;
/* 656 */     if ((localSoftReference != null) && ((localDateFormatSymbols = (DateFormatSymbols)localSoftReference.get()) != null)) {
/* 657 */       copyMembers(localDateFormatSymbols, this);
/* 658 */       return;
/*     */     }
/*     */ 
/* 662 */     ResourceBundle localResourceBundle = LocaleData.getDateFormatData(this.locale);
/*     */ 
/* 664 */     this.eras = localResourceBundle.getStringArray("Eras");
/* 665 */     this.months = localResourceBundle.getStringArray("MonthNames");
/* 666 */     this.shortMonths = localResourceBundle.getStringArray("MonthAbbreviations");
/* 667 */     this.ampms = localResourceBundle.getStringArray("AmPmMarkers");
/* 668 */     this.localPatternChars = localResourceBundle.getString("DateTimePatternChars");
/*     */ 
/* 671 */     this.weekdays = toOneBasedArray(localResourceBundle.getStringArray("DayNames"));
/* 672 */     this.shortWeekdays = toOneBasedArray(localResourceBundle.getStringArray("DayAbbreviations"));
/*     */   }
/*     */ 
/*     */   private static String[] toOneBasedArray(String[] paramArrayOfString) {
/* 676 */     int i = paramArrayOfString.length;
/* 677 */     String[] arrayOfString = new String[i + 1];
/* 678 */     arrayOfString[0] = "";
/* 679 */     for (int j = 0; j < i; j++) {
/* 680 */       arrayOfString[(j + 1)] = paramArrayOfString[j];
/*     */     }
/* 682 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   final int getZoneIndex(String paramString)
/*     */   {
/* 697 */     String[][] arrayOfString = getZoneStringsWrapper();
/* 698 */     for (int i = 0; i < arrayOfString.length; i++)
/*     */     {
/* 700 */       if (paramString.equals(arrayOfString[i][0])) return i;
/*     */     }
/*     */ 
/* 703 */     return -1;
/*     */   }
/*     */ 
/*     */   final String[][] getZoneStringsWrapper()
/*     */   {
/* 712 */     if (isSubclassObject()) {
/* 713 */       return getZoneStrings();
/*     */     }
/* 715 */     return getZoneStringsImpl(false);
/*     */   }
/*     */ 
/*     */   private final String[][] getZoneStringsImpl(boolean paramBoolean)
/*     */   {
/* 720 */     if (this.zoneStrings == null) {
/* 721 */       this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
/*     */     }
/*     */ 
/* 724 */     if (!paramBoolean) {
/* 725 */       return this.zoneStrings;
/*     */     }
/*     */ 
/* 728 */     int i = this.zoneStrings.length;
/* 729 */     String[][] arrayOfString; = new String[i][];
/* 730 */     for (int j = 0; j < i; j++) {
/* 731 */       arrayOfString;[j] = ((String[])Arrays.copyOf(this.zoneStrings[j], this.zoneStrings[j].length));
/*     */     }
/* 733 */     return arrayOfString;;
/*     */   }
/*     */ 
/*     */   private final boolean isSubclassObject() {
/* 737 */     return !getClass().getName().equals("java.text.DateFormatSymbols");
/*     */   }
/*     */ 
/*     */   private final void copyMembers(DateFormatSymbols paramDateFormatSymbols1, DateFormatSymbols paramDateFormatSymbols2)
/*     */   {
/* 748 */     paramDateFormatSymbols2.eras = ((String[])Arrays.copyOf(paramDateFormatSymbols1.eras, paramDateFormatSymbols1.eras.length));
/* 749 */     paramDateFormatSymbols2.months = ((String[])Arrays.copyOf(paramDateFormatSymbols1.months, paramDateFormatSymbols1.months.length));
/* 750 */     paramDateFormatSymbols2.shortMonths = ((String[])Arrays.copyOf(paramDateFormatSymbols1.shortMonths, paramDateFormatSymbols1.shortMonths.length));
/* 751 */     paramDateFormatSymbols2.weekdays = ((String[])Arrays.copyOf(paramDateFormatSymbols1.weekdays, paramDateFormatSymbols1.weekdays.length));
/* 752 */     paramDateFormatSymbols2.shortWeekdays = ((String[])Arrays.copyOf(paramDateFormatSymbols1.shortWeekdays, paramDateFormatSymbols1.shortWeekdays.length));
/* 753 */     paramDateFormatSymbols2.ampms = ((String[])Arrays.copyOf(paramDateFormatSymbols1.ampms, paramDateFormatSymbols1.ampms.length));
/* 754 */     if (paramDateFormatSymbols1.zoneStrings != null)
/* 755 */       paramDateFormatSymbols2.zoneStrings = paramDateFormatSymbols1.getZoneStringsImpl(true);
/*     */     else {
/* 757 */       paramDateFormatSymbols2.zoneStrings = ((String[][])null);
/*     */     }
/* 759 */     paramDateFormatSymbols2.localPatternChars = paramDateFormatSymbols1.localPatternChars;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 770 */     if (this.zoneStrings == null) {
/* 771 */       this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
/*     */     }
/* 773 */     paramObjectOutputStream.defaultWriteObject();
/*     */   }
/*     */ 
/*     */   private static class DateFormatSymbolsGetter
/*     */     implements LocaleServiceProviderPool.LocalizedObjectGetter<DateFormatSymbolsProvider, DateFormatSymbols>
/*     */   {
/* 783 */     private static final DateFormatSymbolsGetter INSTANCE = new DateFormatSymbolsGetter();
/*     */ 
/*     */     public DateFormatSymbols getObject(DateFormatSymbolsProvider paramDateFormatSymbolsProvider, Locale paramLocale, String paramString, Object[] paramArrayOfObject)
/*     */     {
/* 790 */       assert (paramArrayOfObject.length == 0);
/* 791 */       return paramDateFormatSymbolsProvider.getInstance(paramLocale);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.DateFormatSymbols
 * JD-Core Version:    0.6.2
 */