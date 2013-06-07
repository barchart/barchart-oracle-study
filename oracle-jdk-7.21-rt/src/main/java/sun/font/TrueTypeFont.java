/*      */ package sun.font;
/*      */ 
/*      */ import java.awt.FontFormatException;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.geom.Point2D.Float;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.IntBuffer;
/*      */ import java.nio.ShortBuffer;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.java2d.Disposer;
/*      */ import sun.java2d.DisposerRecord;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class TrueTypeFont extends FileFont
/*      */ {
/*      */   public static final int cmapTag = 1668112752;
/*      */   public static final int glyfTag = 1735162214;
/*      */   public static final int headTag = 1751474532;
/*      */   public static final int hheaTag = 1751672161;
/*      */   public static final int hmtxTag = 1752003704;
/*      */   public static final int locaTag = 1819239265;
/*      */   public static final int maxpTag = 1835104368;
/*      */   public static final int nameTag = 1851878757;
/*      */   public static final int postTag = 1886352244;
/*      */   public static final int os_2Tag = 1330851634;
/*      */   public static final int GDEFTag = 1195656518;
/*      */   public static final int GPOSTag = 1196445523;
/*      */   public static final int GSUBTag = 1196643650;
/*      */   public static final int mortTag = 1836020340;
/*      */   public static final int fdscTag = 1717859171;
/*      */   public static final int fvarTag = 1719034226;
/*      */   public static final int featTag = 1717920116;
/*      */   public static final int EBLCTag = 1161972803;
/*      */   public static final int gaspTag = 1734439792;
/*      */   public static final int ttcfTag = 1953784678;
/*      */   public static final int v1ttTag = 65536;
/*      */   public static final int trueTag = 1953658213;
/*      */   public static final int ottoTag = 1330926671;
/*      */   public static final int MS_PLATFORM_ID = 3;
/*      */   public static final short ENGLISH_LOCALE_ID = 1033;
/*      */   public static final int FAMILY_NAME_ID = 1;
/*      */   public static final int FULL_NAME_ID = 4;
/*      */   public static final int POSTSCRIPT_NAME_ID = 6;
/*      */   private static final short US_LCID = 1033;
/*      */   private static Map<String, Short> lcidMap;
/*  139 */   TTDisposerRecord disposerRecord = new TTDisposerRecord(null);
/*      */ 
/*  142 */   int fontIndex = 0;
/*      */ 
/*  145 */   int directoryCount = 1;
/*      */   int directoryOffset;
/*      */   int numTables;
/*      */   DirectoryEntry[] tableDirectory;
/*      */   private boolean supportsJA;
/*      */   private boolean supportsCJK;
/*      */   private Locale nameLocale;
/*      */   private String localeFamilyName;
/*      */   private String localeFullName;
/*      */   private static final int TTCHEADERSIZE = 12;
/*      */   private static final int DIRECTORYHEADERSIZE = 12;
/*      */   private static final int DIRECTORYENTRYSIZE = 16;
/*  580 */   static final String[] encoding_mapping = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361", "", "", "", "", "", "", "", "", "", "" };
/*      */ 
/*  628 */   private static final String[][] languages = { { "en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", "no", "pt", "sq", "sv" }, { "cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", "sq", "sr" }, { "bg", "mk", "ru", "sh", "uk" }, { "el" }, { "tr" }, { "he" }, { "ar" }, { "et", "lt", "lv" }, { "th" }, { "ja" }, { "zh", "zh_CN" }, { "ko" }, { "zh_HK", "zh_TW" }, { "ko" } };
/*      */ 
/*  675 */   private static final String[] codePages = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361" };
/*      */ 
/*  692 */   private static String defaultCodePage = null;
/*      */   public static final int reserved_bits1 = -2147483648;
/*      */   public static final int reserved_bits2 = 65535;
/*      */   private static final int fsSelectionItalicBit = 1;
/*      */   private static final int fsSelectionBoldBit = 32;
/*      */   private static final int fsSelectionRegularBit = 64;
/*      */   private float stSize;
/*      */   private float stPos;
/*      */   private float ulSize;
/*      */   private float ulPos;
/*      */   private char[] gaspTable;
/*      */ 
/*      */   public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean)
/*      */     throws FontFormatException
/*      */   {
/*  186 */     super(paramString, paramObject);
/*  187 */     this.useJavaRasterizer = paramBoolean;
/*  188 */     this.fontRank = 3;
/*      */     try {
/*  190 */       verify();
/*  191 */       init(paramInt);
/*      */     } catch (Throwable localThrowable) {
/*  193 */       close();
/*  194 */       if ((localThrowable instanceof FontFormatException)) {
/*  195 */         throw ((FontFormatException)localThrowable);
/*      */       }
/*  197 */       throw new FontFormatException("Unexpected runtime exception.");
/*      */     }
/*      */ 
/*  200 */     Disposer.addObjectRecord(this, this.disposerRecord);
/*      */   }
/*      */ 
/*      */   protected boolean checkUseNatives()
/*      */   {
/*  216 */     if (this.checkedNatives) {
/*  217 */       return this.useNatives;
/*      */     }
/*  219 */     if ((!FontUtilities.isSolaris) || (this.useJavaRasterizer) || (FontUtilities.useT2K) || (this.nativeNames == null) || (getDirectoryEntry(1161972803) != null) || (GraphicsEnvironment.isHeadless()))
/*      */     {
/*  223 */       this.checkedNatives = true;
/*  224 */       return false;
/*      */     }
/*      */     Object localObject;
/*  225 */     if ((this.nativeNames instanceof String)) {
/*  226 */       localObject = (String)this.nativeNames;
/*      */ 
/*  228 */       if (((String)localObject).indexOf("8859") > 0) {
/*  229 */         this.checkedNatives = true;
/*  230 */         return false;
/*  231 */       }if (NativeFont.hasExternalBitmaps((String)localObject)) {
/*  232 */         this.nativeFonts = new NativeFont[1];
/*      */         try {
/*  234 */           this.nativeFonts[0] = new NativeFont((String)localObject, true);
/*      */ 
/*  238 */           this.useNatives = true;
/*      */         } catch (FontFormatException localFontFormatException1) {
/*  240 */           this.nativeFonts = null;
/*      */         }
/*      */       }
/*  243 */     } else if ((this.nativeNames instanceof String[])) {
/*  244 */       localObject = (String[])this.nativeNames;
/*  245 */       int i = localObject.length;
/*  246 */       int j = 0;
/*  247 */       for (int k = 0; k < i; k++) {
/*  248 */         if (localObject[k].indexOf("8859") > 0) {
/*  249 */           this.checkedNatives = true;
/*  250 */           return false;
/*  251 */         }if (NativeFont.hasExternalBitmaps(localObject[k])) {
/*  252 */           j = 1;
/*      */         }
/*      */       }
/*  255 */       if (j == 0) {
/*  256 */         this.checkedNatives = true;
/*  257 */         return false;
/*      */       }
/*  259 */       this.useNatives = true;
/*  260 */       this.nativeFonts = new NativeFont[i];
/*  261 */       for (k = 0; k < i; k++) {
/*      */         try {
/*  263 */           this.nativeFonts[k] = new NativeFont(localObject[k], true);
/*      */         } catch (FontFormatException localFontFormatException2) {
/*  265 */           this.useNatives = false;
/*  266 */           this.nativeFonts = null;
/*      */         }
/*      */       }
/*      */     }
/*  270 */     if (this.useNatives) {
/*  271 */       this.glyphToCharMap = new char[getMapper().getNumGlyphs()];
/*      */     }
/*  273 */     this.checkedNatives = true;
/*  274 */     return this.useNatives;
/*      */   }
/*      */ 
/*      */   private synchronized FileChannel open()
/*      */     throws FontFormatException
/*      */   {
/*  286 */     if (this.disposerRecord.channel == null) {
/*  287 */       if (FontUtilities.isLogging())
/*  288 */         FontUtilities.getLogger().info("open TTF: " + this.platName);
/*      */       try
/*      */       {
/*  291 */         RandomAccessFile localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run()
/*      */           {
/*      */             try {
/*  296 */               return new RandomAccessFile(TrueTypeFont.this.platName, "r");
/*      */             } catch (FileNotFoundException localFileNotFoundException) {
/*      */             }
/*  299 */             return null;
/*      */           }
/*      */         });
/*  302 */         this.disposerRecord.channel = localRandomAccessFile.getChannel();
/*  303 */         this.fileSize = ((int)this.disposerRecord.channel.size());
/*  304 */         FontManager localFontManager = FontManagerFactory.getInstance();
/*  305 */         if ((localFontManager instanceof SunFontManager))
/*  306 */           ((SunFontManager)localFontManager).addToPool(this);
/*      */       }
/*      */       catch (NullPointerException localNullPointerException) {
/*  309 */         close();
/*  310 */         throw new FontFormatException(localNullPointerException.toString());
/*      */       }
/*      */       catch (ClosedChannelException localClosedChannelException)
/*      */       {
/*  317 */         Thread.interrupted();
/*  318 */         close();
/*  319 */         open();
/*      */       } catch (IOException localIOException) {
/*  321 */         close();
/*  322 */         throw new FontFormatException(localIOException.toString());
/*      */       }
/*      */     }
/*  325 */     return this.disposerRecord.channel;
/*      */   }
/*      */ 
/*      */   protected synchronized void close() {
/*  329 */     this.disposerRecord.dispose();
/*      */   }
/*      */ 
/*      */   int readBlock(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*      */   {
/*  334 */     int i = 0;
/*      */     try {
/*  336 */       synchronized (this) {
/*  337 */         if (this.disposerRecord.channel == null) {
/*  338 */           open();
/*      */         }
/*  340 */         if (paramInt1 + paramInt2 > this.fileSize) {
/*  341 */           if (paramInt1 >= this.fileSize)
/*      */           {
/*  351 */             if (FontUtilities.isLogging()) {
/*  352 */               String str1 = "Read offset is " + paramInt1 + " file size is " + this.fileSize + " file is " + this.platName;
/*      */ 
/*  355 */               FontUtilities.getLogger().severe(str1);
/*      */             }
/*  357 */             return -1;
/*      */           }
/*  359 */           paramInt2 = this.fileSize - paramInt1;
/*      */         }
/*      */ 
/*  362 */         paramByteBuffer.clear();
/*  363 */         this.disposerRecord.channel.position(paramInt1);
/*  364 */         while (i < paramInt2) {
/*  365 */           int j = this.disposerRecord.channel.read(paramByteBuffer);
/*  366 */           if (j == -1) {
/*  367 */             String str2 = "Unexpected EOF " + this;
/*  368 */             int k = (int)this.disposerRecord.channel.size();
/*  369 */             if (k != this.fileSize) {
/*  370 */               str2 = str2 + " File size was " + this.fileSize + " and now is " + k;
/*      */             }
/*      */ 
/*  373 */             if (FontUtilities.isLogging()) {
/*  374 */               FontUtilities.getLogger().severe(str2);
/*      */             }
/*      */ 
/*  386 */             if ((i > paramInt2 / 2) || (i > 16384)) {
/*  387 */               paramByteBuffer.flip();
/*  388 */               if (FontUtilities.isLogging()) {
/*  389 */                 str2 = "Returning " + i + " bytes instead of " + paramInt2;
/*      */ 
/*  391 */                 FontUtilities.getLogger().severe(str2);
/*      */               }
/*      */             } else {
/*  394 */               i = -1;
/*      */             }
/*  396 */             throw new IOException(str2);
/*      */           }
/*  398 */           i += j;
/*      */         }
/*  400 */         paramByteBuffer.flip();
/*  401 */         if (i > paramInt2)
/*  402 */           i = paramInt2;
/*      */       }
/*      */     }
/*      */     catch (FontFormatException localFontFormatException) {
/*  406 */       if (FontUtilities.isLogging()) {
/*  407 */         FontUtilities.getLogger().severe("While reading " + this.platName, localFontFormatException);
/*      */       }
/*      */ 
/*  410 */       i = -1;
/*  411 */       deregisterFontAndClearStrikeCache();
/*      */     }
/*      */     catch (ClosedChannelException localClosedChannelException)
/*      */     {
/*  416 */       Thread.interrupted();
/*  417 */       close();
/*  418 */       return readBlock(paramByteBuffer, paramInt1, paramInt2);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  427 */       if (FontUtilities.isLogging()) {
/*  428 */         FontUtilities.getLogger().severe("While reading " + this.platName, localIOException);
/*      */       }
/*      */ 
/*  431 */       if (i == 0) {
/*  432 */         i = -1;
/*  433 */         deregisterFontAndClearStrikeCache();
/*      */       }
/*      */     }
/*  436 */     return i;
/*      */   }
/*      */ 
/*      */   ByteBuffer readBlock(int paramInt1, int paramInt2)
/*      */   {
/*  441 */     ByteBuffer localByteBuffer = ByteBuffer.allocate(paramInt2);
/*      */     try {
/*  443 */       synchronized (this) {
/*  444 */         if (this.disposerRecord.channel == null) {
/*  445 */           open();
/*      */         }
/*  447 */         if (paramInt1 + paramInt2 > this.fileSize) {
/*  448 */           if (paramInt1 > this.fileSize) {
/*  449 */             return null;
/*      */           }
/*  451 */           localByteBuffer = ByteBuffer.allocate(this.fileSize - paramInt1);
/*      */         }
/*      */ 
/*  454 */         this.disposerRecord.channel.position(paramInt1);
/*  455 */         this.disposerRecord.channel.read(localByteBuffer);
/*  456 */         localByteBuffer.flip();
/*      */       }
/*      */     } catch (FontFormatException localFontFormatException) {
/*  459 */       return null;
/*      */     }
/*      */     catch (ClosedChannelException localClosedChannelException)
/*      */     {
/*  464 */       Thread.interrupted();
/*  465 */       close();
/*  466 */       readBlock(localByteBuffer, paramInt1, paramInt2);
/*      */     } catch (IOException localIOException) {
/*  468 */       return null;
/*      */     }
/*  470 */     return localByteBuffer;
/*      */   }
/*      */ 
/*      */   byte[] readBytes(int paramInt1, int paramInt2)
/*      */   {
/*  480 */     ByteBuffer localByteBuffer = readBlock(paramInt1, paramInt2);
/*  481 */     if (localByteBuffer.hasArray()) {
/*  482 */       return localByteBuffer.array();
/*      */     }
/*  484 */     byte[] arrayOfByte = new byte[localByteBuffer.limit()];
/*  485 */     localByteBuffer.get(arrayOfByte);
/*  486 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private void verify() throws FontFormatException
/*      */   {
/*  491 */     open();
/*      */   }
/*      */ 
/*      */   protected void init(int paramInt)
/*      */     throws FontFormatException
/*      */   {
/*  500 */     int i = 0;
/*  501 */     ByteBuffer localByteBuffer1 = readBlock(0, 12);
/*      */     try {
/*  503 */       switch (localByteBuffer1.getInt())
/*      */       {
/*      */       case 1953784678:
/*  506 */         localByteBuffer1.getInt();
/*  507 */         this.directoryCount = localByteBuffer1.getInt();
/*  508 */         if (paramInt >= this.directoryCount) {
/*  509 */           throw new FontFormatException("Bad collection index");
/*      */         }
/*  511 */         this.fontIndex = paramInt;
/*  512 */         localByteBuffer1 = readBlock(12 + 4 * paramInt, 4);
/*  513 */         i = localByteBuffer1.getInt();
/*  514 */         break;
/*      */       case 65536:
/*      */       case 1330926671:
/*      */       case 1953658213:
/*  519 */         break;
/*      */       default:
/*  522 */         throw new FontFormatException("Unsupported sfnt " + getPublicFileName());
/*      */       }
/*      */ 
/*  532 */       localByteBuffer1 = readBlock(i + 4, 2);
/*  533 */       this.numTables = localByteBuffer1.getShort();
/*  534 */       this.directoryOffset = (i + 12);
/*  535 */       ByteBuffer localByteBuffer2 = readBlock(this.directoryOffset, this.numTables * 16);
/*      */ 
/*  537 */       IntBuffer localIntBuffer = localByteBuffer2.asIntBuffer();
/*      */ 
/*  539 */       this.tableDirectory = new DirectoryEntry[this.numTables];
/*  540 */       for (int j = 0; j < this.numTables; j++)
/*      */       {
/*      */         void tmp231_228 = new DirectoryEntry(); DirectoryEntry localDirectoryEntry = tmp231_228; this.tableDirectory[j] = tmp231_228;
/*  542 */         localDirectoryEntry.tag = localIntBuffer.get();
/*  543 */         localIntBuffer.get();
/*  544 */         localDirectoryEntry.offset = localIntBuffer.get();
/*  545 */         localDirectoryEntry.length = localIntBuffer.get();
/*  546 */         if (localDirectoryEntry.offset + localDirectoryEntry.length > this.fileSize) {
/*  547 */           throw new FontFormatException("bad table, tag=" + localDirectoryEntry.tag);
/*      */         }
/*      */       }
/*  550 */       initNames();
/*      */     } catch (Exception localException) {
/*  552 */       if (FontUtilities.isLogging()) {
/*  553 */         FontUtilities.getLogger().severe(localException.toString());
/*      */       }
/*  555 */       if ((localException instanceof FontFormatException)) {
/*  556 */         throw ((FontFormatException)localException);
/*      */       }
/*  558 */       throw new FontFormatException(localException.toString());
/*      */     }
/*      */ 
/*  561 */     if ((this.familyName == null) || (this.fullName == null)) {
/*  562 */       throw new FontFormatException("Font name not found");
/*      */     }
/*      */ 
/*  568 */     ByteBuffer localByteBuffer3 = getTableBuffer(1330851634);
/*  569 */     setStyle(localByteBuffer3);
/*  570 */     setCJKSupport(localByteBuffer3);
/*      */   }
/*      */ 
/*      */   static String getCodePage()
/*      */   {
/*  695 */     if (defaultCodePage != null) {
/*  696 */       return defaultCodePage;
/*      */     }
/*      */ 
/*  699 */     if (FontUtilities.isWindows) {
/*  700 */       defaultCodePage = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
/*      */     }
/*      */     else
/*      */     {
/*  704 */       if (languages.length != codePages.length) {
/*  705 */         throw new InternalError("wrong code pages array length");
/*      */       }
/*  707 */       Locale localLocale = SunToolkit.getStartupLocale();
/*      */ 
/*  709 */       String str1 = localLocale.getLanguage();
/*  710 */       if (str1 != null) {
/*  711 */         if (str1.equals("zh")) {
/*  712 */           String str2 = localLocale.getCountry();
/*  713 */           if (str2 != null) {
/*  714 */             str1 = str1 + "_" + str2;
/*      */           }
/*      */         }
/*  717 */         for (int i = 0; i < languages.length; i++) {
/*  718 */           for (int j = 0; j < languages[i].length; j++) {
/*  719 */             if (str1.equals(languages[i][j])) {
/*  720 */               defaultCodePage = codePages[i];
/*  721 */               return defaultCodePage;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  727 */     if (defaultCodePage == null) {
/*  728 */       defaultCodePage = "";
/*      */     }
/*  730 */     return defaultCodePage;
/*      */   }
/*      */ 
/*      */   boolean supportsEncoding(String paramString)
/*      */   {
/*  738 */     if (paramString == null) {
/*  739 */       paramString = getCodePage();
/*      */     }
/*  741 */     if ("".equals(paramString)) {
/*  742 */       return false;
/*      */     }
/*      */ 
/*  745 */     paramString = paramString.toLowerCase();
/*      */ 
/*  754 */     if (paramString.equals("gb18030"))
/*  755 */       paramString = "gbk";
/*  756 */     else if (paramString.equals("ms950_hkscs")) {
/*  757 */       paramString = "ms950";
/*      */     }
/*      */ 
/*  760 */     ByteBuffer localByteBuffer = getTableBuffer(1330851634);
/*      */ 
/*  762 */     if ((localByteBuffer == null) || (localByteBuffer.capacity() < 86)) {
/*  763 */       return false;
/*      */     }
/*      */ 
/*  766 */     int i = localByteBuffer.getInt(78);
/*  767 */     int j = localByteBuffer.getInt(82);
/*      */ 
/*  777 */     for (int k = 0; k < encoding_mapping.length; k++) {
/*  778 */       if ((encoding_mapping[k].equals(paramString)) && 
/*  779 */         ((1 << k & i) != 0)) {
/*  780 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  784 */     return false;
/*      */   }
/*      */ 
/*      */   private void setCJKSupport(ByteBuffer paramByteBuffer)
/*      */   {
/*  791 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 50)) {
/*  792 */       return;
/*      */     }
/*  794 */     int i = paramByteBuffer.getInt(46);
/*      */ 
/*  801 */     this.supportsCJK = ((i & 0x29BF0000) != 0);
/*      */ 
/*  810 */     this.supportsJA = ((i & 0x60000) != 0);
/*      */   }
/*      */ 
/*      */   boolean supportsJA() {
/*  814 */     return this.supportsJA;
/*      */   }
/*      */ 
/*      */   ByteBuffer getTableBuffer(int paramInt) {
/*  818 */     DirectoryEntry localDirectoryEntry = null;
/*      */ 
/*  820 */     for (int i = 0; i < this.numTables; i++) {
/*  821 */       if (this.tableDirectory[i].tag == paramInt) {
/*  822 */         localDirectoryEntry = this.tableDirectory[i];
/*  823 */         break;
/*      */       }
/*      */     }
/*  826 */     if ((localDirectoryEntry == null) || (localDirectoryEntry.length == 0) || (localDirectoryEntry.offset + localDirectoryEntry.length > this.fileSize))
/*      */     {
/*  828 */       return null;
/*      */     }
/*      */ 
/*  831 */     i = 0;
/*  832 */     ByteBuffer localByteBuffer = ByteBuffer.allocate(localDirectoryEntry.length);
/*  833 */     synchronized (this) {
/*      */       try {
/*  835 */         if (this.disposerRecord.channel == null) {
/*  836 */           open();
/*      */         }
/*  838 */         this.disposerRecord.channel.position(localDirectoryEntry.offset);
/*  839 */         i = this.disposerRecord.channel.read(localByteBuffer);
/*  840 */         localByteBuffer.flip();
/*      */       }
/*      */       catch (ClosedChannelException localClosedChannelException)
/*      */       {
/*  845 */         Thread.interrupted();
/*  846 */         close();
/*  847 */         return getTableBuffer(paramInt);
/*      */       } catch (IOException localIOException) {
/*  849 */         return null;
/*      */       } catch (FontFormatException localFontFormatException) {
/*  851 */         return null;
/*      */       }
/*      */ 
/*  854 */       if (i < localDirectoryEntry.length) {
/*  855 */         return null;
/*      */       }
/*  857 */       return localByteBuffer;
/*      */     }
/*      */   }
/*      */ 
/*      */   long getLayoutTableCache()
/*      */   {
/*      */     try
/*      */     {
/*  865 */       return getScaler().getLayoutTableCache(); } catch (FontScalerException localFontScalerException) {
/*      */     }
/*  867 */     return 0L;
/*      */   }
/*      */ 
/*      */   byte[] getTableBytes(int paramInt)
/*      */   {
/*  873 */     ByteBuffer localByteBuffer = getTableBuffer(paramInt);
/*  874 */     if (localByteBuffer == null)
/*  875 */       return null;
/*  876 */     if (localByteBuffer.hasArray())
/*      */       try {
/*  878 */         return localByteBuffer.array();
/*      */       }
/*      */       catch (Exception localException) {
/*      */       }
/*  882 */     byte[] arrayOfByte = new byte[getTableSize(paramInt)];
/*  883 */     localByteBuffer.get(arrayOfByte);
/*  884 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   int getTableSize(int paramInt) {
/*  888 */     for (int i = 0; i < this.numTables; i++) {
/*  889 */       if (this.tableDirectory[i].tag == paramInt) {
/*  890 */         return this.tableDirectory[i].length;
/*      */       }
/*      */     }
/*  893 */     return 0;
/*      */   }
/*      */ 
/*      */   int getTableOffset(int paramInt) {
/*  897 */     for (int i = 0; i < this.numTables; i++) {
/*  898 */       if (this.tableDirectory[i].tag == paramInt) {
/*  899 */         return this.tableDirectory[i].offset;
/*      */       }
/*      */     }
/*  902 */     return 0;
/*      */   }
/*      */ 
/*      */   DirectoryEntry getDirectoryEntry(int paramInt) {
/*  906 */     for (int i = 0; i < this.numTables; i++) {
/*  907 */       if (this.tableDirectory[i].tag == paramInt) {
/*  908 */         return this.tableDirectory[i];
/*      */       }
/*      */     }
/*  911 */     return null;
/*      */   }
/*      */ 
/*      */   boolean useEmbeddedBitmapsForSize(int paramInt)
/*      */   {
/*  918 */     if (!this.supportsCJK) {
/*  919 */       return false;
/*      */     }
/*  921 */     if (getDirectoryEntry(1161972803) == null) {
/*  922 */       return false;
/*      */     }
/*  924 */     ByteBuffer localByteBuffer = getTableBuffer(1161972803);
/*  925 */     int i = localByteBuffer.getInt(4);
/*      */ 
/*  930 */     for (int j = 0; j < i; j++) {
/*  931 */       int k = localByteBuffer.get(8 + j * 48 + 45) & 0xFF;
/*  932 */       if (k == paramInt) {
/*  933 */         return true;
/*      */       }
/*      */     }
/*  936 */     return false;
/*      */   }
/*      */ 
/*      */   public String getFullName() {
/*  940 */     return this.fullName;
/*      */   }
/*      */ 
/*      */   protected void setStyle()
/*      */   {
/*  948 */     setStyle(getTableBuffer(1330851634));
/*      */   }
/*      */ 
/*      */   private void setStyle(ByteBuffer paramByteBuffer)
/*      */   {
/*  967 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 64)) {
/*  968 */       super.setStyle();
/*  969 */       return;
/*      */     }
/*  971 */     int i = paramByteBuffer.getChar(62) & 0xFFFF;
/*  972 */     int j = i & 0x1;
/*  973 */     int k = i & 0x20;
/*  974 */     int m = i & 0x40;
/*      */ 
/*  978 */     if ((m != 0) && ((j | k) != 0))
/*      */     {
/*  980 */       super.setStyle();
/*  981 */       return;
/*  982 */     }if ((m | j | k) == 0)
/*      */     {
/*  984 */       super.setStyle();
/*  985 */       return;
/*      */     }
/*  987 */     switch (k | j) {
/*      */     case 1:
/*  989 */       this.style = 2;
/*  990 */       break;
/*      */     case 32:
/*  992 */       if ((FontUtilities.isSolaris) && (this.platName.endsWith("HG-GothicB.ttf")))
/*      */       {
/*  996 */         this.style = 0;
/*      */       }
/*  998 */       else this.style = 1;
/*      */ 
/* 1000 */       break;
/*      */     case 33:
/* 1002 */       this.style = 3;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setStrikethroughMetrics(ByteBuffer paramByteBuffer, int paramInt)
/*      */   {
/* 1009 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 30) || (paramInt < 0)) {
/* 1010 */       this.stSize = 0.05F;
/* 1011 */       this.stPos = -0.4F;
/* 1012 */       return;
/*      */     }
/* 1014 */     ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
/* 1015 */     this.stSize = (localShortBuffer.get(13) / paramInt);
/* 1016 */     this.stPos = (-localShortBuffer.get(14) / paramInt);
/*      */   }
/*      */ 
/*      */   private void setUnderlineMetrics(ByteBuffer paramByteBuffer, int paramInt) {
/* 1020 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 12) || (paramInt < 0)) {
/* 1021 */       this.ulSize = 0.05F;
/* 1022 */       this.ulPos = 0.1F;
/* 1023 */       return;
/*      */     }
/* 1025 */     ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
/* 1026 */     this.ulSize = (localShortBuffer.get(5) / paramInt);
/* 1027 */     this.ulPos = (-localShortBuffer.get(4) / paramInt);
/*      */   }
/*      */ 
/*      */   public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt)
/*      */   {
/* 1033 */     if ((this.ulSize == 0.0F) && (this.ulPos == 0.0F))
/*      */     {
/* 1035 */       ByteBuffer localByteBuffer1 = getTableBuffer(1751474532);
/* 1036 */       int i = -1;
/* 1037 */       if ((localByteBuffer1 != null) && (localByteBuffer1.capacity() >= 18)) {
/* 1038 */         localObject = localByteBuffer1.asShortBuffer();
/* 1039 */         i = ((ShortBuffer)localObject).get(9) & 0xFFFF;
/*      */       }
/*      */ 
/* 1042 */       Object localObject = getTableBuffer(1330851634);
/* 1043 */       setStrikethroughMetrics((ByteBuffer)localObject, i);
/*      */ 
/* 1045 */       ByteBuffer localByteBuffer2 = getTableBuffer(1886352244);
/* 1046 */       setUnderlineMetrics(localByteBuffer2, i);
/*      */     }
/*      */ 
/* 1049 */     paramArrayOfFloat[paramInt] = (this.stPos * paramFloat);
/* 1050 */     paramArrayOfFloat[(paramInt + 1)] = (this.stSize * paramFloat);
/*      */ 
/* 1052 */     paramArrayOfFloat[(paramInt + 2)] = (this.ulPos * paramFloat);
/* 1053 */     paramArrayOfFloat[(paramInt + 3)] = (this.ulSize * paramFloat);
/*      */   }
/*      */ 
/*      */   private String makeString(byte[] paramArrayOfByte, int paramInt, short paramShort)
/*      */   {
/*      */     Object localObject;
/* 1064 */     if ((paramShort >= 2) && (paramShort <= 6)) {
/* 1065 */       localObject = paramArrayOfByte;
/* 1066 */       int i = paramInt;
/* 1067 */       paramArrayOfByte = new byte[i];
/* 1068 */       paramInt = 0;
/* 1069 */       for (int j = 0; j < i; j++) {
/* 1070 */         if (localObject[j] != 0) {
/* 1071 */           paramArrayOfByte[(paramInt++)] = localObject[j];
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1077 */     switch (paramShort) { case 1:
/* 1078 */       localObject = "UTF-16"; break;
/*      */     case 0:
/* 1079 */       localObject = "UTF-16"; break;
/*      */     case 2:
/* 1080 */       localObject = "SJIS"; break;
/*      */     case 3:
/* 1081 */       localObject = "GBK"; break;
/*      */     case 4:
/* 1082 */       localObject = "MS950"; break;
/*      */     case 5:
/* 1083 */       localObject = "EUC_KR"; break;
/*      */     case 6:
/* 1084 */       localObject = "Johab"; break;
/*      */     default:
/* 1085 */       localObject = "UTF-16";
/*      */     }
/*      */     try
/*      */     {
/* 1089 */       return new String(paramArrayOfByte, 0, paramInt, (String)localObject);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1091 */       if (FontUtilities.isLogging()) {
/* 1092 */         FontUtilities.getLogger().warning(localUnsupportedEncodingException + " EncodingID=" + paramShort);
/*      */       }
/* 1094 */       return new String(paramArrayOfByte, 0, paramInt); } catch (Throwable localThrowable) {
/*      */     }
/* 1096 */     return null;
/*      */   }
/*      */ 
/*      */   protected void initNames()
/*      */   {
/* 1102 */     byte[] arrayOfByte = new byte[256];
/* 1103 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/*      */ 
/* 1105 */     if (localByteBuffer != null) {
/* 1106 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1107 */       localShortBuffer.get();
/* 1108 */       int i = localShortBuffer.get();
/*      */ 
/* 1114 */       int j = localShortBuffer.get() & 0xFFFF;
/*      */ 
/* 1116 */       this.nameLocale = SunToolkit.getStartupLocale();
/* 1117 */       int k = getLCIDFromLocale(this.nameLocale);
/*      */ 
/* 1119 */       for (int m = 0; m < i; m++) {
/* 1120 */         int n = localShortBuffer.get();
/* 1121 */         if (n != 3) {
/* 1122 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1125 */           short s = localShortBuffer.get();
/* 1126 */           int i1 = localShortBuffer.get();
/* 1127 */           int i2 = localShortBuffer.get();
/* 1128 */           int i3 = localShortBuffer.get() & 0xFFFF;
/* 1129 */           int i4 = (localShortBuffer.get() & 0xFFFF) + j;
/* 1130 */           String str = null;
/* 1131 */           switch (i2)
/*      */           {
/*      */           case 1:
/* 1135 */             if ((this.familyName == null) || (i1 == 1033) || (i1 == k))
/*      */             {
/* 1138 */               localByteBuffer.position(i4);
/* 1139 */               localByteBuffer.get(arrayOfByte, 0, i3);
/* 1140 */               str = makeString(arrayOfByte, i3, s);
/*      */ 
/* 1142 */               if ((this.familyName == null) || (i1 == 1033)) {
/* 1143 */                 this.familyName = str;
/*      */               }
/* 1145 */               if (i1 == k)
/* 1146 */                 this.localeFamilyName = str;  } break;
/*      */           case 4:
/* 1165 */             if ((this.fullName == null) || (i1 == 1033) || (i1 == k))
/*      */             {
/* 1168 */               localByteBuffer.position(i4);
/* 1169 */               localByteBuffer.get(arrayOfByte, 0, i3);
/* 1170 */               str = makeString(arrayOfByte, i3, s);
/*      */ 
/* 1172 */               if ((this.fullName == null) || (i1 == 1033)) {
/* 1173 */                 this.fullName = str;
/*      */               }
/* 1175 */               if (i1 == k)
/* 1176 */                 this.localeFullName = str;
/*      */             }
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/* 1182 */       if (this.localeFamilyName == null) {
/* 1183 */         this.localeFamilyName = this.familyName;
/*      */       }
/* 1185 */       if (this.localeFullName == null)
/* 1186 */         this.localeFullName = this.fullName;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String lookupName(short paramShort, int paramInt)
/*      */   {
/* 1197 */     String str = null;
/* 1198 */     byte[] arrayOfByte = new byte[1024];
/*      */ 
/* 1200 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/* 1201 */     if (localByteBuffer != null) {
/* 1202 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1203 */       localShortBuffer.get();
/* 1204 */       int i = localShortBuffer.get();
/*      */ 
/* 1211 */       int j = localShortBuffer.get() & 0xFFFF;
/*      */ 
/* 1213 */       for (int k = 0; k < i; k++) {
/* 1214 */         int m = localShortBuffer.get();
/* 1215 */         if (m != 3) {
/* 1216 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1219 */           short s1 = localShortBuffer.get();
/* 1220 */           short s2 = localShortBuffer.get();
/* 1221 */           int n = localShortBuffer.get();
/* 1222 */           int i1 = localShortBuffer.get() & 0xFFFF;
/* 1223 */           int i2 = (localShortBuffer.get() & 0xFFFF) + j;
/* 1224 */           if ((n == paramInt) && (((str == null) && (s2 == 1033)) || (s2 == paramShort)))
/*      */           {
/* 1227 */             localByteBuffer.position(i2);
/* 1228 */             localByteBuffer.get(arrayOfByte, 0, i1);
/* 1229 */             str = makeString(arrayOfByte, i1, s1);
/* 1230 */             if (s2 == paramShort)
/* 1231 */               return str;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1236 */     return str;
/*      */   }
/*      */ 
/*      */   public int getFontCount()
/*      */   {
/* 1243 */     return this.directoryCount;
/*      */   }
/*      */ 
/*      */   protected synchronized FontScaler getScaler() {
/* 1247 */     if (this.scaler == null) {
/* 1248 */       this.scaler = FontScaler.getScaler(this, this.fontIndex, this.supportsCJK, this.fileSize);
/*      */     }
/*      */ 
/* 1251 */     return this.scaler;
/*      */   }
/*      */ 
/*      */   public String getPostscriptName()
/*      */   {
/* 1260 */     String str = lookupName((short)1033, 6);
/* 1261 */     if (str == null) {
/* 1262 */       return this.fullName;
/*      */     }
/* 1264 */     return str;
/*      */   }
/*      */ 
/*      */   public String getFontName(Locale paramLocale)
/*      */   {
/* 1270 */     if (paramLocale == null)
/* 1271 */       return this.fullName;
/* 1272 */     if ((paramLocale.equals(this.nameLocale)) && (this.localeFullName != null)) {
/* 1273 */       return this.localeFullName;
/*      */     }
/* 1275 */     short s = getLCIDFromLocale(paramLocale);
/* 1276 */     String str = lookupName(s, 4);
/* 1277 */     if (str == null) {
/* 1278 */       return this.fullName;
/*      */     }
/* 1280 */     return str;
/*      */   }
/*      */ 
/*      */   private static void addLCIDMapEntry(Map<String, Short> paramMap, String paramString, short paramShort)
/*      */   {
/* 1290 */     paramMap.put(paramString, Short.valueOf(paramShort));
/*      */   }
/*      */ 
/*      */   private static synchronized void createLCIDMap() {
/* 1294 */     if (lcidMap != null) {
/* 1295 */       return;
/*      */     }
/*      */ 
/* 1298 */     HashMap localHashMap = new HashMap(200);
/*      */ 
/* 1326 */     addLCIDMapEntry(localHashMap, "ar", (short)1025);
/* 1327 */     addLCIDMapEntry(localHashMap, "bg", (short)1026);
/* 1328 */     addLCIDMapEntry(localHashMap, "ca", (short)1027);
/* 1329 */     addLCIDMapEntry(localHashMap, "zh", (short)1028);
/* 1330 */     addLCIDMapEntry(localHashMap, "cs", (short)1029);
/* 1331 */     addLCIDMapEntry(localHashMap, "da", (short)1030);
/* 1332 */     addLCIDMapEntry(localHashMap, "de", (short)1031);
/* 1333 */     addLCIDMapEntry(localHashMap, "el", (short)1032);
/* 1334 */     addLCIDMapEntry(localHashMap, "es", (short)1034);
/* 1335 */     addLCIDMapEntry(localHashMap, "fi", (short)1035);
/* 1336 */     addLCIDMapEntry(localHashMap, "fr", (short)1036);
/* 1337 */     addLCIDMapEntry(localHashMap, "iw", (short)1037);
/* 1338 */     addLCIDMapEntry(localHashMap, "hu", (short)1038);
/* 1339 */     addLCIDMapEntry(localHashMap, "is", (short)1039);
/* 1340 */     addLCIDMapEntry(localHashMap, "it", (short)1040);
/* 1341 */     addLCIDMapEntry(localHashMap, "ja", (short)1041);
/* 1342 */     addLCIDMapEntry(localHashMap, "ko", (short)1042);
/* 1343 */     addLCIDMapEntry(localHashMap, "nl", (short)1043);
/* 1344 */     addLCIDMapEntry(localHashMap, "no", (short)1044);
/* 1345 */     addLCIDMapEntry(localHashMap, "pl", (short)1045);
/* 1346 */     addLCIDMapEntry(localHashMap, "pt", (short)1046);
/* 1347 */     addLCIDMapEntry(localHashMap, "rm", (short)1047);
/* 1348 */     addLCIDMapEntry(localHashMap, "ro", (short)1048);
/* 1349 */     addLCIDMapEntry(localHashMap, "ru", (short)1049);
/* 1350 */     addLCIDMapEntry(localHashMap, "hr", (short)1050);
/* 1351 */     addLCIDMapEntry(localHashMap, "sk", (short)1051);
/* 1352 */     addLCIDMapEntry(localHashMap, "sq", (short)1052);
/* 1353 */     addLCIDMapEntry(localHashMap, "sv", (short)1053);
/* 1354 */     addLCIDMapEntry(localHashMap, "th", (short)1054);
/* 1355 */     addLCIDMapEntry(localHashMap, "tr", (short)1055);
/* 1356 */     addLCIDMapEntry(localHashMap, "ur", (short)1056);
/* 1357 */     addLCIDMapEntry(localHashMap, "in", (short)1057);
/* 1358 */     addLCIDMapEntry(localHashMap, "uk", (short)1058);
/* 1359 */     addLCIDMapEntry(localHashMap, "be", (short)1059);
/* 1360 */     addLCIDMapEntry(localHashMap, "sl", (short)1060);
/* 1361 */     addLCIDMapEntry(localHashMap, "et", (short)1061);
/* 1362 */     addLCIDMapEntry(localHashMap, "lv", (short)1062);
/* 1363 */     addLCIDMapEntry(localHashMap, "lt", (short)1063);
/* 1364 */     addLCIDMapEntry(localHashMap, "fa", (short)1065);
/* 1365 */     addLCIDMapEntry(localHashMap, "vi", (short)1066);
/* 1366 */     addLCIDMapEntry(localHashMap, "hy", (short)1067);
/* 1367 */     addLCIDMapEntry(localHashMap, "eu", (short)1069);
/* 1368 */     addLCIDMapEntry(localHashMap, "mk", (short)1071);
/* 1369 */     addLCIDMapEntry(localHashMap, "tn", (short)1074);
/* 1370 */     addLCIDMapEntry(localHashMap, "xh", (short)1076);
/* 1371 */     addLCIDMapEntry(localHashMap, "zu", (short)1077);
/* 1372 */     addLCIDMapEntry(localHashMap, "af", (short)1078);
/* 1373 */     addLCIDMapEntry(localHashMap, "ka", (short)1079);
/* 1374 */     addLCIDMapEntry(localHashMap, "fo", (short)1080);
/* 1375 */     addLCIDMapEntry(localHashMap, "hi", (short)1081);
/* 1376 */     addLCIDMapEntry(localHashMap, "mt", (short)1082);
/* 1377 */     addLCIDMapEntry(localHashMap, "se", (short)1083);
/* 1378 */     addLCIDMapEntry(localHashMap, "gd", (short)1084);
/* 1379 */     addLCIDMapEntry(localHashMap, "ms", (short)1086);
/* 1380 */     addLCIDMapEntry(localHashMap, "kk", (short)1087);
/* 1381 */     addLCIDMapEntry(localHashMap, "ky", (short)1088);
/* 1382 */     addLCIDMapEntry(localHashMap, "sw", (short)1089);
/* 1383 */     addLCIDMapEntry(localHashMap, "tt", (short)1092);
/* 1384 */     addLCIDMapEntry(localHashMap, "bn", (short)1093);
/* 1385 */     addLCIDMapEntry(localHashMap, "pa", (short)1094);
/* 1386 */     addLCIDMapEntry(localHashMap, "gu", (short)1095);
/* 1387 */     addLCIDMapEntry(localHashMap, "ta", (short)1097);
/* 1388 */     addLCIDMapEntry(localHashMap, "te", (short)1098);
/* 1389 */     addLCIDMapEntry(localHashMap, "kn", (short)1099);
/* 1390 */     addLCIDMapEntry(localHashMap, "ml", (short)1100);
/* 1391 */     addLCIDMapEntry(localHashMap, "mr", (short)1102);
/* 1392 */     addLCIDMapEntry(localHashMap, "sa", (short)1103);
/* 1393 */     addLCIDMapEntry(localHashMap, "mn", (short)1104);
/* 1394 */     addLCIDMapEntry(localHashMap, "cy", (short)1106);
/* 1395 */     addLCIDMapEntry(localHashMap, "gl", (short)1110);
/* 1396 */     addLCIDMapEntry(localHashMap, "dv", (short)1125);
/* 1397 */     addLCIDMapEntry(localHashMap, "qu", (short)1131);
/* 1398 */     addLCIDMapEntry(localHashMap, "mi", (short)1153);
/* 1399 */     addLCIDMapEntry(localHashMap, "ar_IQ", (short)2049);
/* 1400 */     addLCIDMapEntry(localHashMap, "zh_CN", (short)2052);
/* 1401 */     addLCIDMapEntry(localHashMap, "de_CH", (short)2055);
/* 1402 */     addLCIDMapEntry(localHashMap, "en_GB", (short)2057);
/* 1403 */     addLCIDMapEntry(localHashMap, "es_MX", (short)2058);
/* 1404 */     addLCIDMapEntry(localHashMap, "fr_BE", (short)2060);
/* 1405 */     addLCIDMapEntry(localHashMap, "it_CH", (short)2064);
/* 1406 */     addLCIDMapEntry(localHashMap, "nl_BE", (short)2067);
/* 1407 */     addLCIDMapEntry(localHashMap, "no_NO_NY", (short)2068);
/* 1408 */     addLCIDMapEntry(localHashMap, "pt_PT", (short)2070);
/* 1409 */     addLCIDMapEntry(localHashMap, "ro_MD", (short)2072);
/* 1410 */     addLCIDMapEntry(localHashMap, "ru_MD", (short)2073);
/* 1411 */     addLCIDMapEntry(localHashMap, "sr_CS", (short)2074);
/* 1412 */     addLCIDMapEntry(localHashMap, "sv_FI", (short)2077);
/* 1413 */     addLCIDMapEntry(localHashMap, "az_AZ", (short)2092);
/* 1414 */     addLCIDMapEntry(localHashMap, "se_SE", (short)2107);
/* 1415 */     addLCIDMapEntry(localHashMap, "ga_IE", (short)2108);
/* 1416 */     addLCIDMapEntry(localHashMap, "ms_BN", (short)2110);
/* 1417 */     addLCIDMapEntry(localHashMap, "uz_UZ", (short)2115);
/* 1418 */     addLCIDMapEntry(localHashMap, "qu_EC", (short)2155);
/* 1419 */     addLCIDMapEntry(localHashMap, "ar_EG", (short)3073);
/* 1420 */     addLCIDMapEntry(localHashMap, "zh_HK", (short)3076);
/* 1421 */     addLCIDMapEntry(localHashMap, "de_AT", (short)3079);
/* 1422 */     addLCIDMapEntry(localHashMap, "en_AU", (short)3081);
/* 1423 */     addLCIDMapEntry(localHashMap, "fr_CA", (short)3084);
/* 1424 */     addLCIDMapEntry(localHashMap, "sr_CS", (short)3098);
/* 1425 */     addLCIDMapEntry(localHashMap, "se_FI", (short)3131);
/* 1426 */     addLCIDMapEntry(localHashMap, "qu_PE", (short)3179);
/* 1427 */     addLCIDMapEntry(localHashMap, "ar_LY", (short)4097);
/* 1428 */     addLCIDMapEntry(localHashMap, "zh_SG", (short)4100);
/* 1429 */     addLCIDMapEntry(localHashMap, "de_LU", (short)4103);
/* 1430 */     addLCIDMapEntry(localHashMap, "en_CA", (short)4105);
/* 1431 */     addLCIDMapEntry(localHashMap, "es_GT", (short)4106);
/* 1432 */     addLCIDMapEntry(localHashMap, "fr_CH", (short)4108);
/* 1433 */     addLCIDMapEntry(localHashMap, "hr_BA", (short)4122);
/* 1434 */     addLCIDMapEntry(localHashMap, "ar_DZ", (short)5121);
/* 1435 */     addLCIDMapEntry(localHashMap, "zh_MO", (short)5124);
/* 1436 */     addLCIDMapEntry(localHashMap, "de_LI", (short)5127);
/* 1437 */     addLCIDMapEntry(localHashMap, "en_NZ", (short)5129);
/* 1438 */     addLCIDMapEntry(localHashMap, "es_CR", (short)5130);
/* 1439 */     addLCIDMapEntry(localHashMap, "fr_LU", (short)5132);
/* 1440 */     addLCIDMapEntry(localHashMap, "bs_BA", (short)5146);
/* 1441 */     addLCIDMapEntry(localHashMap, "ar_MA", (short)6145);
/* 1442 */     addLCIDMapEntry(localHashMap, "en_IE", (short)6153);
/* 1443 */     addLCIDMapEntry(localHashMap, "es_PA", (short)6154);
/* 1444 */     addLCIDMapEntry(localHashMap, "fr_MC", (short)6156);
/* 1445 */     addLCIDMapEntry(localHashMap, "sr_BA", (short)6170);
/* 1446 */     addLCIDMapEntry(localHashMap, "ar_TN", (short)7169);
/* 1447 */     addLCIDMapEntry(localHashMap, "en_ZA", (short)7177);
/* 1448 */     addLCIDMapEntry(localHashMap, "es_DO", (short)7178);
/* 1449 */     addLCIDMapEntry(localHashMap, "sr_BA", (short)7194);
/* 1450 */     addLCIDMapEntry(localHashMap, "ar_OM", (short)8193);
/* 1451 */     addLCIDMapEntry(localHashMap, "en_JM", (short)8201);
/* 1452 */     addLCIDMapEntry(localHashMap, "es_VE", (short)8202);
/* 1453 */     addLCIDMapEntry(localHashMap, "ar_YE", (short)9217);
/* 1454 */     addLCIDMapEntry(localHashMap, "es_CO", (short)9226);
/* 1455 */     addLCIDMapEntry(localHashMap, "ar_SY", (short)10241);
/* 1456 */     addLCIDMapEntry(localHashMap, "en_BZ", (short)10249);
/* 1457 */     addLCIDMapEntry(localHashMap, "es_PE", (short)10250);
/* 1458 */     addLCIDMapEntry(localHashMap, "ar_JO", (short)11265);
/* 1459 */     addLCIDMapEntry(localHashMap, "en_TT", (short)11273);
/* 1460 */     addLCIDMapEntry(localHashMap, "es_AR", (short)11274);
/* 1461 */     addLCIDMapEntry(localHashMap, "ar_LB", (short)12289);
/* 1462 */     addLCIDMapEntry(localHashMap, "en_ZW", (short)12297);
/* 1463 */     addLCIDMapEntry(localHashMap, "es_EC", (short)12298);
/* 1464 */     addLCIDMapEntry(localHashMap, "ar_KW", (short)13313);
/* 1465 */     addLCIDMapEntry(localHashMap, "en_PH", (short)13321);
/* 1466 */     addLCIDMapEntry(localHashMap, "es_CL", (short)13322);
/* 1467 */     addLCIDMapEntry(localHashMap, "ar_AE", (short)14337);
/* 1468 */     addLCIDMapEntry(localHashMap, "es_UY", (short)14346);
/* 1469 */     addLCIDMapEntry(localHashMap, "ar_BH", (short)15361);
/* 1470 */     addLCIDMapEntry(localHashMap, "es_PY", (short)15370);
/* 1471 */     addLCIDMapEntry(localHashMap, "ar_QA", (short)16385);
/* 1472 */     addLCIDMapEntry(localHashMap, "es_BO", (short)16394);
/* 1473 */     addLCIDMapEntry(localHashMap, "es_SV", (short)17418);
/* 1474 */     addLCIDMapEntry(localHashMap, "es_HN", (short)18442);
/* 1475 */     addLCIDMapEntry(localHashMap, "es_NI", (short)19466);
/* 1476 */     addLCIDMapEntry(localHashMap, "es_PR", (short)20490);
/*      */ 
/* 1478 */     lcidMap = localHashMap;
/*      */   }
/*      */ 
/*      */   private static short getLCIDFromLocale(Locale paramLocale)
/*      */   {
/* 1483 */     if (paramLocale.equals(Locale.US)) {
/* 1484 */       return 1033;
/*      */     }
/*      */ 
/* 1487 */     if (lcidMap == null) {
/* 1488 */       createLCIDMap();
/*      */     }
/*      */ 
/* 1491 */     String str = paramLocale.toString();
/* 1492 */     while (!"".equals(str)) {
/* 1493 */       Short localShort = (Short)lcidMap.get(str);
/* 1494 */       if (localShort != null) {
/* 1495 */         return localShort.shortValue();
/*      */       }
/* 1497 */       int i = str.lastIndexOf('_');
/* 1498 */       if (i < 1) {
/* 1499 */         return 1033;
/*      */       }
/* 1501 */       str = str.substring(0, i);
/*      */     }
/*      */ 
/* 1504 */     return 1033;
/*      */   }
/*      */ 
/*      */   public String getFamilyName(Locale paramLocale)
/*      */   {
/* 1509 */     if (paramLocale == null)
/* 1510 */       return this.familyName;
/* 1511 */     if ((paramLocale.equals(this.nameLocale)) && (this.localeFamilyName != null)) {
/* 1512 */       return this.localeFamilyName;
/*      */     }
/* 1514 */     short s = getLCIDFromLocale(paramLocale);
/* 1515 */     String str = lookupName(s, 1);
/* 1516 */     if (str == null) {
/* 1517 */       return this.familyName;
/*      */     }
/* 1519 */     return str;
/*      */   }
/*      */ 
/*      */   public CharToGlyphMapper getMapper()
/*      */   {
/* 1525 */     if (this.mapper == null) {
/* 1526 */       this.mapper = new TrueTypeGlyphMapper(this);
/*      */     }
/* 1528 */     return this.mapper;
/*      */   }
/*      */ 
/*      */   protected void initAllNames(int paramInt, HashSet paramHashSet)
/*      */   {
/* 1537 */     byte[] arrayOfByte = new byte[256];
/* 1538 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/*      */ 
/* 1540 */     if (localByteBuffer != null) {
/* 1541 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1542 */       localShortBuffer.get();
/* 1543 */       int i = localShortBuffer.get();
/*      */ 
/* 1550 */       int j = localShortBuffer.get() & 0xFFFF;
/* 1551 */       for (int k = 0; k < i; k++) {
/* 1552 */         int m = localShortBuffer.get();
/* 1553 */         if (m != 3) {
/* 1554 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1557 */           short s = localShortBuffer.get();
/* 1558 */           int n = localShortBuffer.get();
/* 1559 */           int i1 = localShortBuffer.get();
/* 1560 */           int i2 = localShortBuffer.get() & 0xFFFF;
/* 1561 */           int i3 = (localShortBuffer.get() & 0xFFFF) + j;
/*      */ 
/* 1563 */           if (i1 == paramInt) {
/* 1564 */             localByteBuffer.position(i3);
/* 1565 */             localByteBuffer.get(arrayOfByte, 0, i2);
/* 1566 */             paramHashSet.add(makeString(arrayOfByte, i2, s));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/* 1573 */   String[] getAllFamilyNames() { HashSet localHashSet = new HashSet();
/*      */     try {
/* 1575 */       initAllNames(1, localHashSet);
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/* 1579 */     return (String[])localHashSet.toArray(new String[0]); }
/*      */ 
/*      */   String[] getAllFullNames()
/*      */   {
/* 1583 */     HashSet localHashSet = new HashSet();
/*      */     try {
/* 1585 */       initAllNames(4, localHashSet);
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/* 1589 */     return (String[])localHashSet.toArray(new String[0]);
/*      */   }
/*      */ 
/*      */   Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
/*      */   {
/*      */     try
/*      */     {
/* 1598 */       return getScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
/*      */     } catch (FontScalerException localFontScalerException) {
/*      */     }
/* 1601 */     return null;
/*      */   }
/*      */ 
/*      */   private char[] getGaspTable()
/*      */   {
/* 1609 */     if (this.gaspTable != null) {
/* 1610 */       return this.gaspTable;
/*      */     }
/*      */ 
/* 1613 */     ByteBuffer localByteBuffer = getTableBuffer(1734439792);
/* 1614 */     if (localByteBuffer == null) {
/* 1615 */       return this.gaspTable = new char[0];
/*      */     }
/*      */ 
/* 1618 */     CharBuffer localCharBuffer = localByteBuffer.asCharBuffer();
/* 1619 */     int i = localCharBuffer.get();
/*      */ 
/* 1624 */     if (i > 1) {
/* 1625 */       return this.gaspTable = new char[0];
/*      */     }
/*      */ 
/* 1628 */     int j = localCharBuffer.get();
/* 1629 */     if (4 + j * 4 > getTableSize(1734439792)) {
/* 1630 */       return this.gaspTable = new char[0];
/*      */     }
/* 1632 */     this.gaspTable = new char[2 * j];
/* 1633 */     localCharBuffer.get(this.gaspTable);
/* 1634 */     return this.gaspTable;
/*      */   }
/*      */ 
/*      */   public boolean useAAForPtSize(int paramInt)
/*      */   {
/* 1659 */     char[] arrayOfChar = getGaspTable();
/* 1660 */     if (arrayOfChar.length > 0) {
/* 1661 */       for (int i = 0; i < arrayOfChar.length; i += 2) {
/* 1662 */         if (paramInt <= arrayOfChar[i]) {
/* 1663 */           return (arrayOfChar[(i + 1)] & 0x2) != 0;
/*      */         }
/*      */       }
/* 1666 */       return true;
/*      */     }
/*      */ 
/* 1669 */     if (this.style == 1) {
/* 1670 */       return true;
/*      */     }
/* 1672 */     return (paramInt <= 8) || (paramInt >= 18);
/*      */   }
/*      */ 
/*      */   public boolean hasSupplementaryChars()
/*      */   {
/* 1678 */     return ((TrueTypeGlyphMapper)getMapper()).hasSupplementaryChars();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1683 */     return "** TrueType Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + getPublicFileName();
/*      */   }
/*      */ 
/*      */   class DirectoryEntry
/*      */   {
/*      */     int tag;
/*      */     int offset;
/*      */     int length;
/*      */ 
/*      */     DirectoryEntry()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TTDisposerRecord
/*      */     implements DisposerRecord
/*      */   {
/*  125 */     FileChannel channel = null;
/*      */ 
/*      */     public synchronized void dispose() {
/*      */       try {
/*  129 */         if (this.channel != null)
/*  130 */           this.channel.close();
/*      */       } catch (IOException localIOException) {
/*      */       }
/*      */       finally {
/*  134 */         this.channel = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.TrueTypeFont
 * JD-Core Version:    0.6.2
 */