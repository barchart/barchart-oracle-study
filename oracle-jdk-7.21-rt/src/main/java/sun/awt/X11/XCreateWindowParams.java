/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class XCreateWindowParams extends HashMap
/*    */ {
/*    */   public XCreateWindowParams()
/*    */   {
/*    */   }
/*    */ 
/*    */   public XCreateWindowParams(Object[] paramArrayOfObject)
/*    */   {
/* 36 */     init(paramArrayOfObject);
/*    */   }
/*    */   private void init(Object[] paramArrayOfObject) {
/* 39 */     if (paramArrayOfObject.length % 2 != 0) {
/* 40 */       throw new IllegalArgumentException("Map size should be devisible by two");
/*    */     }
/* 42 */     for (int i = 0; i < paramArrayOfObject.length; i += 2)
/* 43 */       put(paramArrayOfObject[i], paramArrayOfObject[(i + 1)]);
/*    */   }
/*    */ 
/*    */   public XCreateWindowParams putIfNull(Object paramObject1, Object paramObject2)
/*    */   {
/* 48 */     if (!containsKey(paramObject1)) {
/* 49 */       put(paramObject1, paramObject2);
/*    */     }
/* 51 */     return this;
/*    */   }
/*    */   public XCreateWindowParams putIfNull(Object paramObject, int paramInt) {
/* 54 */     if (!containsKey(paramObject)) {
/* 55 */       put(paramObject, Integer.valueOf(paramInt));
/*    */     }
/* 57 */     return this;
/*    */   }
/*    */   public XCreateWindowParams putIfNull(Object paramObject, long paramLong) {
/* 60 */     if (!containsKey(paramObject)) {
/* 61 */       put(paramObject, Long.valueOf(paramLong));
/*    */     }
/* 63 */     return this;
/*    */   }
/*    */ 
/*    */   public XCreateWindowParams add(Object paramObject1, Object paramObject2) {
/* 67 */     put(paramObject1, paramObject2);
/* 68 */     return this;
/*    */   }
/*    */   public XCreateWindowParams add(Object paramObject, int paramInt) {
/* 71 */     put(paramObject, Integer.valueOf(paramInt));
/* 72 */     return this;
/*    */   }
/*    */   public XCreateWindowParams add(Object paramObject, long paramLong) {
/* 75 */     put(paramObject, Long.valueOf(paramLong));
/* 76 */     return this;
/*    */   }
/*    */   public XCreateWindowParams delete(Object paramObject) {
/* 79 */     remove(paramObject);
/* 80 */     return this;
/*    */   }
/*    */   public String toString() {
/* 83 */     StringBuffer localStringBuffer = new StringBuffer();
/* 84 */     Iterator localIterator = entrySet().iterator();
/* 85 */     while (localIterator.hasNext()) {
/* 86 */       Map.Entry localEntry = (Map.Entry)localIterator.next();
/* 87 */       localStringBuffer.append(localEntry.getKey() + ": " + localEntry.getValue() + "\n");
/*    */     }
/* 89 */     return localStringBuffer.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XCreateWindowParams
 * JD-Core Version:    0.6.2
 */