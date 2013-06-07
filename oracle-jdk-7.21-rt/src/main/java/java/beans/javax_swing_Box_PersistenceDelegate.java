/*      */ package java.beans;
/*      */ 
/*      */ import javax.swing.Box;
/*      */ 
/*      */ class javax_swing_Box_PersistenceDelegate extends DefaultPersistenceDelegate
/*      */ {
/*      */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*      */   {
/* 1185 */     return (super.mutatesTo(paramObject1, paramObject2)) && (getAxis(paramObject1).equals(getAxis(paramObject2)));
/*      */   }
/*      */ 
/*      */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 1189 */     return new Expression(paramObject, paramObject.getClass(), "new", new Object[] { getAxis(paramObject) });
/*      */   }
/*      */ 
/*      */   private Integer getAxis(Object paramObject) {
/* 1193 */     Box localBox = (Box)paramObject;
/* 1194 */     return (Integer)MetaData.getPrivateFieldValue(localBox.getLayout(), "javax.swing.BoxLayout.axis");
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.javax_swing_Box_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */