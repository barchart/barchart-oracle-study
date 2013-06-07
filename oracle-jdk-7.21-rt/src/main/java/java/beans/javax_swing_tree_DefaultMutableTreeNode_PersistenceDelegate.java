/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.tree.DefaultMutableTreeNode;
/*      */ 
/*      */ class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1147 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1148 */     DefaultMutableTreeNode localDefaultMutableTreeNode1 = (DefaultMutableTreeNode)paramObject1;
/*      */ 
/* 1150 */     DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)paramObject2;
/*      */ 
/* 1152 */     for (int i = localDefaultMutableTreeNode2.getChildCount(); i < localDefaultMutableTreeNode1.getChildCount(); i++)
/* 1153 */       invokeStatement(paramObject1, "add", new Object[] { localDefaultMutableTreeNode1.getChildAt(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */