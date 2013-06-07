/*    */ package sun.audio;
/*    */ 
/*    */ public class ContinuousAudioDataStream extends AudioDataStream
/*    */ {
/*    */   public ContinuousAudioDataStream(AudioData paramAudioData)
/*    */   {
/* 54 */     super(paramAudioData);
/*    */   }
/*    */ 
/*    */   public int read()
/*    */   {
/* 60 */     int i = super.read();
/*    */ 
/* 62 */     if (i == -1) {
/* 63 */       reset();
/* 64 */       i = super.read();
/*    */     }
/*    */ 
/* 67 */     return i;
/*    */   }
/*    */ 
/*    */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */   {
/* 75 */     for (int i = 0; i < paramInt2; ) {
/* 76 */       int j = super.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
/* 77 */       if (j >= 0) i += j; else {
/* 78 */         reset();
/*    */       }
/*    */     }
/* 81 */     return i;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.ContinuousAudioDataStream
 * JD-Core Version:    0.6.2
 */