/*      */ package sun.font;
/*      */ 
/*      */ import java.awt.Font;
/*      */ import java.awt.FontFormatException;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FilenameFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.Vector;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import javax.swing.plaf.FontUIResource;
/*      */ import sun.applet.AppletSecurity;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.FontConfiguration;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.java2d.FontSupport;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class SunFontManager
/*      */   implements FontSupport, FontManagerForSGE
/*      */ {
/*      */   public static final int FONTFORMAT_NONE = -1;
/*      */   public static final int FONTFORMAT_TRUETYPE = 0;
/*      */   public static final int FONTFORMAT_TYPE1 = 1;
/*      */   public static final int FONTFORMAT_T2K = 2;
/*      */   public static final int FONTFORMAT_TTC = 3;
/*      */   public static final int FONTFORMAT_COMPOSITE = 4;
/*      */   public static final int FONTFORMAT_NATIVE = 5;
/*      */   protected static final int CHANNELPOOLSIZE = 20;
/*  147 */   protected FileFont[] fontFileCache = new FileFont[20];
/*      */ 
/*  149 */   private int lastPoolIndex = 0;
/*      */ 
/*  160 */   private int maxCompFont = 0;
/*  161 */   private CompositeFont[] compFonts = new CompositeFont[20];
/*  162 */   private ConcurrentHashMap<String, CompositeFont> compositeFonts = new ConcurrentHashMap();
/*      */ 
/*  164 */   private ConcurrentHashMap<String, PhysicalFont> physicalFonts = new ConcurrentHashMap();
/*      */ 
/*  166 */   private ConcurrentHashMap<String, PhysicalFont> registeredFonts = new ConcurrentHashMap();
/*      */ 
/*  174 */   protected ConcurrentHashMap<String, Font2D> fullNameToFont = new ConcurrentHashMap();
/*      */   private HashMap<String, TrueTypeFont> localeFullNamesToFont;
/*      */   private PhysicalFont defaultPhysicalFont;
/*      */   static boolean longAddresses;
/*  186 */   private boolean loaded1dot0Fonts = false;
/*  187 */   boolean loadedAllFonts = false;
/*  188 */   boolean loadedAllFontFiles = false;
/*      */   HashMap<String, String> jreFontMap;
/*      */   HashSet<String> jreLucidaFontFiles;
/*      */   String[] jreOtherFontFiles;
/*  192 */   boolean noOtherJREFontFiles = false;
/*      */   public static final String lucidaFontName = "Lucida Sans Regular";
/*      */   public static String jreLibDirName;
/*      */   public static String jreFontDirName;
/*  197 */   private static HashSet<String> missingFontFiles = null;
/*      */   private String defaultFontName;
/*      */   private String defaultFontFileName;
/*  200 */   protected HashSet registeredFontFiles = new HashSet();
/*      */   private ArrayList badFonts;
/*      */   protected String fontPath;
/*      */   private FontConfiguration fontConfig;
/*  217 */   private boolean discoveredAllFonts = false;
/*      */ 
/*  222 */   private static final FilenameFilter ttFilter = new TTFilter(null);
/*  223 */   private static final FilenameFilter t1Filter = new T1Filter(null);
/*      */   private Font[] allFonts;
/*      */   private String[] allFamilies;
/*      */   private Locale lastDefaultLocale;
/*      */   public static boolean noType1Font;
/*  232 */   private static String[] STR_ARRAY = new String[0];
/*      */ 
/*  238 */   private boolean usePlatformFontMetrics = false;
/*      */ 
/*  912 */   private final ConcurrentHashMap<String, FontRegistrationInfo> deferredFontFiles = new ConcurrentHashMap();
/*      */ 
/*  915 */   private final ConcurrentHashMap<String, Font2DHandle> initialisedFonts = new ConcurrentHashMap();
/*      */ 
/* 1280 */   private HashMap<String, String> fontToFileMap = null;
/*      */ 
/* 1286 */   private HashMap<String, String> fontToFamilyNameMap = null;
/*      */ 
/* 1293 */   private HashMap<String, ArrayList<String>> familyToFontListMap = null;
/*      */ 
/* 1296 */   private String[] pathDirs = null;
/*      */   private boolean haveCheckedUnreferencedFontFiles;
/*      */   static HashMap<String, FamilyDescription> platformFontMap;
/* 2062 */   private ConcurrentHashMap<String, Font2D> fontNameCache = new ConcurrentHashMap();
/*      */ 
/* 2445 */   protected Thread fileCloser = null;
/*      */ 
/* 2447 */   Vector<File> tmpFontFiles = null;
/*      */ 
/* 2831 */   private static final Object altJAFontKey = new Object();
/* 2832 */   private static final Object localeFontKey = new Object();
/* 2833 */   private static final Object proportionalFontKey = new Object();
/* 2834 */   private boolean _usingPerAppContextComposites = false;
/* 2835 */   private boolean _usingAlternateComposites = false;
/*      */ 
/* 2840 */   private static boolean gAltJAFont = false;
/* 2841 */   private boolean gLocalePref = false;
/* 2842 */   private boolean gPropPref = false;
/*      */ 
/* 2979 */   private static HashSet<String> installedNames = null;
/*      */ 
/* 3000 */   private static final Object regFamilyKey = new Object();
/* 3001 */   private static final Object regFullNameKey = new Object();
/*      */   private Hashtable<String, FontFamily> createdByFamilyName;
/*      */   private Hashtable<String, Font2D> createdByFullName;
/* 3004 */   private boolean fontsAreRegistered = false;
/* 3005 */   private boolean fontsAreRegisteredPerAppContext = false;
/*      */ 
/* 3841 */   private static Locale systemLocale = null;
/*      */ 
/*      */   public static SunFontManager getInstance()
/*      */   {
/*  249 */     FontManager localFontManager = FontManagerFactory.getInstance();
/*  250 */     return (SunFontManager)localFontManager;
/*      */   }
/*      */ 
/*      */   public FilenameFilter getTrueTypeFilter() {
/*  254 */     return ttFilter;
/*      */   }
/*      */ 
/*      */   public FilenameFilter getType1Filter() {
/*  258 */     return t1Filter;
/*      */   }
/*      */ 
/*      */   public boolean usingPerAppContextComposites()
/*      */   {
/*  263 */     return this._usingPerAppContextComposites;
/*      */   }
/*      */ 
/*      */   private void initJREFontMap()
/*      */   {
/*  279 */     this.jreFontMap = new HashMap();
/*  280 */     this.jreLucidaFontFiles = new HashSet();
/*  281 */     if (isOpenJDK()) {
/*  282 */       return;
/*      */     }
/*      */ 
/*  285 */     this.jreFontMap.put("lucida sans0", "LucidaSansRegular.ttf");
/*  286 */     this.jreFontMap.put("lucida sans1", "LucidaSansDemiBold.ttf");
/*      */ 
/*  288 */     this.jreFontMap.put("lucida sans regular0", "LucidaSansRegular.ttf");
/*  289 */     this.jreFontMap.put("lucida sans regular1", "LucidaSansDemiBold.ttf");
/*  290 */     this.jreFontMap.put("lucida sans bold1", "LucidaSansDemiBold.ttf");
/*  291 */     this.jreFontMap.put("lucida sans demibold1", "LucidaSansDemiBold.ttf");
/*      */ 
/*  294 */     this.jreFontMap.put("lucida sans typewriter0", "LucidaTypewriterRegular.ttf");
/*      */ 
/*  296 */     this.jreFontMap.put("lucida sans typewriter1", "LucidaTypewriterBold.ttf");
/*      */ 
/*  298 */     this.jreFontMap.put("lucida sans typewriter regular0", "LucidaTypewriter.ttf");
/*      */ 
/*  300 */     this.jreFontMap.put("lucida sans typewriter regular1", "LucidaTypewriterBold.ttf");
/*      */ 
/*  302 */     this.jreFontMap.put("lucida sans typewriter bold1", "LucidaTypewriterBold.ttf");
/*      */ 
/*  304 */     this.jreFontMap.put("lucida sans typewriter demibold1", "LucidaTypewriterBold.ttf");
/*      */ 
/*  308 */     this.jreFontMap.put("lucida bright0", "LucidaBrightRegular.ttf");
/*  309 */     this.jreFontMap.put("lucida bright1", "LucidaBrightDemiBold.ttf");
/*  310 */     this.jreFontMap.put("lucida bright2", "LucidaBrightItalic.ttf");
/*  311 */     this.jreFontMap.put("lucida bright3", "LucidaBrightDemiItalic.ttf");
/*      */ 
/*  313 */     this.jreFontMap.put("lucida bright regular0", "LucidaBrightRegular.ttf");
/*  314 */     this.jreFontMap.put("lucida bright regular1", "LucidaBrightDemiBold.ttf");
/*  315 */     this.jreFontMap.put("lucida bright regular2", "LucidaBrightItalic.ttf");
/*  316 */     this.jreFontMap.put("lucida bright regular3", "LucidaBrightDemiItalic.ttf");
/*  317 */     this.jreFontMap.put("lucida bright bold1", "LucidaBrightDemiBold.ttf");
/*  318 */     this.jreFontMap.put("lucida bright bold3", "LucidaBrightDemiItalic.ttf");
/*  319 */     this.jreFontMap.put("lucida bright demibold1", "LucidaBrightDemiBold.ttf");
/*  320 */     this.jreFontMap.put("lucida bright demibold3", "LucidaBrightDemiItalic.ttf");
/*  321 */     this.jreFontMap.put("lucida bright italic2", "LucidaBrightItalic.ttf");
/*  322 */     this.jreFontMap.put("lucida bright italic3", "LucidaBrightDemiItalic.ttf");
/*  323 */     this.jreFontMap.put("lucida bright bold italic3", "LucidaBrightDemiItalic.ttf");
/*      */ 
/*  325 */     this.jreFontMap.put("lucida bright demibold italic3", "LucidaBrightDemiItalic.ttf");
/*      */ 
/*  327 */     for (String str : this.jreFontMap.values())
/*  328 */       this.jreLucidaFontFiles.add(str);
/*      */   }
/*      */ 
/*      */   public TrueTypeFont getEUDCFont()
/*      */   {
/*  365 */     return null;
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   protected SunFontManager()
/*      */   {
/*  374 */     initJREFontMap();
/*  375 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*  378 */         File localFile = new File(SunFontManager.jreFontDirName + File.separator + "badfonts.txt");
/*      */ 
/*  381 */         if (localFile.exists()) {
/*  382 */           localObject = null;
/*      */           try {
/*  384 */             SunFontManager.this.badFonts = new ArrayList();
/*  385 */             localObject = new FileInputStream(localFile);
/*  386 */             InputStreamReader localInputStreamReader = new InputStreamReader((InputStream)localObject);
/*  387 */             BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
/*      */             while (true) {
/*  389 */               str = localBufferedReader.readLine();
/*  390 */               if (str == null) {
/*      */                 break;
/*      */               }
/*  393 */               if (FontUtilities.debugFonts()) {
/*  394 */                 FontUtilities.getLogger().warning("read bad font: " + str);
/*      */               }
/*      */ 
/*  397 */               SunFontManager.this.badFonts.add(str);
/*      */             }
/*      */           }
/*      */           catch (IOException localIOException1) {
/*      */             try {
/*  402 */               if (localObject != null) {
/*  403 */                 ((FileInputStream)localObject).close();
/*      */               }
/*      */ 
/*      */             }
/*      */             catch (IOException localIOException2)
/*      */             {
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  420 */         if (FontUtilities.isLinux)
/*      */         {
/*  422 */           SunFontManager.this.registerFontDir(SunFontManager.jreFontDirName);
/*      */         }
/*  424 */         SunFontManager.this.registerFontsInDir(SunFontManager.jreFontDirName, true, 2, true, false);
/*      */ 
/*  430 */         SunFontManager.this.fontConfig = SunFontManager.this.createFontConfiguration();
/*  431 */         if (SunFontManager.isOpenJDK()) {
/*  432 */           localObject = SunFontManager.this.getDefaultPlatformFont();
/*  433 */           SunFontManager.this.defaultFontName = localObject[0];
/*  434 */           SunFontManager.this.defaultFontFileName = localObject[1];
/*      */         }
/*      */ 
/*  437 */         Object localObject = SunFontManager.this.fontConfig.getExtraFontPath();
/*      */ 
/*  465 */         int i = 0;
/*  466 */         int j = 0;
/*  467 */         String str = System.getProperty("sun.java2d.fontpath");
/*      */ 
/*  470 */         if (str != null) {
/*  471 */           if (str.startsWith("prepend:")) {
/*  472 */             i = 1;
/*  473 */             str = str.substring("prepend:".length());
/*      */           }
/*  475 */           else if (str.startsWith("append:")) {
/*  476 */             j = 1;
/*  477 */             str = str.substring("append:".length());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  482 */         if (FontUtilities.debugFonts()) {
/*  483 */           PlatformLogger localPlatformLogger = FontUtilities.getLogger();
/*  484 */           localPlatformLogger.info("JRE font directory: " + SunFontManager.jreFontDirName);
/*  485 */           localPlatformLogger.info("Extra font path: " + (String)localObject);
/*  486 */           localPlatformLogger.info("Debug font path: " + str);
/*      */         }
/*      */ 
/*  489 */         if (str != null)
/*      */         {
/*  493 */           SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
/*      */ 
/*  495 */           if (localObject != null) {
/*  496 */             SunFontManager.this.fontPath = ((String)localObject + File.pathSeparator + SunFontManager.this.fontPath);
/*      */           }
/*      */ 
/*  499 */           if (j != 0) {
/*  500 */             SunFontManager.this.fontPath = (SunFontManager.this.fontPath + File.pathSeparator + str);
/*      */           }
/*  502 */           else if (i != 0) {
/*  503 */             SunFontManager.this.fontPath = (str + File.pathSeparator + SunFontManager.this.fontPath);
/*      */           }
/*      */           else {
/*  506 */             SunFontManager.this.fontPath = str;
/*      */           }
/*  508 */           SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
/*  509 */         } else if (localObject != null)
/*      */         {
/*  523 */           SunFontManager.this.registerFontDirs((String)localObject);
/*      */         }
/*      */ 
/*  540 */         if ((FontUtilities.isSolaris) && (Locale.JAPAN.equals(Locale.getDefault()))) {
/*  541 */           SunFontManager.this.registerFontDir("/usr/openwin/lib/locale/ja/X11/fonts/TT");
/*      */         }
/*      */ 
/*  544 */         SunFontManager.this.initCompositeFonts(SunFontManager.this.fontConfig, null);
/*      */ 
/*  546 */         return null;
/*      */       }
/*      */     });
/*  550 */     boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/*  553 */         String str1 = System.getProperty("java2d.font.usePlatformFont");
/*      */ 
/*  555 */         String str2 = System.getenv("JAVA2D_USEPLATFORMFONT");
/*  556 */         return Boolean.valueOf(("true".equals(str1)) || (str2 != null));
/*      */       }
/*      */     })).booleanValue();
/*      */ 
/*  560 */     if (bool) {
/*  561 */       this.usePlatformFontMetrics = true;
/*  562 */       System.out.println("Enabling platform font metrics for win32. This is an unsupported option.");
/*  563 */       System.out.println("This yields incorrect composite font metrics as reported by 1.1.x releases.");
/*  564 */       System.out.println("It is appropriate only for use by applications which do not use any Java 2");
/*  565 */       System.out.println("functionality. This property will be removed in a later release.");
/*      */     }
/*      */   }
/*      */ 
/*      */   public Font2DHandle getNewComposite(String paramString, int paramInt, Font2DHandle paramFont2DHandle)
/*      */   {
/*  602 */     if (!(paramFont2DHandle.font2D instanceof CompositeFont)) {
/*  603 */       return paramFont2DHandle;
/*      */     }
/*      */ 
/*  606 */     CompositeFont localCompositeFont1 = (CompositeFont)paramFont2DHandle.font2D;
/*  607 */     PhysicalFont localPhysicalFont1 = localCompositeFont1.getSlotFont(0);
/*      */ 
/*  609 */     if (paramString == null) {
/*  610 */       paramString = localPhysicalFont1.getFamilyName(null);
/*      */     }
/*  612 */     if (paramInt == -1) {
/*  613 */       paramInt = localCompositeFont1.getStyle();
/*      */     }
/*      */ 
/*  616 */     Object localObject = findFont2D(paramString, paramInt, 0);
/*  617 */     if (!(localObject instanceof PhysicalFont)) {
/*  618 */       localObject = localPhysicalFont1;
/*      */     }
/*  620 */     PhysicalFont localPhysicalFont2 = (PhysicalFont)localObject;
/*  621 */     CompositeFont localCompositeFont2 = (CompositeFont)findFont2D("dialog", paramInt, 0);
/*      */ 
/*  623 */     if (localCompositeFont2 == null) {
/*  624 */       return paramFont2DHandle;
/*      */     }
/*  626 */     CompositeFont localCompositeFont3 = new CompositeFont(localPhysicalFont2, localCompositeFont2);
/*  627 */     Font2DHandle localFont2DHandle = new Font2DHandle(localCompositeFont3);
/*  628 */     return localFont2DHandle;
/*      */   }
/*      */ 
/*      */   protected void registerCompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
/*      */   {
/*  639 */     CompositeFont localCompositeFont = new CompositeFont(paramString, paramArrayOfString1, paramArrayOfString2, paramInt, paramArrayOfInt1, paramArrayOfInt2, paramBoolean, this);
/*      */ 
/*  645 */     addCompositeToFontList(localCompositeFont, 2);
/*  646 */     synchronized (this.compFonts) {
/*  647 */       this.compFonts[(this.maxCompFont++)] = localCompositeFont;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void registerCompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean, ConcurrentHashMap<String, Font2D> paramConcurrentHashMap)
/*      */   {
/*  665 */     CompositeFont localCompositeFont = new CompositeFont(paramString, paramArrayOfString1, paramArrayOfString2, paramInt, paramArrayOfInt1, paramArrayOfInt2, paramBoolean, getInstance());
/*      */ 
/*  686 */     Font2D localFont2D = (Font2D)paramConcurrentHashMap.get(paramString.toLowerCase(Locale.ENGLISH));
/*      */ 
/*  688 */     if ((localFont2D instanceof CompositeFont)) {
/*  689 */       localFont2D.handle.font2D = localCompositeFont;
/*      */     }
/*  691 */     paramConcurrentHashMap.put(paramString.toLowerCase(Locale.ENGLISH), localCompositeFont);
/*      */   }
/*      */ 
/*      */   private void addCompositeToFontList(CompositeFont paramCompositeFont, int paramInt)
/*      */   {
/*  696 */     if (FontUtilities.isLogging()) {
/*  697 */       FontUtilities.getLogger().info("Add to Family " + paramCompositeFont.familyName + ", Font " + paramCompositeFont.fullName + " rank=" + paramInt);
/*      */     }
/*      */ 
/*  700 */     paramCompositeFont.setRank(paramInt);
/*  701 */     this.compositeFonts.put(paramCompositeFont.fullName, paramCompositeFont);
/*  702 */     this.fullNameToFont.put(paramCompositeFont.fullName.toLowerCase(Locale.ENGLISH), paramCompositeFont);
/*      */ 
/*  704 */     FontFamily localFontFamily = FontFamily.getFamily(paramCompositeFont.familyName);
/*  705 */     if (localFontFamily == null) {
/*  706 */       localFontFamily = new FontFamily(paramCompositeFont.familyName, true, paramInt);
/*      */     }
/*  708 */     localFontFamily.setFont(paramCompositeFont, paramCompositeFont.style);
/*      */   }
/*      */ 
/*      */   protected PhysicalFont addToFontList(PhysicalFont paramPhysicalFont, int paramInt)
/*      */   {
/*  746 */     String str1 = paramPhysicalFont.fullName;
/*  747 */     String str2 = paramPhysicalFont.familyName;
/*  748 */     if ((str1 == null) || ("".equals(str1))) {
/*  749 */       return null;
/*      */     }
/*  751 */     if (this.compositeFonts.containsKey(str1))
/*      */     {
/*  753 */       return null;
/*      */     }
/*  755 */     paramPhysicalFont.setRank(paramInt);
/*  756 */     if (!this.physicalFonts.containsKey(str1)) {
/*  757 */       if (FontUtilities.isLogging()) {
/*  758 */         FontUtilities.getLogger().info("Add to Family " + str2 + ", Font " + str1 + " rank=" + paramInt);
/*      */       }
/*      */ 
/*  761 */       this.physicalFonts.put(str1, paramPhysicalFont);
/*  762 */       localObject1 = FontFamily.getFamily(str2);
/*  763 */       if (localObject1 == null) {
/*  764 */         localObject1 = new FontFamily(str2, false, paramInt);
/*  765 */         ((FontFamily)localObject1).setFont(paramPhysicalFont, paramPhysicalFont.style);
/*  766 */       } else if (((FontFamily)localObject1).getRank() >= paramInt) {
/*  767 */         ((FontFamily)localObject1).setFont(paramPhysicalFont, paramPhysicalFont.style);
/*      */       }
/*  769 */       this.fullNameToFont.put(str1.toLowerCase(Locale.ENGLISH), paramPhysicalFont);
/*  770 */       return paramPhysicalFont;
/*      */     }
/*  772 */     Object localObject1 = paramPhysicalFont;
/*  773 */     PhysicalFont localPhysicalFont = (PhysicalFont)this.physicalFonts.get(str1);
/*  774 */     if (localPhysicalFont == null) {
/*  775 */       return null;
/*      */     }
/*      */ 
/*  780 */     if (localPhysicalFont.getRank() >= paramInt)
/*      */     {
/*  800 */       if ((localPhysicalFont.mapper != null) && (paramInt > 2)) {
/*  801 */         return localPhysicalFont;
/*      */       }
/*      */ 
/*  809 */       if (localPhysicalFont.getRank() == paramInt) {
/*  810 */         if (((localPhysicalFont instanceof TrueTypeFont)) && ((localObject1 instanceof TrueTypeFont)))
/*      */         {
/*  812 */           localObject2 = (TrueTypeFont)localPhysicalFont;
/*  813 */           TrueTypeFont localTrueTypeFont = (TrueTypeFont)localObject1;
/*  814 */           if (((TrueTypeFont)localObject2).fileSize >= localTrueTypeFont.fileSize)
/*  815 */             return localPhysicalFont;
/*      */         }
/*      */         else {
/*  818 */           return localPhysicalFont;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  830 */       if (localPhysicalFont.platName.startsWith(jreFontDirName)) {
/*  831 */         if (FontUtilities.isLogging()) {
/*  832 */           FontUtilities.getLogger().warning("Unexpected attempt to replace a JRE  font " + str1 + " from " + localPhysicalFont.platName + " with " + ((PhysicalFont)localObject1).platName);
/*      */         }
/*      */ 
/*  838 */         return localPhysicalFont;
/*      */       }
/*      */ 
/*  841 */       if (FontUtilities.isLogging()) {
/*  842 */         FontUtilities.getLogger().info("Replace in Family " + str2 + ",Font " + str1 + " new rank=" + paramInt + " from " + localPhysicalFont.platName + " with " + ((PhysicalFont)localObject1).platName);
/*      */       }
/*      */ 
/*  848 */       replaceFont(localPhysicalFont, (PhysicalFont)localObject1);
/*  849 */       this.physicalFonts.put(str1, localObject1);
/*  850 */       this.fullNameToFont.put(str1.toLowerCase(Locale.ENGLISH), localObject1);
/*      */ 
/*  853 */       Object localObject2 = FontFamily.getFamily(str2);
/*  854 */       if (localObject2 == null) {
/*  855 */         localObject2 = new FontFamily(str2, false, paramInt);
/*  856 */         ((FontFamily)localObject2).setFont((Font2D)localObject1, ((PhysicalFont)localObject1).style);
/*  857 */       } else if (((FontFamily)localObject2).getRank() >= paramInt) {
/*  858 */         ((FontFamily)localObject2).setFont((Font2D)localObject1, ((PhysicalFont)localObject1).style);
/*      */       }
/*  860 */       return localObject1;
/*      */     }
/*  862 */     return localPhysicalFont;
/*      */   }
/*      */ 
/*      */   public Font2D[] getRegisteredFonts()
/*      */   {
/*  868 */     PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
/*  869 */     int i = this.maxCompFont;
/*  870 */     Font2D[] arrayOfFont2D = new Font2D[arrayOfPhysicalFont.length + i];
/*  871 */     System.arraycopy(this.compFonts, 0, arrayOfFont2D, 0, i);
/*  872 */     System.arraycopy(arrayOfPhysicalFont, 0, arrayOfFont2D, i, arrayOfPhysicalFont.length);
/*  873 */     return arrayOfFont2D;
/*      */   }
/*      */ 
/*      */   protected PhysicalFont[] getPhysicalFonts() {
/*  877 */     return (PhysicalFont[])this.physicalFonts.values().toArray(new PhysicalFont[0]);
/*      */   }
/*      */ 
/*      */   protected synchronized void initialiseDeferredFonts()
/*      */   {
/*  925 */     for (String str : this.deferredFontFiles.keySet())
/*  926 */       initialiseDeferredFont(str);
/*      */   }
/*      */ 
/*      */   protected synchronized void registerDeferredJREFonts(String paramString)
/*      */   {
/*  931 */     for (FontRegistrationInfo localFontRegistrationInfo : this.deferredFontFiles.values())
/*  932 */       if ((localFontRegistrationInfo.fontFilePath != null) && (localFontRegistrationInfo.fontFilePath.startsWith(paramString)))
/*      */       {
/*  934 */         initialiseDeferredFont(localFontRegistrationInfo.fontFilePath);
/*      */       }
/*      */   }
/*      */ 
/*      */   public boolean isDeferredFont(String paramString)
/*      */   {
/*  940 */     return this.deferredFontFiles.containsKey(paramString);
/*      */   }
/*      */ 
/*      */   public PhysicalFont findJREDeferredFont(String paramString, int paramInt)
/*      */   {
/*  956 */     String str1 = paramString.toLowerCase(Locale.ENGLISH) + paramInt;
/*  957 */     String str2 = (String)this.jreFontMap.get(str1);
/*      */     PhysicalFont localPhysicalFont;
/*  958 */     if (str2 != null) {
/*  959 */       str2 = jreFontDirName + File.separator + str2;
/*  960 */       if (this.deferredFontFiles.get(str2) != null) {
/*  961 */         localPhysicalFont = initialiseDeferredFont(str2);
/*  962 */         if ((localPhysicalFont != null) && ((localPhysicalFont.getFontName(null).equalsIgnoreCase(paramString)) || (localPhysicalFont.getFamilyName(null).equalsIgnoreCase(paramString))) && (localPhysicalFont.style == paramInt))
/*      */         {
/*  966 */           return localPhysicalFont;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  980 */     if (this.noOtherJREFontFiles) {
/*  981 */       return null;
/*      */     }
/*  983 */     synchronized (this.jreLucidaFontFiles) {
/*  984 */       if (this.jreOtherFontFiles == null) {
/*  985 */         HashSet localHashSet = new HashSet();
/*  986 */         for (String str3 : this.deferredFontFiles.keySet()) {
/*  987 */           File localFile = new File(str3);
/*  988 */           String str4 = localFile.getParent();
/*  989 */           String str5 = localFile.getName();
/*      */ 
/*  993 */           if ((str4 != null) && (str4.equals(jreFontDirName)) && (!this.jreLucidaFontFiles.contains(str5)))
/*      */           {
/*  998 */             localHashSet.add(str3);
/*      */           }
/*      */         }
/* 1000 */         this.jreOtherFontFiles = ((String[])localHashSet.toArray(STR_ARRAY));
/* 1001 */         if (this.jreOtherFontFiles.length == 0) {
/* 1002 */           this.noOtherJREFontFiles = true;
/*      */         }
/*      */       }
/*      */ 
/* 1006 */       for (int i = 0; i < this.jreOtherFontFiles.length; i++) {
/* 1007 */         str2 = this.jreOtherFontFiles[i];
/* 1008 */         if (str2 != null)
/*      */         {
/* 1011 */           this.jreOtherFontFiles[i] = null;
/* 1012 */           localPhysicalFont = initialiseDeferredFont(str2);
/* 1013 */           if ((localPhysicalFont != null) && ((localPhysicalFont.getFontName(null).equalsIgnoreCase(paramString)) || (localPhysicalFont.getFamilyName(null).equalsIgnoreCase(paramString))) && (localPhysicalFont.style == paramInt))
/*      */           {
/* 1017 */             return localPhysicalFont;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1022 */     return null;
/*      */   }
/*      */ 
/*      */   private PhysicalFont findOtherDeferredFont(String paramString, int paramInt)
/*      */   {
/* 1027 */     for (String str1 : this.deferredFontFiles.keySet()) {
/* 1028 */       File localFile = new File(str1);
/* 1029 */       String str2 = localFile.getParent();
/* 1030 */       String str3 = localFile.getName();
/* 1031 */       if ((str2 == null) || (!str2.equals(jreFontDirName)) || (!this.jreLucidaFontFiles.contains(str3)))
/*      */       {
/* 1036 */         PhysicalFont localPhysicalFont = initialiseDeferredFont(str1);
/* 1037 */         if ((localPhysicalFont != null) && ((localPhysicalFont.getFontName(null).equalsIgnoreCase(paramString)) || (localPhysicalFont.getFamilyName(null).equalsIgnoreCase(paramString))) && (localPhysicalFont.style == paramInt))
/*      */         {
/* 1041 */           return localPhysicalFont;
/*      */         }
/*      */       }
/*      */     }
/* 1044 */     return null;
/*      */   }
/*      */ 
/*      */   private PhysicalFont findDeferredFont(String paramString, int paramInt)
/*      */   {
/* 1049 */     PhysicalFont localPhysicalFont = findJREDeferredFont(paramString, paramInt);
/* 1050 */     if (localPhysicalFont != null) {
/* 1051 */       return localPhysicalFont;
/*      */     }
/* 1053 */     return findOtherDeferredFont(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public void registerDeferredFont(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt1, boolean paramBoolean, int paramInt2)
/*      */   {
/* 1063 */     FontRegistrationInfo localFontRegistrationInfo = new FontRegistrationInfo(paramString2, paramArrayOfString, paramInt1, paramBoolean, paramInt2);
/*      */ 
/* 1066 */     this.deferredFontFiles.put(paramString1, localFontRegistrationInfo);
/*      */   }
/*      */ 
/*      */   public synchronized PhysicalFont initialiseDeferredFont(String paramString)
/*      */   {
/* 1073 */     if (paramString == null) {
/* 1074 */       return null;
/*      */     }
/* 1076 */     if (FontUtilities.isLogging()) {
/* 1077 */       FontUtilities.getLogger().info("Opening deferred font file " + paramString);
/*      */     }
/*      */ 
/* 1082 */     FontRegistrationInfo localFontRegistrationInfo = (FontRegistrationInfo)this.deferredFontFiles.get(paramString);
/*      */     PhysicalFont localPhysicalFont;
/* 1083 */     if (localFontRegistrationInfo != null) {
/* 1084 */       this.deferredFontFiles.remove(paramString);
/* 1085 */       localPhysicalFont = registerFontFile(localFontRegistrationInfo.fontFilePath, localFontRegistrationInfo.nativeNames, localFontRegistrationInfo.fontFormat, localFontRegistrationInfo.javaRasterizer, localFontRegistrationInfo.fontRank);
/*      */ 
/* 1092 */       if (localPhysicalFont != null)
/*      */       {
/* 1096 */         this.initialisedFonts.put(paramString, localPhysicalFont.handle);
/*      */       }
/* 1098 */       else this.initialisedFonts.put(paramString, getDefaultPhysicalFont().handle);
/*      */     }
/*      */     else
/*      */     {
/* 1102 */       Font2DHandle localFont2DHandle = (Font2DHandle)this.initialisedFonts.get(paramString);
/* 1103 */       if (localFont2DHandle == null)
/*      */       {
/* 1105 */         localPhysicalFont = getDefaultPhysicalFont();
/*      */       }
/* 1107 */       else localPhysicalFont = (PhysicalFont)localFont2DHandle.font2D;
/*      */     }
/*      */ 
/* 1110 */     return localPhysicalFont;
/*      */   }
/*      */ 
/*      */   public boolean isRegisteredFontFile(String paramString) {
/* 1114 */     return this.registeredFonts.containsKey(paramString);
/*      */   }
/*      */ 
/*      */   public PhysicalFont getRegisteredFontFile(String paramString) {
/* 1118 */     return (PhysicalFont)this.registeredFonts.get(paramString);
/*      */   }
/*      */ 
/*      */   public PhysicalFont registerFontFile(String paramString, String[] paramArrayOfString, int paramInt1, boolean paramBoolean, int paramInt2)
/*      */   {
/* 1131 */     PhysicalFont localPhysicalFont = (PhysicalFont)this.registeredFonts.get(paramString);
/* 1132 */     if (localPhysicalFont != null) {
/* 1133 */       return localPhysicalFont;
/*      */     }
/*      */ 
/* 1136 */     Object localObject1 = null;
/*      */     try
/*      */     {
/*      */       Object localObject2;
/* 1140 */       switch (paramInt1) {
/*      */       case 0:
/* 1143 */         int i = 0;
/*      */         TrueTypeFont localTrueTypeFont;
/*      */         do {
/* 1146 */           localTrueTypeFont = new TrueTypeFont(paramString, paramArrayOfString, i++, paramBoolean);
/*      */ 
/* 1148 */           localObject2 = addToFontList(localTrueTypeFont, paramInt2);
/* 1149 */           if (localObject1 == null) {
/* 1150 */             localObject1 = localObject2;
/*      */           }
/*      */         }
/* 1153 */         while (i < localTrueTypeFont.getFontCount());
/* 1154 */         break;
/*      */       case 1:
/* 1157 */         localObject2 = new Type1Font(paramString, paramArrayOfString);
/* 1158 */         localObject1 = addToFontList((PhysicalFont)localObject2, paramInt2);
/* 1159 */         break;
/*      */       case 5:
/* 1162 */         NativeFont localNativeFont = new NativeFont(paramString, false);
/* 1163 */         localObject1 = addToFontList(localNativeFont, paramInt2);
/*      */       }
/*      */ 
/* 1167 */       if (FontUtilities.isLogging()) {
/* 1168 */         FontUtilities.getLogger().info("Registered file " + paramString + " as font " + localObject1 + " rank=" + paramInt2);
/*      */       }
/*      */     }
/*      */     catch (FontFormatException localFontFormatException)
/*      */     {
/* 1173 */       if (FontUtilities.isLogging()) {
/* 1174 */         FontUtilities.getLogger().warning("Unusable font: " + paramString + " " + localFontFormatException.toString());
/*      */       }
/*      */     }
/*      */ 
/* 1178 */     if ((localObject1 != null) && (paramInt1 != 5))
/*      */     {
/* 1180 */       this.registeredFonts.put(paramString, localObject1);
/*      */     }
/* 1182 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public void registerFonts(String[] paramArrayOfString, String[][] paramArrayOfString1, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
/*      */   {
/* 1192 */     for (int i = 0; i < paramInt1; i++)
/* 1193 */       if (paramBoolean2) {
/* 1194 */         registerDeferredFont(paramArrayOfString[i], paramArrayOfString[i], paramArrayOfString1[i], paramInt2, paramBoolean1, paramInt3);
/*      */       }
/*      */       else
/* 1197 */         registerFontFile(paramArrayOfString[i], paramArrayOfString1[i], paramInt2, paramBoolean1, paramInt3);
/*      */   }
/*      */ 
/*      */   public PhysicalFont getDefaultPhysicalFont()
/*      */   {
/* 1210 */     if (this.defaultPhysicalFont == null)
/*      */     {
/* 1218 */       this.defaultPhysicalFont = ((PhysicalFont)findFont2D("Lucida Sans Regular", 0, 0));
/*      */ 
/* 1220 */       if (this.defaultPhysicalFont == null) {
/* 1221 */         this.defaultPhysicalFont = ((PhysicalFont)findFont2D("Arial", 0, 0));
/*      */       }
/*      */ 
/* 1224 */       if (this.defaultPhysicalFont == null)
/*      */       {
/* 1231 */         Iterator localIterator = this.physicalFonts.values().iterator();
/* 1232 */         if (localIterator.hasNext())
/* 1233 */           this.defaultPhysicalFont = ((PhysicalFont)localIterator.next());
/*      */         else {
/* 1235 */           throw new Error("Probable fatal error:No fonts found.");
/*      */         }
/*      */       }
/*      */     }
/* 1239 */     return this.defaultPhysicalFont;
/*      */   }
/*      */ 
/*      */   public Font2D getDefaultLogicalFont(int paramInt) {
/* 1243 */     return findFont2D("dialog", paramInt, 0);
/*      */   }
/*      */ 
/*      */   private static String dotStyleStr(int paramInt)
/*      */   {
/* 1251 */     switch (paramInt) {
/*      */     case 1:
/* 1253 */       return ".bold";
/*      */     case 2:
/* 1255 */       return ".italic";
/*      */     case 3:
/* 1257 */       return ".bolditalic";
/*      */     }
/* 1259 */     return ".plain";
/*      */   }
/*      */ 
/*      */   protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale)
/*      */   {
/*      */   }
/*      */ 
/*      */   private String[] getFontFilesFromPath(boolean paramBoolean)
/*      */   {
/*      */     Object localObject;
/* 1302 */     if (paramBoolean)
/* 1303 */       localObject = ttFilter;
/*      */     else {
/* 1305 */       localObject = new TTorT1Filter(null);
/*      */     }
/* 1307 */     return (String[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/* 1309 */         if (SunFontManager.this.pathDirs.length == 1) {
/* 1310 */           localObject = new File(SunFontManager.this.pathDirs[0]);
/* 1311 */           String[] arrayOfString1 = ((File)localObject).list(this.val$filter);
/* 1312 */           if (arrayOfString1 == null) {
/* 1313 */             return new String[0];
/*      */           }
/* 1315 */           for (int j = 0; j < arrayOfString1.length; j++) {
/* 1316 */             arrayOfString1[j] = arrayOfString1[j].toLowerCase();
/*      */           }
/* 1318 */           return arrayOfString1;
/*      */         }
/* 1320 */         Object localObject = new ArrayList();
/* 1321 */         for (int i = 0; i < SunFontManager.this.pathDirs.length; i++) {
/* 1322 */           File localFile = new File(SunFontManager.this.pathDirs[i]);
/* 1323 */           String[] arrayOfString2 = localFile.list(this.val$filter);
/* 1324 */           if (arrayOfString2 != null)
/*      */           {
/* 1327 */             for (int k = 0; k < arrayOfString2.length; k++)
/* 1328 */               ((ArrayList)localObject).add(arrayOfString2[k].toLowerCase());
/*      */           }
/*      */         }
/* 1331 */         return ((ArrayList)localObject).toArray(SunFontManager.STR_ARRAY);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void resolveWindowsFonts()
/*      */   {
/* 1359 */     ArrayList localArrayList = null;
/* 1360 */     for (Object localObject1 = this.fontToFamilyNameMap.keySet().iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (String)((Iterator)localObject1).next();
/* 1361 */       localObject3 = (String)this.fontToFileMap.get(localObject2);
/* 1362 */       if (localObject3 == null)
/* 1363 */         if (((String)localObject2).indexOf("  ") > 0) {
/* 1364 */           localObject4 = ((String)localObject2).replaceFirst("  ", " ");
/* 1365 */           localObject3 = (String)this.fontToFileMap.get(localObject4);
/*      */ 
/* 1369 */           if ((localObject3 != null) && (!this.fontToFamilyNameMap.containsKey(localObject4)))
/*      */           {
/* 1371 */             this.fontToFileMap.remove(localObject4);
/* 1372 */             this.fontToFileMap.put(localObject2, localObject3);
/*      */           }
/* 1374 */         } else if (((String)localObject2).equals("marlett")) {
/* 1375 */           this.fontToFileMap.put(localObject2, "marlett.ttf");
/* 1376 */         } else if (((String)localObject2).equals("david")) {
/* 1377 */           localObject3 = (String)this.fontToFileMap.get("david regular");
/* 1378 */           if (localObject3 != null) {
/* 1379 */             this.fontToFileMap.remove("david regular");
/* 1380 */             this.fontToFileMap.put("david", localObject3);
/*      */           }
/*      */         } else {
/* 1383 */           if (localArrayList == null) {
/* 1384 */             localArrayList = new ArrayList();
/*      */           }
/* 1386 */           localArrayList.add(localObject2);
/*      */         }
/*      */     }
/*      */     Object localObject2;
/*      */     Object localObject3;
/*      */     Object localObject4;
/* 1391 */     if (localArrayList != null) {
/* 1392 */       localObject1 = new HashSet();
/*      */ 
/* 1423 */       localObject2 = (HashMap)this.fontToFileMap.clone();
/*      */ 
/* 1425 */       for (localObject3 = this.fontToFamilyNameMap.keySet().iterator(); ((Iterator)localObject3).hasNext(); ) { localObject4 = (String)((Iterator)localObject3).next();
/* 1426 */         ((HashMap)localObject2).remove(localObject4);
/*      */       }
/* 1428 */       for (localObject3 = ((HashMap)localObject2).keySet().iterator(); ((Iterator)localObject3).hasNext(); ) { localObject4 = (String)((Iterator)localObject3).next();
/* 1429 */         ((HashSet)localObject1).add(((HashMap)localObject2).get(localObject4));
/* 1430 */         this.fontToFileMap.remove(localObject4);
/*      */       }
/*      */ 
/* 1433 */       resolveFontFiles((HashSet)localObject1, localArrayList);
/*      */       Object localObject5;
/* 1440 */       if (localArrayList.size() > 0)
/*      */       {
/* 1446 */         localObject3 = new ArrayList();
/*      */ 
/* 1448 */         for (localObject4 = this.fontToFileMap.values().iterator(); ((Iterator)localObject4).hasNext(); ) { String str1 = (String)((Iterator)localObject4).next();
/* 1449 */           ((ArrayList)localObject3).add(str1.toLowerCase());
/*      */         }
/*      */ 
/* 1456 */         for (localObject5 : getFontFilesFromPath(true)) {
/* 1457 */           if (!((ArrayList)localObject3).contains(localObject5)) {
/* 1458 */             ((HashSet)localObject1).add(localObject5);
/*      */           }
/*      */         }
/*      */ 
/* 1462 */         resolveFontFiles((HashSet)localObject1, localArrayList);
/*      */       }
/*      */ 
/* 1468 */       if (localArrayList.size() > 0) {
/* 1469 */         int i = localArrayList.size();
/* 1470 */         for (int j = 0; j < i; j++) {
/* 1471 */           String str2 = (String)localArrayList.get(j);
/* 1472 */           String str3 = (String)this.fontToFamilyNameMap.get(str2);
/* 1473 */           if (str3 != null) {
/* 1474 */             localObject5 = (ArrayList)this.familyToFontListMap.get(str3);
/* 1475 */             if ((localObject5 != null) && 
/* 1476 */               (((ArrayList)localObject5).size() <= 1)) {
/* 1477 */               this.familyToFontListMap.remove(str3);
/*      */             }
/*      */           }
/*      */ 
/* 1481 */           this.fontToFamilyNameMap.remove(str2);
/* 1482 */           if (FontUtilities.isLogging())
/* 1483 */             FontUtilities.getLogger().info("No file for font:" + str2);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void checkForUnreferencedFontFiles()
/*      */   {
/* 1504 */     if (this.haveCheckedUnreferencedFontFiles) {
/* 1505 */       return;
/*      */     }
/* 1507 */     this.haveCheckedUnreferencedFontFiles = true;
/* 1508 */     if (!FontUtilities.isWindows) {
/* 1509 */       return;
/*      */     }
/*      */ 
/* 1515 */     ArrayList localArrayList1 = new ArrayList();
/* 1516 */     for (Object localObject1 = this.fontToFileMap.values().iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (String)((Iterator)localObject1).next();
/* 1517 */       localArrayList1.add(((String)localObject2).toLowerCase());
/*      */     }
/*      */ 
/* 1527 */     localObject1 = null;
/* 1528 */     Object localObject2 = null;
/* 1529 */     HashMap localHashMap = null;
/*      */ 
/* 1531 */     for (String str1 : getFontFilesFromPath(false))
/* 1532 */       if (!localArrayList1.contains(str1)) {
/* 1533 */         if (FontUtilities.isLogging()) {
/* 1534 */           FontUtilities.getLogger().info("Found non-registry file : " + str1);
/*      */         }
/*      */ 
/* 1537 */         PhysicalFont localPhysicalFont = registerFontFile(getPathName(str1));
/* 1538 */         if (localPhysicalFont != null)
/*      */         {
/* 1541 */           if (localObject1 == null) {
/* 1542 */             localObject1 = new HashMap(this.fontToFileMap);
/* 1543 */             localObject2 = new HashMap(this.fontToFamilyNameMap);
/*      */ 
/* 1545 */             localHashMap = new HashMap(this.familyToFontListMap);
/*      */           }
/*      */ 
/* 1548 */           String str2 = localPhysicalFont.getFontName(null);
/* 1549 */           String str3 = localPhysicalFont.getFamilyName(null);
/* 1550 */           String str4 = str3.toLowerCase();
/* 1551 */           ((HashMap)localObject2).put(str2, str3);
/* 1552 */           ((HashMap)localObject1).put(str2, str1);
/* 1553 */           ArrayList localArrayList2 = (ArrayList)localHashMap.get(str4);
/* 1554 */           if (localArrayList2 == null)
/* 1555 */             localArrayList2 = new ArrayList();
/*      */           else {
/* 1557 */             localArrayList2 = new ArrayList(localArrayList2);
/*      */           }
/* 1559 */           localArrayList2.add(str2);
/* 1560 */           localHashMap.put(str4, localArrayList2);
/*      */         }
/*      */       }
/* 1563 */     if (localObject1 != null) {
/* 1564 */       this.fontToFileMap = ((HashMap)localObject1);
/* 1565 */       this.familyToFontListMap = localHashMap;
/* 1566 */       this.fontToFamilyNameMap = ((HashMap)localObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void resolveFontFiles(HashSet<String> paramHashSet, ArrayList<String> paramArrayList)
/*      */   {
/* 1573 */     Locale localLocale = SunToolkit.getStartupLocale();
/*      */ 
/* 1575 */     for (String str1 : paramHashSet)
/*      */       try {
/* 1577 */         int i = 0;
/*      */ 
/* 1579 */         String str2 = getPathName(str1);
/* 1580 */         if (FontUtilities.isLogging())
/* 1581 */           FontUtilities.getLogger().info("Trying to resolve file " + str2);
/*      */         TrueTypeFont localTrueTypeFont;
/*      */         do
/*      */         {
/* 1585 */           localTrueTypeFont = new TrueTypeFont(str2, null, i++, false);
/*      */ 
/* 1587 */           String str3 = localTrueTypeFont.getFontName(localLocale).toLowerCase();
/* 1588 */           if (paramArrayList.contains(str3)) {
/* 1589 */             this.fontToFileMap.put(str3, str1);
/* 1590 */             paramArrayList.remove(str3);
/* 1591 */             if (FontUtilities.isLogging()) {
/* 1592 */               FontUtilities.getLogger().info("Resolved absent registry entry for " + str3 + " located in " + str2);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1598 */         while (i < localTrueTypeFont.getFontCount());
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public HashMap<String, FamilyDescription> populateHardcodedFileNameMap()
/*      */   {
/* 1635 */     return new HashMap(0);
/*      */   }
/*      */ 
/*      */   Font2D findFontFromPlatformMap(String paramString, int paramInt) {
/* 1639 */     if (platformFontMap == null) {
/* 1640 */       platformFontMap = populateHardcodedFileNameMap();
/*      */     }
/*      */ 
/* 1643 */     if ((platformFontMap == null) || (platformFontMap.size() == 0)) {
/* 1644 */       return null;
/*      */     }
/*      */ 
/* 1647 */     int i = paramString.indexOf(' ');
/* 1648 */     String str1 = paramString;
/* 1649 */     if (i > 0) {
/* 1650 */       str1 = paramString.substring(0, i);
/*      */     }
/*      */ 
/* 1653 */     FamilyDescription localFamilyDescription = (FamilyDescription)platformFontMap.get(str1);
/* 1654 */     if (localFamilyDescription == null) {
/* 1655 */       return null;
/*      */     }
/*      */ 
/* 1663 */     int j = -1;
/* 1664 */     if (paramString.equalsIgnoreCase(localFamilyDescription.plainFullName))
/* 1665 */       j = 0;
/* 1666 */     else if (paramString.equalsIgnoreCase(localFamilyDescription.boldFullName))
/* 1667 */       j = 1;
/* 1668 */     else if (paramString.equalsIgnoreCase(localFamilyDescription.italicFullName))
/* 1669 */       j = 2;
/* 1670 */     else if (paramString.equalsIgnoreCase(localFamilyDescription.boldItalicFullName)) {
/* 1671 */       j = 3;
/*      */     }
/* 1673 */     if ((j == -1) && (!paramString.equalsIgnoreCase(localFamilyDescription.familyName))) {
/* 1674 */       return null;
/*      */     }
/*      */ 
/* 1677 */     String str2 = null; String str3 = null;
/* 1678 */     String str4 = null; String str5 = null;
/*      */ 
/* 1680 */     boolean bool = false;
/*      */ 
/* 1687 */     getPlatformFontDirs(noType1Font);
/*      */ 
/* 1689 */     if (localFamilyDescription.plainFileName != null) {
/* 1690 */       str2 = getPathName(localFamilyDescription.plainFileName);
/* 1691 */       if (str2 == null) {
/* 1692 */         bool = true;
/*      */       }
/*      */     }
/*      */ 
/* 1696 */     if (localFamilyDescription.boldFileName != null) {
/* 1697 */       str3 = getPathName(localFamilyDescription.boldFileName);
/* 1698 */       if (str3 == null) {
/* 1699 */         bool = true;
/*      */       }
/*      */     }
/*      */ 
/* 1703 */     if (localFamilyDescription.italicFileName != null) {
/* 1704 */       str4 = getPathName(localFamilyDescription.italicFileName);
/* 1705 */       if (str4 == null) {
/* 1706 */         bool = true;
/*      */       }
/*      */     }
/*      */ 
/* 1710 */     if (localFamilyDescription.boldItalicFileName != null) {
/* 1711 */       str5 = getPathName(localFamilyDescription.boldItalicFileName);
/* 1712 */       if (str5 == null) {
/* 1713 */         bool = true;
/*      */       }
/*      */     }
/*      */ 
/* 1717 */     if (bool) {
/* 1718 */       if (FontUtilities.isLogging()) {
/* 1719 */         FontUtilities.getLogger().info("Hardcoded file missing looking for " + paramString);
/*      */       }
/*      */ 
/* 1722 */       platformFontMap.remove(str1);
/* 1723 */       return null;
/*      */     }
/*      */ 
/* 1727 */     final String[] arrayOfString = { str2, str3, str4, str5 };
/*      */ 
/* 1730 */     bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/* 1733 */         for (int i = 0; i < arrayOfString.length; i++) {
/* 1734 */           if (arrayOfString[i] != null)
/*      */           {
/* 1737 */             File localFile = new File(arrayOfString[i]);
/* 1738 */             if (!localFile.exists())
/* 1739 */               return Boolean.TRUE;
/*      */           }
/*      */         }
/* 1742 */         return Boolean.FALSE;
/*      */       }
/*      */     })).booleanValue();
/*      */ 
/* 1746 */     if (bool) {
/* 1747 */       if (FontUtilities.isLogging()) {
/* 1748 */         FontUtilities.getLogger().info("Hardcoded file missing looking for " + paramString);
/*      */       }
/*      */ 
/* 1751 */       platformFontMap.remove(str1);
/* 1752 */       return null;
/*      */     }
/*      */ 
/* 1761 */     Object localObject = null;
/* 1762 */     for (int k = 0; k < arrayOfString.length; k++) {
/* 1763 */       if (arrayOfString[k] != null)
/*      */       {
/* 1766 */         PhysicalFont localPhysicalFont = registerFontFile(arrayOfString[k], null, 0, false, 3);
/*      */ 
/* 1769 */         if (k == j) {
/* 1770 */           localObject = localPhysicalFont;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1787 */     FontFamily localFontFamily = FontFamily.getFamily(localFamilyDescription.familyName);
/* 1788 */     if (localFontFamily != null) {
/* 1789 */       if (localObject == null) {
/* 1790 */         localObject = localFontFamily.getFont(paramInt);
/* 1791 */         if (localObject == null)
/* 1792 */           localObject = localFontFamily.getClosestStyle(paramInt);
/*      */       }
/* 1794 */       else if ((paramInt > 0) && (paramInt != ((Font2D)localObject).style)) {
/* 1795 */         paramInt |= ((Font2D)localObject).style;
/* 1796 */         localObject = localFontFamily.getFont(paramInt);
/* 1797 */         if (localObject == null) {
/* 1798 */           localObject = localFontFamily.getClosestStyle(paramInt);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1803 */     return localObject;
/*      */   }
/*      */   private synchronized HashMap<String, String> getFullNameToFileMap() {
/* 1806 */     if (this.fontToFileMap == null)
/*      */     {
/* 1808 */       this.pathDirs = getPlatformFontDirs(noType1Font);
/*      */ 
/* 1810 */       this.fontToFileMap = new HashMap(100);
/* 1811 */       this.fontToFamilyNameMap = new HashMap(100);
/* 1812 */       this.familyToFontListMap = new HashMap(50);
/* 1813 */       populateFontFileNameMap(this.fontToFileMap, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
/*      */ 
/* 1817 */       if (FontUtilities.isWindows) {
/* 1818 */         resolveWindowsFonts();
/*      */       }
/* 1820 */       if (FontUtilities.isLogging()) {
/* 1821 */         logPlatformFontInfo();
/*      */       }
/*      */     }
/* 1824 */     return this.fontToFileMap;
/*      */   }
/*      */ 
/*      */   private void logPlatformFontInfo() {
/* 1828 */     PlatformLogger localPlatformLogger = FontUtilities.getLogger();
/* 1829 */     for (int i = 0; i < this.pathDirs.length; i++) {
/* 1830 */       localPlatformLogger.info("fontdir=" + this.pathDirs[i]);
/*      */     }
/* 1832 */     for (Iterator localIterator = this.fontToFileMap.keySet().iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/* 1833 */       localPlatformLogger.info("font=" + str + " file=" + (String)this.fontToFileMap.get(str));
/*      */     }
/* 1835 */     String str;
/* 1835 */     for (localIterator = this.fontToFamilyNameMap.keySet().iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/* 1836 */       localPlatformLogger.info("font=" + str + " family=" + (String)this.fontToFamilyNameMap.get(str));
/*      */     }
/*      */ 
/* 1839 */     for (localIterator = this.familyToFontListMap.keySet().iterator(); localIterator.hasNext(); ) { str = (String)localIterator.next();
/* 1840 */       localPlatformLogger.info("family=" + str + " fonts=" + this.familyToFontListMap.get(str));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String[] getFontNamesFromPlatform()
/*      */   {
/* 1847 */     if (getFullNameToFileMap().size() == 0) {
/* 1848 */       return null;
/*      */     }
/* 1850 */     checkForUnreferencedFontFiles();
/*      */ 
/* 1853 */     ArrayList localArrayList1 = new ArrayList();
/* 1854 */     for (ArrayList localArrayList2 : this.familyToFontListMap.values()) {
/* 1855 */       for (String str : localArrayList2) {
/* 1856 */         localArrayList1.add(str);
/*      */       }
/*      */     }
/* 1859 */     return (String[])localArrayList1.toArray(STR_ARRAY);
/*      */   }
/*      */ 
/*      */   public boolean gotFontsFromPlatform() {
/* 1863 */     return getFullNameToFileMap().size() != 0;
/*      */   }
/*      */ 
/*      */   public String getFileNameForFontName(String paramString) {
/* 1867 */     String str = paramString.toLowerCase(Locale.ENGLISH);
/* 1868 */     return (String)this.fontToFileMap.get(str);
/*      */   }
/*      */ 
/*      */   private PhysicalFont registerFontFile(String paramString) {
/* 1872 */     if ((new File(paramString).isAbsolute()) && (!this.registeredFonts.contains(paramString)))
/*      */     {
/* 1874 */       int i = -1;
/* 1875 */       int j = 6;
/* 1876 */       if (ttFilter.accept(null, paramString)) {
/* 1877 */         i = 0;
/* 1878 */         j = 3;
/* 1879 */       } else if (t1Filter.accept(null, paramString))
/*      */       {
/* 1881 */         i = 1;
/* 1882 */         j = 4;
/*      */       }
/* 1884 */       if (i == -1) {
/* 1885 */         return null;
/*      */       }
/* 1887 */       return registerFontFile(paramString, null, i, false, j);
/*      */     }
/* 1889 */     return null;
/*      */   }
/*      */ 
/*      */   protected void registerOtherFontFiles(HashSet paramHashSet)
/*      */   {
/* 1901 */     if (getFullNameToFileMap().size() == 0) {
/* 1902 */       return;
/*      */     }
/* 1904 */     for (String str : this.fontToFileMap.values())
/* 1905 */       registerFontFile(str);
/*      */   }
/*      */ 
/*      */   public boolean getFamilyNamesFromPlatform(TreeMap<String, String> paramTreeMap, Locale paramLocale)
/*      */   {
/* 1912 */     if (getFullNameToFileMap().size() == 0) {
/* 1913 */       return false;
/*      */     }
/* 1915 */     checkForUnreferencedFontFiles();
/* 1916 */     for (String str : this.fontToFamilyNameMap.values()) {
/* 1917 */       paramTreeMap.put(str.toLowerCase(paramLocale), str);
/*      */     }
/* 1919 */     return true;
/*      */   }
/*      */ 
/*      */   private String getPathName(final String paramString)
/*      */   {
/* 1926 */     File localFile = new File(paramString);
/* 1927 */     if (localFile.isAbsolute())
/* 1928 */       return paramString;
/* 1929 */     if (this.pathDirs.length == 1) {
/* 1930 */       return this.pathDirs[0] + File.separator + paramString;
/*      */     }
/* 1932 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public String run() {
/* 1935 */         for (int i = 0; i < SunFontManager.this.pathDirs.length; i++) {
/* 1936 */           File localFile = new File(SunFontManager.this.pathDirs[i] + File.separator + paramString);
/* 1937 */           if (localFile.exists()) {
/* 1938 */             return localFile.getAbsolutePath();
/*      */           }
/*      */         }
/* 1941 */         return null;
/*      */       }
/*      */     });
/* 1944 */     if (str != null) {
/* 1945 */       return str;
/*      */     }
/*      */ 
/* 1948 */     return paramString;
/*      */   }
/*      */ 
/*      */   private Font2D findFontFromPlatform(String paramString, int paramInt)
/*      */   {
/* 1972 */     if (getFullNameToFileMap().size() == 0) {
/* 1973 */       return null;
/*      */     }
/*      */ 
/* 1976 */     ArrayList localArrayList = null;
/* 1977 */     String str1 = null;
/* 1978 */     String str2 = (String)this.fontToFamilyNameMap.get(paramString);
/* 1979 */     if (str2 != null) {
/* 1980 */       str1 = (String)this.fontToFileMap.get(paramString);
/* 1981 */       localArrayList = (ArrayList)this.familyToFontListMap.get(str2.toLowerCase(Locale.ENGLISH));
/*      */     }
/*      */     else {
/* 1984 */       localArrayList = (ArrayList)this.familyToFontListMap.get(paramString);
/* 1985 */       if ((localArrayList != null) && (localArrayList.size() > 0)) {
/* 1986 */         localObject1 = ((String)localArrayList.get(0)).toLowerCase(Locale.ENGLISH);
/* 1987 */         if (localObject1 != null) {
/* 1988 */           str2 = (String)this.fontToFamilyNameMap.get(localObject1);
/*      */         }
/*      */       }
/*      */     }
/* 1992 */     if ((localArrayList == null) || (str2 == null)) {
/* 1993 */       return null;
/*      */     }
/* 1995 */     Object localObject1 = (String[])localArrayList.toArray(STR_ARRAY);
/* 1996 */     if (localObject1.length == 0) {
/* 1997 */       return null;
/*      */     }
/*      */ 
/* 2011 */     for (int i = 0; i < localObject1.length; i++) {
/* 2012 */       String str3 = localObject1[i].toLowerCase(Locale.ENGLISH);
/* 2013 */       localObject2 = (String)this.fontToFileMap.get(str3);
/* 2014 */       if (localObject2 == null) {
/* 2015 */         if (FontUtilities.isLogging()) {
/* 2016 */           FontUtilities.getLogger().info("Platform lookup : No file for font " + localObject1[i] + " in family " + str2);
/*      */         }
/*      */ 
/* 2020 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2027 */     PhysicalFont localPhysicalFont = null;
/* 2028 */     if (str1 != null) {
/* 2029 */       localPhysicalFont = registerFontFile(getPathName(str1), null, 0, false, 3);
/*      */     }
/*      */ 
/* 2034 */     for (int j = 0; j < localObject1.length; j++) {
/* 2035 */       localObject2 = localObject1[j].toLowerCase(Locale.ENGLISH);
/* 2036 */       String str4 = (String)this.fontToFileMap.get(localObject2);
/* 2037 */       if ((str1 == null) || (!str1.equals(str4)))
/*      */       {
/* 2043 */         registerFontFile(getPathName(str4), null, 0, false, 3);
/*      */       }
/*      */     }
/*      */ 
/* 2047 */     Font2D localFont2D = null;
/* 2048 */     Object localObject2 = FontFamily.getFamily(str2);
/*      */ 
/* 2050 */     if (localPhysicalFont != null) {
/* 2051 */       paramInt |= localPhysicalFont.style;
/*      */     }
/* 2053 */     if (localObject2 != null) {
/* 2054 */       localFont2D = ((FontFamily)localObject2).getFont(paramInt);
/* 2055 */       if (localFont2D == null) {
/* 2056 */         localFont2D = ((FontFamily)localObject2).getClosestStyle(paramInt);
/*      */       }
/*      */     }
/* 2059 */     return localFont2D;
/*      */   }
/*      */ 
/*      */   public Font2D findFont2D(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2073 */     String str1 = paramString.toLowerCase(Locale.ENGLISH);
/* 2074 */     String str2 = str1 + dotStyleStr(paramInt1);
/*      */ 
/* 2083 */     if (this._usingPerAppContextComposites) {
/* 2084 */       localObject2 = (ConcurrentHashMap)AppContext.getAppContext().get(CompositeFont.class);
/*      */ 
/* 2087 */       if (localObject2 != null)
/* 2088 */         localObject1 = (Font2D)((ConcurrentHashMap)localObject2).get(str2);
/*      */       else
/* 2090 */         localObject1 = null;
/*      */     }
/*      */     else {
/* 2093 */       localObject1 = (Font2D)this.fontNameCache.get(str2);
/*      */     }
/* 2095 */     if (localObject1 != null) {
/* 2096 */       return localObject1;
/*      */     }
/*      */ 
/* 2099 */     if (FontUtilities.isLogging()) {
/* 2100 */       FontUtilities.getLogger().info("Search for font: " + paramString);
/*      */     }
/*      */ 
/* 2109 */     if (FontUtilities.isWindows) {
/* 2110 */       if (str1.equals("ms sans serif"))
/* 2111 */         paramString = "sansserif";
/* 2112 */       else if (str1.equals("ms serif")) {
/* 2113 */         paramString = "serif";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2125 */     if (str1.equals("default")) {
/* 2126 */       paramString = "dialog";
/*      */     }
/*      */ 
/* 2130 */     Object localObject2 = FontFamily.getFamily(paramString);
/* 2131 */     if (localObject2 != null) {
/* 2132 */       localObject1 = ((FontFamily)localObject2).getFontWithExactStyleMatch(paramInt1);
/* 2133 */       if (localObject1 == null) {
/* 2134 */         localObject1 = findDeferredFont(paramString, paramInt1);
/*      */       }
/* 2136 */       if (localObject1 == null) {
/* 2137 */         localObject1 = ((FontFamily)localObject2).getFont(paramInt1);
/*      */       }
/* 2139 */       if (localObject1 == null) {
/* 2140 */         localObject1 = ((FontFamily)localObject2).getClosestStyle(paramInt1);
/*      */       }
/* 2142 */       if (localObject1 != null) {
/* 2143 */         this.fontNameCache.put(str2, localObject1);
/* 2144 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2151 */     Object localObject1 = (Font2D)this.fullNameToFont.get(str1);
/*      */     Object localObject3;
/* 2152 */     if (localObject1 != null)
/*      */     {
/* 2165 */       if ((((Font2D)localObject1).style == paramInt1) || (paramInt1 == 0)) {
/* 2166 */         this.fontNameCache.put(str2, localObject1);
/* 2167 */         return localObject1;
/*      */       }
/*      */ 
/* 2175 */       localObject2 = FontFamily.getFamily(((Font2D)localObject1).getFamilyName(null));
/* 2176 */       if (localObject2 != null) {
/* 2177 */         localObject3 = ((FontFamily)localObject2).getFont(paramInt1 | ((Font2D)localObject1).style);
/*      */ 
/* 2179 */         if (localObject3 != null) {
/* 2180 */           this.fontNameCache.put(str2, localObject3);
/* 2181 */           return localObject3;
/*      */         }
/*      */ 
/* 2188 */         localObject3 = ((FontFamily)localObject2).getClosestStyle(paramInt1 | ((Font2D)localObject1).style);
/* 2189 */         if (localObject3 != null)
/*      */         {
/* 2197 */           if (((Font2D)localObject3).canDoStyle(paramInt1 | ((Font2D)localObject1).style)) {
/* 2198 */             this.fontNameCache.put(str2, localObject3);
/* 2199 */             return localObject3;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2207 */     if (FontUtilities.isWindows)
/*      */     {
/* 2209 */       localObject1 = findFontFromPlatformMap(str1, paramInt1);
/* 2210 */       if (FontUtilities.isLogging()) {
/* 2211 */         FontUtilities.getLogger().info("findFontFromPlatformMap returned " + localObject1);
/*      */       }
/*      */ 
/* 2214 */       if (localObject1 != null) {
/* 2215 */         this.fontNameCache.put(str2, localObject1);
/* 2216 */         return localObject1;
/*      */       }
/*      */ 
/* 2222 */       if (this.deferredFontFiles.size() > 0) {
/* 2223 */         localObject1 = findJREDeferredFont(str1, paramInt1);
/* 2224 */         if (localObject1 != null) {
/* 2225 */           this.fontNameCache.put(str2, localObject1);
/* 2226 */           return localObject1;
/*      */         }
/*      */       }
/* 2229 */       localObject1 = findFontFromPlatform(str1, paramInt1);
/* 2230 */       if (localObject1 != null) {
/* 2231 */         if (FontUtilities.isLogging()) {
/* 2232 */           FontUtilities.getLogger().info("Found font via platform API for request:\"" + paramString + "\":, style=" + paramInt1 + " found font: " + localObject1);
/*      */         }
/*      */ 
/* 2237 */         this.fontNameCache.put(str2, localObject1);
/* 2238 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2254 */     if (this.deferredFontFiles.size() > 0) {
/* 2255 */       localObject1 = findDeferredFont(paramString, paramInt1);
/* 2256 */       if (localObject1 != null) {
/* 2257 */         this.fontNameCache.put(str2, localObject1);
/* 2258 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2275 */     if ((FontUtilities.isSolaris) && (!this.loaded1dot0Fonts))
/*      */     {
/* 2279 */       if (str1.equals("timesroman")) {
/* 2280 */         localObject1 = findFont2D("serif", paramInt1, paramInt2);
/* 2281 */         this.fontNameCache.put(str2, localObject1);
/*      */       }
/* 2283 */       register1dot0Fonts();
/* 2284 */       this.loaded1dot0Fonts = true;
/* 2285 */       localObject3 = findFont2D(paramString, paramInt1, paramInt2);
/* 2286 */       return localObject3;
/*      */     }
/*      */ 
/* 2299 */     if ((this.fontsAreRegistered) || (this.fontsAreRegisteredPerAppContext)) {
/* 2300 */       localObject3 = null;
/*      */       Hashtable localHashtable;
/* 2303 */       if (this.fontsAreRegistered) {
/* 2304 */         localObject3 = this.createdByFamilyName;
/* 2305 */         localHashtable = this.createdByFullName;
/*      */       } else {
/* 2307 */         AppContext localAppContext = AppContext.getAppContext();
/* 2308 */         localObject3 = (Hashtable)localAppContext.get(regFamilyKey);
/*      */ 
/* 2310 */         localHashtable = (Hashtable)localAppContext.get(regFullNameKey);
/*      */       }
/*      */ 
/* 2314 */       localObject2 = (FontFamily)((Hashtable)localObject3).get(str1);
/* 2315 */       if (localObject2 != null) {
/* 2316 */         localObject1 = ((FontFamily)localObject2).getFontWithExactStyleMatch(paramInt1);
/* 2317 */         if (localObject1 == null) {
/* 2318 */           localObject1 = ((FontFamily)localObject2).getFont(paramInt1);
/*      */         }
/* 2320 */         if (localObject1 == null) {
/* 2321 */           localObject1 = ((FontFamily)localObject2).getClosestStyle(paramInt1);
/*      */         }
/* 2323 */         if (localObject1 != null) {
/* 2324 */           if (this.fontsAreRegistered) {
/* 2325 */             this.fontNameCache.put(str2, localObject1);
/*      */           }
/* 2327 */           return localObject1;
/*      */         }
/*      */       }
/* 2330 */       localObject1 = (Font2D)localHashtable.get(str1);
/* 2331 */       if (localObject1 != null) {
/* 2332 */         if (this.fontsAreRegistered) {
/* 2333 */           this.fontNameCache.put(str2, localObject1);
/*      */         }
/* 2335 */         return localObject1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2342 */     if (!this.loadedAllFonts) {
/* 2343 */       if (FontUtilities.isLogging()) {
/* 2344 */         FontUtilities.getLogger().info("Load fonts looking for:" + paramString);
/*      */       }
/*      */ 
/* 2347 */       loadFonts();
/* 2348 */       this.loadedAllFonts = true;
/* 2349 */       return findFont2D(paramString, paramInt1, paramInt2);
/*      */     }
/*      */ 
/* 2352 */     if (!this.loadedAllFontFiles) {
/* 2353 */       if (FontUtilities.isLogging()) {
/* 2354 */         FontUtilities.getLogger().info("Load font files looking for:" + paramString);
/*      */       }
/*      */ 
/* 2357 */       loadFontFiles();
/* 2358 */       this.loadedAllFontFiles = true;
/* 2359 */       return findFont2D(paramString, paramInt1, paramInt2);
/*      */     }
/*      */ 
/* 2377 */     if ((localObject1 = findFont2DAllLocales(paramString, paramInt1)) != null) {
/* 2378 */       this.fontNameCache.put(str2, localObject1);
/* 2379 */       return localObject1;
/*      */     }
/*      */ 
/* 2392 */     if (FontUtilities.isWindows) {
/* 2393 */       localObject3 = getFontConfiguration().getFallbackFamilyName(paramString, null);
/*      */ 
/* 2395 */       if (localObject3 != null) {
/* 2396 */         localObject1 = findFont2D((String)localObject3, paramInt1, paramInt2);
/* 2397 */         this.fontNameCache.put(str2, localObject1);
/* 2398 */         return localObject1;
/*      */       }
/*      */     } else { if (str1.equals("timesroman")) {
/* 2401 */         localObject1 = findFont2D("serif", paramInt1, paramInt2);
/* 2402 */         this.fontNameCache.put(str2, localObject1);
/* 2403 */         return localObject1;
/* 2404 */       }if (str1.equals("helvetica")) {
/* 2405 */         localObject1 = findFont2D("sansserif", paramInt1, paramInt2);
/* 2406 */         this.fontNameCache.put(str2, localObject1);
/* 2407 */         return localObject1;
/* 2408 */       }if (str1.equals("courier")) {
/* 2409 */         localObject1 = findFont2D("monospaced", paramInt1, paramInt2);
/* 2410 */         this.fontNameCache.put(str2, localObject1);
/* 2411 */         return localObject1;
/*      */       }
/*      */     }
/* 2414 */     if (FontUtilities.isLogging()) {
/* 2415 */       FontUtilities.getLogger().info("No font found for:" + paramString);
/*      */     }
/*      */ 
/* 2418 */     switch (paramInt2) { case 1:
/* 2419 */       return getDefaultPhysicalFont();
/*      */     case 2:
/* 2420 */       return getDefaultLogicalFont(paramInt1); }
/* 2421 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean usePlatformFontMetrics()
/*      */   {
/* 2431 */     return this.usePlatformFontMetrics;
/*      */   }
/*      */ 
/*      */   public int getNumFonts() {
/* 2435 */     return this.physicalFonts.size() + this.maxCompFont;
/*      */   }
/*      */ 
/*      */   private static boolean fontSupportsEncoding(Font paramFont, String paramString) {
/* 2439 */     return FontUtilities.getFont2D(paramFont).supportsEncoding(paramString);
/*      */   }
/*      */ 
/*      */   protected abstract String getFontPath(boolean paramBoolean);
/*      */ 
/*      */   public Font2D createFont2D(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker)
/*      */     throws FontFormatException
/*      */   {
/* 2453 */     String str = paramFile.getPath();
/* 2454 */     Object localObject1 = null;
/* 2455 */     final File localFile = paramFile;
/* 2456 */     final CreatedFontTracker localCreatedFontTracker = paramCreatedFontTracker;
/*      */     try {
/* 2458 */       switch (paramInt) {
/*      */       case 0:
/* 2460 */         localObject1 = new TrueTypeFont(str, null, 0, true);
/* 2461 */         break;
/*      */       case 1:
/* 2463 */         localObject1 = new Type1Font(str, null, paramBoolean);
/* 2464 */         break;
/*      */       default:
/* 2466 */         throw new FontFormatException("Unrecognised Font Format");
/*      */       }
/*      */     } catch (FontFormatException localFontFormatException) {
/* 2469 */       if (paramBoolean) {
/* 2470 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/* 2473 */             if (localCreatedFontTracker != null) {
/* 2474 */               localCreatedFontTracker.subBytes((int)localFile.length());
/*      */             }
/* 2476 */             localFile.delete();
/* 2477 */             return null;
/*      */           }
/*      */         });
/*      */       }
/* 2481 */       throw localFontFormatException;
/*      */     }
/* 2483 */     if (paramBoolean) {
/* 2484 */       ((FileFont)localObject1).setFileToRemove(paramFile, paramCreatedFontTracker);
/* 2485 */       synchronized (FontManager.class)
/*      */       {
/* 2487 */         if (this.tmpFontFiles == null) {
/* 2488 */           this.tmpFontFiles = new Vector();
/*      */         }
/* 2490 */         this.tmpFontFiles.add(paramFile);
/*      */ 
/* 2492 */         if (this.fileCloser == null) {
/* 2493 */           final Runnable local8 = new Runnable() {
/*      */             public void run() {
/* 2495 */               AccessController.doPrivileged(new PrivilegedAction()
/*      */               {
/*      */                 public Object run()
/*      */                 {
/* 2499 */                   for (int i = 0; i < 20; i++)
/* 2500 */                     if (SunFontManager.this.fontFileCache[i] != null)
/*      */                       try {
/* 2502 */                         SunFontManager.this.fontFileCache[i].close();
/*      */                       }
/*      */                       catch (Exception localException1)
/*      */                       {
/*      */                       }
/* 2507 */                   if (SunFontManager.this.tmpFontFiles != null) {
/* 2508 */                     File[] arrayOfFile = new File[SunFontManager.this.tmpFontFiles.size()];
/* 2509 */                     arrayOfFile = (File[])SunFontManager.this.tmpFontFiles.toArray(arrayOfFile);
/* 2510 */                     for (int j = 0; j < arrayOfFile.length; j++)
/*      */                       try {
/* 2512 */                         arrayOfFile[j].delete();
/*      */                       }
/*      */                       catch (Exception localException2)
/*      */                       {
/*      */                       }
/*      */                   }
/* 2518 */                   return null;
/*      */                 }
/*      */               });
/*      */             }
/*      */           };
/* 2524 */           AccessController.doPrivileged(new PrivilegedAction()
/*      */           {
/*      */             public Object run()
/*      */             {
/* 2531 */               Object localObject1 = Thread.currentThread().getThreadGroup();
/*      */ 
/* 2533 */               for (Object localObject2 = localObject1; 
/* 2534 */                 localObject2 != null; 
/* 2535 */                 localObject2 = ((ThreadGroup)localObject1).getParent()) localObject1 = localObject2;
/* 2536 */               SunFontManager.this.fileCloser = new Thread((ThreadGroup)localObject1, local8);
/* 2537 */               SunFontManager.this.fileCloser.setContextClassLoader(null);
/* 2538 */               Runtime.getRuntime().addShutdownHook(SunFontManager.this.fileCloser);
/* 2539 */               return null;
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/* 2545 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public synchronized String getFullNameByFileName(String paramString)
/*      */   {
/* 2552 */     PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
/* 2553 */     for (int i = 0; i < arrayOfPhysicalFont.length; i++) {
/* 2554 */       if (arrayOfPhysicalFont[i].platName.equals(paramString)) {
/* 2555 */         return arrayOfPhysicalFont[i].getFontName(null);
/*      */       }
/*      */     }
/* 2558 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized void deRegisterBadFont(Font2D paramFont2D)
/*      */   {
/* 2574 */     if (!(paramFont2D instanceof PhysicalFont))
/*      */     {
/* 2576 */       return;
/*      */     }
/* 2578 */     if (FontUtilities.isLogging()) {
/* 2579 */       FontUtilities.getLogger().severe("Deregister bad font: " + paramFont2D);
/*      */     }
/*      */ 
/* 2582 */     replaceFont((PhysicalFont)paramFont2D, getDefaultPhysicalFont());
/*      */   }
/*      */ 
/*      */   public synchronized void replaceFont(PhysicalFont paramPhysicalFont1, PhysicalFont paramPhysicalFont2)
/*      */   {
/* 2593 */     if (paramPhysicalFont1.handle.font2D != paramPhysicalFont1)
/*      */       return;
/*      */     Object localObject;
/*      */     int j;
/* 2601 */     if (paramPhysicalFont1 == paramPhysicalFont2) {
/* 2602 */       if (FontUtilities.isLogging()) {
/* 2603 */         FontUtilities.getLogger().severe("Can't replace bad font with itself " + paramPhysicalFont1);
/*      */       }
/*      */ 
/* 2606 */       localObject = getPhysicalFonts();
/* 2607 */       for (j = 0; j < localObject.length; j++) {
/* 2608 */         if (localObject[j] != paramPhysicalFont2) {
/* 2609 */           paramPhysicalFont2 = localObject[j];
/* 2610 */           break;
/*      */         }
/*      */       }
/* 2613 */       if (paramPhysicalFont1 == paramPhysicalFont2) {
/* 2614 */         if (FontUtilities.isLogging()) {
/* 2615 */           FontUtilities.getLogger().severe("This is bad. No good physicalFonts found.");
/*      */         }
/*      */ 
/* 2618 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2626 */     paramPhysicalFont1.handle.font2D = paramPhysicalFont2;
/* 2627 */     this.physicalFonts.remove(paramPhysicalFont1.fullName);
/* 2628 */     this.fullNameToFont.remove(paramPhysicalFont1.fullName.toLowerCase(Locale.ENGLISH));
/* 2629 */     FontFamily.remove(paramPhysicalFont1);
/*      */ 
/* 2631 */     if (this.localeFullNamesToFont != null) {
/* 2632 */       localObject = (Map.Entry[])this.localeFullNamesToFont.entrySet().toArray(new Map.Entry[0]);
/*      */ 
/* 2638 */       for (j = 0; j < localObject.length; j++) {
/* 2639 */         if (localObject[j].getValue() == paramPhysicalFont1) {
/*      */           try {
/* 2641 */             localObject[j].setValue(paramPhysicalFont2);
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/* 2646 */             this.localeFullNamesToFont.remove(localObject[j].getKey());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2652 */     for (int i = 0; i < this.maxCompFont; i++)
/*      */     {
/* 2682 */       if (paramPhysicalFont2.getRank() > 2)
/* 2683 */         this.compFonts[i].replaceComponentFont(paramPhysicalFont1, paramPhysicalFont2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void loadLocaleNames()
/*      */   {
/* 2689 */     if (this.localeFullNamesToFont != null) {
/* 2690 */       return;
/*      */     }
/* 2692 */     this.localeFullNamesToFont = new HashMap();
/* 2693 */     Font2D[] arrayOfFont2D = getRegisteredFonts();
/* 2694 */     for (int i = 0; i < arrayOfFont2D.length; i++)
/* 2695 */       if ((arrayOfFont2D[i] instanceof TrueTypeFont)) {
/* 2696 */         TrueTypeFont localTrueTypeFont = (TrueTypeFont)arrayOfFont2D[i];
/* 2697 */         String[] arrayOfString = localTrueTypeFont.getAllFullNames();
/* 2698 */         for (int j = 0; j < arrayOfString.length; j++) {
/* 2699 */           this.localeFullNamesToFont.put(arrayOfString[j], localTrueTypeFont);
/*      */         }
/* 2701 */         FontFamily localFontFamily = FontFamily.getFamily(localTrueTypeFont.familyName);
/* 2702 */         if (localFontFamily != null)
/* 2703 */           FontFamily.addLocaleNames(localFontFamily, localTrueTypeFont.getAllFamilyNames());
/*      */       }
/*      */   }
/*      */ 
/*      */   private Font2D findFont2DAllLocales(String paramString, int paramInt)
/*      */   {
/* 2718 */     if (FontUtilities.isLogging()) {
/* 2719 */       FontUtilities.getLogger().info("Searching localised font names for:" + paramString);
/*      */     }
/*      */ 
/* 2727 */     if (this.localeFullNamesToFont == null) {
/* 2728 */       loadLocaleNames();
/*      */     }
/* 2730 */     String str = paramString.toLowerCase();
/* 2731 */     Font2D localFont2D = null;
/*      */ 
/* 2734 */     FontFamily localFontFamily = FontFamily.getLocaleFamily(str);
/* 2735 */     if (localFontFamily != null) {
/* 2736 */       localFont2D = localFontFamily.getFont(paramInt);
/* 2737 */       if (localFont2D == null) {
/* 2738 */         localFont2D = localFontFamily.getClosestStyle(paramInt);
/*      */       }
/* 2740 */       if (localFont2D != null) {
/* 2741 */         return localFont2D;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2746 */     synchronized (this) {
/* 2747 */       localFont2D = (Font2D)this.localeFullNamesToFont.get(paramString);
/*      */     }
/* 2749 */     if (localFont2D != null) {
/* 2750 */       if ((localFont2D.style == paramInt) || (paramInt == 0)) {
/* 2751 */         return localFont2D;
/*      */       }
/* 2753 */       localFontFamily = FontFamily.getFamily(localFont2D.getFamilyName(null));
/* 2754 */       if (localFontFamily != null) {
/* 2755 */         ??? = localFontFamily.getFont(paramInt);
/*      */ 
/* 2757 */         if (??? != null) {
/* 2758 */           return ???;
/*      */         }
/* 2760 */         ??? = localFontFamily.getClosestStyle(paramInt);
/* 2761 */         if (??? != null)
/*      */         {
/* 2770 */           if (!((Font2D)???).canDoStyle(paramInt)) {
/* 2771 */             ??? = null;
/*      */           }
/* 2773 */           return ???;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2779 */     return localFont2D;
/*      */   }
/*      */ 
/*      */   public boolean maybeUsingAlternateCompositeFonts()
/*      */   {
/* 2858 */     return (this._usingAlternateComposites) || (this._usingPerAppContextComposites);
/*      */   }
/*      */ 
/*      */   public boolean usingAlternateCompositeFonts() {
/* 2862 */     return (this._usingAlternateComposites) || ((this._usingPerAppContextComposites) && (AppContext.getAppContext().get(CompositeFont.class) != null));
/*      */   }
/*      */ 
/*      */   private static boolean maybeMultiAppContext()
/*      */   {
/* 2868 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/* 2872 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 2873 */         return new Boolean(localSecurityManager instanceof AppletSecurity);
/*      */       }
/*      */     });
/* 2877 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   public synchronized void useAlternateFontforJALocales()
/*      */   {
/* 2885 */     if (FontUtilities.isLogging()) {
/* 2886 */       FontUtilities.getLogger().info("Entered useAlternateFontforJALocales().");
/*      */     }
/*      */ 
/* 2889 */     if (!FontUtilities.isWindows) {
/* 2890 */       return;
/*      */     }
/*      */ 
/* 2893 */     if (!maybeMultiAppContext()) {
/* 2894 */       gAltJAFont = true;
/*      */     } else {
/* 2896 */       AppContext localAppContext = AppContext.getAppContext();
/* 2897 */       localAppContext.put(altJAFontKey, altJAFontKey);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean usingAlternateFontforJALocales() {
/* 2902 */     if (!maybeMultiAppContext()) {
/* 2903 */       return gAltJAFont;
/*      */     }
/* 2905 */     AppContext localAppContext = AppContext.getAppContext();
/* 2906 */     return localAppContext.get(altJAFontKey) == altJAFontKey;
/*      */   }
/*      */ 
/*      */   public synchronized void preferLocaleFonts()
/*      */   {
/* 2911 */     if (FontUtilities.isLogging()) {
/* 2912 */       FontUtilities.getLogger().info("Entered preferLocaleFonts().");
/*      */     }
/*      */ 
/* 2915 */     if (!FontConfiguration.willReorderForStartupLocale()) {
/* 2916 */       return;
/*      */     }
/*      */ 
/* 2919 */     if (!maybeMultiAppContext()) {
/* 2920 */       if (this.gLocalePref == true) {
/* 2921 */         return;
/*      */       }
/* 2923 */       this.gLocalePref = true;
/* 2924 */       createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
/* 2925 */       this._usingAlternateComposites = true;
/*      */     } else {
/* 2927 */       AppContext localAppContext = AppContext.getAppContext();
/* 2928 */       if (localAppContext.get(localeFontKey) == localeFontKey) {
/* 2929 */         return;
/*      */       }
/* 2931 */       localAppContext.put(localeFontKey, localeFontKey);
/* 2932 */       boolean bool = localAppContext.get(proportionalFontKey) == proportionalFontKey;
/*      */ 
/* 2935 */       ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
/*      */ 
/* 2937 */       localAppContext.put(CompositeFont.class, localConcurrentHashMap);
/* 2938 */       this._usingPerAppContextComposites = true;
/* 2939 */       createCompositeFonts(localConcurrentHashMap, true, bool);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void preferProportionalFonts() {
/* 2944 */     if (FontUtilities.isLogging()) {
/* 2945 */       FontUtilities.getLogger().info("Entered preferProportionalFonts().");
/*      */     }
/*      */ 
/* 2951 */     if (!FontConfiguration.hasMonoToPropMap()) {
/* 2952 */       return;
/*      */     }
/*      */ 
/* 2955 */     if (!maybeMultiAppContext()) {
/* 2956 */       if (this.gPropPref == true) {
/* 2957 */         return;
/*      */       }
/* 2959 */       this.gPropPref = true;
/* 2960 */       createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
/* 2961 */       this._usingAlternateComposites = true;
/*      */     } else {
/* 2963 */       AppContext localAppContext = AppContext.getAppContext();
/* 2964 */       if (localAppContext.get(proportionalFontKey) == proportionalFontKey) {
/* 2965 */         return;
/*      */       }
/* 2967 */       localAppContext.put(proportionalFontKey, proportionalFontKey);
/* 2968 */       boolean bool = localAppContext.get(localeFontKey) == localeFontKey;
/*      */ 
/* 2971 */       ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
/*      */ 
/* 2973 */       localAppContext.put(CompositeFont.class, localConcurrentHashMap);
/* 2974 */       this._usingPerAppContextComposites = true;
/* 2975 */       createCompositeFonts(localConcurrentHashMap, bool, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static HashSet<String> getInstalledNames()
/*      */   {
/* 2981 */     if (installedNames == null) {
/* 2982 */       Locale localLocale = getSystemStartupLocale();
/* 2983 */       SunFontManager localSunFontManager = getInstance();
/* 2984 */       String[] arrayOfString = localSunFontManager.getInstalledFontFamilyNames(localLocale);
/*      */ 
/* 2986 */       Font[] arrayOfFont = localSunFontManager.getAllInstalledFonts();
/* 2987 */       HashSet localHashSet = new HashSet();
/* 2988 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 2989 */         localHashSet.add(arrayOfString[i].toLowerCase(localLocale));
/*      */       }
/* 2991 */       for (i = 0; i < arrayOfFont.length; i++) {
/* 2992 */         localHashSet.add(arrayOfFont[i].getFontName(localLocale).toLowerCase(localLocale));
/*      */       }
/* 2994 */       installedNames = localHashSet;
/*      */     }
/* 2996 */     return installedNames;
/*      */   }
/*      */ 
/*      */   public boolean registerFont(Font paramFont)
/*      */   {
/* 3011 */     if (paramFont == null) {
/* 3012 */       return false;
/*      */     }
/*      */ 
/* 3016 */     synchronized (regFamilyKey) {
/* 3017 */       if (this.createdByFamilyName == null) {
/* 3018 */         this.createdByFamilyName = new Hashtable();
/* 3019 */         this.createdByFullName = new Hashtable();
/*      */       }
/*      */     }
/*      */ 
/* 3023 */     if (!FontAccess.getFontAccess().isCreatedFont(paramFont)) {
/* 3024 */       return false;
/*      */     }
/*      */ 
/* 3044 */     ??? = getInstalledNames();
/* 3045 */     Locale localLocale = getSystemStartupLocale();
/* 3046 */     String str1 = paramFont.getFamily(localLocale).toLowerCase();
/* 3047 */     String str2 = paramFont.getFontName(localLocale).toLowerCase();
/* 3048 */     if ((((HashSet)???).contains(str1)) || (((HashSet)???).contains(str2)))
/* 3049 */       return false;
/*      */     Hashtable localHashtable1;
/*      */     Hashtable localHashtable2;
/* 3055 */     if (!maybeMultiAppContext()) {
/* 3056 */       localHashtable1 = this.createdByFamilyName;
/* 3057 */       localHashtable2 = this.createdByFullName;
/* 3058 */       this.fontsAreRegistered = true;
/*      */     } else {
/* 3060 */       localObject2 = AppContext.getAppContext();
/* 3061 */       localHashtable1 = (Hashtable)((AppContext)localObject2).get(regFamilyKey);
/*      */ 
/* 3063 */       localHashtable2 = (Hashtable)((AppContext)localObject2).get(regFullNameKey);
/*      */ 
/* 3065 */       if (localHashtable1 == null) {
/* 3066 */         localHashtable1 = new Hashtable();
/* 3067 */         localHashtable2 = new Hashtable();
/* 3068 */         ((AppContext)localObject2).put(regFamilyKey, localHashtable1);
/* 3069 */         ((AppContext)localObject2).put(regFullNameKey, localHashtable2);
/*      */       }
/* 3071 */       this.fontsAreRegisteredPerAppContext = true;
/*      */     }
/*      */ 
/* 3074 */     Object localObject2 = FontUtilities.getFont2D(paramFont);
/* 3075 */     int i = ((Font2D)localObject2).getStyle();
/* 3076 */     FontFamily localFontFamily = (FontFamily)localHashtable1.get(str1);
/* 3077 */     if (localFontFamily == null) {
/* 3078 */       localFontFamily = new FontFamily(paramFont.getFamily(localLocale));
/* 3079 */       localHashtable1.put(str1, localFontFamily);
/*      */     }
/*      */ 
/* 3087 */     if (this.fontsAreRegistered) {
/* 3088 */       removeFromCache(localFontFamily.getFont(0));
/* 3089 */       removeFromCache(localFontFamily.getFont(1));
/* 3090 */       removeFromCache(localFontFamily.getFont(2));
/* 3091 */       removeFromCache(localFontFamily.getFont(3));
/* 3092 */       removeFromCache((Font2D)localHashtable2.get(str2));
/*      */     }
/* 3094 */     localFontFamily.setFont((Font2D)localObject2, i);
/* 3095 */     localHashtable2.put(str2, localObject2);
/* 3096 */     return true;
/*      */   }
/*      */ 
/*      */   private void removeFromCache(Font2D paramFont2D)
/*      */   {
/* 3101 */     if (paramFont2D == null) {
/* 3102 */       return;
/*      */     }
/* 3104 */     String[] arrayOfString = (String[])this.fontNameCache.keySet().toArray(STR_ARRAY);
/* 3105 */     for (int i = 0; i < arrayOfString.length; i++)
/* 3106 */       if (this.fontNameCache.get(arrayOfString[i]) == paramFont2D)
/* 3107 */         this.fontNameCache.remove(arrayOfString[i]);
/*      */   }
/*      */ 
/*      */   public TreeMap<String, String> getCreatedFontFamilyNames()
/*      */   {
/*      */     Hashtable localHashtable;
/* 3116 */     if (this.fontsAreRegistered) {
/* 3117 */       localHashtable = this.createdByFamilyName;
/* 3118 */     } else if (this.fontsAreRegisteredPerAppContext) {
/* 3119 */       localObject1 = AppContext.getAppContext();
/* 3120 */       localHashtable = (Hashtable)((AppContext)localObject1).get(regFamilyKey);
/*      */     }
/*      */     else {
/* 3123 */       return null;
/*      */     }
/*      */ 
/* 3126 */     Object localObject1 = getSystemStartupLocale();
/* 3127 */     synchronized (localHashtable) {
/* 3128 */       TreeMap localTreeMap = new TreeMap();
/* 3129 */       for (FontFamily localFontFamily : localHashtable.values()) {
/* 3130 */         Font2D localFont2D = localFontFamily.getFont(0);
/* 3131 */         if (localFont2D == null) {
/* 3132 */           localFont2D = localFontFamily.getClosestStyle(0);
/*      */         }
/* 3134 */         String str = localFont2D.getFamilyName((Locale)localObject1);
/* 3135 */         localTreeMap.put(str.toLowerCase((Locale)localObject1), str);
/*      */       }
/* 3137 */       return localTreeMap;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Font[] getCreatedFonts()
/*      */   {
/*      */     Hashtable localHashtable;
/* 3144 */     if (this.fontsAreRegistered) {
/* 3145 */       localHashtable = this.createdByFullName;
/* 3146 */     } else if (this.fontsAreRegisteredPerAppContext) {
/* 3147 */       localObject1 = AppContext.getAppContext();
/* 3148 */       localHashtable = (Hashtable)((AppContext)localObject1).get(regFullNameKey);
/*      */     }
/*      */     else {
/* 3151 */       return null;
/*      */     }
/*      */ 
/* 3154 */     Object localObject1 = getSystemStartupLocale();
/* 3155 */     synchronized (localHashtable) {
/* 3156 */       Font[] arrayOfFont = new Font[localHashtable.size()];
/* 3157 */       int i = 0;
/* 3158 */       for (Font2D localFont2D : localHashtable.values()) {
/* 3159 */         arrayOfFont[(i++)] = new Font(localFont2D.getFontName((Locale)localObject1), 0, 1);
/*      */       }
/* 3161 */       return arrayOfFont;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String[] getPlatformFontDirs(boolean paramBoolean)
/*      */   {
/* 3169 */     if (this.pathDirs != null) {
/* 3170 */       return this.pathDirs;
/*      */     }
/*      */ 
/* 3173 */     String str = getPlatformFontPath(paramBoolean);
/* 3174 */     StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
/*      */ 
/* 3176 */     ArrayList localArrayList = new ArrayList();
/*      */     try {
/* 3178 */       while (localStringTokenizer.hasMoreTokens())
/* 3179 */         localArrayList.add(localStringTokenizer.nextToken());
/*      */     }
/*      */     catch (NoSuchElementException localNoSuchElementException) {
/*      */     }
/* 3183 */     this.pathDirs = ((String[])localArrayList.toArray(new String[0]));
/* 3184 */     return this.pathDirs;
/*      */   }
/*      */ 
/*      */   public abstract String[] getDefaultPlatformFont();
/*      */ 
/*      */   private void addDirFonts(String paramString, File paramFile, FilenameFilter paramFilenameFilter, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 3203 */     String[] arrayOfString1 = paramFile.list(paramFilenameFilter);
/* 3204 */     if ((arrayOfString1 == null) || (arrayOfString1.length == 0)) {
/* 3205 */       return;
/*      */     }
/* 3207 */     String[] arrayOfString2 = new String[arrayOfString1.length];
/* 3208 */     String[][] arrayOfString; = new String[arrayOfString1.length][];
/* 3209 */     int i = 0;
/*      */ 
/* 3211 */     for (int j = 0; j < arrayOfString1.length; j++) {
/* 3212 */       File localFile = new File(paramFile, arrayOfString1[j]);
/* 3213 */       String str1 = null;
/* 3214 */       if (paramBoolean3)
/*      */         try {
/* 3216 */           str1 = localFile.getCanonicalPath();
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/* 3220 */       if (str1 == null) {
/* 3221 */         str1 = paramString + File.separator + arrayOfString1[j];
/*      */       }
/*      */ 
/* 3225 */       if (!this.registeredFontFiles.contains(str1))
/*      */       {
/* 3229 */         if ((this.badFonts != null) && (this.badFonts.contains(str1))) {
/* 3230 */           if (FontUtilities.debugFonts()) {
/* 3231 */             FontUtilities.getLogger().warning("skip bad font " + str1);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 3237 */           this.registeredFontFiles.add(str1);
/*      */ 
/* 3239 */           if ((FontUtilities.debugFonts()) && (FontUtilities.getLogger().isLoggable(800)))
/*      */           {
/* 3241 */             String str2 = "Registering font " + str1;
/* 3242 */             String[] arrayOfString3 = getNativeNames(str1, null);
/* 3243 */             if (arrayOfString3 == null) {
/* 3244 */               str2 = str2 + " with no native name";
/*      */             } else {
/* 3246 */               str2 = str2 + " with native name(s) " + arrayOfString3[0];
/* 3247 */               for (int k = 1; k < arrayOfString3.length; k++) {
/* 3248 */                 str2 = str2 + ", " + arrayOfString3[k];
/*      */               }
/*      */             }
/* 3251 */             FontUtilities.getLogger().info(str2);
/*      */           }
/* 3253 */           arrayOfString2[i] = str1;
/* 3254 */           arrayOfString;[(i++)] = getNativeNames(str1, null);
/*      */         }
/*      */       }
/*      */     }
/* 3256 */     registerFonts(arrayOfString2, arrayOfString;, i, paramInt1, paramBoolean1, paramInt2, paramBoolean2);
/*      */   }
/*      */ 
/*      */   protected String[] getNativeNames(String paramString1, String paramString2)
/*      */   {
/* 3263 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getFileNameFromPlatformName(String paramString)
/*      */   {
/* 3273 */     return this.fontConfig.getFileNameFromPlatformName(paramString);
/*      */   }
/*      */ 
/*      */   public FontConfiguration getFontConfiguration()
/*      */   {
/* 3280 */     return this.fontConfig;
/*      */   }
/*      */ 
/*      */   public String getPlatformFontPath(boolean paramBoolean)
/*      */   {
/* 3287 */     if (this.fontPath == null) {
/* 3288 */       this.fontPath = getFontPath(paramBoolean);
/*      */     }
/* 3290 */     return this.fontPath;
/*      */   }
/*      */ 
/*      */   public static boolean isOpenJDK() {
/* 3294 */     return FontUtilities.isOpenJDK;
/*      */   }
/*      */ 
/*      */   protected void loadFonts() {
/* 3298 */     if (this.discoveredAllFonts) {
/* 3299 */       return;
/*      */     }
/*      */ 
/* 3302 */     synchronized (this) {
/* 3303 */       if (FontUtilities.debugFonts()) {
/* 3304 */         Thread.dumpStack();
/* 3305 */         FontUtilities.getLogger().info("SunGraphicsEnvironment.loadFonts() called");
/*      */       }
/*      */ 
/* 3308 */       initialiseDeferredFonts();
/*      */ 
/* 3310 */       AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/* 3313 */           if (SunFontManager.this.fontPath == null) {
/* 3314 */             SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
/* 3315 */             SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
/*      */           }
/* 3317 */           if (SunFontManager.this.fontPath != null)
/*      */           {
/* 3321 */             if (!SunFontManager.this.gotFontsFromPlatform()) {
/* 3322 */               SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
/*      */ 
/* 3325 */               SunFontManager.this.loadedAllFontFiles = true;
/*      */             }
/*      */           }
/* 3328 */           SunFontManager.this.registerOtherFontFiles(SunFontManager.this.registeredFontFiles);
/* 3329 */           SunFontManager.this.discoveredAllFonts = true;
/* 3330 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void registerFontDirs(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   private void registerFontsOnPath(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 3344 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
/*      */     try
/*      */     {
/* 3347 */       while (localStringTokenizer.hasMoreTokens())
/* 3348 */         registerFontsInDir(localStringTokenizer.nextToken(), paramBoolean1, paramInt, paramBoolean2, paramBoolean3);
/*      */     }
/*      */     catch (NoSuchElementException localNoSuchElementException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerFontsInDir(String paramString)
/*      */   {
/* 3358 */     registerFontsInDir(paramString, true, 2, true, false);
/*      */   }
/*      */ 
/*      */   protected void registerFontsInDir(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 3366 */     File localFile = new File(paramString);
/* 3367 */     addDirFonts(paramString, localFile, ttFilter, 0, paramBoolean1, paramInt == 6 ? 3 : paramInt, paramBoolean2, paramBoolean3);
/*      */ 
/* 3372 */     addDirFonts(paramString, localFile, t1Filter, 1, paramBoolean1, paramInt == 6 ? 4 : paramInt, paramBoolean2, paramBoolean3);
/*      */   }
/*      */ 
/*      */   protected void registerFontDir(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized String getDefaultFontFile()
/*      */   {
/* 3387 */     if (this.defaultFontFileName == null) {
/* 3388 */       initDefaultFonts();
/*      */     }
/* 3390 */     return this.defaultFontFileName;
/*      */   }
/*      */ 
/*      */   private void initDefaultFonts() {
/* 3394 */     if (!isOpenJDK()) {
/* 3395 */       this.defaultFontName = "Lucida Sans Regular";
/* 3396 */       if (useAbsoluteFontFileNames()) {
/* 3397 */         this.defaultFontFileName = (jreFontDirName + File.separator + "LucidaSansRegular.ttf");
/*      */       }
/*      */       else
/* 3400 */         this.defaultFontFileName = "LucidaSansRegular.ttf";
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean useAbsoluteFontFileNames()
/*      */   {
/* 3410 */     return true;
/*      */   }
/*      */ 
/*      */   protected abstract FontConfiguration createFontConfiguration();
/*      */ 
/*      */   public abstract FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2);
/*      */ 
/*      */   public synchronized String getDefaultFontFaceName()
/*      */   {
/* 3428 */     if (this.defaultFontName == null) {
/* 3429 */       initDefaultFonts();
/*      */     }
/* 3431 */     return this.defaultFontName;
/*      */   }
/*      */ 
/*      */   public void loadFontFiles() {
/* 3435 */     loadFonts();
/* 3436 */     if (this.loadedAllFontFiles) {
/* 3437 */       return;
/*      */     }
/*      */ 
/* 3440 */     synchronized (this) {
/* 3441 */       if (FontUtilities.debugFonts()) {
/* 3442 */         Thread.dumpStack();
/* 3443 */         FontUtilities.getLogger().info("loadAllFontFiles() called");
/*      */       }
/* 3445 */       AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/* 3448 */           if (SunFontManager.this.fontPath == null) {
/* 3449 */             SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
/*      */           }
/* 3451 */           if (SunFontManager.this.fontPath != null)
/*      */           {
/* 3455 */             SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
/*      */           }
/*      */ 
/* 3459 */           SunFontManager.this.loadedAllFontFiles = true;
/* 3460 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initCompositeFonts(FontConfiguration paramFontConfiguration, ConcurrentHashMap<String, Font2D> paramConcurrentHashMap)
/*      */   {
/* 3477 */     if (FontUtilities.isLogging()) {
/* 3478 */       FontUtilities.getLogger().info("Initialising composite fonts");
/*      */     }
/*      */ 
/* 3482 */     int i = paramFontConfiguration.getNumberCoreFonts();
/* 3483 */     String[] arrayOfString1 = paramFontConfiguration.getPlatformFontNames();
/*      */     Object localObject;
/*      */     String[] arrayOfString2;
/* 3484 */     for (int j = 0; j < arrayOfString1.length; j++) {
/* 3485 */       String str = arrayOfString1[j];
/* 3486 */       localObject = getFileNameFromPlatformName(str);
/*      */ 
/* 3488 */       arrayOfString2 = null;
/* 3489 */       if ((localObject == null) || (((String)localObject).equals(str)))
/*      */       {
/* 3494 */         localObject = str;
/*      */       } else {
/* 3496 */         if (j < i)
/*      */         {
/* 3511 */           addFontToPlatformFontPath(str);
/*      */         }
/* 3513 */         arrayOfString2 = getNativeNames((String)localObject, str);
/*      */       }
/*      */ 
/* 3522 */       registerFontFile((String)localObject, arrayOfString2, 2, true);
/*      */     }
/*      */ 
/* 3534 */     registerPlatformFontsUsedByFontConfiguration();
/*      */ 
/* 3536 */     CompositeFontDescriptor[] arrayOfCompositeFontDescriptor = paramFontConfiguration.get2DCompositeFontInfo();
/*      */ 
/* 3538 */     for (int k = 0; k < arrayOfCompositeFontDescriptor.length; k++) {
/* 3539 */       localObject = arrayOfCompositeFontDescriptor[k];
/* 3540 */       arrayOfString2 = ((CompositeFontDescriptor)localObject).getComponentFileNames();
/* 3541 */       String[] arrayOfString3 = ((CompositeFontDescriptor)localObject).getComponentFaceNames();
/*      */ 
/* 3546 */       if (missingFontFiles != null) {
/* 3547 */         for (int m = 0; m < arrayOfString2.length; m++) {
/* 3548 */           if (missingFontFiles.contains(arrayOfString2[m])) {
/* 3549 */             arrayOfString2[m] = getDefaultFontFile();
/* 3550 */             arrayOfString3[m] = getDefaultFontFaceName();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3561 */       if (paramConcurrentHashMap != null) {
/* 3562 */         registerCompositeFont(((CompositeFontDescriptor)localObject).getFaceName(), arrayOfString2, arrayOfString3, ((CompositeFontDescriptor)localObject).getCoreComponentCount(), ((CompositeFontDescriptor)localObject).getExclusionRanges(), ((CompositeFontDescriptor)localObject).getExclusionRangeLimits(), true, paramConcurrentHashMap);
/*      */       }
/*      */       else
/*      */       {
/* 3571 */         registerCompositeFont(((CompositeFontDescriptor)localObject).getFaceName(), arrayOfString2, arrayOfString3, ((CompositeFontDescriptor)localObject).getCoreComponentCount(), ((CompositeFontDescriptor)localObject).getExclusionRanges(), ((CompositeFontDescriptor)localObject).getExclusionRangeLimits(), true);
/*      */       }
/*      */ 
/* 3578 */       if (FontUtilities.debugFonts())
/* 3579 */         FontUtilities.getLogger().info("registered " + ((CompositeFontDescriptor)localObject).getFaceName());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addFontToPlatformFontPath(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
/*      */   {
/* 3596 */     if (this.registeredFontFiles.contains(paramString))
/*      */       return;
/*      */     int i;
/* 3600 */     if (ttFilter.accept(null, paramString))
/* 3601 */       i = 0;
/* 3602 */     else if (t1Filter.accept(null, paramString))
/* 3603 */       i = 1;
/*      */     else {
/* 3605 */       i = 5;
/*      */     }
/* 3607 */     this.registeredFontFiles.add(paramString);
/* 3608 */     if (paramBoolean) {
/* 3609 */       registerDeferredFont(paramString, paramString, paramArrayOfString, i, false, paramInt);
/*      */     }
/*      */     else
/* 3612 */       registerFontFile(paramString, paramArrayOfString, i, false, paramInt);
/*      */   }
/*      */ 
/*      */   protected void registerPlatformFontsUsedByFontConfiguration()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addToMissingFontFileList(String paramString)
/*      */   {
/* 3635 */     if (missingFontFiles == null) {
/* 3636 */       missingFontFiles = new HashSet();
/*      */     }
/* 3638 */     missingFontFiles.add(paramString);
/*      */   }
/*      */ 
/*      */   private boolean isNameForRegisteredFile(String paramString)
/*      */   {
/* 3660 */     String str = getFileNameForFontName(paramString);
/* 3661 */     if (str == null) {
/* 3662 */       return false;
/*      */     }
/* 3664 */     return this.registeredFontFiles.contains(str);
/*      */   }
/*      */ 
/*      */   public void createCompositeFonts(ConcurrentHashMap<String, Font2D> paramConcurrentHashMap, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 3677 */     FontConfiguration localFontConfiguration = createFontConfiguration(paramBoolean1, paramBoolean2);
/*      */ 
/* 3679 */     initCompositeFonts(localFontConfiguration, paramConcurrentHashMap);
/*      */   }
/*      */ 
/*      */   public Font[] getAllInstalledFonts()
/*      */   {
/* 3686 */     if (this.allFonts == null) {
/* 3687 */       loadFonts();
/* 3688 */       localObject1 = new TreeMap();
/*      */ 
/* 3693 */       Font2D[] arrayOfFont2D = getRegisteredFonts();
/* 3694 */       for (int i = 0; i < arrayOfFont2D.length; i++) {
/* 3695 */         if (!(arrayOfFont2D[i] instanceof NativeFont)) {
/* 3696 */           ((TreeMap)localObject1).put(arrayOfFont2D[i].getFontName(null), arrayOfFont2D[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3701 */       String[] arrayOfString1 = getFontNamesFromPlatform();
/* 3702 */       if (arrayOfString1 != null) {
/* 3703 */         for (int j = 0; j < arrayOfString1.length; j++) {
/* 3704 */           if (!isNameForRegisteredFile(arrayOfString1[j])) {
/* 3705 */             ((TreeMap)localObject1).put(arrayOfString1[j], null);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3710 */       String[] arrayOfString2 = null;
/* 3711 */       if (((TreeMap)localObject1).size() > 0) {
/* 3712 */         arrayOfString2 = new String[((TreeMap)localObject1).size()];
/* 3713 */         localObject2 = ((TreeMap)localObject1).keySet().toArray();
/* 3714 */         for (k = 0; k < localObject2.length; k++) {
/* 3715 */           arrayOfString2[k] = ((String)localObject2[k]);
/*      */         }
/*      */       }
/* 3718 */       Object localObject2 = new Font[arrayOfString2.length];
/* 3719 */       for (int k = 0; k < arrayOfString2.length; k++) {
/* 3720 */         localObject2[k] = new Font(arrayOfString2[k], 0, 1);
/* 3721 */         Font2D localFont2D = (Font2D)((TreeMap)localObject1).get(arrayOfString2[k]);
/* 3722 */         if (localFont2D != null) {
/* 3723 */           FontAccess.getFontAccess().setFont2D(localObject2[k], localFont2D.handle);
/*      */         }
/*      */       }
/* 3726 */       this.allFonts = ((Font[])localObject2);
/*      */     }
/*      */ 
/* 3729 */     Object localObject1 = new Font[this.allFonts.length];
/* 3730 */     System.arraycopy(this.allFonts, 0, localObject1, 0, this.allFonts.length);
/* 3731 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public String[] getInstalledFontFamilyNames(Locale paramLocale)
/*      */   {
/* 3743 */     if (paramLocale == null) {
/* 3744 */       paramLocale = Locale.getDefault();
/*      */     }
/* 3746 */     if ((this.allFamilies != null) && (this.lastDefaultLocale != null) && (paramLocale.equals(this.lastDefaultLocale)))
/*      */     {
/* 3748 */       localObject1 = new String[this.allFamilies.length];
/* 3749 */       System.arraycopy(this.allFamilies, 0, localObject1, 0, this.allFamilies.length);
/*      */ 
/* 3751 */       return localObject1;
/*      */     }
/*      */ 
/* 3754 */     Object localObject1 = new TreeMap();
/*      */ 
/* 3757 */     String str1 = "Serif"; ((TreeMap)localObject1).put(str1.toLowerCase(), str1);
/* 3758 */     str1 = "SansSerif"; ((TreeMap)localObject1).put(str1.toLowerCase(), str1);
/* 3759 */     str1 = "Monospaced"; ((TreeMap)localObject1).put(str1.toLowerCase(), str1);
/* 3760 */     str1 = "Dialog"; ((TreeMap)localObject1).put(str1.toLowerCase(), str1);
/* 3761 */     str1 = "DialogInput"; ((TreeMap)localObject1).put(str1.toLowerCase(), str1);
/*      */ 
/* 3767 */     if ((paramLocale.equals(getSystemStartupLocale())) && (getFamilyNamesFromPlatform((TreeMap)localObject1, paramLocale)))
/*      */     {
/* 3770 */       getJREFontFamilyNames((TreeMap)localObject1, paramLocale);
/*      */     } else {
/* 3772 */       loadFontFiles();
/* 3773 */       localObject2 = getPhysicalFonts();
/* 3774 */       for (int i = 0; i < localObject2.length; i++) {
/* 3775 */         if (!(localObject2[i] instanceof NativeFont)) {
/* 3776 */           String str2 = localObject2[i].getFamilyName(paramLocale);
/*      */ 
/* 3778 */           ((TreeMap)localObject1).put(str2.toLowerCase(paramLocale), str2);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3784 */     addNativeFontFamilyNames((TreeMap)localObject1, paramLocale);
/*      */ 
/* 3786 */     Object localObject2 = new String[((TreeMap)localObject1).size()];
/* 3787 */     Object[] arrayOfObject = ((TreeMap)localObject1).keySet().toArray();
/* 3788 */     for (int j = 0; j < arrayOfObject.length; j++) {
/* 3789 */       localObject2[j] = ((String)((TreeMap)localObject1).get(arrayOfObject[j]));
/*      */     }
/* 3791 */     if (paramLocale.equals(Locale.getDefault())) {
/* 3792 */       this.lastDefaultLocale = paramLocale;
/* 3793 */       this.allFamilies = new String[localObject2.length];
/* 3794 */       System.arraycopy(localObject2, 0, this.allFamilies, 0, this.allFamilies.length);
/*      */     }
/* 3796 */     return localObject2;
/*      */   }
/*      */ 
/*      */   protected void addNativeFontFamilyNames(TreeMap<String, String> paramTreeMap, Locale paramLocale) {
/*      */   }
/*      */ 
/*      */   public void register1dot0Fonts() {
/* 3803 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/* 3806 */         String str = "/usr/openwin/lib/X11/fonts/Type1";
/* 3807 */         SunFontManager.this.registerFontsInDir(str, true, 4, false, false);
/*      */ 
/* 3809 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected void getJREFontFamilyNames(TreeMap<String, String> paramTreeMap, Locale paramLocale)
/*      */   {
/* 3820 */     registerDeferredJREFonts(jreFontDirName);
/* 3821 */     PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
/* 3822 */     for (int i = 0; i < arrayOfPhysicalFont.length; i++)
/* 3823 */       if (!(arrayOfPhysicalFont[i] instanceof NativeFont)) {
/* 3824 */         String str = arrayOfPhysicalFont[i].getFamilyName(paramLocale);
/*      */ 
/* 3826 */         paramTreeMap.put(str.toLowerCase(paramLocale), str);
/*      */       }
/*      */   }
/*      */ 
/*      */   private static Locale getSystemStartupLocale()
/*      */   {
/* 3843 */     if (systemLocale == null) {
/* 3844 */       systemLocale = (Locale)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/* 3858 */           String str1 = System.getProperty("file.encoding", "");
/* 3859 */           String str2 = System.getProperty("sun.jnu.encoding");
/* 3860 */           if ((str2 != null) && (!str2.equals(str1))) {
/* 3861 */             return Locale.ROOT;
/*      */           }
/*      */ 
/* 3864 */           String str3 = System.getProperty("user.language", "en");
/* 3865 */           String str4 = System.getProperty("user.country", "");
/* 3866 */           String str5 = System.getProperty("user.variant", "");
/* 3867 */           return new Locale(str3, str4, str5);
/*      */         }
/*      */       });
/*      */     }
/* 3871 */     return systemLocale;
/*      */   }
/*      */ 
/*      */   void addToPool(FileFont paramFileFont)
/*      */   {
/* 3876 */     FileFont localFileFont = null;
/* 3877 */     int i = -1;
/*      */ 
/* 3879 */     synchronized (this.fontFileCache)
/*      */     {
/* 3887 */       for (int j = 0; j < 20; j++) {
/* 3888 */         if (this.fontFileCache[j] == paramFileFont) {
/* 3889 */           return;
/*      */         }
/* 3891 */         if ((this.fontFileCache[j] == null) && (i < 0)) {
/* 3892 */           i = j;
/*      */         }
/*      */       }
/* 3895 */       if (i >= 0) {
/* 3896 */         this.fontFileCache[i] = paramFileFont;
/* 3897 */         return;
/*      */       }
/*      */ 
/* 3900 */       localFileFont = this.fontFileCache[this.lastPoolIndex];
/* 3901 */       this.fontFileCache[this.lastPoolIndex] = paramFileFont;
/*      */ 
/* 3905 */       this.lastPoolIndex = ((this.lastPoolIndex + 1) % 20);
/*      */     }
/*      */ 
/* 3918 */     if (localFileFont != null)
/* 3919 */       localFileFont.close();
/*      */   }
/*      */ 
/*      */   protected FontUIResource getFontConfigFUIR(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 3926 */     return new FontUIResource(paramString, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  334 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  338 */         FontManagerNativeLibrary.load();
/*      */ 
/*  342 */         SunFontManager.access$200();
/*      */ 
/*  344 */         switch (StrikeCache.nativeAddressSize) { case 8:
/*  345 */           SunFontManager.longAddresses = true; break;
/*      */         case 4:
/*  346 */           SunFontManager.longAddresses = false; break;
/*      */         default:
/*  347 */           throw new RuntimeException("Unexpected address size");
/*      */         }
/*      */ 
/*  350 */         SunFontManager.noType1Font = "true".equals(System.getProperty("sun.java2d.noType1Font"));
/*      */ 
/*  352 */         SunFontManager.jreLibDirName = System.getProperty("java.home", "") + File.separator + "lib";
/*      */ 
/*  354 */         SunFontManager.jreFontDirName = SunFontManager.jreLibDirName + File.separator + "fonts";
/*  355 */         File localFile = new File(SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf");
/*      */ 
/*  358 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static class FamilyDescription
/*      */   {
/*      */     public String familyName;
/*      */     public String plainFullName;
/*      */     public String boldFullName;
/*      */     public String italicFullName;
/*      */     public String boldItalicFullName;
/*      */     public String plainFileName;
/*      */     public String boldFileName;
/*      */     public String italicFileName;
/*      */     public String boldItalicFileName;
/*      */   }
/*      */ 
/*      */   private static final class FontRegistrationInfo
/*      */   {
/*      */     String fontFilePath;
/*      */     String[] nativeNames;
/*      */     int fontFormat;
/*      */     boolean javaRasterizer;
/*      */     int fontRank;
/*      */ 
/*      */     FontRegistrationInfo(String paramString, String[] paramArrayOfString, int paramInt1, boolean paramBoolean, int paramInt2)
/*      */     {
/*  904 */       this.fontFilePath = paramString;
/*  905 */       this.nativeNames = paramArrayOfString;
/*  906 */       this.fontFormat = paramInt1;
/*  907 */       this.javaRasterizer = paramBoolean;
/*  908 */       this.fontRank = paramInt2;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class T1Filter
/*      */     implements FilenameFilter
/*      */   {
/*      */     public boolean accept(File paramFile, String paramString)
/*      */     {
/*   85 */       if (SunFontManager.noType1Font) {
/*   86 */         return false;
/*      */       }
/*      */ 
/*   89 */       int i = paramString.length() - 4;
/*   90 */       if (i <= 0) {
/*   91 */         return false;
/*      */       }
/*   93 */       return (paramString.startsWith(".pfa", i)) || (paramString.startsWith(".pfb", i)) || (paramString.startsWith(".PFA", i)) || (paramString.startsWith(".PFB", i));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TTFilter
/*      */     implements FilenameFilter
/*      */   {
/*      */     public boolean accept(File paramFile, String paramString)
/*      */     {
/*   69 */       int i = paramString.length() - 4;
/*   70 */       if (i <= 0) {
/*   71 */         return false;
/*      */       }
/*   73 */       return (paramString.startsWith(".ttf", i)) || (paramString.startsWith(".TTF", i)) || (paramString.startsWith(".ttc", i)) || (paramString.startsWith(".TTC", i)) || (paramString.startsWith(".otf", i)) || (paramString.startsWith(".OTF", i));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TTorT1Filter
/*      */     implements FilenameFilter
/*      */   {
/*      */     public boolean accept(File paramFile, String paramString)
/*      */     {
/*  105 */       int i = paramString.length() - 4;
/*  106 */       if (i <= 0) {
/*  107 */         return false;
/*      */       }
/*  109 */       int j = (paramString.startsWith(".ttf", i)) || (paramString.startsWith(".TTF", i)) || (paramString.startsWith(".ttc", i)) || (paramString.startsWith(".TTC", i)) || (paramString.startsWith(".otf", i)) || (paramString.startsWith(".OTF", i)) ? 1 : 0;
/*      */ 
/*  116 */       if (j != 0)
/*  117 */         return true;
/*  118 */       if (SunFontManager.noType1Font) {
/*  119 */         return false;
/*      */       }
/*  121 */       return (paramString.startsWith(".pfa", i)) || (paramString.startsWith(".pfb", i)) || (paramString.startsWith(".PFA", i)) || (paramString.startsWith(".PFB", i));
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.SunFontManager
 * JD-Core Version:    0.6.2
 */