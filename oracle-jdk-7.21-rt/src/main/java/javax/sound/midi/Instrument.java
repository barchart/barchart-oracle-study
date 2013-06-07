/*    */ package javax.sound.midi;
/*    */ 
/*    */ public abstract class Instrument extends SoundbankResource
/*    */ {
/*    */   private final Patch patch;
/*    */ 
/*    */   protected Instrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass)
/*    */   {
/* 75 */     super(paramSoundbank, paramString, paramClass);
/* 76 */     this.patch = paramPatch;
/*    */   }
/*    */ 
/*    */   public Patch getPatch()
/*    */   {
/* 86 */     return this.patch;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.midi.Instrument
 * JD-Core Version:    0.6.2
 */