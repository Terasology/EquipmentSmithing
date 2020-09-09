// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * Note that these modifiers are additive.
 */
public class RuneEquipmentEffectComponent implements Component {
    /**
     * This stores this particular modifier's ID. Each modifier is normally intended to have a different ID, barring the
     * scenario where certain effects can replace older ones.
     */
    @Replicate
    public String id = "No Effect";
}
