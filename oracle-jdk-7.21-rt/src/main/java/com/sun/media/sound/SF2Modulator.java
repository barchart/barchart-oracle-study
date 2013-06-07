/*    */ package com.sun.media.sound;
/*    */ 
/*    */ public class SF2Modulator
/*    */ {
/*    */   public static final int SOURCE_NONE = 0;
/*    */   public static final int SOURCE_NOTE_ON_VELOCITY = 2;
/*    */   public static final int SOURCE_NOTE_ON_KEYNUMBER = 3;
/*    */   public static final int SOURCE_POLY_PRESSURE = 10;
/*    */   public static final int SOURCE_CHANNEL_PRESSURE = 13;
/*    */   public static final int SOURCE_PITCH_WHEEL = 14;
/*    */   public static final int SOURCE_PITCH_SENSITIVITY = 16;
/*    */   public static final int SOURCE_MIDI_CONTROL = 128;
/*    */   public static final int SOURCE_DIRECTION_MIN_MAX = 0;
/*    */   public static final int SOURCE_DIRECTION_MAX_MIN = 256;
/*    */   public static final int SOURCE_POLARITY_UNIPOLAR = 0;
/*    */   public static final int SOURCE_POLARITY_BIPOLAR = 512;
/*    */   public static final int SOURCE_TYPE_LINEAR = 0;
/*    */   public static final int SOURCE_TYPE_CONCAVE = 1024;
/*    */   public static final int SOURCE_TYPE_CONVEX = 2048;
/*    */   public static final int SOURCE_TYPE_SWITCH = 3072;
/*    */   public static final int TRANSFORM_LINEAR = 0;
/*    */   public static final int TRANSFORM_ABSOLUTE = 2;
/*    */   protected int sourceOperator;
/*    */   protected int destinationOperator;
/*    */   protected short amount;
/*    */   protected int amountSourceOperator;
/*    */   protected int transportOperator;
/*    */ 
/*    */   public short getAmount()
/*    */   {
/* 59 */     return this.amount;
/*    */   }
/*    */ 
/*    */   public void setAmount(short paramShort) {
/* 63 */     this.amount = paramShort;
/*    */   }
/*    */ 
/*    */   public int getAmountSourceOperator() {
/* 67 */     return this.amountSourceOperator;
/*    */   }
/*    */ 
/*    */   public void setAmountSourceOperator(int paramInt) {
/* 71 */     this.amountSourceOperator = paramInt;
/*    */   }
/*    */ 
/*    */   public int getTransportOperator() {
/* 75 */     return this.transportOperator;
/*    */   }
/*    */ 
/*    */   public void setTransportOperator(int paramInt) {
/* 79 */     this.transportOperator = paramInt;
/*    */   }
/*    */ 
/*    */   public int getDestinationOperator() {
/* 83 */     return this.destinationOperator;
/*    */   }
/*    */ 
/*    */   public void setDestinationOperator(int paramInt) {
/* 87 */     this.destinationOperator = paramInt;
/*    */   }
/*    */ 
/*    */   public int getSourceOperator() {
/* 91 */     return this.sourceOperator;
/*    */   }
/*    */ 
/*    */   public void setSourceOperator(int paramInt) {
/* 95 */     this.sourceOperator = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SF2Modulator
 * JD-Core Version:    0.6.2
 */