/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.MenuBar;
/*      */ 
/*      */ class java_awt_MenuBar_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1000 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1001 */     MenuBar localMenuBar1 = (MenuBar)paramObject1;
/* 1002 */     MenuBar localMenuBar2 = (MenuBar)paramObject2;
/* 1003 */     for (int i = localMenuBar2.getMenuCount(); i < localMenuBar1.getMenuCount(); i++)
/* 1004 */       invokeStatement(paramObject1, "add", new Object[] { localMenuBar1.getMenu(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_MenuBar_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */