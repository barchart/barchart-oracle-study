/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.cert.CRLReason;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.Extension;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.URIName;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ public final class OCSP
/*     */ {
/*  65 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   private static final int CONNECT_TIMEOUT = 15000;
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/*  88 */     CertId localCertId = null;
/*  89 */     URI localURI = null;
/*     */     try {
/*  91 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate1);
/*  92 */       localURI = getResponderURI(localX509CertImpl);
/*  93 */       if (localURI == null) {
/*  94 */         throw new CertPathValidatorException("No OCSP Responder URI in certificate");
/*     */       }
/*     */ 
/*  97 */       localCertId = new CertId(paramX509Certificate2, localX509CertImpl.getSerialNumberObject());
/*     */     } catch (CertificateException localCertificateException) {
/*  99 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localCertificateException);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 102 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localIOException);
/*     */     }
/*     */ 
/* 105 */     OCSPResponse localOCSPResponse = check(Collections.singletonList(localCertId), localURI, Collections.singletonList(paramX509Certificate2), null);
/*     */ 
/* 107 */     return localOCSPResponse.getSingleResponse(localCertId);
/*     */   }
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 130 */     return check(paramX509Certificate1, paramX509Certificate2, paramURI, Collections.singletonList(paramX509Certificate3), paramDate);
/*     */   }
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, List<X509Certificate> paramList, Date paramDate)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 154 */     CertId localCertId = null;
/*     */     try {
/* 156 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate1);
/* 157 */       localCertId = new CertId(paramX509Certificate2, localX509CertImpl.getSerialNumberObject());
/*     */     } catch (CertificateException localCertificateException) {
/* 159 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localCertificateException);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 162 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localIOException);
/*     */     }
/*     */ 
/* 165 */     OCSPResponse localOCSPResponse = check(Collections.singletonList(localCertId), paramURI, paramList, paramDate);
/*     */ 
/* 167 */     return localOCSPResponse.getSingleResponse(localCertId);
/*     */   }
/*     */ 
/*     */   static OCSPResponse check(List<CertId> paramList, URI paramURI, List<X509Certificate> paramList1, Date paramDate)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 188 */     byte[] arrayOfByte1 = null;
/*     */     try {
/* 190 */       OCSPRequest localOCSPRequest = new OCSPRequest(paramList);
/* 191 */       arrayOfByte1 = localOCSPRequest.encodeBytes();
/*     */     } catch (IOException localIOException1) {
/* 193 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localIOException1);
/*     */     }
/*     */ 
/* 197 */     InputStream localInputStream = null;
/* 198 */     OutputStream localOutputStream = null;
/* 199 */     byte[] arrayOfByte2 = null;
/*     */     try {
/* 201 */       URL localURL = paramURI.toURL();
/* 202 */       if (debug != null) {
/* 203 */         debug.println("connecting to OCSP service at: " + localURL);
/*     */       }
/* 205 */       HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
/* 206 */       localHttpURLConnection.setConnectTimeout(15000);
/* 207 */       localHttpURLConnection.setReadTimeout(15000);
/* 208 */       localHttpURLConnection.setDoOutput(true);
/* 209 */       localHttpURLConnection.setDoInput(true);
/* 210 */       localHttpURLConnection.setRequestMethod("POST");
/* 211 */       localHttpURLConnection.setRequestProperty("Content-type", "application/ocsp-request");
/*     */ 
/* 213 */       localHttpURLConnection.setRequestProperty("Content-length", String.valueOf(arrayOfByte1.length));
/*     */ 
/* 215 */       localOutputStream = localHttpURLConnection.getOutputStream();
/* 216 */       localOutputStream.write(arrayOfByte1);
/* 217 */       localOutputStream.flush();
/*     */ 
/* 219 */       if ((debug != null) && (localHttpURLConnection.getResponseCode() != 200))
/*     */       {
/* 221 */         debug.println("Received HTTP error: " + localHttpURLConnection.getResponseCode() + " - " + localHttpURLConnection.getResponseMessage());
/*     */       }
/*     */ 
/* 224 */       localInputStream = localHttpURLConnection.getInputStream();
/* 225 */       int i = localHttpURLConnection.getContentLength();
/* 226 */       if (i == -1) {
/* 227 */         i = 2147483647;
/*     */       }
/* 229 */       arrayOfByte2 = new byte[i > 2048 ? 2048 : i];
/* 230 */       int j = 0;
/* 231 */       while (j < i) {
/* 232 */         int k = localInputStream.read(arrayOfByte2, j, arrayOfByte2.length - j);
/* 233 */         if (k < 0) {
/*     */           break;
/*     */         }
/* 236 */         j += k;
/* 237 */         if ((j >= arrayOfByte2.length) && (j < i)) {
/* 238 */           arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j * 2);
/*     */         }
/*     */       }
/* 241 */       arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j);
/*     */     } finally {
/* 243 */       if (localInputStream != null) {
/*     */         try {
/* 245 */           localInputStream.close();
/*     */         } catch (IOException localIOException5) {
/* 247 */           throw localIOException5;
/*     */         }
/*     */       }
/* 250 */       if (localOutputStream != null) {
/*     */         try {
/* 252 */           localOutputStream.close();
/*     */         } catch (IOException localIOException6) {
/* 254 */           throw localIOException6;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 259 */     OCSPResponse localOCSPResponse = null;
/*     */     try {
/* 261 */       localOCSPResponse = new OCSPResponse(arrayOfByte2, paramDate, paramList1);
/*     */     }
/*     */     catch (IOException localIOException4) {
/* 264 */       throw new CertPathValidatorException(localIOException4);
/*     */     }
/* 266 */     if (localOCSPResponse.getResponseStatus() != OCSPResponse.ResponseStatus.SUCCESSFUL) {
/* 267 */       throw new CertPathValidatorException("OCSP response error: " + localOCSPResponse.getResponseStatus());
/*     */     }
/*     */ 
/* 273 */     for (CertId localCertId : paramList) {
/* 274 */       OCSPResponse.SingleResponse localSingleResponse = localOCSPResponse.getSingleResponse(localCertId);
/* 275 */       if (localSingleResponse == null) {
/* 276 */         if (debug != null) {
/* 277 */           debug.println("No response found for CertId: " + localCertId);
/*     */         }
/* 279 */         throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
/*     */       }
/*     */ 
/* 283 */       if (debug != null) {
/* 284 */         debug.println("Status of certificate (with serial number " + localCertId.getSerialNumber() + ") is: " + localSingleResponse.getCertStatus());
/*     */       }
/*     */     }
/*     */ 
/* 288 */     return localOCSPResponse;
/*     */   }
/*     */ 
/*     */   public static URI getResponderURI(X509Certificate paramX509Certificate)
/*     */   {
/*     */     try
/*     */     {
/* 301 */       return getResponderURI(X509CertImpl.toImpl(paramX509Certificate));
/*     */     } catch (CertificateException localCertificateException) {
/*     */     }
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   static URI getResponderURI(X509CertImpl paramX509CertImpl)
/*     */   {
/* 311 */     AuthorityInfoAccessExtension localAuthorityInfoAccessExtension = paramX509CertImpl.getAuthorityInfoAccessExtension();
/*     */ 
/* 313 */     if (localAuthorityInfoAccessExtension == null) {
/* 314 */       return null;
/*     */     }
/*     */ 
/* 317 */     List localList = localAuthorityInfoAccessExtension.getAccessDescriptions();
/* 318 */     for (AccessDescription localAccessDescription : localList) {
/* 319 */       if (localAccessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id))
/*     */       {
/* 322 */         GeneralName localGeneralName = localAccessDescription.getAccessLocation();
/* 323 */         if (localGeneralName.getType() == 6) {
/* 324 */           URIName localURIName = (URIName)localGeneralName.getName();
/* 325 */           return localURIName.getURI();
/*     */         }
/*     */       }
/*     */     }
/* 329 */     return null; } 
/*     */   public static abstract interface RevocationStatus { public abstract CertStatus getCertStatus();
/*     */ 
/*     */     public abstract Date getRevocationTime();
/*     */ 
/*     */     public abstract CRLReason getRevocationReason();
/*     */ 
/*     */     public abstract Map<String, Extension> getSingleExtensions();
/*     */ 
/* 336 */     public static enum CertStatus { GOOD, REVOKED, UNKNOWN; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.OCSP
 * JD-Core Version:    0.6.2
 */