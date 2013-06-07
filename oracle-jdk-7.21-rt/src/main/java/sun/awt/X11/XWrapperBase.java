/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ abstract class XWrapperBase
/*    */ {
/* 32 */   static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.wrappers");
/*    */ 
/*    */   public String toString() {
/* 35 */     String str = "";
/*    */ 
/* 37 */     str = str + getName() + " = " + getFieldsAsString();
/*    */ 
/* 39 */     return str;
/*    */   }
/*    */ 
/*    */   String getFieldsAsString() {
/* 43 */     return "";
/*    */   }
/*    */ 
/*    */   String getName() {
/* 47 */     return "XWrapperBase";
/*    */   }
/*    */   public void zero() {
/* 50 */     log.finest("Cleaning memory");
/* 51 */     if (getPData() != 0L)
/* 52 */       XlibWrapper.unsafe.setMemory(getPData(), getDataSize(), (byte)0); 
/*    */   }
/*    */ 
/*    */   public abstract int getDataSize();
/*    */ 
/* 57 */   String getWindow(long paramLong) { XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong);
/* 58 */     if (localXBaseWindow == null) {
/* 59 */       return Long.toHexString(paramLong);
/*    */     }
/* 61 */     return localXBaseWindow.toString(); }
/*    */ 
/*    */   public abstract long getPData();
/*    */ 
/*    */   public XEvent clone() {
/* 66 */     long l = XlibWrapper.unsafe.allocateMemory(getDataSize());
/* 67 */     XlibWrapper.unsafe.copyMemory(getPData(), l, getDataSize());
/* 68 */     return new XEvent(l);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWrapperBase
 * JD-Core Version:    0.6.2
 */