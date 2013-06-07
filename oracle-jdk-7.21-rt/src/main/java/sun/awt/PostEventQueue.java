/*      */ package sun.awt;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.EventQueue;
/*      */ 
/*      */ class PostEventQueue
/*      */ {
/* 2031 */   private EventQueueItem queueHead = null;
/* 2032 */   private EventQueueItem queueTail = null;
/*      */   private final EventQueue eventQueue;
/* 2036 */   private volatile boolean isFlushing = false;
/*      */ 
/*      */   PostEventQueue(EventQueue paramEventQueue) {
/* 2039 */     this.eventQueue = paramEventQueue;
/*      */   }
/*      */ 
/*      */   public synchronized boolean noEvents() {
/* 2043 */     return (this.queueHead == null) && (!this.isFlushing);
/*      */   }
/*      */ 
/*      */   public void flush()
/*      */   {
/*      */     EventQueueItem localEventQueueItem;
/* 2057 */     synchronized (this) {
/* 2058 */       localEventQueueItem = this.queueHead;
/* 2059 */       this.queueHead = (this.queueTail = null);
/* 2060 */       this.isFlushing = (localEventQueueItem != null);
/*      */     }
/*      */     try {
/* 2063 */       while (localEventQueueItem != null) {
/* 2064 */         this.eventQueue.postEvent(localEventQueueItem.event);
/* 2065 */         localEventQueueItem = localEventQueueItem.next;
/*      */       }
/*      */     }
/*      */     finally {
/* 2069 */       this.isFlushing = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   void postEvent(AWTEvent paramAWTEvent)
/*      */   {
/* 2077 */     EventQueueItem localEventQueueItem = new EventQueueItem(paramAWTEvent);
/*      */ 
/* 2079 */     synchronized (this) {
/* 2080 */       if (this.queueHead == null) {
/* 2081 */         this.queueHead = (this.queueTail = localEventQueueItem);
/*      */       } else {
/* 2083 */         this.queueTail.next = localEventQueueItem;
/* 2084 */         this.queueTail = localEventQueueItem;
/*      */       }
/*      */     }
/* 2087 */     SunToolkit.wakeupEventQueue(this.eventQueue, paramAWTEvent.getSource() == AWTAutoShutdown.getInstance());
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.PostEventQueue
 * JD-Core Version:    0.6.2
 */