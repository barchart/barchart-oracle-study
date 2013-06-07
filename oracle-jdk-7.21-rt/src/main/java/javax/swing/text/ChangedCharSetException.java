/*    */ package javax.swing.text;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ChangedCharSetException extends IOException
/*    */ {
/*    */   String charSetSpec;
/*    */   boolean charSetKey;
/*    */ 
/*    */   public ChangedCharSetException(String paramString, boolean paramBoolean)
/*    */   {
/* 41 */     this.charSetSpec = paramString;
/* 42 */     this.charSetKey = paramBoolean;
/*    */   }
/*    */ 
/*    */   public String getCharSetSpec() {
/* 46 */     return this.charSetSpec;
/*    */   }
/*    */ 
/*    */   public boolean keyEqualsCharSet() {
/* 50 */     return this.charSetKey;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.ChangedCharSetException
 * JD-Core Version:    0.6.2
 */