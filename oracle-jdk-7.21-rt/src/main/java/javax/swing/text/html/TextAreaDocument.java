/*    */ package javax.swing.text.html;
/*    */ 
/*    */ import javax.swing.text.BadLocationException;
/*    */ import javax.swing.text.PlainDocument;
/*    */ 
/*    */ class TextAreaDocument extends PlainDocument
/*    */ {
/*    */   String initialText;
/*    */ 
/*    */   void reset()
/*    */   {
/*    */     try
/*    */     {
/* 50 */       remove(0, getLength());
/* 51 */       if (this.initialText != null)
/* 52 */         insertString(0, this.initialText, null);
/*    */     }
/*    */     catch (BadLocationException localBadLocationException)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   void storeInitialText()
/*    */   {
/*    */     try
/*    */     {
/* 64 */       this.initialText = getText(0, getLength());
/*    */     }
/*    */     catch (BadLocationException localBadLocationException)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.html.TextAreaDocument
 * JD-Core Version:    0.6.2
 */