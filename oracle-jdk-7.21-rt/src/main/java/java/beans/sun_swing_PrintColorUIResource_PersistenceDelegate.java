/*      */ package java.beans;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import javax.swing.plaf.ColorUIResource;
/*      */ 
/*      */ final class sun_swing_PrintColorUIResource_PersistenceDelegate extends PersistenceDelegate
/*      */ {
/*      */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*      */   {
/* 1266 */     return paramObject1.equals(paramObject2);
/*      */   }
/*      */ 
/*      */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 1270 */     Color localColor = (Color)paramObject;
/* 1271 */     Object[] arrayOfObject = { Integer.valueOf(localColor.getRGB()) };
/* 1272 */     return new Expression(localColor, ColorUIResource.class, "new", arrayOfObject);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.sun_swing_PrintColorUIResource_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */