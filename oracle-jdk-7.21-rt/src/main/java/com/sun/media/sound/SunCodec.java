/*    */ package com.sun.media.sound;
/*    */ 
/*    */ import javax.sound.sampled.AudioFormat;
/*    */ import javax.sound.sampled.AudioFormat.Encoding;
/*    */ import javax.sound.sampled.AudioInputStream;
/*    */ import javax.sound.sampled.spi.FormatConversionProvider;
/*    */ 
/*    */ abstract class SunCodec extends FormatConversionProvider
/*    */ {
/*    */   AudioFormat.Encoding[] inputEncodings;
/*    */   AudioFormat.Encoding[] outputEncodings;
/*    */ 
/*    */   protected SunCodec(AudioFormat.Encoding[] paramArrayOfEncoding1, AudioFormat.Encoding[] paramArrayOfEncoding2)
/*    */   {
/* 59 */     this.inputEncodings = paramArrayOfEncoding1;
/* 60 */     this.outputEncodings = paramArrayOfEncoding2;
/*    */   }
/*    */ 
/*    */   public AudioFormat.Encoding[] getSourceEncodings()
/*    */   {
/* 68 */     AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[this.inputEncodings.length];
/* 69 */     System.arraycopy(this.inputEncodings, 0, arrayOfEncoding, 0, this.inputEncodings.length);
/* 70 */     return arrayOfEncoding;
/*    */   }
/*    */ 
/*    */   public AudioFormat.Encoding[] getTargetEncodings()
/*    */   {
/* 76 */     AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[this.outputEncodings.length];
/* 77 */     System.arraycopy(this.outputEncodings, 0, arrayOfEncoding, 0, this.outputEncodings.length);
/* 78 */     return arrayOfEncoding;
/*    */   }
/*    */ 
/*    */   public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat);
/*    */ 
/*    */   public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat);
/*    */ 
/*    */   public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream);
/*    */ 
/*    */   public abstract AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SunCodec
 * JD-Core Version:    0.6.2
 */