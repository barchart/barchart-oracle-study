/*     */ package sun.font;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ class XMap
/*     */ {
/*  40 */   private static HashMap xMappers = new HashMap();
/*     */   char[] convertedGlyphs;
/*     */   static final int SINGLE_BYTE = 1;
/*     */   static final int DOUBLE_BYTE = 2;
/*     */   private static final char SURR_MIN = 'í €';
/*     */   private static final char SURR_MAX = 'í¿¿';
/*     */ 
/*     */   static synchronized XMap getXMapper(String paramString)
/*     */   {
/*  52 */     XMap localXMap = (XMap)xMappers.get(paramString);
/*  53 */     if (localXMap == null) {
/*  54 */       localXMap = getXMapperInternal(paramString);
/*  55 */       xMappers.put(paramString, localXMap);
/*     */     }
/*  57 */     return localXMap;
/*     */   }
/*     */ 
/*     */   private static XMap getXMapperInternal(String paramString)
/*     */   {
/*  65 */     String str = null;
/*  66 */     int i = 1;
/*  67 */     int j = 65535;
/*  68 */     int k = 0;
/*  69 */     boolean bool1 = false;
/*  70 */     boolean bool2 = false;
/*  71 */     if (paramString.equals("dingbats")) {
/*  72 */       str = "sun.awt.motif.X11Dingbats";
/*  73 */       k = 9985;
/*  74 */       j = 10174;
/*  75 */     } else if (paramString.equals("symbol")) {
/*  76 */       str = "sun.awt.Symbol";
/*  77 */       k = 913;
/*  78 */       j = 8943;
/*  79 */     } else if (paramString.equals("iso8859-1")) {
/*  80 */       j = 255;
/*  81 */     } else if (paramString.equals("iso8859-2")) {
/*  82 */       str = "ISO8859_2";
/*  83 */     } else if (paramString.equals("jisx0208.1983-0")) {
/*  84 */       str = "sun.awt.motif.X11JIS0208";
/*  85 */       i = 2;
/*  86 */     } else if (paramString.equals("jisx0201.1976-0")) {
/*  87 */       str = "sun.awt.motif.X11JIS0201";
/*     */ 
/*  94 */       bool1 = true;
/*  95 */       bool2 = true;
/*  96 */     } else if (paramString.equals("jisx0212.1990-0")) {
/*  97 */       str = "sun.awt.motif.X11JIS0212";
/*  98 */       i = 2;
/*  99 */     } else if (paramString.equals("iso8859-4")) {
/* 100 */       str = "ISO8859_4";
/* 101 */     } else if (paramString.equals("iso8859-5")) {
/* 102 */       str = "ISO8859_5";
/* 103 */     } else if (paramString.equals("koi8-r")) {
/* 104 */       str = "KOI8_R";
/* 105 */     } else if (paramString.equals("ansi-1251")) {
/* 106 */       str = "windows-1251";
/* 107 */     } else if (paramString.equals("iso8859-6")) {
/* 108 */       str = "ISO8859_6";
/* 109 */     } else if (paramString.equals("iso8859-7")) {
/* 110 */       str = "ISO8859_7";
/* 111 */     } else if (paramString.equals("iso8859-8")) {
/* 112 */       str = "ISO8859_8";
/* 113 */     } else if (paramString.equals("iso8859-9")) {
/* 114 */       str = "ISO8859_9";
/* 115 */     } else if (paramString.equals("iso8859-13")) {
/* 116 */       str = "ISO8859_13";
/* 117 */     } else if (paramString.equals("iso8859-15")) {
/* 118 */       str = "ISO8859_15";
/* 119 */     } else if (paramString.equals("ksc5601.1987-0")) {
/* 120 */       str = "sun.awt.motif.X11KSC5601";
/* 121 */       i = 2;
/* 122 */     } else if (paramString.equals("ksc5601.1992-3")) {
/* 123 */       str = "sun.awt.motif.X11Johab";
/* 124 */       i = 2;
/* 125 */     } else if (paramString.equals("ksc5601.1987-1")) {
/* 126 */       str = "EUC_KR";
/* 127 */       i = 2;
/* 128 */     } else if (paramString.equals("cns11643-1")) {
/* 129 */       str = "sun.awt.motif.X11CNS11643P1";
/* 130 */       i = 2;
/* 131 */     } else if (paramString.equals("cns11643-2")) {
/* 132 */       str = "sun.awt.motif.X11CNS11643P2";
/* 133 */       i = 2;
/* 134 */     } else if (paramString.equals("cns11643-3")) {
/* 135 */       str = "sun.awt.motif.X11CNS11643P3";
/* 136 */       i = 2;
/* 137 */     } else if (paramString.equals("gb2312.1980-0")) {
/* 138 */       str = "sun.awt.motif.X11GB2312";
/* 139 */       i = 2;
/* 140 */     } else if (paramString.indexOf("big5") >= 0) {
/* 141 */       str = "Big5";
/* 142 */       i = 2;
/* 143 */       bool1 = true;
/* 144 */     } else if (paramString.equals("tis620.2533-0")) {
/* 145 */       str = "TIS620";
/* 146 */     } else if (paramString.equals("gbk-0")) {
/* 147 */       str = "sun.awt.motif.X11GBK";
/* 148 */       i = 2;
/* 149 */     } else if (paramString.indexOf("sun.unicode-0") >= 0) {
/* 150 */       str = "sun.awt.motif.X11SunUnicode_0";
/* 151 */       i = 2;
/* 152 */     } else if (paramString.indexOf("gb18030.2000-1") >= 0) {
/* 153 */       str = "sun.awt.motif.X11GB18030_1";
/* 154 */       i = 2;
/* 155 */     } else if (paramString.indexOf("gb18030.2000-0") >= 0) {
/* 156 */       str = "sun.awt.motif.X11GB18030_0";
/* 157 */       i = 2;
/* 158 */     } else if (paramString.indexOf("hkscs") >= 0) {
/* 159 */       str = "sun.awt.HKSCS";
/* 160 */       i = 2;
/*     */     }
/* 162 */     return new XMap(str, k, j, i, bool1, bool2);
/*     */   }
/*     */ 
/*     */   private XMap(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 171 */     CharsetEncoder localCharsetEncoder = null;
/* 172 */     if (paramString != null)
/*     */       try {
/* 174 */         if (paramString.startsWith("sun.awt"))
/* 175 */           localCharsetEncoder = ((Charset)Class.forName(paramString).newInstance()).newEncoder();
/*     */         else
/* 177 */           localCharsetEncoder = Charset.forName(paramString).newEncoder();
/*     */       } catch (Exception localException1) {
/* 179 */         localException1.printStackTrace();
/*     */       }
/* 181 */     if (localCharsetEncoder == null) {
/* 182 */       this.convertedGlyphs = new char[256];
/* 183 */       for (i = 0; i < 256; i++) {
/* 184 */         this.convertedGlyphs[i] = ((char)i);
/*     */       }
/* 186 */       return;
/*     */     }
/*     */ 
/* 192 */     int i = paramInt2 - paramInt1 + 1;
/* 193 */     byte[] arrayOfByte1 = new byte[i * paramInt3];
/* 194 */     char[] arrayOfChar = new char[i];
/* 195 */     for (int k = 0; k < i; k++) {
/* 196 */       arrayOfChar[k] = ((char)(paramInt1 + k));
/*     */     }
/* 198 */     k = 0;
/*     */ 
/* 200 */     if ((paramInt3 > 1) && (paramInt1 < 256)) {
/* 201 */       k = 256 - paramInt1;
/*     */     }
/* 203 */     byte[] arrayOfByte2 = new byte[paramInt3];
/*     */     try {
/* 205 */       int m = 0;
/* 206 */       int i1 = 0;
/*     */ 
/* 212 */       if ((k < 55296) && (k + i > 57343)) {
/* 213 */         m = 55296 - k;
/* 214 */         i1 = m * paramInt3;
/* 215 */         localCharsetEncoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(arrayOfByte2).encode(CharBuffer.wrap(arrayOfChar, k, m), ByteBuffer.wrap(arrayOfByte1, k * paramInt3, i1), true);
/*     */ 
/* 221 */         k = 57344;
/*     */       }
/* 223 */       m = i - k;
/* 224 */       i1 = m * paramInt3;
/* 225 */       localCharsetEncoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(arrayOfByte2).encode(CharBuffer.wrap(arrayOfChar, k, m), ByteBuffer.wrap(arrayOfByte1, k * paramInt3, i1), true);
/*     */     }
/*     */     catch (Exception localException2)
/*     */     {
/* 231 */       localException2.printStackTrace();
/*     */     }
/* 233 */     this.convertedGlyphs = new char[65536];
/* 234 */     for (int n = 0; n < i; n++) {
/* 235 */       if (paramInt3 == 1)
/* 236 */         this.convertedGlyphs[(n + paramInt1)] = ((char)(arrayOfByte1[n] & 0xFF));
/*     */       else {
/* 238 */         this.convertedGlyphs[(n + paramInt1)] = ((char)(((arrayOfByte1[(n * 2)] & 0xFF) << 8) + (arrayOfByte1[(n * 2 + 1)] & 0xFF)));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 244 */     i = paramBoolean2 ? 128 : 256;
/* 245 */     if ((paramBoolean1) && (this.convertedGlyphs.length >= 256))
/* 246 */       for (int j = 0; j < i; j++)
/* 247 */         if (this.convertedGlyphs[j] == 0)
/* 248 */           this.convertedGlyphs[j] = ((char)j);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.XMap
 * JD-Core Version:    0.6.2
 */