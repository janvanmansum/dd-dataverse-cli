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
import nl.knaw.dans.lib.dataverse.model.RoleAssignment;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(name = "assign-role",
         mixinStandardHelpOptions = true,
         description = "Assign a role to a user in a Dataverse collection.")
public class CollectionAssignRole extends AbstractCmd {
    @ParentCommand
    private CollectionCmd collectionCmd;

    @Parameters(index = "0", paramLabel = "role", description = "The role to assign")
    private String role;

    @Parameters(index = "1", paramLabel = "assignee", description = "The identifier of the user to assign the role to")
    private String assignee;

    public CollectionAssignRole(@NonNull DataverseClient dataverseClient) {
        super(dataverseClient);
    }

    @Override
    public void doCall() throws IOException, DataverseException {
        var assignment = new RoleAssignment();
        assignment.setAssignee(assignee);
        assignment.setRole(role);
        dataverseClient.dataverse(collectionCmd.getAlias()).assignRole(assignment);
    }
}
