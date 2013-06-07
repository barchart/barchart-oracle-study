/*      */ package com.sun.org.apache.xml.internal.utils;
/*      */ 
/*      */ import com.sun.org.apache.xml.internal.res.XMLMessages;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ 
/*      */ public class URI
/*      */   implements Serializable
/*      */ {
/*      */   static final long serialVersionUID = 7096266377907081897L;
/*      */   private static final String RESERVED_CHARACTERS = ";/?:@&=+$,";
/*      */   private static final String MARK_CHARACTERS = "-_.!~*'() ";
/*      */   private static final String SCHEME_CHARACTERS = "+-.";
/*      */   private static final String USERINFO_CHARACTERS = ";:&=+$,";
/*  116 */   private String m_scheme = null;
/*      */ 
/*  120 */   private String m_userinfo = null;
/*      */ 
/*  124 */   private String m_host = null;
/*      */ 
/*  128 */   private int m_port = -1;
/*      */ 
/*  132 */   private String m_path = null;
/*      */ 
/*  139 */   private String m_queryString = null;
/*      */ 
/*  143 */   private String m_fragment = null;
/*      */ 
/*  146 */   private static boolean DEBUG = false;
/*      */ 
/*      */   public URI()
/*      */   {
/*      */   }
/*      */ 
/*      */   public URI(URI p_other)
/*      */   {
/*  161 */     initialize(p_other);
/*      */   }
/*      */ 
/*      */   public URI(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  181 */     this((URI)null, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  198 */     initialize(p_base, p_uriSpec);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_schemeSpecificPart)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  217 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0))
/*      */     {
/*  219 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
/*      */     }
/*      */ 
/*  223 */     if ((p_schemeSpecificPart == null) || (p_schemeSpecificPart.trim().length() == 0))
/*      */     {
/*  226 */       throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
/*      */     }
/*      */ 
/*  230 */     setScheme(p_scheme);
/*  231 */     setPath(p_schemeSpecificPart);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  258 */     this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
/*      */   }
/*      */ 
/*      */   public URI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  290 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0))
/*      */     {
/*  292 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_REQUIRED", null));
/*      */     }
/*      */ 
/*  295 */     if (p_host == null)
/*      */     {
/*  297 */       if (p_userinfo != null)
/*      */       {
/*  299 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_USERINFO_IF_NO_HOST", null));
/*      */       }
/*      */ 
/*  303 */       if (p_port != -1)
/*      */       {
/*  305 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_PORT_IF_NO_HOST", null));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  310 */     if (p_path != null)
/*      */     {
/*  312 */       if ((p_path.indexOf('?') != -1) && (p_queryString != null))
/*      */       {
/*  314 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_QUERY_STRING_IN_PATH", null));
/*      */       }
/*      */ 
/*  318 */       if ((p_path.indexOf('#') != -1) && (p_fragment != null))
/*      */       {
/*  320 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_FRAGMENT_STRING_IN_PATH", null));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  325 */     setScheme(p_scheme);
/*  326 */     setHost(p_host);
/*  327 */     setPort(p_port);
/*  328 */     setUserinfo(p_userinfo);
/*  329 */     setPath(p_path);
/*  330 */     setQueryString(p_queryString);
/*  331 */     setFragment(p_fragment);
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_other)
/*      */   {
/*  342 */     this.m_scheme = p_other.getScheme();
/*  343 */     this.m_userinfo = p_other.getUserinfo();
/*  344 */     this.m_host = p_other.getHost();
/*  345 */     this.m_port = p_other.getPort();
/*  346 */     this.m_path = p_other.getPath();
/*  347 */     this.m_queryString = p_other.getQueryString();
/*  348 */     this.m_fragment = p_other.getFragment();
/*      */   }
/*      */ 
/*      */   private void initialize(URI p_base, String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  371 */     if ((p_base == null) && ((p_uriSpec == null) || (p_uriSpec.trim().length() == 0)))
/*      */     {
/*  374 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_CANNOT_INIT_URI_EMPTY_PARMS", null));
/*      */     }
/*      */ 
/*  379 */     if ((p_uriSpec == null) || (p_uriSpec.trim().length() == 0))
/*      */     {
/*  381 */       initialize(p_base);
/*      */ 
/*  383 */       return;
/*      */     }
/*      */ 
/*  386 */     String uriSpec = p_uriSpec.trim();
/*  387 */     int uriSpecLen = uriSpec.length();
/*  388 */     int index = 0;
/*      */ 
/*  391 */     int colonIndex = uriSpec.indexOf(':');
/*  392 */     if (colonIndex < 0)
/*      */     {
/*  394 */       if (p_base == null)
/*      */       {
/*  396 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_SCHEME_IN_URI", new Object[] { uriSpec }));
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  401 */       initializeScheme(uriSpec);
/*  402 */       uriSpec = uriSpec.substring(colonIndex + 1);
/*      */ 
/*  404 */       if ((this.m_scheme != null) && (p_base != null))
/*      */       {
/*  420 */         if ((uriSpec.startsWith("/")) || (!this.m_scheme.equals(p_base.m_scheme)) || (!p_base.getSchemeSpecificPart().startsWith("/")))
/*      */         {
/*  422 */           p_base = null;
/*      */         }
/*      */       }
/*      */ 
/*  426 */       uriSpecLen = uriSpec.length();
/*      */     }
/*      */ 
/*  430 */     if ((index + 1 < uriSpecLen) && (uriSpec.substring(index).startsWith("//")))
/*      */     {
/*  433 */       index += 2;
/*      */ 
/*  435 */       int startPos = index;
/*      */ 
/*  438 */       char testChar = '\000';
/*      */ 
/*  440 */       while (index < uriSpecLen)
/*      */       {
/*  442 */         testChar = uriSpec.charAt(index);
/*      */ 
/*  444 */         if ((testChar == '/') || (testChar == '?') || (testChar == '#'))
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  449 */         index++;
/*      */       }
/*      */ 
/*  454 */       if (index > startPos)
/*      */       {
/*  456 */         initializeAuthority(uriSpec.substring(startPos, index));
/*      */       }
/*      */       else
/*      */       {
/*  460 */         this.m_host = "";
/*      */       }
/*      */     }
/*      */ 
/*  464 */     initializePath(uriSpec.substring(index));
/*      */ 
/*  471 */     if (p_base != null)
/*      */     {
/*  481 */       if ((this.m_path.length() == 0) && (this.m_scheme == null) && (this.m_host == null))
/*      */       {
/*  483 */         this.m_scheme = p_base.getScheme();
/*  484 */         this.m_userinfo = p_base.getUserinfo();
/*  485 */         this.m_host = p_base.getHost();
/*  486 */         this.m_port = p_base.getPort();
/*  487 */         this.m_path = p_base.getPath();
/*      */ 
/*  489 */         if (this.m_queryString == null)
/*      */         {
/*  491 */           this.m_queryString = p_base.getQueryString();
/*      */         }
/*      */ 
/*  494 */         return;
/*      */       }
/*      */ 
/*  499 */       if (this.m_scheme == null)
/*      */       {
/*  501 */         this.m_scheme = p_base.getScheme();
/*      */       }
/*      */ 
/*  506 */       if (this.m_host == null)
/*      */       {
/*  508 */         this.m_userinfo = p_base.getUserinfo();
/*  509 */         this.m_host = p_base.getHost();
/*  510 */         this.m_port = p_base.getPort();
/*      */       }
/*      */       else
/*      */       {
/*  514 */         return;
/*      */       }
/*      */ 
/*  518 */       if ((this.m_path.length() > 0) && (this.m_path.startsWith("/")))
/*      */       {
/*  520 */         return;
/*      */       }
/*      */ 
/*  525 */       String path = "";
/*  526 */       String basePath = p_base.getPath();
/*      */ 
/*  529 */       if (basePath != null)
/*      */       {
/*  531 */         int lastSlash = basePath.lastIndexOf('/');
/*      */ 
/*  533 */         if (lastSlash != -1)
/*      */         {
/*  535 */           path = basePath.substring(0, lastSlash + 1);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  540 */       path = path.concat(this.m_path);
/*      */ 
/*  543 */       index = -1;
/*      */ 
/*  545 */       while ((index = path.indexOf("/./")) != -1)
/*      */       {
/*  547 */         path = path.substring(0, index + 1).concat(path.substring(index + 3));
/*      */       }
/*      */ 
/*  551 */       if (path.endsWith("/."))
/*      */       {
/*  553 */         path = path.substring(0, path.length() - 1);
/*      */       }
/*      */ 
/*  558 */       index = -1;
/*      */ 
/*  560 */       int segIndex = -1;
/*  561 */       String tempString = null;
/*      */ 
/*  563 */       while ((index = path.indexOf("/../")) > 0)
/*      */       {
/*  565 */         tempString = path.substring(0, path.indexOf("/../"));
/*  566 */         segIndex = tempString.lastIndexOf('/');
/*      */ 
/*  568 */         if ((segIndex != -1) && 
/*  570 */           (!tempString.substring(segIndex++).equals("..")))
/*      */         {
/*  572 */           path = path.substring(0, segIndex).concat(path.substring(index + 4));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  580 */       if (path.endsWith("/.."))
/*      */       {
/*  582 */         tempString = path.substring(0, path.length() - 3);
/*  583 */         segIndex = tempString.lastIndexOf('/');
/*      */ 
/*  585 */         if (segIndex != -1)
/*      */         {
/*  587 */           path = path.substring(0, segIndex + 1);
/*      */         }
/*      */       }
/*      */ 
/*  591 */       this.m_path = path;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeScheme(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  606 */     int uriSpecLen = p_uriSpec.length();
/*  607 */     int index = 0;
/*  608 */     String scheme = null;
/*  609 */     char testChar = '\000';
/*      */ 
/*  611 */     while (index < uriSpecLen)
/*      */     {
/*  613 */       testChar = p_uriSpec.charAt(index);
/*      */ 
/*  615 */       if ((testChar == ':') || (testChar == '/') || (testChar == '?') || (testChar == '#'))
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  621 */       index++;
/*      */     }
/*      */ 
/*  624 */     scheme = p_uriSpec.substring(0, index);
/*      */ 
/*  626 */     if (scheme.length() == 0)
/*      */     {
/*  628 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_SCHEME_INURI", null));
/*      */     }
/*      */ 
/*  632 */     setScheme(scheme);
/*      */   }
/*      */ 
/*      */   private void initializeAuthority(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  648 */     int index = 0;
/*  649 */     int start = 0;
/*  650 */     int end = p_uriSpec.length();
/*  651 */     char testChar = '\000';
/*  652 */     String userinfo = null;
/*      */ 
/*  655 */     if (p_uriSpec.indexOf('@', start) != -1)
/*      */     {
/*  657 */       while (index < end)
/*      */       {
/*  659 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/*  661 */         if (testChar == '@')
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  666 */         index++;
/*      */       }
/*      */ 
/*  669 */       userinfo = p_uriSpec.substring(start, index);
/*      */ 
/*  671 */       index++;
/*      */     }
/*      */ 
/*  675 */     String host = null;
/*      */ 
/*  677 */     start = index;
/*      */ 
/*  679 */     while (index < end)
/*      */     {
/*  681 */       testChar = p_uriSpec.charAt(index);
/*      */ 
/*  683 */       if (testChar == ':')
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  688 */       index++;
/*      */     }
/*      */ 
/*  691 */     host = p_uriSpec.substring(start, index);
/*      */ 
/*  693 */     int port = -1;
/*      */ 
/*  695 */     if (host.length() > 0)
/*      */     {
/*  699 */       if (testChar == ':')
/*      */       {
/*  701 */         index++;
/*      */ 
/*  703 */         start = index;
/*      */ 
/*  705 */         while (index < end)
/*      */         {
/*  707 */           index++;
/*      */         }
/*      */ 
/*  710 */         String portStr = p_uriSpec.substring(start, index);
/*      */ 
/*  712 */         if (portStr.length() > 0)
/*      */         {
/*  714 */           for (int i = 0; i < portStr.length(); i++)
/*      */           {
/*  716 */             if (!isDigit(portStr.charAt(i)))
/*      */             {
/*  718 */               throw new MalformedURIException(portStr + " is invalid. Port should only contain digits!");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/*  725 */             port = Integer.parseInt(portStr);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  736 */     setHost(host);
/*  737 */     setPort(port);
/*  738 */     setUserinfo(userinfo);
/*      */   }
/*      */ 
/*      */   private void initializePath(String p_uriSpec)
/*      */     throws URI.MalformedURIException
/*      */   {
/*  751 */     if (p_uriSpec == null)
/*      */     {
/*  753 */       throw new MalformedURIException("Cannot initialize path from null string!");
/*      */     }
/*      */ 
/*  757 */     int index = 0;
/*  758 */     int start = 0;
/*  759 */     int end = p_uriSpec.length();
/*  760 */     char testChar = '\000';
/*      */ 
/*  763 */     while (index < end)
/*      */     {
/*  765 */       testChar = p_uriSpec.charAt(index);
/*      */ 
/*  767 */       if ((testChar == '?') || (testChar == '#'))
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  773 */       if (testChar == '%')
/*      */       {
/*  775 */         if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */         {
/*  778 */           throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", null));
/*      */         }
/*      */ 
/*      */       }
/*  782 */       else if ((!isReservedCharacter(testChar)) && (!isUnreservedCharacter(testChar)))
/*      */       {
/*  785 */         if ('\\' != testChar) {
/*  786 */           throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_INVALID_CHAR", new Object[] { String.valueOf(testChar) }));
/*      */         }
/*      */       }
/*      */ 
/*  790 */       index++;
/*      */     }
/*      */ 
/*  793 */     this.m_path = p_uriSpec.substring(start, index);
/*      */ 
/*  796 */     if (testChar == '?')
/*      */     {
/*  798 */       index++;
/*      */ 
/*  800 */       start = index;
/*      */ 
/*  802 */       while (index < end)
/*      */       {
/*  804 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/*  806 */         if (testChar == '#')
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  811 */         if (testChar == '%')
/*      */         {
/*  813 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/*  816 */             throw new MalformedURIException("Query string contains invalid escape sequence!");
/*      */           }
/*      */ 
/*      */         }
/*  820 */         else if ((!isReservedCharacter(testChar)) && (!isUnreservedCharacter(testChar)))
/*      */         {
/*  823 */           throw new MalformedURIException("Query string contains invalid character:" + testChar);
/*      */         }
/*      */ 
/*  827 */         index++;
/*      */       }
/*      */ 
/*  830 */       this.m_queryString = p_uriSpec.substring(start, index);
/*      */     }
/*      */ 
/*  834 */     if (testChar == '#')
/*      */     {
/*  836 */       index++;
/*      */ 
/*  838 */       start = index;
/*      */ 
/*  840 */       while (index < end)
/*      */       {
/*  842 */         testChar = p_uriSpec.charAt(index);
/*      */ 
/*  844 */         if (testChar == '%')
/*      */         {
/*  846 */           if ((index + 2 >= end) || (!isHex(p_uriSpec.charAt(index + 1))) || (!isHex(p_uriSpec.charAt(index + 2))))
/*      */           {
/*  849 */             throw new MalformedURIException("Fragment contains invalid escape sequence!");
/*      */           }
/*      */ 
/*      */         }
/*  853 */         else if ((!isReservedCharacter(testChar)) && (!isUnreservedCharacter(testChar)))
/*      */         {
/*  856 */           throw new MalformedURIException("Fragment contains invalid character:" + testChar);
/*      */         }
/*      */ 
/*  860 */         index++;
/*      */       }
/*      */ 
/*  863 */       this.m_fragment = p_uriSpec.substring(start, index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getScheme()
/*      */   {
/*  874 */     return this.m_scheme;
/*      */   }
/*      */ 
/*      */   public String getSchemeSpecificPart()
/*      */   {
/*  886 */     StringBuffer schemespec = new StringBuffer();
/*      */ 
/*  888 */     if ((this.m_userinfo != null) || (this.m_host != null) || (this.m_port != -1))
/*      */     {
/*  890 */       schemespec.append("//");
/*      */     }
/*      */ 
/*  893 */     if (this.m_userinfo != null)
/*      */     {
/*  895 */       schemespec.append(this.m_userinfo);
/*  896 */       schemespec.append('@');
/*      */     }
/*      */ 
/*  899 */     if (this.m_host != null)
/*      */     {
/*  901 */       schemespec.append(this.m_host);
/*      */     }
/*      */ 
/*  904 */     if (this.m_port != -1)
/*      */     {
/*  906 */       schemespec.append(':');
/*  907 */       schemespec.append(this.m_port);
/*      */     }
/*      */ 
/*  910 */     if (this.m_path != null)
/*      */     {
/*  912 */       schemespec.append(this.m_path);
/*      */     }
/*      */ 
/*  915 */     if (this.m_queryString != null)
/*      */     {
/*  917 */       schemespec.append('?');
/*  918 */       schemespec.append(this.m_queryString);
/*      */     }
/*      */ 
/*  921 */     if (this.m_fragment != null)
/*      */     {
/*  923 */       schemespec.append('#');
/*  924 */       schemespec.append(this.m_fragment);
/*      */     }
/*      */ 
/*  927 */     return schemespec.toString();
/*      */   }
/*      */ 
/*      */   public String getUserinfo()
/*      */   {
/*  937 */     return this.m_userinfo;
/*      */   }
/*      */ 
/*      */   public String getHost()
/*      */   {
/*  947 */     return this.m_host;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  957 */     return this.m_port;
/*      */   }
/*      */ 
/*      */   public String getPath(boolean p_includeQueryString, boolean p_includeFragment)
/*      */   {
/*  978 */     StringBuffer pathString = new StringBuffer(this.m_path);
/*      */ 
/*  980 */     if ((p_includeQueryString) && (this.m_queryString != null))
/*      */     {
/*  982 */       pathString.append('?');
/*  983 */       pathString.append(this.m_queryString);
/*      */     }
/*      */ 
/*  986 */     if ((p_includeFragment) && (this.m_fragment != null))
/*      */     {
/*  988 */       pathString.append('#');
/*  989 */       pathString.append(this.m_fragment);
/*      */     }
/*      */ 
/*  992 */     return pathString.toString();
/*      */   }
/*      */ 
/*      */   public String getPath()
/*      */   {
/* 1003 */     return this.m_path;
/*      */   }
/*      */ 
/*      */   public String getQueryString()
/*      */   {
/* 1015 */     return this.m_queryString;
/*      */   }
/*      */ 
/*      */   public String getFragment()
/*      */   {
/* 1027 */     return this.m_fragment;
/*      */   }
/*      */ 
/*      */   public void setScheme(String p_scheme)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1042 */     if (p_scheme == null)
/*      */     {
/* 1044 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_FROM_NULL_STRING", null));
/*      */     }
/*      */ 
/* 1047 */     if (!isConformantSchemeName(p_scheme))
/*      */     {
/* 1049 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_NOT_CONFORMANT", null));
/*      */     }
/*      */ 
/* 1052 */     this.m_scheme = p_scheme.toLowerCase();
/*      */   }
/*      */ 
/*      */   public void setUserinfo(String p_userinfo)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1067 */     if (p_userinfo == null)
/*      */     {
/* 1069 */       this.m_userinfo = null;
/*      */     }
/*      */     else
/*      */     {
/* 1073 */       if (this.m_host == null)
/*      */       {
/* 1075 */         throw new MalformedURIException("Userinfo cannot be set when host is null!");
/*      */       }
/*      */ 
/* 1081 */       int index = 0;
/* 1082 */       int end = p_userinfo.length();
/* 1083 */       char testChar = '\000';
/*      */ 
/* 1085 */       while (index < end)
/*      */       {
/* 1087 */         testChar = p_userinfo.charAt(index);
/*      */ 
/* 1089 */         if (testChar == '%')
/*      */         {
/* 1091 */           if ((index + 2 >= end) || (!isHex(p_userinfo.charAt(index + 1))) || (!isHex(p_userinfo.charAt(index + 2))))
/*      */           {
/* 1094 */             throw new MalformedURIException("Userinfo contains invalid escape sequence!");
/*      */           }
/*      */ 
/*      */         }
/* 1098 */         else if ((!isUnreservedCharacter(testChar)) && (";:&=+$,".indexOf(testChar) == -1))
/*      */         {
/* 1101 */           throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
/*      */         }
/*      */ 
/* 1105 */         index++;
/*      */       }
/*      */     }
/*      */ 
/* 1109 */     this.m_userinfo = p_userinfo;
/*      */   }
/*      */ 
/*      */   public void setHost(String p_host)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1124 */     if ((p_host == null) || (p_host.trim().length() == 0))
/*      */     {
/* 1126 */       this.m_host = p_host;
/* 1127 */       this.m_userinfo = null;
/* 1128 */       this.m_port = -1;
/*      */     }
/* 1130 */     else if (!isWellFormedAddress(p_host))
/*      */     {
/* 1132 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_HOST_ADDRESS_NOT_WELLFORMED", null));
/*      */     }
/*      */ 
/* 1135 */     this.m_host = p_host;
/*      */   }
/*      */ 
/*      */   public void setPort(int p_port)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1152 */     if ((p_port >= 0) && (p_port <= 65535))
/*      */     {
/* 1154 */       if (this.m_host == null)
/*      */       {
/* 1156 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PORT_WHEN_HOST_NULL", null));
/*      */       }
/*      */ 
/*      */     }
/* 1160 */     else if (p_port != -1)
/*      */     {
/* 1162 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_INVALID_PORT", null));
/*      */     }
/*      */ 
/* 1165 */     this.m_port = p_port;
/*      */   }
/*      */ 
/*      */   public void setPath(String p_path)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1185 */     if (p_path == null)
/*      */     {
/* 1187 */       this.m_path = null;
/* 1188 */       this.m_queryString = null;
/* 1189 */       this.m_fragment = null;
/*      */     }
/*      */     else
/*      */     {
/* 1193 */       initializePath(p_path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void appendPath(String p_addToPath)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1213 */     if ((p_addToPath == null) || (p_addToPath.trim().length() == 0))
/*      */     {
/* 1215 */       return;
/*      */     }
/*      */ 
/* 1218 */     if (!isURIString(p_addToPath))
/*      */     {
/* 1220 */       throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_INVALID_CHAR", new Object[] { p_addToPath }));
/*      */     }
/*      */ 
/* 1223 */     if ((this.m_path == null) || (this.m_path.trim().length() == 0))
/*      */     {
/* 1225 */       if (p_addToPath.startsWith("/"))
/*      */       {
/* 1227 */         this.m_path = p_addToPath;
/*      */       }
/*      */       else
/*      */       {
/* 1231 */         this.m_path = ("/" + p_addToPath);
/*      */       }
/*      */     }
/* 1234 */     else if (this.m_path.endsWith("/"))
/*      */     {
/* 1236 */       if (p_addToPath.startsWith("/"))
/*      */       {
/* 1238 */         this.m_path = this.m_path.concat(p_addToPath.substring(1));
/*      */       }
/*      */       else
/*      */       {
/* 1242 */         this.m_path = this.m_path.concat(p_addToPath);
/*      */       }
/*      */ 
/*      */     }
/* 1247 */     else if (p_addToPath.startsWith("/"))
/*      */     {
/* 1249 */       this.m_path = this.m_path.concat(p_addToPath);
/*      */     }
/*      */     else
/*      */     {
/* 1253 */       this.m_path = this.m_path.concat("/" + p_addToPath);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setQueryString(String p_queryString)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1273 */     if (p_queryString == null)
/*      */     {
/* 1275 */       this.m_queryString = null;
/*      */     } else {
/* 1277 */       if (!isGenericURI())
/*      */       {
/* 1279 */         throw new MalformedURIException("Query string can only be set for a generic URI!");
/*      */       }
/*      */ 
/* 1282 */       if (getPath() == null)
/*      */       {
/* 1284 */         throw new MalformedURIException("Query string cannot be set when path is null!");
/*      */       }
/*      */ 
/* 1287 */       if (!isURIString(p_queryString))
/*      */       {
/* 1289 */         throw new MalformedURIException("Query string contains invalid character!");
/*      */       }
/*      */ 
/* 1294 */       this.m_queryString = p_queryString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFragment(String p_fragment)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1312 */     if (p_fragment == null)
/*      */     {
/* 1314 */       this.m_fragment = null;
/*      */     } else {
/* 1316 */       if (!isGenericURI())
/*      */       {
/* 1318 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_FOR_GENERIC_URI", null));
/*      */       }
/*      */ 
/* 1321 */       if (getPath() == null)
/*      */       {
/* 1323 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_WHEN_PATH_NULL", null));
/*      */       }
/*      */ 
/* 1326 */       if (!isURIString(p_fragment))
/*      */       {
/* 1328 */         throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_INVALID_CHAR", null));
/*      */       }
/*      */ 
/* 1332 */       this.m_fragment = p_fragment;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean equals(Object p_test)
/*      */   {
/* 1347 */     if ((p_test instanceof URI))
/*      */     {
/* 1349 */       URI testURI = (URI)p_test;
/*      */ 
/* 1351 */       if (((this.m_scheme == null) && (testURI.m_scheme == null)) || ((this.m_scheme != null) && (testURI.m_scheme != null) && (this.m_scheme.equals(testURI.m_scheme)) && (((this.m_userinfo == null) && (testURI.m_userinfo == null)) || ((this.m_userinfo != null) && (testURI.m_userinfo != null) && (this.m_userinfo.equals(testURI.m_userinfo)) && (((this.m_host == null) && (testURI.m_host == null)) || ((this.m_host != null) && (testURI.m_host != null) && (this.m_host.equals(testURI.m_host)) && (this.m_port == testURI.m_port) && (((this.m_path == null) && (testURI.m_path == null)) || ((this.m_path != null) && (testURI.m_path != null) && (this.m_path.equals(testURI.m_path)) && (((this.m_queryString == null) && (testURI.m_queryString == null)) || ((this.m_queryString != null) && (testURI.m_queryString != null) && (this.m_queryString.equals(testURI.m_queryString)) && (((this.m_fragment == null) && (testURI.m_fragment == null)) || ((this.m_fragment != null) && (testURI.m_fragment != null) && (this.m_fragment.equals(testURI.m_fragment))))))))))))))
/*      */       {
/* 1359 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1363 */     return false;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1374 */     StringBuffer uriSpecString = new StringBuffer();
/*      */ 
/* 1376 */     if (this.m_scheme != null)
/*      */     {
/* 1378 */       uriSpecString.append(this.m_scheme);
/* 1379 */       uriSpecString.append(':');
/*      */     }
/*      */ 
/* 1382 */     uriSpecString.append(getSchemeSpecificPart());
/*      */ 
/* 1384 */     return uriSpecString.toString();
/*      */   }
/*      */ 
/*      */   public boolean isGenericURI()
/*      */   {
/* 1399 */     return this.m_host != null;
/*      */   }
/*      */ 
/*      */   public static boolean isConformantSchemeName(String p_scheme)
/*      */   {
/* 1414 */     if ((p_scheme == null) || (p_scheme.trim().length() == 0))
/*      */     {
/* 1416 */       return false;
/*      */     }
/*      */ 
/* 1419 */     if (!isAlpha(p_scheme.charAt(0)))
/*      */     {
/* 1421 */       return false;
/*      */     }
/*      */ 
/* 1426 */     for (int i = 1; i < p_scheme.length(); i++)
/*      */     {
/* 1428 */       char testChar = p_scheme.charAt(i);
/*      */ 
/* 1430 */       if ((!isAlphanum(testChar)) && ("+-.".indexOf(testChar) == -1))
/*      */       {
/* 1432 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1436 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean isWellFormedAddress(String p_address)
/*      */   {
/* 1455 */     if (p_address == null)
/*      */     {
/* 1457 */       return false;
/*      */     }
/*      */ 
/* 1460 */     String address = p_address.trim();
/* 1461 */     int addrLength = address.length();
/*      */ 
/* 1463 */     if ((addrLength == 0) || (addrLength > 255))
/*      */     {
/* 1465 */       return false;
/*      */     }
/*      */ 
/* 1468 */     if ((address.startsWith(".")) || (address.startsWith("-")))
/*      */     {
/* 1470 */       return false;
/*      */     }
/*      */ 
/* 1476 */     int index = address.lastIndexOf('.');
/*      */ 
/* 1478 */     if (address.endsWith("."))
/*      */     {
/* 1480 */       index = address.substring(0, index).lastIndexOf('.');
/*      */     }
/*      */ 
/* 1483 */     if ((index + 1 < addrLength) && (isDigit(p_address.charAt(index + 1))))
/*      */     {
/* 1486 */       int numDots = 0;
/*      */ 
/* 1491 */       for (int i = 0; i < addrLength; i++)
/*      */       {
/* 1493 */         char testChar = address.charAt(i);
/*      */ 
/* 1495 */         if (testChar == '.')
/*      */         {
/* 1497 */           if ((!isDigit(address.charAt(i - 1))) || ((i + 1 < addrLength) && (!isDigit(address.charAt(i + 1)))))
/*      */           {
/* 1500 */             return false;
/*      */           }
/*      */ 
/* 1503 */           numDots++;
/*      */         }
/* 1505 */         else if (!isDigit(testChar))
/*      */         {
/* 1507 */           return false;
/*      */         }
/*      */       }
/*      */ 
/* 1511 */       if (numDots != 3)
/*      */       {
/* 1513 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1523 */       for (int i = 0; i < addrLength; i++)
/*      */       {
/* 1525 */         char testChar = address.charAt(i);
/*      */ 
/* 1527 */         if (testChar == '.')
/*      */         {
/* 1529 */           if (!isAlphanum(address.charAt(i - 1)))
/*      */           {
/* 1531 */             return false;
/*      */           }
/*      */ 
/* 1534 */           if ((i + 1 < addrLength) && (!isAlphanum(address.charAt(i + 1))))
/*      */           {
/* 1536 */             return false;
/*      */           }
/*      */         }
/* 1539 */         else if ((!isAlphanum(testChar)) && (testChar != '-'))
/*      */         {
/* 1541 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1546 */     return true;
/*      */   }
/*      */ 
/*      */   private static boolean isDigit(char p_char)
/*      */   {
/* 1558 */     return (p_char >= '0') && (p_char <= '9');
/*      */   }
/*      */ 
/*      */   private static boolean isHex(char p_char)
/*      */   {
/* 1571 */     return (isDigit(p_char)) || ((p_char >= 'a') && (p_char <= 'f')) || ((p_char >= 'A') && (p_char <= 'F'));
/*      */   }
/*      */ 
/*      */   private static boolean isAlpha(char p_char)
/*      */   {
/* 1584 */     return ((p_char >= 'a') && (p_char <= 'z')) || ((p_char >= 'A') && (p_char <= 'Z'));
/*      */   }
/*      */ 
/*      */   private static boolean isAlphanum(char p_char)
/*      */   {
/* 1597 */     return (isAlpha(p_char)) || (isDigit(p_char));
/*      */   }
/*      */ 
/*      */   private static boolean isReservedCharacter(char p_char)
/*      */   {
/* 1610 */     return ";/?:@&=+$,".indexOf(p_char) != -1;
/*      */   }
/*      */ 
/*      */   private static boolean isUnreservedCharacter(char p_char)
/*      */   {
/* 1622 */     return (isAlphanum(p_char)) || ("-_.!~*'() ".indexOf(p_char) != -1);
/*      */   }
/*      */ 
/*      */   private static boolean isURIString(String p_uric)
/*      */   {
/* 1637 */     if (p_uric == null)
/*      */     {
/* 1639 */       return false;
/*      */     }
/*      */ 
/* 1642 */     int end = p_uric.length();
/* 1643 */     char testChar = '\000';
/*      */ 
/* 1645 */     for (int i = 0; i < end; i++)
/*      */     {
/* 1647 */       testChar = p_uric.charAt(i);
/*      */ 
/* 1649 */       if (testChar == '%')
/*      */       {
/* 1651 */         if ((i + 2 >= end) || (!isHex(p_uric.charAt(i + 1))) || (!isHex(p_uric.charAt(i + 2))))
/*      */         {
/* 1654 */           return false;
/*      */         }
/*      */ 
/* 1658 */         i += 2;
/*      */       }
/* 1664 */       else if ((!isReservedCharacter(testChar)) && (!isUnreservedCharacter(testChar)))
/*      */       {
/* 1670 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1674 */     return true;
/*      */   }
/*      */ 
/*      */   public static class MalformedURIException extends IOException
/*      */   {
/*      */     public MalformedURIException()
/*      */     {
/*      */     }
/*      */ 
/*      */     public MalformedURIException(String p_msg)
/*      */     {
/*   92 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.URI
 * JD-Core Version:    0.6.2
 */