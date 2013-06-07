/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.midi.Instrument;
/*     */ import javax.sound.midi.MidiChannel;
/*     */ import javax.sound.midi.Patch;
/*     */ import javax.sound.midi.Soundbank;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ 
/*     */ public abstract class ModelInstrument extends Instrument
/*     */ {
/*     */   protected ModelInstrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass)
/*     */   {
/*  54 */     super(paramSoundbank, paramPatch, paramString, paramClass);
/*     */   }
/*     */ 
/*     */   public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
/*     */   {
/*  59 */     return new ModelStandardIndexedDirector(paramArrayOfModelPerformer, paramModelDirectedPlayer);
/*     */   }
/*     */ 
/*     */   public ModelPerformer[] getPerformers() {
/*  63 */     return new ModelPerformer[0];
/*     */   }
/*     */ 
/*     */   public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat)
/*     */   {
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public Patch getPatchAlias()
/*     */   {
/*  73 */     Patch localPatch = getPatch();
/*  74 */     int i = localPatch.getProgram();
/*  75 */     int j = localPatch.getBank();
/*  76 */     if (j != 0)
/*  77 */       return localPatch;
/*  78 */     boolean bool = false;
/*  79 */     if ((getPatch() instanceof ModelPatch))
/*  80 */       bool = ((ModelPatch)getPatch()).isPercussion();
/*  81 */     if (bool) {
/*  82 */       return new Patch(15360, i);
/*     */     }
/*  84 */     return new Patch(15488, i);
/*     */   }
/*     */ 
/*     */   public String[] getKeys()
/*     */   {
/*  91 */     String[] arrayOfString = new String[''];
/*  92 */     for (ModelPerformer localModelPerformer : getPerformers()) {
/*  93 */       for (int k = localModelPerformer.getKeyFrom(); k <= localModelPerformer.getKeyTo(); k++) {
/*  94 */         if ((k >= 0) && (k < 128) && (arrayOfString[k] == null)) {
/*  95 */           String str = localModelPerformer.getName();
/*  96 */           if (str == null)
/*  97 */             str = "untitled";
/*  98 */           arrayOfString[k] = str;
/*     */         }
/*     */       }
/*     */     }
/* 102 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   public boolean[] getChannels()
/*     */   {
/* 108 */     boolean bool = false;
/* 109 */     if ((getPatch() instanceof ModelPatch)) {
/* 110 */       bool = ((ModelPatch)getPatch()).isPercussion();
/*     */     }
/*     */ 
/* 113 */     if (bool) {
/* 114 */       boolean[] arrayOfBoolean1 = new boolean[16];
/* 115 */       for (int j = 0; j < arrayOfBoolean1.length; j++)
/* 116 */         arrayOfBoolean1[j] = false;
/* 117 */       arrayOfBoolean1[9] = true;
/* 118 */       return arrayOfBoolean1;
/*     */     }
/*     */ 
/* 122 */     int i = getPatch().getBank();
/* 123 */     if ((i >> 7 == 120) || (i >> 7 == 121)) {
/* 124 */       arrayOfBoolean2 = new boolean[16];
/* 125 */       for (k = 0; k < arrayOfBoolean2.length; k++)
/* 126 */         arrayOfBoolean2[k] = true;
/* 127 */       return arrayOfBoolean2;
/*     */     }
/*     */ 
/* 130 */     boolean[] arrayOfBoolean2 = new boolean[16];
/* 131 */     for (int k = 0; k < arrayOfBoolean2.length; k++)
/* 132 */       arrayOfBoolean2[k] = true;
/* 133 */     arrayOfBoolean2[9] = false;
/* 134 */     return arrayOfBoolean2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.ModelInstrument
 * JD-Core Version:    0.6.2
 */