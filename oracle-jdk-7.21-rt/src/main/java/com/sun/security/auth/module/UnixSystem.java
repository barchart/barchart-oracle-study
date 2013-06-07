/*    */ package com.sun.security.auth.module;
/*    */ 
/*    */ public class UnixSystem
/*    */ {
/*    */   protected String username;
/*    */   protected long uid;
/*    */   protected long gid;
/*    */   protected long[] groups;
/*    */ 
/*    */   private native void getUnixInfo();
/*    */ 
/*    */   public UnixSystem()
/*    */   {
/* 47 */     System.loadLibrary("jaas_unix");
/* 48 */     getUnixInfo();
/*    */   }
/*    */ 
/*    */   public String getUsername()
/*    */   {
/* 59 */     return this.username;
/*    */   }
/*    */ 
/*    */   public long getUid()
/*    */   {
/* 70 */     return this.uid;
/*    */   }
/*    */ 
/*    */   public long getGid()
/*    */   {
/* 81 */     return this.gid;
/*    */   }
/*    */ 
/*    */   public long[] getGroups()
/*    */   {
/* 92 */     return this.groups == null ? null : (long[])this.groups.clone();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.auth.module.UnixSystem
 * JD-Core Version:    0.6.2
 */