/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.UID;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.server.MarshalInputStream;
/*     */ 
/*     */ class ConnectionInputStream extends MarshalInputStream
/*     */ {
/*  43 */   private boolean dgcAckNeeded = false;
/*     */ 
/*  46 */   private Map incomingRefTable = new HashMap(5);
/*     */   private UID ackID;
/*     */ 
/*     */   ConnectionInputStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  56 */     super(paramInputStream);
/*     */   }
/*     */ 
/*     */   void readID() throws IOException {
/*  60 */     this.ackID = UID.read(this);
/*     */   }
/*     */ 
/*     */   void saveRef(LiveRef paramLiveRef)
/*     */   {
/*  70 */     Endpoint localEndpoint = paramLiveRef.getEndpoint();
/*     */ 
/*  73 */     Object localObject = (List)this.incomingRefTable.get(localEndpoint);
/*     */ 
/*  75 */     if (localObject == null) {
/*  76 */       localObject = new ArrayList();
/*  77 */       this.incomingRefTable.put(localEndpoint, localObject);
/*     */     }
/*     */ 
/*  81 */     ((List)localObject).add(paramLiveRef);
/*     */   }
/*     */ 
/*     */   void registerRefs()
/*     */     throws IOException
/*     */   {
/*  91 */     if (!this.incomingRefTable.isEmpty()) {
/*  92 */       Set localSet = this.incomingRefTable.entrySet();
/*  93 */       Iterator localIterator = localSet.iterator();
/*  94 */       while (localIterator.hasNext()) {
/*  95 */         Map.Entry localEntry = (Map.Entry)localIterator.next();
/*  96 */         Endpoint localEndpoint = (Endpoint)localEntry.getKey();
/*  97 */         List localList = (List)localEntry.getValue();
/*  98 */         DGCClient.registerRefs(localEndpoint, localList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void setAckNeeded()
/*     */   {
/* 108 */     this.dgcAckNeeded = true;
/*     */   }
/*     */ 
/*     */   void done(Connection paramConnection)
/*     */   {
/* 121 */     if (this.dgcAckNeeded) {
/* 122 */       Connection localConnection = null;
/* 123 */       Channel localChannel = null;
/* 124 */       boolean bool = true;
/*     */ 
/* 126 */       DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");
/*     */       try
/*     */       {
/* 129 */         localChannel = paramConnection.getChannel();
/* 130 */         localConnection = localChannel.newConnection();
/* 131 */         DataOutputStream localDataOutputStream = new DataOutputStream(localConnection.getOutputStream());
/*     */ 
/* 133 */         localDataOutputStream.writeByte(84);
/* 134 */         if (this.ackID == null) {
/* 135 */           this.ackID = new UID();
/*     */         }
/* 137 */         this.ackID.write(localDataOutputStream);
/* 138 */         localConnection.releaseOutputStream();
/*     */ 
/* 147 */         localConnection.getInputStream().available();
/* 148 */         localConnection.releaseInputStream();
/*     */       } catch (RemoteException localRemoteException1) {
/* 150 */         bool = false;
/*     */       } catch (IOException localIOException) {
/* 152 */         bool = false;
/*     */       }
/*     */       try {
/* 155 */         if (localConnection != null)
/* 156 */           localChannel.free(localConnection, bool);
/*     */       }
/*     */       catch (RemoteException localRemoteException2)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.ConnectionInputStream
 * JD-Core Version:    0.6.2
 */