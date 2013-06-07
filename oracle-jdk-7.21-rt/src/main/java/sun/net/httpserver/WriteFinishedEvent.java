/*    */ package sun.net.httpserver;
/*    */ 
/*    */ class WriteFinishedEvent extends Event
/*    */ {
/*    */   WriteFinishedEvent(ExchangeImpl paramExchangeImpl)
/*    */   {
/* 42 */     super(paramExchangeImpl);
/* 43 */     assert (!paramExchangeImpl.writefinished);
/* 44 */     paramExchangeImpl.writefinished = true;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.httpserver.WriteFinishedEvent
 * JD-Core Version:    0.6.2
 */