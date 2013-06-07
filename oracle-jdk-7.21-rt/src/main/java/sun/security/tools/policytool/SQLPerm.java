/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class SQLPerm extends Perm
/*      */ {
/*      */   public SQLPerm()
/*      */   {
/* 4098 */     super("SQLPermission", "java.sql.SQLPermission", new String[] { "setLog", "callAbort", "setSyncFactory", "setNetworkTimeout" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.SQLPerm
 * JD-Core Version:    0.6.2
 */