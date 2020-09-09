// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.entitySystem.Component;

/**
 * Add this Component to any recipe prefab that is supposed to be creatable in a ForgingStation or similar. Include in
 * prefab along with CraftingStationRecipeComponent to work properly.
 */
public class ForgingStationRecipeComponent implements Component {
    // The following variables are unused.
    public String recipeId;
}
