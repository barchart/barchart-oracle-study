/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.midi.MidiDevice;
/*     */ 
/*     */ public class MidiOutDeviceProvider extends AbstractMidiDeviceProvider
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
/*  71 */     return new MidiOutDeviceInfo(paramInt, MidiOutDeviceProvider.class, null);
/*     */   }
/*     */ 
/*     */   MidiDevice createDevice(AbstractMidiDeviceProvider.Info paramInfo) {
/*  75 */     if ((enabled) && ((paramInfo instanceof MidiOutDeviceInfo))) {
/*  76 */       return new MidiOutDevice(paramInfo);
/*     */     }
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   int getNumDevices() {
/*  82 */     if (!enabled)
/*     */     {
/*  84 */       return 0;
/*     */     }
/*  86 */     return nGetNumDevices();
/*     */   }
/*     */   MidiDevice[] getDeviceCache() {
/*  89 */     return devices; } 
/*  90 */   void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice) { devices = paramArrayOfMidiDevice; } 
/*  91 */   AbstractMidiDeviceProvider.Info[] getInfoCache() { return infos; } 
/*  92 */   void setInfoCache(AbstractMidiDeviceProvider.Info[] paramArrayOfInfo) { infos = paramArrayOfInfo; }
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
/*     */   static class MidiOutDeviceInfo extends AbstractMidiDeviceProvider.Info
/*     */   {
/*     */     private Class providerClass;
/*     */ 
/*     */     private MidiOutDeviceInfo(int paramInt, Class paramClass)
/*     */     {
/* 111 */       super(MidiOutDeviceProvider.nGetVendor(paramInt), MidiOutDeviceProvider.nGetDescription(paramInt), MidiOutDeviceProvider.nGetVersion(paramInt), paramInt);
/* 112 */       this.providerClass = paramClass;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.MidiOutDeviceProvider
 * JD-Core Version:    0.6.2
 */