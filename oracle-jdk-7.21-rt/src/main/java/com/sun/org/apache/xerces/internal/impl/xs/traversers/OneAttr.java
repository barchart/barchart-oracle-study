/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ class OneAttr
/*      */ {
/*      */   public String name;
/*      */   public int dvIndex;
/*      */   public int valueIndex;
/*      */   public Object dfltValue;
/*      */ 
/*      */   public OneAttr(String name, int dvIndex, int valueIndex, Object dfltValue)
/*      */   {
/* 1769 */     this.name = name;
/* 1770 */     this.dvIndex = dvIndex;
/* 1771 */     this.valueIndex = valueIndex;
/* 1772 */     this.dfltValue = dfltValue;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.OneAttr
 * JD-Core Version:    0.6.2
 */