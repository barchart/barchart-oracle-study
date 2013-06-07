/*    */ package java.lang.invoke;
/*    */ 
/*    */ class DirectMethodHandle extends MethodHandle
/*    */ {
/* 37 */   private final int vmindex = -99;
/*    */ 
/*    */   DirectMethodHandle(MethodType paramMethodType, MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass)
/*    */   {
/* 41 */     super(paramMethodType);
/*    */ 
/* 43 */     assert ((paramMemberName.isMethod()) || ((!paramBoolean) && (paramMemberName.isConstructor())));
/* 44 */     if (!paramMemberName.isResolved()) {
/* 45 */       throw new InternalError();
/*    */     }
/* 47 */     if ((paramMemberName.getDeclaringClass().isInterface()) && (!paramMemberName.isAbstract()))
/*    */     {
/* 49 */       MemberName localMemberName = new MemberName(Object.class, paramMemberName.getName(), paramMemberName.getMethodType(), paramMemberName.getModifiers());
/* 50 */       localMemberName = MemberName.getFactory().resolveOrNull(localMemberName, false, null);
/* 51 */       if ((localMemberName != null) && (localMemberName.isPublic())) {
/* 52 */         paramMemberName = localMemberName;
/*    */       }
/*    */     }
/*    */ 
/* 56 */     MethodHandleNatives.init(this, paramMemberName, paramBoolean, paramClass);
/*    */   }
/*    */ 
/*    */   boolean isValid() {
/* 60 */     return this.vmindex != -99;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.DirectMethodHandle
 * JD-Core Version:    0.6.2
 */