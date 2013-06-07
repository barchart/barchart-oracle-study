/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.MouseDragGestureRecognizer;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import sun.awt.dnd.SunDragSourceContextPeer;
/*     */ 
/*     */ class XMouseDragGestureRecognizer extends MouseDragGestureRecognizer
/*     */ {
/*     */   private static final long serialVersionUID = -841711780352520383L;
/*     */   protected static int motionThreshold;
/*     */   protected static final int ButtonMask = 7168;
/*     */ 
/*     */   protected XMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/*  86 */     super(paramDragSource, paramComponent, paramInt, paramDragGestureListener);
/*     */   }
/*     */ 
/*     */   protected XMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt)
/*     */   {
/*  98 */     this(paramDragSource, paramComponent, paramInt, null);
/*     */   }
/*     */ 
/*     */   protected XMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent)
/*     */   {
/* 109 */     this(paramDragSource, paramComponent, 0);
/*     */   }
/*     */ 
/*     */   protected XMouseDragGestureRecognizer(DragSource paramDragSource)
/*     */   {
/* 119 */     this(paramDragSource, null);
/*     */   }
/*     */ 
/*     */   protected int mapDragOperationFromModifiers(MouseEvent paramMouseEvent)
/*     */   {
/* 127 */     int i = paramMouseEvent.getModifiersEx();
/* 128 */     int j = i & 0x1C00;
/*     */ 
/* 132 */     if ((j != 1024) && (j != 2048))
/*     */     {
/* 134 */       return 0;
/*     */     }
/*     */ 
/* 137 */     return SunDragSourceContextPeer.convertModifiersToDropAction(i, getSourceActions());
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent paramMouseEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent paramMouseEvent)
/*     */   {
/* 155 */     this.events.clear();
/*     */ 
/* 157 */     if (mapDragOperationFromModifiers(paramMouseEvent) != 0) {
/*     */       try {
/* 159 */         motionThreshold = DragSource.getDragThreshold();
/*     */       } catch (Exception localException) {
/* 161 */         motionThreshold = 5;
/*     */       }
/* 163 */       appendEvent(paramMouseEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent paramMouseEvent)
/*     */   {
/* 172 */     this.events.clear();
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent paramMouseEvent)
/*     */   {
/* 180 */     this.events.clear();
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent paramMouseEvent)
/*     */   {
/* 188 */     if (!this.events.isEmpty()) {
/* 189 */       int i = mapDragOperationFromModifiers(paramMouseEvent);
/*     */ 
/* 191 */       if (i == 0)
/* 192 */         this.events.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent paramMouseEvent)
/*     */   {
/* 202 */     if (!this.events.isEmpty()) {
/* 203 */       int i = mapDragOperationFromModifiers(paramMouseEvent);
/*     */ 
/* 206 */       if (i == 0) {
/* 207 */         return;
/*     */       }
/*     */ 
/* 210 */       MouseEvent localMouseEvent = (MouseEvent)this.events.get(0);
/*     */ 
/* 212 */       Point localPoint1 = localMouseEvent.getPoint();
/* 213 */       Point localPoint2 = paramMouseEvent.getPoint();
/*     */ 
/* 215 */       int j = Math.abs(localPoint1.x - localPoint2.x);
/* 216 */       int k = Math.abs(localPoint1.y - localPoint2.y);
/*     */ 
/* 218 */       if ((j > motionThreshold) || (k > motionThreshold))
/* 219 */         fireDragGestureRecognized(i, ((MouseEvent)getTriggerEvent()).getPoint());
/*     */       else
/* 221 */         appendEvent(paramMouseEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent paramMouseEvent)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMouseDragGestureRecognizer
 * JD-Core Version:    0.6.2
 */