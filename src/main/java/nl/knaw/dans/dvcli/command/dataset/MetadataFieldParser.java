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

import lombok.Getter;
import nl.knaw.dans.lib.dataverse.CompoundFieldBuilder;
import nl.knaw.dans.lib.dataverse.model.dataset.MetadataField;
import nl.knaw.dans.lib.dataverse.model.dataset.PrimitiveSingleValueField;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a metadata field value. The value can be a primitive field or a compound field.
 *
 * The syntax for a field is:
 *
 * <pre>
 * metadataFieldValue :: primitiveMetadataFieldValue | compoundMetadataFieldValue
 *
 * primitiveMetadataFieldValue :: fieldName=fieldValue
 * fieldName :: [a-zA-Z0-9_]+
 * fieldValue :: continuousFieldValue | spacedFieldValue
 * continuousFieldValue :: [^[:space:]]+
 * spacedFieldValue :: "[^"]+"
 *
 * compoundMetadataFieldValue :: fieldName[primitiveMetadataFieldValue,primitiveMetadataFieldValue,...]
 *
 * </pre>
 */
@Getter
public class MetadataFieldParser {
    private static final Pattern PRIMITIVE_FIELD_PATTERN = Pattern.compile("(?<fieldName>[a-zA-Z0-9_]+)=(?<fieldValue>[^\\s]+|\"[^\"]+\")");
    private static final Pattern COMPOUND_FIELD_PATTERN = Pattern.compile("(?<fieldName>[a-zA-Z0-9_]+)\\[(?<subfields>.+)]");

    private final String value;

    public MetadataFieldParser(String value) {
        this.value = value;
    }

    public MetadataField parse() {
        Matcher primitiveMatcher = PRIMITIVE_FIELD_PATTERN.matcher(value);
        Matcher compoundMatcher = COMPOUND_FIELD_PATTERN.matcher(value);

        if (primitiveMatcher.matches()) {
            var fieldName = primitiveMatcher.group("fieldName");
            var fieldValue = primitiveMatcher.group("fieldValue");
            if (fieldValue.startsWith("\"") && fieldValue.endsWith("\"")) {
                fieldValue = fieldValue.substring(1, fieldValue.length() - 1);
            }
            return new PrimitiveSingleValueField(fieldName, fieldValue);
        }
        else if (compoundMatcher.matches()) {
            var fieldName = compoundMatcher.group("fieldName");
            var subfields = parseSubfields(compoundMatcher.group("subfields"));
            var builder = new CompoundFieldBuilder(fieldName, false);
            for (var entry : subfields.entrySet()) {
                builder.addSubfield(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
        else {
            throw new IllegalArgumentException("Invalid metadata field value: " + value);
        }
    }

    private Map<String, String> parseSubfields(String subfieldsString) {
        Map<String, String> subfields = new HashMap<>();
        String[] subfieldArray = subfieldsString.split(",");
        for (String subfield : subfieldArray) {
            Matcher subfieldMatcher = PRIMITIVE_FIELD_PATTERN.matcher(subfield.trim());
            if (subfieldMatcher.matches()) {
                String subfieldName = subfieldMatcher.group("fieldName");
                String subfieldValue = subfieldMatcher.group("fieldValue");
                if (subfieldValue.startsWith("\"") && subfieldValue.endsWith("\"")) {
                    subfieldValue = subfieldValue.substring(1, subfieldValue.length() - 1);
                }
                subfields.put(subfieldName, subfieldValue);
            }
            else {
                throw new IllegalArgumentException("Invalid subfield value: " + subfield);
            }
        }
        return subfields;
    }
}