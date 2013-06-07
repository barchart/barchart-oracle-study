/*     */ package javax.sql.rowset.spi;
/*     */ 
/*     */ import javax.sql.RowSetReader;
/*     */ import javax.sql.RowSetWriter;
/*     */ 
/*     */ class ProviderImpl extends SyncProvider
/*     */ {
/* 808 */   private String className = null;
/* 809 */   private String vendorName = null;
/* 810 */   private String ver = null;
/*     */   private int index;
/*     */ 
/*     */   public void setClassname(String paramString)
/*     */   {
/* 814 */     this.className = paramString;
/*     */   }
/*     */ 
/*     */   public String getClassname() {
/* 818 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void setVendor(String paramString) {
/* 822 */     this.vendorName = paramString;
/*     */   }
/*     */ 
/*     */   public String getVendor() {
/* 826 */     return this.vendorName;
/*     */   }
/*     */ 
/*     */   public void setVersion(String paramString) {
/* 830 */     this.ver = paramString;
/*     */   }
/*     */ 
/*     */   public String getVersion() {
/* 834 */     return this.ver;
/*     */   }
/*     */ 
/*     */   public void setIndex(int paramInt) {
/* 838 */     this.index = paramInt;
/*     */   }
/*     */ 
/*     */   public int getIndex() {
/* 842 */     return this.index;
/*     */   }
/*     */ 
/*     */   public int getDataSourceLock() throws SyncProviderException
/*     */   {
/* 847 */     int i = 0;
/*     */     try {
/* 849 */       i = SyncFactory.getInstance(this.className).getDataSourceLock();
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException) {
/* 852 */       throw new SyncProviderException(localSyncFactoryException.getMessage());
/*     */     }
/*     */ 
/* 855 */     return i;
/*     */   }
/*     */ 
/*     */   public int getProviderGrade()
/*     */   {
/* 860 */     int i = 0;
/*     */     try
/*     */     {
/* 863 */       i = SyncFactory.getInstance(this.className).getProviderGrade();
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException)
/*     */     {
/*     */     }
/* 868 */     return i;
/*     */   }
/*     */ 
/*     */   public String getProviderID() {
/* 872 */     return this.className;
/*     */   }
/*     */ 
/*     */   public RowSetReader getRowSetReader()
/*     */   {
/* 887 */     RowSetReader localRowSetReader = null;
/*     */     try
/*     */     {
/* 890 */       localRowSetReader = SyncFactory.getInstance(this.className).getRowSetReader();
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException)
/*     */     {
/*     */     }
/* 895 */     return localRowSetReader;
/*     */   }
/*     */ 
/*     */   public RowSetWriter getRowSetWriter()
/*     */   {
/* 901 */     RowSetWriter localRowSetWriter = null;
/*     */     try {
/* 903 */       localRowSetWriter = SyncFactory.getInstance(this.className).getRowSetWriter();
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException)
/*     */     {
/*     */     }
/* 908 */     return localRowSetWriter;
/*     */   }
/*     */ 
/*     */   public void setDataSourceLock(int paramInt) throws SyncProviderException
/*     */   {
/*     */     try
/*     */     {
/* 915 */       SyncFactory.getInstance(this.className).setDataSourceLock(paramInt);
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException) {
/* 918 */       throw new SyncProviderException(localSyncFactoryException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int supportsUpdatableView()
/*     */   {
/* 924 */     int i = 0;
/*     */     try
/*     */     {
/* 927 */       i = SyncFactory.getInstance(this.className).supportsUpdatableView();
/*     */     }
/*     */     catch (SyncFactoryException localSyncFactoryException)
/*     */     {
/*     */     }
/* 932 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.spi.ProviderImpl
 * JD-Core Version:    0.6.2
 */