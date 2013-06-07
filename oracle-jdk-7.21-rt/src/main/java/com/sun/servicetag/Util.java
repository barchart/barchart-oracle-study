/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class Util
/*     */ {
/*  39 */   private static boolean verbose = System.getProperty("servicetag.verbose") != null;
/*  40 */   private static String jrepath = null;
/*     */   private static final String REGKEY_TAIL = "microsoft\\windows\\currentversion\\app paths\\stclient.exe";
/*     */   private static final String STCLIENT_TAIL = "sun\\servicetag\\stclient.exe";
/*     */   private static final String WIN32_STCLIENT = "c:\\Program Files (x86)\\sun\\servicetag\\stclient.exe";
/* 176 */   private static int jdkVersion = 0;
/* 177 */   private static int jdkUpdate = 0;
/*     */ 
/*     */   static boolean isVerbose()
/*     */   {
/*  49 */     return verbose;
/*     */   }
/*     */ 
/*     */   static synchronized String getJrePath()
/*     */   {
/*  57 */     if (jrepath == null)
/*     */     {
/*  60 */       String str = System.getProperty("java.home");
/*  61 */       jrepath = str + File.separator + "jre";
/*  62 */       File localFile = new File(jrepath, "lib");
/*  63 */       if (!localFile.exists())
/*     */       {
/*  65 */         jrepath = str;
/*     */       }
/*     */     }
/*  68 */     return jrepath;
/*     */   }
/*     */ 
/*     */   static boolean isJdk()
/*     */   {
/*  76 */     return getJrePath().endsWith(File.separator + "jre");
/*     */   }
/*     */ 
/*     */   static String generateURN()
/*     */   {
/*  83 */     return "urn:st:" + UUID.randomUUID().toString();
/*     */   }
/*     */ 
/*     */   static int getIntValue(String paramString) {
/*     */     try {
/*  88 */       return Integer.parseInt(paramString); } catch (NumberFormatException localNumberFormatException) {
/*     */     }
/*  90 */     throw new IllegalArgumentException("\"" + paramString + "\"" + " expected to be an integer");
/*     */   }
/*     */ 
/*     */   static String formatTimestamp(Date paramDate)
/*     */   {
/* 102 */     if (paramDate == null) {
/* 103 */       return "[No timestamp]";
/*     */     }
/* 105 */     SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
/* 106 */     localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 107 */     return localSimpleDateFormat.format(paramDate);
/*     */   }
/*     */ 
/*     */   static Date parseTimestamp(String paramString)
/*     */   {
/* 116 */     SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
/* 117 */     localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     try {
/* 119 */       return localSimpleDateFormat.parse(paramString);
/*     */     }
/*     */     catch (ParseException localParseException) {
/* 122 */       localParseException.printStackTrace();
/* 123 */     }return new Date();
/*     */   }
/*     */ 
/*     */   static String commandOutput(Process paramProcess) throws IOException
/*     */   {
/* 128 */     InputStreamReader localInputStreamReader1 = null;
/* 129 */     InputStreamReader localInputStreamReader2 = null;
/*     */     try {
/* 131 */       localInputStreamReader1 = new InputStreamReader(paramProcess.getInputStream());
/* 132 */       localInputStreamReader2 = new InputStreamReader(paramProcess.getErrorStream());
/* 133 */       String str1 = commandOutput(localInputStreamReader1);
/* 134 */       str2 = commandOutput(localInputStreamReader2);
/* 135 */       paramProcess.waitFor();
/* 136 */       return str1 + str2.trim();
/*     */     }
/*     */     catch (InterruptedException localInterruptedException)
/*     */     {
/*     */       String str2;
/* 138 */       if (isVerbose()) {
/* 139 */         localInterruptedException.printStackTrace();
/*     */       }
/* 141 */       return localInterruptedException.getMessage();
/*     */     } finally {
/*     */       try {
/* 144 */         if (localInputStreamReader1 != null)
/* 145 */           localInputStreamReader1.close();
/*     */       }
/*     */       finally {
/* 148 */         if (localInputStreamReader2 != null)
/* 149 */           localInputStreamReader2.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static String commandOutput(Reader paramReader) throws IOException
/*     */   {
/* 156 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     int i;
/* 158 */     while ((i = paramReader.read()) > 0) {
/* 159 */       if (i != 13) {
/* 160 */         localStringBuilder.append((char)i);
/*     */       }
/*     */     }
/* 163 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   static int getJdkVersion() {
/* 167 */     parseVersion();
/* 168 */     return jdkVersion;
/*     */   }
/*     */ 
/*     */   static int getUpdateVersion() {
/* 172 */     parseVersion();
/* 173 */     return jdkUpdate;
/*     */   }
/*     */ 
/*     */   private static synchronized void parseVersion()
/*     */   {
/* 179 */     if (jdkVersion > 0) {
/* 180 */       return;
/*     */     }
/*     */ 
/* 186 */     String str1 = System.getProperty("java.runtime.version");
/* 187 */     if ((str1.length() >= 5) && (Character.isDigit(str1.charAt(0))) && (str1.charAt(1) == '.') && (Character.isDigit(str1.charAt(2))) && (str1.charAt(3) == '.') && (Character.isDigit(str1.charAt(4))))
/*     */     {
/* 191 */       jdkVersion = Character.digit(str1.charAt(2), 10);
/* 192 */       str1 = str1.substring(5, str1.length());
/* 193 */       if ((str1.charAt(0) == '_') && (str1.length() >= 3) && (Character.isDigit(str1.charAt(1))) && (Character.isDigit(str1.charAt(2))))
/*     */       {
/* 196 */         int i = 3;
/*     */         try {
/* 198 */           String str2 = str1.substring(1, 3);
/* 199 */           jdkUpdate = Integer.valueOf(str2).intValue();
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException) {
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 206 */       throw new InternalError("Invalid java.runtime.version" + str1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static byte[] stringToByteArray(String paramString)
/*     */   {
/* 214 */     return (paramString + "").getBytes();
/*     */   }
/*     */ 
/*     */   private static String byteArrayToString(byte[] paramArrayOfByte)
/*     */   {
/* 221 */     return new String(paramArrayOfByte, 0, paramArrayOfByte.length - 1);
/*     */   }
/*     */ 
/*     */   private static File getWindowsStClientFile(boolean paramBoolean)
/*     */   {
/* 231 */     File localFile = null;
/* 232 */     String str1 = paramBoolean == true ? "software\\Wow6432Node\\microsoft\\windows\\currentversion\\app paths\\stclient.exe" : "software\\microsoft\\windows\\currentversion\\app paths\\stclient.exe";
/*     */ 
/* 235 */     String str2 = "";
/* 236 */     String str3 = getRegistryKey(str1, str2);
/* 237 */     if ((str3 != null) && (new File(str3).exists()) && (str3.toLowerCase().endsWith("sun\\servicetag\\stclient.exe".toLowerCase())))
/*     */     {
/* 240 */       localFile = new File(str3);
/*     */     }
/* 242 */     if (isVerbose()) {
/* 243 */       System.out.println("stclient=" + localFile);
/*     */     }
/* 245 */     return localFile;
/*     */   }
/*     */ 
/*     */   static File getWindowsStClientFile()
/*     */   {
/* 255 */     File localFile = null;
/* 256 */     if (System.getProperty("os.arch").equals("x86"))
/*     */     {
/* 258 */       localFile = getWindowsStClientFile(false);
/* 259 */       if (localFile != null)
/* 260 */         return localFile;
/*     */     }
/*     */     else
/*     */     {
/* 264 */       localFile = getWindowsStClientFile(true);
/* 265 */       if (localFile != null) {
/* 266 */         return localFile;
/*     */       }
/*     */ 
/* 269 */       localFile = new File("c:\\Program Files (x86)\\sun\\servicetag\\stclient.exe");
/* 270 */       if (localFile.canExecute()) {
/* 271 */         if (isVerbose()) {
/* 272 */           System.out.println("stclient(default)=" + localFile);
/*     */         }
/* 274 */         return localFile;
/*     */       }
/*     */     }
/* 277 */     if (isVerbose()) {
/* 278 */       System.out.println("stclient not found");
/*     */     }
/* 280 */     return null;
/*     */   }
/*     */ 
/*     */   private static String getRegistryKey(String paramString1, String paramString2)
/*     */   {
/* 289 */     String str = null;
/*     */     try {
/* 291 */       Class localClass = Class.forName("java.util.prefs.WindowsPreferences");
/*     */ 
/* 294 */       Method localMethod1 = localClass.getDeclaredMethod("WindowsRegOpenKey", new Class[] { Integer.TYPE, [B.class, Integer.TYPE });
/*     */ 
/* 296 */       localMethod1.setAccessible(true);
/*     */ 
/* 298 */       Method localMethod2 = localClass.getDeclaredMethod("WindowsRegCloseKey", new Class[] { Integer.TYPE });
/*     */ 
/* 300 */       localMethod2.setAccessible(true);
/*     */ 
/* 302 */       Method localMethod3 = localClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[] { Integer.TYPE, [B.class });
/*     */ 
/* 304 */       localMethod3.setAccessible(true);
/*     */ 
/* 307 */       int i = getValueFromStaticField("HKEY_LOCAL_MACHINE", localClass);
/* 308 */       int j = getValueFromStaticField("KEY_READ", localClass);
/* 309 */       int k = getValueFromStaticField("ERROR_CODE", localClass);
/* 310 */       int m = getValueFromStaticField("NATIVE_HANDLE", localClass);
/* 311 */       int n = getValueFromStaticField("ERROR_SUCCESS", localClass);
/*     */ 
/* 314 */       byte[] arrayOfByte1 = stringToByteArray(paramString1);
/* 315 */       byte[] arrayOfByte2 = stringToByteArray(paramString2);
/*     */ 
/* 318 */       int[] arrayOfInt = (int[])localMethod1.invoke(null, new Object[] { Integer.valueOf(i), arrayOfByte1, Integer.valueOf(j) });
/*     */ 
/* 320 */       if (arrayOfInt[k] == n) {
/* 321 */         byte[] arrayOfByte3 = (byte[])localMethod3.invoke(null, new Object[] { Integer.valueOf(arrayOfInt[m]), arrayOfByte2 });
/*     */ 
/* 323 */         str = byteArrayToString(arrayOfByte3);
/* 324 */         localMethod2.invoke(null, new Object[] { Integer.valueOf(arrayOfInt[m]) });
/*     */       }
/*     */     } catch (Exception localException) {
/* 327 */       if (isVerbose()) {
/* 328 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 331 */     return str;
/*     */   }
/*     */ 
/*     */   private static int getValueFromStaticField(String paramString, Class<?> paramClass) throws Exception {
/* 335 */     Field localField = paramClass.getDeclaredField(paramString);
/* 336 */     localField.setAccessible(true);
/* 337 */     return localField.getInt(null);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.Util
 * JD-Core Version:    0.6.2
 */