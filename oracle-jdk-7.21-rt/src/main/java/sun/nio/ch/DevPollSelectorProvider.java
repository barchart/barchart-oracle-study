/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.Channel;
/*    */ import java.nio.channels.spi.AbstractSelector;
/*    */ 
/*    */ public class DevPollSelectorProvider extends SelectorProviderImpl
/*    */ {
/*    */   public AbstractSelector openSelector()
/*    */     throws IOException
/*    */   {
/* 36 */     return new DevPollSelectorImpl(this);
/*    */   }
/*    */ 
/*    */   public Channel inheritedChannel() throws IOException {
/* 40 */     return InheritedChannel.getChannel();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DevPollSelectorProvider
 * JD-Core Version:    0.6.2
 */