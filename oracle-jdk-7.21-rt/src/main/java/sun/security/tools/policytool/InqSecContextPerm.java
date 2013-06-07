/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class InqSecContextPerm extends Perm
/*      */ {
/*      */   public InqSecContextPerm()
/*      */   {
/* 3829 */     super("InquireSecContextPermission", "com.sun.security.jgss.InquireSecContextPermission", new String[] { "KRB5_GET_SESSION_KEY", "KRB5_GET_TKT_FLAGS", "KRB5_GET_AUTHZ_DATA", "KRB5_GET_AUTHTIME" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.InqSecContextPerm
 * JD-Core Version:    0.6.2
 */