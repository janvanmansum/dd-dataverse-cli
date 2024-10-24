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

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "collection",
         mixinStandardHelpOptions = true,
         description = "Manage Dataverse collections (i.e. 'dataverses')")
public class CollectionCmd extends AbstractSubcommandContainer {
    @Parameters(index = "0", paramLabel = "alias", description = "The alias of the dataverse collection (default: root)", defaultValue = "root")
    @Getter
    private String alias;

}
