/*     */ package java.nio.channels.spi;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.IllegalBlockingModeException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ 
/*     */ public abstract class AbstractSelectableChannel extends SelectableChannel
/*     */ {
/*     */   private final SelectorProvider provider;
/*  61 */   private SelectionKey[] keys = null;
/*  62 */   private int keyCount = 0;
/*     */ 
/*  65 */   private final Object keyLock = new Object();
/*     */ 
/*  68 */   private final Object regLock = new Object();
/*     */ 
/*  71 */   boolean blocking = true;
/*     */ 
/*     */   protected AbstractSelectableChannel(SelectorProvider paramSelectorProvider)
/*     */   {
/*  77 */     this.provider = paramSelectorProvider;
/*     */   }
/*     */ 
/*     */   public final SelectorProvider provider()
/*     */   {
/*  86 */     return this.provider;
/*     */   }
/*     */ 
/*     */   private void addKey(SelectionKey paramSelectionKey)
/*     */   {
/*  93 */     synchronized (this.keyLock) {
/*  94 */       int i = 0;
/*  95 */       if ((this.keys != null) && (this.keyCount < this.keys.length))
/*     */       {
/*  97 */         for (i = 0; (i < this.keys.length) && 
/*  98 */           (this.keys[i] != null); ) { i++; continue;
/*     */ 
/* 100 */           if (this.keys == null) {
/* 101 */             this.keys = new SelectionKey[3];
/*     */           }
/*     */           else {
/* 104 */             int j = this.keys.length * 2;
/* 105 */             SelectionKey[] arrayOfSelectionKey = new SelectionKey[j];
/* 106 */             for (i = 0; i < this.keys.length; i++)
/* 107 */               arrayOfSelectionKey[i] = this.keys[i];
/* 108 */             this.keys = arrayOfSelectionKey;
/* 109 */             i = this.keyCount;
/*     */           } } 
/*     */       }
/* 111 */       this.keys[i] = paramSelectionKey;
/* 112 */       this.keyCount += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private SelectionKey findKey(Selector paramSelector) {
/* 117 */     synchronized (this.keyLock) {
/* 118 */       if (this.keys == null)
/* 119 */         return null;
/* 120 */       for (int i = 0; i < this.keys.length; i++)
/* 121 */         if ((this.keys[i] != null) && (this.keys[i].selector() == paramSelector))
/* 122 */           return this.keys[i];
/* 123 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeKey(SelectionKey paramSelectionKey) {
/* 128 */     synchronized (this.keyLock) {
/* 129 */       for (int i = 0; i < this.keys.length; i++)
/* 130 */         if (this.keys[i] == paramSelectionKey) {
/* 131 */           this.keys[i] = null;
/* 132 */           this.keyCount -= 1;
/*     */         }
/* 134 */       ((AbstractSelectionKey)paramSelectionKey).invalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean haveValidKeys() {
/* 139 */     synchronized (this.keyLock) {
/* 140 */       if (this.keyCount == 0)
/* 141 */         return false;
/* 142 */       for (int i = 0; i < this.keys.length; i++) {
/* 143 */         if ((this.keys[i] != null) && (this.keys[i].isValid()))
/* 144 */           return true;
/*     */       }
/* 146 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean isRegistered()
/*     */   {
/* 154 */     synchronized (this.keyLock) {
/* 155 */       return this.keyCount != 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final SelectionKey keyFor(Selector paramSelector) {
/* 160 */     return findKey(paramSelector);
/*     */   }
/*     */ 
/*     */   public final SelectionKey register(Selector paramSelector, int paramInt, Object paramObject)
/*     */     throws ClosedChannelException
/*     */   {
/* 193 */     if (!isOpen())
/* 194 */       throw new ClosedChannelException();
/* 195 */     if ((paramInt & (validOps() ^ 0xFFFFFFFF)) != 0)
/* 196 */       throw new IllegalArgumentException();
/* 197 */     synchronized (this.regLock) {
/* 198 */       if (this.blocking)
/* 199 */         throw new IllegalBlockingModeException();
/* 200 */       SelectionKey localSelectionKey = findKey(paramSelector);
/* 201 */       if (localSelectionKey != null) {
/* 202 */         localSelectionKey.interestOps(paramInt);
/* 203 */         localSelectionKey.attach(paramObject);
/*     */       }
/* 205 */       if (localSelectionKey == null)
/*     */       {
/* 207 */         localSelectionKey = ((AbstractSelector)paramSelector).register(this, paramInt, paramObject);
/* 208 */         addKey(localSelectionKey);
/*     */       }
/* 210 */       return localSelectionKey;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void implCloseChannel()
/*     */     throws IOException
/*     */   {
/* 228 */     implCloseSelectableChannel();
/* 229 */     synchronized (this.keyLock) {
/* 230 */       int i = this.keys == null ? 0 : this.keys.length;
/* 231 */       for (int j = 0; j < i; j++) {
/* 232 */         SelectionKey localSelectionKey = this.keys[j];
/* 233 */         if (localSelectionKey != null)
/* 234 */           localSelectionKey.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void implCloseSelectableChannel()
/*     */     throws IOException;
/*     */ 
/*     */   public final boolean isBlocking()
/*     */   {
/* 258 */     synchronized (this.regLock) {
/* 259 */       return this.blocking;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Object blockingLock() {
/* 264 */     return this.regLock;
/*     */   }
/*     */ 
/*     */   public final SelectableChannel configureBlocking(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 278 */     if (!isOpen())
/* 279 */       throw new ClosedChannelException();
/* 280 */     synchronized (this.regLock) {
/* 281 */       if (this.blocking == paramBoolean)
/* 282 */         return this;
/* 283 */       if ((paramBoolean) && (haveValidKeys()))
/* 284 */         throw new IllegalBlockingModeException();
/* 285 */       implConfigureBlocking(paramBoolean);
/* 286 */       this.blocking = paramBoolean;
/*     */     }
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   protected abstract void implConfigureBlocking(boolean paramBoolean)
/*     */     throws IOException;
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.spi.AbstractSelectableChannel
 * JD-Core Version:    0.6.2
 */