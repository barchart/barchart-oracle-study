/*    */ package com.sun.xml.internal.ws.developer;
/*    */ 
/*    */ import com.sun.xml.internal.ws.api.message.Packet;
/*    */ import org.xml.sax.ErrorHandler;
/*    */ 
/*    */ public abstract class ValidationErrorHandler
/*    */   implements ErrorHandler
/*    */ {
/*    */   protected Packet packet;
/*    */ 
/*    */   public void setPacket(Packet packet)
/*    */   {
/* 56 */     this.packet = packet;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.developer.ValidationErrorHandler
 * JD-Core Version:    0.6.2
 */