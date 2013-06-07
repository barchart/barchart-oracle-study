/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class XSettings
/*     */ {
/*     */   private long serial;
/*     */ 
/*     */   public XSettings()
/*     */   {
/*  43 */     this.serial = -1L;
/*     */   }
/*     */ 
/*     */   public Map update(byte[] paramArrayOfByte)
/*     */   {
/*  56 */     return new Update(paramArrayOfByte).update();
/*     */   }
/*     */ 
/*     */   class Update
/*     */   {
/*     */     private static final int LITTLE_ENDIAN = 0;
/*     */     private static final int BIG_ENDIAN = 1;
/*     */     private static final int TYPE_INTEGER = 0;
/*     */     private static final int TYPE_STRING = 1;
/*     */     private static final int TYPE_COLOR = 2;
/*     */     private byte[] data;
/*     */     private int dlen;
/*     */     private int idx;
/*     */     private boolean isLittle;
/*  78 */     private long serial = -1L;
/*  79 */     private int nsettings = 0;
/*     */     private boolean isValid;
/*     */     private HashMap updatedSettings;
/*     */ 
/*     */     Update(byte[] arg2)
/*     */     {
/*     */       Object localObject;
/*  93 */       this.data = localObject;
/*     */ 
/*  95 */       this.dlen = localObject.length;
/*  96 */       if (this.dlen < 12)
/*     */       {
/*  98 */         return;
/*     */       }
/*     */ 
/* 103 */       this.idx = 0;
/* 104 */       this.isLittle = (getCARD8() == 0);
/*     */ 
/* 106 */       this.idx = 4;
/* 107 */       this.serial = getCARD32();
/*     */ 
/* 113 */       this.idx = 8;
/* 114 */       this.nsettings = getINT32();
/*     */ 
/* 116 */       this.updatedSettings = new HashMap();
/*     */ 
/* 118 */       this.isValid = true;
/*     */     }
/*     */ 
/*     */     private void needBytes(int paramInt)
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 125 */       if (this.idx + paramInt <= this.dlen) {
/* 126 */         return;
/*     */       }
/*     */ 
/* 129 */       throw new IndexOutOfBoundsException("at " + this.idx + " need " + paramInt + " length " + this.dlen);
/*     */     }
/*     */ 
/*     */     private int getCARD8()
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 138 */       needBytes(1);
/*     */ 
/* 140 */       int i = this.data[this.idx] & 0xFF;
/*     */ 
/* 142 */       this.idx += 1;
/* 143 */       return i;
/*     */     }
/*     */ 
/*     */     private int getCARD16()
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 150 */       needBytes(2);
/*     */       int i;
/* 153 */       if (this.isLittle) {
/* 154 */         i = this.data[(this.idx + 0)] & 0xFF | (this.data[(this.idx + 1)] & 0xFF) << 8;
/*     */       }
/*     */       else {
/* 157 */         i = (this.data[(this.idx + 0)] & 0xFF) << 8 | this.data[(this.idx + 1)] & 0xFF;
/*     */       }
/*     */ 
/* 161 */       this.idx += 2;
/* 162 */       return i;
/*     */     }
/*     */ 
/*     */     private int getINT32()
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 169 */       needBytes(4);
/*     */       int i;
/* 172 */       if (this.isLittle) {
/* 173 */         i = this.data[(this.idx + 0)] & 0xFF | (this.data[(this.idx + 1)] & 0xFF) << 8 | (this.data[(this.idx + 2)] & 0xFF) << 16 | (this.data[(this.idx + 3)] & 0xFF) << 24;
/*     */       }
/*     */       else
/*     */       {
/* 178 */         i = (this.data[(this.idx + 0)] & 0xFF) << 24 | (this.data[(this.idx + 1)] & 0xFF) << 16 | (this.data[(this.idx + 2)] & 0xFF) << 8 | (this.data[(this.idx + 3)] & 0xFF) << 0;
/*     */       }
/*     */ 
/* 184 */       this.idx += 4;
/* 185 */       return i;
/*     */     }
/*     */ 
/*     */     private long getCARD32()
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 192 */       return getINT32() & 0xFFFFFFFF;
/*     */     }
/*     */ 
/*     */     private String getString(int paramInt)
/*     */       throws IndexOutOfBoundsException
/*     */     {
/* 199 */       needBytes(paramInt);
/*     */ 
/* 201 */       String str = null;
/*     */       try {
/* 203 */         str = new String(this.data, this.idx, paramInt, "UTF-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */       {
/*     */       }
/* 208 */       this.idx = (this.idx + paramInt + 3 & 0xFFFFFFFC);
/* 209 */       return str;
/*     */     }
/*     */ 
/*     */     public Map update()
/*     */     {
/* 217 */       if (!this.isValid) {
/* 218 */         return null;
/*     */       }
/*     */ 
/* 221 */       synchronized (XSettings.this) {
/* 222 */         long l = XSettings.this.serial;
/*     */ 
/* 224 */         if (this.serial <= l) {
/* 225 */           return null;
/*     */         }
/*     */ 
/* 228 */         for (int i = 0; (i < this.nsettings) && (this.idx < this.dlen); i++) {
/* 229 */           updateOne(l);
/*     */         }
/*     */ 
/* 232 */         XSettings.this.serial = this.serial;
/*     */       }
/*     */ 
/* 235 */       return this.updatedSettings;
/*     */     }
/*     */ 
/*     */     private void updateOne(long paramLong)
/*     */       throws IndexOutOfBoundsException, IllegalArgumentException
/*     */     {
/* 249 */       int i = getCARD8();
/* 250 */       this.idx += 1;
/*     */ 
/* 253 */       int j = getCARD16();
/* 254 */       int k = this.idx;
/*     */ 
/* 257 */       this.idx = (this.idx + j + 3 & 0xFFFFFFFC);
/* 258 */       long l = getCARD32();
/*     */ 
/* 262 */       if (l <= paramLong) {
/* 263 */         if (i == 0) {
/* 264 */           this.idx += 4;
/* 265 */         } else if (i == 1) {
/* 266 */           int m = getINT32();
/* 267 */           this.idx = (this.idx + m + 3 & 0xFFFFFFFC);
/* 268 */         } else if (i == 2) {
/* 269 */           this.idx += 8;
/*     */         } else {
/* 271 */           throw new IllegalArgumentException("Unknown type: " + i);
/*     */         }
/*     */ 
/* 275 */         return;
/*     */       }
/*     */ 
/* 278 */       this.idx = k;
/* 279 */       String str = getString(j);
/* 280 */       this.idx += 4;
/*     */ 
/* 282 */       Object localObject = null;
/* 283 */       if (i == 0) {
/* 284 */         localObject = Integer.valueOf(getINT32());
/*     */       }
/* 286 */       else if (i == 1) {
/* 287 */         localObject = getString(getINT32());
/*     */       }
/* 289 */       else if (i == 2) {
/* 290 */         int n = getCARD16();
/* 291 */         int i1 = getCARD16();
/* 292 */         int i2 = getCARD16();
/* 293 */         int i3 = getCARD16();
/*     */ 
/* 295 */         localObject = new Color(n / 65535.0F, i1 / 65535.0F, i2 / 65535.0F, i3 / 65535.0F);
/*     */       }
/*     */       else
/*     */       {
/* 301 */         throw new IllegalArgumentException("Unknown type: " + i);
/*     */       }
/*     */ 
/* 304 */       if (str == null)
/*     */       {
/* 306 */         return;
/*     */       }
/*     */ 
/* 309 */       this.updatedSettings.put(str, localObject);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.XSettings
 * JD-Core Version:    0.6.2
 */