/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.sampled.Mixer;
/*     */ import javax.sound.sampled.Mixer.Info;
/*     */ import javax.sound.sampled.spi.MixerProvider;
/*     */ 
/*     */ public class PortMixerProvider extends MixerProvider
/*     */ {
/*     */   private static PortMixerInfo[] infos;
/*     */   private static PortMixer[] devices;
/*     */ 
/*     */   public PortMixerProvider()
/*     */   {
/*  70 */     if (Platform.isPortsEnabled()) {
/*  71 */       init();
/*     */     } else {
/*  73 */       infos = new PortMixerInfo[0];
/*  74 */       devices = new PortMixer[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void init()
/*     */   {
/*  80 */     int i = nGetNumDevices();
/*     */ 
/*  82 */     if ((infos == null) || (infos.length != i))
/*     */     {
/*  85 */       infos = new PortMixerInfo[i];
/*  86 */       devices = new PortMixer[i];
/*     */ 
/*  90 */       for (int j = 0; j < infos.length; j++)
/*  91 */         infos[j] = nNewPortMixerInfo(j);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Mixer.Info[] getMixerInfo()
/*     */   {
/*  98 */     Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
/*  99 */     System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
/* 100 */     return arrayOfInfo;
/*     */   }
/*     */ 
/*     */   public Mixer getMixer(Mixer.Info paramInfo)
/*     */   {
/* 105 */     for (int i = 0; i < infos.length; i++) {
/* 106 */       if (infos[i].equals(paramInfo)) {
/* 107 */         return getDevice(infos[i]);
/*     */       }
/*     */     }
/* 110 */     throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
/*     */   }
/*     */ 
/*     */   private Mixer getDevice(PortMixerInfo paramPortMixerInfo)
/*     */   {
/* 115 */     int i = paramPortMixerInfo.getIndex();
/* 116 */     if (devices[i] == null) {
/* 117 */       devices[i] = new PortMixer(paramPortMixerInfo);
/*     */     }
/* 119 */     return devices[i];
/*     */   }
/*     */ 
/*     */   private static native int nGetNumDevices();
/*     */ 
/*     */   private static native PortMixerInfo nNewPortMixerInfo(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/*  58 */     Platform.initialize();
/*     */   }
/*     */ 
/*     */   static class PortMixerInfo extends Mixer.Info
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     private PortMixerInfo(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     {
/* 134 */       super(paramString2, paramString3, paramString4);
/* 135 */       this.index = paramInt;
/*     */     }
/*     */ 
/*     */     int getIndex() {
/* 139 */       return this.index;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.PortMixerProvider
 * JD-Core Version:    0.6.2
 */