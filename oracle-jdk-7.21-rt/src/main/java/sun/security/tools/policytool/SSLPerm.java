/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class SSLPerm extends Perm
/*      */ {
/*      */   public SSLPerm()
/*      */   {
/* 4112 */     super("SSLPermission", "javax.net.ssl.SSLPermission", new String[] { "setHostnameVerifier", "getSSLSessionContext" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.SSLPerm
 * JD-Core Version:    0.6.2
 */