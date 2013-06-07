/*    */ package com.sun.xml.internal.ws.developer;
/*    */ 
/*    */ import java.net.URL;
/*    */ import javax.activation.DataSource;
/*    */ 
/*    */ public abstract class StreamingDataHandler extends com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler
/*    */ {
/*    */   public StreamingDataHandler(Object o, String s)
/*    */   {
/* 59 */     super(o, s);
/*    */   }
/*    */ 
/*    */   public StreamingDataHandler(URL url) {
/* 63 */     super(url);
/*    */   }
/*    */ 
/*    */   public StreamingDataHandler(DataSource dataSource) {
/* 67 */     super(dataSource);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.developer.StreamingDataHandler
 * JD-Core Version:    0.6.2
 */