/*     */ package com.sun.org.apache.xpath.internal;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.utils.QName;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ 
/*     */ public class Arg
/*     */ {
/*     */   private QName m_qname;
/*     */   private XObject m_val;
/*     */   private String m_expression;
/*     */   private boolean m_isFromWithParam;
/*     */   private boolean m_isVisible;
/*     */ 
/*     */   public final QName getQName()
/*     */   {
/*  51 */     return this.m_qname;
/*     */   }
/*     */ 
/*     */   public final void setQName(QName name)
/*     */   {
/*  61 */     this.m_qname = name;
/*     */   }
/*     */ 
/*     */   public final XObject getVal()
/*     */   {
/*  78 */     return this.m_val;
/*     */   }
/*     */ 
/*     */   public final void setVal(XObject val)
/*     */   {
/*  89 */     this.m_val = val;
/*     */   }
/*     */ 
/*     */   public void detach()
/*     */   {
/*  98 */     if (null != this.m_val)
/*     */     {
/* 100 */       this.m_val.allowDetachToRelease(true);
/* 101 */       this.m_val.detach();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getExpression()
/*     */   {
/* 121 */     return this.m_expression;
/*     */   }
/*     */ 
/*     */   public void setExpression(String expr)
/*     */   {
/* 133 */     this.m_expression = expr;
/*     */   }
/*     */ 
/*     */   public boolean isFromWithParam()
/*     */   {
/* 148 */     return this.m_isFromWithParam;
/*     */   }
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/* 165 */     return this.m_isVisible;
/*     */   }
/*     */ 
/*     */   public void setIsVisible(boolean b)
/*     */   {
/* 173 */     this.m_isVisible = b;
/*     */   }
/*     */ 
/*     */   public Arg()
/*     */   {
/* 184 */     this.m_qname = new QName("");
/*     */ 
/* 186 */     this.m_val = null;
/* 187 */     this.m_expression = null;
/* 188 */     this.m_isVisible = true;
/* 189 */     this.m_isFromWithParam = false;
/*     */   }
/*     */ 
/*     */   public Arg(QName qname, String expression, boolean isFromWithParam)
/*     */   {
/* 202 */     this.m_qname = qname;
/* 203 */     this.m_val = null;
/* 204 */     this.m_expression = expression;
/* 205 */     this.m_isFromWithParam = isFromWithParam;
/* 206 */     this.m_isVisible = (!isFromWithParam);
/*     */   }
/*     */ 
/*     */   public Arg(QName qname, XObject val)
/*     */   {
/* 219 */     this.m_qname = qname;
/* 220 */     this.m_val = val;
/* 221 */     this.m_isVisible = true;
/* 222 */     this.m_isFromWithParam = false;
/* 223 */     this.m_expression = null;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 236 */     if ((obj instanceof QName))
/*     */     {
/* 238 */       return this.m_qname.equals(obj);
/*     */     }
/*     */ 
/* 241 */     return super.equals(obj);
/*     */   }
/*     */ 
/*     */   public Arg(QName qname, XObject val, boolean isFromWithParam)
/*     */   {
/* 254 */     this.m_qname = qname;
/* 255 */     this.m_val = val;
/* 256 */     this.m_isFromWithParam = isFromWithParam;
/* 257 */     this.m_isVisible = (!isFromWithParam);
/* 258 */     this.m_expression = null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.Arg
 * JD-Core Version:    0.6.2
 */