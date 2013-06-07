/*     */ package sun.java2d;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class Disposer
/*     */   implements Runnable
/*     */ {
/*  50 */   private static final ReferenceQueue queue = new ReferenceQueue();
/*  51 */   private static final Hashtable records = new Hashtable();
/*     */   private static Disposer disposerInstance;
/*     */   public static final int WEAK = 0;
/*     */   public static final int PHANTOM = 1;
/*  56 */   public static int refType = 1;
/*     */ 
/* 168 */   private static ArrayList<DisposerRecord> deferredRecords = null;
/*     */ 
/* 189 */   public static volatile boolean pollingQueue = false;
/*     */ 
/*     */   public static void addRecord(Object paramObject, long paramLong1, long paramLong2)
/*     */   {
/* 107 */     disposerInstance.add(paramObject, new DefaultDisposerRecord(paramLong1, paramLong2));
/*     */   }
/*     */ 
/*     */   public static void addRecord(Object paramObject, DisposerRecord paramDisposerRecord)
/*     */   {
/* 118 */     disposerInstance.add(paramObject, paramDisposerRecord);
/*     */   }
/*     */ 
/*     */   synchronized void add(Object paramObject, DisposerRecord paramDisposerRecord)
/*     */   {
/* 130 */     if ((paramObject instanceof DisposerTarget))
/* 131 */       paramObject = ((DisposerTarget)paramObject).getDisposerReferent();
/*     */     Object localObject;
/* 134 */     if (refType == 1)
/* 135 */       localObject = new PhantomReference(paramObject, queue);
/*     */     else {
/* 137 */       localObject = new WeakReference(paramObject, queue);
/*     */     }
/* 139 */     records.put(localObject, paramDisposerRecord);
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     while (true)
/*     */       try {
/* 145 */         Reference localReference = queue.remove();
/* 146 */         ((Reference)localReference).clear();
/* 147 */         DisposerRecord localDisposerRecord = (DisposerRecord)records.remove(localReference);
/* 148 */         localDisposerRecord.dispose();
/* 149 */         localReference = null;
/* 150 */         localDisposerRecord = null;
/* 151 */         clearDeferredRecords();
/*     */       } catch (Exception localException) {
/* 153 */         System.out.println("Exception while removing reference: " + localException);
/* 154 */         localException.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void clearDeferredRecords()
/*     */   {
/* 171 */     if ((deferredRecords == null) || (deferredRecords.isEmpty())) {
/* 172 */       return;
/*     */     }
/* 174 */     for (int i = 0; i < deferredRecords.size(); i++) {
/*     */       try {
/* 176 */         DisposerRecord localDisposerRecord = (DisposerRecord)deferredRecords.get(i);
/* 177 */         localDisposerRecord.dispose();
/*     */       } catch (Exception localException) {
/* 179 */         System.out.println("Exception while disposing deferred rec.");
/* 180 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 183 */     deferredRecords.clear();
/*     */   }
/*     */ 
/*     */   public static void pollRemove()
/*     */   {
/* 202 */     if (pollingQueue) {
/* 203 */       return;
/*     */     }
/*     */ 
/* 206 */     pollingQueue = true;
/* 207 */     int i = 0;
/* 208 */     int j = 0;
/*     */     try
/*     */     {
/*     */       Reference localReference;
/* 211 */       while (((localReference = queue.poll()) != null) && (i < 10000) && (j < 100)) {
/* 212 */         i++;
/* 213 */         ((Reference)localReference).clear();
/* 214 */         DisposerRecord localDisposerRecord = (DisposerRecord)records.remove(localReference);
/* 215 */         if ((localDisposerRecord instanceof PollDisposable)) {
/* 216 */           localDisposerRecord.dispose();
/* 217 */           localReference = null;
/* 218 */           localDisposerRecord = null;
/*     */         }
/* 220 */         else if (localDisposerRecord != null)
/*     */         {
/* 223 */           j++;
/* 224 */           if (deferredRecords == null) {
/* 225 */             deferredRecords = new ArrayList(5);
/*     */           }
/* 227 */           deferredRecords.add(localDisposerRecord);
/*     */         }
/*     */       }
/*     */     } catch (Exception localException) {
/* 231 */       System.out.println("Exception while removing reference: " + localException);
/* 232 */       localException.printStackTrace();
/*     */     } finally {
/* 234 */       pollingQueue = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   public static void addReference(Reference paramReference, DisposerRecord paramDisposerRecord)
/*     */   {
/* 249 */     records.put(paramReference, paramDisposerRecord);
/*     */   }
/*     */ 
/*     */   public static void addObjectRecord(Object paramObject, DisposerRecord paramDisposerRecord) {
/* 253 */     records.put(new WeakReference(paramObject, queue), paramDisposerRecord);
/*     */   }
/*     */ 
/*     */   public static ReferenceQueue getQueue()
/*     */   {
/* 259 */     return queue;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  59 */     AccessController.doPrivileged(new LoadLibraryAction("awt"));
/*     */ 
/*  61 */     initIDs();
/*  62 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.reftype"));
/*     */ 
/*  64 */     if (str != null) {
/*  65 */       if (str.equals("weak")) {
/*  66 */         refType = 0;
/*  67 */         System.err.println("Using WEAK refs");
/*     */       } else {
/*  69 */         refType = 1;
/*  70 */         System.err.println("Using PHANTOM refs");
/*     */       }
/*     */     }
/*  73 */     disposerInstance = new Disposer();
/*  74 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  81 */         Object localObject1 = Thread.currentThread().getThreadGroup();
/*  82 */         for (Object localObject2 = localObject1; 
/*  83 */           localObject2 != null; 
/*  84 */           localObject2 = ((ThreadGroup)localObject1).getParent()) localObject1 = localObject2;
/*  85 */         localObject2 = new Thread((ThreadGroup)localObject1, Disposer.disposerInstance, "Java2D Disposer");
/*     */ 
/*  87 */         ((Thread)localObject2).setContextClassLoader(null);
/*  88 */         ((Thread)localObject2).setDaemon(true);
/*  89 */         ((Thread)localObject2).setPriority(10);
/*  90 */         ((Thread)localObject2).start();
/*  91 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static abstract interface PollDisposable
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.Disposer
 * JD-Core Version:    0.6.2
 */