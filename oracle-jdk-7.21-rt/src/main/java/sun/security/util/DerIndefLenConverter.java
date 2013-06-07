/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ class DerIndefLenConverter
/*     */ {
/*     */   private static final int TAG_MASK = 31;
/*     */   private static final int FORM_MASK = 32;
/*     */   private static final int CLASS_MASK = 192;
/*     */   private static final int LEN_LONG = 128;
/*     */   private static final int LEN_MASK = 127;
/*     */   private static final int SKIP_EOC_BYTES = 2;
/*     */   private byte[] data;
/*     */   private byte[] newData;
/*     */   private int newDataPos;
/*     */   private int dataPos;
/*     */   private int dataSize;
/*     */   private int index;
/*  53 */   private int unresolved = 0;
/*     */ 
/*  55 */   private ArrayList<Object> ndefsList = new ArrayList();
/*     */ 
/*  57 */   private int numOfTotalLenBytes = 0;
/*     */ 
/*     */   private boolean isEOC(int paramInt) {
/*  60 */     return ((paramInt & 0x1F) == 0) && ((paramInt & 0x20) == 0) && ((paramInt & 0xC0) == 0);
/*     */   }
/*     */ 
/*     */   static boolean isLongForm(int paramInt)
/*     */   {
/*  67 */     return (paramInt & 0x80) == 128;
/*     */   }
/*     */ 
/*     */   static boolean isIndefinite(int paramInt)
/*     */   {
/*  85 */     return (isLongForm(paramInt)) && ((paramInt & 0x7F) == 0);
/*     */   }
/*     */ 
/*     */   private void parseTag()
/*     */     throws IOException
/*     */   {
/*  93 */     if (this.dataPos == this.dataSize)
/*  94 */       return;
/*  95 */     if ((isEOC(this.data[this.dataPos])) && (this.data[(this.dataPos + 1)] == 0)) {
/*  96 */       int i = 0;
/*  97 */       Object localObject = null;
/*     */ 
/*  99 */       for (int j = this.ndefsList.size() - 1; j >= 0; j--)
/*     */       {
/* 102 */         localObject = this.ndefsList.get(j);
/* 103 */         if ((localObject instanceof Integer)) {
/*     */           break;
/*     */         }
/* 106 */         i += ((byte[])localObject).length - 3;
/*     */       }
/*     */ 
/* 109 */       if (j < 0) {
/* 110 */         throw new IOException("EOC does not have matching indefinite-length tag");
/*     */       }
/*     */ 
/* 113 */       int k = this.dataPos - ((Integer)localObject).intValue() + i;
/*     */ 
/* 115 */       byte[] arrayOfByte = getLengthBytes(k);
/* 116 */       this.ndefsList.set(j, arrayOfByte);
/* 117 */       this.unresolved -= 1;
/*     */ 
/* 123 */       this.numOfTotalLenBytes += arrayOfByte.length - 3;
/*     */     }
/* 125 */     this.dataPos += 1;
/*     */   }
/*     */ 
/*     */   private void writeTag()
/*     */   {
/* 133 */     if (this.dataPos == this.dataSize)
/* 134 */       return;
/* 135 */     int i = this.data[(this.dataPos++)];
/* 136 */     if ((isEOC(i)) && (this.data[this.dataPos] == 0)) {
/* 137 */       this.dataPos += 1;
/* 138 */       writeTag();
/*     */     } else {
/* 140 */       this.newData[(this.newDataPos++)] = ((byte)i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int parseLength()
/*     */     throws IOException
/*     */   {
/* 148 */     int i = 0;
/* 149 */     if (this.dataPos == this.dataSize)
/* 150 */       return i;
/* 151 */     int j = this.data[(this.dataPos++)] & 0xFF;
/* 152 */     if (isIndefinite(j)) {
/* 153 */       this.ndefsList.add(new Integer(this.dataPos));
/* 154 */       this.unresolved += 1;
/* 155 */       return i;
/*     */     }
/* 157 */     if (isLongForm(j)) {
/* 158 */       j &= 127;
/* 159 */       if (j > 4)
/* 160 */         throw new IOException("Too much data");
/* 161 */       if (this.dataSize - this.dataPos < j + 1)
/* 162 */         throw new IOException("Too little data");
/* 163 */       for (int k = 0; k < j; k++)
/* 164 */         i = (i << 8) + (this.data[(this.dataPos++)] & 0xFF);
/*     */     } else {
/* 166 */       i = j & 0x7F;
/*     */     }
/* 168 */     return i;
/*     */   }
/*     */ 
/*     */   private void writeLengthAndValue()
/*     */     throws IOException
/*     */   {
/* 178 */     if (this.dataPos == this.dataSize)
/* 179 */       return;
/* 180 */     int i = 0;
/* 181 */     int j = this.data[(this.dataPos++)] & 0xFF;
/* 182 */     if (isIndefinite(j)) {
/* 183 */       byte[] arrayOfByte = (byte[])this.ndefsList.get(this.index++);
/* 184 */       System.arraycopy(arrayOfByte, 0, this.newData, this.newDataPos, arrayOfByte.length);
/*     */ 
/* 186 */       this.newDataPos += arrayOfByte.length;
/* 187 */       return;
/*     */     }
/* 189 */     if (isLongForm(j)) {
/* 190 */       j &= 127;
/* 191 */       for (int k = 0; k < j; k++)
/* 192 */         i = (i << 8) + (this.data[(this.dataPos++)] & 0xFF);
/*     */     } else {
/* 194 */       i = j & 0x7F;
/* 195 */     }writeLength(i);
/* 196 */     writeValue(i);
/*     */   }
/*     */ 
/*     */   private void writeLength(int paramInt) {
/* 200 */     if (paramInt < 128) {
/* 201 */       this.newData[(this.newDataPos++)] = ((byte)paramInt);
/*     */     }
/* 203 */     else if (paramInt < 256) {
/* 204 */       this.newData[(this.newDataPos++)] = -127;
/* 205 */       this.newData[(this.newDataPos++)] = ((byte)paramInt);
/*     */     }
/* 207 */     else if (paramInt < 65536) {
/* 208 */       this.newData[(this.newDataPos++)] = -126;
/* 209 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 8));
/* 210 */       this.newData[(this.newDataPos++)] = ((byte)paramInt);
/*     */     }
/* 212 */     else if (paramInt < 16777216) {
/* 213 */       this.newData[(this.newDataPos++)] = -125;
/* 214 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 16));
/* 215 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 8));
/* 216 */       this.newData[(this.newDataPos++)] = ((byte)paramInt);
/*     */     }
/*     */     else {
/* 219 */       this.newData[(this.newDataPos++)] = -124;
/* 220 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 24));
/* 221 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 16));
/* 222 */       this.newData[(this.newDataPos++)] = ((byte)(paramInt >> 8));
/* 223 */       this.newData[(this.newDataPos++)] = ((byte)paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] getLengthBytes(int paramInt)
/*     */   {
/* 229 */     int i = 0;
/*     */     byte[] arrayOfByte;
/* 231 */     if (paramInt < 128) {
/* 232 */       arrayOfByte = new byte[1];
/* 233 */       arrayOfByte[(i++)] = ((byte)paramInt);
/*     */     }
/* 235 */     else if (paramInt < 256) {
/* 236 */       arrayOfByte = new byte[2];
/* 237 */       arrayOfByte[(i++)] = -127;
/* 238 */       arrayOfByte[(i++)] = ((byte)paramInt);
/*     */     }
/* 240 */     else if (paramInt < 65536) {
/* 241 */       arrayOfByte = new byte[3];
/* 242 */       arrayOfByte[(i++)] = -126;
/* 243 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
/* 244 */       arrayOfByte[(i++)] = ((byte)paramInt);
/*     */     }
/* 246 */     else if (paramInt < 16777216) {
/* 247 */       arrayOfByte = new byte[4];
/* 248 */       arrayOfByte[(i++)] = -125;
/* 249 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 16));
/* 250 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
/* 251 */       arrayOfByte[(i++)] = ((byte)paramInt);
/*     */     }
/*     */     else {
/* 254 */       arrayOfByte = new byte[5];
/* 255 */       arrayOfByte[(i++)] = -124;
/* 256 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 24));
/* 257 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 16));
/* 258 */       arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
/* 259 */       arrayOfByte[(i++)] = ((byte)paramInt);
/*     */     }
/*     */ 
/* 262 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private int getNumOfLenBytes(int paramInt)
/*     */   {
/* 268 */     int i = 0;
/*     */ 
/* 270 */     if (paramInt < 128)
/* 271 */       i = 1;
/* 272 */     else if (paramInt < 256)
/* 273 */       i = 2;
/* 274 */     else if (paramInt < 65536)
/* 275 */       i = 3;
/* 276 */     else if (paramInt < 16777216)
/* 277 */       i = 4;
/*     */     else {
/* 279 */       i = 5;
/*     */     }
/* 281 */     return i;
/*     */   }
/*     */ 
/*     */   private void parseValue(int paramInt)
/*     */   {
/* 288 */     this.dataPos += paramInt;
/*     */   }
/*     */ 
/*     */   private void writeValue(int paramInt)
/*     */   {
/* 295 */     for (int i = 0; i < paramInt; i++)
/* 296 */       this.newData[(this.newDataPos++)] = this.data[(this.dataPos++)];
/*     */   }
/*     */ 
/*     */   byte[] convert(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 310 */     this.data = paramArrayOfByte;
/* 311 */     this.dataPos = 0; this.index = 0;
/* 312 */     this.dataSize = this.data.length;
/* 313 */     int i = 0;
/* 314 */     int j = 0;
/*     */ 
/* 317 */     while (this.dataPos < this.dataSize) {
/* 318 */       parseTag();
/* 319 */       i = parseLength();
/* 320 */       parseValue(i);
/* 321 */       if (this.unresolved == 0) {
/* 322 */         j = this.dataSize - this.dataPos;
/* 323 */         this.dataSize = this.dataPos;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 328 */     if (this.unresolved != 0) {
/* 329 */       throw new IOException("not all indef len BER resolved");
/*     */     }
/*     */ 
/* 332 */     this.newData = new byte[this.dataSize + this.numOfTotalLenBytes + j];
/* 333 */     this.dataPos = 0; this.newDataPos = 0; this.index = 0;
/*     */ 
/* 337 */     while (this.dataPos < this.dataSize) {
/* 338 */       writeTag();
/* 339 */       writeLengthAndValue();
/*     */     }
/* 341 */     System.arraycopy(paramArrayOfByte, this.dataSize, this.newData, this.dataSize + this.numOfTotalLenBytes, j);
/*     */ 
/* 344 */     return this.newData;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.util.DerIndefLenConverter
 * JD-Core Version:    0.6.2
 */