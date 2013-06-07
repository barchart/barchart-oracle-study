/*     */ package javax.print;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.CharArrayReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import javax.print.attribute.AttributeSetUtilities;
/*     */ import javax.print.attribute.DocAttributeSet;
/*     */ 
/*     */ public final class SimpleDoc
/*     */   implements Doc
/*     */ {
/*     */   private DocFlavor flavor;
/*     */   private DocAttributeSet attributes;
/*     */   private Object printData;
/*     */   private Reader reader;
/*     */   private InputStream inStream;
/*     */ 
/*     */   public SimpleDoc(Object paramObject, DocFlavor paramDocFlavor, DocAttributeSet paramDocAttributeSet)
/*     */   {
/*  88 */     if ((paramDocFlavor == null) || (paramObject == null)) {
/*  89 */       throw new IllegalArgumentException("null argument(s)");
/*     */     }
/*     */ 
/*  92 */     Class localClass = null;
/*     */     try {
/*  94 */       localClass = Class.forName(paramDocFlavor.getRepresentationClassName());
/*     */     } catch (Throwable localThrowable) {
/*  96 */       throw new IllegalArgumentException("unknown representation class");
/*     */     }
/*     */ 
/*  99 */     if (!localClass.isInstance(paramObject)) {
/* 100 */       throw new IllegalArgumentException("data is not of declared type");
/*     */     }
/*     */ 
/* 103 */     this.flavor = paramDocFlavor;
/* 104 */     if (paramDocAttributeSet != null) {
/* 105 */       this.attributes = AttributeSetUtilities.unmodifiableView(paramDocAttributeSet);
/*     */     }
/* 107 */     this.printData = paramObject;
/*     */   }
/*     */ 
/*     */   public DocFlavor getDocFlavor()
/*     */   {
/* 117 */     return this.flavor;
/*     */   }
/*     */ 
/*     */   public DocAttributeSet getAttributes()
/*     */   {
/* 137 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   public Object getPrintData()
/*     */     throws IOException
/*     */   {
/* 156 */     return this.printData;
/*     */   }
/*     */ 
/*     */   public Reader getReaderForText()
/*     */     throws IOException
/*     */   {
/* 187 */     if ((this.printData instanceof Reader)) {
/* 188 */       return (Reader)this.printData;
/*     */     }
/*     */ 
/* 191 */     synchronized (this) {
/* 192 */       if (this.reader != null) {
/* 193 */         return this.reader;
/*     */       }
/*     */ 
/* 196 */       if ((this.printData instanceof char[])) {
/* 197 */         this.reader = new CharArrayReader((char[])this.printData);
/*     */       }
/* 199 */       else if ((this.printData instanceof String)) {
/* 200 */         this.reader = new StringReader((String)this.printData);
/*     */       }
/*     */     }
/* 203 */     return this.reader;
/*     */   }
/*     */ 
/*     */   public InputStream getStreamForBytes()
/*     */     throws IOException
/*     */   {
/* 235 */     if ((this.printData instanceof InputStream)) {
/* 236 */       return (InputStream)this.printData;
/*     */     }
/*     */ 
/* 239 */     synchronized (this) {
/* 240 */       if (this.inStream != null) {
/* 241 */         return this.inStream;
/*     */       }
/*     */ 
/* 244 */       if ((this.printData instanceof byte[])) {
/* 245 */         this.inStream = new ByteArrayInputStream((byte[])this.printData);
/*     */       }
/*     */     }
/* 248 */     return this.inStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.SimpleDoc
 * JD-Core Version:    0.6.2
 */