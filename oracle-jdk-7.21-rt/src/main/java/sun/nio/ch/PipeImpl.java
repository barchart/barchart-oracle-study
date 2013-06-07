/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.nio.channels.Pipe;
/*    */ import java.nio.channels.Pipe.SinkChannel;
/*    */ import java.nio.channels.Pipe.SourceChannel;
/*    */ import java.nio.channels.spi.SelectorProvider;
/*    */ 
/*    */ class PipeImpl extends Pipe
/*    */ {
/*    */   private final Pipe.SourceChannel source;
/*    */   private final Pipe.SinkChannel sink;
/*    */ 
/*    */   PipeImpl(SelectorProvider paramSelectorProvider)
/*    */   {
/* 42 */     long l = IOUtil.makePipe(true);
/* 43 */     int i = (int)(l >>> 32);
/* 44 */     int j = (int)l;
/* 45 */     FileDescriptor localFileDescriptor1 = new FileDescriptor();
/* 46 */     IOUtil.setfdVal(localFileDescriptor1, i);
/* 47 */     this.source = new SourceChannelImpl(paramSelectorProvider, localFileDescriptor1);
/* 48 */     FileDescriptor localFileDescriptor2 = new FileDescriptor();
/* 49 */     IOUtil.setfdVal(localFileDescriptor2, j);
/* 50 */     this.sink = new SinkChannelImpl(paramSelectorProvider, localFileDescriptor2);
/*    */   }
/*    */ 
/*    */   public Pipe.SourceChannel source() {
/* 54 */     return this.source;
/*    */   }
/*    */ 
/*    */   public Pipe.SinkChannel sink() {
/* 58 */     return this.sink;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.PipeImpl
 * JD-Core Version:    0.6.2
 */