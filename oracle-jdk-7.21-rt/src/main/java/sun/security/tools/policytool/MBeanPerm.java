/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class MBeanPerm extends Perm
/*      */ {
/*      */   public MBeanPerm()
/*      */   {
/* 3866 */     super("MBeanPermission", "javax.management.MBeanPermission", new String[0], new String[] { "addNotificationListener", "getAttribute", "getClassLoader", "getClassLoaderFor", "getClassLoaderRepository", "getDomains", "getMBeanInfo", "getObjectInstance", "instantiate", "invoke", "isInstanceOf", "queryMBeans", "queryNames", "registerMBean", "removeNotificationListener", "setAttribute", "unregisterMBean" });
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.MBeanPerm
 * JD-Core Version:    0.6.2
 */