/*    */ package com.sun.corba.se.spi.orbutil.fsm;
/*    */ 
/*    */ class TestInput
/*    */ {
/*    */   Input value;
/*    */   String msg;
/*    */ 
/*    */   TestInput(Input paramInput, String paramString)
/*    */   {
/* 39 */     this.value = paramInput;
/* 40 */     this.msg = paramString;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return "Input " + this.value + " : " + this.msg;
/*    */   }
/*    */ 
/*    */   public Input getInput()
/*    */   {
/* 50 */     return this.value;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.fsm.TestInput
 * JD-Core Version:    0.6.2
 */