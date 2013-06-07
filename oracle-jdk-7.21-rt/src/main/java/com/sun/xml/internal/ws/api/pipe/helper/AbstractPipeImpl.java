/*    */ package com.sun.xml.internal.ws.api.pipe.helper;
/*    */ 
/*    */ import com.sun.xml.internal.ws.api.pipe.Pipe;
/*    */ import com.sun.xml.internal.ws.api.pipe.PipeCloner;
/*    */ 
/*    */ public abstract class AbstractPipeImpl
/*    */   implements Pipe
/*    */ {
/*    */   protected AbstractPipeImpl()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected AbstractPipeImpl(Pipe that, PipeCloner cloner)
/*    */   {
/* 57 */     cloner.add(that, this);
/*    */   }
/*    */ 
/*    */   public void preDestroy()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.pipe.helper.AbstractPipeImpl
 * JD-Core Version:    0.6.2
 */