/*      */ package java.net;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectInputStream.GetField;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectOutputStream.PutField;
/*      */ import java.io.ObjectStreamField;
/*      */ import java.io.Serializable;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.List;
/*      */ import java.util.Vector;
/*      */ 
/*      */ final class SocketPermissionCollection extends PermissionCollection
/*      */   implements Serializable
/*      */ {
/*      */   private transient List perms;
/*      */   private static final long serialVersionUID = 2787186408602843674L;
/* 1335 */   private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
/*      */ 
/*      */   public SocketPermissionCollection()
/*      */   {
/* 1236 */     this.perms = new ArrayList();
/*      */   }
/*      */ 
/*      */   public void add(Permission paramPermission)
/*      */   {
/* 1254 */     if (!(paramPermission instanceof SocketPermission)) {
/* 1255 */       throw new IllegalArgumentException("invalid permission: " + paramPermission);
/*      */     }
/* 1257 */     if (isReadOnly()) {
/* 1258 */       throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
/*      */     }
/*      */ 
/* 1263 */     synchronized (this) {
/* 1264 */       this.perms.add(0, paramPermission);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean implies(Permission paramPermission)
/*      */   {
/* 1280 */     if (!(paramPermission instanceof SocketPermission)) {
/* 1281 */       return false;
/*      */     }
/* 1283 */     SocketPermission localSocketPermission1 = (SocketPermission)paramPermission;
/*      */ 
/* 1285 */     int i = localSocketPermission1.getMask();
/* 1286 */     int j = 0;
/* 1287 */     int k = i;
/*      */ 
/* 1289 */     synchronized (this) {
/* 1290 */       int m = this.perms.size();
/*      */ 
/* 1292 */       for (int n = 0; n < m; n++) {
/* 1293 */         SocketPermission localSocketPermission2 = (SocketPermission)this.perms.get(n);
/*      */ 
/* 1295 */         if (((k & localSocketPermission2.getMask()) != 0) && (localSocketPermission2.impliesIgnoreMask(localSocketPermission1))) {
/* 1296 */           j |= localSocketPermission2.getMask();
/* 1297 */           if ((j & i) == i)
/* 1298 */             return true;
/* 1299 */           k = i ^ j;
/*      */         }
/*      */       }
/*      */     }
/* 1303 */     return false;
/*      */   }
/*      */ 
/*      */   public Enumeration elements()
/*      */   {
/* 1315 */     synchronized (this) {
/* 1316 */       return Collections.enumeration(this.perms);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 1350 */     Vector localVector = new Vector(this.perms.size());
/*      */ 
/* 1352 */     synchronized (this) {
/* 1353 */       localVector.addAll(this.perms);
/*      */     }
/*      */ 
/* 1356 */     ??? = paramObjectOutputStream.putFields();
/* 1357 */     ((ObjectOutputStream.PutField)???).put("permissions", localVector);
/* 1358 */     paramObjectOutputStream.writeFields();
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1369 */     ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
/*      */ 
/* 1372 */     Vector localVector = (Vector)localGetField.get("permissions", null);
/* 1373 */     this.perms = new ArrayList(localVector.size());
/* 1374 */     this.perms.addAll(localVector);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocketPermissionCollection
 * JD-Core Version:    0.6.2
 */