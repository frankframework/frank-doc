/* 
Copyright 2022 WeAreFrank! 

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
 * This package avoids conflicts when a feature can be represented in multiple ways.
 * For example, feature {@link Default}, the default value of an attribute, can be
 * set using annotation <code>@Default</code>, JavaDoc tag <code>@ff.default</code>
 * or using annotation <code>@IbisDoc</code>. For the sake of the argument, assume
 * that there is a class <code>Child</code> with attribute setter <code>setX()</code>
 * and that it inherits from <code>Parent.setX()</code>. If the method of <code>Child</code>
 * has <code>@Default</code> and if the method of <code>Parent</code> uses <code>@ff.default</code>,
 * then the <code>@Default</code> should be taken. If the method of <code>Child</code>
 * has <code>@ff.default</code> and if the method of <code>Parent</code> uses <code>@Default</code>,
 * then the <code>@ff.default</code> should be taken.
 * 
 * There is two exception: annotations <code>FrankDocGroup</code> and <code>EnumLabel</code>
 * are not wrapped in a feature
 * because there is no alternative annotation or JavaDoc tag that could cause conflicts.
 * Another reason for this exception is that a group has an optional order number.
 */
package org.frankframework.frankdoc.feature;