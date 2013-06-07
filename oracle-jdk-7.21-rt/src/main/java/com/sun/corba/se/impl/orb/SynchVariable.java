/*      */ package com.sun.corba.se.impl.orb;
/*      */ 
/*      */ class SynchVariable
/*      */ {
/*      */   public boolean _flag;
/*      */ 
/*      */   SynchVariable()
/*      */   {
/* 2107 */     this._flag = false;
/*      */   }
/*      */ 
/*      */   public void set()
/*      */   {
/* 2113 */     this._flag = true;
/*      */   }
/*      */ 
/*      */   public boolean value()
/*      */   {
/* 2119 */     return this._flag;
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/* 2125 */     this._flag = false;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orb.SynchVariable
 * JD-Core Version:    0.6.2
 */