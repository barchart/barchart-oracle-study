/*     */ package javax.activation;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ 
/*     */ class ObjectDataContentHandler
/*     */   implements DataContentHandler
/*     */ {
/* 816 */   private DataFlavor[] transferFlavors = null;
/*     */   private Object obj;
/*     */   private String mimeType;
/* 819 */   private DataContentHandler dch = null;
/*     */ 
/*     */   public ObjectDataContentHandler(DataContentHandler dch, Object obj, String mimeType)
/*     */   {
/* 826 */     this.obj = obj;
/* 827 */     this.mimeType = mimeType;
/* 828 */     this.dch = dch;
/*     */   }
/*     */ 
/*     */   public DataContentHandler getDCH()
/*     */   {
/* 836 */     return this.dch;
/*     */   }
/*     */ 
/*     */   public synchronized DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 844 */     if (this.transferFlavors == null) {
/* 845 */       if (this.dch != null) {
/* 846 */         this.transferFlavors = this.dch.getTransferDataFlavors();
/*     */       } else {
/* 848 */         this.transferFlavors = new DataFlavor[1];
/* 849 */         this.transferFlavors[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
/*     */       }
/*     */     }
/*     */ 
/* 853 */     return this.transferFlavors;
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor df, DataSource ds)
/*     */     throws UnsupportedFlavorException, IOException
/*     */   {
/* 865 */     if (this.dch != null)
/* 866 */       return this.dch.getTransferData(df, ds);
/* 867 */     if (df.equals(getTransferDataFlavors()[0])) {
/* 868 */       return this.obj;
/*     */     }
/* 870 */     throw new UnsupportedFlavorException(df);
/*     */   }
/*     */ 
/*     */   public Object getContent(DataSource ds)
/*     */   {
/* 875 */     return this.obj;
/*     */   }
/*     */ 
/*     */   public void writeTo(Object obj, String mimeType, OutputStream os)
/*     */     throws IOException
/*     */   {
/* 883 */     if (this.dch != null) {
/* 884 */       this.dch.writeTo(obj, mimeType, os);
/* 885 */     } else if ((obj instanceof byte[])) {
/* 886 */       os.write((byte[])obj);
/* 887 */     } else if ((obj instanceof String)) {
/* 888 */       OutputStreamWriter osw = new OutputStreamWriter(os);
/* 889 */       osw.write((String)obj);
/* 890 */       osw.flush(); } else {
/* 891 */       throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.ObjectDataContentHandler
 * JD-Core Version:    0.6.2
 */