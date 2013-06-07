/*      */ package com.sun.rowset.internal;
/*      */ 
/*      */ import com.sun.rowset.CachedRowSetImpl;
/*      */ import com.sun.rowset.JdbcRowSetResourceBundle;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Struct;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import javax.sql.RowSetInternal;
/*      */ import javax.sql.rowset.CachedRowSet;
/*      */ import javax.sql.rowset.RowSetMetaDataImpl;
/*      */ import javax.sql.rowset.serial.SQLInputImpl;
/*      */ import javax.sql.rowset.serial.SerialArray;
/*      */ import javax.sql.rowset.serial.SerialBlob;
/*      */ import javax.sql.rowset.serial.SerialClob;
/*      */ import javax.sql.rowset.serial.SerialStruct;
/*      */ import javax.sql.rowset.spi.SyncProviderException;
/*      */ import javax.sql.rowset.spi.TransactionalWriter;
/*      */ 
/*      */ public class CachedRowSetWriter
/*      */   implements TransactionalWriter, Serializable
/*      */ {
/*      */   private transient Connection con;
/*      */   private String selectCmd;
/*      */   private String updateCmd;
/*      */   private String updateWhere;
/*      */   private String deleteCmd;
/*      */   private String deleteWhere;
/*      */   private String insertCmd;
/*      */   private int[] keyCols;
/*      */   private Object[] params;
/*      */   private CachedRowSetReader reader;
/*      */   private ResultSetMetaData callerMd;
/*      */   private int callerColumnCount;
/*      */   private CachedRowSetImpl crsResolve;
/*      */   private ArrayList status;
/*      */   private int iChangedValsInDbAndCRS;
/*      */   private int iChangedValsinDbOnly;
/*      */   private JdbcRowSetResourceBundle resBundle;
/*      */   static final long serialVersionUID = -8506030970299413976L;
/*      */ 
/*      */   public CachedRowSetWriter()
/*      */   {
/*      */     try
/*      */     {
/*  204 */       this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*      */     } catch (IOException localIOException) {
/*  206 */       throw new RuntimeException(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean writeData(RowSetInternal paramRowSetInternal)
/*      */     throws SQLException
/*      */   {
/*  267 */     boolean bool1 = false;
/*  268 */     boolean bool2 = false;
/*  269 */     PreparedStatement localPreparedStatement = null;
/*  270 */     this.iChangedValsInDbAndCRS = 0;
/*  271 */     this.iChangedValsinDbOnly = 0;
/*      */ 
/*  274 */     CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)paramRowSetInternal;
/*      */ 
/*  276 */     this.crsResolve = new CachedRowSetImpl();
/*      */ 
/*  282 */     this.con = this.reader.connect(paramRowSetInternal);
/*      */ 
/*  285 */     if (this.con == null) {
/*  286 */       throw new SQLException(this.resBundle.handleGetObject("crswriter.connect").toString());
/*      */     }
/*      */ 
/*  300 */     initSQLStatements(localCachedRowSetImpl);
/*      */ 
/*  303 */     RowSetMetaDataImpl localRowSetMetaDataImpl1 = (RowSetMetaDataImpl)localCachedRowSetImpl.getMetaData();
/*  304 */     RowSetMetaDataImpl localRowSetMetaDataImpl2 = new RowSetMetaDataImpl();
/*      */ 
/*  306 */     int i = localRowSetMetaDataImpl1.getColumnCount();
/*  307 */     int j = localCachedRowSetImpl.size() + 1;
/*  308 */     this.status = new ArrayList(j);
/*      */ 
/*  310 */     this.status.add(0, null);
/*  311 */     localRowSetMetaDataImpl2.setColumnCount(i);
/*      */ 
/*  313 */     for (int k = 1; k <= i; k++) {
/*  314 */       localRowSetMetaDataImpl2.setColumnType(k, localRowSetMetaDataImpl1.getColumnType(k));
/*  315 */       localRowSetMetaDataImpl2.setColumnName(k, localRowSetMetaDataImpl1.getColumnName(k));
/*  316 */       localRowSetMetaDataImpl2.setNullable(k, 2);
/*      */     }
/*  318 */     this.crsResolve.setMetaData(localRowSetMetaDataImpl2);
/*      */ 
/*  323 */     if (this.callerColumnCount < 1)
/*      */     {
/*  325 */       if (this.reader.getCloseConnection() == true)
/*  326 */         this.con.close();
/*  327 */       return true;
/*      */     }
/*      */ 
/*  330 */     bool2 = localCachedRowSetImpl.getShowDeleted();
/*  331 */     localCachedRowSetImpl.setShowDeleted(true);
/*      */ 
/*  334 */     localCachedRowSetImpl.beforeFirst();
/*      */ 
/*  336 */     k = 1;
/*  337 */     while (localCachedRowSetImpl.next()) {
/*  338 */       if (localCachedRowSetImpl.rowDeleted())
/*      */       {
/*  340 */         if ((bool1 = deleteOriginalRow(localCachedRowSetImpl, this.crsResolve) == true ? 1 : 0) != 0) {
/*  341 */           this.status.add(k, Integer.valueOf(1));
/*      */         }
/*      */         else
/*      */         {
/*  345 */           this.status.add(k, Integer.valueOf(3));
/*      */         }
/*      */       }
/*  348 */       else if (localCachedRowSetImpl.rowInserted())
/*      */       {
/*  351 */         localPreparedStatement = this.con.prepareStatement(this.insertCmd);
/*  352 */         if ((bool1 = insertNewRow(localCachedRowSetImpl, localPreparedStatement, this.crsResolve)) == true) {
/*  353 */           this.status.add(k, Integer.valueOf(2));
/*      */         }
/*      */         else
/*      */         {
/*  357 */           this.status.add(k, Integer.valueOf(3));
/*      */         }
/*  359 */       } else if (localCachedRowSetImpl.rowUpdated())
/*      */       {
/*  361 */         if ((bool1 = updateOriginalRow(localCachedRowSetImpl) == true ? 1 : 0) != 0) {
/*  362 */           this.status.add(k, Integer.valueOf(0));
/*      */         }
/*      */         else
/*      */         {
/*  366 */           this.status.add(k, Integer.valueOf(3));
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  377 */         m = localCachedRowSetImpl.getMetaData().getColumnCount();
/*  378 */         this.status.add(k, Integer.valueOf(3));
/*      */ 
/*  380 */         this.crsResolve.moveToInsertRow();
/*  381 */         for (n = 0; n < i; n++) {
/*  382 */           this.crsResolve.updateNull(n + 1);
/*      */         }
/*      */ 
/*  385 */         this.crsResolve.insertRow();
/*  386 */         this.crsResolve.moveToCurrentRow();
/*      */       }
/*      */ 
/*  389 */       k++;
/*      */     }
/*      */ 
/*  393 */     if (localPreparedStatement != null) {
/*  394 */       localPreparedStatement.close();
/*      */     }
/*  396 */     localCachedRowSetImpl.setShowDeleted(bool2);
/*      */ 
/*  398 */     int m = 0;
/*  399 */     for (int n = 1; n < this.status.size(); n++)
/*      */     {
/*  401 */       if (!this.status.get(n).equals(Integer.valueOf(3)))
/*      */       {
/*  403 */         m = 1;
/*  404 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  408 */     localCachedRowSetImpl.beforeFirst();
/*  409 */     this.crsResolve.beforeFirst();
/*      */ 
/*  411 */     if (m != 0) {
/*  412 */       SyncProviderException localSyncProviderException = new SyncProviderException(this.status.size() - 1 + this.resBundle.handleGetObject("crswriter.conflictsno").toString());
/*      */ 
/*  415 */       SyncResolverImpl localSyncResolverImpl = (SyncResolverImpl)localSyncProviderException.getSyncResolver();
/*      */ 
/*  417 */       localSyncResolverImpl.setCachedRowSet(localCachedRowSetImpl);
/*  418 */       localSyncResolverImpl.setCachedRowSetResolver(this.crsResolve);
/*      */ 
/*  420 */       localSyncResolverImpl.setStatus(this.status);
/*  421 */       localSyncResolverImpl.setCachedRowSetWriter(this);
/*      */ 
/*  423 */       throw localSyncProviderException;
/*      */     }
/*  425 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean updateOriginalRow(CachedRowSet paramCachedRowSet)
/*      */     throws SQLException
/*      */   {
/*  464 */     int i = 0;
/*  465 */     int j = 0;
/*      */ 
/*  468 */     ResultSet localResultSet1 = paramCachedRowSet.getOriginalRow();
/*  469 */     localResultSet1.next();
/*      */     try
/*      */     {
/*  472 */       this.updateWhere = buildWhereClause(this.updateWhere, localResultSet1);
/*      */ 
/*  486 */       String str1 = this.selectCmd.toLowerCase();
/*      */ 
/*  488 */       int k = str1.indexOf("where");
/*      */ 
/*  490 */       if (k != -1)
/*      */       {
/*  492 */         String str2 = this.selectCmd.substring(0, k);
/*  493 */         this.selectCmd = str2;
/*      */       }
/*      */ 
/*  496 */       PreparedStatement localPreparedStatement = this.con.prepareStatement(this.selectCmd + this.updateWhere, 1005, 1007);
/*      */ 
/*  499 */       for (i = 0; i < this.keyCols.length; i++) {
/*  500 */         if (this.params[i] != null) {
/*  501 */           localPreparedStatement.setObject(++j, this.params[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  508 */         localPreparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
/*  509 */         localPreparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
/*  510 */         localPreparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
/*  511 */         localPreparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*  516 */       ResultSet localResultSet2 = null;
/*  517 */       localResultSet2 = localPreparedStatement.executeQuery();
/*  518 */       ResultSetMetaData localResultSetMetaData = localResultSet2.getMetaData();
/*      */ 
/*  520 */       if (localResultSet2.next()) {
/*  521 */         if (localResultSet2.next())
/*      */         {
/*  532 */           return true;
/*      */         }
/*      */ 
/*  539 */         localResultSet2.first();
/*      */ 
/*  542 */         int m = 0;
/*  543 */         Vector localVector = new Vector();
/*  544 */         String str3 = this.updateCmd;
/*      */ 
/*  548 */         int n = 1;
/*  549 */         Object localObject4 = null;
/*      */ 
/*  554 */         int i1 = 1;
/*  555 */         int i2 = 1;
/*      */ 
/*  557 */         this.crsResolve.moveToInsertRow();
/*      */         Object localObject5;
/*  559 */         for (i = 1; i <= this.callerColumnCount; i++) {
/*  560 */           Object localObject1 = localResultSet1.getObject(i);
/*  561 */           Object localObject2 = paramCachedRowSet.getObject(i);
/*  562 */           Object localObject3 = localResultSet2.getObject(i);
/*      */ 
/*  569 */           localObject5 = paramCachedRowSet.getTypeMap() == null ? this.con.getTypeMap() : paramCachedRowSet.getTypeMap();
/*  570 */           if ((localObject3 instanceof Struct))
/*      */           {
/*  572 */             Struct localStruct = (Struct)localObject3;
/*      */ 
/*  575 */             Class localClass = null;
/*  576 */             localClass = (Class)((Map)localObject5).get(localStruct.getSQLTypeName());
/*  577 */             if (localClass != null)
/*      */             {
/*  579 */               SQLData localSQLData = null;
/*      */               try {
/*  581 */                 localSQLData = (SQLData)localClass.newInstance();
/*      */               } catch (InstantiationException localInstantiationException) {
/*  583 */                 throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localInstantiationException.getMessage() }));
/*      */               }
/*      */               catch (IllegalAccessException localIllegalAccessException) {
/*  586 */                 throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.unableins").toString(), new Object[] { localIllegalAccessException.getMessage() }));
/*      */               }
/*      */ 
/*  590 */               Object[] arrayOfObject = localStruct.getAttributes((Map)localObject5);
/*      */ 
/*  592 */               SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, (Map)localObject5);
/*      */ 
/*  594 */               localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
/*  595 */               localObject3 = localSQLData;
/*      */             }
/*  597 */           } else if ((localObject3 instanceof SQLData)) {
/*  598 */             localObject3 = new SerialStruct((SQLData)localObject3, (Map)localObject5);
/*  599 */           } else if ((localObject3 instanceof Blob)) {
/*  600 */             localObject3 = new SerialBlob((Blob)localObject3);
/*  601 */           } else if ((localObject3 instanceof Clob)) {
/*  602 */             localObject3 = new SerialClob((Clob)localObject3);
/*  603 */           } else if ((localObject3 instanceof Array)) {
/*  604 */             localObject3 = new SerialArray((Array)localObject3, (Map)localObject5);
/*      */           }
/*      */ 
/*  608 */           n = 1;
/*      */ 
/*  615 */           if ((localObject3 == null) && (localObject1 != null))
/*      */           {
/*  620 */             this.iChangedValsinDbOnly += 1;
/*      */ 
/*  623 */             n = 0;
/*  624 */             localObject4 = localObject3;
/*      */           }
/*  632 */           else if ((localObject3 != null) && (!localObject3.equals(localObject1)))
/*      */           {
/*  638 */             this.iChangedValsinDbOnly += 1;
/*      */ 
/*  641 */             n = 0;
/*  642 */             localObject4 = localObject3;
/*  643 */           } else if ((localObject1 == null) || (localObject2 == null))
/*      */           {
/*  651 */             if ((i1 == 0) || (i2 == 0)) {
/*  652 */               str3 = str3 + ", ";
/*      */             }
/*  654 */             str3 = str3 + paramCachedRowSet.getMetaData().getColumnName(i);
/*  655 */             localVector.add(Integer.valueOf(i));
/*  656 */             str3 = str3 + " = ? ";
/*  657 */             i1 = 0;
/*      */           }
/*  665 */           else if (localObject1.equals(localObject2)) {
/*  666 */             m++;
/*      */           }
/*  675 */           else if (!localObject1.equals(localObject2))
/*      */           {
/*  692 */             if (paramCachedRowSet.columnUpdated(i)) {
/*  693 */               if (localObject3.equals(localObject1))
/*      */               {
/*  697 */                 if ((i2 == 0) || (i1 == 0)) {
/*  698 */                   str3 = str3 + ", ";
/*      */                 }
/*  700 */                 str3 = str3 + paramCachedRowSet.getMetaData().getColumnName(i);
/*  701 */                 localVector.add(Integer.valueOf(i));
/*  702 */                 str3 = str3 + " = ? ";
/*  703 */                 i2 = 0;
/*      */               }
/*      */               else
/*      */               {
/*  709 */                 n = 0;
/*  710 */                 localObject4 = localObject3;
/*  711 */                 this.iChangedValsInDbAndCRS += 1;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*  716 */           if (n == 0)
/*  717 */             this.crsResolve.updateObject(i, localObject4);
/*      */           else {
/*  719 */             this.crsResolve.updateNull(i);
/*      */           }
/*      */         }
/*      */ 
/*  723 */         localResultSet2.close();
/*  724 */         localPreparedStatement.close();
/*      */ 
/*  726 */         this.crsResolve.insertRow();
/*  727 */         this.crsResolve.moveToCurrentRow();
/*      */ 
/*  736 */         if (((i1 == 0) && (localVector.size() == 0)) || (m == this.callerColumnCount))
/*      */         {
/*  738 */           return false;
/*      */         }
/*      */ 
/*  741 */         if ((this.iChangedValsInDbAndCRS != 0) || (this.iChangedValsinDbOnly != 0)) {
/*  742 */           return true;
/*      */         }
/*      */ 
/*  746 */         str3 = str3 + this.updateWhere;
/*      */ 
/*  748 */         localPreparedStatement = this.con.prepareStatement(str3);
/*      */ 
/*  751 */         for (i = 0; i < localVector.size(); i++) {
/*  752 */           localObject5 = paramCachedRowSet.getObject(((Integer)localVector.get(i)).intValue());
/*  753 */           if (localObject5 != null)
/*  754 */             localPreparedStatement.setObject(i + 1, localObject5);
/*      */           else
/*  756 */             localPreparedStatement.setNull(i + 1, paramCachedRowSet.getMetaData().getColumnType(i + 1));
/*      */         }
/*  758 */         j = i;
/*      */ 
/*  761 */         for (i = 0; i < this.keyCols.length; i++) {
/*  762 */           if (this.params[i] != null) {
/*  763 */             localPreparedStatement.setObject(++j, this.params[i]);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  769 */         i = localPreparedStatement.executeUpdate();
/*      */ 
/*  779 */         return false;
/*      */       }
/*      */ 
/*  796 */       return true;
/*      */     }
/*      */     catch (SQLException localSQLException) {
/*  799 */       localSQLException.printStackTrace();
/*      */ 
/*  802 */       this.crsResolve.moveToInsertRow();
/*      */ 
/*  804 */       for (i = 1; i <= this.callerColumnCount; i++) {
/*  805 */         this.crsResolve.updateNull(i);
/*      */       }
/*      */ 
/*  808 */       this.crsResolve.insertRow();
/*  809 */       this.crsResolve.moveToCurrentRow();
/*      */     }
/*  811 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean insertNewRow(CachedRowSet paramCachedRowSet, PreparedStatement paramPreparedStatement, CachedRowSetImpl paramCachedRowSetImpl)
/*      */     throws SQLException
/*      */   {
/*  831 */     int i = 0;
/*  832 */     int j = paramCachedRowSet.getMetaData().getColumnCount();
/*      */ 
/*  834 */     boolean bool = false;
/*  835 */     PreparedStatement localPreparedStatement = this.con.prepareStatement(this.selectCmd, 1005, 1007);
/*      */ 
/*  837 */     ResultSet localResultSet2 = null;
/*  838 */     DatabaseMetaData localDatabaseMetaData = this.con.getMetaData();
/*  839 */     ResultSet localResultSet1 = localPreparedStatement.executeQuery();
/*  840 */     String str1 = paramCachedRowSet.getTableName();
/*  841 */     localResultSet2 = localDatabaseMetaData.getPrimaryKeys(null, null, str1);
/*  842 */     String[] arrayOfString = new String[j];
/*  843 */     int k = 0;
/*  844 */     while (localResultSet2.next()) {
/*  845 */       String str2 = localResultSet2.getString("COLUMN_NAME");
/*  846 */       arrayOfString[k] = str2;
/*  847 */       k++;
/*      */     }
/*      */ 
/*  850 */     if (localResultSet1.next()) {
/*  851 */       for (int m = 0; m < arrayOfString.length; m++) {
/*  852 */         if (arrayOfString[m] != null) {
/*  853 */           if (paramCachedRowSet.getObject(arrayOfString[m]) == null) {
/*      */             break;
/*      */           }
/*  856 */           String str3 = paramCachedRowSet.getObject(arrayOfString[m]).toString();
/*  857 */           String str4 = localResultSet1.getObject(arrayOfString[m]).toString();
/*  858 */           if (str3.equals(str4)) {
/*  859 */             bool = true;
/*  860 */             this.crsResolve.moveToInsertRow();
/*  861 */             for (i = 1; i <= j; i++) {
/*  862 */               String str5 = localResultSet1.getMetaData().getColumnName(i);
/*  863 */               if (str5.equals(arrayOfString[m]))
/*  864 */                 this.crsResolve.updateObject(i, str4);
/*      */               else
/*  866 */                 this.crsResolve.updateNull(i);
/*      */             }
/*  868 */             this.crsResolve.insertRow();
/*  869 */             this.crsResolve.moveToCurrentRow();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  874 */     if (bool)
/*  875 */       return bool;
/*      */     try
/*      */     {
/*  878 */       for (i = 1; i <= j; i++) {
/*  879 */         Object localObject = paramCachedRowSet.getObject(i);
/*  880 */         if (localObject != null)
/*  881 */           paramPreparedStatement.setObject(i, localObject);
/*      */         else {
/*  883 */           paramPreparedStatement.setNull(i, paramCachedRowSet.getMetaData().getColumnType(i));
/*      */         }
/*      */       }
/*      */ 
/*  887 */       i = paramPreparedStatement.executeUpdate();
/*  888 */       return false;
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  898 */       this.crsResolve.moveToInsertRow();
/*      */ 
/*  900 */       for (i = 1; i <= j; i++) {
/*  901 */         this.crsResolve.updateNull(i);
/*      */       }
/*      */ 
/*  904 */       this.crsResolve.insertRow();
/*  905 */       this.crsResolve.moveToCurrentRow();
/*      */     }
/*  907 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean deleteOriginalRow(CachedRowSet paramCachedRowSet, CachedRowSetImpl paramCachedRowSetImpl)
/*      */     throws SQLException
/*      */   {
/*  932 */     int j = 0;
/*      */ 
/*  935 */     ResultSet localResultSet1 = paramCachedRowSet.getOriginalRow();
/*  936 */     localResultSet1.next();
/*      */ 
/*  938 */     this.deleteWhere = buildWhereClause(this.deleteWhere, localResultSet1);
/*  939 */     PreparedStatement localPreparedStatement = this.con.prepareStatement(this.selectCmd + this.deleteWhere, 1005, 1007);
/*      */ 
/*  942 */     for (int i = 0; i < this.keyCols.length; i++) {
/*  943 */       if (this.params[i] != null) {
/*  944 */         localPreparedStatement.setObject(++j, this.params[i]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  951 */       localPreparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
/*  952 */       localPreparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
/*  953 */       localPreparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
/*  954 */       localPreparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */ 
/*  962 */     ResultSet localResultSet2 = localPreparedStatement.executeQuery();
/*      */ 
/*  964 */     if (localResultSet2.next() == true) {
/*  965 */       if (localResultSet2.next())
/*      */       {
/*  967 */         return true;
/*      */       }
/*  969 */       localResultSet2.first();
/*      */ 
/*  973 */       int k = 0;
/*      */ 
/*  975 */       paramCachedRowSetImpl.moveToInsertRow();
/*      */ 
/*  977 */       for (i = 1; i <= paramCachedRowSet.getMetaData().getColumnCount(); i++)
/*      */       {
/*  979 */         localObject1 = localResultSet1.getObject(i);
/*  980 */         Object localObject2 = localResultSet2.getObject(i);
/*      */ 
/*  982 */         if ((localObject1 != null) && (localObject2 != null)) {
/*  983 */           if (!localObject1.toString().equals(localObject2.toString())) {
/*  984 */             k = 1;
/*  985 */             paramCachedRowSetImpl.updateObject(i, localResultSet1.getObject(i));
/*      */           }
/*      */         }
/*  988 */         else paramCachedRowSetImpl.updateNull(i);
/*      */ 
/*      */       }
/*      */ 
/*  992 */       paramCachedRowSetImpl.insertRow();
/*  993 */       paramCachedRowSetImpl.moveToCurrentRow();
/*      */ 
/*  995 */       if (k != 0)
/*      */       {
/*  999 */         return true;
/*      */       }
/*      */ 
/* 1006 */       Object localObject1 = this.deleteCmd + this.deleteWhere;
/* 1007 */       localPreparedStatement = this.con.prepareStatement((String)localObject1);
/*      */ 
/* 1009 */       j = 0;
/* 1010 */       for (i = 0; i < this.keyCols.length; i++) {
/* 1011 */         if (this.params[i] != null) {
/* 1012 */           localPreparedStatement.setObject(++j, this.params[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1018 */       if (localPreparedStatement.executeUpdate() != 1) {
/* 1019 */         return true;
/*      */       }
/* 1021 */       localPreparedStatement.close();
/*      */     }
/*      */     else {
/* 1024 */       return true;
/*      */     }
/*      */ 
/* 1028 */     return false;
/*      */   }
/*      */ 
/*      */   public void setReader(CachedRowSetReader paramCachedRowSetReader)
/*      */     throws SQLException
/*      */   {
/* 1037 */     this.reader = paramCachedRowSetReader;
/*      */   }
/*      */ 
/*      */   public CachedRowSetReader getReader()
/*      */     throws SQLException
/*      */   {
/* 1046 */     return this.reader;
/*      */   }
/*      */ 
/*      */   private void initSQLStatements(CachedRowSet paramCachedRowSet)
/*      */     throws SQLException
/*      */   {
/* 1063 */     this.callerMd = paramCachedRowSet.getMetaData();
/* 1064 */     this.callerColumnCount = this.callerMd.getColumnCount();
/* 1065 */     if (this.callerColumnCount < 1)
/*      */     {
/* 1067 */       return;
/*      */     }
/*      */ 
/* 1074 */     String str1 = paramCachedRowSet.getTableName();
/* 1075 */     if (str1 == null)
/*      */     {
/* 1081 */       str1 = this.callerMd.getTableName(1);
/* 1082 */       if ((str1 == null) || (str1.length() == 0)) {
/* 1083 */         throw new SQLException(this.resBundle.handleGetObject("crswriter.tname").toString());
/*      */       }
/*      */     }
/* 1086 */     String str2 = this.callerMd.getCatalogName(1);
/* 1087 */     String str3 = this.callerMd.getSchemaName(1);
/* 1088 */     DatabaseMetaData localDatabaseMetaData = this.con.getMetaData();
/*      */ 
/* 1095 */     this.selectCmd = "SELECT ";
/* 1096 */     for (int i = 1; i <= this.callerColumnCount; i++) {
/* 1097 */       this.selectCmd += this.callerMd.getColumnName(i);
/* 1098 */       if (i < this.callerMd.getColumnCount())
/* 1099 */         this.selectCmd += ", ";
/*      */       else {
/* 1101 */         this.selectCmd += " ";
/*      */       }
/*      */     }
/*      */ 
/* 1105 */     this.selectCmd = (this.selectCmd + "FROM " + buildTableName(localDatabaseMetaData, str2, str3, str1));
/*      */ 
/* 1110 */     this.updateCmd = ("UPDATE " + buildTableName(localDatabaseMetaData, str2, str3, str1));
/*      */ 
/* 1123 */     String str4 = this.updateCmd.toLowerCase();
/*      */ 
/* 1125 */     int j = str4.indexOf("where");
/*      */ 
/* 1127 */     if (j != -1)
/*      */     {
/* 1129 */       this.updateCmd = this.updateCmd.substring(0, j);
/*      */     }
/* 1131 */     this.updateCmd += "SET ";
/*      */ 
/* 1136 */     this.insertCmd = ("INSERT INTO " + buildTableName(localDatabaseMetaData, str2, str3, str1));
/*      */ 
/* 1138 */     this.insertCmd += "(";
/* 1139 */     for (i = 1; i <= this.callerColumnCount; i++) {
/* 1140 */       this.insertCmd += this.callerMd.getColumnName(i);
/* 1141 */       if (i < this.callerMd.getColumnCount())
/* 1142 */         this.insertCmd += ", ";
/*      */       else
/* 1144 */         this.insertCmd += ") VALUES (";
/*      */     }
/* 1146 */     for (i = 1; i <= this.callerColumnCount; i++) {
/* 1147 */       this.insertCmd += "?";
/* 1148 */       if (i < this.callerColumnCount)
/* 1149 */         this.insertCmd += ", ";
/*      */       else {
/* 1151 */         this.insertCmd += ")";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1157 */     this.deleteCmd = ("DELETE FROM " + buildTableName(localDatabaseMetaData, str2, str3, str1));
/*      */ 
/* 1163 */     buildKeyDesc(paramCachedRowSet);
/*      */   }
/*      */ 
/*      */   private String buildTableName(DatabaseMetaData paramDatabaseMetaData, String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 1187 */     String str = "";
/*      */ 
/* 1189 */     paramString1 = paramString1.trim();
/* 1190 */     paramString2 = paramString2.trim();
/* 1191 */     paramString3 = paramString3.trim();
/*      */ 
/* 1193 */     if (paramDatabaseMetaData.isCatalogAtStart() == true) {
/* 1194 */       if ((paramString1 != null) && (paramString1.length() > 0)) {
/* 1195 */         str = str + paramString1 + paramDatabaseMetaData.getCatalogSeparator();
/*      */       }
/* 1197 */       if ((paramString2 != null) && (paramString2.length() > 0)) {
/* 1198 */         str = str + paramString2 + ".";
/*      */       }
/* 1200 */       str = str + paramString3;
/*      */     } else {
/* 1202 */       if ((paramString2 != null) && (paramString2.length() > 0)) {
/* 1203 */         str = str + paramString2 + ".";
/*      */       }
/* 1205 */       str = str + paramString3;
/* 1206 */       if ((paramString1 != null) && (paramString1.length() > 0)) {
/* 1207 */         str = str + paramDatabaseMetaData.getCatalogSeparator() + paramString1;
/*      */       }
/*      */     }
/* 1210 */     str = str + " ";
/* 1211 */     return str;
/*      */   }
/*      */ 
/*      */   private void buildKeyDesc(CachedRowSet paramCachedRowSet)
/*      */     throws SQLException
/*      */   {
/* 1235 */     this.keyCols = paramCachedRowSet.getKeyColumns();
/* 1236 */     ResultSetMetaData localResultSetMetaData = paramCachedRowSet.getMetaData();
/* 1237 */     if ((this.keyCols == null) || (this.keyCols.length == 0)) {
/* 1238 */       ArrayList localArrayList = new ArrayList();
/*      */ 
/* 1240 */       for (int i = 0; i < this.callerColumnCount; i++)
/* 1241 */         if ((localResultSetMetaData.getColumnType(i + 1) != 2005) && (localResultSetMetaData.getColumnType(i + 1) != 2002) && (localResultSetMetaData.getColumnType(i + 1) != 2009) && (localResultSetMetaData.getColumnType(i + 1) != 2004) && (localResultSetMetaData.getColumnType(i + 1) != 2003) && (localResultSetMetaData.getColumnType(i + 1) != 1111))
/*      */         {
/* 1247 */           localArrayList.add(Integer.valueOf(i + 1));
/*      */         }
/* 1249 */       this.keyCols = new int[localArrayList.size()];
/* 1250 */       for (i = 0; i < localArrayList.size(); i++)
/* 1251 */         this.keyCols[i] = ((Integer)localArrayList.get(i)).intValue();
/*      */     }
/* 1253 */     this.params = new Object[this.keyCols.length];
/*      */   }
/*      */ 
/*      */   private String buildWhereClause(String paramString, ResultSet paramResultSet)
/*      */     throws SQLException
/*      */   {
/* 1282 */     paramString = "WHERE ";
/*      */ 
/* 1284 */     for (int i = 0; i < this.keyCols.length; i++) {
/* 1285 */       if (i > 0) {
/* 1286 */         paramString = paramString + "AND ";
/*      */       }
/* 1288 */       paramString = paramString + this.callerMd.getColumnName(this.keyCols[i]);
/* 1289 */       this.params[i] = paramResultSet.getObject(this.keyCols[i]);
/* 1290 */       if (paramResultSet.wasNull() == true)
/* 1291 */         paramString = paramString + " IS NULL ";
/*      */       else {
/* 1293 */         paramString = paramString + " = ? ";
/*      */       }
/*      */     }
/* 1296 */     return paramString;
/*      */   }
/*      */ 
/*      */   void updateResolvedConflictToDB(CachedRowSet paramCachedRowSet, Connection paramConnection)
/*      */     throws SQLException
/*      */   {
/* 1302 */     String str1 = "WHERE ";
/* 1303 */     String str2 = " ";
/* 1304 */     String str3 = "UPDATE ";
/* 1305 */     int i = paramCachedRowSet.getMetaData().getColumnCount();
/* 1306 */     int[] arrayOfInt = paramCachedRowSet.getKeyColumns();
/*      */ 
/* 1308 */     String str4 = "";
/*      */ 
/* 1310 */     str1 = buildWhereClause(str1, paramCachedRowSet);
/*      */ 
/* 1312 */     if ((arrayOfInt == null) || (arrayOfInt.length == 0)) {
/* 1313 */       arrayOfInt = new int[i];
/* 1314 */       for (j = 0; j < arrayOfInt.length; ) {
/* 1315 */         arrayOfInt[(j++)] = j;
/*      */       }
/*      */     }
/* 1318 */     Object[] arrayOfObject = new Object[arrayOfInt.length];
/*      */ 
/* 1320 */     str3 = "UPDATE " + buildTableName(paramConnection.getMetaData(), paramCachedRowSet.getMetaData().getCatalogName(1), paramCachedRowSet.getMetaData().getSchemaName(1), paramCachedRowSet.getTableName());
/*      */ 
/* 1327 */     str3 = str3 + "SET ";
/*      */ 
/* 1329 */     int j = 1;
/*      */ 
/* 1331 */     for (int k = 1; k <= i; k++) {
/* 1332 */       if (paramCachedRowSet.columnUpdated(k)) {
/* 1333 */         if (j == 0) {
/* 1334 */           str4 = str4 + ", ";
/*      */         }
/* 1336 */         str4 = str4 + paramCachedRowSet.getMetaData().getColumnName(k);
/* 1337 */         str4 = str4 + " = ? ";
/* 1338 */         j = 0;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1343 */     str3 = str3 + str4;
/* 1344 */     str1 = "WHERE ";
/*      */ 
/* 1346 */     for (k = 0; k < arrayOfInt.length; k++) {
/* 1347 */       if (k > 0) {
/* 1348 */         str1 = str1 + "AND ";
/*      */       }
/* 1350 */       str1 = str1 + paramCachedRowSet.getMetaData().getColumnName(arrayOfInt[k]);
/* 1351 */       arrayOfObject[k] = paramCachedRowSet.getObject(arrayOfInt[k]);
/* 1352 */       if (paramCachedRowSet.wasNull() == true)
/* 1353 */         str1 = str1 + " IS NULL ";
/*      */       else {
/* 1355 */         str1 = str1 + " = ? ";
/*      */       }
/*      */     }
/* 1358 */     str3 = str3 + str1;
/*      */ 
/* 1360 */     PreparedStatement localPreparedStatement = paramConnection.prepareStatement(str3);
/*      */ 
/* 1362 */     k = 0;
/* 1363 */     for (int m = 0; m < i; m++) {
/* 1364 */       if (paramCachedRowSet.columnUpdated(m + 1)) {
/* 1365 */         Object localObject = paramCachedRowSet.getObject(m + 1);
/* 1366 */         if (localObject != null)
/* 1367 */           localPreparedStatement.setObject(++k, localObject);
/*      */         else {
/* 1369 */           localPreparedStatement.setNull(m + 1, paramCachedRowSet.getMetaData().getColumnType(m + 1));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1375 */     for (m = 0; m < arrayOfInt.length; m++) {
/* 1376 */       if (arrayOfObject[m] != null) {
/* 1377 */         localPreparedStatement.setObject(++k, arrayOfObject[m]);
/*      */       }
/*      */     }
/*      */ 
/* 1381 */     m = localPreparedStatement.executeUpdate();
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/* 1389 */     this.con.commit();
/* 1390 */     if (this.reader.getCloseConnection() == true)
/* 1391 */       this.con.close();
/*      */   }
/*      */ 
/*      */   public void commit(CachedRowSetImpl paramCachedRowSetImpl, boolean paramBoolean) throws SQLException
/*      */   {
/* 1396 */     this.con.commit();
/* 1397 */     if ((paramBoolean) && 
/* 1398 */       (paramCachedRowSetImpl.getCommand() != null)) {
/* 1399 */       paramCachedRowSetImpl.execute(this.con);
/*      */     }
/*      */ 
/* 1402 */     if (this.reader.getCloseConnection() == true)
/* 1403 */       this.con.close();
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/* 1411 */     this.con.rollback();
/* 1412 */     if (this.reader.getCloseConnection() == true)
/* 1413 */       this.con.close();
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint paramSavepoint)
/*      */     throws SQLException
/*      */   {
/* 1421 */     this.con.rollback(paramSavepoint);
/* 1422 */     if (this.reader.getCloseConnection() == true)
/* 1423 */       this.con.close();
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1429 */     paramObjectInputStream.defaultReadObject();
/*      */     try
/*      */     {
/* 1432 */       this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
/*      */     } catch (IOException localIOException) {
/* 1434 */       throw new RuntimeException(localIOException);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.rowset.internal.CachedRowSetWriter
 * JD-Core Version:    0.6.2
 */