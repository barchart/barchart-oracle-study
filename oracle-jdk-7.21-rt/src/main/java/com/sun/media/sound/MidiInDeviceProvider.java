/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.midi.MidiDevice;
/*     */ 
/*     */ public class MidiInDeviceProvider extends AbstractMidiDeviceProvider
/*     */ {
/*  41 */   static AbstractMidiDeviceProvider.Info[] infos = null;
/*     */ 
/*  44 */   static MidiDevice[] devices = null;
/*     */ 
/*  53 */   private static boolean enabled = Platform.isMidiIOEnabled();
/*     */ 
/*     */   AbstractMidiDeviceProvider.Info createInfo(int paramInt)
/*     */   {
/*  68 */     if (!enabled) {
/*  69 */       return null;
/*     */     }
/*  71 */     return new MidiInDeviceInfo(paramInt, MidiInDeviceProvider.class, null);
/*     */   }
/*     */ 
/*     */   MidiDevice createDevice(AbstractMidiDeviceProvider.Info paramInfo) {
/*  75 */     if ((enabled) && ((paramInfo instanceof MidiInDeviceInfo))) {
/*  76 */       return new MidiInDevice(paramInfo);
/*     */     }
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   int getNumDevices() {
/*  82 */     if (!enabled)
/*     */     {
/*  84 */       return 0;
/*     */     }
/*  86 */     int i = nGetNumDevices();
/*     */ 
/*  88 */     return i;
/*     */   }
/*     */   MidiDevice[] getDeviceCache() {
/*  91 */     return devices; } 
/*  92 */   void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice) { devices = paramArrayOfMidiDevice; } 
/*  93 */   AbstractMidiDeviceProvider.Info[] getInfoCache() { return infos; } 
/*  94 */   void setInfoCache(AbstractMidiDeviceProvider.Info[] paramArrayOfInfo) { infos = paramArrayOfInfo; }
/*     */ 
/*     */ 
/*     */   private static native int nGetNumDevices();
/*     */ 
/*     */   private static native String nGetName(int paramInt);
/*     */ 
/*     */   private static native String nGetVendor(int paramInt);
/*     */ 
/*     */   private static native String nGetDescription(int paramInt);
/*     */ 
/*     */   private static native String nGetVersion(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/*  52 */     Platform.initialize();
/*     */   }
/*     */ 
/*     */   static class MidiInDeviceInfo extends AbstractMidiDeviceProvider.Info
/*     */   {
/*     */     private Class providerClass;
/*     */ 
/*     */     private MidiInDeviceInfo(int paramInt, Class paramClass)
/*     */     {
/* 113 */       super(MidiInDeviceProvider.nGetVendor(paramInt), MidiInDeviceProvider.nGetDescription(paramInt), MidiInDeviceProvider.nGetVersion(paramInt), paramInt);
/* 114 */       this.providerClass = paramClass;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.MidiInDeviceProvider
 * JD-Core Version:    0.6.2
 */