/*    */ package sun.management;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.security.AccessController;
/*    */ import sun.security.action.LoadLibraryAction;
/*    */ 
/*    */ public class FileSystemImpl extends FileSystem
/*    */ {
/*    */   public boolean supportsFileSecurity(File paramFile)
/*    */     throws IOException
/*    */   {
/* 37 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isAccessUserOnly(File paramFile) throws IOException {
/* 41 */     return isAccessUserOnly0(paramFile.getPath());
/*    */   }
/*    */ 
/*    */   static native boolean isAccessUserOnly0(String paramString)
/*    */     throws IOException;
/*    */ 
/*    */   static
/*    */   {
/* 51 */     AccessController.doPrivileged(new LoadLibraryAction("management"));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.FileSystemImpl
 * JD-Core Version:    0.6.2
 */