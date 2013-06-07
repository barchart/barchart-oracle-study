/*     */ package javax.activation;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class DataHandler
/*     */   implements Transferable
/*     */ {
/*  81 */   private DataSource dataSource = null;
/*  82 */   private DataSource objDataSource = null;
/*     */ 
/*  87 */   private Object object = null;
/*  88 */   private String objectMimeType = null;
/*     */ 
/*  91 */   private CommandMap currentCommandMap = null;
/*     */ 
/*  94 */   private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
/*  95 */   private DataFlavor[] transferFlavors = emptyFlavors;
/*     */ 
/*  98 */   private DataContentHandler dataContentHandler = null;
/*  99 */   private DataContentHandler factoryDCH = null;
/*     */ 
/* 102 */   private static DataContentHandlerFactory factory = null;
/* 103 */   private DataContentHandlerFactory oldFactory = null;
/*     */ 
/* 105 */   private String shortType = null;
/*     */ 
/*     */   public DataHandler(DataSource ds)
/*     */   {
/* 116 */     this.dataSource = ds;
/* 117 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */   public DataHandler(Object obj, String mimeType)
/*     */   {
/* 130 */     this.object = obj;
/* 131 */     this.objectMimeType = mimeType;
/* 132 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */   public DataHandler(URL url)
/*     */   {
/* 143 */     this.dataSource = new URLDataSource(url);
/* 144 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */   private synchronized CommandMap getCommandMap()
/*     */   {
/* 151 */     if (this.currentCommandMap != null) {
/* 152 */       return this.currentCommandMap;
/*     */     }
/* 154 */     return CommandMap.getDefaultCommandMap();
/*     */   }
/*     */ 
/*     */   public DataSource getDataSource()
/*     */   {
/* 172 */     if (this.dataSource == null)
/*     */     {
/* 174 */       if (this.objDataSource == null)
/* 175 */         this.objDataSource = new DataHandlerDataSource(this);
/* 176 */       return this.objDataSource;
/*     */     }
/* 178 */     return this.dataSource;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 190 */     if (this.dataSource != null) {
/* 191 */       return this.dataSource.getName();
/*     */     }
/* 193 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 204 */     if (this.dataSource != null) {
/* 205 */       return this.dataSource.getContentType();
/*     */     }
/* 207 */     return this.objectMimeType;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 235 */     InputStream ins = null;
/*     */ 
/* 237 */     if (this.dataSource != null) {
/* 238 */       ins = this.dataSource.getInputStream();
/*     */     } else {
/* 240 */       DataContentHandler dch = getDataContentHandler();
/*     */ 
/* 242 */       if (dch == null) {
/* 243 */         throw new UnsupportedDataTypeException("no DCH for MIME type " + getBaseType());
/*     */       }
/*     */ 
/* 246 */       if (((dch instanceof ObjectDataContentHandler)) && 
/* 247 */         (((ObjectDataContentHandler)dch).getDCH() == null)) {
/* 248 */         throw new UnsupportedDataTypeException("no object DCH for MIME type " + getBaseType());
/*     */       }
/*     */ 
/* 252 */       final DataContentHandler fdch = dch;
/*     */ 
/* 260 */       final PipedOutputStream pos = new PipedOutputStream();
/* 261 */       PipedInputStream pin = new PipedInputStream(pos);
/* 262 */       new Thread(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/* 266 */             fdch.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pos);
/*     */           } catch (IOException e) {
/*     */           }
/*     */           finally {
/*     */             try {
/* 271 */               pos.close();
/*     */             }
/*     */             catch (IOException ie)
/*     */             {
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       , "DataHandler.getInputStream").start();
/*     */ 
/* 277 */       ins = pin;
/*     */     }
/*     */ 
/* 280 */     return ins;
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream os)
/*     */     throws IOException
/*     */   {
/* 300 */     if (this.dataSource != null) {
/* 301 */       InputStream is = null;
/* 302 */       byte[] data = new byte[8192];
/*     */ 
/* 305 */       is = this.dataSource.getInputStream();
/*     */       try
/*     */       {
/*     */         int bytes_read;
/* 308 */         while ((bytes_read = is.read(data)) > 0)
/* 309 */           os.write(data, 0, bytes_read);
/*     */       }
/*     */       finally {
/* 312 */         is.close();
/* 313 */         is = null;
/*     */       }
/*     */     } else {
/* 316 */       DataContentHandler dch = getDataContentHandler();
/* 317 */       dch.writeTo(this.object, this.objectMimeType, os);
/*     */     }
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 334 */     if (this.dataSource != null) {
/* 335 */       return this.dataSource.getOutputStream();
/*     */     }
/* 337 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 365 */     if (factory != this.oldFactory) {
/* 366 */       this.transferFlavors = emptyFlavors;
/*     */     }
/*     */ 
/* 369 */     if (this.transferFlavors == emptyFlavors)
/* 370 */       this.transferFlavors = getDataContentHandler().getTransferDataFlavors();
/* 371 */     return this.transferFlavors;
/*     */   }
/*     */ 
/*     */   public boolean isDataFlavorSupported(DataFlavor flavor)
/*     */   {
/* 387 */     DataFlavor[] lFlavors = getTransferDataFlavors();
/*     */ 
/* 389 */     for (int i = 0; i < lFlavors.length; i++) {
/* 390 */       if (lFlavors[i].equals(flavor))
/* 391 */         return true;
/*     */     }
/* 393 */     return false;
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor flavor)
/*     */     throws UnsupportedFlavorException, IOException
/*     */   {
/* 431 */     return getDataContentHandler().getTransferData(flavor, this.dataSource);
/*     */   }
/*     */ 
/*     */   public synchronized void setCommandMap(CommandMap commandMap)
/*     */   {
/* 447 */     if ((commandMap != this.currentCommandMap) || (commandMap == null))
/*     */     {
/* 449 */       this.transferFlavors = emptyFlavors;
/* 450 */       this.dataContentHandler = null;
/*     */ 
/* 452 */       this.currentCommandMap = commandMap;
/*     */     }
/*     */   }
/*     */ 
/*     */   public CommandInfo[] getPreferredCommands()
/*     */   {
/* 470 */     if (this.dataSource != null) {
/* 471 */       return getCommandMap().getPreferredCommands(getBaseType(), this.dataSource);
/*     */     }
/*     */ 
/* 474 */     return getCommandMap().getPreferredCommands(getBaseType());
/*     */   }
/*     */ 
/*     */   public CommandInfo[] getAllCommands()
/*     */   {
/* 490 */     if (this.dataSource != null) {
/* 491 */       return getCommandMap().getAllCommands(getBaseType(), this.dataSource);
/*     */     }
/* 493 */     return getCommandMap().getAllCommands(getBaseType());
/*     */   }
/*     */ 
/*     */   public CommandInfo getCommand(String cmdName)
/*     */   {
/* 509 */     if (this.dataSource != null) {
/* 510 */       return getCommandMap().getCommand(getBaseType(), cmdName, this.dataSource);
/*     */     }
/*     */ 
/* 513 */     return getCommandMap().getCommand(getBaseType(), cmdName);
/*     */   }
/*     */ 
/*     */   public Object getContent()
/*     */     throws IOException
/*     */   {
/* 534 */     if (this.object != null) {
/* 535 */       return this.object;
/*     */     }
/* 537 */     return getDataContentHandler().getContent(getDataSource());
/*     */   }
/*     */ 
/*     */   public Object getBean(CommandInfo cmdinfo)
/*     */   {
/* 553 */     Object bean = null;
/*     */     try
/*     */     {
/* 557 */       ClassLoader cld = null;
/*     */ 
/* 559 */       cld = SecuritySupport.getContextClassLoader();
/* 560 */       if (cld == null)
/* 561 */         cld = getClass().getClassLoader();
/* 562 */       bean = cmdinfo.getCommandObject(this, cld);
/*     */     } catch (IOException e) {
/*     */     } catch (ClassNotFoundException e) {
/*     */     }
/* 566 */     return bean;
/*     */   }
/*     */ 
/*     */   private synchronized DataContentHandler getDataContentHandler()
/*     */   {
/* 589 */     if (factory != this.oldFactory) {
/* 590 */       this.oldFactory = factory;
/* 591 */       this.factoryDCH = null;
/* 592 */       this.dataContentHandler = null;
/* 593 */       this.transferFlavors = emptyFlavors;
/*     */     }
/*     */ 
/* 596 */     if (this.dataContentHandler != null) {
/* 597 */       return this.dataContentHandler;
/*     */     }
/* 599 */     String simpleMT = getBaseType();
/*     */ 
/* 601 */     if ((this.factoryDCH == null) && (factory != null)) {
/* 602 */       this.factoryDCH = factory.createDataContentHandler(simpleMT);
/*     */     }
/* 604 */     if (this.factoryDCH != null) {
/* 605 */       this.dataContentHandler = this.factoryDCH;
/*     */     }
/* 607 */     if (this.dataContentHandler == null) {
/* 608 */       if (this.dataSource != null) {
/* 609 */         this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT, this.dataSource);
/*     */       }
/*     */       else {
/* 612 */         this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 618 */     if (this.dataSource != null) {
/* 619 */       this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
/*     */     }
/*     */     else
/*     */     {
/* 623 */       this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
/*     */     }
/*     */ 
/* 627 */     return this.dataContentHandler;
/*     */   }
/*     */ 
/*     */   private synchronized String getBaseType()
/*     */   {
/* 635 */     if (this.shortType == null) {
/* 636 */       String ct = getContentType();
/*     */       try {
/* 638 */         MimeType mt = new MimeType(ct);
/* 639 */         this.shortType = mt.getBaseType();
/*     */       } catch (MimeTypeParseException e) {
/* 641 */         this.shortType = ct;
/*     */       }
/*     */     }
/* 644 */     return this.shortType;
/*     */   }
/*     */ 
/*     */   public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory newFactory)
/*     */   {
/* 662 */     if (factory != null) {
/* 663 */       throw new Error("DataContentHandlerFactory already defined");
/*     */     }
/* 665 */     SecurityManager security = System.getSecurityManager();
/* 666 */     if (security != null)
/*     */       try
/*     */       {
/* 669 */         security.checkSetFactory();
/*     */       }
/*     */       catch (SecurityException ex)
/*     */       {
/* 674 */         if (DataHandler.class.getClassLoader() != newFactory.getClass().getClassLoader())
/*     */         {
/* 676 */           throw ex;
/*     */         }
/*     */       }
/* 679 */     factory = newFactory;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activation.DataHandler
 * JD-Core Version:    0.6.2
 */