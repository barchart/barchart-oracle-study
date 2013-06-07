/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ import java.util.ResourceBundle;
/*      */ 
/*      */ class SecurityPerm extends Perm
/*      */ {
/*      */   public SecurityPerm()
/*      */   {
/* 4018 */     super("SecurityPermission", "java.security.SecurityPermission", new String[] { "createAccessControlContext", "getDomainCombiner", "getPolicy", "setPolicy", "createPolicy.<" + PolicyTool.rb.getString("policy.type") + ">", "getProperty.<" + PolicyTool.rb.getString("property.name") + ">", "setProperty.<" + PolicyTool.rb.getString("property.name") + ">", "insertProvider.<" + PolicyTool.rb.getString("provider.name") + ">", "removeProvider.<" + PolicyTool.rb.getString("provider.name") + ">", "clearProviderProperties.<" + PolicyTool.rb.getString("provider.name") + ">", "putProviderProperty.<" + PolicyTool.rb.getString("provider.name") + ">", "removeProviderProperty.<" + PolicyTool.rb.getString("provider.name") + ">" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.SecurityPerm
 * JD-Core Version:    0.6.2
 */