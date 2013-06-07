/*    */ package com.sun.xml.internal.ws.client;
/*    */ 
/*    */ import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;
/*    */ import com.sun.xml.internal.ws.util.localization.Localizable;
/*    */ 
/*    */ public class ClientTransportException extends JAXWSExceptionBase
/*    */ {
/*    */   public ClientTransportException(Localizable msg)
/*    */   {
/* 37 */     super(msg);
/*    */   }
/*    */ 
/*    */   public ClientTransportException(Localizable msg, Throwable cause) {
/* 41 */     super(msg, cause);
/*    */   }
/*    */ 
/*    */   public ClientTransportException(Throwable throwable) {
/* 45 */     super(throwable);
/*    */   }
/*    */ 
/*    */   public String getDefaultResourceBundleName() {
/* 49 */     return "com.sun.xml.internal.ws.resources.client";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.client.ClientTransportException
 * JD-Core Version:    0.6.2
 */