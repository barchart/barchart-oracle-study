/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ class SmallContainer extends Container
/*      */ {
/*      */   String[] keys;
/*      */ 
/*      */   SmallContainer(int size)
/*      */   {
/* 1794 */     this.keys = new String[size];
/* 1795 */     this.values = new OneAttr[size];
/*      */   }
/*      */   void put(String key, OneAttr value) {
/* 1798 */     this.keys[this.pos] = key;
/* 1799 */     this.values[(this.pos++)] = value;
/*      */   }
/*      */   OneAttr get(String key) {
/* 1802 */     for (int i = 0; i < this.pos; i++) {
/* 1803 */       if (this.keys[i].equals(key)) {
/* 1804 */         return this.values[i];
/*      */       }
/*      */     }
/* 1807 */     return null;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.SmallContainer
 * JD-Core Version:    0.6.2
 */