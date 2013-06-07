/*    */ package com.sun.xml.internal.ws.api.fastinfoset;
/*    */ 
/*    */ import com.sun.org.glassfish.gmbal.ManagedAttribute;
/*    */ import com.sun.org.glassfish.gmbal.ManagedData;
/*    */ import com.sun.xml.internal.ws.api.FeatureConstructor;
/*    */ import javax.xml.ws.WebServiceFeature;
/*    */ 
/*    */ @ManagedData
/*    */ public class FastInfosetFeature extends WebServiceFeature
/*    */ {
/*    */   public static final String ID = "http://java.sun.com/xml/ns/jaxws/fastinfoset";
/*    */ 
/*    */   public FastInfosetFeature()
/*    */   {
/* 62 */     this.enabled = true;
/*    */   }
/*    */ 
/*    */   @FeatureConstructor({"enabled"})
/*    */   public FastInfosetFeature(boolean enabled)
/*    */   {
/* 73 */     this.enabled = enabled;
/*    */   }
/*    */ 
/*    */   @ManagedAttribute
/*    */   public String getID()
/*    */   {
/* 81 */     return "http://java.sun.com/xml/ns/jaxws/fastinfoset";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature
 * JD-Core Version:    0.6.2
 */