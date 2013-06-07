/*    */ package sun.util;
/*    */ 
/*    */ import java.util.ListResourceBundle;
/*    */ 
/*    */ public class EmptyListResourceBundle extends ListResourceBundle
/*    */ {
/*    */   protected final Object[][] getContents()
/*    */   {
/* 33 */     return new Object[0][];
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.util.EmptyListResourceBundle
 * JD-Core Version:    0.6.2
 */