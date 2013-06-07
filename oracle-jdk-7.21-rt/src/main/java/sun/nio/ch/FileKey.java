/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class FileKey
/*    */ {
/*    */   private long st_dev;
/*    */   private long st_ino;
/*    */ 
/*    */   public static FileKey create(FileDescriptor paramFileDescriptor)
/*    */   {
/* 42 */     FileKey localFileKey = new FileKey();
/*    */     try {
/* 44 */       localFileKey.init(paramFileDescriptor);
/*    */     } catch (IOException localIOException) {
/* 46 */       throw new Error(localIOException);
/*    */     }
/* 48 */     return localFileKey;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 52 */     return (int)(this.st_dev ^ this.st_dev >>> 32) + (int)(this.st_ino ^ this.st_ino >>> 32);
/*    */   }
/*    */ 
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 57 */     if (paramObject == this)
/* 58 */       return true;
/* 59 */     if (!(paramObject instanceof FileKey))
/* 60 */       return false;
/* 61 */     FileKey localFileKey = (FileKey)paramObject;
/* 62 */     if ((this.st_dev != localFileKey.st_dev) || (this.st_ino != localFileKey.st_ino))
/*    */     {
/* 64 */       return false;
/*    */     }
/* 66 */     return true;
/*    */   }
/*    */   private native void init(FileDescriptor paramFileDescriptor) throws IOException;
/*    */ 
/*    */   private static native void initIDs();
/*    */ 
/*    */   static {
/* 73 */     initIDs();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.FileKey
 * JD-Core Version:    0.6.2
 */