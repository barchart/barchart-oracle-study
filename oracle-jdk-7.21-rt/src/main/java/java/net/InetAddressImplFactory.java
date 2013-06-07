/*      */ package java.net;
/*      */ 
/*      */ class InetAddressImplFactory
/*      */ {
/*      */   static InetAddressImpl create()
/*      */   {
/* 1607 */     return InetAddress.loadImpl(isIPv6Supported() ? "Inet6AddressImpl" : "Inet4AddressImpl");
/*      */   }
/*      */ 
/*      */   static native boolean isIPv6Supported();
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.InetAddressImplFactory
 * JD-Core Version:    0.6.2
 */