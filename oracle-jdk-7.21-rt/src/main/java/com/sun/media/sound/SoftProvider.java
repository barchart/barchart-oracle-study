/*    */ package com.sun.media.sound;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import javax.sound.midi.MidiDevice;
/*    */ import javax.sound.midi.MidiDevice.Info;
/*    */ import javax.sound.midi.spi.MidiDeviceProvider;
/*    */ 
/*    */ public class SoftProvider extends MidiDeviceProvider
/*    */ {
/* 39 */   protected static final MidiDevice.Info softinfo = SoftSynthesizer.info;
/* 40 */   private static MidiDevice.Info[] softinfos = { softinfo };
/*    */ 
/*    */   public MidiDevice.Info[] getDeviceInfo() {
/* 43 */     return (MidiDevice.Info[])Arrays.copyOf(softinfos, softinfos.length);
/*    */   }
/*    */ 
/*    */   public MidiDevice getDevice(MidiDevice.Info paramInfo) {
/* 47 */     if (paramInfo == softinfo) {
/* 48 */       return new SoftSynthesizer();
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SoftProvider
 * JD-Core Version:    0.6.2
 */