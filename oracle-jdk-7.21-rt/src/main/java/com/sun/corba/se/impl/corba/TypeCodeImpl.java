/*      */ package com.sun.corba.se.impl.corba;
/*      */ 
/*      */ import com.sun.corba.se.impl.encoding.CDRInputStream;
/*      */ import com.sun.corba.se.impl.encoding.CDROutputStream;
/*      */ import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
/*      */ import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
/*      */ import com.sun.corba.se.impl.encoding.TypeCodeReader;
/*      */ import com.sun.corba.se.impl.encoding.WrapperInputStream;
/*      */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import org.omg.CORBA.Any;
/*      */ import org.omg.CORBA.StructMember;
/*      */ import org.omg.CORBA.TCKind;
/*      */ import org.omg.CORBA.TypeCode;
/*      */ import org.omg.CORBA.TypeCodePackage.BadKind;
/*      */ import org.omg.CORBA.TypeCodePackage.Bounds;
/*      */ import org.omg.CORBA.UnionMember;
/*      */ import org.omg.CORBA.ValueMember;
/*      */ 
/*      */ public final class TypeCodeImpl extends TypeCode
/*      */ {
/*      */   protected static final int tk_indirect = -1;
/*      */   private static final int EMPTY = 0;
/*      */   private static final int SIMPLE = 1;
/*      */   private static final int COMPLEX = 2;
/*   91 */   private static final int[] typeTable = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2 };
/*      */ 
/*  129 */   static final String[] kindNames = { "null", "void", "short", "long", "ushort", "ulong", "float", "double", "boolean", "char", "octet", "any", "typecode", "principal", "objref", "struct", "union", "enum", "string", "sequence", "array", "alias", "exception", "longlong", "ulonglong", "longdouble", "wchar", "wstring", "fixed", "value", "valueBox", "native", "abstractInterface" };
/*      */ 
/*  165 */   private int _kind = 0;
/*      */ 
/*  168 */   private String _id = "";
/*  169 */   private String _name = "";
/*  170 */   private int _memberCount = 0;
/*  171 */   private String[] _memberNames = null;
/*  172 */   private TypeCodeImpl[] _memberTypes = null;
/*  173 */   private AnyImpl[] _unionLabels = null;
/*  174 */   private TypeCodeImpl _discriminator = null;
/*  175 */   private int _defaultIndex = -1;
/*  176 */   private int _length = 0;
/*  177 */   private TypeCodeImpl _contentType = null;
/*      */ 
/*  179 */   private short _digits = 0;
/*  180 */   private short _scale = 0;
/*      */ 
/*  185 */   private short _type_modifier = -1;
/*      */ 
/*  187 */   private TypeCodeImpl _concrete_base = null;
/*  188 */   private short[] _memberAccess = null;
/*      */ 
/*  190 */   private TypeCodeImpl _parent = null;
/*  191 */   private int _parentOffset = 0;
/*      */ 
/*  193 */   private TypeCodeImpl _indirectType = null;
/*      */ 
/*  196 */   private byte[] outBuffer = null;
/*      */ 
/*  198 */   private boolean cachingEnabled = false;
/*      */   private com.sun.corba.se.spi.orb.ORB _orb;
/*      */   private ORBUtilSystemException wrapper;
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB)
/*      */   {
/*  210 */     this._orb = paramORB;
/*  211 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, TypeCode paramTypeCode)
/*      */   {
/*  219 */     this(paramORB);
/*      */     Object localObject;
/*  225 */     if ((paramTypeCode instanceof TypeCodeImpl)) {
/*  226 */       localObject = (TypeCodeImpl)paramTypeCode;
/*  227 */       if (((TypeCodeImpl)localObject)._kind == -1)
/*  228 */         throw this.wrapper.badRemoteTypecode();
/*  229 */       if ((((TypeCodeImpl)localObject)._kind == 19) && (((TypeCodeImpl)localObject)._contentType == null)) {
/*  230 */         throw this.wrapper.badRemoteTypecode();
/*      */       }
/*      */     }
/*      */ 
/*  234 */     this._kind = paramTypeCode.kind().value();
/*      */     try
/*      */     {
/*      */       int j;
/*  238 */       switch (this._kind) {
/*      */       case 29:
/*  240 */         this._type_modifier = paramTypeCode.type_modifier();
/*      */ 
/*  242 */         localObject = paramTypeCode.concrete_base_type();
/*  243 */         if (localObject != null)
/*  244 */           this._concrete_base = convertToNative(this._orb, (TypeCode)localObject);
/*      */         else {
/*  246 */           this._concrete_base = null;
/*      */         }
/*      */ 
/*  250 */         this._memberAccess = new short[paramTypeCode.member_count()];
/*  251 */         for (j = 0; j < paramTypeCode.member_count(); j++) {
/*  252 */           this._memberAccess[j] = paramTypeCode.member_visibility(j);
/*      */         }
/*      */ 
/*      */       case 15:
/*      */       case 16:
/*      */       case 22:
/*  258 */         this._memberTypes = new TypeCodeImpl[paramTypeCode.member_count()];
/*  259 */         for (j = 0; j < paramTypeCode.member_count(); j++) {
/*  260 */           this._memberTypes[j] = convertToNative(this._orb, paramTypeCode.member_type(j));
/*  261 */           this._memberTypes[j].setParent(this);
/*      */         }
/*      */ 
/*      */       case 17:
/*  265 */         this._memberNames = new String[paramTypeCode.member_count()];
/*  266 */         for (j = 0; j < paramTypeCode.member_count(); j++) {
/*  267 */           this._memberNames[j] = paramTypeCode.member_name(j);
/*      */         }
/*      */ 
/*  270 */         this._memberCount = paramTypeCode.member_count();
/*      */       case 14:
/*      */       case 21:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*  276 */         setId(paramTypeCode.id());
/*  277 */         this._name = paramTypeCode.name();
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*  282 */       case 28: } switch (this._kind) {
/*      */       case 16:
/*  284 */         this._discriminator = convertToNative(this._orb, paramTypeCode.discriminator_type());
/*  285 */         this._defaultIndex = paramTypeCode.default_index();
/*  286 */         this._unionLabels = new AnyImpl[this._memberCount];
/*  287 */         for (int i = 0; i < this._memberCount; i++) {
/*  288 */           this._unionLabels[i] = new AnyImpl(this._orb, paramTypeCode.member_label(i));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  293 */       switch (this._kind) {
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 27:
/*  298 */         this._length = paramTypeCode.length();
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*  302 */       case 26: } switch (this._kind) {
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 30:
/*  307 */         this._contentType = convertToNative(this._orb, paramTypeCode.content_type());
/*      */       }
/*      */     }
/*      */     catch (Bounds localBounds) {
/*      */     }
/*      */     catch (BadKind localBadKind) {
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt) {
/*  317 */     this(paramORB);
/*      */ 
/*  322 */     this._kind = paramInt;
/*      */ 
/*  325 */     switch (this._kind)
/*      */     {
/*      */     case 14:
/*  329 */       setId("IDL:omg.org/CORBA/Object:1.0");
/*  330 */       this._name = "Object";
/*  331 */       break;
/*      */     case 18:
/*      */     case 27:
/*  337 */       this._length = 0;
/*  338 */       break;
/*      */     case 29:
/*  343 */       this._concrete_base = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
/*      */   {
/*  356 */     this(paramORB);
/*      */ 
/*  358 */     if ((paramInt == 15) || (paramInt == 22)) {
/*  359 */       this._kind = paramInt;
/*  360 */       setId(paramString1);
/*  361 */       this._name = paramString2;
/*  362 */       this._memberCount = paramArrayOfStructMember.length;
/*      */ 
/*  364 */       this._memberNames = new String[this._memberCount];
/*  365 */       this._memberTypes = new TypeCodeImpl[this._memberCount];
/*      */ 
/*  367 */       for (int i = 0; i < this._memberCount; i++) {
/*  368 */         this._memberNames[i] = paramArrayOfStructMember[i].name;
/*  369 */         this._memberTypes[i] = convertToNative(this._orb, paramArrayOfStructMember[i].type);
/*  370 */         this._memberTypes[i].setParent(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember)
/*      */   {
/*  383 */     this(paramORB);
/*      */ 
/*  385 */     if (paramInt == 16) {
/*  386 */       this._kind = paramInt;
/*  387 */       setId(paramString1);
/*  388 */       this._name = paramString2;
/*  389 */       this._memberCount = paramArrayOfUnionMember.length;
/*  390 */       this._discriminator = convertToNative(this._orb, paramTypeCode);
/*      */ 
/*  392 */       this._memberNames = new String[this._memberCount];
/*  393 */       this._memberTypes = new TypeCodeImpl[this._memberCount];
/*  394 */       this._unionLabels = new AnyImpl[this._memberCount];
/*      */ 
/*  396 */       for (int i = 0; i < this._memberCount; i++) {
/*  397 */         this._memberNames[i] = paramArrayOfUnionMember[i].name;
/*  398 */         this._memberTypes[i] = convertToNative(this._orb, paramArrayOfUnionMember[i].type);
/*  399 */         this._memberTypes[i].setParent(this);
/*  400 */         this._unionLabels[i] = new AnyImpl(this._orb, paramArrayOfUnionMember[i].label);
/*      */ 
/*  402 */         if ((this._unionLabels[i].type().kind() == TCKind.tk_octet) && 
/*  403 */           (this._unionLabels[i].extract_octet() == 0))
/*  404 */           this._defaultIndex = i;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
/*      */   {
/*  420 */     this(paramORB);
/*      */ 
/*  422 */     if (paramInt == 29) {
/*  423 */       this._kind = paramInt;
/*  424 */       setId(paramString1);
/*  425 */       this._name = paramString2;
/*  426 */       this._type_modifier = paramShort;
/*  427 */       if (paramTypeCode != null) {
/*  428 */         this._concrete_base = convertToNative(this._orb, paramTypeCode);
/*      */       }
/*  430 */       this._memberCount = paramArrayOfValueMember.length;
/*      */ 
/*  432 */       this._memberNames = new String[this._memberCount];
/*  433 */       this._memberTypes = new TypeCodeImpl[this._memberCount];
/*  434 */       this._memberAccess = new short[this._memberCount];
/*      */ 
/*  436 */       for (int i = 0; i < this._memberCount; i++) {
/*  437 */         this._memberNames[i] = paramArrayOfValueMember[i].name;
/*  438 */         this._memberTypes[i] = convertToNative(this._orb, paramArrayOfValueMember[i].type);
/*  439 */         this._memberTypes[i].setParent(this);
/*  440 */         this._memberAccess[i] = paramArrayOfValueMember[i].access;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, String[] paramArrayOfString)
/*      */   {
/*  453 */     this(paramORB);
/*      */ 
/*  455 */     if (paramInt == 17)
/*      */     {
/*  457 */       this._kind = paramInt;
/*  458 */       setId(paramString1);
/*  459 */       this._name = paramString2;
/*  460 */       this._memberCount = paramArrayOfString.length;
/*      */ 
/*  462 */       this._memberNames = new String[this._memberCount];
/*      */ 
/*  464 */       for (int i = 0; i < this._memberCount; i++)
/*  465 */         this._memberNames[i] = paramArrayOfString[i];
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode)
/*      */   {
/*  476 */     this(paramORB);
/*      */ 
/*  478 */     if ((paramInt == 21) || (paramInt == 30))
/*      */     {
/*  480 */       this._kind = paramInt;
/*  481 */       setId(paramString1);
/*  482 */       this._name = paramString2;
/*  483 */       this._contentType = convertToNative(this._orb, paramTypeCode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, String paramString1, String paramString2)
/*      */   {
/*  494 */     this(paramORB);
/*      */ 
/*  496 */     if ((paramInt == 14) || (paramInt == 31) || (paramInt == 32))
/*      */     {
/*  500 */       this._kind = paramInt;
/*  501 */       setId(paramString1);
/*  502 */       this._name = paramString2;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2)
/*      */   {
/*  512 */     this(paramORB);
/*      */ 
/*  514 */     if (paramInt2 < 0) {
/*  515 */       throw this.wrapper.negativeBounds();
/*      */     }
/*  517 */     if ((paramInt1 == 18) || (paramInt1 == 27)) {
/*  518 */       this._kind = paramInt1;
/*  519 */       this._length = paramInt2;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2, TypeCode paramTypeCode)
/*      */   {
/*  529 */     this(paramORB);
/*      */ 
/*  531 */     if ((paramInt1 == 19) || (paramInt1 == 20)) {
/*  532 */       this._kind = paramInt1;
/*  533 */       this._length = paramInt2;
/*  534 */       this._contentType = convertToNative(this._orb, paramTypeCode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  544 */     this(paramORB);
/*      */ 
/*  546 */     if (paramInt1 == 19) {
/*  547 */       this._kind = paramInt1;
/*  548 */       this._length = paramInt2;
/*  549 */       this._parentOffset = paramInt3;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, String paramString)
/*      */   {
/*  557 */     this(paramORB);
/*      */ 
/*  559 */     this._kind = -1;
/*      */ 
/*  561 */     this._id = paramString;
/*      */ 
/*  564 */     tryIndirectType();
/*      */   }
/*      */ 
/*      */   public TypeCodeImpl(com.sun.corba.se.spi.orb.ORB paramORB, int paramInt, short paramShort1, short paramShort2)
/*      */   {
/*  573 */     this(paramORB);
/*      */ 
/*  578 */     if (paramInt == 28) {
/*  579 */       this._kind = paramInt;
/*  580 */       this._digits = paramShort1;
/*  581 */       this._scale = paramShort2;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static TypeCodeImpl convertToNative(com.sun.corba.se.spi.orb.ORB paramORB, TypeCode paramTypeCode)
/*      */   {
/*  596 */     if ((paramTypeCode instanceof TypeCodeImpl)) {
/*  597 */       return (TypeCodeImpl)paramTypeCode;
/*      */     }
/*  599 */     return new TypeCodeImpl(paramORB, paramTypeCode);
/*      */   }
/*      */ 
/*      */   public static CDROutputStream newOutputStream(com.sun.corba.se.spi.orb.ORB paramORB) {
/*  603 */     TypeCodeOutputStream localTypeCodeOutputStream = new TypeCodeOutputStream(paramORB);
/*      */ 
/*  606 */     return localTypeCodeOutputStream;
/*      */   }
/*      */ 
/*      */   private TypeCodeImpl indirectType()
/*      */   {
/*  612 */     this._indirectType = tryIndirectType();
/*  613 */     if (this._indirectType == null)
/*      */     {
/*  615 */       throw this.wrapper.unresolvedRecursiveTypecode();
/*      */     }
/*  617 */     return this._indirectType;
/*      */   }
/*      */ 
/*      */   private TypeCodeImpl tryIndirectType()
/*      */   {
/*  622 */     if (this._indirectType != null) {
/*  623 */       return this._indirectType;
/*      */     }
/*  625 */     setIndirectType(this._orb.getTypeCode(this._id));
/*      */ 
/*  627 */     return this._indirectType;
/*      */   }
/*      */ 
/*      */   private void setIndirectType(TypeCodeImpl paramTypeCodeImpl) {
/*  631 */     this._indirectType = paramTypeCodeImpl;
/*  632 */     if (this._indirectType != null)
/*      */       try {
/*  634 */         this._id = this._indirectType.id();
/*      */       }
/*      */       catch (BadKind localBadKind) {
/*  637 */         throw this.wrapper.badkindCannotOccur();
/*      */       }
/*      */   }
/*      */ 
/*      */   private void setId(String paramString)
/*      */   {
/*  643 */     this._id = paramString;
/*  644 */     if ((this._orb instanceof TypeCodeFactory))
/*  645 */       this._orb.setTypeCode(this._id, this);
/*      */   }
/*      */ 
/*      */   private void setParent(TypeCodeImpl paramTypeCodeImpl)
/*      */   {
/*  652 */     this._parent = paramTypeCodeImpl;
/*      */   }
/*      */ 
/*      */   private TypeCodeImpl getParentAtLevel(int paramInt) {
/*  656 */     if (paramInt == 0) {
/*  657 */       return this;
/*      */     }
/*  659 */     if (this._parent == null) {
/*  660 */       throw this.wrapper.unresolvedRecursiveTypecode();
/*      */     }
/*  662 */     return this._parent.getParentAtLevel(paramInt - 1);
/*      */   }
/*      */ 
/*      */   private TypeCodeImpl lazy_content_type() {
/*  666 */     if ((this._contentType == null) && 
/*  667 */       (this._kind == 19) && (this._parentOffset > 0) && (this._parent != null))
/*      */     {
/*  670 */       TypeCodeImpl localTypeCodeImpl = getParentAtLevel(this._parentOffset);
/*  671 */       if ((localTypeCodeImpl != null) && (localTypeCodeImpl._id != null))
/*      */       {
/*  675 */         this._contentType = new TypeCodeImpl(this._orb, localTypeCodeImpl._id);
/*      */       }
/*      */     }
/*      */ 
/*  679 */     return this._contentType;
/*      */   }
/*      */ 
/*      */   private TypeCode realType(TypeCode paramTypeCode)
/*      */   {
/*  685 */     TypeCode localTypeCode = paramTypeCode;
/*      */     try
/*      */     {
/*  688 */       while (localTypeCode.kind().value() == 21)
/*  689 */         localTypeCode = localTypeCode.content_type();
/*      */     }
/*      */     catch (BadKind localBadKind)
/*      */     {
/*  693 */       throw this.wrapper.badkindCannotOccur();
/*      */     }
/*  695 */     return localTypeCode;
/*      */   }
/*      */ 
/*      */   public final boolean equal(TypeCode paramTypeCode)
/*      */   {
/*  705 */     if (paramTypeCode == this) {
/*  706 */       return true;
/*      */     }
/*      */     try
/*      */     {
/*  710 */       if (this._kind == -1)
/*      */       {
/*  712 */         if ((this._id != null) && (paramTypeCode.id() != null))
/*  713 */           return this._id.equals(paramTypeCode.id());
/*  714 */         return (this._id == null) && (paramTypeCode.id() == null);
/*      */       }
/*      */ 
/*  718 */       if (this._kind != paramTypeCode.kind().value()) {
/*  719 */         return false;
/*      */       }
/*      */ 
/*  722 */       switch (typeTable[this._kind])
/*      */       {
/*      */       case 0:
/*  725 */         return true;
/*      */       case 1:
/*  728 */         switch (this._kind)
/*      */         {
/*      */         case 18:
/*      */         case 27:
/*  732 */           return this._length == paramTypeCode.length();
/*      */         case 28:
/*  735 */           return (this._digits == paramTypeCode.fixed_digits()) && (this._scale == paramTypeCode.fixed_scale());
/*      */         }
/*  737 */         return false;
/*      */       case 2:
/*      */         int i;
/*  742 */         switch (this._kind)
/*      */         {
/*      */         case 14:
/*  747 */           if (this._id.compareTo(paramTypeCode.id()) == 0) {
/*  748 */             return true;
/*      */           }
/*      */ 
/*  751 */           if (this._id.compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0)
/*      */           {
/*  754 */             return true;
/*      */           }
/*      */ 
/*  757 */           if (paramTypeCode.id().compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0)
/*      */           {
/*  760 */             return true;
/*      */           }
/*      */ 
/*  763 */           return false;
/*      */         case 31:
/*      */         case 32:
/*  770 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  771 */             return false;
/*      */           }
/*      */ 
/*  775 */           return true;
/*      */         case 15:
/*      */         case 22:
/*  782 */           if (this._memberCount != paramTypeCode.member_count()) {
/*  783 */             return false;
/*      */           }
/*  785 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  786 */             return false;
/*      */           }
/*  788 */           for (i = 0; i < this._memberCount; i++) {
/*  789 */             if (!this._memberTypes[i].equal(paramTypeCode.member_type(i)))
/*  790 */               return false;
/*      */           }
/*  792 */           return true;
/*      */         case 16:
/*  798 */           if (this._memberCount != paramTypeCode.member_count()) {
/*  799 */             return false;
/*      */           }
/*  801 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  802 */             return false;
/*      */           }
/*  804 */           if (this._defaultIndex != paramTypeCode.default_index()) {
/*  805 */             return false;
/*      */           }
/*  807 */           if (!this._discriminator.equal(paramTypeCode.discriminator_type())) {
/*  808 */             return false;
/*      */           }
/*  810 */           for (i = 0; i < this._memberCount; i++) {
/*  811 */             if (!this._unionLabels[i].equal(paramTypeCode.member_label(i)))
/*  812 */               return false;
/*      */           }
/*  814 */           for (i = 0; i < this._memberCount; i++) {
/*  815 */             if (!this._memberTypes[i].equal(paramTypeCode.member_type(i)))
/*  816 */               return false;
/*      */           }
/*  818 */           return true;
/*      */         case 17:
/*  824 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  825 */             return false;
/*      */           }
/*  827 */           if (this._memberCount != paramTypeCode.member_count()) {
/*  828 */             return false;
/*      */           }
/*  830 */           return true;
/*      */         case 19:
/*      */         case 20:
/*  837 */           if (this._length != paramTypeCode.length()) {
/*  838 */             return false;
/*      */           }
/*      */ 
/*  841 */           if (!lazy_content_type().equal(paramTypeCode.content_type())) {
/*  842 */             return false;
/*      */           }
/*      */ 
/*  845 */           return true;
/*      */         case 29:
/*  851 */           if (this._memberCount != paramTypeCode.member_count()) {
/*  852 */             return false;
/*      */           }
/*  854 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  855 */             return false;
/*      */           }
/*  857 */           for (i = 0; i < this._memberCount; i++)
/*  858 */             if ((this._memberAccess[i] != paramTypeCode.member_visibility(i)) || (!this._memberTypes[i].equal(paramTypeCode.member_type(i))))
/*      */             {
/*  860 */               return false;
/*      */             }
/*  861 */           if (this._type_modifier == paramTypeCode.type_modifier()) {
/*  862 */             return false;
/*      */           }
/*  864 */           TypeCode localTypeCode = paramTypeCode.concrete_base_type();
/*  865 */           if (((this._concrete_base == null) && (localTypeCode != null)) || ((this._concrete_base != null) && (localTypeCode == null)) || (!this._concrete_base.equal(localTypeCode)))
/*      */           {
/*  869 */             return false;
/*      */           }
/*      */ 
/*  872 */           return true;
/*      */         case 21:
/*      */         case 30:
/*  879 */           if (this._id.compareTo(paramTypeCode.id()) != 0) {
/*  880 */             return false;
/*      */           }
/*      */ 
/*  883 */           return this._contentType.equal(paramTypeCode.content_type());
/*      */         case 18:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*  890 */         case 28: } break; }  } catch (Bounds localBounds) {  } catch (BadKind localBadKind) {  } return false;
/*      */   }
/*      */ 
/*      */   public boolean equivalent(TypeCode paramTypeCode)
/*      */   {
/*  898 */     if (paramTypeCode == this) {
/*  899 */       return true;
/*      */     }
/*      */ 
/*  906 */     Object localObject = this._kind == -1 ? indirectType() : this;
/*  907 */     localObject = realType((TypeCode)localObject);
/*  908 */     TypeCode localTypeCode = realType(paramTypeCode);
/*      */ 
/*  912 */     if (((TypeCode)localObject).kind().value() != localTypeCode.kind().value()) {
/*  913 */       return false;
/*      */     }
/*      */ 
/*  916 */     String str1 = null;
/*  917 */     String str2 = null;
/*      */     try {
/*  919 */       str1 = id();
/*  920 */       str2 = paramTypeCode.id();
/*      */ 
/*  926 */       if ((str1 != null) && (str2 != null)) {
/*  927 */         return str1.equals(str2);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (BadKind localBadKind1)
/*      */     {
/*      */     }
/*      */ 
/*  936 */     int i = ((TypeCode)localObject).kind().value();
/*      */     try {
/*  938 */       if ((i == 15) || (i == 16) || (i == 17) || (i == 22) || (i == 29))
/*      */       {
/*  944 */         if (((TypeCode)localObject).member_count() != localTypeCode.member_count())
/*  945 */           return false;
/*      */       }
/*  947 */       if (i == 16)
/*      */       {
/*  949 */         if (((TypeCode)localObject).default_index() != localTypeCode.default_index())
/*  950 */           return false;
/*      */       }
/*  952 */       if ((i == 18) || (i == 27) || (i == 19) || (i == 20))
/*      */       {
/*  957 */         if (((TypeCode)localObject).length() != localTypeCode.length())
/*  958 */           return false;
/*      */       }
/*  960 */       if (i == 28)
/*      */       {
/*  962 */         if ((((TypeCode)localObject).fixed_digits() != localTypeCode.fixed_digits()) || (((TypeCode)localObject).fixed_scale() != localTypeCode.fixed_scale()))
/*      */         {
/*  964 */           return false;
/*      */         }
/*      */       }
/*      */       int j;
/*  966 */       if (i == 16)
/*      */       {
/*  968 */         for (j = 0; j < ((TypeCode)localObject).member_count(); j++) {
/*  969 */           if (((TypeCode)localObject).member_label(j) != localTypeCode.member_label(j))
/*  970 */             return false;
/*      */         }
/*  972 */         if (!((TypeCode)localObject).discriminator_type().equivalent(localTypeCode.discriminator_type()))
/*      */         {
/*  974 */           return false;
/*      */         }
/*      */       }
/*  976 */       if ((i == 21) || (i == 30) || (i == 19) || (i == 20))
/*      */       {
/*  981 */         if (!((TypeCode)localObject).content_type().equivalent(localTypeCode.content_type()))
/*  982 */           return false;
/*      */       }
/*  984 */       if ((i == 15) || (i == 16) || (i == 22) || (i == 29))
/*      */       {
/*  989 */         for (j = 0; j < ((TypeCode)localObject).member_count(); j++)
/*  990 */           if (!((TypeCode)localObject).member_type(j).equivalent(localTypeCode.member_type(j)))
/*      */           {
/*  992 */             return false;
/*      */           }
/*      */       }
/*      */     }
/*      */     catch (BadKind localBadKind2) {
/*  997 */       throw this.wrapper.badkindCannotOccur();
/*      */     }
/*      */     catch (Bounds localBounds) {
/* 1000 */       throw this.wrapper.boundsCannotOccur();
/*      */     }
/*      */ 
/* 1004 */     return true;
/*      */   }
/*      */ 
/*      */   public TypeCode get_compact_typecode()
/*      */   {
/* 1011 */     return this;
/*      */   }
/*      */ 
/*      */   public TCKind kind()
/*      */   {
/* 1016 */     if (this._kind == -1)
/* 1017 */       return indirectType().kind();
/* 1018 */     return TCKind.from_int(this._kind);
/*      */   }
/*      */ 
/*      */   public boolean is_recursive()
/*      */   {
/* 1025 */     return this._kind == -1;
/*      */   }
/*      */ 
/*      */   public String id()
/*      */     throws BadKind
/*      */   {
/* 1031 */     switch (this._kind)
/*      */     {
/*      */     case -1:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 21:
/*      */     case 22:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/* 1046 */       return this._id;
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/* 1049 */     case 28: } throw new BadKind();
/*      */   }
/*      */ 
/*      */   public String name()
/*      */     throws BadKind
/*      */   {
/* 1056 */     switch (this._kind) {
/*      */     case -1:
/* 1058 */       return indirectType().name();
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 21:
/*      */     case 22:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/* 1069 */       return this._name;
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/* 1071 */     case 28: } throw new BadKind();
/*      */   }
/*      */ 
/*      */   public int member_count()
/*      */     throws BadKind
/*      */   {
/* 1078 */     switch (this._kind) {
/*      */     case -1:
/* 1080 */       return indirectType().member_count();
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 22:
/*      */     case 29:
/* 1086 */       return this._memberCount;
/*      */     }
/* 1088 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public String member_name(int paramInt)
/*      */     throws BadKind, Bounds
/*      */   {
/* 1095 */     switch (this._kind) {
/*      */     case -1:
/* 1097 */       return indirectType().member_name(paramInt);
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 22:
/*      */     case 29:
/*      */       try {
/* 1104 */         return this._memberNames[paramInt];
/*      */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 1106 */         throw new Bounds();
/*      */       }
/*      */     }
/* 1109 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public TypeCode member_type(int paramInt)
/*      */     throws BadKind, Bounds
/*      */   {
/* 1116 */     switch (this._kind) {
/*      */     case -1:
/* 1118 */       return indirectType().member_type(paramInt);
/*      */     case 15:
/*      */     case 16:
/*      */     case 22:
/*      */     case 29:
/*      */       try {
/* 1124 */         return this._memberTypes[paramInt];
/*      */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 1126 */         throw new Bounds();
/*      */       }
/*      */     }
/* 1129 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public Any member_label(int paramInt)
/*      */     throws BadKind, Bounds
/*      */   {
/* 1136 */     switch (this._kind) {
/*      */     case -1:
/* 1138 */       return indirectType().member_label(paramInt);
/*      */     case 16:
/*      */       try
/*      */       {
/* 1142 */         return new AnyImpl(this._orb, this._unionLabels[paramInt]);
/*      */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 1144 */         throw new Bounds();
/*      */       }
/*      */     }
/* 1147 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public TypeCode discriminator_type()
/*      */     throws BadKind
/*      */   {
/* 1154 */     switch (this._kind) {
/*      */     case -1:
/* 1156 */       return indirectType().discriminator_type();
/*      */     case 16:
/* 1158 */       return this._discriminator;
/*      */     }
/* 1160 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public int default_index()
/*      */     throws BadKind
/*      */   {
/* 1167 */     switch (this._kind) {
/*      */     case -1:
/* 1169 */       return indirectType().default_index();
/*      */     case 16:
/* 1171 */       return this._defaultIndex;
/*      */     }
/* 1173 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public int length()
/*      */     throws BadKind
/*      */   {
/* 1180 */     switch (this._kind) {
/*      */     case -1:
/* 1182 */       return indirectType().length();
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 27:
/* 1187 */       return this._length;
/*      */     }
/* 1189 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public TypeCode content_type()
/*      */     throws BadKind
/*      */   {
/* 1196 */     switch (this._kind) {
/*      */     case -1:
/* 1198 */       return indirectType().content_type();
/*      */     case 19:
/* 1200 */       return lazy_content_type();
/*      */     case 20:
/*      */     case 21:
/*      */     case 30:
/* 1204 */       return this._contentType;
/*      */     }
/* 1206 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public short fixed_digits() throws BadKind
/*      */   {
/* 1211 */     switch (this._kind) {
/*      */     case 28:
/* 1213 */       return this._digits;
/*      */     }
/* 1215 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public short fixed_scale() throws BadKind
/*      */   {
/* 1220 */     switch (this._kind) {
/*      */     case 28:
/* 1222 */       return this._scale;
/*      */     }
/* 1224 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public short member_visibility(int paramInt)
/*      */     throws BadKind, Bounds
/*      */   {
/* 1230 */     switch (this._kind) {
/*      */     case -1:
/* 1232 */       return indirectType().member_visibility(paramInt);
/*      */     case 29:
/*      */       try {
/* 1235 */         return this._memberAccess[paramInt];
/*      */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 1237 */         throw new Bounds();
/*      */       }
/*      */     }
/* 1240 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public short type_modifier() throws BadKind
/*      */   {
/* 1245 */     switch (this._kind) {
/*      */     case -1:
/* 1247 */       return indirectType().type_modifier();
/*      */     case 29:
/* 1249 */       return this._type_modifier;
/*      */     }
/* 1251 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public TypeCode concrete_base_type() throws BadKind
/*      */   {
/* 1256 */     switch (this._kind) {
/*      */     case -1:
/* 1258 */       return indirectType().concrete_base_type();
/*      */     case 29:
/* 1260 */       return this._concrete_base;
/*      */     }
/* 1262 */     throw new BadKind();
/*      */   }
/*      */ 
/*      */   public void read_value(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
/*      */   {
/* 1267 */     if ((paramInputStream instanceof TypeCodeReader))
/*      */     {
/* 1269 */       if (read_value_kind((TypeCodeReader)paramInputStream))
/* 1270 */         read_value_body(paramInputStream);
/* 1271 */     } else if ((paramInputStream instanceof CDRInputStream)) {
/* 1272 */       WrapperInputStream localWrapperInputStream = new WrapperInputStream((CDRInputStream)paramInputStream);
/*      */ 
/* 1275 */       if (read_value_kind(localWrapperInputStream))
/* 1276 */         read_value_body(localWrapperInputStream);
/*      */     } else {
/* 1278 */       read_value_kind(paramInputStream);
/* 1279 */       read_value_body(paramInputStream);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void read_value_recursive(TypeCodeInputStream paramTypeCodeInputStream)
/*      */   {
/* 1285 */     if ((paramTypeCodeInputStream instanceof TypeCodeReader)) {
/* 1286 */       if (read_value_kind(paramTypeCodeInputStream))
/* 1287 */         read_value_body(paramTypeCodeInputStream);
/*      */     } else {
/* 1289 */       read_value_kind(paramTypeCodeInputStream);
/* 1290 */       read_value_body(paramTypeCodeInputStream);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean read_value_kind(TypeCodeReader paramTypeCodeReader)
/*      */   {
/* 1296 */     this._kind = paramTypeCodeReader.read_long();
/*      */ 
/* 1299 */     int i = paramTypeCodeReader.getTopLevelPosition() - 4;
/*      */ 
/* 1302 */     if (((this._kind < 0) || (this._kind > typeTable.length)) && (this._kind != -1)) {
/* 1303 */       throw this.wrapper.cannotMarshalBadTckind();
/*      */     }
/*      */ 
/* 1307 */     if (this._kind == 31) {
/* 1308 */       throw this.wrapper.cannotMarshalNative();
/*      */     }
/*      */ 
/* 1312 */     TypeCodeReader localTypeCodeReader = paramTypeCodeReader.getTopLevelStream();
/*      */ 
/* 1314 */     if (this._kind == -1) {
/* 1315 */       int j = paramTypeCodeReader.read_long();
/* 1316 */       if (j > -4) {
/* 1317 */         throw this.wrapper.invalidIndirection(new Integer(j));
/*      */       }
/*      */ 
/* 1322 */       int k = paramTypeCodeReader.getTopLevelPosition();
/*      */ 
/* 1324 */       int m = k - 4 + j;
/*      */ 
/* 1331 */       TypeCodeImpl localTypeCodeImpl = localTypeCodeReader.getTypeCodeAtPosition(m);
/* 1332 */       if (localTypeCodeImpl == null)
/* 1333 */         throw this.wrapper.indirectionNotFound(new Integer(m));
/* 1334 */       setIndirectType(localTypeCodeImpl);
/* 1335 */       return false;
/*      */     }
/*      */ 
/* 1338 */     localTypeCodeReader.addTypeCodeAtPosition(this, i);
/* 1339 */     return true;
/*      */   }
/*      */ 
/*      */   void read_value_kind(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
/*      */   {
/* 1344 */     this._kind = paramInputStream.read_long();
/*      */ 
/* 1347 */     if (((this._kind < 0) || (this._kind > typeTable.length)) && (this._kind != -1)) {
/* 1348 */       throw this.wrapper.cannotMarshalBadTckind();
/*      */     }
/*      */ 
/* 1351 */     if (this._kind == 31) {
/* 1352 */       throw this.wrapper.cannotMarshalNative();
/*      */     }
/* 1354 */     if (this._kind == -1)
/* 1355 */       throw this.wrapper.recursiveTypecodeError();
/*      */   }
/*      */ 
/*      */   void read_value_body(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
/*      */   {
/* 1363 */     switch (typeTable[this._kind])
/*      */     {
/*      */     case 0:
/* 1366 */       break;
/*      */     case 1:
/* 1369 */       switch (this._kind) {
/*      */       case 18:
/*      */       case 27:
/* 1372 */         this._length = paramInputStream.read_long();
/* 1373 */         break;
/*      */       case 28:
/* 1375 */         this._digits = paramInputStream.read_ushort();
/* 1376 */         this._scale = paramInputStream.read_short();
/* 1377 */         break;
/*      */       default:
/* 1379 */         throw this.wrapper.invalidSimpleTypecode();
/*      */       }
/*      */ 
/*      */       break;
/*      */     case 2:
/* 1385 */       TypeCodeInputStream localTypeCodeInputStream = TypeCodeInputStream.readEncapsulation(paramInputStream, paramInputStream.orb());
/*      */       int i;
/* 1388 */       switch (this._kind)
/*      */       {
/*      */       case 14:
/*      */       case 32:
/* 1394 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1396 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1398 */         break;
/*      */       case 16:
/* 1403 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1406 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1409 */         this._discriminator = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1410 */         this._discriminator.read_value_recursive(localTypeCodeInputStream);
/*      */ 
/* 1413 */         this._defaultIndex = localTypeCodeInputStream.read_long();
/*      */ 
/* 1416 */         this._memberCount = localTypeCodeInputStream.read_long();
/*      */ 
/* 1419 */         this._unionLabels = new AnyImpl[this._memberCount];
/* 1420 */         this._memberNames = new String[this._memberCount];
/* 1421 */         this._memberTypes = new TypeCodeImpl[this._memberCount];
/*      */ 
/* 1424 */         for (i = 0; i < this._memberCount; i++) {
/* 1425 */           this._unionLabels[i] = new AnyImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1426 */           if (i == this._defaultIndex)
/*      */           {
/* 1428 */             this._unionLabels[i].insert_octet(localTypeCodeInputStream.read_octet());
/*      */           }
/* 1430 */           else switch (realType(this._discriminator).kind().value()) {
/*      */             case 2:
/* 1432 */               this._unionLabels[i].insert_short(localTypeCodeInputStream.read_short());
/* 1433 */               break;
/*      */             case 3:
/* 1435 */               this._unionLabels[i].insert_long(localTypeCodeInputStream.read_long());
/* 1436 */               break;
/*      */             case 4:
/* 1438 */               this._unionLabels[i].insert_ushort(localTypeCodeInputStream.read_short());
/* 1439 */               break;
/*      */             case 5:
/* 1441 */               this._unionLabels[i].insert_ulong(localTypeCodeInputStream.read_long());
/* 1442 */               break;
/*      */             case 6:
/* 1444 */               this._unionLabels[i].insert_float(localTypeCodeInputStream.read_float());
/* 1445 */               break;
/*      */             case 7:
/* 1447 */               this._unionLabels[i].insert_double(localTypeCodeInputStream.read_double());
/* 1448 */               break;
/*      */             case 8:
/* 1450 */               this._unionLabels[i].insert_boolean(localTypeCodeInputStream.read_boolean());
/* 1451 */               break;
/*      */             case 9:
/* 1453 */               this._unionLabels[i].insert_char(localTypeCodeInputStream.read_char());
/* 1454 */               break;
/*      */             case 17:
/* 1456 */               this._unionLabels[i].type(this._discriminator);
/* 1457 */               this._unionLabels[i].insert_long(localTypeCodeInputStream.read_long());
/* 1458 */               break;
/*      */             case 23:
/* 1460 */               this._unionLabels[i].insert_longlong(localTypeCodeInputStream.read_longlong());
/* 1461 */               break;
/*      */             case 24:
/* 1463 */               this._unionLabels[i].insert_ulonglong(localTypeCodeInputStream.read_longlong());
/* 1464 */               break;
/*      */             case 26:
/* 1470 */               this._unionLabels[i].insert_wchar(localTypeCodeInputStream.read_wchar());
/* 1471 */               break;
/*      */             case 10:
/*      */             case 11:
/*      */             case 12:
/*      */             case 13:
/*      */             case 14:
/*      */             case 15:
/*      */             case 16:
/*      */             case 18:
/*      */             case 19:
/*      */             case 20:
/*      */             case 21:
/*      */             case 22:
/*      */             case 25:
/*      */             default:
/* 1473 */               throw this.wrapper.invalidComplexTypecode();
/*      */             }
/*      */ 
/* 1476 */           this._memberNames[i] = localTypeCodeInputStream.read_string();
/* 1477 */           this._memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1478 */           this._memberTypes[i].read_value_recursive(localTypeCodeInputStream);
/* 1479 */           this._memberTypes[i].setParent(this);
/*      */         }
/*      */ 
/* 1482 */         break;
/*      */       case 17:
/* 1487 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1490 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1493 */         this._memberCount = localTypeCodeInputStream.read_long();
/*      */ 
/* 1496 */         this._memberNames = new String[this._memberCount];
/*      */ 
/* 1499 */         for (i = 0; i < this._memberCount; i++) {
/* 1500 */           this._memberNames[i] = localTypeCodeInputStream.read_string();
/*      */         }
/* 1502 */         break;
/*      */       case 19:
/* 1507 */         this._contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1508 */         this._contentType.read_value_recursive(localTypeCodeInputStream);
/*      */ 
/* 1511 */         this._length = localTypeCodeInputStream.read_long();
/*      */ 
/* 1513 */         break;
/*      */       case 20:
/* 1518 */         this._contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1519 */         this._contentType.read_value_recursive(localTypeCodeInputStream);
/*      */ 
/* 1522 */         this._length = localTypeCodeInputStream.read_long();
/*      */ 
/* 1524 */         break;
/*      */       case 21:
/*      */       case 30:
/* 1530 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1533 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1536 */         this._contentType = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1537 */         this._contentType.read_value_recursive(localTypeCodeInputStream);
/*      */ 
/* 1539 */         break;
/*      */       case 15:
/*      */       case 22:
/* 1545 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1548 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1551 */         this._memberCount = localTypeCodeInputStream.read_long();
/*      */ 
/* 1554 */         this._memberNames = new String[this._memberCount];
/* 1555 */         this._memberTypes = new TypeCodeImpl[this._memberCount];
/*      */ 
/* 1558 */         for (i = 0; i < this._memberCount; i++) {
/* 1559 */           this._memberNames[i] = localTypeCodeInputStream.read_string();
/* 1560 */           this._memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/*      */ 
/* 1563 */           this._memberTypes[i].read_value_recursive(localTypeCodeInputStream);
/* 1564 */           this._memberTypes[i].setParent(this);
/*      */         }
/*      */ 
/* 1567 */         break;
/*      */       case 29:
/* 1572 */         setId(localTypeCodeInputStream.read_string());
/*      */ 
/* 1575 */         this._name = localTypeCodeInputStream.read_string();
/*      */ 
/* 1578 */         this._type_modifier = localTypeCodeInputStream.read_short();
/*      */ 
/* 1581 */         this._concrete_base = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/* 1582 */         this._concrete_base.read_value_recursive(localTypeCodeInputStream);
/* 1583 */         if (this._concrete_base.kind().value() == 0) {
/* 1584 */           this._concrete_base = null;
/*      */         }
/*      */ 
/* 1588 */         this._memberCount = localTypeCodeInputStream.read_long();
/*      */ 
/* 1591 */         this._memberNames = new String[this._memberCount];
/* 1592 */         this._memberTypes = new TypeCodeImpl[this._memberCount];
/* 1593 */         this._memberAccess = new short[this._memberCount];
/*      */ 
/* 1596 */         for (i = 0; i < this._memberCount; i++) {
/* 1597 */           this._memberNames[i] = localTypeCodeInputStream.read_string();
/* 1598 */           this._memberTypes[i] = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/*      */ 
/* 1601 */           this._memberTypes[i].read_value_recursive(localTypeCodeInputStream);
/* 1602 */           this._memberTypes[i].setParent(this);
/* 1603 */           this._memberAccess[i] = localTypeCodeInputStream.read_short();
/*      */         }
/*      */ 
/* 1606 */         break;
/*      */       case 18:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 31:
/*      */       default:
/* 1609 */         throw this.wrapper.invalidTypecodeKindMarshal();
/*      */       }
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void write_value(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream)
/*      */   {
/* 1619 */     if ((paramOutputStream instanceof TypeCodeOutputStream)) {
/* 1620 */       write_value((TypeCodeOutputStream)paramOutputStream);
/*      */     } else {
/* 1622 */       TypeCodeOutputStream localTypeCodeOutputStream = null;
/*      */ 
/* 1624 */       if (this.outBuffer == null) {
/* 1625 */         localTypeCodeOutputStream = TypeCodeOutputStream.wrapOutputStream(paramOutputStream);
/* 1626 */         write_value(localTypeCodeOutputStream);
/* 1627 */         if (this.cachingEnabled)
/*      */         {
/* 1629 */           this.outBuffer = localTypeCodeOutputStream.getTypeCodeBuffer();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1640 */       if ((this.cachingEnabled) && (this.outBuffer != null)) {
/* 1641 */         paramOutputStream.write_long(this._kind);
/* 1642 */         paramOutputStream.write_octet_array(this.outBuffer, 0, this.outBuffer.length);
/*      */       }
/*      */       else {
/* 1645 */         localTypeCodeOutputStream.writeRawBuffer(paramOutputStream, this._kind);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void write_value(TypeCodeOutputStream paramTypeCodeOutputStream)
/*      */   {
/* 1653 */     if (this._kind == 31) {
/* 1654 */       throw this.wrapper.cannotMarshalNative();
/*      */     }
/* 1656 */     TypeCodeOutputStream localTypeCodeOutputStream1 = paramTypeCodeOutputStream.getTopLevelStream();
/*      */     int j;
/* 1659 */     if (this._kind == -1)
/*      */     {
/* 1664 */       int i = localTypeCodeOutputStream1.getPositionForID(this._id);
/* 1665 */       j = paramTypeCodeOutputStream.getTopLevelPosition();
/*      */ 
/* 1669 */       paramTypeCodeOutputStream.writeIndirection(-1, i);
/*      */ 
/* 1671 */       return;
/*      */     }
/*      */ 
/* 1678 */     paramTypeCodeOutputStream.write_long(this._kind);
/*      */ 
/* 1687 */     localTypeCodeOutputStream1.addIDAtPosition(this._id, paramTypeCodeOutputStream.getTopLevelPosition() - 4);
/*      */ 
/* 1689 */     switch (typeTable[this._kind])
/*      */     {
/*      */     case 0:
/* 1692 */       break;
/*      */     case 1:
/* 1695 */       switch (this._kind)
/*      */       {
/*      */       case 18:
/*      */       case 27:
/* 1699 */         paramTypeCodeOutputStream.write_long(this._length);
/* 1700 */         break;
/*      */       case 28:
/* 1702 */         paramTypeCodeOutputStream.write_ushort(this._digits);
/* 1703 */         paramTypeCodeOutputStream.write_short(this._scale);
/* 1704 */         break;
/*      */       default:
/* 1707 */         throw this.wrapper.invalidSimpleTypecode();
/*      */       }
/*      */ 
/*      */       break;
/*      */     case 2:
/* 1714 */       TypeCodeOutputStream localTypeCodeOutputStream2 = paramTypeCodeOutputStream.createEncapsulation(paramTypeCodeOutputStream.orb());
/*      */ 
/* 1716 */       switch (this._kind)
/*      */       {
/*      */       case 14:
/*      */       case 32:
/* 1722 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1725 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1727 */         break;
/*      */       case 16:
/* 1732 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1735 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1738 */         this._discriminator.write_value(localTypeCodeOutputStream2);
/*      */ 
/* 1741 */         localTypeCodeOutputStream2.write_long(this._defaultIndex);
/*      */ 
/* 1744 */         localTypeCodeOutputStream2.write_long(this._memberCount);
/*      */ 
/* 1747 */         for (j = 0; j < this._memberCount; j++)
/*      */         {
/* 1750 */           if (j == this._defaultIndex) {
/* 1751 */             localTypeCodeOutputStream2.write_octet(this._unionLabels[j].extract_octet());
/*      */           }
/*      */           else {
/* 1754 */             switch (realType(this._discriminator).kind().value()) {
/*      */             case 2:
/* 1756 */               localTypeCodeOutputStream2.write_short(this._unionLabels[j].extract_short());
/* 1757 */               break;
/*      */             case 3:
/* 1759 */               localTypeCodeOutputStream2.write_long(this._unionLabels[j].extract_long());
/* 1760 */               break;
/*      */             case 4:
/* 1762 */               localTypeCodeOutputStream2.write_short(this._unionLabels[j].extract_ushort());
/* 1763 */               break;
/*      */             case 5:
/* 1765 */               localTypeCodeOutputStream2.write_long(this._unionLabels[j].extract_ulong());
/* 1766 */               break;
/*      */             case 6:
/* 1768 */               localTypeCodeOutputStream2.write_float(this._unionLabels[j].extract_float());
/* 1769 */               break;
/*      */             case 7:
/* 1771 */               localTypeCodeOutputStream2.write_double(this._unionLabels[j].extract_double());
/* 1772 */               break;
/*      */             case 8:
/* 1774 */               localTypeCodeOutputStream2.write_boolean(this._unionLabels[j].extract_boolean());
/* 1775 */               break;
/*      */             case 9:
/* 1777 */               localTypeCodeOutputStream2.write_char(this._unionLabels[j].extract_char());
/* 1778 */               break;
/*      */             case 17:
/* 1780 */               localTypeCodeOutputStream2.write_long(this._unionLabels[j].extract_long());
/* 1781 */               break;
/*      */             case 23:
/* 1783 */               localTypeCodeOutputStream2.write_longlong(this._unionLabels[j].extract_longlong());
/* 1784 */               break;
/*      */             case 24:
/* 1786 */               localTypeCodeOutputStream2.write_longlong(this._unionLabels[j].extract_ulonglong());
/* 1787 */               break;
/*      */             case 26:
/* 1793 */               localTypeCodeOutputStream2.write_wchar(this._unionLabels[j].extract_wchar());
/* 1794 */               break;
/*      */             case 10:
/*      */             case 11:
/*      */             case 12:
/*      */             case 13:
/*      */             case 14:
/*      */             case 15:
/*      */             case 16:
/*      */             case 18:
/*      */             case 19:
/*      */             case 20:
/*      */             case 21:
/*      */             case 22:
/*      */             case 25:
/*      */             default:
/* 1796 */               throw this.wrapper.invalidComplexTypecode();
/*      */             }
/*      */           }
/* 1799 */           localTypeCodeOutputStream2.write_string(this._memberNames[j]);
/* 1800 */           this._memberTypes[j].write_value(localTypeCodeOutputStream2);
/*      */         }
/*      */ 
/* 1803 */         break;
/*      */       case 17:
/* 1808 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1811 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1814 */         localTypeCodeOutputStream2.write_long(this._memberCount);
/*      */ 
/* 1817 */         for (j = 0; j < this._memberCount; j++) {
/* 1818 */           localTypeCodeOutputStream2.write_string(this._memberNames[j]);
/*      */         }
/* 1820 */         break;
/*      */       case 19:
/* 1825 */         lazy_content_type().write_value(localTypeCodeOutputStream2);
/*      */ 
/* 1828 */         localTypeCodeOutputStream2.write_long(this._length);
/*      */ 
/* 1830 */         break;
/*      */       case 20:
/* 1835 */         this._contentType.write_value(localTypeCodeOutputStream2);
/*      */ 
/* 1838 */         localTypeCodeOutputStream2.write_long(this._length);
/*      */ 
/* 1840 */         break;
/*      */       case 21:
/*      */       case 30:
/* 1846 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1849 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1852 */         this._contentType.write_value(localTypeCodeOutputStream2);
/*      */ 
/* 1854 */         break;
/*      */       case 15:
/*      */       case 22:
/* 1860 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1863 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1866 */         localTypeCodeOutputStream2.write_long(this._memberCount);
/*      */ 
/* 1869 */         for (j = 0; j < this._memberCount; j++) {
/* 1870 */           localTypeCodeOutputStream2.write_string(this._memberNames[j]);
/*      */ 
/* 1873 */           this._memberTypes[j].write_value(localTypeCodeOutputStream2);
/*      */         }
/*      */ 
/* 1876 */         break;
/*      */       case 29:
/* 1881 */         localTypeCodeOutputStream2.write_string(this._id);
/*      */ 
/* 1884 */         localTypeCodeOutputStream2.write_string(this._name);
/*      */ 
/* 1887 */         localTypeCodeOutputStream2.write_short(this._type_modifier);
/*      */ 
/* 1890 */         if (this._concrete_base == null)
/* 1891 */           this._orb.get_primitive_tc(0).write_value(localTypeCodeOutputStream2);
/*      */         else {
/* 1893 */           this._concrete_base.write_value(localTypeCodeOutputStream2);
/*      */         }
/*      */ 
/* 1897 */         localTypeCodeOutputStream2.write_long(this._memberCount);
/*      */ 
/* 1900 */         for (j = 0; j < this._memberCount; j++) {
/* 1901 */           localTypeCodeOutputStream2.write_string(this._memberNames[j]);
/*      */ 
/* 1904 */           this._memberTypes[j].write_value(localTypeCodeOutputStream2);
/* 1905 */           localTypeCodeOutputStream2.write_short(this._memberAccess[j]);
/*      */         }
/*      */ 
/* 1908 */         break;
/*      */       case 18:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 31:
/*      */       default:
/* 1911 */         throw this.wrapper.invalidTypecodeKindMarshal();
/*      */       }
/*      */ 
/* 1915 */       localTypeCodeOutputStream2.writeOctetSequenceTo(paramTypeCodeOutputStream);
/* 1916 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void copy(org.omg.CORBA.portable.InputStream paramInputStream, org.omg.CORBA.portable.OutputStream paramOutputStream)
/*      */   {
/*      */     Object localObject;
/*      */     int i2;
/* 1931 */     switch (this._kind)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*      */     case 31:
/*      */     case 32:
/* 1937 */       break;
/*      */     case 2:
/*      */     case 4:
/* 1941 */       paramOutputStream.write_short(paramInputStream.read_short());
/* 1942 */       break;
/*      */     case 3:
/*      */     case 5:
/* 1946 */       paramOutputStream.write_long(paramInputStream.read_long());
/* 1947 */       break;
/*      */     case 6:
/* 1950 */       paramOutputStream.write_float(paramInputStream.read_float());
/* 1951 */       break;
/*      */     case 7:
/* 1954 */       paramOutputStream.write_double(paramInputStream.read_double());
/* 1955 */       break;
/*      */     case 23:
/*      */     case 24:
/* 1959 */       paramOutputStream.write_longlong(paramInputStream.read_longlong());
/* 1960 */       break;
/*      */     case 25:
/* 1963 */       throw this.wrapper.tkLongDoubleNotSupported();
/*      */     case 8:
/* 1966 */       paramOutputStream.write_boolean(paramInputStream.read_boolean());
/* 1967 */       break;
/*      */     case 9:
/* 1970 */       paramOutputStream.write_char(paramInputStream.read_char());
/* 1971 */       break;
/*      */     case 26:
/* 1974 */       paramOutputStream.write_wchar(paramInputStream.read_wchar());
/* 1975 */       break;
/*      */     case 10:
/* 1978 */       paramOutputStream.write_octet(paramInputStream.read_octet());
/* 1979 */       break;
/*      */     case 18:
/* 1984 */       localObject = paramInputStream.read_string();
/*      */ 
/* 1986 */       if ((this._length != 0) && (((String)localObject).length() > this._length)) {
/* 1987 */         throw this.wrapper.badStringBounds(new Integer(((String)localObject).length()), new Integer(this._length));
/*      */       }
/* 1989 */       paramOutputStream.write_string((String)localObject);
/*      */ 
/* 1991 */       break;
/*      */     case 27:
/* 1996 */       localObject = paramInputStream.read_wstring();
/*      */ 
/* 1998 */       if ((this._length != 0) && (((String)localObject).length() > this._length)) {
/* 1999 */         throw this.wrapper.badStringBounds(new Integer(((String)localObject).length()), new Integer(this._length));
/*      */       }
/* 2001 */       paramOutputStream.write_wstring((String)localObject);
/*      */ 
/* 2003 */       break;
/*      */     case 28:
/* 2007 */       paramOutputStream.write_ushort(paramInputStream.read_ushort());
/* 2008 */       paramOutputStream.write_short(paramInputStream.read_short());
/*      */ 
/* 2010 */       break;
/*      */     case 11:
/* 2015 */       localObject = ((CDRInputStream)paramInputStream).orb().create_any();
/* 2016 */       TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl((com.sun.corba.se.spi.orb.ORB)paramOutputStream.orb());
/* 2017 */       localTypeCodeImpl.read_value((org.omg.CORBA_2_3.portable.InputStream)paramInputStream);
/* 2018 */       localTypeCodeImpl.write_value((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream);
/* 2019 */       ((Any)localObject).read_value(paramInputStream, localTypeCodeImpl);
/* 2020 */       ((Any)localObject).write_value(paramOutputStream);
/* 2021 */       break;
/*      */     case 12:
/* 2026 */       paramOutputStream.write_TypeCode(paramInputStream.read_TypeCode());
/* 2027 */       break;
/*      */     case 13:
/* 2032 */       paramOutputStream.write_Principal(paramInputStream.read_Principal());
/* 2033 */       break;
/*      */     case 14:
/* 2038 */       paramOutputStream.write_Object(paramInputStream.read_Object());
/* 2039 */       break;
/*      */     case 22:
/* 2044 */       paramOutputStream.write_string(paramInputStream.read_string());
/*      */     case 15:
/*      */     case 29:
/* 2052 */       for (int i = 0; i < this._memberTypes.length; i++) {
/* 2053 */         this._memberTypes[i].copy(paramInputStream, paramOutputStream);
/*      */       }
/* 2055 */       break;
/*      */     case 16:
/* 2078 */       AnyImpl localAnyImpl = new AnyImpl((com.sun.corba.se.spi.orb.ORB)paramInputStream.orb());
/*      */       int k;
/*      */       long l;
/* 2080 */       switch (realType(this._discriminator).kind().value())
/*      */       {
/*      */       case 2:
/* 2083 */         short s = paramInputStream.read_short();
/* 2084 */         localAnyImpl.insert_short(s);
/* 2085 */         paramOutputStream.write_short(s);
/* 2086 */         break;
/*      */       case 3:
/* 2090 */         k = paramInputStream.read_long();
/* 2091 */         localAnyImpl.insert_long(k);
/* 2092 */         paramOutputStream.write_long(k);
/* 2093 */         break;
/*      */       case 4:
/* 2097 */         k = paramInputStream.read_short();
/* 2098 */         localAnyImpl.insert_ushort(k);
/* 2099 */         paramOutputStream.write_short(k);
/* 2100 */         break;
/*      */       case 5:
/* 2104 */         int m = paramInputStream.read_long();
/* 2105 */         localAnyImpl.insert_ulong(m);
/* 2106 */         paramOutputStream.write_long(m);
/* 2107 */         break;
/*      */       case 6:
/* 2111 */         float f = paramInputStream.read_float();
/* 2112 */         localAnyImpl.insert_float(f);
/* 2113 */         paramOutputStream.write_float(f);
/* 2114 */         break;
/*      */       case 7:
/* 2118 */         double d = paramInputStream.read_double();
/* 2119 */         localAnyImpl.insert_double(d);
/* 2120 */         paramOutputStream.write_double(d);
/* 2121 */         break;
/*      */       case 8:
/* 2125 */         boolean bool = paramInputStream.read_boolean();
/* 2126 */         localAnyImpl.insert_boolean(bool);
/* 2127 */         paramOutputStream.write_boolean(bool);
/* 2128 */         break;
/*      */       case 9:
/* 2132 */         char c = paramInputStream.read_char();
/* 2133 */         localAnyImpl.insert_char(c);
/* 2134 */         paramOutputStream.write_char(c);
/* 2135 */         break;
/*      */       case 17:
/* 2139 */         int n = paramInputStream.read_long();
/* 2140 */         localAnyImpl.type(this._discriminator);
/* 2141 */         localAnyImpl.insert_long(n);
/* 2142 */         paramOutputStream.write_long(n);
/* 2143 */         break;
/*      */       case 23:
/* 2147 */         l = paramInputStream.read_longlong();
/* 2148 */         localAnyImpl.insert_longlong(l);
/* 2149 */         paramOutputStream.write_longlong(l);
/* 2150 */         break;
/*      */       case 24:
/* 2154 */         l = paramInputStream.read_longlong();
/* 2155 */         localAnyImpl.insert_ulonglong(l);
/* 2156 */         paramOutputStream.write_longlong(l);
/* 2157 */         break;
/*      */       case 26:
/* 2169 */         i1 = paramInputStream.read_wchar();
/* 2170 */         localAnyImpl.insert_wchar(i1);
/* 2171 */         paramOutputStream.write_wchar(i1);
/* 2172 */         break;
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 14:
/*      */       case 15:
/*      */       case 16:
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 22:
/*      */       case 25:
/*      */       default:
/* 2175 */         throw this.wrapper.illegalUnionDiscriminatorType();
/*      */       }
/*      */ 
/* 2182 */       for (int i1 = 0; i1 < this._unionLabels.length; i1++)
/*      */       {
/* 2184 */         if (localAnyImpl.equal(this._unionLabels[i1])) {
/* 2185 */           this._memberTypes[i1].copy(paramInputStream, paramOutputStream);
/* 2186 */           break;
/*      */         }
/*      */       }
/*      */ 
/* 2190 */       if (i1 == this._unionLabels.length)
/*      */       {
/* 2192 */         if (this._defaultIndex == -1)
/*      */         {
/* 2194 */           throw this.wrapper.unexpectedUnionDefault();
/*      */         }
/*      */ 
/* 2197 */         this._memberTypes[this._defaultIndex].copy(paramInputStream, paramOutputStream); } break;
/*      */     case 17:
/* 2203 */       paramOutputStream.write_long(paramInputStream.read_long());
/* 2204 */       break;
/*      */     case 19:
/* 2208 */       int j = paramInputStream.read_long();
/*      */ 
/* 2211 */       if ((this._length != 0) && (j > this._length)) {
/* 2212 */         throw this.wrapper.badSequenceBounds(new Integer(j), new Integer(this._length));
/*      */       }
/*      */ 
/* 2216 */       paramOutputStream.write_long(j);
/*      */ 
/* 2219 */       lazy_content_type();
/* 2220 */       for (i2 = 0; i2 < j; i2++)
/* 2221 */         this._contentType.copy(paramInputStream, paramOutputStream);
/* 2222 */       break;
/*      */     case 20:
/* 2226 */       for (i2 = 0; i2 < this._length; i2++)
/* 2227 */         this._contentType.copy(paramInputStream, paramOutputStream);
/* 2228 */       break;
/*      */     case 21:
/*      */     case 30:
/* 2233 */       this._contentType.copy(paramInputStream, paramOutputStream);
/* 2234 */       break;
/*      */     case -1:
/* 2241 */       indirectType().copy(paramInputStream, paramOutputStream);
/* 2242 */       break;
/*      */     default:
/* 2245 */       throw this.wrapper.invalidTypecodeKindMarshal();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static short digits(BigDecimal paramBigDecimal)
/*      */   {
/* 2251 */     if (paramBigDecimal == null)
/* 2252 */       return 0;
/* 2253 */     short s = (short)paramBigDecimal.unscaledValue().toString().length();
/* 2254 */     if (paramBigDecimal.signum() == -1)
/* 2255 */       s = (short)(s - 1);
/* 2256 */     return s;
/*      */   }
/*      */ 
/*      */   protected static short scale(BigDecimal paramBigDecimal) {
/* 2260 */     if (paramBigDecimal == null)
/* 2261 */       return 0;
/* 2262 */     return (short)paramBigDecimal.scale();
/*      */   }
/*      */ 
/*      */   int currentUnionMemberIndex(Any paramAny)
/*      */     throws BadKind
/*      */   {
/* 2271 */     if (this._kind != 16)
/* 2272 */       throw new BadKind();
/*      */     try
/*      */     {
/* 2275 */       for (int i = 0; i < member_count(); i++) {
/* 2276 */         if (member_label(i).equal(paramAny)) {
/* 2277 */           return i;
/*      */         }
/*      */       }
/* 2280 */       if (this._defaultIndex != -1)
/* 2281 */         return this._defaultIndex;
/*      */     } catch (BadKind localBadKind) {
/*      */     }
/*      */     catch (Bounds localBounds) {
/*      */     }
/* 2286 */     return -1;
/*      */   }
/*      */ 
/*      */   public String description() {
/* 2290 */     return "TypeCodeImpl with kind " + this._kind + " and id " + this._id;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 2294 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
/* 2295 */     PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream, true);
/* 2296 */     printStream(localPrintStream);
/* 2297 */     return super.toString() + " =\n" + localByteArrayOutputStream.toString();
/*      */   }
/*      */ 
/*      */   public void printStream(PrintStream paramPrintStream) {
/* 2301 */     printStream(paramPrintStream, 0);
/*      */   }
/*      */ 
/*      */   private void printStream(PrintStream paramPrintStream, int paramInt) {
/* 2305 */     if (this._kind == -1) {
/* 2306 */       paramPrintStream.print("indirect " + this._id);
/* 2307 */       return;
/*      */     }
/*      */ 
/* 2310 */     switch (this._kind) {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 31:
/* 2331 */       paramPrintStream.print(kindNames[this._kind] + " " + this._name);
/* 2332 */       break;
/*      */     case 15:
/*      */     case 22:
/*      */     case 29:
/* 2337 */       paramPrintStream.println(kindNames[this._kind] + " " + this._name + " = {");
/* 2338 */       for (int i = 0; i < this._memberCount; i++)
/*      */       {
/* 2340 */         paramPrintStream.print(indent(paramInt + 1));
/* 2341 */         if (this._memberTypes[i] != null)
/* 2342 */           this._memberTypes[i].printStream(paramPrintStream, paramInt + 1);
/*      */         else
/* 2344 */           paramPrintStream.print("<unknown type>");
/* 2345 */         paramPrintStream.println(" " + this._memberNames[i] + ";");
/*      */       }
/* 2347 */       paramPrintStream.print(indent(paramInt) + "}");
/* 2348 */       break;
/*      */     case 16:
/* 2351 */       paramPrintStream.print("union " + this._name + "...");
/* 2352 */       break;
/*      */     case 17:
/* 2355 */       paramPrintStream.print("enum " + this._name + "...");
/* 2356 */       break;
/*      */     case 18:
/* 2359 */       if (this._length == 0)
/* 2360 */         paramPrintStream.print("unbounded string " + this._name);
/*      */       else
/* 2362 */         paramPrintStream.print("bounded string(" + this._length + ") " + this._name);
/* 2363 */       break;
/*      */     case 19:
/*      */     case 20:
/* 2367 */       paramPrintStream.println(kindNames[this._kind] + "[" + this._length + "] " + this._name + " = {");
/* 2368 */       paramPrintStream.print(indent(paramInt + 1));
/* 2369 */       if (lazy_content_type() != null) {
/* 2370 */         lazy_content_type().printStream(paramPrintStream, paramInt + 1);
/*      */       }
/* 2372 */       paramPrintStream.println(indent(paramInt) + "}");
/* 2373 */       break;
/*      */     case 21:
/* 2376 */       paramPrintStream.print("alias " + this._name + " = " + (this._contentType != null ? this._contentType._name : "<unresolved>"));
/*      */ 
/* 2378 */       break;
/*      */     case 27:
/* 2381 */       paramPrintStream.print("wstring[" + this._length + "] " + this._name);
/* 2382 */       break;
/*      */     case 28:
/* 2385 */       paramPrintStream.print("fixed(" + this._digits + ", " + this._scale + ") " + this._name);
/* 2386 */       break;
/*      */     case 30:
/* 2389 */       paramPrintStream.print("valueBox " + this._name + "...");
/* 2390 */       break;
/*      */     case 32:
/* 2393 */       paramPrintStream.print("abstractInterface " + this._name + "...");
/* 2394 */       break;
/*      */     default:
/* 2397 */       paramPrintStream.print("<unknown type>");
/*      */     }
/*      */   }
/*      */ 
/*      */   private String indent(int paramInt)
/*      */   {
/* 2403 */     String str = "";
/* 2404 */     for (int i = 0; i < paramInt; i++) {
/* 2405 */       str = str + "  ";
/*      */     }
/* 2407 */     return str;
/*      */   }
/*      */ 
/*      */   protected void setCaching(boolean paramBoolean) {
/* 2411 */     this.cachingEnabled = paramBoolean;
/* 2412 */     if (!paramBoolean)
/* 2413 */       this.outBuffer = null;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.corba.TypeCodeImpl
 * JD-Core Version:    0.6.2
 */