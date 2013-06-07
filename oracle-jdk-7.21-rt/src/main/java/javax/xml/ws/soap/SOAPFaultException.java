/*    */ package javax.xml.ws.soap;
/*    */ 
/*    */ import javax.xml.soap.SOAPFault;
/*    */ import javax.xml.ws.ProtocolException;
/*    */ 
/*    */ public class SOAPFaultException extends ProtocolException
/*    */ {
/*    */   private SOAPFault fault;
/*    */ 
/*    */   public SOAPFaultException(SOAPFault fault)
/*    */   {
/* 63 */     super(fault.getFaultString());
/* 64 */     this.fault = fault;
/*    */   }
/*    */ 
/*    */   public SOAPFault getFault()
/*    */   {
/* 73 */     return this.fault;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.soap.SOAPFaultException
 * JD-Core Version:    0.6.2
 */