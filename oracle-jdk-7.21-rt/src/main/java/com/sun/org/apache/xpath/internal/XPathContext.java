/*      */ package com.sun.org.apache.xpath.internal;
/*      */ 
/*      */ import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
/*      */ import com.sun.org.apache.xalan.internal.res.XSLMessages;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTM;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMFilter;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMIterator;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMManager;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
/*      */ import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
/*      */ import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
/*      */ import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
/*      */ import com.sun.org.apache.xml.internal.utils.IntStack;
/*      */ import com.sun.org.apache.xml.internal.utils.NodeVector;
/*      */ import com.sun.org.apache.xml.internal.utils.ObjectStack;
/*      */ import com.sun.org.apache.xml.internal.utils.PrefixResolver;
/*      */ import com.sun.org.apache.xml.internal.utils.QName;
/*      */ import com.sun.org.apache.xml.internal.utils.SAXSourceLocator;
/*      */ import com.sun.org.apache.xml.internal.utils.XMLString;
/*      */ import com.sun.org.apache.xpath.internal.axes.OneStepIteratorForward;
/*      */ import com.sun.org.apache.xpath.internal.axes.SubContextList;
/*      */ import com.sun.org.apache.xpath.internal.objects.DTMXRTreeFrag;
/*      */ import com.sun.org.apache.xpath.internal.objects.XMLStringFactoryImpl;
/*      */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*      */ import com.sun.org.apache.xpath.internal.objects.XString;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Stack;
/*      */ import java.util.Vector;
/*      */ import javax.xml.transform.ErrorListener;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.SourceLocator;
/*      */ import javax.xml.transform.TransformerException;
/*      */ import javax.xml.transform.URIResolver;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.traversal.NodeIterator;
/*      */ import org.xml.sax.XMLReader;
/*      */ 
/*      */ public class XPathContext extends DTMManager
/*      */ {
/*   67 */   IntStack m_last_pushed_rtfdtm = new IntStack();
/*      */ 
/*   78 */   private Vector m_rtfdtm_stack = null;
/*      */ 
/*   80 */   private int m_which_rtfdtm = -1;
/*      */ 
/*   86 */   private SAX2RTFDTM m_global_rtfdtm = null;
/*      */ 
/*   92 */   private HashMap m_DTMXRTreeFrags = null;
/*      */ 
/*   97 */   private boolean m_isSecureProcessing = false;
/*      */ 
/*   99 */   private boolean m_useServicesMechanism = true;
/*      */ 
/*  106 */   protected DTMManager m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
/*      */ 
/*  386 */   ObjectStack m_saxLocations = new ObjectStack(4096);
/*      */   private Object m_owner;
/*      */   private Method m_ownerGetErrorListener;
/*  464 */   private VariableStack m_variableStacks = new VariableStack();
/*      */ 
/*  492 */   private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();
/*      */   private ErrorListener m_errorListener;
/*      */   private ErrorListener m_defaultErrorListener;
/*      */   private URIResolver m_uriResolver;
/*      */   public XMLReader m_primaryReader;
/*  658 */   private Stack m_contextNodeLists = new Stack();
/*      */   public static final int RECURSIONLIMIT = 4096;
/*  712 */   private IntStack m_currentNodes = new IntStack(4096);
/*      */ 
/*  843 */   private NodeVector m_iteratorRoots = new NodeVector();
/*      */ 
/*  846 */   private NodeVector m_predicateRoots = new NodeVector();
/*      */ 
/*  849 */   private IntStack m_currentExpressionNodes = new IntStack(4096);
/*      */ 
/*  855 */   private IntStack m_predicatePos = new IntStack();
/*      */ 
/*  901 */   private ObjectStack m_prefixResolvers = new ObjectStack(4096);
/*      */ 
/*  961 */   private Stack m_axesIteratorStack = new Stack();
/*      */ 
/* 1047 */   XPathExpressionContext expressionContext = new XPathExpressionContext();
/*      */ 
/*      */   public DTMManager getDTMManager()
/*      */   {
/*  117 */     return this.m_dtmManager;
/*      */   }
/*      */ 
/*      */   public void setSecureProcessing(boolean flag)
/*      */   {
/*  125 */     this.m_isSecureProcessing = flag;
/*      */   }
/*      */ 
/*      */   public boolean isSecureProcessing()
/*      */   {
/*  133 */     return this.m_isSecureProcessing;
/*      */   }
/*      */ 
/*      */   public DTM getDTM(Source source, boolean unique, DTMWSFilter wsfilter, boolean incremental, boolean doIndexing)
/*      */   {
/*  162 */     return this.m_dtmManager.getDTM(source, unique, wsfilter, incremental, doIndexing);
/*      */   }
/*      */ 
/*      */   public DTM getDTM(int nodeHandle)
/*      */   {
/*  175 */     return this.m_dtmManager.getDTM(nodeHandle);
/*      */   }
/*      */ 
/*      */   public int getDTMHandleFromNode(Node node)
/*      */   {
/*  188 */     return this.m_dtmManager.getDTMHandleFromNode(node);
/*      */   }
/*      */ 
/*      */   public int getDTMIdentity(DTM dtm)
/*      */   {
/*  197 */     return this.m_dtmManager.getDTMIdentity(dtm);
/*      */   }
/*      */ 
/*      */   public DTM createDocumentFragment()
/*      */   {
/*  206 */     return this.m_dtmManager.createDocumentFragment();
/*      */   }
/*      */ 
/*      */   public boolean release(DTM dtm, boolean shouldHardDelete)
/*      */   {
/*  225 */     if ((this.m_rtfdtm_stack != null) && (this.m_rtfdtm_stack.contains(dtm)))
/*      */     {
/*  227 */       return false;
/*      */     }
/*      */ 
/*  230 */     return this.m_dtmManager.release(dtm, shouldHardDelete);
/*      */   }
/*      */ 
/*      */   public DTMIterator createDTMIterator(Object xpathCompiler, int pos)
/*      */   {
/*  247 */     return this.m_dtmManager.createDTMIterator(xpathCompiler, pos);
/*      */   }
/*      */ 
/*      */   public DTMIterator createDTMIterator(String xpathString, PrefixResolver presolver)
/*      */   {
/*  266 */     return this.m_dtmManager.createDTMIterator(xpathString, presolver);
/*      */   }
/*      */ 
/*      */   public DTMIterator createDTMIterator(int whatToShow, DTMFilter filter, boolean entityReferenceExpansion)
/*      */   {
/*  289 */     return this.m_dtmManager.createDTMIterator(whatToShow, filter, entityReferenceExpansion);
/*      */   }
/*      */ 
/*      */   public DTMIterator createDTMIterator(int node)
/*      */   {
/*  302 */     DTMIterator iter = new OneStepIteratorForward(13);
/*  303 */     iter.setRoot(node, this);
/*  304 */     return iter;
/*      */   }
/*      */ 
/*      */   public XPathContext()
/*      */   {
/*  313 */     this(true);
/*      */   }
/*      */ 
/*      */   public XPathContext(boolean useServicesMechanism) {
/*  317 */     init(useServicesMechanism);
/*      */   }
/*      */ 
/*      */   public XPathContext(Object owner)
/*      */   {
/*  327 */     this.m_owner = owner;
/*      */     try {
/*  329 */       this.m_ownerGetErrorListener = this.m_owner.getClass().getMethod("getErrorListener", new Class[0]);
/*      */     } catch (NoSuchMethodException nsme) {
/*      */     }
/*  332 */     init(true);
/*      */   }
/*      */ 
/*      */   private void init(boolean useServicesMechanism) {
/*  336 */     this.m_prefixResolvers.push(null);
/*  337 */     this.m_currentNodes.push(-1);
/*  338 */     this.m_currentExpressionNodes.push(-1);
/*  339 */     this.m_saxLocations.push(null);
/*  340 */     this.m_useServicesMechanism = useServicesMechanism;
/*  341 */     this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory(), this.m_useServicesMechanism);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  351 */     releaseDTMXRTreeFrags();
/*      */     Enumeration e;
/*  353 */     if (this.m_rtfdtm_stack != null) {
/*  354 */       for (e = this.m_rtfdtm_stack.elements(); e.hasMoreElements(); )
/*  355 */         this.m_dtmManager.release((DTM)e.nextElement(), true);
/*      */     }
/*  357 */     this.m_rtfdtm_stack = null;
/*  358 */     this.m_which_rtfdtm = -1;
/*      */ 
/*  360 */     if (this.m_global_rtfdtm != null)
/*  361 */       this.m_dtmManager.release(this.m_global_rtfdtm, true);
/*  362 */     this.m_global_rtfdtm = null;
/*      */ 
/*  365 */     this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory(), this.m_useServicesMechanism);
/*      */ 
/*  369 */     this.m_saxLocations.removeAllElements();
/*  370 */     this.m_axesIteratorStack.removeAllElements();
/*  371 */     this.m_contextNodeLists.removeAllElements();
/*  372 */     this.m_currentExpressionNodes.removeAllElements();
/*  373 */     this.m_currentNodes.removeAllElements();
/*  374 */     this.m_iteratorRoots.RemoveAllNoClear();
/*  375 */     this.m_predicatePos.removeAllElements();
/*  376 */     this.m_predicateRoots.RemoveAllNoClear();
/*  377 */     this.m_prefixResolvers.removeAllElements();
/*      */ 
/*  379 */     this.m_prefixResolvers.push(null);
/*  380 */     this.m_currentNodes.push(-1);
/*  381 */     this.m_currentExpressionNodes.push(-1);
/*  382 */     this.m_saxLocations.push(null);
/*      */   }
/*      */ 
/*      */   public void setSAXLocator(SourceLocator location)
/*      */   {
/*  395 */     this.m_saxLocations.setTop(location);
/*      */   }
/*      */ 
/*      */   public void pushSAXLocator(SourceLocator location)
/*      */   {
/*  405 */     this.m_saxLocations.push(location);
/*      */   }
/*      */ 
/*      */   public void pushSAXLocatorNull()
/*      */   {
/*  415 */     this.m_saxLocations.push(null);
/*      */   }
/*      */ 
/*      */   public void popSAXLocator()
/*      */   {
/*  424 */     this.m_saxLocations.pop();
/*      */   }
/*      */ 
/*      */   public SourceLocator getSAXLocator()
/*      */   {
/*  434 */     return (SourceLocator)this.m_saxLocations.peek();
/*      */   }
/*      */ 
/*      */   public Object getOwnerObject()
/*      */   {
/*  455 */     return this.m_owner;
/*      */   }
/*      */ 
/*      */   public final VariableStack getVarStack()
/*      */   {
/*  474 */     return this.m_variableStacks;
/*      */   }
/*      */ 
/*      */   public final void setVarStack(VariableStack varStack)
/*      */   {
/*  485 */     this.m_variableStacks = varStack;
/*      */   }
/*      */ 
/*      */   public final SourceTreeManager getSourceTreeManager()
/*      */   {
/*  501 */     return this.m_sourceTreeManager;
/*      */   }
/*      */ 
/*      */   public void setSourceTreeManager(SourceTreeManager mgr)
/*      */   {
/*  512 */     this.m_sourceTreeManager = mgr;
/*      */   }
/*      */ 
/*      */   public final ErrorListener getErrorListener()
/*      */   {
/*  533 */     if (null != this.m_errorListener) {
/*  534 */       return this.m_errorListener;
/*      */     }
/*  536 */     ErrorListener retval = null;
/*      */     try
/*      */     {
/*  539 */       if (null != this.m_ownerGetErrorListener)
/*  540 */         retval = (ErrorListener)this.m_ownerGetErrorListener.invoke(this.m_owner, new Object[0]);
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*  544 */     if (null == retval)
/*      */     {
/*  546 */       if (null == this.m_defaultErrorListener)
/*  547 */         this.m_defaultErrorListener = new DefaultErrorHandler();
/*  548 */       retval = this.m_defaultErrorListener;
/*      */     }
/*      */ 
/*  551 */     return retval;
/*      */   }
/*      */ 
/*      */   public void setErrorListener(ErrorListener listener)
/*      */     throws IllegalArgumentException
/*      */   {
/*  561 */     if (listener == null)
/*  562 */       throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", null));
/*  563 */     this.m_errorListener = listener;
/*      */   }
/*      */ 
/*      */   public final URIResolver getURIResolver()
/*      */   {
/*  580 */     return this.m_uriResolver;
/*      */   }
/*      */ 
/*      */   public void setURIResolver(URIResolver resolver)
/*      */   {
/*  591 */     this.m_uriResolver = resolver;
/*      */   }
/*      */ 
/*      */   public final XMLReader getPrimaryReader()
/*      */   {
/*  606 */     return this.m_primaryReader;
/*      */   }
/*      */ 
/*      */   public void setPrimaryReader(XMLReader reader)
/*      */   {
/*  616 */     this.m_primaryReader = reader;
/*      */   }
/*      */ 
/*      */   private void assertion(boolean b, String msg)
/*      */     throws TransformerException
/*      */   {
/*  636 */     if (!b)
/*      */     {
/*  638 */       ErrorListener errorHandler = getErrorListener();
/*      */ 
/*  640 */       if (errorHandler != null)
/*      */       {
/*  642 */         errorHandler.fatalError(new TransformerException(XSLMessages.createMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { msg }), (SAXSourceLocator)getSAXLocator()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Stack getContextNodeListsStack()
/*      */   {
/*  660 */     return this.m_contextNodeLists; } 
/*  661 */   public void setContextNodeListsStack(Stack s) { this.m_contextNodeLists = s; }
/*      */ 
/*      */ 
/*      */   public final DTMIterator getContextNodeList()
/*      */   {
/*  672 */     if (this.m_contextNodeLists.size() > 0) {
/*  673 */       return (DTMIterator)this.m_contextNodeLists.peek();
/*      */     }
/*  675 */     return null;
/*      */   }
/*      */ 
/*      */   public final void pushContextNodeList(DTMIterator nl)
/*      */   {
/*  687 */     this.m_contextNodeLists.push(nl);
/*      */   }
/*      */ 
/*      */   public final void popContextNodeList()
/*      */   {
/*  696 */     if (this.m_contextNodeLists.isEmpty())
/*  697 */       System.err.println("Warning: popContextNodeList when stack is empty!");
/*      */     else
/*  699 */       this.m_contextNodeLists.pop();
/*      */   }
/*      */ 
/*      */   public IntStack getCurrentNodeStack()
/*      */   {
/*  716 */     return this.m_currentNodes; } 
/*  717 */   public void setCurrentNodeStack(IntStack nv) { this.m_currentNodes = nv; }
/*      */ 
/*      */ 
/*      */   public final int getCurrentNode()
/*      */   {
/*  726 */     return this.m_currentNodes.peek();
/*      */   }
/*      */ 
/*      */   public final void pushCurrentNodeAndExpression(int cn, int en)
/*      */   {
/*  737 */     this.m_currentNodes.push(cn);
/*  738 */     this.m_currentExpressionNodes.push(cn);
/*      */   }
/*      */ 
/*      */   public final void popCurrentNodeAndExpression()
/*      */   {
/*  746 */     this.m_currentNodes.quickPop(1);
/*  747 */     this.m_currentExpressionNodes.quickPop(1);
/*      */   }
/*      */ 
/*      */   public final void pushExpressionState(int cn, int en, PrefixResolver nc)
/*      */   {
/*  759 */     this.m_currentNodes.push(cn);
/*  760 */     this.m_currentExpressionNodes.push(cn);
/*  761 */     this.m_prefixResolvers.push(nc);
/*      */   }
/*      */ 
/*      */   public final void popExpressionState()
/*      */   {
/*  769 */     this.m_currentNodes.quickPop(1);
/*  770 */     this.m_currentExpressionNodes.quickPop(1);
/*  771 */     this.m_prefixResolvers.pop();
/*      */   }
/*      */ 
/*      */   public final void pushCurrentNode(int n)
/*      */   {
/*  783 */     this.m_currentNodes.push(n);
/*      */   }
/*      */ 
/*      */   public final void popCurrentNode()
/*      */   {
/*  791 */     this.m_currentNodes.quickPop(1);
/*      */   }
/*      */ 
/*      */   public final void pushPredicateRoot(int n)
/*      */   {
/*  799 */     this.m_predicateRoots.push(n);
/*      */   }
/*      */ 
/*      */   public final void popPredicateRoot()
/*      */   {
/*  807 */     this.m_predicateRoots.popQuick();
/*      */   }
/*      */ 
/*      */   public final int getPredicateRoot()
/*      */   {
/*  815 */     return this.m_predicateRoots.peepOrNull();
/*      */   }
/*      */ 
/*      */   public final void pushIteratorRoot(int n)
/*      */   {
/*  823 */     this.m_iteratorRoots.push(n);
/*      */   }
/*      */ 
/*      */   public final void popIteratorRoot()
/*      */   {
/*  831 */     this.m_iteratorRoots.popQuick();
/*      */   }
/*      */ 
/*      */   public final int getIteratorRoot()
/*      */   {
/*  839 */     return this.m_iteratorRoots.peepOrNull();
/*      */   }
/*      */ 
/*      */   public IntStack getCurrentExpressionNodeStack()
/*      */   {
/*  852 */     return this.m_currentExpressionNodes; } 
/*  853 */   public void setCurrentExpressionNodeStack(IntStack nv) { this.m_currentExpressionNodes = nv; }
/*      */ 
/*      */ 
/*      */   public final int getPredicatePos()
/*      */   {
/*  859 */     return this.m_predicatePos.peek();
/*      */   }
/*      */ 
/*      */   public final void pushPredicatePos(int n)
/*      */   {
/*  864 */     this.m_predicatePos.push(n);
/*      */   }
/*      */ 
/*      */   public final void popPredicatePos()
/*      */   {
/*  869 */     this.m_predicatePos.pop();
/*      */   }
/*      */ 
/*      */   public final int getCurrentExpressionNode()
/*      */   {
/*  879 */     return this.m_currentExpressionNodes.peek();
/*      */   }
/*      */ 
/*      */   public final void pushCurrentExpressionNode(int n)
/*      */   {
/*  889 */     this.m_currentExpressionNodes.push(n);
/*      */   }
/*      */ 
/*      */   public final void popCurrentExpressionNode()
/*      */   {
/*  898 */     this.m_currentExpressionNodes.quickPop(1);
/*      */   }
/*      */ 
/*      */   public final PrefixResolver getNamespaceContext()
/*      */   {
/*  912 */     return (PrefixResolver)this.m_prefixResolvers.peek();
/*      */   }
/*      */ 
/*      */   public final void setNamespaceContext(PrefixResolver pr)
/*      */   {
/*  923 */     this.m_prefixResolvers.setTop(pr);
/*      */   }
/*      */ 
/*      */   public final void pushNamespaceContext(PrefixResolver pr)
/*      */   {
/*  934 */     this.m_prefixResolvers.push(pr);
/*      */   }
/*      */ 
/*      */   public final void pushNamespaceContextNull()
/*      */   {
/*  943 */     this.m_prefixResolvers.push(null);
/*      */   }
/*      */ 
/*      */   public final void popNamespaceContext()
/*      */   {
/*  951 */     this.m_prefixResolvers.pop();
/*      */   }
/*      */ 
/*      */   public Stack getAxesIteratorStackStacks()
/*      */   {
/*  963 */     return this.m_axesIteratorStack; } 
/*  964 */   public void setAxesIteratorStackStacks(Stack s) { this.m_axesIteratorStack = s; }
/*      */ 
/*      */ 
/*      */   public final void pushSubContextList(SubContextList iter)
/*      */   {
/*  974 */     this.m_axesIteratorStack.push(iter);
/*      */   }
/*      */ 
/*      */   public final void popSubContextList()
/*      */   {
/*  983 */     this.m_axesIteratorStack.pop();
/*      */   }
/*      */ 
/*      */   public SubContextList getSubContextList()
/*      */   {
/*  994 */     return this.m_axesIteratorStack.isEmpty() ? null : (SubContextList)this.m_axesIteratorStack.peek();
/*      */   }
/*      */ 
/*      */   public SubContextList getCurrentNodeList()
/*      */   {
/* 1008 */     return this.m_axesIteratorStack.isEmpty() ? null : (SubContextList)this.m_axesIteratorStack.elementAt(0);
/*      */   }
/*      */ 
/*      */   public final int getContextNode()
/*      */   {
/* 1021 */     return getCurrentNode();
/*      */   }
/*      */ 
/*      */   public final DTMIterator getContextNodes()
/*      */   {
/*      */     try
/*      */     {
/* 1034 */       DTMIterator cnl = getContextNodeList();
/*      */ 
/* 1036 */       if (null != cnl) {
/* 1037 */         return cnl.cloneWithReset();
/*      */       }
/* 1039 */       return null;
/*      */     }
/*      */     catch (CloneNotSupportedException cnse) {
/*      */     }
/* 1043 */     return null;
/*      */   }
/*      */ 
/*      */   public ExpressionContext getExpressionContext()
/*      */   {
/* 1056 */     return this.expressionContext;
/*      */   }
/*      */ 
/*      */   public DTM getGlobalRTFDTM()
/*      */   {
/* 1201 */     if ((this.m_global_rtfdtm == null) || (this.m_global_rtfdtm.isTreeIncomplete()))
/*      */     {
/* 1203 */       this.m_global_rtfdtm = ((SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false));
/*      */     }
/* 1205 */     return this.m_global_rtfdtm;
/*      */   }
/*      */ 
/*      */   public DTM getRTFDTM()
/*      */   {
/*      */     SAX2RTFDTM rtfdtm;
/* 1234 */     if (this.m_rtfdtm_stack == null)
/*      */     {
/* 1236 */       this.m_rtfdtm_stack = new Vector();
/* 1237 */       SAX2RTFDTM rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
/* 1238 */       this.m_rtfdtm_stack.addElement(rtfdtm);
/* 1239 */       this.m_which_rtfdtm += 1;
/*      */     }
/*      */     else
/*      */     {
/*      */       SAX2RTFDTM rtfdtm;
/* 1241 */       if (this.m_which_rtfdtm < 0)
/*      */       {
/* 1243 */         rtfdtm = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(++this.m_which_rtfdtm);
/*      */       }
/*      */       else
/*      */       {
/* 1247 */         rtfdtm = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
/*      */ 
/* 1255 */         if (rtfdtm.isTreeIncomplete())
/*      */         {
/* 1257 */           if (++this.m_which_rtfdtm < this.m_rtfdtm_stack.size()) {
/* 1258 */             rtfdtm = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
/*      */           }
/*      */           else {
/* 1261 */             rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
/* 1262 */             this.m_rtfdtm_stack.addElement(rtfdtm);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1267 */     return rtfdtm;
/*      */   }
/*      */ 
/*      */   public void pushRTFContext()
/*      */   {
/* 1276 */     this.m_last_pushed_rtfdtm.push(this.m_which_rtfdtm);
/* 1277 */     if (null != this.m_rtfdtm_stack)
/* 1278 */       ((SAX2RTFDTM)getRTFDTM()).pushRewindMark();
/*      */   }
/*      */ 
/*      */   public void popRTFContext()
/*      */   {
/* 1297 */     int previous = this.m_last_pushed_rtfdtm.pop();
/* 1298 */     if (null == this.m_rtfdtm_stack)
/*      */       return;
/*      */     boolean isEmpty;
/* 1301 */     if (this.m_which_rtfdtm == previous)
/*      */     {
/* 1303 */       if (previous >= 0)
/*      */       {
/* 1305 */         isEmpty = ((SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(previous)).popRewindMark();
/*      */       }
/*      */     }
/* 1308 */     else while (this.m_which_rtfdtm != previous)
/*      */       {
/* 1313 */         boolean isEmpty = ((SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm)).popRewindMark();
/* 1314 */         this.m_which_rtfdtm -= 1;
/*      */       }
/*      */   }
/*      */ 
/*      */   public DTMXRTreeFrag getDTMXRTreeFrag(int dtmIdentity)
/*      */   {
/* 1326 */     if (this.m_DTMXRTreeFrags == null) {
/* 1327 */       this.m_DTMXRTreeFrags = new HashMap();
/*      */     }
/*      */ 
/* 1330 */     if (this.m_DTMXRTreeFrags.containsKey(new Integer(dtmIdentity))) {
/* 1331 */       return (DTMXRTreeFrag)this.m_DTMXRTreeFrags.get(new Integer(dtmIdentity));
/*      */     }
/* 1333 */     DTMXRTreeFrag frag = new DTMXRTreeFrag(dtmIdentity, this);
/* 1334 */     this.m_DTMXRTreeFrags.put(new Integer(dtmIdentity), frag);
/* 1335 */     return frag;
/*      */   }
/*      */ 
/*      */   private final void releaseDTMXRTreeFrags()
/*      */   {
/* 1344 */     if (this.m_DTMXRTreeFrags == null) {
/* 1345 */       return;
/*      */     }
/* 1347 */     Iterator iter = this.m_DTMXRTreeFrags.values().iterator();
/* 1348 */     while (iter.hasNext()) {
/* 1349 */       DTMXRTreeFrag frag = (DTMXRTreeFrag)iter.next();
/* 1350 */       frag.destruct();
/* 1351 */       iter.remove();
/*      */     }
/* 1353 */     this.m_DTMXRTreeFrags = null;
/*      */   }
/*      */ 
/*      */   public class XPathExpressionContext
/*      */     implements ExpressionContext
/*      */   {
/*      */     public XPathExpressionContext()
/*      */     {
/*      */     }
/*      */ 
/*      */     public XPathContext getXPathContext()
/*      */     {
/* 1070 */       return XPathContext.this;
/*      */     }
/*      */ 
/*      */     public DTMManager getDTMManager()
/*      */     {
/* 1081 */       return XPathContext.this.m_dtmManager;
/*      */     }
/*      */ 
/*      */     public Node getContextNode()
/*      */     {
/* 1090 */       int context = XPathContext.this.getCurrentNode();
/*      */ 
/* 1092 */       return XPathContext.this.getDTM(context).getNode(context);
/*      */     }
/*      */ 
/*      */     public NodeIterator getContextNodes()
/*      */     {
/* 1102 */       return new DTMNodeIterator(XPathContext.this.getContextNodeList());
/*      */     }
/*      */ 
/*      */     public ErrorListener getErrorListener()
/*      */     {
/* 1111 */       return XPathContext.this.getErrorListener();
/*      */     }
/*      */ 
/*      */     public boolean useServicesMechnism()
/*      */     {
/* 1117 */       return XPathContext.this.m_useServicesMechanism;
/*      */     }
/*      */ 
/*      */     public void setServicesMechnism(boolean flag)
/*      */     {
/* 1124 */       XPathContext.this.m_useServicesMechanism = flag;
/*      */     }
/*      */ 
/*      */     public double toNumber(Node n)
/*      */     {
/* 1135 */       int nodeHandle = XPathContext.this.getDTMHandleFromNode(n);
/* 1136 */       DTM dtm = XPathContext.this.getDTM(nodeHandle);
/* 1137 */       XString xobj = (XString)dtm.getStringValue(nodeHandle);
/* 1138 */       return xobj.num();
/*      */     }
/*      */ 
/*      */     public String toString(Node n)
/*      */     {
/* 1149 */       int nodeHandle = XPathContext.this.getDTMHandleFromNode(n);
/* 1150 */       DTM dtm = XPathContext.this.getDTM(nodeHandle);
/* 1151 */       XMLString strVal = dtm.getStringValue(nodeHandle);
/* 1152 */       return strVal.toString();
/*      */     }
/*      */ 
/*      */     public final XObject getVariableOrParam(QName qname)
/*      */       throws TransformerException
/*      */     {
/* 1165 */       return XPathContext.this.m_variableStacks.getVariableOrParam(XPathContext.this, qname);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.XPathContext
 * JD-Core Version:    0.6.2
 */