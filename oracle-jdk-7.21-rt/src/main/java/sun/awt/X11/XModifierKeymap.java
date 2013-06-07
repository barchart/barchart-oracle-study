/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ public class XModifierKeymap extends XWrapperBase
/*    */ {
/*  9 */   private Unsafe unsafe = XlibWrapper.unsafe;
/*    */   private final boolean should_free_memory;
/*    */   long pData;
/*    */ 
/*    */   public static int getSize()
/*    */   {
/* 11 */     return 8; } 
/* 12 */   public int getDataSize() { return getSize(); }
/*    */ 
/*    */   public long getPData()
/*    */   {
/* 16 */     return this.pData;
/*    */   }
/*    */ 
/*    */   public XModifierKeymap(long paramLong) {
/* 20 */     log.finest("Creating");
/* 21 */     this.pData = paramLong;
/* 22 */     this.should_free_memory = false;
/*    */   }
/*    */ 
/*    */   public XModifierKeymap()
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
/* 40 */   public int get_max_keypermod() { log.finest(""); return Native.getInt(this.pData + 0L); } 
/* 41 */   public void set_max_keypermod(int paramInt) { log.finest(""); Native.putInt(this.pData + 0L, paramInt); } 
/* 42 */   public long get_modifiermap(int paramInt) { log.finest(""); return Native.getLong(this.pData + 4L) + paramInt * Native.getLongSize(); } 
/* 43 */   public long get_modifiermap() { log.finest(""); return Native.getLong(this.pData + 4L); } 
/* 44 */   public void set_modifiermap(long paramLong) { log.finest(""); Native.putLong(this.pData + 4L, paramLong); }
/*    */ 
/*    */   String getName()
/*    */   {
/* 48 */     return "XModifierKeymap";
/*    */   }
/*    */ 
/*    */   String getFieldsAsString()
/*    */   {
/* 53 */     StringBuilder localStringBuilder = new StringBuilder(80);
/*    */ 
/* 55 */     localStringBuilder.append("max_keypermod = ").append(get_max_keypermod()).append(", ");
/* 56 */     localStringBuilder.append("modifiermap = ").append(get_modifiermap()).append(", ");
/* 57 */     return localStringBuilder.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XModifierKeymap
 * JD-Core Version:    0.6.2
 */