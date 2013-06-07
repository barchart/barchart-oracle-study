/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ 
/*      */ class java_awt_BorderLayout_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/* 1026 */   private static final String[] CONSTRAINTS = { "North", "South", "East", "West", "Center", "First", "Last", "Before", "After" };
/*      */ 
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1040 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1041 */     BorderLayout localBorderLayout1 = (BorderLayout)paramObject1;
/* 1042 */     BorderLayout localBorderLayout2 = (BorderLayout)paramObject2;
/* 1043 */     for (String str : CONSTRAINTS) {
/* 1044 */       Component localComponent1 = localBorderLayout1.getLayoutComponent(str);
/* 1045 */       Component localComponent2 = localBorderLayout2.getLayoutComponent(str);
/*      */ 
/* 1047 */       if ((localComponent1 != null) && (localComponent2 == null))
/* 1048 */         invokeStatement(paramObject1, "addLayoutComponent", new Object[] { localComponent1, str }, paramEncoder);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_BorderLayout_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */