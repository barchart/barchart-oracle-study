/*    */ package sun.rmi.server;
/*    */ 
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.SoftReference;
/*    */ import java.util.Map;
/*    */ import java.util.WeakHashMap;
/*    */ 
/*    */ public abstract class WeakClassHashMap<V>
/*    */ {
/* 49 */   private Map<Class<?>, ValueCell<V>> internalMap = new WeakHashMap();
/*    */ 
/*    */   public V get(Class<?> paramClass)
/*    */   {
/*    */     ValueCell localValueCell;
/* 62 */     synchronized (this.internalMap) {
/* 63 */       localValueCell = (ValueCell)this.internalMap.get(paramClass);
/* 64 */       if (localValueCell == null) {
/* 65 */         localValueCell = new ValueCell();
/* 66 */         this.internalMap.put(paramClass, localValueCell);
/*    */       }
/*    */     }
/* 69 */     synchronized (localValueCell) {
/* 70 */       Object localObject2 = null;
/* 71 */       if (localValueCell.ref != null) {
/* 72 */         localObject2 = localValueCell.ref.get();
/*    */       }
/* 74 */       if (localObject2 == null) {
/* 75 */         localObject2 = computeValue(paramClass);
/* 76 */         localValueCell.ref = new SoftReference(localObject2);
/*    */       }
/* 78 */       return localObject2;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected abstract V computeValue(Class<?> paramClass);
/*    */ 
/*    */   private static class ValueCell<T> {
/* 85 */     Reference<T> ref = null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.WeakClassHashMap
 * JD-Core Version:    0.6.2
 */