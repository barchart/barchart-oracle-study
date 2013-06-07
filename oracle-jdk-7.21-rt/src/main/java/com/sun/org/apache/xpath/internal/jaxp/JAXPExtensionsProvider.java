/*     */ package com.sun.org.apache.xpath.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.res.XSLMessages;
/*     */ import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
/*     */ import com.sun.org.apache.xpath.internal.ExtensionsProvider;
/*     */ import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
/*     */ import com.sun.org.apache.xpath.internal.objects.XNodeSet;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.xpath.XPathFunction;
/*     */ import javax.xml.xpath.XPathFunctionException;
/*     */ import javax.xml.xpath.XPathFunctionResolver;
/*     */ 
/*     */ public class JAXPExtensionsProvider
/*     */   implements ExtensionsProvider
/*     */ {
/*     */   private final XPathFunctionResolver resolver;
/*  49 */   private boolean extensionInvocationDisabled = false;
/*     */ 
/*     */   public JAXPExtensionsProvider(XPathFunctionResolver resolver) {
/*  52 */     this.resolver = resolver;
/*  53 */     this.extensionInvocationDisabled = false;
/*     */   }
/*     */ 
/*     */   public JAXPExtensionsProvider(XPathFunctionResolver resolver, boolean featureSecureProcessing)
/*     */   {
/*  58 */     this.resolver = resolver;
/*  59 */     this.extensionInvocationDisabled = featureSecureProcessing;
/*     */   }
/*     */ 
/*     */   public boolean functionAvailable(String ns, String funcName)
/*     */     throws TransformerException
/*     */   {
/*     */     try
/*     */     {
/*  69 */       if (funcName == null) {
/*  70 */         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
/*     */ 
/*  73 */         throw new NullPointerException(fmsg);
/*     */       }
/*     */ 
/*  76 */       QName myQName = new QName(ns, funcName);
/*  77 */       XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, 0);
/*     */ 
/*  79 */       if (xpathFunction == null) {
/*  80 */         return false;
/*     */       }
/*  82 */       return true; } catch (Exception e) {
/*     */     }
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean elementAvailable(String ns, String elemName)
/*     */     throws TransformerException
/*     */   {
/*  96 */     return false;
/*     */   }
/*     */ 
/*     */   public Object extFunction(String ns, String funcName, Vector argVec, Object methodKey)
/*     */     throws TransformerException
/*     */   {
/*     */     try
/*     */     {
/* 106 */       if (funcName == null) {
/* 107 */         String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
/*     */ 
/* 110 */         throw new NullPointerException(fmsg);
/*     */       }
/*     */ 
/* 113 */       QName myQName = new QName(ns, funcName);
/*     */ 
/* 118 */       if (this.extensionInvocationDisabled) {
/* 119 */         String fmsg = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { myQName.toString() });
/*     */ 
/* 122 */         throw new XPathFunctionException(fmsg);
/*     */       }
/*     */ 
/* 127 */       int arity = argVec.size();
/*     */ 
/* 129 */       XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, arity);
/*     */ 
/* 133 */       ArrayList argList = new ArrayList(arity);
/* 134 */       for (int i = 0; i < arity; i++) {
/* 135 */         Object argument = argVec.elementAt(i);
/*     */ 
/* 138 */         if ((argument instanceof XNodeSet)) {
/* 139 */           argList.add(i, ((XNodeSet)argument).nodelist());
/* 140 */         } else if ((argument instanceof XObject)) {
/* 141 */           Object passedArgument = ((XObject)argument).object();
/* 142 */           argList.add(i, passedArgument);
/*     */         } else {
/* 144 */           argList.add(i, argument);
/*     */         }
/*     */       }
/*     */ 
/* 148 */       return xpathFunction.evaluate(argList);
/*     */     }
/*     */     catch (XPathFunctionException xfe)
/*     */     {
/* 152 */       throw new WrappedRuntimeException(xfe);
/*     */     } catch (Exception e) {
/* 154 */       throw new TransformerException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object extFunction(FuncExtFunction extFunction, Vector argVec)
/*     */     throws TransformerException
/*     */   {
/*     */     try
/*     */     {
/* 166 */       String namespace = extFunction.getNamespace();
/* 167 */       String functionName = extFunction.getFunctionName();
/* 168 */       int arity = extFunction.getArgCount();
/* 169 */       QName myQName = new QName(namespace, functionName);
/*     */ 
/* 175 */       if (this.extensionInvocationDisabled) {
/* 176 */         String fmsg = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { myQName.toString() });
/*     */ 
/* 178 */         throw new XPathFunctionException(fmsg);
/*     */       }
/*     */ 
/* 181 */       XPathFunction xpathFunction = this.resolver.resolveFunction(myQName, arity);
/*     */ 
/* 184 */       ArrayList argList = new ArrayList(arity);
/* 185 */       for (int i = 0; i < arity; i++) {
/* 186 */         Object argument = argVec.elementAt(i);
/*     */ 
/* 189 */         if ((argument instanceof XNodeSet)) {
/* 190 */           argList.add(i, ((XNodeSet)argument).nodelist());
/* 191 */         } else if ((argument instanceof XObject)) {
/* 192 */           Object passedArgument = ((XObject)argument).object();
/* 193 */           argList.add(i, passedArgument);
/*     */         } else {
/* 195 */           argList.add(i, argument);
/*     */         }
/*     */       }
/*     */ 
/* 199 */       return xpathFunction.evaluate(argList);
/*     */     }
/*     */     catch (XPathFunctionException xfe)
/*     */     {
/* 204 */       throw new WrappedRuntimeException(xfe);
/*     */     } catch (Exception e) {
/* 206 */       throw new TransformerException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.jaxp.JAXPExtensionsProvider
 * JD-Core Version:    0.6.2
 */