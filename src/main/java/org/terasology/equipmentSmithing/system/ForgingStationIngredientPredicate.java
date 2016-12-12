/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.equipmentSmithing.system;

import com.google.common.base.Predicate;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.equipmentSmithing.EquipmentSmithing;
import org.terasology.equipmentSmithing.component.ForgingStationRecipeComponent;
import org.terasology.multiBlock.Basic2DSizeFilter;
import org.terasology.multiBlock.BlockUriEntityFilter;
import org.terasology.multiBlock.MultiBlockFormRecipeRegistry;
import org.terasology.multiBlock.recipe.LayeredMultiBlockFormItemRecipe;
import org.terasology.processing.system.AnyActivityFilter;
import org.terasology.processing.system.ToolTypeEntityFilter;
import org.terasology.registry.In;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationCrafting.component.CraftingStationIngredientComponent;
import org.terasology.workstationCrafting.component.CraftingStationMaterialComponent;
import org.terasology.workstationCrafting.component.CraftingStationRecipeComponent;
import org.terasology.workstationCrafting.system.CraftInHandRecipeRegistry;
import org.terasology.workstationCrafting.system.CraftingWorkstationProcessFactory;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;

/**
 * This predicate class is used to filter out items that are not compatible with HerbalismStations.
 */
public class ForgingStationIngredientPredicate implements Predicate<EntityRef> {
    /** Item type to check for. */
    private String itemType;

    /**
     * Define what item type this filter should look for.
     *
     * @param itemType   Item type name being filtered.
     */
    public ForgingStationIngredientPredicate(String itemType) {
        this.itemType = itemType;
    }

    /**
     * Apply an entity to this filter to see if it's compatible with HerbalismStations and it has the same item type.
     *
     * @param input    Reference to the entity being checked.
     * @return         True if the entity fulfills the above conditions. False if not.
     */
    @Override
    public boolean apply(EntityRef input) {
        CraftingStationIngredientComponent component = input.getComponent(CraftingStationIngredientComponent.class);

        // If this contains a valid instance of (HerbComponent or EmptyPotionComponent, CraftingStationIngredientComponent,
        // and the input's type matches the itemType of this predicate, return true.
        return true;
        //return (hComponent != null || epComponent != null) && component != null && component.type.equalsIgnoreCase(itemType);
    }

    /**
     * This system registers all of the EquipmentSmithing recipes in this module.
     */
    @RegisterSystem
    public static class RegisterEquipmentSmithingRecipes extends BaseComponentSystem {
        @In
        private CraftInHandRecipeRegistry recipeRegistry;
        @In
        private WorkstationRegistry workstationRegistry;
        @In
        private MultiBlockFormRecipeRegistry multiBlockFormRecipeRegistry;
        @In
        private BlockManager blockManager;
        @In
        private PrefabManager prefabManager;
        @In
        private EntityManager entityManager;

        /**
         * Initialization phase where all of the recipes are added.
         */
        @Override
        public void initialise() {
            // Register the process factory for generic Herbalism process recipes that don't use the Herbalism Station.
            workstationRegistry.registerProcessFactory(EquipmentSmithing.FORGING_TIER_1_PROCESS, new CraftingWorkstationProcessFactory());
            workstationRegistry.registerProcessFactory(EquipmentSmithing.FORGING_TIER_2_PROCESS, new CraftingWorkstationProcessFactory());

            addWorkstationFormingRecipes();

            //addForgingStationRecipes();
        }

        /**
         * Add the recipe for building the Forging Station.
         */
        private void addWorkstationFormingRecipes() {
            LayeredMultiBlockFormItemRecipe herbalismStationRecipe = new LayeredMultiBlockFormItemRecipe(
                    new ToolTypeEntityFilter("forgeHammer"), new Basic2DSizeFilter(3, 1), new AnyActivityFilter(),
                    "EquipmentSmithing:BasicForgingStation", null);
            herbalismStationRecipe.addLayer(1, 1, new BlockUriEntityFilter(new BlockUri("Core:Brick")));
            herbalismStationRecipe.addLayer(1, 1, new BlockUriEntityFilter(new BlockUri(new ResourceUrn("Core:CobbleStone"), new ResourceUrn(("Engine:EighthBlock")))));
            multiBlockFormRecipeRegistry.addMultiBlockFormItemRecipe(herbalismStationRecipe);
        }

        /**
         * Add all of the potion recipes to the HerbalismStation.
         */
        private void addForgingStationRecipes() {
            // TODO: Temporarily removed for sake of testing.
            /* workstationRegistry.registerProcess(WorkstationCrafting.HERBALISM_PROCESS_TYPE,
                    new CraftingWorkstationProcess(WorkstationCrafting.HERBALISM_PROCESS_TYPE, "WorkstationCrafting:HerbPotion", new HerbalismCraftingStationRecipe()));*/

            // Add all the recipes marked with "HerbalismStationRecipeComponent" in their prefabs and add them to the list.
            for (Prefab prefab : prefabManager.listPrefabs(ForgingStationRecipeComponent.class)) {
                // Get the Crafting Station recipe component of this recipe prefab.
                CraftingStationRecipeComponent recipeComponent = prefab.getComponent(CraftingStationRecipeComponent.class);

                // We individually register each process instead of using registerProcessFactory (with CraftingWorkstationProcessFactory)
                // as we need to add in some custom actions. The createProcess method in CraftingWorkstationProcessFactory won't do.
                /*
                workstationRegistry.registerProcess(Alchemy.HERBALISM_PROCESS_TYPE,
                        new CraftingWorkstationProcess(Alchemy.HERBALISM_PROCESS_TYPE, recipeComponent.recipeId,
                                new HerbalismCraftingStationRecipe(recipeComponent), prefab, entityManager));
                */
            }
        }

        /**
         * This internal predicate class is used to filter out incompatible crafting station types.
         */
        private final class StationTypeFilter implements Predicate<EntityRef> {
            /** Name of the station type. */
            private String stationType;

            /**
             * Define what station this filter should look for.
             *
             * @param stationType   Name of the station being filtered.
             */
            private StationTypeFilter(String stationType) {
                this.stationType = stationType;
            }

            /**
             * Apply an entity to this filter to see if it has the same station type.
             * @param entity    Reference to the entity being checked.
             * @return          True if the entity has the same station type. False if not.
             */
            @Override
            public boolean apply(EntityRef entity) {
                CraftingStationMaterialComponent stationMaterial = entity.getComponent(CraftingStationMaterialComponent.class);
                return stationMaterial != null && stationMaterial.stationType.equals(stationType);
            }
        }
    }
}
