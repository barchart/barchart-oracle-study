/*    */ package com.sun.xml.internal.ws.api.policy;
/*    */ 
/*    */ import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
/*    */ import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
/*    */ import com.sun.xml.internal.ws.policy.PolicyException;
/*    */ 
/*    */ public class AlternativeSelector extends EffectiveAlternativeSelector
/*    */ {
/*    */   public static void doSelection(EffectivePolicyModifier modifier)
/*    */     throws PolicyException
/*    */   {
/* 39 */     ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
/* 40 */     selectAlternatives(modifier, validationProcessor);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.policy.AlternativeSelector
 * JD-Core Version:    0.6.2
 */