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
package org.aludratest.service.file;

import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Verification;

/**
 * {@link Verification} interface of the {@link FileService}.
 * @author Volker Bergmann
 */
public interface FileVerification extends Verification {

    /** Expects a file to exist.
     *  @param filePath the path of the file of which to assert presence
     *  @throws org.aludratest.service.file.exception.FileAbsentException if the file was not found. 
     */
    void assertPresence(@TechnicalLocator String filePath);

    /** Expects a file to be absent.
     *  @param filePath the path of the file of which to assert absence 
     *  @throws org.aludratest.service.file.exception.FilePresentException if the file was encountered. 
     */
    void assertAbsence(@TechnicalLocator String filePath);

    /**
     * Asserts that the file object at the given location is not a directory
     * @param filePath
     * @throws org.aludratest.service.file.exception.NotAFileException 
     * 		if the file object located at the given path is a directory 
     */
    void assertFile(@TechnicalLocator String filePath);

    /**
     * Asserts that the file object at the given location is a directory
     * @param filePath
     * @throws org.aludratest.service.file.exception.NotADirectoryException
     * 		if the file object located at the given path is not a directory
     */
    void assertDirectory(@TechnicalLocator String filePath);

}
