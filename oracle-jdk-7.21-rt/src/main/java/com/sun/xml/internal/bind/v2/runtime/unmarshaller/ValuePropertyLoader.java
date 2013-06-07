/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ import com.sun.xml.internal.bind.api.AccessorException;
/*    */ import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
/*    */ import javax.xml.bind.JAXBElement;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class ValuePropertyLoader extends Loader
/*    */ {
/*    */   private final TransducedAccessor xacc;
/*    */ 
/*    */   public ValuePropertyLoader(TransducedAccessor xacc)
/*    */   {
/* 46 */     super(true);
/* 47 */     this.xacc = xacc;
/*    */   }
/*    */ 
/*    */   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
/*    */     try {
/* 52 */       this.xacc.parse(state.target, text);
/*    */     } catch (AccessorException e) {
/* 54 */       handleGenericException(e, true);
/*    */     } catch (RuntimeException e) {
/* 56 */       if (state.prev != null) {
/* 57 */         if (!(state.prev.target instanceof JAXBElement))
/*    */         {
/* 62 */           handleParseConversionException(state, e);
/*    */         }
/*    */       }
/* 65 */       else handleParseConversionException(state, e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.ValuePropertyLoader
 * JD-Core Version:    0.6.2
 */