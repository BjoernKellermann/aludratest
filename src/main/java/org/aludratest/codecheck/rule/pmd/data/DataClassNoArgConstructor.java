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
package org.aludratest.codecheck.rule.pmd.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.aludratest.codecheck.rule.pmd.AbstractAludraTestRule;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site
 * for rule description.
 * 
 * @author falbrech
 * 
 */
public class DataClassNoArgConstructor extends AbstractAludraTestRule {

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		if (!isDataClass(node)) {
			return super.visit(node, data);
		}

		// find a constructor with no arguments
		boolean found = false;
		try {
			// easy way using Java Reflection
			Constructor<?> cstr = node.getType().getConstructor();
			found = cstr != null && Modifier.isPublic(cstr.getModifiers());
		}
		catch (NoSuchMethodException e) {
            // found = false -> initial value
		}
		if (!found) {
			addViolationWithMessage(data, node, "Data class " + node.getImage() + " must provide a public no-arg constructor");
		}

		return super.visit(node, data);
	}

}
