/*    */ package javax.swing.text.html.parser;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ 
/*    */ class ResourceLoader
/*    */   implements PrivilegedAction
/*    */ {
/*    */   private String name;
/*    */ 
/*    */   ResourceLoader(String paramString)
/*    */   {
/* 46 */     this.name = paramString;
/*    */   }
/*    */ 
/*    */   public Object run() {
/* 50 */     InputStream localInputStream = ParserDelegator.class.getResourceAsStream(this.name);
/* 51 */     return localInputStream;
/*    */   }
/*    */ 
/*    */   public static InputStream getResourceAsStream(String paramString) {
/* 55 */     ResourceLoader localResourceLoader = new ResourceLoader(paramString);
/* 56 */     return (InputStream)AccessController.doPrivileged(localResourceLoader);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.text.html.parser.ResourceLoader
 * JD-Core Version:    0.6.2
 */