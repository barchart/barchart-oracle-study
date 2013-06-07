/*    */ package com.sun.org.apache.xerces.internal.impl.dv.xs;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*    */ 
/*    */ public class UnionDV extends TypeValidator
/*    */ {
/*    */   public short getAllowedFacets()
/*    */   {
/* 38 */     return 2056;
/*    */   }
/*    */ 
/*    */   public Object getActualValue(String content, ValidationContext context)
/*    */     throws InvalidDatatypeValueException
/*    */   {
/* 44 */     return content;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.xs.UnionDV
 * JD-Core Version:    0.6.2
 */