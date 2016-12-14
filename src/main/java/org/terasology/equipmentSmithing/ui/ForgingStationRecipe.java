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
package org.terasology.equipmentSmithing.ui;

import com.google.common.base.Predicate;
import org.terasology.durability.components.DurabilityComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.equipment.component.EquipmentItemComponent;
import org.terasology.equipment.component.effects.JumpSpeedEffectComponent;
import org.terasology.equipment.component.effects.RegenEffectComponent;
import org.terasology.equipment.component.effects.SwimSpeedEffectComponent;
import org.terasology.equipment.component.effects.WalkSpeedEffectComponent;
import org.terasology.equipmentSmithing.component.RuneComponent;
import org.terasology.equipmentSmithing.component.RuneEquipmentEffectComponent;
import org.terasology.equipmentSmithing.component.RuneEquipmentModifierComponent;
import org.terasology.equipmentSmithing.component.RunePhysicalModifierComponent;
import org.terasology.equipmentSmithing.system.ForgingStationIngredientPredicate;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.physicalstats.component.PhysicalStatsModifierComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.utilities.Assets;
import org.terasology.workstationCrafting.component.CraftingStationRecipeComponent;
import org.terasology.workstationCrafting.system.recipe.behaviour.ConsumeItemCraftBehaviour;
import org.terasology.workstationCrafting.system.recipe.behaviour.InventorySlotResolver;
import org.terasology.workstationCrafting.system.recipe.behaviour.InventorySlotTypeResolver;
import org.terasology.workstationCrafting.system.recipe.behaviour.ReduceDurabilityCraftBehaviour;
import org.terasology.workstationCrafting.system.recipe.render.result.ItemRecipeResultFactory;
import org.terasology.workstationCrafting.system.recipe.workstation.AbstractWorkstationRecipe;
import org.terasology.workstationCrafting.system.recipe.workstation.CraftingStationToolPredicate;

import java.util.Arrays;
import java.util.List;

/**
 * A custom workstation recipe format for herbalism related recipes. Note that this is only intended to be used with the HerbalismCraftingStations.
 * Potions specifically.
 */
public class ForgingStationRecipe extends AbstractWorkstationRecipe {
    /**
     * Create the Herbalism Crafting Station's recipe based on the assigned CraftingStationRecipeComponent (i.e. the recipe parameters).
     *
     * @param recipe    The titular recipe applicable to this HerbalismStation.
     */
    public ForgingStationRecipe(CraftingStationRecipeComponent recipe) {
        // Set the required heat and processing duration. We don't need to set the required fluids as this station will
        // not use them.
        setRequiredHeat(recipe.requiredTemperature);
        setProcessingDuration(recipe.processingDuration);

        String[] splitItemResult = recipe.itemResult.split("\\*");

        // Create and set the custom result factory.
        EquipmentRecipeResultFactory forgeRecipeResultFactory = new EquipmentRecipeResultFactory(Assets.getPrefab(splitItemResult[1]).get(),
                splitItemResult[1], Integer.parseInt(splitItemResult[0]));

        // Add each of the ingredient (consumption) behaviors by parsing through the recipe components.
        for (String component : recipe.recipeComponents) {
            String[] split = component.split("\\*");
            int count = Integer.parseInt(split[0]);
            String type = split[1];

            addIngredientBehaviour(new ConsumeForgeIngredientBehaviour(new ForgingStationIngredientPredicate(type), count, new InventorySlotTypeResolver("INPUT"), forgeRecipeResultFactory));
            //addIngredientBehaviour(new ConsumeBoosterBehaviour(new ForgingStationIngredientPredicate(type), count, new InventorySlotTypeResolver("BOOSTER"), potionRecipeResultFactory));
        }

        // Add each of the tool behaviors by parsing through the recipe tools.
        if (recipe.recipeTools != null) {
            for (String recipeTool : recipe.recipeTools) {
                String[] split = recipeTool.split("\\*", 2);
                int count = Integer.parseInt(split[0]);
                String type = split[1];

                final ReduceDurabilityCraftBehaviour behaviour = new ReduceDurabilityCraftBehaviour(
                        new CraftingStationToolPredicate(type), count, new InventorySlotTypeResolver("TOOL"));
                addToolBehaviour(behaviour);
            }
        }

        setResultFactory(forgeRecipeResultFactory);
    }

    /**
     * This internal class is used for creating and defining the resultant equipment item.
     */
    private final class EquipmentRecipeResultFactory extends ItemRecipeResultFactory {
        private String toolTip;                      /** Item's tooltip. This id displayed by default when mouse hovered over. */
        private EntityRef runeRef = EntityRef.NULL;  /** Reference to the last tune used.

        /**
         * Set the reference to the current rune.
         *
         * @param ref           Reference to the rune entity.
         * @param destroyOld    Should the old rune entity be destroyed?
         */
        public void setRuneRef(EntityRef ref, boolean destroyOld) {
            if (destroyOld && runeRef != EntityRef.NULL) {
                runeRef.destroy();
            }

            runeRef = ref;
        }

        /**
         * Basic constructor for setting up this result factory.
         *
         * @param prefab    Prefab of the item to be created.
         * @param count     Number of the item to be created.
         */
        private EquipmentRecipeResultFactory(Prefab prefab, int count) {
            super(prefab, count);
            this.toolTip = toolTip;
        }

        /**
         * Constructor for when the equipment item's toolTip needs to be replaced.
         *
         * @param prefab    Prefab of the item to be created.
         * @param toolTip   Item's tooltip to be displayed.
         * @param count     Number of the items to be created.
         */
        private EquipmentRecipeResultFactory(Prefab prefab, String toolTip, int count) {
            super(prefab, count);
            this.toolTip = toolTip;
        }

        /**
         * Setup the display of the resultant item. This includes the icon and description text.
         *
         * @param parameters    List of parameters of this particular recipe component.
         * @param itemIcon      Graphical icon of this item.
         */
        @Override
        public void setupDisplay(List<String> parameters, ItemIcon itemIcon) {
            super.setupDisplay(parameters, itemIcon);

            itemIcon.setTooltipLines(
                    Arrays.asList(new TooltipLine(toolTip)));
        }

        /**
         * Create the resultant equipment item(s) using the given recipe components.
         *
         * @param parameters    All of the recipe components necessary for forging this equipment piece.
         * @param multiplier    The number of items that are created by this recipe.
         * @return              A reference to the resultant equipment item.
         */
        @Override
        public EntityRef createResult(List<String> parameters, int multiplier) {
            // Extract the herb parameters.
            final EntityRef result = super.createResult(parameters, multiplier);

            // If there was a rune processed.
            if (runeRef != EntityRef.NULL) {
                RuneEquipmentModifierComponent runeEquipModifier = runeRef.getComponent(RuneEquipmentModifierComponent.class);
                RunePhysicalModifierComponent runePhysicalModifierComponent = runeRef.getComponent(RunePhysicalModifierComponent.class);
                String runeName = runeRef.getComponent(DisplayNameComponent.class).name;

                // Add the effects of the rune's equip modifier (if any).
                if (runeEquipModifier != null) {
                    EquipmentItemComponent item = result.getComponent(EquipmentItemComponent.class);

                    item.attack += runeEquipModifier.attack;
                    item.defense += runeEquipModifier.defense;
                    item.weight += runeEquipModifier.weight;
                    item.speed += runeEquipModifier.speed;

                    result.saveComponent(item);
                }

                // Add the effects of the rune's physical stats modifiers (if any).
                if (runePhysicalModifierComponent != null) {

                    PhysicalStatsModifierComponent phyStatsMod = result.getComponent(PhysicalStatsModifierComponent.class);

                    if (phyStatsMod == null) {
                        phyStatsMod = new PhysicalStatsModifierComponent();
                    }

                    DisplayNameComponent resultDisplayName = result.getComponent(DisplayNameComponent.class);
                    resultDisplayName.name += " of " + runePhysicalModifierComponent.id;
                    resultDisplayName.description += "\nThis has a " + runeName + " fused into it.";

                    phyStatsMod.id = resultDisplayName.name;
                    phyStatsMod.strength += runePhysicalModifierComponent.strength;
                    phyStatsMod.dexterity += runePhysicalModifierComponent.dexterity;
                    phyStatsMod.constitution += runePhysicalModifierComponent.constitution;
                    phyStatsMod.agility += runePhysicalModifierComponent.agility;
                    phyStatsMod.endurance += runePhysicalModifierComponent.endurance;
                    phyStatsMod.charisma += runePhysicalModifierComponent.charisma;
                    phyStatsMod.luck += runePhysicalModifierComponent.luck;

                    result.saveComponent(resultDisplayName);
                    result.addOrSaveComponent(phyStatsMod);
                }

                // Add the effects of the rune's equipment effect (if any).
                if (runeRef.hasComponent(RuneEquipmentEffectComponent.class)) {
                    addEquipmentEffect(result, runeRef);

                    DisplayNameComponent resultDisplayName = result.getComponent(DisplayNameComponent.class);
                    resultDisplayName.name += " of " + runeRef.getComponent(RuneEquipmentEffectComponent.class).id;
                    resultDisplayName.description += "\nThis has a " + runeName + " fused into it.";
                    result.saveComponent(resultDisplayName);
                }
            }

            return result;
        }

        /**
         * Add an equipment effect onto an item. Note that this will replace the older effect if it already exists
         * on this item.
         * TODO: Only four effects are added for onw. Add the rest later.
         *
         * @param item  A reference to the equipment item.
         * @param rune  A reference to the rune being applied.
         */
        private void addEquipmentEffect(EntityRef item, EntityRef rune) {
            if (rune.hasComponent(JumpSpeedEffectComponent.class)) {
                item.addComponent(rune.getComponent(JumpSpeedEffectComponent.class));
            } else if (rune.hasComponent(RegenEffectComponent.class)) {
                item.addComponent(rune.getComponent(RegenEffectComponent.class));
            } else if (rune.hasComponent(SwimSpeedEffectComponent.class)) {
                item.addComponent(rune.getComponent(SwimSpeedEffectComponent.class));
            } else if (rune.hasComponent(WalkSpeedEffectComponent.class)) {
                item.addComponent(rune.getComponent(WalkSpeedEffectComponent.class));
            }
        }
    }

    /**
     * This internal class is used to define the custom consumption behavior of forge ingredients during crafting.
     */
    private final class ConsumeForgeIngredientBehaviour extends ConsumeItemCraftBehaviour {
        /** Reference to the equipment forging result factory. */
        private EquipmentRecipeResultFactory equipmentRecipeResultFactory;
        private EntityRef runeRef = EntityRef.NULL;

        /**
         * Constructor which creates the baseline for this item consumption behavior.
         *
         * @param matcher                       Predicate matcher for filtering out items that are not forge ingredients.
         * @param count                         Quantity of this ingredient to consume while crafting.
         * @param resolver                      To manage the inventory changes during this behavior.
         * @param equipmentRecipeResultFactory  Reference to the associated equipment result factory.
         */
        private ConsumeForgeIngredientBehaviour(Predicate<EntityRef> matcher, int count, InventorySlotResolver resolver,
                                                EquipmentRecipeResultFactory equipmentRecipeResultFactory) {
            super(matcher, count, resolver);
            this.equipmentRecipeResultFactory = equipmentRecipeResultFactory;
        }

        /**
         * Get the ingredient parameters and where they are located in the workstation's inventory, and return them as
         * a String.
         *
         * @param slots     List of workstation inventory slots that the item is present in.
         * @param item      Reference to the recipe component item in question.
         * @return          The ingredient parameters of this item in a combined String.
         */
        @Override
        protected String getParameter(List<Integer> slots, EntityRef item) {
            return super.getParameter(slots, item);
        }

        @Override
        public void processIngredient(EntityRef instigator, EntityRef entity, String parameter, int multiplier) {
            EntityRef boosterItem = InventoryUtils.getItemAt(entity, 6);
            if (boosterItem != EntityRef.NULL && boosterItem.hasComponent(RuneComponent.class)) {
                runeRef = boosterItem.copy();
                CoreRegistry.get(InventoryManager.class).removeItem(entity, instigator, boosterItem, true, 1 * multiplier);
            } else {
                runeRef = EntityRef.NULL;
            }

            equipmentRecipeResultFactory.setRuneRef(runeRef, true);
            super.processIngredient(instigator, entity, parameter, multiplier);
        }
    }
}
