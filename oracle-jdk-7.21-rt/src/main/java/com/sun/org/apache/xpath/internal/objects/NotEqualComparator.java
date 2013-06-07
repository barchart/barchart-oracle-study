/*     */ package com.sun.org.apache.xpath.internal.objects;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.utils.XMLString;
/*     */ 
/*     */ class NotEqualComparator extends Comparator
/*     */ {
/*     */   boolean compareStrings(XMLString s1, XMLString s2)
/*     */   {
/* 955 */     return !s1.equals(s2);
/*     */   }
/*     */ 
/*     */   boolean compareNumbers(double n1, double n2)
/*     */   {
/* 969 */     return n1 != n2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.objects.NotEqualComparator
 * JD-Core Version:    0.6.2
 */