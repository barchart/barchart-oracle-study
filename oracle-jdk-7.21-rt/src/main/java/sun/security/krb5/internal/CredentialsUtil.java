/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.KrbTgsReq;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.ServiceName;
/*     */ 
/*     */ public class CredentialsUtil
/*     */ {
/*  54 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */ 
/*     */   public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/*  75 */     ServiceName localServiceName1 = new ServiceName(paramString);
/*  76 */     Object localObject1 = localServiceName1.getRealmString();
/*  77 */     Object localObject2 = paramCredentials.getClient().getRealmString();
/*  78 */     String str1 = Config.getInstance().getDefaultRealm();
/*     */ 
/*  80 */     if (localObject2 == null) {
/*  81 */       localObject3 = null;
/*  82 */       if ((localObject3 = paramCredentials.getServer()) != null)
/*  83 */         localObject2 = ((PrincipalName)localObject3).getRealmString();
/*     */     }
/*  85 */     if (localObject2 == null) {
/*  86 */       localObject2 = str1;
/*     */     }
/*  88 */     if (localObject1 == null) {
/*  89 */       localObject1 = localObject2;
/*  90 */       localServiceName1.setRealm((String)localObject1);
/*     */     }
/*     */ 
/* 111 */     if (((String)localObject2).equals(localObject1))
/*     */     {
/* 113 */       if (DEBUG)
/* 114 */         System.out.println(">>> Credentials acquireServiceCreds: same realm");
/* 115 */       return serviceCreds(localServiceName1, paramCredentials);
/*     */     }
/*     */ 
/* 119 */     Object localObject3 = Realm.getRealmsList((String)localObject2, (String)localObject1);
/* 120 */     int i = 1;
/*     */ 
/* 122 */     if ((localObject3 == null) || (localObject3.length == 0))
/*     */     {
/* 124 */       if (DEBUG)
/* 125 */         System.out.println(">>> Credentials acquireServiceCreds: no realms list");
/* 126 */       return null;
/*     */     }
/*     */ 
/* 129 */     int j = 0; int k = 0;
/* 130 */     Object localObject4 = null; Credentials localCredentials1 = null; Credentials localCredentials2 = null;
/* 131 */     ServiceName localServiceName2 = null;
/* 132 */     Object localObject5 = null; String str2 = null; String str3 = null;
/*     */ 
/* 134 */     localObject4 = paramCredentials; for (j = 0; j < localObject3.length; )
/*     */     {
/* 136 */       localServiceName2 = new ServiceName("krbtgt", (String)localObject1, localObject3[j]);
/*     */ 
/* 139 */       if (DEBUG)
/*     */       {
/* 141 */         System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + j + "] tempService=" + localServiceName2);
/*     */       }
/*     */       try
/*     */       {
/* 145 */         localCredentials1 = serviceCreds(localServiceName2, (Credentials)localObject4);
/*     */       } catch (Exception localException1) {
/* 147 */         localCredentials1 = null;
/*     */       }
/*     */ 
/* 150 */       if (localCredentials1 == null)
/*     */       {
/* 152 */         if (DEBUG)
/*     */         {
/* 154 */           System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching backwards");
/*     */         }
/*     */ 
/* 163 */         localCredentials1 = null; for (k = localObject3.length - 1; 
/* 164 */           (localCredentials1 == null) && (k > j); k--)
/*     */         {
/* 167 */           localServiceName2 = new ServiceName("krbtgt", localObject3[k], localObject3[j]);
/*     */ 
/* 170 */           if (DEBUG)
/*     */           {
/* 172 */             System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + k + "] tempService=" + localServiceName2);
/*     */           }
/*     */           try
/*     */           {
/* 176 */             localCredentials1 = serviceCreds(localServiceName2, (Credentials)localObject4);
/*     */           } catch (Exception localException2) {
/* 178 */             localCredentials1 = null;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 183 */       if (localCredentials1 == null)
/*     */       {
/* 185 */         if (!DEBUG)
/*     */           break;
/* 187 */         System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds"); break;
/*     */       }
/*     */ 
/* 197 */       str2 = localCredentials1.getServer().getInstanceComponent();
/* 198 */       if ((i != 0) && (!localCredentials1.checkDelegate())) {
/* 199 */         if (DEBUG)
/*     */         {
/* 201 */           System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + localCredentials1.getServer());
/*     */         }
/*     */ 
/* 205 */         i = 0;
/*     */       }
/*     */ 
/* 208 */       if (DEBUG)
/*     */       {
/* 210 */         System.out.println(">>> Credentials acquireServiceCreds: got tgt");
/*     */       }
/*     */ 
/* 214 */       if (str2.equals(localObject1))
/*     */       {
/* 217 */         localCredentials2 = localCredentials1;
/* 218 */         str3 = str2;
/* 219 */         break;
/*     */       }
/*     */ 
/* 228 */       for (k = j + 1; k < localObject3.length; k++)
/*     */       {
/* 230 */         if (str2.equals(localObject3[k]))
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */ 
/* 236 */       if (k >= localObject3.length)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 242 */       j = k;
/* 243 */       localObject4 = localCredentials1;
/*     */ 
/* 245 */       if (DEBUG)
/*     */       {
/* 247 */         System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + j);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 265 */     Credentials localCredentials3 = null;
/*     */ 
/* 267 */     if (localCredentials2 != null)
/*     */     {
/* 271 */       if (DEBUG)
/*     */       {
/* 273 */         System.out.println(">>> Credentials acquireServiceCreds: got right tgt");
/*     */ 
/* 277 */         System.out.println(">>> Credentials acquireServiceCreds: obtaining service creds for " + localServiceName1);
/*     */       }
/*     */       try
/*     */       {
/* 281 */         localCredentials3 = serviceCreds(localServiceName1, localCredentials2);
/*     */       } catch (Exception localException3) {
/* 283 */         if (DEBUG)
/* 284 */           System.out.println(localException3);
/* 285 */         localCredentials3 = null;
/*     */       }
/*     */     }
/*     */ 
/* 289 */     if (localCredentials3 != null)
/*     */     {
/* 291 */       if (DEBUG)
/*     */       {
/* 293 */         System.out.println(">>> Credentials acquireServiceCreds: returning creds:");
/* 294 */         Credentials.printDebug(localCredentials3);
/*     */       }
/* 296 */       if (i == 0) {
/* 297 */         localCredentials3.resetDelegate();
/*     */       }
/* 299 */       return localCredentials3;
/*     */     }
/* 301 */     throw new KrbApErrException(63, "No service creds");
/*     */   }
/*     */ 
/*     */   private static Credentials serviceCreds(ServiceName paramServiceName, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 311 */     return new KrbTgsReq(paramCredentials, paramServiceName).sendAndGetCreds();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.CredentialsUtil
 * JD-Core Version:    0.6.2
 */