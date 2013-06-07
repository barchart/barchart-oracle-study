/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ 
/*      */ class LargeContainer extends Container
/*      */ {
/*      */   Map items;
/*      */ 
/*      */   LargeContainer(int size)
/*      */   {
/* 1814 */     this.items = new HashMap(size * 2 + 1);
/* 1815 */     this.values = new OneAttr[size];
/*      */   }
/*      */   void put(String key, OneAttr value) {
/* 1818 */     this.items.put(key, value);
/* 1819 */     this.values[(this.pos++)] = value;
/*      */   }
/*      */   OneAttr get(String key) {
/* 1822 */     OneAttr ret = (OneAttr)this.items.get(key);
/* 1823 */     return ret;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.LargeContainer
 * JD-Core Version:    0.6.2
 */