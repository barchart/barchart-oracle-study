/*    */ package sun.nio.fs;
/*    */ 
/*    */ class UnixFileKey
/*    */ {
/*    */   private final long st_dev;
/*    */   private final long st_ino;
/*    */ 
/*    */   UnixFileKey(long paramLong1, long paramLong2)
/*    */   {
/* 37 */     this.st_dev = paramLong1;
/* 38 */     this.st_ino = paramLong2;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 43 */     return (int)(this.st_dev ^ this.st_dev >>> 32) + (int)(this.st_ino ^ this.st_ino >>> 32);
/*    */   }
/*    */ 
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 49 */     if (paramObject == this)
/* 50 */       return true;
/* 51 */     if (!(paramObject instanceof UnixFileKey))
/* 52 */       return false;
/* 53 */     UnixFileKey localUnixFileKey = (UnixFileKey)paramObject;
/* 54 */     return (this.st_dev == localUnixFileKey.st_dev) && (this.st_ino == localUnixFileKey.st_ino);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 59 */     StringBuilder localStringBuilder = new StringBuilder();
/* 60 */     localStringBuilder.append("(dev=").append(Long.toHexString(this.st_dev)).append(",ino=").append(this.st_ino).append(')');
/*    */ 
/* 65 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileKey
 * JD-Core Version:    0.6.2
 */