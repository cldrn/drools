/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.junit.Test;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.KiePMMLTargetValue;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTarget;
import static org.kie.pmml.compiler.api.utils.ModelUtils.convertToKiePMMLTarget;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLTargetFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLTargetFactoryTest_01.txt";

    @Test
    public void getKiePMMLTargetValueVariableInitializer() throws IOException {
        KiePMMLTarget kiePMMLTarget = convertToKiePMMLTarget(getRandomTarget());
        MethodCallExpr retrieved = KiePMMLTargetFactory.getKiePMMLTargetVariableInitializer(kiePMMLTarget);
        String text = getFileContent(TEST_01_SOURCE);
        List<KiePMMLTargetValue> targetValues = kiePMMLTarget.getTargetValues();
        String opType = OP_TYPE.class.getCanonicalName() + "." + kiePMMLTarget.getOpType().toString();
        String castInteger = CAST_INTEGER.class.getCanonicalName() + "." + kiePMMLTarget.getCastInteger().toString();
        Expression expected = JavaParserUtils.parseExpression(String.format(text, kiePMMLTarget.getName(),
                                                                            targetValues.get(0).getName(),
                                                                            targetValues.get(0).getValue(),
                                                                            targetValues.get(0).getDisplayValue(),
                                                                            targetValues.get(0).getPriorProbability(),
                                                                            targetValues.get(0).getDefaultValue(),
                                                                            targetValues.get(1).getName(),
                                                                            targetValues.get(1).getValue(),
                                                                            targetValues.get(1).getDisplayValue(),
                                                                            targetValues.get(1).getPriorProbability(),
                                                                            targetValues.get(1).getDefaultValue(),
                                                                            targetValues.get(2).getName(),
                                                                            targetValues.get(2).getValue(),
                                                                            targetValues.get(2).getDisplayValue(),
                                                                            targetValues.get(2).getPriorProbability(),
                                                                            targetValues.get(2).getDefaultValue(),
                                                                            opType,
                                                                            kiePMMLTarget.getField(),
                                                                            castInteger,
                                                                            kiePMMLTarget.getMin(),
                                                                            kiePMMLTarget.getMax(),
                                                                            kiePMMLTarget.getRescaleConstant(),
                                                                            kiePMMLTarget.getRescaleFactor()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLTarget.class,
                                               KiePMMLTargetValue.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}