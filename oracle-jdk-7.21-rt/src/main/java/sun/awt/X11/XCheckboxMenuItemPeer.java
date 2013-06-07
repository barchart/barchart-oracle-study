/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.CheckboxMenuItem;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.peer.CheckboxMenuItemPeer;
/*    */ import sun.awt.AWTAccessor;
/*    */ import sun.awt.AWTAccessor.CheckboxMenuItemAccessor;
/*    */ 
/*    */ class XCheckboxMenuItemPeer extends XMenuItemPeer
/*    */   implements CheckboxMenuItemPeer
/*    */ {
/*    */   XCheckboxMenuItemPeer(CheckboxMenuItem paramCheckboxMenuItem)
/*    */   {
/* 42 */     super(paramCheckboxMenuItem);
/*    */   }
/*    */ 
/*    */   public void setState(boolean paramBoolean)
/*    */   {
/* 53 */     repaintIfShowing();
/*    */   }
/*    */ 
/*    */   boolean getTargetState()
/*    */   {
/* 62 */     return AWTAccessor.getCheckboxMenuItemAccessor().getState((CheckboxMenuItem)getTarget());
/*    */   }
/*    */ 
/*    */   void action(final long paramLong)
/*    */   {
/* 76 */     XToolkit.executeOnEventHandlerThread((CheckboxMenuItem)getTarget(), new Runnable() {
/*    */       public void run() {
/* 78 */         XCheckboxMenuItemPeer.this.doToggleState(paramLong);
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   private void doToggleState(long paramLong)
/*    */   {
/* 90 */     CheckboxMenuItem localCheckboxMenuItem = (CheckboxMenuItem)getTarget();
/* 91 */     boolean bool = !getTargetState();
/* 92 */     localCheckboxMenuItem.setState(bool);
/* 93 */     ItemEvent localItemEvent = new ItemEvent(localCheckboxMenuItem, 701, getTargetLabel(), getTargetState() ? 1 : 2);
/*    */ 
/* 97 */     XWindow.postEventStatic(localItemEvent);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XCheckboxMenuItemPeer
 * JD-Core Version:    0.6.2
 */