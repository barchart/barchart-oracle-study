/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class MBeanTrustPerm extends Perm
/*      */ {
/*      */   public MBeanTrustPerm()
/*      */   {
/* 3909 */     super("MBeanTrustPermission", "javax.management.MBeanTrustPermission", new String[] { "register" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.MBeanTrustPerm
 * JD-Core Version:    0.6.2
 */