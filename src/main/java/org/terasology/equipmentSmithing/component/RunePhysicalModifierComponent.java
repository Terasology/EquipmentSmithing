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
package org.terasology.equipmentSmithing.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * Note that these modifiers are additive.
 */
public class RunePhysicalModifierComponent implements Component {
    /**
     * This stores this particular modifier's ID. Each modifier is normally intended to have a different ID, barring
     * the scenario where certain effects can replace older ones.
     */
    @Replicate
    public String id = "No Effect";

    /** The strength stat affects how much physical damage an entity does upon striking a target. */
    @Replicate
    public int strength = 0;

    /** The dexterity stat will affect weapon accuracy and item use speed in the future. */
    @Replicate
    public int dexterity = 0;

    /** The constitution stat affects player health. */
    @Replicate
    public int constitution = 0;

    /** The agility stat affects player movement speed. */
    @Replicate
    public int agility = 0;

    /** The endurance stat will affect something in the future. */
    @Replicate
    public int endurance = 0;

    /** The charisma stat will affect NPC interactions and shopping in the future. */
    @Replicate
    public int charisma = 0;

    /** The luck stat will provide benefits to many different actions in the future. */
    @Replicate
    public int luck = 0;
}
