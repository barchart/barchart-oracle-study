/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Window;
/*     */ import java.util.Objects;
/*     */ 
/*     */ class java_awt_Component_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 894 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 895 */     Component localComponent1 = (Component)paramObject1;
/* 896 */     Component localComponent2 = (Component)paramObject2;
/*     */ 
/* 901 */     if (!(paramObject1 instanceof Window)) {
/* 902 */       localObject1 = localComponent1.isBackgroundSet() ? localComponent1.getBackground() : null;
/* 903 */       Object localObject2 = localComponent2.isBackgroundSet() ? localComponent2.getBackground() : null;
/* 904 */       if (!Objects.equals(localObject1, localObject2)) {
/* 905 */         invokeStatement(paramObject1, "setBackground", new Object[] { localObject1 }, paramEncoder);
/*     */       }
/* 907 */       Object localObject3 = localComponent1.isForegroundSet() ? localComponent1.getForeground() : null;
/* 908 */       Object localObject4 = localComponent2.isForegroundSet() ? localComponent2.getForeground() : null;
/* 909 */       if (!Objects.equals(localObject3, localObject4)) {
/* 910 */         invokeStatement(paramObject1, "setForeground", new Object[] { localObject3 }, paramEncoder);
/*     */       }
/* 912 */       Object localObject5 = localComponent1.isFontSet() ? localComponent1.getFont() : null;
/* 913 */       Object localObject6 = localComponent2.isFontSet() ? localComponent2.getFont() : null;
/* 914 */       if (!Objects.equals(localObject5, localObject6)) {
/* 915 */         invokeStatement(paramObject1, "setFont", new Object[] { localObject5 }, paramEncoder);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 920 */     Object localObject1 = localComponent1.getParent();
/* 921 */     if ((localObject1 == null) || (((Container)localObject1).getLayout() == null))
/*     */     {
/* 923 */       boolean bool1 = localComponent1.getLocation().equals(localComponent2.getLocation());
/* 924 */       boolean bool2 = localComponent1.getSize().equals(localComponent2.getSize());
/* 925 */       if ((!bool1) && (!bool2)) {
/* 926 */         invokeStatement(paramObject1, "setBounds", new Object[] { localComponent1.getBounds() }, paramEncoder);
/*     */       }
/* 928 */       else if (!bool1) {
/* 929 */         invokeStatement(paramObject1, "setLocation", new Object[] { localComponent1.getLocation() }, paramEncoder);
/*     */       }
/* 931 */       else if (!bool2)
/* 932 */         invokeStatement(paramObject1, "setSize", new Object[] { localComponent1.getSize() }, paramEncoder);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Component_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */