/*     */ package javax.activation;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ class DataSourceDataContentHandler
/*     */   implements DataContentHandler
/*     */ {
/* 739 */   private DataSource ds = null;
/* 740 */   private DataFlavor[] transferFlavors = null;
/* 741 */   private DataContentHandler dch = null;
/*     */ 
/*     */   public DataSourceDataContentHandler(DataContentHandler dch, DataSource ds)
/*     */   {
/* 747 */     this.ds = ds;
/* 748 */     this.dch = dch;
/*     */   }
/*     */ 
/*     */   public DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 757 */     if (this.transferFlavors == null) {
/* 758 */       if (this.dch != null) {
/* 759 */         this.transferFlavors = this.dch.getTransferDataFlavors();
/*     */       } else {
/* 761 */         this.transferFlavors = new DataFlavor[1];
/* 762 */         this.transferFlavors[0] = new ActivationDataFlavor(this.ds.getContentType(), this.ds.getContentType());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 767 */     return this.transferFlavors;
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor df, DataSource ds)
/*     */     throws UnsupportedFlavorException, IOException
/*     */   {
/* 779 */     if (this.dch != null)
/* 780 */       return this.dch.getTransferData(df, ds);
/* 781 */     if (df.equals(getTransferDataFlavors()[0])) {
/* 782 */       return ds.getInputStream();
/*     */     }
/* 784 */     throw new UnsupportedFlavorException(df);
/*     */   }
/*     */ 
/*     */   public Object getContent(DataSource ds) throws IOException
/*     */   {
/* 789 */     if (this.dch != null) {
/* 790 */       return this.dch.getContent(ds);
/*     */     }
/* 792 */     return ds.getInputStream();
/*     */   }
/*     */ 
/*     */   public void writeTo(Object obj, String mimeType, OutputStream os)
/*     */     throws IOException
/*     */   {
/* 800 */     if (this.dch != null)
/* 801 */       this.dch.writeTo(obj, mimeType, os);
/*     */     else
/* 803 */       throw new UnsupportedDataTypeException("no DCH for content type " + this.ds.getContentType());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.DataSourceDataContentHandler
 * JD-Core Version:    0.6.2
 */