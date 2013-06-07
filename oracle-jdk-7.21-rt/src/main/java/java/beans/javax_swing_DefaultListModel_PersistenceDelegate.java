/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.DefaultListModel;
/*      */ 
/*      */ class javax_swing_DefaultListModel_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1121 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1122 */     DefaultListModel localDefaultListModel1 = (DefaultListModel)paramObject1;
/* 1123 */     DefaultListModel localDefaultListModel2 = (DefaultListModel)paramObject2;
/* 1124 */     for (int i = localDefaultListModel2.getSize(); i < localDefaultListModel1.getSize(); i++)
/* 1125 */       invokeStatement(paramObject1, "add", new Object[] { localDefaultListModel1.getElementAt(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_DefaultListModel_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */