/*     */ package java.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.misc.JavaAWTAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.TimeZoneNameUtility;
/*     */ import sun.util.calendar.ZoneInfo;
/*     */ import sun.util.calendar.ZoneInfoFile;
/*     */ 
/*     */ public abstract class TimeZone
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   public static final int SHORT = 0;
/*     */   public static final int LONG = 1;
/*     */   private static final int ONE_MINUTE = 60000;
/*     */   private static final int ONE_HOUR = 3600000;
/*     */   private static final int ONE_DAY = 86400000;
/*     */   private static JavaAWTAccess javaAWTAccess;
/*     */   static final long serialVersionUID = 3581463369166924961L;
/* 804 */   static final TimeZone NO_TIMEZONE = null;
/*     */   private String ID;
/*     */   private static volatile TimeZone defaultTimeZone;
/*     */   static final String GMT_ID = "GMT";
/*     */   private static final int GMT_ID_LENGTH = 3;
/*     */   private static TimeZone mainAppContextDefault;
/*     */ 
/*     */   public abstract int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public int getOffset(long paramLong)
/*     */   {
/* 222 */     if (inDaylightTime(new Date(paramLong))) {
/* 223 */       return getRawOffset() + getDSTSavings();
/*     */     }
/* 225 */     return getRawOffset();
/*     */   }
/*     */ 
/*     */   int getOffsets(long paramLong, int[] paramArrayOfInt)
/*     */   {
/* 245 */     int i = getRawOffset();
/* 246 */     int j = 0;
/* 247 */     if (inDaylightTime(new Date(paramLong))) {
/* 248 */       j = getDSTSavings();
/*     */     }
/* 250 */     if (paramArrayOfInt != null) {
/* 251 */       paramArrayOfInt[0] = i;
/* 252 */       paramArrayOfInt[1] = j;
/*     */     }
/* 254 */     return i + j;
/*     */   }
/*     */ 
/*     */   public abstract void setRawOffset(int paramInt);
/*     */ 
/*     */   public abstract int getRawOffset();
/*     */ 
/*     */   public String getID()
/*     */   {
/* 295 */     return this.ID;
/*     */   }
/*     */ 
/*     */   public void setID(String paramString)
/*     */   {
/* 305 */     if (paramString == null) {
/* 306 */       throw new NullPointerException();
/*     */     }
/* 308 */     this.ID = paramString;
/*     */   }
/*     */ 
/*     */   public final String getDisplayName()
/*     */   {
/* 328 */     return getDisplayName(false, 1, Locale.getDefault(Locale.Category.DISPLAY));
/*     */   }
/*     */ 
/*     */   public final String getDisplayName(Locale paramLocale)
/*     */   {
/* 348 */     return getDisplayName(false, 1, paramLocale);
/*     */   }
/*     */ 
/*     */   public final String getDisplayName(boolean paramBoolean, int paramInt)
/*     */   {
/* 376 */     return getDisplayName(paramBoolean, paramInt, Locale.getDefault(Locale.Category.DISPLAY));
/*     */   }
/*     */ 
/*     */   public String getDisplayName(boolean paramBoolean, int paramInt, Locale paramLocale)
/*     */   {
/* 409 */     if ((paramInt != 0) && (paramInt != 1)) {
/* 410 */       throw new IllegalArgumentException("Illegal style: " + paramInt);
/*     */     }
/*     */ 
/* 413 */     String str = getID();
/* 414 */     String[] arrayOfString = getDisplayNames(str, paramLocale);
/* 415 */     if (arrayOfString == null) {
/* 416 */       if (str.startsWith("GMT")) {
/* 417 */         i = str.charAt(3);
/* 418 */         if ((i == 43) || (i == 45)) {
/* 419 */           return str;
/*     */         }
/*     */       }
/* 422 */       i = getRawOffset();
/* 423 */       if (paramBoolean) {
/* 424 */         i += getDSTSavings();
/*     */       }
/* 426 */       return ZoneInfoFile.toCustomID(i);
/*     */     }
/*     */ 
/* 429 */     int i = paramBoolean ? 3 : 1;
/* 430 */     if (paramInt == 0) {
/* 431 */       i++;
/*     */     }
/* 433 */     return arrayOfString[i];
/*     */   }
/*     */ 
/*     */   private static final String[] getDisplayNames(String paramString, Locale paramLocale)
/*     */   {
/* 445 */     Map localMap = DisplayNames.CACHE;
/*     */ 
/* 447 */     SoftReference localSoftReference = (SoftReference)localMap.get(paramString);
/*     */     Object localObject2;
/* 448 */     if (localSoftReference != null) {
/* 449 */       localObject1 = (Map)localSoftReference.get();
/* 450 */       if (localObject1 != null) {
/* 451 */         localObject2 = (String[])((Map)localObject1).get(paramLocale);
/* 452 */         if (localObject2 != null) {
/* 453 */           return localObject2;
/*     */         }
/* 455 */         localObject2 = TimeZoneNameUtility.retrieveDisplayNames(paramString, paramLocale);
/* 456 */         if (localObject2 != null) {
/* 457 */           ((Map)localObject1).put(paramLocale, localObject2);
/*     */         }
/* 459 */         return localObject2;
/*     */       }
/*     */     }
/*     */ 
/* 463 */     Object localObject1 = TimeZoneNameUtility.retrieveDisplayNames(paramString, paramLocale);
/* 464 */     if (localObject1 != null) {
/* 465 */       localObject2 = new ConcurrentHashMap();
/* 466 */       ((Map)localObject2).put(paramLocale, localObject1);
/* 467 */       localSoftReference = new SoftReference(localObject2);
/* 468 */       localMap.put(paramString, localSoftReference);
/*     */     }
/* 470 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public int getDSTSavings()
/*     */   {
/* 500 */     if (useDaylightTime()) {
/* 501 */       return 3600000;
/*     */     }
/* 503 */     return 0;
/*     */   }
/*     */ 
/*     */   public abstract boolean useDaylightTime();
/*     */ 
/*     */   public boolean observesDaylightTime()
/*     */   {
/* 542 */     return (useDaylightTime()) || (inDaylightTime(new Date()));
/*     */   }
/*     */ 
/*     */   public abstract boolean inDaylightTime(Date paramDate);
/*     */ 
/*     */   public static synchronized TimeZone getTimeZone(String paramString)
/*     */   {
/* 567 */     return getTimeZone(paramString, true);
/*     */   }
/*     */ 
/*     */   private static TimeZone getTimeZone(String paramString, boolean paramBoolean) {
/* 571 */     Object localObject = ZoneInfo.getTimeZone(paramString);
/* 572 */     if (localObject == null) {
/* 573 */       localObject = parseCustomTimeZone(paramString);
/* 574 */       if ((localObject == null) && (paramBoolean)) {
/* 575 */         localObject = new ZoneInfo("GMT", 0);
/*     */       }
/*     */     }
/* 578 */     return localObject;
/*     */   }
/*     */ 
/*     */   public static synchronized String[] getAvailableIDs(int paramInt)
/*     */   {
/* 591 */     return ZoneInfo.getAvailableIDs(paramInt);
/*     */   }
/*     */ 
/*     */   public static synchronized String[] getAvailableIDs()
/*     */   {
/* 599 */     return ZoneInfo.getAvailableIDs();
/*     */   }
/*     */ 
/*     */   private static native String getSystemTimeZoneID(String paramString1, String paramString2);
/*     */ 
/*     */   private static native String getSystemGMTOffsetID();
/*     */ 
/*     */   public static TimeZone getDefault()
/*     */   {
/* 622 */     return (TimeZone)getDefaultRef().clone();
/*     */   }
/*     */ 
/*     */   static TimeZone getDefaultRef()
/*     */   {
/* 630 */     TimeZone localTimeZone = getDefaultInAppContext();
/* 631 */     if (localTimeZone == null) {
/* 632 */       localTimeZone = defaultTimeZone;
/* 633 */       if (localTimeZone == null)
/*     */       {
/* 635 */         localTimeZone = setDefaultZone();
/* 636 */         assert (localTimeZone != null);
/*     */       }
/*     */     }
/*     */ 
/* 640 */     return localTimeZone;
/*     */   }
/*     */ 
/*     */   private static synchronized TimeZone setDefaultZone() {
/* 644 */     TimeZone localTimeZone = null;
/*     */ 
/* 646 */     Object localObject1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.timezone"));
/*     */ 
/* 651 */     if ((localObject1 == null) || (((String)localObject1).equals(""))) {
/* 652 */       localObject2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country"));
/*     */ 
/* 654 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
/*     */       try
/*     */       {
/* 657 */         localObject1 = getSystemTimeZoneID(str, (String)localObject2);
/* 658 */         if (localObject1 == null)
/* 659 */           localObject1 = "GMT";
/*     */       }
/*     */       catch (NullPointerException localNullPointerException) {
/* 662 */         localObject1 = "GMT";
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 668 */     localTimeZone = getTimeZone((String)localObject1, false);
/*     */ 
/* 670 */     if (localTimeZone == null)
/*     */     {
/* 674 */       localObject2 = getSystemGMTOffsetID();
/* 675 */       if (localObject2 != null) {
/* 676 */         localObject1 = localObject2;
/*     */       }
/* 678 */       localTimeZone = getTimeZone((String)localObject1, true);
/*     */     }
/* 680 */     assert (localTimeZone != null);
/*     */ 
/* 682 */     Object localObject2 = localObject1;
/* 683 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 685 */         System.setProperty("user.timezone", this.val$id);
/* 686 */         return null;
/*     */       }
/*     */     });
/* 690 */     defaultTimeZone = localTimeZone;
/* 691 */     return localTimeZone;
/*     */   }
/*     */ 
/*     */   private static boolean hasPermission() {
/* 695 */     boolean bool = true;
/* 696 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 697 */     if (localSecurityManager != null) {
/*     */       try {
/* 699 */         localSecurityManager.checkPermission(new PropertyPermission("user.timezone", "write"));
/*     */       }
/*     */       catch (SecurityException localSecurityException) {
/* 702 */         bool = false;
/*     */       }
/*     */     }
/* 705 */     return bool;
/*     */   }
/*     */ 
/*     */   public static void setDefault(TimeZone paramTimeZone)
/*     */   {
/* 718 */     if (hasPermission())
/* 719 */       synchronized (TimeZone.class) {
/* 720 */         defaultTimeZone = paramTimeZone;
/* 721 */         setDefaultInAppContext(null);
/*     */       }
/*     */     else
/* 724 */       setDefaultInAppContext(paramTimeZone);
/*     */   }
/*     */ 
/*     */   private static synchronized TimeZone getDefaultInAppContext()
/*     */   {
/* 734 */     javaAWTAccess = SharedSecrets.getJavaAWTAccess();
/* 735 */     if (javaAWTAccess == null) {
/* 736 */       return mainAppContextDefault;
/*     */     }
/* 738 */     if (!javaAWTAccess.isDisposed()) {
/* 739 */       TimeZone localTimeZone = (TimeZone)javaAWTAccess.get(TimeZone.class);
/*     */ 
/* 741 */       if ((localTimeZone == null) && (javaAWTAccess.isMainAppContext())) {
/* 742 */         return mainAppContextDefault;
/*     */       }
/* 744 */       return localTimeZone;
/*     */     }
/*     */ 
/* 748 */     return null;
/*     */   }
/*     */ 
/*     */   private static synchronized void setDefaultInAppContext(TimeZone paramTimeZone)
/*     */   {
/* 758 */     javaAWTAccess = SharedSecrets.getJavaAWTAccess();
/* 759 */     if (javaAWTAccess == null) {
/* 760 */       mainAppContextDefault = paramTimeZone;
/*     */     }
/* 762 */     else if (!javaAWTAccess.isDisposed()) {
/* 763 */       javaAWTAccess.put(TimeZone.class, paramTimeZone);
/* 764 */       if (javaAWTAccess.isMainAppContext())
/* 765 */         mainAppContextDefault = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasSameRules(TimeZone paramTimeZone)
/*     */   {
/* 781 */     return (paramTimeZone != null) && (getRawOffset() == paramTimeZone.getRawOffset()) && (useDaylightTime() == paramTimeZone.useDaylightTime());
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 793 */       TimeZone localTimeZone = (TimeZone)super.clone();
/* 794 */       localTimeZone.ID = this.ID;
/* 795 */       return localTimeZone; } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 797 */     throw new InternalError();
/*     */   }
/*     */ 
/*     */   private static final TimeZone parseCustomTimeZone(String paramString)
/*     */   {
/*     */     int i;
/* 838 */     if (((i = paramString.length()) < 5) || (paramString.indexOf("GMT") != 0))
/*     */     {
/* 840 */       return null;
/*     */     }
/*     */ 
/* 848 */     ZoneInfo localZoneInfo = ZoneInfoFile.getZoneInfo(paramString);
/* 849 */     if (localZoneInfo != null) {
/* 850 */       return localZoneInfo;
/*     */     }
/*     */ 
/* 853 */     int j = 3;
/* 854 */     int k = 0;
/* 855 */     int m = paramString.charAt(j++);
/* 856 */     if (m == 45)
/* 857 */       k = 1;
/* 858 */     else if (m != 43) {
/* 859 */       return null;
/*     */     }
/*     */ 
/* 862 */     int n = 0;
/* 863 */     int i1 = 0;
/* 864 */     int i2 = 0;
/* 865 */     int i3 = 0;
/* 866 */     while (j < i) {
/* 867 */       m = paramString.charAt(j++);
/* 868 */       if (m == 58) {
/* 869 */         if (i2 > 0) {
/* 870 */           return null;
/*     */         }
/* 872 */         if (i3 > 2) {
/* 873 */           return null;
/*     */         }
/* 875 */         n = i1;
/* 876 */         i2++;
/* 877 */         i1 = 0;
/* 878 */         i3 = 0;
/*     */       }
/*     */       else {
/* 881 */         if ((m < 48) || (m > 57)) {
/* 882 */           return null;
/*     */         }
/* 884 */         i1 = i1 * 10 + (m - 48);
/* 885 */         i3++;
/*     */       }
/*     */     }
/* 887 */     if (j != i) {
/* 888 */       return null;
/*     */     }
/* 890 */     if (i2 == 0) {
/* 891 */       if (i3 <= 2) {
/* 892 */         n = i1;
/* 893 */         i1 = 0;
/*     */       } else {
/* 895 */         n = i1 / 100;
/* 896 */         i1 %= 100;
/*     */       }
/*     */     }
/* 899 */     else if (i3 != 2) {
/* 900 */       return null;
/*     */     }
/*     */ 
/* 903 */     if ((n > 23) || (i1 > 59)) {
/* 904 */       return null;
/*     */     }
/* 906 */     int i4 = (n * 60 + i1) * 60 * 1000;
/*     */ 
/* 908 */     if (i4 == 0) {
/* 909 */       localZoneInfo = ZoneInfoFile.getZoneInfo("GMT");
/* 910 */       if (k != 0)
/* 911 */         localZoneInfo.setID("GMT-00:00");
/*     */       else
/* 913 */         localZoneInfo.setID("GMT+00:00");
/*     */     }
/*     */     else {
/* 916 */       localZoneInfo = ZoneInfoFile.getCustomTimeZone(paramString, k != 0 ? -i4 : i4);
/*     */     }
/* 918 */     return localZoneInfo;
/*     */   }
/*     */ 
/*     */   private static class DisplayNames
/*     */   {
/* 440 */     private static final Map<String, SoftReference<Map<Locale, String[]>>> CACHE = new ConcurrentHashMap();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.TimeZone
 * JD-Core Version:    0.6.2
 */