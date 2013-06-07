/*    */ package com.sun.org.apache.xerces.internal.impl.dv.dtd;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
/*    */ 
/*    */ public class ENTITYDatatypeValidator
/*    */   implements DatatypeValidator
/*    */ {
/*    */   public void validate(String content, ValidationContext context)
/*    */     throws InvalidDatatypeValueException
/*    */   {
/* 63 */     if (!context.isEntityUnparsed(content))
/* 64 */       throw new InvalidDatatypeValueException("ENTITYNotUnparsed", new Object[] { content });
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator
 * JD-Core Version:    0.6.2
 */