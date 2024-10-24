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

import nl.knaw.dans.dvcli.command.AbstractCmd;
import nl.knaw.dans.lib.dataverse.model.dataset.FieldList;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.util.Collections;
import java.util.List;

@Command(name = "delete-metadata",
         mixinStandardHelpOptions = true,
         description = "Deletes one or more metadata values from the dataset")
public class DatasetDeleteMetadata extends AbstractCmd {
    @ParentCommand
    private DatasetCmd datasetCmd;

    @Option(names = { "-v", "--value" },
            description = "Values to delete, formatted as fieldX=value (for primitive fields), or as fieldX[fieldXSubfieldA=valueA,fieldXSubfieldB=valueB] (for compound fields).", required = true)
    private List<String> values;

    @Override
    public void doCall() throws Exception {
        datasetCmd.batchProcessor(d -> {
            var fieldList = new FieldList();
            for (String value : values) {
                fieldList.add(new MetadataFieldParser(value).parse());
            }
            d.deleteMetadata(fieldList, Collections.emptyMap());
            return "Metadata deleted";
        }).process();
    }
}
