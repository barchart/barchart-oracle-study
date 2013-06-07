/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MethodDescriptor extends FeatureDescriptor
/*     */ {
/*     */   private Reference<Method> methodRef;
/*     */   private String[] paramNames;
/*     */   private List params;
/*     */   private ParameterDescriptor[] parameterDescriptors;
/*     */ 
/*     */   public MethodDescriptor(Method paramMethod)
/*     */   {
/*  56 */     this(paramMethod, null);
/*     */   }
/*     */ 
/*     */   public MethodDescriptor(Method paramMethod, ParameterDescriptor[] paramArrayOfParameterDescriptor)
/*     */   {
/*  71 */     setName(paramMethod.getName());
/*  72 */     setMethod(paramMethod);
/*  73 */     this.parameterDescriptors = paramArrayOfParameterDescriptor;
/*     */   }
/*     */ 
/*     */   public synchronized Method getMethod()
/*     */   {
/*  82 */     Method localMethod = getMethod0();
/*  83 */     if (localMethod == null) {
/*  84 */       Class localClass = getClass0();
/*  85 */       String str = getName();
/*  86 */       if ((localClass != null) && (str != null)) {
/*  87 */         Class[] arrayOfClass = getParams();
/*  88 */         if (arrayOfClass == null) {
/*  89 */           for (int i = 0; i < 3; i++)
/*     */           {
/*  93 */             localMethod = Introspector.findMethod(localClass, str, i, null);
/*  94 */             if (localMethod != null)
/*     */               break;
/*     */           }
/*     */         }
/*     */         else {
/*  99 */           localMethod = Introspector.findMethod(localClass, str, arrayOfClass.length, arrayOfClass);
/*     */         }
/* 101 */         setMethod(localMethod);
/*     */       }
/*     */     }
/* 104 */     return localMethod;
/*     */   }
/*     */ 
/*     */   private synchronized void setMethod(Method paramMethod) {
/* 108 */     if (paramMethod == null) {
/* 109 */       return;
/*     */     }
/* 111 */     if (getClass0() == null) {
/* 112 */       setClass0(paramMethod.getDeclaringClass());
/*     */     }
/* 114 */     setParams(getParameterTypes(getClass0(), paramMethod));
/* 115 */     this.methodRef = getSoftReference(paramMethod);
/*     */   }
/*     */ 
/*     */   private Method getMethod0() {
/* 119 */     return this.methodRef != null ? (Method)this.methodRef.get() : null;
/*     */   }
/*     */ 
/*     */   private synchronized void setParams(Class[] paramArrayOfClass)
/*     */   {
/* 125 */     if (paramArrayOfClass == null) {
/* 126 */       return;
/*     */     }
/* 128 */     this.paramNames = new String[paramArrayOfClass.length];
/* 129 */     this.params = new ArrayList(paramArrayOfClass.length);
/* 130 */     for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 131 */       this.paramNames[i] = paramArrayOfClass[i].getName();
/* 132 */       this.params.add(new WeakReference(paramArrayOfClass[i]));
/*     */     }
/*     */   }
/*     */ 
/*     */   String[] getParamNames()
/*     */   {
/* 138 */     return this.paramNames;
/*     */   }
/*     */ 
/*     */   private synchronized Class[] getParams() {
/* 142 */     Class[] arrayOfClass = new Class[this.params.size()];
/*     */ 
/* 144 */     for (int i = 0; i < this.params.size(); i++) {
/* 145 */       Reference localReference = (Reference)this.params.get(i);
/* 146 */       Class localClass = (Class)localReference.get();
/* 147 */       if (localClass == null) {
/* 148 */         return null;
/*     */       }
/* 150 */       arrayOfClass[i] = localClass;
/*     */     }
/*     */ 
/* 153 */     return arrayOfClass;
/*     */   }
/*     */ 
/*     */   public ParameterDescriptor[] getParameterDescriptors()
/*     */   {
/* 164 */     return this.parameterDescriptors;
/*     */   }
/*     */ 
/*     */   MethodDescriptor(MethodDescriptor paramMethodDescriptor1, MethodDescriptor paramMethodDescriptor2)
/*     */   {
/* 176 */     super(paramMethodDescriptor1, paramMethodDescriptor2);
/*     */ 
/* 178 */     this.methodRef = paramMethodDescriptor1.methodRef;
/* 179 */     if (paramMethodDescriptor2.methodRef != null) {
/* 180 */       this.methodRef = paramMethodDescriptor2.methodRef;
/*     */     }
/* 182 */     this.params = paramMethodDescriptor1.params;
/* 183 */     if (paramMethodDescriptor2.params != null) {
/* 184 */       this.params = paramMethodDescriptor2.params;
/*     */     }
/* 186 */     this.paramNames = paramMethodDescriptor1.paramNames;
/* 187 */     if (paramMethodDescriptor2.paramNames != null) {
/* 188 */       this.paramNames = paramMethodDescriptor2.paramNames;
/*     */     }
/*     */ 
/* 191 */     this.parameterDescriptors = paramMethodDescriptor1.parameterDescriptors;
/* 192 */     if (paramMethodDescriptor2.parameterDescriptors != null)
/* 193 */       this.parameterDescriptors = paramMethodDescriptor2.parameterDescriptors;
/*     */   }
/*     */ 
/*     */   MethodDescriptor(MethodDescriptor paramMethodDescriptor)
/*     */   {
/* 202 */     super(paramMethodDescriptor);
/*     */ 
/* 204 */     this.methodRef = paramMethodDescriptor.methodRef;
/* 205 */     this.params = paramMethodDescriptor.params;
/* 206 */     this.paramNames = paramMethodDescriptor.paramNames;
/*     */ 
/* 208 */     if (paramMethodDescriptor.parameterDescriptors != null) {
/* 209 */       int i = paramMethodDescriptor.parameterDescriptors.length;
/* 210 */       this.parameterDescriptors = new ParameterDescriptor[i];
/* 211 */       for (int j = 0; j < i; j++)
/* 212 */         this.parameterDescriptors[j] = new ParameterDescriptor(paramMethodDescriptor.parameterDescriptors[j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   void appendTo(StringBuilder paramStringBuilder)
/*     */   {
/* 218 */     appendTo(paramStringBuilder, "method", this.methodRef);
/* 219 */     if (this.parameterDescriptors != null) {
/* 220 */       paramStringBuilder.append("; parameterDescriptors={");
/* 221 */       for (ParameterDescriptor localParameterDescriptor : this.parameterDescriptors) {
/* 222 */         paramStringBuilder.append(localParameterDescriptor).append(", ");
/*     */       }
/* 224 */       paramStringBuilder.setLength(paramStringBuilder.length() - 2);
/* 225 */       paramStringBuilder.append("}");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.MethodDescriptor
 * JD-Core Version:    0.6.2
 */