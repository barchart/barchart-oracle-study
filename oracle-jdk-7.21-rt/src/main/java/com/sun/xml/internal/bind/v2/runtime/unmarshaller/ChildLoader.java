/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ public final class ChildLoader
/*    */ {
/*    */   public final Loader loader;
/*    */   public final Receiver receiver;
/*    */ 
/*    */   public ChildLoader(Loader loader, Receiver receiver)
/*    */   {
/* 40 */     assert (loader != null);
/* 41 */     this.loader = loader;
/* 42 */     this.receiver = receiver;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader
 * JD-Core Version:    0.6.2
 */