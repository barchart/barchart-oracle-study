/*     */ package javax.accessibility;
/*     */ 
/*     */ import java.awt.IllegalComponentStateException;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public abstract class AccessibleContext
/*     */ {
/*     */   public static final String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";
/*     */   public static final String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";
/*     */   public static final String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";
/*     */   public static final String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";
/*     */   public static final String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";
/*     */   public static final String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";
/*     */   public static final String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";
/*     */   public static final String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";
/*     */   public static final String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";
/*     */   public static final String ACCESSIBLE_TABLE_CAPTION_CHANGED = "accessibleTableCaptionChanged";
/*     */   public static final String ACCESSIBLE_TABLE_SUMMARY_CHANGED = "accessibleTableSummaryChanged";
/*     */   public static final String ACCESSIBLE_TABLE_MODEL_CHANGED = "accessibleTableModelChanged";
/*     */   public static final String ACCESSIBLE_TABLE_ROW_HEADER_CHANGED = "accessibleTableRowHeaderChanged";
/*     */   public static final String ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED = "accessibleTableRowDescriptionChanged";
/*     */   public static final String ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED = "accessibleTableColumnHeaderChanged";
/*     */   public static final String ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED = "accessibleTableColumnDescriptionChanged";
/*     */   public static final String ACCESSIBLE_ACTION_PROPERTY = "accessibleActionProperty";
/*     */   public static final String ACCESSIBLE_HYPERTEXT_OFFSET = "AccessibleHypertextOffset";
/*     */   public static final String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";
/*     */   public static final String ACCESSIBLE_INVALIDATE_CHILDREN = "accessibleInvalidateChildren";
/*     */   public static final String ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED = "accessibleTextAttributesChanged";
/*     */   public static final String ACCESSIBLE_COMPONENT_BOUNDS_CHANGED = "accessibleComponentBoundsChanged";
/* 363 */   protected Accessible accessibleParent = null;
/*     */ 
/* 371 */   protected String accessibleName = null;
/*     */ 
/* 379 */   protected String accessibleDescription = null;
/*     */ 
/* 388 */   private PropertyChangeSupport accessibleChangeSupport = null;
/*     */ 
/* 394 */   private AccessibleRelationSet relationSet = new AccessibleRelationSet();
/*     */   private Object nativeAXResource;
/*     */ 
/*     */   public String getAccessibleName()
/*     */   {
/* 415 */     return this.accessibleName;
/*     */   }
/*     */ 
/*     */   public void setAccessibleName(String paramString)
/*     */   {
/* 433 */     String str = this.accessibleName;
/* 434 */     this.accessibleName = paramString;
/* 435 */     firePropertyChange("AccessibleName", str, this.accessibleName);
/*     */   }
/*     */ 
/*     */   public String getAccessibleDescription()
/*     */   {
/* 451 */     return this.accessibleDescription;
/*     */   }
/*     */ 
/*     */   public void setAccessibleDescription(String paramString)
/*     */   {
/* 469 */     String str = this.accessibleDescription;
/* 470 */     this.accessibleDescription = paramString;
/* 471 */     firePropertyChange("AccessibleDescription", str, this.accessibleDescription);
/*     */   }
/*     */ 
/*     */   public abstract AccessibleRole getAccessibleRole();
/*     */ 
/*     */   public abstract AccessibleStateSet getAccessibleStateSet();
/*     */ 
/*     */   public Accessible getAccessibleParent()
/*     */   {
/* 516 */     return this.accessibleParent;
/*     */   }
/*     */ 
/*     */   public void setAccessibleParent(Accessible paramAccessible)
/*     */   {
/* 528 */     this.accessibleParent = paramAccessible;
/*     */   }
/*     */ 
/*     */   public abstract int getAccessibleIndexInParent();
/*     */ 
/*     */   public abstract int getAccessibleChildrenCount();
/*     */ 
/*     */   public abstract Accessible getAccessibleChild(int paramInt);
/*     */ 
/*     */   public abstract Locale getLocale()
/*     */     throws IllegalComponentStateException;
/*     */ 
/*     */   public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 592 */     if (this.accessibleChangeSupport == null) {
/* 593 */       this.accessibleChangeSupport = new PropertyChangeSupport(this);
/*     */     }
/* 595 */     this.accessibleChangeSupport.addPropertyChangeListener(paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 606 */     if (this.accessibleChangeSupport != null)
/* 607 */       this.accessibleChangeSupport.removePropertyChangeListener(paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public AccessibleAction getAccessibleAction()
/*     */   {
/* 619 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleComponent getAccessibleComponent()
/*     */   {
/* 630 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleSelection getAccessibleSelection()
/*     */   {
/* 641 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleText getAccessibleText()
/*     */   {
/* 652 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleEditableText getAccessibleEditableText()
/*     */   {
/* 664 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleValue getAccessibleValue()
/*     */   {
/* 676 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleIcon[] getAccessibleIcon()
/*     */   {
/* 689 */     return null;
/*     */   }
/*     */ 
/*     */   public AccessibleRelationSet getAccessibleRelationSet()
/*     */   {
/* 701 */     return this.relationSet;
/*     */   }
/*     */ 
/*     */   public AccessibleTable getAccessibleTable()
/*     */   {
/* 713 */     return null;
/*     */   }
/*     */ 
/*     */   public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
/*     */   {
/* 740 */     if (this.accessibleChangeSupport != null)
/* 741 */       if ((paramObject2 instanceof PropertyChangeEvent)) {
/* 742 */         PropertyChangeEvent localPropertyChangeEvent = (PropertyChangeEvent)paramObject2;
/* 743 */         this.accessibleChangeSupport.firePropertyChange(localPropertyChangeEvent);
/*     */       } else {
/* 745 */         this.accessibleChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleContext
 * JD-Core Version:    0.6.2
 */