/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.plugins.cpp.msvcpp.internal;

import org.gradle.api.internal.tasks.compile.ArgCollector;
import org.gradle.api.internal.tasks.compile.ArgWriter;
import org.gradle.api.internal.tasks.compile.CompileSpecToArguments;
import org.gradle.internal.Factory;
import org.gradle.plugins.cpp.compiler.internal.CommandLineCppCompiler;
import org.gradle.plugins.cpp.compiler.internal.CommandLineCppCompilerArgumentsToOptionFile;
import org.gradle.plugins.cpp.internal.LinkerSpec;
import org.gradle.process.internal.ExecAction;

import java.io.File;

class LinkExeLinker extends CommandLineCppCompiler<LinkerSpec> {

    protected LinkExeLinker(File executable, Factory<ExecAction> execActionFactory, boolean createLibrary) {
        super(executable, execActionFactory, new CommandLineCppCompilerArgumentsToOptionFile<LinkerSpec>(
                ArgWriter.windowsStyleFactory(), new VisualCppLinkerSpecArguments(createLibrary)
        ));
    }

    private static class VisualCppLinkerSpecArguments implements CompileSpecToArguments<LinkerSpec> {
        private final boolean createLibrary;

        public VisualCppLinkerSpecArguments(boolean createLibrary) {
            this.createLibrary = createLibrary;
        }

        public void collectArguments(LinkerSpec spec, ArgCollector collector) {
            collector.args("/OUT:" + spec.getOutputFile().getAbsolutePath());
            collector.args("/NOLOGO");
            if (createLibrary) {
                collector.args("/DLL");
            }
            for (File file : spec.getSource()) {
                collector.args(file.getAbsolutePath());
            }
            for (File file : spec.getLibs()) {
                collector.args(file.getAbsolutePath().replaceFirst("\\.dll$", ".lib"));
            }
            // Last arg wins in case of duplicates
            collector.args(spec.getArgs());
        }
    }
}
