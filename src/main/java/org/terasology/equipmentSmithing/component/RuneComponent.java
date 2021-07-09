// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

public class RuneComponent implements Component<RuneComponent> {
    @Replicate
    public String runeID;

    @Replicate
    public int tier = 1;
}
