/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.JTabbedPane;
/*      */ 
/*      */ class javax_swing_JTabbedPane_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1170 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1171 */     JTabbedPane localJTabbedPane = (JTabbedPane)paramObject1;
/* 1172 */     for (int i = 0; i < localJTabbedPane.getTabCount(); i++)
/* 1173 */       invokeStatement(paramObject1, "addTab", new Object[] { localJTabbedPane.getTitleAt(i), localJTabbedPane.getIconAt(i), localJTabbedPane.getComponentAt(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_JTabbedPane_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */