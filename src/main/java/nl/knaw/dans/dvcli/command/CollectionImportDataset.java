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
package nl.knaw.dans.dvcli.command;

import lombok.NonNull;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Command(name = "import-dataset",
         mixinStandardHelpOptions = true,
         description = "Import a JSON dataset into a dataverse collection.")
public class CollectionImportDataset extends AbstractCmd {
    @ParentCommand
    private CollectionCmd collectionCmd;

    @CommandLine.Parameters(index = "0", paramLabel = "dataset", description = "A JSON string defining the dataset to import..")
    private String dataset;

    @CommandLine.Option(names = { "-p", "--persistentId" }, paramLabel = "persistentId", description = "Existing persistent identifier (PID)")
    String persistentId = "";

    @CommandLine.Option(names = { "-a", "--autoPublish" }, paramLabel = "autoPublish", type = Boolean.class, description = "Immediately publish the dataset")
    Boolean autoPublish = false;

    @CommandLine.Option(names = { "-m", "--mdkeys" }, paramLabel = "metadataKeys", description = "Maps the names of the metadata blocks to their 'secret' key values")
    private Map<String, String> metadataKeys = new HashMap<>();

    public CollectionImportDataset(@NonNull DataverseClient dataverseClient) {
        super(dataverseClient);
    }

    @Override
    public void doCall() throws IOException, DataverseException {
        var r = dataverseClient.dataverse(collectionCmd.getAlias()).importDataset(dataset, persistentId, autoPublish, metadataKeys);
        System.out.println(r.getEnvelopeAsString());
    }
}
