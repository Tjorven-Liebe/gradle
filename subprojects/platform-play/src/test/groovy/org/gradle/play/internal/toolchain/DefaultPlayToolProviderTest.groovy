/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.play.internal.toolchain

import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.internal.Factory
import org.gradle.play.internal.run.PlayRunSpec
import org.gradle.play.platform.PlayPlatform
import org.gradle.process.internal.WorkerProcessBuilder
import spock.lang.Specification
import spock.lang.Unroll

class DefaultPlayToolProviderTest extends Specification {
    FileResolver fileResolver = Mock()
    CompilerDaemonManager compilerDaemonManager = Mock()
    ConfigurationContainer configurationContainer = Mock()
    DependencyHandler dependencyHandler = Mock()
    PlayPlatform playPlatform = Mock()

    DefaultPlayToolProvider playToolProvider
    Factory<WorkerProcessBuilder> workerProcessBuilderFactory = Mock()
    PlayRunSpec playRunSpec = Mock()


    def setup(){
        playToolProvider = new DefaultPlayToolProvider(fileResolver, compilerDaemonManager, configurationContainer, dependencyHandler, playPlatform)
    }

    @Unroll
    def "provides playRunner for play #playVersion"(){
        setup:
        Configuration runConfiguration = Mock()

        2 * playPlatform.getPlayVersion() >> playVersion

        when:
        def runner = playToolProvider.newApplicationRunner(workerProcessBuilderFactory, playRunSpec)

        then:
        runner != null
        1 * configurationContainer.detachedConfiguration(_) >> runConfiguration

        where:
        playVersion << ["2.2.x", "2.3.x"]
    }

    def "fails on providing playRunner unsupported play versions"(){
        setup:
        1 * playPlatform.getPlayVersion() >> playVersion

        when:
        playToolProvider.newApplicationRunner(workerProcessBuilderFactory, playRunSpec)

        then:
        def exception = thrown(InvalidUserDataException)
        exception.message == "Could not find a compatible Play version for the Run service. This plugin is compatible with: 2.3.x, 2.2.x"

        where:
        playVersion << ["2.1.x", "2.4.x", "3.0.0"]
    }
}
