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

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Constant;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLConstantFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLConstantFactoryTest_01.txt";

    @Test
    public void getConstantVariableDeclaration() throws IOException {
        String variableName = "variableName";
        Object value = 2342.21;
        Constant constant = new Constant();
        constant.setValue(value);
        BlockStmt retrieved = KiePMMLConstantFactory.getConstantVariableDeclaration(variableName, constant);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, value));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}