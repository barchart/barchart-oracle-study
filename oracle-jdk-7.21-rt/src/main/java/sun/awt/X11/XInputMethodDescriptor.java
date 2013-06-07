/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.im.spi.InputMethod;
/*    */ import sun.awt.X11InputMethodDescriptor;
/*    */ 
/*    */ class XInputMethodDescriptor extends X11InputMethodDescriptor
/*    */ {
/*    */   public InputMethod createInputMethod()
/*    */     throws Exception
/*    */   {
/* 38 */     return new XInputMethod();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XInputMethodDescriptor
 * JD-Core Version:    0.6.2
 */