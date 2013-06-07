/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ 
/*    */ public class AudioDataStream extends ByteArrayInputStream
/*    */ {
/*    */   AudioData ad;
/*    */ 
/*    */   public AudioDataStream(AudioData paramAudioData)
/*    */   {
/* 49 */     super(paramAudioData.buffer);
/* 50 */     this.ad = paramAudioData;
/*    */   }
/*    */ 
/*    */   AudioData getAudioData() {
/* 54 */     return this.ad;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioDataStream
 * JD-Core Version:    0.6.2
 */