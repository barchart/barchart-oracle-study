/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XRenderPictFormat extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 32; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XRenderPictFormat(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XRenderPictFormat()
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
/* 40 */   public long get_id() { log.finest(""); return Native.getLong(this.pData + 0L); } 
/* 41 */   public void set_id(long paramLong) { log.finest(""); Native.putLong(this.pData + 0L, paramLong); } 
/* 42 */   public int get_type() { log.finest(""); return Native.getInt(this.pData + 4L); } 
/* 43 */   public void set_type(int paramInt) { log.finest(""); Native.putInt(this.pData + 4L, paramInt); } 
/* 44 */   public int get_depth() { log.finest(""); return Native.getInt(this.pData + 8L); } 
/* 45 */   public void set_depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 8L, paramInt); } 
/* 46 */   public XRenderDirectFormat get_direct() { log.finest(""); return new XRenderDirectFormat(this.pData + 12L); } 
/* 47 */   public long get_colormap() { log.finest(""); return Native.getLong(this.pData + 28L); } 
/* 48 */   public void set_colormap(long paramLong) { log.finest(""); Native.putLong(this.pData + 28L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 52 */     return "XRenderPictFormat";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 57 */     StringBuilder localStringBuilder = new StringBuilder(200);
/*    */ 
/* 59 */     localStringBuilder.append("id = ").append(get_id()).append(", ");
/* 60 */     localStringBuilder.append("type = ").append(XlibWrapper.eventToString[get_type()]).append(", ");
/* 61 */     localStringBuilder.append("depth = ").append(get_depth()).append(", ");
/* 62 */     localStringBuilder.append("direct = ").append(get_direct()).append(", ");
/* 63 */     localStringBuilder.append("colormap = ").append(get_colormap()).append(", ");
/* 64 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XRenderPictFormat
 * JD-Core Version:    0.6.2
 */