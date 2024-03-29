// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

public class RuneEssenceComponent implements Component<RuneEssenceComponent> {
    @Replicate
    public String runeEssenceID;

    @Replicate
    public int tier = 1;

    @Override
    public void copyFrom(RuneEssenceComponent other) {
        this.runeEssenceID = other.runeEssenceID;
        this.tier = other.tier;
    }
}
