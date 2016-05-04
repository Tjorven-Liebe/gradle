/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling

import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion
import org.gradle.tooling.ProjectConnection
import org.gradle.util.RedirectStdOutAndErr
import org.junit.Rule

class ToolingApiDeprecatedVersionCrossVersionSpec extends ToolingApiSpecification {
    @Rule RedirectStdOutAndErr stdout = new RedirectStdOutAndErr()

    @ToolingApiVersion("current")
    @TargetGradleVersion(">=1.0-milestone-8 <1.2")
    def "warning is received when building using pre 1.2 provider"() {
        when:
        def stdout = new ByteArrayOutputStream()
        withConnection { ProjectConnection connection ->
            def build = connection.newBuild()
            build.standardOutput = stdout
            build.run()
        }

        then:
        stdout.toString().contains("Support for Gradle version ${targetDist.version.version} is deprecated and will be removed in tooling API version 3.0. You should upgrade your Gradle build to use Gradle 1.2 or later.")
    }
}
