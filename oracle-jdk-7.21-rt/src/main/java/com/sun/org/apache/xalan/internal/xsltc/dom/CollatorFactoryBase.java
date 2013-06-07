/*    */ package com.sun.org.apache.xalan.internal.xsltc.dom;
/*    */ 
/*    */ import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
/*    */ import java.text.Collator;
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class CollatorFactoryBase
/*    */   implements CollatorFactory
/*    */ {
/* 36 */   public static final Locale DEFAULT_LOCALE = Locale.getDefault();
/* 37 */   public static final Collator DEFAULT_COLLATOR = Collator.getInstance();
/*    */ 
/*    */   public Collator getCollator(String lang, String country)
/*    */   {
/* 43 */     return Collator.getInstance(new Locale(lang, country));
/*    */   }
/*    */ 
/*    */   public Collator getCollator(Locale locale) {
/* 47 */     if (locale == DEFAULT_LOCALE) {
/* 48 */       return DEFAULT_COLLATOR;
/*    */     }
/* 50 */     return Collator.getInstance(locale);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.dom.CollatorFactoryBase
 * JD-Core Version:    0.6.2
 */