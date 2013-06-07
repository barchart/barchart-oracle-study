/*      */ package com.sun.imageio.plugins.jpeg;
/*      */ 
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ 
/*      */ class ImageTypeIterator
/*      */   implements Iterator<ImageTypeSpecifier>
/*      */ {
/*      */   private Iterator<ImageTypeProducer> producers;
/* 1691 */   private ImageTypeSpecifier theNext = null;
/*      */ 
/*      */   public ImageTypeIterator(Iterator<ImageTypeProducer> paramIterator) {
/* 1694 */     this.producers = paramIterator;
/*      */   }
/*      */ 
/*      */   public boolean hasNext() {
/* 1698 */     if (this.theNext != null) {
/* 1699 */       return true;
/*      */     }
/* 1701 */     if (!this.producers.hasNext()) {
/* 1702 */       return false;
/*      */     }
/*      */     do
/* 1705 */       this.theNext = ((ImageTypeProducer)this.producers.next()).getType();
/* 1706 */     while ((this.theNext == null) && (this.producers.hasNext()));
/*      */ 
/* 1708 */     return this.theNext != null;
/*      */   }
/*      */ 
/*      */   public ImageTypeSpecifier next() {
/* 1712 */     if ((this.theNext != null) || (hasNext())) {
/* 1713 */       ImageTypeSpecifier localImageTypeSpecifier = this.theNext;
/* 1714 */       this.theNext = null;
/* 1715 */       return localImageTypeSpecifier;
/*      */     }
/* 1717 */     throw new NoSuchElementException();
/*      */   }
/*      */ 
/*      */   public void remove()
/*      */   {
/* 1722 */     this.producers.remove();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.imageio.plugins.jpeg.ImageTypeIterator
 * JD-Core Version:    0.6.2
 */