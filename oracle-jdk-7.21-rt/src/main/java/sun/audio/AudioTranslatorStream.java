/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class AudioTranslatorStream extends NativeAudioStream
/*    */ {
/* 40 */   private int length = 0;
/*    */ 
/*    */   public AudioTranslatorStream(InputStream paramInputStream) throws IOException {
/* 43 */     super(paramInputStream);
/*    */ 
/* 45 */     throw new InvalidAudioFormatException();
/*    */   }
/*    */ 
/*    */   public int getLength() {
/* 49 */     return this.length;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioTranslatorStream
 * JD-Core Version:    0.6.2
 */