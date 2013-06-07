/*     */ package javax.management.openmbean;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
/*     */ import com.sun.jmx.mbeanserver.MXBeanLookup;
/*     */ import com.sun.jmx.mbeanserver.MXBeanMapping;
/*     */ import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ public class CompositeDataInvocationHandler
/*     */   implements InvocationHandler
/*     */ {
/*     */   private final CompositeData compositeData;
/*     */   private final MXBeanLookup lookup;
/*     */ 
/*     */   public CompositeDataInvocationHandler(CompositeData paramCompositeData)
/*     */   {
/* 120 */     this(paramCompositeData, null);
/*     */   }
/*     */ 
/*     */   CompositeDataInvocationHandler(CompositeData paramCompositeData, MXBeanLookup paramMXBeanLookup)
/*     */   {
/* 140 */     if (paramCompositeData == null)
/* 141 */       throw new IllegalArgumentException("compositeData");
/* 142 */     this.compositeData = paramCompositeData;
/* 143 */     this.lookup = paramMXBeanLookup;
/*     */   }
/*     */ 
/*     */   public CompositeData getCompositeData()
/*     */   {
/* 153 */     assert (this.compositeData != null);
/* 154 */     return this.compositeData;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable
/*     */   {
/* 159 */     String str1 = paramMethod.getName();
/*     */ 
/* 162 */     if (paramMethod.getDeclaringClass() == Object.class) {
/* 163 */       if ((str1.equals("toString")) && (paramArrayOfObject == null))
/* 164 */         return "Proxy[" + this.compositeData + "]";
/* 165 */       if ((str1.equals("hashCode")) && (paramArrayOfObject == null))
/* 166 */         return Integer.valueOf(this.compositeData.hashCode() + 1128548680);
/* 167 */       if ((str1.equals("equals")) && (paramArrayOfObject.length == 1) && (paramMethod.getParameterTypes()[0] == Object.class))
/*     */       {
/* 169 */         return Boolean.valueOf(equals(paramObject, paramArrayOfObject[0]));
/*     */       }
/*     */ 
/* 177 */       return paramMethod.invoke(this, paramArrayOfObject);
/*     */     }
/*     */ 
/* 181 */     String str2 = DefaultMXBeanMappingFactory.propertyName(paramMethod);
/* 182 */     if (str2 == null)
/* 183 */       throw new IllegalArgumentException("Method is not getter: " + paramMethod.getName());
/*     */     Object localObject1;
/* 187 */     if (this.compositeData.containsKey(str2)) {
/* 188 */       localObject1 = this.compositeData.get(str2);
/*     */     } else {
/* 190 */       localObject2 = DefaultMXBeanMappingFactory.decapitalize(str2);
/* 191 */       if (this.compositeData.containsKey((String)localObject2)) {
/* 192 */         localObject1 = this.compositeData.get((String)localObject2);
/*     */       } else {
/* 194 */         String str3 = "No CompositeData item " + str2 + (((String)localObject2).equals(str2) ? "" : new StringBuilder().append(" or ").append((String)localObject2).toString()) + " to match " + str1;
/*     */ 
/* 198 */         throw new IllegalArgumentException(str3);
/*     */       }
/*     */     }
/* 201 */     Object localObject2 = MXBeanMappingFactory.DEFAULT.mappingForType(paramMethod.getGenericReturnType(), MXBeanMappingFactory.DEFAULT);
/*     */ 
/* 204 */     return ((MXBeanMapping)localObject2).fromOpenValue(localObject1);
/*     */   }
/*     */ 
/*     */   private boolean equals(Object paramObject1, Object paramObject2)
/*     */   {
/* 231 */     if (paramObject2 == null) {
/* 232 */       return false;
/*     */     }
/* 234 */     Class localClass1 = paramObject1.getClass();
/* 235 */     Class localClass2 = paramObject2.getClass();
/* 236 */     if (localClass1 != localClass2)
/* 237 */       return false;
/* 238 */     InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramObject2);
/* 239 */     if (!(localInvocationHandler instanceof CompositeDataInvocationHandler))
/* 240 */       return false;
/* 241 */     CompositeDataInvocationHandler localCompositeDataInvocationHandler = (CompositeDataInvocationHandler)localInvocationHandler;
/*     */ 
/* 243 */     return this.compositeData.equals(localCompositeDataInvocationHandler.compositeData);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.openmbean.CompositeDataInvocationHandler
 * JD-Core Version:    0.6.2
 */