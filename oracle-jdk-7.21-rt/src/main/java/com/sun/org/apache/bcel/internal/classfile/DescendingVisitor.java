/*     */ package com.sun.org.apache.bcel.internal.classfile;
/*     */ 
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class DescendingVisitor
/*     */   implements Visitor
/*     */ {
/*     */   private JavaClass clazz;
/*     */   private Visitor visitor;
/*  73 */   private Stack stack = new Stack();
/*     */ 
/*     */   public Object predecessor()
/*     */   {
/*  78 */     return predecessor(0);
/*     */   }
/*     */ 
/*     */   public Object predecessor(int level)
/*     */   {
/*  86 */     int size = this.stack.size();
/*     */ 
/*  88 */     if ((size < 2) || (level < 0)) {
/*  89 */       return null;
/*     */     }
/*  91 */     return this.stack.elementAt(size - (level + 2));
/*     */   }
/*     */ 
/*     */   public Object current()
/*     */   {
/*  97 */     return this.stack.peek();
/*     */   }
/*     */ 
/*     */   public DescendingVisitor(JavaClass clazz, Visitor visitor)
/*     */   {
/* 105 */     this.clazz = clazz;
/* 106 */     this.visitor = visitor;
/*     */   }
/*     */ 
/*     */   public void visit()
/*     */   {
/* 112 */     this.clazz.accept(this);
/*     */   }
/*     */   public void visitJavaClass(JavaClass clazz) {
/* 115 */     this.stack.push(clazz);
/* 116 */     clazz.accept(this.visitor);
/*     */ 
/* 118 */     Field[] fields = clazz.getFields();
/* 119 */     for (int i = 0; i < fields.length; i++) {
/* 120 */       fields[i].accept(this);
/*     */     }
/* 122 */     Method[] methods = clazz.getMethods();
/* 123 */     for (int i = 0; i < methods.length; i++) {
/* 124 */       methods[i].accept(this);
/*     */     }
/* 126 */     Attribute[] attributes = clazz.getAttributes();
/* 127 */     for (int i = 0; i < attributes.length; i++) {
/* 128 */       attributes[i].accept(this);
/*     */     }
/* 130 */     clazz.getConstantPool().accept(this);
/* 131 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitField(Field field) {
/* 135 */     this.stack.push(field);
/* 136 */     field.accept(this.visitor);
/*     */ 
/* 138 */     Attribute[] attributes = field.getAttributes();
/* 139 */     for (int i = 0; i < attributes.length; i++)
/* 140 */       attributes[i].accept(this);
/* 141 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantValue(ConstantValue cv) {
/* 145 */     this.stack.push(cv);
/* 146 */     cv.accept(this.visitor);
/* 147 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitMethod(Method method) {
/* 151 */     this.stack.push(method);
/* 152 */     method.accept(this.visitor);
/*     */ 
/* 154 */     Attribute[] attributes = method.getAttributes();
/* 155 */     for (int i = 0; i < attributes.length; i++) {
/* 156 */       attributes[i].accept(this);
/*     */     }
/* 158 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitExceptionTable(ExceptionTable table) {
/* 162 */     this.stack.push(table);
/* 163 */     table.accept(this.visitor);
/* 164 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitCode(Code code) {
/* 168 */     this.stack.push(code);
/* 169 */     code.accept(this.visitor);
/*     */ 
/* 171 */     CodeException[] table = code.getExceptionTable();
/* 172 */     for (int i = 0; i < table.length; i++) {
/* 173 */       table[i].accept(this);
/*     */     }
/* 175 */     Attribute[] attributes = code.getAttributes();
/* 176 */     for (int i = 0; i < attributes.length; i++)
/* 177 */       attributes[i].accept(this);
/* 178 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitCodeException(CodeException ce) {
/* 182 */     this.stack.push(ce);
/* 183 */     ce.accept(this.visitor);
/* 184 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitLineNumberTable(LineNumberTable table) {
/* 188 */     this.stack.push(table);
/* 189 */     table.accept(this.visitor);
/*     */ 
/* 191 */     LineNumber[] numbers = table.getLineNumberTable();
/* 192 */     for (int i = 0; i < numbers.length; i++)
/* 193 */       numbers[i].accept(this);
/* 194 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitLineNumber(LineNumber number) {
/* 198 */     this.stack.push(number);
/* 199 */     number.accept(this.visitor);
/* 200 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitLocalVariableTable(LocalVariableTable table) {
/* 204 */     this.stack.push(table);
/* 205 */     table.accept(this.visitor);
/*     */ 
/* 207 */     LocalVariable[] vars = table.getLocalVariableTable();
/* 208 */     for (int i = 0; i < vars.length; i++)
/* 209 */       vars[i].accept(this);
/* 210 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitStackMap(StackMap table) {
/* 214 */     this.stack.push(table);
/* 215 */     table.accept(this.visitor);
/*     */ 
/* 217 */     StackMapEntry[] vars = table.getStackMap();
/*     */ 
/* 219 */     for (int i = 0; i < vars.length; i++)
/* 220 */       vars[i].accept(this);
/* 221 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitStackMapEntry(StackMapEntry var) {
/* 225 */     this.stack.push(var);
/* 226 */     var.accept(this.visitor);
/* 227 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitLocalVariable(LocalVariable var) {
/* 231 */     this.stack.push(var);
/* 232 */     var.accept(this.visitor);
/* 233 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantPool(ConstantPool cp) {
/* 237 */     this.stack.push(cp);
/* 238 */     cp.accept(this.visitor);
/*     */ 
/* 240 */     Constant[] constants = cp.getConstantPool();
/* 241 */     for (int i = 1; i < constants.length; i++) {
/* 242 */       if (constants[i] != null) {
/* 243 */         constants[i].accept(this);
/*     */       }
/*     */     }
/* 246 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantClass(ConstantClass constant) {
/* 250 */     this.stack.push(constant);
/* 251 */     constant.accept(this.visitor);
/* 252 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantDouble(ConstantDouble constant) {
/* 256 */     this.stack.push(constant);
/* 257 */     constant.accept(this.visitor);
/* 258 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantFieldref(ConstantFieldref constant) {
/* 262 */     this.stack.push(constant);
/* 263 */     constant.accept(this.visitor);
/* 264 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantFloat(ConstantFloat constant) {
/* 268 */     this.stack.push(constant);
/* 269 */     constant.accept(this.visitor);
/* 270 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantInteger(ConstantInteger constant) {
/* 274 */     this.stack.push(constant);
/* 275 */     constant.accept(this.visitor);
/* 276 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref constant) {
/* 280 */     this.stack.push(constant);
/* 281 */     constant.accept(this.visitor);
/* 282 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantLong(ConstantLong constant) {
/* 286 */     this.stack.push(constant);
/* 287 */     constant.accept(this.visitor);
/* 288 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantMethodref(ConstantMethodref constant) {
/* 292 */     this.stack.push(constant);
/* 293 */     constant.accept(this.visitor);
/* 294 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantNameAndType(ConstantNameAndType constant) {
/* 298 */     this.stack.push(constant);
/* 299 */     constant.accept(this.visitor);
/* 300 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantString(ConstantString constant) {
/* 304 */     this.stack.push(constant);
/* 305 */     constant.accept(this.visitor);
/* 306 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitConstantUtf8(ConstantUtf8 constant) {
/* 310 */     this.stack.push(constant);
/* 311 */     constant.accept(this.visitor);
/* 312 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitInnerClasses(InnerClasses ic) {
/* 316 */     this.stack.push(ic);
/* 317 */     ic.accept(this.visitor);
/*     */ 
/* 319 */     InnerClass[] ics = ic.getInnerClasses();
/* 320 */     for (int i = 0; i < ics.length; i++)
/* 321 */       ics[i].accept(this);
/* 322 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitInnerClass(InnerClass inner) {
/* 326 */     this.stack.push(inner);
/* 327 */     inner.accept(this.visitor);
/* 328 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitDeprecated(Deprecated attribute) {
/* 332 */     this.stack.push(attribute);
/* 333 */     attribute.accept(this.visitor);
/* 334 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitSignature(Signature attribute) {
/* 338 */     this.stack.push(attribute);
/* 339 */     attribute.accept(this.visitor);
/* 340 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitSourceFile(SourceFile attribute) {
/* 344 */     this.stack.push(attribute);
/* 345 */     attribute.accept(this.visitor);
/* 346 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitSynthetic(Synthetic attribute) {
/* 350 */     this.stack.push(attribute);
/* 351 */     attribute.accept(this.visitor);
/* 352 */     this.stack.pop();
/*     */   }
/*     */ 
/*     */   public void visitUnknown(Unknown attribute) {
/* 356 */     this.stack.push(attribute);
/* 357 */     attribute.accept(this.visitor);
/* 358 */     this.stack.pop();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.classfile.DescendingVisitor
 * JD-Core Version:    0.6.2
 */