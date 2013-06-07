/*      */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*      */ 
/*      */ import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
/*      */ import com.sun.org.apache.bcel.internal.generic.IFEQ;
/*      */ import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
/*      */ import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
/*      */ import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
/*      */ import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
/*      */ import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
/*      */ import com.sun.org.apache.bcel.internal.generic.InstructionList;
/*      */ import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
/*      */ import com.sun.org.apache.bcel.internal.generic.NEW;
/*      */ import com.sun.org.apache.bcel.internal.generic.PUSH;
/*      */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ 
/*      */ class FunctionCall extends Expression
/*      */ {
/*      */   private QName _fname;
/*      */   private final Vector _arguments;
/*   72 */   private static final Vector EMPTY_ARG_LIST = new Vector(0);
/*      */   protected static final String EXT_XSLTC = "http://xml.apache.org/xalan/xsltc";
/*      */   protected static final String JAVA_EXT_XSLTC = "http://xml.apache.org/xalan/xsltc/java";
/*      */   protected static final String EXT_XALAN = "http://xml.apache.org/xalan";
/*      */   protected static final String JAVA_EXT_XALAN = "http://xml.apache.org/xalan/java";
/*      */   protected static final String JAVA_EXT_XALAN_OLD = "http://xml.apache.org/xslt/java";
/*      */   protected static final String EXSLT_COMMON = "http://exslt.org/common";
/*      */   protected static final String EXSLT_MATH = "http://exslt.org/math";
/*      */   protected static final String EXSLT_SETS = "http://exslt.org/sets";
/*      */   protected static final String EXSLT_DATETIME = "http://exslt.org/dates-and-times";
/*      */   protected static final String EXSLT_STRINGS = "http://exslt.org/strings";
/*      */   protected static final int NAMESPACE_FORMAT_JAVA = 0;
/*      */   protected static final int NAMESPACE_FORMAT_CLASS = 1;
/*      */   protected static final int NAMESPACE_FORMAT_PACKAGE = 2;
/*      */   protected static final int NAMESPACE_FORMAT_CLASS_OR_PACKAGE = 3;
/*  112 */   private int _namespace_format = 0;
/*      */ 
/*  117 */   Expression _thisArgument = null;
/*      */   private String _className;
/*      */   private Class _clazz;
/*      */   private Method _chosenMethod;
/*      */   private Constructor _chosenConstructor;
/*      */   private MethodType _chosenMethodType;
/*      */   private boolean unresolvedExternal;
/*  130 */   private boolean _isExtConstructor = false;
/*      */ 
/*  133 */   private boolean _isStatic = false;
/*      */ 
/*  136 */   private static final MultiHashtable _internal2Java = new MultiHashtable();
/*      */ 
/*  139 */   private static final Hashtable _java2Internal = new Hashtable();
/*      */ 
/*  142 */   private static final Hashtable _extensionNamespaceTable = new Hashtable();
/*      */ 
/*  145 */   private static final Hashtable _extensionFunctionTable = new Hashtable();
/*      */ 
/*      */   public FunctionCall(QName fname, Vector arguments)
/*      */   {
/*  267 */     this._fname = fname;
/*  268 */     this._arguments = arguments;
/*  269 */     this._type = null;
/*      */   }
/*      */ 
/*      */   public FunctionCall(QName fname) {
/*  273 */     this(fname, EMPTY_ARG_LIST);
/*      */   }
/*      */ 
/*      */   public String getName() {
/*  277 */     return this._fname.toString();
/*      */   }
/*      */ 
/*      */   public void setParser(Parser parser) {
/*  281 */     super.setParser(parser);
/*  282 */     if (this._arguments != null) {
/*  283 */       int n = this._arguments.size();
/*  284 */       for (int i = 0; i < n; i++) {
/*  285 */         Expression exp = (Expression)this._arguments.elementAt(i);
/*  286 */         exp.setParser(parser);
/*  287 */         exp.setParent(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getClassNameFromUri(String uri)
/*      */   {
/*  294 */     String className = (String)_extensionNamespaceTable.get(uri);
/*      */ 
/*  296 */     if (className != null) {
/*  297 */       return className;
/*      */     }
/*  299 */     if (uri.startsWith("http://xml.apache.org/xalan/xsltc/java")) {
/*  300 */       int length = "http://xml.apache.org/xalan/xsltc/java".length() + 1;
/*  301 */       return uri.length() > length ? uri.substring(length) : "";
/*      */     }
/*  303 */     if (uri.startsWith("http://xml.apache.org/xalan/java")) {
/*  304 */       int length = "http://xml.apache.org/xalan/java".length() + 1;
/*  305 */       return uri.length() > length ? uri.substring(length) : "";
/*      */     }
/*  307 */     if (uri.startsWith("http://xml.apache.org/xslt/java")) {
/*  308 */       int length = "http://xml.apache.org/xslt/java".length() + 1;
/*  309 */       return uri.length() > length ? uri.substring(length) : "";
/*      */     }
/*      */ 
/*  312 */     int index = uri.lastIndexOf('/');
/*  313 */     return index > 0 ? uri.substring(index + 1) : uri;
/*      */   }
/*      */ 
/*      */   public Type typeCheck(SymbolTable stable)
/*      */     throws TypeCheckError
/*      */   {
/*  325 */     if (this._type != null) return this._type;
/*      */ 
/*  327 */     String namespace = this._fname.getNamespace();
/*  328 */     String local = this._fname.getLocalPart();
/*      */ 
/*  330 */     if (isExtension()) {
/*  331 */       this._fname = new QName(null, null, local);
/*  332 */       return typeCheckStandard(stable);
/*      */     }
/*  334 */     if (isStandard()) {
/*  335 */       return typeCheckStandard(stable);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  340 */       this._className = getClassNameFromUri(namespace);
/*      */ 
/*  342 */       int pos = local.lastIndexOf('.');
/*  343 */       if (pos > 0) {
/*  344 */         this._isStatic = true;
/*  345 */         if ((this._className != null) && (this._className.length() > 0)) {
/*  346 */           this._namespace_format = 2;
/*  347 */           this._className = (this._className + "." + local.substring(0, pos));
/*      */         }
/*      */         else {
/*  350 */           this._namespace_format = 0;
/*  351 */           this._className = local.substring(0, pos);
/*      */         }
/*      */ 
/*  354 */         this._fname = new QName(namespace, null, local.substring(pos + 1));
/*      */       }
/*      */       else {
/*  357 */         if ((this._className != null) && (this._className.length() > 0)) {
/*      */           try {
/*  359 */             this._clazz = ObjectFactory.findProviderClass(this._className, true);
/*  360 */             this._namespace_format = 1;
/*      */           }
/*      */           catch (ClassNotFoundException e) {
/*  363 */             this._namespace_format = 2;
/*      */           }
/*      */         }
/*      */         else {
/*  367 */           this._namespace_format = 0;
/*      */         }
/*  369 */         if (local.indexOf('-') > 0) {
/*  370 */           local = replaceDash(local);
/*      */         }
/*      */ 
/*  373 */         String extFunction = (String)_extensionFunctionTable.get(namespace + ":" + local);
/*  374 */         if (extFunction != null) {
/*  375 */           this._fname = new QName(null, null, extFunction);
/*  376 */           return typeCheckStandard(stable);
/*      */         }
/*      */ 
/*  379 */         this._fname = new QName(namespace, null, local);
/*      */       }
/*      */ 
/*  382 */       return typeCheckExternal(stable);
/*      */     }
/*      */     catch (TypeCheckError e) {
/*  385 */       ErrorMsg errorMsg = e.getErrorMsg();
/*  386 */       if (errorMsg == null) {
/*  387 */         String name = this._fname.getLocalPart();
/*  388 */         errorMsg = new ErrorMsg("METHOD_NOT_FOUND_ERR", name);
/*      */       }
/*  390 */       getParser().reportError(3, errorMsg);
/*  391 */     }return this._type = Type.Void;
/*      */   }
/*      */ 
/*      */   public Type typeCheckStandard(SymbolTable stable)
/*      */     throws TypeCheckError
/*      */   {
/*  402 */     this._fname.clearNamespace();
/*      */ 
/*  404 */     int n = this._arguments.size();
/*  405 */     Vector argsType = typeCheckArgs(stable);
/*  406 */     MethodType args = new MethodType(Type.Void, argsType);
/*  407 */     MethodType ptype = lookupPrimop(stable, this._fname.getLocalPart(), args);
/*      */ 
/*  410 */     if (ptype != null) {
/*  411 */       for (int i = 0; i < n; i++) {
/*  412 */         Type argType = (Type)ptype.argsType().elementAt(i);
/*  413 */         Expression exp = (Expression)this._arguments.elementAt(i);
/*  414 */         if (!argType.identicalTo(exp.getType())) {
/*      */           try {
/*  416 */             this._arguments.setElementAt(new CastExpr(exp, argType), i);
/*      */           }
/*      */           catch (TypeCheckError e) {
/*  419 */             throw new TypeCheckError(this);
/*      */           }
/*      */         }
/*      */       }
/*  423 */       this._chosenMethodType = ptype;
/*  424 */       return this._type = ptype.resultType();
/*      */     }
/*  426 */     throw new TypeCheckError(this);
/*      */   }
/*      */ 
/*      */   public Type typeCheckConstructor(SymbolTable stable)
/*      */     throws TypeCheckError
/*      */   {
/*  432 */     Vector constructors = findConstructors();
/*  433 */     if (constructors == null)
/*      */     {
/*  435 */       throw new TypeCheckError("CONSTRUCTOR_NOT_FOUND", this._className);
/*      */     }
/*      */ 
/*  440 */     int nConstructors = constructors.size();
/*  441 */     int nArgs = this._arguments.size();
/*  442 */     Vector argsType = typeCheckArgs(stable);
/*      */ 
/*  445 */     int bestConstrDistance = 2147483647;
/*  446 */     this._type = null;
/*  447 */     for (int i = 0; i < nConstructors; i++)
/*      */     {
/*  449 */       Constructor constructor = (Constructor)constructors.elementAt(i);
/*      */ 
/*  451 */       Class[] paramTypes = constructor.getParameterTypes();
/*      */ 
/*  453 */       Class extType = null;
/*  454 */       int currConstrDistance = 0;
/*  455 */       for (int j = 0; j < nArgs; j++)
/*      */       {
/*  457 */         extType = paramTypes[j];
/*  458 */         Type intType = (Type)argsType.elementAt(j);
/*  459 */         Object match = _internal2Java.maps(intType, extType);
/*  460 */         if (match != null) {
/*  461 */           currConstrDistance += ((JavaType)match).distance;
/*      */         }
/*  463 */         else if ((intType instanceof ObjectType)) {
/*  464 */           ObjectType objectType = (ObjectType)intType;
/*  465 */           if (objectType.getJavaClass() != extType)
/*      */           {
/*  467 */             if (extType.isAssignableFrom(objectType.getJavaClass())) {
/*  468 */               currConstrDistance++;
/*      */             } else {
/*  470 */               currConstrDistance = 2147483647;
/*  471 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  476 */           currConstrDistance = 2147483647;
/*  477 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  481 */       if ((j == nArgs) && (currConstrDistance < bestConstrDistance)) {
/*  482 */         this._chosenConstructor = constructor;
/*  483 */         this._isExtConstructor = true;
/*  484 */         bestConstrDistance = currConstrDistance;
/*      */ 
/*  486 */         this._type = (this._clazz != null ? Type.newObjectType(this._clazz) : Type.newObjectType(this._className));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  491 */     if (this._type != null) {
/*  492 */       return this._type;
/*      */     }
/*      */ 
/*  495 */     throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(argsType));
/*      */   }
/*      */ 
/*      */   public Type typeCheckExternal(SymbolTable stable)
/*      */     throws TypeCheckError
/*      */   {
/*  507 */     int nArgs = this._arguments.size();
/*  508 */     String name = this._fname.getLocalPart();
/*      */ 
/*  511 */     if (this._fname.getLocalPart().equals("new")) {
/*  512 */       return typeCheckConstructor(stable);
/*      */     }
/*      */ 
/*  516 */     boolean hasThisArgument = false;
/*      */ 
/*  518 */     if (nArgs == 0) {
/*  519 */       this._isStatic = true;
/*      */     }
/*  521 */     if (!this._isStatic) {
/*  522 */       if ((this._namespace_format == 0) || (this._namespace_format == 2))
/*      */       {
/*  524 */         hasThisArgument = true;
/*      */       }
/*  526 */       Expression firstArg = (Expression)this._arguments.elementAt(0);
/*  527 */       Type firstArgType = firstArg.typeCheck(stable);
/*      */ 
/*  529 */       if ((this._namespace_format == 1) && ((firstArgType instanceof ObjectType)) && (this._clazz != null) && (this._clazz.isAssignableFrom(((ObjectType)firstArgType).getJavaClass())))
/*      */       {
/*  533 */         hasThisArgument = true;
/*      */       }
/*  535 */       if (hasThisArgument) {
/*  536 */         this._thisArgument = ((Expression)this._arguments.elementAt(0));
/*  537 */         this._arguments.remove(0); nArgs--;
/*  538 */         if ((firstArgType instanceof ObjectType)) {
/*  539 */           this._className = ((ObjectType)firstArgType).getJavaClassName();
/*      */         }
/*      */         else
/*  542 */           throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", name);
/*      */       }
/*      */     }
/*  545 */     else if (this._className.length() == 0)
/*      */     {
/*  552 */       Parser parser = getParser();
/*  553 */       if (parser != null) {
/*  554 */         reportWarning(this, parser, "FUNCTION_RESOLVE_ERR", this._fname.toString());
/*      */       }
/*      */ 
/*  557 */       this.unresolvedExternal = true;
/*  558 */       return this._type = Type.Int;
/*      */     }
/*      */ 
/*  562 */     Vector methods = findMethods();
/*      */ 
/*  564 */     if (methods == null)
/*      */     {
/*  566 */       throw new TypeCheckError("METHOD_NOT_FOUND_ERR", this._className + "." + name);
/*      */     }
/*      */ 
/*  569 */     Class extType = null;
/*  570 */     int nMethods = methods.size();
/*  571 */     Vector argsType = typeCheckArgs(stable);
/*      */ 
/*  574 */     int bestMethodDistance = 2147483647;
/*  575 */     this._type = null;
/*  576 */     for (int i = 0; i < nMethods; i++)
/*      */     {
/*  578 */       Method method = (Method)methods.elementAt(i);
/*  579 */       Class[] paramTypes = method.getParameterTypes();
/*      */ 
/*  581 */       int currMethodDistance = 0;
/*  582 */       for (int j = 0; j < nArgs; j++)
/*      */       {
/*  584 */         extType = paramTypes[j];
/*  585 */         Type intType = (Type)argsType.elementAt(j);
/*  586 */         Object match = _internal2Java.maps(intType, extType);
/*  587 */         if (match != null) {
/*  588 */           currMethodDistance += ((JavaType)match).distance;
/*      */         }
/*  595 */         else if ((intType instanceof ReferenceType)) {
/*  596 */           currMethodDistance++;
/*      */         }
/*  598 */         else if ((intType instanceof ObjectType)) {
/*  599 */           ObjectType object = (ObjectType)intType;
/*  600 */           if (extType.getName().equals(object.getJavaClassName())) {
/*  601 */             currMethodDistance += 0;
/*  602 */           } else if (extType.isAssignableFrom(object.getJavaClass())) {
/*  603 */             currMethodDistance++;
/*      */           } else {
/*  605 */             currMethodDistance = 2147483647;
/*  606 */             break;
/*      */           }
/*      */         }
/*      */         else {
/*  610 */           currMethodDistance = 2147483647;
/*  611 */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  616 */       if (j == nArgs)
/*      */       {
/*  618 */         extType = method.getReturnType();
/*      */ 
/*  620 */         this._type = ((Type)_java2Internal.get(extType));
/*  621 */         if (this._type == null) {
/*  622 */           this._type = Type.newObjectType(extType);
/*      */         }
/*      */ 
/*  626 */         if ((this._type != null) && (currMethodDistance < bestMethodDistance)) {
/*  627 */           this._chosenMethod = method;
/*  628 */           bestMethodDistance = currMethodDistance;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  635 */     if ((this._chosenMethod != null) && (this._thisArgument == null) && (!Modifier.isStatic(this._chosenMethod.getModifiers())))
/*      */     {
/*  637 */       throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", getMethodSignature(argsType));
/*      */     }
/*      */ 
/*  640 */     if (this._type != null) {
/*  641 */       if (this._type == Type.NodeSet) {
/*  642 */         getXSLTC().setMultiDocument(true);
/*      */       }
/*  644 */       return this._type;
/*      */     }
/*      */ 
/*  647 */     throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(argsType));
/*      */   }
/*      */ 
/*      */   public Vector typeCheckArgs(SymbolTable stable)
/*      */     throws TypeCheckError
/*      */   {
/*  654 */     Vector result = new Vector();
/*  655 */     Enumeration e = this._arguments.elements();
/*  656 */     while (e.hasMoreElements()) {
/*  657 */       Expression exp = (Expression)e.nextElement();
/*  658 */       result.addElement(exp.typeCheck(stable));
/*      */     }
/*  660 */     return result;
/*      */   }
/*      */ 
/*      */   protected final Expression argument(int i) {
/*  664 */     return (Expression)this._arguments.elementAt(i);
/*      */   }
/*      */ 
/*      */   protected final Expression argument() {
/*  668 */     return argument(0);
/*      */   }
/*      */ 
/*      */   protected final int argumentCount() {
/*  672 */     return this._arguments.size();
/*      */   }
/*      */ 
/*      */   protected final void setArgument(int i, Expression exp) {
/*  676 */     this._arguments.setElementAt(exp, i);
/*      */   }
/*      */ 
/*      */   public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen)
/*      */   {
/*  686 */     Type type = Type.Boolean;
/*  687 */     if (this._chosenMethodType != null) {
/*  688 */       type = this._chosenMethodType.resultType();
/*      */     }
/*  690 */     InstructionList il = methodGen.getInstructionList();
/*  691 */     translate(classGen, methodGen);
/*      */ 
/*  693 */     if (((type instanceof BooleanType)) || ((type instanceof IntType)))
/*  694 */       this._falseList.add(il.append(new IFEQ(null)));
/*      */   }
/*      */ 
/*      */   public void translate(ClassGenerator classGen, MethodGenerator methodGen)
/*      */   {
/*  704 */     int n = argumentCount();
/*  705 */     ConstantPoolGen cpg = classGen.getConstantPool();
/*  706 */     InstructionList il = methodGen.getInstructionList();
/*  707 */     boolean isSecureProcessing = classGen.getParser().getXSLTC().isSecureProcessing();
/*      */ 
/*  711 */     if ((isStandard()) || (isExtension())) {
/*  712 */       for (int i = 0; i < n; i++) {
/*  713 */         Expression exp = argument(i);
/*  714 */         exp.translate(classGen, methodGen);
/*  715 */         exp.startIterator(classGen, methodGen);
/*      */       }
/*      */ 
/*  719 */       String name = this._fname.toString().replace('-', '_') + "F";
/*  720 */       String args = "";
/*      */ 
/*  723 */       if (name.equals("sumF")) {
/*  724 */         args = "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
/*  725 */         il.append(methodGen.loadDOM());
/*      */       }
/*  727 */       else if ((name.equals("normalize_spaceF")) && 
/*  728 */         (this._chosenMethodType.toSignature(args).equals("()Ljava/lang/String;")))
/*      */       {
/*  730 */         args = "ILcom/sun/org/apache/xalan/internal/xsltc/DOM;";
/*  731 */         il.append(methodGen.loadContextNode());
/*  732 */         il.append(methodGen.loadDOM());
/*      */       }
/*      */ 
/*  737 */       int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", name, this._chosenMethodType.toSignature(args));
/*      */ 
/*  739 */       il.append(new INVOKESTATIC(index));
/*      */     }
/*  743 */     else if (this.unresolvedExternal) {
/*  744 */       int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unresolved_externalF", "(Ljava/lang/String;)V");
/*      */ 
/*  747 */       il.append(new PUSH(cpg, this._fname.toString()));
/*  748 */       il.append(new INVOKESTATIC(index));
/*      */     }
/*  750 */     else if (this._isExtConstructor) {
/*  751 */       if (isSecureProcessing) {
/*  752 */         translateUnallowedExtension(cpg, il);
/*      */       }
/*  754 */       String clazz = this._chosenConstructor.getDeclaringClass().getName();
/*      */ 
/*  756 */       Class[] paramTypes = this._chosenConstructor.getParameterTypes();
/*  757 */       LocalVariableGen[] paramTemp = new LocalVariableGen[n];
/*      */ 
/*  768 */       for (int i = 0; i < n; i++) {
/*  769 */         Expression exp = argument(i);
/*  770 */         Type expType = exp.getType();
/*  771 */         exp.translate(classGen, methodGen);
/*      */ 
/*  773 */         exp.startIterator(classGen, methodGen);
/*  774 */         expType.translateTo(classGen, methodGen, paramTypes[i]);
/*  775 */         paramTemp[i] = methodGen.addLocalVariable("function_call_tmp" + i, expType.toJCType(), null, null);
/*      */ 
/*  779 */         paramTemp[i].setStart(il.append(expType.STORE(paramTemp[i].getIndex())));
/*      */       }
/*      */ 
/*  784 */       il.append(new NEW(cpg.addClass(this._className)));
/*  785 */       il.append(InstructionConstants.DUP);
/*      */ 
/*  787 */       for (int i = 0; i < n; i++) {
/*  788 */         Expression arg = argument(i);
/*  789 */         paramTemp[i].setEnd(il.append(arg.getType().LOAD(paramTemp[i].getIndex())));
/*      */       }
/*      */ 
/*  793 */       StringBuffer buffer = new StringBuffer();
/*  794 */       buffer.append('(');
/*  795 */       for (int i = 0; i < paramTypes.length; i++) {
/*  796 */         buffer.append(getSignature(paramTypes[i]));
/*      */       }
/*  798 */       buffer.append(')');
/*  799 */       buffer.append("V");
/*      */ 
/*  801 */       int index = cpg.addMethodref(clazz, "<init>", buffer.toString());
/*      */ 
/*  804 */       il.append(new INVOKESPECIAL(index));
/*      */ 
/*  807 */       Type.Object.translateFrom(classGen, methodGen, this._chosenConstructor.getDeclaringClass());
/*      */     }
/*      */     else
/*      */     {
/*  813 */       if (isSecureProcessing) {
/*  814 */         translateUnallowedExtension(cpg, il);
/*      */       }
/*  816 */       String clazz = this._chosenMethod.getDeclaringClass().getName();
/*  817 */       Class[] paramTypes = this._chosenMethod.getParameterTypes();
/*      */ 
/*  820 */       if (this._thisArgument != null) {
/*  821 */         this._thisArgument.translate(classGen, methodGen);
/*      */       }
/*      */ 
/*  824 */       for (int i = 0; i < n; i++) {
/*  825 */         Expression exp = argument(i);
/*  826 */         exp.translate(classGen, methodGen);
/*      */ 
/*  828 */         exp.startIterator(classGen, methodGen);
/*  829 */         exp.getType().translateTo(classGen, methodGen, paramTypes[i]);
/*      */       }
/*      */ 
/*  832 */       StringBuffer buffer = new StringBuffer();
/*  833 */       buffer.append('(');
/*  834 */       for (int i = 0; i < paramTypes.length; i++) {
/*  835 */         buffer.append(getSignature(paramTypes[i]));
/*      */       }
/*  837 */       buffer.append(')');
/*  838 */       buffer.append(getSignature(this._chosenMethod.getReturnType()));
/*      */ 
/*  840 */       if ((this._thisArgument != null) && (this._clazz.isInterface())) {
/*  841 */         int index = cpg.addInterfaceMethodref(clazz, this._fname.getLocalPart(), buffer.toString());
/*      */ 
/*  844 */         il.append(new INVOKEINTERFACE(index, n + 1));
/*      */       }
/*      */       else {
/*  847 */         int index = cpg.addMethodref(clazz, this._fname.getLocalPart(), buffer.toString());
/*      */ 
/*  850 */         il.append(this._thisArgument != null ? new INVOKEVIRTUAL(index) : new INVOKESTATIC(index));
/*      */       }
/*      */ 
/*  855 */       this._type.translateFrom(classGen, methodGen, this._chosenMethod.getReturnType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  861 */     return "funcall(" + this._fname + ", " + this._arguments + ')';
/*      */   }
/*      */ 
/*      */   public boolean isStandard() {
/*  865 */     String namespace = this._fname.getNamespace();
/*  866 */     return (namespace == null) || (namespace.equals(""));
/*      */   }
/*      */ 
/*      */   public boolean isExtension() {
/*  870 */     String namespace = this._fname.getNamespace();
/*  871 */     return (namespace != null) && (namespace.equals("http://xml.apache.org/xalan/xsltc"));
/*      */   }
/*      */ 
/*      */   private Vector findMethods()
/*      */   {
/*  881 */     Vector result = null;
/*  882 */     String namespace = this._fname.getNamespace();
/*      */ 
/*  884 */     if ((this._className != null) && (this._className.length() > 0)) {
/*  885 */       int nArgs = this._arguments.size();
/*      */       try {
/*  887 */         if (this._clazz == null) {
/*  888 */           this._clazz = ObjectFactory.findProviderClass(this._className, true);
/*      */ 
/*  890 */           if (this._clazz == null) {
/*  891 */             ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
/*      */ 
/*  893 */             getParser().reportError(3, msg);
/*      */           }
/*      */         }
/*      */ 
/*  897 */         String methodName = this._fname.getLocalPart();
/*  898 */         Method[] methods = this._clazz.getMethods();
/*      */ 
/*  900 */         for (int i = 0; i < methods.length; i++) {
/*  901 */           int mods = methods[i].getModifiers();
/*      */ 
/*  903 */           if ((Modifier.isPublic(mods)) && (methods[i].getName().equals(methodName)) && (methods[i].getParameterTypes().length == nArgs))
/*      */           {
/*  907 */             if (result == null) {
/*  908 */               result = new Vector();
/*      */             }
/*  910 */             result.addElement(methods[i]);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (ClassNotFoundException e) {
/*  915 */         ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
/*  916 */         getParser().reportError(3, msg);
/*      */       }
/*      */     }
/*  919 */     return result;
/*      */   }
/*      */ 
/*      */   private Vector findConstructors()
/*      */   {
/*  928 */     Vector result = null;
/*  929 */     String namespace = this._fname.getNamespace();
/*      */ 
/*  931 */     int nArgs = this._arguments.size();
/*      */     try {
/*  933 */       if (this._clazz == null) {
/*  934 */         this._clazz = ObjectFactory.findProviderClass(this._className, true);
/*      */ 
/*  936 */         if (this._clazz == null) {
/*  937 */           ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
/*  938 */           getParser().reportError(3, msg);
/*      */         }
/*      */       }
/*      */ 
/*  942 */       Constructor[] constructors = this._clazz.getConstructors();
/*      */ 
/*  944 */       for (int i = 0; i < constructors.length; i++) {
/*  945 */         int mods = constructors[i].getModifiers();
/*      */ 
/*  947 */         if ((Modifier.isPublic(mods)) && (constructors[i].getParameterTypes().length == nArgs))
/*      */         {
/*  950 */           if (result == null) {
/*  951 */             result = new Vector();
/*      */           }
/*  953 */           result.addElement(constructors[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  958 */       ErrorMsg msg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
/*  959 */       getParser().reportError(3, msg);
/*      */     }
/*      */ 
/*  962 */     return result;
/*      */   }
/*      */ 
/*      */   static final String getSignature(Class clazz)
/*      */   {
/*  970 */     if (clazz.isArray()) {
/*  971 */       StringBuffer sb = new StringBuffer();
/*  972 */       Class cl = clazz;
/*  973 */       while (cl.isArray()) {
/*  974 */         sb.append("[");
/*  975 */         cl = cl.getComponentType();
/*      */       }
/*  977 */       sb.append(getSignature(cl));
/*  978 */       return sb.toString();
/*      */     }
/*  980 */     if (clazz.isPrimitive()) {
/*  981 */       if (clazz == Integer.TYPE) {
/*  982 */         return "I";
/*      */       }
/*  984 */       if (clazz == Byte.TYPE) {
/*  985 */         return "B";
/*      */       }
/*  987 */       if (clazz == Long.TYPE) {
/*  988 */         return "J";
/*      */       }
/*  990 */       if (clazz == Float.TYPE) {
/*  991 */         return "F";
/*      */       }
/*  993 */       if (clazz == Double.TYPE) {
/*  994 */         return "D";
/*      */       }
/*  996 */       if (clazz == Short.TYPE) {
/*  997 */         return "S";
/*      */       }
/*  999 */       if (clazz == Character.TYPE) {
/* 1000 */         return "C";
/*      */       }
/* 1002 */       if (clazz == Boolean.TYPE) {
/* 1003 */         return "Z";
/*      */       }
/* 1005 */       if (clazz == Void.TYPE) {
/* 1006 */         return "V";
/*      */       }
/*      */ 
/* 1009 */       String name = clazz.toString();
/* 1010 */       ErrorMsg err = new ErrorMsg("UNKNOWN_SIG_TYPE_ERR", name);
/* 1011 */       throw new Error(err.toString());
/*      */     }
/*      */ 
/* 1015 */     return "L" + clazz.getName().replace('.', '/') + ';';
/*      */   }
/*      */ 
/*      */   static final String getSignature(Method meth)
/*      */   {
/* 1023 */     StringBuffer sb = new StringBuffer();
/* 1024 */     sb.append('(');
/* 1025 */     Class[] params = meth.getParameterTypes();
/* 1026 */     for (int j = 0; j < params.length; j++) {
/* 1027 */       sb.append(getSignature(params[j]));
/*      */     }
/* 1029 */     return ')' + getSignature(meth.getReturnType());
/*      */   }
/*      */ 
/*      */   static final String getSignature(Constructor cons)
/*      */   {
/* 1037 */     StringBuffer sb = new StringBuffer();
/* 1038 */     sb.append('(');
/* 1039 */     Class[] params = cons.getParameterTypes();
/* 1040 */     for (int j = 0; j < params.length; j++) {
/* 1041 */       sb.append(getSignature(params[j]));
/*      */     }
/* 1043 */     return ")V";
/*      */   }
/*      */ 
/*      */   private String getMethodSignature(Vector argsType)
/*      */   {
/* 1050 */     StringBuffer buf = new StringBuffer(this._className);
/* 1051 */     buf.append('.').append(this._fname.getLocalPart()).append('(');
/*      */ 
/* 1053 */     int nArgs = argsType.size();
/* 1054 */     for (int i = 0; i < nArgs; i++) {
/* 1055 */       Type intType = (Type)argsType.elementAt(i);
/* 1056 */       buf.append(intType.toString());
/* 1057 */       if (i < nArgs - 1) buf.append(", ");
/*      */     }
/*      */ 
/* 1060 */     buf.append(')');
/* 1061 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   protected static String replaceDash(String name)
/*      */   {
/* 1071 */     char dash = '-';
/* 1072 */     StringBuffer buff = new StringBuffer("");
/* 1073 */     for (int i = 0; i < name.length(); i++) {
/* 1074 */       if ((i > 0) && (name.charAt(i - 1) == dash))
/* 1075 */         buff.append(Character.toUpperCase(name.charAt(i)));
/* 1076 */       else if (name.charAt(i) != dash)
/* 1077 */         buff.append(name.charAt(i));
/*      */     }
/* 1079 */     return buff.toString();
/*      */   }
/*      */ 
/*      */   private void translateUnallowedExtension(ConstantPoolGen cpg, InstructionList il)
/*      */   {
/* 1088 */     int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_functionF", "(Ljava/lang/String;)V");
/*      */ 
/* 1091 */     il.append(new PUSH(cpg, this._fname.toString()));
/* 1092 */     il.append(new INVOKESTATIC(index));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  172 */       Class nodeClass = Class.forName("org.w3c.dom.Node");
/*  173 */       Class nodeListClass = Class.forName("org.w3c.dom.NodeList");
/*      */ 
/*  178 */       _internal2Java.put(Type.Boolean, new JavaType(Boolean.TYPE, 0));
/*  179 */       _internal2Java.put(Type.Boolean, new JavaType(Boolean.class, 1));
/*  180 */       _internal2Java.put(Type.Boolean, new JavaType(Object.class, 2));
/*      */ 
/*  184 */       _internal2Java.put(Type.Real, new JavaType(Double.TYPE, 0));
/*  185 */       _internal2Java.put(Type.Real, new JavaType(Double.class, 1));
/*  186 */       _internal2Java.put(Type.Real, new JavaType(Float.TYPE, 2));
/*  187 */       _internal2Java.put(Type.Real, new JavaType(Long.TYPE, 3));
/*  188 */       _internal2Java.put(Type.Real, new JavaType(Integer.TYPE, 4));
/*  189 */       _internal2Java.put(Type.Real, new JavaType(Short.TYPE, 5));
/*  190 */       _internal2Java.put(Type.Real, new JavaType(Byte.TYPE, 6));
/*  191 */       _internal2Java.put(Type.Real, new JavaType(Character.TYPE, 7));
/*  192 */       _internal2Java.put(Type.Real, new JavaType(Object.class, 8));
/*      */ 
/*  195 */       _internal2Java.put(Type.Int, new JavaType(Double.TYPE, 0));
/*  196 */       _internal2Java.put(Type.Int, new JavaType(Double.class, 1));
/*  197 */       _internal2Java.put(Type.Int, new JavaType(Float.TYPE, 2));
/*  198 */       _internal2Java.put(Type.Int, new JavaType(Long.TYPE, 3));
/*  199 */       _internal2Java.put(Type.Int, new JavaType(Integer.TYPE, 4));
/*  200 */       _internal2Java.put(Type.Int, new JavaType(Short.TYPE, 5));
/*  201 */       _internal2Java.put(Type.Int, new JavaType(Byte.TYPE, 6));
/*  202 */       _internal2Java.put(Type.Int, new JavaType(Character.TYPE, 7));
/*  203 */       _internal2Java.put(Type.Int, new JavaType(Object.class, 8));
/*      */ 
/*  206 */       _internal2Java.put(Type.String, new JavaType(String.class, 0));
/*  207 */       _internal2Java.put(Type.String, new JavaType(Object.class, 1));
/*      */ 
/*  210 */       _internal2Java.put(Type.NodeSet, new JavaType(nodeListClass, 0));
/*  211 */       _internal2Java.put(Type.NodeSet, new JavaType(nodeClass, 1));
/*  212 */       _internal2Java.put(Type.NodeSet, new JavaType(Object.class, 2));
/*  213 */       _internal2Java.put(Type.NodeSet, new JavaType(String.class, 3));
/*      */ 
/*  216 */       _internal2Java.put(Type.Node, new JavaType(nodeListClass, 0));
/*  217 */       _internal2Java.put(Type.Node, new JavaType(nodeClass, 1));
/*  218 */       _internal2Java.put(Type.Node, new JavaType(Object.class, 2));
/*  219 */       _internal2Java.put(Type.Node, new JavaType(String.class, 3));
/*      */ 
/*  222 */       _internal2Java.put(Type.ResultTree, new JavaType(nodeListClass, 0));
/*  223 */       _internal2Java.put(Type.ResultTree, new JavaType(nodeClass, 1));
/*  224 */       _internal2Java.put(Type.ResultTree, new JavaType(Object.class, 2));
/*  225 */       _internal2Java.put(Type.ResultTree, new JavaType(String.class, 3));
/*      */ 
/*  227 */       _internal2Java.put(Type.Reference, new JavaType(Object.class, 0));
/*      */ 
/*  230 */       _java2Internal.put(Boolean.TYPE, Type.Boolean);
/*  231 */       _java2Internal.put(Void.TYPE, Type.Void);
/*  232 */       _java2Internal.put(Character.TYPE, Type.Real);
/*  233 */       _java2Internal.put(Byte.TYPE, Type.Real);
/*  234 */       _java2Internal.put(Short.TYPE, Type.Real);
/*  235 */       _java2Internal.put(Integer.TYPE, Type.Real);
/*  236 */       _java2Internal.put(Long.TYPE, Type.Real);
/*  237 */       _java2Internal.put(Float.TYPE, Type.Real);
/*  238 */       _java2Internal.put(Double.TYPE, Type.Real);
/*      */ 
/*  240 */       _java2Internal.put(String.class, Type.String);
/*      */ 
/*  242 */       _java2Internal.put(Object.class, Type.Reference);
/*      */ 
/*  245 */       _java2Internal.put(nodeListClass, Type.NodeSet);
/*  246 */       _java2Internal.put(nodeClass, Type.NodeSet);
/*      */ 
/*  249 */       _extensionNamespaceTable.put("http://xml.apache.org/xalan", "com.sun.org.apache.xalan.internal.lib.Extensions");
/*  250 */       _extensionNamespaceTable.put("http://exslt.org/common", "com.sun.org.apache.xalan.internal.lib.ExsltCommon");
/*  251 */       _extensionNamespaceTable.put("http://exslt.org/math", "com.sun.org.apache.xalan.internal.lib.ExsltMath");
/*  252 */       _extensionNamespaceTable.put("http://exslt.org/sets", "com.sun.org.apache.xalan.internal.lib.ExsltSets");
/*  253 */       _extensionNamespaceTable.put("http://exslt.org/dates-and-times", "com.sun.org.apache.xalan.internal.lib.ExsltDatetime");
/*  254 */       _extensionNamespaceTable.put("http://exslt.org/strings", "com.sun.org.apache.xalan.internal.lib.ExsltStrings");
/*      */ 
/*  257 */       _extensionFunctionTable.put("http://exslt.org/common:nodeSet", "nodeset");
/*  258 */       _extensionFunctionTable.put("http://exslt.org/common:objectType", "objectType");
/*  259 */       _extensionFunctionTable.put("http://xml.apache.org/xalan:nodeset", "nodeset");
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  262 */       System.err.println(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class JavaType
/*      */   {
/*      */     public Class type;
/*      */     public int distance;
/*      */ 
/*      */     public JavaType(Class type, int distance)
/*      */     {
/*  156 */       this.type = type;
/*  157 */       this.distance = distance;
/*      */     }
/*      */     public boolean equals(Object query) {
/*  160 */       return query.equals(this.type);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.FunctionCall
 * JD-Core Version:    0.6.2
 */