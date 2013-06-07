/*      */ package java.awt;
/*      */ 
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.LineMetrics;
/*      */ import java.awt.font.TextAttribute;
/*      */ import java.awt.font.TextLayout;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Point2D.Float;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.awt.peer.FontPeer;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.AttributedCharacterIterator.Attribute;
/*      */ import java.text.CharacterIterator;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import sun.font.AttributeMap;
/*      */ import sun.font.AttributeValues;
/*      */ import sun.font.CompositeFont;
/*      */ import sun.font.CoreMetrics;
/*      */ import sun.font.CreatedFontTracker;
/*      */ import sun.font.EAttribute;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.Font2DHandle;
/*      */ import sun.font.FontAccess;
/*      */ import sun.font.FontLineMetrics;
/*      */ import sun.font.FontManager;
/*      */ import sun.font.FontManagerFactory;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.font.GlyphLayout;
/*      */ import sun.font.StandardGlyphVector;
/*      */ 
/*      */ public class Font
/*      */   implements Serializable
/*      */ {
/*      */   private Hashtable fRequestedAttributes;
/*      */   public static final String DIALOG = "Dialog";
/*      */   public static final String DIALOG_INPUT = "DialogInput";
/*      */   public static final String SANS_SERIF = "SansSerif";
/*      */   public static final String SERIF = "Serif";
/*      */   public static final String MONOSPACED = "Monospaced";
/*      */   public static final int PLAIN = 0;
/*      */   public static final int BOLD = 1;
/*      */   public static final int ITALIC = 2;
/*      */   public static final int ROMAN_BASELINE = 0;
/*      */   public static final int CENTER_BASELINE = 1;
/*      */   public static final int HANGING_BASELINE = 2;
/*      */   public static final int TRUETYPE_FONT = 0;
/*      */   public static final int TYPE1_FONT = 1;
/*      */   protected String name;
/*      */   protected int style;
/*      */   protected int size;
/*      */   protected float pointSize;
/*      */   private transient FontPeer peer;
/*      */   private transient long pData;
/*      */   private transient Font2DHandle font2DHandle;
/*      */   private transient AttributeValues values;
/*      */   private transient boolean hasLayoutAttributes;
/*  416 */   private transient boolean createdFont = false;
/*      */   private transient boolean nonIdentityTx;
/*  429 */   private static final AffineTransform identityTx = new AffineTransform();
/*      */   private static final long serialVersionUID = -4206021311591459213L;
/*  720 */   private static final int RECOGNIZED_MASK = AttributeValues.MASK_ALL & (AttributeValues.getMask(EAttribute.EFONT) ^ 0xFFFFFFFF);
/*      */ 
/*  726 */   private static final int PRIMARY_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.EFAMILY, EAttribute.EWEIGHT, EAttribute.EWIDTH, EAttribute.EPOSTURE, EAttribute.ESIZE, EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.ETRACKING });
/*      */ 
/*  733 */   private static final int SECONDARY_MASK = RECOGNIZED_MASK & (PRIMARY_MASK ^ 0xFFFFFFFF);
/*      */ 
/*  739 */   private static final int LAYOUT_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.ECHAR_REPLACEMENT, EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.EUNDERLINE, EAttribute.ESTRIKETHROUGH, EAttribute.ERUN_DIRECTION, EAttribute.EBIDI_EMBEDDING, EAttribute.EJUSTIFICATION, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE, EAttribute.ESWAP_COLORS, EAttribute.ENUMERIC_SHAPING, EAttribute.EKERNING, EAttribute.ELIGATURES, EAttribute.ETRACKING, EAttribute.ESUPERSCRIPT });
/*      */ 
/*  747 */   private static final int EXTRA_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.EWIDTH });
/*      */ 
/* 1122 */   private static final float[] ssinfo = { 0.0F, 0.375F, 0.625F, 0.7916667F, 0.9027778F, 0.9768519F, 1.026235F, 1.059156F };
/*      */   transient int hash;
/* 1669 */   private int fontSerializedDataVersion = 1;
/*      */   private transient SoftReference flmref;
/*      */   public static final int LAYOUT_LEFT_TO_RIGHT = 0;
/*      */   public static final int LAYOUT_RIGHT_TO_LEFT = 1;
/*      */   public static final int LAYOUT_NO_START_CONTEXT = 2;
/*      */   public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;
/*      */ 
/*      */   @Deprecated
/*      */   public FontPeer getPeer()
/*      */   {
/*  444 */     return getPeer_NoClientCode();
/*      */   }
/*      */ 
/*      */   final FontPeer getPeer_NoClientCode()
/*      */   {
/*  451 */     if (this.peer == null) {
/*  452 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  453 */       this.peer = localToolkit.getFontPeer(this.name, this.style);
/*      */     }
/*  455 */     return this.peer;
/*      */   }
/*      */ 
/*      */   private AttributeValues getAttributeValues()
/*      */   {
/*  470 */     if (this.values == null) {
/*  471 */       AttributeValues localAttributeValues = new AttributeValues();
/*  472 */       localAttributeValues.setFamily(this.name);
/*  473 */       localAttributeValues.setSize(this.pointSize);
/*      */ 
/*  475 */       if ((this.style & 0x1) != 0) {
/*  476 */         localAttributeValues.setWeight(2.0F);
/*      */       }
/*      */ 
/*  479 */       if ((this.style & 0x2) != 0) {
/*  480 */         localAttributeValues.setPosture(0.2F);
/*      */       }
/*  482 */       localAttributeValues.defineAll(PRIMARY_MASK);
/*  483 */       this.values = localAttributeValues;
/*      */     }
/*      */ 
/*  486 */     return this.values;
/*      */   }
/*      */ 
/*      */   private Font2D getFont2D() {
/*  490 */     FontManager localFontManager = FontManagerFactory.getInstance();
/*  491 */     if ((localFontManager.usingPerAppContextComposites()) && (this.font2DHandle != null) && ((this.font2DHandle.font2D instanceof CompositeFont)) && (((CompositeFont)this.font2DHandle.font2D).isStdComposite()))
/*      */     {
/*  495 */       return localFontManager.findFont2D(this.name, this.style, 2);
/*      */     }
/*  497 */     if (this.font2DHandle == null) {
/*  498 */       this.font2DHandle = localFontManager.findFont2D(this.name, this.style, 2).handle;
/*      */     }
/*      */ 
/*  506 */     return this.font2DHandle.font2D;
/*      */   }
/*      */ 
/*      */   public Font(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  567 */     this.name = (paramString != null ? paramString : "Default");
/*  568 */     this.style = ((paramInt1 & 0xFFFFFFFC) == 0 ? paramInt1 : 0);
/*  569 */     this.size = paramInt2;
/*  570 */     this.pointSize = paramInt2;
/*      */   }
/*      */ 
/*      */   private Font(String paramString, int paramInt, float paramFloat) {
/*  574 */     this.name = (paramString != null ? paramString : "Default");
/*  575 */     this.style = ((paramInt & 0xFFFFFFFC) == 0 ? paramInt : 0);
/*  576 */     this.size = ((int)(paramFloat + 0.5D));
/*  577 */     this.pointSize = paramFloat;
/*      */   }
/*      */ 
/*      */   private Font(String paramString, int paramInt, float paramFloat, boolean paramBoolean, Font2DHandle paramFont2DHandle)
/*      */   {
/*  583 */     this(paramString, paramInt, paramFloat);
/*  584 */     this.createdFont = paramBoolean;
/*      */ 
/*  594 */     if (paramBoolean)
/*  595 */       if (((paramFont2DHandle.font2D instanceof CompositeFont)) && (paramFont2DHandle.font2D.getStyle() != paramInt))
/*      */       {
/*  597 */         FontManager localFontManager = FontManagerFactory.getInstance();
/*  598 */         this.font2DHandle = localFontManager.getNewComposite(null, paramInt, paramFont2DHandle);
/*      */       } else {
/*  600 */         this.font2DHandle = paramFont2DHandle;
/*      */       }
/*      */   }
/*      */ 
/*      */   private Font(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker)
/*      */     throws FontFormatException
/*      */   {
/*  609 */     this.createdFont = true;
/*      */ 
/*  613 */     FontManager localFontManager = FontManagerFactory.getInstance();
/*  614 */     this.font2DHandle = localFontManager.createFont2D(paramFile, paramInt, paramBoolean, paramCreatedFontTracker).handle;
/*      */ 
/*  616 */     this.name = this.font2DHandle.font2D.getFontName(Locale.getDefault());
/*  617 */     this.style = 0;
/*  618 */     this.size = 1;
/*  619 */     this.pointSize = 1.0F;
/*      */   }
/*      */ 
/*      */   private Font(AttributeValues paramAttributeValues, String paramString, int paramInt, boolean paramBoolean, Font2DHandle paramFont2DHandle)
/*      */   {
/*  648 */     this.createdFont = paramBoolean;
/*  649 */     if (paramBoolean) {
/*  650 */       this.font2DHandle = paramFont2DHandle;
/*      */ 
/*  652 */       String str = null;
/*  653 */       if (paramString != null) {
/*  654 */         str = paramAttributeValues.getFamily();
/*  655 */         if (paramString.equals(str)) str = null;
/*      */       }
/*  657 */       int i = 0;
/*  658 */       if (paramInt == -1) {
/*  659 */         i = -1;
/*      */       } else {
/*  661 */         if (paramAttributeValues.getWeight() >= 2.0F) i = 1;
/*  662 */         if (paramAttributeValues.getPosture() >= 0.2F) i |= 2;
/*  663 */         if (paramInt == i) i = -1;
/*      */       }
/*  665 */       if ((paramFont2DHandle.font2D instanceof CompositeFont)) {
/*  666 */         if ((i != -1) || (str != null)) {
/*  667 */           FontManager localFontManager = FontManagerFactory.getInstance();
/*  668 */           this.font2DHandle = localFontManager.getNewComposite(str, i, paramFont2DHandle);
/*      */         }
/*      */       }
/*  671 */       else if (str != null) {
/*  672 */         this.createdFont = false;
/*  673 */         this.font2DHandle = null;
/*      */       }
/*      */     }
/*  676 */     initFromValues(paramAttributeValues);
/*      */   }
/*      */ 
/*      */   public Font(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
/*      */   {
/*  694 */     initFromValues(AttributeValues.fromMap(paramMap, RECOGNIZED_MASK));
/*      */   }
/*      */ 
/*      */   protected Font(Font paramFont)
/*      */   {
/*  705 */     if (paramFont.values != null) {
/*  706 */       initFromValues(paramFont.getAttributeValues().clone());
/*      */     } else {
/*  708 */       this.name = paramFont.name;
/*  709 */       this.style = paramFont.style;
/*  710 */       this.size = paramFont.size;
/*  711 */       this.pointSize = paramFont.pointSize;
/*      */     }
/*  713 */     this.font2DHandle = paramFont.font2DHandle;
/*  714 */     this.createdFont = paramFont.createdFont;
/*      */   }
/*      */ 
/*      */   private void initFromValues(AttributeValues paramAttributeValues)
/*      */   {
/*  754 */     this.values = paramAttributeValues;
/*  755 */     paramAttributeValues.defineAll(PRIMARY_MASK);
/*      */ 
/*  757 */     this.name = paramAttributeValues.getFamily();
/*  758 */     this.pointSize = paramAttributeValues.getSize();
/*  759 */     this.size = ((int)(paramAttributeValues.getSize() + 0.5D));
/*  760 */     if (paramAttributeValues.getWeight() >= 2.0F) this.style |= 1;
/*  761 */     if (paramAttributeValues.getPosture() >= 0.2F) this.style |= 2;
/*      */ 
/*  763 */     this.nonIdentityTx = paramAttributeValues.anyNonDefault(EXTRA_MASK);
/*  764 */     this.hasLayoutAttributes = paramAttributeValues.anyNonDefault(LAYOUT_MASK);
/*      */   }
/*      */ 
/*      */   public static Font getFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
/*      */   {
/*      */     Object localObject2;
/*  789 */     if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null))
/*      */     {
/*  791 */       localObject1 = ((AttributeMap)paramMap).getValues();
/*  792 */       if (((AttributeValues)localObject1).isNonDefault(EAttribute.EFONT)) {
/*  793 */         localObject2 = ((AttributeValues)localObject1).getFont();
/*  794 */         if (!((AttributeValues)localObject1).anyDefined(SECONDARY_MASK)) {
/*  795 */           return localObject2;
/*      */         }
/*      */ 
/*  798 */         localObject1 = ((Font)localObject2).getAttributeValues().clone();
/*  799 */         ((AttributeValues)localObject1).merge(paramMap, SECONDARY_MASK);
/*  800 */         return new Font((AttributeValues)localObject1, ((Font)localObject2).name, ((Font)localObject2).style, ((Font)localObject2).createdFont, ((Font)localObject2).font2DHandle);
/*      */       }
/*      */ 
/*  803 */       return new Font(paramMap);
/*      */     }
/*      */ 
/*  806 */     Object localObject1 = (Font)paramMap.get(TextAttribute.FONT);
/*  807 */     if (localObject1 != null) {
/*  808 */       if (paramMap.size() > 1) {
/*  809 */         localObject2 = ((Font)localObject1).getAttributeValues().clone();
/*  810 */         ((AttributeValues)localObject2).merge(paramMap, SECONDARY_MASK);
/*  811 */         return new Font((AttributeValues)localObject2, ((Font)localObject1).name, ((Font)localObject1).style, ((Font)localObject1).createdFont, ((Font)localObject1).font2DHandle);
/*      */       }
/*      */ 
/*  815 */       return localObject1;
/*      */     }
/*      */ 
/*  818 */     return new Font(paramMap);
/*      */   }
/*      */ 
/*      */   private static boolean hasTempPermission()
/*      */   {
/*  828 */     if (System.getSecurityManager() == null) {
/*  829 */       return true;
/*      */     }
/*  831 */     File localFile = null;
/*  832 */     boolean bool = false;
/*      */     try {
/*  834 */       localFile = Files.createTempFile("+~JT", ".tmp", new FileAttribute[0]).toFile();
/*  835 */       localFile.delete();
/*  836 */       localFile = null;
/*  837 */       bool = true;
/*      */     }
/*      */     catch (Throwable localThrowable) {
/*      */     }
/*  841 */     return bool;
/*      */   }
/*      */ 
/*      */   public static Font createFont(int paramInt, InputStream paramInputStream)
/*      */     throws FontFormatException, IOException
/*      */   {
/*  875 */     if ((paramInt != 0) && (paramInt != 1))
/*      */     {
/*  877 */       throw new IllegalArgumentException("font format not recognized");
/*      */     }
/*  879 */     int i = 0;
/*      */     try {
/*  881 */       File localFile = (File)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public File run() throws IOException {
/*  884 */           return Files.createTempFile("+~JF", ".tmp", new FileAttribute[0]).toFile();
/*      */         }
/*      */       });
/*  889 */       int j = 0;
/*  890 */       CreatedFontTracker localCreatedFontTracker = null;
/*      */       try {
/*  892 */         OutputStream localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public OutputStream run() throws IOException
/*      */           {
/*  896 */             return new FileOutputStream(this.val$tFile);
/*      */           }
/*      */         });
/*  900 */         if (!hasTempPermission())
/*  901 */           localCreatedFontTracker = CreatedFontTracker.getTracker();
/*      */         try
/*      */         {
/*  904 */           localObject1 = new byte[8192];
/*      */           while (true) {
/*  906 */             int k = paramInputStream.read((byte[])localObject1);
/*  907 */             if (k < 0) {
/*      */               break;
/*      */             }
/*  910 */             if (localCreatedFontTracker != null) {
/*  911 */               if (j + k > 33554432) {
/*  912 */                 throw new IOException("File too big.");
/*      */               }
/*  914 */               if (j + localCreatedFontTracker.getNumBytes() > 335544320)
/*      */               {
/*  917 */                 throw new IOException("Total files too big.");
/*      */               }
/*  919 */               j += k;
/*  920 */               localCreatedFontTracker.addBytes(k);
/*      */             }
/*  922 */             localOutputStream.write((byte[])localObject1, 0, k);
/*      */           }
/*      */         }
/*      */         finally {
/*  926 */           localOutputStream.close();
/*      */         }
/*      */ 
/*  938 */         i = 1;
/*  939 */         Object localObject1 = new Font(localFile, paramInt, true, localCreatedFontTracker);
/*  940 */         return localObject1;
/*      */       } finally {
/*  942 */         if (i == 0) {
/*  943 */           if (localCreatedFontTracker != null) {
/*  944 */             localCreatedFontTracker.subBytes(j);
/*      */           }
/*  946 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public Void run() {
/*  949 */               this.val$tFile.delete();
/*  950 */               return null;
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable localThrowable1) {
/*  957 */       if ((localThrowable1 instanceof FontFormatException)) {
/*  958 */         throw ((FontFormatException)localThrowable1);
/*      */       }
/*  960 */       if ((localThrowable1 instanceof IOException)) {
/*  961 */         throw ((IOException)localThrowable1);
/*      */       }
/*  963 */       Throwable localThrowable2 = localThrowable1.getCause();
/*  964 */       if ((localThrowable2 instanceof FontFormatException))
/*  965 */         throw ((FontFormatException)localThrowable2);
/*      */     }
/*  967 */     throw new IOException("Problem reading font data.");
/*      */   }
/*      */ 
/*      */   public static Font createFont(int paramInt, File paramFile)
/*      */     throws FontFormatException, IOException
/*      */   {
/* 1008 */     paramFile = new File(paramFile.getPath());
/*      */ 
/* 1010 */     if ((paramInt != 0) && (paramInt != 1))
/*      */     {
/* 1012 */       throw new IllegalArgumentException("font format not recognized");
/*      */     }
/* 1014 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1015 */     if (localSecurityManager != null) {
/* 1016 */       FilePermission localFilePermission = new FilePermission(paramFile.getPath(), "read");
/*      */ 
/* 1018 */       localSecurityManager.checkPermission(localFilePermission);
/*      */     }
/* 1020 */     if (!paramFile.canRead()) {
/* 1021 */       throw new IOException("Can't read " + paramFile);
/*      */     }
/* 1023 */     return new Font(paramFile, paramInt, false, null);
/*      */   }
/*      */ 
/*      */   public AffineTransform getTransform()
/*      */   {
/* 1054 */     if (this.nonIdentityTx) {
/* 1055 */       AttributeValues localAttributeValues = getAttributeValues();
/*      */ 
/* 1057 */       AffineTransform localAffineTransform = localAttributeValues.isNonDefault(EAttribute.ETRANSFORM) ? new AffineTransform(localAttributeValues.getTransform()) : new AffineTransform();
/*      */ 
/* 1061 */       if (localAttributeValues.getSuperscript() != 0)
/*      */       {
/* 1066 */         int i = localAttributeValues.getSuperscript();
/*      */ 
/* 1068 */         double d1 = 0.0D;
/* 1069 */         int j = 0;
/* 1070 */         int k = i > 0 ? 1 : 0;
/* 1071 */         int m = k != 0 ? -1 : 1;
/* 1072 */         int n = k != 0 ? i : -i;
/*      */ 
/* 1074 */         while ((n & 0x7) > j) {
/* 1075 */           int i1 = n & 0x7;
/* 1076 */           d1 += m * (ssinfo[i1] - ssinfo[j]);
/* 1077 */           n >>= 3;
/* 1078 */           m = -m;
/* 1079 */           j = i1;
/*      */         }
/* 1081 */         d1 *= this.pointSize;
/* 1082 */         double d2 = Math.pow(0.6666666666666666D, j);
/*      */ 
/* 1084 */         localAffineTransform.preConcatenate(AffineTransform.getTranslateInstance(0.0D, d1));
/* 1085 */         localAffineTransform.scale(d2, d2);
/*      */       }
/*      */ 
/* 1101 */       if (localAttributeValues.isNonDefault(EAttribute.EWIDTH)) {
/* 1102 */         localAffineTransform.scale(localAttributeValues.getWidth(), 1.0D);
/*      */       }
/*      */ 
/* 1105 */       return localAffineTransform;
/*      */     }
/*      */ 
/* 1108 */     return new AffineTransform();
/*      */   }
/*      */ 
/*      */   public String getFamily()
/*      */   {
/* 1153 */     return getFamily_NoClientCode();
/*      */   }
/*      */ 
/*      */   final String getFamily_NoClientCode()
/*      */   {
/* 1161 */     return getFamily(Locale.getDefault());
/*      */   }
/*      */ 
/*      */   public String getFamily(Locale paramLocale)
/*      */   {
/* 1184 */     if (paramLocale == null) {
/* 1185 */       throw new NullPointerException("null locale doesn't mean default");
/*      */     }
/* 1187 */     return getFont2D().getFamilyName(paramLocale);
/*      */   }
/*      */ 
/*      */   public String getPSName()
/*      */   {
/* 1199 */     return getFont2D().getPostscriptName();
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/* 1213 */     return this.name;
/*      */   }
/*      */ 
/*      */   public String getFontName()
/*      */   {
/* 1228 */     return getFontName(Locale.getDefault());
/*      */   }
/*      */ 
/*      */   public String getFontName(Locale paramLocale)
/*      */   {
/* 1243 */     if (paramLocale == null) {
/* 1244 */       throw new NullPointerException("null locale doesn't mean default");
/*      */     }
/* 1246 */     return getFont2D().getFontName(paramLocale);
/*      */   }
/*      */ 
/*      */   public int getStyle()
/*      */   {
/* 1259 */     return this.style;
/*      */   }
/*      */ 
/*      */   public int getSize()
/*      */   {
/* 1285 */     return this.size;
/*      */   }
/*      */ 
/*      */   public float getSize2D()
/*      */   {
/* 1297 */     return this.pointSize;
/*      */   }
/*      */ 
/*      */   public boolean isPlain()
/*      */   {
/* 1310 */     return this.style == 0;
/*      */   }
/*      */ 
/*      */   public boolean isBold()
/*      */   {
/* 1323 */     return (this.style & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isItalic()
/*      */   {
/* 1336 */     return (this.style & 0x2) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isTransformed()
/*      */   {
/* 1350 */     return this.nonIdentityTx;
/*      */   }
/*      */ 
/*      */   public boolean hasLayoutAttributes()
/*      */   {
/* 1360 */     return this.hasLayoutAttributes;
/*      */   }
/*      */ 
/*      */   public static Font getFont(String paramString)
/*      */   {
/* 1380 */     return getFont(paramString, null);
/*      */   }
/*      */ 
/*      */   public static Font decode(String paramString)
/*      */   {
/* 1457 */     String str1 = paramString;
/* 1458 */     String str2 = "";
/* 1459 */     int i = 12;
/* 1460 */     int j = 0;
/*      */ 
/* 1462 */     if (paramString == null) {
/* 1463 */       return new Font("Dialog", j, i);
/*      */     }
/*      */ 
/* 1466 */     int k = paramString.lastIndexOf('-');
/* 1467 */     int m = paramString.lastIndexOf(' ');
/* 1468 */     int n = k > m ? 45 : 32;
/* 1469 */     NumberFormatException localNumberFormatException1 = paramString.lastIndexOf(n);
/* 1470 */     NumberFormatException localNumberFormatException2 = paramString.lastIndexOf(n, localNumberFormatException1 - 1);
/* 1471 */     NumberFormatException localNumberFormatException3 = paramString.length();
/*      */ 
/* 1473 */     if ((localNumberFormatException1 > 0) && (localNumberFormatException1 + 1 < localNumberFormatException3)) {
/*      */       try {
/* 1475 */         i = Integer.valueOf(paramString.substring(localNumberFormatException1 + 1)).intValue();
/*      */ 
/* 1477 */         if (i <= 0) {
/* 1478 */           i = 12;
/*      */         }
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException4)
/*      */       {
/* 1483 */         localNumberFormatException2 = localNumberFormatException1;
/* 1484 */         localNumberFormatException1 = localNumberFormatException3;
/* 1485 */         if (paramString.charAt(localNumberFormatException1 - 1) == n) {
/* 1486 */           localNumberFormatException1--;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1491 */     if ((localNumberFormatException2 >= 0) && (localNumberFormatException2 + 1 < localNumberFormatException3)) {
/* 1492 */       str2 = paramString.substring(localNumberFormatException2 + 1, localNumberFormatException1);
/* 1493 */       str2 = str2.toLowerCase(Locale.ENGLISH);
/* 1494 */       if (str2.equals("bolditalic")) {
/* 1495 */         j = 3;
/* 1496 */       } else if (str2.equals("italic")) {
/* 1497 */         j = 2;
/* 1498 */       } else if (str2.equals("bold")) {
/* 1499 */         j = 1;
/* 1500 */       } else if (str2.equals("plain")) {
/* 1501 */         j = 0;
/*      */       }
/*      */       else
/*      */       {
/* 1506 */         localNumberFormatException2 = localNumberFormatException1;
/* 1507 */         if (paramString.charAt(localNumberFormatException2 - 1) == n) {
/* 1508 */           localNumberFormatException2--;
/*      */         }
/*      */       }
/* 1511 */       str1 = paramString.substring(0, localNumberFormatException2);
/*      */     }
/*      */     else {
/* 1514 */       localNumberFormatException4 = localNumberFormatException3;
/* 1515 */       if (localNumberFormatException2 > 0)
/* 1516 */         localNumberFormatException4 = localNumberFormatException2;
/* 1517 */       else if (localNumberFormatException1 > 0) {
/* 1518 */         localNumberFormatException4 = localNumberFormatException1;
/*      */       }
/* 1520 */       if ((localNumberFormatException4 > 0) && (paramString.charAt(localNumberFormatException4 - 1) == n)) {
/* 1521 */         localNumberFormatException4--;
/*      */       }
/* 1523 */       str1 = paramString.substring(0, localNumberFormatException4);
/*      */     }
/*      */ 
/* 1526 */     return new Font(str1, j, i);
/*      */   }
/*      */ 
/*      */   public static Font getFont(String paramString, Font paramFont)
/*      */   {
/* 1550 */     String str = null;
/*      */     try {
/* 1552 */       str = System.getProperty(paramString);
/*      */     } catch (SecurityException localSecurityException) {
/*      */     }
/* 1555 */     if (str == null) {
/* 1556 */       return paramFont;
/*      */     }
/* 1558 */     return decode(str);
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1568 */     if (this.hash == 0) {
/* 1569 */       this.hash = (this.name.hashCode() ^ this.style ^ this.size);
/*      */ 
/* 1576 */       if ((this.nonIdentityTx) && (this.values != null) && (this.values.getTransform() != null))
/*      */       {
/* 1578 */         this.hash ^= this.values.getTransform().hashCode();
/*      */       }
/*      */     }
/* 1581 */     return this.hash;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/* 1595 */     if (paramObject == this) {
/* 1596 */       return true;
/*      */     }
/*      */ 
/* 1599 */     if (paramObject != null) {
/*      */       try {
/* 1601 */         Font localFont = (Font)paramObject;
/* 1602 */         if ((this.size == localFont.size) && (this.style == localFont.style) && (this.nonIdentityTx == localFont.nonIdentityTx) && (this.hasLayoutAttributes == localFont.hasLayoutAttributes) && (this.pointSize == localFont.pointSize) && (this.name.equals(localFont.name)))
/*      */         {
/* 1615 */           if (this.values == null) {
/* 1616 */             if (localFont.values == null) {
/* 1617 */               return true;
/*      */             }
/* 1619 */             return getAttributeValues().equals(localFont.values);
/*      */           }
/*      */ 
/* 1622 */           return this.values.equals(localFont.getAttributeValues());
/*      */         }
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/*      */       }
/*      */     }
/* 1629 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*      */     String str;
/* 1644 */     if (isBold())
/* 1645 */       str = isItalic() ? "bolditalic" : "bold";
/*      */     else {
/* 1647 */       str = isItalic() ? "italic" : "plain";
/*      */     }
/*      */ 
/* 1650 */     return getClass().getName() + "[family=" + getFamily() + ",name=" + this.name + ",style=" + str + ",size=" + this.size + "]";
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws ClassNotFoundException, IOException
/*      */   {
/* 1682 */     if (this.values != null)
/* 1683 */       synchronized (this.values)
/*      */       {
/* 1685 */         this.fRequestedAttributes = this.values.toSerializableHashtable();
/* 1686 */         paramObjectOutputStream.defaultWriteObject();
/* 1687 */         this.fRequestedAttributes = null;
/*      */       }
/*      */     else
/* 1690 */       paramObjectOutputStream.defaultWriteObject();
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws ClassNotFoundException, IOException
/*      */   {
/* 1706 */     paramObjectInputStream.defaultReadObject();
/* 1707 */     if (this.pointSize == 0.0F) {
/* 1708 */       this.pointSize = this.size;
/*      */     }
/*      */ 
/* 1719 */     if (this.fRequestedAttributes != null) {
/* 1720 */       this.values = getAttributeValues();
/* 1721 */       AttributeValues localAttributeValues = AttributeValues.fromSerializableHashtable(this.fRequestedAttributes);
/*      */ 
/* 1723 */       if (!AttributeValues.is16Hashtable(this.fRequestedAttributes)) {
/* 1724 */         localAttributeValues.unsetDefault();
/*      */       }
/* 1726 */       this.values = getAttributeValues().merge(localAttributeValues);
/* 1727 */       this.nonIdentityTx = this.values.anyNonDefault(EXTRA_MASK);
/* 1728 */       this.hasLayoutAttributes = this.values.anyNonDefault(LAYOUT_MASK);
/*      */ 
/* 1730 */       this.fRequestedAttributes = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getNumGlyphs()
/*      */   {
/* 1742 */     return getFont2D().getNumGlyphs();
/*      */   }
/*      */ 
/*      */   public int getMissingGlyphCode()
/*      */   {
/* 1752 */     return getFont2D().getMissingGlyphCode();
/*      */   }
/*      */ 
/*      */   public byte getBaselineFor(char paramChar)
/*      */   {
/* 1772 */     return getFont2D().getBaselineFor(paramChar);
/*      */   }
/*      */ 
/*      */   public Map<TextAttribute, ?> getAttributes()
/*      */   {
/* 1782 */     return new AttributeMap(getAttributeValues());
/*      */   }
/*      */ 
/*      */   public AttributedCharacterIterator.Attribute[] getAvailableAttributes()
/*      */   {
/* 1796 */     AttributedCharacterIterator.Attribute[] arrayOfAttribute = { TextAttribute.FAMILY, TextAttribute.WEIGHT, TextAttribute.WIDTH, TextAttribute.POSTURE, TextAttribute.SIZE, TextAttribute.TRANSFORM, TextAttribute.SUPERSCRIPT, TextAttribute.CHAR_REPLACEMENT, TextAttribute.FOREGROUND, TextAttribute.BACKGROUND, TextAttribute.UNDERLINE, TextAttribute.STRIKETHROUGH, TextAttribute.RUN_DIRECTION, TextAttribute.BIDI_EMBEDDING, TextAttribute.JUSTIFICATION, TextAttribute.INPUT_METHOD_HIGHLIGHT, TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.SWAP_COLORS, TextAttribute.NUMERIC_SHAPING, TextAttribute.KERNING, TextAttribute.LIGATURES, TextAttribute.TRACKING };
/*      */ 
/* 1821 */     return arrayOfAttribute;
/*      */   }
/*      */ 
/*      */   public Font deriveFont(int paramInt, float paramFloat)
/*      */   {
/* 1833 */     if (this.values == null) {
/* 1834 */       return new Font(this.name, paramInt, paramFloat, this.createdFont, this.font2DHandle);
/*      */     }
/* 1836 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1837 */     int i = this.style != paramInt ? this.style : -1;
/* 1838 */     applyStyle(paramInt, localAttributeValues);
/* 1839 */     localAttributeValues.setSize(paramFloat);
/* 1840 */     return new Font(localAttributeValues, null, i, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public Font deriveFont(int paramInt, AffineTransform paramAffineTransform)
/*      */   {
/* 1855 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1856 */     int i = this.style != paramInt ? this.style : -1;
/* 1857 */     applyStyle(paramInt, localAttributeValues);
/* 1858 */     applyTransform(paramAffineTransform, localAttributeValues);
/* 1859 */     return new Font(localAttributeValues, null, i, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public Font deriveFont(float paramFloat)
/*      */   {
/* 1870 */     if (this.values == null) {
/* 1871 */       return new Font(this.name, this.style, paramFloat, this.createdFont, this.font2DHandle);
/*      */     }
/* 1873 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1874 */     localAttributeValues.setSize(paramFloat);
/* 1875 */     return new Font(localAttributeValues, null, -1, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public Font deriveFont(AffineTransform paramAffineTransform)
/*      */   {
/* 1889 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1890 */     applyTransform(paramAffineTransform, localAttributeValues);
/* 1891 */     return new Font(localAttributeValues, null, -1, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public Font deriveFont(int paramInt)
/*      */   {
/* 1902 */     if (this.values == null) {
/* 1903 */       return new Font(this.name, paramInt, this.size, this.createdFont, this.font2DHandle);
/*      */     }
/* 1905 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1906 */     int i = this.style != paramInt ? this.style : -1;
/* 1907 */     applyStyle(paramInt, localAttributeValues);
/* 1908 */     return new Font(localAttributeValues, null, i, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
/*      */   {
/* 1922 */     if (paramMap == null) {
/* 1923 */       return this;
/*      */     }
/* 1925 */     AttributeValues localAttributeValues = getAttributeValues().clone();
/* 1926 */     localAttributeValues.merge(paramMap, RECOGNIZED_MASK);
/*      */ 
/* 1928 */     return new Font(localAttributeValues, this.name, this.style, this.createdFont, this.font2DHandle);
/*      */   }
/*      */ 
/*      */   public boolean canDisplay(char paramChar)
/*      */   {
/* 1947 */     return getFont2D().canDisplay(paramChar);
/*      */   }
/*      */ 
/*      */   public boolean canDisplay(int paramInt)
/*      */   {
/* 1964 */     if (!Character.isValidCodePoint(paramInt)) {
/* 1965 */       throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(paramInt));
/*      */     }
/*      */ 
/* 1968 */     return getFont2D().canDisplay(paramInt);
/*      */   }
/*      */ 
/*      */   public int canDisplayUpTo(String paramString)
/*      */   {
/* 1989 */     Font2D localFont2D = getFont2D();
/* 1990 */     int i = paramString.length();
/* 1991 */     for (int j = 0; j < i; j++) {
/* 1992 */       char c = paramString.charAt(j);
/* 1993 */       if (!localFont2D.canDisplay(c))
/*      */       {
/* 1996 */         if (!Character.isHighSurrogate(c)) {
/* 1997 */           return j;
/*      */         }
/* 1999 */         if (!localFont2D.canDisplay(paramString.codePointAt(j))) {
/* 2000 */           return j;
/*      */         }
/* 2002 */         j++;
/*      */       }
/*      */     }
/* 2004 */     return -1;
/*      */   }
/*      */ 
/*      */   public int canDisplayUpTo(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */   {
/* 2027 */     Font2D localFont2D = getFont2D();
/* 2028 */     for (int i = paramInt1; i < paramInt2; i++) {
/* 2029 */       char c = paramArrayOfChar[i];
/* 2030 */       if (!localFont2D.canDisplay(c))
/*      */       {
/* 2033 */         if (!Character.isHighSurrogate(c)) {
/* 2034 */           return i;
/*      */         }
/* 2036 */         if (!localFont2D.canDisplay(Character.codePointAt(paramArrayOfChar, i, paramInt2))) {
/* 2037 */           return i;
/*      */         }
/* 2039 */         i++;
/*      */       }
/*      */     }
/* 2041 */     return -1;
/*      */   }
/*      */ 
/*      */   public int canDisplayUpTo(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2)
/*      */   {
/* 2062 */     Font2D localFont2D = getFont2D();
/* 2063 */     char c1 = paramCharacterIterator.setIndex(paramInt1);
/* 2064 */     for (int i = paramInt1; i < paramInt2; c1 = paramCharacterIterator.next()) {
/* 2065 */       if (!localFont2D.canDisplay(c1))
/*      */       {
/* 2068 */         if (!Character.isHighSurrogate(c1)) {
/* 2069 */           return i;
/*      */         }
/* 2071 */         char c2 = paramCharacterIterator.next();
/*      */ 
/* 2073 */         if (!Character.isLowSurrogate(c2)) {
/* 2074 */           return i;
/*      */         }
/* 2076 */         if (!localFont2D.canDisplay(Character.toCodePoint(c1, c2))) {
/* 2077 */           return i;
/*      */         }
/* 2079 */         i++;
/*      */       }
/* 2064 */       i++;
/*      */     }
/*      */ 
/* 2081 */     return -1;
/*      */   }
/*      */ 
/*      */   public float getItalicAngle()
/*      */   {
/* 2092 */     return getItalicAngle(null);
/*      */   }
/*      */ 
/*      */   private float getItalicAngle(FontRenderContext paramFontRenderContext)
/*      */   {
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2105 */     if (paramFontRenderContext == null) {
/* 2106 */       localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
/* 2107 */       localObject2 = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
/*      */     } else {
/* 2109 */       localObject1 = paramFontRenderContext.getAntiAliasingHint();
/* 2110 */       localObject2 = paramFontRenderContext.getFractionalMetricsHint();
/*      */     }
/* 2112 */     return getFont2D().getItalicAngle(this, identityTx, localObject1, localObject2);
/*      */   }
/*      */ 
/*      */   public boolean hasUniformLineMetrics()
/*      */   {
/* 2127 */     return false;
/*      */   }
/*      */ 
/*      */   private FontLineMetrics defaultLineMetrics(FontRenderContext paramFontRenderContext)
/*      */   {
/* 2132 */     FontLineMetrics localFontLineMetrics = null;
/* 2133 */     if ((this.flmref == null) || ((localFontLineMetrics = (FontLineMetrics)this.flmref.get()) == null) || (!localFontLineMetrics.frc.equals(paramFontRenderContext)))
/*      */     {
/* 2142 */       float[] arrayOfFloat1 = new float[8];
/* 2143 */       getFont2D().getFontMetrics(this, identityTx, paramFontRenderContext.getAntiAliasingHint(), paramFontRenderContext.getFractionalMetricsHint(), arrayOfFloat1);
/*      */ 
/* 2147 */       float f1 = arrayOfFloat1[0];
/* 2148 */       float f2 = arrayOfFloat1[1];
/* 2149 */       float f3 = arrayOfFloat1[2];
/* 2150 */       float f4 = 0.0F;
/* 2151 */       if ((this.values != null) && (this.values.getSuperscript() != 0)) {
/* 2152 */         f4 = (float)getTransform().getTranslateY();
/* 2153 */         f1 -= f4;
/* 2154 */         f2 += f4;
/*      */       }
/* 2156 */       float f5 = f1 + f2 + f3;
/*      */ 
/* 2158 */       int i = 0;
/*      */ 
/* 2160 */       float[] arrayOfFloat2 = { 0.0F, (f2 / 2.0F - f1) / 2.0F, -f1 };
/*      */ 
/* 2162 */       float f6 = arrayOfFloat1[4];
/* 2163 */       float f7 = arrayOfFloat1[5];
/*      */ 
/* 2165 */       float f8 = arrayOfFloat1[6];
/* 2166 */       float f9 = arrayOfFloat1[7];
/*      */ 
/* 2168 */       float f10 = getItalicAngle(paramFontRenderContext);
/*      */ 
/* 2170 */       if (isTransformed()) {
/* 2171 */         localObject = this.values.getCharTransform();
/* 2172 */         if (localObject != null) {
/* 2173 */           Point2D.Float localFloat = new Point2D.Float();
/* 2174 */           localFloat.setLocation(0.0F, f6);
/* 2175 */           ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
/* 2176 */           f6 = localFloat.y;
/* 2177 */           localFloat.setLocation(0.0F, f7);
/* 2178 */           ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
/* 2179 */           f7 = localFloat.y;
/* 2180 */           localFloat.setLocation(0.0F, f8);
/* 2181 */           ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
/* 2182 */           f8 = localFloat.y;
/* 2183 */           localFloat.setLocation(0.0F, f9);
/* 2184 */           ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
/* 2185 */           f9 = localFloat.y;
/*      */         }
/*      */       }
/* 2188 */       f6 += f4;
/* 2189 */       f8 += f4;
/*      */ 
/* 2191 */       Object localObject = new CoreMetrics(f1, f2, f3, f5, i, arrayOfFloat2, f6, f7, f8, f9, f4, f10);
/*      */ 
/* 2197 */       localFontLineMetrics = new FontLineMetrics(0, (CoreMetrics)localObject, paramFontRenderContext);
/* 2198 */       this.flmref = new SoftReference(localFontLineMetrics);
/*      */     }
/*      */ 
/* 2201 */     return (FontLineMetrics)localFontLineMetrics.clone();
/*      */   }
/*      */ 
/*      */   public LineMetrics getLineMetrics(String paramString, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2213 */     FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
/* 2214 */     localFontLineMetrics.numchars = paramString.length();
/* 2215 */     return localFontLineMetrics;
/*      */   }
/*      */ 
/*      */   public LineMetrics getLineMetrics(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2231 */     FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
/* 2232 */     int i = paramInt2 - paramInt1;
/* 2233 */     localFontLineMetrics.numchars = (i < 0 ? 0 : i);
/* 2234 */     return localFontLineMetrics;
/*      */   }
/*      */ 
/*      */   public LineMetrics getLineMetrics(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2250 */     FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
/* 2251 */     int i = paramInt2 - paramInt1;
/* 2252 */     localFontLineMetrics.numchars = (i < 0 ? 0 : i);
/* 2253 */     return localFontLineMetrics;
/*      */   }
/*      */ 
/*      */   public LineMetrics getLineMetrics(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2269 */     FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
/* 2270 */     int i = paramInt2 - paramInt1;
/* 2271 */     localFontLineMetrics.numchars = (i < 0 ? 0 : i);
/* 2272 */     return localFontLineMetrics;
/*      */   }
/*      */ 
/*      */   public Rectangle2D getStringBounds(String paramString, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2297 */     char[] arrayOfChar = paramString.toCharArray();
/* 2298 */     return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public Rectangle2D getStringBounds(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2331 */     String str = paramString.substring(paramInt1, paramInt2);
/* 2332 */     return getStringBounds(str, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public Rectangle2D getStringBounds(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2366 */     if (paramInt1 < 0) {
/* 2367 */       throw new IndexOutOfBoundsException("beginIndex: " + paramInt1);
/*      */     }
/* 2369 */     if (paramInt2 > paramArrayOfChar.length) {
/* 2370 */       throw new IndexOutOfBoundsException("limit: " + paramInt2);
/*      */     }
/* 2372 */     if (paramInt1 > paramInt2) {
/* 2373 */       throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1));
/*      */     }
/*      */ 
/* 2380 */     int i = (this.values == null) || ((this.values.getKerning() == 0) && (this.values.getLigatures() == 0) && (this.values.getBaselineTransform() == null)) ? 1 : 0;
/*      */ 
/* 2383 */     if (i != 0) {
/* 2384 */       i = !FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2) ? 1 : 0;
/*      */     }
/*      */ 
/* 2387 */     if (i != 0) {
/* 2388 */       localObject = new StandardGlyphVector(this, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramFontRenderContext);
/*      */ 
/* 2390 */       return ((GlyphVector)localObject).getLogicalBounds();
/*      */     }
/*      */ 
/* 2393 */     Object localObject = new String(paramArrayOfChar, paramInt1, paramInt2 - paramInt1);
/* 2394 */     TextLayout localTextLayout = new TextLayout((String)localObject, this, paramFontRenderContext);
/* 2395 */     return new Rectangle2D.Float(0.0F, -localTextLayout.getAscent(), localTextLayout.getAdvance(), localTextLayout.getAscent() + localTextLayout.getDescent() + localTextLayout.getLeading());
/*      */   }
/*      */ 
/*      */   public Rectangle2D getStringBounds(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/* 2433 */     int i = paramCharacterIterator.getBeginIndex();
/* 2434 */     int j = paramCharacterIterator.getEndIndex();
/*      */ 
/* 2436 */     if (paramInt1 < i) {
/* 2437 */       throw new IndexOutOfBoundsException("beginIndex: " + paramInt1);
/*      */     }
/* 2439 */     if (paramInt2 > j) {
/* 2440 */       throw new IndexOutOfBoundsException("limit: " + paramInt2);
/*      */     }
/* 2442 */     if (paramInt1 > paramInt2) {
/* 2443 */       throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1));
/*      */     }
/*      */ 
/* 2447 */     char[] arrayOfChar = new char[paramInt2 - paramInt1];
/*      */ 
/* 2449 */     paramCharacterIterator.setIndex(paramInt1);
/* 2450 */     for (int k = 0; k < arrayOfChar.length; k++) {
/* 2451 */       arrayOfChar[k] = paramCharacterIterator.current();
/* 2452 */       paramCharacterIterator.next();
/*      */     }
/*      */ 
/* 2455 */     return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public Rectangle2D getMaxCharBounds(FontRenderContext paramFontRenderContext)
/*      */   {
/* 2468 */     float[] arrayOfFloat = new float[4];
/*      */ 
/* 2470 */     getFont2D().getFontMetrics(this, paramFontRenderContext, arrayOfFloat);
/*      */ 
/* 2472 */     return new Rectangle2D.Float(0.0F, -arrayOfFloat[0], arrayOfFloat[3], arrayOfFloat[0] + arrayOfFloat[1] + arrayOfFloat[2]);
/*      */   }
/*      */ 
/*      */   public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, String paramString)
/*      */   {
/* 2493 */     return new StandardGlyphVector(this, paramString, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar)
/*      */   {
/* 2512 */     return new StandardGlyphVector(this, paramArrayOfChar, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, CharacterIterator paramCharacterIterator)
/*      */   {
/* 2532 */     return new StandardGlyphVector(this, paramCharacterIterator, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, int[] paramArrayOfInt)
/*      */   {
/* 2552 */     return new StandardGlyphVector(this, paramArrayOfInt, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public GlyphVector layoutGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 2603 */     GlyphLayout localGlyphLayout = GlyphLayout.get(null);
/* 2604 */     StandardGlyphVector localStandardGlyphVector = localGlyphLayout.layout(this, paramFontRenderContext, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramInt3, null);
/*      */ 
/* 2606 */     GlyphLayout.done(localGlyphLayout);
/* 2607 */     return localStandardGlyphVector;
/*      */   }
/*      */ 
/*      */   private static void applyTransform(AffineTransform paramAffineTransform, AttributeValues paramAttributeValues)
/*      */   {
/* 2636 */     if (paramAffineTransform == null) {
/* 2637 */       throw new IllegalArgumentException("transform must not be null");
/*      */     }
/* 2639 */     paramAttributeValues.setTransform(paramAffineTransform);
/*      */   }
/*      */ 
/*      */   private static void applyStyle(int paramInt, AttributeValues paramAttributeValues)
/*      */   {
/* 2644 */     paramAttributeValues.setWeight((paramInt & 0x1) != 0 ? 2.0F : 1.0F);
/*      */ 
/* 2646 */     paramAttributeValues.setPosture((paramInt & 0x2) != 0 ? 0.2F : 0.0F);
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   static
/*      */   {
/*  246 */     Toolkit.loadLibraries();
/*  247 */     initIDs();
/*  248 */     FontAccess.setFontAccess(new FontAccessImpl(null));
/*      */   }
/*      */ 
/*      */   private static class FontAccessImpl extends FontAccess
/*      */   {
/*      */     public Font2D getFont2D(Font paramFont)
/*      */     {
/*  228 */       return paramFont.getFont2D();
/*      */     }
/*      */ 
/*      */     public void setFont2D(Font paramFont, Font2DHandle paramFont2DHandle) {
/*  232 */       paramFont.font2DHandle = paramFont2DHandle;
/*      */     }
/*      */ 
/*      */     public void setCreatedFont(Font paramFont) {
/*  236 */       paramFont.createdFont = true;
/*      */     }
/*      */ 
/*      */     public boolean isCreatedFont(Font paramFont) {
/*  240 */       return paramFont.createdFont;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.Font
 * JD-Core Version:    0.6.2
 */