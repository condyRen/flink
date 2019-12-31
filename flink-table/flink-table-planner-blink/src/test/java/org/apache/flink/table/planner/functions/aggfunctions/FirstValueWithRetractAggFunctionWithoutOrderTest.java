/*
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

package org.apache.flink.table.planner.functions.aggfunctions;

import org.apache.flink.table.dataformat.BinaryString;
import org.apache.flink.table.dataformat.Decimal;
import org.apache.flink.table.dataformat.GenericRow;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.BooleanFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.ByteFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.DecimalFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.DoubleFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.FloatFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.IntFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.LongFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.ShortFirstValueWithRetractAggFunction;
import org.apache.flink.table.planner.functions.aggfunctions.FirstValueWithRetractAggFunction.StringFirstValueWithRetractAggFunction;
import org.apache.flink.table.runtime.typeutils.DecimalTypeInfo;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Test case for built-in FirstValue with retract aggregate function.
 * This class tests `accumulate` method without order argument.
 */
@RunWith(Parameterized.class)
public class FirstValueWithRetractAggFunctionWithoutOrderTest<T> extends AggFunctionTestBase<T, GenericRow> {

	@Parameterized.Parameter
	public AggFunctionTestSpec<T, GenericRow> aggFunctionTestSpec;

	private static final int DECIMAL_PRECISION = 20;
	private static final int DECIMAL_SCALE = 6;

	@Override
	protected List<List<T>> getInputValueSets() {
		return aggFunctionTestSpec.inputValueSets;
	}

	@Override
	protected List<T> getExpectedResults() {
		return aggFunctionTestSpec.expectedResults;
	}

	@Override
	protected AggregateFunction<T, GenericRow> getAggregator() {
		return aggFunctionTestSpec.aggregator;
	}

	@Override
	protected Class<?> getAccClass() {
		return GenericRow.class;
	}

	@Override
	protected Method getRetractFunc() throws NoSuchMethodException {
		return getAggregator().getClass().getMethod("retract", getAccClass(), Object.class);
	}

	@Parameterized.Parameters(name = "{index}: {0}")
	public static List<AggFunctionTestSpec> testData() {
		return Arrays.asList(
				/**
				 * Test for ByteFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new ByteFirstValueWithRetractAggFunction(),
						numberInputValueSets(Byte::valueOf),
						numberExpectedResults(Byte::valueOf)
				),
				/**
				 * Test for ShortFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new ShortFirstValueWithRetractAggFunction(),
						numberInputValueSets(Short::valueOf),
						numberExpectedResults(Short::valueOf)
				),
				/**
				 * Test for IntFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new IntFirstValueWithRetractAggFunction(),
						numberInputValueSets(Integer::valueOf),
						numberExpectedResults(Integer::valueOf)
				),
				/**
				 * Test for LongFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new LongFirstValueWithRetractAggFunction(),
						numberInputValueSets(Long::valueOf),
						numberExpectedResults(Long::valueOf)
				),
				/**
				 * Test for FloatFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new FloatFirstValueWithRetractAggFunction(),
						numberInputValueSets(Float::valueOf),
						numberExpectedResults(Float::valueOf)
				),
				/**
				 * Test for DoubleFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new DoubleFirstValueWithRetractAggFunction(),
						numberInputValueSets(Double::valueOf),
						numberExpectedResults(Double::valueOf)
				),
				/**
				 * Test for BooleanFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new BooleanFirstValueWithRetractAggFunction(),
						Arrays.asList(
								Arrays.asList(
										false,
										false,
										false
								),
								Arrays.asList(
										true,
										true,
										true
								),
								Arrays.asList(
										true,
										false,
										null,
										true,
										false,
										true,
										null
								),
								Arrays.asList(
										null,
										null,
										null
								),
								Arrays.asList(
										null,
										true
								)),
						Arrays.asList(
								false,
								true,
								true,
								null,
								true
						)
				),
				/**
				 * Test for DecimalFirstValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new DecimalFirstValueWithRetractAggFunction(
								DecimalTypeInfo.of(DECIMAL_PRECISION, DECIMAL_SCALE)),
						Arrays.asList(
								Arrays.asList(
										Decimal.castFrom("1", DECIMAL_PRECISION, DECIMAL_SCALE),
										Decimal.castFrom("1000.000001", DECIMAL_PRECISION, DECIMAL_SCALE),
										Decimal.castFrom("-1", DECIMAL_PRECISION, DECIMAL_SCALE),
										Decimal.castFrom("-999.998999", DECIMAL_PRECISION, DECIMAL_SCALE),
										null,
										Decimal.castFrom("0", DECIMAL_PRECISION, DECIMAL_SCALE),
										Decimal.castFrom("-999.999", DECIMAL_PRECISION, DECIMAL_SCALE),
										null,
										Decimal.castFrom("999.999", DECIMAL_PRECISION, DECIMAL_SCALE)
								),
								Arrays.asList(
										null,
										null,
										null,
										null,
										null
								),
								Arrays.asList(
										null,
										Decimal.castFrom("0", DECIMAL_PRECISION, DECIMAL_SCALE)
								)
						),
						Arrays.asList(
								Decimal.castFrom("1", DECIMAL_PRECISION, DECIMAL_SCALE),
								null,
								Decimal.castFrom("0", DECIMAL_PRECISION, DECIMAL_SCALE)
						)
				),
				/**
				 * Test for StringLastValueWithRetractAggFunction.
				 */
				new AggFunctionTestSpec<>(
						new StringFirstValueWithRetractAggFunction(),
						Arrays.asList(
								Arrays.asList(
										BinaryString.fromString("abc"),
										BinaryString.fromString("def"),
										BinaryString.fromString("ghi"),
										null,
										BinaryString.fromString("jkl"),
										null,
										BinaryString.fromString("zzz")
								),
								Arrays.asList(
										null,
										null
								),
								Arrays.asList(
										null,
										BinaryString.fromString("a")
								),
								Arrays.asList(
										BinaryString.fromString("x"),
										null,
										BinaryString.fromString("e")
								)
						),
						Arrays.asList(
								BinaryString.fromString("abc"),
								null,
								BinaryString.fromString("a"),
								BinaryString.fromString("x")
						)
				)

		);
	}

	private static <N> List<List<N>> numberInputValueSets(Function<String, N> strToValueFun) {
		return Arrays.asList(
				Arrays.asList(
						strToValueFun.apply("1"),
						null,
						strToValueFun.apply("-99"),
						strToValueFun.apply("3"),
						null
				),
				Arrays.asList(
						null,
						null,
						null,
						null
				),
				Arrays.asList(
						null,
						strToValueFun.apply("10"),
						null,
						strToValueFun.apply("3")
				)
		);
	}

	private static <N> List<N> numberExpectedResults(Function<String, N> strToValueFun) {
		return Arrays.asList(
				strToValueFun.apply("1"),
				null,
				strToValueFun.apply("10")
		);
	}
}
