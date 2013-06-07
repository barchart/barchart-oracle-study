/*    */ package com.sun.xml.internal.ws.model;
/*    */ 
/*    */ import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;
/*    */ import com.sun.xml.internal.ws.util.localization.Localizable;
/*    */ 
/*    */ public class RuntimeModelerException extends JAXWSExceptionBase
/*    */ {
/*    */   public RuntimeModelerException(String key, Object[] args)
/*    */   {
/* 41 */     super(key, args);
/*    */   }
/*    */ 
/*    */   public RuntimeModelerException(Throwable throwable) {
/* 45 */     super(throwable);
/*    */   }
/*    */ 
/*    */   public RuntimeModelerException(Localizable arg) {
/* 49 */     super("nestedModelerError", new Object[] { arg });
/*    */   }
/*    */ 
/*    */   public String getDefaultResourceBundleName() {
/* 53 */     return "com.sun.xml.internal.ws.resources.modeler";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.model.RuntimeModelerException
 * JD-Core Version:    0.6.2
 */