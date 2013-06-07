/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Image;
/*    */ import java.awt.Point;
/*    */ import sun.awt.X11CustomCursor;
/*    */ import sun.misc.Unsafe;
/*    */ 
/*    */ public class XCustomCursor extends X11CustomCursor
/*    */ {
/*    */   public XCustomCursor(Image paramImage, Point paramPoint, String paramString)
/*    */     throws IndexOutOfBoundsException
/*    */   {
/* 42 */     super(paramImage, paramPoint, paramString);
/*    */   }
/*    */ 
/*    */   static Dimension getBestCursorSize(int paramInt1, int paramInt2)
/*    */   {
/* 57 */     XToolkit.awtLock();
/*    */     Dimension localDimension;
/*    */     try
/*    */     {
/* 59 */       long l1 = XToolkit.getDisplay();
/* 60 */       long l2 = XlibWrapper.RootWindow(l1, XlibWrapper.DefaultScreen(l1));
/*    */ 
/* 63 */       XlibWrapper.XQueryBestCursor(l1, l2, Math.abs(paramInt1), Math.abs(paramInt2), XlibWrapper.larg1, XlibWrapper.larg2);
/* 64 */       localDimension = new Dimension(XlibWrapper.unsafe.getInt(XlibWrapper.larg1), XlibWrapper.unsafe.getInt(XlibWrapper.larg2));
/*    */     }
/*    */     finally {
/* 67 */       XToolkit.awtUnlock();
/*    */     }
/* 69 */     return localDimension;
/*    */   }
/*    */ 
/*    */   protected void createCursor(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*    */   {
/* 77 */     XToolkit.awtLock();
/*    */     try {
/* 79 */       long l1 = XToolkit.getDisplay();
/* 80 */       long l2 = XlibWrapper.RootWindow(l1, XlibWrapper.DefaultScreen(l1));
/*    */ 
/* 83 */       long l3 = XToolkit.getDefaultXColormap();
/* 84 */       XColor localXColor1 = new XColor();
/*    */ 
/* 86 */       localXColor1.set_flags((byte)7);
/* 87 */       localXColor1.set_red((short)((paramInt3 >> 16 & 0xFF) << 8));
/* 88 */       localXColor1.set_green((short)((paramInt3 >> 8 & 0xFF) << 8));
/* 89 */       localXColor1.set_blue((short)((paramInt3 >> 0 & 0xFF) << 8));
/*    */ 
/* 91 */       XlibWrapper.XAllocColor(l1, l3, localXColor1.pData);
/*    */ 
/* 94 */       XColor localXColor2 = new XColor();
/* 95 */       localXColor2.set_flags((byte)7);
/*    */ 
/* 97 */       localXColor2.set_red((short)((paramInt4 >> 16 & 0xFF) << 8));
/* 98 */       localXColor2.set_green((short)((paramInt4 >> 8 & 0xFF) << 8));
/* 99 */       localXColor2.set_blue((short)((paramInt4 >> 0 & 0xFF) << 8));
/*    */ 
/* 101 */       XlibWrapper.XAllocColor(l1, l3, localXColor2.pData);
/*    */ 
/* 104 */       long l4 = Native.toData(paramArrayOfByte1);
/* 105 */       long l5 = XlibWrapper.XCreateBitmapFromData(l1, l2, l4, paramInt1, paramInt2);
/*    */ 
/* 107 */       long l6 = Native.toData(paramArrayOfByte2);
/* 108 */       long l7 = XlibWrapper.XCreateBitmapFromData(l1, l2, l6, paramInt1, paramInt2);
/*    */ 
/* 110 */       long l8 = XlibWrapper.XCreatePixmapCursor(l1, l5, l7, localXColor1.pData, localXColor2.pData, paramInt5, paramInt6);
/*    */ 
/* 112 */       XlibWrapper.unsafe.freeMemory(l4);
/* 113 */       XlibWrapper.unsafe.freeMemory(l6);
/* 114 */       XlibWrapper.XFreePixmap(l1, l5);
/* 115 */       XlibWrapper.XFreePixmap(l1, l7);
/* 116 */       localXColor2.dispose();
/* 117 */       localXColor1.dispose();
/*    */ 
/* 119 */       XGlobalCursorManager.setPData(this, l8);
/*    */     }
/*    */     finally {
/* 122 */       XToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XCustomCursor
 * JD-Core Version:    0.6.2
 */