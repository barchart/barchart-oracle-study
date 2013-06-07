/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.SequenceInputStream;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ public class AudioStreamSequence extends SequenceInputStream
/*    */ {
/*    */   Enumeration e;
/*    */   InputStream in;
/*    */ 
/*    */   public AudioStreamSequence(Enumeration paramEnumeration)
/*    */   {
/* 57 */     super(paramEnumeration);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioStreamSequence
 * JD-Core Version:    0.6.2
 */