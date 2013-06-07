/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.Window;
/*      */ 
/*      */ class javax_swing_JFrame_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1100 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1101 */     Window localWindow1 = (Window)paramObject1;
/* 1102 */     Window localWindow2 = (Window)paramObject2;
/* 1103 */     boolean bool1 = localWindow1.isVisible();
/* 1104 */     boolean bool2 = localWindow2.isVisible();
/* 1105 */     if (bool2 != bool1)
/*      */     {
/* 1107 */       boolean bool3 = paramEncoder.executeStatements;
/* 1108 */       paramEncoder.executeStatements = false;
/* 1109 */       invokeStatement(paramObject1, "setVisible", new Object[] { Boolean.valueOf(bool1) }, paramEncoder);
/* 1110 */       paramEncoder.executeStatements = bool3;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_JFrame_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */