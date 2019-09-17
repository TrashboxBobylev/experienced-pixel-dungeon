/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Speck;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Generator;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Gold;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Honeypot;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Item;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.BanditSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class BanditSupreme extends DepthyMob {
	
	public Item item;
	
	{
		spriteClass = BanditSprite.class;

		HP = HT = 6000;

		//1 in 50 chance to be a crazy bandit, equates to overall 1/100 chance.
		lootChance = 0.5f;

        loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);

        WANDERING = new Wandering();
        FLEEING = new Fleeing();

        properties.add(Property.UNDEAD);
	}

    protected boolean steal( Hero hero ) {

        Item item = hero.belongings.randomUnequipped();

        if (item != null && !item.unique && item.level() < 1 ) {

            GLog.w( Messages.get(Thief.class, "stole", item.name()) );
            if (!item.stackable) {
                Dungeon.quickslot.convertToPlaceholder(item);
            }
            item.updateQuickslot();

            Buff.prolong( hero, Blindness.class, Random.Int( 20, 50 ) );
            Buff.affect( hero, Poison.class ).set(Random.Int(50, 70) );
            Buff.prolong( hero, Cripple.class, Random.Int( 30, 80 ) );
            Dungeon.observe();

            if (item instanceof Honeypot){
                this.item = ((Honeypot)item).shatter(this, this.pos);
                item.detach( hero.belongings.backpack );
            } else {
                this.item = item.detach( hero.belongings.backpack );
                if ( item instanceof Honeypot.ShatteredPot)
                    ((Honeypot.ShatteredPot)item).setHolder(this);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public int attackProc(Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );

        if (item == null && enemy instanceof Hero && steal( (Hero)enemy )) {
            state = FLEEING;
        }

        return damage;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.25f;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (state == FLEEING) {
            Dungeon.level.drop( new Gold(), pos ).sprite.drop();
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public String description() {
        String desc = super.description();

        if (item != null) {
            desc += Messages.get(this, "carries", item.name() );
        }

        return desc;
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            super.act(enemyInFOV, justAlerted);

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state == HUNTING && item != null){
                state = FLEEING;
            }

            return true;
        }
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff( Terror.class ) == null && buff( Corruption.class ) == null) {
                if (enemySeen) {
                    sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
                    state = HUNTING;
                } else if (item != null
                        && !Dungeon.level.heroFOV[pos]
                        && Dungeon.level.distance(Dungeon.hero.pos, pos) < 6) {

                    int count = 150;
                    int newPos;
                    do {
                        newPos = Dungeon.level.randomRespawnCell();
                        if (count-- <= 0) {
                            break;
                        }
                    } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

                    if (newPos != -1) {

                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
                        pos = newPos;
                        sprite.place( pos );
                        sprite.visible = Dungeon.level.heroFOV[pos];
                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                    }

                    if (item != null) GLog.n( Messages.get(Thief.class, "escapes", item.name()));
                    item = null;
                    state = WANDERING;
                } else {
                    state = WANDERING;
                }
            } else {
                super.nowhereToRun();
            }
        }
    }
	
}
