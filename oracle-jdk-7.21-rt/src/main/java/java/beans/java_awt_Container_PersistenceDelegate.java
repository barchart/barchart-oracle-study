/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import javax.swing.JLayeredPane;
/*     */ import javax.swing.JScrollPane;
/*     */ 
/*     */ class java_awt_Container_PersistenceDelegate extends DefaultPersistenceDelegate
/*     */ {
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 941 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/*     */ 
/* 944 */     if ((paramObject1 instanceof JScrollPane)) {
/* 945 */       return;
/*     */     }
/* 947 */     Container localContainer1 = (Container)paramObject1;
/* 948 */     Component[] arrayOfComponent1 = localContainer1.getComponents();
/* 949 */     Container localContainer2 = (Container)paramObject2;
/* 950 */     Component[] arrayOfComponent2 = localContainer2 == null ? new Component[0] : localContainer2.getComponents();
/*     */ 
/* 952 */     Object localObject1 = (localContainer1.getLayout() instanceof BorderLayout) ? (BorderLayout)localContainer1.getLayout() : null;
/*     */ 
/* 956 */     Object localObject2 = (paramObject1 instanceof JLayeredPane) ? (JLayeredPane)paramObject1 : null;
/*     */ 
/* 961 */     for (int i = arrayOfComponent2.length; i < arrayOfComponent1.length; i++) {
/* 962 */       Object[] arrayOfObject = { localObject2 != null ? new Object[] { arrayOfComponent1[i], Integer.valueOf(localObject2.getLayer(arrayOfComponent1[i])), Integer.valueOf(-1) } : localObject1 != null ? new Object[] { arrayOfComponent1[i], localObject1.getConstraints(arrayOfComponent1[i]) } : arrayOfComponent1[i] };
/*     */ 
/* 968 */       invokeStatement(paramObject1, "add", arrayOfObject, paramEncoder);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Container_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */