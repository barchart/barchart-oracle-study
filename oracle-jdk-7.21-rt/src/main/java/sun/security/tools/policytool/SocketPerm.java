/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class SocketPerm extends Perm
/*      */ {
/*      */   public SocketPerm()
/*      */   {
/* 4082 */     super("SocketPermission", "java.net.SocketPermission", new String[0], new String[] { "accept", "connect", "listen", "resolve" });
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.SocketPerm
 * JD-Core Version:    0.6.2
 */