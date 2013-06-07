/*    */ package com.sun.imageio.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.imageio.stream.ImageInputStream;
/*    */ 
/*    */ public class StreamFinalizer
/*    */ {
/*    */   private ImageInputStream stream;
/*    */ 
/*    */   public StreamFinalizer(ImageInputStream paramImageInputStream)
/*    */   {
/* 60 */     this.stream = paramImageInputStream;
/*    */   }
/*    */ 
/*    */   protected void finalize() throws Throwable {
/*    */     try {
/* 65 */       this.stream.close();
/*    */     } catch (IOException localIOException) {
/*    */     } finally {
/* 68 */       this.stream = null;
/* 69 */       super.finalize();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.imageio.stream.StreamFinalizer
 * JD-Core Version:    0.6.2
 */