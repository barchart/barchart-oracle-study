/*     */ package sun.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.UIDefaults.LazyValue;
/*     */ import javax.swing.plaf.ColorUIResource;
/*     */ 
/*     */ public class SwingLazyValue
/*     */   implements UIDefaults.LazyValue
/*     */ {
/*     */   private String className;
/*     */   private String methodName;
/*     */   private Object[] args;
/*     */ 
/*     */   public SwingLazyValue(String paramString)
/*     */   {
/*  48 */     this(paramString, (String)null);
/*     */   }
/*     */   public SwingLazyValue(String paramString1, String paramString2) {
/*  51 */     this(paramString1, paramString2, null);
/*     */   }
/*     */   public SwingLazyValue(String paramString, Object[] paramArrayOfObject) {
/*  54 */     this(paramString, null, paramArrayOfObject);
/*     */   }
/*     */   public SwingLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject) {
/*  57 */     this.className = paramString1;
/*  58 */     this.methodName = paramString2;
/*  59 */     if (paramArrayOfObject != null)
/*  60 */       this.args = ((Object[])paramArrayOfObject.clone());
/*     */   }
/*     */ 
/*     */   public Object createValue(UIDefaults paramUIDefaults)
/*     */   {
/*     */     try
/*     */     {
/*  67 */       Class localClass = Class.forName(this.className, true, null);
/*  68 */       if (this.methodName != null) {
/*  69 */         arrayOfClass = getClassArray(this.args);
/*  70 */         localObject = localClass.getMethod(this.methodName, arrayOfClass);
/*  71 */         makeAccessible((AccessibleObject)localObject);
/*  72 */         return ((Method)localObject).invoke(localClass, this.args);
/*     */       }
/*  74 */       Class[] arrayOfClass = getClassArray(this.args);
/*  75 */       Object localObject = localClass.getConstructor(arrayOfClass);
/*  76 */       makeAccessible((AccessibleObject)localObject);
/*  77 */       return ((Constructor)localObject).newInstance(this.args);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   private void makeAccessible(final AccessibleObject paramAccessibleObject) {
/*  90 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*  92 */         paramAccessibleObject.setAccessible(true);
/*  93 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private Class[] getClassArray(Object[] paramArrayOfObject) {
/*  99 */     Class[] arrayOfClass = null;
/* 100 */     if (paramArrayOfObject != null) {
/* 101 */       arrayOfClass = new Class[paramArrayOfObject.length];
/* 102 */       for (int i = 0; i < paramArrayOfObject.length; i++)
/*     */       {
/* 106 */         if ((paramArrayOfObject[i] instanceof Integer))
/* 107 */           arrayOfClass[i] = Integer.TYPE;
/* 108 */         else if ((paramArrayOfObject[i] instanceof Boolean))
/* 109 */           arrayOfClass[i] = Boolean.TYPE;
/* 110 */         else if ((paramArrayOfObject[i] instanceof ColorUIResource))
/*     */         {
/* 119 */           arrayOfClass[i] = Color.class;
/*     */         }
/* 121 */         else arrayOfClass[i] = paramArrayOfObject[i].getClass();
/*     */       }
/*     */     }
/*     */ 
/* 125 */     return arrayOfClass;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.SwingLazyValue
 * JD-Core Version:    0.6.2
 */