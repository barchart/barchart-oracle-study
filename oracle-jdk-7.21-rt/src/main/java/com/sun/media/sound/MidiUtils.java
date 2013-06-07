/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Track;
/*     */ 
/*     */ public class MidiUtils
/*     */ {
/*     */   public static final int DEFAULT_TEMPO_MPQ = 500000;
/*     */   public static final int META_END_OF_TRACK_TYPE = 47;
/*     */   public static final int META_TEMPO_TYPE = 81;
/*     */ 
/*     */   public static boolean isMetaEndOfTrack(MidiMessage paramMidiMessage)
/*     */   {
/*  49 */     if ((paramMidiMessage.getLength() != 3) || (paramMidiMessage.getStatus() != 255))
/*     */     {
/*  51 */       return false;
/*     */     }
/*     */ 
/*  54 */     byte[] arrayOfByte = paramMidiMessage.getMessage();
/*  55 */     return ((arrayOfByte[1] & 0xFF) == 47) && (arrayOfByte[2] == 0);
/*     */   }
/*     */ 
/*     */   public static boolean isMetaTempo(MidiMessage paramMidiMessage)
/*     */   {
/*  62 */     if ((paramMidiMessage.getLength() != 6) || (paramMidiMessage.getStatus() != 255))
/*     */     {
/*  64 */       return false;
/*     */     }
/*     */ 
/*  67 */     byte[] arrayOfByte = paramMidiMessage.getMessage();
/*     */ 
/*  69 */     return ((arrayOfByte[1] & 0xFF) == 81) && (arrayOfByte[2] == 3);
/*     */   }
/*     */ 
/*     */   public static int getTempoMPQ(MidiMessage paramMidiMessage)
/*     */   {
/*  78 */     if ((paramMidiMessage.getLength() != 6) || (paramMidiMessage.getStatus() != 255))
/*     */     {
/*  80 */       return -1;
/*     */     }
/*  82 */     byte[] arrayOfByte = paramMidiMessage.getMessage();
/*  83 */     if (((arrayOfByte[1] & 0xFF) != 81) || (arrayOfByte[2] != 3)) {
/*  84 */       return -1;
/*     */     }
/*  86 */     int i = arrayOfByte[5] & 0xFF | (arrayOfByte[4] & 0xFF) << 8 | (arrayOfByte[3] & 0xFF) << 16;
/*     */ 
/*  89 */     return i;
/*     */   }
/*     */ 
/*     */   public static double convertTempo(double paramDouble)
/*     */   {
/*  99 */     if (paramDouble <= 0.0D) {
/* 100 */       paramDouble = 1.0D;
/*     */     }
/* 102 */     return 60000000.0D / paramDouble;
/*     */   }
/*     */ 
/*     */   public static long ticks2microsec(long paramLong, double paramDouble, int paramInt)
/*     */   {
/* 112 */     return ()(paramLong * paramDouble / paramInt);
/*     */   }
/*     */ 
/*     */   public static long microsec2ticks(long paramLong, double paramDouble, int paramInt)
/*     */   {
/* 123 */     return ()(paramLong * paramInt / paramDouble);
/*     */   }
/*     */ 
/*     */   public static long tick2microsecond(Sequence paramSequence, long paramLong, TempoCache paramTempoCache)
/*     */   {
/* 132 */     if (paramSequence.getDivisionType() != 0.0F) {
/* 133 */       double d = paramLong / (paramSequence.getDivisionType() * paramSequence.getResolution());
/* 134 */       return ()(1000000.0D * d);
/*     */     }
/*     */ 
/* 137 */     if (paramTempoCache == null) {
/* 138 */       paramTempoCache = new TempoCache(paramSequence);
/*     */     }
/*     */ 
/* 141 */     int i = paramSequence.getResolution();
/*     */ 
/* 143 */     long[] arrayOfLong = paramTempoCache.ticks;
/* 144 */     int[] arrayOfInt = paramTempoCache.tempos;
/* 145 */     int j = arrayOfInt.length;
/*     */ 
/* 148 */     int k = paramTempoCache.snapshotIndex;
/* 149 */     int m = paramTempoCache.snapshotMicro;
/*     */ 
/* 152 */     long l = 0L;
/*     */ 
/* 154 */     if ((k <= 0) || (k >= j) || (arrayOfLong[k] > paramLong))
/*     */     {
/* 157 */       m = 0;
/* 158 */       k = 0;
/*     */     }
/* 160 */     if (j > 0)
/*     */     {
/* 162 */       int n = k + 1;
/* 163 */       while ((n < j) && (arrayOfLong[n] <= paramLong)) {
/* 164 */         m = (int)(m + ticks2microsec(arrayOfLong[n] - arrayOfLong[(n - 1)], arrayOfInt[(n - 1)], i));
/* 165 */         k = n;
/* 166 */         n++;
/*     */       }
/* 168 */       l = m + ticks2microsec(paramLong - arrayOfLong[k], arrayOfInt[k], i);
/*     */     }
/*     */ 
/* 173 */     paramTempoCache.snapshotIndex = k;
/* 174 */     paramTempoCache.snapshotMicro = m;
/* 175 */     return l;
/*     */   }
/*     */ 
/*     */   public static long microsecond2tick(Sequence paramSequence, long paramLong, TempoCache paramTempoCache)
/*     */   {
/* 183 */     if (paramSequence.getDivisionType() != 0.0F) {
/* 184 */       double d = paramLong * paramSequence.getDivisionType() * paramSequence.getResolution() / 1000000.0D;
/*     */ 
/* 188 */       long l1 = ()d;
/* 189 */       if (paramTempoCache != null) {
/* 190 */         paramTempoCache.currTempo = ((int)paramTempoCache.getTempoMPQAt(l1));
/*     */       }
/* 192 */       return l1;
/*     */     }
/*     */ 
/* 195 */     if (paramTempoCache == null) {
/* 196 */       paramTempoCache = new TempoCache(paramSequence);
/*     */     }
/* 198 */     long[] arrayOfLong = paramTempoCache.ticks;
/* 199 */     int[] arrayOfInt = paramTempoCache.tempos;
/* 200 */     int i = arrayOfInt.length;
/*     */ 
/* 202 */     int j = paramSequence.getResolution();
/*     */ 
/* 204 */     long l2 = 0L; long l3 = 0L; int k = 0; int m = 1;
/*     */ 
/* 208 */     if ((paramLong > 0L) && (i > 0))
/*     */     {
/* 210 */       while (m < i) {
/* 211 */         long l4 = l2 + ticks2microsec(arrayOfLong[m] - arrayOfLong[(m - 1)], arrayOfInt[(m - 1)], j);
/*     */ 
/* 213 */         if (l4 > paramLong) {
/*     */           break;
/*     */         }
/* 216 */         l2 = l4;
/* 217 */         m++;
/*     */       }
/* 219 */       l3 = arrayOfLong[(m - 1)] + microsec2ticks(paramLong - l2, arrayOfInt[(m - 1)], j);
/*     */     }
/*     */ 
/* 223 */     paramTempoCache.currTempo = arrayOfInt[(m - 1)];
/* 224 */     return l3;
/*     */   }
/*     */ 
/*     */   public static int tick2index(Track paramTrack, long paramLong)
/*     */   {
/* 236 */     int i = 0;
/* 237 */     if (paramLong > 0L) {
/* 238 */       int j = 0;
/* 239 */       int k = paramTrack.size() - 1;
/* 240 */       while (j < k)
/*     */       {
/* 242 */         i = j + k >> 1;
/*     */ 
/* 244 */         long l = paramTrack.get(i).getTick();
/* 245 */         if (l == paramLong)
/*     */           break;
/* 247 */         if (l < paramLong)
/*     */         {
/* 249 */           if (j == k - 1)
/*     */           {
/* 251 */             i++;
/* 252 */             break;
/*     */           }
/* 254 */           j = i;
/*     */         }
/*     */         else {
/* 257 */           k = i;
/*     */         }
/*     */       }
/*     */     }
/* 261 */     return i;
/*     */   }
/*     */ 
/*     */   public static class TempoCache
/*     */   {
/*     */     long[] ticks;
/*     */     int[] tempos;
/* 269 */     int snapshotIndex = 0;
/*     */ 
/* 271 */     int snapshotMicro = 0;
/*     */     int currTempo;
/* 275 */     private boolean firstTempoIsFake = false;
/*     */ 
/*     */     public TempoCache()
/*     */     {
/* 279 */       this.ticks = new long[1];
/* 280 */       this.tempos = new int[1];
/* 281 */       this.tempos[0] = 500000;
/* 282 */       this.snapshotIndex = 0;
/* 283 */       this.snapshotMicro = 0;
/*     */     }
/*     */ 
/*     */     public TempoCache(Sequence paramSequence) {
/* 287 */       this();
/* 288 */       refresh(paramSequence);
/*     */     }
/*     */ 
/*     */     public synchronized void refresh(Sequence paramSequence)
/*     */     {
/* 293 */       ArrayList localArrayList = new ArrayList();
/* 294 */       Track[] arrayOfTrack = paramSequence.getTracks();
/*     */       MidiEvent localMidiEvent;
/* 295 */       if (arrayOfTrack.length > 0)
/*     */       {
/* 297 */         Track localTrack = arrayOfTrack[0];
/* 298 */         j = localTrack.size();
/* 299 */         for (k = 0; k < j; k++) {
/* 300 */           localMidiEvent = localTrack.get(k);
/* 301 */           MidiMessage localMidiMessage = localMidiEvent.getMessage();
/* 302 */           if (MidiUtils.isMetaTempo(localMidiMessage))
/*     */           {
/* 304 */             localArrayList.add(localMidiEvent);
/*     */           }
/*     */         }
/*     */       }
/* 308 */       int i = localArrayList.size() + 1;
/* 309 */       this.firstTempoIsFake = true;
/* 310 */       if ((i > 1) && (((MidiEvent)localArrayList.get(0)).getTick() == 0L))
/*     */       {
/* 313 */         i--;
/* 314 */         this.firstTempoIsFake = false;
/*     */       }
/* 316 */       this.ticks = new long[i];
/* 317 */       this.tempos = new int[i];
/* 318 */       int j = 0;
/* 319 */       if (this.firstTempoIsFake)
/*     */       {
/* 321 */         this.ticks[0] = 0L;
/* 322 */         this.tempos[0] = 500000;
/* 323 */         j++;
/*     */       }
/* 325 */       for (int k = 0; k < localArrayList.size(); j++) {
/* 326 */         localMidiEvent = (MidiEvent)localArrayList.get(k);
/* 327 */         this.ticks[j] = localMidiEvent.getTick();
/* 328 */         this.tempos[j] = MidiUtils.getTempoMPQ(localMidiEvent.getMessage());
/*     */ 
/* 325 */         k++;
/*     */       }
/*     */ 
/* 330 */       this.snapshotIndex = 0;
/* 331 */       this.snapshotMicro = 0;
/*     */     }
/*     */ 
/*     */     public int getCurrTempoMPQ() {
/* 335 */       return this.currTempo;
/*     */     }
/*     */ 
/*     */     float getTempoMPQAt(long paramLong) {
/* 339 */       return getTempoMPQAt(paramLong, -1.0F);
/*     */     }
/*     */ 
/*     */     synchronized float getTempoMPQAt(long paramLong, float paramFloat) {
/* 343 */       for (int i = 0; i < this.ticks.length; i++) {
/* 344 */         if (this.ticks[i] > paramLong) {
/* 345 */           if (i > 0) i--;
/* 346 */           if ((paramFloat > 0.0F) && (i == 0) && (this.firstTempoIsFake)) {
/* 347 */             return paramFloat;
/*     */           }
/* 349 */           return this.tempos[i];
/*     */         }
/*     */       }
/* 352 */       return this.tempos[(this.tempos.length - 1)];
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.MidiUtils
 * JD-Core Version:    0.6.2
 */