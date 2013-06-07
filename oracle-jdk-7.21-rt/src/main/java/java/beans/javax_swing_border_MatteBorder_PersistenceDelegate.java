/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.Insets;
/*      */ import javax.swing.border.MatteBorder;
/*      */ 
/*      */ final class javax_swing_border_MatteBorder_PersistenceDelegate extends PersistenceDelegate
/*      */ {
/*      */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*      */   {
/* 1224 */     MatteBorder localMatteBorder = (MatteBorder)paramObject;
/* 1225 */     Insets localInsets = localMatteBorder.getBorderInsets();
/* 1226 */     Object localObject = localMatteBorder.getTileIcon();
/* 1227 */     if (localObject == null) {
/* 1228 */       localObject = localMatteBorder.getMatteColor();
/*      */     }
/* 1230 */     Object[] arrayOfObject = { Integer.valueOf(localInsets.top), Integer.valueOf(localInsets.left), Integer.valueOf(localInsets.bottom), Integer.valueOf(localInsets.right), localObject };
/*      */ 
/* 1237 */     return new Expression(localMatteBorder, localMatteBorder.getClass(), "new", arrayOfObject);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_border_MatteBorder_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */