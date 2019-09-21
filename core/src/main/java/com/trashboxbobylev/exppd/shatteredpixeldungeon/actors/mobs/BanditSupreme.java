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

		HP = HT = 4000;

		//1 in 50 chance to be a crazy bandit, equates to overall 1/100 chance.
		lootChance = 0.5f;

        loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);

        properties.add(Property.UNDEAD);
	}

    @Override
    public int attackProc(Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        enemy.damage(Random.NormalIntRange(0, damage / 3), this);

        Buff.prolong( enemy, Blindness.class, Random.Int( 2, 5 ) );
        Buff.affect( enemy, Slow.class, Random.Int( 2, 5 ));
        Buff.prolong( enemy, Cripple.class, Random.Int( 3, 8 ) );
        Dungeon.observe();

        return damage;
    }



    @Override
    public int drRoll() {
        return Random.NormalIntRange(250, 500);
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.25f;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (state == FLEEING) {
            Dungeon.level.drop( new Gold().random(), pos ).sprite.drop();
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 200, 300 );
    }

    @Override
    public String description() {
        String desc = super.description();

        if (item != null) {
            desc += Messages.get(this, "carries", item.name() );
        }

        return desc;
    }

}
