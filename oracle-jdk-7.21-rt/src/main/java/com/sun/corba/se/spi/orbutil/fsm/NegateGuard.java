/*     */ package com.sun.corba.se.spi.orbutil.fsm;
/*     */ 
/*     */ class NegateGuard
/*     */   implements Guard
/*     */ {
/*     */   Guard guard;
/*     */ 
/*     */   public NegateGuard(Guard paramGuard)
/*     */   {
/* 130 */     this.guard = paramGuard;
/*     */   }
/*     */ 
/*     */   public Guard.Result evaluate(FSM paramFSM, Input paramInput)
/*     */   {
/* 135 */     return this.guard.evaluate(paramFSM, paramInput).complement();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.fsm.NegateGuard
 * JD-Core Version:    0.6.2
 */