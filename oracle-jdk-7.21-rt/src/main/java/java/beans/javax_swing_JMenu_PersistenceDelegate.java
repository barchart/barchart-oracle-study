/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import javax.swing.JMenu;
/*      */ 
/*      */ class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*      */   {
/* 1206 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 1207 */     JMenu localJMenu = (JMenu)paramObject1;
/* 1208 */     Component[] arrayOfComponent = localJMenu.getMenuComponents();
/* 1209 */     for (int i = 0; i < arrayOfComponent.length; i++)
/* 1210 */       invokeStatement(paramObject1, "add", new Object[] { arrayOfComponent[i] }, paramEncoder);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_JMenu_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */