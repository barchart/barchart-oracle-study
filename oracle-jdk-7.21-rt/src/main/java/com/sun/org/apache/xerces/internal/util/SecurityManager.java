/*     */ package com.sun.org.apache.xerces.internal.util;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public final class SecurityManager
/*     */ {
/*     */   private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;
/*     */   private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;
/*     */   private static final int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;
/*     */   private int entityExpansionLimit;
/*     */   private int maxOccurLimit;
/*     */   private int fElementAttributeLimit;
/*     */ 
/*     */   public SecurityManager()
/*     */   {
/* 116 */     this.entityExpansionLimit = 64000;
/* 117 */     this.maxOccurLimit = 5000;
/* 118 */     this.fElementAttributeLimit = 10000;
/*     */ 
/* 121 */     readSystemProperties();
/*     */   }
/*     */ 
/*     */   public void setEntityExpansionLimit(int limit)
/*     */   {
/* 132 */     this.entityExpansionLimit = limit;
/*     */   }
/*     */ 
/*     */   public int getEntityExpansionLimit()
/*     */   {
/* 143 */     return this.entityExpansionLimit;
/*     */   }
/*     */ 
/*     */   public void setMaxOccurNodeLimit(int limit)
/*     */   {
/* 156 */     this.maxOccurLimit = limit;
/*     */   }
/*     */ 
/*     */   public int getMaxOccurNodeLimit()
/*     */   {
/* 169 */     return this.maxOccurLimit;
/*     */   }
/*     */ 
/*     */   public int getElementAttrLimit() {
/* 173 */     return this.fElementAttributeLimit;
/*     */   }
/*     */ 
/*     */   public void setElementAttrLimit(int limit) {
/* 177 */     this.fElementAttributeLimit = limit;
/*     */   }
/*     */ 
/*     */   private void readSystemProperties()
/*     */   {
/*     */     try
/*     */     {
/* 184 */       String value = getSystemProperty("entityExpansionLimit");
/* 185 */       if ((value != null) && (!value.equals(""))) {
/* 186 */         this.entityExpansionLimit = Integer.parseInt(value);
/* 187 */         if (this.entityExpansionLimit < 0)
/* 188 */           this.entityExpansionLimit = 64000;
/*     */       }
/*     */       else {
/* 191 */         this.entityExpansionLimit = 64000;
/*     */       }
/*     */     } catch (Exception ex) {
/*     */     }
/*     */     try { String value = getSystemProperty("maxOccurLimit");
/* 196 */       if ((value != null) && (!value.equals(""))) {
/* 197 */         this.maxOccurLimit = Integer.parseInt(value);
/* 198 */         if (this.maxOccurLimit < 0)
/* 199 */           this.maxOccurLimit = 5000;
/*     */       }
/*     */       else {
/* 202 */         this.maxOccurLimit = 5000;
/*     */       } } catch (Exception ex) {
/*     */     }
/*     */     try {
/* 206 */       String value = getSystemProperty("elementAttributeLimit");
/* 207 */       if ((value != null) && (!value.equals(""))) {
/* 208 */         this.fElementAttributeLimit = Integer.parseInt(value);
/* 209 */         if (this.fElementAttributeLimit < 0)
/* 210 */           this.fElementAttributeLimit = 10000;
/*     */       }
/*     */       else {
/* 213 */         this.fElementAttributeLimit = 10000;
/*     */       }
/*     */     } catch (Exception ex) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getSystemProperty(final String propName) {
/* 220 */     return (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public String run() {
/* 222 */         return System.getProperty(propName);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.util.SecurityManager
 * JD-Core Version:    0.6.2
 */