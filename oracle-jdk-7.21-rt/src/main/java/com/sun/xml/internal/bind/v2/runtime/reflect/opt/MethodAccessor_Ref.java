/*    */ package com.sun.xml.internal.bind.v2.runtime.reflect.opt;
/*    */ 
/*    */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*    */ 
/*    */ public class MethodAccessor_Ref extends Accessor
/*    */ {
/*    */   public MethodAccessor_Ref()
/*    */   {
/* 37 */     super(Ref.class);
/*    */   }
/*    */ 
/*    */   public Object get(Object bean) {
/* 41 */     return ((Bean)bean).get_ref();
/*    */   }
/*    */ 
/*    */   public void set(Object bean, Object value) {
/* 45 */     ((Bean)bean).set_ref((Ref)value);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.reflect.opt.MethodAccessor_Ref
 * JD-Core Version:    0.6.2
 */