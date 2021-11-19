This project is a doclet that is executed during the Maven build of the https://github.com/ibissource/iaf project. Its job is explained in the [CONTRIBUTING.md](https://github.com/ibissource/iaf/blob/master/CONTRIBUTING.md) and [FRANKDOC.md](https://github.com/ibissource/iaf/blob/master/FRANKDOC.md) files. The remainder of this text explains it in more detail.

## What is the Frank!Doc?

From a user perspective, the Frank!Doc consists of two parts. First, Frank developers use the file `FrankConfig-strict.xsd` that is generated by the frankDoc doclet. Frank developers typically get this file under the name `FrankConfig.xsd`. They include a reference to this XML schema in the XML Frank configs they write. This gives them autocomplete functionality and tooltip help in their text editor.

The second part is a [web application](https://ibis4example.ibissource.org/iaf/frankdoc) with reference information about the XML-based language of Frank configurations. Both parts are shown here now to introduce you to the Frank!Doc.

#### XML schema for Frank developers

A Frank developer uses the XML schema when he writes a Frank config. He may start writing a config in Visual Studio Code as shown below:

![vscodeStartConfig](./picturesForReadme/vscodeStartConfig.jpg)

He types the text needed to reference the XML schema of the Frank!Doc (number 1). Inside an `<Adapter>`, he writes the text `<R` (number 2). VSCode shows a red flag because this is invalid XML. VSCode can provide autocomplete help as shown (number 3). One of the options is the XML element `<Receiver>`. There is a button to get the tooltip help text of that element (number 4). This text is derived from the JavaDoc comment above the class declaration of [Receiver](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/receivers/Receiver.java).

Frank developers not only see errors about invalid XML, but also about violations of the XML schema.

![vscodeXsdViolation](./picturesForReadme/vscodeXsdViolation.jpg)

The Frank developer has used an XML element `<xxx>` that does not exist. VSCode produces a red flag. The tooltip help he can get is also shown. He sees which XML elements he can use instead of `<xxx>`. You may be surprised that this figure shows option `<Pipeline>` while the previous figure did not. The reason is that in the previous picture one letter, `R`, was provided already.

#### Web app with reference information for Frank developers

The XML schema helps Frank developers when they are writing their configs, but they also need help while designing them. They can use the Frank!Doc web application as the reference manual. It looks like shown below:

![webapp-intro](./picturesForReadme/webapp-intro.jpg)

To the top-left, you see a list of groups (number 1). These groups are controlled by Java annotation [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java). To the bottom-left, you see the Java class names that are members of the chosen group (number 2). When you select a class name, you get information about it (number 3). More explanation of this text follows later.

## Descriptions of classes, child elements and attributes

Most descriptions you see in the Frank!Doc come from JavaDoc comments. This section shows where your JavaDoc comments appear in the Frank!Doc. Below you see some snippets of Java class [Configuration](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/configuration/Configuration.java).

![eclipseConfigurationJava](./picturesForReadme/eclipseConfigurationJava.jpg)

You see the JavaDoc comment of the class (number 1), the JavaDoc of a config child setter (number 2) and the JavaDoc of an attribute (number 3).

These JavaDoc comments appear in VSCode as shown below:

![vscodeConfigurationDescriptions](./picturesForReadme/vscodeConfigurationDescriptions.jpg)

The JavaDoc comment of the class appears as help about XML element `<Configuration>` (number 1). The JavaDoc of the attribute setter appears as help about the attribute (number 3). The JavaDoc of the config child setter is not in the XML schema file (number 2 is not present).

The JavaDoc of a config child setter is available in the web application as shown below.

![webappConfigurationDescriptions](./picturesForReadme/webappConfigurationDescriptions.jpg)

The JavaDoc of the class appears at the top of the page. The JavaDoc of the config child setter appears in the table of config children. And the JavaDoc of the attribute setter appears with the attributes. The name of the config child (number 4) does not come from file [Configuration.java](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/configuration/Configuration.java). It comes from file [digester-rules.xml](https://github.com/ibissource/iaf/blob/master/core/src/main/resources/digester-rules.xml), which contains the following line:

![digesterRulesRoleAdapter](./picturesForReadme/digesterRulesRoleAdapter.jpg)

There is a `<rule>` XML element that links the name `adapter` to the name of the config child setter, `registerAdapter`.

#### Lombok

The Frank!Framework uses [Project Lombok](https://projectlombok.org/). Instead of writing out getter and setter methods of fields, you can generate them automatically using `@Getter` and `@Setter` annotations. Frank attributes can be defined using Lombok-generated setters as shown below:

![eclipseLombokSetter](./picturesForReadme/eclipseLombokSetter.jpg)

This snippet was produced by temporarily changing [Adapter](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/Adapter.java) (this snippet has been tested). It introduces attribute "name".

We have implemented Lombok-generated Frank attributes using the lombok-maven-plugin, because the JavaDoc tool does not understand `@Getter` and `@Setter` annotations. This plugin copies the source code and adds the getters and the setters. Lombok moves JavaDoc comments of the field to the generated getter and the generated setter like described [here](https://projectlombok.org/features/GetterSetter). In the remainder of this text, some custom JavaDoc tags are explained. Custom tags are moved to the setter. Therefore you can add them to the field and the Frank!Doc will see them.

## How Java inheritance is shown

In Java, config child setters and attribute setters are inherited like any Java method. As a consequence, attributes and config children are inherited. In `FrankConfig-strict.xsd` and in the JSON, inheritance is used to avoid unnecessary repetition and reduce file sizes. Frank developers do not see this in their text editor. Attributes look the same whether they are declared or inherited, and the same is true for child elements. In the web application, Frank developers can choose how they want to see inheritance. The default is shown below:

![webappInheritance](./picturesForReadme/webappInheritance.jpg)

After the JavaDoc you first see the nested config children, both inherited and declared. The shown config children are inherited from [SenderWithParametersBase](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/senders/SenderWithParametersBase.java). After the config children come the attributes. The attributes are grouped by their origin in the inheritance hierarchy: declared goes before inherited and closer ancestors go before further ancestors.

Frank developers can change the way config children and attributes are grouped. They can use the button shown below:

![webappButtonShowInheritance](./picturesForReadme/webappButtonShowInheritance.jpg)

When the button is pressed, it changes as shown:

![webappButtonHideInheritance](./picturesForReadme/webappButtonHideInheritance.jpg)

The sender information is now grouped as follows:

![webappInheritanceShown](./picturesForReadme/webappInheritanceShown.jpg)

First, everything declared in the [EchoSender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/senders/EchoSender.java) is shown. This class has no declared config children, so in this case there are only attributes. Then in a next section the first ancestor is shown for which information is available. There are additional sections until the inheritance hierarchy is exhausted.

## Preferred order of attributes and child elements

The order of config child setters and attribute setters in the Java code is important for the Frank!Doc. With the old IbisDoc documentation, this is not the case because the value fields in [@IbisDoc](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDoc.java) and [@IbisDocRef](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDocRef.java) annotations can hold the order. In the Frank!Doc, the order kept in [@IbisDoc](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDoc.java) and [@IbisDocRef](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDocRef.java) annotations is ignored and the method order is used.

In the Frank!Doc web application, the order of the attributes follows the order of the methods. This order is not enforced in any way in Frank configurations, because XML schemas in general do not prescribe the order of XML attributes.

The order of the config child setters defines a *preferred* order for config children (child elements). The previous section showed config children in their preferred order. The preferred order of config children follows from the following rules:
* Declared config children go first. The preferred order of the declared config children is the sequence of the config child setters in the Java source code.
* Inherited config children go after declared config children. Config children from a closer ancestor go before config children from a further ancestor.

Frank configs that violate the preferred order can still be parsed by the Frank!Framework, which is desirable for backward compatibility. The strict XSD does enforce the order as shown below:

![vscodeOrderChecked](./picturesForReadme/vscodeOrderChecked.jpg)

## Groups in the web application

This section is about the groups shown in the top-left of the Frank!Doc web application:

![webapp-groups-batch](./picturesForReadme/webapp-groups-batch.jpg)

The overview of all groups is shown as number 1. We have selected group `Batch` (number 2). How does the Frank!Doc know the order of the groups and the elements they contain? First, see the following snippet of [IRecordHandlerManager](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/IRecordHandlerManager.java)

![eclipseIRecordHandlerManager](./picturesForReadme/eclipseIRecordHandlerManager.jpg)

The interface has a Java annotation [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java). The annotation has fields `name` and `order`. The `order` is an integer that is used to sort the groups in the shown order.

When [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) is placed on a Java interface, then all Java classes that implement the interface are added to the group. Here is the type hierarchy of [IRecordHandlerManager](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/IRecordHandlerManager.java):

![eclipseTypeHierarchyIRecordHandlerManager](./picturesForReadme/eclipseTypeHierarchyIRecordHandlerManager.jpg)

The Java classes that implement [IRecordHandlerManager](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/IRecordHandlerManager.java) are highlighted. They are annotated in the first figure of this section as number 3.

The annotation on [IRecordHandlerManager](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/IRecordHandlerManager.java) only adds three classes to group `Batch`. The other elements are added by other [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) annotations. These do not have their `order` field set, see for example [RecordHandlingFlow](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/RecordHandlingFlow.java)

![eclipseRecordHandlingFlow](./picturesForReadme/eclipseRecordHandlingFlow.jpg)

The annotation is placed on a class here. Then only that class and its descendants are added to the group in the Frank!Doc web application.

You can also use the [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) annotation to restrict group membership. An example is [MessageStoreSender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/jdbc/MessageStoreSender.java). It implements both [ISender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/ISender.java) and [ITransactionalStorage](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/ITransactionalStorage.java) that have groups "Senders" and "TransactionalStorages". Class [MessageStoreSender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/jdbc/MessageStoreSender.java) has a [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) annotation for group "Senders" to restrict it to that group. Restricting to a group that is not inherited from any interface would be an error. If the [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) would refer a non-existing group "xxx", then the build of the F!F would fail with an error.

Finally, Java classes that do not have or inherit a [@FrankDocGroup](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/FrankDocGroup.java) annotation are put in group `Other`.

## Attribute types

The Frank!Doc supports type-checking for attributes. Attributes can be Boolean, integer or string. In addition, string attributes can have their values restricted by a Java enum.

The Frank!Doc type of an attribute depends on the argument type of the setter in the Java code. Here is an example from [Adapter](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/Adapter.java):

![eclipseAdapterAttributeAutoStart](./picturesForReadme/eclipseAdapterAttributeAutoStart.jpg)

Attribute `autoStart` has a setter with argument type `boolean`. The Frank!Doc type is Boolean. Below, you see a type violation of this attribute in VSCode:

![vscodeAttributeAutostartTypeViolation](./picturesForReadme/vscodeAttributeAutostartTypeViolation.jpg)

Value `xyz` is not a Boolean value and hence you see a red flag. You may be surprised by the complicated regular expression in this screenshot. It is there to allow references to properties like `${myProperty}`. Type checking is also done by `FrankConfig-compatibility.xsd`. If a configuration has type-violating attributes, the errors are detected when the configuration is loaded.

An example of a restricted string attribute is present in [HttpSenderBase](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/http/HttpSenderBase.java):

![eclipseEnumSetterArgument](./picturesForReadme/eclipseEnumSetterArgument.jpg)

Here is the definition of enum type HttpMethod:

![eclipseHttpMethodEnum](./picturesForReadme/eclipseHttpMethodEnum.jpg)

It appears as follows in the Frank!Doc website:

![webappEnumSetterArgument](./picturesForReadme/webappEnumSetterArgument.jpg)

The setter `setMethodType()` produces an attribute `methodType` (number 1). The row shows an empty field for the type (number 2), but it would be filled if the attribute would be Boolean or integer. Below that all possible values are shown (number 3).

There is an alternative way to define restricted string attributes in the Java code. An example appears in [LdapSender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/ldap/LdapSender.java):

![eclipseLdapSenderAttributeOperation](./picturesForReadme/eclipseLdapSenderAttributeOperation.jpg)

You give the attribute setter argument type String. You add a getter that appends the attribute name with the word `Enum`. The return type of the enum getter is the enum that determines what values are allowed. Part of enum `Operation` is shown below:

![eclipseEnumOperation](./picturesForReadme/eclipseEnumOperation.jpg)

With Java annotation [@EnumLabel](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/EnumLabel.java), you can alter the representation of the enum value in Frank configs (number 1). In the example, the string "read" in a Frank configuration is parsed as enum value `OPERATION_READ` (number 2). JavaDoc comments of enum values are used to provide descriptions (number 3). Please note that a JavaDoc comment must come before the [@EnumLabel](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/EnumLabel.java). It would not work if you interchanged the two. Below you see type checking for attribute `operation` in action:

![vscodeEnumOperation](./picturesForReadme/vscodeEnumOperation.jpg)

You see a list of the allowed values. Enum value `OPERATION_READ` shows up as "read" (number 1). You see the description (number 3) below the list.

In `FrankConfig-strict.xsd`, type checking against enums is case sensitive. In the example, the value "READ" for attribute `operation` would be flagged as an error. In `FrankConfig-compatibility.xsd`, type checking is case insensitive. This means that value "READ" is accepted when your configuration is parsed. This is the case to support backward compatibility.

Here is again how restricted string attributes are shown, this time with `@EnumLabel` annotations and value descriptions:

![webappAttributeOperation](./picturesForReadme/webappAttributeOperation.jpg)

You see the description in the JavaDoc comment (or [@IbisDoc](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDoc.java) Java annotation) of the attribute setter (number 1). Enum-restricted string attributes do not show a type (number 2) as said earlier (you may have to scroll to the right to see this), but it is shown for Boolean and integer attributes. Each enum value (number 3) is shown with its description (number 4) that comes from the JavaDoc comment of the enum value.

## Attribute default value

You can document a default value for attributes. The preferred way to do this is using JavaDoc tag `@ff.default`. Using the [@IbisDoc](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/doc/IbisDoc.java) Java annotation is deprecated. You document here what value is assumed if the attribute is not set. Only document this value if this default is applied already by the F!F source code. The `@ff.default` JavaDoc annotation does not alter the behavior of the Frank!Framework.

Here is how it looks like in the Java source code:

![postTypeSetterDefaultValue](./picturesForReadme/postTypeSetterDefaultValue.jpg)

In VScode, the default value appears as tool-tip help as shown:

![vscodeHttpSenderPostTypeDefault](./picturesForReadme/vscodeHttpSenderPostTypeDefault.jpg)

The default value is also shown in the Frank!Doc web application:

![webappPostTypeDefault](./picturesForReadme/webappPostTypeDefault.jpg)

## Parameters

Some XML elements in a Frank config can have nested element `<Param>`. The meaning of this element depends on the context: parameters can be query parameters of a database query, HTTP request parameters or something else. You can document this meaning in the JavaDoc comment above a Java class declaration. You use JavaDoc tag `@ff.parameters`. An example is Java class [HttpSender](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/http/HttpSender.java):

![eclipseFfParameters](./picturesForReadme/eclipseFfParameters.jpg)

In the Frank!Doc website parameters have a separate subsection next to config children and attributes, as shown below:

![webappParameters](./picturesForReadme/webappParameters.jpg)

In addition to the `@ff.parameters` tag, you can use the `@ff.parameter` tag to document the meaning of specific parameters. That tag also appears in the JavaDoc comment above a class. In contrast to the `@ff.parameters` tag, the `@ff.parameter` tag can appear multiple times. An example can be found in [CompareIntegerPipe](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/pipes/CompareIntegerPipe.java)

![eclipseFfParameter](./picturesForReadme/eclipseFfParameter.jpg)

The pipe compares two values that can each be supplied through `<Param>` tags. The JavaDoc explains the meaning of the specific parameters named `operand1` and `operand2`. Here is how this appears in the Frank!Doc website:

![webappTwoFfParameter](./picturesForReadme/webappTwoFfParameter.jpg)

**Note:** All text that you put after a JavaDoc tag is assumed to belong to that JavaDoc tag. Therefore, a JavaDoc comment should start with the text that does not belong to JavaDoc tags. You cannot mix JavaDoc tags with text that does not belong to a tag.

## Deprecated child elements and attributes

As a Java engineer, you know the `@Deprecated` Java annotation. You use it for Java classes and methods in a library that should no longer be used. Deprecated items only exist for backward compatibility, and they may be removed in a future version of the library. In the source code of the Frank!Framework, WeAreFrank! also uses this annotation. This section explains how `@Deprecated` attribute setters, `@Deprecated` config child setters and `@Deprecated` Java classes appear in the Frank!Doc.

First, the strict XSD that is used by Frank developers does not allow you to use `@Deprecated` items. Any attribute setter, config child setter or Java class that is `@Deprecated` does not appear in the strict XSD. Accessing it in a Frank config is flagged as an error as shown in earlier images of Visual Studio Code.

Second, `@Deprecated` items are available in `FrankConfig-compatibility.xsd`. Frank configs that reference them can be parsed by the Frank!Framework. This means that these Frank configs still do their job.

Third, you can choose in the Frank!Doc website whether you want to see `@Deprecated` items or not. Use the button shown below:

![webappShowDeprecated](./picturesForReadme/webappShowDeprecated.jpg)

`Deprecated` Java classes are crossed out in the list of class names to the bottom-left. See the picture below for the example of [DummyTransactionalStorage](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/jdbc/DummyTransactionalStorage.java):

![webappDeprecatedClass](./picturesForReadme/webappDeprecatedClass.jpg)

The button "Show Deprecated" has been pressed (number 3). We selected [DummyTransactionalStorage](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/jdbc/DummyTransactionalStorage.java) (number 1) in group "TransactionalStorages" (number 2). To the bottom-left, you see that "DummyTransactionalStorage" is crossed out. To the right, in the information about [DummyTransactionalStorage](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/jdbc/DummyTransactionalStorage.java), you see a label (number 4) showing that this class is deprecated.

Here is an example of how deprecated children look like:

![webappDeprecatedConfigChild](./picturesForReadme/webappDeprecatedConfigChild.jpg)

It is taken from [RecordTransformer](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/batch/RecordTransformer.java). The child called "child" is crossed out because method `registerChild()` is `@Deprecated`. Similarly, attributes are crossed out in the Attributes section when their setter is `@Deprecated`.

## Other JavaDoc tags

There are a few other JavaDoc tags that are relevant for the Frank!Doc:

**@ff.noAttribute:** Put this JavaDoc tag above a setter method to prevent it from becoming an attribute. You can also use this JavaDoc tag to suppress inheritance of an attribute. If you override an attribute setter and put the `@ff.noAttribute` tag above the overriding method, then the overriding Java class will not declare and not inherit the attribute.

**@ff.defaultElement:** To explain this tag, something else must be explained first. Consider the example Frank configuration shown below:

![vscodeGenericElementOption](./picturesForReadme/vscodeGenericElementOption.jpg)

As explained earlier, VSCode shows all allowed sub-elements. Examples of these are `<MailSenderPipe>` and `<PdfPipe>`, which are references to Java classes that implement [IPipe](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/IPipe.java). In the figure another possibility is highlighted, which is `<Pipe>`. This one is called the generic element option. Editing can proceed as follows:

![vscodePipeGenericElementOption](./picturesForReadme/vscodePipeGenericElementOption.jpg)

The tag `<Pipe>` encodes the role that the referenced Java class plays, and you reference the Java class through the `className` attribute.

Now we can see the meaning of the `@ff.defaultElement` tag. You can use it to provide a default value for the `className` attribute. It is present in Java interface [IPipe](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/core/IPipe.java) as shown:

![eclipseIPipe](./picturesForReadme/eclipseIPipe.jpg)

The default value is `nl.nn.adapterframework.pipes.SenderPipe` in this example. The autocomplete function of VSCode can add this default value for the `className` attribute. If it is omitted in a Frank config, then it is assumed to have the default value. You can thus use `<Pipe>` (without `className` attribute) as a synonym of `<SenderPipe>` because `<SenderPipe>` points to Java class [SenderPipe](https://github.com/ibissource/iaf/blob/master/core/src/main/java/nl/nn/adapterframework/pipes/SenderPipe.java).

**@ff.mandatory:** JavaDoc tag that applies to attribute setters and has no arguments. If an attribute setter has this annotation, then the attribute definition in the strict XSD gets a `use="required"` attribute. Frank developers will see an error in their text editor if they omit the attribute. The compatibility XSD is affected in the same way. Configurations in which the attribute is not set will not load.
