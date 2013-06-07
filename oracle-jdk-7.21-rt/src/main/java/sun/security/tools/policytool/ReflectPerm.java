/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class ReflectPerm extends Perm
/*      */ {
/*      */   public ReflectPerm()
/*      */   {
/* 3966 */     super("ReflectPermission", "java.lang.reflect.ReflectPermission", new String[] { "suppressAccessChecks" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.ReflectPerm
 * JD-Core Version:    0.6.2
 */