/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public final class DefaultValueLoaderDecorator extends Loader
/*    */ {
/*    */   private final Loader l;
/*    */   private final String defaultValue;
/*    */ 
/*    */   public DefaultValueLoaderDecorator(Loader l, String defaultValue)
/*    */   {
/* 40 */     this.l = l;
/* 41 */     this.defaultValue = defaultValue;
/*    */   }
/*    */ 
/*    */   public void startElement(UnmarshallingContext.State state, TagName ea)
/*    */     throws SAXException
/*    */   {
/* 47 */     if (state.elementDefaultValue == null) {
/* 48 */       state.elementDefaultValue = this.defaultValue;
/*    */     }
/* 50 */     state.loader = this.l;
/* 51 */     this.l.startElement(state, ea);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator
 * JD-Core Version:    0.6.2
 */