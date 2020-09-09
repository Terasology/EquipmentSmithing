// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * Note that these modifiers are additive.
 */
public class RuneEquipmentModifierComponent implements Component {
    @Replicate
    public int attack = 0;

    @Replicate
    public int defense = 0;

    @Replicate
    public int weight = 0;

    @Replicate
    public int speed = 0;
}
