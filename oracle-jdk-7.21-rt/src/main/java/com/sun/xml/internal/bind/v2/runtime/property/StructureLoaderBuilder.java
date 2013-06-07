/*    */ package com.sun.xml.internal.bind.v2.runtime.property;
/*    */ 
/*    */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
/*    */ import com.sun.xml.internal.bind.v2.util.QNameMap;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public abstract interface StructureLoaderBuilder
/*    */ {
/* 64 */   public static final QName TEXT_HANDLER = new QName("", "text");
/*    */ 
/* 78 */   public static final QName CATCH_ALL = new QName("", "catchAll");
/*    */ 
/*    */   public abstract void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder
 * JD-Core Version:    0.6.2
 */