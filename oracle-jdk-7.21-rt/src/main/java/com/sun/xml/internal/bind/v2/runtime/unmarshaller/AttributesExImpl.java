/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ import com.sun.xml.internal.bind.util.AttributesImpl;
/*    */ 
/*    */ public final class AttributesExImpl extends AttributesImpl
/*    */   implements AttributesEx
/*    */ {
/*    */   public CharSequence getData(int idx)
/*    */   {
/* 39 */     return getValue(idx);
/*    */   }
/*    */ 
/*    */   public CharSequence getData(String nsUri, String localName) {
/* 43 */     return getValue(nsUri, localName);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.AttributesExImpl
 * JD-Core Version:    0.6.2
 */