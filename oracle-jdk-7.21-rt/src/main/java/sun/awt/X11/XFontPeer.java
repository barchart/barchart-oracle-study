/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.GraphicsEnvironment;
/*    */ import sun.awt.PlatformFont;
/*    */ 
/*    */ public class XFontPeer extends PlatformFont
/*    */ {
/*    */   private String xfsname;
/*    */ 
/*    */   private static native void initIDs();
/*    */ 
/*    */   public XFontPeer(String paramString, int paramInt)
/*    */   {
/* 50 */     super(paramString, paramInt);
/*    */   }
/*    */ 
/*    */   protected char getMissingGlyphCharacter() {
/* 54 */     return '❏';
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 38 */     if (!GraphicsEnvironment.isHeadless())
/* 39 */       initIDs();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XFontPeer
 * JD-Core Version:    0.6.2
 */