/*      */ package javax.management.openmbean;
/*      */ 
/*      */ import com.sun.jmx.remote.util.EnvHelp;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.management.Descriptor;
/*      */ import javax.management.DescriptorRead;
/*      */ import javax.management.ImmutableDescriptor;
/*      */ import javax.management.MBeanAttributeInfo;
/*      */ 
/*      */ public class OpenMBeanAttributeInfoSupport extends MBeanAttributeInfo
/*      */   implements OpenMBeanAttributeInfo
/*      */ {
/*      */   static final long serialVersionUID = -4867215622149721849L;
/*      */   private OpenType<?> openType;
/*      */   private final Object defaultValue;
/*      */   private final Set<?> legalValues;
/*      */   private final Comparable<?> minValue;
/*      */   private final Comparable<?> maxValue;
/*   91 */   private transient Integer myHashCode = null;
/*   92 */   private transient String myToString = null;
/*      */ 
/*      */   public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/*  126 */     this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null);
/*      */   }
/*      */ 
/*      */   public OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor)
/*      */   {
/*  176 */     super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, paramOpenType == null ? null : paramOpenType.getDescriptor() }));
/*      */ 
/*  187 */     this.openType = paramOpenType;
/*      */ 
/*  189 */     paramDescriptor = getDescriptor();
/*  190 */     this.defaultValue = valueFrom(paramDescriptor, "defaultValue", paramOpenType);
/*  191 */     this.legalValues = valuesFrom(paramDescriptor, "legalValues", paramOpenType);
/*  192 */     this.minValue = comparableValueFrom(paramDescriptor, "minValue", paramOpenType);
/*  193 */     this.maxValue = comparableValueFrom(paramDescriptor, "maxValue", paramOpenType);
/*      */     try
/*      */     {
/*  196 */       check(this);
/*      */     } catch (OpenDataException localOpenDataException) {
/*  198 */       throw new IllegalArgumentException(localOpenDataException.getMessage(), localOpenDataException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT)
/*      */     throws OpenDataException
/*      */   {
/*  249 */     this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, (Object[])null);
/*      */   }
/*      */ 
/*      */   public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT)
/*      */     throws OpenDataException
/*      */   {
/*  319 */     this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, paramArrayOfT, null, null);
/*      */   }
/*      */ 
/*      */   public <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
/*      */     throws OpenDataException
/*      */   {
/*  392 */     this(paramString1, paramString2, paramOpenType, paramBoolean1, paramBoolean2, paramBoolean3, paramT, null, paramComparable1, paramComparable2);
/*      */   }
/*      */ 
/*      */   private <T> OpenMBeanAttributeInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
/*      */     throws OpenDataException
/*      */   {
/*  407 */     super(paramString1, paramOpenType == null ? null : paramOpenType.getClassName(), paramString2, paramBoolean1, paramBoolean2, paramBoolean3, makeDescriptor(paramOpenType, paramT, paramArrayOfT, paramComparable1, paramComparable2));
/*      */ 
/*  416 */     this.openType = paramOpenType;
/*      */ 
/*  418 */     Descriptor localDescriptor = getDescriptor();
/*  419 */     this.defaultValue = paramT;
/*  420 */     this.minValue = paramComparable1;
/*  421 */     this.maxValue = paramComparable2;
/*      */ 
/*  424 */     this.legalValues = ((Set)localDescriptor.getFieldValue("legalValues"));
/*      */ 
/*  426 */     check(this);
/*      */   }
/*      */ 
/*      */   private Object readResolve()
/*      */   {
/*  438 */     if (getDescriptor().getFieldNames().length == 0) {
/*  439 */       OpenType localOpenType = (OpenType)cast(this.openType);
/*  440 */       Set localSet = (Set)cast(this.legalValues);
/*  441 */       Comparable localComparable1 = (Comparable)cast(this.minValue);
/*  442 */       Comparable localComparable2 = (Comparable)cast(this.maxValue);
/*  443 */       return new OpenMBeanAttributeInfoSupport(this.name, this.description, this.openType, isReadable(), isWritable(), isIs(), makeDescriptor(localOpenType, this.defaultValue, localSet, localComparable1, localComparable2));
/*      */     }
/*      */ 
/*  449 */     return this;
/*      */   }
/*      */ 
/*      */   static void check(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) throws OpenDataException {
/*  453 */     OpenType localOpenType = paramOpenMBeanParameterInfo.getOpenType();
/*  454 */     if (localOpenType == null) {
/*  455 */       throw new IllegalArgumentException("OpenType cannot be null");
/*      */     }
/*  457 */     if ((paramOpenMBeanParameterInfo.getName() == null) || (paramOpenMBeanParameterInfo.getName().trim().equals("")))
/*      */     {
/*  459 */       throw new IllegalArgumentException("Name cannot be null or empty");
/*      */     }
/*  461 */     if ((paramOpenMBeanParameterInfo.getDescription() == null) || (paramOpenMBeanParameterInfo.getDescription().trim().equals("")))
/*      */     {
/*  463 */       throw new IllegalArgumentException("Description cannot be null or empty");
/*      */     }
/*      */     Object localObject1;
/*  467 */     if (paramOpenMBeanParameterInfo.hasDefaultValue())
/*      */     {
/*  470 */       if ((localOpenType.isArray()) || ((localOpenType instanceof TabularType))) {
/*  471 */         throw new OpenDataException("Default value not supported for ArrayType and TabularType");
/*      */       }
/*      */ 
/*  475 */       if (!localOpenType.isValue(paramOpenMBeanParameterInfo.getDefaultValue())) {
/*  476 */         localObject1 = "Argument defaultValue's class [\"" + paramOpenMBeanParameterInfo.getDefaultValue().getClass().getName() + "\"] does not match the one defined in openType[\"" + localOpenType.getClassName() + "\"]";
/*      */ 
/*  481 */         throw new OpenDataException((String)localObject1);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  487 */     if ((paramOpenMBeanParameterInfo.hasLegalValues()) && ((paramOpenMBeanParameterInfo.hasMinValue()) || (paramOpenMBeanParameterInfo.hasMaxValue())))
/*      */     {
/*  489 */       throw new OpenDataException("cannot have both legalValue and minValue or maxValue");
/*      */     }
/*      */ 
/*  494 */     if ((paramOpenMBeanParameterInfo.hasMinValue()) && (!localOpenType.isValue(paramOpenMBeanParameterInfo.getMinValue()))) {
/*  495 */       localObject1 = "Type of minValue [" + paramOpenMBeanParameterInfo.getMinValue().getClass().getName() + "] does not match OpenType [" + localOpenType.getClassName() + "]";
/*      */ 
/*  498 */       throw new OpenDataException((String)localObject1);
/*      */     }
/*  500 */     if ((paramOpenMBeanParameterInfo.hasMaxValue()) && (!localOpenType.isValue(paramOpenMBeanParameterInfo.getMaxValue()))) {
/*  501 */       localObject1 = "Type of maxValue [" + paramOpenMBeanParameterInfo.getMaxValue().getClass().getName() + "] does not match OpenType [" + localOpenType.getClassName() + "]";
/*      */ 
/*  504 */       throw new OpenDataException((String)localObject1);
/*      */     }
/*      */ 
/*  509 */     if (paramOpenMBeanParameterInfo.hasDefaultValue()) {
/*  510 */       localObject1 = paramOpenMBeanParameterInfo.getDefaultValue();
/*  511 */       if ((paramOpenMBeanParameterInfo.hasLegalValues()) && (!paramOpenMBeanParameterInfo.getLegalValues().contains(localObject1)))
/*      */       {
/*  513 */         throw new OpenDataException("defaultValue is not contained in legalValues");
/*      */       }
/*      */ 
/*  519 */       if ((paramOpenMBeanParameterInfo.hasMinValue()) && 
/*  520 */         (compare(paramOpenMBeanParameterInfo.getMinValue(), localObject1) > 0)) {
/*  521 */         throw new OpenDataException("minValue cannot be greater than defaultValue");
/*      */       }
/*      */ 
/*  525 */       if ((paramOpenMBeanParameterInfo.hasMaxValue()) && 
/*  526 */         (compare(paramOpenMBeanParameterInfo.getMaxValue(), localObject1) < 0)) {
/*  527 */         throw new OpenDataException("maxValue cannot be less than defaultValue");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  535 */     if (paramOpenMBeanParameterInfo.hasLegalValues())
/*      */     {
/*  537 */       if (((localOpenType instanceof TabularType)) || (localOpenType.isArray())) {
/*  538 */         throw new OpenDataException("Legal values not supported for TabularType and arrays");
/*      */       }
/*      */ 
/*  542 */       for (localObject1 = paramOpenMBeanParameterInfo.getLegalValues().iterator(); ((Iterator)localObject1).hasNext(); ) { Object localObject2 = ((Iterator)localObject1).next();
/*  543 */         if (!localOpenType.isValue(localObject2)) {
/*  544 */           String str = "Element of legalValues [" + localObject2 + "] is not a valid value for the specified openType [" + localOpenType.toString() + "]";
/*      */ 
/*  548 */           throw new OpenDataException(str);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  556 */     if ((paramOpenMBeanParameterInfo.hasMinValue()) && (paramOpenMBeanParameterInfo.hasMaxValue()) && 
/*  557 */       (compare(paramOpenMBeanParameterInfo.getMinValue(), paramOpenMBeanParameterInfo.getMaxValue()) > 0))
/*  558 */       throw new OpenDataException("minValue cannot be greater than maxValue");
/*      */   }
/*      */ 
/*      */   static int compare(Object paramObject1, Object paramObject2)
/*      */   {
/*  567 */     return ((Comparable)paramObject1).compareTo(paramObject2);
/*      */   }
/*      */ 
/*      */   static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
/*      */   {
/*  575 */     HashMap localHashMap = new HashMap();
/*  576 */     if (paramT != null)
/*  577 */       localHashMap.put("defaultValue", paramT);
/*  578 */     if (paramArrayOfT != null) {
/*  579 */       Object localObject = new HashSet();
/*  580 */       for (T ? : paramArrayOfT)
/*  581 */         ((Set)localObject).add(?);
/*  582 */       localObject = Collections.unmodifiableSet((Set)localObject);
/*  583 */       localHashMap.put("legalValues", localObject);
/*      */     }
/*  585 */     if (paramComparable1 != null)
/*  586 */       localHashMap.put("minValue", paramComparable1);
/*  587 */     if (paramComparable2 != null)
/*  588 */       localHashMap.put("maxValue", paramComparable2);
/*  589 */     if (localHashMap.isEmpty()) {
/*  590 */       return paramOpenType.getDescriptor();
/*      */     }
/*  592 */     localHashMap.put("openType", paramOpenType);
/*  593 */     return new ImmutableDescriptor(localHashMap);
/*      */   }
/*      */ 
/*      */   static <T> Descriptor makeDescriptor(OpenType<T> paramOpenType, T paramT, Set<T> paramSet, Comparable<T> paramComparable1, Comparable<T> paramComparable2)
/*      */   {
/*      */     Object[] arrayOfObject;
/*  603 */     if (paramSet == null) {
/*  604 */       arrayOfObject = null;
/*      */     } else {
/*  606 */       arrayOfObject = (Object[])cast(new Object[paramSet.size()]);
/*  607 */       paramSet.toArray(arrayOfObject);
/*      */     }
/*  609 */     return makeDescriptor(paramOpenType, paramT, arrayOfObject, paramComparable1, paramComparable2);
/*      */   }
/*      */ 
/*      */   static <T> T valueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
/*      */   {
/*  614 */     Object localObject = paramDescriptor.getFieldValue(paramString);
/*  615 */     if (localObject == null)
/*  616 */       return null;
/*      */     try {
/*  618 */       return convertFrom(localObject, paramOpenType);
/*      */     } catch (Exception localException) {
/*  620 */       String str = "Cannot convert descriptor field " + paramString + "  to " + paramOpenType.getTypeName();
/*      */ 
/*  623 */       throw ((IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(str), localException));
/*      */     }
/*      */   }
/*      */ 
/*      */   static <T> Set<T> valuesFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
/*      */   {
/*  629 */     Object localObject1 = paramDescriptor.getFieldValue(paramString);
/*  630 */     if (localObject1 == null)
/*  631 */       return null;
/*      */     Object localObject4;
/*      */     Object localObject2;
/*  633 */     if ((localObject1 instanceof Set)) {
/*  634 */       localObject3 = (Set)localObject1;
/*  635 */       int i = 1;
/*  636 */       for (localObject4 = ((Set)localObject3).iterator(); ((Iterator)localObject4).hasNext(); ) { Object localObject5 = ((Iterator)localObject4).next();
/*  637 */         if (!paramOpenType.isValue(localObject5)) {
/*  638 */           i = 0;
/*  639 */           break;
/*      */         }
/*      */       }
/*  642 */       if (i != 0)
/*  643 */         return (Set)cast(localObject3);
/*  644 */       localObject2 = localObject3;
/*  645 */     } else if ((localObject1 instanceof Object[])) {
/*  646 */       localObject2 = Arrays.asList((Object[])localObject1);
/*      */     } else {
/*  648 */       localObject3 = "Descriptor value for " + paramString + " must be a Set or " + "an array: " + localObject1.getClass().getName();
/*      */ 
/*  651 */       throw new IllegalArgumentException((String)localObject3);
/*      */     }
/*      */ 
/*  654 */     Object localObject3 = new HashSet();
/*  655 */     for (Iterator localIterator = ((Collection)localObject2).iterator(); localIterator.hasNext(); ) { localObject4 = localIterator.next();
/*  656 */       ((Set)localObject3).add(convertFrom(localObject4, paramOpenType)); }
/*  657 */     return localObject3;
/*      */   }
/*      */ 
/*      */   static <T> Comparable<?> comparableValueFrom(Descriptor paramDescriptor, String paramString, OpenType<T> paramOpenType)
/*      */   {
/*  662 */     Object localObject = valueFrom(paramDescriptor, paramString, paramOpenType);
/*  663 */     if ((localObject == null) || ((localObject instanceof Comparable)))
/*  664 */       return (Comparable)localObject;
/*  665 */     String str = "Descriptor field " + paramString + " with value " + localObject + " is not Comparable";
/*      */ 
/*  668 */     throw new IllegalArgumentException(str);
/*      */   }
/*      */ 
/*      */   private static <T> T convertFrom(Object paramObject, OpenType<T> paramOpenType) {
/*  672 */     if (paramOpenType.isValue(paramObject)) {
/*  673 */       Object localObject = cast(paramObject);
/*  674 */       return localObject;
/*      */     }
/*  676 */     return convertFromStrings(paramObject, paramOpenType);
/*      */   }
/*      */ 
/*      */   private static <T> T convertFromStrings(Object paramObject, OpenType<T> paramOpenType) {
/*  680 */     if ((paramOpenType instanceof ArrayType))
/*  681 */       return convertFromStringArray(paramObject, paramOpenType);
/*  682 */     if ((paramObject instanceof String))
/*  683 */       return convertFromString((String)paramObject, paramOpenType);
/*  684 */     String str = "Cannot convert value " + paramObject + " of type " + paramObject.getClass().getName() + " to type " + paramOpenType.getTypeName();
/*      */ 
/*  687 */     throw new IllegalArgumentException(str);
/*      */   }
/*      */ 
/*      */   private static <T> T convertFromString(String paramString, OpenType<T> paramOpenType) {
/*      */     Class localClass;
/*      */     try {
/*  693 */       localClass = (Class)cast(Class.forName(paramOpenType.safeGetClassName()));
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/*  695 */       throw new NoClassDefFoundError(localClassNotFoundException.toString());
/*      */     }
/*      */ 
/*      */     Method localMethod;
/*      */     try
/*      */     {
/*  701 */       localMethod = localClass.getMethod("valueOf", new Class[] { String.class });
/*  702 */       if ((!Modifier.isStatic(localMethod.getModifiers())) || (localMethod.getReturnType() != localClass))
/*      */       {
/*  704 */         localMethod = null;
/*      */       }
/*      */     } catch (NoSuchMethodException localNoSuchMethodException1) { localMethod = null; }
/*      */ 
/*  708 */     if (localMethod != null) {
/*      */       try {
/*  710 */         return localClass.cast(localMethod.invoke(null, new Object[] { paramString }));
/*      */       } catch (Exception localException1) {
/*  712 */         String str1 = "Could not convert \"" + paramString + "\" using method: " + localMethod;
/*      */ 
/*  714 */         throw new IllegalArgumentException(str1, localException1);
/*      */       }
/*      */     }
/*      */ 
/*      */     Constructor localConstructor;
/*      */     try
/*      */     {
/*  721 */       localConstructor = localClass.getConstructor(new Class[] { String.class });
/*      */     } catch (NoSuchMethodException localNoSuchMethodException2) {
/*  723 */       localConstructor = null;
/*      */     }
/*  725 */     if (localConstructor != null) {
/*      */       try {
/*  727 */         return localConstructor.newInstance(new Object[] { paramString });
/*      */       } catch (Exception localException2) {
/*  729 */         String str2 = "Could not convert \"" + paramString + "\" using constructor: " + localConstructor;
/*      */ 
/*  731 */         throw new IllegalArgumentException(str2, localException2);
/*      */       }
/*      */     }
/*      */ 
/*  735 */     throw new IllegalArgumentException("Don't know how to convert string to " + paramOpenType.getTypeName());
/*      */   }
/*      */ 
/*      */   private static <T> T convertFromStringArray(Object paramObject, OpenType<T> paramOpenType)
/*      */   {
/*  748 */     ArrayType localArrayType = (ArrayType)paramOpenType;
/*  749 */     OpenType localOpenType = localArrayType.getElementOpenType();
/*  750 */     int i = localArrayType.getDimension();
/*  751 */     String str = "[";
/*  752 */     for (int j = 1; j < i; j++)
/*  753 */       str = str + "["; Class localClass1;
/*      */     Class localClass2;
/*      */     try {
/*  757 */       localClass1 = Class.forName(str + "Ljava.lang.String;");
/*      */ 
/*  759 */       localClass2 = Class.forName(str + "L" + localOpenType.safeGetClassName() + ";");
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException)
/*      */     {
/*  763 */       throw new NoClassDefFoundError(localClassNotFoundException.toString());
/*      */     }
/*      */     Object localObject1;
/*  765 */     if (!localClass1.isInstance(paramObject)) {
/*  766 */       localObject1 = "Value for " + i + "-dimensional array of " + localOpenType.getTypeName() + " must be same type or a String " + "array with same dimensions";
/*      */ 
/*  770 */       throw new IllegalArgumentException((String)localObject1);
/*      */     }
/*      */ 
/*  773 */     if (i == 1)
/*  774 */       localObject1 = localOpenType;
/*      */     else {
/*      */       try {
/*  777 */         localObject1 = new ArrayType(i - 1, localOpenType);
/*      */       } catch (OpenDataException localOpenDataException) {
/*  779 */         throw new IllegalArgumentException(localOpenDataException.getMessage(), localOpenDataException);
/*      */       }
/*      */     }
/*      */ 
/*  783 */     int k = Array.getLength(paramObject);
/*  784 */     Object[] arrayOfObject = (Object[])Array.newInstance(localClass2.getComponentType(), k);
/*      */ 
/*  786 */     for (int m = 0; m < k; m++) {
/*  787 */       Object localObject2 = Array.get(paramObject, m);
/*  788 */       Object localObject3 = convertFromStrings(localObject2, (OpenType)localObject1);
/*      */ 
/*  790 */       Array.set(arrayOfObject, m, localObject3);
/*      */     }
/*  792 */     return cast(arrayOfObject);
/*      */   }
/*      */ 
/*      */   static <T> T cast(Object paramObject)
/*      */   {
/*  797 */     return paramObject;
/*      */   }
/*      */ 
/*      */   public OpenType<?> getOpenType()
/*      */   {
/*  805 */     return this.openType;
/*      */   }
/*      */ 
/*      */   public Object getDefaultValue()
/*      */   {
/*  821 */     return this.defaultValue;
/*      */   }
/*      */ 
/*      */   public Set<?> getLegalValues()
/*      */   {
/*  838 */     return this.legalValues;
/*      */   }
/*      */ 
/*      */   public Comparable<?> getMinValue()
/*      */   {
/*  851 */     return this.minValue;
/*      */   }
/*      */ 
/*      */   public Comparable<?> getMaxValue()
/*      */   {
/*  864 */     return this.maxValue;
/*      */   }
/*      */ 
/*      */   public boolean hasDefaultValue()
/*      */   {
/*  875 */     return this.defaultValue != null;
/*      */   }
/*      */ 
/*      */   public boolean hasLegalValues()
/*      */   {
/*  886 */     return this.legalValues != null;
/*      */   }
/*      */ 
/*      */   public boolean hasMinValue()
/*      */   {
/*  897 */     return this.minValue != null;
/*      */   }
/*      */ 
/*      */   public boolean hasMaxValue()
/*      */   {
/*  908 */     return this.maxValue != null;
/*      */   }
/*      */ 
/*      */   public boolean isValue(Object paramObject)
/*      */   {
/*  925 */     return isValue(this, paramObject);
/*      */   }
/*      */ 
/*      */   static boolean isValue(OpenMBeanParameterInfo paramOpenMBeanParameterInfo, Object paramObject)
/*      */   {
/*  930 */     if ((paramOpenMBeanParameterInfo.hasDefaultValue()) && (paramObject == null))
/*  931 */       return true;
/*  932 */     return (paramOpenMBeanParameterInfo.getOpenType().isValue(paramObject)) && ((!paramOpenMBeanParameterInfo.hasLegalValues()) || (paramOpenMBeanParameterInfo.getLegalValues().contains(paramObject))) && ((!paramOpenMBeanParameterInfo.hasMinValue()) || (paramOpenMBeanParameterInfo.getMinValue().compareTo(paramObject) <= 0)) && ((!paramOpenMBeanParameterInfo.hasMaxValue()) || (paramOpenMBeanParameterInfo.getMaxValue().compareTo(paramObject) >= 0));
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  972 */     if (!(paramObject instanceof OpenMBeanAttributeInfo)) {
/*  973 */       return false;
/*      */     }
/*  975 */     OpenMBeanAttributeInfo localOpenMBeanAttributeInfo = (OpenMBeanAttributeInfo)paramObject;
/*      */ 
/*  977 */     return (isReadable() == localOpenMBeanAttributeInfo.isReadable()) && (isWritable() == localOpenMBeanAttributeInfo.isWritable()) && (isIs() == localOpenMBeanAttributeInfo.isIs()) && (equal(this, localOpenMBeanAttributeInfo));
/*      */   }
/*      */ 
/*      */   static boolean equal(OpenMBeanParameterInfo paramOpenMBeanParameterInfo1, OpenMBeanParameterInfo paramOpenMBeanParameterInfo2)
/*      */   {
/*  985 */     if ((paramOpenMBeanParameterInfo1 instanceof DescriptorRead)) {
/*  986 */       if (!(paramOpenMBeanParameterInfo2 instanceof DescriptorRead))
/*  987 */         return false;
/*  988 */       Descriptor localDescriptor1 = ((DescriptorRead)paramOpenMBeanParameterInfo1).getDescriptor();
/*  989 */       Descriptor localDescriptor2 = ((DescriptorRead)paramOpenMBeanParameterInfo2).getDescriptor();
/*  990 */       if (!localDescriptor1.equals(localDescriptor2))
/*  991 */         return false;
/*  992 */     } else if ((paramOpenMBeanParameterInfo2 instanceof DescriptorRead)) {
/*  993 */       return false;
/*      */     }
/*  995 */     return (paramOpenMBeanParameterInfo1.getName().equals(paramOpenMBeanParameterInfo2.getName())) && (paramOpenMBeanParameterInfo1.getOpenType().equals(paramOpenMBeanParameterInfo2.getOpenType())) && (paramOpenMBeanParameterInfo1.hasDefaultValue() ? paramOpenMBeanParameterInfo1.getDefaultValue().equals(paramOpenMBeanParameterInfo2.getDefaultValue()) : !paramOpenMBeanParameterInfo2.hasDefaultValue()) && (paramOpenMBeanParameterInfo1.hasMinValue() ? paramOpenMBeanParameterInfo1.getMinValue().equals(paramOpenMBeanParameterInfo2.getMinValue()) : !paramOpenMBeanParameterInfo2.hasMinValue()) && (paramOpenMBeanParameterInfo1.hasMaxValue() ? paramOpenMBeanParameterInfo1.getMaxValue().equals(paramOpenMBeanParameterInfo2.getMaxValue()) : !paramOpenMBeanParameterInfo2.hasMaxValue()) && (paramOpenMBeanParameterInfo1.hasLegalValues() ? paramOpenMBeanParameterInfo1.getLegalValues().equals(paramOpenMBeanParameterInfo2.getLegalValues()) : !paramOpenMBeanParameterInfo2.hasLegalValues());
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1047 */     if (this.myHashCode == null) {
/* 1048 */       this.myHashCode = Integer.valueOf(hashCode(this));
/*      */     }
/*      */ 
/* 1052 */     return this.myHashCode.intValue();
/*      */   }
/*      */ 
/*      */   static int hashCode(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) {
/* 1056 */     int i = 0;
/* 1057 */     i += paramOpenMBeanParameterInfo.getName().hashCode();
/* 1058 */     i += paramOpenMBeanParameterInfo.getOpenType().hashCode();
/* 1059 */     if (paramOpenMBeanParameterInfo.hasDefaultValue())
/* 1060 */       i += paramOpenMBeanParameterInfo.getDefaultValue().hashCode();
/* 1061 */     if (paramOpenMBeanParameterInfo.hasMinValue())
/* 1062 */       i += paramOpenMBeanParameterInfo.getMinValue().hashCode();
/* 1063 */     if (paramOpenMBeanParameterInfo.hasMaxValue())
/* 1064 */       i += paramOpenMBeanParameterInfo.getMaxValue().hashCode();
/* 1065 */     if (paramOpenMBeanParameterInfo.hasLegalValues())
/* 1066 */       i += paramOpenMBeanParameterInfo.getLegalValues().hashCode();
/* 1067 */     if ((paramOpenMBeanParameterInfo instanceof DescriptorRead))
/* 1068 */       i += ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor().hashCode();
/* 1069 */     return i;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1096 */     if (this.myToString == null) {
/* 1097 */       this.myToString = toString(this);
/*      */     }
/*      */ 
/* 1102 */     return this.myToString;
/*      */   }
/*      */ 
/*      */   static String toString(OpenMBeanParameterInfo paramOpenMBeanParameterInfo) {
/* 1106 */     Object localObject = (paramOpenMBeanParameterInfo instanceof DescriptorRead) ? ((DescriptorRead)paramOpenMBeanParameterInfo).getDescriptor() : null;
/*      */ 
/* 1108 */     return paramOpenMBeanParameterInfo.getClass().getName() + "(name=" + paramOpenMBeanParameterInfo.getName() + ",openType=" + paramOpenMBeanParameterInfo.getOpenType() + ",default=" + paramOpenMBeanParameterInfo.getDefaultValue() + ",minValue=" + paramOpenMBeanParameterInfo.getMinValue() + ",maxValue=" + paramOpenMBeanParameterInfo.getMaxValue() + ",legalValues=" + paramOpenMBeanParameterInfo.getLegalValues() + (localObject == null ? "" : new StringBuilder().append(",descriptor=").append(localObject).toString()) + ")";
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.openmbean.OpenMBeanAttributeInfoSupport
 * JD-Core Version:    0.6.2
 */