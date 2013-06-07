/*     */ package javax.swing.text.html;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.ComponentView;
/*     */ import javax.swing.text.Element;
/*     */ 
/*     */ public class ObjectView extends ComponentView
/*     */ {
/*     */   public ObjectView(Element paramElement)
/*     */   {
/*  78 */     super(paramElement);
/*     */   }
/*     */ 
/*     */   protected Component createComponent()
/*     */   {
/*  87 */     AttributeSet localAttributeSet = getElement().getAttributes();
/*  88 */     String str = (String)localAttributeSet.getAttribute(HTML.Attribute.CLASSID);
/*     */     try {
/*  90 */       Class localClass = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
/*     */ 
/*  92 */       Object localObject = localClass.newInstance();
/*  93 */       if ((localObject instanceof Component)) {
/*  94 */         Component localComponent = (Component)localObject;
/*  95 */         setParameters(localComponent, localAttributeSet);
/*  96 */         return localComponent;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */     }
/*     */ 
/* 103 */     return getUnloadableRepresentation();
/*     */   }
/*     */ 
/*     */   Component getUnloadableRepresentation()
/*     */   {
/* 113 */     JLabel localJLabel = new JLabel("??");
/* 114 */     localJLabel.setForeground(Color.red);
/* 115 */     return localJLabel;
/*     */   }
/*     */ 
/*     */   private Class getClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 130 */     Class localClass2 = getDocument().getClass();
/* 131 */     ClassLoader localClassLoader = localClass2.getClassLoader();
/*     */     Class localClass1;
/* 132 */     if (localClassLoader != null)
/* 133 */       localClass1 = localClassLoader.loadClass(paramString);
/*     */     else {
/* 135 */       localClass1 = Class.forName(paramString);
/*     */     }
/* 137 */     return localClass1;
/*     */   }
/*     */ 
/*     */   private void setParameters(Component paramComponent, AttributeSet paramAttributeSet)
/*     */   {
/* 146 */     Class localClass = paramComponent.getClass();
/*     */     BeanInfo localBeanInfo;
/*     */     try
/*     */     {
/* 149 */       localBeanInfo = Introspector.getBeanInfo(localClass);
/*     */     } catch (IntrospectionException localIntrospectionException) {
/* 151 */       System.err.println("introspector failed, ex: " + localIntrospectionException);
/* 152 */       return;
/*     */     }
/* 154 */     PropertyDescriptor[] arrayOfPropertyDescriptor = localBeanInfo.getPropertyDescriptors();
/* 155 */     for (int i = 0; i < arrayOfPropertyDescriptor.length; i++)
/*     */     {
/* 157 */       Object localObject = paramAttributeSet.getAttribute(arrayOfPropertyDescriptor[i].getName());
/* 158 */       if ((localObject instanceof String))
/*     */       {
/* 160 */         String str = (String)localObject;
/* 161 */         Method localMethod = arrayOfPropertyDescriptor[i].getWriteMethod();
/* 162 */         if (localMethod == null)
/*     */         {
/* 164 */           return;
/*     */         }
/* 166 */         Class[] arrayOfClass = localMethod.getParameterTypes();
/* 167 */         if (arrayOfClass.length != 1)
/*     */         {
/* 169 */           return;
/*     */         }
/* 171 */         Object[] arrayOfObject = { str };
/*     */         try {
/* 173 */           localMethod.invoke(paramComponent, arrayOfObject);
/*     */         } catch (Exception localException) {
/* 175 */           System.err.println("Invocation failed");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.html.ObjectView
 * JD-Core Version:    0.6.2
 */