/*    */ package java.lang;
/*    */ 
/*    */ import sun.misc.Signal;
/*    */ import sun.misc.SignalHandler;
/*    */ 
/*    */ class Terminator
/*    */ {
/* 42 */   private static SignalHandler handler = null;
/*    */ 
/*    */   static void setup()
/*    */   {
/* 49 */     if (handler != null) return;
/* 50 */     SignalHandler local1 = new SignalHandler() {
/*    */       public void handle(Signal paramAnonymousSignal) {
/* 52 */         Shutdown.exit(paramAnonymousSignal.getNumber() + 128);
/*    */       }
/*    */     };
/* 55 */     handler = local1;
/*    */     try {
/* 57 */       Signal.handle(new Signal("HUP"), local1);
/* 58 */       Signal.handle(new Signal("INT"), local1);
/* 59 */       Signal.handle(new Signal("TERM"), local1);
/*    */     }
/*    */     catch (IllegalArgumentException localIllegalArgumentException)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   static void teardown()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Terminator
 * JD-Core Version:    0.6.2
 */