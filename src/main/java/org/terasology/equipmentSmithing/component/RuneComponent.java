// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

public class RuneComponent implements Component {
    @Replicate
    public String runeID;

    @Replicate
    public int tier = 1;
}
