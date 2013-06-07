/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ import com.sun.xml.internal.bind.api.AccessorException;
/*    */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class TextLoader extends Loader
/*    */ {
/*    */   private final Transducer xducer;
/*    */ 
/*    */   public TextLoader(Transducer xducer)
/*    */   {
/* 48 */     super(true);
/* 49 */     this.xducer = xducer;
/*    */   }
/*    */ 
/*    */   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
/*    */     try {
/* 54 */       state.target = this.xducer.parse(text);
/*    */     } catch (AccessorException e) {
/* 56 */       handleGenericException(e, true);
/*    */     } catch (RuntimeException e) {
/* 58 */       handleParseConversionException(state, e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader
 * JD-Core Version:    0.6.2
 */