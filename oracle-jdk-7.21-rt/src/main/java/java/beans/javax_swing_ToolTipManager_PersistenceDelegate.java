/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.ToolTipManager;
/*      */ 
/*      */ class javax_swing_ToolTipManager_PersistenceDelegate extends PersistenceDelegate
/*      */ {
/*      */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*      */   {
/* 1162 */     return new Expression(paramObject, ToolTipManager.class, "sharedInstance", new Object[0]);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_ToolTipManager_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */