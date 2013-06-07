/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.List;
/*      */ 
/*      */ class java_awt_List_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1012 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1013 */     List localList1 = (List)paramObject1;
/* 1014 */     List localList2 = (List)paramObject2;
/* 1015 */     for (int i = localList2.getItemCount(); i < localList1.getItemCount(); i++)
/* 1016 */       invokeStatement(paramObject1, "add", new Object[] { localList1.getItem(i) }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_List_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */