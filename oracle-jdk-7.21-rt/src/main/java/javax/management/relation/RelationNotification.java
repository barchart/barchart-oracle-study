/*     */ package javax.management.relation;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.GetPropertyAction;
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectInputStream.GetField;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.ObjectOutputStream.PutField;
/*     */ import java.io.ObjectStreamField;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.management.Notification;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public class RelationNotification extends Notification
/*     */ {
/*     */   private static final long oldSerialVersionUID = -2126464566505527147L;
/*     */   private static final long newSerialVersionUID = -6871117877523310399L;
/*  73 */   private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myNewRoleValue", ArrayList.class), new ObjectStreamField("myOldRoleValue", ArrayList.class), new ObjectStreamField("myRelId", String.class), new ObjectStreamField("myRelObjName", ObjectName.class), new ObjectStreamField("myRelTypeName", String.class), new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myUnregMBeanList", ArrayList.class) };
/*     */ 
/*  85 */   private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("newRoleValue", List.class), new ObjectStreamField("oldRoleValue", List.class), new ObjectStreamField("relationId", String.class), new ObjectStreamField("relationObjName", ObjectName.class), new ObjectStreamField("relationTypeName", String.class), new ObjectStreamField("roleName", String.class), new ObjectStreamField("unregisterMBeanList", List.class) };
/*     */   private static final long serialVersionUID;
/*     */   private static final ObjectStreamField[] serialPersistentFields;
/* 116 */   private static boolean compat = false;
/*     */   public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
/*     */   public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
/*     */   public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
/*     */   public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
/*     */   public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
/*     */   public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
/* 172 */   private String relationId = null;
/*     */ 
/* 177 */   private String relationTypeName = null;
/*     */ 
/* 183 */   private ObjectName relationObjName = null;
/*     */ 
/* 189 */   private List<ObjectName> unregisterMBeanList = null;
/*     */ 
/* 194 */   private String roleName = null;
/*     */ 
/* 199 */   private List<ObjectName> oldRoleValue = null;
/*     */ 
/* 204 */   private List<ObjectName> newRoleValue = null;
/*     */ 
/*     */   public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, List<ObjectName> paramList)
/*     */     throws IllegalArgumentException
/*     */   {
/* 257 */     super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
/*     */ 
/* 260 */     initMembers(1, paramString1, paramObject, paramLong1, paramLong2, paramString2, paramString3, paramString4, paramObjectName, paramList, null, null, null);
/*     */   }
/*     */ 
/*     */   public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, String paramString5, List<ObjectName> paramList1, List<ObjectName> paramList2)
/*     */     throws IllegalArgumentException
/*     */   {
/* 314 */     super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
/*     */ 
/* 317 */     initMembers(2, paramString1, paramObject, paramLong1, paramLong2, paramString2, paramString3, paramString4, paramObjectName, null, paramString5, paramList1, paramList2);
/*     */   }
/*     */ 
/*     */   public String getRelationId()
/*     */   {
/* 343 */     return this.relationId;
/*     */   }
/*     */ 
/*     */   public String getRelationTypeName()
/*     */   {
/* 352 */     return this.relationTypeName;
/*     */   }
/*     */ 
/*     */   public ObjectName getObjectName()
/*     */   {
/* 362 */     return this.relationObjName;
/*     */   }
/*     */ 
/*     */   public List<ObjectName> getMBeansToUnregister()
/*     */   {
/*     */     Object localObject;
/* 373 */     if (this.unregisterMBeanList != null)
/* 374 */       localObject = new ArrayList(this.unregisterMBeanList);
/*     */     else {
/* 376 */       localObject = Collections.emptyList();
/*     */     }
/* 378 */     return localObject;
/*     */   }
/*     */ 
/*     */   public String getRoleName()
/*     */   {
/* 387 */     String str = null;
/* 388 */     if (this.roleName != null) {
/* 389 */       str = this.roleName;
/*     */     }
/* 391 */     return str;
/*     */   }
/*     */ 
/*     */   public List<ObjectName> getOldRoleValue()
/*     */   {
/*     */     Object localObject;
/* 401 */     if (this.oldRoleValue != null)
/* 402 */       localObject = new ArrayList(this.oldRoleValue);
/*     */     else {
/* 404 */       localObject = Collections.emptyList();
/*     */     }
/* 406 */     return localObject;
/*     */   }
/*     */ 
/*     */   public List<ObjectName> getNewRoleValue()
/*     */   {
/*     */     Object localObject;
/* 416 */     if (this.newRoleValue != null)
/* 417 */       localObject = new ArrayList(this.newRoleValue);
/*     */     else {
/* 419 */       localObject = Collections.emptyList();
/*     */     }
/* 421 */     return localObject;
/*     */   }
/*     */ 
/*     */   private void initMembers(int paramInt, String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, List<ObjectName> paramList1, String paramString5, List<ObjectName> paramList2, List<ObjectName> paramList3)
/*     */     throws IllegalArgumentException
/*     */   {
/* 481 */     int i = 0;
/*     */ 
/* 483 */     if ((paramString1 == null) || (paramObject == null) || ((!(paramObject instanceof RelationService)) && (!(paramObject instanceof ObjectName))) || (paramString3 == null) || (paramString4 == null))
/*     */     {
/* 490 */       i = 1;
/*     */     }
/*     */ 
/* 493 */     if (paramInt == 1)
/*     */     {
/* 495 */       if ((!paramString1.equals("jmx.relation.creation.basic")) && (!paramString1.equals("jmx.relation.creation.mbean")) && (!paramString1.equals("jmx.relation.removal.basic")) && (!paramString1.equals("jmx.relation.removal.mbean")))
/*     */       {
/* 505 */         i = 1;
/*     */       }
/*     */     }
/* 508 */     else if (paramInt == 2)
/*     */     {
/* 510 */       if (((!paramString1.equals("jmx.relation.update.basic")) && (!paramString1.equals("jmx.relation.update.mbean"))) || (paramString5 == null) || (paramList3 == null) || (paramList2 == null))
/*     */       {
/* 518 */         i = 1;
/*     */       }
/*     */     }
/*     */ 
/* 522 */     if (i != 0) {
/* 523 */       String str = "Invalid parameter.";
/* 524 */       throw new IllegalArgumentException(str);
/*     */     }
/*     */ 
/* 527 */     this.relationId = paramString3;
/* 528 */     this.relationTypeName = paramString4;
/* 529 */     this.relationObjName = paramObjectName;
/* 530 */     if (paramList1 != null) {
/* 531 */       this.unregisterMBeanList = new ArrayList(paramList1);
/*     */     }
/* 533 */     if (paramString5 != null) {
/* 534 */       this.roleName = paramString5;
/*     */     }
/* 536 */     if (paramList3 != null) {
/* 537 */       this.oldRoleValue = new ArrayList(paramList3);
/*     */     }
/* 539 */     if (paramList2 != null)
/* 540 */       this.newRoleValue = new ArrayList(paramList2);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 550 */     if (compat)
/*     */     {
/* 554 */       ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
/* 555 */       this.newRoleValue = ((List)Util.cast(localGetField.get("myNewRoleValue", null)));
/* 556 */       if (localGetField.defaulted("myNewRoleValue"))
/*     */       {
/* 558 */         throw new NullPointerException("newRoleValue");
/*     */       }
/* 560 */       this.oldRoleValue = ((List)Util.cast(localGetField.get("myOldRoleValue", null)));
/* 561 */       if (localGetField.defaulted("myOldRoleValue"))
/*     */       {
/* 563 */         throw new NullPointerException("oldRoleValue");
/*     */       }
/* 565 */       this.relationId = ((String)localGetField.get("myRelId", null));
/* 566 */       if (localGetField.defaulted("myRelId"))
/*     */       {
/* 568 */         throw new NullPointerException("relationId");
/*     */       }
/* 570 */       this.relationObjName = ((ObjectName)localGetField.get("myRelObjName", null));
/* 571 */       if (localGetField.defaulted("myRelObjName"))
/*     */       {
/* 573 */         throw new NullPointerException("relationObjName");
/*     */       }
/* 575 */       this.relationTypeName = ((String)localGetField.get("myRelTypeName", null));
/* 576 */       if (localGetField.defaulted("myRelTypeName"))
/*     */       {
/* 578 */         throw new NullPointerException("relationTypeName");
/*     */       }
/* 580 */       this.roleName = ((String)localGetField.get("myRoleName", null));
/* 581 */       if (localGetField.defaulted("myRoleName"))
/*     */       {
/* 583 */         throw new NullPointerException("roleName");
/*     */       }
/* 585 */       this.unregisterMBeanList = ((List)Util.cast(localGetField.get("myUnregMBeanList", null)));
/* 586 */       if (localGetField.defaulted("myUnregMBeanList"))
/*     */       {
/* 588 */         throw new NullPointerException("unregisterMBeanList");
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 595 */       paramObjectInputStream.defaultReadObject();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 605 */     if (compat)
/*     */     {
/* 609 */       ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
/* 610 */       localPutField.put("myNewRoleValue", this.newRoleValue);
/* 611 */       localPutField.put("myOldRoleValue", this.oldRoleValue);
/* 612 */       localPutField.put("myRelId", this.relationId);
/* 613 */       localPutField.put("myRelObjName", this.relationObjName);
/* 614 */       localPutField.put("myRelTypeName", this.relationTypeName);
/* 615 */       localPutField.put("myRoleName", this.roleName);
/* 616 */       localPutField.put("myUnregMBeanList", this.unregisterMBeanList);
/* 617 */       paramObjectOutputStream.writeFields();
/*     */     }
/*     */     else
/*     */     {
/* 623 */       paramObjectOutputStream.defaultWriteObject();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 119 */       GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
/* 120 */       String str = (String)AccessController.doPrivileged(localGetPropertyAction);
/* 121 */       compat = (str != null) && (str.equals("1.0"));
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 125 */     if (compat) {
/* 126 */       serialPersistentFields = oldSerialPersistentFields;
/* 127 */       serialVersionUID = -2126464566505527147L;
/*     */     } else {
/* 129 */       serialPersistentFields = newSerialPersistentFields;
/* 130 */       serialVersionUID = -6871117877523310399L;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.relation.RelationNotification
 * JD-Core Version:    0.6.2
 */