/*     */ package javax.swing;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class DefaultComboBoxModel<E> extends AbstractListModel<E>
/*     */   implements MutableComboBoxModel<E>, Serializable
/*     */ {
/*     */   Vector<E> objects;
/*     */   Object selectedObject;
/*     */ 
/*     */   public DefaultComboBoxModel()
/*     */   {
/*  48 */     this.objects = new Vector();
/*     */   }
/*     */ 
/*     */   public DefaultComboBoxModel(E[] paramArrayOfE)
/*     */   {
/*  58 */     this.objects = new Vector();
/*  59 */     this.objects.ensureCapacity(paramArrayOfE.length);
/*     */ 
/*  62 */     int i = 0; for (int j = paramArrayOfE.length; i < j; i++) {
/*  63 */       this.objects.addElement(paramArrayOfE[i]);
/*     */     }
/*  65 */     if (getSize() > 0)
/*  66 */       this.selectedObject = getElementAt(0);
/*     */   }
/*     */ 
/*     */   public DefaultComboBoxModel(Vector<E> paramVector)
/*     */   {
/*  77 */     this.objects = paramVector;
/*     */ 
/*  79 */     if (getSize() > 0)
/*  80 */       this.selectedObject = getElementAt(0);
/*     */   }
/*     */ 
/*     */   public void setSelectedItem(Object paramObject)
/*     */   {
/*  91 */     if (((this.selectedObject != null) && (!this.selectedObject.equals(paramObject))) || ((this.selectedObject == null) && (paramObject != null)))
/*     */     {
/*  93 */       this.selectedObject = paramObject;
/*  94 */       fireContentsChanged(this, -1, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getSelectedItem()
/*     */   {
/* 100 */     return this.selectedObject;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 105 */     return this.objects.size();
/*     */   }
/*     */ 
/*     */   public E getElementAt(int paramInt)
/*     */   {
/* 110 */     if ((paramInt >= 0) && (paramInt < this.objects.size())) {
/* 111 */       return this.objects.elementAt(paramInt);
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public int getIndexOf(Object paramObject)
/*     */   {
/* 124 */     return this.objects.indexOf(paramObject);
/*     */   }
/*     */ 
/*     */   public void addElement(E paramE)
/*     */   {
/* 129 */     this.objects.addElement(paramE);
/* 130 */     fireIntervalAdded(this, this.objects.size() - 1, this.objects.size() - 1);
/* 131 */     if ((this.objects.size() == 1) && (this.selectedObject == null) && (paramE != null))
/* 132 */       setSelectedItem(paramE);
/*     */   }
/*     */ 
/*     */   public void insertElementAt(E paramE, int paramInt)
/*     */   {
/* 138 */     this.objects.insertElementAt(paramE, paramInt);
/* 139 */     fireIntervalAdded(this, paramInt, paramInt);
/*     */   }
/*     */ 
/*     */   public void removeElementAt(int paramInt)
/*     */   {
/* 144 */     if (getElementAt(paramInt) == this.selectedObject) {
/* 145 */       if (paramInt == 0) {
/* 146 */         setSelectedItem(getSize() == 1 ? null : getElementAt(paramInt + 1));
/*     */       }
/*     */       else {
/* 149 */         setSelectedItem(getElementAt(paramInt - 1));
/*     */       }
/*     */     }
/*     */ 
/* 153 */     this.objects.removeElementAt(paramInt);
/*     */ 
/* 155 */     fireIntervalRemoved(this, paramInt, paramInt);
/*     */   }
/*     */ 
/*     */   public void removeElement(Object paramObject)
/*     */   {
/* 160 */     int i = this.objects.indexOf(paramObject);
/* 161 */     if (i != -1)
/* 162 */       removeElementAt(i);
/*     */   }
/*     */ 
/*     */   public void removeAllElements()
/*     */   {
/* 170 */     if (this.objects.size() > 0) {
/* 171 */       int i = 0;
/* 172 */       int j = this.objects.size() - 1;
/* 173 */       this.objects.removeAllElements();
/* 174 */       this.selectedObject = null;
/* 175 */       fireIntervalRemoved(this, i, j);
/*     */     } else {
/* 177 */       this.selectedObject = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.DefaultComboBoxModel
 * JD-Core Version:    0.6.2
 */