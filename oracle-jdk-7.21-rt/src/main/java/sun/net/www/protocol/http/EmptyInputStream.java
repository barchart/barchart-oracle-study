/*      */ package sun.net.www.protocol.http;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ 
/*      */ class EmptyInputStream extends InputStream
/*      */ {
/*      */   public int available()
/*      */   {
/* 3378 */     return 0;
/*      */   }
/*      */ 
/*      */   public int read() {
/* 3382 */     return -1;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.EmptyInputStream
 * JD-Core Version:    0.6.2
 */