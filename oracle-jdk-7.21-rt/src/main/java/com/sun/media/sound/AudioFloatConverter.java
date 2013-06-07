/*      */ package com.sun.media.sound;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.DoubleBuffer;
/*      */ import java.nio.FloatBuffer;
/*      */ import javax.sound.sampled.AudioFormat;
/*      */ import javax.sound.sampled.AudioFormat.Encoding;
/*      */ 
/*      */ public abstract class AudioFloatConverter
/*      */ {
/*      */   private AudioFormat format;
/*      */ 
/*      */   public static AudioFloatConverter getConverter(AudioFormat paramAudioFormat)
/*      */   {
/*  908 */     Object localObject = null;
/*  909 */     if (paramAudioFormat.getFrameSize() == 0)
/*  910 */       return null;
/*  911 */     if (paramAudioFormat.getFrameSize() != (paramAudioFormat.getSampleSizeInBits() + 7) / 8 * paramAudioFormat.getChannels())
/*      */     {
/*  913 */       return null;
/*      */     }
/*  915 */     if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
/*  916 */       if (paramAudioFormat.isBigEndian()) {
/*  917 */         if (paramAudioFormat.getSampleSizeInBits() <= 8)
/*  918 */           localObject = new AudioFloatConversion8S(null);
/*  919 */         else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16))
/*      */         {
/*  921 */           localObject = new AudioFloatConversion16SB(null);
/*  922 */         } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24))
/*      */         {
/*  924 */           localObject = new AudioFloatConversion24SB(null);
/*  925 */         } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32))
/*      */         {
/*  927 */           localObject = new AudioFloatConversion32SB(null);
/*  928 */         } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
/*  929 */           localObject = new AudioFloatConversion32xSB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
/*      */         }
/*      */ 
/*      */       }
/*  933 */       else if (paramAudioFormat.getSampleSizeInBits() <= 8)
/*  934 */         localObject = new AudioFloatConversion8S(null);
/*  935 */       else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16))
/*      */       {
/*  937 */         localObject = new AudioFloatConversion16SL(null);
/*  938 */       } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24))
/*      */       {
/*  940 */         localObject = new AudioFloatConversion24SL(null);
/*  941 */       } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32))
/*      */       {
/*  943 */         localObject = new AudioFloatConversion32SL(null);
/*  944 */       } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
/*  945 */         localObject = new AudioFloatConversion32xSL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
/*      */       }
/*      */ 
/*      */     }
/*  949 */     else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
/*  950 */       if (paramAudioFormat.isBigEndian()) {
/*  951 */         if (paramAudioFormat.getSampleSizeInBits() <= 8)
/*  952 */           localObject = new AudioFloatConversion8U(null);
/*  953 */         else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16))
/*      */         {
/*  955 */           localObject = new AudioFloatConversion16UB(null);
/*  956 */         } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24))
/*      */         {
/*  958 */           localObject = new AudioFloatConversion24UB(null);
/*  959 */         } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32))
/*      */         {
/*  961 */           localObject = new AudioFloatConversion32UB(null);
/*  962 */         } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
/*  963 */           localObject = new AudioFloatConversion32xUB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
/*      */         }
/*      */ 
/*      */       }
/*  967 */       else if (paramAudioFormat.getSampleSizeInBits() <= 8)
/*  968 */         localObject = new AudioFloatConversion8U(null);
/*  969 */       else if ((paramAudioFormat.getSampleSizeInBits() > 8) && (paramAudioFormat.getSampleSizeInBits() <= 16))
/*      */       {
/*  971 */         localObject = new AudioFloatConversion16UL(null);
/*  972 */       } else if ((paramAudioFormat.getSampleSizeInBits() > 16) && (paramAudioFormat.getSampleSizeInBits() <= 24))
/*      */       {
/*  974 */         localObject = new AudioFloatConversion24UL(null);
/*  975 */       } else if ((paramAudioFormat.getSampleSizeInBits() > 24) && (paramAudioFormat.getSampleSizeInBits() <= 32))
/*      */       {
/*  977 */         localObject = new AudioFloatConversion32UL(null);
/*  978 */       } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
/*  979 */         localObject = new AudioFloatConversion32xUL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
/*      */       }
/*      */ 
/*      */     }
/*  983 */     else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
/*  984 */       if (paramAudioFormat.getSampleSizeInBits() == 32) {
/*  985 */         if (paramAudioFormat.isBigEndian())
/*  986 */           localObject = new AudioFloatConversion32B(null);
/*      */         else
/*  988 */           localObject = new AudioFloatConversion32L(null);
/*  989 */       } else if (paramAudioFormat.getSampleSizeInBits() == 64) {
/*  990 */         if (paramAudioFormat.isBigEndian())
/*  991 */           localObject = new AudioFloatConversion64B(null);
/*      */         else {
/*  993 */           localObject = new AudioFloatConversion64L(null);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  998 */     if (((paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) && (paramAudioFormat.getSampleSizeInBits() % 8 != 0))
/*      */     {
/* 1001 */       localObject = new AudioFloatLSBFilter((AudioFloatConverter)localObject, paramAudioFormat);
/*      */     }
/*      */ 
/* 1004 */     if (localObject != null)
/* 1005 */       ((AudioFloatConverter)localObject).format = paramAudioFormat;
/* 1006 */     return localObject;
/*      */   }
/*      */ 
/*      */   public AudioFormat getFormat()
/*      */   {
/* 1012 */     return this.format;
/*      */   }
/*      */ 
/*      */   public abstract float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/*      */ 
/*      */   public float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt1, int paramInt2)
/*      */   {
/* 1020 */     return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2)
/*      */   {
/* 1025 */     return toFloatArray(paramArrayOfByte, paramInt1, paramArrayOfFloat, 0, paramInt2);
/*      */   }
/*      */ 
/*      */   public float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt) {
/* 1029 */     return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramInt);
/*      */   }
/*      */ 
/*      */   public float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat) {
/* 1033 */     return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramArrayOfFloat.length);
/*      */   }
/*      */ 
/*      */   public abstract byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
/*      */ 
/*      */   public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*      */   {
/* 1041 */     return toByteArray(paramArrayOfFloat, 0, paramInt1, paramArrayOfByte, paramInt2);
/*      */   }
/*      */ 
/*      */   public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*      */   {
/* 1046 */     return toByteArray(paramArrayOfFloat, paramInt1, paramInt2, paramArrayOfByte, 0);
/*      */   }
/*      */ 
/*      */   public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt, byte[] paramArrayOfByte) {
/* 1050 */     return toByteArray(paramArrayOfFloat, 0, paramInt, paramArrayOfByte, 0);
/*      */   }
/*      */ 
/*      */   public byte[] toByteArray(float[] paramArrayOfFloat, byte[] paramArrayOfByte) {
/* 1054 */     return toByteArray(paramArrayOfFloat, 0, paramArrayOfFloat.length, paramArrayOfByte, 0);
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion16SB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  397 */       int i = paramInt1;
/*  398 */       int j = paramInt2;
/*  399 */       for (int k = 0; k < paramInt3; k++) {
/*  400 */         paramArrayOfFloat[(j++)] = ((short)(paramArrayOfByte[(i++)] << 8 | paramArrayOfByte[(i++)] & 0xFF) * 3.051851E-05F);
/*      */       }
/*      */ 
/*  403 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  408 */       int i = paramInt1;
/*  409 */       int j = paramInt3;
/*  410 */       for (int k = 0; k < paramInt2; k++) {
/*  411 */         int m = (int)(paramArrayOfFloat[(i++)] * 32767.0D);
/*  412 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  413 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  415 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion16SL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  370 */       int i = paramInt1;
/*  371 */       int j = paramInt2 + paramInt3;
/*  372 */       for (int k = paramInt2; k < j; k++) {
/*  373 */         paramArrayOfFloat[k] = ((short)(paramArrayOfByte[(i++)] & 0xFF | paramArrayOfByte[(i++)] << 8) * 3.051851E-05F);
/*      */       }
/*      */ 
/*  377 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  382 */       int i = paramInt3;
/*  383 */       int j = paramInt1 + paramInt2;
/*  384 */       for (int k = paramInt1; k < j; k++) {
/*  385 */         int m = (int)(paramArrayOfFloat[k] * 32767.0D);
/*  386 */         paramArrayOfByte[(i++)] = ((byte)m);
/*  387 */         paramArrayOfByte[(i++)] = ((byte)(m >>> 8));
/*      */       }
/*  389 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion16UB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  449 */       int i = paramInt1;
/*  450 */       int j = paramInt2;
/*  451 */       for (int k = 0; k < paramInt3; k++) {
/*  452 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*  453 */         paramArrayOfFloat[(j++)] = ((m - 32767) * 3.051851E-05F);
/*      */       }
/*  455 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  460 */       int i = paramInt1;
/*  461 */       int j = paramInt3;
/*  462 */       for (int k = 0; k < paramInt2; k++) {
/*  463 */         int m = 32767 + (int)(paramArrayOfFloat[(i++)] * 32767.0D);
/*  464 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  465 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  467 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion16UL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  423 */       int i = paramInt1;
/*  424 */       int j = paramInt2;
/*  425 */       for (int k = 0; k < paramInt3; k++) {
/*  426 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8;
/*  427 */         paramArrayOfFloat[(j++)] = ((m - 32767) * 3.051851E-05F);
/*      */       }
/*  429 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  434 */       int i = paramInt1;
/*  435 */       int j = paramInt3;
/*  436 */       for (int k = 0; k < paramInt2; k++) {
/*  437 */         int m = 32767 + (int)(paramArrayOfFloat[(i++)] * 32767.0D);
/*  438 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  439 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*      */       }
/*  441 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion24SB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  513 */       int i = paramInt1;
/*  514 */       int j = paramInt2;
/*  515 */       for (int k = 0; k < paramInt3; k++) {
/*  516 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  518 */         if (m > 8388607)
/*  519 */           m -= 16777216;
/*  520 */         paramArrayOfFloat[(j++)] = (m * 1.192093E-07F);
/*      */       }
/*  522 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  527 */       int i = paramInt1;
/*  528 */       int j = paramInt3;
/*  529 */       for (int k = 0; k < paramInt2; k++) {
/*  530 */         int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
/*  531 */         if (m < 0)
/*  532 */           m += 16777216;
/*  533 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  534 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  535 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  537 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion24SL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  481 */       int i = paramInt1;
/*  482 */       int j = paramInt2;
/*  483 */       for (int k = 0; k < paramInt3; k++) {
/*  484 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16;
/*      */ 
/*  486 */         if (m > 8388607)
/*  487 */           m -= 16777216;
/*  488 */         paramArrayOfFloat[(j++)] = (m * 1.192093E-07F);
/*      */       }
/*  490 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  495 */       int i = paramInt1;
/*  496 */       int j = paramInt3;
/*  497 */       for (int k = 0; k < paramInt2; k++) {
/*  498 */         int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
/*  499 */         if (m < 0)
/*  500 */           m += 16777216;
/*  501 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  502 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  503 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*      */       }
/*  505 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion24UB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  575 */       int i = paramInt1;
/*  576 */       int j = paramInt2;
/*  577 */       for (int k = 0; k < paramInt3; k++) {
/*  578 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  580 */         m -= 8388607;
/*  581 */         paramArrayOfFloat[(j++)] = (m * 1.192093E-07F);
/*      */       }
/*  583 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  588 */       int i = paramInt1;
/*  589 */       int j = paramInt3;
/*  590 */       for (int k = 0; k < paramInt2; k++) {
/*  591 */         int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
/*  592 */         m += 8388607;
/*  593 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  594 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  595 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  597 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion24UL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  545 */       int i = paramInt1;
/*  546 */       int j = paramInt2;
/*  547 */       for (int k = 0; k < paramInt3; k++) {
/*  548 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16;
/*      */ 
/*  550 */         m -= 8388607;
/*  551 */         paramArrayOfFloat[(j++)] = (m * 1.192093E-07F);
/*      */       }
/*  553 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  558 */       int i = paramInt1;
/*  559 */       int j = paramInt3;
/*  560 */       for (int k = 0; k < paramInt2; k++) {
/*  561 */         int m = (int)(paramArrayOfFloat[(i++)] * 8388607.0F);
/*  562 */         m += 8388607;
/*  563 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  564 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  565 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*      */       }
/*  567 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32B extends AudioFloatConverter
/*      */   {
/*  276 */     ByteBuffer bytebuffer = null;
/*      */ 
/*  278 */     FloatBuffer floatbuffer = null;
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  282 */       int i = paramInt3 * 4;
/*  283 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  284 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
/*      */ 
/*  286 */         this.floatbuffer = this.bytebuffer.asFloatBuffer();
/*      */       }
/*  288 */       this.bytebuffer.position(0);
/*  289 */       this.floatbuffer.position(0);
/*  290 */       this.bytebuffer.put(paramArrayOfByte, paramInt1, i);
/*  291 */       this.floatbuffer.get(paramArrayOfFloat, paramInt2, paramInt3);
/*  292 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  297 */       int i = paramInt2 * 4;
/*  298 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  299 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
/*      */ 
/*  301 */         this.floatbuffer = this.bytebuffer.asFloatBuffer();
/*      */       }
/*  303 */       this.floatbuffer.position(0);
/*  304 */       this.bytebuffer.position(0);
/*  305 */       this.floatbuffer.put(paramArrayOfFloat, paramInt1, paramInt2);
/*  306 */       this.bytebuffer.get(paramArrayOfByte, paramInt3, i);
/*  307 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32L extends AudioFloatConverter
/*      */   {
/*  239 */     ByteBuffer bytebuffer = null;
/*      */ 
/*  241 */     FloatBuffer floatbuffer = null;
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  245 */       int i = paramInt3 * 4;
/*  246 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  247 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
/*      */ 
/*  249 */         this.floatbuffer = this.bytebuffer.asFloatBuffer();
/*      */       }
/*  251 */       this.bytebuffer.position(0);
/*  252 */       this.floatbuffer.position(0);
/*  253 */       this.bytebuffer.put(paramArrayOfByte, paramInt1, i);
/*  254 */       this.floatbuffer.get(paramArrayOfFloat, paramInt2, paramInt3);
/*  255 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  260 */       int i = paramInt2 * 4;
/*  261 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  262 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
/*      */ 
/*  264 */         this.floatbuffer = this.bytebuffer.asFloatBuffer();
/*      */       }
/*  266 */       this.floatbuffer.position(0);
/*  267 */       this.bytebuffer.position(0);
/*  268 */       this.floatbuffer.put(paramArrayOfFloat, paramInt1, paramInt2);
/*  269 */       this.bytebuffer.get(paramArrayOfByte, paramInt3, i);
/*  270 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32SB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  641 */       int i = paramInt1;
/*  642 */       int j = paramInt2;
/*  643 */       for (int k = 0; k < paramInt3; k++) {
/*  644 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  647 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  649 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  654 */       int i = paramInt1;
/*  655 */       int j = paramInt3;
/*  656 */       for (int k = 0; k < paramInt2; k++) {
/*  657 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  658 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*  659 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  660 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  661 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  663 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32SL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  611 */       int i = paramInt1;
/*  612 */       int j = paramInt2;
/*  613 */       for (int k = 0; k < paramInt3; k++) {
/*  614 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
/*      */ 
/*  617 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  619 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  624 */       int i = paramInt1;
/*  625 */       int j = paramInt3;
/*  626 */       for (int k = 0; k < paramInt2; k++) {
/*  627 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  628 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  629 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  630 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  631 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*      */       }
/*  633 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32UB extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  704 */       int i = paramInt1;
/*  705 */       int j = paramInt2;
/*  706 */       for (int k = 0; k < paramInt3; k++) {
/*  707 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  710 */         m -= 2147483647;
/*  711 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  713 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  718 */       int i = paramInt1;
/*  719 */       int j = paramInt3;
/*  720 */       for (int k = 0; k < paramInt2; k++) {
/*  721 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  722 */         m += 2147483647;
/*  723 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*  724 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  725 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  726 */         paramArrayOfByte[(j++)] = ((byte)m);
/*      */       }
/*  728 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32UL extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  671 */       int i = paramInt1;
/*  672 */       int j = paramInt2;
/*  673 */       for (int k = 0; k < paramInt3; k++) {
/*  674 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
/*      */ 
/*  677 */         m -= 2147483647;
/*  678 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  680 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  685 */       int i = paramInt1;
/*  686 */       int j = paramInt3;
/*  687 */       for (int k = 0; k < paramInt2; k++) {
/*  688 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  689 */         m += 2147483647;
/*  690 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  691 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  692 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  693 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*      */       }
/*  695 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32xSB extends AudioFloatConverter
/*      */   {
/*      */     final int xbytes;
/*      */ 
/*      */     public AudioFloatConversion32xSB(int paramInt)
/*      */     {
/*  785 */       this.xbytes = paramInt;
/*      */     }
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  790 */       int i = paramInt1;
/*  791 */       int j = paramInt2;
/*  792 */       for (int k = 0; k < paramInt3; k++) {
/*  793 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  797 */         i += this.xbytes;
/*  798 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  800 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  805 */       int i = paramInt1;
/*  806 */       int j = paramInt3;
/*  807 */       for (int k = 0; k < paramInt2; k++) {
/*  808 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  809 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*  810 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  811 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  812 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  813 */         for (int n = 0; n < this.xbytes; n++) {
/*  814 */           paramArrayOfByte[(j++)] = 0;
/*      */         }
/*      */       }
/*  817 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32xSL extends AudioFloatConverter
/*      */   {
/*      */     final int xbytes;
/*      */ 
/*      */     public AudioFloatConversion32xSL(int paramInt)
/*      */     {
/*  744 */       this.xbytes = paramInt;
/*      */     }
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  749 */       int i = paramInt1;
/*  750 */       int j = paramInt2;
/*  751 */       for (int k = 0; k < paramInt3; k++) {
/*  752 */         i += this.xbytes;
/*  753 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
/*      */ 
/*  756 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  758 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  763 */       int i = paramInt1;
/*  764 */       int j = paramInt3;
/*  765 */       for (int k = 0; k < paramInt2; k++) {
/*  766 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  767 */         for (int n = 0; n < this.xbytes; n++) {
/*  768 */           paramArrayOfByte[(j++)] = 0;
/*      */         }
/*  770 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  771 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  772 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  773 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*      */       }
/*  775 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32xUB extends AudioFloatConverter
/*      */   {
/*      */     final int xbytes;
/*      */ 
/*      */     public AudioFloatConversion32xUB(int paramInt)
/*      */     {
/*  870 */       this.xbytes = paramInt;
/*      */     }
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  875 */       int i = paramInt1;
/*  876 */       int j = paramInt2;
/*  877 */       for (int k = 0; k < paramInt3; k++) {
/*  878 */         int m = (paramArrayOfByte[(i++)] & 0xFF) << 24 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 8 | paramArrayOfByte[(i++)] & 0xFF;
/*      */ 
/*  881 */         i += this.xbytes;
/*  882 */         m -= 2147483647;
/*  883 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  885 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  890 */       int i = paramInt1;
/*  891 */       int j = paramInt3;
/*  892 */       for (int k = 0; k < paramInt2; k++) {
/*  893 */         int m = (int)(paramArrayOfFloat[(i++)] * 2147483647.0D);
/*  894 */         m += 2147483647;
/*  895 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*  896 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  897 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  898 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  899 */         for (int n = 0; n < this.xbytes; n++) {
/*  900 */           paramArrayOfByte[(j++)] = 0;
/*      */         }
/*      */       }
/*  903 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion32xUL extends AudioFloatConverter
/*      */   {
/*      */     final int xbytes;
/*      */ 
/*      */     public AudioFloatConversion32xUL(int paramInt)
/*      */     {
/*  827 */       this.xbytes = paramInt;
/*      */     }
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  832 */       int i = paramInt1;
/*  833 */       int j = paramInt2;
/*  834 */       for (int k = 0; k < paramInt3; k++) {
/*  835 */         i += this.xbytes;
/*  836 */         int m = paramArrayOfByte[(i++)] & 0xFF | (paramArrayOfByte[(i++)] & 0xFF) << 8 | (paramArrayOfByte[(i++)] & 0xFF) << 16 | (paramArrayOfByte[(i++)] & 0xFF) << 24;
/*      */ 
/*  839 */         m -= 2147483647;
/*  840 */         paramArrayOfFloat[(j++)] = (m * 4.656613E-10F);
/*      */       }
/*  842 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  847 */       int i = paramInt1;
/*  848 */       int j = paramInt3;
/*  849 */       for (int k = 0; k < paramInt2; k++) {
/*  850 */         int m = (int)(paramArrayOfFloat[(i++)] * 2.147484E+09F);
/*  851 */         m += 2147483647;
/*  852 */         for (int n = 0; n < this.xbytes; n++) {
/*  853 */           paramArrayOfByte[(j++)] = 0;
/*      */         }
/*  855 */         paramArrayOfByte[(j++)] = ((byte)m);
/*  856 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 8));
/*  857 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 16));
/*  858 */         paramArrayOfByte[(j++)] = ((byte)(m >>> 24));
/*      */       }
/*  860 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion64B extends AudioFloatConverter
/*      */   {
/*  181 */     ByteBuffer bytebuffer = null;
/*      */ 
/*  183 */     DoubleBuffer floatbuffer = null;
/*      */ 
/*  185 */     double[] double_buff = null;
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  189 */       int i = paramInt3 * 8;
/*  190 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  191 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
/*      */ 
/*  193 */         this.floatbuffer = this.bytebuffer.asDoubleBuffer();
/*      */       }
/*  195 */       this.bytebuffer.position(0);
/*  196 */       this.floatbuffer.position(0);
/*  197 */       this.bytebuffer.put(paramArrayOfByte, paramInt1, i);
/*  198 */       if ((this.double_buff == null) || (this.double_buff.length < paramInt3 + paramInt2))
/*      */       {
/*  200 */         this.double_buff = new double[paramInt3 + paramInt2];
/*  201 */       }this.floatbuffer.get(this.double_buff, paramInt2, paramInt3);
/*  202 */       int j = paramInt2 + paramInt3;
/*  203 */       for (int k = paramInt2; k < j; k++) {
/*  204 */         paramArrayOfFloat[k] = ((float)this.double_buff[k]);
/*      */       }
/*  206 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  211 */       int i = paramInt2 * 8;
/*  212 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  213 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
/*      */ 
/*  215 */         this.floatbuffer = this.bytebuffer.asDoubleBuffer();
/*      */       }
/*  217 */       this.floatbuffer.position(0);
/*  218 */       this.bytebuffer.position(0);
/*  219 */       if ((this.double_buff == null) || (this.double_buff.length < paramInt1 + paramInt2))
/*  220 */         this.double_buff = new double[paramInt1 + paramInt2];
/*  221 */       int j = paramInt1 + paramInt2;
/*  222 */       for (int k = paramInt1; k < j; k++) {
/*  223 */         this.double_buff[k] = paramArrayOfFloat[k];
/*      */       }
/*  225 */       this.floatbuffer.put(this.double_buff, paramInt1, paramInt2);
/*  226 */       this.bytebuffer.get(paramArrayOfByte, paramInt3, i);
/*  227 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion64L extends AudioFloatConverter
/*      */   {
/*  129 */     ByteBuffer bytebuffer = null;
/*      */ 
/*  131 */     DoubleBuffer floatbuffer = null;
/*      */ 
/*  133 */     double[] double_buff = null;
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  137 */       int i = paramInt3 * 8;
/*  138 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  139 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
/*      */ 
/*  141 */         this.floatbuffer = this.bytebuffer.asDoubleBuffer();
/*      */       }
/*  143 */       this.bytebuffer.position(0);
/*  144 */       this.floatbuffer.position(0);
/*  145 */       this.bytebuffer.put(paramArrayOfByte, paramInt1, i);
/*  146 */       if ((this.double_buff == null) || (this.double_buff.length < paramInt3 + paramInt2))
/*      */       {
/*  148 */         this.double_buff = new double[paramInt3 + paramInt2];
/*  149 */       }this.floatbuffer.get(this.double_buff, paramInt2, paramInt3);
/*  150 */       int j = paramInt2 + paramInt3;
/*  151 */       for (int k = paramInt2; k < j; k++) {
/*  152 */         paramArrayOfFloat[k] = ((float)this.double_buff[k]);
/*      */       }
/*  154 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  159 */       int i = paramInt2 * 8;
/*  160 */       if ((this.bytebuffer == null) || (this.bytebuffer.capacity() < i)) {
/*  161 */         this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
/*      */ 
/*  163 */         this.floatbuffer = this.bytebuffer.asDoubleBuffer();
/*      */       }
/*  165 */       this.floatbuffer.position(0);
/*  166 */       this.bytebuffer.position(0);
/*  167 */       if ((this.double_buff == null) || (this.double_buff.length < paramInt1 + paramInt2))
/*  168 */         this.double_buff = new double[paramInt1 + paramInt2];
/*  169 */       int j = paramInt1 + paramInt2;
/*  170 */       for (int k = paramInt1; k < j; k++) {
/*  171 */         this.double_buff[k] = paramArrayOfFloat[k];
/*      */       }
/*  173 */       this.floatbuffer.put(this.double_buff, paramInt1, paramInt2);
/*  174 */       this.bytebuffer.get(paramArrayOfByte, paramInt3, i);
/*  175 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion8S extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  321 */       int i = paramInt1;
/*  322 */       int j = paramInt2;
/*  323 */       for (int k = 0; k < paramInt3; k++)
/*  324 */         paramArrayOfFloat[(j++)] = (paramArrayOfByte[(i++)] * 0.007874016F);
/*  325 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  330 */       int i = paramInt1;
/*  331 */       int j = paramInt3;
/*  332 */       for (int k = 0; k < paramInt2; k++)
/*  333 */         paramArrayOfByte[(j++)] = ((byte)(int)(paramArrayOfFloat[(i++)] * 127.0F));
/*  334 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatConversion8U extends AudioFloatConverter
/*      */   {
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  342 */       int i = paramInt1;
/*  343 */       int j = paramInt2;
/*  344 */       for (int k = 0; k < paramInt3; k++) {
/*  345 */         paramArrayOfFloat[(j++)] = (((paramArrayOfByte[(i++)] & 0xFF) - 127) * 0.007874016F);
/*      */       }
/*  347 */       return paramArrayOfFloat;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*  352 */       int i = paramInt1;
/*  353 */       int j = paramInt3;
/*  354 */       for (int k = 0; k < paramInt2; k++)
/*  355 */         paramArrayOfByte[(j++)] = ((byte)(int)(127.0F + paramArrayOfFloat[(i++)] * 127.0F));
/*  356 */       return paramArrayOfByte;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AudioFloatLSBFilter extends AudioFloatConverter
/*      */   {
/*      */     private AudioFloatConverter converter;
/*      */     private final int offset;
/*      */     private final int stepsize;
/*      */     private final byte mask;
/*      */     private byte[] mask_buffer;
/*      */ 
/*      */     public AudioFloatLSBFilter(AudioFloatConverter paramAudioFloatConverter, AudioFormat paramAudioFormat)
/*      */     {
/*   66 */       int i = paramAudioFormat.getSampleSizeInBits();
/*   67 */       boolean bool = paramAudioFormat.isBigEndian();
/*   68 */       this.converter = paramAudioFloatConverter;
/*   69 */       this.stepsize = ((i + 7) / 8);
/*   70 */       this.offset = (bool ? this.stepsize - 1 : 0);
/*   71 */       int j = i % 8;
/*   72 */       if (j == 0)
/*   73 */         this.mask = 0;
/*   74 */       else if (j == 1)
/*   75 */         this.mask = -128;
/*   76 */       else if (j == 2)
/*   77 */         this.mask = -64;
/*   78 */       else if (j == 3)
/*   79 */         this.mask = -32;
/*   80 */       else if (j == 4)
/*   81 */         this.mask = -16;
/*   82 */       else if (j == 5)
/*   83 */         this.mask = -8;
/*   84 */       else if (j == 6)
/*   85 */         this.mask = -4;
/*   86 */       else if (j == 7)
/*   87 */         this.mask = -2;
/*      */       else
/*   89 */         this.mask = -1;
/*      */     }
/*      */ 
/*      */     public byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
/*      */     {
/*   94 */       byte[] arrayOfByte = this.converter.toByteArray(paramArrayOfFloat, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
/*      */ 
/*   97 */       int i = paramInt2 * this.stepsize;
/*   98 */       for (int j = paramInt3 + this.offset; j < i; j += this.stepsize) {
/*   99 */         paramArrayOfByte[j] = ((byte)(paramArrayOfByte[j] & this.mask));
/*      */       }
/*      */ 
/*  102 */       return arrayOfByte;
/*      */     }
/*      */ 
/*      */     public float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
/*      */     {
/*  107 */       if ((this.mask_buffer == null) || (this.mask_buffer.length < paramArrayOfByte.length))
/*  108 */         this.mask_buffer = new byte[paramArrayOfByte.length];
/*  109 */       System.arraycopy(paramArrayOfByte, 0, this.mask_buffer, 0, paramArrayOfByte.length);
/*  110 */       int i = paramInt3 * this.stepsize;
/*  111 */       for (int j = paramInt1 + this.offset; j < i; j += this.stepsize) {
/*  112 */         this.mask_buffer[j] = ((byte)(this.mask_buffer[j] & this.mask));
/*      */       }
/*  114 */       float[] arrayOfFloat = this.converter.toFloatArray(this.mask_buffer, paramInt1, paramArrayOfFloat, paramInt2, paramInt3);
/*      */ 
/*  116 */       return arrayOfFloat;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AudioFloatConverter
 * JD-Core Version:    0.6.2
 */