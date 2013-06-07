/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.classfile.Attribute;
/*     */ import com.sun.org.apache.bcel.internal.classfile.Code;
/*     */ import com.sun.org.apache.bcel.internal.classfile.CodeException;
/*     */ import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
/*     */ import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
/*     */ import com.sun.org.apache.bcel.internal.classfile.LineNumber;
/*     */ import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
/*     */ import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
/*     */ import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
/*     */ import com.sun.org.apache.bcel.internal.classfile.Method;
/*     */ import com.sun.org.apache.bcel.internal.classfile.Utility;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class MethodGen extends FieldGenOrMethodGen
/*     */ {
/*     */   private String class_name;
/*     */   private Type[] arg_types;
/*     */   private String[] arg_names;
/*     */   private int max_locals;
/*     */   private int max_stack;
/*     */   private InstructionList il;
/*     */   private boolean strip_attributes;
/*  89 */   private ArrayList variable_vec = new ArrayList();
/*  90 */   private ArrayList line_number_vec = new ArrayList();
/*  91 */   private ArrayList exception_vec = new ArrayList();
/*  92 */   private ArrayList throws_vec = new ArrayList();
/*  93 */   private ArrayList code_attrs_vec = new ArrayList();
/*     */   private ArrayList observers;
/*     */ 
/*     */   public MethodGen(int access_flags, Type return_type, Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cp)
/*     */   {
/* 120 */     setAccessFlags(access_flags);
/* 121 */     setType(return_type);
/* 122 */     setArgumentTypes(arg_types);
/* 123 */     setArgumentNames(arg_names);
/* 124 */     setName(method_name);
/* 125 */     setClassName(class_name);
/* 126 */     setInstructionList(il);
/* 127 */     setConstantPool(cp);
/*     */ 
/* 129 */     boolean abstract_ = (isAbstract()) || (isNative());
/* 130 */     InstructionHandle start = null;
/* 131 */     InstructionHandle end = null;
/*     */ 
/* 133 */     if (!abstract_) {
/* 134 */       start = il.getStart();
/* 135 */       end = il.getEnd();
/*     */ 
/* 139 */       if ((!isStatic()) && (class_name != null)) {
/* 140 */         addLocalVariable("this", new ObjectType(class_name), start, end);
/*     */       }
/*     */     }
/*     */ 
/* 144 */     if (arg_types != null) {
/* 145 */       int size = arg_types.length;
/*     */ 
/* 147 */       for (int i = 0; i < size; i++) {
/* 148 */         if (Type.VOID == arg_types[i]) {
/* 149 */           throw new ClassGenException("'void' is an illegal argument type for a method");
/*     */         }
/*     */       }
/*     */ 
/* 153 */       if (arg_names != null) {
/* 154 */         if (size != arg_names.length)
/* 155 */           throw new ClassGenException("Mismatch in argument array lengths: " + size + " vs. " + arg_names.length);
/*     */       }
/*     */       else {
/* 158 */         arg_names = new String[size];
/*     */ 
/* 160 */         for (int i = 0; i < size; i++) {
/* 161 */           arg_names[i] = ("arg" + i);
/*     */         }
/* 163 */         setArgumentNames(arg_names);
/*     */       }
/*     */ 
/* 166 */       if (!abstract_)
/* 167 */         for (int i = 0; i < size; i++)
/* 168 */           addLocalVariable(arg_names[i], arg_types[i], start, end);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MethodGen(Method m, String class_name, ConstantPoolGen cp)
/*     */   {
/* 182 */     this(m.getAccessFlags(), Type.getReturnType(m.getSignature()), Type.getArgumentTypes(m.getSignature()), null, m.getName(), class_name, (m.getAccessFlags() & 0x500) == 0 ? new InstructionList(m.getCode().getCode()) : null, cp);
/*     */ 
/* 189 */     Attribute[] attributes = m.getAttributes();
/* 190 */     for (int i = 0; i < attributes.length; i++) {
/* 191 */       Attribute a = attributes[i];
/*     */ 
/* 193 */       if ((a instanceof Code)) {
/* 194 */         Code c = (Code)a;
/* 195 */         setMaxStack(c.getMaxStack());
/* 196 */         setMaxLocals(c.getMaxLocals());
/*     */ 
/* 198 */         CodeException[] ces = c.getExceptionTable();
/*     */ 
/* 200 */         if (ces != null) {
/* 201 */           for (int j = 0; j < ces.length; j++) {
/* 202 */             CodeException ce = ces[j];
/* 203 */             int type = ce.getCatchType();
/* 204 */             ObjectType c_type = null;
/*     */ 
/* 206 */             if (type > 0) {
/* 207 */               String cen = m.getConstantPool().getConstantString(type, (byte)7);
/* 208 */               c_type = new ObjectType(cen);
/*     */             }
/*     */ 
/* 211 */             int end_pc = ce.getEndPC();
/* 212 */             int length = m.getCode().getCode().length;
/*     */             InstructionHandle end;
/*     */             InstructionHandle end;
/* 216 */             if (length == end_pc) {
/* 217 */               end = this.il.getEnd();
/*     */             } else {
/* 219 */               end = this.il.findHandle(end_pc);
/* 220 */               end = end.getPrev();
/*     */             }
/*     */ 
/* 223 */             addExceptionHandler(this.il.findHandle(ce.getStartPC()), end, this.il.findHandle(ce.getHandlerPC()), c_type);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 228 */         Attribute[] c_attributes = c.getAttributes();
/* 229 */         for (int j = 0; j < c_attributes.length; j++) {
/* 230 */           a = c_attributes[j];
/*     */ 
/* 232 */           if ((a instanceof LineNumberTable)) {
/* 233 */             LineNumber[] ln = ((LineNumberTable)a).getLineNumberTable();
/*     */ 
/* 235 */             for (int k = 0; k < ln.length; k++) {
/* 236 */               LineNumber l = ln[k];
/* 237 */               addLineNumber(this.il.findHandle(l.getStartPC()), l.getLineNumber());
/*     */             }
/* 239 */           } else if ((a instanceof LocalVariableTable)) {
/* 240 */             LocalVariable[] lv = ((LocalVariableTable)a).getLocalVariableTable();
/*     */ 
/* 242 */             removeLocalVariables();
/*     */ 
/* 244 */             for (int k = 0; k < lv.length; k++) {
/* 245 */               LocalVariable l = lv[k];
/* 246 */               InstructionHandle start = this.il.findHandle(l.getStartPC());
/* 247 */               InstructionHandle end = this.il.findHandle(l.getStartPC() + l.getLength());
/*     */ 
/* 250 */               if (null == start) {
/* 251 */                 start = this.il.getStart();
/*     */               }
/*     */ 
/* 254 */               if (null == end) {
/* 255 */                 end = this.il.getEnd();
/*     */               }
/*     */ 
/* 258 */               addLocalVariable(l.getName(), Type.getType(l.getSignature()), l.getIndex(), start, end);
/*     */             }
/*     */           }
/*     */           else {
/* 262 */             addCodeAttribute(a);
/*     */           }
/*     */         } } else if ((a instanceof ExceptionTable)) {
/* 265 */         String[] names = ((ExceptionTable)a).getExceptionNames();
/* 266 */         for (int j = 0; j < names.length; j++)
/* 267 */           addException(names[j]);
/*     */       } else {
/* 269 */         addAttribute(a);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public LocalVariableGen addLocalVariable(String name, Type type, int slot, InstructionHandle start, InstructionHandle end)
/*     */   {
/* 288 */     byte t = type.getType();
/*     */ 
/* 290 */     if (t != 16) {
/* 291 */       int add = type.getSize();
/*     */ 
/* 293 */       if (slot + add > this.max_locals) {
/* 294 */         this.max_locals = (slot + add);
/*     */       }
/* 296 */       LocalVariableGen l = new LocalVariableGen(slot, name, type, start, end);
/*     */       int i;
/* 299 */       if ((i = this.variable_vec.indexOf(l)) >= 0)
/* 300 */         this.variable_vec.set(i, l);
/*     */       else {
/* 302 */         this.variable_vec.add(l);
/*     */       }
/* 304 */       return l;
/*     */     }
/* 306 */     throw new IllegalArgumentException("Can not use " + type + " as type for local variable");
/*     */   }
/*     */ 
/*     */   public LocalVariableGen addLocalVariable(String name, Type type, InstructionHandle start, InstructionHandle end)
/*     */   {
/* 327 */     return addLocalVariable(name, type, this.max_locals, start, end);
/*     */   }
/*     */ 
/*     */   public void removeLocalVariable(LocalVariableGen l)
/*     */   {
/* 335 */     this.variable_vec.remove(l);
/*     */   }
/*     */ 
/*     */   public void removeLocalVariables()
/*     */   {
/* 342 */     this.variable_vec.clear();
/*     */   }
/*     */ 
/*     */   private static final void sort(LocalVariableGen[] vars, int l, int r)
/*     */   {
/* 349 */     int i = l; int j = r;
/* 350 */     int m = vars[((l + r) / 2)].getIndex();
/*     */     do
/*     */     {
/* 354 */       while (vars[i].getIndex() < m) i++;
/* 355 */       while (m < vars[j].getIndex()) j--;
/*     */ 
/* 357 */       if (i <= j) {
/* 358 */         LocalVariableGen h = vars[i]; vars[i] = vars[j]; vars[j] = h;
/* 359 */         i++; j--;
/*     */       }
/*     */     }
/* 361 */     while (i <= j);
/*     */ 
/* 363 */     if (l < j) sort(vars, l, j);
/* 364 */     if (i < r) sort(vars, i, r);
/*     */   }
/*     */ 
/*     */   public LocalVariableGen[] getLocalVariables()
/*     */   {
/* 374 */     int size = this.variable_vec.size();
/* 375 */     LocalVariableGen[] lg = new LocalVariableGen[size];
/* 376 */     this.variable_vec.toArray(lg);
/*     */ 
/* 378 */     for (int i = 0; i < size; i++) {
/* 379 */       if (lg[i].getStart() == null) {
/* 380 */         lg[i].setStart(this.il.getStart());
/*     */       }
/* 382 */       if (lg[i].getEnd() == null) {
/* 383 */         lg[i].setEnd(this.il.getEnd());
/*     */       }
/*     */     }
/* 386 */     if (size > 1) {
/* 387 */       sort(lg, 0, size - 1);
/*     */     }
/* 389 */     return lg;
/*     */   }
/*     */ 
/*     */   public LocalVariableTable getLocalVariableTable(ConstantPoolGen cp)
/*     */   {
/* 396 */     LocalVariableGen[] lg = getLocalVariables();
/* 397 */     int size = lg.length;
/* 398 */     LocalVariable[] lv = new LocalVariable[size];
/*     */ 
/* 400 */     for (int i = 0; i < size; i++) {
/* 401 */       lv[i] = lg[i].getLocalVariable(cp);
/*     */     }
/* 403 */     return new LocalVariableTable(cp.addUtf8("LocalVariableTable"), 2 + lv.length * 10, lv, cp.getConstantPool());
/*     */   }
/*     */ 
/*     */   public LineNumberGen addLineNumber(InstructionHandle ih, int src_line)
/*     */   {
/* 415 */     LineNumberGen l = new LineNumberGen(ih, src_line);
/* 416 */     this.line_number_vec.add(l);
/* 417 */     return l;
/*     */   }
/*     */ 
/*     */   public void removeLineNumber(LineNumberGen l)
/*     */   {
/* 424 */     this.line_number_vec.remove(l);
/*     */   }
/*     */ 
/*     */   public void removeLineNumbers()
/*     */   {
/* 431 */     this.line_number_vec.clear();
/*     */   }
/*     */ 
/*     */   public LineNumberGen[] getLineNumbers()
/*     */   {
/* 438 */     LineNumberGen[] lg = new LineNumberGen[this.line_number_vec.size()];
/* 439 */     this.line_number_vec.toArray(lg);
/* 440 */     return lg;
/*     */   }
/*     */ 
/*     */   public LineNumberTable getLineNumberTable(ConstantPoolGen cp)
/*     */   {
/* 447 */     int size = this.line_number_vec.size();
/* 448 */     LineNumber[] ln = new LineNumber[size];
/*     */     try
/*     */     {
/* 451 */       for (int i = 0; i < size; i++)
/* 452 */         ln[i] = ((LineNumberGen)this.line_number_vec.get(i)).getLineNumber();
/*     */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */     }
/* 455 */     return new LineNumberTable(cp.addUtf8("LineNumberTable"), 2 + ln.length * 4, ln, cp.getConstantPool());
/*     */   }
/*     */ 
/*     */   public CodeExceptionGen addExceptionHandler(InstructionHandle start_pc, InstructionHandle end_pc, InstructionHandle handler_pc, ObjectType catch_type)
/*     */   {
/* 474 */     if ((start_pc == null) || (end_pc == null) || (handler_pc == null)) {
/* 475 */       throw new ClassGenException("Exception handler target is null instruction");
/*     */     }
/* 477 */     CodeExceptionGen c = new CodeExceptionGen(start_pc, end_pc, handler_pc, catch_type);
/*     */ 
/* 479 */     this.exception_vec.add(c);
/* 480 */     return c;
/*     */   }
/*     */ 
/*     */   public void removeExceptionHandler(CodeExceptionGen c)
/*     */   {
/* 487 */     this.exception_vec.remove(c);
/*     */   }
/*     */ 
/*     */   public void removeExceptionHandlers()
/*     */   {
/* 494 */     this.exception_vec.clear();
/*     */   }
/*     */ 
/*     */   public CodeExceptionGen[] getExceptionHandlers()
/*     */   {
/* 501 */     CodeExceptionGen[] cg = new CodeExceptionGen[this.exception_vec.size()];
/* 502 */     this.exception_vec.toArray(cg);
/* 503 */     return cg;
/*     */   }
/*     */ 
/*     */   private CodeException[] getCodeExceptions()
/*     */   {
/* 510 */     int size = this.exception_vec.size();
/* 511 */     CodeException[] c_exc = new CodeException[size];
/*     */     try
/*     */     {
/* 514 */       for (int i = 0; i < size; i++) {
/* 515 */         CodeExceptionGen c = (CodeExceptionGen)this.exception_vec.get(i);
/* 516 */         c_exc[i] = c.getCodeException(this.cp);
/*     */       }
/*     */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */     }
/* 520 */     return c_exc;
/*     */   }
/*     */ 
/*     */   public void addException(String class_name)
/*     */   {
/* 529 */     this.throws_vec.add(class_name);
/*     */   }
/*     */ 
/*     */   public void removeException(String c)
/*     */   {
/* 536 */     this.throws_vec.remove(c);
/*     */   }
/*     */ 
/*     */   public void removeExceptions()
/*     */   {
/* 543 */     this.throws_vec.clear();
/*     */   }
/*     */ 
/*     */   public String[] getExceptions()
/*     */   {
/* 550 */     String[] e = new String[this.throws_vec.size()];
/* 551 */     this.throws_vec.toArray(e);
/* 552 */     return e;
/*     */   }
/*     */ 
/*     */   private ExceptionTable getExceptionTable(ConstantPoolGen cp)
/*     */   {
/* 559 */     int size = this.throws_vec.size();
/* 560 */     int[] ex = new int[size];
/*     */     try
/*     */     {
/* 563 */       for (int i = 0; i < size; i++)
/* 564 */         ex[i] = cp.addClass((String)this.throws_vec.get(i));
/*     */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */     }
/* 567 */     return new ExceptionTable(cp.addUtf8("Exceptions"), 2 + 2 * size, ex, cp.getConstantPool());
/*     */   }
/*     */ 
/*     */   public void addCodeAttribute(Attribute a)
/*     */   {
/* 580 */     this.code_attrs_vec.add(a);
/*     */   }
/*     */ 
/*     */   public void removeCodeAttribute(Attribute a)
/*     */   {
/* 585 */     this.code_attrs_vec.remove(a);
/*     */   }
/*     */ 
/*     */   public void removeCodeAttributes()
/*     */   {
/* 591 */     this.code_attrs_vec.clear();
/*     */   }
/*     */ 
/*     */   public Attribute[] getCodeAttributes()
/*     */   {
/* 598 */     Attribute[] attributes = new Attribute[this.code_attrs_vec.size()];
/* 599 */     this.code_attrs_vec.toArray(attributes);
/* 600 */     return attributes;
/*     */   }
/*     */ 
/*     */   public Method getMethod()
/*     */   {
/* 610 */     String signature = getSignature();
/* 611 */     int name_index = this.cp.addUtf8(this.name);
/* 612 */     int signature_index = this.cp.addUtf8(signature);
/*     */ 
/* 616 */     byte[] byte_code = null;
/*     */ 
/* 618 */     if (this.il != null) {
/* 619 */       byte_code = this.il.getByteCode();
/*     */     }
/* 621 */     LineNumberTable lnt = null;
/* 622 */     LocalVariableTable lvt = null;
/*     */ 
/* 626 */     if ((this.variable_vec.size() > 0) && (!this.strip_attributes)) {
/* 627 */       addCodeAttribute(lvt = getLocalVariableTable(this.cp));
/*     */     }
/* 629 */     if ((this.line_number_vec.size() > 0) && (!this.strip_attributes)) {
/* 630 */       addCodeAttribute(lnt = getLineNumberTable(this.cp));
/*     */     }
/* 632 */     Attribute[] code_attrs = getCodeAttributes();
/*     */ 
/* 636 */     int attrs_len = 0;
/* 637 */     for (int i = 0; i < code_attrs.length; i++) {
/* 638 */       attrs_len += code_attrs[i].getLength() + 6;
/*     */     }
/* 640 */     CodeException[] c_exc = getCodeExceptions();
/* 641 */     int exc_len = c_exc.length * 8;
/*     */ 
/* 643 */     Code code = null;
/*     */ 
/* 645 */     if ((this.il != null) && (!isAbstract()))
/*     */     {
/* 647 */       Attribute[] attributes = getAttributes();
/* 648 */       for (int i = 0; i < attributes.length; i++) {
/* 649 */         Attribute a = attributes[i];
/*     */ 
/* 651 */         if ((a instanceof Code)) {
/* 652 */           removeAttribute(a);
/*     */         }
/*     */       }
/* 655 */       code = new Code(this.cp.addUtf8("Code"), 8 + byte_code.length + 2 + exc_len + 2 + attrs_len, this.max_stack, this.max_locals, byte_code, c_exc, code_attrs, this.cp.getConstantPool());
/*     */ 
/* 664 */       addAttribute(code);
/*     */     }
/*     */ 
/* 667 */     ExceptionTable et = null;
/*     */ 
/* 669 */     if (this.throws_vec.size() > 0) {
/* 670 */       addAttribute(et = getExceptionTable(this.cp));
/*     */     }
/* 672 */     Method m = new Method(this.access_flags, name_index, signature_index, getAttributes(), this.cp.getConstantPool());
/*     */ 
/* 676 */     if (lvt != null) removeCodeAttribute(lvt);
/* 677 */     if (lnt != null) removeCodeAttribute(lnt);
/* 678 */     if (code != null) removeAttribute(code);
/* 679 */     if (et != null) removeAttribute(et);
/*     */ 
/* 681 */     return m;
/*     */   }
/*     */ 
/*     */   public void removeNOPs()
/*     */   {
/* 690 */     if (this.il != null)
/*     */     {
/*     */       InstructionHandle next;
/* 694 */       for (InstructionHandle ih = this.il.getStart(); ih != null; ih = next) {
/* 695 */         next = ih.next;
/*     */ 
/* 697 */         if ((next != null) && ((ih.getInstruction() instanceof NOP))) { InstructionHandle[] targets;
/*     */           int i;
/*     */           try { this.il.delete(ih);
/*     */           } catch (TargetLostException e) {
/* 701 */             targets = e.getTargets();
/*     */ 
/* 703 */             i = 0; } for (; i < targets.length; i++) {
/* 704 */             InstructionTargeter[] targeters = targets[i].getTargeters();
/*     */ 
/* 706 */             for (int j = 0; j < targeters.length; j++)
/* 707 */               targeters[j].updateTarget(targets[i], next);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxLocals(int m)
/*     */   {
/* 718 */     this.max_locals = m; } 
/* 719 */   public int getMaxLocals() { return this.max_locals; }
/*     */ 
/*     */ 
/*     */   public void setMaxStack(int m)
/*     */   {
/* 724 */     this.max_stack = m; } 
/* 725 */   public int getMaxStack() { return this.max_stack; }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 729 */     return this.class_name; } 
/* 730 */   public void setClassName(String class_name) { this.class_name = class_name; } 
/*     */   public void setReturnType(Type return_type) {
/* 732 */     setType(return_type); } 
/* 733 */   public Type getReturnType() { return getType(); } 
/*     */   public void setArgumentTypes(Type[] arg_types) {
/* 735 */     this.arg_types = arg_types; } 
/* 736 */   public Type[] getArgumentTypes() { return (Type[])this.arg_types.clone(); } 
/* 737 */   public void setArgumentType(int i, Type type) { this.arg_types[i] = type; } 
/* 738 */   public Type getArgumentType(int i) { return this.arg_types[i]; } 
/*     */   public void setArgumentNames(String[] arg_names) {
/* 740 */     this.arg_names = arg_names; } 
/* 741 */   public String[] getArgumentNames() { return (String[])this.arg_names.clone(); } 
/* 742 */   public void setArgumentName(int i, String name) { this.arg_names[i] = name; } 
/* 743 */   public String getArgumentName(int i) { return this.arg_names[i]; } 
/*     */   public InstructionList getInstructionList() {
/* 745 */     return this.il; } 
/* 746 */   public void setInstructionList(InstructionList il) { this.il = il; }
/*     */ 
/*     */   public String getSignature() {
/* 749 */     return Type.getMethodSignature(this.type, this.arg_types);
/*     */   }
/*     */ 
/*     */   public void setMaxStack()
/*     */   {
/* 756 */     if (this.il != null)
/* 757 */       this.max_stack = getMaxStack(this.cp, this.il, getExceptionHandlers());
/*     */     else
/* 759 */       this.max_stack = 0;
/*     */   }
/*     */ 
/*     */   public void setMaxLocals()
/*     */   {
/* 766 */     if (this.il != null) {
/* 767 */       int max = isStatic() ? 0 : 1;
/*     */ 
/* 769 */       if (this.arg_types != null) {
/* 770 */         for (int i = 0; i < this.arg_types.length; i++)
/* 771 */           max += this.arg_types[i].getSize();
/*     */       }
/* 773 */       for (InstructionHandle ih = this.il.getStart(); ih != null; ih = ih.getNext()) {
/* 774 */         Instruction ins = ih.getInstruction();
/*     */ 
/* 776 */         if (((ins instanceof LocalVariableInstruction)) || ((ins instanceof RET)) || ((ins instanceof IINC)))
/*     */         {
/* 779 */           int index = ((IndexedInstruction)ins).getIndex() + ((TypedInstruction)ins).getType(this.cp).getSize();
/*     */ 
/* 782 */           if (index > max) {
/* 783 */             max = index;
/*     */           }
/*     */         }
/*     */       }
/* 787 */       this.max_locals = max;
/*     */     } else {
/* 789 */       this.max_locals = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stripAttributes(boolean flag)
/*     */   {
/* 795 */     this.strip_attributes = flag;
/*     */   }
/*     */ 
/*     */   public static int getMaxStack(ConstantPoolGen cp, InstructionList il, CodeExceptionGen[] et)
/*     */   {
/* 845 */     BranchStack branchTargets = new BranchStack();
/*     */ 
/* 852 */     for (int i = 0; i < et.length; i++) {
/* 853 */       InstructionHandle handler_pc = et[i].getHandlerPC();
/* 854 */       if (handler_pc != null) {
/* 855 */         branchTargets.push(handler_pc, 1);
/*     */       }
/*     */     }
/* 858 */     int stackDepth = 0; int maxStackDepth = 0;
/* 859 */     InstructionHandle ih = il.getStart();
/*     */ 
/* 861 */     while (ih != null) {
/* 862 */       Instruction instruction = ih.getInstruction();
/* 863 */       short opcode = instruction.getOpcode();
/* 864 */       int delta = instruction.produceStack(cp) - instruction.consumeStack(cp);
/*     */ 
/* 866 */       stackDepth += delta;
/* 867 */       if (stackDepth > maxStackDepth) {
/* 868 */         maxStackDepth = stackDepth;
/*     */       }
/*     */ 
/* 871 */       if ((instruction instanceof BranchInstruction)) {
/* 872 */         BranchInstruction branch = (BranchInstruction)instruction;
/* 873 */         if ((instruction instanceof Select))
/*     */         {
/* 875 */           Select select = (Select)branch;
/* 876 */           InstructionHandle[] targets = select.getTargets();
/* 877 */           for (int i = 0; i < targets.length; i++) {
/* 878 */             branchTargets.push(targets[i], stackDepth);
/*     */           }
/* 880 */           ih = null;
/* 881 */         } else if (!(branch instanceof IfInstruction))
/*     */         {
/* 884 */           if ((opcode == 168) || (opcode == 201))
/* 885 */             branchTargets.push(ih.getNext(), stackDepth - 1);
/* 886 */           ih = null;
/*     */         }
/*     */ 
/* 891 */         branchTargets.push(branch.getTarget(), stackDepth);
/*     */       }
/* 894 */       else if ((opcode == 191) || (opcode == 169) || ((opcode >= 172) && (opcode <= 177)))
/*     */       {
/* 896 */         ih = null;
/*     */       }
/*     */ 
/* 899 */       if (ih != null) {
/* 900 */         ih = ih.getNext();
/*     */       }
/* 902 */       if (ih == null) {
/* 903 */         BranchTarget bt = branchTargets.pop();
/* 904 */         if (bt != null) {
/* 905 */           ih = bt.target;
/* 906 */           stackDepth = bt.stackDepth;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 911 */     return maxStackDepth;
/*     */   }
/*     */ 
/*     */   public void addObserver(MethodObserver o)
/*     */   {
/* 919 */     if (this.observers == null) {
/* 920 */       this.observers = new ArrayList();
/*     */     }
/* 922 */     this.observers.add(o);
/*     */   }
/*     */ 
/*     */   public void removeObserver(MethodObserver o)
/*     */   {
/* 928 */     if (this.observers != null)
/* 929 */       this.observers.remove(o);
/*     */   }
/*     */ 
/*     */   public void update()
/*     */   {
/*     */     Iterator e;
/* 937 */     if (this.observers != null)
/* 938 */       for (e = this.observers.iterator(); e.hasNext(); )
/* 939 */         ((MethodObserver)e.next()).notify(this);
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 949 */     String access = Utility.accessToString(this.access_flags);
/* 950 */     String signature = Type.getMethodSignature(this.type, this.arg_types);
/*     */ 
/* 952 */     signature = Utility.methodSignatureToString(signature, this.name, access, true, getLocalVariableTable(this.cp));
/*     */ 
/* 955 */     StringBuffer buf = new StringBuffer(signature);
/*     */     Iterator e;
/* 957 */     if (this.throws_vec.size() > 0) {
/* 958 */       for (e = this.throws_vec.iterator(); e.hasNext(); ) {
/* 959 */         buf.append("\n\t\tthrows " + e.next());
/*     */       }
/*     */     }
/* 962 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public MethodGen copy(String class_name, ConstantPoolGen cp)
/*     */   {
/* 968 */     Method m = ((MethodGen)clone()).getMethod();
/* 969 */     MethodGen mg = new MethodGen(m, class_name, this.cp);
/*     */ 
/* 971 */     if (this.cp != cp) {
/* 972 */       mg.setConstantPool(cp);
/* 973 */       mg.getInstructionList().replaceConstantPool(this.cp, cp);
/*     */     }
/*     */ 
/* 976 */     return mg;
/*     */   }
/*     */ 
/*     */   static final class BranchStack
/*     */   {
/* 808 */     Stack branchTargets = new Stack();
/* 809 */     Hashtable visitedTargets = new Hashtable();
/*     */ 
/*     */     public void push(InstructionHandle target, int stackDepth) {
/* 812 */       if (visited(target)) {
/* 813 */         return;
/*     */       }
/* 815 */       this.branchTargets.push(visit(target, stackDepth));
/*     */     }
/*     */ 
/*     */     public MethodGen.BranchTarget pop() {
/* 819 */       if (!this.branchTargets.empty()) {
/* 820 */         MethodGen.BranchTarget bt = (MethodGen.BranchTarget)this.branchTargets.pop();
/* 821 */         return bt;
/*     */       }
/*     */ 
/* 824 */       return null;
/*     */     }
/*     */ 
/*     */     private final MethodGen.BranchTarget visit(InstructionHandle target, int stackDepth) {
/* 828 */       MethodGen.BranchTarget bt = new MethodGen.BranchTarget(target, stackDepth);
/* 829 */       this.visitedTargets.put(target, bt);
/*     */ 
/* 831 */       return bt;
/*     */     }
/*     */ 
/*     */     private final boolean visited(InstructionHandle target) {
/* 835 */       return this.visitedTargets.get(target) != null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class BranchTarget
/*     */   {
/*     */     InstructionHandle target;
/*     */     int stackDepth;
/*     */ 
/*     */     BranchTarget(InstructionHandle target, int stackDepth)
/*     */     {
/* 802 */       this.target = target;
/* 803 */       this.stackDepth = stackDepth;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.MethodGen
 * JD-Core Version:    0.6.2
 */