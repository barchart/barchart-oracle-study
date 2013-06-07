/*    */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*    */ 
/*    */ public class AnySimpleDV extends TypeValidator
/*    */ {
/*    */   public short getAllowedFacets()
/*    */   {
/* 39 */     return 0;
/*    */   }
/*    */ 
/*    */   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
/* 43 */     return content;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.AnySimpleDV
 * JD-Core Version:    0.6.2
 */