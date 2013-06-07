/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.SysexMessage;
/*     */ import javax.sound.midi.Track;
/*     */ 
/*     */ class SMFParser
/*     */ {
/*     */   private static final int MTrk_MAGIC = 1297379947;
/*     */   private static final boolean STRICT_PARSER = false;
/*     */   private static final boolean DEBUG = false;
/*     */   int tracks;
/*     */   DataInputStream stream;
/* 267 */   private int trackLength = 0;
/* 268 */   private byte[] trackData = null;
/* 269 */   private int pos = 0;
/*     */ 
/*     */   private int readUnsigned()
/*     */     throws IOException
/*     */   {
/* 275 */     return this.trackData[(this.pos++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   private void read(byte[] paramArrayOfByte) throws IOException {
/* 279 */     System.arraycopy(this.trackData, this.pos, paramArrayOfByte, 0, paramArrayOfByte.length);
/* 280 */     this.pos += paramArrayOfByte.length;
/*     */   }
/*     */ 
/*     */   private long readVarInt() throws IOException {
/* 284 */     long l = 0L;
/* 285 */     int i = 0;
/*     */     do {
/* 287 */       i = this.trackData[(this.pos++)] & 0xFF;
/* 288 */       l = (l << 7) + (i & 0x7F);
/* 289 */     }while ((i & 0x80) != 0);
/* 290 */     return l;
/*     */   }
/*     */ 
/*     */   private int readIntFromStream() throws IOException {
/*     */     try {
/* 295 */       return this.stream.readInt(); } catch (EOFException localEOFException) {
/*     */     }
/* 297 */     throw new EOFException("invalid MIDI file");
/*     */   }
/*     */ 
/*     */   boolean nextTrack() throws IOException, InvalidMidiDataException
/*     */   {
/* 303 */     this.trackLength = 0;
/*     */     int i;
/*     */     do {
/* 306 */       if (this.stream.skipBytes(this.trackLength) != this.trackLength)
/*     */       {
/* 308 */         return false;
/*     */       }
/*     */ 
/* 312 */       i = readIntFromStream();
/* 313 */       this.trackLength = readIntFromStream();
/* 314 */     }while (i != 1297379947);
/*     */ 
/* 316 */     if (this.trackLength < 0) {
/* 317 */       return false;
/*     */     }
/*     */ 
/* 321 */     this.trackData = new byte[this.trackLength];
/*     */     try
/*     */     {
/* 324 */       this.stream.readFully(this.trackData);
/*     */     }
/*     */     catch (EOFException localEOFException) {
/* 327 */       return false;
/*     */     }
/*     */ 
/* 331 */     this.pos = 0;
/* 332 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean trackFinished() {
/* 336 */     return this.pos >= this.trackLength;
/*     */   }
/*     */ 
/*     */   void readTrack(Track paramTrack) throws IOException, InvalidMidiDataException
/*     */   {
/*     */     try {
/* 342 */       long l = 0L;
/*     */ 
/* 347 */       int i = 0;
/* 348 */       int j = 0;
/*     */ 
/* 350 */       while ((!trackFinished()) && (j == 0))
/*     */       {
/* 353 */         int k = -1;
/* 354 */         int m = 0;
/*     */ 
/* 359 */         l += readVarInt();
/*     */ 
/* 362 */         int n = readUnsigned();
/*     */ 
/* 364 */         if (n >= 128)
/* 365 */           i = n;
/*     */         else
/* 367 */           k = n;
/*     */         Object localObject;
/* 370 */         switch (i & 0xF0)
/*     */         {
/*     */         case 128:
/*     */         case 144:
/*     */         case 160:
/*     */         case 176:
/*     */         case 224:
/* 377 */           if (k == -1) {
/* 378 */             k = readUnsigned();
/*     */           }
/* 380 */           m = readUnsigned();
/* 381 */           localObject = new FastShortMessage(i | k << 8 | m << 16);
/* 382 */           break;
/*     */         case 192:
/*     */         case 208:
/* 386 */           if (k == -1) {
/* 387 */             k = readUnsigned();
/*     */           }
/* 389 */           localObject = new FastShortMessage(i | k << 8);
/* 390 */           break;
/*     */         case 240:
/* 393 */           switch (i)
/*     */           {
/*     */           case 240:
/*     */           case 247:
/* 397 */             int i1 = (int)readVarInt();
/* 398 */             byte[] arrayOfByte1 = new byte[i1];
/* 399 */             read(arrayOfByte1);
/*     */ 
/* 401 */             SysexMessage localSysexMessage = new SysexMessage();
/* 402 */             localSysexMessage.setMessage(i, arrayOfByte1, i1);
/* 403 */             localObject = localSysexMessage;
/* 404 */             break;
/*     */           case 255:
/* 408 */             int i2 = readUnsigned();
/* 409 */             int i3 = (int)readVarInt();
/*     */ 
/* 411 */             byte[] arrayOfByte2 = new byte[i3];
/* 412 */             read(arrayOfByte2);
/*     */ 
/* 414 */             MetaMessage localMetaMessage = new MetaMessage();
/* 415 */             localMetaMessage.setMessage(i2, arrayOfByte2, i3);
/* 416 */             localObject = localMetaMessage;
/* 417 */             if (i2 == 47)
/*     */             {
/* 419 */               j = 1; } break;
/*     */           default:
/* 423 */             throw new InvalidMidiDataException("Invalid status byte: " + i);
/*     */           }
/* 425 */           break;
/*     */         default:
/* 427 */           throw new InvalidMidiDataException("Invalid status byte: " + i);
/*     */         }
/* 429 */         paramTrack.add(new MidiEvent((MidiMessage)localObject, l));
/*     */       }
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
/*     */     {
/* 434 */       throw new EOFException("invalid MIDI file");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SMFParser
 * JD-Core Version:    0.6.2
 */