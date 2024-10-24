/*
 * Copyright (C) 2024 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.dvcli.command.dataset;

import nl.knaw.dans.lib.dataverse.model.dataset.CompoundSingleValueField;
import nl.knaw.dans.lib.dataverse.model.dataset.PrimitiveSingleValueField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MetadataFieldParserTest {

    @Test
    public void continuous_primitive_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1=value1");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(PrimitiveSingleValueField.class);
        PrimitiveSingleValueField primitiveSingleValueField = (PrimitiveSingleValueField) metadataField;
        assertThat(primitiveSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(primitiveSingleValueField.getValue()).isEqualTo("value1");
    }

    @Test
    public void quoted_primitive_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1=\"value1\"");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(PrimitiveSingleValueField.class);
        PrimitiveSingleValueField primitiveSingleValueField = (PrimitiveSingleValueField) metadataField;
        assertThat(primitiveSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(primitiveSingleValueField.getValue()).isEqualTo("value1");
    }

    @Test
    public void spaced_primitive_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1=\"value 1\"");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(PrimitiveSingleValueField.class);
        PrimitiveSingleValueField primitiveSingleValueField = (PrimitiveSingleValueField) metadataField;
        assertThat(primitiveSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(primitiveSingleValueField.getValue()).isEqualTo("value 1");
    }

    @Test
    public void invalid_primitive_value_should_throw_exception() {
        assertThatThrownBy(() -> new MetadataFieldParser("field1=value1 value2").parse())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid metadata field value: field1=value1 value2");
    }

    @Test
    public void primitive_value_without_equals_should_throw_exception() {
        assertThatThrownBy(() -> new MetadataFieldParser("field1 value1").parse())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid metadata field value: field1 value1");
    }

    @Test
    public void primitive_value_without_fieldName_should_throw_exception() {
        assertThatThrownBy(() -> new MetadataFieldParser("=value1").parse())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid metadata field value: =value1");
    }

    @Test
    public void primitive_value_without_fieldValue_should_throw_exception() {
        assertThatThrownBy(() -> new MetadataFieldParser("field1=").parse())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid metadata field value: field1=");
    }

    @Test
    public void compound_field_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1[subfieldA=valueA,subfieldB=valueb]");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(CompoundSingleValueField.class);
        CompoundSingleValueField compoundSingleValueField = (CompoundSingleValueField) metadataField;
        assertThat(compoundSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(compoundSingleValueField.getValue()).hasSize(2);
        assertThat(compoundSingleValueField.getValue().values()).extracting("typeName").contains("subfieldA", "subfieldB");
        assertThat(compoundSingleValueField.getValue().values()).extracting("value").contains("valueA", "valueb");
    }

    @Test
    public void compound_field_with_single_subfield_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1[subfieldA=valueA]");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(CompoundSingleValueField.class);
        CompoundSingleValueField compoundSingleValueField = (CompoundSingleValueField) metadataField;
        assertThat(compoundSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(compoundSingleValueField.getValue()).hasSize(1);
        assertThat(compoundSingleValueField.getValue().values()).extracting("typeName").contains("subfieldA");
        assertThat(compoundSingleValueField.getValue().values()).extracting("value").contains("valueA");
    }

    @Test
    public void compound_field_with_quoted_subfield_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1[subfieldA=\"value A\"]");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(CompoundSingleValueField.class);
        CompoundSingleValueField compoundSingleValueField = (CompoundSingleValueField) metadataField;
        assertThat(compoundSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(compoundSingleValueField.getValue()).hasSize(1);
        assertThat(compoundSingleValueField.getValue().values()).extracting("typeName").contains("subfieldA");
        assertThat(compoundSingleValueField.getValue().values()).extracting("value").contains("value A");
    }

    @Test
    public void compound_field_with_spaced_subfield_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1[subfieldA=\"value A\"]");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(CompoundSingleValueField.class);
        CompoundSingleValueField compoundSingleValueField = (CompoundSingleValueField) metadataField;
        assertThat(compoundSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(compoundSingleValueField.getValue()).hasSize(1);
        assertThat(compoundSingleValueField.getValue().values()).extracting("typeName").contains("subfieldA");
    }

    @Test
    public void compound_field_with_spaced_and_non_spaced_subfield_value_should_be_parsed() {
        MetadataFieldParser parser = new MetadataFieldParser("field1[subfieldA=valueA,subfieldB=\"value B\"]");
        var metadataField = parser.parse();
        assertThat(metadataField).isInstanceOf(CompoundSingleValueField.class);
        CompoundSingleValueField compoundSingleValueField = (CompoundSingleValueField) metadataField;
        assertThat(compoundSingleValueField.getTypeName()).isEqualTo("field1");
        assertThat(compoundSingleValueField.getValue()).hasSize(2);
        assertThat(compoundSingleValueField.getValue().values()).extracting("typeName").contains("subfieldA", "subfieldB");
        assertThat(compoundSingleValueField.getValue().values()).extracting("value").contains("valueA", "value B");
    }

    @Test
    public void compound_field_with_spaced_subfield_name_should_throw_exception() {
        assertThatThrownBy(() -> new MetadataFieldParser("field1[subfield A=valueA]").parse())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid subfield value: subfield A=valueA");
    }
}
