/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XColor extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 12; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XColor(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XColor()
/*    */   {
/* 27 */     log.finest("Creating");
/* 28 */     this.pData = this.unsafe.allocateMemory(getSize());
/* 29 */     this.should_free_memory = true;
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 34 */     log.finest("Disposing");
/* 35 */     if (this.should_free_memory) {
/* 36 */       log.finest("freeing memory");
/* 37 */       this.unsafe.freeMemory(this.pData);
/*    */     }
/*    */   }
/* 40 */   public long get_pixel() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 41 */   public void set_pixel(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 42 */   public short get_red() { log.finest(""); return Native.getShort(this.pData + 4L); } 
/* 43 */   public void set_red(short paramShort) { log.finest(""); Native.putShort(this.pData + 4L, paramShort); } 
/* 44 */   public short get_green() { log.finest(""); return Native.getShort(this.pData + 6L); } 
/* 45 */   public void set_green(short paramShort) { log.finest(""); Native.putShort(this.pData + 6L, paramShort); } 
/* 46 */   public short get_blue() { log.finest(""); return Native.getShort(this.pData + 8L); } 
/* 47 */   public void set_blue(short paramShort) { log.finest(""); Native.putShort(this.pData + 8L, paramShort); } 
/* 48 */   public byte get_flags() { log.finest(""); return Native.getByte(this.pData + 10L); } 
/* 49 */   public void set_flags(byte paramByte) { log.finest(""); Native.putByte(this.pData + 10L, paramByte); } 
/* 50 */   public byte get_pad() { log.finest(""); return Native.getByte(this.pData + 11L); } 
/* 51 */   public void set_pad(byte paramByte) { log.finest(""); Native.putByte(this.pData + 11L, paramByte); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 55 */     return "XColor";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 60 */     StringBuilder localStringBuilder = new StringBuilder(240);
/*    */ 
/* 62 */     localStringBuilder.append("pixel = ").append(get_pixel()).append(", ");
/* 63 */     localStringBuilder.append("red = ").append(get_red()).append(", ");
/* 64 */     localStringBuilder.append("green = ").append(get_green()).append(", ");
/* 65 */     localStringBuilder.append("blue = ").append(get_blue()).append(", ");
/* 66 */     localStringBuilder.append("flags = ").append(get_flags()).append(", ");
/* 67 */     localStringBuilder.append("pad = ").append(get_pad()).append(", ");
/* 68 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XColor
 * JD-Core Version:    0.6.2
 */