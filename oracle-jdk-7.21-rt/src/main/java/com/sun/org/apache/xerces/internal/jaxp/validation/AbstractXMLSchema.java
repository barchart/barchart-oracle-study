/*    */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import javax.xml.validation.Schema;
/*    */ import javax.xml.validation.Validator;
/*    */ import javax.xml.validation.ValidatorHandler;
/*    */ 
/*    */ abstract class AbstractXMLSchema extends Schema
/*    */   implements XSGrammarPoolContainer
/*    */ {
/*    */   private final HashMap fFeatures;
/*    */ 
/*    */   public AbstractXMLSchema()
/*    */   {
/* 45 */     this.fFeatures = new HashMap();
/*    */   }
/*    */ 
/*    */   public final Validator newValidator()
/*    */   {
/* 56 */     return new ValidatorImpl(this);
/*    */   }
/*    */ 
/*    */   public final ValidatorHandler newValidatorHandler()
/*    */   {
/* 63 */     return new ValidatorHandlerImpl(this);
/*    */   }
/*    */ 
/*    */   public final Boolean getFeature(String featureId)
/*    */   {
/* 76 */     return (Boolean)this.fFeatures.get(featureId);
/*    */   }
/*    */ 
/*    */   final void setFeature(String featureId, boolean state)
/*    */   {
/* 84 */     this.fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.AbstractXMLSchema
 * JD-Core Version:    0.6.2
 */