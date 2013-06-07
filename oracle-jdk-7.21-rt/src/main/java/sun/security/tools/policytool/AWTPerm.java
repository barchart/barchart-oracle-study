/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ class AWTPerm extends Perm
/*      */ {
/*      */   public AWTPerm()
/*      */   {
/* 3779 */     super("AWTPermission", "java.awt.AWTPermission", new String[] { "accessClipboard", "accessEventQueue", "accessSystemTray", "createRobot", "fullScreenExclusive", "listenToAllAWTEvents", "readDisplayPixels", "replaceKeyboardFocusManager", "setAppletStub", "setWindowAlwaysOnTop", "showWindowWithoutWarningBanner", "toolkitModality", "watchMousePointer" }, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.policytool.AWTPerm
 * JD-Core Version:    0.6.2
 */