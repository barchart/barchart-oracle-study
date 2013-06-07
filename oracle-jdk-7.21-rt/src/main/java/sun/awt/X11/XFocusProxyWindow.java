/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public class XFocusProxyWindow extends XBaseWindow
/*    */ {
/*    */   XWindowPeer owner;
/*    */ 
/*    */   public XFocusProxyWindow(XWindowPeer paramXWindowPeer)
/*    */   {
/* 39 */     super(new XCreateWindowParams(new Object[] { "bounds", new Rectangle(-1, -1, 1, 1), "parent window", new Long(paramXWindowPeer.getWindow()), "event mask", new Long(2097155L) }));
/*    */ 
/* 44 */     this.owner = paramXWindowPeer;
/*    */   }
/*    */ 
/*    */   public void postInit(XCreateWindowParams paramXCreateWindowParams) {
/* 48 */     super.postInit(paramXCreateWindowParams);
/* 49 */     setWMClass(getWMClass());
/* 50 */     xSetVisible(true);
/*    */   }
/*    */ 
/*    */   protected String getWMName() {
/* 54 */     return "FocusProxy";
/*    */   }
/*    */   protected String[] getWMClass() {
/* 57 */     return new String[] { "Focus-Proxy-Window", "FocusProxy" };
/*    */   }
/*    */ 
/*    */   public XWindowPeer getOwner() {
/* 61 */     return this.owner;
/*    */   }
/*    */ 
/*    */   public void dispatchEvent(XEvent paramXEvent) {
/* 65 */     int i = paramXEvent.get_type();
/* 66 */     switch (i)
/*    */     {
/*    */     case 9:
/*    */     case 10:
/* 70 */       handleFocusEvent(paramXEvent);
/*    */     }
/*    */ 
/* 73 */     super.dispatchEvent(paramXEvent);
/*    */   }
/*    */ 
/*    */   public void handleFocusEvent(XEvent paramXEvent) {
/* 77 */     this.owner.handleFocusEvent(paramXEvent);
/*    */   }
/*    */ 
/*    */   public void handleKeyPress(XEvent paramXEvent) {
/* 81 */     this.owner.handleKeyPress(paramXEvent);
/*    */   }
/*    */ 
/*    */   public void handleKeyRelease(XEvent paramXEvent) {
/* 85 */     this.owner.handleKeyRelease(paramXEvent);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XFocusProxyWindow
 * JD-Core Version:    0.6.2
 */