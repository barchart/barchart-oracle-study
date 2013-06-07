/*     */ package sun.util.locale;
/*     */ 
/*     */ public final class BaseLocale
/*     */ {
/*     */   public static final String SEP = "_";
/*  40 */   private static final Cache CACHE = new Cache();
/*     */   private final String language;
/*     */   private final String script;
/*     */   private final String region;
/*     */   private final String variant;
/*  47 */   private volatile int hash = 0;
/*     */ 
/*     */   private BaseLocale(String paramString1, String paramString2)
/*     */   {
/*  51 */     this.language = paramString1;
/*  52 */     this.script = "";
/*  53 */     this.region = paramString2;
/*  54 */     this.variant = "";
/*     */   }
/*     */ 
/*     */   private BaseLocale(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  58 */     this.language = (paramString1 != null ? LocaleUtils.toLowerString(paramString1).intern() : "");
/*  59 */     this.script = (paramString2 != null ? LocaleUtils.toTitleString(paramString2).intern() : "");
/*  60 */     this.region = (paramString3 != null ? LocaleUtils.toUpperString(paramString3).intern() : "");
/*  61 */     this.variant = (paramString4 != null ? paramString4.intern() : "");
/*     */   }
/*     */ 
/*     */   public static BaseLocale createInstance(String paramString1, String paramString2)
/*     */   {
/*  67 */     BaseLocale localBaseLocale = new BaseLocale(paramString1, paramString2);
/*  68 */     CACHE.put(new Key(paramString1, paramString2, null), localBaseLocale);
/*  69 */     return localBaseLocale;
/*     */   }
/*     */ 
/*     */   public static BaseLocale getInstance(String paramString1, String paramString2, String paramString3, String paramString4)
/*     */   {
/*  75 */     if (paramString1 != null) {
/*  76 */       if (LocaleUtils.caseIgnoreMatch(paramString1, "he"))
/*  77 */         paramString1 = "iw";
/*  78 */       else if (LocaleUtils.caseIgnoreMatch(paramString1, "yi"))
/*  79 */         paramString1 = "ji";
/*  80 */       else if (LocaleUtils.caseIgnoreMatch(paramString1, "id")) {
/*  81 */         paramString1 = "in";
/*     */       }
/*     */     }
/*     */ 
/*  85 */     Key localKey = new Key(paramString1, paramString2, paramString3, paramString4);
/*  86 */     BaseLocale localBaseLocale = (BaseLocale)CACHE.get(localKey);
/*  87 */     return localBaseLocale;
/*     */   }
/*     */ 
/*     */   public String getLanguage() {
/*  91 */     return this.language;
/*     */   }
/*     */ 
/*     */   public String getScript() {
/*  95 */     return this.script;
/*     */   }
/*     */ 
/*     */   public String getRegion() {
/*  99 */     return this.region;
/*     */   }
/*     */ 
/*     */   public String getVariant() {
/* 103 */     return this.variant;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 108 */     if (this == paramObject) {
/* 109 */       return true;
/*     */     }
/* 111 */     if (!(paramObject instanceof BaseLocale)) {
/* 112 */       return false;
/*     */     }
/* 114 */     BaseLocale localBaseLocale = (BaseLocale)paramObject;
/* 115 */     return (this.language == localBaseLocale.language) && (this.script == localBaseLocale.script) && (this.region == localBaseLocale.region) && (this.variant == localBaseLocale.variant);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 123 */     StringBuilder localStringBuilder = new StringBuilder();
/* 124 */     if (this.language.length() > 0) {
/* 125 */       localStringBuilder.append("language=");
/* 126 */       localStringBuilder.append(this.language);
/*     */     }
/* 128 */     if (this.script.length() > 0) {
/* 129 */       if (localStringBuilder.length() > 0) {
/* 130 */         localStringBuilder.append(", ");
/*     */       }
/* 132 */       localStringBuilder.append("script=");
/* 133 */       localStringBuilder.append(this.script);
/*     */     }
/* 135 */     if (this.region.length() > 0) {
/* 136 */       if (localStringBuilder.length() > 0) {
/* 137 */         localStringBuilder.append(", ");
/*     */       }
/* 139 */       localStringBuilder.append("region=");
/* 140 */       localStringBuilder.append(this.region);
/*     */     }
/* 142 */     if (this.variant.length() > 0) {
/* 143 */       if (localStringBuilder.length() > 0) {
/* 144 */         localStringBuilder.append(", ");
/*     */       }
/* 146 */       localStringBuilder.append("variant=");
/* 147 */       localStringBuilder.append(this.variant);
/*     */     }
/* 149 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 154 */     int i = this.hash;
/* 155 */     if (i == 0)
/*     */     {
/* 157 */       i = this.language.hashCode();
/* 158 */       i = 31 * i + this.script.hashCode();
/* 159 */       i = 31 * i + this.region.hashCode();
/* 160 */       i = 31 * i + this.variant.hashCode();
/* 161 */       this.hash = i;
/*     */     }
/* 163 */     return i;
/*     */   }
/*     */ 
/*     */   private static class Cache extends LocaleObjectCache<BaseLocale.Key, BaseLocale>
/*     */   {
/*     */     protected BaseLocale.Key normalizeKey(BaseLocale.Key paramKey)
/*     */     {
/* 297 */       return BaseLocale.Key.normalize(paramKey);
/*     */     }
/*     */ 
/*     */     protected BaseLocale createObject(BaseLocale.Key paramKey)
/*     */     {
/* 302 */       return new BaseLocale(BaseLocale.Key.access$100(paramKey), BaseLocale.Key.access$200(paramKey), BaseLocale.Key.access$300(paramKey), BaseLocale.Key.access$400(paramKey), null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class Key
/*     */     implements Comparable<Key>
/*     */   {
/*     */     private final String lang;
/*     */     private final String scrt;
/*     */     private final String regn;
/*     */     private final String vart;
/*     */     private final boolean normalized;
/*     */     private final int hash;
/*     */ 
/*     */     private Key(String paramString1, String paramString2)
/*     */     {
/* 179 */       assert ((paramString1.intern() == paramString1) && (paramString2.intern() == paramString2));
/*     */ 
/* 182 */       this.lang = paramString1;
/* 183 */       this.scrt = "";
/* 184 */       this.regn = paramString2;
/* 185 */       this.vart = "";
/* 186 */       this.normalized = true;
/*     */ 
/* 188 */       int i = paramString1.hashCode();
/* 189 */       if (paramString2 != "") {
/* 190 */         int j = paramString2.length();
/* 191 */         for (int k = 0; k < j; k++) {
/* 192 */           i = 31 * i + LocaleUtils.toLower(paramString2.charAt(k));
/*     */         }
/*     */       }
/* 195 */       this.hash = i;
/*     */     }
/*     */ 
/*     */     public Key(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 199 */       this(paramString1, paramString2, paramString3, paramString4, false);
/*     */     }
/*     */ 
/*     */     private Key(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
/*     */     {
/* 204 */       int i = 0;
/*     */       int j;
/*     */       int k;
/* 205 */       if (paramString1 != null) {
/* 206 */         this.lang = paramString1;
/* 207 */         j = paramString1.length();
/* 208 */         for (k = 0; k < j; k++)
/* 209 */           i = 31 * i + LocaleUtils.toLower(paramString1.charAt(k));
/*     */       }
/*     */       else {
/* 212 */         this.lang = "";
/*     */       }
/* 214 */       if (paramString2 != null) {
/* 215 */         this.scrt = paramString2;
/* 216 */         j = paramString2.length();
/* 217 */         for (k = 0; k < j; k++)
/* 218 */           i = 31 * i + LocaleUtils.toLower(paramString2.charAt(k));
/*     */       }
/*     */       else {
/* 221 */         this.scrt = "";
/*     */       }
/* 223 */       if (paramString3 != null) {
/* 224 */         this.regn = paramString3;
/* 225 */         j = paramString3.length();
/* 226 */         for (k = 0; k < j; k++)
/* 227 */           i = 31 * i + LocaleUtils.toLower(paramString3.charAt(k));
/*     */       }
/*     */       else {
/* 230 */         this.regn = "";
/*     */       }
/* 232 */       if (paramString4 != null) {
/* 233 */         this.vart = paramString4;
/* 234 */         j = paramString4.length();
/* 235 */         for (k = 0; k < j; k++)
/* 236 */           i = 31 * i + paramString4.charAt(k);
/*     */       }
/*     */       else {
/* 239 */         this.vart = "";
/*     */       }
/* 241 */       this.hash = i;
/* 242 */       this.normalized = paramBoolean;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 247 */       return (this == paramObject) || (((paramObject instanceof Key)) && (this.hash == ((Key)paramObject).hash) && (LocaleUtils.caseIgnoreMatch(((Key)paramObject).lang, this.lang)) && (LocaleUtils.caseIgnoreMatch(((Key)paramObject).scrt, this.scrt)) && (LocaleUtils.caseIgnoreMatch(((Key)paramObject).regn, this.regn)) && (((Key)paramObject).vart.equals(this.vart)));
/*     */     }
/*     */ 
/*     */     public int compareTo(Key paramKey)
/*     */     {
/* 258 */       int i = LocaleUtils.caseIgnoreCompare(this.lang, paramKey.lang);
/* 259 */       if (i == 0) {
/* 260 */         i = LocaleUtils.caseIgnoreCompare(this.scrt, paramKey.scrt);
/* 261 */         if (i == 0) {
/* 262 */           i = LocaleUtils.caseIgnoreCompare(this.regn, paramKey.regn);
/* 263 */           if (i == 0) {
/* 264 */             i = this.vart.compareTo(paramKey.vart);
/*     */           }
/*     */         }
/*     */       }
/* 268 */       return i;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 273 */       return this.hash;
/*     */     }
/*     */ 
/*     */     public static Key normalize(Key paramKey) {
/* 277 */       if (paramKey.normalized) {
/* 278 */         return paramKey;
/*     */       }
/*     */ 
/* 281 */       String str1 = LocaleUtils.toLowerString(paramKey.lang).intern();
/* 282 */       String str2 = LocaleUtils.toTitleString(paramKey.scrt).intern();
/* 283 */       String str3 = LocaleUtils.toUpperString(paramKey.regn).intern();
/* 284 */       String str4 = paramKey.vart.intern();
/*     */ 
/* 286 */       return new Key(str1, str2, str3, str4, true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.util.locale.BaseLocale
 * JD-Core Version:    0.6.2
 */