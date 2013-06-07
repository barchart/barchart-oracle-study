/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.Channel;
/*    */ import java.nio.channels.spi.AbstractSelector;
/*    */ 
/*    */ public class PollSelectorProvider extends SelectorProviderImpl
/*    */ {
/*    */   public AbstractSelector openSelector()
/*    */     throws IOException
/*    */   {
/* 36 */     return new PollSelectorImpl(this);
/*    */   }
/*    */ 
/*    */   public Channel inheritedChannel() throws IOException {
/* 40 */     return InheritedChannel.getChannel();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.PollSelectorProvider
 * JD-Core Version:    0.6.2
 */