// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Note that these modifiers are additive.
 */
public class RuneEquipmentModifierComponent implements Component<RuneEquipmentModifierComponent> {
    @Replicate
    public int attack = 0;

    @Replicate
    public int defense = 0;

    @Replicate
    public int weight = 0;

    @Replicate
    public int speed = 0;

    @Override
    public void copy(RuneEquipmentModifierComponent other) {
        this.attack = other.attack;
        this.defense = other.defense;
        this.weight = other.weight;
        this.speed = other.speed;
    }
}
