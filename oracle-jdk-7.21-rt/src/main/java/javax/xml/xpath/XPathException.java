/*     */ package javax.xml.xpath;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class XPathException extends Exception
/*     */ {
/*     */   private final Throwable cause;
/*     */   private static final long serialVersionUID = -1837080260374986980L;
/*     */ 
/*     */   public XPathException(String message)
/*     */   {
/*  61 */     super(message);
/*  62 */     if (message == null) {
/*  63 */       throw new NullPointerException("message can't be null");
/*     */     }
/*  65 */     this.cause = null;
/*     */   }
/*     */ 
/*     */   public XPathException(Throwable cause)
/*     */   {
/*  81 */     this.cause = cause;
/*  82 */     if (cause == null)
/*  83 */       throw new NullPointerException("cause can't be null");
/*     */   }
/*     */ 
/*     */   public Throwable getCause()
/*     */   {
/*  93 */     return this.cause;
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintStream s)
/*     */   {
/* 102 */     if (getCause() != null) {
/* 103 */       getCause().printStackTrace(s);
/* 104 */       s.println("--------------- linked to ------------------");
/*     */     }
/*     */ 
/* 107 */     super.printStackTrace(s);
/*     */   }
/*     */ 
/*     */   public void printStackTrace()
/*     */   {
/* 114 */     printStackTrace(System.err);
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintWriter s)
/*     */   {
/* 124 */     if (getCause() != null) {
/* 125 */       getCause().printStackTrace(s);
/* 126 */       s.println("--------------- linked to ------------------");
/*     */     }
/*     */ 
/* 129 */     super.printStackTrace(s);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.xpath.XPathException
 * JD-Core Version:    0.6.2
 */