/*      */ package com.sun.corba.se.impl.corba;
/*      */ 
/*      */ import com.sun.corba.se.impl.encoding.CDRInputStream;
/*      */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*      */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*      */ import com.sun.corba.se.impl.io.ValueUtility;
/*      */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*      */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*      */ import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
/*      */ import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
/*      */ import com.sun.corba.se.spi.orb.ORB;
/*      */ import com.sun.corba.se.spi.orb.ORBVersion;
/*      */ import com.sun.corba.se.spi.orb.ORBVersionFactory;
/*      */ import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import org.omg.CORBA.Any;
/*      */ import org.omg.CORBA.CompletionStatus;
/*      */ import org.omg.CORBA.Principal;
/*      */ import org.omg.CORBA.TCKind;
/*      */ import org.omg.CORBA.TypeCode;
/*      */ import org.omg.CORBA.TypeCodePackage.BadKind;
/*      */ import org.omg.CORBA.TypeCodePackage.Bounds;
/*      */ import org.omg.CORBA.portable.Streamable;
/*      */ 
/*      */ public class AnyImpl extends Any
/*      */ {
/*      */   private TypeCodeImpl typeCode;
/*      */   protected ORB orb;
/*      */   private ORBUtilSystemException wrapper;
/*      */   private CDRInputStream stream;
/*      */   private long value;
/*      */   private java.lang.Object object;
/*  113 */   private boolean isInitialized = false;
/*      */   private static final int DEFAULT_BUFFER_SIZE = 32;
/*  121 */   static boolean[] isStreamed = { false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, true, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false };
/*      */ 
/*      */   static AnyImpl convertToNative(ORB paramORB, Any paramAny)
/*      */   {
/*  158 */     if ((paramAny instanceof AnyImpl)) {
/*  159 */       return (AnyImpl)paramAny;
/*      */     }
/*  161 */     AnyImpl localAnyImpl = new AnyImpl(paramORB, paramAny);
/*  162 */     localAnyImpl.typeCode = TypeCodeImpl.convertToNative(paramORB, localAnyImpl.typeCode);
/*  163 */     return localAnyImpl;
/*      */   }
/*      */ 
/*      */   public AnyImpl(ORB paramORB)
/*      */   {
/*  177 */     this.orb = paramORB;
/*  178 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
/*      */ 
/*  181 */     this.typeCode = paramORB.get_primitive_tc(0);
/*  182 */     this.stream = null;
/*  183 */     this.object = null;
/*  184 */     this.value = 0L;
/*      */ 
/*  186 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public AnyImpl(ORB paramORB, Any paramAny)
/*      */   {
/*  193 */     this(paramORB);
/*      */ 
/*  195 */     if ((paramAny instanceof AnyImpl)) {
/*  196 */       AnyImpl localAnyImpl = (AnyImpl)paramAny;
/*  197 */       this.typeCode = localAnyImpl.typeCode;
/*  198 */       this.value = localAnyImpl.value;
/*  199 */       this.object = localAnyImpl.object;
/*  200 */       this.isInitialized = localAnyImpl.isInitialized;
/*      */ 
/*  202 */       if (localAnyImpl.stream != null)
/*  203 */         this.stream = localAnyImpl.stream.dup();
/*      */     }
/*      */     else {
/*  206 */       read_value(paramAny.create_input_stream(), paramAny.type());
/*      */     }
/*      */   }
/*      */ 
/*      */   public TypeCode type()
/*      */   {
/*  219 */     return this.typeCode;
/*      */   }
/*      */ 
/*      */   private TypeCode realType() {
/*  223 */     return realType(this.typeCode);
/*      */   }
/*      */ 
/*      */   private TypeCode realType(TypeCode paramTypeCode) {
/*  227 */     TypeCode localTypeCode = paramTypeCode;
/*      */     try
/*      */     {
/*  230 */       while (localTypeCode.kind().value() == 21)
/*  231 */         localTypeCode = localTypeCode.content_type();
/*      */     }
/*      */     catch (BadKind localBadKind) {
/*  234 */       throw this.wrapper.badkindCannotOccur(localBadKind);
/*      */     }
/*  236 */     return localTypeCode;
/*      */   }
/*      */ 
/*      */   public void type(TypeCode paramTypeCode)
/*      */   {
/*  248 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
/*      */ 
/*  250 */     this.stream = null;
/*  251 */     this.value = 0L;
/*  252 */     this.object = null;
/*      */ 
/*  254 */     this.isInitialized = (paramTypeCode.kind().value() == 0);
/*      */   }
/*      */ 
/*      */   public boolean equal(Any paramAny)
/*      */   {
/*  267 */     if (paramAny == this) {
/*  268 */       return true;
/*      */     }
/*      */ 
/*  272 */     if (!this.typeCode.equal(paramAny.type())) {
/*  273 */       return false;
/*      */     }
/*      */ 
/*  276 */     TypeCode localTypeCode = realType();
/*      */ 
/*  290 */     switch (localTypeCode.kind().value())
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*  294 */       return true;
/*      */     case 2:
/*  296 */       return extract_short() == paramAny.extract_short();
/*      */     case 3:
/*  298 */       return extract_long() == paramAny.extract_long();
/*      */     case 4:
/*  300 */       return extract_ushort() == paramAny.extract_ushort();
/*      */     case 5:
/*  302 */       return extract_ulong() == paramAny.extract_ulong();
/*      */     case 6:
/*  304 */       return extract_float() == paramAny.extract_float();
/*      */     case 7:
/*  306 */       return extract_double() == paramAny.extract_double();
/*      */     case 8:
/*  308 */       return extract_boolean() == paramAny.extract_boolean();
/*      */     case 9:
/*  310 */       return extract_char() == paramAny.extract_char();
/*      */     case 26:
/*  312 */       return extract_wchar() == paramAny.extract_wchar();
/*      */     case 10:
/*  314 */       return extract_octet() == paramAny.extract_octet();
/*      */     case 11:
/*  316 */       return extract_any().equal(paramAny.extract_any());
/*      */     case 12:
/*  318 */       return extract_TypeCode().equal(paramAny.extract_TypeCode());
/*      */     case 18:
/*  320 */       return extract_string().equals(paramAny.extract_string());
/*      */     case 27:
/*  322 */       return extract_wstring().equals(paramAny.extract_wstring());
/*      */     case 23:
/*  324 */       return extract_longlong() == paramAny.extract_longlong();
/*      */     case 24:
/*  326 */       return extract_ulonglong() == paramAny.extract_ulonglong();
/*      */     case 14:
/*  329 */       return extract_Object().equals(paramAny.extract_Object());
/*      */     case 13:
/*  331 */       return extract_Principal().equals(paramAny.extract_Principal());
/*      */     case 17:
/*  334 */       return extract_long() == paramAny.extract_long();
/*      */     case 28:
/*  336 */       return extract_fixed().compareTo(paramAny.extract_fixed()) == 0;
/*      */     case 15:
/*      */     case 16:
/*      */     case 19:
/*      */     case 20:
/*      */     case 22:
/*  342 */       org.omg.CORBA.portable.InputStream localInputStream1 = create_input_stream();
/*  343 */       org.omg.CORBA.portable.InputStream localInputStream2 = paramAny.create_input_stream();
/*  344 */       return equalMember(localTypeCode, localInputStream1, localInputStream2);
/*      */     case 29:
/*      */     case 30:
/*  351 */       return extract_Value().equals(paramAny.extract_Value());
/*      */     case 21:
/*  354 */       throw this.wrapper.errorResolvingAlias();
/*      */     case 25:
/*  358 */       throw this.wrapper.tkLongDoubleNotSupported();
/*      */     }
/*      */ 
/*  361 */     throw this.wrapper.typecodeNotSupported();
/*      */   }
/*      */ 
/*      */   private boolean equalMember(TypeCode paramTypeCode, org.omg.CORBA.portable.InputStream paramInputStream1, org.omg.CORBA.portable.InputStream paramInputStream2)
/*      */   {
/*  369 */     TypeCode localTypeCode = realType(paramTypeCode);
/*      */     try
/*      */     {
/*      */       int j;
/*      */       int m;
/*  372 */       switch (localTypeCode.kind().value())
/*      */       {
/*      */       case 0:
/*      */       case 1:
/*  376 */         return true;
/*      */       case 2:
/*  378 */         return paramInputStream1.read_short() == paramInputStream2.read_short();
/*      */       case 3:
/*  380 */         return paramInputStream1.read_long() == paramInputStream2.read_long();
/*      */       case 4:
/*  382 */         return paramInputStream1.read_ushort() == paramInputStream2.read_ushort();
/*      */       case 5:
/*  384 */         return paramInputStream1.read_ulong() == paramInputStream2.read_ulong();
/*      */       case 6:
/*  386 */         return paramInputStream1.read_float() == paramInputStream2.read_float();
/*      */       case 7:
/*  388 */         return paramInputStream1.read_double() == paramInputStream2.read_double();
/*      */       case 8:
/*  390 */         return paramInputStream1.read_boolean() == paramInputStream2.read_boolean();
/*      */       case 9:
/*  392 */         return paramInputStream1.read_char() == paramInputStream2.read_char();
/*      */       case 26:
/*  394 */         return paramInputStream1.read_wchar() == paramInputStream2.read_wchar();
/*      */       case 10:
/*  396 */         return paramInputStream1.read_octet() == paramInputStream2.read_octet();
/*      */       case 11:
/*  398 */         return paramInputStream1.read_any().equal(paramInputStream2.read_any());
/*      */       case 12:
/*  400 */         return paramInputStream1.read_TypeCode().equal(paramInputStream2.read_TypeCode());
/*      */       case 18:
/*  402 */         return paramInputStream1.read_string().equals(paramInputStream2.read_string());
/*      */       case 27:
/*  404 */         return paramInputStream1.read_wstring().equals(paramInputStream2.read_wstring());
/*      */       case 23:
/*  406 */         return paramInputStream1.read_longlong() == paramInputStream2.read_longlong();
/*      */       case 24:
/*  408 */         return paramInputStream1.read_ulonglong() == paramInputStream2.read_ulonglong();
/*      */       case 14:
/*  411 */         return paramInputStream1.read_Object().equals(paramInputStream2.read_Object());
/*      */       case 13:
/*  413 */         return paramInputStream1.read_Principal().equals(paramInputStream2.read_Principal());
/*      */       case 17:
/*  416 */         return paramInputStream1.read_long() == paramInputStream2.read_long();
/*      */       case 28:
/*  418 */         return paramInputStream1.read_fixed().compareTo(paramInputStream2.read_fixed()) == 0;
/*      */       case 15:
/*      */       case 22:
/*  421 */         int i = localTypeCode.member_count();
/*  422 */         for (int k = 0; k < i; k++) {
/*  423 */           if (!equalMember(localTypeCode.member_type(k), paramInputStream1, paramInputStream2)) {
/*  424 */             return false;
/*      */           }
/*      */         }
/*  427 */         return true;
/*      */       case 16:
/*  430 */         Any localAny1 = this.orb.create_any();
/*  431 */         Any localAny2 = this.orb.create_any();
/*  432 */         localAny1.read_value(paramInputStream1, localTypeCode.discriminator_type());
/*  433 */         localAny2.read_value(paramInputStream2, localTypeCode.discriminator_type());
/*      */ 
/*  435 */         if (!localAny1.equal(localAny2)) {
/*  436 */           return false;
/*      */         }
/*  438 */         TypeCodeImpl localTypeCodeImpl = TypeCodeImpl.convertToNative(this.orb, localTypeCode);
/*  439 */         int n = localTypeCodeImpl.currentUnionMemberIndex(localAny1);
/*  440 */         if (n == -1) {
/*  441 */           throw this.wrapper.unionDiscriminatorError();
/*      */         }
/*  443 */         if (!equalMember(localTypeCode.member_type(n), paramInputStream1, paramInputStream2)) {
/*  444 */           return false;
/*      */         }
/*  446 */         return true;
/*      */       case 19:
/*  449 */         j = paramInputStream1.read_long();
/*  450 */         paramInputStream2.read_long();
/*  451 */         for (m = 0; m < j; m++) {
/*  452 */           if (!equalMember(localTypeCode.content_type(), paramInputStream1, paramInputStream2)) {
/*  453 */             return false;
/*      */           }
/*      */         }
/*  456 */         return true;
/*      */       case 20:
/*  459 */         j = localTypeCode.member_count();
/*  460 */         for (m = 0; m < j; m++) {
/*  461 */           if (!equalMember(localTypeCode.content_type(), paramInputStream1, paramInputStream2)) {
/*  462 */             return false;
/*      */           }
/*      */         }
/*  465 */         return true;
/*      */       case 29:
/*      */       case 30:
/*  473 */         org.omg.CORBA_2_3.portable.InputStream localInputStream1 = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream1;
/*      */ 
/*  475 */         org.omg.CORBA_2_3.portable.InputStream localInputStream2 = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream2;
/*      */ 
/*  477 */         return localInputStream1.read_value().equals(localInputStream2.read_value());
/*      */       case 21:
/*  481 */         throw this.wrapper.errorResolvingAlias();
/*      */       case 25:
/*  484 */         throw this.wrapper.tkLongDoubleNotSupported();
/*      */       }
/*      */ 
/*  487 */       throw this.wrapper.typecodeNotSupported();
/*      */     }
/*      */     catch (BadKind localBadKind) {
/*  490 */       throw this.wrapper.badkindCannotOccur(); } catch (Bounds localBounds) {
/*      */     }
/*  492 */     throw this.wrapper.boundsCannotOccur();
/*      */   }
/*      */ 
/*      */   public org.omg.CORBA.portable.OutputStream create_output_stream()
/*      */   {
/*  507 */     return new AnyOutputStream(this.orb);
/*      */   }
/*      */ 
/*      */   public org.omg.CORBA.portable.InputStream create_input_stream()
/*      */   {
/*  522 */     if (isStreamed[realType().kind().value()] != 0) {
/*  523 */       return this.stream.dup();
/*      */     }
/*  525 */     org.omg.CORBA.portable.OutputStream localOutputStream = this.orb.create_output_stream();
/*  526 */     TCUtility.marshalIn(localOutputStream, realType(), this.value, this.object);
/*      */ 
/*  528 */     return localOutputStream.create_input_stream();
/*      */   }
/*      */ 
/*      */   public void read_value(org.omg.CORBA.portable.InputStream paramInputStream, TypeCode paramTypeCode)
/*      */   {
/*  554 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
/*  555 */     int i = realType().kind().value();
/*  556 */     if (i >= isStreamed.length)
/*  557 */       throw this.wrapper.invalidIsstreamedTckind(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
/*      */     java.lang.Object localObject;
/*  561 */     if (isStreamed[i] != 0) {
/*  562 */       if ((paramInputStream instanceof AnyInputStream))
/*      */       {
/*  564 */         this.stream = ((CDRInputStream)paramInputStream);
/*      */       } else {
/*  566 */         localObject = (org.omg.CORBA_2_3.portable.OutputStream)this.orb.create_output_stream();
/*      */ 
/*  568 */         this.typeCode.copy((org.omg.CORBA_2_3.portable.InputStream)paramInputStream, (org.omg.CORBA.portable.OutputStream)localObject);
/*  569 */         this.stream = ((CDRInputStream)((org.omg.CORBA_2_3.portable.OutputStream)localObject).create_input_stream());
/*      */       }
/*      */     } else {
/*  572 */       localObject = new java.lang.Object[1];
/*  573 */       localObject[0] = this.object;
/*  574 */       long[] arrayOfLong = new long[1];
/*  575 */       TCUtility.unmarshalIn(paramInputStream, this.typeCode, arrayOfLong, (java.lang.Object[])localObject);
/*  576 */       this.value = arrayOfLong[0];
/*  577 */       this.object = localObject[0];
/*  578 */       this.stream = null;
/*      */     }
/*  580 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public void write_value(org.omg.CORBA.portable.OutputStream paramOutputStream)
/*      */   {
/*  594 */     if (isStreamed[realType().kind().value()] != 0) {
/*  595 */       this.typeCode.copy(this.stream.dup(), paramOutputStream);
/*      */     }
/*      */     else
/*  598 */       TCUtility.marshalIn(paramOutputStream, realType(), this.value, this.object);
/*      */   }
/*      */ 
/*      */   public void insert_Streamable(Streamable paramStreamable)
/*      */   {
/*  610 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramStreamable._type());
/*  611 */     this.object = paramStreamable;
/*  612 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public Streamable extract_Streamable()
/*      */   {
/*  618 */     return (Streamable)this.object;
/*      */   }
/*      */ 
/*      */   public void insert_short(short paramShort)
/*      */   {
/*  630 */     this.typeCode = this.orb.get_primitive_tc(2);
/*  631 */     this.value = paramShort;
/*  632 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   private String getTCKindName(int paramInt)
/*      */   {
/*  637 */     if ((paramInt >= 0) && (paramInt < TypeCodeImpl.kindNames.length)) {
/*  638 */       return TypeCodeImpl.kindNames[paramInt];
/*      */     }
/*  640 */     return "UNKNOWN(" + paramInt + ")";
/*      */   }
/*      */ 
/*      */   private void checkExtractBadOperation(int paramInt)
/*      */   {
/*  645 */     if (!this.isInitialized) {
/*  646 */       throw this.wrapper.extractNotInitialized();
/*      */     }
/*  648 */     int i = realType().kind().value();
/*  649 */     if (i != paramInt) {
/*  650 */       String str1 = getTCKindName(i);
/*  651 */       String str2 = getTCKindName(paramInt);
/*  652 */       throw this.wrapper.extractWrongType(str2, str1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkExtractBadOperationList(int[] paramArrayOfInt)
/*      */   {
/*  658 */     if (!this.isInitialized) {
/*  659 */       throw this.wrapper.extractNotInitialized();
/*      */     }
/*  661 */     int i = realType().kind().value();
/*  662 */     for (int j = 0; j < paramArrayOfInt.length; j++) {
/*  663 */       if (i == paramArrayOfInt[j])
/*  664 */         return;
/*      */     }
/*  666 */     ArrayList localArrayList = new ArrayList();
/*  667 */     for (int k = 0; k < paramArrayOfInt.length; k++) {
/*  668 */       localArrayList.add(getTCKindName(paramArrayOfInt[k]));
/*      */     }
/*  670 */     String str = getTCKindName(i);
/*  671 */     throw this.wrapper.extractWrongTypeList(localArrayList, str);
/*      */   }
/*      */ 
/*      */   public short extract_short()
/*      */   {
/*  680 */     checkExtractBadOperation(2);
/*  681 */     return (short)(int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_long(int paramInt)
/*      */   {
/*  692 */     int i = realType().kind().value();
/*  693 */     if ((i != 3) && (i != 17)) {
/*  694 */       this.typeCode = this.orb.get_primitive_tc(3);
/*      */     }
/*  696 */     this.value = paramInt;
/*  697 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public int extract_long()
/*      */   {
/*  706 */     checkExtractBadOperationList(new int[] { 3, 17 });
/*  707 */     return (int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_ushort(short paramShort)
/*      */   {
/*  716 */     this.typeCode = this.orb.get_primitive_tc(4);
/*  717 */     this.value = paramShort;
/*  718 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public short extract_ushort()
/*      */   {
/*  727 */     checkExtractBadOperation(4);
/*  728 */     return (short)(int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_ulong(int paramInt)
/*      */   {
/*  737 */     this.typeCode = this.orb.get_primitive_tc(5);
/*  738 */     this.value = paramInt;
/*  739 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public int extract_ulong()
/*      */   {
/*  748 */     checkExtractBadOperation(5);
/*  749 */     return (int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_float(float paramFloat)
/*      */   {
/*  758 */     this.typeCode = this.orb.get_primitive_tc(6);
/*  759 */     this.value = Float.floatToIntBits(paramFloat);
/*  760 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public float extract_float()
/*      */   {
/*  769 */     checkExtractBadOperation(6);
/*  770 */     return Float.intBitsToFloat((int)this.value);
/*      */   }
/*      */ 
/*      */   public void insert_double(double paramDouble)
/*      */   {
/*  779 */     this.typeCode = this.orb.get_primitive_tc(7);
/*  780 */     this.value = Double.doubleToLongBits(paramDouble);
/*  781 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public double extract_double()
/*      */   {
/*  790 */     checkExtractBadOperation(7);
/*  791 */     return Double.longBitsToDouble(this.value);
/*      */   }
/*      */ 
/*      */   public void insert_longlong(long paramLong)
/*      */   {
/*  800 */     this.typeCode = this.orb.get_primitive_tc(23);
/*  801 */     this.value = paramLong;
/*  802 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public long extract_longlong()
/*      */   {
/*  811 */     checkExtractBadOperation(23);
/*  812 */     return this.value;
/*      */   }
/*      */ 
/*      */   public void insert_ulonglong(long paramLong)
/*      */   {
/*  821 */     this.typeCode = this.orb.get_primitive_tc(24);
/*  822 */     this.value = paramLong;
/*  823 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public long extract_ulonglong()
/*      */   {
/*  832 */     checkExtractBadOperation(24);
/*  833 */     return this.value;
/*      */   }
/*      */ 
/*      */   public void insert_boolean(boolean paramBoolean)
/*      */   {
/*  842 */     this.typeCode = this.orb.get_primitive_tc(8);
/*  843 */     this.value = (paramBoolean ? 1L : 0L);
/*  844 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public boolean extract_boolean()
/*      */   {
/*  853 */     checkExtractBadOperation(8);
/*  854 */     return this.value != 0L;
/*      */   }
/*      */ 
/*      */   public void insert_char(char paramChar)
/*      */   {
/*  863 */     this.typeCode = this.orb.get_primitive_tc(9);
/*  864 */     this.value = paramChar;
/*  865 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public char extract_char()
/*      */   {
/*  874 */     checkExtractBadOperation(9);
/*  875 */     return (char)(int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_wchar(char paramChar)
/*      */   {
/*  884 */     this.typeCode = this.orb.get_primitive_tc(26);
/*  885 */     this.value = paramChar;
/*  886 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public char extract_wchar()
/*      */   {
/*  895 */     checkExtractBadOperation(26);
/*  896 */     return (char)(int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_octet(byte paramByte)
/*      */   {
/*  906 */     this.typeCode = this.orb.get_primitive_tc(10);
/*  907 */     this.value = paramByte;
/*  908 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public byte extract_octet()
/*      */   {
/*  917 */     checkExtractBadOperation(10);
/*  918 */     return (byte)(int)this.value;
/*      */   }
/*      */ 
/*      */   public void insert_string(String paramString)
/*      */   {
/*  928 */     if (this.typeCode.kind() == TCKind.tk_string) {
/*  929 */       int i = 0;
/*      */       try {
/*  931 */         i = this.typeCode.length();
/*      */       } catch (BadKind localBadKind) {
/*  933 */         throw this.wrapper.badkindCannotOccur();
/*      */       }
/*      */ 
/*  937 */       if ((i != 0) && (paramString != null) && (paramString.length() > i))
/*  938 */         throw this.wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i));
/*      */     }
/*      */     else
/*      */     {
/*  942 */       this.typeCode = this.orb.get_primitive_tc(18);
/*      */     }
/*  944 */     this.object = paramString;
/*  945 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public String extract_string()
/*      */   {
/*  954 */     checkExtractBadOperation(18);
/*  955 */     return (String)this.object;
/*      */   }
/*      */ 
/*      */   public void insert_wstring(String paramString)
/*      */   {
/*  965 */     if (this.typeCode.kind() == TCKind.tk_wstring) {
/*  966 */       int i = 0;
/*      */       try {
/*  968 */         i = this.typeCode.length();
/*      */       } catch (BadKind localBadKind) {
/*  970 */         throw this.wrapper.badkindCannotOccur();
/*      */       }
/*      */ 
/*  974 */       if ((i != 0) && (paramString != null) && (paramString.length() > i))
/*  975 */         throw this.wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i));
/*      */     }
/*      */     else
/*      */     {
/*  979 */       this.typeCode = this.orb.get_primitive_tc(27);
/*      */     }
/*  981 */     this.object = paramString;
/*  982 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public String extract_wstring()
/*      */   {
/*  991 */     checkExtractBadOperation(27);
/*  992 */     return (String)this.object;
/*      */   }
/*      */ 
/*      */   public void insert_any(Any paramAny)
/*      */   {
/* 1001 */     this.typeCode = this.orb.get_primitive_tc(11);
/* 1002 */     this.object = paramAny;
/* 1003 */     this.stream = null;
/* 1004 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public Any extract_any()
/*      */   {
/* 1013 */     checkExtractBadOperation(11);
/* 1014 */     return (Any)this.object;
/*      */   }
/*      */ 
/*      */   public void insert_Object(org.omg.CORBA.Object paramObject)
/*      */   {
/* 1023 */     if (paramObject == null) {
/* 1024 */       this.typeCode = this.orb.get_primitive_tc(14);
/*      */     }
/* 1026 */     else if (StubAdapter.isStub(paramObject)) {
/* 1027 */       String[] arrayOfString = StubAdapter.getTypeIds(paramObject);
/* 1028 */       this.typeCode = new TypeCodeImpl(this.orb, 14, arrayOfString[0], "");
/*      */     } else {
/* 1030 */       throw this.wrapper.badInsertobjParam(CompletionStatus.COMPLETED_MAYBE, paramObject.getClass().getName());
/*      */     }
/*      */ 
/* 1035 */     this.object = paramObject;
/* 1036 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public void insert_Object(org.omg.CORBA.Object paramObject, TypeCode paramTypeCode)
/*      */   {
/*      */     try
/*      */     {
/* 1047 */       if ((paramTypeCode.id().equals("IDL:omg.org/CORBA/Object:1.0")) || (paramObject._is_a(paramTypeCode.id())))
/*      */       {
/* 1049 */         this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
/* 1050 */         this.object = paramObject;
/*      */       }
/*      */       else {
/* 1053 */         throw this.wrapper.insertObjectIncompatible();
/*      */       }
/*      */     } catch (Exception localException) {
/* 1056 */       throw this.wrapper.insertObjectFailed(localException);
/*      */     }
/* 1058 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public org.omg.CORBA.Object extract_Object()
/*      */   {
/* 1067 */     if (!this.isInitialized) {
/* 1068 */       throw this.wrapper.extractNotInitialized();
/*      */     }
/*      */ 
/* 1071 */     org.omg.CORBA.Object localObject = null;
/*      */     try {
/* 1073 */       localObject = (org.omg.CORBA.Object)this.object;
/* 1074 */       if ((this.typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0")) || (localObject._is_a(this.typeCode.id()))) {
/* 1075 */         return localObject;
/*      */       }
/* 1077 */       throw this.wrapper.extractObjectIncompatible();
/*      */     }
/*      */     catch (Exception localException) {
/* 1080 */       throw this.wrapper.extractObjectFailed(localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void insert_TypeCode(TypeCode paramTypeCode)
/*      */   {
/* 1090 */     this.typeCode = this.orb.get_primitive_tc(12);
/* 1091 */     this.object = paramTypeCode;
/* 1092 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public TypeCode extract_TypeCode()
/*      */   {
/* 1101 */     checkExtractBadOperation(12);
/* 1102 */     return (TypeCode)this.object;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void insert_Principal(Principal paramPrincipal)
/*      */   {
/* 1111 */     this.typeCode = this.orb.get_primitive_tc(13);
/* 1112 */     this.object = paramPrincipal;
/* 1113 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Principal extract_Principal()
/*      */   {
/* 1122 */     checkExtractBadOperation(13);
/* 1123 */     return (Principal)this.object;
/*      */   }
/*      */ 
/*      */   public Serializable extract_Value()
/*      */   {
/* 1135 */     checkExtractBadOperationList(new int[] { 29, 30, 32 });
/*      */ 
/* 1137 */     return (Serializable)this.object;
/*      */   }
/*      */ 
/*      */   public void insert_Value(Serializable paramSerializable)
/*      */   {
/* 1143 */     this.object = paramSerializable;
/*      */     TypeCode localTypeCode;
/* 1147 */     if (paramSerializable == null) {
/* 1148 */       localTypeCode = this.orb.get_primitive_tc(TCKind.tk_value);
/*      */     }
/*      */     else
/*      */     {
/* 1159 */       localTypeCode = createTypeCodeForClass(paramSerializable.getClass(), (ORB)ORB.init());
/*      */     }
/*      */ 
/* 1162 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, localTypeCode);
/* 1163 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public void insert_Value(Serializable paramSerializable, TypeCode paramTypeCode)
/*      */   {
/* 1169 */     this.object = paramSerializable;
/* 1170 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
/* 1171 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public void insert_fixed(BigDecimal paramBigDecimal) {
/* 1175 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, this.orb.create_fixed_tc(TypeCodeImpl.digits(paramBigDecimal), TypeCodeImpl.scale(paramBigDecimal)));
/*      */ 
/* 1177 */     this.object = paramBigDecimal;
/* 1178 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public void insert_fixed(BigDecimal paramBigDecimal, TypeCode paramTypeCode)
/*      */   {
/*      */     try {
/* 1184 */       if ((TypeCodeImpl.digits(paramBigDecimal) > paramTypeCode.fixed_digits()) || (TypeCodeImpl.scale(paramBigDecimal) > paramTypeCode.fixed_scale()))
/*      */       {
/* 1187 */         throw this.wrapper.fixedNotMatch();
/*      */       }
/*      */     }
/*      */     catch (BadKind localBadKind) {
/* 1191 */       throw this.wrapper.fixedBadTypecode(localBadKind);
/*      */     }
/* 1193 */     this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
/* 1194 */     this.object = paramBigDecimal;
/* 1195 */     this.isInitialized = true;
/*      */   }
/*      */ 
/*      */   public BigDecimal extract_fixed() {
/* 1199 */     checkExtractBadOperation(28);
/* 1200 */     return (BigDecimal)this.object;
/*      */   }
/*      */ 
/*      */   public TypeCode createTypeCodeForClass(Class paramClass, ORB paramORB)
/*      */   {
/* 1212 */     TypeCodeImpl localTypeCodeImpl = paramORB.getTypeCodeForClass(paramClass);
/* 1213 */     if (localTypeCodeImpl != null) {
/* 1214 */       return localTypeCodeImpl;
/*      */     }
/*      */ 
/* 1220 */     RepositoryIdStrings localRepositoryIdStrings = RepositoryIdFactory.getRepIdStringsFactory();
/*      */     java.lang.Object localObject1;
/*      */     java.lang.Object localObject2;
/* 1226 */     if (paramClass.isArray())
/*      */     {
/* 1228 */       localObject1 = paramClass.getComponentType();
/*      */ 
/* 1230 */       if (((Class)localObject1).isPrimitive()) {
/* 1231 */         localObject2 = getPrimitiveTypeCodeForClass((Class)localObject1, paramORB);
/*      */       }
/*      */       else {
/* 1234 */         localObject2 = createTypeCodeForClass((Class)localObject1, paramORB);
/*      */       }
/*      */ 
/* 1237 */       TypeCode localTypeCode = paramORB.create_sequence_tc(0, (TypeCode)localObject2);
/*      */ 
/* 1239 */       String str = localRepositoryIdStrings.createForJavaType(paramClass);
/*      */ 
/* 1241 */       return paramORB.create_value_box_tc(str, "Sequence", localTypeCode);
/* 1242 */     }if (paramClass == String.class)
/*      */     {
/* 1244 */       localObject1 = paramORB.create_string_tc(0);
/*      */ 
/* 1246 */       localObject2 = localRepositoryIdStrings.createForJavaType(paramClass);
/*      */ 
/* 1248 */       return paramORB.create_value_box_tc((String)localObject2, "StringValue", (TypeCode)localObject1);
/*      */     }
/*      */ 
/* 1253 */     localTypeCodeImpl = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(paramORB, paramClass, ORBUtility.createValueHandler());
/*      */ 
/* 1256 */     localTypeCodeImpl.setCaching(true);
/*      */ 
/* 1258 */     paramORB.setTypeCodeForClass(paramClass, localTypeCodeImpl);
/* 1259 */     return localTypeCodeImpl;
/*      */   }
/*      */ 
/*      */   private TypeCode getPrimitiveTypeCodeForClass(Class paramClass, ORB paramORB)
/*      */   {
/* 1274 */     if (paramClass == Integer.TYPE)
/* 1275 */       return paramORB.get_primitive_tc(TCKind.tk_long);
/* 1276 */     if (paramClass == Byte.TYPE)
/* 1277 */       return paramORB.get_primitive_tc(TCKind.tk_octet);
/* 1278 */     if (paramClass == Long.TYPE)
/* 1279 */       return paramORB.get_primitive_tc(TCKind.tk_longlong);
/* 1280 */     if (paramClass == Float.TYPE)
/* 1281 */       return paramORB.get_primitive_tc(TCKind.tk_float);
/* 1282 */     if (paramClass == Double.TYPE)
/* 1283 */       return paramORB.get_primitive_tc(TCKind.tk_double);
/* 1284 */     if (paramClass == Short.TYPE)
/* 1285 */       return paramORB.get_primitive_tc(TCKind.tk_short);
/* 1286 */     if (paramClass == Character.TYPE)
/*      */     {
/* 1298 */       if ((ORBVersionFactory.getFOREIGN().compareTo(paramORB.getORBVersion()) == 0) || (ORBVersionFactory.getNEWER().compareTo(paramORB.getORBVersion()) <= 0))
/*      */       {
/* 1300 */         return paramORB.get_primitive_tc(TCKind.tk_wchar);
/*      */       }
/* 1302 */       return paramORB.get_primitive_tc(TCKind.tk_char);
/* 1303 */     }if (paramClass == Boolean.TYPE) {
/* 1304 */       return paramORB.get_primitive_tc(TCKind.tk_boolean);
/*      */     }
/*      */ 
/* 1307 */     return paramORB.get_primitive_tc(TCKind.tk_any);
/*      */   }
/*      */ 
/*      */   public Any extractAny(TypeCode paramTypeCode, ORB paramORB)
/*      */   {
/* 1315 */     Any localAny = paramORB.create_any();
/* 1316 */     org.omg.CORBA.portable.OutputStream localOutputStream = localAny.create_output_stream();
/* 1317 */     TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(this.stream, localOutputStream);
/* 1318 */     localAny.read_value(localOutputStream.create_input_stream(), paramTypeCode);
/* 1319 */     return localAny;
/*      */   }
/*      */ 
/*      */   public static Any extractAnyFromStream(TypeCode paramTypeCode, org.omg.CORBA.portable.InputStream paramInputStream, ORB paramORB)
/*      */   {
/* 1325 */     Any localAny = paramORB.create_any();
/* 1326 */     org.omg.CORBA.portable.OutputStream localOutputStream = localAny.create_output_stream();
/* 1327 */     TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(paramInputStream, localOutputStream);
/* 1328 */     localAny.read_value(localOutputStream.create_input_stream(), paramTypeCode);
/* 1329 */     return localAny;
/*      */   }
/*      */ 
/*      */   public boolean isInitialized()
/*      */   {
/* 1334 */     return this.isInitialized;
/*      */   }
/*      */ 
/*      */   private static final class AnyInputStream extends EncapsInputStream
/*      */   {
/*      */     public AnyInputStream(EncapsInputStream paramEncapsInputStream)
/*      */     {
/*   72 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class AnyOutputStream extends EncapsOutputStream
/*      */   {
/*      */     public AnyOutputStream(ORB paramORB)
/*      */     {
/*   80 */       super();
/*      */     }
/*      */ 
/*      */     public org.omg.CORBA.portable.InputStream create_input_stream()
/*      */     {
/*   85 */       return new AnyImpl.AnyInputStream((EncapsInputStream)super.create_input_stream());
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.corba.AnyImpl
 * JD-Core Version:    0.6.2
 */