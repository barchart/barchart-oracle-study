/*     */ package java.beans;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ final class java_awt_Font_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 738 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 742 */     Font localFont = (Font)paramObject;
/*     */ 
/* 744 */     int i = 0;
/* 745 */     String str = null;
/* 746 */     int j = 0;
/* 747 */     int k = 12;
/*     */ 
/* 749 */     Map localMap = localFont.getAttributes();
/* 750 */     HashMap localHashMap = new HashMap(localMap.size());
/* 751 */     for (Object localObject1 = localMap.keySet().iterator(); ((Iterator)localObject1).hasNext(); ) { Object localObject2 = ((Iterator)localObject1).next();
/* 752 */       Object localObject3 = localMap.get(localObject2);
/* 753 */       if (localObject3 != null) {
/* 754 */         localHashMap.put(localObject2, localObject3);
/*     */       }
/* 756 */       if (localObject2 == TextAttribute.FAMILY) {
/* 757 */         if ((localObject3 instanceof String)) {
/* 758 */           i++;
/* 759 */           str = (String)localObject3;
/*     */         }
/*     */       }
/* 762 */       else if (localObject2 == TextAttribute.WEIGHT) {
/* 763 */         if (TextAttribute.WEIGHT_REGULAR.equals(localObject3)) {
/* 764 */           i++;
/* 765 */         } else if (TextAttribute.WEIGHT_BOLD.equals(localObject3)) {
/* 766 */           i++;
/* 767 */           j |= 1;
/*     */         }
/*     */       }
/* 770 */       else if (localObject2 == TextAttribute.POSTURE) {
/* 771 */         if (TextAttribute.POSTURE_REGULAR.equals(localObject3)) {
/* 772 */           i++;
/* 773 */         } else if (TextAttribute.POSTURE_OBLIQUE.equals(localObject3)) {
/* 774 */           i++;
/* 775 */           j |= 2;
/*     */         }
/* 777 */       } else if ((localObject2 == TextAttribute.SIZE) && 
/* 778 */         ((localObject3 instanceof Number))) {
/* 779 */         Number localNumber = (Number)localObject3;
/* 780 */         k = localNumber.intValue();
/* 781 */         if (k == localNumber.floatValue()) {
/* 782 */           i++;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 787 */     localObject1 = localFont.getClass();
/* 788 */     if (i == localHashMap.size()) {
/* 789 */       return new Expression(localFont, localObject1, "new", new Object[] { str, Integer.valueOf(j), Integer.valueOf(k) });
/*     */     }
/* 791 */     if (localObject1 == Font.class) {
/* 792 */       return new Expression(localFont, localObject1, "getFont", new Object[] { localHashMap });
/*     */     }
/* 794 */     return new Expression(localFont, localObject1, "new", new Object[] { Font.getFont(localHashMap) });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_awt_Font_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */