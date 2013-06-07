/*    */ package com.sun.xml.internal.bind.v2.runtime.reflect.opt;
/*    */ 
/*    */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*    */ 
/*    */ public class MethodAccessor_Double extends Accessor
/*    */ {
/*    */   public MethodAccessor_Double()
/*    */   {
/* 40 */     super(Double.class);
/*    */   }
/*    */ 
/*    */   public Object get(Object bean) {
/* 44 */     return Double.valueOf(((Bean)bean).get_double());
/*    */   }
/*    */ 
/*    */   public void set(Object bean, Object value) {
/* 48 */     ((Bean)bean).set_double(value == null ? Const.default_value_double : ((Double)value).doubleValue());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.reflect.opt.MethodAccessor_Double
 * JD-Core Version:    0.6.2
 */