/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.nio.file.spi.FileTypeDetector;
/*    */ 
/*    */ public class DefaultFileTypeDetector
/*    */ {
/*    */   public static FileTypeDetector create()
/*    */   {
/* 34 */     return new GnomeFileTypeDetector();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.DefaultFileTypeDetector
 * JD-Core Version:    0.6.2
 */