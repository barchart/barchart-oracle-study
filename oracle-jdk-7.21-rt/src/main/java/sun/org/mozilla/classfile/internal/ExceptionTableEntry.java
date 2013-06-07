/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ final class ExceptionTableEntry
/*      */ {
/*      */   int itsStartLabel;
/*      */   int itsEndLabel;
/*      */   int itsHandlerLabel;
/*      */   short itsCatchType;
/*      */ 
/*      */   ExceptionTableEntry(int paramInt1, int paramInt2, int paramInt3, short paramShort)
/*      */   {
/* 4271 */     this.itsStartLabel = paramInt1;
/* 4272 */     this.itsEndLabel = paramInt2;
/* 4273 */     this.itsHandlerLabel = paramInt3;
/* 4274 */     this.itsCatchType = paramShort;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.ExceptionTableEntry
 * JD-Core Version:    0.6.2
 */