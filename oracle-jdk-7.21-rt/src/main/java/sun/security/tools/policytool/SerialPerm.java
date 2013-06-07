/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class SerialPerm extends Perm
/*      */ {
/*      */   public SerialPerm()
/*      */   {
/* 4056 */     super("SerializablePermission", "java.io.SerializablePermission", new String[] { "enableSubclassImplementation", "enableSubstitution" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.SerialPerm
 * JD-Core Version:    0.6.2
 */