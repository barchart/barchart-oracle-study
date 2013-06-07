/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class LogPerm extends Perm
/*      */ {
/*      */   public LogPerm()
/*      */   {
/* 3843 */     super("LoggingPermission", "java.util.logging.LoggingPermission", new String[] { "control" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.LogPerm
 * JD-Core Version:    0.6.2
 */