/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.DefaultComboBoxModel;
/*      */ 
/*      */ class javax_swing_DefaultComboBoxModel_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1134 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1135 */     DefaultComboBoxModel localDefaultComboBoxModel = (DefaultComboBoxModel)paramObject1;
/* 1136 */     for (int i = 0; i < localDefaultComboBoxModel.getSize(); i++)
/* 1137 */       invokeStatement(paramObject1, "addElement", new Object[] { localDefaultComboBoxModel.getElementAt(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_DefaultComboBoxModel_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */