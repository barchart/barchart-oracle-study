/*     */ package com.sun.org.apache.xpath.internal.objects;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.utils.XMLString;
/*     */ 
/*     */ class GreaterThanComparator extends Comparator
/*     */ {
/*     */   boolean compareStrings(XMLString s1, XMLString s2)
/*     */   {
/* 848 */     return s1.toDouble() > s2.toDouble();
/*     */   }
/*     */ 
/*     */   boolean compareNumbers(double n1, double n2)
/*     */   {
/* 863 */     return n1 > n2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.objects.GreaterThanComparator
 * JD-Core Version:    0.6.2
 */