/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ class DataHandlerDataSource
/*     */   implements DataSource
/*     */ {
/* 689 */   DataHandler dataHandler = null;
/*     */ 
/*     */   public DataHandlerDataSource(DataHandler dh)
/*     */   {
/* 695 */     this.dataHandler = dh;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 703 */     return this.dataHandler.getInputStream();
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 711 */     return this.dataHandler.getOutputStream();
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 719 */     return this.dataHandler.getContentType();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 727 */     return this.dataHandler.getName();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.DataHandlerDataSource
 * JD-Core Version:    0.6.2
 */