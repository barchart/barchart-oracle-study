/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.sampled.Mixer;
/*     */ import javax.sound.sampled.Mixer.Info;
/*     */ import javax.sound.sampled.spi.MixerProvider;
/*     */ 
/*     */ public class DirectAudioDeviceProvider extends MixerProvider
/*     */ {
/*     */   private static DirectAudioDeviceInfo[] infos;
/*     */   private static DirectAudioDevice[] devices;
/*     */ 
/*     */   public DirectAudioDeviceProvider()
/*     */   {
/*  70 */     if (Platform.isDirectAudioEnabled()) {
/*  71 */       init();
/*     */     } else {
/*  73 */       infos = new DirectAudioDeviceInfo[0];
/*  74 */       devices = new DirectAudioDevice[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void init()
/*     */   {
/*  80 */     int i = nGetNumDevices();
/*     */ 
/*  82 */     if ((infos == null) || (infos.length != i))
/*     */     {
/*  85 */       infos = new DirectAudioDeviceInfo[i];
/*  86 */       devices = new DirectAudioDevice[i];
/*     */ 
/*  89 */       for (int j = 0; j < infos.length; j++)
/*  90 */         infos[j] = nNewDirectAudioDeviceInfo(j);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Mixer.Info[] getMixerInfo()
/*     */   {
/*  97 */     Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
/*  98 */     System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
/*  99 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public Mixer getMixer(Mixer.Info paramInfo)
/*     */   {
/* 106 */     if (paramInfo == null) {
/* 107 */       for (i = 0; i < infos.length; i++) {
/* 108 */         Mixer localMixer = getDevice(infos[i]);
/* 109 */         if (localMixer.getSourceLineInfo().length > 0) {
/* 110 */           return localMixer;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 116 */     for (int i = 0; i < infos.length; i++) {
/* 117 */       if (infos[i].equals(paramInfo)) {
/* 118 */         return getDevice(infos[i]);
/*     */       }
/*     */     }
/*     */ 
/* 122 */     throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
/*     */   }
/*     */ 
/*     */   private Mixer getDevice(DirectAudioDeviceInfo paramDirectAudioDeviceInfo)
/*     */   {
/* 127 */     int i = paramDirectAudioDeviceInfo.getIndex();
/* 128 */     if (devices[i] == null) {
/* 129 */       devices[i] = new DirectAudioDevice(paramDirectAudioDeviceInfo);
/*     */     }
/* 131 */     return devices[i];
/*     */   }
/*     */ 
/*     */   private static native int nGetNumDevices();
/*     */ 
/*     */   private static native DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/*  58 */     Platform.initialize();
/*     */   }
/*     */ 
/*     */   static class DirectAudioDeviceInfo extends Mixer.Info
/*     */   {
/*     */     private int index;
/*     */     private int maxSimulLines;
/*     */     private int deviceID;
/*     */ 
/*     */     private DirectAudioDeviceInfo(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     {
/* 152 */       super(paramString2, "Direct Audio Device: " + paramString3, paramString4);
/* 153 */       this.index = paramInt1;
/* 154 */       this.maxSimulLines = paramInt3;
/* 155 */       this.deviceID = paramInt2;
/*     */     }
/*     */ 
/*     */     int getIndex() {
/* 159 */       return this.index;
/*     */     }
/*     */ 
/*     */     int getMaxSimulLines() {
/* 163 */       return this.maxSimulLines;
/*     */     }
/*     */ 
/*     */     int getDeviceID() {
/* 167 */       return this.deviceID;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.DirectAudioDeviceProvider
 * JD-Core Version:    0.6.2
 */