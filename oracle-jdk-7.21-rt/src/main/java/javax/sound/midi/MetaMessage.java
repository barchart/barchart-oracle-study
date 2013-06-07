/*     */ package javax.sound.midi;
/*     */ 
/*     */ public class MetaMessage extends MidiMessage
/*     */ {
/*     */   public static final int META = 255;
/*  78 */   private static byte[] defaultMessage = { -1, 0 };
/*     */ 
/*  91 */   private int dataLength = 0;
/*     */   private static final long mask = 127L;
/*     */ 
/*     */   public MetaMessage()
/*     */   {
/* 102 */     this(defaultMessage);
/*     */   }
/*     */ 
/*     */   public MetaMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */     throws InvalidMidiDataException
/*     */   {
/* 124 */     super(null);
/* 125 */     setMessage(paramInt1, paramArrayOfByte, paramInt2);
/*     */   }
/*     */ 
/*     */   protected MetaMessage(byte[] paramArrayOfByte)
/*     */   {
/* 137 */     super(paramArrayOfByte);
/*     */ 
/* 139 */     if (paramArrayOfByte.length >= 3) {
/* 140 */       this.dataLength = (paramArrayOfByte.length - 3);
/* 141 */       for (int i = 2; 
/* 142 */         (i < paramArrayOfByte.length) && ((paramArrayOfByte[i] & 0x80) != 0); 
/* 143 */         i++) this.dataLength -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */     throws InvalidMidiDataException
/*     */   {
/* 169 */     if ((paramInt1 >= 128) || (paramInt1 < 0)) {
/* 170 */       throw new InvalidMidiDataException("Invalid meta event with type " + paramInt1);
/*     */     }
/* 172 */     if (((paramInt2 > 0) && (paramInt2 > paramArrayOfByte.length)) || (paramInt2 < 0)) {
/* 173 */       throw new InvalidMidiDataException("length out of bounds: " + paramInt2);
/*     */     }
/*     */ 
/* 176 */     this.length = (2 + getVarIntLength(paramInt2) + paramInt2);
/* 177 */     this.dataLength = paramInt2;
/* 178 */     this.data = new byte[this.length];
/* 179 */     this.data[0] = -1;
/* 180 */     this.data[1] = ((byte)paramInt1);
/* 181 */     writeVarInt(this.data, 2, paramInt2);
/* 182 */     if (paramInt2 > 0)
/* 183 */       System.arraycopy(paramArrayOfByte, 0, this.data, this.length - this.dataLength, this.dataLength);
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 193 */     if (this.length >= 2) {
/* 194 */       return this.data[1] & 0xFF;
/*     */     }
/* 196 */     return 0;
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/* 212 */     byte[] arrayOfByte = new byte[this.dataLength];
/* 213 */     System.arraycopy(this.data, this.length - this.dataLength, arrayOfByte, 0, this.dataLength);
/* 214 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 224 */     byte[] arrayOfByte = new byte[this.length];
/* 225 */     System.arraycopy(this.data, 0, arrayOfByte, 0, arrayOfByte.length);
/*     */ 
/* 227 */     MetaMessage localMetaMessage = new MetaMessage(arrayOfByte);
/* 228 */     return localMetaMessage;
/*     */   }
/*     */ 
/*     */   private int getVarIntLength(long paramLong)
/*     */   {
/* 234 */     int i = 0;
/*     */     do {
/* 236 */       paramLong >>= 7;
/* 237 */       i++;
/* 238 */     }while (paramLong > 0L);
/* 239 */     return i;
/*     */   }
/*     */ 
/*     */   private void writeVarInt(byte[] paramArrayOfByte, int paramInt, long paramLong)
/*     */   {
/* 245 */     int i = 63;
/*     */ 
/* 247 */     while ((i > 0) && ((paramLong & 127L << i) == 0L)) i -= 7;
/*     */ 
/* 249 */     while (i > 0) {
/* 250 */       paramArrayOfByte[(paramInt++)] = ((byte)(int)((paramLong & 127L << i) >> i | 0x80));
/* 251 */       i -= 7;
/*     */     }
/* 253 */     paramArrayOfByte[paramInt] = ((byte)(int)(paramLong & 0x7F));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.midi.MetaMessage
 * JD-Core Version:    0.6.2
 */