/*     */ package java.util.logging;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class Level
/*     */   implements Serializable
/*     */ {
/*  66 */   private static String defaultBundle = "sun.util.logging.resources.logging";
/*     */   private final String name;
/*     */   private final int value;
/*     */   private final String resourceBundleName;
/*     */   private String localizedLevelName;
/*  90 */   public static final Level OFF = new Level("OFF", 2147483647, defaultBundle);
/*     */ 
/* 101 */   public static final Level SEVERE = new Level("SEVERE", 1000, defaultBundle);
/*     */ 
/* 111 */   public static final Level WARNING = new Level("WARNING", 900, defaultBundle);
/*     */ 
/* 122 */   public static final Level INFO = new Level("INFO", 800, defaultBundle);
/*     */ 
/* 134 */   public static final Level CONFIG = new Level("CONFIG", 700, defaultBundle);
/*     */ 
/* 155 */   public static final Level FINE = new Level("FINE", 500, defaultBundle);
/*     */ 
/* 163 */   public static final Level FINER = new Level("FINER", 400, defaultBundle);
/*     */ 
/* 169 */   public static final Level FINEST = new Level("FINEST", 300, defaultBundle);
/*     */ 
/* 175 */   public static final Level ALL = new Level("ALL", -2147483648, defaultBundle);
/*     */   private static final long serialVersionUID = -8176160795706313070L;
/*     */ 
/*     */   protected Level(String paramString, int paramInt)
/*     */   {
/* 190 */     this(paramString, paramInt, null);
/*     */   }
/*     */ 
/*     */   protected Level(String paramString1, int paramInt, String paramString2)
/*     */   {
/* 205 */     if (paramString1 == null) {
/* 206 */       throw new NullPointerException();
/*     */     }
/* 208 */     this.name = paramString1;
/* 209 */     this.value = paramInt;
/* 210 */     this.resourceBundleName = paramString2;
/* 211 */     this.localizedLevelName = (paramString2 == null ? paramString1 : null);
/* 212 */     KnownLevel.add(this);
/*     */   }
/*     */ 
/*     */   public String getResourceBundleName()
/*     */   {
/* 222 */     return this.resourceBundleName;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 231 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getLocalizedName()
/*     */   {
/* 244 */     return getLocalizedLevelName();
/*     */   }
/*     */ 
/*     */   final String getLevelName()
/*     */   {
/* 250 */     return this.name;
/*     */   }
/*     */ 
/*     */   final synchronized String getLocalizedLevelName() {
/* 254 */     if (this.localizedLevelName != null) {
/* 255 */       return this.localizedLevelName;
/*     */     }
/*     */     try
/*     */     {
/* 259 */       ResourceBundle localResourceBundle = ResourceBundle.getBundle(this.resourceBundleName);
/* 260 */       this.localizedLevelName = localResourceBundle.getString(this.name);
/*     */     } catch (Exception localException) {
/* 262 */       this.localizedLevelName = this.name;
/*     */     }
/* 264 */     return this.localizedLevelName;
/*     */   }
/*     */ 
/*     */   static Level findLevel(String paramString)
/*     */   {
/* 279 */     if (paramString == null) {
/* 280 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 286 */     KnownLevel localKnownLevel = KnownLevel.findByName(paramString);
/* 287 */     if (localKnownLevel != null) {
/* 288 */       return localKnownLevel.mirroredLevel;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 295 */       int i = Integer.parseInt(paramString);
/* 296 */       localKnownLevel = KnownLevel.findByValue(i);
/* 297 */       if (localKnownLevel == null)
/*     */       {
/* 299 */         Level localLevel = new Level(paramString, i);
/* 300 */         localKnownLevel = KnownLevel.findByValue(i);
/*     */       }
/* 302 */       return localKnownLevel.mirroredLevel;
/*     */     }
/*     */     catch (NumberFormatException localNumberFormatException)
/*     */     {
/* 308 */       localKnownLevel = KnownLevel.findByLocalizedLevelName(paramString);
/* 309 */       if (localKnownLevel != null) {
/* 310 */         return localKnownLevel.mirroredLevel;
/*     */       }
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 322 */     return this.name;
/*     */   }
/*     */ 
/*     */   public final int intValue()
/*     */   {
/* 332 */     return this.value;
/*     */   }
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 340 */     KnownLevel localKnownLevel = KnownLevel.matches(this);
/* 341 */     if (localKnownLevel != null) {
/* 342 */       return localKnownLevel.levelObject;
/*     */     }
/*     */ 
/* 347 */     Level localLevel = new Level(this.name, this.value, this.resourceBundleName);
/* 348 */     return localLevel;
/*     */   }
/*     */ 
/*     */   public static synchronized Level parse(String paramString)
/*     */     throws IllegalArgumentException
/*     */   {
/* 380 */     paramString.length();
/*     */ 
/* 385 */     KnownLevel localKnownLevel = KnownLevel.findByName(paramString);
/* 386 */     if (localKnownLevel != null) {
/* 387 */       return localKnownLevel.levelObject;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 394 */       int i = Integer.parseInt(paramString);
/* 395 */       localKnownLevel = KnownLevel.findByValue(i);
/* 396 */       if (localKnownLevel == null)
/*     */       {
/* 398 */         Level localLevel = new Level(paramString, i);
/* 399 */         localKnownLevel = KnownLevel.findByValue(i);
/*     */       }
/* 401 */       return localKnownLevel.levelObject;
/*     */     }
/*     */     catch (NumberFormatException localNumberFormatException)
/*     */     {
/* 410 */       localKnownLevel = KnownLevel.findByLocalizedName(paramString);
/* 411 */       if (localKnownLevel != null) {
/* 412 */         return localKnownLevel.levelObject;
/*     */       }
/*     */     }
/*     */ 
/* 416 */     throw new IllegalArgumentException("Bad level \"" + paramString + "\"");
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*     */     try
/*     */     {
/* 425 */       Level localLevel = (Level)paramObject;
/* 426 */       return localLevel.value == this.value; } catch (Exception localException) {
/*     */     }
/* 428 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 437 */     return this.value;
/*     */   }
/*     */ 
/*     */   static final class KnownLevel
/*     */   {
/* 461 */     private static Map<String, List<KnownLevel>> nameToLevels = new HashMap();
/* 462 */     private static Map<Integer, List<KnownLevel>> intToLevels = new HashMap();
/*     */     final Level levelObject;
/*     */     final Level mirroredLevel;
/*     */ 
/*     */     KnownLevel(Level paramLevel)
/*     */     {
/* 466 */       this.levelObject = paramLevel;
/* 467 */       if (paramLevel.getClass() == Level.class)
/* 468 */         this.mirroredLevel = paramLevel;
/*     */       else
/* 470 */         this.mirroredLevel = new Level(paramLevel.name, paramLevel.value, paramLevel.resourceBundleName);
/*     */     }
/*     */ 
/*     */     static synchronized void add(Level paramLevel)
/*     */     {
/* 477 */       KnownLevel localKnownLevel = new KnownLevel(paramLevel);
/* 478 */       Object localObject = (List)nameToLevels.get(paramLevel.name);
/* 479 */       if (localObject == null) {
/* 480 */         localObject = new ArrayList();
/* 481 */         nameToLevels.put(paramLevel.name, localObject);
/*     */       }
/* 483 */       ((List)localObject).add(localKnownLevel);
/*     */ 
/* 485 */       localObject = (List)intToLevels.get(Integer.valueOf(paramLevel.value));
/* 486 */       if (localObject == null) {
/* 487 */         localObject = new ArrayList();
/* 488 */         intToLevels.put(Integer.valueOf(paramLevel.value), localObject);
/*     */       }
/* 490 */       ((List)localObject).add(localKnownLevel);
/*     */     }
/*     */ 
/*     */     static synchronized KnownLevel findByName(String paramString)
/*     */     {
/* 495 */       List localList = (List)nameToLevels.get(paramString);
/* 496 */       if (localList != null) {
/* 497 */         return (KnownLevel)localList.get(0);
/*     */       }
/* 499 */       return null;
/*     */     }
/*     */ 
/*     */     static synchronized KnownLevel findByValue(int paramInt)
/*     */     {
/* 504 */       List localList = (List)intToLevels.get(Integer.valueOf(paramInt));
/* 505 */       if (localList != null) {
/* 506 */         return (KnownLevel)localList.get(0);
/*     */       }
/* 508 */       return null;
/*     */     }
/*     */ 
/*     */     static synchronized KnownLevel findByLocalizedLevelName(String paramString)
/*     */     {
/* 517 */       for (List localList : nameToLevels.values()) {
/* 518 */         for (KnownLevel localKnownLevel : localList) {
/* 519 */           String str = localKnownLevel.levelObject.getLocalizedLevelName();
/* 520 */           if (paramString.equals(str)) {
/* 521 */             return localKnownLevel;
/*     */           }
/*     */         }
/*     */       }
/* 525 */       return null;
/*     */     }
/*     */ 
/*     */     static synchronized KnownLevel findByLocalizedName(String paramString)
/*     */     {
/* 531 */       for (List localList : nameToLevels.values()) {
/* 532 */         for (KnownLevel localKnownLevel : localList) {
/* 533 */           String str = localKnownLevel.levelObject.getLocalizedName();
/* 534 */           if (paramString.equals(str)) {
/* 535 */             return localKnownLevel;
/*     */           }
/*     */         }
/*     */       }
/* 539 */       return null;
/*     */     }
/*     */ 
/*     */     static synchronized KnownLevel matches(Level paramLevel) {
/* 543 */       List localList = (List)nameToLevels.get(paramLevel.name);
/* 544 */       if (localList != null) {
/* 545 */         for (KnownLevel localKnownLevel : localList) {
/* 546 */           Level localLevel = localKnownLevel.mirroredLevel;
/* 547 */           if ((paramLevel.value == localLevel.value) && ((paramLevel.resourceBundleName == localLevel.resourceBundleName) || ((paramLevel.resourceBundleName != null) && (paramLevel.resourceBundleName.equals(localLevel.resourceBundleName)))))
/*     */           {
/* 551 */             return localKnownLevel;
/*     */           }
/*     */         }
/*     */       }
/* 555 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.logging.Level
 * JD-Core Version:    0.6.2
 */