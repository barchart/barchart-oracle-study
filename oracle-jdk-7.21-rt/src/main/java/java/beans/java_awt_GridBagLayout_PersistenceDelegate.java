/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.GridBagLayout;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ 
/*      */ class java_awt_GridBagLayout_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1078 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1079 */     Hashtable localHashtable = (Hashtable)ReflectionUtils.getPrivateField(paramObject1, GridBagLayout.class, "comptable", paramEncoder.getExceptionListener());
/*      */     Enumeration localEnumeration;
/* 1083 */     if (localHashtable != null)
/* 1084 */       for (localEnumeration = localHashtable.keys(); localEnumeration.hasMoreElements(); ) {
/* 1085 */         Object localObject = localEnumeration.nextElement();
/* 1086 */         invokeStatement(paramObject1, "addLayoutComponent", new Object[] { localObject, localHashtable.get(localObject) }, paramEncoder);
/*      */       }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_GridBagLayout_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */