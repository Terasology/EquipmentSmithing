// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.equipmentSmithing.system;

import com.google.common.base.Predicate;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.workstationCrafting.component.CraftingStationIngredientComponent;

/**
 * This predicate class is used to filter out items that are not compatible with HerbalismStations.
 */
public class ForgingStationIngredientPredicate implements Predicate<EntityRef> {
    /**
     * Item type to check for.
     */
    private final String itemType;

    /**
     * Define what item type this filter should look for.
     *
     * @param itemType Item type name being filtered.
     */
    public ForgingStationIngredientPredicate(String itemType) {
        this.itemType = itemType;
    }

    /**
     * Apply an entity to this filter to see if it's compatible with HerbalismStations and it has the same item type.
     *
     * @param input Reference to the entity being checked.
     * @return True if the entity fulfills the above conditions. False if not.
     */
    @Override
    public boolean apply(EntityRef input) {
        CraftingStationIngredientComponent component = input.getComponent(CraftingStationIngredientComponent.class);

        // If this contains a valid instance of (HerbComponent or EmptyPotionComponent, 
        // CraftingStationIngredientComponent,
        // and the input's type matches the itemType of this predicate, return true.
        return component != null && component.type.equalsIgnoreCase(itemType);
        //return (hComponent != null || epComponent != null) && component != null && component.type.equalsIgnoreCase
        // (itemType);
    }
}
