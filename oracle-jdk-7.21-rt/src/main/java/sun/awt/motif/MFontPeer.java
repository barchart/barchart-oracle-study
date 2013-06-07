/*    */ package sun.awt.motif;
/*    */ 
/*    */ import java.awt.GraphicsEnvironment;
/*    */ import sun.awt.PlatformFont;
/*    */ 
/*    */ public class MFontPeer extends PlatformFont
/*    */ {
/*    */   private String xfsname;
/*    */   private String converter;
/*    */ 
/*    */   private static native void initIDs();
/*    */ 
/*    */   public MFontPeer(String paramString, int paramInt)
/*    */   {
/* 55 */     super(paramString, paramInt);
/*    */ 
/* 57 */     if (this.fontConfig != null)
/* 58 */       this.xfsname = ((MFontConfiguration)this.fontConfig).getMotifFontSet(this.familyName, paramInt);
/*    */   }
/*    */ 
/*    */   protected char getMissingGlyphCharacter()
/*    */   {
/* 63 */     return '❏';
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 43 */     if (!GraphicsEnvironment.isHeadless())
/* 44 */       initIDs();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.MFontPeer
 * JD-Core Version:    0.6.2
 */