/*      */ package com.sun.media.sound;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.util.Vector;
/*      */ import javax.sound.sampled.AudioFormat;
/*      */ import javax.sound.sampled.AudioFormat.Encoding;
/*      */ import javax.sound.sampled.AudioInputStream;
/*      */ import javax.sound.sampled.BooleanControl;
/*      */ import javax.sound.sampled.BooleanControl.Type;
/*      */ import javax.sound.sampled.Clip;
/*      */ import javax.sound.sampled.Control;
/*      */ import javax.sound.sampled.DataLine.Info;
/*      */ import javax.sound.sampled.FloatControl;
/*      */ import javax.sound.sampled.FloatControl.Type;
/*      */ import javax.sound.sampled.Line;
/*      */ import javax.sound.sampled.Line.Info;
/*      */ import javax.sound.sampled.LineUnavailableException;
/*      */ import javax.sound.sampled.SourceDataLine;
/*      */ import javax.sound.sampled.TargetDataLine;
/*      */ 
/*      */ class DirectAudioDevice extends AbstractMixer
/*      */ {
/*      */   private static final int CLIP_BUFFER_TIME = 1000;
/*      */   private static final int DEFAULT_LINE_BUFFER_TIME = 500;
/*   55 */   private int deviceCountOpened = 0;
/*      */ 
/*   58 */   private int deviceCountStarted = 0;
/*      */ 
/*      */   DirectAudioDevice(DirectAudioDeviceProvider.DirectAudioDeviceInfo paramDirectAudioDeviceInfo)
/*      */   {
/*   63 */     super(paramDirectAudioDeviceInfo, null, null, null);
/*      */ 
/*   71 */     DirectDLI localDirectDLI1 = createDataLineInfo(true);
/*   72 */     if (localDirectDLI1 != null) {
/*   73 */       this.sourceLineInfo = new Line.Info[2];
/*      */ 
/*   75 */       this.sourceLineInfo[0] = localDirectDLI1;
/*      */ 
/*   77 */       this.sourceLineInfo[1] = new DirectDLI(Clip.class, localDirectDLI1.getFormats(), localDirectDLI1.getHardwareFormats(), 32, -1, null);
/*      */     }
/*      */     else
/*      */     {
/*   82 */       this.sourceLineInfo = new Line.Info[0];
/*      */     }
/*      */ 
/*   86 */     DirectDLI localDirectDLI2 = createDataLineInfo(false);
/*   87 */     if (localDirectDLI2 != null) {
/*   88 */       this.targetLineInfo = new Line.Info[1];
/*   89 */       this.targetLineInfo[0] = localDirectDLI2;
/*      */     } else {
/*   91 */       this.targetLineInfo = new Line.Info[0];
/*      */     }
/*      */   }
/*      */ 
/*      */   private DirectDLI createDataLineInfo(boolean paramBoolean)
/*      */   {
/*   97 */     Vector localVector = new Vector();
/*   98 */     AudioFormat[] arrayOfAudioFormat1 = null;
/*   99 */     AudioFormat[] arrayOfAudioFormat2 = null;
/*      */ 
/*  101 */     synchronized (localVector) {
/*  102 */       nGetFormats(getMixerIndex(), getDeviceID(), paramBoolean, localVector);
/*      */ 
/*  105 */       if (localVector.size() > 0) {
/*  106 */         int i = localVector.size();
/*  107 */         int j = i;
/*  108 */         arrayOfAudioFormat1 = new AudioFormat[i];
/*      */         boolean bool2;
/*  109 */         for (int k = 0; k < i; k++) {
/*  110 */           AudioFormat localAudioFormat1 = (AudioFormat)localVector.elementAt(k);
/*  111 */           arrayOfAudioFormat1[k] = localAudioFormat1;
/*  112 */           int n = localAudioFormat1.getSampleSizeInBits();
/*  113 */           boolean bool1 = localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
/*  114 */           bool2 = localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
/*  115 */           if ((bool1) || (bool2))
/*      */           {
/*  117 */             j++;
/*      */           }
/*      */         }
/*  120 */         arrayOfAudioFormat2 = new AudioFormat[j];
/*  121 */         k = 0;
/*  122 */         for (int m = 0; m < i; m++) {
/*  123 */           AudioFormat localAudioFormat2 = arrayOfAudioFormat1[m];
/*  124 */           arrayOfAudioFormat2[(k++)] = localAudioFormat2;
/*  125 */           int i1 = localAudioFormat2.getSampleSizeInBits();
/*  126 */           bool2 = localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
/*  127 */           boolean bool3 = localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
/*      */ 
/*  129 */           if (i1 == 8)
/*      */           {
/*  131 */             if (bool2) {
/*  132 */               arrayOfAudioFormat2[(k++)] = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), localAudioFormat2.isBigEndian());
/*      */             }
/*  138 */             else if (bool3) {
/*  139 */               arrayOfAudioFormat2[(k++)] = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), localAudioFormat2.isBigEndian());
/*      */             }
/*      */ 
/*      */           }
/*  145 */           else if ((i1 > 8) && ((bool2) || (bool3)))
/*      */           {
/*  147 */             arrayOfAudioFormat2[(k++)] = new AudioFormat(localAudioFormat2.getEncoding(), localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), !localAudioFormat2.isBigEndian());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  160 */     if (arrayOfAudioFormat2 != null) {
/*  161 */       return new DirectDLI(paramBoolean ? SourceDataLine.class : TargetDataLine.class, arrayOfAudioFormat2, arrayOfAudioFormat1, 32, -1, null);
/*      */     }
/*      */ 
/*  166 */     return null;
/*      */   }
/*      */ 
/*      */   public Line getLine(Line.Info paramInfo)
/*      */     throws LineUnavailableException
/*      */   {
/*  172 */     Line.Info localInfo = getLineInfo(paramInfo);
/*  173 */     if (localInfo == null) {
/*  174 */       throw new IllegalArgumentException("Line unsupported: " + paramInfo);
/*      */     }
/*  176 */     if ((localInfo instanceof DataLine.Info))
/*      */     {
/*  178 */       DataLine.Info localInfo1 = (DataLine.Info)localInfo;
/*      */ 
/*  180 */       int i = -1;
/*      */ 
/*  185 */       AudioFormat[] arrayOfAudioFormat = null;
/*      */ 
/*  187 */       if ((paramInfo instanceof DataLine.Info)) {
/*  188 */         arrayOfAudioFormat = ((DataLine.Info)paramInfo).getFormats();
/*  189 */         i = ((DataLine.Info)paramInfo).getMaxBufferSize();
/*      */       }
/*      */       AudioFormat localAudioFormat;
/*  192 */       if ((arrayOfAudioFormat == null) || (arrayOfAudioFormat.length == 0))
/*      */       {
/*  194 */         localAudioFormat = null;
/*      */       }
/*      */       else
/*      */       {
/*  198 */         localAudioFormat = arrayOfAudioFormat[(arrayOfAudioFormat.length - 1)];
/*      */ 
/*  201 */         if (!Toolkit.isFullySpecifiedPCMFormat(localAudioFormat)) {
/*  202 */           localAudioFormat = null;
/*      */         }
/*      */       }
/*      */ 
/*  206 */       if (localInfo1.getLineClass().isAssignableFrom(DirectSDL.class)) {
/*  207 */         return new DirectSDL(localInfo1, localAudioFormat, i, this, null);
/*      */       }
/*  209 */       if (localInfo1.getLineClass().isAssignableFrom(DirectClip.class)) {
/*  210 */         return new DirectClip(localInfo1, localAudioFormat, i, this, null);
/*      */       }
/*  212 */       if (localInfo1.getLineClass().isAssignableFrom(DirectTDL.class)) {
/*  213 */         return new DirectTDL(localInfo1, localAudioFormat, i, this, null);
/*      */       }
/*      */     }
/*  216 */     throw new IllegalArgumentException("Line unsupported: " + paramInfo);
/*      */   }
/*      */ 
/*      */   public int getMaxLines(Line.Info paramInfo)
/*      */   {
/*  221 */     Line.Info localInfo = getLineInfo(paramInfo);
/*      */ 
/*  224 */     if (localInfo == null) {
/*  225 */       return 0;
/*      */     }
/*      */ 
/*  228 */     if ((localInfo instanceof DataLine.Info))
/*      */     {
/*  230 */       return getMaxSimulLines();
/*      */     }
/*      */ 
/*  233 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void implOpen()
/*      */     throws LineUnavailableException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void implClose()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void implStart()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void implStop()
/*      */   {
/*      */   }
/*      */ 
/*      */   int getMixerIndex()
/*      */   {
/*  257 */     return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getIndex();
/*      */   }
/*      */ 
/*      */   int getDeviceID() {
/*  261 */     return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getDeviceID();
/*      */   }
/*      */ 
/*      */   int getMaxSimulLines() {
/*  265 */     return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getMaxSimulLines();
/*      */   }
/*      */ 
/*      */   private static void addFormat(Vector paramVector, int paramInt1, int paramInt2, int paramInt3, float paramFloat, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  270 */     AudioFormat.Encoding localEncoding = null;
/*  271 */     switch (paramInt4) {
/*      */     case 0:
/*  273 */       localEncoding = paramBoolean1 ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED;
/*  274 */       break;
/*      */     case 1:
/*  276 */       localEncoding = AudioFormat.Encoding.ULAW;
/*  277 */       if (paramInt1 != 8)
/*      */       {
/*  279 */         paramInt1 = 8; paramInt2 = paramInt3; } break;
/*      */     case 2:
/*  283 */       localEncoding = AudioFormat.Encoding.ALAW;
/*  284 */       if (paramInt1 != 8)
/*      */       {
/*  286 */         paramInt1 = 8; paramInt2 = paramInt3;
/*      */       }
/*      */       break;
/*      */     }
/*  290 */     if (localEncoding == null)
/*      */     {
/*  292 */       return;
/*      */     }
/*  294 */     if (paramInt2 <= 0) {
/*  295 */       if (paramInt3 > 0)
/*  296 */         paramInt2 = (paramInt1 + 7) / 8 * paramInt3;
/*      */       else {
/*  298 */         paramInt2 = -1;
/*      */       }
/*      */     }
/*  301 */     paramVector.add(new AudioFormat(localEncoding, paramFloat, paramInt1, paramInt3, paramInt2, paramFloat, paramBoolean2));
/*      */   }
/*      */ 
/*      */   protected static AudioFormat getSignOrEndianChangedFormat(AudioFormat paramAudioFormat) {
/*  305 */     boolean bool1 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
/*  306 */     boolean bool2 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
/*  307 */     if ((paramAudioFormat.getSampleSizeInBits() > 8) && (bool1))
/*      */     {
/*  309 */       return new AudioFormat(paramAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), !paramAudioFormat.isBigEndian());
/*      */     }
/*      */ 
/*  313 */     if ((paramAudioFormat.getSampleSizeInBits() == 8) && ((bool1) || (bool2)))
/*      */     {
/*  315 */       return new AudioFormat(bool1 ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), paramAudioFormat.isBigEndian());
/*      */     }
/*      */ 
/*  319 */     return null;
/*      */   }
/*      */ 
/*      */   private static native void nGetFormats(int paramInt1, int paramInt2, boolean paramBoolean, Vector paramVector);
/*      */ 
/*      */   private static native long nOpen(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, float paramFloat, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2, boolean paramBoolean3, int paramInt7)
/*      */     throws LineUnavailableException;
/*      */ 
/*      */   private static native void nStart(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native void nStop(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native void nClose(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native int nWrite(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2);
/*      */ 
/*      */   private static native int nRead(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   private static native int nGetBufferSize(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native boolean nIsStillDraining(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native void nFlush(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native int nAvailable(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native long nGetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
/*      */ 
/*      */   private static native void nSetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
/*      */ 
/*      */   private static native boolean nRequiresServicing(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static native void nService(long paramLong, boolean paramBoolean);
/*      */ 
/*      */   private static class DirectBAOS extends ByteArrayOutputStream
/*      */   {
/*      */     public byte[] getInternalBuffer()
/*      */     {
/* 1451 */       return this.buf;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DirectClip extends DirectAudioDevice.DirectDL
/*      */     implements Clip, Runnable, AutoClosingClip
/*      */   {
/*      */     private Thread thread;
/* 1017 */     private byte[] audioData = null;
/*      */     private int frameSize;
/*      */     private int m_lengthInFrames;
/*      */     private int loopCount;
/*      */     private int clipBytePosition;
/*      */     private int newFramePosition;
/*      */     private int loopStartFrame;
/*      */     private int loopEndFrame;
/* 1027 */     private boolean autoclosing = false;
/*      */ 
/*      */     private DirectClip(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
/*      */     {
/* 1034 */       super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), true);
/*      */     }
/*      */ 
/*      */     public void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */       throws LineUnavailableException
/*      */     {
/* 1044 */       Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
/*      */ 
/* 1046 */       byte[] arrayOfByte = new byte[paramInt2];
/* 1047 */       System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
/* 1048 */       open(paramAudioFormat, paramArrayOfByte, paramInt2 / paramAudioFormat.getFrameSize());
/*      */     }
/*      */ 
/*      */     private void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt)
/*      */       throws LineUnavailableException
/*      */     {
/* 1056 */       Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
/*      */ 
/* 1058 */       synchronized (this.mixer)
/*      */       {
/* 1063 */         if (isOpen()) {
/* 1064 */           throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
/*      */         }
/*      */ 
/* 1068 */         this.audioData = paramArrayOfByte;
/* 1069 */         this.frameSize = paramAudioFormat.getFrameSize();
/* 1070 */         this.m_lengthInFrames = paramInt;
/*      */ 
/* 1072 */         this.bytePosition = 0L;
/* 1073 */         this.clipBytePosition = 0;
/* 1074 */         this.newFramePosition = -1;
/* 1075 */         this.loopStartFrame = 0;
/* 1076 */         this.loopEndFrame = (paramInt - 1);
/* 1077 */         this.loopCount = 0;
/*      */         try
/*      */         {
/* 1081 */           open(paramAudioFormat, (int)Toolkit.millis2bytes(paramAudioFormat, 1000L));
/*      */         } catch (LineUnavailableException localLineUnavailableException) {
/* 1083 */           this.audioData = null;
/* 1084 */           throw localLineUnavailableException;
/*      */         } catch (IllegalArgumentException localIllegalArgumentException) {
/* 1086 */           this.audioData = null;
/* 1087 */           throw localIllegalArgumentException;
/*      */         }
/*      */ 
/* 1091 */         int i = 6;
/*      */ 
/* 1093 */         this.thread = JSSecurityManager.createThread(this, "Direct Clip", true, i, false);
/*      */ 
/* 1101 */         this.thread.start();
/*      */       }
/*      */ 
/* 1104 */       if (isAutoClosing())
/* 1105 */         getEventDispatcher().autoClosingClipOpened(this);
/*      */     }
/*      */ 
/*      */     public void open(AudioInputStream paramAudioInputStream)
/*      */       throws LineUnavailableException, IOException
/*      */     {
/* 1114 */       Toolkit.isFullySpecifiedAudioFormat(this.format);
/*      */ 
/* 1116 */       synchronized (this.mixer)
/*      */       {
/* 1118 */         byte[] arrayOfByte1 = null;
/*      */ 
/* 1120 */         if (isOpen()) {
/* 1121 */           throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
/*      */         }
/*      */ 
/* 1124 */         int i = (int)paramAudioInputStream.getFrameLength();
/*      */ 
/* 1127 */         int j = 0;
/*      */         int k;
/* 1128 */         if (i != -1)
/*      */         {
/* 1130 */           k = i * paramAudioInputStream.getFormat().getFrameSize();
/* 1131 */           arrayOfByte1 = new byte[k];
/*      */ 
/* 1133 */           int m = k;
/* 1134 */           int n = 0;
/* 1135 */           while ((m > 0) && (n >= 0)) {
/* 1136 */             n = paramAudioInputStream.read(arrayOfByte1, j, m);
/* 1137 */             if (n > 0) {
/* 1138 */               j += n;
/* 1139 */               m -= n;
/*      */             }
/* 1141 */             else if (n == 0) {
/* 1142 */               Thread.yield();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1150 */           k = 16384;
/* 1151 */           DirectAudioDevice.DirectBAOS localDirectBAOS = new DirectAudioDevice.DirectBAOS();
/* 1152 */           byte[] arrayOfByte2 = new byte[k];
/* 1153 */           int i1 = 0;
/* 1154 */           while (i1 >= 0) {
/* 1155 */             i1 = paramAudioInputStream.read(arrayOfByte2, 0, arrayOfByte2.length);
/* 1156 */             if (i1 > 0) {
/* 1157 */               localDirectBAOS.write(arrayOfByte2, 0, i1);
/* 1158 */               j += i1;
/*      */             }
/* 1160 */             else if (i1 == 0) {
/* 1161 */               Thread.yield();
/*      */             }
/*      */           }
/* 1164 */           arrayOfByte1 = localDirectBAOS.getInternalBuffer();
/*      */         }
/* 1166 */         i = j / paramAudioInputStream.getFormat().getFrameSize();
/*      */ 
/* 1171 */         open(paramAudioInputStream.getFormat(), arrayOfByte1, i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getFrameLength()
/*      */     {
/* 1179 */       return this.m_lengthInFrames;
/*      */     }
/*      */ 
/*      */     public long getMicrosecondLength()
/*      */     {
/* 1184 */       return Toolkit.frames2micros(getFormat(), getFrameLength());
/*      */     }
/*      */ 
/*      */     public void setFramePosition(int paramInt)
/*      */     {
/* 1191 */       if (paramInt < 0) {
/* 1192 */         paramInt = 0;
/*      */       }
/* 1194 */       else if (paramInt >= getFrameLength()) {
/* 1195 */         paramInt = getFrameLength();
/*      */       }
/* 1197 */       if (this.doIO) {
/* 1198 */         this.newFramePosition = paramInt;
/*      */       } else {
/* 1200 */         this.clipBytePosition = (paramInt * this.frameSize);
/* 1201 */         this.newFramePosition = -1;
/*      */       }
/*      */ 
/* 1207 */       this.bytePosition = (paramInt * this.frameSize);
/*      */ 
/* 1210 */       flush();
/*      */ 
/* 1214 */       synchronized (this.lockNative) {
/* 1215 */         DirectAudioDevice.nSetBytePosition(this.id, this.isSource, paramInt * this.frameSize);
/*      */       }
/*      */     }
/*      */ 
/*      */     public long getLongFramePosition()
/*      */     {
/* 1239 */       return super.getLongFramePosition();
/*      */     }
/*      */ 
/*      */     public synchronized void setMicrosecondPosition(long paramLong)
/*      */     {
/* 1246 */       long l = Toolkit.micros2frames(getFormat(), paramLong);
/* 1247 */       setFramePosition((int)l);
/*      */     }
/*      */ 
/*      */     public void setLoopPoints(int paramInt1, int paramInt2)
/*      */     {
/* 1255 */       if ((paramInt1 < 0) || (paramInt1 >= getFrameLength())) {
/* 1256 */         throw new IllegalArgumentException("illegal value for start: " + paramInt1);
/*      */       }
/* 1258 */       if (paramInt2 >= getFrameLength()) {
/* 1259 */         throw new IllegalArgumentException("illegal value for end: " + paramInt2);
/*      */       }
/*      */ 
/* 1262 */       if (paramInt2 == -1) {
/* 1263 */         paramInt2 = getFrameLength() - 1;
/* 1264 */         if (paramInt2 < 0) {
/* 1265 */           paramInt2 = 0;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1270 */       if (paramInt2 < paramInt1) {
/* 1271 */         throw new IllegalArgumentException("End position " + paramInt2 + "  preceeds start position " + paramInt1);
/*      */       }
/*      */ 
/* 1275 */       this.loopStartFrame = paramInt1;
/* 1276 */       this.loopEndFrame = paramInt2;
/*      */     }
/*      */ 
/*      */     public void loop(int paramInt)
/*      */     {
/* 1286 */       this.loopCount = paramInt;
/* 1287 */       start();
/*      */     }
/*      */ 
/*      */     void implOpen(AudioFormat paramAudioFormat, int paramInt)
/*      */       throws LineUnavailableException
/*      */     {
/* 1297 */       if (this.audioData == null) {
/* 1298 */         throw new IllegalArgumentException("illegal call to open() in interface Clip");
/*      */       }
/* 1300 */       super.implOpen(paramAudioFormat, paramInt);
/*      */     }
/*      */ 
/*      */     void implClose()
/*      */     {
/* 1307 */       Thread localThread = this.thread;
/* 1308 */       this.thread = null;
/* 1309 */       this.doIO = false;
/* 1310 */       if (localThread != null)
/*      */       {
/* 1312 */         synchronized (this.lock) {
/* 1313 */           this.lock.notifyAll();
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1318 */           localThread.join(2000L); } catch (InterruptedException localInterruptedException) {
/*      */         }
/*      */       }
/* 1321 */       super.implClose();
/*      */ 
/* 1323 */       this.audioData = null;
/* 1324 */       this.newFramePosition = -1;
/*      */ 
/* 1327 */       getEventDispatcher().autoClosingClipClosed(this);
/*      */     }
/*      */ 
/*      */     void implStart()
/*      */     {
/* 1335 */       super.implStart();
/*      */     }
/*      */ 
/*      */     void implStop()
/*      */     {
/* 1342 */       super.implStop();
/*      */ 
/* 1345 */       this.loopCount = 0;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1354 */       if (this.thread != null)
/*      */       {
/* 1358 */         synchronized (this.lock) {
/* 1359 */           if (!this.doIO)
/*      */             try {
/* 1361 */               this.lock.wait();
/*      */             } catch (InterruptedException localInterruptedException) {
/*      */             }
/*      */         }
/* 1365 */         while (this.doIO) {
/* 1366 */           if (this.newFramePosition >= 0) {
/* 1367 */             this.clipBytePosition = (this.newFramePosition * this.frameSize);
/* 1368 */             this.newFramePosition = -1;
/*      */           }
/* 1370 */           int i = getFrameLength() - 1;
/* 1371 */           if ((this.loopCount > 0) || (this.loopCount == -1)) {
/* 1372 */             i = this.loopEndFrame;
/*      */           }
/* 1374 */           long l = this.clipBytePosition / this.frameSize;
/* 1375 */           int j = (int)(i - l + 1L);
/* 1376 */           int k = j * this.frameSize;
/* 1377 */           if (k > getBufferSize()) {
/* 1378 */             k = Toolkit.align(getBufferSize(), this.frameSize);
/*      */           }
/* 1380 */           int m = write(this.audioData, this.clipBytePosition, k);
/* 1381 */           this.clipBytePosition += m;
/*      */ 
/* 1383 */           if ((this.doIO) && (this.newFramePosition < 0) && (m >= 0)) {
/* 1384 */             l = this.clipBytePosition / this.frameSize;
/*      */ 
/* 1388 */             if (l > i)
/*      */             {
/* 1390 */               if ((this.loopCount > 0) || (this.loopCount == -1)) {
/* 1391 */                 if (this.loopCount != -1) {
/* 1392 */                   this.loopCount -= 1;
/*      */                 }
/* 1394 */                 this.newFramePosition = this.loopStartFrame;
/*      */               }
/*      */               else
/*      */               {
/* 1400 */                 drain();
/* 1401 */                 stop();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isAutoClosing()
/*      */     {
/* 1418 */       return this.autoclosing;
/*      */     }
/*      */ 
/*      */     public void setAutoClosing(boolean paramBoolean) {
/* 1422 */       if (paramBoolean != this.autoclosing) {
/* 1423 */         if (isOpen()) {
/* 1424 */           if (paramBoolean)
/* 1425 */             getEventDispatcher().autoClosingClipOpened(this);
/*      */           else {
/* 1427 */             getEventDispatcher().autoClosingClipClosed(this);
/*      */           }
/*      */         }
/* 1430 */         this.autoclosing = paramBoolean;
/*      */       }
/*      */     }
/*      */ 
/*      */     protected boolean requiresServicing()
/*      */     {
/* 1436 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DirectDL extends AbstractDataLine
/*      */     implements EventDispatcher.LineMonitor
/*      */   {
/*      */     protected int mixerIndex;
/*      */     protected int deviceID;
/*      */     protected long id;
/*      */     protected int waitTime;
/*  377 */     protected volatile boolean flushing = false;
/*      */     protected boolean isSource;
/*      */     protected volatile long bytePosition;
/*  380 */     protected volatile boolean doIO = false;
/*  381 */     protected volatile boolean stoppedWritten = false;
/*  382 */     protected volatile boolean drained = false;
/*  383 */     protected boolean monitoring = false;
/*      */ 
/*  387 */     protected int softwareConversionSize = 0;
/*      */     protected AudioFormat hardwareFormat;
/*  390 */     private Gain gainControl = new Gain(null);
/*  391 */     private Mute muteControl = new Mute(null);
/*  392 */     private Balance balanceControl = new Balance(null);
/*  393 */     private Pan panControl = new Pan(null);
/*      */     private float leftGain;
/*      */     private float rightGain;
/*  395 */     protected volatile boolean noService = false;
/*      */ 
/*  398 */     protected final Object lockNative = new Object();
/*      */ 
/*      */     protected DirectDL(DataLine.Info paramInfo, DirectAudioDevice paramDirectAudioDevice, AudioFormat paramAudioFormat, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
/*      */     {
/*  408 */       super(paramDirectAudioDevice, null, paramAudioFormat, paramInt1);
/*      */ 
/*  410 */       this.mixerIndex = paramInt2;
/*  411 */       this.deviceID = paramInt3;
/*  412 */       this.waitTime = 10;
/*  413 */       this.isSource = paramBoolean;
/*      */     }
/*      */ 
/*      */     void implOpen(AudioFormat paramAudioFormat, int paramInt)
/*      */       throws LineUnavailableException
/*      */     {
/*  426 */       Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
/*      */ 
/*  429 */       if (!this.isSource) {
/*  430 */         JSSecurityManager.checkRecordPermission();
/*      */       }
/*  432 */       int i = 0;
/*  433 */       if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
/*  434 */         i = 1;
/*      */       }
/*  436 */       else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
/*  437 */         i = 2;
/*      */       }
/*      */ 
/*  440 */       if (paramInt <= -1) {
/*  441 */         paramInt = (int)Toolkit.millis2bytes(paramAudioFormat, 500L);
/*      */       }
/*      */ 
/*  444 */       DirectAudioDevice.DirectDLI localDirectDLI = null;
/*  445 */       if ((this.info instanceof DirectAudioDevice.DirectDLI)) {
/*  446 */         localDirectDLI = (DirectAudioDevice.DirectDLI)this.info;
/*      */       }
/*      */ 
/*  450 */       if (this.isSource) {
/*  451 */         if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
/*      */         {
/*  454 */           this.controls = new Control[0];
/*      */         }
/*  456 */         else if ((paramAudioFormat.getChannels() > 2) || (paramAudioFormat.getSampleSizeInBits() > 16))
/*      */         {
/*  459 */           this.controls = new Control[0];
/*      */         } else {
/*  461 */           if (paramAudioFormat.getChannels() == 1) {
/*  462 */             this.controls = new Control[2];
/*      */           } else {
/*  464 */             this.controls = new Control[4];
/*  465 */             this.controls[2] = this.balanceControl;
/*      */ 
/*  469 */             this.controls[3] = this.panControl;
/*      */           }
/*  471 */           this.controls[0] = this.gainControl;
/*  472 */           this.controls[1] = this.muteControl;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  477 */       this.hardwareFormat = paramAudioFormat;
/*      */ 
/*  480 */       this.softwareConversionSize = 0;
/*  481 */       if ((localDirectDLI != null) && (!localDirectDLI.isFormatSupportedInHardware(paramAudioFormat))) {
/*  482 */         AudioFormat localAudioFormat = DirectAudioDevice.getSignOrEndianChangedFormat(paramAudioFormat);
/*  483 */         if (localDirectDLI.isFormatSupportedInHardware(localAudioFormat))
/*      */         {
/*  485 */           this.hardwareFormat = localAudioFormat;
/*      */ 
/*  487 */           this.softwareConversionSize = (paramAudioFormat.getFrameSize() / paramAudioFormat.getChannels());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  498 */       paramInt = paramInt / paramAudioFormat.getFrameSize() * paramAudioFormat.getFrameSize();
/*      */ 
/*  500 */       this.id = DirectAudioDevice.nOpen(this.mixerIndex, this.deviceID, this.isSource, i, this.hardwareFormat.getSampleRate(), this.hardwareFormat.getSampleSizeInBits(), this.hardwareFormat.getFrameSize(), this.hardwareFormat.getChannels(), this.hardwareFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED), this.hardwareFormat.isBigEndian(), paramInt);
/*      */ 
/*  511 */       if (this.id == 0L)
/*      */       {
/*  513 */         throw new LineUnavailableException("line with format " + paramAudioFormat + " not supported.");
/*      */       }
/*      */ 
/*  517 */       this.bufferSize = DirectAudioDevice.nGetBufferSize(this.id, this.isSource);
/*  518 */       if (this.bufferSize < 1)
/*      */       {
/*  520 */         this.bufferSize = paramInt;
/*      */       }
/*  522 */       this.format = paramAudioFormat;
/*      */ 
/*  524 */       this.waitTime = ((int)Toolkit.bytes2millis(paramAudioFormat, this.bufferSize) / 4);
/*  525 */       if (this.waitTime < 10) {
/*  526 */         this.waitTime = 1;
/*      */       }
/*  528 */       else if (this.waitTime > 1000)
/*      */       {
/*  531 */         this.waitTime = 1000;
/*      */       }
/*  533 */       this.bytePosition = 0L;
/*  534 */       this.stoppedWritten = false;
/*  535 */       this.doIO = false;
/*  536 */       calcVolume();
/*      */     }
/*      */ 
/*      */     void implStart()
/*      */     {
/*  546 */       if (!this.isSource) {
/*  547 */         JSSecurityManager.checkRecordPermission();
/*      */       }
/*      */ 
/*  550 */       synchronized (this.lockNative)
/*      */       {
/*  552 */         DirectAudioDevice.nStart(this.id, this.isSource);
/*      */       }
/*      */ 
/*  555 */       this.monitoring = requiresServicing();
/*  556 */       if (this.monitoring) {
/*  557 */         getEventDispatcher().addLineMonitor(this);
/*      */       }
/*      */ 
/*  560 */       this.doIO = true;
/*      */ 
/*  565 */       if ((this.isSource) && (this.stoppedWritten)) {
/*  566 */         setStarted(true);
/*  567 */         setActive(true);
/*      */       }
/*      */     }
/*      */ 
/*      */     void implStop()
/*      */     {
/*  577 */       if (!this.isSource) {
/*  578 */         JSSecurityManager.checkRecordPermission();
/*      */       }
/*      */ 
/*  581 */       if (this.monitoring) {
/*  582 */         getEventDispatcher().removeLineMonitor(this);
/*  583 */         this.monitoring = false;
/*      */       }
/*  585 */       synchronized (this.lockNative) {
/*  586 */         DirectAudioDevice.nStop(this.id, this.isSource);
/*      */       }
/*      */ 
/*  589 */       synchronized (this.lock)
/*      */       {
/*  593 */         this.doIO = false;
/*  594 */         this.lock.notifyAll();
/*      */       }
/*  596 */       setActive(false);
/*  597 */       setStarted(false);
/*  598 */       this.stoppedWritten = false;
/*      */     }
/*      */ 
/*      */     void implClose()
/*      */     {
/*  607 */       if (!this.isSource) {
/*  608 */         JSSecurityManager.checkRecordPermission();
/*      */       }
/*      */ 
/*  612 */       if (this.monitoring) {
/*  613 */         getEventDispatcher().removeLineMonitor(this);
/*  614 */         this.monitoring = false;
/*      */       }
/*      */ 
/*  617 */       this.doIO = false;
/*  618 */       long l = this.id;
/*  619 */       this.id = 0L;
/*  620 */       synchronized (this.lockNative) {
/*  621 */         DirectAudioDevice.nClose(l, this.isSource);
/*      */       }
/*  623 */       this.bytePosition = 0L;
/*  624 */       this.softwareConversionSize = 0;
/*      */     }
/*      */ 
/*      */     public int available()
/*      */     {
/*  631 */       if (this.id == 0L)
/*  632 */         return 0;
/*      */       int i;
/*  635 */       synchronized (this.lockNative) {
/*  636 */         i = DirectAudioDevice.nAvailable(this.id, this.isSource);
/*      */       }
/*  638 */       return i;
/*      */     }
/*      */ 
/*      */     public void drain()
/*      */     {
/*  643 */       this.noService = true;
/*      */ 
/*  647 */       int i = 0;
/*  648 */       long l = getLongFramePosition();
/*  649 */       int j = 0;
/*  650 */       while (!this.drained) {
/*  651 */         synchronized (this.lockNative) {
/*  652 */           if ((this.id == 0L) || (!this.doIO) || (!DirectAudioDevice.nIsStillDraining(this.id, this.isSource))) {
/*  653 */             break;
/*      */           }
/*      */         }
/*  656 */         if (i % 5 == 4) {
/*  657 */           ??? = getLongFramePosition();
/*  658 */           j |= (??? != l ? 1 : 0);
/*  659 */           if (i % 50 > 45)
/*      */           {
/*  662 */             if (j == 0)
/*      */             {
/*      */               break;
/*      */             }
/*  666 */             j = 0;
/*  667 */             l = ???;
/*      */           }
/*      */         }
/*  670 */         i++;
/*  671 */         synchronized (this.lock) {
/*      */           try {
/*  673 */             this.lock.wait(10L);
/*      */           } catch (InterruptedException localInterruptedException) {
/*      */           }
/*      */         }
/*      */       }
/*  678 */       if ((this.doIO) && (this.id != 0L)) {
/*  679 */         this.drained = true;
/*      */       }
/*  681 */       this.noService = false;
/*      */     }
/*      */ 
/*      */     public void flush() {
/*  685 */       if (this.id != 0L)
/*      */       {
/*  687 */         this.flushing = true;
/*  688 */         synchronized (this.lock) {
/*  689 */           this.lock.notifyAll();
/*      */         }
/*  691 */         synchronized (this.lockNative) {
/*  692 */           if (this.id != 0L)
/*      */           {
/*  694 */             DirectAudioDevice.nFlush(this.id, this.isSource);
/*      */           }
/*      */         }
/*  697 */         this.drained = true;
/*      */       }
/*      */     }
/*      */ 
/*      */     public long getLongFramePosition()
/*      */     {
/*      */       long l;
/*  704 */       synchronized (this.lockNative) {
/*  705 */         l = DirectAudioDevice.nGetBytePosition(this.id, this.isSource, this.bytePosition);
/*      */       }
/*      */ 
/*  708 */       if (l < 0L)
/*      */       {
/*  711 */         l = 0L;
/*      */       }
/*  713 */       return l / getFormat().getFrameSize();
/*      */     }
/*      */ 
/*      */     public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     {
/*  723 */       this.flushing = false;
/*  724 */       if (paramInt2 == 0) {
/*  725 */         return 0;
/*      */       }
/*  727 */       if (paramInt2 < 0) {
/*  728 */         throw new IllegalArgumentException("illegal len: " + paramInt2);
/*      */       }
/*  730 */       if (paramInt2 % getFormat().getFrameSize() != 0) {
/*  731 */         throw new IllegalArgumentException("illegal request to write non-integral number of frames (" + paramInt2 + " bytes, " + "frameSize = " + getFormat().getFrameSize() + " bytes)");
/*      */       }
/*      */ 
/*  736 */       if (paramInt1 < 0) {
/*  737 */         throw new ArrayIndexOutOfBoundsException(paramInt1);
/*      */       }
/*  739 */       if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
/*  740 */         throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
/*      */       }
/*      */ 
/*  743 */       if ((!isActive()) && (this.doIO))
/*      */       {
/*  746 */         setActive(true);
/*  747 */         setStarted(true);
/*      */       }
/*  749 */       int i = 0;
/*  750 */       while (!this.flushing)
/*      */       {
/*      */         int j;
/*  752 */         synchronized (this.lockNative) {
/*  753 */           j = DirectAudioDevice.nWrite(this.id, paramArrayOfByte, paramInt1, paramInt2, this.softwareConversionSize, this.leftGain, this.rightGain);
/*      */ 
/*  756 */           if (j < 0)
/*      */           {
/*      */             break;
/*      */           }
/*  760 */           this.bytePosition += j;
/*  761 */           if (j > 0) {
/*  762 */             this.drained = false;
/*      */           }
/*      */         }
/*  765 */         paramInt2 -= j;
/*  766 */         i += j;
/*  767 */         if ((!this.doIO) || (paramInt2 <= 0)) break;
/*  768 */         paramInt1 += j;
/*  769 */         synchronized (this.lock) {
/*      */           try {
/*  771 */             this.lock.wait(this.waitTime);
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*  778 */       if ((i > 0) && (!this.doIO)) {
/*  779 */         this.stoppedWritten = true;
/*      */       }
/*  781 */       return i;
/*      */     }
/*      */ 
/*      */     protected boolean requiresServicing() {
/*  785 */       return DirectAudioDevice.nRequiresServicing(this.id, this.isSource);
/*      */     }
/*      */ 
/*      */     public void checkLine()
/*      */     {
/*  790 */       synchronized (this.lockNative) {
/*  791 */         if ((this.monitoring) && (this.doIO) && (this.id != 0L) && (!this.flushing) && (!this.noService))
/*      */         {
/*  796 */           DirectAudioDevice.nService(this.id, this.isSource);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void calcVolume() {
/*  802 */       if (getFormat() == null) {
/*  803 */         return;
/*      */       }
/*  805 */       if (this.muteControl.getValue()) {
/*  806 */         this.leftGain = 0.0F;
/*  807 */         this.rightGain = 0.0F;
/*  808 */         return;
/*      */       }
/*  810 */       float f1 = this.gainControl.getLinearGain();
/*  811 */       if (getFormat().getChannels() == 1)
/*      */       {
/*  813 */         this.leftGain = f1;
/*  814 */         this.rightGain = f1;
/*      */       }
/*      */       else {
/*  817 */         float f2 = this.balanceControl.getValue();
/*  818 */         if (f2 < 0.0F)
/*      */         {
/*  820 */           this.leftGain = f1;
/*  821 */           this.rightGain = (f1 * (f2 + 1.0F));
/*      */         } else {
/*  823 */           this.leftGain = (f1 * (1.0F - f2));
/*  824 */           this.rightGain = f1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private class Balance extends FloatControl
/*      */     {
/*      */       private Balance()
/*      */       {
/*  880 */         super(-1.0F, 1.0F, 0.007813F, -1, 0.0F, "", "Left", "Center", "Right");
/*      */       }
/*      */ 
/*      */       public void setValue(float paramFloat)
/*      */       {
/*  885 */         setValueImpl(paramFloat);
/*  886 */         DirectAudioDevice.DirectDL.this.panControl.setValueImpl(paramFloat);
/*  887 */         DirectAudioDevice.DirectDL.this.calcVolume();
/*      */       }
/*      */ 
/*      */       void setValueImpl(float paramFloat) {
/*  891 */         super.setValue(paramFloat);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected class Gain extends FloatControl
/*      */     {
/*  834 */       private float linearGain = 1.0F;
/*      */ 
/*      */       private Gain()
/*      */       {
/*  838 */         super(Toolkit.linearToDB(0.0F), Toolkit.linearToDB(2.0F), Math.abs(Toolkit.linearToDB(1.0F) - Toolkit.linearToDB(0.0F)) / 128.0F, -1, 0.0F, "dB", "Minimum", "", "Maximum");
/*      */       }
/*      */ 
/*      */       public void setValue(float paramFloat)
/*      */       {
/*  852 */         float f = Toolkit.dBToLinear(paramFloat);
/*  853 */         super.setValue(Toolkit.linearToDB(f));
/*      */ 
/*  855 */         this.linearGain = f;
/*  856 */         DirectAudioDevice.DirectDL.this.calcVolume();
/*      */       }
/*      */ 
/*      */       float getLinearGain() {
/*  860 */         return this.linearGain;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class Mute extends BooleanControl
/*      */     {
/*      */       private Mute()
/*      */       {
/*  868 */         super(false, "True", "False");
/*      */       }
/*      */ 
/*      */       public void setValue(boolean paramBoolean) {
/*  872 */         super.setValue(paramBoolean);
/*  873 */         DirectAudioDevice.DirectDL.this.calcVolume();
/*      */       }
/*      */     }
/*      */ 
/*      */     private class Pan extends FloatControl
/*      */     {
/*      */       private Pan()
/*      */       {
/*  899 */         super(-1.0F, 1.0F, 0.007813F, -1, 0.0F, "", "Left", "Center", "Right");
/*      */       }
/*      */ 
/*      */       public void setValue(float paramFloat)
/*      */       {
/*  904 */         setValueImpl(paramFloat);
/*  905 */         DirectAudioDevice.DirectDL.this.balanceControl.setValueImpl(paramFloat);
/*  906 */         DirectAudioDevice.DirectDL.this.calcVolume();
/*      */       }
/*      */       void setValueImpl(float paramFloat) {
/*  909 */         super.setValue(paramFloat);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DirectDLI extends DataLine.Info
/*      */   {
/*      */     AudioFormat[] hardwareFormats;
/*      */ 
/*      */     private DirectDLI(Class paramClass, AudioFormat[] paramArrayOfAudioFormat1, AudioFormat[] paramArrayOfAudioFormat2, int paramInt1, int paramInt2)
/*      */     {
/*  344 */       super(paramArrayOfAudioFormat1, paramInt1, paramInt2);
/*  345 */       this.hardwareFormats = paramArrayOfAudioFormat2;
/*      */     }
/*      */ 
/*      */     public boolean isFormatSupportedInHardware(AudioFormat paramAudioFormat) {
/*  349 */       if (paramAudioFormat == null) return false;
/*  350 */       for (int i = 0; i < this.hardwareFormats.length; i++) {
/*  351 */         if (paramAudioFormat.matches(this.hardwareFormats[i])) {
/*  352 */           return true;
/*      */         }
/*      */       }
/*  355 */       return false;
/*      */     }
/*      */ 
/*      */     private AudioFormat[] getHardwareFormats()
/*      */     {
/*  365 */       return this.hardwareFormats;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DirectSDL extends DirectAudioDevice.DirectDL
/*      */     implements SourceDataLine
/*      */   {
/*      */     private DirectSDL(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
/*      */     {
/*  928 */       super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DirectTDL extends DirectAudioDevice.DirectDL
/*      */     implements TargetDataLine
/*      */   {
/*      */     private DirectTDL(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
/*      */     {
/*  944 */       super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), false);
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     {
/*  951 */       this.flushing = false;
/*  952 */       if (paramInt2 == 0) {
/*  953 */         return 0;
/*      */       }
/*  955 */       if (paramInt2 < 0) {
/*  956 */         throw new IllegalArgumentException("illegal len: " + paramInt2);
/*      */       }
/*  958 */       if (paramInt2 % getFormat().getFrameSize() != 0) {
/*  959 */         throw new IllegalArgumentException("illegal request to read non-integral number of frames (" + paramInt2 + " bytes, " + "frameSize = " + getFormat().getFrameSize() + " bytes)");
/*      */       }
/*      */ 
/*  964 */       if (paramInt1 < 0) {
/*  965 */         throw new ArrayIndexOutOfBoundsException(paramInt1);
/*      */       }
/*  967 */       if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
/*  968 */         throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
/*      */       }
/*  970 */       if ((!isActive()) && (this.doIO))
/*      */       {
/*  973 */         setActive(true);
/*  974 */         setStarted(true);
/*      */       }
/*  976 */       int i = 0;
/*  977 */       while ((this.doIO) && (!this.flushing))
/*      */       {
/*      */         int j;
/*  979 */         synchronized (this.lockNative) {
/*  980 */           j = DirectAudioDevice.nRead(this.id, paramArrayOfByte, paramInt1, paramInt2, this.softwareConversionSize);
/*  981 */           if (j < 0)
/*      */           {
/*      */             break;
/*      */           }
/*  985 */           this.bytePosition += j;
/*  986 */           if (j > 0) {
/*  987 */             this.drained = false;
/*      */           }
/*      */         }
/*  990 */         paramInt2 -= j;
/*  991 */         i += j;
/*  992 */         if (paramInt2 <= 0) break;
/*  993 */         paramInt1 += j;
/*  994 */         synchronized (this.lock) {
/*      */           try {
/*  996 */             this.lock.wait(this.waitTime);
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/* 1003 */       if (this.flushing) {
/* 1004 */         i = 0;
/*      */       }
/* 1006 */       return i;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.DirectAudioDevice
 * JD-Core Version:    0.6.2
 */