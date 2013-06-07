/*     */ package sun.print;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class AttributeClass
/*     */ {
/*     */   private String myName;
/*     */   private int myType;
/*     */   private int nameLen;
/*     */   private Object myValue;
/*     */   public static final int TAG_UNSUPPORTED_VALUE = 16;
/*     */   public static final int TAG_INT = 33;
/*     */   public static final int TAG_BOOL = 34;
/*     */   public static final int TAG_ENUM = 35;
/*     */   public static final int TAG_OCTET = 48;
/*     */   public static final int TAG_DATE = 49;
/*     */   public static final int TAG_RESOLUTION = 50;
/*     */   public static final int TAG_RANGE_INTEGER = 51;
/*     */   public static final int TAG_TEXT_LANGUAGE = 53;
/*     */   public static final int TAG_NAME_LANGUAGE = 54;
/*     */   public static final int TAG_TEXT_WO_LANGUAGE = 65;
/*     */   public static final int TAG_NAME_WO_LANGUAGE = 66;
/*     */   public static final int TAG_KEYWORD = 68;
/*     */   public static final int TAG_URI = 69;
/*     */   public static final int TAG_CHARSET = 71;
/*     */   public static final int TAG_NATURALLANGUAGE = 72;
/*     */   public static final int TAG_MIME_MEDIATYPE = 73;
/*     */   public static final int TAG_MEMBER_ATTRNAME = 74;
/*  57 */   public static final AttributeClass ATTRIBUTES_CHARSET = new AttributeClass("attributes-charset", 71, "utf-8");
/*     */ 
/*  60 */   public static final AttributeClass ATTRIBUTES_NATURAL_LANGUAGE = new AttributeClass("attributes-natural-language", 72, "en");
/*     */ 
/*     */   protected AttributeClass(String paramString, int paramInt, Object paramObject)
/*     */   {
/*  72 */     this.myName = paramString;
/*  73 */     this.myType = paramInt;
/*  74 */     this.nameLen = paramString.length();
/*  75 */     this.myValue = paramObject;
/*     */   }
/*     */ 
/*     */   public byte getType() {
/*  79 */     return (byte)this.myType;
/*     */   }
/*     */ 
/*     */   public char[] getLenChars() {
/*  83 */     char[] arrayOfChar = new char[2];
/*  84 */     arrayOfChar[0] = '\000';
/*  85 */     arrayOfChar[1] = ((char)this.nameLen);
/*  86 */     return arrayOfChar;
/*     */   }
/*     */ 
/*     */   public Object getObjectValue()
/*     */   {
/*  93 */     return this.myValue;
/*     */   }
/*     */ 
/*     */   public int getIntValue()
/*     */   {
/* 100 */     byte[] arrayOfByte1 = (byte[])this.myValue;
/*     */ 
/* 102 */     if (arrayOfByte1 != null) {
/* 103 */       byte[] arrayOfByte2 = new byte[4];
/* 104 */       for (int i = 0; i < 4; i++) {
/* 105 */         arrayOfByte2[i] = arrayOfByte1[(i + 1)];
/*     */       }
/*     */ 
/* 108 */       return convertToInt(arrayOfByte2);
/*     */     }
/* 110 */     return 0;
/*     */   }
/*     */ 
/*     */   public int[] getArrayOfIntValues()
/*     */   {
/* 118 */     byte[] arrayOfByte1 = (byte[])this.myValue;
/* 119 */     if (arrayOfByte1 != null)
/*     */     {
/* 122 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
/*     */ 
/* 124 */       int i = localByteArrayInputStream.available();
/*     */ 
/* 127 */       localByteArrayInputStream.mark(i);
/* 128 */       localByteArrayInputStream.skip(i - 1);
/* 129 */       int j = localByteArrayInputStream.read();
/* 130 */       localByteArrayInputStream.reset();
/*     */ 
/* 132 */       int[] arrayOfInt = new int[j];
/* 133 */       for (int k = 0; k < j; k++)
/*     */       {
/* 135 */         int m = localByteArrayInputStream.read();
/* 136 */         if (m != 4)
/*     */         {
/* 138 */           return null;
/*     */         }
/*     */ 
/* 141 */         byte[] arrayOfByte2 = new byte[m];
/* 142 */         localByteArrayInputStream.read(arrayOfByte2, 0, m);
/* 143 */         arrayOfInt[k] = convertToInt(arrayOfByte2);
/*     */       }
/*     */ 
/* 146 */       return arrayOfInt;
/*     */     }
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public int[] getIntRangeValue()
/*     */   {
/* 155 */     int[] arrayOfInt = { 0, 0 };
/* 156 */     byte[] arrayOfByte1 = (byte[])this.myValue;
/* 157 */     if (arrayOfByte1 != null) {
/* 158 */       int i = 4;
/* 159 */       for (int j = 0; j < 2; j++) {
/* 160 */         byte[] arrayOfByte2 = new byte[i];
/*     */ 
/* 162 */         for (int k = 0; k < i; k++)
/*     */         {
/* 164 */           arrayOfByte2[k] = arrayOfByte1[(k + 4 * j + 1)];
/*     */         }
/* 166 */         arrayOfInt[j] = convertToInt(arrayOfByte2);
/*     */       }
/*     */     }
/* 169 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public String getStringValue()
/*     */   {
/* 179 */     String str = null;
/* 180 */     byte[] arrayOfByte1 = (byte[])this.myValue;
/* 181 */     if (arrayOfByte1 != null) {
/* 182 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
/*     */ 
/* 185 */       int i = localByteArrayInputStream.read();
/*     */ 
/* 187 */       byte[] arrayOfByte2 = new byte[i];
/* 188 */       localByteArrayInputStream.read(arrayOfByte2, 0, i);
/*     */       try {
/* 190 */         str = new String(arrayOfByte2, "UTF-8");
/*     */       } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */       }
/*     */     }
/* 194 */     return str;
/*     */   }
/*     */ 
/*     */   public String[] getArrayOfStringValues()
/*     */   {
/* 203 */     byte[] arrayOfByte1 = (byte[])this.myValue;
/* 204 */     if (arrayOfByte1 != null) {
/* 205 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
/*     */ 
/* 207 */       int i = localByteArrayInputStream.available();
/*     */ 
/* 210 */       localByteArrayInputStream.mark(i);
/* 211 */       localByteArrayInputStream.skip(i - 1);
/* 212 */       int j = localByteArrayInputStream.read();
/* 213 */       localByteArrayInputStream.reset();
/*     */ 
/* 215 */       String[] arrayOfString = new String[j];
/* 216 */       for (int k = 0; k < j; k++)
/*     */       {
/* 218 */         int m = localByteArrayInputStream.read();
/* 219 */         byte[] arrayOfByte2 = new byte[m];
/* 220 */         localByteArrayInputStream.read(arrayOfByte2, 0, m);
/*     */         try {
/* 222 */           arrayOfString[k] = new String(arrayOfByte2, "UTF-8");
/*     */         } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */         }
/*     */       }
/* 226 */       return arrayOfString;
/*     */     }
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   public byte getByteValue()
/*     */   {
/* 236 */     byte[] arrayOfByte = (byte[])this.myValue;
/*     */ 
/* 238 */     if ((arrayOfByte != null) && (arrayOfByte.length >= 2)) {
/* 239 */       return arrayOfByte[1];
/*     */     }
/* 241 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 248 */     return this.myName;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 252 */     return (paramObject != null) && ((paramObject instanceof AttributeClass)) && (paramObject.toString().equals(((AttributeClass)paramObject).toString()));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 259 */     return this.myName;
/*     */   }
/*     */ 
/*     */   private int unsignedByteToInt(byte paramByte) {
/* 263 */     return paramByte & 0xFF;
/*     */   }
/*     */ 
/*     */   private int convertToInt(byte[] paramArrayOfByte) {
/* 267 */     int i = 0;
/* 268 */     int j = 0;
/* 269 */     i += (unsignedByteToInt(paramArrayOfByte[(j++)]) << 24);
/* 270 */     i += (unsignedByteToInt(paramArrayOfByte[(j++)]) << 16);
/* 271 */     i += (unsignedByteToInt(paramArrayOfByte[(j++)]) << 8);
/* 272 */     i += (unsignedByteToInt(paramArrayOfByte[(j++)]) << 0);
/* 273 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.AttributeClass
 * JD-Core Version:    0.6.2
 */