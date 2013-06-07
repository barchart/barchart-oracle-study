/*     */ package java.io;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ class UnixFileSystem extends FileSystem
/*     */ {
/*     */   private final char slash;
/*     */   private final char colon;
/*     */   private final String javaHome;
/* 142 */   private ExpiringCache cache = new ExpiringCache();
/*     */ 
/* 146 */   private ExpiringCache javaHomePrefixCache = new ExpiringCache();
/*     */ 
/*     */   public UnixFileSystem()
/*     */   {
/*  39 */     this.slash = ((String)AccessController.doPrivileged(new GetPropertyAction("file.separator"))).charAt(0);
/*     */ 
/*  41 */     this.colon = ((String)AccessController.doPrivileged(new GetPropertyAction("path.separator"))).charAt(0);
/*     */ 
/*  43 */     this.javaHome = ((String)AccessController.doPrivileged(new GetPropertyAction("java.home")));
/*     */   }
/*     */ 
/*     */   public char getSeparator()
/*     */   {
/*  51 */     return this.slash;
/*     */   }
/*     */ 
/*     */   public char getPathSeparator() {
/*  55 */     return this.colon;
/*     */   }
/*     */ 
/*     */   private String normalize(String paramString, int paramInt1, int paramInt2)
/*     */   {
/*  64 */     if (paramInt1 == 0) return paramString;
/*  65 */     int i = paramInt1;
/*  66 */     while ((i > 0) && (paramString.charAt(i - 1) == '/')) i--;
/*  67 */     if (i == 0) return "/";
/*  68 */     StringBuffer localStringBuffer = new StringBuffer(paramString.length());
/*  69 */     if (paramInt2 > 0) localStringBuffer.append(paramString.substring(0, paramInt2));
/*  70 */     int j = 0;
/*  71 */     for (int k = paramInt2; k < i; k++) {
/*  72 */       char c = paramString.charAt(k);
/*  73 */       if ((j != 47) || (c != '/')) {
/*  74 */         localStringBuffer.append(c);
/*  75 */         j = c;
/*     */       }
/*     */     }
/*  77 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public String normalize(String paramString)
/*     */   {
/*  84 */     int i = paramString.length();
/*  85 */     int j = 0;
/*  86 */     for (int k = 0; k < i; k++) {
/*  87 */       int m = paramString.charAt(k);
/*  88 */       if ((j == 47) && (m == 47))
/*  89 */         return normalize(paramString, i, k - 1);
/*  90 */       j = m;
/*     */     }
/*  92 */     if (j == 47) return normalize(paramString, i, i - 1);
/*  93 */     return paramString;
/*     */   }
/*     */ 
/*     */   public int prefixLength(String paramString) {
/*  97 */     if (paramString.length() == 0) return 0;
/*  98 */     return paramString.charAt(0) == '/' ? 1 : 0;
/*     */   }
/*     */ 
/*     */   public String resolve(String paramString1, String paramString2) {
/* 102 */     if (paramString2.equals("")) return paramString1;
/* 103 */     if (paramString2.charAt(0) == '/') {
/* 104 */       if (paramString1.equals("/")) return paramString2;
/* 105 */       return paramString1 + paramString2;
/*     */     }
/* 107 */     if (paramString1.equals("/")) return paramString1 + paramString2;
/* 108 */     return paramString1 + '/' + paramString2;
/*     */   }
/*     */ 
/*     */   public String getDefaultParent() {
/* 112 */     return "/";
/*     */   }
/*     */ 
/*     */   public String fromURIPath(String paramString) {
/* 116 */     String str = paramString;
/* 117 */     if ((str.endsWith("/")) && (str.length() > 1))
/*     */     {
/* 119 */       str = str.substring(0, str.length() - 1);
/*     */     }
/* 121 */     return str;
/*     */   }
/*     */ 
/*     */   public boolean isAbsolute(File paramFile)
/*     */   {
/* 128 */     return paramFile.getPrefixLength() != 0;
/*     */   }
/*     */ 
/*     */   public String resolve(File paramFile) {
/* 132 */     if (isAbsolute(paramFile)) return paramFile.getPath();
/* 133 */     return resolve(System.getProperty("user.dir"), paramFile.getPath());
/*     */   }
/*     */ 
/*     */   public String canonicalize(String paramString)
/*     */     throws IOException
/*     */   {
/* 149 */     if (!useCanonCaches) {
/* 150 */       return canonicalize0(paramString);
/*     */     }
/* 152 */     String str1 = this.cache.get(paramString);
/* 153 */     if (str1 == null) {
/* 154 */       String str2 = null;
/* 155 */       String str3 = null;
/*     */       Object localObject;
/* 156 */       if (useCanonPrefixCache)
/*     */       {
/* 160 */         str2 = parentOrNull(paramString);
/* 161 */         if (str2 != null) {
/* 162 */           str3 = this.javaHomePrefixCache.get(str2);
/* 163 */           if (str3 != null)
/*     */           {
/* 165 */             localObject = paramString.substring(1 + str2.length());
/* 166 */             str1 = str3 + this.slash + (String)localObject;
/* 167 */             this.cache.put(str2 + this.slash + (String)localObject, str1);
/*     */           }
/*     */         }
/*     */       }
/* 171 */       if (str1 == null) {
/* 172 */         str1 = canonicalize0(paramString);
/* 173 */         this.cache.put(paramString, str1);
/* 174 */         if ((useCanonPrefixCache) && (str2 != null) && (str2.startsWith(this.javaHome)))
/*     */         {
/* 176 */           str3 = parentOrNull(str1);
/*     */ 
/* 181 */           if ((str3 != null) && (str3.equals(str2))) {
/* 182 */             localObject = new File(str1);
/* 183 */             if ((((File)localObject).exists()) && (!((File)localObject).isDirectory())) {
/* 184 */               this.javaHomePrefixCache.put(str2, str3);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 190 */     return str1;
/*     */   }
/*     */ 
/*     */   private native String canonicalize0(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   static String parentOrNull(String paramString)
/*     */   {
/* 202 */     if (paramString == null) return null;
/* 203 */     int i = File.separatorChar;
/* 204 */     int j = paramString.length() - 1;
/* 205 */     int k = j;
/* 206 */     int m = 0;
/* 207 */     int n = 0;
/* 208 */     while (k > 0) {
/* 209 */       int i1 = paramString.charAt(k);
/* 210 */       if (i1 == 46) {
/* 211 */         m++; if (m >= 2)
/*     */         {
/* 213 */           return null;
/*     */         }
/*     */       } else { if (i1 == i) {
/* 216 */           if ((m == 1) && (n == 0))
/*     */           {
/* 218 */             return null;
/*     */           }
/* 220 */           if ((k == 0) || (k >= j - 1) || (paramString.charAt(k - 1) == i))
/*     */           {
/* 225 */             return null;
/*     */           }
/* 227 */           return paramString.substring(0, k);
/*     */         }
/* 229 */         n++;
/* 230 */         m = 0;
/*     */       }
/* 232 */       k--;
/*     */     }
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   public native int getBooleanAttributes0(File paramFile);
/*     */ 
/*     */   public int getBooleanAttributes(File paramFile)
/*     */   {
/* 242 */     int i = getBooleanAttributes0(paramFile);
/* 243 */     String str = paramFile.getName();
/* 244 */     int j = (str.length() > 0) && (str.charAt(0) == '.') ? 1 : 0;
/* 245 */     return i | (j != 0 ? 8 : 0);
/*     */   }
/*     */ 
/*     */   public native boolean checkAccess(File paramFile, int paramInt);
/*     */ 
/*     */   public native long getLastModifiedTime(File paramFile);
/*     */ 
/*     */   public native long getLength(File paramFile);
/*     */ 
/*     */   public native boolean setPermission(File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
/*     */ 
/*     */   public native boolean createFileExclusively(String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public boolean delete(File paramFile)
/*     */   {
/* 263 */     this.cache.clear();
/* 264 */     this.javaHomePrefixCache.clear();
/* 265 */     return delete0(paramFile);
/*     */   }
/*     */ 
/*     */   private native boolean delete0(File paramFile);
/*     */ 
/*     */   public native String[] list(File paramFile);
/*     */ 
/*     */   public native boolean createDirectory(File paramFile);
/*     */ 
/*     */   public boolean rename(File paramFile1, File paramFile2)
/*     */   {
/* 276 */     this.cache.clear();
/* 277 */     this.javaHomePrefixCache.clear();
/* 278 */     return rename0(paramFile1, paramFile2);
/*     */   }
/*     */ 
/*     */   private native boolean rename0(File paramFile1, File paramFile2);
/*     */ 
/*     */   public native boolean setLastModifiedTime(File paramFile, long paramLong);
/*     */ 
/*     */   public native boolean setReadOnly(File paramFile);
/*     */ 
/*     */   public File[] listRoots() {
/*     */     try {
/* 289 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 290 */       if (localSecurityManager != null) {
/* 291 */         localSecurityManager.checkRead("/");
/*     */       }
/* 293 */       return new File[] { new File("/") }; } catch (SecurityException localSecurityException) {
/*     */     }
/* 295 */     return new File[0];
/*     */   }
/*     */ 
/*     */   public native long getSpace(File paramFile, int paramInt);
/*     */ 
/*     */   public int compare(File paramFile1, File paramFile2)
/*     */   {
/* 305 */     return paramFile1.getPath().compareTo(paramFile2.getPath());
/*     */   }
/*     */ 
/*     */   public int hashCode(File paramFile) {
/* 309 */     return paramFile.getPath().hashCode() ^ 0x12D591;
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   static
/*     */   {
/* 316 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.UnixFileSystem
 * JD-Core Version:    0.6.2
 */