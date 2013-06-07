/*    */ package com.sun.xml.internal.messaging.saaj.soap.dynamic;
/*    */ 
/*    */ import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
/*    */ import javax.xml.soap.SOAPException;
/*    */ import javax.xml.soap.SOAPMessage;
/*    */ 
/*    */ public class SOAPMessageFactoryDynamicImpl extends MessageFactoryImpl
/*    */ {
/*    */   public SOAPMessage createMessage()
/*    */     throws SOAPException
/*    */   {
/* 42 */     throw new UnsupportedOperationException("createMessage() not supported for Dynamic Protocol");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.soap.dynamic.SOAPMessageFactoryDynamicImpl
 * JD-Core Version:    0.6.2
 */