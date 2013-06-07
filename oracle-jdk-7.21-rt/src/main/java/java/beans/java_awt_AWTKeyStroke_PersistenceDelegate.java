/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.AWTKeyStroke;
/*     */ 
/*     */ final class java_awt_AWTKeyStroke_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 807 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 811 */     AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)paramObject;
/*     */ 
/* 813 */     int i = localAWTKeyStroke.getKeyChar();
/* 814 */     int j = localAWTKeyStroke.getKeyCode();
/* 815 */     int k = localAWTKeyStroke.getModifiers();
/* 816 */     boolean bool = localAWTKeyStroke.isOnKeyRelease();
/*     */ 
/* 818 */     Object[] arrayOfObject = null;
/* 819 */     if (i == 65535) {
/* 820 */       arrayOfObject = new Object[] { Integer.valueOf(j), Integer.valueOf(k), !bool ? new Object[] { Integer.valueOf(j), Integer.valueOf(k) } : Boolean.valueOf(bool) };
/*     */     }
/* 823 */     else if (j == 0) {
/* 824 */       if (!bool) {
/* 825 */         arrayOfObject = new Object[] { Character.valueOf(i), k == 0 ? new Object[] { Character.valueOf(i) } : Integer.valueOf(k) };
/*     */       }
/* 828 */       else if (k == 0) {
/* 829 */         arrayOfObject = new Object[] { Character.valueOf(i), Boolean.valueOf(bool) };
/*     */       }
/*     */     }
/* 832 */     if (arrayOfObject == null) {
/* 833 */       throw new IllegalStateException("Unsupported KeyStroke: " + localAWTKeyStroke);
/*     */     }
/* 835 */     Class localClass = localAWTKeyStroke.getClass();
/* 836 */     String str = localClass.getName();
/*     */ 
/* 838 */     int m = str.lastIndexOf('.') + 1;
/* 839 */     if (m > 0) {
/* 840 */       str = str.substring(m);
/*     */     }
/* 842 */     return new Expression(localAWTKeyStroke, localClass, "get" + str, arrayOfObject);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_AWTKeyStroke_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */