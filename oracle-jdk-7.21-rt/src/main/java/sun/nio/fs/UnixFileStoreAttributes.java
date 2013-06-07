/*    */ package sun.nio.fs;
/*    */ 
/*    */ class UnixFileStoreAttributes
/*    */ {
/*    */   private long f_frsize;
/*    */   private long f_blocks;
/*    */   private long f_bfree;
/*    */   private long f_bavail;
/*    */ 
/*    */   static UnixFileStoreAttributes get(UnixPath paramUnixPath)
/*    */     throws UnixException
/*    */   {
/* 38 */     UnixFileStoreAttributes localUnixFileStoreAttributes = new UnixFileStoreAttributes();
/* 39 */     UnixNativeDispatcher.statvfs(paramUnixPath, localUnixFileStoreAttributes);
/* 40 */     return localUnixFileStoreAttributes;
/*    */   }
/*    */ 
/*    */   long blockSize() {
/* 44 */     return this.f_frsize;
/*    */   }
/*    */ 
/*    */   long totalBlocks() {
/* 48 */     return this.f_blocks;
/*    */   }
/*    */ 
/*    */   long freeBlocks() {
/* 52 */     return this.f_bfree;
/*    */   }
/*    */ 
/*    */   long availableBlocks() {
/* 56 */     return this.f_bavail;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileStoreAttributes
 * JD-Core Version:    0.6.2
 */