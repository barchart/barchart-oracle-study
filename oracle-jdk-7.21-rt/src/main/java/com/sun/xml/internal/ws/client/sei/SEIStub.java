/*     */ package com.sun.xml.internal.ws.client.sei;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.ws.api.SOAPVersion;
/*     */ import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
/*     */ import com.sun.xml.internal.ws.api.client.WSPortInfo;
/*     */ import com.sun.xml.internal.ws.api.message.Header;
/*     */ import com.sun.xml.internal.ws.api.message.Headers;
/*     */ import com.sun.xml.internal.ws.api.message.Packet;
/*     */ import com.sun.xml.internal.ws.api.model.MEP;
/*     */ import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
/*     */ import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
/*     */ import com.sun.xml.internal.ws.api.pipe.Tube;
/*     */ import com.sun.xml.internal.ws.binding.BindingImpl;
/*     */ import com.sun.xml.internal.ws.client.RequestContext;
/*     */ import com.sun.xml.internal.ws.client.ResponseContextReceiver;
/*     */ import com.sun.xml.internal.ws.client.Stub;
/*     */ import com.sun.xml.internal.ws.client.WSServiceDelegate;
/*     */ import com.sun.xml.internal.ws.model.JavaMethodImpl;
/*     */ import com.sun.xml.internal.ws.model.SOAPSEIModel;
/*     */ import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public final class SEIStub extends Stub
/*     */   implements InvocationHandler
/*     */ {
/*     */   public final SOAPSEIModel seiModel;
/*     */   public final SOAPVersion soapVersion;
/* 124 */   private final Map<Method, MethodHandler> methodHandlers = new HashMap();
/*     */ 
/*     */   @Deprecated
/*     */   public SEIStub(WSServiceDelegate owner, BindingImpl binding, SOAPSEIModel seiModel, Tube master, WSEndpointReference epr)
/*     */   {
/*  63 */     super(owner, master, binding, seiModel.getPort(), seiModel.getPort().getAddress(), epr);
/*  64 */     this.seiModel = seiModel;
/*  65 */     this.soapVersion = binding.getSOAPVersion();
/*  66 */     initMethodHandlers();
/*     */   }
/*     */ 
/*     */   public SEIStub(WSPortInfo portInfo, BindingImpl binding, SOAPSEIModel seiModel, WSEndpointReference epr) {
/*  70 */     super(portInfo, binding, seiModel.getPort().getAddress(), epr);
/*  71 */     this.seiModel = seiModel;
/*  72 */     this.soapVersion = binding.getSOAPVersion();
/*  73 */     initMethodHandlers();
/*     */   }
/*     */ 
/*     */   private void initMethodHandlers() {
/*  77 */     Map syncs = new HashMap();
/*     */ 
/*  81 */     for (JavaMethodImpl m : this.seiModel.getJavaMethods()) {
/*  82 */       if (!m.getMEP().isAsync) {
/*  83 */         SyncMethodHandler handler = new SyncMethodHandler(this, m);
/*  84 */         syncs.put(m.getOperation(), m);
/*  85 */         this.methodHandlers.put(m.getMethod(), handler);
/*     */       }
/*     */     }
/*     */ 
/*  89 */     for (JavaMethodImpl jm : this.seiModel.getJavaMethods()) {
/*  90 */       JavaMethodImpl sync = (JavaMethodImpl)syncs.get(jm.getOperation());
/*  91 */       if (jm.getMEP() == MEP.ASYNC_CALLBACK) {
/*  92 */         Method m = jm.getMethod();
/*  93 */         CallbackMethodHandler handler = new CallbackMethodHandler(this, jm, sync, m.getParameterTypes().length - 1);
/*     */ 
/*  95 */         this.methodHandlers.put(m, handler);
/*     */       }
/*  97 */       if (jm.getMEP() == MEP.ASYNC_POLL) {
/*  98 */         Method m = jm.getMethod();
/*  99 */         PollingMethodHandler handler = new PollingMethodHandler(this, jm, sync);
/* 100 */         this.methodHandlers.put(m, handler);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public OperationDispatcher getOperationDispatcher()
/*     */   {
/* 115 */     if ((this.operationDispatcher == null) && (this.wsdlPort != null))
/* 116 */       this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel);
/* 117 */     return this.operationDispatcher;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 127 */     MethodHandler handler = (MethodHandler)this.methodHandlers.get(method);
/* 128 */     if (handler != null) {
/* 129 */       return handler.invoke(proxy, args);
/*     */     }
/*     */     try
/*     */     {
/* 133 */       return method.invoke(this, args);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 136 */       throw new AssertionError(e);
/*     */     } catch (IllegalArgumentException e) {
/* 138 */       throw new AssertionError(e);
/*     */     } catch (InvocationTargetException e) {
/* 140 */       throw e.getCause();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Packet doProcess(Packet request, RequestContext rc, ResponseContextReceiver receiver)
/*     */   {
/* 146 */     return super.process(request, rc, receiver);
/*     */   }
/*     */ 
/*     */   public final void doProcessAsync(Packet request, RequestContext rc, Fiber.CompletionCallback callback) {
/* 150 */     super.processAsync(request, rc, callback);
/*     */   }
/*     */   @NotNull
/*     */   protected final QName getPortName() {
/* 154 */     return this.wsdlPort.getName();
/*     */   }
/*     */ 
/*     */   public void setOutboundHeaders(Object[] headers)
/*     */   {
/* 159 */     if (headers == null)
/* 160 */       throw new IllegalArgumentException();
/* 161 */     Header[] hl = new Header[headers.length];
/* 162 */     for (int i = 0; i < hl.length; i++) {
/* 163 */       if (headers[i] == null)
/* 164 */         throw new IllegalArgumentException();
/* 165 */       hl[i] = Headers.create(this.seiModel.getJAXBContext(), headers[i]);
/*     */     }
/* 167 */     super.setOutboundHeaders(hl);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.client.sei.SEIStub
 * JD-Core Version:    0.6.2
 */