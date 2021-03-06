/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.exec.vector.reducesink;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.CompilationOpContext;
import org.apache.hadoop.hive.ql.exec.vector.VectorizationContext;
import org.apache.hadoop.hive.ql.exec.vector.keyseries.VectorKeySeriesLongSerialized;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.OperatorDesc;
import org.apache.hadoop.hive.ql.plan.VectorDesc;
import org.apache.hadoop.hive.serde2.binarysortable.fast.BinarySortableSerializeWrite;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;

/*
 * Specialized class for native vectorized reduce sink that is reducing on a Uniform Hash
 * single long key column.
 */
public class VectorReduceSinkLongOperator extends VectorReduceSinkUniformHashOperator {

  private static final long serialVersionUID = 1L;
  private static final String CLASS_NAME = VectorReduceSinkLongOperator.class.getName();
  private static final Log LOG = LogFactory.getLog(CLASS_NAME);

  // The column number and type information for this one column long reduce key.
  private transient int singleKeyColumn;
  private transient PrimitiveTypeInfo singleKeyColumnPrimitiveTypeInfo;

  // The above members are initialized by the constructor and must not be
  // transient.
  //---------------------------------------------------------------------------

  //---------------------------------------------------------------------------
  // Pass-thru constructors.
  //

  /** Kryo ctor. */
  protected VectorReduceSinkLongOperator() {
    super();
  }

  public VectorReduceSinkLongOperator(CompilationOpContext ctx) {
    super(ctx);
  }

  public VectorReduceSinkLongOperator(CompilationOpContext ctx, OperatorDesc conf,
      VectorizationContext vContext, VectorDesc vectorDesc) throws HiveException {
    super(ctx, conf, vContext, vectorDesc);
  }

  @Override
  protected void initializeOp(Configuration hconf) throws HiveException {
    super.initializeOp(hconf);

    singleKeyColumn = reduceSinkKeyColumnMap[0];
    singleKeyColumnPrimitiveTypeInfo = (PrimitiveTypeInfo) reduceSinkKeyTypeInfos[0];

    serializedKeySeries =
        new VectorKeySeriesLongSerialized<BinarySortableSerializeWrite>(
            singleKeyColumn, singleKeyColumnPrimitiveTypeInfo, keyBinarySortableSerializeWrite);
  }
}