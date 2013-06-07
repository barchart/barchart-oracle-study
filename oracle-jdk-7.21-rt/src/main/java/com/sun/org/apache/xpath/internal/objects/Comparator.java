package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

abstract class Comparator
{
  abstract boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2);

  abstract boolean compareNumbers(double paramDouble1, double paramDouble2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.objects.Comparator
 * JD-Core Version:    0.6.2
 */