/*      */ package com.sun.org.apache.xerces.internal.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ 
/*      */ public class URI
/*      */   implements Serializable
/*      */ {
/*      */   static final long serialVersionUID = 1601921774685357214L;
/*   95 */   private static final byte[] fgLookupTable = new byte[''];
/*      */   private static final int RESERVED_CHARACTERS = 1;
/*      */   private static final int MARK_CHARACTERS = 2;
/*      */   private static final int SCHEME_CHARACTERS = 4;
/*      */   private static final int USERINFO_CHARACTERS = 8;
/*      */   private static final int ASCII_ALPHA_CHARACTERS = 16;
/*      */   private static final int ASCII_DIGIT_CHARACTERS = 32;
/*      */   private static final int ASCII_HEX_CHARACTERS = 64;
/*      */   private static final int PATH_CHARACTERS = 128;
/*      */   private static final int MASK_ALPHA_NUMERIC = 48;
/*      */   private static final int MASK_UNRESERVED_MASK = 50;
/*      */   private static final int MASK_URI_CHARACTER = 51;
/*      */   private static final int MASK_SCHEME_CHARACTER = 52;
/*      */   private static final int MASK_USERINFO_CHARACTER = 58;
/*      */   private static final int MASK_PATH_CHARACTER = 178;
/*  216 */   private String m_scheme = null;
/*      */ 
/*  219 */   private String m_userinfo = null;
/*      */ 
/*  222 */   private String m_host = null;
/*      */ 
/*  225 */   private int m_port = -1;
/*      */ 
/*  228 */   private String m_regAuthority = null;
/*      */ 
/*  231 */   private String m_path = null;
/*      */ 
/*  235 */   private String m_queryString = null;
/*      */ 
/*  238 */   private String m_fragment = null;
/*      */ 
/*  240 */   private static boolean DEBUG = false;
/*      */ 
/*      */   public URI()
/*      */   {
/*      */   }
/*      */ 
/*      */   public URI(URI p_other)
/*      */   {
/*  255 */     initialize(p_other);
/*      */   }
/*      */ 
/*      */   public URI(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  274 */     this((URI)null, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  297 */     this((URI)null, p_uriSpec, allowNonAbsoluteURI);
/*      */   }
/*      */ 
/*      */   public URI(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  313 */     initialize(p_base, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  334 */     initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_schemeSpecificPart)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  351 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/*  352 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
/*      */     }
/*      */ 
/*  355 */     if ((p_schemeSpecificPart == null) || (p_schemeSpecificPart.trim().length() == 0))
/*      */     {
/*  357 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
/*      */     }
/*      */ 
/*  360 */     setScheme(p_scheme);
/*  361 */     setPath(p_schemeSpecificPart);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  388 */     this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  420 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/*  421 */       throw new MalformedURIException("Scheme is required!");
/*      */     }
/*      */ 
/*  424 */     if (p_host == null) {
/*  425 */       if (p_userinfo != null) {
/*  426 */         throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
/*      */       }
/*      */ 
/*  429 */       if (p_port != -1) {
/*  430 */         throw new MalformedURIException("Port may not be specified if host is not specified!");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  435 */     if (p_path != null) {
/*  436 */       if ((p_path.indexOf('?') != -1) && (p_queryString != null)) {
/*  437 */         throw new MalformedURIException("Query string cannot be specified in path and query string!");
/*      */       }
/*      */ 
/*  441 */       if ((p_path.indexOf('#') != -1) && (p_fragment != null)) {
/*  442 */         throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  447 */     setScheme(p_scheme);
/*  448 */     setHost(p_host);
/*  449 */     setPort(p_port);
/*  450 */     setUserinfo(p_userinfo);
/*  451 */     setPath(p_path);
/*  452 */     setQueryString(p_queryString);
/*  453 */     setFragment(p_fragment);
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_other)
/*      */   {
/*  462 */     this.m_scheme = p_other.getScheme();
/*  463 */     this.m_userinfo = p_other.getUserinfo();
/*  464 */     this.m_host = p_other.getHost();
/*  465 */     this.m_port = p_other.getPort();
/*  466 */     this.m_regAuthority = p_other.getRegBasedAuthority();
/*  467 */     this.m_path = p_other.getPath();
/*  468 */     this.m_queryString = p_other.getQueryString();
/*  469 */     this.m_fragment = p_other.getFragment();
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  493 */     String uriSpec = p_uriSpec;
/*  494 */     int uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
/*      */ 
/*  496 */     if ((p_base == null) && (uriSpecLen == 0)) {
/*  497 */       if (allowNonAbsoluteURI) {
/*  498 */         this.m_path = "";
/*  499 */         return;
/*      */       }
/*  501 */       throw new MalformedURIException("Cannot initialize URI with empty parameters.");
/*      */     }
/*      */ 
/*  505 */     if (uriSpecLen == 0) {
/*  506 */       initialize(p_base);
/*  507 */       return;
/*      */     }
/*      */ 
/*  510 */     int index = 0;
/*      */ 
/*  513 */     int colonIdx = uriSpec.indexOf(':');
/*  514 */     if (colonIdx != -1) {
/*  515 */       int searchFrom = colonIdx - 1;
/*      */ 
/*  517 */       int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
/*  518 */       int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
/*  519 */       int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
/*      */ 
/*  521 */       if ((colonIdx == 0) || (slashIdx != -1) || (queryIdx != -1) || (fragmentIdx != -1))
/*      */       {
/*  524 */         if ((colonIdx == 0) || ((p_base == null) && (fragmentIdx != 0) && (!allowNonAbsoluteURI)))
/*  525 */           throw new MalformedURIException("No scheme found in URI.");
/*      */       }
/*      */       else
/*      */       {
/*  529 */         initializeScheme(uriSpec);
/*  530 */         index = this.m_scheme.length() + 1;
/*      */ 
/*  533 */         if ((colonIdx == uriSpecLen - 1) || (uriSpec.charAt(colonIdx + 1) == '#')) {
/*  534 */           throw new MalformedURIException("Scheme specific part cannot be empty.");
/*      */         }
/*      */       }
/*      */     }
/*  538 */     else if ((p_base == null) && (uriSpec.indexOf('#') != 0) && (!allowNonAbsoluteURI)) {
/*  539 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  551 */     if ((index + 1 < uriSpecLen) && (uriSpec.charAt(index) == '/') && (uriSpec.charAt(index + 1) == '/'))
/*      */     {
/*  553 */       index += 2;
/*  554 */       int startPos = index;
/*      */ 
/*  557 */       char testChar = '\000';
/*  558 */       while (index < uriSpecLen) {
/*  559 */         testChar = uriSpec.charAt(index);
/*  560 */         if ((testChar == '/') || (testChar == '?') || (testChar == '#')) {
/*      */           break;
/*      */         }
/*  563 */         index++;
/*      */       }
/*      */ 
/*  569 */       if (index > startPos)
/*      */       {
/*  572 */         if (!initializeAuthority(uriSpec.substring(startPos, index))) {
/*  573 */           index = startPos - 2;
/*      */         }
/*      */       }
/*      */       else {
/*  577 */         this.m_host = "";
/*      */       }
/*      */     }
/*      */ 
/*  581 */     initializePath(uriSpec, index);
/*      */ 
/*  588 */     if (p_base != null)
/*  589 */       absolutize(p_base);
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  612 */     String uriSpec = p_uriSpec;
/*  613 */     int uriSpecLen = uriSpec != null ? uriSpec.length() : 0;
/*      */ 
/*  615 */     if ((p_base == null) && (uriSpecLen == 0)) {
/*  616 */       throw new MalformedURIException("Cannot initialize URI with empty parameters.");
/*      */     }
/*      */ 
/*  621 */     if (uriSpecLen == 0) {
/*  622 */       initialize(p_base);
/*  623 */       return;
/*      */     }
/*      */ 
/*  626 */     int index = 0;
/*      */ 
/*  629 */     int colonIdx = uriSpec.indexOf(':');
/*  630 */     if (colonIdx != -1) {
/*  631 */       int searchFrom = colonIdx - 1;
/*      */ 
/*  633 */       int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
/*  634 */       int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
/*  635 */       int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
/*      */ 
/*  637 */       if ((colonIdx == 0) || (slashIdx != -1) || (queryIdx != -1) || (fragmentIdx != -1))
/*      */       {
/*  640 */         if ((colonIdx == 0) || ((p_base == null) && (fragmentIdx != 0)))
/*  641 */           throw new MalformedURIException("No scheme found in URI.");
/*      */       }
/*      */       else
/*      */       {
/*  645 */         initializeScheme(uriSpec);
/*  646 */         index = this.m_scheme.length() + 1;
/*      */ 
/*  649 */         if ((colonIdx == uriSpecLen - 1) || (uriSpec.charAt(colonIdx + 1) == '#')) {
/*  650 */           throw new MalformedURIException("Scheme specific part cannot be empty.");
/*      */         }
/*      */       }
/*      */     }
/*  654 */     else if ((p_base == null) && (uriSpec.indexOf('#') != 0)) {
/*  655 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  667 */     if ((index + 1 < uriSpecLen) && (uriSpec.charAt(index) == '/') && (uriSpec.charAt(index + 1) == '/'))
/*      */     {
/*  669 */       index += 2;
/*  670 */       int startPos = index;
/*      */ 
/*  673 */       char testChar = '\000';
/*  674 */       while (index < uriSpecLen) {
/*  675 */         testChar = uriSpec.charAt(index);
/*  676 */         if ((testChar == '/') || (testChar == '?') || (testChar == '#')) {
/*      */           break;
/*      */         }
/*  679 */         index++;
/*      */       }
/*      */ 
/*  685 */       if (index > startPos)
/*      */       {
/*  688 */         if (!initializeAuthority(uriSpec.substring(startPos, index))) {
/*  689 */           index = startPos - 2;
/*      */         }
/*      */       }
/*      */       else {
/*  693 */         this.m_host = "";
/*      */       }
/*      */     }
/*      */ 
/*  697 */     initializePath(uriSpec, index);
/*      */ 
/*  704 */     if (p_base != null)
/*  705 */       absolutize(p_base);
/*      */   }
/*      */ 
/*      */   public void absolutize(URI p_base)
/*      */   {
/*  723 */     if ((this.m_path.length() == 0) && (this.m_scheme == null) && (this.m_host == null) && (this.m_regAuthority == null))
/*      */     {
/*  725 */       this.m_scheme = p_base.getScheme();
/*  726 */       this.m_userinfo = p_base.getUserinfo();
/*  727 */       this.m_host = p_base.getHost();
/*  728 */       this.m_port = p_base.getPort();
/*  729 */       this.m_regAuthority = p_base.getRegBasedAuthority();
/*  730 */       this.m_path = p_base.getPath();
/*      */ 
/*  732 */       if (this.m_queryString == null) {
/*  733 */         this.m_queryString = p_base.getQueryString();
/*      */ 
/*  735 */         if (this.m_fragment == null) {
/*  736 */           this.m_fragment = p_base.getFragment();
/*      */         }
/*      */       }
/*  739 */       return;
/*      */     }
/*      */ 
/*  744 */     if (this.m_scheme == null) {
/*  745 */       this.m_scheme = p_base.getScheme();
/*      */     }
/*      */     else {
/*  748 */       return;
/*      */     }
/*      */ 
/*  753 */     if ((this.m_host == null) && (this.m_regAuthority == null)) {
/*  754 */       this.m_userinfo = p_base.getUserinfo();
/*  755 */       this.m_host = p_base.getHost();
/*  756 */       this.m_port = p_base.getPort();
/*  757 */       this.m_regAuthority = p_base.getRegBasedAuthority();
/*      */     }
/*      */     else {
/*  760 */       return;
/*      */     }
/*      */ 
/*  764 */     if ((this.m_path.length() > 0) && (this.m_path.startsWith("/")))
/*      */     {
/*  766 */       return;
/*      */     }
/*      */ 
/*  771 */     String path = "";
/*  772 */     String basePath = p_base.getPath();
/*      */ 
/*  775 */     if ((basePath != null) && (basePath.length() > 0)) {
/*  776 */       int lastSlash = basePath.lastIndexOf('/');
/*  777 */       if (lastSlash != -1) {
/*  778 */         path = basePath.substring(0, lastSlash + 1);
/*      */       }
/*      */     }
/*  781 */     else if (this.m_path.length() > 0) {
/*  782 */       path = "/";
/*      */     }
/*      */ 
/*  786 */     path = path.concat(this.m_path);
/*      */ 
/*  789 */     int index = -1;
/*  790 */     while ((index = path.indexOf("/./")) != -1) {
/*  791 */       path = path.substring(0, index + 1).concat(path.substring(index + 3));
/*      */     }
/*      */ 
/*  795 */     if (path.endsWith("/.")) {
/*  796 */       path = path.substring(0, path.length() - 1);
/*      */     }
/*      */ 
/*  801 */     index = 1;
/*  802 */     int segIndex = -1;
/*  803 */     String tempString = null;
/*      */ 
/*  805 */     while ((index = path.indexOf("/../", index)) > 0) {
/*  806 */       tempString = path.substring(0, path.indexOf("/../"));
/*  807 */       segIndex = tempString.lastIndexOf('/');
/*  808 */       if (segIndex != -1) {
/*  809 */         if (!tempString.substring(segIndex).equals("..")) {
/*  810 */           path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
/*  811 */           index = segIndex;
/*      */         }
/*      */         else {
/*  814 */           index += 4;
/*      */         }
/*      */       }
/*      */       else {
/*  818 */         index += 4;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  824 */     if (path.endsWith("/..")) {
/*  825 */       tempString = path.substring(0, path.length() - 3);
/*  826 */       segIndex = tempString.lastIndexOf('/');
/*  827 */       if (segIndex != -1) {
/*  828 */         path = path.substring(0, segIndex + 1);
/*      */       }
/*      */     }
/*  831 */     this.m_path = path;
/*      */   }
/*      */ 
/*      */   private void initializeScheme(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  844 */     int uriSpecLen = p_uriSpec.length();
/*  845 */     int index = 0;
/*  846 */     String scheme = null;
/*  847 */     char testChar = '\000';
/*      */ 
/*  849 */     while (index < uriSpecLen) {
/*  850 */       testChar = p_uriSpec.charAt(index);
/*  851 */       if ((testChar == ':') || (testChar == '/') || (testChar == '?') || (testChar == '#'))
/*      */       {
/*      */         break;
/*      */       }
/*  855 */       index++;
/*      */     }
/*  857 */     scheme = p_uriSpec.substring(0, index);
/*      */ 
/*  859 */     if (scheme.length() == 0) {
/*  860 */       throw new MalformedURIException("No scheme found in URI.");
/*      */     }
/*      */ 
/*  863 */     setScheme(scheme);
/*      */   }
/*      */ 
/*      */   private boolean initializeAuthority(String p_uriSpec)
/*      */   {
/*  878 */     int index = 0;
/*  879 */     int start = 0;
/*  880 */     int end = p_uriSpec.length();
/*      */ 
/*  882 */     char testChar = '\000';
/*  883 */     String userinfo = null;
/*      */ 
/*  886 */     if (p_uriSpec.indexOf('@', start) != -1) {
/*  887 */       while (index < end) {
/*  888 */         testChar = p_uriSpec.charAt(index);
/*  889 */         if (testChar == '@') {
/*      */           break;
/*      */         }
/*  892 */         index++;
/*      */       }
/*  894 */       userinfo = p_uriSpec.substring(start, index);
/*  895 */       index++;
/*      */     }
/*      */ 
/*  900 */     String host = null;
/*  901 */     start = index;
/*  902 */     boolean hasPort = false;
/*  903 */     if (index < end) {
/*  904 */       if (p_uriSpec.charAt(start) == '[') {
/*  905 */         int bracketIndex = p_uriSpec.indexOf(']', start);
/*  906 */         index = bracketIndex != -1 ? bracketIndex : end;
/*  907 */         if ((index + 1 < end) && (p_uriSpec.charAt(index + 1) == ':')) {
/*  908 */           index++;
/*  909 */           hasPort = true;
/*      */         }
/*      */         else {
/*  912 */           index = end;
/*      */         }
/*      */       }
/*      */       else {
/*  916 */         int colonIndex = p_uriSpec.lastIndexOf(':', end);
/*  917 */         index = colonIndex > start ? colonIndex : end;
/*  918 */         hasPort = index != end;
/*      */       }
/*      */     }
/*  921 */     host = p_uriSpec.substring(start, index);
/*  922 */     int port = -1;
/*  923 */     if (host.length() > 0)
/*      */     {
/*  925 */       if (hasPort) {
/*  926 */         index++;
/*  927 */         start = index;
/*  928 */         while (index < end) {
/*  929 */           index++;
/*      */         }
/*  931 */         String portStr = p_uriSpec.substring(start, index);
/*  932 */         if (portStr.length() > 0)
/*      */         {
/*      */           try
/*      */           {
/*  944 */             port = Integer.parseInt(portStr);
/*  945 */             if (port == -1) port--; 
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*  948 */             port = -2;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  954 */     if (isValidServerBasedAuthority(host, port, userinfo)) {
/*  955 */       this.m_host = host;
/*  956 */       this.m_port = port;
/*  957 */       this.m_userinfo = userinfo;
/*  958 */       return true;
/*      */     }
/*      */ 
/*  964 */     if (isValidRegistryBasedAuthority(p_uriSpec)) {
/*  965 */       this.m_regAuthority = p_uriSpec;
/*  966 */       return true;
/*      */     }
/*  968 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isValidServerBasedAuthority(String host, int port, String userinfo)
/*      */   {
/*  985 */     if (!isWellFormedAddress(host)) {
/*  986 */       return false;
/*      */     }
/*      */ 
/*  993 */     if ((port < -1) || (port > 65535)) {
/*  994 */       return false;
/*      */     }
/*      */ 
/*  998 */     if (userinfo != null)
/*      */     {
/* 1001 */       int index = 0;
/* 1002 */       int end = userinfo.length();
/* 1003 */       char testChar = '\000';
/* 1004 */       while (index < end) {
/* 1005 */         testChar = userinfo.charAt(index);
/* 1006 */         if (testChar == '%') {
/* 1007 */           if ((index + 2 >= end) || (!isHex(userinfo.charAt(index + 1))) || (!isHex(userinfo.charAt(index + 2))))
/*      */           {
/* 1010 */             return false;
/*      */           }
/* 1012 */           index += 2;
/*      */         }
/* 1014 */         else if (!isUserinfoCharacter(testChar)) {
/* 1015 */           return false;
/*      */         }
/* 1017 */         index++;
/*      */       }
/*      */     }
/* 1020 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isValidRegistryBasedAuthority(String authority)
/*      */   {
/* 1031 */     int index = 0;
/* 1032 */     int end = authority.length();
/*      */ 
/* 1035 */     while (index < end) {
/* 1036 */       char testChar = authority.charAt(index);
/*      */ 
/* 1039 */       if (testChar == '%') {
/* 1040 */         if ((index + 2 >= end) || (!isHex(authority.charAt(index + 1))) || (!isHex(authority.charAt(index + 2))))
/*      */         {
/* 1043 */           return false;
/*      */         }
/* 1045 */         index += 2;
/*      */       }
/* 1049 */       else if (!isPathCharacter(testChar)) {
/* 1050 */         return false;
/*      */       }
/* 1052 */       index++;
/*      */     }
/* 1054 */     return true;
/*      */   }
/*      */ 
/*      */   private void initializePath(String p_uriSpec, int p_nStartIndex)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1067 */     if (p_uriSpec == null) {
/* 1068 */       throw new MalformedURIException("Cannot initialize path from null string!");
/*      */     }
/*      */ 
/* 1072 */     int index = p_nStartIndex;
/* 1073 */     int start = p_nStartIndex;
/* 1074 */     int end = p_uriSpec.length();
/* 1075 */     char testChar = '\000';
/*      */ 
/* 1078 */     if (start < end)
/*      */     {
/* 1080 */       if ((getScheme() == null) || (p_uriSpec.charAt(start) == '/'));
/* 1085 */       while (index < end) {
/* 1086 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1089 */         if (testChar == '%') {
/* 1090 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1093 */             throw new MalformedURIException("Path contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1096 */           index += 2;
/*      */         }
/* 1100 */         else if (!isPathCharacter(testChar)) {
/* 1101 */           if ((testChar == '?') || (testChar == '#')) {
/*      */             break;
/*      */           }
/* 1104 */           throw new MalformedURIException("Path contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1107 */         index++; continue;
/*      */ 
/* 1114 */         while (index < end) {
/* 1115 */           testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1117 */           if ((testChar == '?') || (testChar == '#'))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1122 */           if (testChar == '%') {
/* 1123 */             if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */             {
/* 1126 */               throw new MalformedURIException("Opaque part contains invalid escape sequence!");
/*      */             }
/*      */ 
/* 1129 */             index += 2;
/*      */           }
/* 1136 */           else if (!isURICharacter(testChar)) {
/* 1137 */             throw new MalformedURIException("Opaque part contains invalid character: " + testChar);
/*      */           }
/*      */ 
/* 1140 */           index++;
/*      */         }
/*      */       }
/*      */     }
/* 1144 */     this.m_path = p_uriSpec.substring(start, index);
/*      */ 
/* 1147 */     if (testChar == '?') {
/* 1148 */       index++;
/* 1149 */       start = index;
/* 1150 */       while (index < end) {
/* 1151 */         testChar = p_uriSpec.charAt(index);
/* 1152 */         if (testChar == '#') {
/*      */           break;
/*      */         }
/* 1155 */         if (testChar == '%') {
/* 1156 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1159 */             throw new MalformedURIException("Query string contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1162 */           index += 2;
/*      */         }
/* 1164 */         else if (!isURICharacter(testChar)) {
/* 1165 */           throw new MalformedURIException("Query string contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1168 */         index++;
/*      */       }
/* 1170 */       this.m_queryString = p_uriSpec.substring(start, index);
/*      */     }
/*      */ 
/* 1174 */     if (testChar == '#') {
/* 1175 */       index++;
/* 1176 */       start = index;
/* 1177 */       while (index < end) {
/* 1178 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/* 1180 */         if (testChar == '%') {
/* 1181 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/* 1184 */             throw new MalformedURIException("Fragment contains invalid escape sequence!");
/*      */           }
/*      */ 
/* 1187 */           index += 2;
/*      */         }
/* 1189 */         else if (!isURICharacter(testChar)) {
/* 1190 */           throw new MalformedURIException("Fragment contains invalid character: " + testChar);
/*      */         }
/*      */ 
/* 1193 */         index++;
/*      */       }
/* 1195 */       this.m_fragment = p_uriSpec.substring(start, index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getScheme()
/*      */   {
/* 1205 */     return this.m_scheme;
/*      */   }
/*      */ 
/*      */   public String getSchemeSpecificPart()
/*      */   {
/* 1215 */     StringBuffer schemespec = new StringBuffer();
/*      */ 
/* 1217 */     if ((this.m_host != null) || (this.m_regAuthority != null)) {
/* 1218 */       schemespec.append("//");
/*      */ 
/* 1221 */       if (this.m_host != null)
/*      */       {
/* 1223 */         if (this.m_userinfo != null) {
/* 1224 */           schemespec.append(this.m_userinfo);
/* 1225 */           schemespec.append('@');
/*      */         }
/*      */ 
/* 1228 */         schemespec.append(this.m_host);
/*      */ 
/* 1230 */         if (this.m_port != -1) {
/* 1231 */           schemespec.append(':');
/* 1232 */           schemespec.append(this.m_port);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1237 */         schemespec.append(this.m_regAuthority);
/*      */       }
/*      */     }
/*      */ 
/* 1241 */     if (this.m_path != null) {
/* 1242 */       schemespec.append(this.m_path);
/*      */     }
/*      */ 
/* 1245 */     if (this.m_queryString != null) {
/* 1246 */       schemespec.append('?');
/* 1247 */       schemespec.append(this.m_queryString);
/*      */     }
/*      */ 
/* 1250 */     if (this.m_fragment != null) {
/* 1251 */       schemespec.append('#');
/* 1252 */       schemespec.append(this.m_fragment);
/*      */     }
/*      */ 
/* 1255 */     return schemespec.toString();
/*      */   }
/*      */ 
/*      */   public String getUserinfo()
/*      */   {
/* 1264 */     return this.m_userinfo;
/*      */   }
/*      */ 
/*      */   public String getHost()
/*      */   {
/* 1273 */     return this.m_host;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/* 1282 */     return this.m_port;
/*      */   }
/*      */ 
/*      */   public String getRegBasedAuthority()
/*      */   {
/* 1291 */     return this.m_regAuthority;
/*      */   }
/*      */ 
/*      */   public String getAuthority()
/*      */   {
/* 1300 */     StringBuffer authority = new StringBuffer();
/* 1301 */     if ((this.m_host != null) || (this.m_regAuthority != null)) {
/* 1302 */       authority.append("//");
/*      */ 
/* 1305 */       if (this.m_host != null)
/*      */       {
/* 1307 */         if (this.m_userinfo != null) {
/* 1308 */           authority.append(this.m_userinfo);
/* 1309 */           authority.append('@');
/*      */         }
/*      */ 
/* 1312 */         authority.append(this.m_host);
/*      */ 
/* 1314 */         if (this.m_port != -1) {
/* 1315 */           authority.append(':');
/* 1316 */           authority.append(this.m_port);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1321 */         authority.append(this.m_regAuthority);
/*      */       }
/*      */     }
/* 1324 */     return authority.toString();
/*      */   }
/*      */ 
/*      */   public String getPath(boolean p_includeQueryString, boolean p_includeFragment)
/*      */   {
/* 1343 */     StringBuffer pathString = new StringBuffer(this.m_path);
/*      */ 
/* 1345 */     if ((p_includeQueryString) && (this.m_queryString != null)) {
/* 1346 */       pathString.append('?');
/* 1347 */       pathString.append(this.m_queryString);
/*      */     }
/*      */ 
/* 1350 */     if ((p_includeFragment) && (this.m_fragment != null)) {
/* 1351 */       pathString.append('#');
/* 1352 */       pathString.append(this.m_fragment);
/*      */     }
/* 1354 */     return pathString.toString();
/*      */   }
/*      */ 
/*      */   public String getPath()
/*      */   {
/* 1364 */     return this.m_path;
/*      */   }
/*      */ 
/*      */   public String getQueryString()
/*      */   {
/* 1375 */     return this.m_queryString;
/*      */   }
/*      */ 
/*      */   public String getFragment()
/*      */   {
/* 1386 */     return this.m_fragment;
/*      */   }
/*      */ 
/*      */   public void setScheme(String p_scheme)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1399 */     if (p_scheme == null) {
/* 1400 */       throw new MalformedURIException("Cannot set scheme from null string!");
/*      */     }
/*      */ 
/* 1403 */     if (!isConformantSchemeName(p_scheme)) {
/* 1404 */       throw new MalformedURIException("The scheme is not conformant.");
/*      */     }
/*      */ 
/* 1407 */     this.m_scheme = p_scheme.toLowerCase();
/*      */   }
/*      */ 
/*      */   public void setUserinfo(String p_userinfo)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1420 */     if (p_userinfo == null) {
/* 1421 */       this.m_userinfo = null;
/* 1422 */       return;
/*      */     }
/*      */ 
/* 1425 */     if (this.m_host == null) {
/* 1426 */       throw new MalformedURIException("Userinfo cannot be set when host is null!");
/*      */     }
/*      */ 
/* 1432 */     int index = 0;
/* 1433 */     int end = p_userinfo.length();
/* 1434 */     char testChar = '\000';
/* 1435 */     while (index < end) {
/* 1436 */       testChar = p_userinfo.charAt(index);
/* 1437 */       if (testChar == '%') {
/* 1438 */         if ((index + 2 >= end) || (!isHex(p_userinfo.charAt(index + 1))) || (!isHex(p_userinfo.charAt(index + 2))))
/*      */         {
/* 1441 */           throw new MalformedURIException("Userinfo contains invalid escape sequence!");
/*      */         }
/*      */ 
/*      */       }
/* 1445 */       else if (!isUserinfoCharacter(testChar)) {
/* 1446 */         throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
/*      */       }
/*      */ 
/* 1449 */       index++;
/*      */     }
/*      */ 
/* 1452 */     this.m_userinfo = p_userinfo;
/*      */   }
/*      */ 
/*      */   public void setHost(String p_host)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1468 */     if ((p_host == null) || (p_host.length() == 0)) {
/* 1469 */       if (p_host != null) {
/* 1470 */         this.m_regAuthority = null;
/*      */       }
/* 1472 */       this.m_host = p_host;
/* 1473 */       this.m_userinfo = null;
/* 1474 */       this.m_port = -1;
/* 1475 */       return;
/*      */     }
/* 1477 */     if (!isWellFormedAddress(p_host)) {
/* 1478 */       throw new MalformedURIException("Host is not a well formed address!");
/*      */     }
/* 1480 */     this.m_host = p_host;
/* 1481 */     this.m_regAuthority = null;
/*      */   }
/*      */ 
/*      */   public void setPort(int p_port)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1496 */     if ((p_port >= 0) && (p_port <= 65535)) {
/* 1497 */       if (this.m_host == null) {
/* 1498 */         throw new MalformedURIException("Port cannot be set when host is null!");
/*      */       }
/*      */ 
/*      */     }
/* 1502 */     else if (p_port != -1) {
/* 1503 */       throw new MalformedURIException("Invalid port number!");
/*      */     }
/* 1505 */     this.m_port = p_port;
/*      */   }
/*      */ 
/*      */   public void setRegBasedAuthority(String authority)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1522 */     if (authority == null) {
/* 1523 */       this.m_regAuthority = null;
/* 1524 */       return;
/*      */     }
/*      */ 
/* 1528 */     if ((authority.length() < 1) || (!isValidRegistryBasedAuthority(authority)) || (authority.indexOf('/') != -1))
/*      */     {
/* 1531 */       throw new MalformedURIException("Registry based authority is not well formed.");
/*      */     }
/* 1533 */     this.m_regAuthority = authority;
/* 1534 */     this.m_host = null;
/* 1535 */     this.m_userinfo = null;
/* 1536 */     this.m_port = -1;
/*      */   }
/*      */ 
/*      */   public void setPath(String p_path)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1554 */     if (p_path == null) {
/* 1555 */       this.m_path = null;
/* 1556 */       this.m_queryString = null;
/* 1557 */       this.m_fragment = null;
/*      */     }
/*      */     else {
/* 1560 */       initializePath(p_path, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void appendPath(String p_addToPath)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1579 */     if ((p_addToPath == null) || (p_addToPath.trim().length() == 0)) {
/* 1580 */       return;
/*      */     }
/*      */ 
/* 1583 */     if (!isURIString(p_addToPath)) {
/* 1584 */       throw new MalformedURIException("Path contains invalid character!");
/*      */     }
/*      */ 
/* 1588 */     if ((this.m_path == null) || (this.m_path.trim().length() == 0)) {
/* 1589 */       if (p_addToPath.startsWith("/")) {
/* 1590 */         this.m_path = p_addToPath;
/*      */       }
/*      */       else {
/* 1593 */         this.m_path = ("/" + p_addToPath);
/*      */       }
/*      */     }
/* 1596 */     else if (this.m_path.endsWith("/")) {
/* 1597 */       if (p_addToPath.startsWith("/")) {
/* 1598 */         this.m_path = this.m_path.concat(p_addToPath.substring(1));
/*      */       }
/*      */       else {
/* 1601 */         this.m_path = this.m_path.concat(p_addToPath);
/*      */       }
/*      */ 
/*      */     }
/* 1605 */     else if (p_addToPath.startsWith("/")) {
/* 1606 */       this.m_path = this.m_path.concat(p_addToPath);
/*      */     }
/*      */     else
/* 1609 */       this.m_path = this.m_path.concat("/" + p_addToPath);
/*      */   }
/*      */ 
/*      */   public void setQueryString(String p_queryString)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1626 */     if (p_queryString == null) {
/* 1627 */       this.m_queryString = null;
/*      */     } else {
/* 1629 */       if (!isGenericURI()) {
/* 1630 */         throw new MalformedURIException("Query string can only be set for a generic URI!");
/*      */       }
/*      */ 
/* 1633 */       if (getPath() == null) {
/* 1634 */         throw new MalformedURIException("Query string cannot be set when path is null!");
/*      */       }
/*      */ 
/* 1637 */       if (!isURIString(p_queryString)) {
/* 1638 */         throw new MalformedURIException("Query string contains invalid character!");
/*      */       }
/*      */ 
/* 1642 */       this.m_queryString = p_queryString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFragment(String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1658 */     if (p_fragment == null) {
/* 1659 */       this.m_fragment = null;
/*      */     } else {
/* 1661 */       if (!isGenericURI()) {
/* 1662 */         throw new MalformedURIException("Fragment can only be set for a generic URI!");
/*      */       }
/*      */ 
/* 1665 */       if (getPath() == null) {
/* 1666 */         throw new MalformedURIException("Fragment cannot be set when path is null!");
/*      */       }
/*      */ 
/* 1669 */       if (!isURIString(p_fragment)) {
/* 1670 */         throw new MalformedURIException("Fragment contains invalid character!");
/*      */       }
/*      */ 
/* 1674 */       this.m_fragment = p_fragment;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean equals(Object p_test)
/*      */   {
/* 1687 */     if ((p_test instanceof URI)) {
/* 1688 */       URI testURI = (URI)p_test;
/* 1689 */       if (((this.m_scheme == null) && (testURI.m_scheme == null)) || ((this.m_scheme != null) && (testURI.m_scheme != null) && (this.m_scheme.equals(testURI.m_scheme)) && (((this.m_userinfo == null) && (testURI.m_userinfo == null)) || ((this.m_userinfo != null) && (testURI.m_userinfo != null) && (this.m_userinfo.equals(testURI.m_userinfo)) && (((this.m_host == null) && (testURI.m_host == null)) || ((this.m_host != null) && (testURI.m_host != null) && (this.m_host.equals(testURI.m_host)) && (this.m_port == testURI.m_port) && (((this.m_path == null) && (testURI.m_path == null)) || ((this.m_path != null) && (testURI.m_path != null) && (this.m_path.equals(testURI.m_path)) && (((this.m_queryString == null) && (testURI.m_queryString == null)) || ((this.m_queryString != null) && (testURI.m_queryString != null) && (this.m_queryString.equals(testURI.m_queryString)) && (((this.m_fragment == null) && (testURI.m_fragment == null)) || ((this.m_fragment != null) && (testURI.m_fragment != null) && (this.m_fragment.equals(testURI.m_fragment))))))))))))))
/*      */       {
/* 1708 */         return true;
/*      */       }
/*      */     }
/* 1711 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1720 */     StringBuffer uriSpecString = new StringBuffer();
/*      */ 
/* 1722 */     if (this.m_scheme != null) {
/* 1723 */       uriSpecString.append(this.m_scheme);
/* 1724 */       uriSpecString.append(':');
/*      */     }
/* 1726 */     uriSpecString.append(getSchemeSpecificPart());
/* 1727 */     return uriSpecString.toString();
/*      */   }
/*      */ 
/*      */   public boolean isGenericURI()
/*      */   {
/* 1740 */     return this.m_host != null;
/*      */   }
/*      */ 
/*      */   public boolean isAbsoluteURI()
/*      */   {
/* 1751 */     return this.m_scheme != null;
/*      */   }
/*      */ 
/*      */   public static boolean isConformantSchemeName(String p_scheme)
/*      */   {
/* 1762 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0)) {
/* 1763 */       return false;
/*      */     }
/*      */ 
/* 1766 */     if (!isAlpha(p_scheme.charAt(0))) {
/* 1767 */       return false;
/*      */     }
/*      */ 
/* 1771 */     int schemeLength = p_scheme.length();
/* 1772 */     for (int i = 1; i < schemeLength; i++) {
/* 1773 */       char testChar = p_scheme.charAt(i);
/* 1774 */       if (!isSchemeCharacter(testChar)) {
/* 1775 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1779 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedAddress(String address)
/*      */   {
/* 1795 */     if (address == null) {
/* 1796 */       return false;
/*      */     }
/*      */ 
/* 1799 */     int addrLength = address.length();
/* 1800 */     if (addrLength == 0) {
/* 1801 */       return false;
/*      */     }
/*      */ 
/* 1805 */     if (address.startsWith("[")) {
/* 1806 */       return isWellFormedIPv6Reference(address);
/*      */     }
/*      */ 
/* 1810 */     if ((address.startsWith(".")) || (address.startsWith("-")) || (address.endsWith("-")))
/*      */     {
/* 1813 */       return false;
/*      */     }
/*      */ 
/* 1819 */     int index = address.lastIndexOf('.');
/* 1820 */     if (address.endsWith(".")) {
/* 1821 */       index = address.substring(0, index).lastIndexOf('.');
/*      */     }
/*      */ 
/* 1824 */     if ((index + 1 < addrLength) && (isDigit(address.charAt(index + 1)))) {
/* 1825 */       return isWellFormedIPv4Address(address);
/*      */     }
/*      */ 
/* 1835 */     if (addrLength > 255) {
/* 1836 */       return false;
/*      */     }
/*      */ 
/* 1842 */     int labelCharCount = 0;
/*      */ 
/* 1844 */     for (int i = 0; i < addrLength; i++) {
/* 1845 */       char testChar = address.charAt(i);
/* 1846 */       if (testChar == '.') {
/* 1847 */         if (!isAlphanum(address.charAt(i - 1))) {
/* 1848 */           return false;
/*      */         }
/* 1850 */         if ((i + 1 < addrLength) && (!isAlphanum(address.charAt(i + 1)))) {
/* 1851 */           return false;
/*      */         }
/* 1853 */         labelCharCount = 0;
/*      */       } else {
/* 1855 */         if ((!isAlphanum(testChar)) && (testChar != '-')) {
/* 1856 */           return false;
/*      */         }
/*      */ 
/* 1859 */         labelCharCount++; if (labelCharCount > 63) {
/* 1860 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 1864 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedIPv4Address(String address)
/*      */   {
/* 1880 */     int addrLength = address.length();
/*      */ 
/* 1882 */     int numDots = 0;
/* 1883 */     int numDigits = 0;
/*      */ 
/* 1895 */     for (int i = 0; i < addrLength; i++) {
/* 1896 */       char testChar = address.charAt(i);
/* 1897 */       if (testChar == '.') {
/* 1898 */         if (((i > 0) && (!isDigit(address.charAt(i - 1)))) || ((i + 1 < addrLength) && (!isDigit(address.charAt(i + 1)))))
/*      */         {
/* 1900 */           return false;
/*      */         }
/* 1902 */         numDigits = 0;
/* 1903 */         numDots++; if (numDots > 3)
/* 1904 */           return false;
/*      */       }
/*      */       else {
/* 1907 */         if (!isDigit(testChar)) {
/* 1908 */           return false;
/*      */         }
/*      */ 
/* 1912 */         numDigits++; if (numDigits > 3) {
/* 1913 */           return false;
/*      */         }
/*      */ 
/* 1916 */         if (numDigits == 3) {
/* 1917 */           char first = address.charAt(i - 2);
/* 1918 */           char second = address.charAt(i - 1);
/* 1919 */           if ((first >= '2') && ((first != '2') || ((second >= '5') && ((second != '5') || (testChar > '5')))))
/*      */           {
/* 1923 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1927 */     return numDots == 3;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedIPv6Reference(String address)
/*      */   {
/* 1947 */     int addrLength = address.length();
/* 1948 */     int index = 1;
/* 1949 */     int end = addrLength - 1;
/*      */ 
/* 1952 */     if ((addrLength <= 2) || (address.charAt(0) != '[') || (address.charAt(end) != ']'))
/*      */     {
/* 1954 */       return false;
/*      */     }
/*      */ 
/* 1958 */     int[] counter = new int[1];
/*      */ 
/* 1961 */     index = scanHexSequence(address, index, end, counter);
/* 1962 */     if (index == -1) {
/* 1963 */       return false;
/*      */     }
/*      */ 
/* 1966 */     if (index == end) {
/* 1967 */       return counter[0] == 8;
/*      */     }
/*      */ 
/* 1970 */     if ((index + 1 < end) && (address.charAt(index) == ':')) {
/* 1971 */       if (address.charAt(index + 1) == ':')
/*      */       {
/* 1973 */         if (counter[0] += 1 > 8) {
/* 1974 */           return false;
/*      */         }
/* 1976 */         index += 2;
/*      */ 
/* 1978 */         if (index == end) {
/* 1979 */           return true;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1986 */         return (counter[0] == 6) && (isWellFormedIPv4Address(address.substring(index + 1, end)));
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1991 */       return false;
/*      */     }
/*      */ 
/* 1995 */     int prevCount = counter[0];
/* 1996 */     index = scanHexSequence(address, index, end, counter);
/*      */ 
/* 2001 */     if (index != end) if (index == -1) break label221; label221: return isWellFormedIPv4Address(address.substring(counter[0] > prevCount ? index + 1 : index, end)); } 
/*      */   private static int scanHexSequence(String address, int index, int end, int[] counter) { // Byte code:
/*      */     //   0: iconst_0
/*      */     //   1: istore 5
/*      */     //   3: iload_1
/*      */     //   4: istore 6
/*      */     //   6: iload_1
/*      */     //   7: iload_2
/*      */     //   8: if_icmpge +147 -> 155
/*      */     //   11: aload_0
/*      */     //   12: iload_1
/*      */     //   13: invokevirtual 464	java/lang/String:charAt	(I)C
/*      */     //   16: istore 4
/*      */     //   18: iload 4
/*      */     //   20: bipush 58
/*      */     //   22: if_icmpne +55 -> 77
/*      */     //   25: iload 5
/*      */     //   27: ifle +18 -> 45
/*      */     //   30: aload_3
/*      */     //   31: iconst_0
/*      */     //   32: dup2
/*      */     //   33: iaload
/*      */     //   34: iconst_1
/*      */     //   35: iadd
/*      */     //   36: dup_x2
/*      */     //   37: iastore
/*      */     //   38: bipush 8
/*      */     //   40: if_icmple +5 -> 45
/*      */     //   43: iconst_m1
/*      */     //   44: ireturn
/*      */     //   45: iload 5
/*      */     //   47: ifeq +22 -> 69
/*      */     //   50: iload_1
/*      */     //   51: iconst_1
/*      */     //   52: iadd
/*      */     //   53: iload_2
/*      */     //   54: if_icmpge +17 -> 71
/*      */     //   57: aload_0
/*      */     //   58: iload_1
/*      */     //   59: iconst_1
/*      */     //   60: iadd
/*      */     //   61: invokevirtual 464	java/lang/String:charAt	(I)C
/*      */     //   64: bipush 58
/*      */     //   66: if_icmpne +5 -> 71
/*      */     //   69: iload_1
/*      */     //   70: ireturn
/*      */     //   71: iconst_0
/*      */     //   72: istore 5
/*      */     //   74: goto +75 -> 149
/*      */     //   77: iload 4
/*      */     //   79: invokestatic 422	com/sun/org/apache/xerces/internal/util/URI:isHex	(C)Z
/*      */     //   82: ifne +56 -> 138
/*      */     //   85: iload 4
/*      */     //   87: bipush 46
/*      */     //   89: if_icmpne +47 -> 136
/*      */     //   92: iload 5
/*      */     //   94: iconst_4
/*      */     //   95: if_icmpge +41 -> 136
/*      */     //   98: iload 5
/*      */     //   100: ifle +36 -> 136
/*      */     //   103: aload_3
/*      */     //   104: iconst_0
/*      */     //   105: iaload
/*      */     //   106: bipush 6
/*      */     //   108: if_icmpgt +28 -> 136
/*      */     //   111: iload_1
/*      */     //   112: iload 5
/*      */     //   114: isub
/*      */     //   115: iconst_1
/*      */     //   116: isub
/*      */     //   117: istore 7
/*      */     //   119: iload 7
/*      */     //   121: iload 6
/*      */     //   123: if_icmplt +8 -> 131
/*      */     //   126: iload 7
/*      */     //   128: goto +7 -> 135
/*      */     //   131: iload 7
/*      */     //   133: iconst_1
/*      */     //   134: iadd
/*      */     //   135: ireturn
/*      */     //   136: iconst_m1
/*      */     //   137: ireturn
/*      */     //   138: iinc 5 1
/*      */     //   141: iload 5
/*      */     //   143: iconst_4
/*      */     //   144: if_icmple +5 -> 149
/*      */     //   147: iconst_m1
/*      */     //   148: ireturn
/*      */     //   149: iinc 1 1
/*      */     //   152: goto -146 -> 6
/*      */     //   155: iload 5
/*      */     //   157: ifle +20 -> 177
/*      */     //   160: aload_3
/*      */     //   161: iconst_0
/*      */     //   162: dup2
/*      */     //   163: iaload
/*      */     //   164: iconst_1
/*      */     //   165: iadd
/*      */     //   166: dup_x2
/*      */     //   167: iastore
/*      */     //   168: bipush 8
/*      */     //   170: if_icmpgt +7 -> 177
/*      */     //   173: iload_2
/*      */     //   174: goto +4 -> 178
/*      */     //   177: iconst_m1
/*      */     //   178: ireturn } 
/* 2067 */   private static boolean isDigit(char p_char) { return (p_char >= '0') && (p_char <= '9'); }
/*      */ 
/*      */ 
/*      */   private static boolean isHex(char p_char)
/*      */   {
/* 2077 */     return (p_char <= 'f') && ((fgLookupTable[p_char] & 0x40) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isAlpha(char p_char)
/*      */   {
/* 2086 */     return ((p_char >= 'a') && (p_char <= 'z')) || ((p_char >= 'A') && (p_char <= 'Z'));
/*      */   }
/*      */ 
/*      */   private static boolean isAlphanum(char p_char)
/*      */   {
/* 2095 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x30) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isReservedCharacter(char p_char)
/*      */   {
/* 2105 */     return (p_char <= ']') && ((fgLookupTable[p_char] & 0x1) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isUnreservedCharacter(char p_char)
/*      */   {
/* 2114 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0x32) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isURICharacter(char p_char)
/*      */   {
/* 2124 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0x33) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isSchemeCharacter(char p_char)
/*      */   {
/* 2133 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x34) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isUserinfoCharacter(char p_char)
/*      */   {
/* 2142 */     return (p_char <= 'z') && ((fgLookupTable[p_char] & 0x3A) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isPathCharacter(char p_char)
/*      */   {
/* 2151 */     return (p_char <= '~') && ((fgLookupTable[p_char] & 0xB2) != 0);
/*      */   }
/*      */ 
/*      */   private static boolean isURIString(String p_uric)
/*      */   {
/* 2163 */     if (p_uric == null) {
/* 2164 */       return false;
/*      */     }
/* 2166 */     int end = p_uric.length();
/* 2167 */     char testChar = '\000';
/* 2168 */     for (int i = 0; i < end; i++) {
/* 2169 */       testChar = p_uric.charAt(i);
/* 2170 */       if (testChar == '%') {
/* 2171 */         if ((i + 2 >= end) || (!isHex(p_uric.charAt(i + 1))) || (!isHex(p_uric.charAt(i + 2))))
/*      */         {
/* 2174 */           return false;
/*      */         }
/*      */ 
/* 2177 */         i += 2;
/*      */       }
/* 2181 */       else if (!isURICharacter(testChar))
/*      */       {
/* 2185 */         return false;
/*      */       }
/*      */     }
/* 2188 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  148 */     for (int i = 48; i <= 57; i++)
/*      */     {
/*      */       int tmp21_20 = i;
/*      */       byte[] tmp21_17 = fgLookupTable; tmp21_17[tmp21_20] = ((byte)(tmp21_17[tmp21_20] | 0x60));
/*      */     }
/*      */ 
/*  153 */     for (int i = 65; i <= 70; i++)
/*      */     {
/*      */       int tmp47_46 = i;
/*      */       byte[] tmp47_43 = fgLookupTable; tmp47_43[tmp47_46] = ((byte)(tmp47_43[tmp47_46] | 0x50));
/*      */       int tmp61_60 = (i + 32);
/*      */       byte[] tmp61_54 = fgLookupTable; tmp61_54[tmp61_60] = ((byte)(tmp61_54[tmp61_60] | 0x50));
/*      */     }
/*      */ 
/*  159 */     for (int i = 71; i <= 90; i++)
/*      */     {
/*      */       int tmp87_86 = i;
/*      */       byte[] tmp87_83 = fgLookupTable; tmp87_83[tmp87_86] = ((byte)(tmp87_83[tmp87_86] | 0x10));
/*      */       int tmp101_100 = (i + 32);
/*      */       byte[] tmp101_94 = fgLookupTable; tmp101_94[tmp101_100] = ((byte)(tmp101_94[tmp101_100] | 0x10));
/*      */     }
/*      */     byte[] tmp119_114 = fgLookupTable; tmp119_114[59] = ((byte)(tmp119_114[59] | 0x1));
/*      */     byte[] tmp130_125 = fgLookupTable; tmp130_125[47] = ((byte)(tmp130_125[47] | 0x1));
/*      */     byte[] tmp141_136 = fgLookupTable; tmp141_136[63] = ((byte)(tmp141_136[63] | 0x1));
/*      */     byte[] tmp152_147 = fgLookupTable; tmp152_147[58] = ((byte)(tmp152_147[58] | 0x1));
/*      */     byte[] tmp163_158 = fgLookupTable; tmp163_158[64] = ((byte)(tmp163_158[64] | 0x1));
/*      */     byte[] tmp174_169 = fgLookupTable; tmp174_169[38] = ((byte)(tmp174_169[38] | 0x1));
/*      */     byte[] tmp185_180 = fgLookupTable; tmp185_180[61] = ((byte)(tmp185_180[61] | 0x1));
/*      */     byte[] tmp196_191 = fgLookupTable; tmp196_191[43] = ((byte)(tmp196_191[43] | 0x1));
/*      */     byte[] tmp207_202 = fgLookupTable; tmp207_202[36] = ((byte)(tmp207_202[36] | 0x1));
/*      */     byte[] tmp218_213 = fgLookupTable; tmp218_213[44] = ((byte)(tmp218_213[44] | 0x1));
/*      */     byte[] tmp229_224 = fgLookupTable; tmp229_224[91] = ((byte)(tmp229_224[91] | 0x1));
/*      */     byte[] tmp240_235 = fgLookupTable; tmp240_235[93] = ((byte)(tmp240_235[93] | 0x1));
/*      */     byte[] tmp251_246 = fgLookupTable; tmp251_246[45] = ((byte)(tmp251_246[45] | 0x2));
/*      */     byte[] tmp262_257 = fgLookupTable; tmp262_257[95] = ((byte)(tmp262_257[95] | 0x2));
/*      */     byte[] tmp273_268 = fgLookupTable; tmp273_268[46] = ((byte)(tmp273_268[46] | 0x2));
/*      */     byte[] tmp284_279 = fgLookupTable; tmp284_279[33] = ((byte)(tmp284_279[33] | 0x2));
/*      */     byte[] tmp295_290 = fgLookupTable; tmp295_290[126] = ((byte)(tmp295_290[126] | 0x2));
/*      */     byte[] tmp306_301 = fgLookupTable; tmp306_301[42] = ((byte)(tmp306_301[42] | 0x2));
/*      */     byte[] tmp317_312 = fgLookupTable; tmp317_312[39] = ((byte)(tmp317_312[39] | 0x2));
/*      */     byte[] tmp328_323 = fgLookupTable; tmp328_323[40] = ((byte)(tmp328_323[40] | 0x2));
/*      */     byte[] tmp339_334 = fgLookupTable; tmp339_334[41] = ((byte)(tmp339_334[41] | 0x2));
/*      */     byte[] tmp350_345 = fgLookupTable; tmp350_345[43] = ((byte)(tmp350_345[43] | 0x4));
/*      */     byte[] tmp361_356 = fgLookupTable; tmp361_356[45] = ((byte)(tmp361_356[45] | 0x4));
/*      */     byte[] tmp372_367 = fgLookupTable; tmp372_367[46] = ((byte)(tmp372_367[46] | 0x4));
/*      */     byte[] tmp383_378 = fgLookupTable; tmp383_378[59] = ((byte)(tmp383_378[59] | 0x8));
/*      */     byte[] tmp395_390 = fgLookupTable; tmp395_390[58] = ((byte)(tmp395_390[58] | 0x8));
/*      */     byte[] tmp407_402 = fgLookupTable; tmp407_402[38] = ((byte)(tmp407_402[38] | 0x8));
/*      */     byte[] tmp419_414 = fgLookupTable; tmp419_414[61] = ((byte)(tmp419_414[61] | 0x8));
/*      */     byte[] tmp431_426 = fgLookupTable; tmp431_426[43] = ((byte)(tmp431_426[43] | 0x8));
/*      */     byte[] tmp443_438 = fgLookupTable; tmp443_438[36] = ((byte)(tmp443_438[36] | 0x8));
/*      */     byte[] tmp455_450 = fgLookupTable; tmp455_450[44] = ((byte)(tmp455_450[44] | 0x8));
/*      */     byte[] tmp467_462 = fgLookupTable; tmp467_462[59] = ((byte)(tmp467_462[59] | 0x80));
/*      */     byte[] tmp480_475 = fgLookupTable; tmp480_475[47] = ((byte)(tmp480_475[47] | 0x80));
/*      */     byte[] tmp493_488 = fgLookupTable; tmp493_488[58] = ((byte)(tmp493_488[58] | 0x80));
/*      */     byte[] tmp506_501 = fgLookupTable; tmp506_501[64] = ((byte)(tmp506_501[64] | 0x80));
/*      */     byte[] tmp519_514 = fgLookupTable; tmp519_514[38] = ((byte)(tmp519_514[38] | 0x80));
/*      */     byte[] tmp532_527 = fgLookupTable; tmp532_527[61] = ((byte)(tmp532_527[61] | 0x80));
/*      */     byte[] tmp545_540 = fgLookupTable; tmp545_540[43] = ((byte)(tmp545_540[43] | 0x80));
/*      */     byte[] tmp558_553 = fgLookupTable; tmp558_553[36] = ((byte)(tmp558_553[36] | 0x80));
/*      */     byte[] tmp571_566 = fgLookupTable; tmp571_566[44] = ((byte)(tmp571_566[44] | 0x80));
/*      */   }
/*      */ 
/*      */   public static class MalformedURIException extends IOException
/*      */   {
/*      */     static final long serialVersionUID = -6695054834342951930L;
/*      */ 
/*      */     public MalformedURIException()
/*      */     {
/*      */     }
/*      */ 
/*      */     public MalformedURIException(String p_msg)
/*      */     {
/*   88 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.util.URI
 * JD-Core Version:    0.6.2
 */