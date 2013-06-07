/*    */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*    */ 
/*    */ class AnyAtomicDV extends TypeValidator
/*    */ {
/*    */   public short getAllowedFacets()
/*    */   {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
/* 41 */     return content;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.AnyAtomicDV
 * JD-Core Version:    0.6.2
 */