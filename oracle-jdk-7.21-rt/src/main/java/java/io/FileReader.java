/*    */ package java.io;
/*    */ 
/*    */ public class FileReader extends InputStreamReader
/*    */ {
/*    */   public FileReader(String paramString)
/*    */     throws FileNotFoundException
/*    */   {
/* 58 */     super(new FileInputStream(paramString));
/*    */   }
/*    */ 
/*    */   public FileReader(File paramFile)
/*    */     throws FileNotFoundException
/*    */   {
/* 72 */     super(new FileInputStream(paramFile));
/*    */   }
/*    */ 
/*    */   public FileReader(FileDescriptor paramFileDescriptor)
/*    */   {
/* 82 */     super(new FileInputStream(paramFileDescriptor));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileReader
 * JD-Core Version:    0.6.2
 */