/*    */ package java.awt;
/*    */ 
/*    */ abstract interface EventFilter
/*    */ {
/*    */   public abstract FilterAction acceptEvent(AWTEvent paramAWTEvent);
/*    */ 
/*    */   public static enum FilterAction
/*    */   {
/* 40 */     ACCEPT, 
/*    */ 
/* 46 */     REJECT, 
/*    */ 
/* 57 */     ACCEPT_IMMEDIATELY;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.EventFilter
 * JD-Core Version:    0.6.2
 */