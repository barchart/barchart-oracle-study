/*      */ package sun.org.mozilla.javascript.internal.optimizer;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import sun.org.mozilla.classfile.internal.ClassFileWriter;
/*      */ import sun.org.mozilla.javascript.internal.CompilerEnvirons;
/*      */ import sun.org.mozilla.javascript.internal.Context;
/*      */ import sun.org.mozilla.javascript.internal.Kit;
/*      */ import sun.org.mozilla.javascript.internal.Node;
/*      */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.Jump;
/*      */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*      */ 
/*      */ class BodyCodegen
/*      */ {
/*      */   private static final int JAVASCRIPT_EXCEPTION = 0;
/*      */   private static final int EVALUATOR_EXCEPTION = 1;
/*      */   private static final int ECMAERROR_EXCEPTION = 2;
/*      */   private static final int THROWABLE_EXCEPTION = 3;
/*      */   static final int GENERATOR_TERMINATE = -1;
/*      */   static final int GENERATOR_START = 0;
/*      */   static final int GENERATOR_YIELD_START = 1;
/*      */   ClassFileWriter cfw;
/*      */   Codegen codegen;
/*      */   CompilerEnvirons compilerEnv;
/*      */   ScriptNode scriptOrFn;
/*      */   public int scriptOrFnIndex;
/*      */   private int savedCodeOffset;
/*      */   private OptFunctionNode fnCurrent;
/*      */   private boolean isTopLevel;
/*      */   private static final int MAX_LOCALS = 256;
/*      */   private int[] locals;
/*      */   private short firstFreeLocal;
/*      */   private short localsMax;
/*      */   private int itsLineNumber;
/*      */   private boolean hasVarsInRegs;
/*      */   private short[] varRegisters;
/*      */   private boolean inDirectCallFunction;
/*      */   private boolean itsForcedObjectParameters;
/*      */   private int enterAreaStartLabel;
/*      */   private int epilogueLabel;
/*      */   private short variableObjectLocal;
/*      */   private short popvLocal;
/*      */   private short contextLocal;
/*      */   private short argsLocal;
/*      */   private short operationLocal;
/*      */   private short thisObjLocal;
/*      */   private short funObjLocal;
/*      */   private short itsZeroArgArray;
/*      */   private short itsOneArgArray;
/*      */   private short scriptRegexpLocal;
/*      */   private short generatorStateLocal;
/*      */   private boolean isGenerator;
/*      */   private int generatorSwitch;
/*      */   private int maxLocals;
/*      */   private int maxStack;
/*      */   private Map<Node, FinallyReturnPoint> finallys;
/*      */ 
/*      */   BodyCodegen()
/*      */   {
/* 5096 */     this.maxLocals = 0;
/* 5097 */     this.maxStack = 0;
/*      */   }
/*      */ 
/*      */   void generateBodyCode()
/*      */   {
/* 1362 */     this.isGenerator = Codegen.isGenerator(this.scriptOrFn);
/*      */ 
/* 1365 */     initBodyGeneration();
/*      */     Object localObject;
/* 1367 */     if (this.isGenerator)
/*      */     {
/* 1371 */       localObject = "(" + this.codegen.mainClassSignature + "Lsun/org/mozilla/javascript/internal/Context;" + "Lsun/org/mozilla/javascript/internal/Scriptable;" + "Ljava/lang/Object;" + "Ljava/lang/Object;I)Ljava/lang/Object;";
/*      */ 
/* 1377 */       this.cfw.startMethod(this.codegen.getBodyMethodName(this.scriptOrFn) + "_gen", (String)localObject, (short)10);
/*      */     }
/*      */     else
/*      */     {
/* 1382 */       this.cfw.startMethod(this.codegen.getBodyMethodName(this.scriptOrFn), this.codegen.getBodyMethodSignature(this.scriptOrFn), (short)10);
/*      */     }
/*      */ 
/* 1388 */     generatePrologue();
/*      */ 
/* 1390 */     if (this.fnCurrent != null)
/* 1391 */       localObject = this.scriptOrFn.getLastChild();
/*      */     else {
/* 1393 */       localObject = this.scriptOrFn;
/*      */     }
/* 1395 */     generateStatement((Node)localObject);
/* 1396 */     generateEpilogue();
/*      */ 
/* 1398 */     this.cfw.stopMethod((short)(this.localsMax + 1));
/*      */ 
/* 1400 */     if (this.isGenerator)
/*      */     {
/* 1403 */       generateGenerator();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateGenerator()
/*      */   {
/* 1411 */     this.cfw.startMethod(this.codegen.getBodyMethodName(this.scriptOrFn), this.codegen.getBodyMethodSignature(this.scriptOrFn), (short)10);
/*      */ 
/* 1416 */     initBodyGeneration();
/* 1417 */     this.argsLocal = (this.firstFreeLocal++);
/* 1418 */     this.localsMax = this.firstFreeLocal;
/*      */ 
/* 1421 */     if ((this.fnCurrent != null) && (!this.inDirectCallFunction) && ((!this.compilerEnv.isUseDynamicScope()) || (this.fnCurrent.fnode.getIgnoreDynamicScope())))
/*      */     {
/* 1427 */       this.cfw.addALoad(this.funObjLocal);
/* 1428 */       this.cfw.addInvoke(185, "sun/org/mozilla/javascript/internal/Scriptable", "getParentScope", "()Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1432 */       this.cfw.addAStore(this.variableObjectLocal);
/*      */     }
/*      */ 
/* 1436 */     this.cfw.addALoad(this.funObjLocal);
/* 1437 */     this.cfw.addALoad(this.variableObjectLocal);
/* 1438 */     this.cfw.addALoad(this.argsLocal);
/* 1439 */     addScriptRuntimeInvoke("createFunctionActivation", "(Lsun/org/mozilla/javascript/internal/NativeFunction;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1444 */     this.cfw.addAStore(this.variableObjectLocal);
/*      */ 
/* 1447 */     this.cfw.add(187, this.codegen.mainClassName);
/*      */ 
/* 1449 */     this.cfw.add(89);
/* 1450 */     this.cfw.addALoad(this.variableObjectLocal);
/* 1451 */     this.cfw.addALoad(this.contextLocal);
/* 1452 */     this.cfw.addPush(this.scriptOrFnIndex);
/* 1453 */     this.cfw.addInvoke(183, this.codegen.mainClassName, "<init>", "(Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;I)V");
/*      */ 
/* 1457 */     this.cfw.add(89);
/* 1458 */     if (this.isTopLevel) Kit.codeBug();
/* 1459 */     this.cfw.add(42);
/* 1460 */     this.cfw.add(180, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */ 
/* 1464 */     this.cfw.add(181, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */ 
/* 1469 */     generateNestedFunctionInits();
/*      */ 
/* 1472 */     this.cfw.addALoad(this.variableObjectLocal);
/* 1473 */     this.cfw.addALoad(this.thisObjLocal);
/* 1474 */     this.cfw.addLoadConstant(this.maxLocals);
/* 1475 */     this.cfw.addLoadConstant(this.maxStack);
/* 1476 */     addOptRuntimeInvoke("createNativeGenerator", "(Lsun/org/mozilla/javascript/internal/NativeFunction;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;II)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1482 */     this.cfw.add(176);
/* 1483 */     this.cfw.stopMethod((short)(this.localsMax + 1));
/*      */   }
/*      */ 
/*      */   private void generateNestedFunctionInits()
/*      */   {
/* 1488 */     int i = this.scriptOrFn.getFunctionCount();
/* 1489 */     for (int j = 0; j != i; j++) {
/* 1490 */       OptFunctionNode localOptFunctionNode = OptFunctionNode.get(this.scriptOrFn, j);
/* 1491 */       if (localOptFunctionNode.fnode.getFunctionType() == 1)
/*      */       {
/* 1494 */         visitFunction(localOptFunctionNode, 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initBodyGeneration()
/*      */   {
/* 1501 */     this.isTopLevel = (this.scriptOrFn == this.codegen.scriptOrFnNodes[0]);
/*      */ 
/* 1503 */     this.varRegisters = null;
/* 1504 */     if (this.scriptOrFn.getType() == 109) {
/* 1505 */       this.fnCurrent = OptFunctionNode.get(this.scriptOrFn);
/* 1506 */       this.hasVarsInRegs = (!this.fnCurrent.fnode.requiresActivation());
/* 1507 */       if (this.hasVarsInRegs) {
/* 1508 */         int i = this.fnCurrent.fnode.getParamAndVarCount();
/* 1509 */         if (i != 0) {
/* 1510 */           this.varRegisters = new short[i];
/*      */         }
/*      */       }
/* 1513 */       this.inDirectCallFunction = this.fnCurrent.isTargetOfDirectCall();
/* 1514 */       if ((this.inDirectCallFunction) && (!this.hasVarsInRegs)) Codegen.badTree(); 
/*      */     }
/* 1516 */     else { this.fnCurrent = null;
/* 1517 */       this.hasVarsInRegs = false;
/* 1518 */       this.inDirectCallFunction = false;
/*      */     }
/*      */ 
/* 1521 */     this.locals = new int[256];
/*      */ 
/* 1523 */     this.funObjLocal = 0;
/* 1524 */     this.contextLocal = 1;
/* 1525 */     this.variableObjectLocal = 2;
/* 1526 */     this.thisObjLocal = 3;
/* 1527 */     this.localsMax = 4;
/* 1528 */     this.firstFreeLocal = 4;
/*      */ 
/* 1530 */     this.popvLocal = -1;
/* 1531 */     this.argsLocal = -1;
/* 1532 */     this.itsZeroArgArray = -1;
/* 1533 */     this.itsOneArgArray = -1;
/* 1534 */     this.scriptRegexpLocal = -1;
/* 1535 */     this.epilogueLabel = -1;
/* 1536 */     this.enterAreaStartLabel = -1;
/* 1537 */     this.generatorStateLocal = -1;
/*      */   }
/*      */ 
/*      */   private void generatePrologue()
/*      */   {
/*      */     int k;
/*      */     int m;
/* 1545 */     if (this.inDirectCallFunction) {
/* 1546 */       int i = this.scriptOrFn.getParamCount();
/*      */ 
/* 1551 */       if (this.firstFreeLocal != 4) Kit.codeBug();
/* 1552 */       for (k = 0; k != i; k++) {
/* 1553 */         this.varRegisters[k] = this.firstFreeLocal;
/*      */ 
/* 1555 */         this.firstFreeLocal = ((short)(this.firstFreeLocal + 3));
/*      */       }
/* 1557 */       if (!this.fnCurrent.getParameterNumberContext())
/*      */       {
/* 1559 */         this.itsForcedObjectParameters = true;
/* 1560 */         for (k = 0; k != i; k++) {
/* 1561 */           m = this.varRegisters[k];
/* 1562 */           this.cfw.addALoad(m);
/* 1563 */           this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 1567 */           int n = this.cfw.acquireLabel();
/* 1568 */           this.cfw.add(166, n);
/* 1569 */           this.cfw.addDLoad(m + 1);
/* 1570 */           addDoubleWrap();
/* 1571 */           this.cfw.addAStore(m);
/* 1572 */           this.cfw.markLabel(n);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1577 */     if ((this.fnCurrent != null) && (!this.inDirectCallFunction) && ((!this.compilerEnv.isUseDynamicScope()) || (this.fnCurrent.fnode.getIgnoreDynamicScope())))
/*      */     {
/* 1583 */       this.cfw.addALoad(this.funObjLocal);
/* 1584 */       this.cfw.addInvoke(185, "sun/org/mozilla/javascript/internal/Scriptable", "getParentScope", "()Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1588 */       this.cfw.addAStore(this.variableObjectLocal);
/*      */     }
/*      */ 
/* 1592 */     this.argsLocal = (this.firstFreeLocal++);
/* 1593 */     this.localsMax = this.firstFreeLocal;
/*      */ 
/* 1596 */     if (this.isGenerator)
/*      */     {
/* 1599 */       this.operationLocal = (this.firstFreeLocal++);
/* 1600 */       this.localsMax = this.firstFreeLocal;
/*      */ 
/* 1606 */       this.cfw.addALoad(this.thisObjLocal);
/* 1607 */       this.generatorStateLocal = (this.firstFreeLocal++);
/* 1608 */       this.localsMax = this.firstFreeLocal;
/* 1609 */       this.cfw.add(192, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime$GeneratorState");
/* 1610 */       this.cfw.add(89);
/* 1611 */       this.cfw.addAStore(this.generatorStateLocal);
/* 1612 */       this.cfw.add(180, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime$GeneratorState", "thisObj", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1616 */       this.cfw.addAStore(this.thisObjLocal);
/*      */ 
/* 1618 */       if (this.epilogueLabel == -1) {
/* 1619 */         this.epilogueLabel = this.cfw.acquireLabel();
/*      */       }
/*      */ 
/* 1622 */       List localList = ((FunctionNode)this.scriptOrFn).getResumptionPoints();
/* 1623 */       if (localList != null)
/*      */       {
/* 1625 */         generateGetGeneratorResumptionPoint();
/*      */ 
/* 1628 */         this.generatorSwitch = this.cfw.addTableSwitch(0, localList.size() + 0);
/*      */ 
/* 1630 */         generateCheckForThrowOrClose(-1, false, 0);
/*      */       }
/*      */     }
/*      */ 
/* 1634 */     if (this.fnCurrent == null)
/*      */     {
/* 1636 */       if (this.scriptOrFn.getRegexpCount() != 0) {
/* 1637 */         this.scriptRegexpLocal = getNewWordLocal();
/* 1638 */         this.codegen.pushRegExpArray(this.cfw, this.scriptOrFn, this.contextLocal, this.variableObjectLocal);
/*      */ 
/* 1640 */         this.cfw.addAStore(this.scriptRegexpLocal);
/*      */       }
/*      */     }
/*      */ 
/* 1644 */     if (this.compilerEnv.isGenerateObserverCount()) {
/* 1645 */       saveCurrentCodeOffset();
/*      */     }
/* 1647 */     if (this.hasVarsInRegs)
/*      */     {
/* 1649 */       int j = this.scriptOrFn.getParamCount();
/* 1650 */       if ((j > 0) && (!this.inDirectCallFunction))
/*      */       {
/* 1653 */         this.cfw.addALoad(this.argsLocal);
/* 1654 */         this.cfw.add(190);
/* 1655 */         this.cfw.addPush(j);
/* 1656 */         k = this.cfw.acquireLabel();
/* 1657 */         this.cfw.add(162, k);
/* 1658 */         this.cfw.addALoad(this.argsLocal);
/* 1659 */         this.cfw.addPush(j);
/* 1660 */         addScriptRuntimeInvoke("padArguments", "([Ljava/lang/Object;I)[Ljava/lang/Object;");
/*      */ 
/* 1663 */         this.cfw.addAStore(this.argsLocal);
/* 1664 */         this.cfw.markLabel(k);
/*      */       }
/*      */ 
/* 1667 */       k = this.fnCurrent.fnode.getParamCount();
/* 1668 */       m = this.fnCurrent.fnode.getParamAndVarCount();
/* 1669 */       boolean[] arrayOfBoolean = this.fnCurrent.fnode.getParamAndVarConst();
/*      */ 
/* 1673 */       int i1 = -1;
/* 1674 */       for (int i2 = 0; i2 != m; i2++) {
/* 1675 */         int i3 = -1;
/* 1676 */         if (i2 < k) {
/* 1677 */           if (!this.inDirectCallFunction) {
/* 1678 */             i3 = getNewWordLocal();
/* 1679 */             this.cfw.addALoad(this.argsLocal);
/* 1680 */             this.cfw.addPush(i2);
/* 1681 */             this.cfw.add(50);
/* 1682 */             this.cfw.addAStore(i3);
/*      */           }
/* 1684 */         } else if (this.fnCurrent.isNumberVar(i2)) {
/* 1685 */           i3 = getNewWordPairLocal(arrayOfBoolean[i2]);
/* 1686 */           this.cfw.addPush(0.0D);
/* 1687 */           this.cfw.addDStore(i3);
/*      */         } else {
/* 1689 */           i3 = getNewWordLocal(arrayOfBoolean[i2]);
/* 1690 */           if (i1 == -1) {
/* 1691 */             Codegen.pushUndefined(this.cfw);
/* 1692 */             i1 = i3;
/*      */           } else {
/* 1694 */             this.cfw.addALoad(i1);
/*      */           }
/* 1696 */           this.cfw.addAStore(i3);
/*      */         }
/* 1698 */         if (i3 >= 0) {
/* 1699 */           if (arrayOfBoolean[i2] != 0) {
/* 1700 */             this.cfw.addPush(0);
/* 1701 */             this.cfw.addIStore(i3 + (this.fnCurrent.isNumberVar(i2) ? 2 : 1));
/*      */           }
/* 1703 */           this.varRegisters[i2] = i3;
/*      */         }
/*      */ 
/* 1707 */         if (this.compilerEnv.isGenerateDebugInfo()) {
/* 1708 */           String str2 = this.fnCurrent.fnode.getParamOrVarName(i2);
/* 1709 */           String str3 = this.fnCurrent.isNumberVar(i2) ? "D" : "Ljava/lang/Object;";
/*      */ 
/* 1711 */           int i4 = this.cfw.getCurrentCodeOffset();
/* 1712 */           if (i3 < 0) {
/* 1713 */             i3 = this.varRegisters[i2];
/*      */           }
/* 1715 */           this.cfw.addVariableDescriptor(str2, str3, i4, i3);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1720 */       return;
/*      */     }
/*      */ 
/* 1726 */     if (this.isGenerator)
/*      */       return;
/*      */     String str1;
/* 1731 */     if (this.fnCurrent != null) {
/* 1732 */       str1 = "activation";
/* 1733 */       this.cfw.addALoad(this.funObjLocal);
/* 1734 */       this.cfw.addALoad(this.variableObjectLocal);
/* 1735 */       this.cfw.addALoad(this.argsLocal);
/* 1736 */       addScriptRuntimeInvoke("createFunctionActivation", "(Lsun/org/mozilla/javascript/internal/NativeFunction;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1741 */       this.cfw.addAStore(this.variableObjectLocal);
/* 1742 */       this.cfw.addALoad(this.contextLocal);
/* 1743 */       this.cfw.addALoad(this.variableObjectLocal);
/* 1744 */       addScriptRuntimeInvoke("enterActivationFunction", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)V");
/*      */     }
/*      */     else
/*      */     {
/* 1749 */       str1 = "global";
/* 1750 */       this.cfw.addALoad(this.funObjLocal);
/* 1751 */       this.cfw.addALoad(this.thisObjLocal);
/* 1752 */       this.cfw.addALoad(this.contextLocal);
/* 1753 */       this.cfw.addALoad(this.variableObjectLocal);
/* 1754 */       this.cfw.addPush(0);
/* 1755 */       addScriptRuntimeInvoke("initScript", "(Lsun/org/mozilla/javascript/internal/NativeFunction;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Z)V");
/*      */     }
/*      */ 
/* 1764 */     this.enterAreaStartLabel = this.cfw.acquireLabel();
/* 1765 */     this.epilogueLabel = this.cfw.acquireLabel();
/* 1766 */     this.cfw.markLabel(this.enterAreaStartLabel);
/*      */ 
/* 1768 */     generateNestedFunctionInits();
/*      */ 
/* 1771 */     if (this.compilerEnv.isGenerateDebugInfo()) {
/* 1772 */       this.cfw.addVariableDescriptor(str1, "Lsun/org/mozilla/javascript/internal/Scriptable;", this.cfw.getCurrentCodeOffset(), this.variableObjectLocal);
/*      */     }
/*      */ 
/* 1777 */     if (this.fnCurrent == null)
/*      */     {
/* 1779 */       this.popvLocal = getNewWordLocal();
/* 1780 */       Codegen.pushUndefined(this.cfw);
/* 1781 */       this.cfw.addAStore(this.popvLocal);
/*      */ 
/* 1783 */       k = this.scriptOrFn.getEndLineno();
/* 1784 */       if (k != -1)
/* 1785 */         this.cfw.addLineNumberEntry((short)k);
/*      */     }
/*      */     else {
/* 1788 */       if (this.fnCurrent.itsContainsCalls0) {
/* 1789 */         this.itsZeroArgArray = getNewWordLocal();
/* 1790 */         this.cfw.add(178, "sun/org/mozilla/javascript/internal/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
/*      */ 
/* 1793 */         this.cfw.addAStore(this.itsZeroArgArray);
/*      */       }
/* 1795 */       if (this.fnCurrent.itsContainsCalls1) {
/* 1796 */         this.itsOneArgArray = getNewWordLocal();
/* 1797 */         this.cfw.addPush(1);
/* 1798 */         this.cfw.add(189, "java/lang/Object");
/* 1799 */         this.cfw.addAStore(this.itsOneArgArray);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateGetGeneratorResumptionPoint()
/*      */   {
/* 1806 */     this.cfw.addALoad(this.generatorStateLocal);
/* 1807 */     this.cfw.add(180, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime$GeneratorState", "resumptionPoint", "I");
/*      */   }
/*      */ 
/*      */   private void generateSetGeneratorResumptionPoint(int paramInt)
/*      */   {
/* 1815 */     this.cfw.addALoad(this.generatorStateLocal);
/* 1816 */     this.cfw.addLoadConstant(paramInt);
/* 1817 */     this.cfw.add(181, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime$GeneratorState", "resumptionPoint", "I");
/*      */   }
/*      */ 
/*      */   private void generateGetGeneratorStackState()
/*      */   {
/* 1825 */     this.cfw.addALoad(this.generatorStateLocal);
/* 1826 */     addOptRuntimeInvoke("getGeneratorStackState", "(Ljava/lang/Object;)[Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void generateEpilogue()
/*      */   {
/* 1832 */     if (this.compilerEnv.isGenerateObserverCount())
/* 1833 */       addInstructionCount();
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     int m;
/* 1834 */     if (this.isGenerator)
/*      */     {
/* 1836 */       Map localMap = ((FunctionNode)this.scriptOrFn).getLiveLocals();
/* 1837 */       if (localMap != null) {
/* 1838 */         localObject1 = ((FunctionNode)this.scriptOrFn).getResumptionPoints();
/* 1839 */         for (int j = 0; j < ((List)localObject1).size(); j++) {
/* 1840 */           localObject2 = (Node)((List)localObject1).get(j);
/* 1841 */           int[] arrayOfInt = (int[])localMap.get(localObject2);
/* 1842 */           if (arrayOfInt != null) {
/* 1843 */             this.cfw.markTableSwitchCase(this.generatorSwitch, getNextGeneratorState((Node)localObject2));
/*      */ 
/* 1845 */             generateGetGeneratorLocalsState();
/* 1846 */             for (m = 0; m < arrayOfInt.length; m++) {
/* 1847 */               this.cfw.add(89);
/* 1848 */               this.cfw.addLoadConstant(m);
/* 1849 */               this.cfw.add(50);
/* 1850 */               this.cfw.addAStore(arrayOfInt[m]);
/*      */             }
/* 1852 */             this.cfw.add(87);
/* 1853 */             this.cfw.add(167, getTargetLabel((Node)localObject2));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1859 */       if (this.finallys != null) {
/* 1860 */         for (localObject1 = this.finallys.keySet().iterator(); ((Iterator)localObject1).hasNext(); ) { Node localNode = (Node)((Iterator)localObject1).next();
/* 1861 */           if (localNode.getType() == 125) {
/* 1862 */             localObject2 = (FinallyReturnPoint)this.finallys.get(localNode);
/*      */ 
/* 1864 */             this.cfw.markLabel(((FinallyReturnPoint)localObject2).tableLabel, (short)1);
/*      */ 
/* 1867 */             int k = this.cfw.addTableSwitch(0, ((FinallyReturnPoint)localObject2).jsrPoints.size() - 1);
/*      */ 
/* 1869 */             m = 0;
/* 1870 */             this.cfw.markTableSwitchDefault(k);
/* 1871 */             for (int n = 0; n < ((FinallyReturnPoint)localObject2).jsrPoints.size(); n++)
/*      */             {
/* 1873 */               this.cfw.markTableSwitchCase(k, m);
/* 1874 */               this.cfw.add(167, ((Integer)((FinallyReturnPoint)localObject2).jsrPoints.get(n)).intValue());
/*      */ 
/* 1876 */               m++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1883 */     if (this.epilogueLabel != -1) {
/* 1884 */       this.cfw.markLabel(this.epilogueLabel);
/*      */     }
/*      */ 
/* 1887 */     if (this.hasVarsInRegs) {
/* 1888 */       this.cfw.add(176);
/* 1889 */       return;
/* 1890 */     }if (this.isGenerator) {
/* 1891 */       if (((FunctionNode)this.scriptOrFn).getResumptionPoints() != null) {
/* 1892 */         this.cfw.markTableSwitchDefault(this.generatorSwitch);
/*      */       }
/*      */ 
/* 1896 */       generateSetGeneratorResumptionPoint(-1);
/*      */ 
/* 1899 */       this.cfw.addALoad(this.variableObjectLocal);
/* 1900 */       addOptRuntimeInvoke("throwStopIteration", "(Ljava/lang/Object;)V");
/*      */ 
/* 1903 */       Codegen.pushUndefined(this.cfw);
/* 1904 */       this.cfw.add(176);
/*      */     }
/* 1906 */     else if (this.fnCurrent == null) {
/* 1907 */       this.cfw.addALoad(this.popvLocal);
/* 1908 */       this.cfw.add(176);
/*      */     } else {
/* 1910 */       generateActivationExit();
/* 1911 */       this.cfw.add(176);
/*      */ 
/* 1916 */       int i = this.cfw.acquireLabel();
/* 1917 */       this.cfw.markHandler(i);
/* 1918 */       short s = getNewWordLocal();
/* 1919 */       this.cfw.addAStore(s);
/*      */ 
/* 1923 */       generateActivationExit();
/*      */ 
/* 1925 */       this.cfw.addALoad(s);
/* 1926 */       releaseWordLocal(s);
/*      */ 
/* 1928 */       this.cfw.add(191);
/*      */ 
/* 1931 */       this.cfw.addExceptionHandler(this.enterAreaStartLabel, this.epilogueLabel, i, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateGetGeneratorLocalsState()
/*      */   {
/* 1937 */     this.cfw.addALoad(this.generatorStateLocal);
/* 1938 */     addOptRuntimeInvoke("getGeneratorLocalsState", "(Ljava/lang/Object;)[Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void generateActivationExit()
/*      */   {
/* 1944 */     if ((this.fnCurrent == null) || (this.hasVarsInRegs)) throw Kit.codeBug();
/* 1945 */     this.cfw.addALoad(this.contextLocal);
/* 1946 */     addScriptRuntimeInvoke("exitActivationFunction", "(Lsun/org/mozilla/javascript/internal/Context;)V");
/*      */   }
/*      */ 
/*      */   private void generateStatement(Node paramNode)
/*      */   {
/* 1952 */     updateLineNumber(paramNode);
/* 1953 */     int i = paramNode.getType();
/* 1954 */     Node localNode = paramNode.getFirstChild();
/* 1955 */     switch (i)
/*      */     {
/*      */     case 123:
/*      */     case 128:
/*      */     case 129:
/*      */     case 130:
/*      */     case 132:
/*      */     case 136:
/* 1963 */       if (this.compilerEnv.isGenerateObserverCount())
/*      */       {
/* 1966 */         addInstructionCount(1); } break;
/*      */     case 141:
/*      */     case 109:
/*      */     case 81:
/*      */     case 57:
/*      */     case 50:
/*      */     case 51:
/*      */     case 4:
/*      */     case 64:
/*      */     case 114:
/*      */     case 2:
/*      */     case 3:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 133:
/*      */     case 134:
/*      */     case 131:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 135:
/*      */     case 125:
/* 1968 */     case 160: } while (localNode != null) {
/* 1969 */       generateStatement(localNode);
/* 1970 */       localNode = localNode.getNext(); continue;
/*      */ 
/* 1975 */       int j = getNewWordLocal();
/* 1976 */       if (this.isGenerator) {
/* 1977 */         this.cfw.add(1);
/* 1978 */         this.cfw.addAStore(j);
/*      */       }
/* 1980 */       paramNode.putIntProp(2, j);
/* 1981 */       while (localNode != null) {
/* 1982 */         generateStatement(localNode);
/* 1983 */         localNode = localNode.getNext();
/*      */       }
/* 1985 */       releaseWordLocal((short)j);
/* 1986 */       paramNode.removeProp(2);
/* 1987 */       break;
/*      */ 
/* 1991 */       j = paramNode.getExistingIntProp(1);
/* 1992 */       OptFunctionNode localOptFunctionNode = OptFunctionNode.get(this.scriptOrFn, j);
/* 1993 */       int m = localOptFunctionNode.fnode.getFunctionType();
/* 1994 */       if (m == 3) {
/* 1995 */         visitFunction(localOptFunctionNode, m);
/*      */       }
/* 1997 */       else if (m != 1) {
/* 1998 */         throw Codegen.badTree();
/*      */ 
/* 2005 */         visitTryCatchFinally((Jump)paramNode, localNode);
/* 2006 */         break;
/*      */ 
/* 2011 */         this.cfw.setStackTop((short)0);
/*      */ 
/* 2013 */         j = getLocalBlockRegister(paramNode);
/* 2014 */         int k = paramNode.getExistingIntProp(14);
/*      */ 
/* 2017 */         Object localObject = localNode.getString();
/* 2018 */         localNode = localNode.getNext();
/* 2019 */         generateExpression(localNode, paramNode);
/* 2020 */         if (k == 0) {
/* 2021 */           this.cfw.add(1);
/*      */         }
/*      */         else {
/* 2024 */           this.cfw.addALoad(j);
/*      */         }
/* 2026 */         this.cfw.addPush((String)localObject);
/* 2027 */         this.cfw.addALoad(this.contextLocal);
/* 2028 */         this.cfw.addALoad(this.variableObjectLocal);
/*      */ 
/* 2030 */         addScriptRuntimeInvoke("newCatchScope", "(Ljava/lang/Throwable;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 2038 */         this.cfw.addAStore(j);
/*      */ 
/* 2040 */         break;
/*      */ 
/* 2043 */         generateExpression(localNode, paramNode);
/* 2044 */         if (this.compilerEnv.isGenerateObserverCount())
/* 2045 */           addInstructionCount();
/* 2046 */         generateThrowJavaScriptException();
/* 2047 */         break;
/*      */ 
/* 2050 */         if (this.compilerEnv.isGenerateObserverCount())
/* 2051 */           addInstructionCount();
/* 2052 */         this.cfw.addALoad(getLocalBlockRegister(paramNode));
/* 2053 */         this.cfw.add(191);
/* 2054 */         break;
/*      */ 
/* 2058 */         if (!this.isGenerator) {
/* 2059 */           if (localNode != null) {
/* 2060 */             generateExpression(localNode, paramNode);
/* 2061 */           } else if (i == 4) {
/* 2062 */             Codegen.pushUndefined(this.cfw);
/*      */           } else {
/* 2064 */             if (this.popvLocal < 0) throw Codegen.badTree();
/* 2065 */             this.cfw.addALoad(this.popvLocal);
/*      */           }
/*      */         }
/* 2068 */         if (this.compilerEnv.isGenerateObserverCount())
/* 2069 */           addInstructionCount();
/* 2070 */         if (this.epilogueLabel == -1) {
/* 2071 */           if (!this.hasVarsInRegs) throw Codegen.badTree();
/* 2072 */           this.epilogueLabel = this.cfw.acquireLabel();
/*      */         }
/* 2074 */         this.cfw.add(167, this.epilogueLabel);
/* 2075 */         break;
/*      */ 
/* 2078 */         if (this.compilerEnv.isGenerateObserverCount())
/* 2079 */           addInstructionCount();
/* 2080 */         visitSwitch((Jump)paramNode, localNode);
/* 2081 */         break;
/*      */ 
/* 2084 */         generateExpression(localNode, paramNode);
/* 2085 */         this.cfw.addALoad(this.contextLocal);
/* 2086 */         this.cfw.addALoad(this.variableObjectLocal);
/* 2087 */         addScriptRuntimeInvoke("enterWith", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 2093 */         this.cfw.addAStore(this.variableObjectLocal);
/* 2094 */         incReferenceWordLocal(this.variableObjectLocal);
/* 2095 */         break;
/*      */ 
/* 2098 */         this.cfw.addALoad(this.variableObjectLocal);
/* 2099 */         addScriptRuntimeInvoke("leaveWith", "(Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 2103 */         this.cfw.addAStore(this.variableObjectLocal);
/* 2104 */         decReferenceWordLocal(this.variableObjectLocal);
/* 2105 */         break;
/*      */ 
/* 2110 */         generateExpression(localNode, paramNode);
/* 2111 */         this.cfw.addALoad(this.contextLocal);
/* 2112 */         j = i == 59 ? 1 : i == 58 ? 0 : 2;
/*      */ 
/* 2117 */         this.cfw.addPush(j);
/* 2118 */         addScriptRuntimeInvoke("enumInit", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */ 
/* 2123 */         this.cfw.addAStore(getLocalBlockRegister(paramNode));
/* 2124 */         break;
/*      */ 
/* 2127 */         if (localNode.getType() == 56)
/*      */         {
/* 2130 */           visitSetVar(localNode, localNode.getFirstChild(), false);
/*      */         }
/* 2132 */         else if (localNode.getType() == 156)
/*      */         {
/* 2135 */           visitSetConstVar(localNode, localNode.getFirstChild(), false);
/*      */         }
/* 2137 */         else if (localNode.getType() == 72) {
/* 2138 */           generateYieldPoint(localNode, false);
/*      */         }
/*      */         else {
/* 2141 */           generateExpression(localNode, paramNode);
/* 2142 */           if (paramNode.getIntProp(8, -1) != -1) {
/* 2143 */             this.cfw.add(88);
/*      */           } else {
/* 2145 */             this.cfw.add(87);
/*      */ 
/* 2147 */             break;
/*      */ 
/* 2150 */             generateExpression(localNode, paramNode);
/* 2151 */             if (this.popvLocal < 0) {
/* 2152 */               this.popvLocal = getNewWordLocal();
/*      */             }
/* 2154 */             this.cfw.addAStore(this.popvLocal);
/* 2155 */             break;
/*      */ 
/* 2159 */             if (this.compilerEnv.isGenerateObserverCount())
/* 2160 */               addInstructionCount();
/* 2161 */             k = getTargetLabel(paramNode);
/* 2162 */             this.cfw.markLabel(k);
/* 2163 */             if (this.compilerEnv.isGenerateObserverCount()) {
/* 2164 */               saveCurrentCodeOffset();
/*      */             }
/* 2166 */             break;
/*      */ 
/* 2172 */             if (this.compilerEnv.isGenerateObserverCount())
/* 2173 */               addInstructionCount();
/* 2174 */             visitGoto((Jump)paramNode, i, localNode);
/* 2175 */             break;
/*      */ 
/* 2179 */             if (this.compilerEnv.isGenerateObserverCount()) {
/* 2180 */               saveCurrentCodeOffset();
/*      */             }
/*      */ 
/* 2183 */             this.cfw.setStackTop((short)1);
/*      */ 
/* 2186 */             k = getNewWordLocal();
/* 2187 */             if (this.isGenerator)
/* 2188 */               generateIntegerWrap();
/* 2189 */             this.cfw.addAStore(k);
/*      */ 
/* 2191 */             while (localNode != null) {
/* 2192 */               generateStatement(localNode);
/* 2193 */               localNode = localNode.getNext();
/*      */             }
/* 2195 */             if (this.isGenerator) {
/* 2196 */               this.cfw.addALoad(k);
/* 2197 */               this.cfw.add(192, "java/lang/Integer");
/* 2198 */               generateIntegerUnwrap();
/* 2199 */               localObject = (FinallyReturnPoint)this.finallys.get(paramNode);
/* 2200 */               ((FinallyReturnPoint)localObject).tableLabel = this.cfw.acquireLabel();
/* 2201 */               this.cfw.add(167, ((FinallyReturnPoint)localObject).tableLabel);
/*      */             } else {
/* 2203 */               this.cfw.add(169, k);
/*      */             }
/* 2205 */             releaseWordLocal((short)k);
/*      */ 
/* 2207 */             break;
/*      */ 
/* 2210 */             break;
/*      */ 
/* 2213 */             throw Codegen.badTree();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/* 2220 */   private void generateIntegerWrap() { this.cfw.addInvoke(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"); }
/*      */ 
/*      */ 
/*      */   private void generateIntegerUnwrap()
/*      */   {
/* 2227 */     this.cfw.addInvoke(182, "java/lang/Integer", "intValue", "()I");
/*      */   }
/*      */ 
/*      */   private void generateThrowJavaScriptException()
/*      */   {
/* 2234 */     this.cfw.add(187, "sun/org/mozilla/javascript/internal/JavaScriptException");
/*      */ 
/* 2236 */     this.cfw.add(90);
/* 2237 */     this.cfw.add(95);
/* 2238 */     this.cfw.addPush(this.scriptOrFn.getSourceName());
/* 2239 */     this.cfw.addPush(this.itsLineNumber);
/* 2240 */     this.cfw.addInvoke(183, "sun/org/mozilla/javascript/internal/JavaScriptException", "<init>", "(Ljava/lang/Object;Ljava/lang/String;I)V");
/*      */ 
/* 2245 */     this.cfw.add(191);
/*      */   }
/*      */ 
/*      */   private int getNextGeneratorState(Node paramNode)
/*      */   {
/* 2250 */     int i = ((FunctionNode)this.scriptOrFn).getResumptionPoints().indexOf(paramNode);
/*      */ 
/* 2252 */     return i + 1;
/*      */   }
/*      */ 
/*      */   private void generateExpression(Node paramNode1, Node paramNode2)
/*      */   {
/* 2257 */     int i = paramNode1.getType();
/* 2258 */     Object localObject1 = paramNode1.getFirstChild();
/*      */     int j;
/*      */     OptFunctionNode localOptFunctionNode;
/*      */     int i4;
/*      */     int m;
/*      */     int n;
/*      */     int i3;
/*      */     Object localObject3;
/*      */     Object localObject4;
/*      */     Object localObject2;
/* 2259 */     switch (i) {
/*      */     case 138:
/* 2261 */       break;
/*      */     case 109:
/* 2264 */       if ((this.fnCurrent != null) || (paramNode2.getType() != 136)) {
/* 2265 */         j = paramNode1.getExistingIntProp(1);
/* 2266 */         localOptFunctionNode = OptFunctionNode.get(this.scriptOrFn, j);
/*      */ 
/* 2268 */         i4 = localOptFunctionNode.fnode.getFunctionType();
/* 2269 */         if (i4 != 2) {
/* 2270 */           throw Codegen.badTree();
/*      */         }
/* 2272 */         visitFunction(localOptFunctionNode, i4);
/* 2273 */       }break;
/*      */     case 39:
/* 2278 */       this.cfw.addALoad(this.contextLocal);
/* 2279 */       this.cfw.addALoad(this.variableObjectLocal);
/* 2280 */       this.cfw.addPush(paramNode1.getString());
/* 2281 */       addScriptRuntimeInvoke("name", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Ljava/lang/Object;");
/*      */ 
/* 2288 */       break;
/*      */     case 30:
/*      */     case 38:
/* 2293 */       j = paramNode1.getIntProp(10, 0);
/*      */ 
/* 2295 */       if (j == 0)
/*      */       {
/* 2297 */         localOptFunctionNode = (OptFunctionNode)paramNode1.getProp(9);
/*      */ 
/* 2300 */         if (localOptFunctionNode != null)
/* 2301 */           visitOptimizedCall(paramNode1, localOptFunctionNode, i, (Node)localObject1);
/* 2302 */         else if (i == 38)
/* 2303 */           visitStandardCall(paramNode1, (Node)localObject1);
/*      */         else
/* 2305 */           visitStandardNew(paramNode1, (Node)localObject1);
/*      */       }
/*      */       else {
/* 2308 */         visitSpecialCall(paramNode1, i, j, (Node)localObject1);
/*      */       }
/*      */ 
/* 2311 */       break;
/*      */     case 70:
/* 2314 */       generateFunctionAndThisObj((Node)localObject1, paramNode1);
/*      */ 
/* 2316 */       localObject1 = ((Node)localObject1).getNext();
/* 2317 */       generateCallArgArray(paramNode1, (Node)localObject1, false);
/* 2318 */       this.cfw.addALoad(this.contextLocal);
/* 2319 */       addScriptRuntimeInvoke("callRef", "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/Ref;");
/*      */ 
/* 2326 */       break;
/*      */     case 40:
/* 2330 */       double d = paramNode1.getDouble();
/* 2331 */       if (paramNode1.getIntProp(8, -1) != -1)
/* 2332 */         this.cfw.addPush(d);
/*      */       else {
/* 2334 */         this.codegen.pushNumberAsObject(this.cfw, d);
/*      */       }
/*      */ 
/* 2337 */       break;
/*      */     case 41:
/* 2340 */       this.cfw.addPush(paramNode1.getString());
/* 2341 */       break;
/*      */     case 43:
/* 2344 */       this.cfw.addALoad(this.thisObjLocal);
/* 2345 */       break;
/*      */     case 63:
/* 2348 */       this.cfw.add(42);
/* 2349 */       break;
/*      */     case 42:
/* 2352 */       this.cfw.add(1);
/* 2353 */       break;
/*      */     case 45:
/* 2356 */       this.cfw.add(178, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
/*      */ 
/* 2358 */       break;
/*      */     case 44:
/* 2361 */       this.cfw.add(178, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
/*      */ 
/* 2363 */       break;
/*      */     case 48:
/* 2367 */       int k = paramNode1.getExistingIntProp(4);
/*      */ 
/* 2373 */       if (this.fnCurrent == null) {
/* 2374 */         this.cfw.addALoad(this.scriptRegexpLocal);
/*      */       } else {
/* 2376 */         this.cfw.addALoad(this.funObjLocal);
/* 2377 */         this.cfw.add(180, this.codegen.mainClassName, "_re", "[Ljava/lang/Object;");
/*      */       }
/*      */ 
/* 2381 */       this.cfw.addPush(k);
/* 2382 */       this.cfw.add(50);
/*      */ 
/* 2384 */       break;
/*      */     case 89:
/* 2387 */       Node localNode1 = ((Node)localObject1).getNext();
/* 2388 */       while (localNode1 != null) {
/* 2389 */         generateExpression((Node)localObject1, paramNode1);
/* 2390 */         this.cfw.add(87);
/* 2391 */         localObject1 = localNode1;
/* 2392 */         localNode1 = localNode1.getNext();
/*      */       }
/* 2394 */       generateExpression((Node)localObject1, paramNode1);
/* 2395 */       break;
/*      */     case 61:
/*      */     case 62:
/* 2400 */       m = getLocalBlockRegister(paramNode1);
/* 2401 */       this.cfw.addALoad(m);
/* 2402 */       if (i == 61) {
/* 2403 */         addScriptRuntimeInvoke("enumNext", "(Ljava/lang/Object;)Ljava/lang/Boolean;");
/*      */       }
/*      */       else {
/* 2406 */         this.cfw.addALoad(this.contextLocal);
/* 2407 */         addScriptRuntimeInvoke("enumId", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */ 
/* 2412 */       break;
/*      */     case 65:
/* 2416 */       visitArrayLiteral(paramNode1, (Node)localObject1);
/* 2417 */       break;
/*      */     case 66:
/* 2420 */       visitObjectLiteral(paramNode1, (Node)localObject1);
/* 2421 */       break;
/*      */     case 26:
/* 2424 */       m = this.cfw.acquireLabel();
/* 2425 */       int i2 = this.cfw.acquireLabel();
/* 2426 */       i4 = this.cfw.acquireLabel();
/* 2427 */       generateIfJump((Node)localObject1, paramNode1, m, i2);
/*      */ 
/* 2429 */       this.cfw.markLabel(m);
/* 2430 */       this.cfw.add(178, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
/*      */ 
/* 2432 */       this.cfw.add(167, i4);
/* 2433 */       this.cfw.markLabel(i2);
/* 2434 */       this.cfw.add(178, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
/*      */ 
/* 2436 */       this.cfw.markLabel(i4);
/* 2437 */       this.cfw.adjustStackTop(-1);
/* 2438 */       break;
/*      */     case 27:
/* 2442 */       generateExpression((Node)localObject1, paramNode1);
/* 2443 */       addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
/* 2444 */       this.cfw.addPush(-1);
/* 2445 */       this.cfw.add(130);
/* 2446 */       this.cfw.add(135);
/* 2447 */       addDoubleWrap();
/* 2448 */       break;
/*      */     case 126:
/* 2451 */       generateExpression((Node)localObject1, paramNode1);
/* 2452 */       this.cfw.add(87);
/* 2453 */       Codegen.pushUndefined(this.cfw);
/* 2454 */       break;
/*      */     case 32:
/* 2457 */       generateExpression((Node)localObject1, paramNode1);
/* 2458 */       addScriptRuntimeInvoke("typeof", "(Ljava/lang/Object;)Ljava/lang/String;");
/*      */ 
/* 2461 */       break;
/*      */     case 137:
/* 2464 */       visitTypeofname(paramNode1);
/* 2465 */       break;
/*      */     case 106:
/*      */     case 107:
/* 2469 */       visitIncDec(paramNode1);
/* 2470 */       break;
/*      */     case 104:
/*      */     case 105:
/* 2474 */       generateExpression((Node)localObject1, paramNode1);
/* 2475 */       this.cfw.add(89);
/* 2476 */       addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
/*      */ 
/* 2478 */       m = this.cfw.acquireLabel();
/* 2479 */       if (i == 105)
/* 2480 */         this.cfw.add(153, m);
/*      */       else
/* 2482 */         this.cfw.add(154, m);
/* 2483 */       this.cfw.add(87);
/* 2484 */       generateExpression(((Node)localObject1).getNext(), paramNode1);
/* 2485 */       this.cfw.markLabel(m);
/*      */ 
/* 2487 */       break;
/*      */     case 102:
/* 2490 */       Node localNode2 = ((Node)localObject1).getNext();
/* 2491 */       Node localNode3 = localNode2.getNext();
/* 2492 */       generateExpression((Node)localObject1, paramNode1);
/* 2493 */       addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
/*      */ 
/* 2495 */       i4 = this.cfw.acquireLabel();
/* 2496 */       this.cfw.add(153, i4);
/* 2497 */       short s = this.cfw.getStackTop();
/* 2498 */       generateExpression(localNode2, paramNode1);
/* 2499 */       int i5 = this.cfw.acquireLabel();
/* 2500 */       this.cfw.add(167, i5);
/* 2501 */       this.cfw.markLabel(i4, s);
/* 2502 */       generateExpression(localNode3, paramNode1);
/* 2503 */       this.cfw.markLabel(i5);
/*      */ 
/* 2505 */       break;
/*      */     case 21:
/* 2508 */       generateExpression((Node)localObject1, paramNode1);
/* 2509 */       generateExpression(((Node)localObject1).getNext(), paramNode1);
/* 2510 */       switch (paramNode1.getIntProp(8, -1)) {
/*      */       case 0:
/* 2512 */         this.cfw.add(99);
/* 2513 */         break;
/*      */       case 1:
/* 2515 */         addOptRuntimeInvoke("add", "(DLjava/lang/Object;)Ljava/lang/Object;");
/*      */ 
/* 2517 */         break;
/*      */       case 2:
/* 2519 */         addOptRuntimeInvoke("add", "(Ljava/lang/Object;D)Ljava/lang/Object;");
/*      */ 
/* 2521 */         break;
/*      */       default:
/* 2523 */         if (((Node)localObject1).getType() == 41) {
/* 2524 */           addScriptRuntimeInvoke("add", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
/*      */         }
/* 2528 */         else if (((Node)localObject1).getNext().getType() == 41) {
/* 2529 */           addScriptRuntimeInvoke("add", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;");
/*      */         }
/*      */         else
/*      */         {
/* 2534 */           this.cfw.addALoad(this.contextLocal);
/* 2535 */           addScriptRuntimeInvoke("add", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */         }
/*      */ 
/*      */         break;
/*      */       }
/*      */ 
/* 2543 */       break;
/*      */     case 23:
/* 2546 */       visitArithmetic(paramNode1, 107, (Node)localObject1, paramNode2);
/* 2547 */       break;
/*      */     case 22:
/* 2550 */       visitArithmetic(paramNode1, 103, (Node)localObject1, paramNode2);
/* 2551 */       break;
/*      */     case 24:
/*      */     case 25:
/* 2555 */       visitArithmetic(paramNode1, i == 24 ? 111 : 115, (Node)localObject1, paramNode2);
/*      */ 
/* 2558 */       break;
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/* 2566 */       visitBitOp(paramNode1, i, (Node)localObject1);
/* 2567 */       break;
/*      */     case 28:
/*      */     case 29:
/* 2571 */       generateExpression((Node)localObject1, paramNode1);
/* 2572 */       addObjectToDouble();
/* 2573 */       if (i == 29) {
/* 2574 */         this.cfw.add(119);
/*      */       }
/* 2576 */       addDoubleWrap();
/* 2577 */       break;
/*      */     case 150:
/* 2581 */       generateExpression((Node)localObject1, paramNode1);
/* 2582 */       addObjectToDouble();
/* 2583 */       break;
/*      */     case 149:
/* 2587 */       n = -1;
/* 2588 */       if (((Node)localObject1).getType() == 40) {
/* 2589 */         n = ((Node)localObject1).getIntProp(8, -1);
/*      */       }
/* 2591 */       if (n != -1) {
/* 2592 */         ((Node)localObject1).removeProp(8);
/* 2593 */         generateExpression((Node)localObject1, paramNode1);
/* 2594 */         ((Node)localObject1).putIntProp(8, n);
/*      */       } else {
/* 2596 */         generateExpression((Node)localObject1, paramNode1);
/* 2597 */         addDoubleWrap();
/*      */       }
/* 2599 */       break;
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 52:
/*      */     case 53:
/* 2608 */       n = this.cfw.acquireLabel();
/* 2609 */       i3 = this.cfw.acquireLabel();
/* 2610 */       visitIfJumpRelOp(paramNode1, (Node)localObject1, n, i3);
/* 2611 */       addJumpedBooleanWrap(n, i3);
/* 2612 */       break;
/*      */     case 12:
/*      */     case 13:
/*      */     case 46:
/*      */     case 47:
/* 2619 */       n = this.cfw.acquireLabel();
/* 2620 */       i3 = this.cfw.acquireLabel();
/* 2621 */       visitIfJumpEqOp(paramNode1, (Node)localObject1, n, i3);
/* 2622 */       addJumpedBooleanWrap(n, i3);
/* 2623 */       break;
/*      */     case 33:
/*      */     case 34:
/* 2628 */       visitGetProp(paramNode1, (Node)localObject1);
/* 2629 */       break;
/*      */     case 36:
/* 2632 */       generateExpression((Node)localObject1, paramNode1);
/* 2633 */       generateExpression(((Node)localObject1).getNext(), paramNode1);
/* 2634 */       this.cfw.addALoad(this.contextLocal);
/* 2635 */       if (paramNode1.getIntProp(8, -1) != -1) {
/* 2636 */         addScriptRuntimeInvoke("getObjectIndex", "(Ljava/lang/Object;DLsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */       else
/*      */       {
/* 2643 */         this.cfw.addALoad(this.variableObjectLocal);
/* 2644 */         addScriptRuntimeInvoke("getObjectElem", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;");
/*      */       }
/*      */ 
/* 2652 */       break;
/*      */     case 67:
/* 2655 */       generateExpression((Node)localObject1, paramNode1);
/* 2656 */       this.cfw.addALoad(this.contextLocal);
/* 2657 */       addScriptRuntimeInvoke("refGet", "(Lsun/org/mozilla/javascript/internal/Ref;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 2662 */       break;
/*      */     case 55:
/* 2665 */       visitGetVar(paramNode1);
/* 2666 */       break;
/*      */     case 56:
/* 2669 */       visitSetVar(paramNode1, (Node)localObject1, true);
/* 2670 */       break;
/*      */     case 8:
/* 2673 */       visitSetName(paramNode1, (Node)localObject1);
/* 2674 */       break;
/*      */     case 73:
/* 2677 */       visitStrictSetName(paramNode1, (Node)localObject1);
/* 2678 */       break;
/*      */     case 155:
/* 2681 */       visitSetConst(paramNode1, (Node)localObject1);
/* 2682 */       break;
/*      */     case 156:
/* 2685 */       visitSetConstVar(paramNode1, (Node)localObject1, true);
/* 2686 */       break;
/*      */     case 35:
/*      */     case 139:
/* 2690 */       visitSetProp(i, paramNode1, (Node)localObject1);
/* 2691 */       break;
/*      */     case 37:
/*      */     case 140:
/* 2695 */       visitSetElem(i, paramNode1, (Node)localObject1);
/* 2696 */       break;
/*      */     case 68:
/*      */     case 142:
/* 2701 */       generateExpression((Node)localObject1, paramNode1);
/* 2702 */       localObject1 = ((Node)localObject1).getNext();
/* 2703 */       if (i == 142) {
/* 2704 */         this.cfw.add(89);
/* 2705 */         this.cfw.addALoad(this.contextLocal);
/* 2706 */         addScriptRuntimeInvoke("refGet", "(Lsun/org/mozilla/javascript/internal/Ref;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */ 
/* 2712 */       generateExpression((Node)localObject1, paramNode1);
/* 2713 */       this.cfw.addALoad(this.contextLocal);
/* 2714 */       addScriptRuntimeInvoke("refSet", "(Lsun/org/mozilla/javascript/internal/Ref;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 2721 */       break;
/*      */     case 69:
/* 2724 */       generateExpression((Node)localObject1, paramNode1);
/* 2725 */       this.cfw.addALoad(this.contextLocal);
/* 2726 */       addScriptRuntimeInvoke("refDel", "(Lsun/org/mozilla/javascript/internal/Ref;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 2730 */       break;
/*      */     case 31:
/* 2733 */       generateExpression((Node)localObject1, paramNode1);
/* 2734 */       localObject1 = ((Node)localObject1).getNext();
/* 2735 */       generateExpression((Node)localObject1, paramNode1);
/* 2736 */       this.cfw.addALoad(this.contextLocal);
/* 2737 */       addScriptRuntimeInvoke("delete", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 2742 */       break;
/*      */     case 49:
/* 2746 */       while (localObject1 != null) {
/* 2747 */         generateExpression((Node)localObject1, paramNode1);
/* 2748 */         localObject1 = ((Node)localObject1).getNext();
/*      */       }
/*      */ 
/* 2751 */       this.cfw.addALoad(this.contextLocal);
/* 2752 */       this.cfw.addALoad(this.variableObjectLocal);
/* 2753 */       this.cfw.addPush(paramNode1.getString());
/* 2754 */       addScriptRuntimeInvoke("bind", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 2761 */       break;
/*      */     case 54:
/* 2764 */       this.cfw.addALoad(getLocalBlockRegister(paramNode1));
/* 2765 */       break;
/*      */     case 71:
/* 2769 */       String str = (String)paramNode1.getProp(17);
/* 2770 */       generateExpression((Node)localObject1, paramNode1);
/* 2771 */       this.cfw.addPush(str);
/* 2772 */       this.cfw.addALoad(this.contextLocal);
/* 2773 */       addScriptRuntimeInvoke("specialRef", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/Ref;");
/*      */ 
/* 2780 */       break;
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/* 2787 */       int i1 = paramNode1.getIntProp(16, 0);
/*      */       do
/*      */       {
/* 2791 */         generateExpression((Node)localObject1, paramNode1);
/* 2792 */         localObject1 = ((Node)localObject1).getNext();
/* 2793 */       }while (localObject1 != null);
/* 2794 */       this.cfw.addALoad(this.contextLocal);
/*      */ 
/* 2796 */       switch (i) {
/*      */       case 77:
/* 2798 */         localObject3 = "memberRef";
/* 2799 */         localObject4 = "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;I)Lsun/org/mozilla/javascript/internal/Ref;";
/*      */ 
/* 2804 */         break;
/*      */       case 78:
/* 2806 */         localObject3 = "memberRef";
/* 2807 */         localObject4 = "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;I)Lsun/org/mozilla/javascript/internal/Ref;";
/*      */ 
/* 2813 */         break;
/*      */       case 79:
/* 2815 */         localObject3 = "nameRef";
/* 2816 */         localObject4 = "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;I)Lsun/org/mozilla/javascript/internal/Ref;";
/*      */ 
/* 2821 */         this.cfw.addALoad(this.variableObjectLocal);
/* 2822 */         break;
/*      */       case 80:
/* 2824 */         localObject3 = "nameRef";
/* 2825 */         localObject4 = "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;I)Lsun/org/mozilla/javascript/internal/Ref;";
/*      */ 
/* 2831 */         this.cfw.addALoad(this.variableObjectLocal);
/* 2832 */         break;
/*      */       default:
/* 2834 */         throw Kit.codeBug();
/*      */       }
/* 2836 */       this.cfw.addPush(i1);
/* 2837 */       addScriptRuntimeInvoke((String)localObject3, (String)localObject4);
/*      */ 
/* 2839 */       break;
/*      */     case 146:
/* 2842 */       visitDotQuery(paramNode1, (Node)localObject1);
/* 2843 */       break;
/*      */     case 75:
/* 2846 */       generateExpression((Node)localObject1, paramNode1);
/* 2847 */       this.cfw.addALoad(this.contextLocal);
/* 2848 */       addScriptRuntimeInvoke("escapeAttributeValue", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/String;");
/*      */ 
/* 2852 */       break;
/*      */     case 76:
/* 2855 */       generateExpression((Node)localObject1, paramNode1);
/* 2856 */       this.cfw.addALoad(this.contextLocal);
/* 2857 */       addScriptRuntimeInvoke("escapeTextValue", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/String;");
/*      */ 
/* 2861 */       break;
/*      */     case 74:
/* 2864 */       generateExpression((Node)localObject1, paramNode1);
/* 2865 */       this.cfw.addALoad(this.contextLocal);
/* 2866 */       addScriptRuntimeInvoke("setDefaultNamespace", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 2870 */       break;
/*      */     case 72:
/* 2873 */       generateYieldPoint(paramNode1, true);
/* 2874 */       break;
/*      */     case 159:
/* 2877 */       localObject2 = localObject1;
/* 2878 */       localObject3 = localObject2.getNext();
/* 2879 */       localObject4 = ((Node)localObject3).getNext();
/* 2880 */       generateStatement(localObject2);
/* 2881 */       generateExpression(((Node)localObject3).getFirstChild(), (Node)localObject3);
/* 2882 */       generateStatement((Node)localObject4);
/* 2883 */       break;
/*      */     case 157:
/* 2887 */       localObject2 = localObject1;
/* 2888 */       localObject3 = ((Node)localObject1).getNext();
/* 2889 */       generateStatement(localObject2);
/* 2890 */       generateExpression((Node)localObject3, paramNode1);
/* 2891 */       break;
/*      */     case 50:
/*      */     case 51:
/*      */     case 57:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 64:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 90:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*      */     case 96:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 101:
/*      */     case 103:
/*      */     case 108:
/*      */     case 110:
/*      */     case 111:
/*      */     case 112:
/*      */     case 113:
/*      */     case 114:
/*      */     case 115:
/*      */     case 116:
/*      */     case 117:
/*      */     case 118:
/*      */     case 119:
/*      */     case 120:
/*      */     case 121:
/*      */     case 122:
/*      */     case 123:
/*      */     case 124:
/*      */     case 125:
/*      */     case 127:
/*      */     case 128:
/*      */     case 129:
/*      */     case 130:
/*      */     case 131:
/*      */     case 132:
/*      */     case 133:
/*      */     case 134:
/*      */     case 135:
/*      */     case 136:
/*      */     case 141:
/*      */     case 143:
/*      */     case 144:
/*      */     case 145:
/*      */     case 147:
/*      */     case 148:
/*      */     case 151:
/*      */     case 152:
/*      */     case 153:
/*      */     case 154:
/*      */     case 158:
/*      */     default:
/* 2895 */       throw new RuntimeException("Unexpected node type " + i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateYieldPoint(Node paramNode, boolean paramBoolean)
/*      */   {
/* 2902 */     int i = this.cfw.getStackTop();
/* 2903 */     this.maxStack = (this.maxStack > i ? this.maxStack : i);
/* 2904 */     if (this.cfw.getStackTop() != 0) {
/* 2905 */       generateGetGeneratorStackState();
/* 2906 */       for (int j = 0; j < i; j++) {
/* 2907 */         this.cfw.add(90);
/* 2908 */         this.cfw.add(95);
/* 2909 */         this.cfw.addLoadConstant(j);
/* 2910 */         this.cfw.add(95);
/* 2911 */         this.cfw.add(83);
/*      */       }
/*      */ 
/* 2914 */       this.cfw.add(87);
/*      */     }
/*      */ 
/* 2918 */     Node localNode = paramNode.getFirstChild();
/* 2919 */     if (localNode != null)
/* 2920 */       generateExpression(localNode, paramNode);
/*      */     else {
/* 2922 */       Codegen.pushUndefined(this.cfw);
/*      */     }
/*      */ 
/* 2925 */     int k = getNextGeneratorState(paramNode);
/* 2926 */     generateSetGeneratorResumptionPoint(k);
/*      */ 
/* 2928 */     boolean bool = generateSaveLocals(paramNode);
/*      */ 
/* 2930 */     this.cfw.add(176);
/*      */ 
/* 2932 */     generateCheckForThrowOrClose(getTargetLabel(paramNode), bool, k);
/*      */ 
/* 2936 */     if (i != 0) {
/* 2937 */       generateGetGeneratorStackState();
/* 2938 */       for (int m = 0; m < i; m++) {
/* 2939 */         this.cfw.add(89);
/* 2940 */         this.cfw.addLoadConstant(i - m - 1);
/* 2941 */         this.cfw.add(50);
/* 2942 */         this.cfw.add(95);
/*      */       }
/* 2944 */       this.cfw.add(87);
/*      */     }
/*      */ 
/* 2948 */     if (paramBoolean)
/* 2949 */       this.cfw.addALoad(this.argsLocal);
/*      */   }
/*      */ 
/*      */   private void generateCheckForThrowOrClose(int paramInt1, boolean paramBoolean, int paramInt2)
/*      */   {
/* 2956 */     int i = this.cfw.acquireLabel();
/* 2957 */     int j = this.cfw.acquireLabel();
/*      */ 
/* 2960 */     this.cfw.markLabel(i);
/* 2961 */     this.cfw.addALoad(this.argsLocal);
/* 2962 */     generateThrowJavaScriptException();
/*      */ 
/* 2965 */     this.cfw.markLabel(j);
/* 2966 */     this.cfw.addALoad(this.argsLocal);
/* 2967 */     this.cfw.add(192, "java/lang/Throwable");
/* 2968 */     this.cfw.add(191);
/*      */ 
/* 2972 */     if (paramInt1 != -1)
/* 2973 */       this.cfw.markLabel(paramInt1);
/* 2974 */     if (!paramBoolean)
/*      */     {
/* 2976 */       this.cfw.markTableSwitchCase(this.generatorSwitch, paramInt2);
/*      */     }
/*      */ 
/* 2980 */     this.cfw.addILoad(this.operationLocal);
/* 2981 */     this.cfw.addLoadConstant(2);
/* 2982 */     this.cfw.add(159, j);
/* 2983 */     this.cfw.addILoad(this.operationLocal);
/* 2984 */     this.cfw.addLoadConstant(1);
/* 2985 */     this.cfw.add(159, i);
/*      */   }
/*      */ 
/*      */   private void generateIfJump(Node paramNode1, Node paramNode2, int paramInt1, int paramInt2)
/*      */   {
/* 2993 */     int i = paramNode1.getType();
/* 2994 */     Node localNode = paramNode1.getFirstChild();
/*      */ 
/* 2996 */     switch (i) {
/*      */     case 26:
/* 2998 */       generateIfJump(localNode, paramNode1, paramInt2, paramInt1);
/* 2999 */       break;
/*      */     case 104:
/*      */     case 105:
/* 3003 */       int j = this.cfw.acquireLabel();
/* 3004 */       if (i == 105) {
/* 3005 */         generateIfJump(localNode, paramNode1, j, paramInt2);
/*      */       }
/*      */       else {
/* 3008 */         generateIfJump(localNode, paramNode1, paramInt1, j);
/*      */       }
/* 3010 */       this.cfw.markLabel(j);
/* 3011 */       localNode = localNode.getNext();
/* 3012 */       generateIfJump(localNode, paramNode1, paramInt1, paramInt2);
/* 3013 */       break;
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 52:
/*      */     case 53:
/* 3022 */       visitIfJumpRelOp(paramNode1, localNode, paramInt1, paramInt2);
/* 3023 */       break;
/*      */     case 12:
/*      */     case 13:
/*      */     case 46:
/*      */     case 47:
/* 3029 */       visitIfJumpEqOp(paramNode1, localNode, paramInt1, paramInt2);
/* 3030 */       break;
/*      */     default:
/* 3034 */       generateExpression(paramNode1, paramNode2);
/* 3035 */       addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
/* 3036 */       this.cfw.add(154, paramInt1);
/* 3037 */       this.cfw.add(167, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitFunction(OptFunctionNode paramOptFunctionNode, int paramInt)
/*      */   {
/* 3043 */     int i = this.codegen.getIndex(paramOptFunctionNode.fnode);
/* 3044 */     this.cfw.add(187, this.codegen.mainClassName);
/*      */ 
/* 3046 */     this.cfw.add(89);
/* 3047 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3048 */     this.cfw.addALoad(this.contextLocal);
/* 3049 */     this.cfw.addPush(i);
/* 3050 */     this.cfw.addInvoke(183, this.codegen.mainClassName, "<init>", "(Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;I)V");
/*      */ 
/* 3054 */     this.cfw.add(89);
/* 3055 */     if (this.isTopLevel) {
/* 3056 */       this.cfw.add(42);
/*      */     } else {
/* 3058 */       this.cfw.add(42);
/* 3059 */       this.cfw.add(180, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */     }
/*      */ 
/* 3064 */     this.cfw.add(181, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */ 
/* 3069 */     int j = paramOptFunctionNode.getDirectTargetIndex();
/* 3070 */     if (j >= 0) {
/* 3071 */       this.cfw.add(89);
/* 3072 */       if (this.isTopLevel) {
/* 3073 */         this.cfw.add(42);
/*      */       } else {
/* 3075 */         this.cfw.add(42);
/* 3076 */         this.cfw.add(180, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */       }
/*      */ 
/* 3081 */       this.cfw.add(95);
/* 3082 */       this.cfw.add(181, this.codegen.mainClassName, Codegen.getDirectTargetFieldName(j), this.codegen.mainClassSignature);
/*      */     }
/*      */ 
/* 3088 */     if (paramInt == 2)
/*      */     {
/* 3091 */       return;
/*      */     }
/* 3093 */     this.cfw.addPush(paramInt);
/* 3094 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3095 */     this.cfw.addALoad(this.contextLocal);
/* 3096 */     addOptRuntimeInvoke("initFunction", "(Lsun/org/mozilla/javascript/internal/NativeFunction;ILsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;)V");
/*      */   }
/*      */ 
/*      */   private int getTargetLabel(Node paramNode)
/*      */   {
/* 3106 */     int i = paramNode.labelId();
/* 3107 */     if (i == -1) {
/* 3108 */       i = this.cfw.acquireLabel();
/* 3109 */       paramNode.labelId(i);
/*      */     }
/* 3111 */     return i;
/*      */   }
/*      */ 
/*      */   private void visitGoto(Jump paramJump, int paramInt, Node paramNode)
/*      */   {
/* 3116 */     Node localNode = paramJump.target;
/* 3117 */     if ((paramInt == 6) || (paramInt == 7)) {
/* 3118 */       if (paramNode == null) throw Codegen.badTree();
/* 3119 */       int i = getTargetLabel(localNode);
/* 3120 */       int j = this.cfw.acquireLabel();
/* 3121 */       if (paramInt == 6)
/* 3122 */         generateIfJump(paramNode, paramJump, i, j);
/*      */       else
/* 3124 */         generateIfJump(paramNode, paramJump, j, i);
/* 3125 */       this.cfw.markLabel(j);
/*      */     }
/* 3127 */     else if (paramInt == 135) {
/* 3128 */       if (this.isGenerator)
/* 3129 */         addGotoWithReturn(localNode);
/*      */       else
/* 3131 */         addGoto(localNode, 168);
/*      */     }
/*      */     else {
/* 3134 */       addGoto(localNode, 167);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGotoWithReturn(Node paramNode)
/*      */   {
/* 3140 */     FinallyReturnPoint localFinallyReturnPoint = (FinallyReturnPoint)this.finallys.get(paramNode);
/* 3141 */     this.cfw.addLoadConstant(localFinallyReturnPoint.jsrPoints.size());
/* 3142 */     addGoto(paramNode, 167);
/* 3143 */     int i = this.cfw.acquireLabel();
/* 3144 */     this.cfw.markLabel(i);
/* 3145 */     localFinallyReturnPoint.jsrPoints.add(Integer.valueOf(i));
/*      */   }
/*      */ 
/*      */   private void visitArrayLiteral(Node paramNode1, Node paramNode2)
/*      */   {
/* 3150 */     int i = 0;
/* 3151 */     for (Node localNode = paramNode2; localNode != null; localNode = localNode.getNext()) {
/* 3152 */       i++;
/*      */     }
/*      */ 
/* 3155 */     addNewObjectArray(i);
/* 3156 */     for (int j = 0; j != i; j++) {
/* 3157 */       this.cfw.add(89);
/* 3158 */       this.cfw.addPush(j);
/* 3159 */       generateExpression(paramNode2, paramNode1);
/* 3160 */       this.cfw.add(83);
/* 3161 */       paramNode2 = paramNode2.getNext();
/*      */     }
/* 3163 */     int[] arrayOfInt = (int[])paramNode1.getProp(11);
/* 3164 */     if (arrayOfInt == null) {
/* 3165 */       this.cfw.add(1);
/* 3166 */       this.cfw.add(3);
/*      */     } else {
/* 3168 */       this.cfw.addPush(OptRuntime.encodeIntArray(arrayOfInt));
/* 3169 */       this.cfw.addPush(arrayOfInt.length);
/*      */     }
/* 3171 */     this.cfw.addALoad(this.contextLocal);
/* 3172 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3173 */     addOptRuntimeInvoke("newArrayLiteral", "([Ljava/lang/Object;Ljava/lang/String;ILsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */   }
/*      */ 
/*      */   private void visitObjectLiteral(Node paramNode1, Node paramNode2)
/*      */   {
/* 3184 */     Object[] arrayOfObject = (Object[])paramNode1.getProp(12);
/* 3185 */     int i = arrayOfObject.length;
/*      */ 
/* 3188 */     addNewObjectArray(i);
/* 3189 */     for (int j = 0; j != i; j++) {
/* 3190 */       this.cfw.add(89);
/* 3191 */       this.cfw.addPush(j);
/* 3192 */       Object localObject = arrayOfObject[j];
/* 3193 */       if ((localObject instanceof String)) {
/* 3194 */         this.cfw.addPush((String)localObject);
/*      */       } else {
/* 3196 */         this.cfw.addPush(((Integer)localObject).intValue());
/* 3197 */         addScriptRuntimeInvoke("wrapInt", "(I)Ljava/lang/Integer;");
/*      */       }
/* 3199 */       this.cfw.add(83);
/*      */     }
/*      */ 
/* 3202 */     addNewObjectArray(i);
/* 3203 */     Node localNode = paramNode2;
/*      */     int m;
/* 3204 */     for (int k = 0; k != i; k++) {
/* 3205 */       this.cfw.add(89);
/* 3206 */       this.cfw.addPush(k);
/* 3207 */       m = paramNode2.getType();
/* 3208 */       if (m == 151)
/* 3209 */         generateExpression(paramNode2.getFirstChild(), paramNode1);
/* 3210 */       else if (m == 152)
/* 3211 */         generateExpression(paramNode2.getFirstChild(), paramNode1);
/*      */       else {
/* 3213 */         generateExpression(paramNode2, paramNode1);
/*      */       }
/* 3215 */       this.cfw.add(83);
/* 3216 */       paramNode2 = paramNode2.getNext();
/*      */     }
/*      */ 
/* 3219 */     this.cfw.addPush(i);
/* 3220 */     this.cfw.add(188, 10);
/* 3221 */     for (k = 0; k != i; k++) {
/* 3222 */       this.cfw.add(89);
/* 3223 */       this.cfw.addPush(k);
/* 3224 */       m = localNode.getType();
/* 3225 */       if (m == 151)
/* 3226 */         this.cfw.add(2);
/* 3227 */       else if (m == 152)
/* 3228 */         this.cfw.add(4);
/*      */       else {
/* 3230 */         this.cfw.add(3);
/*      */       }
/* 3232 */       this.cfw.add(79);
/* 3233 */       localNode = localNode.getNext();
/*      */     }
/*      */ 
/* 3236 */     this.cfw.addALoad(this.contextLocal);
/* 3237 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3238 */     addScriptRuntimeInvoke("newObjectLiteral", "([Ljava/lang/Object;[Ljava/lang/Object;[ILsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */   }
/*      */ 
/*      */   private void visitSpecialCall(Node paramNode1, int paramInt1, int paramInt2, Node paramNode2)
/*      */   {
/* 3250 */     this.cfw.addALoad(this.contextLocal);
/*      */ 
/* 3252 */     if (paramInt1 == 30) {
/* 3253 */       generateExpression(paramNode2, paramNode1);
/*      */     }
/*      */     else {
/* 3256 */       generateFunctionAndThisObj(paramNode2, paramNode1);
/*      */     }
/*      */ 
/* 3259 */     paramNode2 = paramNode2.getNext();
/*      */ 
/* 3261 */     generateCallArgArray(paramNode1, paramNode2, false);
/*      */     String str1;
/*      */     String str2;
/* 3266 */     if (paramInt1 == 30) {
/* 3267 */       str1 = "newObjectSpecial";
/* 3268 */       str2 = "(Lsun/org/mozilla/javascript/internal/Context;Ljava/lang/Object;[Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;I)Ljava/lang/Object;";
/*      */ 
/* 3275 */       this.cfw.addALoad(this.variableObjectLocal);
/* 3276 */       this.cfw.addALoad(this.thisObjLocal);
/* 3277 */       this.cfw.addPush(paramInt2);
/*      */     } else {
/* 3279 */       str1 = "callSpecial";
/* 3280 */       str2 = "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;ILjava/lang/String;I)Ljava/lang/Object;";
/*      */ 
/* 3289 */       this.cfw.addALoad(this.variableObjectLocal);
/* 3290 */       this.cfw.addALoad(this.thisObjLocal);
/* 3291 */       this.cfw.addPush(paramInt2);
/* 3292 */       String str3 = this.scriptOrFn.getSourceName();
/* 3293 */       this.cfw.addPush(str3 == null ? "" : str3);
/* 3294 */       this.cfw.addPush(this.itsLineNumber);
/*      */     }
/*      */ 
/* 3297 */     addOptRuntimeInvoke(str1, str2);
/*      */   }
/*      */ 
/*      */   private void visitStandardCall(Node paramNode1, Node paramNode2)
/*      */   {
/* 3302 */     if (paramNode1.getType() != 38) throw Codegen.badTree();
/*      */ 
/* 3304 */     Node localNode1 = paramNode2.getNext();
/* 3305 */     int i = paramNode2.getType();
/*      */     Object localObject;
/*      */     String str1;
/*      */     String str2;
/*      */     Node localNode2;
/* 3310 */     if (localNode1 == null) {
/* 3311 */       if (i == 39)
/*      */       {
/* 3313 */         localObject = paramNode2.getString();
/* 3314 */         this.cfw.addPush((String)localObject);
/* 3315 */         str1 = "callName0";
/* 3316 */         str2 = "(Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/* 3320 */       else if (i == 33)
/*      */       {
/* 3322 */         localObject = paramNode2.getFirstChild();
/* 3323 */         generateExpression((Node)localObject, paramNode1);
/* 3324 */         localNode2 = ((Node)localObject).getNext();
/* 3325 */         String str3 = localNode2.getString();
/* 3326 */         this.cfw.addPush(str3);
/* 3327 */         str1 = "callProp0";
/* 3328 */         str2 = "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/*      */       else
/*      */       {
/* 3333 */         if (i == 34) {
/* 3334 */           throw Kit.codeBug();
/*      */         }
/* 3336 */         generateFunctionAndThisObj(paramNode2, paramNode1);
/* 3337 */         str1 = "call0";
/* 3338 */         str2 = "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/*      */ 
/*      */     }
/* 3345 */     else if (i == 39)
/*      */     {
/* 3350 */       localObject = paramNode2.getString();
/* 3351 */       generateCallArgArray(paramNode1, localNode1, false);
/* 3352 */       this.cfw.addPush((String)localObject);
/* 3353 */       str1 = "callName";
/* 3354 */       str2 = "([Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */     }
/*      */     else
/*      */     {
/* 3360 */       int j = 0;
/* 3361 */       for (localNode2 = localNode1; localNode2 != null; localNode2 = localNode2.getNext()) {
/* 3362 */         j++;
/*      */       }
/* 3364 */       generateFunctionAndThisObj(paramNode2, paramNode1);
/*      */ 
/* 3366 */       if (j == 1) {
/* 3367 */         generateExpression(localNode1, paramNode1);
/* 3368 */         str1 = "call1";
/* 3369 */         str2 = "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/* 3375 */       else if (j == 2) {
/* 3376 */         generateExpression(localNode1, paramNode1);
/* 3377 */         generateExpression(localNode1.getNext(), paramNode1);
/* 3378 */         str1 = "call2";
/* 3379 */         str2 = "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/*      */       else
/*      */       {
/* 3387 */         generateCallArgArray(paramNode1, localNode1, false);
/* 3388 */         str1 = "callN";
/* 3389 */         str2 = "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3398 */     this.cfw.addALoad(this.contextLocal);
/* 3399 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3400 */     addOptRuntimeInvoke(str1, str2);
/*      */   }
/*      */ 
/*      */   private void visitStandardNew(Node paramNode1, Node paramNode2)
/*      */   {
/* 3405 */     if (paramNode1.getType() != 30) throw Codegen.badTree();
/*      */ 
/* 3407 */     Node localNode = paramNode2.getNext();
/*      */ 
/* 3409 */     generateExpression(paramNode2, paramNode1);
/*      */ 
/* 3411 */     this.cfw.addALoad(this.contextLocal);
/* 3412 */     this.cfw.addALoad(this.variableObjectLocal);
/*      */ 
/* 3414 */     generateCallArgArray(paramNode1, localNode, false);
/* 3415 */     addScriptRuntimeInvoke("newObject", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */   }
/*      */ 
/*      */   private void visitOptimizedCall(Node paramNode1, OptFunctionNode paramOptFunctionNode, int paramInt, Node paramNode2)
/*      */   {
/* 3427 */     Node localNode1 = paramNode2.getNext();
/*      */ 
/* 3429 */     short s1 = 0;
/* 3430 */     if (paramInt == 30) {
/* 3431 */       generateExpression(paramNode2, paramNode1);
/*      */     } else {
/* 3433 */       generateFunctionAndThisObj(paramNode2, paramNode1);
/* 3434 */       s1 = getNewWordLocal();
/* 3435 */       this.cfw.addAStore(s1);
/*      */     }
/*      */ 
/* 3439 */     int i = this.cfw.acquireLabel();
/*      */ 
/* 3441 */     int j = paramOptFunctionNode.getDirectTargetIndex();
/* 3442 */     if (this.isTopLevel) {
/* 3443 */       this.cfw.add(42);
/*      */     } else {
/* 3445 */       this.cfw.add(42);
/* 3446 */       this.cfw.add(180, this.codegen.mainClassName, "_dcp", this.codegen.mainClassSignature);
/*      */     }
/*      */ 
/* 3450 */     this.cfw.add(180, this.codegen.mainClassName, Codegen.getDirectTargetFieldName(j), this.codegen.mainClassSignature);
/*      */ 
/* 3454 */     this.cfw.add(92);
/*      */ 
/* 3457 */     int k = this.cfw.acquireLabel();
/* 3458 */     this.cfw.add(166, k);
/*      */ 
/* 3461 */     short s2 = this.cfw.getStackTop();
/* 3462 */     this.cfw.add(95);
/* 3463 */     this.cfw.add(87);
/*      */ 
/* 3465 */     if (this.compilerEnv.isUseDynamicScope()) {
/* 3466 */       this.cfw.addALoad(this.contextLocal);
/* 3467 */       this.cfw.addALoad(this.variableObjectLocal);
/*      */     } else {
/* 3469 */       this.cfw.add(89);
/*      */ 
/* 3471 */       this.cfw.addInvoke(185, "sun/org/mozilla/javascript/internal/Scriptable", "getParentScope", "()Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 3476 */       this.cfw.addALoad(this.contextLocal);
/*      */ 
/* 3478 */       this.cfw.add(95);
/*      */     }
/*      */ 
/* 3482 */     if (paramInt == 30)
/* 3483 */       this.cfw.add(1);
/*      */     else {
/* 3485 */       this.cfw.addALoad(s1);
/*      */     }
/*      */ 
/* 3495 */     Node localNode2 = localNode1;
/* 3496 */     while (localNode2 != null) {
/* 3497 */       int m = nodeIsDirectCallParameter(localNode2);
/* 3498 */       if (m >= 0) {
/* 3499 */         this.cfw.addALoad(m);
/* 3500 */         this.cfw.addDLoad(m + 1);
/* 3501 */       } else if (localNode2.getIntProp(8, -1) == 0)
/*      */       {
/* 3504 */         this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 3508 */         generateExpression(localNode2, paramNode1);
/*      */       } else {
/* 3510 */         generateExpression(localNode2, paramNode1);
/* 3511 */         this.cfw.addPush(0.0D);
/*      */       }
/* 3513 */       localNode2 = localNode2.getNext();
/*      */     }
/*      */ 
/* 3516 */     this.cfw.add(178, "sun/org/mozilla/javascript/internal/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
/*      */ 
/* 3519 */     this.cfw.addInvoke(184, this.codegen.mainClassName, paramInt == 30 ? this.codegen.getDirectCtorName(paramOptFunctionNode.fnode) : this.codegen.getBodyMethodName(paramOptFunctionNode.fnode), this.codegen.getBodyMethodSignature(paramOptFunctionNode.fnode));
/*      */ 
/* 3526 */     this.cfw.add(167, i);
/*      */ 
/* 3528 */     this.cfw.markLabel(k, s2);
/*      */ 
/* 3530 */     this.cfw.add(87);
/* 3531 */     this.cfw.addALoad(this.contextLocal);
/* 3532 */     this.cfw.addALoad(this.variableObjectLocal);
/*      */ 
/* 3534 */     if (paramInt != 30) {
/* 3535 */       this.cfw.addALoad(s1);
/* 3536 */       releaseWordLocal(s1);
/*      */     }
/*      */ 
/* 3541 */     generateCallArgArray(paramNode1, localNode1, true);
/*      */ 
/* 3543 */     if (paramInt == 30) {
/* 3544 */       addScriptRuntimeInvoke("newObject", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */     }
/*      */     else
/*      */     {
/* 3552 */       this.cfw.addInvoke(185, "sun/org/mozilla/javascript/internal/Callable", "call", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
/*      */     }
/*      */ 
/* 3562 */     this.cfw.markLabel(i);
/*      */   }
/*      */ 
/*      */   private void generateCallArgArray(Node paramNode1, Node paramNode2, boolean paramBoolean)
/*      */   {
/* 3567 */     int i = 0;
/* 3568 */     for (Node localNode = paramNode2; localNode != null; localNode = localNode.getNext()) {
/* 3569 */       i++;
/*      */     }
/*      */ 
/* 3572 */     if ((i == 1) && (this.itsOneArgArray >= 0))
/* 3573 */       this.cfw.addALoad(this.itsOneArgArray);
/*      */     else {
/* 3575 */       addNewObjectArray(i);
/*      */     }
/*      */ 
/* 3578 */     for (int j = 0; j != i; j++)
/*      */     {
/* 3582 */       if (!this.isGenerator) {
/* 3583 */         this.cfw.add(89);
/* 3584 */         this.cfw.addPush(j);
/*      */       }
/*      */       int k;
/* 3587 */       if (!paramBoolean) {
/* 3588 */         generateExpression(paramNode2, paramNode1);
/*      */       }
/*      */       else
/*      */       {
/* 3595 */         k = nodeIsDirectCallParameter(paramNode2);
/* 3596 */         if (k >= 0) {
/* 3597 */           dcpLoadAsObject(k);
/*      */         } else {
/* 3599 */           generateExpression(paramNode2, paramNode1);
/* 3600 */           int m = paramNode2.getIntProp(8, -1);
/*      */ 
/* 3602 */           if (m == 0) {
/* 3603 */             addDoubleWrap();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3611 */       if (this.isGenerator) {
/* 3612 */         k = getNewWordLocal();
/* 3613 */         this.cfw.addAStore(k);
/* 3614 */         this.cfw.add(192, "[Ljava/lang/Object;");
/* 3615 */         this.cfw.add(89);
/* 3616 */         this.cfw.addPush(j);
/* 3617 */         this.cfw.addALoad(k);
/* 3618 */         releaseWordLocal(k);
/*      */       }
/*      */ 
/* 3621 */       this.cfw.add(83);
/*      */ 
/* 3623 */       paramNode2 = paramNode2.getNext();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateFunctionAndThisObj(Node paramNode1, Node paramNode2)
/*      */   {
/* 3630 */     int i = paramNode1.getType();
/*      */     Object localObject;
/* 3631 */     switch (paramNode1.getType()) {
/*      */     case 34:
/* 3633 */       throw Kit.codeBug();
/*      */     case 33:
/*      */     case 36:
/* 3637 */       localObject = paramNode1.getFirstChild();
/* 3638 */       generateExpression((Node)localObject, paramNode1);
/* 3639 */       Node localNode = ((Node)localObject).getNext();
/* 3640 */       if (i == 33) {
/* 3641 */         String str = localNode.getString();
/* 3642 */         this.cfw.addPush(str);
/* 3643 */         this.cfw.addALoad(this.contextLocal);
/* 3644 */         this.cfw.addALoad(this.variableObjectLocal);
/* 3645 */         addScriptRuntimeInvoke("getPropFunctionAndThis", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Callable;");
/*      */       }
/*      */       else
/*      */       {
/* 3654 */         if (paramNode1.getIntProp(8, -1) != -1)
/* 3655 */           throw Codegen.badTree();
/* 3656 */         generateExpression(localNode, paramNode1);
/* 3657 */         this.cfw.addALoad(this.contextLocal);
/* 3658 */         addScriptRuntimeInvoke("getElemFunctionAndThis", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/Callable;");
/*      */       }
/*      */ 
/* 3665 */       break;
/*      */     case 39:
/* 3669 */       localObject = paramNode1.getString();
/* 3670 */       this.cfw.addPush((String)localObject);
/* 3671 */       this.cfw.addALoad(this.contextLocal);
/* 3672 */       this.cfw.addALoad(this.variableObjectLocal);
/* 3673 */       addScriptRuntimeInvoke("getNameFunctionAndThis", "(Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Callable;");
/*      */ 
/* 3679 */       break;
/*      */     case 35:
/*      */     case 37:
/*      */     case 38:
/*      */     default:
/* 3683 */       generateExpression(paramNode1, paramNode2);
/* 3684 */       this.cfw.addALoad(this.contextLocal);
/* 3685 */       addScriptRuntimeInvoke("getValueFunctionAndThis", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/Callable;");
/*      */     }
/*      */ 
/* 3693 */     this.cfw.addALoad(this.contextLocal);
/* 3694 */     addScriptRuntimeInvoke("lastStoredScriptable", "(Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */   }
/*      */ 
/*      */   private void updateLineNumber(Node paramNode)
/*      */   {
/* 3702 */     this.itsLineNumber = paramNode.getLineno();
/* 3703 */     if (this.itsLineNumber == -1)
/* 3704 */       return;
/* 3705 */     this.cfw.addLineNumberEntry((short)this.itsLineNumber);
/*      */   }
/*      */ 
/*      */   private void visitTryCatchFinally(Jump paramJump, Node paramNode)
/*      */   {
/* 3722 */     short s = getNewWordLocal();
/* 3723 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3724 */     this.cfw.addAStore(s);
/*      */ 
/* 3731 */     int i = this.cfw.acquireLabel();
/* 3732 */     this.cfw.markLabel(i, (short)0);
/*      */ 
/* 3734 */     Node localNode1 = paramJump.target;
/* 3735 */     Node localNode2 = paramJump.getFinally();
/*      */ 
/* 3738 */     if ((this.isGenerator) && (localNode2 != null)) {
/* 3739 */       FinallyReturnPoint localFinallyReturnPoint = new FinallyReturnPoint();
/* 3740 */       if (this.finallys == null) {
/* 3741 */         this.finallys = new HashMap();
/*      */       }
/*      */ 
/* 3744 */       this.finallys.put(localNode2, localFinallyReturnPoint);
/*      */ 
/* 3746 */       this.finallys.put(localNode2.getNext(), localFinallyReturnPoint);
/*      */     }
/*      */ 
/* 3749 */     while (paramNode != null) {
/* 3750 */       generateStatement(paramNode);
/* 3751 */       paramNode = paramNode.getNext();
/*      */     }
/*      */ 
/* 3755 */     int j = this.cfw.acquireLabel();
/* 3756 */     this.cfw.add(167, j);
/*      */ 
/* 3758 */     int k = getLocalBlockRegister(paramJump);
/*      */     int m;
/* 3761 */     if (localNode1 != null)
/*      */     {
/* 3763 */       m = localNode1.labelId();
/*      */ 
/* 3765 */       generateCatchBlock(0, s, m, i, k);
/*      */ 
/* 3771 */       generateCatchBlock(1, s, m, i, k);
/*      */ 
/* 3778 */       generateCatchBlock(2, s, m, i, k);
/*      */ 
/* 3781 */       Context localContext = Context.getCurrentContext();
/* 3782 */       if ((localContext != null) && (localContext.hasFeature(13)))
/*      */       {
/* 3785 */         generateCatchBlock(3, s, m, i, k);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3792 */     if (localNode2 != null) {
/* 3793 */       m = this.cfw.acquireLabel();
/* 3794 */       this.cfw.markHandler(m);
/* 3795 */       this.cfw.addAStore(k);
/*      */ 
/* 3798 */       this.cfw.addALoad(s);
/* 3799 */       this.cfw.addAStore(this.variableObjectLocal);
/*      */ 
/* 3802 */       int n = localNode2.labelId();
/* 3803 */       if (this.isGenerator)
/* 3804 */         addGotoWithReturn(localNode2);
/*      */       else {
/* 3806 */         this.cfw.add(168, n);
/*      */       }
/*      */ 
/* 3809 */       this.cfw.addALoad(k);
/* 3810 */       if (this.isGenerator)
/* 3811 */         this.cfw.add(192, "java/lang/Throwable");
/* 3812 */       this.cfw.add(191);
/*      */ 
/* 3815 */       this.cfw.addExceptionHandler(i, n, m, null);
/*      */     }
/*      */ 
/* 3818 */     releaseWordLocal(s);
/* 3819 */     this.cfw.markLabel(j);
/*      */   }
/*      */ 
/*      */   private void generateCatchBlock(int paramInt1, short paramShort, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 3832 */     int i = this.cfw.acquireLabel();
/* 3833 */     this.cfw.markHandler(i);
/*      */ 
/* 3836 */     this.cfw.addAStore(paramInt4);
/*      */ 
/* 3839 */     this.cfw.addALoad(paramShort);
/* 3840 */     this.cfw.addAStore(this.variableObjectLocal);
/*      */     String str;
/* 3843 */     if (paramInt1 == 0)
/* 3844 */       str = "sun/org/mozilla/javascript/internal/JavaScriptException";
/* 3845 */     else if (paramInt1 == 1)
/* 3846 */       str = "sun/org/mozilla/javascript/internal/EvaluatorException";
/* 3847 */     else if (paramInt1 == 2)
/* 3848 */       str = "sun/org/mozilla/javascript/internal/EcmaError";
/* 3849 */     else if (paramInt1 == 3)
/* 3850 */       str = "java/lang/Throwable";
/*      */     else {
/* 3852 */       throw Kit.codeBug();
/*      */     }
/*      */ 
/* 3856 */     this.cfw.addExceptionHandler(paramInt3, paramInt2, i, str);
/*      */ 
/* 3859 */     this.cfw.add(167, paramInt2);
/*      */   }
/*      */ 
/*      */   private boolean generateSaveLocals(Node paramNode)
/*      */   {
/* 3865 */     int i = 0;
/* 3866 */     for (int j = 0; j < this.firstFreeLocal; j++) {
/* 3867 */       if (this.locals[j] != 0) {
/* 3868 */         i++;
/*      */       }
/*      */     }
/* 3871 */     if (i == 0) {
/* 3872 */       ((FunctionNode)this.scriptOrFn).addLiveLocals(paramNode, null);
/* 3873 */       return false;
/*      */     }
/*      */ 
/* 3877 */     this.maxLocals = (this.maxLocals > i ? this.maxLocals : i);
/*      */ 
/* 3880 */     int[] arrayOfInt = new int[i];
/* 3881 */     int k = 0;
/* 3882 */     for (int m = 0; m < this.firstFreeLocal; m++) {
/* 3883 */       if (this.locals[m] != 0) {
/* 3884 */         arrayOfInt[k] = m;
/* 3885 */         k++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3890 */     ((FunctionNode)this.scriptOrFn).addLiveLocals(paramNode, arrayOfInt);
/*      */ 
/* 3893 */     generateGetGeneratorLocalsState();
/* 3894 */     for (m = 0; m < i; m++) {
/* 3895 */       this.cfw.add(89);
/* 3896 */       this.cfw.addLoadConstant(m);
/* 3897 */       this.cfw.addALoad(arrayOfInt[m]);
/* 3898 */       this.cfw.add(83);
/*      */     }
/*      */ 
/* 3901 */     this.cfw.add(87);
/*      */ 
/* 3903 */     return true;
/*      */   }
/*      */ 
/*      */   private void visitSwitch(Jump paramJump, Node paramNode)
/*      */   {
/* 3911 */     generateExpression(paramNode, paramJump);
/*      */ 
/* 3913 */     short s = getNewWordLocal();
/* 3914 */     this.cfw.addAStore(s);
/*      */ 
/* 3916 */     for (Jump localJump = (Jump)paramNode.getNext(); 
/* 3917 */       localJump != null; 
/* 3918 */       localJump = (Jump)localJump.getNext())
/*      */     {
/* 3920 */       if (localJump.getType() != 115)
/* 3921 */         throw Codegen.badTree();
/* 3922 */       Node localNode = localJump.getFirstChild();
/* 3923 */       generateExpression(localNode, localJump);
/* 3924 */       this.cfw.addALoad(s);
/* 3925 */       addScriptRuntimeInvoke("shallowEq", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
/*      */ 
/* 3929 */       addGoto(localJump.target, 154);
/*      */     }
/* 3931 */     releaseWordLocal(s);
/*      */   }
/*      */ 
/*      */   private void visitTypeofname(Node paramNode)
/*      */   {
/* 3936 */     if (this.hasVarsInRegs) {
/* 3937 */       int i = this.fnCurrent.fnode.getIndexForNameNode(paramNode);
/* 3938 */       if (i >= 0) {
/* 3939 */         if (this.fnCurrent.isNumberVar(i)) {
/* 3940 */           this.cfw.addPush("number");
/* 3941 */         } else if (varIsDirectCallParameter(i)) {
/* 3942 */           int j = this.varRegisters[i];
/* 3943 */           this.cfw.addALoad(j);
/* 3944 */           this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 3946 */           int k = this.cfw.acquireLabel();
/* 3947 */           this.cfw.add(165, k);
/* 3948 */           short s = this.cfw.getStackTop();
/* 3949 */           this.cfw.addALoad(j);
/* 3950 */           addScriptRuntimeInvoke("typeof", "(Ljava/lang/Object;)Ljava/lang/String;");
/*      */ 
/* 3953 */           int m = this.cfw.acquireLabel();
/* 3954 */           this.cfw.add(167, m);
/* 3955 */           this.cfw.markLabel(k, s);
/* 3956 */           this.cfw.addPush("number");
/* 3957 */           this.cfw.markLabel(m);
/*      */         } else {
/* 3959 */           this.cfw.addALoad(this.varRegisters[i]);
/* 3960 */           addScriptRuntimeInvoke("typeof", "(Ljava/lang/Object;)Ljava/lang/String;");
/*      */         }
/*      */ 
/* 3964 */         return;
/*      */       }
/*      */     }
/* 3967 */     this.cfw.addALoad(this.variableObjectLocal);
/* 3968 */     this.cfw.addPush(paramNode.getString());
/* 3969 */     addScriptRuntimeInvoke("typeofName", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Ljava/lang/String;");
/*      */   }
/*      */ 
/*      */   private void saveCurrentCodeOffset()
/*      */   {
/* 3981 */     this.savedCodeOffset = this.cfw.getCurrentCodeOffset();
/*      */   }
/*      */ 
/*      */   private void addInstructionCount()
/*      */   {
/* 3991 */     int i = this.cfw.getCurrentCodeOffset() - this.savedCodeOffset;
/* 3992 */     if (i == 0)
/* 3993 */       return;
/* 3994 */     addInstructionCount(i);
/*      */   }
/*      */ 
/*      */   private void addInstructionCount(int paramInt)
/*      */   {
/* 4006 */     this.cfw.addALoad(this.contextLocal);
/* 4007 */     this.cfw.addPush(paramInt);
/* 4008 */     addScriptRuntimeInvoke("addInstructionCount", "(Lsun/org/mozilla/javascript/internal/Context;I)V");
/*      */   }
/*      */ 
/*      */   private void visitIncDec(Node paramNode)
/*      */   {
/* 4015 */     int i = paramNode.getExistingIntProp(13);
/* 4016 */     Node localNode1 = paramNode.getFirstChild();
/*      */     Node localNode2;
/* 4017 */     switch (localNode1.getType()) {
/*      */     case 55:
/* 4019 */       if (!this.hasVarsInRegs) Kit.codeBug();
/*      */       int j;
/*      */       int k;
/*      */       int m;
/* 4020 */       if (paramNode.getIntProp(8, -1) != -1) {
/* 4021 */         j = (i & 0x2) != 0 ? 1 : 0;
/* 4022 */         k = this.fnCurrent.getVarIndex(localNode1);
/* 4023 */         m = this.varRegisters[k];
/* 4024 */         int n = varIsDirectCallParameter(k) ? 1 : 0;
/* 4025 */         this.cfw.addDLoad(m + n);
/* 4026 */         if (j != 0) {
/* 4027 */           this.cfw.add(92);
/*      */         }
/* 4029 */         this.cfw.addPush(1.0D);
/* 4030 */         if ((i & 0x1) == 0)
/* 4031 */           this.cfw.add(99);
/*      */         else {
/* 4033 */           this.cfw.add(103);
/*      */         }
/* 4035 */         if (j == 0) {
/* 4036 */           this.cfw.add(92);
/*      */         }
/* 4038 */         this.cfw.addDStore(m + n);
/*      */       } else {
/* 4040 */         j = (i & 0x2) != 0 ? 1 : 0;
/* 4041 */         k = this.fnCurrent.getVarIndex(localNode1);
/* 4042 */         m = this.varRegisters[k];
/* 4043 */         this.cfw.addALoad(m);
/* 4044 */         if (j != 0) {
/* 4045 */           this.cfw.add(89);
/*      */         }
/* 4047 */         addObjectToDouble();
/* 4048 */         this.cfw.addPush(1.0D);
/* 4049 */         if ((i & 0x1) == 0)
/* 4050 */           this.cfw.add(99);
/*      */         else {
/* 4052 */           this.cfw.add(103);
/*      */         }
/* 4054 */         addDoubleWrap();
/* 4055 */         if (j == 0) {
/* 4056 */           this.cfw.add(89);
/*      */         }
/* 4058 */         this.cfw.addAStore(m);
/* 4059 */       }break;
/*      */     case 39:
/* 4063 */       this.cfw.addALoad(this.variableObjectLocal);
/* 4064 */       this.cfw.addPush(localNode1.getString());
/* 4065 */       this.cfw.addALoad(this.contextLocal);
/* 4066 */       this.cfw.addPush(i);
/* 4067 */       addScriptRuntimeInvoke("nameIncrDecr", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */ 
/* 4072 */       break;
/*      */     case 34:
/* 4074 */       throw Kit.codeBug();
/*      */     case 33:
/* 4076 */       localNode2 = localNode1.getFirstChild();
/* 4077 */       generateExpression(localNode2, paramNode);
/* 4078 */       generateExpression(localNode2.getNext(), paramNode);
/* 4079 */       this.cfw.addALoad(this.contextLocal);
/* 4080 */       this.cfw.addPush(i);
/* 4081 */       addScriptRuntimeInvoke("propIncrDecr", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */ 
/* 4086 */       break;
/*      */     case 36:
/* 4089 */       localNode2 = localNode1.getFirstChild();
/* 4090 */       generateExpression(localNode2, paramNode);
/* 4091 */       generateExpression(localNode2.getNext(), paramNode);
/* 4092 */       this.cfw.addALoad(this.contextLocal);
/* 4093 */       this.cfw.addPush(i);
/* 4094 */       if (localNode2.getNext().getIntProp(8, -1) != -1) {
/* 4095 */         addOptRuntimeInvoke("elemIncrDecr", "(Ljava/lang/Object;DLsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */       }
/*      */       else
/*      */       {
/* 4102 */         addScriptRuntimeInvoke("elemIncrDecr", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */       }
/*      */ 
/* 4109 */       break;
/*      */     case 67:
/* 4112 */       localNode2 = localNode1.getFirstChild();
/* 4113 */       generateExpression(localNode2, paramNode);
/* 4114 */       this.cfw.addALoad(this.contextLocal);
/* 4115 */       this.cfw.addPush(i);
/* 4116 */       addScriptRuntimeInvoke("refIncrDecr", "(Lsun/org/mozilla/javascript/internal/Ref;Lsun/org/mozilla/javascript/internal/Context;I)Ljava/lang/Object;");
/*      */ 
/* 4121 */       break;
/*      */     default:
/* 4124 */       Codegen.badTree();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isArithmeticNode(Node paramNode)
/*      */   {
/* 4130 */     int i = paramNode.getType();
/* 4131 */     return (i == 22) || (i == 25) || (i == 24) || (i == 23);
/*      */   }
/*      */ 
/*      */   private void visitArithmetic(Node paramNode1, int paramInt, Node paramNode2, Node paramNode3)
/*      */   {
/* 4140 */     int i = paramNode1.getIntProp(8, -1);
/* 4141 */     if (i != -1) {
/* 4142 */       generateExpression(paramNode2, paramNode1);
/* 4143 */       generateExpression(paramNode2.getNext(), paramNode1);
/* 4144 */       this.cfw.add(paramInt);
/*      */     }
/*      */     else {
/* 4147 */       boolean bool = isArithmeticNode(paramNode3);
/* 4148 */       generateExpression(paramNode2, paramNode1);
/* 4149 */       if (!isArithmeticNode(paramNode2))
/* 4150 */         addObjectToDouble();
/* 4151 */       generateExpression(paramNode2.getNext(), paramNode1);
/* 4152 */       if (!isArithmeticNode(paramNode2.getNext()))
/* 4153 */         addObjectToDouble();
/* 4154 */       this.cfw.add(paramInt);
/* 4155 */       if (!bool)
/* 4156 */         addDoubleWrap();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitBitOp(Node paramNode1, int paramInt, Node paramNode2)
/*      */   {
/* 4163 */     int i = paramNode1.getIntProp(8, -1);
/* 4164 */     generateExpression(paramNode2, paramNode1);
/*      */ 
/* 4169 */     if (paramInt == 20) {
/* 4170 */       addScriptRuntimeInvoke("toUint32", "(Ljava/lang/Object;)J");
/* 4171 */       generateExpression(paramNode2.getNext(), paramNode1);
/* 4172 */       addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
/*      */ 
/* 4175 */       this.cfw.addPush(31);
/* 4176 */       this.cfw.add(126);
/* 4177 */       this.cfw.add(125);
/* 4178 */       this.cfw.add(138);
/* 4179 */       addDoubleWrap();
/* 4180 */       return;
/*      */     }
/* 4182 */     if (i == -1) {
/* 4183 */       addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
/* 4184 */       generateExpression(paramNode2.getNext(), paramNode1);
/* 4185 */       addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
/*      */     }
/*      */     else {
/* 4188 */       addScriptRuntimeInvoke("toInt32", "(D)I");
/* 4189 */       generateExpression(paramNode2.getNext(), paramNode1);
/* 4190 */       addScriptRuntimeInvoke("toInt32", "(D)I");
/*      */     }
/* 4192 */     switch (paramInt) {
/*      */     case 9:
/* 4194 */       this.cfw.add(128);
/* 4195 */       break;
/*      */     case 10:
/* 4197 */       this.cfw.add(130);
/* 4198 */       break;
/*      */     case 11:
/* 4200 */       this.cfw.add(126);
/* 4201 */       break;
/*      */     case 19:
/* 4203 */       this.cfw.add(122);
/* 4204 */       break;
/*      */     case 18:
/* 4206 */       this.cfw.add(120);
/* 4207 */       break;
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     default:
/* 4209 */       throw Codegen.badTree();
/*      */     }
/* 4211 */     this.cfw.add(135);
/* 4212 */     if (i == -1)
/* 4213 */       addDoubleWrap();
/*      */   }
/*      */ 
/*      */   private int nodeIsDirectCallParameter(Node paramNode)
/*      */   {
/* 4219 */     if ((paramNode.getType() == 55) && (this.inDirectCallFunction) && (!this.itsForcedObjectParameters))
/*      */     {
/* 4222 */       int i = this.fnCurrent.getVarIndex(paramNode);
/* 4223 */       if (this.fnCurrent.isParameter(i)) {
/* 4224 */         return this.varRegisters[i];
/*      */       }
/*      */     }
/* 4227 */     return -1;
/*      */   }
/*      */ 
/*      */   private boolean varIsDirectCallParameter(int paramInt)
/*      */   {
/* 4232 */     return (this.fnCurrent.isParameter(paramInt)) && (this.inDirectCallFunction) && (!this.itsForcedObjectParameters);
/*      */   }
/*      */ 
/*      */   private void genSimpleCompare(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 4238 */     if (paramInt2 == -1) throw Codegen.badTree();
/* 4239 */     switch (paramInt1) {
/*      */     case 15:
/* 4241 */       this.cfw.add(152);
/* 4242 */       this.cfw.add(158, paramInt2);
/* 4243 */       break;
/*      */     case 17:
/* 4245 */       this.cfw.add(151);
/* 4246 */       this.cfw.add(156, paramInt2);
/* 4247 */       break;
/*      */     case 14:
/* 4249 */       this.cfw.add(152);
/* 4250 */       this.cfw.add(155, paramInt2);
/* 4251 */       break;
/*      */     case 16:
/* 4253 */       this.cfw.add(151);
/* 4254 */       this.cfw.add(157, paramInt2);
/* 4255 */       break;
/*      */     default:
/* 4257 */       throw Codegen.badTree();
/*      */     }
/*      */ 
/* 4260 */     if (paramInt3 != -1)
/* 4261 */       this.cfw.add(167, paramInt3);
/*      */   }
/*      */ 
/*      */   private void visitIfJumpRelOp(Node paramNode1, Node paramNode2, int paramInt1, int paramInt2)
/*      */   {
/* 4267 */     if ((paramInt1 == -1) || (paramInt2 == -1)) throw Codegen.badTree();
/* 4268 */     int i = paramNode1.getType();
/* 4269 */     Node localNode = paramNode2.getNext();
/* 4270 */     if ((i == 53) || (i == 52)) {
/* 4271 */       generateExpression(paramNode2, paramNode1);
/* 4272 */       generateExpression(localNode, paramNode1);
/* 4273 */       this.cfw.addALoad(this.contextLocal);
/* 4274 */       addScriptRuntimeInvoke(i == 53 ? "instanceOf" : "in", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Z");
/*      */ 
/* 4280 */       this.cfw.add(154, paramInt1);
/* 4281 */       this.cfw.add(167, paramInt2);
/* 4282 */       return;
/*      */     }
/* 4284 */     int j = paramNode1.getIntProp(8, -1);
/* 4285 */     int k = nodeIsDirectCallParameter(paramNode2);
/* 4286 */     int m = nodeIsDirectCallParameter(localNode);
/* 4287 */     if (j != -1)
/*      */     {
/* 4291 */       if (j != 2)
/*      */       {
/* 4293 */         generateExpression(paramNode2, paramNode1);
/* 4294 */       } else if (k != -1) {
/* 4295 */         dcpLoadAsNumber(k);
/*      */       } else {
/* 4297 */         generateExpression(paramNode2, paramNode1);
/* 4298 */         addObjectToDouble();
/*      */       }
/*      */ 
/* 4301 */       if (j != 1)
/*      */       {
/* 4303 */         generateExpression(localNode, paramNode1);
/* 4304 */       } else if (m != -1) {
/* 4305 */         dcpLoadAsNumber(m);
/*      */       } else {
/* 4307 */         generateExpression(localNode, paramNode1);
/* 4308 */         addObjectToDouble();
/*      */       }
/*      */ 
/* 4311 */       genSimpleCompare(i, paramInt1, paramInt2);
/*      */     }
/*      */     else {
/* 4314 */       if ((k != -1) && (m != -1))
/*      */       {
/* 4317 */         int n = this.cfw.getStackTop();
/* 4318 */         int i1 = this.cfw.acquireLabel();
/* 4319 */         this.cfw.addALoad(k);
/* 4320 */         this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4324 */         this.cfw.add(166, i1);
/* 4325 */         this.cfw.addDLoad(k + 1);
/* 4326 */         dcpLoadAsNumber(m);
/* 4327 */         genSimpleCompare(i, paramInt1, paramInt2);
/* 4328 */         if (n != this.cfw.getStackTop()) throw Codegen.badTree();
/*      */ 
/* 4330 */         this.cfw.markLabel(i1);
/* 4331 */         int i2 = this.cfw.acquireLabel();
/* 4332 */         this.cfw.addALoad(m);
/* 4333 */         this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4337 */         this.cfw.add(166, i2);
/* 4338 */         this.cfw.addALoad(k);
/* 4339 */         addObjectToDouble();
/* 4340 */         this.cfw.addDLoad(m + 1);
/* 4341 */         genSimpleCompare(i, paramInt1, paramInt2);
/* 4342 */         if (n != this.cfw.getStackTop()) throw Codegen.badTree();
/*      */ 
/* 4344 */         this.cfw.markLabel(i2);
/*      */ 
/* 4346 */         this.cfw.addALoad(k);
/* 4347 */         this.cfw.addALoad(m);
/*      */       }
/*      */       else {
/* 4350 */         generateExpression(paramNode2, paramNode1);
/* 4351 */         generateExpression(localNode, paramNode1);
/*      */       }
/*      */ 
/* 4354 */       if ((i == 17) || (i == 16)) {
/* 4355 */         this.cfw.add(95);
/*      */       }
/* 4357 */       String str = (i == 14) || (i == 16) ? "cmp_LT" : "cmp_LE";
/*      */ 
/* 4359 */       addScriptRuntimeInvoke(str, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
/*      */ 
/* 4363 */       this.cfw.add(154, paramInt1);
/* 4364 */       this.cfw.add(167, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitIfJumpEqOp(Node paramNode1, Node paramNode2, int paramInt1, int paramInt2)
/*      */   {
/* 4371 */     if ((paramInt1 == -1) || (paramInt2 == -1)) throw Codegen.badTree();
/*      */ 
/* 4373 */     int i = this.cfw.getStackTop();
/* 4374 */     int j = paramNode1.getType();
/* 4375 */     Node localNode = paramNode2.getNext();
/*      */     int k;
/* 4378 */     if ((paramNode2.getType() == 42) || (localNode.getType() == 42))
/*      */     {
/* 4380 */       if (paramNode2.getType() == 42) {
/* 4381 */         paramNode2 = localNode;
/*      */       }
/* 4383 */       generateExpression(paramNode2, paramNode1);
/* 4384 */       if ((j == 46) || (j == 47)) {
/* 4385 */         k = j == 46 ? 198 : 199;
/*      */ 
/* 4387 */         this.cfw.add(k, paramInt1);
/*      */       } else {
/* 4389 */         if (j != 12)
/*      */         {
/* 4391 */           if (j != 13) throw Codegen.badTree();
/* 4392 */           k = paramInt1;
/* 4393 */           paramInt1 = paramInt2;
/* 4394 */           paramInt2 = k;
/*      */         }
/* 4396 */         this.cfw.add(89);
/* 4397 */         k = this.cfw.acquireLabel();
/* 4398 */         this.cfw.add(199, k);
/* 4399 */         short s = this.cfw.getStackTop();
/* 4400 */         this.cfw.add(87);
/* 4401 */         this.cfw.add(167, paramInt1);
/* 4402 */         this.cfw.markLabel(k, s);
/* 4403 */         Codegen.pushUndefined(this.cfw);
/* 4404 */         this.cfw.add(165, paramInt1);
/*      */       }
/* 4406 */       this.cfw.add(167, paramInt2);
/*      */     } else {
/* 4408 */       k = nodeIsDirectCallParameter(paramNode2);
/*      */       Object localObject;
/*      */       int m;
/* 4409 */       if ((k != -1) && (localNode.getType() == 149))
/*      */       {
/* 4412 */         localObject = localNode.getFirstChild();
/* 4413 */         if (((Node)localObject).getType() == 40) {
/* 4414 */           this.cfw.addALoad(k);
/* 4415 */           this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4419 */           m = this.cfw.acquireLabel();
/* 4420 */           this.cfw.add(166, m);
/* 4421 */           this.cfw.addDLoad(k + 1);
/* 4422 */           this.cfw.addPush(((Node)localObject).getDouble());
/* 4423 */           this.cfw.add(151);
/* 4424 */           if (j == 12)
/* 4425 */             this.cfw.add(153, paramInt1);
/*      */           else
/* 4427 */             this.cfw.add(154, paramInt1);
/* 4428 */           this.cfw.add(167, paramInt2);
/* 4429 */           this.cfw.markLabel(m);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4434 */       generateExpression(paramNode2, paramNode1);
/* 4435 */       generateExpression(localNode, paramNode1);
/*      */ 
/* 4439 */       switch (j) {
/*      */       case 12:
/* 4441 */         localObject = "eq";
/* 4442 */         m = 154;
/* 4443 */         break;
/*      */       case 13:
/* 4445 */         localObject = "eq";
/* 4446 */         m = 153;
/* 4447 */         break;
/*      */       case 46:
/* 4449 */         localObject = "shallowEq";
/* 4450 */         m = 154;
/* 4451 */         break;
/*      */       case 47:
/* 4453 */         localObject = "shallowEq";
/* 4454 */         m = 153;
/* 4455 */         break;
/*      */       default:
/* 4457 */         throw Codegen.badTree();
/*      */       }
/* 4459 */       addScriptRuntimeInvoke((String)localObject, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
/*      */ 
/* 4463 */       this.cfw.add(m, paramInt1);
/* 4464 */       this.cfw.add(167, paramInt2);
/*      */     }
/* 4466 */     if (i != this.cfw.getStackTop()) throw Codegen.badTree();
/*      */   }
/*      */ 
/*      */   private void visitSetName(Node paramNode1, Node paramNode2)
/*      */   {
/* 4471 */     String str = paramNode1.getFirstChild().getString();
/* 4472 */     while (paramNode2 != null) {
/* 4473 */       generateExpression(paramNode2, paramNode1);
/* 4474 */       paramNode2 = paramNode2.getNext();
/*      */     }
/* 4476 */     this.cfw.addALoad(this.contextLocal);
/* 4477 */     this.cfw.addALoad(this.variableObjectLocal);
/* 4478 */     this.cfw.addPush(str);
/* 4479 */     addScriptRuntimeInvoke("setName", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void visitStrictSetName(Node paramNode1, Node paramNode2)
/*      */   {
/* 4491 */     String str = paramNode1.getFirstChild().getString();
/* 4492 */     while (paramNode2 != null) {
/* 4493 */       generateExpression(paramNode2, paramNode1);
/* 4494 */       paramNode2 = paramNode2.getNext();
/*      */     }
/* 4496 */     this.cfw.addALoad(this.contextLocal);
/* 4497 */     this.cfw.addALoad(this.variableObjectLocal);
/* 4498 */     this.cfw.addPush(str);
/* 4499 */     addScriptRuntimeInvoke("strictSetName", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void visitSetConst(Node paramNode1, Node paramNode2)
/*      */   {
/* 4511 */     String str = paramNode1.getFirstChild().getString();
/* 4512 */     while (paramNode2 != null) {
/* 4513 */       generateExpression(paramNode2, paramNode1);
/* 4514 */       paramNode2 = paramNode2.getNext();
/*      */     }
/* 4516 */     this.cfw.addALoad(this.contextLocal);
/* 4517 */     this.cfw.addPush(str);
/* 4518 */     addScriptRuntimeInvoke("setConst", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;Ljava/lang/String;)Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void visitGetVar(Node paramNode)
/*      */   {
/* 4529 */     if (!this.hasVarsInRegs) Kit.codeBug();
/* 4530 */     int i = this.fnCurrent.getVarIndex(paramNode);
/* 4531 */     int j = this.varRegisters[i];
/* 4532 */     if (varIsDirectCallParameter(i))
/*      */     {
/* 4537 */       if (paramNode.getIntProp(8, -1) != -1)
/* 4538 */         dcpLoadAsNumber(j);
/*      */       else
/* 4540 */         dcpLoadAsObject(j);
/*      */     }
/* 4542 */     else if (this.fnCurrent.isNumberVar(i))
/* 4543 */       this.cfw.addDLoad(j);
/*      */     else
/* 4545 */       this.cfw.addALoad(j);
/*      */   }
/*      */ 
/*      */   private void visitSetVar(Node paramNode1, Node paramNode2, boolean paramBoolean)
/*      */   {
/* 4551 */     if (!this.hasVarsInRegs) Kit.codeBug();
/* 4552 */     int i = this.fnCurrent.getVarIndex(paramNode1);
/* 4553 */     generateExpression(paramNode2.getNext(), paramNode1);
/* 4554 */     int j = paramNode1.getIntProp(8, -1) != -1 ? 1 : 0;
/* 4555 */     int k = this.varRegisters[i];
/* 4556 */     boolean[] arrayOfBoolean = this.fnCurrent.fnode.getParamAndVarConst();
/* 4557 */     if (arrayOfBoolean[i] != 0) {
/* 4558 */       if (!paramBoolean) {
/* 4559 */         if (j != 0)
/* 4560 */           this.cfw.add(88);
/*      */         else
/* 4562 */           this.cfw.add(87);
/*      */       }
/*      */     }
/* 4565 */     else if (varIsDirectCallParameter(i)) {
/* 4566 */       if (j != 0) {
/* 4567 */         if (paramBoolean) this.cfw.add(92);
/* 4568 */         this.cfw.addALoad(k);
/* 4569 */         this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4573 */         int m = this.cfw.acquireLabel();
/* 4574 */         int n = this.cfw.acquireLabel();
/* 4575 */         this.cfw.add(165, m);
/* 4576 */         short s = this.cfw.getStackTop();
/* 4577 */         addDoubleWrap();
/* 4578 */         this.cfw.addAStore(k);
/* 4579 */         this.cfw.add(167, n);
/* 4580 */         this.cfw.markLabel(m, s);
/* 4581 */         this.cfw.addDStore(k + 1);
/* 4582 */         this.cfw.markLabel(n);
/*      */       }
/*      */       else {
/* 4585 */         if (paramBoolean) this.cfw.add(89);
/* 4586 */         this.cfw.addAStore(k);
/*      */       }
/*      */     } else {
/* 4589 */       boolean bool = this.fnCurrent.isNumberVar(i);
/* 4590 */       if (j != 0) {
/* 4591 */         if (bool) {
/* 4592 */           this.cfw.addDStore(k);
/* 4593 */           if (paramBoolean) this.cfw.addDLoad(k); 
/*      */         }
/* 4595 */         else { if (paramBoolean) this.cfw.add(92);
/*      */ 
/* 4598 */           addDoubleWrap();
/* 4599 */           this.cfw.addAStore(k); }
/*      */       }
/*      */       else {
/* 4602 */         if (bool) Kit.codeBug();
/* 4603 */         this.cfw.addAStore(k);
/* 4604 */         if (paramBoolean) this.cfw.addALoad(k);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitSetConstVar(Node paramNode1, Node paramNode2, boolean paramBoolean)
/*      */   {
/* 4611 */     if (!this.hasVarsInRegs) Kit.codeBug();
/* 4612 */     int i = this.fnCurrent.getVarIndex(paramNode1);
/* 4613 */     generateExpression(paramNode2.getNext(), paramNode1);
/* 4614 */     int j = paramNode1.getIntProp(8, -1) != -1 ? 1 : 0;
/* 4615 */     int k = this.varRegisters[i];
/* 4616 */     int m = this.cfw.acquireLabel();
/* 4617 */     int n = this.cfw.acquireLabel();
/*      */     short s;
/* 4618 */     if (j != 0) {
/* 4619 */       this.cfw.addILoad(k + 2);
/* 4620 */       this.cfw.add(154, n);
/* 4621 */       s = this.cfw.getStackTop();
/* 4622 */       this.cfw.addPush(1);
/* 4623 */       this.cfw.addIStore(k + 2);
/* 4624 */       this.cfw.addDStore(k);
/* 4625 */       if (paramBoolean) {
/* 4626 */         this.cfw.addDLoad(k);
/* 4627 */         this.cfw.markLabel(n, s);
/*      */       } else {
/* 4629 */         this.cfw.add(167, m);
/* 4630 */         this.cfw.markLabel(n, s);
/* 4631 */         this.cfw.add(88);
/*      */       }
/*      */     }
/*      */     else {
/* 4635 */       this.cfw.addILoad(k + 1);
/* 4636 */       this.cfw.add(154, n);
/* 4637 */       s = this.cfw.getStackTop();
/* 4638 */       this.cfw.addPush(1);
/* 4639 */       this.cfw.addIStore(k + 1);
/* 4640 */       this.cfw.addAStore(k);
/* 4641 */       if (paramBoolean) {
/* 4642 */         this.cfw.addALoad(k);
/* 4643 */         this.cfw.markLabel(n, s);
/*      */       } else {
/* 4645 */         this.cfw.add(167, m);
/* 4646 */         this.cfw.markLabel(n, s);
/* 4647 */         this.cfw.add(87);
/*      */       }
/*      */     }
/* 4650 */     this.cfw.markLabel(m);
/*      */   }
/*      */ 
/*      */   private void visitGetProp(Node paramNode1, Node paramNode2)
/*      */   {
/* 4655 */     generateExpression(paramNode2, paramNode1);
/* 4656 */     Node localNode = paramNode2.getNext();
/* 4657 */     generateExpression(localNode, paramNode1);
/* 4658 */     if (paramNode1.getType() == 34) {
/* 4659 */       this.cfw.addALoad(this.contextLocal);
/* 4660 */       addScriptRuntimeInvoke("getObjectPropNoWarn", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */ 
/* 4666 */       return;
/*      */     }
/*      */ 
/* 4672 */     int i = paramNode2.getType();
/* 4673 */     if ((i == 43) && (localNode.getType() == 41)) {
/* 4674 */       this.cfw.addALoad(this.contextLocal);
/* 4675 */       addScriptRuntimeInvoke("getObjectProp", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */     }
/*      */     else
/*      */     {
/* 4682 */       this.cfw.addALoad(this.contextLocal);
/* 4683 */       this.cfw.addALoad(this.variableObjectLocal);
/* 4684 */       addScriptRuntimeInvoke("getObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitSetProp(int paramInt, Node paramNode1, Node paramNode2)
/*      */   {
/* 4696 */     Node localNode1 = paramNode2;
/* 4697 */     generateExpression(paramNode2, paramNode1);
/* 4698 */     paramNode2 = paramNode2.getNext();
/* 4699 */     if (paramInt == 139) {
/* 4700 */       this.cfw.add(89);
/*      */     }
/* 4702 */     Node localNode2 = paramNode2;
/* 4703 */     generateExpression(paramNode2, paramNode1);
/* 4704 */     paramNode2 = paramNode2.getNext();
/* 4705 */     if (paramInt == 139)
/*      */     {
/* 4707 */       this.cfw.add(90);
/*      */ 
/* 4710 */       if ((localNode1.getType() == 43) && (localNode2.getType() == 41))
/*      */       {
/* 4713 */         this.cfw.addALoad(this.contextLocal);
/* 4714 */         addScriptRuntimeInvoke("getObjectProp", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */       else
/*      */       {
/* 4721 */         this.cfw.addALoad(this.contextLocal);
/* 4722 */         addScriptRuntimeInvoke("getObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4730 */     generateExpression(paramNode2, paramNode1);
/* 4731 */     this.cfw.addALoad(this.contextLocal);
/* 4732 */     addScriptRuntimeInvoke("setObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   private void visitSetElem(int paramInt, Node paramNode1, Node paramNode2)
/*      */   {
/* 4743 */     generateExpression(paramNode2, paramNode1);
/* 4744 */     paramNode2 = paramNode2.getNext();
/* 4745 */     if (paramInt == 140) {
/* 4746 */       this.cfw.add(89);
/*      */     }
/* 4748 */     generateExpression(paramNode2, paramNode1);
/* 4749 */     paramNode2 = paramNode2.getNext();
/* 4750 */     int i = paramNode1.getIntProp(8, -1) != -1 ? 1 : 0;
/* 4751 */     if (paramInt == 140) {
/* 4752 */       if (i != 0)
/*      */       {
/* 4755 */         this.cfw.add(93);
/* 4756 */         this.cfw.addALoad(this.contextLocal);
/* 4757 */         addOptRuntimeInvoke("getObjectIndex", "(Ljava/lang/Object;DLsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */       else
/*      */       {
/* 4765 */         this.cfw.add(90);
/* 4766 */         this.cfw.addALoad(this.contextLocal);
/* 4767 */         addScriptRuntimeInvoke("getObjectElem", "(Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4775 */     generateExpression(paramNode2, paramNode1);
/* 4776 */     this.cfw.addALoad(this.contextLocal);
/* 4777 */     if (i != 0) {
/* 4778 */       addScriptRuntimeInvoke("setObjectIndex", "(Ljava/lang/Object;DLjava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */     }
/*      */     else
/*      */     {
/* 4786 */       addScriptRuntimeInvoke("setObjectElem", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Context;)Ljava/lang/Object;");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void visitDotQuery(Node paramNode1, Node paramNode2)
/*      */   {
/* 4798 */     updateLineNumber(paramNode1);
/* 4799 */     generateExpression(paramNode2, paramNode1);
/* 4800 */     this.cfw.addALoad(this.variableObjectLocal);
/* 4801 */     addScriptRuntimeInvoke("enterDotQuery", "(Ljava/lang/Object;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 4805 */     this.cfw.addAStore(this.variableObjectLocal);
/*      */ 
/* 4810 */     this.cfw.add(1);
/* 4811 */     int i = this.cfw.acquireLabel();
/* 4812 */     this.cfw.markLabel(i);
/* 4813 */     this.cfw.add(87);
/*      */ 
/* 4815 */     generateExpression(paramNode2.getNext(), paramNode1);
/* 4816 */     addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
/* 4817 */     this.cfw.addALoad(this.variableObjectLocal);
/* 4818 */     addScriptRuntimeInvoke("updateDotQuery", "(ZLsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;");
/*      */ 
/* 4822 */     this.cfw.add(89);
/* 4823 */     this.cfw.add(198, i);
/*      */ 
/* 4825 */     this.cfw.addALoad(this.variableObjectLocal);
/* 4826 */     addScriptRuntimeInvoke("leaveDotQuery", "(Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 4829 */     this.cfw.addAStore(this.variableObjectLocal);
/*      */   }
/*      */ 
/*      */   private int getLocalBlockRegister(Node paramNode)
/*      */   {
/* 4834 */     Node localNode = (Node)paramNode.getProp(3);
/* 4835 */     int i = localNode.getExistingIntProp(2);
/* 4836 */     return i;
/*      */   }
/*      */ 
/*      */   private void dcpLoadAsNumber(int paramInt)
/*      */   {
/* 4841 */     this.cfw.addALoad(paramInt);
/* 4842 */     this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4846 */     int i = this.cfw.acquireLabel();
/* 4847 */     this.cfw.add(165, i);
/* 4848 */     short s = this.cfw.getStackTop();
/* 4849 */     this.cfw.addALoad(paramInt);
/* 4850 */     addObjectToDouble();
/* 4851 */     int j = this.cfw.acquireLabel();
/* 4852 */     this.cfw.add(167, j);
/* 4853 */     this.cfw.markLabel(i, s);
/* 4854 */     this.cfw.addDLoad(paramInt + 1);
/* 4855 */     this.cfw.markLabel(j);
/*      */   }
/*      */ 
/*      */   private void dcpLoadAsObject(int paramInt)
/*      */   {
/* 4860 */     this.cfw.addALoad(paramInt);
/* 4861 */     this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
/*      */ 
/* 4865 */     int i = this.cfw.acquireLabel();
/* 4866 */     this.cfw.add(165, i);
/* 4867 */     short s = this.cfw.getStackTop();
/* 4868 */     this.cfw.addALoad(paramInt);
/* 4869 */     int j = this.cfw.acquireLabel();
/* 4870 */     this.cfw.add(167, j);
/* 4871 */     this.cfw.markLabel(i, s);
/* 4872 */     this.cfw.addDLoad(paramInt + 1);
/* 4873 */     addDoubleWrap();
/* 4874 */     this.cfw.markLabel(j);
/*      */   }
/*      */ 
/*      */   private void addGoto(Node paramNode, int paramInt)
/*      */   {
/* 4879 */     int i = getTargetLabel(paramNode);
/* 4880 */     this.cfw.add(paramInt, i);
/*      */   }
/*      */ 
/*      */   private void addObjectToDouble()
/*      */   {
/* 4885 */     addScriptRuntimeInvoke("toNumber", "(Ljava/lang/Object;)D");
/*      */   }
/*      */ 
/*      */   private void addNewObjectArray(int paramInt)
/*      */   {
/* 4890 */     if (paramInt == 0) {
/* 4891 */       if (this.itsZeroArgArray >= 0)
/* 4892 */         this.cfw.addALoad(this.itsZeroArgArray);
/*      */       else {
/* 4894 */         this.cfw.add(178, "sun/org/mozilla/javascript/internal/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4899 */       this.cfw.addPush(paramInt);
/* 4900 */       this.cfw.add(189, "java/lang/Object");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addScriptRuntimeInvoke(String paramString1, String paramString2)
/*      */   {
/* 4907 */     this.cfw.addInvoke(184, "sun.org.mozilla.javascript.internal.ScriptRuntime", paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   private void addOptRuntimeInvoke(String paramString1, String paramString2)
/*      */   {
/* 4916 */     this.cfw.addInvoke(184, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime", paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   private void addJumpedBooleanWrap(int paramInt1, int paramInt2)
/*      */   {
/* 4924 */     this.cfw.markLabel(paramInt2);
/* 4925 */     int i = this.cfw.acquireLabel();
/* 4926 */     this.cfw.add(178, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
/*      */ 
/* 4928 */     this.cfw.add(167, i);
/* 4929 */     this.cfw.markLabel(paramInt1);
/* 4930 */     this.cfw.add(178, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
/*      */ 
/* 4932 */     this.cfw.markLabel(i);
/* 4933 */     this.cfw.adjustStackTop(-1);
/*      */   }
/*      */ 
/*      */   private void addDoubleWrap()
/*      */   {
/* 4938 */     addOptRuntimeInvoke("wrapDouble", "(D)Ljava/lang/Double;");
/*      */   }
/*      */ 
/*      */   private short getNewWordPairLocal(boolean paramBoolean)
/*      */   {
/* 4949 */     short s = getConsecutiveSlots(2, paramBoolean);
/* 4950 */     if (s < 255) {
/* 4951 */       this.locals[s] = 1;
/* 4952 */       this.locals[(s + 1)] = 1;
/* 4953 */       if (paramBoolean)
/* 4954 */         this.locals[(s + 2)] = 1;
/* 4955 */       if (s == this.firstFreeLocal) {
/* 4956 */         for (int i = this.firstFreeLocal + 2; i < 256; i++) {
/* 4957 */           if (this.locals[i] == 0) {
/* 4958 */             this.firstFreeLocal = ((short)i);
/* 4959 */             if (this.localsMax < this.firstFreeLocal)
/* 4960 */               this.localsMax = this.firstFreeLocal;
/* 4961 */             return s;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 4966 */         return s;
/*      */       }
/*      */     }
/* 4969 */     throw Context.reportRuntimeError("Program too complex (out of locals)");
/*      */   }
/*      */ 
/*      */   private short getNewWordLocal(boolean paramBoolean)
/*      */   {
/* 4975 */     short s = getConsecutiveSlots(1, paramBoolean);
/* 4976 */     if (s < 255) {
/* 4977 */       this.locals[s] = 1;
/* 4978 */       if (paramBoolean)
/* 4979 */         this.locals[(s + 1)] = 1;
/* 4980 */       if (s == this.firstFreeLocal) {
/* 4981 */         for (int i = this.firstFreeLocal + 2; i < 256; i++) {
/* 4982 */           if (this.locals[i] == 0) {
/* 4983 */             this.firstFreeLocal = ((short)i);
/* 4984 */             if (this.localsMax < this.firstFreeLocal)
/* 4985 */               this.localsMax = this.firstFreeLocal;
/* 4986 */             return s;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 4991 */         return s;
/*      */       }
/*      */     }
/* 4994 */     throw Context.reportRuntimeError("Program too complex (out of locals)");
/*      */   }
/*      */ 
/*      */   private short getNewWordLocal()
/*      */   {
/* 5000 */     short s = this.firstFreeLocal;
/* 5001 */     this.locals[s] = 1;
/* 5002 */     for (int i = this.firstFreeLocal + 1; i < 256; i++) {
/* 5003 */       if (this.locals[i] == 0) {
/* 5004 */         this.firstFreeLocal = ((short)i);
/* 5005 */         if (this.localsMax < this.firstFreeLocal)
/* 5006 */           this.localsMax = this.firstFreeLocal;
/* 5007 */         return s;
/*      */       }
/*      */     }
/* 5010 */     throw Context.reportRuntimeError("Program too complex (out of locals)");
/*      */   }
/*      */ 
/*      */   private short getConsecutiveSlots(int paramInt, boolean paramBoolean)
/*      */   {
/* 5015 */     if (paramBoolean)
/* 5016 */       paramInt++;
/* 5017 */     int i = this.firstFreeLocal;
/*      */ 
/* 5019 */     while (i < 255)
/*      */     {
/* 5022 */       for (int j = 0; (j < paramInt) && 
/* 5023 */         (this.locals[(i + j)] == 0); j++);
/* 5025 */       if (j >= paramInt)
/*      */         break;
/* 5027 */       i = (short)(i + 1);
/*      */     }
/* 5029 */     return i;
/*      */   }
/*      */ 
/*      */   private void incReferenceWordLocal(short paramShort)
/*      */   {
/* 5035 */     this.locals[paramShort] += 1;
/*      */   }
/*      */ 
/*      */   private void decReferenceWordLocal(short paramShort)
/*      */   {
/* 5041 */     this.locals[paramShort] -= 1;
/*      */   }
/*      */ 
/*      */   private void releaseWordLocal(short paramShort)
/*      */   {
/* 5046 */     if (paramShort < this.firstFreeLocal)
/* 5047 */       this.firstFreeLocal = paramShort;
/* 5048 */     this.locals[paramShort] = 0;
/*      */   }
/*      */ 
/*      */   static class FinallyReturnPoint
/*      */   {
/* 5102 */     public List<Integer> jsrPoints = new ArrayList();
/* 5103 */     public int tableLabel = 0;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.BodyCodegen
 * JD-Core Version:    0.6.2
 */