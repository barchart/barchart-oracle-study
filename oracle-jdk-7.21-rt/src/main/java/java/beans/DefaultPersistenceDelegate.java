/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.EventListener;
/*     */ import java.util.Objects;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import sun.reflect.misc.MethodUtil;
/*     */ 
/*     */ public class DefaultPersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   private String[] constructor;
/*     */   private Boolean definesEquals;
/*     */ 
/*     */   public DefaultPersistenceDelegate()
/*     */   {
/*  70 */     this(new String[0]);
/*     */   }
/*     */ 
/*     */   public DefaultPersistenceDelegate(String[] paramArrayOfString)
/*     */   {
/*  95 */     this.constructor = paramArrayOfString;
/*     */   }
/*     */ 
/*     */   private static boolean definesEquals(Class paramClass) {
/*     */     try {
/* 100 */       return paramClass == paramClass.getMethod("equals", new Class[] { Object.class }).getDeclaringClass();
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */     }
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean definesEquals(Object paramObject)
/*     */   {
/* 108 */     if (this.definesEquals != null) {
/* 109 */       return this.definesEquals == Boolean.TRUE;
/*     */     }
/*     */ 
/* 112 */     boolean bool = definesEquals(paramObject.getClass());
/* 113 */     this.definesEquals = (bool ? Boolean.TRUE : Boolean.FALSE);
/* 114 */     return bool;
/*     */   }
/*     */ 
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 135 */     return (this.constructor.length == 0) || (!definesEquals(paramObject1)) ? super.mutatesTo(paramObject1, paramObject2) : paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */   {
/* 155 */     int i = this.constructor.length;
/* 156 */     Class localClass = paramObject.getClass();
/* 157 */     Object[] arrayOfObject = new Object[i];
/* 158 */     for (int j = 0; j < i; j++) {
/*     */       try {
/* 160 */         Method localMethod = findMethod(localClass, this.constructor[j]);
/* 161 */         arrayOfObject[j] = MethodUtil.invoke(localMethod, paramObject, new Object[0]);
/*     */       }
/*     */       catch (Exception localException) {
/* 164 */         paramEncoder.getExceptionListener().exceptionThrown(localException);
/*     */       }
/*     */     }
/* 167 */     return new Expression(paramObject, paramObject.getClass(), "new", arrayOfObject);
/*     */   }
/*     */ 
/*     */   private Method findMethod(Class paramClass, String paramString) {
/* 171 */     if (paramString == null) {
/* 172 */       throw new IllegalArgumentException("Property name is null");
/*     */     }
/* 174 */     PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramClass, paramString);
/* 175 */     if (localPropertyDescriptor == null) {
/* 176 */       throw new IllegalStateException("Could not find property by the name " + paramString);
/*     */     }
/* 178 */     Method localMethod = localPropertyDescriptor.getReadMethod();
/* 179 */     if (localMethod == null) {
/* 180 */       throw new IllegalStateException("Could not find getter for the property " + paramString);
/*     */     }
/* 182 */     return localMethod;
/*     */   }
/*     */ 
/*     */   private void doProperty(Class paramClass, PropertyDescriptor paramPropertyDescriptor, Object paramObject1, Object paramObject2, Encoder paramEncoder) throws Exception {
/* 186 */     Method localMethod1 = paramPropertyDescriptor.getReadMethod();
/* 187 */     Method localMethod2 = paramPropertyDescriptor.getWriteMethod();
/*     */ 
/* 189 */     if ((localMethod1 != null) && (localMethod2 != null)) {
/* 190 */       Expression localExpression1 = new Expression(paramObject1, localMethod1.getName(), new Object[0]);
/* 191 */       Expression localExpression2 = new Expression(paramObject2, localMethod1.getName(), new Object[0]);
/* 192 */       Object localObject1 = localExpression1.getValue();
/* 193 */       Object localObject2 = localExpression2.getValue();
/* 194 */       paramEncoder.writeExpression(localExpression1);
/* 195 */       if (!Objects.equals(localObject2, paramEncoder.get(localObject1)))
/*     */       {
/* 197 */         Object[] arrayOfObject1 = (Object[])paramPropertyDescriptor.getValue("enumerationValues");
/* 198 */         if (((arrayOfObject1 instanceof Object[])) && (Array.getLength(arrayOfObject1) % 3 == 0)) {
/* 199 */           Object[] arrayOfObject2 = (Object[])arrayOfObject1;
/* 200 */           for (int i = 0; i < arrayOfObject2.length; i += 3)
/*     */             try {
/* 202 */               Field localField = paramClass.getField((String)arrayOfObject2[i]);
/* 203 */               if (localField.get(null).equals(localObject1)) {
/* 204 */                 paramEncoder.remove(localObject1);
/* 205 */                 paramEncoder.writeExpression(new Expression(localObject1, localField, "get", new Object[] { null }));
/*     */               }
/*     */             }
/*     */             catch (Exception localException) {
/*     */             }
/*     */         }
/* 211 */         invokeStatement(paramObject1, localMethod2.getName(), new Object[] { localObject1 }, paramEncoder);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void invokeStatement(Object paramObject, String paramString, Object[] paramArrayOfObject, Encoder paramEncoder) {
/* 217 */     paramEncoder.writeStatement(new Statement(paramObject, paramString, paramArrayOfObject));
/*     */   }
/*     */ 
/*     */   private void initBean(Class paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/*     */     Object localObject4;
/*     */     Object localObject5;
/*     */     Object localObject6;
/* 222 */     for (Object localObject3 : paramClass.getFields()) {
/* 223 */       int m = localObject3.getModifiers();
/* 224 */       if ((!Modifier.isFinal(m)) && (!Modifier.isStatic(m)) && (!Modifier.isTransient(m)))
/*     */       {
/*     */         try
/*     */         {
/* 228 */           Expression localExpression = new Expression(localObject3, "get", new Object[] { paramObject1 });
/* 229 */           localObject4 = new Expression(localObject3, "get", new Object[] { paramObject2 });
/* 230 */           localObject5 = localExpression.getValue();
/* 231 */           localObject6 = ((Expression)localObject4).getValue();
/* 232 */           paramEncoder.writeExpression(localExpression);
/* 233 */           if (!Objects.equals(localObject6, paramEncoder.get(localObject5)))
/* 234 */             paramEncoder.writeStatement(new Statement(localObject3, "set", new Object[] { paramObject1, localObject5 }));
/*     */         }
/*     */         catch (Exception localException1)
/*     */         {
/* 238 */           paramEncoder.getExceptionListener().exceptionThrown(localException1);
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 243 */       ??? = Introspector.getBeanInfo(paramClass);
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException)
/*     */     {
/*     */       return;
/*     */     }
/*     */     PropertyDescriptor localPropertyDescriptor;
/* 248 */     for (localPropertyDescriptor : ((BeanInfo)???).getPropertyDescriptors()) {
/* 249 */       if (!localPropertyDescriptor.isTransient())
/*     */       {
/*     */         try
/*     */         {
/* 253 */           doProperty(paramClass, localPropertyDescriptor, paramObject1, paramObject2, paramEncoder);
/*     */         }
/*     */         catch (Exception localException2) {
/* 256 */           paramEncoder.getExceptionListener().exceptionThrown(localException2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     if (!Component.class.isAssignableFrom(paramClass)) {
/* 285 */       return;
/*     */     }
/* 287 */     for (localPropertyDescriptor : ((BeanInfo)???).getEventSetDescriptors())
/* 288 */       if (!localPropertyDescriptor.isTransient())
/*     */       {
/* 291 */         Class localClass = localPropertyDescriptor.getListenerType();
/*     */ 
/* 296 */         if (localClass != ComponentListener.class)
/*     */         {
/* 307 */           if ((localClass != ChangeListener.class) || (paramClass != JMenuItem.class))
/*     */           {
/* 312 */             localObject4 = new EventListener[0];
/* 313 */             localObject5 = new EventListener[0];
/*     */             try {
/* 315 */               localObject6 = localPropertyDescriptor.getGetListenerMethod();
/* 316 */               localObject4 = (EventListener[])MethodUtil.invoke((Method)localObject6, paramObject1, new Object[0]);
/* 317 */               localObject5 = (EventListener[])MethodUtil.invoke((Method)localObject6, paramObject2, new Object[0]);
/*     */             }
/*     */             catch (Exception localException3) {
/*     */               try {
/* 321 */                 Method localMethod = paramClass.getMethod("getListeners", new Class[] { Class.class });
/* 322 */                 localObject4 = (EventListener[])MethodUtil.invoke(localMethod, paramObject1, new Object[] { localClass });
/* 323 */                 localObject5 = (EventListener[])MethodUtil.invoke(localMethod, paramObject2, new Object[] { localClass });
/*     */               }
/*     */               catch (Exception localException4) {
/* 326 */                 return;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 332 */             String str1 = localPropertyDescriptor.getAddListenerMethod().getName();
/* 333 */             for (int n = localObject5.length; n < localObject4.length; n++)
/*     */             {
/* 335 */               invokeStatement(paramObject1, str1, new Object[] { localObject4[n] }, paramEncoder);
/*     */             }
/*     */ 
/* 338 */             String str2 = localPropertyDescriptor.getRemoveListenerMethod().getName();
/* 339 */             for (int i1 = localObject4.length; i1 < localObject5.length; i1++)
/* 340 */               invokeStatement(paramObject1, str2, new Object[] { localObject5[i1] }, paramEncoder);
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 398 */     super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
/* 399 */     if (paramObject1.getClass() == paramClass)
/* 400 */       initBean(paramClass, paramObject1, paramObject2, paramEncoder);
/*     */   }
/*     */ 
/*     */   private static PropertyDescriptor getPropertyDescriptor(Class paramClass, String paramString)
/*     */   {
/*     */     try {
/* 406 */       for (PropertyDescriptor localPropertyDescriptor : Introspector.getBeanInfo(paramClass).getPropertyDescriptors())
/* 407 */         if (paramString.equals(localPropertyDescriptor.getName()))
/* 408 */           return localPropertyDescriptor;
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException) {
/*     */     }
/* 412 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.DefaultPersistenceDelegate
 * JD-Core Version:    0.6.2
 */