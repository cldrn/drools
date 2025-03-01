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

package org.kie.pmml.compiler.commons.builders;

import java.io.IOException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.mocks.TestingModelImplementationProvider.KIE_PMML_TEST_MODEL_TEMPLATE;
import static org.kie.pmml.compiler.commons.mocks.TestingModelImplementationProvider.KIE_PMML_TEST_MODEL_TEMPLATE_JAVA;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelCodegenUtilsTest {

    private static final String MODEL_FILE = "TreeSample.pmml";
    private static final String TEST_01_SOURCE = "KiePMMLModelCodegenUtilsTest_01.txt";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML pmml;
    private static Model model;
    private static ClassOrInterfaceDeclaration modelTemplate;

    @BeforeClass
    public static void setup() throws Exception {
        pmml = KiePMMLUtil.load(getFileInputStream(MODEL_FILE), MODEL_FILE);
        model = pmml.getModels().get(0);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit("className", "packageName",
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_TEST_MODEL_TEMPLATE);
        modelTemplate = cloneCU.getClassByName("className")
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + "className"));
    }

    @Test
    public void init() throws IOException {
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmml,
                                                                                                     model,
                                                                                                     new HasClassLoaderMock());
        KiePMMLModelCodegenUtils.init(compilationDTO, modelTemplate);
        BlockStmt body = constructorDeclaration.getBody();
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseConstructorBlock(text);
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }
}
