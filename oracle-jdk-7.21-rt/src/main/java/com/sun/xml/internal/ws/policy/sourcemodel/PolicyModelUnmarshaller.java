/*    */ package com.sun.xml.internal.ws.policy.sourcemodel;
/*    */ 
/*    */ import com.sun.xml.internal.ws.policy.PolicyException;
/*    */ 
/*    */ public abstract class PolicyModelUnmarshaller
/*    */ {
/* 41 */   private static final PolicyModelUnmarshaller xmlUnmarshaller = new XmlPolicyModelUnmarshaller();
/*    */ 
/*    */   public abstract PolicySourceModel unmarshalModel(Object paramObject)
/*    */     throws PolicyException;
/*    */ 
/*    */   public static PolicyModelUnmarshaller getXmlUnmarshaller()
/*    */   {
/* 73 */     return xmlUnmarshaller;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller
 * JD-Core Version:    0.6.2
 */