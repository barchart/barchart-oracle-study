/*    */ package com.sun.media.sound;
/*    */ 
/*    */ import javax.sound.midi.MidiChannel;
/*    */ import javax.sound.midi.Patch;
/*    */ import javax.sound.sampled.AudioFormat;
/*    */ 
/*    */ public class ModelMappedInstrument extends ModelInstrument
/*    */ {
/*    */   private ModelInstrument ins;
/*    */ 
/*    */   public ModelMappedInstrument(ModelInstrument paramModelInstrument, Patch paramPatch)
/*    */   {
/* 41 */     super(paramModelInstrument.getSoundbank(), paramPatch, paramModelInstrument.getName(), paramModelInstrument.getDataClass());
/* 42 */     this.ins = paramModelInstrument;
/*    */   }
/*    */ 
/*    */   public Object getData() {
/* 46 */     return this.ins.getData();
/*    */   }
/*    */ 
/*    */   public ModelPerformer[] getPerformers() {
/* 50 */     return this.ins.getPerformers();
/*    */   }
/*    */ 
/*    */   public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
/*    */   {
/* 55 */     return this.ins.getDirector(paramArrayOfModelPerformer, paramMidiChannel, paramModelDirectedPlayer);
/*    */   }
/*    */ 
/*    */   public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat)
/*    */   {
/* 60 */     return this.ins.getChannelMixer(paramMidiChannel, paramAudioFormat);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.ModelMappedInstrument
 * JD-Core Version:    0.6.2
 */