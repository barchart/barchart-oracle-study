/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class MBeanSvrPerm extends Perm
/*      */ {
/*      */   public MBeanSvrPerm()
/*      */   {
/* 3895 */     super("MBeanServerPermission", "javax.management.MBeanServerPermission", new String[] { "createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.MBeanSvrPerm
 * JD-Core Version:    0.6.2
 */