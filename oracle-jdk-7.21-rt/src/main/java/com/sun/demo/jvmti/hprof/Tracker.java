/*    */ package com.sun.demo.jvmti.hprof;
/*    */ 
/*    */ public class Tracker
/*    */ {
/* 44 */   private static int engaged = 0;
/*    */ 
/*    */   private static native void nativeObjectInit(Object paramObject1, Object paramObject2);
/*    */ 
/*    */   public static void ObjectInit(Object paramObject)
/*    */   {
/* 56 */     if (engaged != 0)
/* 57 */       nativeObjectInit(Thread.currentThread(), paramObject);
/*    */   }
/*    */ 
/*    */   private static native void nativeNewArray(Object paramObject1, Object paramObject2);
/*    */ 
/*    */   public static void NewArray(Object paramObject)
/*    */   {
/* 69 */     if (engaged != 0)
/* 70 */       nativeNewArray(Thread.currentThread(), paramObject);
/*    */   }
/*    */ 
/*    */   private static native void nativeCallSite(Object paramObject, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static void CallSite(int paramInt1, int paramInt2)
/*    */   {
/* 84 */     if (engaged != 0)
/* 85 */       nativeCallSite(Thread.currentThread(), paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   private static native void nativeReturnSite(Object paramObject, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static void ReturnSite(int paramInt1, int paramInt2)
/*    */   {
/* 97 */     if (engaged != 0)
/* 98 */       nativeReturnSite(Thread.currentThread(), paramInt1, paramInt2);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.demo.jvmti.hprof.Tracker
 * JD-Core Version:    0.6.2
 */