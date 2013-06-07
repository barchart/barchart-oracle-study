/*    */ package sun.awt.X11;
/*    */ 
/*    */ class XRootWindow extends XBaseWindow
/*    */ {
/* 34 */   private static XRootWindow xawtRootWindow = null;
/*    */ 
/* 36 */   static XRootWindow getInstance() { XToolkit.awtLock();
/*    */     try {
/* 38 */       if (xawtRootWindow == null) {
/* 39 */         xawtRootWindow = new XRootWindow();
/* 40 */         xawtRootWindow.init(xawtRootWindow.getDelayedParams().delete("delayed"));
/*    */       }
/* 42 */       return xawtRootWindow;
/*    */     } finally {
/* 44 */       XToolkit.awtUnlock();
/*    */     } }
/*    */ 
/*    */   private XRootWindow()
/*    */   {
/* 49 */     super(new XCreateWindowParams(new Object[] { "delayed", Boolean.TRUE }));
/*    */   }
/*    */ 
/*    */   public void postInit(XCreateWindowParams paramXCreateWindowParams) {
/* 53 */     super.postInit(paramXCreateWindowParams);
/* 54 */     setWMClass(getWMClass());
/*    */   }
/*    */ 
/*    */   protected String getWMName() {
/* 58 */     return XToolkit.getAWTAppClassName();
/*    */   }
/*    */   protected String[] getWMClass() {
/* 61 */     return new String[] { XToolkit.getAWTAppClassName(), XToolkit.getAWTAppClassName() };
/*    */   }
/*    */ 
/*    */   private static long getXRootWindow()
/*    */   {
/* 66 */     return getXAWTRootWindow().getWindow();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XRootWindow
 * JD-Core Version:    0.6.2
 */