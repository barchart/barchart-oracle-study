/*    */ package com.sun.media.sound;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class DLSSampleOptions
/*    */ {
/*    */   protected int unitynote;
/*    */   protected short finetune;
/*    */   protected int attenuation;
/*    */   protected long options;
/* 43 */   protected List<DLSSampleLoop> loops = new ArrayList();
/*    */ 
/*    */   public int getAttenuation() {
/* 46 */     return this.attenuation;
/*    */   }
/*    */ 
/*    */   public void setAttenuation(int paramInt) {
/* 50 */     this.attenuation = paramInt;
/*    */   }
/*    */ 
/*    */   public short getFinetune() {
/* 54 */     return this.finetune;
/*    */   }
/*    */ 
/*    */   public void setFinetune(short paramShort) {
/* 58 */     this.finetune = paramShort;
/*    */   }
/*    */ 
/*    */   public List<DLSSampleLoop> getLoops() {
/* 62 */     return this.loops;
/*    */   }
/*    */ 
/*    */   public long getOptions() {
/* 66 */     return this.options;
/*    */   }
/*    */ 
/*    */   public void setOptions(long paramLong) {
/* 70 */     this.options = paramLong;
/*    */   }
/*    */ 
/*    */   public int getUnitynote() {
/* 74 */     return this.unitynote;
/*    */   }
/*    */ 
/*    */   public void setUnitynote(int paramInt) {
/* 78 */     this.unitynote = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.DLSSampleOptions
 * JD-Core Version:    0.6.2
 */