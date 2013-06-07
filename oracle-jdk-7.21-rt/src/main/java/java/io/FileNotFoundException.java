/*    */ package java.io;
/*    */ 
/*    */ public class FileNotFoundException extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = -897856973823710492L;
/*    */ 
/*    */   public FileNotFoundException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FileNotFoundException(String paramString)
/*    */   {
/* 64 */     super(paramString);
/*    */   }
/*    */ 
/*    */   private FileNotFoundException(String paramString1, String paramString2)
/*    */   {
/* 77 */     super(paramString1 + (paramString2 == null ? "" : new StringBuilder().append(" (").append(paramString2).append(")").toString()));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileNotFoundException
 * JD-Core Version:    0.6.2
 */