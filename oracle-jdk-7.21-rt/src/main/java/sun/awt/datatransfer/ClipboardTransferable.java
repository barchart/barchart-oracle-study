/*     */ package sun.awt.datatransfer;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ClipboardTransferable
/*     */   implements Transferable
/*     */ {
/*  58 */   private final HashMap flavorsToData = new HashMap();
/*  59 */   private DataFlavor[] flavors = new DataFlavor[0];
/*     */ 
/*     */   public ClipboardTransferable(SunClipboard paramSunClipboard)
/*     */   {
/*  78 */     paramSunClipboard.openClipboard(null);
/*     */     try
/*     */     {
/*  81 */       long[] arrayOfLong = paramSunClipboard.getClipboardFormats();
/*     */ 
/*  83 */       if ((arrayOfLong != null) && (arrayOfLong.length > 0))
/*     */       {
/*  87 */         HashMap localHashMap = new HashMap(arrayOfLong.length, 1.0F);
/*     */ 
/*  89 */         Map localMap = DataTransferer.getInstance().getFlavorsForFormats(arrayOfLong, SunClipboard.flavorMap);
/*     */ 
/*  91 */         Iterator localIterator = localMap.keySet().iterator();
/*  92 */         while (localIterator.hasNext())
/*     */         {
/*  94 */           DataFlavor localDataFlavor = (DataFlavor)localIterator.next();
/*  95 */           Long localLong = (Long)localMap.get(localDataFlavor);
/*     */ 
/*  97 */           fetchOneFlavor(paramSunClipboard, localDataFlavor, localLong, localHashMap);
/*     */         }
/*     */ 
/* 100 */         DataTransferer.getInstance(); this.flavors = DataTransferer.setToSortedDataFlavorArray(this.flavorsToData.keySet(), localMap);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 105 */       paramSunClipboard.closeClipboard();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean fetchOneFlavor(SunClipboard paramSunClipboard, DataFlavor paramDataFlavor, Long paramLong, HashMap paramHashMap)
/*     */   {
/* 112 */     if (!this.flavorsToData.containsKey(paramDataFlavor)) {
/* 113 */       long l = paramLong.longValue();
/* 114 */       Object localObject = null;
/*     */ 
/* 116 */       if (!paramHashMap.containsKey(paramLong)) {
/*     */         try {
/* 118 */           localObject = paramSunClipboard.getClipboardData(l);
/*     */         } catch (IOException localIOException) {
/* 120 */           localObject = localIOException;
/*     */         } catch (Throwable localThrowable) {
/* 122 */           localThrowable.printStackTrace();
/*     */         }
/*     */ 
/* 127 */         paramHashMap.put(paramLong, localObject);
/*     */       } else {
/* 129 */         localObject = paramHashMap.get(paramLong);
/*     */       }
/*     */ 
/* 135 */       if ((localObject instanceof IOException)) {
/* 136 */         this.flavorsToData.put(paramDataFlavor, localObject);
/* 137 */         return false;
/* 138 */       }if (localObject != null) {
/* 139 */         this.flavorsToData.put(paramDataFlavor, new DataFactory(l, (byte[])localObject));
/*     */ 
/* 141 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   public DataFlavor[] getTransferDataFlavors() {
/* 149 */     return (DataFlavor[])this.flavors.clone();
/*     */   }
/*     */ 
/*     */   public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
/* 153 */     return this.flavorsToData.containsKey(paramDataFlavor);
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor paramDataFlavor)
/*     */     throws UnsupportedFlavorException, IOException
/*     */   {
/* 159 */     if (!isDataFlavorSupported(paramDataFlavor)) {
/* 160 */       throw new UnsupportedFlavorException(paramDataFlavor);
/*     */     }
/* 162 */     Object localObject = this.flavorsToData.get(paramDataFlavor);
/* 163 */     if ((localObject instanceof IOException))
/*     */     {
/* 165 */       throw ((IOException)localObject);
/* 166 */     }if ((localObject instanceof DataFactory))
/*     */     {
/* 168 */       DataFactory localDataFactory = (DataFactory)localObject;
/* 169 */       localObject = localDataFactory.getTransferData(paramDataFlavor);
/*     */     }
/* 171 */     return localObject;
/*     */   }
/*     */ 
/*     */   private final class DataFactory
/*     */   {
/*     */     final long format;
/*     */     final byte[] data;
/*     */ 
/*     */     DataFactory(long arg2, byte[] arg4)
/*     */     {
/*  65 */       this.format = ???;
/*     */       Object localObject;
/*  66 */       this.data = localObject;
/*     */     }
/*     */ 
/*     */     public Object getTransferData(DataFlavor paramDataFlavor) throws IOException {
/*  70 */       return DataTransferer.getInstance().translateBytes(this.data, paramDataFlavor, this.format, ClipboardTransferable.this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.datatransfer.ClipboardTransferable
 * JD-Core Version:    0.6.2
 */