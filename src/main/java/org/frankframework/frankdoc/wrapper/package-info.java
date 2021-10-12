/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
/**
 * This package was created to allow the Frank!Doc model to be instantiated both from Java reflection or from within a doclet.
 * The doclet that creates the Frank!Doc is in package {@link org.frankframework.frankdoc.doclet}. In June 2021,
 * the transition from using reflection to using doclets is complete. We do not need to created the Frank!Doc model
 * from Java reflection anymore. The reflection implementation of this API is therefore removed. 
 * <p>
 * The picture below shows what classes are available to model elements that appear in Java code.
 * <p>
 * <img src="doc-files/classHierarchy.jpg" width="400" alt="Image classHierarchy.jpg can not be shown" />
 * <p>
 */
package org.frankframework.frankdoc.wrapper;