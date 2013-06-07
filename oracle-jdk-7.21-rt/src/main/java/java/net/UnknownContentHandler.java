/*      */ package java.net;
/*      */ 
/*      */ import java.io.IOException;
/*      */ 
/*      */ class UnknownContentHandler extends ContentHandler
/*      */ {
/* 1792 */   static final ContentHandler INSTANCE = new UnknownContentHandler();
/*      */ 
/*      */   public Object getContent(URLConnection paramURLConnection) throws IOException {
/* 1795 */     return paramURLConnection.getInputStream();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.UnknownContentHandler
 * JD-Core Version:    0.6.2
 */