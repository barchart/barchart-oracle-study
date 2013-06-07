/*    */ package com.sun.xml.internal.messaging.saaj.client.p2p;
/*    */ 
/*    */ import javax.xml.soap.SOAPConnection;
/*    */ import javax.xml.soap.SOAPConnectionFactory;
/*    */ import javax.xml.soap.SOAPException;
/*    */ 
/*    */ public class HttpSOAPConnectionFactory extends SOAPConnectionFactory
/*    */ {
/*    */   public SOAPConnection createConnection()
/*    */     throws SOAPException
/*    */   {
/* 40 */     return new HttpSOAPConnection();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory
 * JD-Core Version:    0.6.2
 */