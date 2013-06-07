/*    */ package com.sun.org.apache.xpath.internal.functions;
/*    */ 
/*    */ import com.sun.org.apache.xpath.internal.Expression;
/*    */ import com.sun.org.apache.xpath.internal.XPathContext;
/*    */ import com.sun.org.apache.xpath.internal.objects.XNumber;
/*    */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*    */ import javax.xml.transform.TransformerException;
/*    */ 
/*    */ public class FuncCeiling extends FunctionOneArg
/*    */ {
/*    */   static final long serialVersionUID = -1275988936390464739L;
/*    */ 
/*    */   public XObject execute(XPathContext xctxt)
/*    */     throws TransformerException
/*    */   {
/* 47 */     return new XNumber(Math.ceil(this.m_arg0.execute(xctxt).num()));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.functions.FuncCeiling
 * JD-Core Version:    0.6.2
 */