/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ abstract class Container
/*      */ {
/*      */   static final int THRESHOLD = 5;
/*      */   OneAttr[] values;
/* 1788 */   int pos = 0;
/*      */ 
/*      */   static Container getContainer(int size)
/*      */   {
/* 1779 */     if (size > 5) {
/* 1780 */       return new LargeContainer(size);
/*      */     }
/* 1782 */     return new SmallContainer(size);
/*      */   }
/*      */ 
/*      */   abstract void put(String paramString, OneAttr paramOneAttr);
/*      */ 
/*      */   abstract OneAttr get(String paramString);
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.Container
 * JD-Core Version:    0.6.2
 */