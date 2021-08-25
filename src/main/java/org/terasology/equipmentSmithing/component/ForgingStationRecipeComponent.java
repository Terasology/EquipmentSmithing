// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Add this Component to any recipe prefab that is supposed to be creatable in a ForgingStation or similar.
 * Include in prefab along with CraftingStationRecipeComponent to work properly.
 */
public class ForgingStationRecipeComponent implements Component<ForgingStationRecipeComponent> {
    // The following variables are unused.
    public String recipeId;

    @Override
    public void copyFrom(ForgingStationRecipeComponent other) {
        this.recipeId = other.recipeId;
    }
}
