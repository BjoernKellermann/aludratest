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
package org.aludratest.service.flatfile.impl;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aludratest.content.flat.FlatContent;
import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.content.flat.data.RowTypeData;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.Action;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.FileService;
import org.aludratest.service.flatfile.FlatFileCondition;
import org.aludratest.service.flatfile.FlatFileInteraction;
import org.aludratest.service.flatfile.FlatFileService;
import org.aludratest.service.flatfile.FlatFileVerification;
import org.aludratest.testcase.event.attachment.Attachment;

/**
 * Implements all {@link FlatFileService} action interfaces.
 * @author Volker Bergmann
 */
public class DefaultFlatFileAction implements FlatFileInteraction, FlatFileVerification, FlatFileCondition {

    private FlatContent contentHandler;

    /** A reference to the underlying FileService. */
    private FileService fileService;

    /** Keeps references to the writers, each one wrapped with a WriterConfig. */
    private Map<Object, WriterConfig> writers;

    /** Constructor. 
     *  @param contentHandler 
     *  @param fileService */
    public DefaultFlatFileAction(FlatContent contentHandler, FileService fileService) {
        this.contentHandler = contentHandler;
        this.fileService = fileService;
        this.writers = new HashMap<Object, WriterConfig>();
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }
    
    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
    }
    
    /** Empty implementation of the {@link Action} interface returning null. */
    @Override
    public List<Attachment> createDebugAttachments() {
        return null;
    }

    // general file operations -------------------------------------------------

    @Override
    public boolean exists(String filePath) {
        return fileService.check().exists(filePath);
    }

    @Override
    public void delete(String filePath) {
        fileService.perform().delete(filePath);
    }

    @Override
    public void waitUntilExists(String elementType, String filePath) {
        fileService.perform().waitUntilExists(elementType, filePath);
    }

    @Override
    public void waitUntilNotExists(String filePath) {
        fileService.perform().waitUntilNotExists(filePath);
    }

    // writer operations -------------------------------------------------------

    @Override
    public Object createWriter(String filePath, boolean overwrite) {
        WriterConfig config = new WriterConfig(filePath, overwrite);
        Object writerId = config.createWriter(contentHandler);
        this.writers.put(writerId, config);
        return writerId;
    }

    @Override
    public void writeRow(Object bean, Object writerId) {
        contentHandler.writeRow(bean, writerId);
    }

    @Override
    public void closeWriter(Object writerId) {
        contentHandler.closeWriter(writerId);
        WriterConfig writerConfig = getWriterConfig(writerId, true);
        String content = writerConfig.getContent();
        writers.remove(writerId);
        fileService.perform().writeTextFile(writerConfig.getFilePath(), content, writerConfig.isOverwrite());
    }

    @Override
    public Object createReader(String filePath) {
        String content = fileService.perform().readTextFile(filePath);
        Object readerId = contentHandler.createReader(new StringReader(content));
        return readerId;
    }

    @Override
    public void addRowType(RowTypeData rowType, Object readerId) {
        contentHandler.addRowType(rowType, readerId);
    }

    @Override
    public FlatFileBeanData readRow(Object readerId) {
        return contentHandler.readRow(readerId);
    }

    @Override
    public void closeReader(Object readerId) {
        contentHandler.closeReader(readerId);
    }

    // private helper methods --------------------------------------------------

    private WriterConfig getWriterConfig(Object writerId, boolean required) {
        WriterConfig writer = writers.get(writerId);
        if (writer == null && required) {
            throw new AutomationException("Writer not found");
        }
        return writer;
    }

}
