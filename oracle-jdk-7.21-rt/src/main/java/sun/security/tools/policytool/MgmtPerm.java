/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class MgmtPerm extends Perm
/*      */ {
/*      */   public MgmtPerm()
/*      */   {
/* 3854 */     super("ManagementPermission", "java.lang.management.ManagementPermission", new String[] { "control", "monitor" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.MgmtPerm
 * JD-Core Version:    0.6.2
 */