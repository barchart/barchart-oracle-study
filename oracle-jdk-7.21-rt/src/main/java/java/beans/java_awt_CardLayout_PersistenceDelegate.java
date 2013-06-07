/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.CardLayout;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ 
/*      */ class java_awt_CardLayout_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1059 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1060 */     Hashtable localHashtable = (Hashtable)ReflectionUtils.getPrivateField(paramObject1, CardLayout.class, "tab", paramEncoder.getExceptionListener());
/*      */     Enumeration localEnumeration;
/* 1064 */     if (localHashtable != null)
/* 1065 */       for (localEnumeration = localHashtable.keys(); localEnumeration.hasMoreElements(); ) {
/* 1066 */         Object localObject = localEnumeration.nextElement();
/* 1067 */         invokeStatement(paramObject1, "addLayoutComponent", new Object[] { localObject, (String)localHashtable.get(localObject) }, paramEncoder);
/*      */       }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_CardLayout_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */