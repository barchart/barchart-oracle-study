/*    */ package sun.rmi.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import sun.rmi.transport.LiveRef;
/*    */ 
/*    */ public class UnicastRef2 extends UnicastRef
/*    */ {
/*    */   public UnicastRef2()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnicastRef2(LiveRef paramLiveRef)
/*    */   {
/* 50 */     super(paramLiveRef);
/*    */   }
/*    */ 
/*    */   public String getRefClass(ObjectOutput paramObjectOutput)
/*    */   {
/* 58 */     return "UnicastRef2";
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput paramObjectOutput)
/*    */     throws IOException
/*    */   {
/* 66 */     this.ref.write(paramObjectOutput, true);
/*    */   }
/*    */ 
/*    */   public void readExternal(ObjectInput paramObjectInput)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 77 */     this.ref = LiveRef.read(paramObjectInput, true);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.UnicastRef2
 * JD-Core Version:    0.6.2
 */