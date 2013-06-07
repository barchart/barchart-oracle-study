/*      */ package java.net;
/*      */ 
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Objects;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import sun.misc.JavaNetHttpCookieAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ 
/*      */ public final class HttpCookie
/*      */   implements Cloneable
/*      */ {
/*      */   private String name;
/*      */   private String value;
/*      */   private String comment;
/*      */   private String commentURL;
/*      */   private boolean toDiscard;
/*      */   private String domain;
/*   76 */   private long maxAge = -1L;
/*      */   private String path;
/*      */   private String portlist;
/*      */   private boolean secure;
/*      */   private boolean httpOnly;
/*   81 */   private int version = 1;
/*      */   private final String header;
/*   91 */   private long whenCreated = 0L;
/*      */   private static final long MAX_AGE_UNSPECIFIED = -1L;
/*  105 */   private static final String[] COOKIE_DATE_FORMATS = { "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE MMM dd yyyy HH:mm:ss 'GMT'Z" };
/*      */   private static final String SET_COOKIE = "set-cookie:";
/*      */   private static final String SET_COOKIE2 = "set-cookie2:";
/*      */   private static final String tspecials = ",;";
/*  980 */   static Map<String, CookieAttributeAssignor> assignors = null;
/*      */ 
/* 1118 */   static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*      */ 
/*      */   public HttpCookie(String paramString1, String paramString2)
/*      */   {
/*  152 */     this(paramString1, paramString2, null);
/*      */   }
/*      */ 
/*      */   private HttpCookie(String paramString1, String paramString2, String paramString3) {
/*  156 */     paramString1 = paramString1.trim();
/*  157 */     if ((paramString1.length() == 0) || (!isToken(paramString1)) || (paramString1.charAt(0) == '$')) {
/*  158 */       throw new IllegalArgumentException("Illegal cookie name");
/*      */     }
/*      */ 
/*  161 */     this.name = paramString1;
/*  162 */     this.value = paramString2;
/*  163 */     this.toDiscard = false;
/*  164 */     this.secure = false;
/*      */ 
/*  166 */     this.whenCreated = System.currentTimeMillis();
/*  167 */     this.portlist = null;
/*  168 */     this.header = paramString3;
/*      */   }
/*      */ 
/*      */   public static List<HttpCookie> parse(String paramString)
/*      */   {
/*  190 */     return parse(paramString, false);
/*      */   }
/*      */ 
/*      */   private static List<HttpCookie> parse(String paramString, boolean paramBoolean)
/*      */   {
/*  199 */     int i = guessCookieVersion(paramString);
/*      */ 
/*  202 */     if (startsWithIgnoreCase(paramString, "set-cookie2:"))
/*  203 */       paramString = paramString.substring("set-cookie2:".length());
/*  204 */     else if (startsWithIgnoreCase(paramString, "set-cookie:")) {
/*  205 */       paramString = paramString.substring("set-cookie:".length());
/*      */     }
/*      */ 
/*  209 */     ArrayList localArrayList = new ArrayList();
/*      */     Object localObject;
/*  213 */     if (i == 0)
/*      */     {
/*  215 */       localObject = parseInternal(paramString, paramBoolean);
/*  216 */       ((HttpCookie)localObject).setVersion(0);
/*  217 */       localArrayList.add(localObject);
/*      */     }
/*      */     else
/*      */     {
/*  222 */       localObject = splitMultiCookies(paramString);
/*  223 */       for (String str : (List)localObject) {
/*  224 */         HttpCookie localHttpCookie = parseInternal(str, paramBoolean);
/*  225 */         localHttpCookie.setVersion(1);
/*  226 */         localArrayList.add(localHttpCookie);
/*      */       }
/*      */     }
/*      */ 
/*  230 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   public boolean hasExpired()
/*      */   {
/*  246 */     if (this.maxAge == 0L) return true;
/*      */ 
/*  251 */     if (this.maxAge == -1L) return false;
/*      */ 
/*  253 */     long l = (System.currentTimeMillis() - this.whenCreated) / 1000L;
/*  254 */     if (l > this.maxAge) {
/*  255 */       return true;
/*      */     }
/*  257 */     return false;
/*      */   }
/*      */ 
/*      */   public void setComment(String paramString)
/*      */   {
/*  275 */     this.comment = paramString;
/*      */   }
/*      */ 
/*      */   public String getComment()
/*      */   {
/*  293 */     return this.comment;
/*      */   }
/*      */ 
/*      */   public void setCommentURL(String paramString)
/*      */   {
/*  311 */     this.commentURL = paramString;
/*      */   }
/*      */ 
/*      */   public String getCommentURL()
/*      */   {
/*  329 */     return this.commentURL;
/*      */   }
/*      */ 
/*      */   public void setDiscard(boolean paramBoolean)
/*      */   {
/*  343 */     this.toDiscard = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getDiscard()
/*      */   {
/*  358 */     return this.toDiscard;
/*      */   }
/*      */ 
/*      */   public void setPortlist(String paramString)
/*      */   {
/*  372 */     this.portlist = paramString;
/*      */   }
/*      */ 
/*      */   public String getPortlist()
/*      */   {
/*  387 */     return this.portlist;
/*      */   }
/*      */ 
/*      */   public void setDomain(String paramString)
/*      */   {
/*  411 */     if (paramString != null)
/*  412 */       this.domain = paramString.toLowerCase();
/*      */     else
/*  414 */       this.domain = paramString;
/*      */   }
/*      */ 
/*      */   public String getDomain()
/*      */   {
/*  432 */     return this.domain;
/*      */   }
/*      */ 
/*      */   public void setMaxAge(long paramLong)
/*      */   {
/*  458 */     this.maxAge = paramLong;
/*      */   }
/*      */ 
/*      */   public long getMaxAge()
/*      */   {
/*  479 */     return this.maxAge;
/*      */   }
/*      */ 
/*      */   public void setPath(String paramString)
/*      */   {
/*  507 */     this.path = paramString;
/*      */   }
/*      */ 
/*      */   public String getPath()
/*      */   {
/*  527 */     return this.path;
/*      */   }
/*      */ 
/*      */   public void setSecure(boolean paramBoolean)
/*      */   {
/*  549 */     this.secure = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getSecure()
/*      */   {
/*  568 */     return this.secure;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  584 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void setValue(String paramString)
/*      */   {
/*  610 */     this.value = paramString;
/*      */   }
/*      */ 
/*      */   public String getValue()
/*      */   {
/*  627 */     return this.value;
/*      */   }
/*      */ 
/*      */   public int getVersion()
/*      */   {
/*  650 */     return this.version;
/*      */   }
/*      */ 
/*      */   public void setVersion(int paramInt)
/*      */   {
/*  673 */     if ((paramInt != 0) && (paramInt != 1)) {
/*  674 */       throw new IllegalArgumentException("cookie version should be 0 or 1");
/*      */     }
/*      */ 
/*  677 */     this.version = paramInt;
/*      */   }
/*      */ 
/*      */   public boolean isHttpOnly()
/*      */   {
/*  690 */     return this.httpOnly;
/*      */   }
/*      */ 
/*      */   public void setHttpOnly(boolean paramBoolean)
/*      */   {
/*  704 */     this.httpOnly = paramBoolean;
/*      */   }
/*      */ 
/*      */   public static boolean domainMatches(String paramString1, String paramString2)
/*      */   {
/*  757 */     if ((paramString1 == null) || (paramString2 == null)) {
/*  758 */       return false;
/*      */     }
/*      */ 
/*  761 */     boolean bool = ".local".equalsIgnoreCase(paramString1);
/*  762 */     int i = paramString1.indexOf('.');
/*  763 */     if (i == 0)
/*  764 */       i = paramString1.indexOf('.', 1);
/*  765 */     if ((!bool) && ((i == -1) || (i == paramString1.length() - 1)))
/*      */     {
/*  767 */       return false;
/*      */     }
/*      */ 
/*  770 */     int j = paramString2.indexOf('.');
/*  771 */     if ((j == -1) && (bool)) {
/*  772 */       return true;
/*      */     }
/*  774 */     int k = paramString1.length();
/*  775 */     int m = paramString2.length() - k;
/*  776 */     if (m == 0)
/*      */     {
/*  778 */       return paramString2.equalsIgnoreCase(paramString1);
/*      */     }
/*  780 */     if (m > 0)
/*      */     {
/*  782 */       String str1 = paramString2.substring(0, m);
/*  783 */       String str2 = paramString2.substring(m);
/*      */ 
/*  785 */       return (str1.indexOf('.') == -1) && (str2.equalsIgnoreCase(paramString1));
/*      */     }
/*  787 */     if (m == -1)
/*      */     {
/*  789 */       return (paramString1.charAt(0) == '.') && (paramString2.equalsIgnoreCase(paramString1.substring(1)));
/*      */     }
/*      */ 
/*  793 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  806 */     if (getVersion() > 0) {
/*  807 */       return toRFC2965HeaderString();
/*      */     }
/*  809 */     return toNetscapeHeaderString();
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  827 */     if (paramObject == this)
/*  828 */       return true;
/*  829 */     if (!(paramObject instanceof HttpCookie))
/*  830 */       return false;
/*  831 */     HttpCookie localHttpCookie = (HttpCookie)paramObject;
/*      */ 
/*  837 */     return (equalsIgnoreCase(getName(), localHttpCookie.getName())) && (equalsIgnoreCase(getDomain(), localHttpCookie.getDomain())) && (Objects.equals(getPath(), localHttpCookie.getPath()));
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  858 */     int i = this.name.toLowerCase().hashCode();
/*  859 */     int j = this.domain != null ? this.domain.toLowerCase().hashCode() : 0;
/*  860 */     int k = this.path != null ? this.path.hashCode() : 0;
/*      */ 
/*  862 */     return i + j + k;
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  873 */       return super.clone();
/*      */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*  875 */       throw new RuntimeException(localCloneNotSupportedException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isToken(String paramString)
/*      */   {
/*  899 */     int i = paramString.length();
/*      */ 
/*  901 */     for (int j = 0; j < i; j++) {
/*  902 */       int k = paramString.charAt(j);
/*      */ 
/*  904 */       if ((k < 32) || (k >= 127) || (",;".indexOf(k) != -1))
/*  905 */         return false;
/*      */     }
/*  907 */     return true;
/*      */   }
/*      */ 
/*      */   private static HttpCookie parseInternal(String paramString, boolean paramBoolean)
/*      */   {
/*  924 */     HttpCookie localHttpCookie = null;
/*  925 */     String str1 = null;
/*      */ 
/*  927 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ";");
/*      */     String str2;
/*      */     String str3;
/*      */     try
/*      */     {
/*  932 */       str1 = localStringTokenizer.nextToken();
/*  933 */       int i = str1.indexOf('=');
/*  934 */       if (i != -1) {
/*  935 */         str2 = str1.substring(0, i).trim();
/*  936 */         str3 = str1.substring(i + 1).trim();
/*  937 */         if (paramBoolean) {
/*  938 */           localHttpCookie = new HttpCookie(str2, stripOffSurroundingQuote(str3), paramString);
/*      */         }
/*      */         else
/*      */         {
/*  942 */           localHttpCookie = new HttpCookie(str2, stripOffSurroundingQuote(str3));
/*      */         }
/*      */       }
/*      */       else {
/*  946 */         throw new IllegalArgumentException("Invalid cookie name-value pair");
/*      */       }
/*      */     } catch (NoSuchElementException localNoSuchElementException) {
/*  949 */       throw new IllegalArgumentException("Empty cookie header string");
/*      */     }
/*      */ 
/*  953 */     while (localStringTokenizer.hasMoreTokens()) {
/*  954 */       str1 = localStringTokenizer.nextToken();
/*  955 */       int j = str1.indexOf('=');
/*      */ 
/*  957 */       if (j != -1) {
/*  958 */         str2 = str1.substring(0, j).trim();
/*  959 */         str3 = str1.substring(j + 1).trim();
/*      */       } else {
/*  961 */         str2 = str1.trim();
/*  962 */         str3 = null;
/*      */       }
/*      */ 
/*  966 */       assignAttribute(localHttpCookie, str2, str3);
/*      */     }
/*      */ 
/*  969 */     return localHttpCookie;
/*      */   }
/*      */ 
/*      */   private static void assignAttribute(HttpCookie paramHttpCookie, String paramString1, String paramString2)
/*      */   {
/* 1056 */     paramString2 = stripOffSurroundingQuote(paramString2);
/*      */ 
/* 1058 */     CookieAttributeAssignor localCookieAttributeAssignor = (CookieAttributeAssignor)assignors.get(paramString1.toLowerCase());
/* 1059 */     if (localCookieAttributeAssignor != null)
/* 1060 */       localCookieAttributeAssignor.assign(paramHttpCookie, paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   private String header()
/*      */   {
/* 1085 */     return this.header;
/*      */   }
/*      */ 
/*      */   private String toNetscapeHeaderString()
/*      */   {
/* 1093 */     StringBuilder localStringBuilder = new StringBuilder();
/*      */ 
/* 1095 */     localStringBuilder.append(getName() + "=" + getValue());
/*      */ 
/* 1097 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private String toRFC2965HeaderString()
/*      */   {
/* 1105 */     StringBuilder localStringBuilder = new StringBuilder();
/*      */ 
/* 1107 */     localStringBuilder.append(getName()).append("=\"").append(getValue()).append('"');
/* 1108 */     if (getPath() != null)
/* 1109 */       localStringBuilder.append(";$Path=\"").append(getPath()).append('"');
/* 1110 */     if (getDomain() != null)
/* 1111 */       localStringBuilder.append(";$Domain=\"").append(getDomain()).append('"');
/* 1112 */     if (getPortlist() != null) {
/* 1113 */       localStringBuilder.append(";$Port=\"").append(getPortlist()).append('"');
/*      */     }
/* 1115 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private long expiryDate2DeltaSeconds(String paramString)
/*      */   {
/* 1128 */     for (int i = 0; i < COOKIE_DATE_FORMATS.length; i++) {
/* 1129 */       SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(COOKIE_DATE_FORMATS[i], Locale.US);
/* 1130 */       localSimpleDateFormat.setTimeZone(GMT);
/*      */       try {
/* 1132 */         Date localDate = localSimpleDateFormat.parse(paramString);
/* 1133 */         return (localDate.getTime() - this.whenCreated) / 1000L;
/*      */       }
/*      */       catch (Exception localException) {
/*      */       }
/*      */     }
/* 1138 */     return 0L;
/*      */   }
/*      */ 
/*      */   private static int guessCookieVersion(String paramString)
/*      */   {
/* 1147 */     int i = 0;
/*      */ 
/* 1149 */     paramString = paramString.toLowerCase();
/* 1150 */     if (paramString.indexOf("expires=") != -1)
/*      */     {
/* 1152 */       i = 0;
/* 1153 */     } else if (paramString.indexOf("version=") != -1)
/*      */     {
/* 1155 */       i = 1;
/* 1156 */     } else if (paramString.indexOf("max-age") != -1)
/*      */     {
/* 1158 */       i = 1;
/* 1159 */     } else if (startsWithIgnoreCase(paramString, "set-cookie2:"))
/*      */     {
/* 1161 */       i = 1;
/*      */     }
/*      */ 
/* 1164 */     return i;
/*      */   }
/*      */ 
/*      */   private static String stripOffSurroundingQuote(String paramString) {
/* 1168 */     if ((paramString != null) && (paramString.length() > 2) && (paramString.charAt(0) == '"') && (paramString.charAt(paramString.length() - 1) == '"'))
/*      */     {
/* 1170 */       return paramString.substring(1, paramString.length() - 1);
/*      */     }
/* 1172 */     if ((paramString != null) && (paramString.length() > 2) && (paramString.charAt(0) == '\'') && (paramString.charAt(paramString.length() - 1) == '\''))
/*      */     {
/* 1174 */       return paramString.substring(1, paramString.length() - 1);
/*      */     }
/* 1176 */     return paramString;
/*      */   }
/*      */ 
/*      */   private static boolean equalsIgnoreCase(String paramString1, String paramString2) {
/* 1180 */     if (paramString1 == paramString2) return true;
/* 1181 */     if ((paramString1 != null) && (paramString2 != null)) {
/* 1182 */       return paramString1.equalsIgnoreCase(paramString2);
/*      */     }
/* 1184 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean startsWithIgnoreCase(String paramString1, String paramString2) {
/* 1188 */     if ((paramString1 == null) || (paramString2 == null)) return false;
/*      */ 
/* 1190 */     if ((paramString1.length() >= paramString2.length()) && (paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length()))))
/*      */     {
/* 1192 */       return true;
/*      */     }
/*      */ 
/* 1195 */     return false;
/*      */   }
/*      */ 
/*      */   private static List<String> splitMultiCookies(String paramString)
/*      */   {
/* 1210 */     ArrayList localArrayList = new ArrayList();
/* 1211 */     int i = 0;
/*      */ 
/* 1214 */     int j = 0; for (int k = 0; j < paramString.length(); j++) {
/* 1215 */       int m = paramString.charAt(j);
/* 1216 */       if (m == 34) i++;
/* 1217 */       if ((m == 44) && (i % 2 == 0)) {
/* 1218 */         localArrayList.add(paramString.substring(k, j));
/* 1219 */         k = j + 1;
/*      */       }
/*      */     }
/*      */ 
/* 1223 */     localArrayList.add(paramString.substring(k));
/*      */ 
/* 1225 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  982 */     assignors = new HashMap();
/*  983 */     assignors.put("comment", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/*  985 */         if (paramAnonymousHttpCookie.getComment() == null) paramAnonymousHttpCookie.setComment(paramAnonymousString2);
/*      */       }
/*      */     });
/*  988 */     assignors.put("commenturl", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/*  990 */         if (paramAnonymousHttpCookie.getCommentURL() == null) paramAnonymousHttpCookie.setCommentURL(paramAnonymousString2);
/*      */       }
/*      */     });
/*  993 */     assignors.put("discard", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/*  995 */         paramAnonymousHttpCookie.setDiscard(true);
/*      */       }
/*      */     });
/*  998 */     assignors.put("domain", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1000 */         if (paramAnonymousHttpCookie.getDomain() == null) paramAnonymousHttpCookie.setDomain(paramAnonymousString2);
/*      */       }
/*      */     });
/* 1003 */     assignors.put("max-age", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/*      */         try {
/* 1006 */           long l = Long.parseLong(paramAnonymousString2);
/* 1007 */           if (paramAnonymousHttpCookie.getMaxAge() == -1L) paramAnonymousHttpCookie.setMaxAge(l); 
/*      */         }
/* 1009 */         catch (NumberFormatException localNumberFormatException) { throw new IllegalArgumentException("Illegal cookie max-age attribute"); }
/*      */ 
/*      */       }
/*      */     });
/* 1013 */     assignors.put("path", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1015 */         if (paramAnonymousHttpCookie.getPath() == null) paramAnonymousHttpCookie.setPath(paramAnonymousString2);
/*      */       }
/*      */     });
/* 1018 */     assignors.put("port", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1020 */         if (paramAnonymousHttpCookie.getPortlist() == null) paramAnonymousHttpCookie.setPortlist(paramAnonymousString2 == null ? "" : paramAnonymousString2);
/*      */       }
/*      */     });
/* 1023 */     assignors.put("secure", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1025 */         paramAnonymousHttpCookie.setSecure(true);
/*      */       }
/*      */     });
/* 1028 */     assignors.put("httponly", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1030 */         paramAnonymousHttpCookie.setHttpOnly(true);
/*      */       }
/*      */     });
/* 1033 */     assignors.put("version", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/*      */         try {
/* 1036 */           int i = Integer.parseInt(paramAnonymousString2);
/* 1037 */           paramAnonymousHttpCookie.setVersion(i);
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException)
/*      */         {
/*      */         }
/*      */       }
/*      */     });
/* 1043 */     assignors.put("expires", new CookieAttributeAssignor() {
/*      */       public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2) {
/* 1045 */         if (paramAnonymousHttpCookie.getMaxAge() == -1L)
/* 1046 */           paramAnonymousHttpCookie.setMaxAge(paramAnonymousHttpCookie.expiryDate2DeltaSeconds(paramAnonymousString2));
/*      */       }
/*      */     });
/* 1067 */     SharedSecrets.setJavaNetHttpCookieAccess(new JavaNetHttpCookieAccess()
/*      */     {
/*      */       public List<HttpCookie> parse(String paramAnonymousString) {
/* 1070 */         return HttpCookie.parse(paramAnonymousString, true);
/*      */       }
/*      */ 
/*      */       public String header(HttpCookie paramAnonymousHttpCookie) {
/* 1074 */         return paramAnonymousHttpCookie.header;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   static abstract interface CookieAttributeAssignor
/*      */   {
/*      */     public abstract void assign(HttpCookie paramHttpCookie, String paramString1, String paramString2);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.HttpCookie
 * JD-Core Version:    0.6.2
 */