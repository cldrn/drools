/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.xml.support.converters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.WildcardTypePermission;
import org.drools.compiler.kproject.models.ChannelModelImpl;
import org.drools.compiler.kproject.models.FileLoggerModelImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.compiler.kproject.models.ListenerModelImpl;
import org.drools.compiler.kproject.models.QualifierModelImpl;
import org.drools.compiler.kproject.models.RuleTemplateModelImpl;
import org.drools.compiler.kproject.models.WorkItemHandlerModelImpl;
import org.drools.core.base.XMLSupport;
import org.kie.api.builder.model.KieModuleModel;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.utll.xml.XStreamUtils.createNonTrustingXStream;

public class KieModuleMarshaller implements XMLSupport.XmlMarshaller<KieModuleModel> {

    public static final KieModuleMarshaller MARSHALLER = new KieModuleMarshaller();

    private final XStream xStream = createNonTrustingXStream(new DomDriver());

    private KieModuleMarshaller() {
        xStream.addPermission(new WildcardTypePermission( new String[] {
                "org.drools.compiler.kproject.models.*"
        }));

        xStream.registerConverter(new KieModuleConverter());
        xStream.registerConverter(new KBaseConverter());
        xStream.registerConverter(new KSessionConverter());
        xStream.registerConverter(new ListenerConverter());
        xStream.registerConverter(new QualifierConverter());
        xStream.registerConverter(new WorkItemHandelerConverter());
        xStream.registerConverter(new ChannelConverter());
        xStream.registerConverter(new RuleTemplateConverter());
        xStream.alias("kmodule", KieModuleModelImpl.class);
        xStream.alias("kbase", KieBaseModelImpl.class);
        xStream.alias("ksession", KieSessionModelImpl.class);
        xStream.alias("listener", ListenerModelImpl.class);
        xStream.alias("qualifier", QualifierModelImpl.class);
        xStream.alias("workItemHandler", WorkItemHandlerModelImpl.class);
        xStream.alias("channel", ChannelModelImpl.class);
        xStream.alias("fileLogger", FileLoggerModelImpl.class);
        xStream.alias("ruleTemplate", RuleTemplateModelImpl.class);
        xStream.setClassLoader(KieModuleModelImpl.class.getClassLoader());
    }

    public String toXML( KieModuleModel kieProject) {
        return xStream.toXML(kieProject);
    }

    public KieModuleModel fromXML( InputStream kModuleStream) {
        byte[] bytes = null;
        try {
            bytes = readBytesFromInputStream(kModuleStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieModuleValidator.validate(bytes);
        return (KieModuleModel)xStream.fromXML(new ByteArrayInputStream(bytes));
    }

    public KieModuleModel fromXML(java.io.File kModuleFile) {
        KieModuleValidator.validate(kModuleFile);
        return (KieModuleModel)xStream.fromXML(kModuleFile);
    }

    public KieModuleModel fromXML( URL kModuleUrl) {
        KieModuleValidator.validate(kModuleUrl);
        return (KieModuleModel)xStream.fromXML(kModuleUrl);
    }

    public KieModuleModel fromXML(String kModuleString) {
        KieModuleValidator.validate(kModuleString);
        return (KieModuleModel)xStream.fromXML(kModuleString);
    }
}
