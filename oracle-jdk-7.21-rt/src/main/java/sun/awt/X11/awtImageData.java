/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class awtImageData extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 304; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public awtImageData(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public awtImageData()
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
/* 40 */   public int get_Depth() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/* 41 */   public void set_Depth(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/* 42 */   public XPixmapFormatValues get_wsImageFormat() { log.finest(""); return new XPixmapFormatValues(this.pData + 4L); } 
/* 43 */   public long get_clrdata(int paramInt) { log.finest(""); return Native.getLong(this.pData + 16L) + paramInt * Native.getLongSize(); } 
/* 44 */   public long get_clrdata() { log.finest(""); return Native.getLong(this.pData + 16L); } 
/* 45 */   public void set_clrdata(long paramLong) { log.finest(""); Native.putLong(this.pData + 16L, paramLong); } 
/* 46 */   public long get_convert(int paramInt) { log.finest(""); return Native.getLong(this.pData + 48L + paramInt * Native.getLongSize()); } 
/* 47 */   public void set_convert(int paramInt, long paramLong) { log.finest(""); Native.putLong(this.pData + 48L + paramInt * Native.getLongSize(), paramLong); } 
/* 48 */   public long get_convert() { log.finest(""); return this.pData + 48L; }
/*    */ 
/*    */   String getName()
/*    */   {
/* 52 */     return "awtImageData";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 57 */     StringBuilder localStringBuilder = new StringBuilder(160);
/*    */ 
/* 59 */     localStringBuilder.append("Depth = ").append(get_Depth()).append(", ");
/* 60 */     localStringBuilder.append("wsImageFormat = ").append(get_wsImageFormat()).append(", ");
/* 61 */     localStringBuilder.append("clrdata = ").append(get_clrdata()).append(", ");
/* 62 */     localStringBuilder.append("{").append(get_convert(0)).append(" ").append(get_convert(1)).append(" ").append(get_convert(2)).append(" ").append(get_convert(3)).append(" ").append(get_convert(4)).append(" ").append(get_convert(5)).append(" ").append(get_convert(6)).append(" ").append(get_convert(7)).append(" ").append(get_convert(8)).append(" ").append(get_convert(9)).append(" ").append(get_convert(10)).append(" ").append(get_convert(11)).append(" ").append(get_convert(12)).append(" ").append(get_convert(13)).append(" ").append(get_convert(14)).append(" ").append(get_convert(15)).append(" ").append(get_convert(16)).append(" ").append(get_convert(17)).append(" ").append(get_convert(18)).append(" ").append(get_convert(19)).append(" ").append(get_convert(20)).append(" ").append(get_convert(21)).append(" ").append(get_convert(22)).append(" ").append(get_convert(23)).append(" ").append(get_convert(24)).append(" ").append(get_convert(25)).append(" ").append(get_convert(26)).append(" ").append(get_convert(27)).append(" ").append(get_convert(28)).append(" ").append(get_convert(29)).append(" ").append(get_convert(30)).append(" ").append(get_convert(31)).append(" ").append("}");
/*    */ 
/* 95 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.awtImageData
 * JD-Core Version:    0.6.2
 */