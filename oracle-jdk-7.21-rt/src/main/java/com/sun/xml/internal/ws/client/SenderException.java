/*    */ package com.sun.xml.internal.ws.client;
/*    */ 
/*    */ import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;
/*    */ import com.sun.xml.internal.ws.util.localization.Localizable;
/*    */ 
/*    */ public class SenderException extends JAXWSExceptionBase
/*    */ {
/*    */   public SenderException(String key, Object[] args)
/*    */   {
/* 37 */     super(key, args);
/*    */   }
/*    */ 
/*    */   public SenderException(Throwable throwable) {
/* 41 */     super(throwable);
/*    */   }
/*    */ 
/*    */   public SenderException(Localizable arg) {
/* 45 */     super("sender.nestedError", new Object[] { arg });
/*    */   }
/*    */ 
/*    */   public String getDefaultResourceBundleName() {
/* 49 */     return "com.sun.xml.internal.ws.resources.sender";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.client.SenderException
 * JD-Core Version:    0.6.2
 */