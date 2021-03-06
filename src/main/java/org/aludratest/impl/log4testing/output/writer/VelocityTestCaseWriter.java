/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.impl.log4testing.output.writer;

import java.io.File;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes test cases using a Velocity macro.
 * @author Marcel Malitz
 */
public class VelocityTestCaseWriter extends VelocityWriter<TestCaseLog> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityTestCaseWriter.class);

    public File write(TestCaseLog testCase, String path) {
        File osFile = super.write(testCase, path);
        LOGGER.debug("Persisted test case {} in file {}", testCase, osFile);
        return osFile;
    }

}